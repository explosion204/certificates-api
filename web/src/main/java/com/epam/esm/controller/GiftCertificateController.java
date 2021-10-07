package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.PageContext;
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
    private HateoasProvider<GiftCertificateDto> hateoasProvider;

    public GiftCertificateController(GiftCertificateService certificateService,
                HateoasProvider<GiftCertificateDto> hateoasProvider) {
        this.certificateService = certificateService;
        this.hateoasProvider = hateoasProvider;
    }

    /**
     * Retrieve certificates according to specified parameters.
     * All parameters are optional, so if they are not present, all certificates will be retrieved.
     *
     * @param searchParamsDto {@link GiftCertificateSearchParamsDto} instance
     * @return JSON {@link ResponseEntity} object that contains list of {@link HateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<List<HateoasModel>> getCertificates(
            @ModelAttribute GiftCertificateSearchParamsDto searchParamsDto,
            @ModelAttribute PageContext pageContext
    ) {
        List<GiftCertificateDto> certificates = certificateService.find(searchParamsDto, pageContext);
        List<HateoasModel> models = HateoasModel.build(hateoasProvider, certificates);
        return new ResponseEntity<>(models, OK);
    }

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel> getCertificate(@PathVariable("id") long id) {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        HateoasModel model = HateoasModel.build(hateoasProvider, certificateDto);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Create a new certificate.
     *
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @PostMapping
    public ResponseEntity<HateoasModel> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        GiftCertificateDto createdDto = certificateService.create(certificateDto);
        HateoasModel model = HateoasModel.build(hateoasProvider, createdDto);
        return new ResponseEntity<>(model, CREATED);
    }

    /**
     * Update an existing certificate.
     *
     * @param id             certificate id
     * @param certificateDto {@link GiftCertificateDto} instance
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @throws InvalidEntityException in case when passed DTO object contains invalid data
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @PatchMapping("/{id}")
    public ResponseEntity<HateoasModel> updateCertificate(@PathVariable("id") long id,
                @RequestBody GiftCertificateDto certificateDto) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        HateoasModel model = HateoasModel.build(hateoasProvider, updatedDto);
        return new ResponseEntity<>(model, OK);
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
