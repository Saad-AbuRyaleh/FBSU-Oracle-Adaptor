package com.invoiceq.oracleebsadapter.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "node_logger")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NodeLogger {
    @Id
    @SequenceGenerator(name = "node_Seq", sequenceName = "node_sequence", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "node_Seq")
    @Column
    private Long id;

    @Column
    private LocalDateTime lastActiveOn;

    @Column
    private String serviceCode;
}
