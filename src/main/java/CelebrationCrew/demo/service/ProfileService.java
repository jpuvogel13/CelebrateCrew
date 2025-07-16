package CelebrationCrew.demo.service;

import CelebrationCrew.demo.entity.Profile;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ProfileService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProfileRepository profileRepository;

    public List<Map<String, String>> getUpcomingBirthdays() {
        try {
            String sql = "SELECT p.first_name, p.last_name, p.birth_date, u.email " +
                    "FROM profiles p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "WHERE p.birth_date IS NOT NULL AND u.status = 'ACTIVE'";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            List<Map<String, String>> result = new ArrayList<>();

            System.out.println("=== DEBUG BIRTHDAYS ===");
            System.out.println("Total profiles found: " + rows.size());
            System.out.println("Today is: " + LocalDate.now());

            for (Map<String, Object> row : rows) {
                String firstName = (String) row.get("first_name");
                String lastName = (String) row.get("last_name");
                String email = (String) row.get("email");
                java.sql.Date sqlDate = (java.sql.Date) row.get("birth_date");

                if (sqlDate != null) {
                    LocalDate birthDate = sqlDate.toLocalDate();
                    System.out.println("Profile: " + firstName + " " + lastName + " - Birth: " + birthDate + " - Email: " + email);

                    if (isUpcomingEvent(birthDate)) {
                        System.out.println("MATCH! Adding birthday for: " + firstName);
                        Map<String, String> birthday = new HashMap<>();
                        birthday.put("name", firstName + " " + lastName);
                        birthday.put("email", email);
                        birthday.put("date", formatDate(birthDate));
                        result.add(birthday);
                    }
                }
            }

            System.out.println("Final birthdays result size: " + result.size());
            return result;

        } catch (Exception e) {
            System.out.println("Error in getUpcomingBirthdays: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Map<String, String>> getUpcomingAnniversaries() {
        try {
            String sql = "SELECT p.first_name, p.last_name, p.joining_date, u.email " +
                    "FROM profiles p " +
                    "JOIN users u ON p.user_id = u.id " +
                    "WHERE p.joining_date IS NOT NULL AND u.status = 'ACTIVE'";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

            List<Map<String, String>> result = new ArrayList<>();

            System.out.println("=== DEBUG ANNIVERSARIES ===");
            System.out.println("Total profiles found: " + rows.size());
            System.out.println("Today is: " + LocalDate.now());

            for (Map<String, Object> row : rows) {
                String firstName = (String) row.get("first_name");
                String lastName = (String) row.get("last_name");
                String email = (String) row.get("email");
                java.sql.Date sqlDate = (java.sql.Date) row.get("joining_date");

                if (sqlDate != null) {
                    LocalDate joiningDate = sqlDate.toLocalDate();
                    System.out.println("Profile: " + firstName + " " + lastName + " - Joining: " + joiningDate + " - Email: " + email);

                    if (isUpcomingEvent(joiningDate)) {
                        System.out.println("MATCH! Adding anniversary for: " + firstName);
                        Map<String, String> anniversary = new HashMap<>();
                        anniversary.put("name", firstName + " " + lastName);
                        anniversary.put("email", email);
                        anniversary.put("date", formatDate(joiningDate));
                        result.add(anniversary);
                    }
                }
            }

            System.out.println("Final anniversaries result size: " + result.size());
            return result;

        } catch (Exception e) {
            System.out.println("Error in getUpcomingAnniversaries: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public Profile getProfileByUser(User user) {
        Optional<Profile> profileOpt = profileRepository.findByUser(user);
        return profileOpt.orElse(null);
    }

    public Profile saveProfile(Profile profile) {
        return profileRepository.save(profile);
    }

    private boolean isUpcomingEvent(LocalDate eventDate) {
        LocalDate today = LocalDate.now();

        // Check if the event (birthday/anniversary) is in the next 7 days
        for (int i = 0; i <= 7; i++) {
            LocalDate checkDate = today.plusDays(i);
            if (eventDate.getMonth() == checkDate.getMonth() &&
                    eventDate.getDayOfMonth() == checkDate.getDayOfMonth()) {
                return true;
            }
        }
        return false;
    }

    private String formatDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return date.format(formatter);
    }
}