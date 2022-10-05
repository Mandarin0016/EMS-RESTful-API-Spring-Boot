package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.CompetitionQuestionRepository;
import org.modis.EmsApplication.dao.CompetitionRepository;
import org.modis.EmsApplication.dto.*;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.InvalidOperationException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.*;
import org.modis.EmsApplication.service.CertificateService;
import org.modis.EmsApplication.service.CompetitionService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CompetitionServiceImpl implements CompetitionService {

    private final CompetitionRepository competitionRepository;
    private final CompetitionQuestionRepository competitionQuestionRepository;
    private final UserService userService;
    private final CertificateService certificateService;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public CompetitionServiceImpl(CompetitionRepository competitionRepository, CompetitionQuestionRepository competitionQuestionRepository, UserService userService, CertificateService certificateService) {
        this.competitionRepository = competitionRepository;
        this.competitionQuestionRepository = competitionQuestionRepository;
        this.userService = userService;
        this.certificateService = certificateService;
    }

    @Override
    @Transactional(readOnly = true)
    public Collection<CompetitionExposeDTO> getAllCompetitions() {
        return competitionRepository.findAll().stream().map(competition -> mapper.map(competition, CompetitionExposeDTO.class)).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CompetitionExposeDTO getCompetitionById(Long id) throws NonexistingEntityException {
        return competitionRepository.findById(id).stream().map(competition -> mapper.map(competition, CompetitionExposeDTO.class)).findFirst().orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.COMPETITION_DOES_NOT_EXISTS, id)));
    }

    @Override
    public Competition getCompetitionByIdModel(Long id) throws NonexistingEntityException {
        return competitionRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.COMPETITION_DOES_NOT_EXISTS, id)));
    }

    @Override
    public CompetitionExposeDTO create(CompetitionDTO competitionDTO) throws InvalidEntityDataException {
        Competition competition = CompetitionDTO.parseDTO(competitionDTO);
        competitionQuestionRepository.saveAll(competition.getQuestions());
        competition.setId(null);
        return mapper.map(competitionRepository.save(competition), CompetitionExposeDTO.class);
    }

    @Override
    public CompetitionExposeDTO update(Long competitionId, CompetitionDTO competitionDTO) throws InvalidEntityDataException, NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(competitionId);
        competitionQuestionRepository.deleteAll(competition.getQuestions());
        LocalDateTime startDate = LocalDateTime.parse(String.format("%02d-%02d-%02dT%02d:%02d:00", competitionDTO.getStartYear(), competitionDTO.getStartMonth(), competitionDTO.getStartDay(), competitionDTO.getStartHour(), competitionDTO.getStartMinutes()));
        LocalDateTime endDate = LocalDateTime.parse(String.format("%02d-%02d-%02dT%02d:%02d:00", competitionDTO.getStartYear(), competitionDTO.getEndMonth(), competitionDTO.getEndDay(), competitionDTO.getEndHour(), competitionDTO.getEndMinutes()));
        competition.setStartDate(startDate);
        competition.setEndDate(endDate);
        competition.setQuestions(competitionDTO.getQuestions().stream().map(CompetitionQuestionDTO::parseDTO).collect(Collectors.toList()));
        competitionQuestionRepository.saveAll(competition.getQuestions());
        competition.setCOMPETITION_QUESTION_POINTS(competitionDTO.getPointsPerQuestion());
        competition.setModified(LocalDateTime.now());
        return mapper.map(competitionRepository.save(competition), CompetitionExposeDTO.class);
    }

    @Override
    public Competition updateModel(Competition competition) {
        return competitionRepository.save(competition);
    }

    @Override
    public CompetitionExposeDTO deleteById(Long id) throws NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(id);
        competition.getRegisteredStudent().forEach(registeredStudent -> registeredStudent.getCompetitions().removeIf(model -> model.getId().equals(id)));
        competitionRepository.deleteById(id);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    @PreAuthorize("#studentId == authentication.principal.id or hasRole('ROLE_HEADMASTER')")
    public CompetitionExposeDTO performCompetition(Long competitionId, Long studentId, List<Integer> competitionAnswers) throws NonexistingEntityException, InvalidOperationException {
        Competition competition = getCompetitionByIdModel(competitionId);
        User student = userService.getUserByIdModel(studentId);
        if (!(student instanceof Student)) {
            throw new InvalidOperationException(CommonMessages.CAN_NOT_PERFORM_THIS_OPERATION_ON_NON_STUDENT);
        }
        if (!((Student) student).getCompetitions().contains(competition)) {
            throw new NonexistingEntityException(String.format(CommonMessages.STUDENT_IS_NOT_REGISTERED_FOR_THIS_COMPETITION, student.getId(), competition.getId()));
        }
        if (!Competition.isStarted(competition.getStartDate())) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_NOT_STARTED_YET, competitionId));
        }
        if (Competition.isFinished(competition.getEndDate())) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_FINISHED, competitionId));
        }
        if (competition.getPerformers().containsKey(student)) {
            throw new InvalidOperationException(String.format(CommonMessages.STUDENT_ALREADY_PARTICIPATED_IN_THIS_COMPETITION, student.getId(), competition.getId()));
        }
        competition.executeCompetitionTest((Student) student, competitionAnswers);
        competition.setModified(LocalDateTime.now());
        ((Student) student).getCompetitions().remove(competition);
        userService.updateModel(student);
        competitionRepository.save(competition);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    public CompetitionExposeDTO assignCertificateById(Long competitionId, Long certificateId) throws NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(competitionId);
        Certificate certificate = certificateService.getCertificateByIdModel(certificateId);
        if (competition.getCertificateReward() != null) {
            throw new InvalidEntityDataException(String.format(CommonMessages.COMPETITION_ALREADY_POSSESS_CERTIFICATE, competition.getId(), certificate.getId()));
        }
        if (certificate.getOwnerId() != null) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_ACQUIRED_CERTIFICATE, certificate.getId(), certificate.getOwnerId()));
        }
        competition.setModified(LocalDateTime.now());
        competition.setCertificateReward(certificate);
        competitionRepository.save(competition);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    public CompetitionExposeDTO deleteCertificate(Long competitionId) throws NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(competitionId);
        if (competition.getCertificateReward() == null) {
            throw new NonexistingEntityException(String.format(CommonMessages.COMPETITION_MISSING_CERTIFICATE, competition.getId()));
        }
        if (competition.getCertificateReward().getOwnerId() != null) {
            User user = userService.getUserByIdModel(competition.getCertificateReward().getOwnerId());
            ((Student) user).getCertificates().remove(competition.getCertificateReward());
            userService.updateModel(user);
        }
        competition.setCertificateReward(null);
        competition.setModified(LocalDateTime.now());
        competitionRepository.save(competition);
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public CertificateExposeDTO getCertificate(Long competitionId) throws NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(competitionId);
        if (competition.getCertificateReward() == null) {
            throw new NonexistingEntityException(String.format(CommonMessages.COMPETITION_MISSING_CERTIFICATE, competition.getId()));
        }
        return CertificateExposeDTO.parseModel(competition.getCertificateReward());
    }

    @Override
    @Transactional(readOnly = true)
    public List<StudentExposeDTO> getRegisteredStudents(Long competitionId) throws NonexistingEntityException {
        Competition competition = getCompetitionByIdModel(competitionId);
        return competition.getRegisteredStudent().stream().map(StudentExposeDTO::parseModel).toList();
    }

    @Override
    public UserExposeDTO getCompetitionWinner(Long competitionId) throws NonexistingEntityException, InvalidOperationException, InvalidEntityDataException {
        Competition competition = getCompetitionByIdModel(competitionId);
        if (!Competition.isFinished(competition.getEndDate())) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_NOT_FINISHED_YET, competition.getId()));
        }
        if (competition.getCertificateReward() == null) {
            throw new NonexistingEntityException(String.format(CommonMessages.COMPETITION_MISSING_REWARD, competition.getId()));
        }
        if (competition.getWinner() != null) {
            return mapper.map(competition.getWinner(), UserExposeDTO.class);
        }
        Certificate certificate = certificateService.getCertificateByIdModel(competition.getCertificateReward().getId());
        User competitionWinner = competition.findWinner();
        competition.setWinner((Student) competitionWinner);
        ((Student) competitionWinner).getCertificates().add(certificate);
        certificate.setOwnerId(competitionWinner.getId());
        userService.updateModel(competitionWinner);
        competitionRepository.save(competition);
        certificateService.updateModel(certificate);
        return mapper.map(competitionWinner, UserExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Map<StudentExposeDTO, Double> showCompetitionResults(Long competitionId) throws NonexistingEntityException, InvalidEntityDataException {
        Competition competition = getCompetitionByIdModel(competitionId);
        Map<StudentExposeDTO, Double> exposeMap = new HashMap<>();
        for (Map.Entry<Student, Double> studentDoubleEntry : competition.getPerformers().entrySet()) {
            exposeMap.put(StudentExposeDTO.parseModel(studentDoubleEntry.getKey()), studentDoubleEntry.getValue());
        }
        return exposeMap;
    }

    @Override
    public CompetitionExposeDTO deleteCompetitionWinner(Long competitionId) throws NonexistingEntityException, InvalidOperationException {
        Competition competition = getCompetitionByIdModel(competitionId);
        if (!Competition.isFinished(competition.getEndDate())) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_NOT_FINISHED_YET, competition.getId()));
        }
        if (competition.getWinner() != null) {
            Certificate certificate = competition.getCertificateReward();
            Student oldWinner = competition.getWinner();
            oldWinner.getCertificates().remove(certificate);
            certificate.setOwnerId(null);
            competition.setWinner(null);
            certificate.setModified(LocalDateTime.now());
            oldWinner.setModified(LocalDateTime.now());
            competition.setModified(LocalDateTime.now());
            competitionRepository.save(competition);
            userService.updateModel(oldWinner);
            certificateService.updateModel(certificate);
        } else {
            throw new NonexistingEntityException(String.format(CommonMessages.COMPETITION_MISSING_WINNER, competition.getId()));
        }
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    public CompetitionExposeDTO changeCompetitionWinner(Long competitionId, Long newWinnerId) throws NonexistingEntityException, InvalidOperationException {
        Competition competition = getCompetitionByIdModel(competitionId);
        User newWinner = userService.getUserByIdModel(newWinnerId);
        if (!(newWinner instanceof Student)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.INVALID_USER_TYPE, newWinner.getId(), Student.class.getSimpleName()));
        }
        if (!Competition.isFinished(competition.getEndDate())) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_NOT_FINISHED_YET, competition.getId()));
        }
        if (competition.getCertificateReward() == null) {
            throw new InvalidOperationException(String.format(CommonMessages.COMPETITION_MISSING_REWARD, competition.getId()));
        }
        if (!competition.getPerformers().containsKey(newWinner)) {
            throw new InvalidOperationException(String.format(CommonMessages.ONLY_PERFORMERS_CAN_BECOME_WINNERS, newWinner.getId()));
        }
        if (competition.getWinner() != null) {
            if (Objects.equals(competition.getWinner().getId(), newWinnerId)) {
                throw new InvalidOperationException(CommonMessages.THIS_STUDENT_IS_ALREADY_WINNER);
            }
            Student oldWinner = competition.getWinner();
            oldWinner.getCompetitions().remove(competition);
            oldWinner.getCertificates().remove(competition.getCertificateReward());

            Certificate certificate = competition.getCertificateReward();
            certificate.setOwnerId(newWinner.getId());
            certificate.setModified(LocalDateTime.now());

            competition.setWinner((Student) newWinner);
            ((Student) newWinner).getCertificates().add(certificate);

            newWinner.setModified(LocalDateTime.now());
            oldWinner.setModified(LocalDateTime.now());
            competition.setModified(LocalDateTime.now());

            userService.updateModel(newWinner);
            userService.updateModel(oldWinner);
            certificateService.updateModel(certificate);
            competitionRepository.save(competition);

        } else {
            Certificate certificate = competition.getCertificateReward();
            certificate.setOwnerId(newWinner.getId());
            certificate.setModified(LocalDateTime.now());
            competition.setWinner((Student) newWinner);
            ((Student) newWinner).getCertificates().add(certificate);
            newWinner.setModified(LocalDateTime.now());
            competition.setModified(LocalDateTime.now());
            userService.updateModel(newWinner);
            certificateService.updateModel(certificate);
            competitionRepository.save(competition);
        }
        return mapper.map(competition, CompetitionExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String competitionsCount() {
        return String.valueOf(competitionRepository.count());
    }
}
