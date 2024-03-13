package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.creditNote.InvoiceLevelAllowance;
import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Service
public class OutwardInvoiceTransformer extends AbstractInvoiceTransformer<UploadOutwardInvoiceRequest> {
    private final Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceTransformer.class);

    @Override
    public List<UploadOutwardInvoiceRequest> transform(List<InvoiceHeader> invoices) {
        List<UploadOutwardInvoiceRequest> transformedInvoices = new ArrayList<>();
        Template template = getMarkerTemplate("OutwardInvoiceTemplate.ftl");
        invoices.forEach(invoiceHeader -> {
            try {
                Long begin = System.currentTimeMillis();
                List<InvoiceLine> invoiceLines = invoiceLineRepository.findAllByInvoiceSequence(invoiceHeader.getInvoiceSequence());
                StringWriter writer = new StringWriter();
                Map<String, Object> data = new HashMap<>();
                reformatObjectData(invoiceHeader);
                data.put("inv", invoiceHeader);
                data.put("invoiceLevelAllowances", extractInvoiceLevelDiscount(invoiceLines));
                data.put("invoiceLines", invoiceLines);
                data.put("transformer", this);
                LOGGER.info("Data is {}", data);
                template.process(data, writer);
                UploadOutwardInvoiceRequest invoice = mapper.readValue(writer.toString(), UploadOutwardInvoiceRequest.class);
                transformedInvoices.add(invoice);
                invoiceHeadersRepository.updateReadStatus(ZatcaStatus.ZATCA_LOCKED, invoice.getInvoiceNumber());
                Long end = System.currentTimeMillis();
                LOGGER.info("time to execute the transformation {} millisecond", end - begin);
            } catch (TemplateException | IOException e) {
                LOGGER.error("error happened", e);
            }
        });
        return transformedInvoices;
    }

    private List<InvoiceLevelAllowance> extractInvoiceLevelDiscount(List<InvoiceLine> invoiceLines) {
        List<InvoiceLevelAllowance> invoiceLevelDiscounts = new ArrayList<>();
        LOGGER.info("current invoice lines [{}]", invoiceLines);
        Iterator<InvoiceLine> iterator = invoiceLines.iterator();
        while (iterator.hasNext()) {
            InvoiceLine invoiceLine = iterator.next();
            if (ObjectUtils.isNotEmpty(invoiceLine.getDiscount())) {
                invoiceLevelDiscounts.add(InvoiceLevelAllowance.builder().amount(invoiceLine.getLineAmount().abs()).allowanceCode("95").exemptionCode(invoiceLine.getExemptionCode()).exemptionOtherTypeDesc(invoiceLine.getExemptionOtherTypeDesc()).taxRate(invoiceLine.getTaxRate()).build());
                iterator.remove();
            }
        }
        LOGGER.info("current invoice lines without discount [{}]", invoiceLines);
        return invoiceLevelDiscounts;
    }


}
