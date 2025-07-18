package CelebrationCrew.demo.service.impl;

import java.util.List;

import CelebrationCrew.demo.entity.Message;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.repository.MessageRepository;
import CelebrationCrew.demo.repository.UserRepository;
import CelebrationCrew.demo.service.EmailService;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import CelebrationCrew.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
@Service
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final MessageRepository messageRepo;


    public EmailServiceImpl(JavaMailSender javaMailSender, MessageRepository messageRepo) {
        this.javaMailSender = javaMailSender;
        this.messageRepo = messageRepo;
    }
    @Override
    public void sendEmail(String to, String subject, String indicator, User user) throws MessagingException {
        String txt;
        if(indicator.equalsIgnoreCase("anniversary")) {
                txt = builtAnniversaryHtml(user, messageRepo.findByReceiver(user, "ANNIVERSARY"));
        } else {
            txt = builtBdayHTml(user, messageRepo.findByReceiver(user, "BIRTHDAY"));
            System.out.println("Unknown email indicator: " + indicator);
        }
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom("eradj.boboev@leasetrack.ai");

        helper.setText(txt, true);
        javaMailSender.send(message);
    }
    @Override
    public void sendEmailWithAttachment(String to, String subject, String body, String attachmentPath) {
        // Implementation for sending an email with an attachment
        System.out.println("Sending email with attachment to: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
        System.out.println("Attachment Path: " + attachmentPath);
    }

    private String builtAnniversaryHtml(User user, List<Message> messages){
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; text-align: center; background-color: #f9f9f9; padding: 20px;'>");
        html.append("<div style='background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; margin: 0 auto;'>");

// Happy Anniversary message
        html.append("<h1 style='color: #FF69B4;'>Happy Anniversary!</h1>");
        html.append("<p style='font-size: 18px; color: #333;'>Your loved ones are celebrating with you!</p>");

        html.append("<ul style='list-style-type: none; padding: 0;'>");

// Loop through messages
        for (Message message : messages) {
            html.append("<li style='background-color: #fafafa; border: 1px solid #ddd; border-radius: 5px; margin: 10px 0; padding: 10px;'>")
                .append("<div style='font-size: 16px; color: #333;'>")
                .append(message.getMessageText())
                .append("</div>")
                .append("<div style='font-size: 12px; color: #888; margin-top: 5px;'>")
                .append("</div>")
                .append("</li>");
        }

        html.append("</ul>");

// Ending message
        html.append("<p style='font-size: 16px; color: #333;'>Wishing you endless love,</p>");
        html.append("<p style='font-size: 16px; color: #333;'>Celebrate Team</p>");

        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }

    private String builtBdayHTml(User user, List<Message> messages){
        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; text-align: center; background-color: #f9f9f9; padding: 20px;'>");
        html.append("<div style='background-color: #ffffff; border-radius: 10px; padding: 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1); width: 80%; max-width: 600px; margin: 0 auto;'>");

// Happy Birthday message
        html.append("<h1 style='color: #4CAF50;'>Happy Birthday!</h1>");
        html.append("<p style='font-size: 18px; color: #333;'>Your friends are thinking of you!</p>");

        html.append("<ul style='list-style-type: none; padding: 0;'>");

// Loop through messages
        for (Message message : messages) {
            html.append("<li style='background-color: #fafafa; border: 1px solid #ddd; border-radius: 5px; margin: 10px 0; padding: 10px;'>")
                .append("<div style='font-size: 16px; color: #333;'>")
                .append(message.getMessageText())
                .append("</div>")
                .append("<div style='font-size: 12px; color: #888; margin-top: 5px;'>")
                .append("</div>")
                .append("</li>");
        }

        html.append("</ul>");

// Ending message
        html.append("<p style='font-size: 16px; color: #333;'>Have a great day,</p>");
        html.append("<p style='font-size: 16px; color: #333;'>Celebrate Team</p>");

        html.append("</div>");
        html.append("</body></html>");

        return html.toString();
    }
}