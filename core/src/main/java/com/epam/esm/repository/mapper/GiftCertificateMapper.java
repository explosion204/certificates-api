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
        return GiftCertificate.builder()
                .id(resultSet.getLong(ID))
                .name(resultSet.getString(NAME))
                .description(resultSet.getString(DESCRIPTION))
                .price(resultSet.getBigDecimal(PRICE))
                .duration(Duration.ofDays(resultSet.getInt(DURATION)))
                .createDate(resultSet.getTimestamp(CREATE_DATE).toInstant().atZone(UTC))
                .lastUpdateDate(resultSet.getTimestamp(LAST_UPDATE_DATE).toInstant().atZone(UTC))
                .build();
    }
}
