package CelebrationCrew.demo.controller;

import CelebrationCrew.demo.entity.Profile;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.repository.UserRepository;
import CelebrationCrew.demo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(HttpSession session) {
        String userEmail = (String) session.getAttribute("user");
        if (userEmail == null) {
            return ResponseEntity.status(401).body(null);
        }

        Map<String, Object> response = new HashMap<>();

        try {
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            User user = userOpt.get();
            Profile profile = profileService.getProfileByUser(user);

            if (profile == null) {
                response.put("error", "Profile not found");
                return ResponseEntity.status(404).body(response);
            }

            response.put("id", profile.getId());
            response.put("firstName", profile.getFirstName());
            response.put("lastName", profile.getLastName());
            response.put("birthDate", profile.getBirthDate() != null ? profile.getBirthDate().toString() : "");
            response.put("joiningDate", profile.getJoiningDate() != null ? profile.getJoiningDate().toString() : "");
            response.put("email", user.getEmail());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("error", "Error fetching profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String birthDate,
            @RequestParam String joiningDate,
            HttpSession session) {

        String userEmail = (String) session.getAttribute("user");
        if (userEmail == null) {
            return ResponseEntity.status(401).body(null);
        }

        Map<String, Object> response = new HashMap<>();

        try {
            // Validation
            if (firstName == null || firstName.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "First name is required");
                return ResponseEntity.badRequest().body(response);
            }

            if (lastName == null || lastName.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Last name is required");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse dates
            LocalDate birthDateParsed = null;
            LocalDate joiningDateParsed = null;

            if (birthDate != null && !birthDate.trim().isEmpty()) {
                try {
                    birthDateParsed = LocalDate.parse(birthDate);
                } catch (DateTimeParseException e) {
                    response.put("success", false);
                    response.put("error", "Invalid birth date format. Use YYYY-MM-DD");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            if (joiningDate != null && !joiningDate.trim().isEmpty()) {
                try {
                    joiningDateParsed = LocalDate.parse(joiningDate);
                } catch (DateTimeParseException e) {
                    response.put("success", false);
                    response.put("error", "Invalid joining date format. Use YYYY-MM-DD");
                    return ResponseEntity.badRequest().body(response);
                }
            }

            // Find user and profile
            Optional<User> userOpt = userRepository.findByEmail(userEmail);
            if (userOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return ResponseEntity.status(404).body(response);
            }

            User user = userOpt.get();
            Profile profile = profileService.getProfileByUser(user);

            if (profile == null) {
                response.put("success", false);
                response.put("error", "Profile not found");
                return ResponseEntity.status(404).body(response);
            }

            // Update profile
            profile.setFirstName(firstName.trim());
            profile.setLastName(lastName.trim());
            profile.setBirthDate(birthDateParsed);
            profile.setJoiningDate(joiningDateParsed);

            profileService.saveProfile(profile);

            response.put("success", true);
            response.put("message", "Profile updated successfully!");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}