package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.IdentifiableDto;
import org.springframework.hateoas.Link;

import java.util.ArrayList;
import java.util.List;

public abstract class ModelHateoasProvider<T extends IdentifiableDto> implements HateoasProvider<T> {
    @Override
    public final List<Link> provide(T model) {
        Class<?> controllerClass = getControllerClass();
        String allResourcesRel = getAllResourcesRel();
        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass, allResourcesRel);

        List<Link> links = new ArrayList<>();
        links.add(selfLink);
        links.add(allResourcesLink);

        return addSpecificLinks(links, model);
    }

    protected abstract List<Link> addSpecificLinks(List<Link> baseLinks, T model);
    protected abstract Class<?> getControllerClass();
    protected abstract String getAllResourcesRel();
}
