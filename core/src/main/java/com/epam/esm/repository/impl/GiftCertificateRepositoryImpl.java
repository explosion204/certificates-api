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

import java.util.Optional;

import static com.epam.esm.repository.TableColumn.*;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String SELECT_CERTIFICATE_BY_ID = """
            SELECT id, name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATE = """
            INSERT gift_certificate (name, description, price, duration, create_date, last_update_date)
            VALUES (:name, :description, :price, :duration, :create_date, :last_update_date);
            """;

    private static final String UPDATE_CERTIFICATE = """
            UPDATE gift_certificate
            SET name = :name, description = :description, price = :price, duration = :duration,
                create_date = :create_date, last_update_date = :last_update_date
            WHERE id = :id;
            """;

    private static final String DELETE_CERTIFICATE = """
        DELETE FROM gift_certificate
        WHERE id = :id;
        """;

    private RowMapper<GiftCertificate> rowMapper;
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public GiftCertificateRepositoryImpl(DataSource dataSource, RowMapper<GiftCertificate> rowMapper) {
        namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        this.rowMapper = rowMapper;
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        GiftCertificate certificate = namedJdbcTemplate.queryForObject(SELECT_CERTIFICATE_BY_ID, parameters, rowMapper);
        // TODO: 9/17/2021 check use-case when id does not exist
        return Optional.ofNullable(certificate);
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
            throw new RepositoryException("An error occurred trying to create certificate", e);
        }

        if (keyHolder.getKey() == null) {
            throw new RepositoryException("An error occurred trying to get generated key");
        }

        return keyHolder.getKey().longValue();
    }

    @Override
    public void update(GiftCertificate certificate) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, certificate.getId())
                .addValue(NAME, certificate.getName())
                .addValue(DESCRIPTION, certificate.getDescription())
                .addValue(PRICE, certificate.getPrice())
                .addValue(DURATION, certificate.getDuration())
                .addValue(CREATE_DATE, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

        try {
            namedJdbcTemplate.update(UPDATE_CERTIFICATE, parameters);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to update certificate", e);
        }
    }

    @Override
    public void delete(long id) throws RepositoryException {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        try {
            namedJdbcTemplate.update(DELETE_CERTIFICATE, parameters);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to delete certificate", e);
        }
    }
}
