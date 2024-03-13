package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "T_ATTACHMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAttachment {
    @Id
    @Column(name = "ID", nullable = false)
    private Long id;

    @Column(name = "PDF_FILE_PATH ")
    private String pdfFilePath;

    @Column(name = "PDF_FILE_NAME")
    private String pdfFileName;

    @Column(name = "INVOICE_SEQ ", nullable = false)
    private Long invoiceSequence;

    @Column(name = "CREATED_ON", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdOn;

    @Column(name = "STATUS")
    private String status;


}
