package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.Prepayment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PrepaymentRepository extends JpaRepository<Prepayment,Long> {
    Optional<Prepayment> findByInvoiceIdAndInvoiceSequenceAndLineNumber(String invoiceId,Long invoiceSeq ,String lineNumber);
}
