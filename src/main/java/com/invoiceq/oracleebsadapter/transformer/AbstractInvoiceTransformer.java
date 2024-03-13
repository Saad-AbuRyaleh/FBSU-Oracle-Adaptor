package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.InvoiceType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.repository.InvoiceLineRepository;
import com.invoiceq.oracleebsadapter.service.OutwardInvoiceService;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;


public abstract class AbstractInvoiceTransformer<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(OutwardInvoiceService.class);

    protected final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected final DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    protected final ObjectMapper mapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setTimeZone(TimeZone.getTimeZone("Asia/Riyadh"));
    protected final Random rand = new Random();

    protected final Map<String, InvoiceType> invoiceTypes = Map.of("B2B", InvoiceType.NORMAL, "B2C", InvoiceType.SIMPLE);

    @Autowired
    protected InvoiceLineRepository invoiceLineRepository;

    @Autowired
    protected InvoiceHeadersRepository invoiceHeadersRepository;


    public abstract List<T> transform(List<InvoiceHeader> invoices);

    protected Template getMarkerTemplate(String templateName) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setNumberFormat("@@");
        configuration.setLocale(Locale.US);
        Template template = null;
        try {
            FileTemplateLoader templateLoader = new FileTemplateLoader(new File("invoiceTemplate"));
            configuration.setTemplateLoader(templateLoader);
            configuration.setFallbackOnNullLoopVariable(false);
            template = configuration.getTemplate(templateName);
        } catch (Exception e) {
            LOGGER.error("An Error Occurred While try to get Free Marker Template", e);
        }
        return template;
    }

    protected void reformatObjectData(InvoiceHeader invoiceHeader) {
        try {
            LOGGER.info("request issue date :{}", invoiceHeader.getIssueDate() + invoiceHeader.getIssueTime());
            invoiceHeader.setTotInvoiceDiscount(Objects.nonNull(invoiceHeader.getTotInvoiceDiscount()) ? invoiceHeader.getTotInvoiceDiscount().abs() : null);
            invoiceHeader.setInvoiceQIssueDate(LocalDateTime.parse(invoiceHeader.getIssueDate() + " " + invoiceHeader.getIssueTime(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
            if (StringUtils.isNotEmpty(invoiceHeader.getSupplyFromDate())) {
                invoiceHeader.setSupplyFromZonedDate(LocalDateTime.parse(invoiceHeader.getSupplyFromDate(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
            }
            if (StringUtils.isNotEmpty(invoiceHeader.getSupplyEndDate())) {
                invoiceHeader.setSupplyEndZonedDate(LocalDateTime.parse(invoiceHeader.getSupplyEndDate(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
            }
            LOGGER.info("invoiceq issue date :{}", invoiceHeader.getInvoiceQIssueDate());
            invoiceHeader.setInvType(invoiceTypes.get(invoiceHeader.getInvoiceBussType()));
        } catch (Exception e) {
            LOGGER.error("error happened", e);
        }
    }

}
