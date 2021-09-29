package com.epam.esm.service;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.entity.Tag;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.GiftCertificateRepository;
import com.epam.esm.repository.OrderingType;
import com.epam.esm.repository.TagRepository;
import com.epam.esm.validator.GiftCertificateValidator;
import com.epam.esm.validator.TagValidator;
import com.epam.esm.validator.ValidationError;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
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
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
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
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFind() {
        List<GiftCertificate> certificateList = new ArrayList<>() {{
            add(provideCertificate());
        }};

        List<GiftCertificateDto> certificateDtoList = new ArrayList<>() {{
            add(provideCertificateDto());
        }};

        long certificateId = 1;
        String tagName = "tag";
        String certificateName = "certificate";
        String certificateDescription = "description";
        OrderingType orderByName = OrderingType.ASC;
        OrderingType orderByCreateDate = OrderingType.DESC;
        when(certificateRepository.find(tagName, certificateName, certificateDescription, orderByName,
                orderByCreateDate)).thenReturn(certificateList);
        when(tagRepository.findByCertificate(certificateId)).thenReturn(provideTags());

        GiftCertificateSearchParamsDto searchParamsDto = provideSearchParamsDto();
        List<GiftCertificateDto> actualDtoList = certificateService.find(searchParamsDto);

        verify(certificateRepository).find(tagName, certificateName, certificateDescription,
               orderByName, orderByCreateDate);

        assertEquals(certificateDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto expectedCertificateDto = provideCertificateDto();

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(tagRepository.findByCertificate(certificateId)).thenReturn(provideTags());

        GiftCertificateDto actualCertificateDto = certificateService.findById(certificateId);

        verify(certificateRepository).findById(anyLong());
        verify(tagRepository).findByCertificate(anyLong());

        assertEquals(expectedCertificateDto, actualCertificateDto);
    }

    @Test
    void testFindByIdWhenCertificateNotFound() {
        int certificateId = 1;
        when(certificateRepository.findById(certificateId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
    }

    @Test
    void testCreate() {
        GiftCertificateDto certificateDto = provideCertificateDto();

        certificateService.create(certificateDto);

        verify(certificateValidator).validate(certificateCaptor.capture(), eq(false));
        GiftCertificate certificate = certificateCaptor.getValue();
        verify(certificateRepository).create(certificate);
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
        long tagId = 1;
        String tagName = "tag";
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setId(certificateId);

        Tag newTag = new Tag();
        newTag.setName(tagName);

        when(certificateRepository.findById(certificateId)).thenReturn(Optional.of(certificate));
        when(certificateRepository.update(certificate)).thenReturn(true);
        when(tagRepository.findByCertificate(certificateId)).thenReturn(provideTags());
        when(tagRepository.findByName(tagName)).thenReturn(Optional.empty());
        when(tagRepository.create(newTag)).thenReturn(tagId);

        certificateService.update(updatedCertificateDto);

        verify(certificateValidator).validate(certificateCaptor.capture(), eq(true));
        GiftCertificate capturedCertificate = certificateCaptor.getValue();
        verify(certificateRepository).update(capturedCertificate);
        verify(tagValidator).validate(tagName);
        verify(tagRepository).findByName(tagName);
        verify(certificateRepository).attachTag(certificateId, tagId);
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
        long certificateId = 1;
        when(certificateRepository.delete(certificateId)).thenReturn(true);

        certificateRepository.delete(certificateId);

        verify(certificateRepository).delete(certificateId);
    }

    @Test
    void testDeleteWhenCertificateNotFound() {
        long certificateId = 1;
        when(certificateRepository.delete(certificateId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));

        verify(certificateRepository).delete(certificateId);
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

        searchParamsDto.setTagName("tag");
        searchParamsDto.setCertificateName("certificate");
        searchParamsDto.setCertificateDescription("description");
        searchParamsDto.setOrderByCreateDate(OrderingType.DESC);
        searchParamsDto.setOrderByName(OrderingType.ASC);

        return searchParamsDto;
    }

    private List<Tag> provideTags() {
        Tag tag = new Tag();

        tag.setId(1);
        tag.setName("tag");

        return new ArrayList<>() {{
            add(tag);
        }};
    }

    private List<String> provideTagNames() {
        return new ArrayList<>() {{
           add("tag");
        }};
    }
}
