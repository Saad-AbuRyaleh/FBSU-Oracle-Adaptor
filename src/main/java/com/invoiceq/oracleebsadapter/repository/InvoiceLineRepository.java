package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, Long> {
    List<InvoiceLine> findAllByInvoiceSequence(Long invoiceSequence);

}
