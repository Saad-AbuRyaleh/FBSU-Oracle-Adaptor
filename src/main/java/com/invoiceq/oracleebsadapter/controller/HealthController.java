package com.invoiceq.oracleebsadapter.controller;

import com.invoiceq.oracleebsadapter.repository.InvoiceHeadersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1")
public class HealthController {
    @Autowired
    private InvoiceHeadersRepository invoiceHeadersRepository;

    @GetMapping("health")
    public ResponseEntity<?> healthCheck() {
        try {
            invoiceHeadersRepository.count();
            return ResponseEntity.ok("status :200 OK");
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}
