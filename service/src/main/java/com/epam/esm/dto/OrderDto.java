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
public class OrderDto extends IdentifiableDto<OrderDto> {
    private long id;
    private long userId;
    private long certificateId;
    private BigDecimal cost;

    // TODO: 10/5/2021
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
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
