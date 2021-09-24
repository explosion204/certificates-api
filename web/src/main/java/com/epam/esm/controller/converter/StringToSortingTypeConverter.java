package com.epam.esm.controller.converter;

import com.epam.esm.repository.OrderingType;
import org.springframework.core.convert.converter.Converter;

public class StringToSortingTypeConverter implements Converter<String, OrderingType> {
    @Override
    public OrderingType convert(String source) {
        return OrderingType.valueOf(source.toUpperCase());
    }
}
