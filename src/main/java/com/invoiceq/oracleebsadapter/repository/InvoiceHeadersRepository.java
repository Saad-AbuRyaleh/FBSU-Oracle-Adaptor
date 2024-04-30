package com.invoiceq.oracleebsadapter.repository;


import com.invoiceq.oracleebsadapter.model.InvoiceHeader;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

public interface InvoiceHeadersRepository extends JpaRepository<InvoiceHeader, Long> {

    Optional<List<InvoiceHeader>> findByStatusAndInvoiceType(ZatcaStatus status, String invoiceType);

    Optional<InvoiceHeader> findFirstByInvoiceIdOrderByInvoiceSequenceDesc(String invoiceId);

    Optional<InvoiceHeader> findByInvoiceId(String invoiceId);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader ih set ih.status=:status where ih.invoiceId=:invoiceId")
    void updateZatcaStatus(@Param("status") ZatcaStatus zatcaStatus, @Param("invoiceId") String invoiceId);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader ih set ih.status=:status where ih.invoiceId=:invoiceId")
    void updateReadStatus(@Param("status") ZatcaStatus zatcaStatus, @Param("invoiceId") String invoiceId);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader ih set ih.reference=:reference,ih.stringQrCode=:stringQrCode,ih.payableRoundingAmount=:payableRoundingAmount,ih.createdOn=:createdOn where ih.invoiceId=:invoiceId")
    void updateSuccessfullResponse(@Param("invoiceId") String invoiceId, @Param("reference") String reference, @Param("stringQrCode") String stringQrCode, @Param("payableRoundingAmount") BigDecimal payableRoundingAmount, @Param("createdOn") Timestamp createdOn);

    @Modifying
    @Transactional
    @Query("update InvoiceHeader ih set ih.errorDetails=:errorDetails where ih.invoiceId=:invoiceId")
    void updateFailedStatus(@Param("invoiceId") String invoiceId, @Param("errorDetails") String errorDetails);


}







