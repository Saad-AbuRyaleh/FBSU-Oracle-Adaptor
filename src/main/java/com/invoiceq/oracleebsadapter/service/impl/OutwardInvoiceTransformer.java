package com.invoiceq.oracleebsadapter.service.impl;

import com.Invoiceq.connector.model.InvoiceType;
import com.Invoiceq.connector.model.creditNote.InvoiceLevelAllowance;
import com.Invoiceq.connector.model.outward.InvoiceLevelDiscount;
import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.SaptcoZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import com.invoiceq.oracleebsadapter.service.AbstractInvoiceTransformer;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
public class OutwardInvoiceTransformer extends AbstractInvoiceTransformer<UploadOutwardInvoiceRequest> {
    private final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceTransformer.class);


    @Override
    public List<UploadOutwardInvoiceRequest> transform(List<SaptcoZatcaHeaderERP> invoices) {
        List<UploadOutwardInvoiceRequest> transformedInvoices = new ArrayList<>();
        Template template = getInvoiceTemplate("OutwardInvoiceTemplate.ftl");
        invoices.forEach(inv->{
            try {
                Long begin = System.currentTimeMillis();
                List<InvoiceLine> invoiceLines = invoiceLineReposiroty.findAllByCustomerTrxIdAndSeqId(inv.getCustomerTrxId(),inv.getSeqId());
//                List<InvoiceLine> invoiceLines = inv.getInvoiceLines();
                StringWriter writer = new StringWriter();
                Map<String, Object> data = new HashMap<>();
                reformatObjectData(inv);
                setProductsCode(invoiceLines);
                data.put("inv",inv);
                data.put("invoiceLevelAllowances", extractInvoiceLevelDiscount(invoiceLines));
                data.put("invoiceLines", invoiceLines);
                data.put("transformer",this);
                LOGGER.info("Data is {}", data);
                template.process(data, writer);
//                UploadOutwardInvoiceRequest invoice = mapper.readValue(writer.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\r", ""), UploadOutwardInvoiceRequest.class);
                UploadOutwardInvoiceRequest invoice = mapper.readValue(writer.toString(), UploadOutwardInvoiceRequest.class);
                transformedInvoices.add(invoice);
                saptcoZatcaHeaderErpRepository.updateReadStatus(ZatcaStatus.ZATCA_LOCKED, inv.getCustomerTrxId(),inv.getSeqId());
                Long end = System.currentTimeMillis();
                LOGGER.info("time to execute the transformation {} millisecond", end - begin);
            } catch (TemplateException | IOException e) {
                LOGGER.error("error happened", e);
            }
        });
        return transformedInvoices;
    }

    private void setProductsCode(List<InvoiceLine> invoiceLines){
        invoiceLines.forEach(line->{
            if (StringUtils.isBlank(line.getProductCode())) {
                line.setProductCode(line.hashCode() + "");
                invoiceLineReposiroty.updateProductCodeByCustomerTrxIdAndSeqIdAndLineNumber(line.getProductCode(), line.getCustomerTrxId(), line.getSeqId(), line.getLineNumber());
//            invoiceLineReposiroty.updateProductCodeByInvoiceLineEmbeddable(line.getProductCode(), line.getInvoiceLineEmbeddable());
            }
        });
    }


    private List<InvoiceLevelAllowance> extractInvoiceLevelDiscount(List<InvoiceLine> invoiceLines){
        List<InvoiceLevelAllowance> invoiceLevelDiscounts = new ArrayList<>();
        LOGGER.info("current invoice lines [{}]", invoiceLines);
        Iterator<InvoiceLine> iterator = invoiceLines.iterator();
        while (iterator.hasNext()) {
            InvoiceLine inv = iterator.next();
            if (StringUtils.isNotEmpty(inv.getIsDiscount()) && inv.getIsDiscount().equals("Y")) {
                invoiceLevelDiscounts.add(InvoiceLevelAllowance.builder().amount(inv.getLineAmount().abs()).allowanceCode("95").exemptionCode(inv.getExemptionCode()).exemptionOtherTypeDesc(inv.getExemptionOtherTypeDesc()).taxRate(inv.getTaxRate()).build());
                iterator.remove();
            }
        }
        LOGGER.info("current invoice lines without discount [{}]", invoiceLines);
        return invoiceLevelDiscounts;
    }

}
