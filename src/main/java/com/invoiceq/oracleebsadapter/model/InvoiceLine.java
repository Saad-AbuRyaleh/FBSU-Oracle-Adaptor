package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "XXSAPTCO_ZATCA_LINE_ERP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceLine {
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "SEQ_ID", nullable = false)
    private Long seqId;

    @Column(name = "LINE_NUMBER", nullable = false)
    private Long lineNumber;

    @Column(name = "CUSTOMER_TRX_ID", nullable = false)
    private Long customerTrxId;

    @Column(name = "QUANTITY_INVOICED")
    private BigDecimal quantityInvoiced;

    @Column(name = "EXTENDED_AMOUNT")
    private BigDecimal extendedAmount;

    @Column(name = "DISCOUNT")
    private BigDecimal discount;

    @Column(name = "IS_DISCOUNT", length = 2)
    private String isDiscount;

    @Column(name = "TOT_TAX")
    private BigDecimal totTax;

    @Column(name = "LINE_AMOUNT")
    private BigDecimal lineAmount;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PRODUCT_CODE")
    private String productCode;

    @Column(name = "PRODUCT_DESC")
    private String description;

    @Column(name = "TAX_RATE")
    private Short taxRate;

    @Column(name = "UNIT_SELLING_PRICE")
    private BigDecimal unitSellingPrice;

    @Column(name = "INVOICE_CAT")
    private String invoiceCat;

    @Column(name = "EXEMPTION_PERCENTAGE")
    private BigDecimal exemptionPercentage;

    @Column(name = "EXEMPTION_CODE")
    private String exemptionCode;

    @Column(name = "EXEMPTION_REASON")
    private String exemptionOtherTypeDesc;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvoiceLine that = (InvoiceLine) o;
        return this.hashCode() == that.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, unitSellingPrice);
    }
}
