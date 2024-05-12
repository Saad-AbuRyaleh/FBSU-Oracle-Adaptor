package com.invoiceq.oracleebsadapter.model;

import com.Invoiceq.connector.model.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;


@Entity
@Table(name = "T_INVOICE_HEADER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceHeader implements Serializable {
    @Id
    @Column(name = "INVOICE_SEQ ", nullable = false)
    private Long invoiceSequence;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    @JoinColumn(name="INVOICE_SEQ", referencedColumnName="INVOICE_SEQ")
    private List<InvoiceLine> invoiceLines;

    @Column(name = "INVOICE_ID", nullable = false)
    private String invoiceId;

    @Column(name = "ISSUE_DATE")
    private String issueDate;

    @Column(name = "ISSUE_TIME")
    private String issueTime;

    @Column(name = "DUE_DATE")
    private String dueDate;

    @Column(name = "SUPPLY_DATE")
    private String supplyFromDate;

    @Column(name = "SUPPLY_END_DATE")
    private String supplyEndDate;

    @Column(name = "ORGANIZATION_UNIT")
    private Long organizationUnit;

    @Column(name = "PO_REFERENCE_NO")
    private Long poReferenceNumber;

    @Column(name = "NOTES")
    private String notes;

    @Column(name = "CUST_NUMBER")
    private String customerNumber;

    @Column(name = "CUST_NAME")
    private String customerName;

    @Column(name = "CUST_NAME_AR")
    private String customerNameAr;

    @Column(name = "CUST_VAT")
    private String customerVat;

    @Column(name = "CUST_IDENT_NUMBER")
    private String customerIdentNumber;

    @Column(name = "CUST_IDENT_TYPE")
    private String customerIdentType;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CITY")
    private String city;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "BLDG_NO")
    private String buildingNo;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "ADDITIONAL_NO")
    private String additionalNo;

    @Column(name = "ADDRESS_STREET")
    private String addressStreet;

    @Column(name = "INVOICE_TYPE")
    private String invoiceType;

    @Column(name = "TOTAL_TAX")
    private BigDecimal totalTax;

    @Column(name = "RETEN_AMOUNT")
    private BigDecimal retentionAmount;

    @Column(name = "ADMIN_CHARGES_AMOUNT ")
    private BigDecimal adminChargesAmount;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "TOTAL_DUE_AMOUNT")
    private BigDecimal totalDueAmount;

    @Column(name = "TOT_INVOICE_DISCOUNT")
    private BigDecimal totInvoiceDiscount;

    @Column(name = "TOTAL_ADVANCED_AMOUNT")
    private BigDecimal totalAdvancedAmount;

    @Column(name = "MEMO_COMMENT")
    private String memoComment;

    @Column(name = "MEMO_ORG_NO")
    private String memoNo;

    @Column(name = "MEMO_ORG_TAX_NUMBER")
    private String memoInvoiceQReference;

    @Column(name = "IS_HISTORICAL")
    private Boolean isHistorical;

    @Column(name = "INVOICE_BUSS_TYPE")
    private String invoiceBussType;

    @Column(name = "BANK_ACCOUNT_NUMBER")
    private String bankAccountNumber;

    @Column(name = "BANK_NAME")
    private String bankName;

    @Column(name = "BANK_SWIFT")
    private String bankSwift;

    @Column(name = "BANK_IBAN")
    private String bankIban;

    @Column(name = "CURRENCY")
    private String currencyIsoCode;

    @Column(name = "TAX_AMOUNT_IN_BASE_CURRENCY")
    private BigDecimal totalTaxInBaseCurrency;

    @Column(name = "CREATED_ON", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdOn;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ZatcaStatus status;

    @Column(name = "ERROR_DETAILS")
    private String errorDetails;

    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "PAYABLE_ROUNDING_AMOUNT")
    private BigDecimal payableRoundingAmount;

    @Column(name = "QR_CODE")
    private String stringQrCode;

    @Transient
    private String invoiceQIssueDate;

    @Transient
    private String supplyFromZonedDate;

    @Transient
    private String supplyEndZonedDate;

    @Transient
    private InvoiceType invType;

}
