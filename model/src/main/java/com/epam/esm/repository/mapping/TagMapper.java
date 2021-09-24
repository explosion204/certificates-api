package com.epam.esm.repository.mapping;

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
        Tag tag = new Tag();

        tag.setId(resultSet.getLong(ID));
        tag.setName(resultSet.getString(NAME));

        return tag;
    }
}
