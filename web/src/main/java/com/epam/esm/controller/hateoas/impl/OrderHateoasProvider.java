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
    private static final String ALL_ORDERS_REL = "allOrders";
    private static final String ALL_CERTIFICATES_REL = "allCertificates";

    @Override
    public List<Link> provide(OrderDto model) {
        Class<?> controllerClass = OrderController.class;

        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link allResourcesLink = LinkConstructor.constructControllerLink(controllerClass, ALL_ORDERS_REL);
        Link allCertificatesLink = LinkConstructor.constructControllerLink(GiftCertificateController.class,
                ALL_CERTIFICATES_REL);
        Link userLink = LinkConstructor.constructControllerLinkWithId(UserController.class, model.getUserId(), USER_REL);

        return List.of(selfLink, allResourcesLink, allCertificatesLink, userLink);
    }
}
