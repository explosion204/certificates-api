package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.hateoas.Link;

import java.util.List;

public interface HateoasProvider<T extends IdentifiableDto> {
    List<Link> provide(T model);
}
