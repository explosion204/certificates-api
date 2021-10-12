package com.epam.esm.entity.audit;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "audit_table")
public class AuditEntity {
    @Id
    @GeneratedValue
    private long id;

    @Enumerated(EnumType.STRING)
    private Operation operation;
    private String entityName;
    private LocalDateTime timestamp;

    public enum Operation {
        CREATE, UPDATE, DELETE
    }
}
