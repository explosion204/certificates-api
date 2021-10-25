package com.epam.esm.controller.hateoas.model;

import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.IdentifiableDto;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.domain.Page;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
public class PageHateoasModel<T extends IdentifiableDto> extends RepresentationModel<PageHateoasModel<T>> {
    private List<T> data;
    private int pageNumber;
    private int pageSize;
    private int totalPages;
    private long totalEntities;

    public static <T extends IdentifiableDto> PageHateoasModel<T> build(HateoasProvider<List<T>> hateoasProvider,
                Page<T> page) {
        PageHateoasModel<T> pageHateoasModel = new PageHateoasModel<>();

        pageHateoasModel.data = page.getContent();
        pageHateoasModel.pageNumber = page.getNumber() + 1; // zero based
        pageHateoasModel.pageSize = page.getSize();
        pageHateoasModel.totalPages = page.getTotalPages();
        pageHateoasModel.totalEntities = page.getTotalElements();

        List<Link> links = hateoasProvider.provide(page.getContent());
        pageHateoasModel.add(links);

        return pageHateoasModel;
    }
}
