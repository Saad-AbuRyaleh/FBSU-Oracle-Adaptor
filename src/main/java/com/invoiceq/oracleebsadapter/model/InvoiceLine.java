package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "T_INVOICE_LINES")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLine {
    @Id
    @Column(name = "INVOICE_SEQ", nullable = false)
    private Long invoiceSequence;

    @Column(name = "LINE_NUMBER", nullable = false)
    private Long lineNumber;

    @Column(name = "QUANTITY_INVOICED")
    private BigDecimal quantityInvoiced;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Column(name = "TOT_TAX")
    private BigDecimal totTax;

    @Column(name = "LINE_AMOUNT")
    private BigDecimal lineAmount;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TAX_RATE")
    private Short taxRate;

    @Column(name = "UNIT_SELLING_PRICE")
    private BigDecimal unitSellingPrice;

    @Column(name = "INVOICE_CAT")
    private String invoiceCat;

    @Column(name = "EXEMPTION_PERCENTAGE")
    private Short exemptionPercentage;

    @Column(name = "EXEMPTION_CODE")
    private String exemptionCode;

    @Column(name = "EXEMPTION_OTHER_TYPE_DESC")
    private String exemptionOtherTypeDesc;

    @Column(name = "PREPAYMENT_INVOICE_DATE")
    private String prepaymentInvoiceDate;

    @Column(name = "IS_HISTORICAL")
    private boolean isHistorical;

    @Column(name = "PREPAYMENT_TAX_AMOUNT")
    private BigDecimal prepaymentTaxAmount;

    @Column(name = "PREPAYMENT_TAXABLE_AMOUNT")
    private BigDecimal prepaymentTaxableAmount;

    @Column(name = "PREPAYMENT_INVOICE_REF")
    private String prepaymentInvoiceRef;

}
