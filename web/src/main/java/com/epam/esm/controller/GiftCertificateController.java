package com.epam.esm.controller;

import com.epam.esm.controller.hateoas.model.HateoasModel;
import com.epam.esm.controller.hateoas.HateoasProvider;
import com.epam.esm.controller.hateoas.model.ListHateoasModel;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.entity.GiftCertificate;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.PageContext;
import com.epam.esm.repository.exception.InvalidPageContextException;
import com.epam.esm.service.GiftCertificateService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.epam.esm.security.KeycloakAuthority.CERTIFICATES_DELETE;
import static com.epam.esm.security.KeycloakAuthority.CERTIFICATES_SAVE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
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
    private HateoasProvider<GiftCertificateDto> modelHateoasProvider;
    private HateoasProvider<List<GiftCertificateDto>> listHateoasProvider;

    public GiftCertificateController(
            GiftCertificateService certificateService,
            HateoasProvider<GiftCertificateDto> modelHateoasProvider,
            HateoasProvider<List<GiftCertificateDto>> listHateoasProvider) {
        this.certificateService = certificateService;
        this.modelHateoasProvider = modelHateoasProvider;
        this.listHateoasProvider = listHateoasProvider;
    }

    /**
     * Retrieve certificates according to specified parameters.
     * All parameters are optional, so if they are not present, all certificates will be retrieved.
     *
     * @param searchParamsDto {@link GiftCertificateSearchParamsDto} instance
     * @throws InvalidPageContextException if passed page or page size values are invalid
     * @return JSON {@link ResponseEntity} object that contains list of {@link ListHateoasModel} objects
     */
    @GetMapping
    public ResponseEntity<ListHateoasModel<GiftCertificateDto>> getCertificates(
            @ModelAttribute GiftCertificateSearchParamsDto searchParamsDto,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer pageSize
    ) {
        List<GiftCertificateDto> certificates = certificateService.find(searchParamsDto, PageContext.of(page, pageSize));
        ListHateoasModel<GiftCertificateDto> model = ListHateoasModel.build(listHateoasProvider, certificates);
        return new ResponseEntity<>(model, OK);
    }

    /**
     * Retrieve certificate by its unique id.
     *
     * @param id certificate id
     * @throws EntityNotFoundException in case when certificate with this id does not exist
     * @return JSON {@link ResponseEntity} object that contains {@link HateoasModel} object
     */
    @GetMapping("/{id}")
    public ResponseEntity<HateoasModel<GiftCertificateDto>> getCertificate(@PathVariable("id") long id) {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        HateoasModel<GiftCertificateDto> model = HateoasModel.build(modelHateoasProvider, certificateDto);
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
    @PreAuthorize("hasAuthority('" + CERTIFICATES_SAVE + "')")
    public ResponseEntity<HateoasModel<GiftCertificateDto>> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        GiftCertificateDto createdDto = certificateService.create(certificateDto);
        HateoasModel<GiftCertificateDto> model = HateoasModel.build(modelHateoasProvider, createdDto);
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
    @PreAuthorize("hasAuthority('" + CERTIFICATES_SAVE + "')")
    public ResponseEntity<HateoasModel<GiftCertificateDto>> updateCertificate(@PathVariable("id") long id,
                @RequestBody GiftCertificateDto certificateDto) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        HateoasModel<GiftCertificateDto> model = HateoasModel.build(modelHateoasProvider, updatedDto);
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
    @PreAuthorize("hasAuthority('" + CERTIFICATES_DELETE + "')")
    public ResponseEntity<Void> deleteCertificate(@PathVariable("id") long id) {
        certificateService.delete(id);
        return new ResponseEntity<>(NO_CONTENT);
    }
}
