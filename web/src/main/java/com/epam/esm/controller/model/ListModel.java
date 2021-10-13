package com.epam.esm.controller.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ListModel<T> {
    private List<T> data;

    public static <T> ListModel<T> build(List<T> data) {
        return new ListModel<>(data);
    }
}
