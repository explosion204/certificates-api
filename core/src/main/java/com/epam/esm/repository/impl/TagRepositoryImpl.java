package com.epam.esm.repository.impl;

import com.epam.esm.entity.Tag;
import com.epam.esm.exception.RepositoryException;
import com.epam.esm.repository.TagRepository;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.util.Optional;

import static com.epam.esm.repository.TableColumn.ID;
import static com.epam.esm.repository.TableColumn.NAME;

@Repository
public class TagRepositoryImpl implements TagRepository {
    private static final String SELECT_TAG_BY_ID = """
            SELECT id, name
            FROM tag
            WHERE id = :id;
            """;

    private static final String SELECT_TAG_BY_NAME = """
            SELECT id, name
            FROM tag
            WHERE name = :name;
            """;

    private static final String INSERT_TAG = """
            INSERT tag (name)
            VALUES (:name);
            """;

    private static final String DELETE_TAG = """
            DELETE FROM tag
            WHERE id = :id;
            """;

    private RowMapper<Tag> rowMapper;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public TagRepositoryImpl(DataSource dataSource, RowMapper<Tag> rowMapper) {
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<Tag> findById(long id) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);

        try {
            Tag tag = namedJdbcTemplate.queryForObject(SELECT_TAG_BY_ID, parameters, rowMapper);
            return Optional.ofNullable(tag);
        } catch (DataAccessException e) {
            throw new RepositoryException("Caught an error trying to find tag by id = " + id, e);
        }
    }

    @Override
    public Optional<Tag> findByName(String name) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME, name);

        try {
            Tag tag = namedJdbcTemplate.queryForObject(SELECT_TAG_BY_NAME, parameters, rowMapper);
            return Optional.ofNullable(tag);
        } catch (DataAccessException e) {
            throw new RepositoryException("Caught an error trying to find tag by name = " + name, e);
        }
    }

    @Override
    public long create(Tag tag) throws RepositoryException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME, tag.getName());

        try {
            namedJdbcTemplate.update(INSERT_TAG, parameters, keyHolder);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to create tag (" + tag + ")", e);
        }

        if (keyHolder.getKey() == null) {
            throw new RepositoryException("An error occurred trying to get generated key for tag: " + tag);
        }

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean delete(long id) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        try {
            return namedJdbcTemplate.update(DELETE_TAG, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to delete tag with id = " + id, e);
        }
    }
}
