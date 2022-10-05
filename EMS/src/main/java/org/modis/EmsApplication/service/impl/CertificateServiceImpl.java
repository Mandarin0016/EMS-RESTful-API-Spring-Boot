package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.CertificateRepository;
import org.modis.EmsApplication.dao.CompetitionRepository;
import org.modis.EmsApplication.dto.CertificateExposeDTO;
import org.modis.EmsApplication.dto.CreateCertificateDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Certificate;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.service.CertificateService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {

    private final CertificateRepository certificateRepository;
    private final CompetitionRepository competitionRepository;
    private final UserService userService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public CertificateServiceImpl(CertificateRepository certificateRepository, UserService userService, CompetitionRepository competitionRepository) {
        this.certificateRepository = certificateRepository;
        this.userService = userService;
        this.competitionRepository = competitionRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CertificateExposeDTO> getAllCertificates() {
        return certificateRepository.findAll().stream().map(CertificateExposeDTO::parseModel).toList();
    }

    private Collection<Certificate> getAllCertificatesModels() {
        return certificateRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateExposeDTO getCertificateById(Long id) throws NonexistingEntityException {
        return certificateRepository.findById(id).stream().map(CertificateExposeDTO::parseModel).findFirst().orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.CERTIFICATE_DOES_NOT_EXISTS, id)));
    }

    @Override
    public Certificate getCertificateByIdModel(Long id) throws NonexistingEntityException {
        return certificateRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.CERTIFICATE_DOES_NOT_EXISTS, id)));
    }

    @Override
    public CertificateExposeDTO create(CreateCertificateDTO certificateDTO) throws InvalidEntityDataException, InvalidOperationException {
        Certificate certificate = mapper.map(certificateDTO, Certificate.class);
        if (certificateRepository.findAll().stream().anyMatch(model -> model.getImageUrl().equals(certificate.getImageUrl()))) {
            throw new InvalidOperationException(CommonMessages.SUCH_CERTIFICATE_ALREADY_EXISTS);
        }
        if (certificate.getOwnerId() != null) {
            User owner = userService.getUserByIdModel(certificate.getOwnerId());
            if (owner instanceof Student) {
                Certificate createdCertificate = certificateRepository.save(certificate);
                ((Student) owner).getCertificates().add(createdCertificate);
                return CertificateExposeDTO.parseModel(createdCertificate);
            } else {
                throw new InvalidOperationException(CommonMessages.CANT_ADD_CERTIFICATE_TO_NON_STUDENT);
            }
        }
        return CertificateExposeDTO.parseModel(certificateRepository.save(certificate));
    }

    @Override
    public CertificateExposeDTO update(Long id, CreateCertificateDTO newCertificateInformation) throws InvalidEntityDataException, NonexistingEntityException, InvalidOperationException {
        Certificate certificate = getCertificateByIdModel(id);
        if (getAllCertificatesModels().stream().anyMatch(model -> model.getImageUrl().equals(newCertificateInformation.getImageUrl()) && !certificate.getImageUrl().equals(newCertificateInformation.getImageUrl()))) {
            throw new InvalidOperationException(CommonMessages.SUCH_CERTIFICATE_ALREADY_EXISTS);
        }
        certificate.setImageUrl(newCertificateInformation.getImageUrl());
        certificateRepository.save(certificate);
        certificate.setModified(LocalDateTime.now());
        return CertificateExposeDTO.parseModel(certificateRepository.save(certificate));
    }

    @Override
    public Certificate updateModel(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    @Override
    public CertificateExposeDTO deleteById(Long id) throws NonexistingEntityException {
        Certificate certificate = getCertificateByIdModel(id);
        if (certificate.getOwnerId() != null) {
            ((Student) userService.getUserByIdModel(certificate.getOwnerId())).getCertificates().remove(certificate);
        }
        certificateRepository.deleteById(id);
        return CertificateExposeDTO.parseModel(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public UserExposeDTO getCertificateOwner(Long certificateId) throws NonexistingEntityException {
        Certificate certificate = getCertificateByIdModel(certificateId);
        if (certificate.getOwnerId() != null) {
            return mapper.map(userService.getUserById(certificate.getOwnerId()), UserExposeDTO.class);
        } else {
            throw new NonexistingEntityException(String.format(CommonMessages.CERTIFICATE_MISSING_OWNER, certificate.getId()));
        }
    }

    @Override
    public CertificateExposeDTO assignCertificateToStudent(Long certificateId, Long studentId) throws NonexistingEntityException, InvalidOperationException {
        Certificate certificate = getCertificateByIdModel(certificateId);
        User user = userService.getUserByIdModel(studentId);
        if (user instanceof Student) {
            if (((Student) user).getCertificates().stream().anyMatch(model -> model.getId().equals(certificate.getId()))) {
                throw new InvalidOperationException(String.format(CommonMessages.STUDENT_ALREADY_POSSESS_THIS_CERTIFICATE, user.getId()));
            }
            if (certificate.getOwnerId() != null) {
                throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_ACQUIRED_CERTIFICATE, certificate.getId(), certificate.getOwnerId()));
            }
            ((Student) user).getCertificates().add(certificate);
            user.setModified(LocalDateTime.now());
            certificate.setOwnerId(user.getId());
            certificate.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CANT_ADD_CERTIFICATE_TO_NON_STUDENT);
        }
        updateModel(certificate);
        userService.updateModel(user);
        return CertificateExposeDTO.parseModel(certificate);
    }

    @Override
    public CertificateExposeDTO removeCertificateFromStudent(Long certificateId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        Certificate certificate = getCertificateByIdModel(certificateId);
        if (certificate.getOwnerId() == null) {
            throw new NonexistingEntityException(String.format(CommonMessages.THIS_CERTIFICATE_HAS_NO_OWNER, certificate.getId()));
        }
        User user = userService.getUserByIdModel(certificate.getOwnerId());
        if (user instanceof Student) {
            ((Student) user).getCertificates().removeIf(model -> model.getId().equals(certificate.getId()));
            user.setModified(LocalDateTime.now());
            certificate.setOwnerId(null);
            certificate.setModified(LocalDateTime.now());
        } else {
            throw new InvalidOperationException(CommonMessages.CANT_ADD_CERTIFICATE_TO_NON_STUDENT);
        }
        updateModel(certificate);
        userService.updateModel(user);
        return CertificateExposeDTO.parseModel(certificate);
    }

    @Override
    @Transactional(readOnly = true)
    public String certificatesCount() {
        return String.valueOf(certificateRepository.count());
    }
}
