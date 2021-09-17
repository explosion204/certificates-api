package com.epam.esm.entity;

import org.springframework.stereotype.Component;

@Component
public record Tag(
        long id,
        String name
) {}
