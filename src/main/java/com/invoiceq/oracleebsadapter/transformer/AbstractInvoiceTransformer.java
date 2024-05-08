package com.invoiceq.oracleebsadapter.transformer;

import com.Invoiceq.connector.model.EntitySchemeId;
import com.Invoiceq.connector.model.InvoiceType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.invoiceq.oracleebsadapter.model.*;
import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import com.invoiceq.oracleebsadapter.repository.InvoiceLineRepository;
import com.invoiceq.oracleebsadapter.repository.PrepaymentRepository;
import com.invoiceq.oracleebsadapter.service.OutwardInvoiceService;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

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

    @Autowired
    protected PrepaymentRepository prepaymentRepository;
    public abstract List<T> transform(List<InvoiceHeader> invoices);

    protected Template getMarkerTemplate(String templateName) {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setNumberFormat("@@");
        configuration.setLocale(Locale.US);
        Template template = null;
        try {
            FileTemplateLoader templateLoader = new FileTemplateLoader(new File("invoiceTemplates"));
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
    public static String convertIso2CodeToIso3Code(String is2Code){
        try {
            Locale locale = new Locale("",is2Code.trim());
            return locale.getISO3Country();
        }catch (Exception e){
            LOGGER.error("error happened", e);
        }
        return "";
    }
    public static String convertToValidSchemeId(String schemeId){
        String result  = "";
        try {
            result = (StringUtils.equalsIgnoreCase("700",schemeId)) ? EntitySchemeId.NUMBER_700.name() : schemeId ;
            return EnumUtils.isValidEnumIgnoreCase(EntitySchemeId.class, result) ?  EnumUtils.getEnumIgnoreCase(EntitySchemeId.class,result).name() : null;
        }catch (Exception e){
            LOGGER.error("error happened when map to entitySchemeId {}", schemeId, e);
        }
        return "";
    }
    protected void handleFTLException(String message, String invoiceId) {
        LOGGER.error("Something went wrong with Transforming Invoice [{}]", invoiceId);
        invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.TECHNICAL_FAILED, invoiceId);
        invoiceHeadersRepository.updateFailedStatus(invoiceId,message);
    }
    protected boolean checkTheLinkedInvoices(InvoiceHeader memo) {
        String invoiceReferences = memo.getMemoNo();
        String invoiceQReferences = memo.getMemoInvoiceQReference();
        boolean isLinkedToValidInvoices = StringUtils.isNotBlank(invoiceReferences) || StringUtils.isNotBlank(invoiceQReferences);
        if (isLinkedToValidInvoices){
            return true;
        }
        updateStatusForUnlinkedMemo(memo);
        return false;
    }
    private void updateStatusForUnlinkedMemo(InvoiceHeader memo) {
        LOGGER.info("Update Status To [{}] For Unlinked Memo [{}]", ZatcaStatus.BUSINESS_FAILED,memo.getInvoiceId());
        invoiceHeadersRepository.updateZatcaStatus(ZatcaStatus.BUSINESS_FAILED, memo.getInvoiceId());
        String errorMessage = "The Memo#"+memo.getInvoiceId()+" Still Not Linked to Invoice";
        invoiceHeadersRepository.updateFailedStatus(memo.getInvoiceId(),errorMessage);
    }
    protected Map <String,Object> retrieveGroupDetails (InvoiceHeader memo){
        Map <String,Object> map = new HashMap<>();
        String invoiceReferences = memo.getMemoNo();
        String invoiceQReferences = memo.getMemoInvoiceQReference();
        boolean isGroupReference= (StringUtils.isNotBlank(invoiceReferences) && invoiceReferences.contains(",")) || (StringUtils.isNotBlank(invoiceQReferences) && invoiceQReferences.contains(","));
        String references = StringUtils.defaultIfBlank(invoiceQReferences, invoiceReferences);
        String invoiceQReference = isGroupReference?references.split(",")[0]:references;
        map.put("isGroupReference",isGroupReference);
        map.put("GroupReference",references);
        map.put("isHistorical",memo.getIsHistorical());
        map.put("invoiceQReference",invoiceQReference);
        return map;
    }
protected boolean isMemoReadyToSend(Map<String, Object> groupContext) {
    boolean isHistorical = (boolean) groupContext.getOrDefault("isHistorical", false);
    boolean isGroupReference = (boolean) groupContext.getOrDefault("isGroupReference", false);
    String reference = (String) groupContext.getOrDefault("invoiceQReference","");
    if (isHistorical) {
        return true;
    }

    if (!isGroupReference) {
        return isInvoiceReady(reference);
    }

    String[] references = groupContext.getOrDefault("GroupReference", "").toString().split(",");
    return Arrays.stream(references)
            .map(this::isInvoiceReady)
            .reduce(false, (a, b) -> a || b);
}

    private boolean isInvoiceReady(String invoiceReference) {
        Optional<InvoiceHeader> originalInvoice = invoiceHeadersRepository.findByReference(invoiceReference);
        return originalInvoice.map(invoice -> invoice.getStatus() == ZatcaStatus.SUCCESS).orElse(false);
    }
    protected void addPrePaymentDetailsIfExists(List<InvoiceLine> invoiceLines) {
        if (!CollectionUtils.isEmpty(invoiceLines)) {
            invoiceLines.forEach(line -> {
                line.setPrepaymentDetails(extractPrePaymentDetails(line));
            });
        }
    }

    protected List<Prepayment> extractPrePaymentDetails(InvoiceLine invoiceLine) {
        List<Prepayment> prepaymentDetailsList = new ArrayList<>();
        if (StringUtils.isNotBlank(invoiceLine.getPrepaymentInvoiceRef())){
            String [] references = new String[0];
            String prepaymentInvoiceRef= invoiceLine.getPrepaymentInvoiceRef();
            if (prepaymentInvoiceRef.contains(",")){
                references = prepaymentInvoiceRef.split(",");
            }else {
                references = new String[]{prepaymentInvoiceRef};
            }
            processPrepayment(references,prepaymentDetailsList,invoiceLine.getInvoiceLineEmbeddable());
        }
        return prepaymentDetailsList;
    }

    protected void processPrepayment(String[] references, List<Prepayment> prepaymentDetailsList, InvoiceLineEmbeddable invoiceLineEmbeddable) {
        if (!CollectionUtils.isEmpty(Arrays.asList(references))){
            for (String reference : references) {
                Prepayment linePrepaymentDetails =new Prepayment();
                Optional<Prepayment> prepaymentInfo = prepaymentRepository.findByInvoiceIdAndInvoiceSequenceAndLineNumber(reference,invoiceLineEmbeddable.getInvoiceSequence(),invoiceLineEmbeddable.getLineNumber());
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
