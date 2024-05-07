package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "T_PREPAID_INVOICES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Prepayment implements Serializable {
    @Id
    @Column(name = "INVOICE_SEQ ", nullable = false)
    private Long invoiceSequence;

    @Column(name = "PREPAYMENT_INVOICE_NO", nullable = false)
    private String invoiceId;

    @Column(name = "PREPAYMENT_INVOICE_DATE")
    private String invoiceDate;

    @Column(name = "IS_HISTORICAL")
    private Boolean isHistorical;

    @Column(name = "PREPAYMENT_TAX_AMOUNT")
    private BigDecimal prepaymentTaxAmount;

    @Column(name = "PREPAYMENT_TAXABLE_AMOUNT")
    private BigDecimal prepaymentTaxableAmount;

    @Column(name = "PREPAYMENT_INVOICE_REF")
    private String invoiceQReference;

    @Transient
    private String prePaymentInvoiceDate;
}
