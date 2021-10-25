package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.hateoas.ModelHateoasProvider;
import com.epam.esm.dto.GiftCertificateDto;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_CERTIFICATES_REL;

@Component
public class GiftCertificateHateoasProvider extends ModelHateoasProvider<GiftCertificateDto> {
    @Override
    protected List<Link> addSpecificLinks(List<Link> baseLinks, GiftCertificateDto model) {
        return baseLinks;
    }

    @Override
    protected Class<?> getControllerClass() {
        return GiftCertificateController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_CERTIFICATES_REL;
    }


}
