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
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(PER_CLASS)
class GiftCertificateServiceTest {
    private static final ZonedDateTime INITIAL_DATE = LocalDateTime.now().atZone(ZoneOffset.UTC);

    @InjectMocks
    private GiftCertificateService certificateService;

    @Mock
    private GiftCertificateRepository certificateRepository;

    @Mock
    private TagRepository tagRepository;

    @Spy
    private GiftCertificateValidator certificateValidator;

    @Spy
    private TagValidator tagValidator;

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

        when(certificateRepository.find(anyString(), anyString(), anyString(), any(OrderingType.class),
                any(OrderingType.class))).thenReturn(certificateList);
        when(tagRepository.findByCertificate(anyLong())).thenReturn(provideTags());

        GiftCertificateSearchParamsDto searchParamsDto = provideSearchParamsDto();
        List<GiftCertificateDto> actualDtoList = certificateService.find(searchParamsDto);

        int expectedInteractions = 1;
        verify(certificateRepository, times(expectedInteractions)).find(anyString(), anyString(), anyString(),
                any(OrderingType.class), any(OrderingType.class));

        assertEquals(certificateDtoList, actualDtoList);
    }

    @Test
    void testFindById() {
        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        certificate.setId(certificateId);

        GiftCertificateDto expectedCertificateDto = provideCertificateDto();
        expectedCertificateDto.setId(certificateId);

        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(certificate));
        when(tagRepository.findByCertificate(anyLong())).thenReturn(provideTags());

        GiftCertificateDto actualCertificateDto = certificateService.findById(certificateId);

        int expectedInteractions = 1;
        verify(certificateRepository, times(expectedInteractions)).findById(anyLong());
        verify(tagRepository, times(expectedInteractions)).findByCertificate(anyLong());

        assertEquals(expectedCertificateDto, actualCertificateDto);
    }

    @Test
    void testFindByIdWhenCertificateNotFound() {
        int certificateId = 1;
        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> certificateService.findById(certificateId));
    }

    @Test
    void testCreate() {
        GiftCertificateDto certificateDto = provideCertificateDto();

        certificateService.create(certificateDto);

        int expectedInteractions = 1;
        verify(certificateValidator, times(expectedInteractions)).validate(any(GiftCertificate.class), eq(false));
        verify(certificateRepository, times(expectedInteractions)).create(any());
    }

    @Test
    void testCreateWhenCertificateInvalid() {
        GiftCertificateDto certificateDto = provideCertificateDto();
        certificateDto.setName(null);

        assertThrows(InvalidEntityException.class, () -> certificateService.create(certificateDto));
    }

    @Test
    void testUpdate() {
        Tag tag = provideTags().get(0);

        long certificateId = 1;
        GiftCertificate certificate = provideCertificate();
        certificate.setId(certificateId);
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setId(certificateId);

        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(certificate));
        when(certificateRepository.update(any(GiftCertificate.class))).thenReturn(true);
        when(tagRepository.findByCertificate(anyLong())).thenReturn(provideTags());
        when(tagRepository.findByName(anyString())).thenReturn(Optional.empty());

        certificateService.update(updatedCertificateDto);

        int expectedInteractions = 1;
        verify(certificateValidator, times(expectedInteractions)).validate(any(GiftCertificate.class), eq(true));
        verify(certificateRepository, times(expectedInteractions)).update(any(GiftCertificate.class));
        verify(tagValidator, times(expectedInteractions)).validate(anyString());
        verify(tagRepository, times(expectedInteractions)).findByName(anyString());
        verify(certificateRepository, times(expectedInteractions)).attachTag(anyLong(), anyLong());
    }

    @Test
    void testUpdateWhenCertificateNotFound() {
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();

        when(certificateRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testUpdateWhenCertificateInvalid() {
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setName("");

        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(certificate));

        assertThrows(InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testUpdateWhenTagInvalid() {
        GiftCertificate certificate = provideCertificate();
        GiftCertificateDto updatedCertificateDto = provideCertificateDto();
        updatedCertificateDto.setTags(new ArrayList<>() {{ add(""); }});

        when(certificateRepository.findById(anyLong())).thenReturn(Optional.of(certificate));
        when(certificateRepository.update(any(GiftCertificate.class))).thenReturn(true);

        assertThrows(InvalidEntityException.class, () -> certificateService.update(updatedCertificateDto));
    }

    @Test
    void testDelete() {
        when(certificateRepository.delete(anyLong())).thenReturn(true);

        int certificateId = 1;
        certificateRepository.delete(certificateId);

        int expectedInteractions = 1;
        verify(certificateRepository, times(expectedInteractions)).delete(anyLong());
    }

    @Test
    void testDeleteWhenCertificateNotFound() {
        when(certificateRepository.delete(anyLong())).thenReturn(false);

        int certificateId = 1;
        assertThrows(EntityNotFoundException.class, () -> certificateService.delete(certificateId));

        int expectedInteractions = 1;
        verify(certificateRepository, times(expectedInteractions)).delete(anyLong());
    }

    private GiftCertificate provideCertificate() {
        GiftCertificate certificate = new GiftCertificate();
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
        searchParamsDto.setOrderByCreateDate(OrderingType.ASC);
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
