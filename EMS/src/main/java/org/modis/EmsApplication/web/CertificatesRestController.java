package org.modis.EmsApplication.web;

import org.modis.EmsApplication.dto.CertificateExposeDTO;
import org.modis.EmsApplication.dto.CreateCertificateDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.model.Certificate;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;

@RestController
@RequestMapping("/api/certificates")
public class CertificatesRestController {

    private final CertificateService certificateService;

    @Autowired
    public CertificatesRestController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping
    public Collection<CertificateExposeDTO> getAllCertificates() {
        return certificateService.getAllCertificates();
    }

    @PostMapping
    public ResponseEntity<CertificateExposeDTO> createCertificate(@Valid @RequestBody CreateCertificateDTO createCertificateDTO) throws InvalidOperationException {
        CertificateExposeDTO certificate = certificateService.create(createCertificateDTO);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest().pathSegment("{id}")
                .buildAndExpand(certificate.getId()).toUri()).body(certificate);
    }

    @GetMapping("/{id:\\d+}")
    public CertificateExposeDTO getCertificateById(@PathVariable("id") Long id) {
        return certificateService.getCertificateById(id);
    }

    @PutMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.OK)
    public CertificateExposeDTO updateCertificateById(@PathVariable("id") Long id, @Valid @RequestBody CreateCertificateDTO createCertificateDTO) throws InvalidOperationException {
        return certificateService.update(id, createCertificateDTO);
    }

    @DeleteMapping("/{id:\\d+}")
    public CertificateExposeDTO deleteCertificateById(@PathVariable("id") Long id) {
        return certificateService.deleteById(id);
    }

    @GetMapping("/{id}/owner")
    public UserExposeDTO getCertificateOwner(@PathVariable("id") Long certificateId) {
        return certificateService.getCertificateOwner(certificateId);
    }

    @PostMapping("/{id}/owner")
    public ResponseEntity<CertificateExposeDTO> addCertificateOwner(@PathVariable("id") Long certificateId, @RequestParam(name = "userId") Long userId) throws InvalidOperationException, InterruptedException {
        CertificateExposeDTO certificate = certificateService.assignCertificateToStudent(certificateId, userId);
        return ResponseEntity.created(
                ServletUriComponentsBuilder.fromCurrentRequest().replacePath("/api/certificate/{id}").buildAndExpand(certificate.getId()).toUri()
        ).body(certificate);
    }

    @DeleteMapping("/{id}/owner")
    public CertificateExposeDTO removeCertificateOwner(@PathVariable("id") Long certificateId) throws InvalidOperationException {
        return certificateService.removeCertificateFromStudent(certificateId);
    }

    @GetMapping("/count")
    public String getCertificatesCount() {
        return certificateService.certificatesCount();
    }
}
