package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.model.ResponseTemplate;
import com.Invoiceq.connector.model.outward.OutwardInvoiceOperationResponse;
import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.InvoiceAttachment;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.repository.InvoiceAttachmentRepository;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.transformer.OutwardInvoiceTransformer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
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
public class OutwardInvoiceService {
    @Value("${invoiceq.connector.orgkey}")
    private String orgKey;

    @Value("${invoiceq.connector.channelId}")
    private String channelId;

    private final InvoiceHeadersRepository invoiceHeadersRepository;
    private final OutwardInvoiceTransformer transformer;
    private final InvoiceqConnector invoiceqConnector;
    private final InvoiceAttachmentRepository invoiceAttachmentRepository;
    private static final String INVOICE_TYPE_CODE = "388";
    private static final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceService.class);
    private final ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    public void handlePendingInvoices() {
        Optional<List<InvoiceHeader>> invoiceHeaderList = invoiceHeadersRepository.findByStatusAndInvoiceType(ZatcaStatus.PENDING, INVOICE_TYPE_CODE);
        if (invoiceHeaderList.isPresent() && !CollectionUtils.isEmpty(invoiceHeaderList.get())) {
            List<UploadOutwardInvoiceRequest> outwardInvoiceRequests = transformer.transform(invoiceHeaderList.get());
            for (int i = 0; i < outwardInvoiceRequests.size(); i++) {
                doIQIntegration(outwardInvoiceRequests.get(i));
            }
        }
    }

    private void doIQIntegration(UploadOutwardInvoiceRequest uploadOutwardInvoiceRequest) {
        try {
            LOGGER.info("Start InvoiceQ Integration for Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber());
            OutwardInvoiceOperationResponse response = invoiceqConnector.createInvoice(uploadOutwardInvoiceRequest, orgKey, channelId);
            if (isValid(uploadOutwardInvoiceRequest.getInvoiceNumber(), response)) {
                LOGGER.info("Success Integration for Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.SUCCESS, uploadOutwardInvoiceRequest.getInvoiceNumber());
                invoiceHeadersRepository.updateSuccessfullResponse(uploadOutwardInvoiceRequest.getInvoiceNumber(),response.getBody().getInvoiceqReference(),response.getBody().getQrCode(),response.getBody().getSubmittedPayableRoundingAmount(), new Timestamp(new Date().getTime()));
                writePdfData(response.getBody().getInvoiceqReference(),uploadOutwardInvoiceRequest.getInvoiceNumber());
            } else {
                LOGGER.info("Failed Integration for Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.BUSINESS_FAILED, uploadOutwardInvoiceRequest.getInvoiceNumber());
                invoiceHeadersRepository.updateFailedStatus(uploadOutwardInvoiceRequest.getInvoiceNumber(),response.getErrors().toString());
            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong with Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber(), e);
            invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.TECHNICAL_FAILED, uploadOutwardInvoiceRequest.getInvoiceNumber());
            invoiceHeadersRepository.updateFailedStatus(uploadOutwardInvoiceRequest.getInvoiceNumber(),e.getMessage());
        }

    }

    private void writePdfData(String invoiceqReference, String invoiceNumber) throws InterruptedException {
        Thread.sleep(5000);
        OutwardInvoiceOperationResponse response = getInvoicePdf(invoiceqReference);
        if(BooleanUtils.isTrue(response.getValid()) && Objects.nonNull(response.getBody())) {
            InvoiceAttachment invoiceAttachment = new InvoiceAttachment();
            invoiceAttachment.setPdfFileName(response.getBody().getPdfFileName());
            invoiceAttachment.setStatus(ZatcaStatus.SUCCESS.name());
            invoiceAttachment.setCreatedOn(new Timestamp(new Date().getTime()));
            invoiceAttachment.setPdfFilePath(response.getBody().getDirectLink());
            Optional<InvoiceHeader> invoiceHeader = invoiceHeadersRepository.findByInvoiceId(invoiceNumber);
            invoiceHeader.ifPresent(header -> invoiceAttachment.setInvoiceSequence(header.getInvoiceSequence()));
            invoiceAttachmentRepository.save(invoiceAttachment);
        }
    }

    private OutwardInvoiceOperationResponse getInvoicePdf(String invoiceqReference) {
        OutwardInvoiceOperationResponse outwardInvoiceOperationResponse = null;
        try{
            outwardInvoiceOperationResponse= invoiceqConnector.getInvoicePdfByInvoiceQReference(invoiceqReference, ResponseTemplate.PDF_A3,orgKey,channelId);
        }
        catch (Exception e){
            LOGGER.error("error in get invoice pdf",e);
        }
        return outwardInvoiceOperationResponse;
    }

    private boolean isValid(String invoiceNumber, OutwardInvoiceOperationResponse response) {
        if (BooleanUtils.isNotTrue(response.getValid()) && !CollectionUtils.isEmpty(response.getErrors())) {
            LOGGER.error("InvoiceQ Validation Errors For Invoice Number [{}] is [{}]", invoiceNumber, toJson(response));
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
