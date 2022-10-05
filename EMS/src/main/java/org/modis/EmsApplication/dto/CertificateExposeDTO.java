package org.modis.EmsApplication.dto;

import lombok.Data;
import org.modis.EmsApplication.model.Certificate;

import java.time.LocalDateTime;

@Data
public class CertificateExposeDTO {
    private Long id;
    private Long ownerId;
    private String imageUrl;
    private LocalDateTime created = LocalDateTime.now();
    private LocalDateTime modified = LocalDateTime.now();

    public static CertificateExposeDTO parseModel(Certificate certificate) {
        CertificateExposeDTO exposeDTO = new CertificateExposeDTO();
        exposeDTO.setImageUrl(certificate.getImageUrl());
        exposeDTO.setId(certificate.getId());
        exposeDTO.setOwnerId(certificate.getOwnerId());
        exposeDTO.setCreated(certificate.getCreated());
        exposeDTO.setModified(certificate.getModified());
        return exposeDTO;
    }

}
