package com.epam.esm.dto;

import com.epam.esm.dto.mapping.DayDurationDeserializer;
import com.epam.esm.dto.mapping.DayDurationSerializer;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
public class GiftCertificateDto {
    private long id;
    private String name;
    private String description;
    private BigDecimal price;

    @JsonSerialize(using = DayDurationSerializer.class)
    @JsonDeserialize(using = DayDurationDeserializer.class)
    private Duration duration;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime createDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private LocalDateTime lastUpdateDate;

    private List<String> tags;

    public GiftCertificate toCertificate() {
        GiftCertificate certificate = new GiftCertificate();

        certificate.setId(id);
        certificate.setName(name);
        certificate.setDescription(description);
        certificate.setPrice(price);
        certificate.setDuration(duration);

        return certificate;
    }

    public static GiftCertificateDto fromCertificate(GiftCertificate certificate, List<Tag> tags) {
        GiftCertificateDto certificateDto = new GiftCertificateDto();

        certificateDto.setId(certificate.getId());
        certificateDto.setName(certificate.getName());
        certificateDto.setDescription(certificate.getDescription());
        certificateDto.setPrice(certificate.getPrice());
        certificateDto.setDuration(certificate.getDuration());
        certificateDto.setCreateDate(certificate.getCreateDate());
        certificateDto.setLastUpdateDate(certificate.getLastUpdateDate());

        List<String> tagNames = tags.stream().map(Tag::getName).toList();
        certificateDto.setTags(tagNames);

        return certificateDto;
    }
}
