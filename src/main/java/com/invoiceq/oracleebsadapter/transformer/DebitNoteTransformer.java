package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
import com.Invoiceq.connector.model.debitNote.DebitNoteRequest;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.BooleanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.util.*;

@Service
public class DebitNoteTransformer extends AbstractInvoiceTransformer<DebitNoteRequest>{
    private final Logger LOGGER = LoggerFactory.getLogger(DebitNoteTransformer.class);
    @Override
    public List<DebitNoteRequest> transform(List<InvoiceHeader> debits){
        List<DebitNoteRequest> transformedDebits = new ArrayList<>();
        Template template = getMarkerTemplate("DebitNoteTemplate.ftl");
        debits.forEach(debit -> {
            boolean isValidLinking = checkTheLinkedInvoices(debit);
            if (isValidLinking){
                try {
                    Long begin = System.currentTimeMillis();
                    Map<String, Object> groupContext = retrieveGroupDetails(debit);
                    boolean isReadyToSend = isMemoReadyToSend(groupContext);
                    if (isReadyToSend) {
                        reformatObjectData(debit);
                        List<InvoiceLine> invoiceLines = debit.getInvoiceLines();
                        addPrePaymentDetailsIfExists (invoiceLines);
                        StringWriter writer = new StringWriter();
                        Map<String, Object> data = new HashMap<>();
                        data.put("inv", debit);
                        data.put("invoiceLines", invoiceLines);
                        data.put("isGroupReference",groupContext.get("isGroupReference"));
                        data.put("invoiceQReference",groupContext.get("invoiceQReference"));
                        data.put("groupedInvoiceIQReferences",groupContext.get("GroupReference"));
                        data.put("transformer", this);
                        LOGGER.info("Data is {}", data);
                        template.process(data, writer);
                        DebitNoteRequest debitNoteRequest = mapper.readValue(writer.toString(), DebitNoteRequest.class);
                        transformedDebits.add(debitNoteRequest);
                        invoiceHeadersRepository.updateReadStatus(ZatcaStatus.ZATCA_LOCKED, debit.getInvoiceId());
                        Long end = System.currentTimeMillis();
                        LOGGER.info("time to execute the transformation {} millisecond", end - begin);
                    }
                } catch (TemplateException | IOException e) {
                    LOGGER.error("error happened {}", e.getMessage());
                    handleFTLException(e.getMessage(),debit.getInvoiceId());

                }
            }

        });
        return transformedDebits;
    }
}
