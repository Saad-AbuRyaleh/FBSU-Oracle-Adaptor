package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.SaptcoZatcaHeaderERP;
import com.invoiceq.oracleebsadapter.model.ZatcaStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SaptcoZatcaHeaderErpRepository extends JpaRepository<SaptcoZatcaHeaderERP, Long> {
//    Optional<List<SaptcoZatcaHeaderERP>> findFirst10ByStatusAndInvoiceType(ZatcaStatus status, String invoiceType);
    Optional<List<SaptcoZatcaHeaderERP>> findAllByStatusAndInvoiceType(ZatcaStatus status, String invoiceType);
    Optional<SaptcoZatcaHeaderERP> findFirstByInvoiceIdOrderBySeqIdDesc(String invoiceId);

    @Transactional
    @Modifying
    @Query("update SaptcoZatcaHeaderERP s  set s.status = :status, s.errorDetails=:errorMessage, s.reference=:invoiceqReference,s.payableRoundingAmount=:payableRoundingAmount where s.invoiceId = :invoiceId and s.seqId = :seqId")
    void updateZatcaStatus(@Param("seqId") Long seqId,@Param("invoiceId") String invoiceId, @Param("status") ZatcaStatus status, @Param("errorMessage") String errorMessage , @Param("invoiceqReference") String invoiceqReference,@Param("payableRoundingAmount") BigDecimal payableRoundingAmount);

    @Transactional
    @Modifying
    @Query("update SaptcoZatcaHeaderERP s  set s.status = :status where s.customerTrxId = :customerTrxId and s.seqId = :seqId")
    void updateReadStatus(@Param("status") ZatcaStatus zatcaStatus,@Param("customerTrxId") Long customerTrxId,@Param("seqId") Long seqId );


    @Procedure(procedureName = "XXSAPTCO_CALL_ZATCA_BEFORE")
    void fetchInvoiceProcedure();

    @Procedure(procedureName = "XXSAPTCO_CALL_ZATCA_AFTER")
    void updateInvoiceStatusProcedure(Long P_CUSTOMER_TRX_ID,String P_STATUS,String P_RESPONSE);
}
