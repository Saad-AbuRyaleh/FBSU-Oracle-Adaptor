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
            try {
                Long begin = System.currentTimeMillis();
                InvoiceHeader originalInvoice = processAndGetOriginalInvoiceInfo(credit);
                if (readyToSend(Objects.nonNull(originalInvoice) && Objects.equals(originalInvoice.getStatus(), ZatcaStatus.SUCCESS), credit.isHistorical())) {
                    reformatObjectData(credit);
                    List<InvoiceLine> invoiceLines = invoiceLineRepository.findAllByInvoiceSequence(credit.getInvoiceSequence());
                    List<InvoiceLine> originalInvoiceLines = credit.isHistorical() ? null : invoiceLineRepository.findAllByInvoiceSequence(originalInvoice.getInvoiceSequence());
                    StringWriter writer = new StringWriter();
                    Map<String, Object> data = new HashMap<>();
                    data.put("inv", credit);
                    data.put("invoiceLines", invoiceLines);
                    data.put("originalInvoice", originalInvoice);
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
                LOGGER.error("error happened", e);

            }

        });
        return transformedCredits;
    }

    private InvoiceHeader processAndGetOriginalInvoiceInfo(InvoiceHeader credit) {
        if (Objects.nonNull(credit.getMemoNo())) {
            Optional<InvoiceHeader> originalInvoice = invoiceHeadersRepository.findFirstByInvoiceIdOrderBySeqIdDesc(credit.getMemoNo());
            return originalInvoice.orElse(null);
        }
        return credit;
    }

    private boolean readyToSend(Boolean originalInvoiceExistAndSuccess, Boolean isHistorical) {
        return BooleanUtils.isTrue(originalInvoiceExistAndSuccess) || BooleanUtils.isTrue(isHistorical);
    }
}
