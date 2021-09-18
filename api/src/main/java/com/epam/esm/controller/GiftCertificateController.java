package com.epam.esm.controller;

import com.epam.esm.controller.response.ResponseEntityFactory;
import com.epam.esm.entity.GiftCertificate;
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
    private static final String DATA_FIELD = "data";
    private GiftCertificateService certificateService;

    public GiftCertificateController(GiftCertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCertificate(@PathVariable("id") long id) throws ServiceException,
                EntityNotFoundException {
        GiftCertificate certificate = certificateService.findById(id);
        return ResponseEntityFactory.createResponseEntity(OK, certificate);
    }

    @PostMapping
    public ResponseEntity<Object> createCertificate(@RequestBody GiftCertificate certificate) throws ServiceException,
                InvalidEntityException {
        long id = certificateService.create(certificate, null);
        return ResponseEntityFactory.createResponseEntity(CREATED, id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateCertificate(@PathVariable("id") long id, @RequestBody GiftCertificate certificate)
                throws ServiceException, EntityNotFoundException, InvalidEntityException {
        certificateService.update(certificate, null);
        return ResponseEntityFactory.createResponseEntity(OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCertificate(@PathVariable("id") long id) throws ServiceException,
                EntityNotFoundException {
        certificateService.delete(id);
        return ResponseEntityFactory.createResponseEntity(OK);
    }
}
