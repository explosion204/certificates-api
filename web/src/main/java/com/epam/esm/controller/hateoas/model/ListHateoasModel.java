package com.epam.esm.controller.hateoas.model;

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
public class ListHateoasModel<T extends IdentifiableDto> extends RepresentationModel<ListHateoasModel<T>> {
    private List<T> data;

    public static <T extends IdentifiableDto> ListHateoasModel<T> build(HateoasProvider<List<T>> hateoasProvider,
                List<T> list) {
        ListHateoasModel<T> hateoasModel = new ListHateoasModel<>(list);
        List<Link> links = hateoasProvider.provide(list);
        hateoasModel.add(links);

        return hateoasModel;
    }
}
