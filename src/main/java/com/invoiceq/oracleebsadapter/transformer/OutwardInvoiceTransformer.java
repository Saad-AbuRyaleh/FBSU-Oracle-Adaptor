package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.invoiceq.oracleebsadapter.model.*;
import freemarker.template.Template;
import freemarker.template.TemplateException;
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
                List<InvoiceLine> invoiceLines = invoiceHeader.getInvoiceLines();
                addPrePaymentDetailsIfExists (invoiceLines);
                StringWriter writer = new StringWriter();
                Map<String, Object> data = new HashMap<>();
                reformatObjectData(invoiceHeader);
                data.put("inv", invoiceHeader);
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
                LOGGER.error("error happened {}", e.getMessage());
                handleFTLException(e.getMessage(),invoiceHeader.getInvoiceId());
            }
        });
        return transformedInvoices;
    }

}
