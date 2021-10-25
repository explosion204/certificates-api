package com.epam.esm.controller.hateoas;

import org.springframework.hateoas.Link;
import java.util.List;

public interface HateoasProvider<T> {
    List<Link> provide(T object);
}
