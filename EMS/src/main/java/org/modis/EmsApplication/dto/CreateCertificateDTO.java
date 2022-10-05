package org.modis.EmsApplication.dto;

import lombok.*;
import org.hibernate.validator.constraints.URL;
import org.modis.EmsApplication.model.Certificate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateCertificateDTO {
    @NonNull
    @URL
    private String imageUrl;
}
