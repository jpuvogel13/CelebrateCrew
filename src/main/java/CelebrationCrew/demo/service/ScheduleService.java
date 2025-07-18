package CelebrationCrew.demo.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import CelebrationCrew.demo.entity.Profile;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.repository.ProfileRepository;
import CelebrationCrew.demo.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScheduleService {

    private final EmailService emailService;
    private final UserRepository userRepo;
    private final ProfileRepository profileRepo;

    public ScheduleService(EmailService emailService,
                           UserRepository userRepo,
                           ProfileRepository profileRepo) {
        this.emailService = emailService;
        this.userRepo = userRepo;
        this.profileRepo = profileRepo;
    }

    @Scheduled(cron = "0 */2 * * * *") // Every 2 seconds
    public void scheduleSendingEmail() {
        LocalDate today = LocalDate.now();
        findAndSendAnniversaries(today);
        findAndSendBirthdays(today);

    }

    private void findAndSendAnniversaries(LocalDate today){
        List<Profile> usersWithAnniversaryNextWeek = profileRepo.findProfilesByJoiningDateBetween(today);
        for (Profile profile : usersWithAnniversaryNextWeek) {
            Optional<User> userOpt = userRepo.findById(profile.getUser().getId());
            if (userOpt.isEmpty()) {
                log.info("User with ID {} not found, skipping email sending for profile: {}", profile.getUser(), profile);
                continue;
            }
            User user = userOpt.get();
            String email = user.getEmail();
            String subject = "Happy Work Anniversary!!!";
            try {
                emailService.sendEmail(email, subject, "anniversary", user);
                log.info("Scheduled task running every 5 seconds to send email.");
            } catch (Exception e) {
                log.error("Failed to send birthday email to {}: {}", email, e.getMessage());
            }
        }
    }

    private void findAndSendBirthdays(LocalDate today){
        List<Profile> usersWithBirthdayNextWeek = profileRepo.findProfilesByBirthdateBetween((today));
        for (Profile profile : usersWithBirthdayNextWeek) {
            Optional<User> userOpt = userRepo.findById(profile.getUser().getId());
            if (userOpt.isEmpty()) {
                log.info("User with ID {} not found, skipping email sending for profile: {}", profile.getUser(), profile);
                continue;
            }
            User user = userOpt.get();
            String email = user.getEmail();
            String subject = "Happy Birthday!!!!!";
            try {
                emailService.sendEmail(email, subject, "birthday", user);
                log.info("Scheduled task running every 5 seconds to send email.");
            } catch (Exception e) {
                log.error("Failed to send anniversary email to {}: {}", email, e.getMessage());
            }
        }

    }


}















