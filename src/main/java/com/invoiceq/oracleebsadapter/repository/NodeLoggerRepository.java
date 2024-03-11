package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.NodeLogger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeLoggerRepository extends JpaRepository<NodeLogger, Long> {

    Optional<NodeLogger> findNodeLoggerByServiceCode(String serviceCode);

    Optional<NodeLogger> findFirstByOrderByLastActiveOnDesc();
}
