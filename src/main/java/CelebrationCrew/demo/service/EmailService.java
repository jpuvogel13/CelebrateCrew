package CelebrationCrew.demo.service;

import org.springframework.stereotype.Service;

import CelebrationCrew.demo.entity.User;
import jakarta.mail.MessagingException;

@Service
public interface EmailService {

    void sendEmail(String to, String subject, String indicator, User user) throws MessagingException, MessagingException;

    void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath);

}
