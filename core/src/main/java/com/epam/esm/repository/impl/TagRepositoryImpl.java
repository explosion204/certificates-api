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

import java.util.List;
import java.util.Optional;

import static com.epam.esm.repository.TableColumn.*;

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
            WHERE name = :tag_name;
            """;

    private static final String SELECT_TAGS_BY_CERTIFICATE = """
            SELECT tag.id, tag.name
            FROM tag
            INNER JOIN certificate_tag AS ct
            ON tag.id = ct.id_tag
            WHERE ct.id_certificate = :id_certificate;
            """;

    private static final String INSERT_TAG = """
            INSERT INTO tag (name)
            VALUES (:tag_name);
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
    public Optional<Tag> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);

        try {
            List<Tag> tags = namedJdbcTemplate.query(SELECT_TAG_BY_ID, parameters, rowMapper);
            return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
        } catch (DataAccessException e) {
            throw new RepositoryException("Caught an error trying to find tag by id = " + id, e);
        }
    }

    @Override
    public Optional<Tag> findByName(String name) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(NAME, name);

        try {
            List<Tag> tags = namedJdbcTemplate.query(SELECT_TAG_BY_NAME, parameters, rowMapper);
            return Optional.ofNullable(tags.size() == 1 ? tags.get(0) : null);
        } catch (DataAccessException e) {
            throw new RepositoryException("Caught an error trying to find tag by name = " + name, e);
        }
    }

    @Override
    public List<Tag> findByCertificate(long certificateId) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(CERTIFICATE_ID, certificateId);

        try {
            return namedJdbcTemplate.query(SELECT_TAGS_BY_CERTIFICATE, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RepositoryException("Caught an error trying to find tags by certificate id = " + certificateId, e);
        }
    }

    @Override
    public long create(Tag tag) {
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
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        try {
            return namedJdbcTemplate.update(DELETE_TAG, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to delete tag with id = " + id, e);
        }
    }
}
