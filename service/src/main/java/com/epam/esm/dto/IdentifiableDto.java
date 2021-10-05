package com.epam.esm.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.Arrays;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class IdentifiableDto<T extends RepresentationModel<T>> extends RepresentationModel<T> {
    private long id;

    public void addLinks(Link ... links) {
        Arrays.stream(links).forEach(this::add);
    }
}
