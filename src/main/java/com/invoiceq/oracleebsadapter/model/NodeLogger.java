package com.invoiceq.oracleebsadapter.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class NodeLogger {
    @Id
    @SequenceGenerator(name="node_Seq",sequenceName="node_sequence",allocationSize = 1)
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="node_Seq")
    @Column
    private Long id;

    @Column
    private LocalDateTime lastActiveOn;

    @Column
    private String serviceCode;
}
