package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.Prepayment;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    private void addPrePaymentDetailsIfExists(List<InvoiceLine> invoiceLines) {
        if (!CollectionUtils.isEmpty(invoiceLines)) {
        invoiceLines.forEach(line -> {
            line.setPrepaymentDetails(extractPrePaymentDetails(line));
        });
        }
    }

    private List<Prepayment> extractPrePaymentDetails(InvoiceLine invoiceLine) {
        List<Prepayment> prepaymentDetailsList = new ArrayList<>();
            if (StringUtils.isNotBlank(invoiceLine.getPrepaymentInvoiceRef())){
                String [] references = new String[0];
                String prepaymentInvoiceRef= invoiceLine.getPrepaymentInvoiceRef();
                if (prepaymentInvoiceRef.contains(",")){
                    references = prepaymentInvoiceRef.split(",");
                }else {
                    references = new String[]{prepaymentInvoiceRef};
                }
                processPrepayment(references,prepaymentDetailsList);
            }
        return prepaymentDetailsList;
    }

    private void processPrepayment(String[] references, List<Prepayment> prepaymentDetailsList) {
        if (!CollectionUtils.isEmpty(Arrays.asList(references))){
            for (String reference : references) {
                Prepayment linePrepaymentDetails =new Prepayment();
                Optional<Prepayment> prepaymentInfo = prepaymentRepository.findByInvoiceId(reference);
                if (prepaymentInfo.isPresent()){
                    boolean isHistorical = prepaymentInfo.get().getIsHistorical();
                    linePrepaymentDetails.setInvoiceId(prepaymentInfo.get().getInvoiceId());
                    linePrepaymentDetails.setInvoiceQReference(StringUtils.defaultIfBlank(prepaymentInfo.get().getInvoiceQReference(),searchForInvoiceQReference(prepaymentInfo.get().getInvoiceId())));
                    linePrepaymentDetails.setIsHistorical(isHistorical);
                    linePrepaymentDetails.setInvoiceDate(prepaymentInfo.get().getInvoiceDate());
                    linePrepaymentDetails.setPrePaymentInvoiceDate(LocalDateTime.parse(prepaymentInfo.get().getInvoiceDate(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
                    linePrepaymentDetails.setPrepaymentTaxAmount(prepaymentInfo.get().getPrepaymentTaxAmount());
                    linePrepaymentDetails.setPrepaymentTaxableAmount(prepaymentInfo.get().getPrepaymentTaxableAmount());
                }
                prepaymentDetailsList.add(linePrepaymentDetails);
            }

        }
    }

    private String searchForInvoiceQReference(String invoiceId) {
        Optional<InvoiceHeader> invoiceHeader = invoiceHeadersRepository.findFirstByInvoiceIdOrderByInvoiceSequenceDesc(invoiceId);
        if (invoiceHeader.isPresent()){
            return invoiceHeader.get().getReference();
        }
        return "";
    }

}
