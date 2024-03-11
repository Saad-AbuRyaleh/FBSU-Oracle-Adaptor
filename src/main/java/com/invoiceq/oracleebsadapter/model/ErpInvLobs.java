package com.invoiceq.oracleebsadapter.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "XX_AR_EREP_INV_LOBS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErpInvLobs {
    @Id
    @SequenceGenerator(name="lobs_seq",sequenceName="lobs_seq",allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="lobs_seq")
    @Column(name = "EREP_LOB_ID", nullable = false)
    private Long erepLobId;

    @Lob
    @Column(name = "BLOB_FILE_CONTENT")
    private byte[] blobFileContent;

    @Column(name = "BLOB_FILE_NAME")
    private String blobFileName;

    @Column(name = "CUSTOMER_TRX_ID")
    private Long customerTrxId;

    @Column(name = "CREATED_BY")
    private Long createdBy;

    @Column(name = "CREATED_ON", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private Timestamp createdOn ;

    @Column(name = "ERP_FILE_STATUS")
    private String erpFileStatus ;

}
