package com.invoiceq.oracleebsadapter.repository;


import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public interface InvoiceHeadersRepository extends JpaRepository<InvoiceHeader, Long> {

    Optional<List<InvoiceHeader>> findByStatusAndInvoiceType(ZatcaStatus status, String invoiceType);

    Optional<InvoiceHeader> findFirstByInvoiceIdOrderBySeqIdDesc(String invoiceId);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader set InvoiceHeader.status=:status where InvoiceHeader.invoiceId=:invoiceId")
    void updateZatcaStatus(@Param("status") ZatcaStatus zatcaStatus, @Param("invoiceId") String invoiceId);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader set InvoiceHeader.status=:status where InvoiceHeader.invoiceId=:invoiceId")
    void updateReadStatus(@Param("status") ZatcaStatus zatcaStatus, @Param("invoiceId") String invoiceId);


//    @Modifying
//    @Transactional
//    @Query("update InvoiceHeader set InvoiceHeader.status=:status,InvoiceHeader.reference=:reference,InvoiceHeader.stringQrCode=:stringQrCode,InvoiceHeader.payableRoundingAmount=:payableRoundingAmount,InvoiceHeader.errorDetails=:errorDetails,InvoiceHeader.createdOn=:createdOn where InvoiceHeader.invoiceId=:invoiceId")
//    void updateZatcaStatus(@Param("status") ZatcaStatus zatcaStatus, @Param("reference") String iqRef, @Param("stringQrCode") String stringQrCode, @Param("payableRoundingAmount") BigDecimal payableRoundingAmount, @Param("errorDetails") String errorDetails, @Param("createdOn") Timestamp createdOn, @Param("invoiceId") String invoiceId);
}







