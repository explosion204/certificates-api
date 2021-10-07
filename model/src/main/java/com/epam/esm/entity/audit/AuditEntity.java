package com.epam.esm.entity.audit;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "audit_table")
public class AuditEntity {
    @Id
    @GeneratedValue
    private long id;

    @Enumerated
    private Operation operation;
    private String entityName;
    private LocalDateTime timestamp;

    public enum Operation {
        CREATE, UPDATE, DELETE
    }
}
