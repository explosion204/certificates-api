package com.epam.esm.dto;

import com.epam.esm.entity.Order;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class OrderDto extends IdentifiableDto {
    private static final String ISO_8601_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String UTC_ZONE = "UTC";

    private long id;
    private long userId;
    private long certificateId;
    private BigDecimal cost;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = ISO_8601_FORMAT, timezone = UTC_ZONE)
    private LocalDateTime purchaseDate;

    public static OrderDto fromOrder(Order order) {
        OrderDto orderDto = new OrderDto();

        orderDto.setId(order.getId());
        orderDto.setUserId(order.getUser().getId());
        orderDto.setCertificateId(order.getCertificate().getId());
        orderDto.setCost(order.getCost());
        orderDto.setPurchaseDate(order.getPurchaseDate());

        return orderDto;
    }
}
