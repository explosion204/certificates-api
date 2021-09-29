package com.epam.esm.controller;

import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.service.GiftCertificateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.*;

/**
 * This class contains public REST API endpoints related to {@link GiftCertificate} entity.
 *
 * @author Dmitry Karnyshov
 */
@RestController
@RequestMapping("/api/certificates")
public class GiftCertificateController {
    private GiftCertificateService certificateService;

    public GiftCertificateController(GiftCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    /**
     * Retrieve certificates according to specified parameters.
     * All parameters are optional, so if they are not present, all certificates will be retrieved.
     *
     * @param searchParamsDto {@link GiftCertificateSearchParamsDto} instance
     * @return JSON {@link ResponseEntity} object that contains list of {@link GiftCertificateDto}
     */
    @GetMapping
    public ResponseEntity<List<GiftCertificateDto>> getCertificates(
                @RequestParam GiftCertificateSearchParamsDto searchParamsDto) {
        List<GiftCertificateDto> certificates = certificateService.find(searchParamsDto);
        return new ResponseEntity<>(certificates, OK);
    }

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link GiftCertificateDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<GiftCertificateDto> getCertificate(@PathVariable("id") long id) {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        return new ResponseEntity<>(certificateDto, OK);
    }

    /**
     * Create a new certificate.
     *
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains created {@link GiftCertificateDto} object
     */
    @PostMapping
    public ResponseEntity<GiftCertificateDto> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        GiftCertificateDto createdCertificateDto = certificateService.create(certificateDto);
        return new ResponseEntity<>(createdCertificateDto, CREATED);
    }

    /**
     * Update an existing certificate.
     *
     * @param id             certificate id
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains updated {@link GiftCertificateDto} object
     */
    @PatchMapping("/{id}")
    public ResponseEntity<GiftCertificateDto> updateCertificate(@PathVariable("id") long id,
                @RequestBody GiftCertificateDto certificateDto) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        return new ResponseEntity<>(updatedDto, OK);
    }

    /**
     * Delete an existing certificate.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return empty {@link ResponseEntity}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable("id") long id) {
        certificateService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
