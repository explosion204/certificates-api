package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.dto.GiftCertificateSearchParamsDto;
import com.epam.esm.service.GiftCertificateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/certificates")
public class GiftCertificateController {
    private GiftCertificateService certificateService;

    public GiftCertificateController(GiftCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public ResponseEntity<Object> getCertificates(GiftCertificateSearchParamsDto searchParamsDto) {
        List<GiftCertificateDto> certificates = certificateService.find(searchParamsDto);
        return ResponseEntityFactory.createResponseEntity(OK, certificates);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCertificate(@PathVariable("id") long id) {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, certificateDto);
    }

    @PostMapping
    public ResponseEntity<Object> createCertificate(@RequestBody GiftCertificateDto certificateDto) {
        long id = certificateService.create(certificateDto);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCertificate(@PathVariable("id") long id,
                @RequestBody GiftCertificateDto certificateDto) {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        return ResponseEntityFactory.createResponseEntity(OK, updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCertificate(@PathVariable("id") long id) {
        certificateService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
