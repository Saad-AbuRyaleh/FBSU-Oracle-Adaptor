package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.model.outward.OutwardInvoiceOperationResponse;
import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.transformer.OutwardInvoiceTransformer;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
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
                //TODO HANDLE THE INSERTION INTO ATTACHMENTS
                //TODO HANDLE OTHER RESPONSES , LIKE IQ REF ...ETC
            } else {
                LOGGER.info("Failed Integration for Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber());
                invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.BUSINESS_FAILED, uploadOutwardInvoiceRequest.getInvoiceNumber());
                //TODO HANDLE OTHER RESPONSES , LIKE ERROR DETAILS ...ETC

            }
        } catch (Exception e) {
            LOGGER.error("Something went wrong with Invoice [{}]", uploadOutwardInvoiceRequest.getInvoiceNumber(), e);
            invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.TECHNICAL_FAILED, uploadOutwardInvoiceRequest.getInvoiceNumber());
            //TODO HANDLE OTHER RESPONSES , LIKE ERROR DETAILS ...ETC
        }

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
