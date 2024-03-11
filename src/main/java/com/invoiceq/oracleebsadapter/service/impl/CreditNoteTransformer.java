package com.invoiceq.oracleebsadapter.service.impl;

import com.Invoiceq.connector.model.InvoiceType;
import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.SaptcoZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.service.AbstractInvoiceTransformer;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;


@Service
public class CreditNoteTransformer extends AbstractInvoiceTransformer<CreditNoteRequest> {
    private final Logger LOGGER = LoggerFactory.getLogger(CreditNoteTransformer.class);


    @Override
    public List<CreditNoteRequest> transform(List<SaptcoZatcaHeaderERP> invoices) {
        List<CreditNoteRequest> transformedInvoices = new ArrayList<>();
        Template template = getInvoiceTemplate("CreditNoteTemplate.ftl");
        invoices.forEach(inv->{
            try {
                Long begin = System.currentTimeMillis();
                SaptcoZatcaHeaderERP originalInv = processAndGetOriginalInvoiceInfo(inv);
                if(readyToSend(Objects.nonNull(originalInv) && Objects.equals(originalInv.getStatus(),ZatcaStatus.SUCCESS), inv.getIsHistorical())){
                    reformatObjectData(inv);
                    List<InvoiceLine> invoiceLines = invoiceLineReposiroty.findAllByCustomerTrxIdAndSeqId(inv.getCustomerTrxId(),inv.getSeqId());
//                    List<InvoiceLine> invoiceLines = inv.getInvoiceLines();
                    List<InvoiceLine> originalInvoiceLines = inv.getIsHistorical()?null:invoiceLineReposiroty.findAllByCustomerTrxIdAndSeqId(originalInv.getCustomerTrxId(), originalInv.getSeqId());
//                    List<InvoiceLine> originalInvoiceLines = inv.getIsHistorical()?null:originalInv.getInvoiceLines();
                    setProductsCode(invoiceLines,originalInvoiceLines,inv.getIsHistorical());
                    StringWriter writer = new StringWriter();
                    Map<String, Object> data = new HashMap<>();
                    data.put("inv", inv);
                    data.put("invoiceLines", invoiceLines);
                    data.put("originalInvoice",originalInv);
                    data.put("transformer",this);
                    LOGGER.info("Data is {}", data);
                    template.process(data, writer);
//                    CreditNoteRequest invoice = mapper.readValue(writer.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""), CreditNoteRequest.class);
                    CreditNoteRequest invoice = mapper.readValue(writer.toString(), CreditNoteRequest.class);
                    transformedInvoices.add(invoice);
                    saptcoZatcaHeaderErpRepository.updateReadStatus(ZatcaStatus.ZATCA_LOCKED, inv.getCustomerTrxId(),inv.getSeqId());
                    Long end = System.currentTimeMillis();
                    LOGGER.info("time to execute the transformation {} millisecond", end - begin);
                }
            } catch (TemplateException | IOException e) {
                LOGGER.error("error happened", e);
            }
        });
        return transformedInvoices;
    }


    private void setProductsCode(List<InvoiceLine> invoiceLines ,List<InvoiceLine> originalInvoiceLines, Boolean isHistorical){
        if(isHistorical){
            invoiceLines.forEach(line -> {
                line.setProductCode(line.hashCode()+"");
                invoiceLineReposiroty.updateProductCodeByCustomerTrxIdAndSeqIdAndLineNumber(line.getProductCode(), line.getCustomerTrxId(),line.getSeqId(),line.getLineNumber());
//                invoiceLineReposiroty.updateProductCodeByInvoiceLineEmbeddable(line.getProductCode(), line.getInvoiceLineEmbeddable());
            });
        }
        else{
            invoiceLines.stream().distinct().filter(originalInvoiceLines::contains).forEach(line -> {
                if (StringUtils.isBlank(line.getProductCode())) {
                    line.setProductCode(line.hashCode() + "");
                    invoiceLineReposiroty.updateProductCodeByCustomerTrxIdAndSeqIdAndLineNumber(line.getProductCode(), line.getCustomerTrxId(), line.getSeqId(), line.getLineNumber());
                }
//                invoiceLineReposiroty.updateProductCodeByInvoiceLineEmbeddable(line.getProductCode(), line.getInvoiceLineEmbeddable());
            });
        }
    }

    private SaptcoZatcaHeaderERP processAndGetOriginalInvoiceInfo(SaptcoZatcaHeaderERP inv){
        if(Objects.nonNull(inv.getCreditMemoNo())){
            Optional<SaptcoZatcaHeaderERP> originalInvoice = saptcoZatcaHeaderErpRepository.findFirstByInvoiceIdOrderBySeqIdDesc(inv.getCreditMemoNo());
            if(originalInvoice.isPresent()){
                inv.setOriginalInvoiceqReference(originalInvoice.get().getReference());
                inv.setIsHistorical(false);
                return originalInvoice.get();
            }
            else {
                inv.setIsHistorical(true);
            }
        }
        return null;
    }

    private boolean readyToSend(Boolean originalInvoiceExistAndSuccess,Boolean isHistorical){
     return BooleanUtils.isTrue(originalInvoiceExistAndSuccess) || BooleanUtils.isTrue(isHistorical);
    }
}