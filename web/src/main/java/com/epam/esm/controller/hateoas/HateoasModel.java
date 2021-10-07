package com.epam.esm.controller.hateoas;

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
public class HateoasModel extends RepresentationModel<HateoasModel> {
    private Object data;

    private HateoasModel() {

    }

    public static <T extends IdentifiableDto> HateoasModel build(HateoasProvider<T> hateoasProvider, T object) {
        HateoasModel hateoasModel = new HateoasModel(object);
        List<Link> links = hateoasProvider.provide(object);
        hateoasModel.add(links);

        return hateoasModel;
    }

    public static <T extends IdentifiableDto> List<HateoasModel> build(HateoasProvider<T> hateoasProvider,
                List<T> collection) {
        return collection.stream()
                .map(object -> build(hateoasProvider, object))
                .toList();
    }
}
