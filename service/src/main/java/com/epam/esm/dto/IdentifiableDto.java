package com.epam.esm.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public abstract class IdentifiableDto {
    public static final String ID_PROPERTY_NAME = "id";

    @JsonProperty(ID_PROPERTY_NAME)
    private long id;
}
