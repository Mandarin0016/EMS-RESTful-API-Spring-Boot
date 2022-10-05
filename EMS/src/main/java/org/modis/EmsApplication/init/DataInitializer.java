package org.modis.EmsApplication.init;

import lombok.extern.slf4j.Slf4j;
import org.modis.EmsApplication.dao.UserRepository;
import org.modis.EmsApplication.exception.NonexistingEntityException;
import org.modis.EmsApplication.model.Headmaster;
import org.modis.EmsApplication.model.User;
import org.modis.EmsApplication.model.enums.AccountStatus;
import org.modis.EmsApplication.model.enums.Gender;
import org.modis.EmsApplication.model.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import static org.modis.EmsApplication.utils.CommonMessages.DEFAULT_HEADMASTER_ACCOUNT_SUCCESSFULLY_CREATED;


@Component
@Slf4j
@Profile("!test")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private static final User DEFAULT_HEADMASTER_ACCOUNT = new Headmaster();
    @Value("${environments.default-headmaster-account.credentials.email}")
    private String DEFAULT_HEADMASTER_EMAIL_ADDRESS;
    @Value("${environments.default-headmaster-account.credentials.password}")
    private String DEFAULT_HEADMASTER_PASSWORD;
    @Value("${environments.default-headmaster-account.details.first-name}")
    private String DEFAULT_HEADMASTER_FIRST_NAME;
    @Value("${environments.default-headmaster-account.details.last-name}")
    private String DEFAULT_HEADMASTER_LAST_NAME;
    @Value("${environments.default-headmaster-account.details.gender}")
    private String DEFAULT_HEADMASTER_GENDER;
    private final Role HEADMASTER_ROLE = Role.HEADMASTER;

    @Autowired
    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (userRepository.count() <= 0) {
            return;
        }
        if (!userRepository.existsByEmail(DEFAULT_HEADMASTER_EMAIL_ADDRESS)) {
            DEFAULT_HEADMASTER_ACCOUNT.setFirstName(DEFAULT_HEADMASTER_FIRST_NAME);
            DEFAULT_HEADMASTER_ACCOUNT.setLastName(DEFAULT_HEADMASTER_LAST_NAME);
            DEFAULT_HEADMASTER_ACCOUNT.setGender(Gender.valueOf(DEFAULT_HEADMASTER_GENDER));
            DEFAULT_HEADMASTER_ACCOUNT.setRole(HEADMASTER_ROLE);
            DEFAULT_HEADMASTER_ACCOUNT.setAccountStatus(AccountStatus.ACTIVE);
            DEFAULT_HEADMASTER_ACCOUNT.setEmail(DEFAULT_HEADMASTER_EMAIL_ADDRESS);
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] raw = md.digest(DEFAULT_HEADMASTER_PASSWORD.getBytes(StandardCharsets.UTF_8));
            final String hashBase64 = Base64.getEncoder().encodeToString(md.digest(raw));
            DEFAULT_HEADMASTER_ACCOUNT.setPassword(hashBase64);
            userRepository.save(DEFAULT_HEADMASTER_ACCOUNT);
        }
        User tp = userRepository.findByEmail(DEFAULT_HEADMASTER_EMAIL_ADDRESS).orElseThrow(() -> new NonexistingEntityException("THE DEFAULT_HEADMASTER ACCOUNT CAN'T BE FOUND."));
        log.info(String.format(DEFAULT_HEADMASTER_ACCOUNT_SUCCESSFULLY_CREATED, tp.getId(), tp.getFirstName(), tp.getLastName(), tp.getEmail(), tp.getRole().toString(), "Full access level."));
    }
}
