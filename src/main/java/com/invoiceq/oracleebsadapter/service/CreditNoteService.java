package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.connector.InvoiceqConnector;
import com.Invoiceq.connector.exception.InvoiceqException;
import com.Invoiceq.connector.model.ResponseTemplate;
import com.Invoiceq.connector.model.creditNote.CreditNoteOperationResponse;
import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.invoiceq.oracleebsadapter.model.ErpInvLobs;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.model.SaptcoZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.repository.ErpInvLobsRepository;
import com.invoiceq.oracleebsadapter.repository.SaptcoZatcaHeaderErpRepository;
import com.invoiceq.oracleebsadapter.service.impl.CreditNoteTransformer;
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
public class CreditNoteService {
    @Autowired
    private InvoiceqConnector invoiceqConnector;

    @Autowired
    private CreditNoteTransformer invoiceTransformer;
    @Autowired
    private SaptcoZatcaHeaderErpRepository saptcoZatcaHeaderErpRepository;

    @Autowired
    private ErpInvLobsRepository erpLobsRepository;

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    private static final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceService.class);

    @Value("${invoiceq.connector.orgkey}")
    private String orgKey;

    @Value("${invoiceq.connector.channelId}")
    private String channelId;

    public void sendAndHandle(CreditNoteRequest request, String originalInvoiceId , Long customerTrxId,Long seqId){
        LOGGER.info("Send invoice with INVOICE_ID {} to invoiceQ ..",request.getCreditNoteNumber());
        CreditNoteOperationResponse response=null;
        try {
            LOGGER.info("Request {}",toJson(request));
            response = invoiceqConnector.createCreditNote(request,orgKey,channelId);
            handleInvoiceStatus(response,originalInvoiceId,customerTrxId,seqId);
            LOGGER.info("Receive response from invoiceQ .. {}",toJson(response));
        } catch (InvoiceqException iqEx) {
            LOGGER.error("InvoiceQ error happened", iqEx);
            saptcoZatcaHeaderErpRepository.updateZatcaStatus(seqId,originalInvoiceId,ZatcaStatus.TECHNICAL_FAILED,toJson(response.getErrors()),null,null);
            saptcoZatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId,ZatcaStatus.TECHNICAL_FAILED.name(),toJson(response));
        }
    }

    private void handleInvoiceStatus(CreditNoteOperationResponse response, String invoiceId, Long customerTrxId,Long seqId){
        try {
            if(response.getValid() && Objects.nonNull(response.getBody())){
                saptcoZatcaHeaderErpRepository.updateZatcaStatus(seqId,invoiceId,ZatcaStatus.SUCCESS,toJson(response.getErrors()),response.getBody().getInvoiceqReference(),response.getBody().getSubmittedPayableRoundingAmount());
                getAndWriteInvoicePdf(response.getBody().getInvoiceqReference(),customerTrxId,"PENDING");
                saptcoZatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId,ZatcaStatus.SUCCESS.name(),toJson(response));
            }
            else{
                saptcoZatcaHeaderErpRepository.updateZatcaStatus(seqId,invoiceId,ZatcaStatus.BUSINESS_FAILED,toJson(response.getErrors()),null,null);
                saptcoZatcaHeaderErpRepository.updateInvoiceStatusProcedure(customerTrxId,ZatcaStatus.BUSINESS_FAILED.name(),toJson(response));
            }
        }catch (Exception e){
            LOGGER.error("error happened ",e);
        }

    }



    private void getAndWriteInvoicePdf(String invoiceqReference, Long customerTrxId , String fileStatus) throws InterruptedException {
        Thread.sleep(5000);
        CreditNoteOperationResponse response = getInvoicePdf(invoiceqReference);
        if(BooleanUtils.isTrue(response.getValid()) && Objects.nonNull(response.getBody())) {
            ErpInvLobs erpInvLobs = new ErpInvLobs();
            erpInvLobs.setCustomerTrxId(customerTrxId);
            erpInvLobs.setBlobFileName(response.getBody().getPdfFileName());
            erpInvLobs.setErpFileStatus(fileStatus);
            erpInvLobs.setCreatedOn(new Timestamp(new Date().getTime()));
            erpInvLobs.setBlobFileContent(Base64.getDecoder().decode(response.getBody().getBase64PDF()));
            erpLobsRepository.save(erpInvLobs);
        }
    }

    public void proceedInvoices(){
//        saptcoZatcaHeaderErpRepository.fetchInvoiceProcedure();
        Optional<List<SaptcoZatcaHeaderERP>> invoicesOptional = saptcoZatcaHeaderErpRepository.findAllByStatusAndInvoiceType(ZatcaStatus.HOST_READY,"381");
        if(invoicesOptional.isPresent() && !CollectionUtils.isEmpty(invoicesOptional.get())){
            List<CreditNoteRequest> creditNoteRequests =invoiceTransformer.transform(invoicesOptional.get());
            for (int i=0;i < creditNoteRequests.size();i++) {
                sendAndHandle(creditNoteRequests.get(i),invoicesOptional.get().get(i).getInvoiceId(),invoicesOptional.get().get(i).getCustomerTrxId(),invoicesOptional.get().get(i).getSeqId());
            }
        }

    }

    private CreditNoteOperationResponse getInvoicePdf(String invoiceqReference){
        CreditNoteOperationResponse creditNoteOperationResponse = null;
        try{
            creditNoteOperationResponse= invoiceqConnector.getCreditPdfByInvoiceQReference(invoiceqReference, ResponseTemplate.PDF_A3,orgKey,channelId);
        }
        catch (Exception e){
            LOGGER.error("error in get invoice pdf",e);
        }
        return creditNoteOperationResponse;
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
