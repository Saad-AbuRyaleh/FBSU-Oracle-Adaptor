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

    @Column(name= "INVOICE_SEQ" , nullable = false)
    private long invoiceSequence;

    @Column(name = "LINE_NUMBER" , nullable = false)
    private String lineNumber;

}