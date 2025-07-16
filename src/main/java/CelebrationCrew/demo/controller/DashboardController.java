package CelebrationCrew.demo.controller;

import CelebrationCrew.demo.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DashboardController {

    @Autowired
    private ProfileService profileService;

    @GetMapping("/upcoming-events")
    public ResponseEntity<Map<String, Object>> getUpcomingEvents(HttpSession session) {
        try {
            String userEmail = (String) session.getAttribute("user");
            if (userEmail == null) {
                return ResponseEntity.status(401).body(null);
            }

            Map<String, Object> response = new HashMap<>();

            // Try to get data, if it fails, return empty lists
            List<Map<String, String>> birthdays = new ArrayList<>();
            List<Map<String, String>> anniversaries = new ArrayList<>();

            try {
                birthdays = profileService.getUpcomingBirthdays();
            } catch (Exception e) {
                System.out.println("Error getting birthdays: " + e.getMessage());
                e.printStackTrace();
            }

            try {
                anniversaries = profileService.getUpcomingAnniversaries();
            } catch (Exception e) {
                System.out.println("Error getting anniversaries: " + e.getMessage());
                e.printStackTrace();
            }

            response.put("birthdays", birthdays);
            response.put("anniversaries", anniversaries);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("Controller error: " + e.getMessage());
            e.printStackTrace();

            // Return empty data instead of error
            Map<String, Object> response = new HashMap<>();
            response.put("birthdays", new ArrayList<>());
            response.put("anniversaries", new ArrayList<>());
            return ResponseEntity.ok(response);
        }
    }
}