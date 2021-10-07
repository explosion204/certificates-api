package com.epam.esm.entity;

import com.epam.esm.entity.audit.AuditListener;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "app_order") // "order" is reserved
@EntityListeners(AuditListener.class)
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal cost;
    private LocalDateTime purchaseDate;

    @ManyToOne
    @JoinColumn(name = "id_user", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @OneToOne
    @JoinColumn(name = "id_certificate", referencedColumnName = "id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private GiftCertificate certificate;
}
