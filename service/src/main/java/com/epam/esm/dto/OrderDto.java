package com.epam.esm.dto;

import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderDto extends IdentifiableDto {
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String UTC_ZONE = "UTC";

    public static final String USER_ID_PROPERTY_NAME = "userId";
    public static final String CERTIFICATE_IDS_PROPERTY_NAME = "certificateIds";
    public static final String COST_PROPERTY_NAME = "cost";

    @JsonProperty(USER_ID_PROPERTY_NAME)
    private long userId;

    @JsonProperty(CERTIFICATE_IDS_PROPERTY_NAME)
    private List<Long> certificateIds;

    @JsonProperty(COST_PROPERTY_NAME)
    private BigDecimal cost;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_FORMAT, timezone = UTC_ZONE)
    private LocalDateTime purchaseDate;

    public static OrderDto fromOrder(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setCost(order.getCost());
        orderDto.setPurchaseDate(order.getPurchaseDate());

        List<Long> certificates = order.getCertificates()
                .stream()
                .map(GiftCertificate::getId)
                .toList();
        orderDto.setCertificateIds(certificates);

        return orderDto;
    }
}
