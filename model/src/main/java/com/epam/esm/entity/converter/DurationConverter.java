package com.epam.esm.entity.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.time.Duration;

@Converter(autoApply = true)
public class DurationConverter implements AttributeConverter<Duration, Long> {
    // JPA maps Duration in days to Long in nanoseconds
    // This class performs conversion logic for us

    @Override
    public Long convertToDatabaseColumn(Duration duration) {
        return duration.toDays();
    }

    @Override
    public Duration convertToEntityAttribute(Long value) {
        return Duration.ofDays(value);
    }
}
