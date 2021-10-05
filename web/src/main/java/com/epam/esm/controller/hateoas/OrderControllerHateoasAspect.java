package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.OrderDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class OrderControllerHateoasAspect extends BaseHateoasAspect<OrderDto> {
    OrderControllerHateoasAspect() {
        super(OrderDto.class);
    }

    @AfterReturning(pointcut = SINGLE_ENTITY_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<OrderDto> singleEntityAdvice(ResponseEntity<OrderDto> responseEntity) {
        return applyForSingleEntity(responseEntity);
    }

    @AfterReturning(pointcut = LIST_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<List<OrderDto>> listAdvice(ResponseEntity<List<OrderDto>> responseEntity) {
        return applyForList(responseEntity);
    }

    @Override
    void processModel(OrderDto model) {
        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link listLink = LinkConstructor.constructListLink(controllerClass);
        Link createLink = LinkConstructor.constructCreateLink(controllerClass, model);

        model.addLinks(selfLink, listLink, createLink);
    }
}
