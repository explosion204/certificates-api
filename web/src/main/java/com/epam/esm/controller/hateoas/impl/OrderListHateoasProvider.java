package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.OrderController;
import com.epam.esm.controller.hateoas.ListHateoasProvider;
import com.epam.esm.dto.OrderDto;
import org.springframework.stereotype.Component;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_ORDERS_REL;

@Component
public class OrderListHateoasProvider extends ListHateoasProvider<OrderDto> {
    @Override
    protected Class<?> getControllerClass() {
        return OrderController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_ORDERS_REL;
    }
}
