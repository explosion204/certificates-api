package com.epam.esm.repository.impl;

import com.epam.esm.TestConfig;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import com.epam.esm.repository.PageContext;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@TestPropertySource(properties = {
        // this makes JPA generate database schema before init script is called
        "spring.jpa.defer-datasource-initialization=true"
})
class GiftCertificateRepositoryImplTest {
    @Autowired
    private GiftCertificateRepository certificateRepository;

    @ParameterizedTest
    @MethodSource("provideCertificateSearchParams")
    void testFindByParams(long expectedSize, PageContext pageContext, List<String> tagNames, String certificateName,
                String certificateDescription) {
        List<GiftCertificate> certificates = certificateRepository.find(pageContext, tagNames, certificateName,
                certificateDescription, null, null);

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
        PageContext pageContext = provideAllPagesContext();
        List<GiftCertificate> actual = certificateRepository.find(pageContext, null, null, null,
                OrderingType.ASC, null);
        List<GiftCertificate> expected = actual.stream()
                .sorted(Comparator.comparing(GiftCertificate::getName))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByNameDescending() {
        PageContext pageContext = provideAllPagesContext();
        List<GiftCertificate> actual = certificateRepository.find(pageContext, null, null, null,
                OrderingType.DESC, null);
        List<GiftCertificate> expected = actual.stream()
                .sorted(Collections.reverseOrder(Comparator.comparing(GiftCertificate::getName)))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByCreateDateAscending() {
        PageContext pageContext = provideAllPagesContext();
        List<GiftCertificate> actual = certificateRepository.find(pageContext, null, null, null,
                null, OrderingType.ASC);

        List<GiftCertificate> expected = actual.stream()
                .sorted(Comparator.comparing(GiftCertificate::getCreateDate))
                .toList();

        assertEquals(expected, actual);
    }

    @Test
    void testSortByCreateDateDescending() {
        PageContext pageContext = provideAllPagesContext();
        List<GiftCertificate> actual = certificateRepository.find(pageContext, null, null, null,
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
    void testCreate() {
        GiftCertificate expectedCertificate = provideCertificate();

        GiftCertificate actualCertificate = certificateRepository.create(expectedCertificate);

        assertEquals(expectedCertificate, actualCertificate);
    }

    @Test
    void testUpdate() {
        GiftCertificate expectedCertificate = provideCertificate();
        expectedCertificate.setId(1);

        GiftCertificate actualCertificate = certificateRepository.update(expectedCertificate);

        assertEquals(expectedCertificate, actualCertificate);
    }

    @Test
    void testDelete() {
        GiftCertificate certificate = certificateRepository.findById(1).get();
        certificateRepository.delete(certificate);

        assertTrue(certificateRepository.findById(1).isEmpty());
    }

    private static Stream<Arguments> provideCertificateSearchParams() {
        PageContext allItems = provideAllPagesContext();
        PageContext notAllItems = provideOnePageContext();

        return Stream.of(
                Arguments.of(4, allItems, null, null, null),
                Arguments.of(1, notAllItems, null, null, null),
                Arguments.of(1, allItems, List.of("tag1", "tag2"), null, null),
                Arguments.of(0, allItems, List.of("tag22"), "name", "desc"),
                Arguments.of(2, allItems, null, null, "description"),
                Arguments.of(1, allItems, null, "hello there", null)
        );
    }

    private GiftCertificate provideCertificate() {
        GiftCertificate certificate = new GiftCertificate();
        certificate.setName("test");
        certificate.setDescription("test");
        certificate.setPrice(BigDecimal.ONE);
        certificate.setDuration(Duration.ofDays(1));
        certificate.setCreateDate(LocalDateTime.now(UTC));
        certificate.setLastUpdateDate(LocalDateTime.now(UTC));

        return certificate;
    }

    private static PageContext provideAllPagesContext() {
        return PageContext.of(1, 50);
    }

    private static PageContext provideOnePageContext() {
        return PageContext.of(1, 1);
    }
}
