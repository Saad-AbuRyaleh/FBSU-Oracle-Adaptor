package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.InvoiceAttachment;
import org.springframework.data.jpa.repository.JpaRepository;


public interface InvoiceAttachmentRepository extends JpaRepository<InvoiceAttachment,Long> {
}
