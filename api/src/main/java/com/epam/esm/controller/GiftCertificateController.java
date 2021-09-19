package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.dto.GiftCertificateDto;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.exception.ServiceException;
import com.epam.esm.service.GiftCertificateService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/certificates")
public class GiftCertificateController {
    private GiftCertificateService certificateService;

    public GiftCertificateController(GiftCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCertificate(@PathVariable("id") long id) throws ServiceException,
                EntityNotFoundException {
        GiftCertificateDto certificateDto = certificateService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, certificateDto);
    }

    @PostMapping
    public ResponseEntity<Object> createCertificate(@RequestBody GiftCertificateDto certificateDto) throws ServiceException,
                InvalidEntityException {
        long id = certificateService.create(certificateDto);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCertificate(@PathVariable("id") long id, @RequestBody GiftCertificateDto certificateDto)
                throws ServiceException, EntityNotFoundException, InvalidEntityException {
        certificateDto.setId(id);
        GiftCertificateDto updatedDto = certificateService.update(certificateDto);
        return ResponseEntityFactory.createResponseEntity(OK, updatedDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCertificate(@PathVariable("id") long id) throws ServiceException,
                EntityNotFoundException {
        certificateService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
