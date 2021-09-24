package com.epam.esm.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
public class GiftCertificate {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Duration duration;
    private ZonedDateTime createDate;
    private ZonedDateTime lastUpdateDate;
}