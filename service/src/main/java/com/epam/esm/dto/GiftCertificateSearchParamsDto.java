package com.epam.esm.dto;

import com.epam.esm.repository.OrderingType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GiftCertificateSearchParamsDto {
    private List<String> tagNames;
    private String certificateName;
    private String certificateDescription;
    private OrderingType orderByName;
    private OrderingType orderByCreateDate;
}
