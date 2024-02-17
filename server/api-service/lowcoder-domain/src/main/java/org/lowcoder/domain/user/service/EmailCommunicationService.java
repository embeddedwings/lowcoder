package org.lowcoder.domain.user.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j(topic = "EmailCommunicationService")
public class EmailCommunicationService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public boolean sendMail(String to, String token, String message) {
        try {
            String subject = "Lost Password Email";
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);

            mimeMessageHelper.setFrom(fromEmail);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject(subject);

            // Construct the message with the token link
            String resetLink = "http://localhost:8080/lost-password?token=" + token;
            String messageWithLink = message + "\n\nReset your password here: " + resetLink;
            mimeMessageHelper.setText(messageWithLink, true); // Set HTML to true to allow links

            javaMailSender.send(mimeMessage);

            return true;

        } catch (Exception e) {
            log.error("Failed to send mail to: {}, Exception: {}", to, e);
            return false;
        }


    }

}
