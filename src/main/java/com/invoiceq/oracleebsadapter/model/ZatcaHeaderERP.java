package com.invoiceq.oracleebsadapter.model;

import com.Invoiceq.connector.model.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


@Entity
@Table(name = "XXSAPTCO_ZATCA_HEADER_ERP")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZatcaHeaderERP implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEQ_ID", nullable = false)
    private Long seqId;

    @Column(name = "CUSTOMER_TRX_ID", nullable = false)
    private Long customerTrxId;

    @Column(name = "INVOICE_ID", nullable = false)
    private String invoiceId;

    @Column(name = "ISSUE_DATE")
    private String issueDate;

    @Column(name = "ISSUE_TIME")
    private String issueTime;

    @Column(name = "SUPPLY_FROM_DATE")
    private String supplyFromDate;

    @Column(name = "SUPPLY_END_DATE")
    private String supplyEndDate;

    @Column(name = "ORGANIZATION_UNIT")
    private Short organizationUnit;

    @Column(name = "CUST_NAME")
    private String custName;

    @Column(name = "CUST_NAME_AR")
    private String custNameAr;

    @Column(name = "CUST_NUMBER")
    private String custNumber;

    @Column(name = "CUST_VAT")
    private String custVat;

    @Column(name = "CUST_IDENT_NUMBER")
    private String custIdentNumber;

    @Column(name = "CUST_IDENT_TYPE")
    private String custIdentType;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "CITY")
    private String city;

    @Column(name = "POSTAL_CODE")
    private String postalCode;

    @Column(name = "DISTRICT")
    private String district;

    @Column(name = "ADDITIONAL_NO")
    private String additionalNo;

    @Column(name = "ADDRESS_STREET")
    private String addressStreet;

    @Column(name = "BUILDING_NO")
    private String buildingNo;

    @Column(name = "INVOICE_TYPE")
    private String invoiceType;

    @Column(name = "TOTAL_TAX")
    private BigDecimal totalTax;

    @Column(name = "TOTAL_AMOUNT")
    private BigDecimal totalAmount;

    @Column(name = "TOTAL_DUE_AMOUNT")
    private BigDecimal totalDueAmount;

    @Column(name = "TOT_INVOICE_DISCOUNT")
    private BigDecimal totInvoiceDiscount;

    @Column(name = "TOTAL_ADVANCED_AMOUNT")
    private BigDecimal totalAdvancedAmount;

    @Column(name = "CREDIT_MEMO_COMMENT")
    private String creditMemoComment;

    @Column(name = "CREDIT_MEMO_NO")
    private String creditMemoNo;

    @Column(name = "READ_FROM_ERP")
    private Short readFromErp;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private ZatcaStatus status;

    @Column(name = "REFERENCE")
    private String reference;

    @Column(name = "UUID")
    private String uuid;

    @Column(name = "SIGNATURE")
    private String signature;

    @Column(name = "INVOICE_BUSS_TYPE")
    private String invoiceBussType;

    @Column(name = "ERROR_DETAILS")
    @Lob
    private String errorDetails;

    @Column(name = "PAYABLE_ROUNDING_AMOUNT")
    private BigDecimal payableRoundingAmount;

    @Transient
    private InvoiceType invType;

    @Transient
    private String invoiceQIssueDate;

    @Transient
    private String supplyFromZonedDate;

    @Transient
    private String supplyEndZonedDate;

    @Transient
    private String originalInvoiceqReference;

    @Transient
    private Boolean isHistorical;
}
