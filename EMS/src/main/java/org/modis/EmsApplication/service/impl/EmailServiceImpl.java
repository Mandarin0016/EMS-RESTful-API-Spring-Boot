package org.modis.EmsApplication.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.modis.EmsApplication.dto.email.EmailDetails;
import org.modis.EmsApplication.service.EmailService;
import org.modis.EmsApplication.utils.CommonMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Autowired
    public EmailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendEmail(EmailDetails emailDetails){
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        try {
            mimeMessage.setSubject(emailDetails.getSubject());
            MimeMessageHelper helper;
            helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(emailDetails.getSender());
            helper.setTo(emailDetails.getRecipient());
            helper.setText(emailDetails.getMsgBody(), true);
            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            log.warn(CommonMessages.EMAIL_CANT_BE_SENT);
        }
    }
}
