package com.invoiceq.oracleebsadapter.service;

import com.Invoiceq.connector.model.EntitySchemeId;
import com.Invoiceq.connector.model.InvoiceType;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.invoiceq.oracleebsadapter.model.SaptcoZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.repository.InvoiceLineReposiroty;
import com.invoiceq.oracleebsadapter.repository.SaptcoZatcaHeaderErpRepository;
import freemarker.cache.FileTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.time.LocalDateTime;
import java.time.LocalTime;
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

     protected final Map<String, InvoiceType> invoiceTypes = Map.of("B2B",InvoiceType.NORMAL,"B2C",InvoiceType.SIMPLE);


     @Autowired
     protected InvoiceLineReposiroty invoiceLineReposiroty;

     @Autowired
     protected SaptcoZatcaHeaderErpRepository saptcoZatcaHeaderErpRepository;


     public abstract List<T> transform(List<SaptcoZatcaHeaderERP> invoices);

     protected Template getInvoiceTemplate(String templateName){
          Configuration cfg = new Configuration(Configuration.VERSION_2_3_31);
          cfg.setNumberFormat("@@");
          cfg.setLocale(Locale.US);
          Template template = null;
          try {
               FileTemplateLoader templateLoader = new FileTemplateLoader(new File("invoiceTemplates"));
               cfg.setTemplateLoader(templateLoader);
               cfg.setFallbackOnNullLoopVariable(false);
               template = cfg.getTemplate(templateName);
          } catch (Exception e) {
               LOGGER.error("Error happened", e);
          }
          return template;
     }


     protected void reformatObjectData(SaptcoZatcaHeaderERP inv){
          try {
               LOGGER.info("request issue date :{}",inv.getIssueDate()+inv.getIssueTime());
               inv.setTotInvoiceDiscount(Objects.nonNull(inv.getTotInvoiceDiscount())?inv.getTotInvoiceDiscount().abs():null);
               inv.setInvoiceQIssueDate(LocalDateTime.parse(inv.getIssueDate()+" "+inv.getIssueTime(),inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
               if (StringUtils.isNotEmpty(inv.getSupplyFromDate())) {
                    inv.setSupplyFromZonedDate(LocalDateTime.parse(inv.getSupplyFromDate(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
               }
               if (StringUtils.isNotEmpty(inv.getSupplyEndDate())) {
                    inv.setSupplyEndZonedDate(LocalDateTime.parse(inv.getSupplyEndDate(), inputFormatter).atZone(ZoneId.of("Asia/Riyadh")).format(outputFormatter));
               }
               LOGGER.info("invoiceq issue date :{}",inv.getInvoiceQIssueDate());
               inv.setInvType(invoiceTypes.get(inv.getInvoiceBussType()));
          }
          catch (Exception e){
               LOGGER.error("error happened",e);
          }
     }

     protected String generateRandomCode(){
          StringBuilder randomCode = new StringBuilder();
          String abc = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
          for(int i=0;i<3;i++){
               randomCode.append(abc.charAt(rand.nextInt(abc.length())));
          }
          randomCode.append("-");
          randomCode.append(rand.nextInt(999999)+10000);
          return randomCode.toString();
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

}
