package com.invoiceq.oracleebsadapter.scheduler;

import com.invoiceq.oracleebsadapter.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;


@Component
public class InvoicesReaderScheduler {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvoicesReaderScheduler.class);
    @Autowired
    private PrePaymentService prePaymentService;
    @Autowired
    private OutwardInvoiceService outwardInvoiceService;

    @Autowired
    private CreditNoteService creditNoteService;

    @Autowired
    private DebitNoteService debitNoteService;

    @Autowired
    private NodeLoggerService nodeLoggerService;

    @Value("${instance.node-code}")
    private String serviceCode;

    @Qualifier("readOutwardExecutor")
    @Autowired
    private ThreadPoolTaskExecutor outwardTaskExecutor;

    @Qualifier("readCreditExecutor")
    @Autowired
    private ThreadPoolTaskExecutor creditTaskExecutor;

    @Qualifier("readDebitExecutor")
    @Autowired
    private ThreadPoolTaskExecutor debitTaskExecutor;

    @Qualifier("readPrePaymentExecutor")
    @Autowired
    private ThreadPoolTaskExecutor prePaymentTaskExecutor;

    @Scheduled(fixedRateString = "${scheduler.delay:PT1M}")
    public void prePaymentReader() {
        if (prePaymentTaskExecutor.getActiveCount() == 0 && nodeLoggerService.isEnabledToRun(serviceCode)) {
            prePaymentTaskExecutor.execute(() -> {
                LOGGER.info("Start check for unsigned prePayment invoices");
                try {
                    prePaymentService.handlePendingInvoices();
                } catch (Exception e) {
                    LOGGER.error("error happened", e);
                }
                LOGGER.info("Finish checking for unsigned prePayment invoices");
            });
        }

    }

    @Scheduled(fixedRateString = "${scheduler.delay:PT1M}")
    public void invoiceReader() {
        if (outwardTaskExecutor.getActiveCount() == 0 && nodeLoggerService.isEnabledToRun(serviceCode)) {
            outwardTaskExecutor.execute(() -> {
                LOGGER.info("Start check for unsigned invoices");
                try {
                    outwardInvoiceService.handlePendingInvoices();
                } catch (Exception e) {
                    LOGGER.error("error happened", e);
                }
                LOGGER.info("Finish checking for unsigned invoices");
            });
        }

    }


    @Scheduled(fixedRateString = "${scheduler.delay:PT1M}")
    public void creditReader() {
        if (creditTaskExecutor.getActiveCount() == 0 && nodeLoggerService.isEnabledToRun(serviceCode)) {
            creditTaskExecutor.execute(() -> {
                LOGGER.info("Start check for unsigned credit notes");
                try {
                    creditNoteService.handlePendingCredits();
                } catch (Exception e) {
                    LOGGER.error("error happened", e);
                }
                LOGGER.info("Finish checking for unsigned credit notes");
            });
        }
    }
    @Scheduled(fixedRateString = "${scheduler.delay:PT1M}")
    public void debitReader() {
        if (debitTaskExecutor.getActiveCount() == 0 && nodeLoggerService.isEnabledToRun(serviceCode)) {
            debitTaskExecutor.execute(() -> {
                LOGGER.info("Start check for unsigned debit notes");
                try {
                    debitNoteService.handlePendingDebits();
                } catch (Exception e) {
                    LOGGER.error("error happened", e);
                }
                LOGGER.info("Finish checking for unsigned debit notes");
            });
        }
    }
}
