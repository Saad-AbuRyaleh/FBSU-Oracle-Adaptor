package com.invoiceq.oracleebsadapter.repository;

import com.invoiceq.oracleebsadapter.model.NodeLogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.w3c.dom.Node;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NodeLoggerRepository extends JpaRepository<NodeLogger, Long> {

      Optional<NodeLogger> findNodeLoggerByServiceCode(String serviceCode);
//    @Query("SELECT n FROM NodeLogger n ORDER BY n.lastActiveOn DESC LIMIT 1")
//    Optional<NodeLogger> findLastActiveNode();
//
//    @Query(value = "SELECT  FROM node_logger ORDER BY last_active_on DESC LIMIT 1", nativeQuery = true)
      Optional<NodeLogger> findFirstByOrderByLastActiveOnDesc();
}
