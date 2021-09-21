package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.RepositoryException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
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

    private static final String ORDER_BY_NAME = "order_by_name";
    private static final String ORDER_BY_CREATE_DATE = "order_by_create_date";
    private static final String SELECT_CERTIFICATES = """
            SELECT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificate AS gc
            INNER JOIN certificate_tag AS ct
            ON ct.id_certificate = gc.id
            INNER JOIN tag
            ON ct.id_tag = tag.id
            WHERE
                IF(:tag_name IS NOT NULL, tag.name = :tag_name, true) AND
                IF(:certificate_name IS NOT NULL, gc.name LIKE CONCAT('%', :certificate_name, '%'), true) AND
                IF(:description IS NOT NULL, gc.description LIKE CONCAT('%', :description, '%'), true)
            ORDER BY
                CASE WHEN :order_by_name = 'asc' THEN gc.name END,
                CASE WHEN :order_by_name = 'desc' THEN gc.name END DESC,
                CASE WHEN :order_by_create_date = 'asc' THEN create_date END,
                CASE WHEN :order_by_create_date = 'desc' THEN create_date END DESC;
            """;

    private static final String SELECT_CERTIFICATE_BY_ID = """
            SELECT id, name, description, price, duration, create_date, last_update_date
            FROM gift_certificate
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATE = """
            INSERT gift_certificate (name, description, price, duration, create_date, last_update_date)
            VALUES (:certificate_name, :description, :price, :duration, :create_date, :last_update_date);
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
    public List<GiftCertificate> find(String tagName, String certificateName, String certificateDescription,
                OrderingType orderByName, OrderingType orderByCreateDate) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(TAG_NAME, tagName)
                .addValue(CERTIFICATE_NAME, certificateName)
                .addValue(DESCRIPTION, certificateDescription)
                .addValue(ORDER_BY_NAME, orderByName)
                .addValue(ORDER_BY_CREATE_DATE, orderByCreateDate);

        try {
            return namedJdbcTemplate.query(SELECT_CERTIFICATES, parameters, rowMapper);
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to query certificates");
        }
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
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
    public boolean attachTag(long certificateId, long tagId) {
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
    public boolean detachTag(long certificateId, long tagId) {
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
    public long create(GiftCertificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_NAME, certificate.getName())
                .addValue(DESCRIPTION, certificate.getDescription())
                .addValue(PRICE, certificate.getPrice())
                .addValue(DURATION, certificate.getDuration().toDays())
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
    public boolean update(GiftCertificate certificate) {
        StringBuilder updateQuery = new StringBuilder("UPDATE gift_certificate SET ");
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, certificate.getId())
                .addValue(CREATE_DATE, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

        if (certificate.getName() != null) {
            updateQuery.append("name = :").append(CERTIFICATE_NAME).append(UPDATE_DATA_SEPARATOR);
            parameters.addValue(CERTIFICATE_NAME, certificate.getName());
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
            parameters.addValue(DURATION, certificate.getDuration().toDays());
        }

        updateQuery.append("last_update_date = :").append(LAST_UPDATE_DATE);
        updateQuery.append(" WHERE id = :").append(ID);

        try {
            return namedJdbcTemplate.update(updateQuery.toString(), parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to update certificate (" + certificate + ")", e);
        }
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        try {
            return namedJdbcTemplate.update(DELETE_CERTIFICATE, parameters) > 0;
        } catch (DataAccessException e) {
            throw new RepositoryException("An error occurred trying to delete certificate with id = " + id, e);
        }
    }
}
