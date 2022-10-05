package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Competition;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface CompetitionService {
    Collection<CompetitionExposeDTO> getAllCompetitions();

    CompetitionExposeDTO getCompetitionById(Long id) throws NonexistingEntityException;

    Competition getCompetitionByIdModel(Long id) throws NonexistingEntityException;

    CompetitionExposeDTO create(CompetitionDTO competition) throws InvalidEntityDataException;

    CompetitionExposeDTO update(Long competitionId, CompetitionDTO competition) throws InvalidEntityDataException, NonexistingEntityException;

    Competition updateModel(Competition competition) throws InvalidEntityDataException, NonexistingEntityException;

    CompetitionExposeDTO deleteById(Long id) throws NonexistingEntityException;

    CompetitionExposeDTO performCompetition(Long competitionId, Long studentId, List<Integer> competitionAnswers) throws NonexistingEntityException, InvalidOperationException;

    CompetitionExposeDTO assignCertificateById(Long competitionId, Long certificateId) throws NonexistingEntityException;

    CompetitionExposeDTO deleteCertificate(Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    CertificateExposeDTO getCertificate(Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    List<StudentExposeDTO> getRegisteredStudents(Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    Map<StudentExposeDTO, Double> showCompetitionResults(Long competitionId) throws NonexistingEntityException, InvalidEntityDataException;

    UserExposeDTO getCompetitionWinner(Long competitionId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException;

    CompetitionExposeDTO changeCompetitionWinner(Long competitionId, Long newWinnerId) throws NonexistingEntityException, InvalidOperationException;

    CompetitionExposeDTO deleteCompetitionWinner(Long competitionId) throws NonexistingEntityException, InvalidOperationException;

    String competitionsCount();
}
