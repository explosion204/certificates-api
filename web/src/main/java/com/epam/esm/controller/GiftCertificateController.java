package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.service.GiftCertificateService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

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
     * @param searchParamsDto {@link GiftCertificateDto} instance
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and list of {@link GiftCertificateDto}
     */
    @GetMapping
    public ResponseEntity<Object> getCertificates(GiftCertificateSearchParamsDto searchParamsDto) {
        List<GiftCertificateDto> certificates = certificateService.find(searchParamsDto);
        return ResponseEntityFactory.createResponseEntity(OK, certificates);
    }

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and {@link GiftCertificateDto} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<Object> getCertificate(@PathVariable("id") long id) {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, certificateDto);
    }

    /**
     * Create a new certificate.
     *
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and unique id of the created {@link GiftCertificate}
     */
    @PostMapping
    public ResponseEntity<Object> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        long id = certificateService.create(certificateDto);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    /**
     * Update an existing certificate.
     *
     * @param id             certificate id
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     * and updated {@link GiftCertificateDto} object
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCertificate(@PathVariable("id") long id,
                @RequestBody GiftCertificateDto certificateDto) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        return ResponseEntityFactory.createResponseEntity(OK, updatedDto);
    }

    /**
     * Delete an existing certificate.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HttpStatus} code
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCertificate(@PathVariable("id") long id) {
        certificateService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
