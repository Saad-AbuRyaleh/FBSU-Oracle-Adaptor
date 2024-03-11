//package com.invoiceq.oracleebsadapter.configuration;
//
//import com.Invoiceq.connector.model.creditNote.CreditNoteRequest;
//import com.Invoiceq.connector.model.outward.UploadOutwardInvoiceRequest;
//import com.invoiceq.oracleebsadapter.service.CreditNoteService;
//import com.invoiceq.oracleebsadapter.service.OutwardInvoiceService;
//import org.apache.camel.ExchangePattern;
//import org.apache.camel.builder.RouteBuilder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CamelRouteConfig extends RouteBuilder {
//
//    @Autowired
//    private OutwardInvoiceService outwardInvoiceService;
//
//    @Autowired
//    private CreditNoteService creditNoteService;
//
//    @Override
//    public void configure() {
//        from("direct:createInvoiceRoute")
//                .setExchangePattern(ExchangePattern.InOnly)
//                .process(exchange -> {
//                    outwardInvoiceService.sendAndHandle(exchange.getIn().getBody(UploadOutwardInvoiceRequest.class), (String) exchange.getIn().getHeader("invoiceId"), (Long) exchange.getIn().getHeader("customerTrxId"));
//                })
//                .end();
//
//        from("direct:createCreditRoute")
//                .setExchangePattern(ExchangePattern.InOnly)
//                .process(exchange -> {
//                    creditNoteService.sendAndHandle(exchange.getIn().getBody(CreditNoteRequest.class), (String) exchange.getIn().getHeader("invoiceId"), (Long) exchange.getIn().getHeader("customerTrxId"));
//                })
//                .end();
//    }
//
//
//
//}