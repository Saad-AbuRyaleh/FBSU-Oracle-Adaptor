package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.exception.InvoiceqException;
import com.Invoiceq.connector.model.ResponseTemplate;
import com.Invoiceq.connector.model.outward.OutwardInvoiceOperationResponse;
import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.ErpInvLobs;
import com.invoiceq.oracleebsadapter.model.ZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.repository.ErpInvLobsRepository;
import com.invoiceq.oracleebsadapter.repository.ZatcaHeaderErpRepository;
import com.invoiceq.oracleebsadapter.service.impl.OutwardInvoiceTransformer;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.util.*;


@Service
public class OutwardInvoiceService {
    @Autowired
    private InvoiceqConnector invoiceqConnector;
    @Autowired
    private OutwardInvoiceTransformer invoiceTransformer;
    @Autowired
    private ZatcaHeaderErpRepository zatcaHeaderErpRepository;

    @Autowired
    private ErpInvLobsRepository erpLobsRepository;

    @Value("${invoiceq.connector.orgkey}")
    private String orgKey;

    @Value("${invoiceq.connector.channelId}")
    private String channelId;


    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceService.class);

    public void proceedInvoices() {
        zatcaHeaderErpRepository.fetchInvoiceProcedure();
        Optional<List<ZatcaHeaderERP>> invoicesOptional = zatcaHeaderErpRepository.findAllByStatusAndInvoiceType(ZatcaStatus.HOST_READY, "388");
        if (invoicesOptional.isPresent() && !CollectionUtils.isEmpty(invoicesOptional.get())) {
            List<UploadOutwardInvoiceRequest> outwardInvoiceRequests = invoiceTransformer.transform(invoicesOptional.get());
            for (int i = 0; i < outwardInvoiceRequests.size(); i++) {
                sendAndHandle(outwardInvoiceRequests.get(i), invoicesOptional.get().get(i).getInvoiceId(), invoicesOptional.get().get(i).getCustomerTrxId(), invoicesOptional.get().get(i).getSeqId());
            }
        }

    }

    public void sendAndHandle(UploadOutwardInvoiceRequest request, String originalInvoiceId, Long customerTrxId, Long seqId) {
        LOGGER.info("Send invoice with INVOICE_ID {} to invoiceQ ..", request.getInvoiceNumber());
        OutwardInvoiceOperationResponse response = null;
        try {
            LOGGER.info("Request {}", toJson(request));
            response = invoiceqConnector.createInvoice(request, orgKey, channelId);
            handleInvoiceStatus(response, originalInvoiceId, customerTrxId, seqId);
            LOGGER.info("Receive response from invoiceQ .. {}", toJson(response));
        } catch (InvoiceqException iqEx) {
            LOGGER.error("InvoiceQ error happened", iqEx);
            zatcaHeaderErpRepository.updateZatcaStatus(seqId, originalInvoiceId, ZatcaStatus.TECHNICAL_FAILED, toJson(iqEx.getResponseEntity()), null, null);
            zatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId, ZatcaStatus.TECHNICAL_FAILED.name(), toJson(iqEx.getResponseEntity()));
        }
    }

    private void handleInvoiceStatus(OutwardInvoiceOperationResponse response, String invoiceId, Long customerTrxId, Long seqId) {
        try {
            if (response.getValid() && Objects.nonNull(response.getBody())) {
                zatcaHeaderErpRepository.updateZatcaStatus(seqId, invoiceId, ZatcaStatus.SUCCESS, toJson(response.getErrors()), response.getBody().getInvoiceqReference(), response.getBody().getSubmittedPayableRoundingAmount());
                getAndWriteInvoicePdf(response.getBody().getInvoiceqReference(), customerTrxId, "PENDING");
                zatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId, ZatcaStatus.SUCCESS.name(), toJson(response));
            } else {
                zatcaHeaderErpRepository.updateZatcaStatus(seqId, invoiceId, ZatcaStatus.BUSINESS_FAILED, toJson(response.getErrors()), null, null);
                zatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId, ZatcaStatus.BUSINESS_FAILED.name(), toJson(response.getErrors()));
            }
        } catch (Exception e) {
            LOGGER.error("error happened ", e);
        }

    }


    private void getAndWriteInvoicePdf(String invoiceqReference, Long customerTrxId, String fileStatus) throws InterruptedException {
        Thread.sleep(5000);
        OutwardInvoiceOperationResponse response = getInvoicePdf(invoiceqReference);
        if (BooleanUtils.isTrue(response.getValid()) && Objects.nonNull(response.getBody())) {
            ErpInvLobs erpInvLobs = new ErpInvLobs();
            erpInvLobs.setCustomerTrxId(customerTrxId);
            erpInvLobs.setBlobFileName(response.getBody().getPdfFileName());
            erpInvLobs.setErpFileStatus(fileStatus);
            erpInvLobs.setCreatedOn(new Timestamp(new Date().getTime()));
            erpInvLobs.setBlobFileContent(Base64.getDecoder().decode(response.getBody().getBase64PDF()));
            erpLobsRepository.save(erpInvLobs);
        }
    }


    private OutwardInvoiceOperationResponse getInvoicePdf(String invoiceqReference) {
        OutwardInvoiceOperationResponse outwardInvoiceOperationResponse = null;
        try {
            outwardInvoiceOperationResponse = invoiceqConnector.getInvoicePdfByInvoiceQReference(invoiceqReference, ResponseTemplate.PDF_A3, orgKey, channelId);
        } catch (Exception e) {
            LOGGER.error("error in get invoice pdf", e);
        }
        return outwardInvoiceOperationResponse;
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
