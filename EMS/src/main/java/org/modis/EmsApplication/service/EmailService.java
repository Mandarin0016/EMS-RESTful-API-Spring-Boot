package org.modis.EmsApplication.service;

import org.modis.EmsApplication.dto.email.EmailDetails;

public interface EmailService {
    void sendEmail(EmailDetails details);
}
