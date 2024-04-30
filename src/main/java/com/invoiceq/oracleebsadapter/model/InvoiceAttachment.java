package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "T_ATTACHMENTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceAttachment {
    @Id
    @SequenceGenerator(name = "attachment_Seq", sequenceName = "attachment_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "attachment_Seq")
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
