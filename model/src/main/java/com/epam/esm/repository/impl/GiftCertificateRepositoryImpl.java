package com.epam.esm.repository.impl;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.repository.TableColumn.*;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String TAG_NAME = "tag_name";
    private static final String CERTIFICATE_NAME = "certificate_name";
    private static final String ORDER_BY_NAME = "order_by_name";
    private static final String ORDER_BY_CREATE_DATE = "order_by_create_date";

    private static final String BASE_SELECT_CERTIFICATES = """
            SELECT DISTINCT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificate AS gc
            LEFT OUTER JOIN certificate_tag AS ct
            ON ct.id_certificate = gc.id
            LEFT OUTER JOIN tag
            ON ct.id_tag = tag.id;
            """;
    private static final String SELECT_CERTIFICATES = """
            SELECT DISTINCT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificate AS gc
            LEFT OUTER JOIN certificate_tag AS ct
            ON ct.id_certificate = gc.id
            LEFT OUTER JOIN tag
            ON ct.id_tag = tag.id
            WHERE
                CASE
                    WHEN :tag_name IS NOT NULL
                    THEN tag.name = :tag_name
                    ELSE true
                END
                AND
                CASE
                    WHEN :certificate_name IS NOT NULL
                    THEN gc.name LIKE CONCAT('%', :certificate_name, '%')
                    ELSE true
                END
                AND
                CASE
                    WHEN :description IS NOT NULL
                    THEN gc.description LIKE CONCAT('%', :description, '%')
                    ELSE true
                END
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
            INSERT INTO gift_certificate (name, description, price, duration, create_date, last_update_date)
            VALUES (:name, :description, :price, :duration, :create_date, :last_update_date);
            """;

    private static final String UPDATE_CERTIFICATE = """
            UPDATE gift_certificate
            SET name = :name, description = :description, price = :price, duration = :duration, 
                last_update_date = :last_update_date
            WHERE id = :id;
            """;

    private static final String DELETE_CERTIFICATE = """
            DELETE FROM gift_certificate
            WHERE id = :id;
            """;

    private static final String INSERT_CERTIFICATE_TAG_RELATION = """
            INSERT INTO certificate_tag (id_certificate, id_tag)
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
                .addValue(ORDER_BY_NAME, orderByName != null ? orderByName.name() : null)
                .addValue(ORDER_BY_CREATE_DATE, orderByCreateDate != null ? orderByCreateDate.name() : null);

        return namedJdbcTemplate.query(SELECT_CERTIFICATES, parameters, rowMapper);
//        StringBuilder selectQuery = new StringBuilder(BASE_SELECT_CERTIFICATES);
//        SqlParameterSource parameters = new MapSqlParameterSource();
//        boolean hasWhereCondition = false;
//
//        if (hasWhereCondition = (tagName != null)) {
//            selectQuery.append(" id = :").append(TAG_NAME).
//        }
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID, id);
        List<GiftCertificate> certificates = namedJdbcTemplate.query(SELECT_CERTIFICATE_BY_ID, parameters, rowMapper);
        // TODO: 9/17/2021 check use-case when id does not exist
        return Optional.ofNullable(certificates.size() == 1 ? certificates.get(0) : null);
    }

    @Override
    public boolean attachTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID, certificateId)
                .addValue(TAG_ID, tagId);

        return namedJdbcTemplate.update(INSERT_CERTIFICATE_TAG_RELATION, parameters) > 0;
    }

    @Override
    public boolean detachTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID, certificateId)
                .addValue(TAG_ID, tagId);

        return namedJdbcTemplate.update(DELETE_CERTIFICATE_TAG_RELATION, parameters) > 0;
    }

    @Override
    public long create(GiftCertificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(NAME, certificate.getName())
                .addValue(DESCRIPTION, certificate.getDescription())
                .addValue(PRICE, certificate.getPrice())
                .addValue(DURATION, certificate.getDuration().toDays())
                .addValue(CREATE_DATE, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

        namedJdbcTemplate.update(INSERT_CERTIFICATE, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(GiftCertificate certificate) {
        Optional<GiftCertificate> optionalCertificate = findById(certificate.getId());

        if (optionalCertificate.isPresent()) {
            GiftCertificate currentCertificate = optionalCertificate.get();

            String name = certificate.getName();
            String description = certificate.getDescription();
            BigDecimal price = certificate.getPrice();
            Duration duration = certificate.getDuration();

            MapSqlParameterSource parameters = new MapSqlParameterSource()
                    .addValue(ID, certificate.getId())
                    .addValue(NAME, name != null ? name : currentCertificate.getName())
                    .addValue(DESCRIPTION, description != null ? description : currentCertificate.getDescription())
                    .addValue(PRICE, price != null ? price : currentCertificate.getPrice())
                    .addValue(DURATION, duration != null ? duration : currentCertificate.getDuration().toDays())
                    .addValue(CREATE_DATE, certificate.getCreateDate())
                    .addValue(LAST_UPDATE_DATE, certificate.getLastUpdateDate());

            return namedJdbcTemplate.update(UPDATE_CERTIFICATE, parameters) > 0;
        }

        return false;
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID, id);

        return namedJdbcTemplate.update(DELETE_CERTIFICATE, parameters) > 0;
    }
}
