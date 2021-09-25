package com.epam.esm.repository.impl;

import com.epam.esm.TestProfileResolver;
import com.epam.esm.config.DatabaseConfig;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import com.epam.esm.repository.TagRepository;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = DatabaseConfig.class)
@Transactional
@ActiveProfiles(resolver = TestProfileResolver.class)
@TestInstance(PER_CLASS)
class GiftCertificateRepositoryImplTest {
    @Autowired
    private GiftCertificateRepository certificateRepository;

    @Autowired
    private TagRepository tagRepository;

    @ParameterizedTest
    @MethodSource("provideCertificateSearchParams")
    void testFindByParams(long expectedSize, String tagName, String certificateName, String certificateDescription) {
        List<GiftCertificate> certificates = certificateRepository.find(tagName, certificateName, certificateDescription,
                null, null);

        boolean valid = certificates.stream()
                .allMatch(cert -> {
                    boolean isValid = certificateName == null || StringUtils.containsIgnoreCase(cert.getName(),
                            certificateName);
                    isValid &= certificateDescription == null || StringUtils.containsIgnoreCase(cert.getDescription(),
                            certificateDescription);

                    return isValid;
                });

        assertTrue(valid && certificates.size() == expectedSize);
    }

    @Test
    void testSortByNameAscending() {
        List<GiftCertificate> actual = certificateRepository.find(null, null, null,
                OrderingType.ASC, null);
        List<GiftCertificate> expected = actual.stream()
                .sorted(Comparator.comparing(GiftCertificate::getName))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByNameDescending() {
        List<GiftCertificate> actual = certificateRepository.find(null, null, null,
                OrderingType.DESC, null);
        List<GiftCertificate> expected = actual.stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(GiftCertificate::getName)))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByCreateDateAscending() {
        List<GiftCertificate> actual = certificateRepository.find(null, null, null,
                null, OrderingType.ASC);

        List<GiftCertificate> expected = actual.stream()
                .sorted(Comparator.comparing(GiftCertificate::getCreateDate))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByCreateDateDescending() {
        List<GiftCertificate> actual = certificateRepository.find(null, null, null,
                null, OrderingType.DESC);

        List<GiftCertificate> expected = actual.stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(GiftCertificate::getCreateDate)))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testFindById() {
        Optional<GiftCertificate> certificate = certificateRepository.findById(1);
        assertTrue(certificate.isPresent() && certificate.get().getId() == 1);
    }

    @Test
    void testFindByIdNotFound() {
        Optional<GiftCertificate> certificate = certificateRepository.findById(0);
        assertTrue(certificate.isEmpty());
    }

    @Test
    void testAttachTag() {
        certificateRepository.attachTag(2, 2);
        List<Tag> tags = tagRepository.findByCertificate(2);

        assertEquals(tags.size(), 1);
    }

    @Test
    void testDetachTag() {
        certificateRepository.detachTag(1, 1);
        List<Tag> tags = tagRepository.findByCertificate(1);

        assertTrue(tags.isEmpty());
    }

    @Test
    void testCreate() {
        GiftCertificate newCertificate = provideCertificate();

        long expectedId = 5;
        long actualId = certificateRepository.create(newCertificate);

        assertEquals(expectedId, actualId);
    }

    @Test
    void testUpdate() {
        GiftCertificate updatedCertificate = provideCertificate();
        updatedCertificate.setId(1);

        boolean result = certificateRepository.update(updatedCertificate);

        assertTrue(result);
    }

    @Test
    void testUpdateNotFound() {
        GiftCertificate updatedCertificate = provideCertificate();
        updatedCertificate.setId(0);

        boolean result = certificateRepository.update(updatedCertificate);

        assertFalse(result);
    }

    @Test
    void testDelete() {
        boolean result = certificateRepository.delete(1);

        assertTrue(result);
    }

    @Test
    void testDeleteNotFound() {
        boolean result = certificateRepository.delete(0);

        assertFalse(result);
    }

    private Stream<Arguments> provideCertificateSearchParams() {
        return Stream.of(
                Arguments.of(4, null, null, null),
                Arguments.of(1, "tag1", null, null),
                Arguments.of(0, "tag22", "name", "desc"),
                Arguments.of(2, null, null, "description"),
                Arguments.of(1, null, "hello there", null)
        );
    }

    private GiftCertificate provideCertificate() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("test");
        certificate.setDescription("test");
        certificate.setPrice(BigDecimal.ONE);
        certificate.setDuration(Duration.ofDays(1));
        certificate.setCreateDate(LocalDateTime.now().atZone(ZoneOffset.UTC));
        certificate.setLastUpdateDate(LocalDateTime.now().atZone(ZoneOffset.UTC));

        return certificate;
    }
}
