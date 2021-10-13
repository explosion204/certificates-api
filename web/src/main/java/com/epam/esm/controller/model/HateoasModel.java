package com.epam.esm.controller.model;

import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.IdentifiableDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class HateoasModel<T extends IdentifiableDto> extends RepresentationModel<HateoasModel<T>> {
    private T data;

    public static <T extends IdentifiableDto> HateoasModel<T> build(HateoasProvider<T> hateoasProvider, T object) {
        HateoasModel<T> hateoasModel = new HateoasModel<>(object);
        List<Link> links = hateoasProvider.provide(object);
        hateoasModel.add(links);

        return hateoasModel;
    }
}
