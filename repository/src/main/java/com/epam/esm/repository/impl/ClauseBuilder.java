package com.epam.esm.repository.impl;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ClauseBuilder {
    private List<String> components = new ArrayList<>();
    private String initial;
    private String delimiter;

    ClauseBuilder(String initial, String delimiter) {
        this.initial = initial;
        this.delimiter = delimiter;
    }

    void addComponent(String condition) {
        components.add(condition);
    }

    String build() {
        if (components.isEmpty()) {
            return StringUtils.EMPTY;
        }

        StringBuilder clause = new StringBuilder(initial);
        Iterator<String> iterator = components.iterator();

        while (iterator.hasNext()) {
            String condition = iterator.next();
            clause.append(condition);

            if (iterator.hasNext()) {
                clause.append(delimiter);
            }
        }

        return clause.toString();
    }
}
