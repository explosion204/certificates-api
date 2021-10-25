package com.epam.esm.controller.hateoas.impl;

import com.epam.esm.controller.GiftCertificateController;
import com.epam.esm.controller.hateoas.ListHateoasProvider;
import com.epam.esm.dto.GiftCertificateDto;
import org.springframework.stereotype.Component;

import static com.epam.esm.controller.hateoas.impl.ResourceRelName.ALL_CERTIFICATES_REL;

@Component
public class GiftCertificateListHateoasProvider extends ListHateoasProvider<GiftCertificateDto> {
    @Override
    protected Class<?> getControllerClass() {
        return GiftCertificateController.class;
    }

    @Override
    protected String getAllResourcesRel() {
        return ALL_CERTIFICATES_REL;
    }
}
