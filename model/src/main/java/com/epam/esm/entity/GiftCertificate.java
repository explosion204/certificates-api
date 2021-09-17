package com.epam.esm.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Set;

@Component
@Data
public class GiftCertificate {
    private long id;
    private String description;
    private BigDecimal price;
    private Duration duration;
    private ZonedDateTime creationDate;
    private ZonedDateTime lastUpdateDate;
    private Set<Tag> tags;
}
