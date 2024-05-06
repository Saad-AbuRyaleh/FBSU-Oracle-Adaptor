package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.InvoiceLine;
import com.invoiceq.oracleebsadapter.model.InvoiceLineEmbeddable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InvoiceLineRepository extends JpaRepository<InvoiceLine, InvoiceLineEmbeddable> {

}
