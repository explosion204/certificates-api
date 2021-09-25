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

import java.util.List;
import java.util.Optional;

@Repository
public class GiftCertificateRepositoryImpl implements GiftCertificateRepository {
    private static final String ID_PARAM = "id";
    private static final String NAME_PARAM = "name";
    private static final String DESCRIPTION_PARAM = "description";
    private static final String PRICE_PARAM = "price";
    private static final String DURATION_PARAM = "duration";
    private static final String CREATE_DATE_PARAM = "create_date";
    private static final String LAST_UPDATE_DATE_PARAM = "last_update_date";
    private static final String CERTIFICATE_ID_PARAM = "certificate_id";
    private static final String TAG_ID_PARAM = "tag_id";
    private static final String TAG_NAME_PARAM = "tag_name";
    private static final String CERTIFICATE_NAME_PARAM = "certificate_name";

    private static final String BASE_SELECT_CERTIFICATES = """
            SELECT DISTINCT gc.id, gc.name, description, price, duration, create_date, last_update_date
            FROM gift_certificate AS gc
            LEFT OUTER JOIN certificate_tag AS ct
            ON ct.id_certificate = gc.id
            LEFT OUTER JOIN tag
            ON ct.id_tag = tag.id
            %s
            %s;
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
            VALUES (:certificate_id, :tag_id);
            """;

    private static final String DELETE_CERTIFICATE_TAG_RELATION = """
            DELETE FROM certificate_tag
            WHERE id_certificate = :certificate_id AND id_tag = :tag_id
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
        ClauseBuilder whereClauseBuilder = new ClauseBuilder("WHERE ", " AND ");
        ClauseBuilder orderByClauseBuilder = new ClauseBuilder("ORDER BY ", ", ");
        MapSqlParameterSource parameters = new MapSqlParameterSource();

        if (tagName != null) {
            whereClauseBuilder.addComponent("tag.name = :" + TAG_NAME_PARAM);
            parameters.addValue(TAG_NAME_PARAM, tagName);
        }

        if (certificateName != null) {
            whereClauseBuilder.addComponent("gc.name LIKE CONCAT('%', :" + CERTIFICATE_NAME_PARAM + ", '%')");
            parameters.addValue(CERTIFICATE_NAME_PARAM, certificateName);
        }

        if (certificateDescription != null) {
            whereClauseBuilder.addComponent("gc.description LIKE CONCAT('%', :" + DESCRIPTION_PARAM + ", '%')");
            parameters.addValue(DESCRIPTION_PARAM, certificateDescription);
        }

        if (orderByName != null) {
            orderByClauseBuilder.addComponent("gc.name " + orderByName.name());
        }

        if (orderByCreateDate != null) {
            orderByClauseBuilder.addComponent("create_date " + orderByCreateDate.name());
        }

        String whereClause = whereClauseBuilder.build();
        String orderByClause = orderByClauseBuilder.build();
        String selectQuery = String.format(BASE_SELECT_CERTIFICATES, whereClause, orderByClause);

        return namedJdbcTemplate.query(selectQuery, parameters, rowMapper);
    }

    @Override
    public Optional<GiftCertificate> findById(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource().addValue(ID_PARAM, id);
        List<GiftCertificate> certificates = namedJdbcTemplate.query(SELECT_CERTIFICATE_BY_ID, parameters, rowMapper);

        return Optional.ofNullable(certificates.size() == 1 ? certificates.get(0) : null);
    }

    @Override
    public void attachTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID_PARAM, certificateId)
                .addValue(TAG_ID_PARAM, tagId);

        namedJdbcTemplate.update(INSERT_CERTIFICATE_TAG_RELATION, parameters);
    }

    @Override
    public void detachTag(long certificateId, long tagId) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(CERTIFICATE_ID_PARAM, certificateId)
                .addValue(TAG_ID_PARAM, tagId);

        namedJdbcTemplate.update(DELETE_CERTIFICATE_TAG_RELATION, parameters);
    }

    @Override
    public long create(GiftCertificate certificate) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(NAME_PARAM, certificate.getName())
                .addValue(DESCRIPTION_PARAM, certificate.getDescription())
                .addValue(PRICE_PARAM, certificate.getPrice())
                .addValue(DURATION_PARAM, certificate.getDuration().toDays())
                .addValue(CREATE_DATE_PARAM, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE_PARAM, certificate.getLastUpdateDate());

        namedJdbcTemplate.update(INSERT_CERTIFICATE, parameters, keyHolder);

        return keyHolder.getKey().longValue();
    }

    @Override
    public boolean update(GiftCertificate certificate) {
        MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID_PARAM, certificate.getId())
                .addValue(NAME_PARAM, certificate.getName())
                .addValue(DESCRIPTION_PARAM, certificate.getDescription())
                .addValue(PRICE_PARAM, certificate.getPrice())
                .addValue(DURATION_PARAM, certificate.getDuration().toDays())
                .addValue(CREATE_DATE_PARAM, certificate.getCreateDate())
                .addValue(LAST_UPDATE_DATE_PARAM, certificate.getLastUpdateDate());

        return namedJdbcTemplate.update(UPDATE_CERTIFICATE, parameters) > 0;
    }

    @Override
    public boolean delete(long id) {
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue(ID_PARAM, id);

        return namedJdbcTemplate.update(DELETE_CERTIFICATE, parameters) > 0;
    }
}
