package org.modis.EmsApplication.service.impl;

import org.modelmapper.ModelMapper;
import org.modis.EmsApplication.dao.HomeworkRepository;
import org.modis.EmsApplication.dao.SchoolYearRepository;
import org.modis.EmsApplication.dao.UserRepository;
import org.modis.EmsApplication.dto.CreateUserDTO;
import org.modis.EmsApplication.dto.UpdateUserDTO;
import org.modis.EmsApplication.dto.UserExposeDTO;
import org.modis.EmsApplication.dto.email.EmailDetails;
import org.modis.EmsApplication.model.Parent;
import org.modis.EmsApplication.model.Student;
import org.modis.EmsApplication.model.Teacher;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.exception.InvalidEntityDataException;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.service.EmailService;
import org.modis.EmsApplication.service.UserService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.modis.EmsApplication.utils.PassayPasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static org.modis.EmsApplication.dto.CreateUserDTO.parseUser;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final HomeworkRepository homeworkRepository;
    private final EmailService emailService;
    private final SchoolYearRepository schoolYearRepository;
    private final ModelMapper mapper = new ModelMapper();

    @Autowired
    public UserServiceImpl(UserRepository userRepository, HomeworkRepository homeworkRepository, EmailService emailService1, SchoolYearRepository schoolYearRepository) {
        this.userRepository = userRepository;
        this.homeworkRepository = homeworkRepository;
        this.emailService = emailService1;
        this.schoolYearRepository = schoolYearRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @PostFilter("filterObject.id == authentication.principal.id or hasRole('HEADMASTER')")
    public List<UserExposeDTO> getAllUsers() {
        return userRepository.findAll().stream().map(user -> mapper.map(user, UserExposeDTO.class)).collect(Collectors.toList());
    }

    @Override
    public List<User> getAllUsersModels() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public UserExposeDTO getUserById(Long id) throws NonexistingEntityException {
        return userRepository.findById(id).map(user -> mapper.map(user, UserExposeDTO.class)).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.USER_DOES_NOT_EXISTS, id)));
    }

    @Override
    public User getUserByIdModel(Long id) throws NonexistingEntityException {
        return userRepository.findById(id).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.USER_DOES_NOT_EXISTS, id)));
    }

    @Override
    public User getUserByEmail(String email) throws NonexistingEntityException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NonexistingEntityException(String.format(CommonMessages.USER_DOES_NOT_EXISTS_BY_EMAIL, email)));
    }

    @Override
    public UserExposeDTO create(CreateUserDTO createUserDTO) throws InvalidEntityDataException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException, NoSuchAlgorithmException {
        User userToBeCreated = parseUser(createUserDTO);

        if (userRepository.findByEmail(userToBeCreated.getEmail()).isPresent()) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_EMAIL, userToBeCreated.getEmail()));
        }

        String password = PassayPasswordGenerator.generatePassayPassword();

        MessageDigest md = MessageDigest.getInstance("SHA-512");
        byte[] raw = md.digest(password.getBytes(StandardCharsets.UTF_8));
        final String hashBase64 = Base64.getEncoder().encodeToString(md.digest(raw));

        userToBeCreated.setPassword(hashBase64);
        userToBeCreated.setAccountStatus(AccountStatus.PASSWORD_RESET);

        //Send email to the user with random generated secure password
        EmailDetails emailDetails = new EmailDetails(CommonMessages.EMS_CREATION_SERVICE_MAILBOX, userToBeCreated.getEmail(), String.format(CommonMessages.USER_CREATION_EMAIL, userToBeCreated.getFirstName(), userToBeCreated.getLastName(), userToBeCreated.getEmail(), password), CommonMessages.SCHOOL_ACCOUNT_SUCCESSFULLY_CREATED);
        emailService.sendEmail(emailDetails);

        userToBeCreated.setCreated(LocalDateTime.now());
        userToBeCreated.setModified(LocalDateTime.now());

        return mapper.map(userRepository.save(userToBeCreated), UserExposeDTO.class);
    }

    @Override
    public UserExposeDTO update(UpdateUserDTO updateUserDTO) throws InvalidEntityDataException, NonexistingEntityException {
        User user = getUserByIdModel(updateUserDTO.getId());
        User updatedUser = mapper.map(updateUserDTO, User.class);
        if (userRepository.findByEmail(updatedUser.getEmail()).isPresent() && !userRepository.findByEmail(updatedUser.getEmail()).get().equals(user)) {
            throw new InvalidEntityDataException(String.format(CommonMessages.ALREADY_EXISTING_EMAIL, updatedUser.getEmail()));
        }
        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setGender(updatedUser.getGender());
        user.setModified(LocalDateTime.now());
        if (user instanceof Teacher) {
            if (updateUserDTO.getSubject() != null) {
                ((Teacher) user).setSubject(updateUserDTO.getSubject());
            }
        }
        return mapper.map(userRepository.save(user), UserExposeDTO.class);
    }

    @Override
    public User updateModel(User userModel) throws InvalidEntityDataException, NonexistingEntityException {
        return userRepository.save(userModel);
    }

    @Override
    public UserExposeDTO deleteById(Long id) throws NonexistingEntityException {
        User old = getUserByIdModel(id);
        if (old instanceof Teacher) {
            ((Teacher) old).setTimetable(null);
            ((Teacher) old).setSchoolYears(new HashSet<>());
            userRepository.cleanTeacherFromSchoolYears(old.getId());
        }
        if (old instanceof Student) {
            ((Student) old).setTimetable(null);
            ((Student) old).setHomework(new ArrayList<>());
            homeworkRepository.cleanStudentEntryAnswer(old.getId());
            if (((Student) old).getSchoolYear() != null) {
                ((Student) old).getSchoolYear().getStudents().remove(old);
                schoolYearRepository.save(((Student) old).getSchoolYear());
            }
            userRepository.cleanExamResults(old.getId());
            userRepository.cleanExamAnswers(old.getId());
            userRepository.cleanCompetitionRegisteredStudents(old.getId());
            userRepository.cleanCompetitionPerformers(old.getId());
            userRepository.cleanCompetitionWinner(old.getId());
            userRepository.cleanCertificateOwner(old.getId());
        }
        if (old instanceof Parent) {
            userRepository.cleanParentRelation(old.getId());
        }
        userRepository.delete(old);
        return mapper.map(old, UserExposeDTO.class);
    }

    @Override
    @Transactional(readOnly = true)
    public String usersCount() {
        return String.valueOf(userRepository.count());
    }

}
