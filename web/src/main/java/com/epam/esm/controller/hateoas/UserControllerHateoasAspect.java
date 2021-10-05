package com.epam.esm.controller.hateoas;

import com.epam.esm.dto.UserDto;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class UserControllerHateoasAspect extends BaseHateoasAspect<UserDto> {
    UserControllerHateoasAspect() {
        super(UserControllerHateoasAspect.class);
    }

    @AfterReturning(pointcut = SINGLE_ENTITY_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<UserDto> singleEntityAdvice(ResponseEntity<UserDto> responseEntity) {
        return applyForSingleEntity(responseEntity);
    }

    @AfterReturning(pointcut = LIST_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<List<UserDto>> listAdvice(ResponseEntity<List<UserDto>> responseEntity) {
        return applyForList(responseEntity);
    }

    @Override
    void processModel(UserDto model) {
        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link listLink = LinkConstructor.constructListLink(controllerClass);

        model.addLinks(selfLink, listLink);
    }
}
