package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.OrderController;
import com.epam.esm.controller.UserController;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.LinkConstructor;
import com.epam.esm.dto.OrderDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderHateoasProvider implements HateoasProvider<OrderDto> {
    private static final String USER_REL = "user";
    private static final String CERTIFICATE_REL = "certificate";

    @Override
    public List<Link> provide(OrderDto model) {
        Class<?> controllerClass = OrderController.class;

        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass);
        Link certificateLink = LinkConstructor.constructControllerLinkWithId(GiftCertificateController.class,
                model.getCertificateId(), CERTIFICATE_REL);
        Link userLink = LinkConstructor.constructControllerLinkWithId(UserController.class, model.getUserId(), USER_REL);

        return List.of(selfLink, allResourcesLink, certificateLink, userLink);
    }
}
