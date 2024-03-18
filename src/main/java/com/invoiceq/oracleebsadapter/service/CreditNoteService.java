package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.model.ResponseTemplate;
import com.Invoiceq.connector.model.creditNote.CreditNoteOperationResponse;
import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.InvoiceAttachment;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.repository.InvoiceAttachmentRepository;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.transformer.CreditNoteTransformer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CreditNoteService {

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceService.class);

    @Value("${invoiceq.connector.orgkey}")
    private String orgKey;

    @Value("${invoiceq.connector.channelId}")
    private String channelId;

    private final InvoiceHeadersRepository invoiceHeadersRepository;
    private final CreditNoteTransformer transformer;
    private final InvoiceqConnector invoiceqConnector;
    private final InvoiceAttachmentRepository invoiceAttachmentRepository;
    private final static String CREDIT_TYPE_CODE = "381";

    public void handlePendingCredits() {
        Optional<List<InvoiceHeader>> invoiceHeaderList = invoiceHeadersRepository.findByStatusAndInvoiceType(ZatcaStatus.PENDING, CREDIT_TYPE_CODE);
        if (invoiceHeaderList.isPresent() && !CollectionUtils.isEmpty(invoiceHeaderList.get())) {
            List<CreditNoteRequest> creditNoteRequests = transformer.transform(invoiceHeaderList.get());
            for (int i = 0; i < creditNoteRequests.size(); i++) {
                doIQIntegration(creditNoteRequests.get(i));
            }
        }
    }

    private void doIQIntegration(CreditNoteRequest creditNoteRequest) {
        try {
            LOGGER.info("Start InvoiceQ Integration for Credit [{}]", creditNoteRequest.getCreditNoteNumber());
            CreditNoteOperationResponse response = invoiceqConnector.createCreditNote(creditNoteRequest, orgKey, channelId);
            if (isValid(creditNoteRequest.getCreditNoteNumber(), response)) {
                LOGGER.info("Success Integration for Invoice [{}]", creditNoteRequest.getCreditNoteNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.SUCCESS, creditNoteRequest.getCreditNoteNumber());
                invoiceHeadersRepository.updateSuccessfullResponse(creditNoteRequest.getCreditNoteNumber(),response.getBody().getInvoiceqReference(),response.getBody().getQrCode(),response.getBody().getSubmittedPayableRoundingAmount(), new Timestamp(new Date().getTime()));
                writePdfData(response.getBody().getInvoiceqReference());
            } else {
                LOGGER.info("Failed Integration for Credit [{}]", creditNoteRequest.getCreditNoteNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.BUSINESS_FAILED, creditNoteRequest.getCreditNoteNumber());
                invoiceHeadersRepository.updateFailedStatus(creditNoteRequest.getCreditNoteNumber(),response.getErrors().toString());

            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong with Credit [{}]", creditNoteRequest.getCreditNoteNumber(), e);
            invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.TECHNICAL_FAILED, creditNoteRequest.getCreditNoteNumber());
            invoiceHeadersRepository.updateFailedStatus(creditNoteRequest.getCreditNoteNumber(),e.getMessage());
        }
    }

    private void writePdfData(String invoiceqReference) throws InterruptedException {
        Thread.sleep(5000);
        CreditNoteOperationResponse response = getInvoicePdf(invoiceqReference);
        if(BooleanUtils.isTrue(response.getValid()) && Objects.nonNull(response.getBody())) {
            InvoiceAttachment invoiceAttachment = new InvoiceAttachment();
            invoiceAttachment.setPdfFileName(response.getBody().getPdfFileName());
            invoiceAttachment.setStatus("WRITTEN");
            invoiceAttachment.setCreatedOn(new Timestamp(new Date().getTime()));
            invoiceAttachment.setPdfFilePath(response.getBody().getDirectLink());
            invoiceAttachmentRepository.save(invoiceAttachment);
        }
    }

    private CreditNoteOperationResponse getInvoicePdf(String invoiceqReference) {
        CreditNoteOperationResponse creditNoteOperationResponse = null;
        try{
            creditNoteOperationResponse= invoiceqConnector.getCreditPdfByInvoiceQReference(invoiceqReference, ResponseTemplate.PDF_A3,orgKey,channelId);
        }
        catch (Exception e){
            LOGGER.error("error in get invoice pdf",e);
        }
        return creditNoteOperationResponse;

    }

    private boolean isValid(String creditNoteNumber, CreditNoteOperationResponse response) {
        if (BooleanUtils.isNotTrue(response.getValid()) && !CollectionUtils.isEmpty(response.getErrors())) {
            LOGGER.error("InvoiceQ Validation Errors For Credit Number [{}] is [{}]", creditNoteNumber, toJson(response));
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
