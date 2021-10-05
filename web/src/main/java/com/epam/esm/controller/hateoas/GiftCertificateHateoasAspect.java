package com.epam.esm.controller.hateoas;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.dto.GiftCertificateDto;
import org.aspectj.lang.annotation.*;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
class GiftCertificateHateoasAspect extends BaseHateoasAspect<GiftCertificateDto> {
    GiftCertificateHateoasAspect() {
        super(GiftCertificateController.class);
    }

    @AfterReturning(pointcut = SINGLE_ENTITY_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<GiftCertificateDto> singleEntityAdvice(ResponseEntity<GiftCertificateDto> responseEntity) {
        return applyForSingleEntity(responseEntity);
    }

    @AfterReturning(pointcut = LIST_POINTCUT_PATTERN, returning = RETURN_VALUE_NAME)
    ResponseEntity<List<GiftCertificateDto>> listAdvice(ResponseEntity<List<GiftCertificateDto>> responseEntity) {
        return applyForList(responseEntity);
    }

    @Override
    void processModel(GiftCertificateDto model) {
        Link selfLink = LinkConstructor.constructSelfLink(controllerClass, model);
        Link listLink = LinkConstructor.constructListLink(controllerClass);
        Link createLink = LinkConstructor.constructCreateLink(controllerClass, model);
        Link updateLink = LinkConstructor.constructUpdateLink(controllerClass, model);
        Link deleteLink = LinkConstructor.constructDeleteLink(controllerClass, model);

        model.addLinks(selfLink, listLink, createLink, updateLink, deleteLink);
    }
}
