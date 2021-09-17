package com.epam.esm.repository.mapper;

import com.epam.esm.entity.Tag;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.epam.esm.repository.TableColumn.ID;
import static com.epam.esm.repository.TableColumn.NAME;

@Component
public class TagMapper implements RowMapper<Tag> {
    @Override
    public Tag mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        return Tag.builder()
                .id(resultSet.getLong(ID))
                .name(resultSet.getString(NAME))
                .build();
    }
}
