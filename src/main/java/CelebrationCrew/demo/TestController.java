package CelebrationCrew.demo;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import lombok.AllArgsConstructor;


@Controller
@RequestMapping("/test")
public class TestController {
    private final JavaMailSender javaMailSender;
    public TestController(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello, World!";
    }

    @GetMapping("/greet")
    public String greet() {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo("justin.puvogel@leasetrack.ai");
        message.setSubject("Test Email");
        message.setText("This is a test email sent from the TestController.");
        message.setFrom("justin.puvogel@leasetrack.ai");
        javaMailSender.send(message);
        return "Hello!";
    }
}
