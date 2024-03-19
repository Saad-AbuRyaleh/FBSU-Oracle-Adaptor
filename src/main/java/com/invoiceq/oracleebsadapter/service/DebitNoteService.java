package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.model.ResponseTemplate;
import com.Invoiceq.connector.model.debitNote.DebitNoteOperationResponse;
import com.Invoiceq.connector.model.debitNote.DebitNoteRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.InvoiceAttachment;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.repository.InvoiceAttachmentRepository;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.transformer.DebitNoteTransformer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DebitNoteService {
    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger LOGGER = LoggerFactory.getLogger(CreditNoteService.class);

    @Value("${invoiceq.connector.orgkey}")
    private String orgKey;

    @Value("${invoiceq.connector.channelId}")
    private String channelId;
    private final static String DEBIT_TYPE_CODE = "383";
    private final InvoiceHeadersRepository invoiceHeadersRepository;
    private final InvoiceqConnector invoiceqConnector;
    private final InvoiceAttachmentRepository invoiceAttachmentRepository;
    private final DebitNoteTransformer transformer;
    public void handlePendingDebits() {
        Optional<List<InvoiceHeader>> invoiceHeaderList = invoiceHeadersRepository.findByStatusAndInvoiceType(ZatcaStatus.PENDING, DEBIT_TYPE_CODE);
        if (invoiceHeaderList.isPresent() && !CollectionUtils.isEmpty(invoiceHeaderList.get())) {
            List<DebitNoteRequest> debitNoteRequests = transformer.transform(invoiceHeaderList.get());
            for (int i = 0; i < debitNoteRequests.size(); i++) {
                doIQIntegration(debitNoteRequests.get(i));
            }
        }
    }

    private void doIQIntegration(DebitNoteRequest debitNoteRequest) {
        try {
            LOGGER.info("Start InvoiceQ Integration for Debit [{}]", debitNoteRequest.getDebitNoteNumber());
            DebitNoteOperationResponse response = invoiceqConnector.createDebitNote(debitNoteRequest, orgKey, channelId);
            if (isValid(debitNoteRequest.getDebitNoteNumber(), response)) {
                LOGGER.info("Success Integration for Debit [{}]", debitNoteRequest.getDebitNoteNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.SUCCESS, debitNoteRequest.getDebitNoteNumber());
                invoiceHeadersRepository.updateSuccessfullResponse(debitNoteRequest.getDebitNoteNumber(),response.getBody().getInvoiceqReference(),response.getBody().getQrCode(),response.getBody().getSubmittedPayableRoundingAmount(), new Timestamp(new Date().getTime()));
                writePdfData(response.getBody().getInvoiceqReference());
            } else {
                LOGGER.info("Failed Integration for Debit [{}]", debitNoteRequest.getDebitNoteNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.BUSINESS_FAILED, debitNoteRequest.getDebitNoteNumber());
                invoiceHeadersRepository.updateFailedStatus(debitNoteRequest.getDebitNoteNumber(),response.getErrors().toString());

            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong with Debit [{}]", debitNoteRequest.getDebitNoteNumber(), e);
            invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.TECHNICAL_FAILED, debitNoteRequest.getDebitNoteNumber());
            invoiceHeadersRepository.updateFailedStatus(debitNoteRequest.getDebitNoteNumber(),e.getMessage());
        }
    }

    private void writePdfData(String invoiceqReference) throws InterruptedException {
        Thread.sleep(5000);
        DebitNoteOperationResponse response = getInvoicePdf(invoiceqReference);
        if(BooleanUtils.isTrue(response.getValid()) && Objects.nonNull(response.getBody())) {
            InvoiceAttachment invoiceAttachment = new InvoiceAttachment();
            invoiceAttachment.setPdfFileName(response.getBody().getPdfFileName());
            invoiceAttachment.setStatus("WRITTEN");
            invoiceAttachment.setCreatedOn(new Timestamp(new Date().getTime()));
            invoiceAttachment.setPdfFilePath(response.getBody().getDirectLink());
            invoiceAttachmentRepository.save(invoiceAttachment);
        }
    }

    private DebitNoteOperationResponse getInvoicePdf(String invoiceqReference) {
        DebitNoteOperationResponse debitNoteOperationResponse = null;
        try{
            debitNoteOperationResponse= invoiceqConnector.getDebitPdfByInvoiceQReference(invoiceqReference, ResponseTemplate.PDF_A3,orgKey,channelId);
        }
        catch (Exception e){
            LOGGER.error("error in get invoice pdf",e);
        }
        return debitNoteOperationResponse;
    }

    private boolean isValid(String debitNoteNumber, DebitNoteOperationResponse response) {
        if (BooleanUtils.isNotTrue(response.getValid()) && !CollectionUtils.isEmpty(response.getErrors())) {
            LOGGER.error("InvoiceQ Validation Errors For Debit Number [{}] is [{}]", debitNoteNumber, toJson(response));
            return false;
        }
        return true;
    }
    private String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (Exception e) {
            LOGGER.error("error happened", e);
            return "";
        }
    }
}
