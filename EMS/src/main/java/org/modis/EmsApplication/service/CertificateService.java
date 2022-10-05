package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.CertificateExposeDTO;
import org.modis.EmsApplication.dto.CreateCertificateDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Certificate;
import org.modis.EmsApplication.model.User;

import java.util.Collection;

public interface CertificateService {
    Collection<CertificateExposeDTO> getAllCertificates();

    CertificateExposeDTO getCertificateById(Long id) throws NonexistingEntityException;

    Certificate getCertificateByIdModel(Long id) throws NonexistingEntityException;

    CertificateExposeDTO create(CreateCertificateDTO certificateDTO) throws InvalidEntityDataException, InvalidOperationException;

    CertificateExposeDTO update(Long id, CreateCertificateDTO certificateDTO) throws InvalidEntityDataException, NonexistingEntityException, InvalidOperationException;

    Certificate updateModel(Certificate certificate) throws InvalidEntityDataException, NonexistingEntityException, InvalidOperationException;

    CertificateExposeDTO deleteById(Long id) throws NonexistingEntityException;

    UserExposeDTO getCertificateOwner(Long certificateId) throws NonexistingEntityException;

    CertificateExposeDTO assignCertificateToStudent(Long certificateId, Long studentId) throws NonexistingEntityException, InvalidOperationException;

    CertificateExposeDTO removeCertificateFromStudent(Long certificateId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    String certificatesCount();
}
