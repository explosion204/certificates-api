package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.hateoas.Link;

import java.util.List;

public abstract class ListHateoasProvider<T extends IdentifiableDto> implements HateoasProvider<List<T>> {
    @Override
    public final List<Link> provide(List<T> object) {
        Class<?> controllerClass = getControllerClass();
        String allResourcesRel = getAllResourcesRel();
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass, allResourcesRel);

        return List.of(allResourcesLink);
    }

    protected abstract Class<?> getControllerClass();
    protected abstract String getAllResourcesRel();
}
