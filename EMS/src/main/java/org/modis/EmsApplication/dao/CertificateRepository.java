package org.modis.EmsApplication.dao;

import org.modis.EmsApplication.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    @Modifying
    @Query(value = "UPDATE student_certificates set student_id = :studentID WHERE certificates_id = :certificateID", nativeQuery = true)
    void updateCertificateOwner(@Param("studentID") Long studentID, @Param("certificateID") Long certificateID);
}
