package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
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
public class CreditNoteTransformer extends AbstractInvoiceTransformer<CreditNoteRequest> {
    private final Logger LOGGER = LoggerFactory.getLogger(CreditNoteTransformer.class);


    @Override
    public List<CreditNoteRequest> transform(List<InvoiceHeader> credits) {
        List<CreditNoteRequest> transformedCredits = new ArrayList<>();
        Template template = getMarkerTemplate("CreditNoteTemplate.ftl");
        credits.forEach(credit -> {
            boolean isValidLinking = checkTheLinkedInvoices(credit);
            if (isValidLinking){
                try {
                    Long begin = System.currentTimeMillis();
                    Map<String, Object> groupContext = retrieveGroupDetails(credit);
                    boolean isReadyToSend = isMemoReadyToSend(groupContext);
                    if (isReadyToSend) {
                        reformatObjectData(credit);
                        List<InvoiceLine> invoiceLines = credit.getInvoiceLines();
                        StringWriter writer = new StringWriter();
                        Map<String, Object> data = new HashMap<>();
                        data.put("inv", credit);
                        data.put("invoiceLines", invoiceLines);
                        data.put("isGroupReference",groupContext.get("isGroupReference"));
                        data.put("invoiceQReference",groupContext.get("invoiceQReference"));
                        data.put("groupedInvoiceIQReferences",groupContext.get("GroupReference"));
                        data.put("transformer", this);
                        LOGGER.info("Data is {}", data);
                        template.process(data, writer);
                        CreditNoteRequest creditNoteRequest = mapper.readValue(writer.toString(), CreditNoteRequest.class);
                        transformedCredits.add(creditNoteRequest);
                        invoiceHeadersRepository.updateReadStatus(ZatcaStatus.ZATCA_LOCKED, credit.getInvoiceId());
                        Long end = System.currentTimeMillis();
                        LOGGER.info("time to execute the transformation {} millisecond", end - begin);
                    }
                } catch (TemplateException | IOException e) {
                    LOGGER.error("error happened {}", e.getMessage());
                    handleFTLException(e.getMessage(),credit.getInvoiceId());

                }
            }

        });
        return transformedCredits;
    }
}
