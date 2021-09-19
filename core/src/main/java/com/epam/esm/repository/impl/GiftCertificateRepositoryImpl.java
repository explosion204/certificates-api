package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RepositoryException;
import com.epam.esm.repository.GiftCertificateRepository;
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
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String UPDATE_DATA_SEPARATOR = ", ";

    private static final String SELECT_CERTIFICATE_BY_ID = """
            SELECT id, name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATE = """
            INSERT gift_certificate (name, description, price, duration, create_date, last_update_date)
            VALUES (:name, :description, :price, :duration, :create_date, :last_update_date);
            """;

    private static final String DELETE_CERTIFICATE = """
            DELETE FROM gift_certificate
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATE_TAG_RELATION = """
            INSERT certificate_tag (id_certificate, id_tag)
            VALUES (:id_certificate, :id_tag);
            """;

    private static final String DELETE_CERTIFICATE_TAG_RELATION = """
            DELETE FROM certificate_tag
            WHERE id_certificate = :id_certificate AND id_tag = :id_tag
            """;

    private RowMapper<GiftCertificate> rowMapper;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public GiftCertificateRepositoryImpl(DataSource dataSource, RowMapper<GiftCertificate> rowMapper) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<GiftCertificate> findById(long id) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        try {
            List<GiftCertificate> certificates = namedJdbcTemplate.query(SELECT_CERTIFICATE_BY_ID, parameters, rowMapper);
            // TODO: 9/17/2021 check use-case when id does not exist
            return Optional.ofNullable(certificates.size() == 1 ? certificates.get(0) : null);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to find certificate by id = " + id, e);
        }
    }

    @Override
    public boolean attachTag(long certificateId, long tagId) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID, certificateId)
                .addValue(TAG_ID, tagId);
        try {
            return namedJdbcTemplate.update(INSERT_CERTIFICATE_TAG_RELATION, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to attach tag (id = " + tagId + ") to certificate " +
                    "(id = " + tagId + ")");
        }
    }

    @Override
    public boolean detachTag(long certificateId, long tagId) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID, certificateId)
                .addValue(TAG_ID, tagId);
        try {
            return namedJdbcTemplate.update(DELETE_CERTIFICATE_TAG_RELATION, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to detach tag (id = " + tagId + ") to certificate " +
                    "(id = " + tagId + ")");
        }
    }

    @Override
    public long create(GiftCertificate certificate) throws RepositoryException {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(NAME, certificate.getName())
                .addValue(DESCRIPTION, certificate.getDescription())
                .addValue(PRICE, certificate.getPrice())
                .addValue(DURATION, certificate.getDuration())
                .addValue(CREATE_DATE, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

        try {
            namedJdbcTemplate.update(INSERT_CERTIFICATE, parameters, keyHolder);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to create certificate (" + certificate + ")", e);
        }

        if (keyHolder.getKey() == null) {
            throw new RepositoryException("An error occurred trying to get generated key for certificate: " + certificate);
        }

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(GiftCertificate certificate) throws RepositoryException {
        StringBuilder updateQuery = new StringBuilder("UPDATE gift_certificate SET ");
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, certificate.getId())
                .addValue(CREATE_DATE, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

        if (certificate.getName() != null) {
            updateQuery.append("name = :").append(NAME).append(UPDATE_DATA_SEPARATOR);
            parameters.addValue(NAME, certificate.getName());
        }

        if (certificate.getDescription() != null) {
            updateQuery.append("description = :").append(DESCRIPTION).append(UPDATE_DATA_SEPARATOR);
            parameters.addValue(DESCRIPTION, certificate.getDescription());
        }

        if (certificate.getPrice() != null) {
            updateQuery.append("price = :").append(PRICE).append(UPDATE_DATA_SEPARATOR);
            parameters.addValue(PRICE, certificate.getPrice());
        }

        if (certificate.getDuration() != null) {
            updateQuery.append("duration = :").append(DURATION).append(UPDATE_DATA_SEPARATOR);
            parameters.addValue(DURATION, certificate.getDuration());
        }

        updateQuery.append("last_update_date = :last_update_date ");
        updateQuery.append("WHERE id = :id;");

        try {
            return namedJdbcTemplate.update(updateQuery.toString(), parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to update certificate (" + certificate + ")", e);
        }
    }

    @Override
    public boolean delete(long id) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        try {
            return namedJdbcTemplate.update(DELETE_CERTIFICATE, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to delete certificate with id = " + id, e);
        }
    }
}
