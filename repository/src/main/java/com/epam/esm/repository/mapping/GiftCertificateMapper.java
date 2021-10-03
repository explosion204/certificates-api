package com.epam.esm.repository.mapping;

import com.epam.esm.entity.GiftCertificate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import static com.epam.esm.repository.mapping.TableColumn.*;

@Component
public class GiftCertificateMapper implements RowMapper<GiftCertificate> {
    @Override
    public GiftCertificate mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        GiftCertificate certificate = new GiftCertificate();

        certificate.setId(resultSet.getLong(ID));
        certificate.setName(resultSet.getString(CERTIFICATE_NAME));
        certificate.setDescription(resultSet.getString(CERTIFICATE_DESCRIPTION));
        certificate.setPrice(resultSet.getBigDecimal(CERTIFICATE_PRICE));
        certificate.setDuration(Duration.ofDays(resultSet.getInt(CERTIFICATE_DURATION)));
        certificate.setCreateDate(resultSet.getTimestamp(CERTIFICATE_CREATE_DATE).toLocalDateTime());
        certificate.setLastUpdateDate(resultSet.getTimestamp(CERTIFICATE_LAST_UPDATE_DATE).toLocalDateTime());

        return certificate;
    }
}
