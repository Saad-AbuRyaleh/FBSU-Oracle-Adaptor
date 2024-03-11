package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface InvoiceLineReposiroty extends JpaRepository<InvoiceLine, Long> {
    List<InvoiceLine> findAllByCustomerTrxIdAndSeqId(Long customerTrxId, Long seqId);

    @Transactional
    @Modifying
    @Query("update InvoiceLine i  set i.productCode = :productCode where i.customerTrxId = :customerTrxId and i.seqId = :seqid and i.lineNumber = :lineNumber")
    void updateProductCodeByCustomerTrxIdAndSeqIdAndLineNumber(@Param("productCode") String productCode, @Param("customerTrxId") Long customerTrxId, @Param("seqid") Long seqid, @Param("lineNumber") Long lineNumber);

}