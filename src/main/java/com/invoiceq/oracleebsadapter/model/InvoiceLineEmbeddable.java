package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class InvoiceLineEmbeddable implements Serializable {

    @Column(name = "CUSTOMER_TRX_ID", nullable = false)
    private Long customerTrxId;

    @Column(name = "SEQ_ID", nullable = false)
    private Long seqId;

    @Column(name = "LINE_NUMBER", nullable = false)
    private Long lineNumber;

}