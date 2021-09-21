package com.epam.esm.dto;

import com.epam.esm.repository.OrderingType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftCertificateSearchParamsDto {
    private String tagName;
    private String certificateName;
    private String certificateDescription;
    private OrderingType orderByName;
    private OrderingType orderByCreateDate;
}
