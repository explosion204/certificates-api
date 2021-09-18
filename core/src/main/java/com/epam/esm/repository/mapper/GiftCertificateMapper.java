package com.epam.esm.repository.mapper;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import static com.epam.esm.repository.TableColumn.*;
import static java.time.ZoneOffset.UTC;

@Component
public class GiftCertificateMapper implements RowMapper<GiftCertificate> {
    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        GiftCertificate certificate = new GiftCertificate();

        certificate.setId(resultSet.getLong(ID));
        certificate.setName(resultSet.getString(NAME));
        certificate.setDescription(resultSet.getString(DESCRIPTION));
        certificate.setPrice(resultSet.getBigDecimal(PRICE));
        certificate.setDuration(Duration.ofDays(resultSet.getInt(DURATION)));
        certificate.setCreateDate(resultSet.getTimestamp(CREATE_DATE).toInstant().atZone(UTC));
        certificate.setLastUpdateDate(resultSet.getTimestamp(LAST_UPDATE_DATE).toInstant().atZone(UTC));

        return certificate;
    }
}
