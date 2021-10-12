package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Order;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.epam.esm.validator.ValidationError.INVALID_NAME;
import static java.time.ZoneOffset.UTC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GiftCertificateServiceTest {
    private static final LocalDateTime INITIAL_DATE = LocalDateTime.now(UTC);

    @InjectMocks
    private GiftCertificateService certificateService;

    @Mock
    private GiftCertificateRepository certificateRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private GiftCertificateValidator certificateValidator;

    @Mock
    private TagValidator tagValidator;

    @Captor
    private ArgumentCaptor<GiftCertificate> certificateCaptor;

    @BeforeAll
    static void setUp() {
        MockitoAnnotations.openMocks(GiftCertificateServiceTest.class);
    }

    @Test
    void testFind() {
        List<GiftCertificate> certificateList = new ArrayList<>() {{
            add(provideCertificate());
        }};

        List<GiftCertificateDto> certificateDtoList = new ArrayList<>() {{
            add(provideCertificateDto());
        }};

        PageContext pageContext = new PageContext();
        List<String> tagNames = List.of("tag1", "tag2");
        String certificateName = "certificate";
        String certificateDescription = "description";
        OrderingType orderByName = OrderingType.ASC;
        OrderingType orderByCreateDate = OrderingType.DESC;
        when(certificateRepository.find(pageContext, tagNames, certificateName, certificateDescription, orderByName,
                orderByCreateDate)).thenReturn(certificateList);

        GiftCertificateSearchParamsDto searchParamsDto = provideSearchParamsDto();
        List<GiftCertificateDto> actualDtoList = certificateService.find(searchParamsDto, pageContext);

        verify(certificateRepository).find(pageContext, tagNames, certificateName, certificateDescription,
               orderByName, orderByCreateDate);

        assertEquals(certificateDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto expectedCertificateDto = provideCertificateDto();
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        GiftCertificateDto actualCertificateDto = certificateService.findById(certificateId);

        verify(certificateRepository).findById(certificateId);
        assertEquals(expectedCertificateDto, actualCertificateDto);
    }

    @Test
    void testFindByIdWhenCertificateNotFound() {
        long certificateId = 1;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
    }

    @Test
    void testCreate() {
        GiftCertificateDto expectedDto = provideCertificateDto();
        GiftCertificate certificate = provideCertificate();

        when(certificateRepository.create(any(GiftCertificate.class))).thenReturn(certificate);
        GiftCertificateDto actualDto = certificateService.create(expectedDto);

        verify(certificateValidator).validate(certificateCaptor.capture(), eq(false));
        verify(certificateRepository).create(certificateCaptor.getValue());
        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testCreateWhenCertificateInvalid() {
        GiftCertificateDto certificateDto = provideCertificateDto();
        certificateDto.setName(null);

        GiftCertificate certificate = provideCertificate();
        certificate.setName(null);
        certificate.setCreateDate(null);
        certificate.setLastUpdateDate(null);

        List<ValidationError> errorList = List.of(INVALID_NAME);
        when(certificateValidator.validate(certificate, false)).thenReturn(errorList);

        assertThrows(InvalidEntityException.class, () -> certificateService.create(certificateDto));
    }

    @Test
    void testUpdate() {
        long certificateId = 1;
        String tagName = "tag1";
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setId(certificateId);

        Tag newTag = new Tag();
        newTag.setName(tagName);

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(certificateRepository.update(certificate)).thenReturn(certificate);
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(tagRepository.create(newTag)).thenReturn(newTag);

        certificateService.update(updatedCertificateDto);

        verify(certificateValidator).validate(certificateCaptor.capture(), eq(true));
        verify(certificateRepository).update(certificateCaptor.getValue());
        verify(tagValidator).validate(tagName);
        verify(tagRepository).findByName(tagName);
    }

    @Test
    void testUpdateWhenCertificateNotFound() {
        long certificateId = 1;
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testUpdateWhenCertificateInvalid() {
        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setName("");

        List<ValidationError> errorList = List.of(INVALID_NAME);
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(certificateValidator.validate(certificate, true)).thenReturn(errorList);

        assertThrows(InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testUpdateWhenTagInvalid() {
        String tagName = "";
        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setTags(new ArrayList<>() {{ add(""); }});

        List<ValidationError> errorList = List.of(INVALID_NAME);
        when(tagValidator.validate(tagName)).thenReturn(errorList);
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        assertThrows(InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testDelete() {
        GiftCertificate certificate = provideCertificate();
        long certificateId = 1;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));

        certificateService.delete(certificateId);
        verify(certificateRepository).delete(certificate);
    }

    @Test
    void testDeleteWhenCertificateNotFound() {
        long certificateId = 1;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));
    }

    private GiftCertificate provideCertificate() {
        GiftCertificate certificate = new GiftCertificate();

        certificate.setId(1);
        certificate.setName("certificate");
        certificate.setDescription("description");
        certificate.setPrice(BigDecimal.ONE);
        certificate.setDuration(Duration.ofDays(1));
        certificate.setCreateDate(INITIAL_DATE);
        certificate.setLastUpdateDate(INITIAL_DATE);

        certificate.setTags(provideTags());
        certificate.setOrders(List.of(mock(Order.class)));

        return certificate;
    }

    private GiftCertificateDto provideCertificateDto() {
        GiftCertificateDto certificateDto = new GiftCertificateDto();

        certificateDto.setId(1);
        certificateDto.setName("certificate");
        certificateDto.setDescription("description");
        certificateDto.setPrice(BigDecimal.ONE);
        certificateDto.setDuration(Duration.ofDays(1));
        certificateDto.setCreateDate(INITIAL_DATE);
        certificateDto.setLastUpdateDate(INITIAL_DATE);
        certificateDto.setTags(provideTagNames());

        return certificateDto;
    }

    private GiftCertificateSearchParamsDto provideSearchParamsDto() {
        GiftCertificateSearchParamsDto searchParamsDto = new GiftCertificateSearchParamsDto();

        searchParamsDto.setTagNames(List.of("tag1", "tag2"));
        searchParamsDto.setCertificateName("certificate");
        searchParamsDto.setCertificateDescription("description");
        searchParamsDto.setOrderByCreateDate(OrderingType.DESC);
        searchParamsDto.setOrderByName(OrderingType.ASC);

        return searchParamsDto;
    }

    private List<Tag> provideTags() {
        Tag firstTag = new Tag();
        firstTag.setId(1);
        firstTag.setName("tag1");

        Tag secondTag = new Tag();
        secondTag.setId(1);
        secondTag.setName("tag2");

        return new ArrayList<>() {{
            add(firstTag);
            add(secondTag);
        }};
    }

    private List<String> provideTagNames() {
        return new ArrayList<>() {{
           add("tag1");
           add("tag2");
        }};
    }
}
