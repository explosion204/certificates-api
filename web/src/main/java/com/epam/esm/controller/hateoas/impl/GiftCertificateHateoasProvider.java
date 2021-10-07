package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.LinkConstructor;
import com.epam.esm.dto.GiftCertificateDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GiftCertificateHateoasProvider implements HateoasProvider<GiftCertificateDto> {
    @Override
    public List<Link> provide(GiftCertificateDto model) {
        Class<?> controllerClass = GiftCertificateController.class;

        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass);

        return List.of(selfLink, allResourcesLink);
    }
}
