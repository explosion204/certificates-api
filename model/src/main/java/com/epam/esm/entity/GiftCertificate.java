package com.epam.esm.entity;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Set;

@Component
public record GiftCertificate(
        long id,
        String description,
        BigDecimal price,
        Duration duration,
        ZonedDateTime creationDate,
        ZonedDateTime lastUpdateDate,
        Set<Tag> tags
) {}
