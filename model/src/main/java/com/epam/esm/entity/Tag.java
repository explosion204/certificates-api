package com.epam.esm.entity;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Tag {
    private long id;
    private String name;
}
