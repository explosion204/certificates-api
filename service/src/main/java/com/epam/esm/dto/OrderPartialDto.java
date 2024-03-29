package com.epam.esm.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import static com.epam.esm.dto.OrderDto.CERTIFICATE_IDS_PROPERTY_NAME;
import static com.epam.esm.dto.OrderDto.ID_PROPERTY_NAME;
import static com.epam.esm.dto.OrderDto.USER_ID_PROPERTY_NAME;

@JsonIgnoreProperties({ ID_PROPERTY_NAME, USER_ID_PROPERTY_NAME, CERTIFICATE_IDS_PROPERTY_NAME })
public class OrderPartialDto extends OrderDto {
    public OrderPartialDto(OrderDto parentDto) {
        setId(parentDto.getId());
        setUserId(parentDto.getUserId());
        setCost(parentDto.getCost());
        setPurchaseDate(parentDto.getPurchaseDate());
    }
}
