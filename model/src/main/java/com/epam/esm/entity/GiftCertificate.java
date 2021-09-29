package com.epam.esm.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class GiftCertificate {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Duration duration;
    private LocalDateTime createDate;
    private LocalDateTime lastUpdateDate;
}
