package CelebrationCrew.demo.controller;

import CelebrationCrew.demo.service.MessageService;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuthService authService;

    @PostMapping("/save-message")
    public ResponseEntity<Map<String, Object>> saveMessage(
            @RequestParam("receiverEmail") String receiverEmail,
            @RequestParam("messageText") String messageText,
            @RequestParam("eventType") String eventType,
            @RequestParam("eventDate") String eventDate,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            // Get sender from session - try different attribute names
            String senderEmail = (String) session.getAttribute("userEmail");
            if (senderEmail == null) {
                senderEmail = (String) session.getAttribute("email");
            }
            if (senderEmail == null) {
                senderEmail = (String) session.getAttribute("user");
            }

            // Debug: Print all session attributes
            System.out.println("=== SESSION DEBUG ===");
            System.out.println("Session ID: " + session.getId());
            System.out.println("Session attributes:");
            session.getAttributeNames().asIterator().forEachRemaining(name -> {
                System.out.println("  " + name + " = " + session.getAttribute(name));
            });
            System.out.println("Sender email found: " + senderEmail);
            System.out.println("===================");

            if (senderEmail == null) {
                response.put("success", false);
                response.put("error", "User not logged in - no email found in session");
                return ResponseEntity.status(401).body(response);
            }

            // Validate message text
            if (messageText == null || messageText.trim().isEmpty()) {
                response.put("success", false);
                response.put("error", "Message text is required");
                return ResponseEntity.badRequest().body(response);
            }

            messageText = messageText.trim();
            if (messageText.length() > 100) {
                response.put("success", false);
                response.put("error", "Message must be 100 characters or less");
                return ResponseEntity.badRequest().body(response);
            }

            // Validate event type
            if (!eventType.equals("BIRTHDAY") && !eventType.equals("ANNIVERSARY")) {
                response.put("success", false);
                response.put("error", "Invalid event type");
                return ResponseEntity.badRequest().body(response);
            }

            // Parse event date
            LocalDateTime eventDateTime;
            try {
                LocalDate date = LocalDate.parse(eventDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                eventDateTime = date.atStartOfDay();
            } catch (Exception e) {
                response.put("success", false);
                response.put("error", "Invalid date format");
                return ResponseEntity.badRequest().body(response);
            }

            // Get users using AuthService
            Optional<User> senderOpt = authService.findUserByEmail(senderEmail);

            // Debug receiver email
            System.out.println("=== RECEIVER DEBUG ===");
            System.out.println("Looking for receiver email: '" + receiverEmail + "'");
            System.out.println("Email length: " + receiverEmail.length());
            System.out.println("Email trimmed: '" + receiverEmail.trim() + "'");

            Optional<User> receiverOpt = authService.findUserByEmail(receiverEmail.trim());

            // If not found, try case-insensitive search
            if (receiverOpt.isEmpty()) {
                System.out.println("Receiver not found with exact email, trying case-insensitive...");
                receiverOpt = authService.findUserByEmail(receiverEmail.trim().toLowerCase());
            }

            System.out.println("Receiver found: " + receiverOpt.isPresent());
            if (receiverOpt.isPresent()) {
                System.out.println("Receiver email in DB: '" + receiverOpt.get().getEmail() + "'");
            }
            System.out.println("====================");

            if (senderOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "Sender not found");
                return ResponseEntity.status(401).body(response);
            }

            if (receiverOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "Receiver not found");
                return ResponseEntity.badRequest().body(response);
            }

            User sender = senderOpt.get();
            User receiver = receiverOpt.get();

            // Check if user is active
            if (sender.getStatus() != User.UserStatus.ACTIVE) {
                response.put("success", false);
                response.put("error", "Sender account is not active");
                return ResponseEntity.status(401).body(response);
            }

            if (receiver.getStatus() != User.UserStatus.ACTIVE) {
                response.put("success", false);
                response.put("error", "Receiver account is not active");
                return ResponseEntity.badRequest().body(response);
            }

            // Check if message already exists for this combination
            if (messageService.messageExists(sender, receiver, eventType, eventDateTime)) {
                // Update existing message
                messageService.updateExistingMessage(sender, receiver, messageText, eventType, eventDateTime);
                response.put("success", true);
                response.put("message", "Message updated successfully");
                return ResponseEntity.ok(response);
            } else {
                // Save new message
                messageService.saveMessage(sender, receiver, messageText, eventType, eventDateTime);
                response.put("success", true);
                response.put("message", "Message saved successfully");
                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/user-messages")
    public ResponseEntity<Map<String, Object>> getUserMessages(HttpSession session) {
        Map<String, Object> response = new HashMap<>();

        try {
            String senderEmail = (String) session.getAttribute("userEmail");
            if (senderEmail == null) {
                senderEmail = (String) session.getAttribute("email");
            }
            if (senderEmail == null) {
                senderEmail = (String) session.getAttribute("user");
            }

            if (senderEmail == null) {
                response.put("success", false);
                response.put("error", "User not logged in");
                return ResponseEntity.status(401).body(response);
            }

            Optional<User> senderOpt = authService.findUserByEmail(senderEmail);
            if (senderOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return ResponseEntity.status(401).body(response);
            }

            User sender = senderOpt.get();

            // Check if user is active
            if (sender.getStatus() != User.UserStatus.ACTIVE) {
                response.put("success", false);
                response.put("error", "User account is not active");
                return ResponseEntity.status(401).body(response);
            }

            response.put("success", true);
            response.put("messages", messageService.getMessagesBySender(sender));
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete-message/{messageId}")
    public ResponseEntity<Map<String, Object>> deleteMessage(
            @PathVariable Long messageId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            String senderEmail = (String) session.getAttribute("userEmail");
            if (senderEmail == null) {
                response.put("success", false);
                response.put("error", "User not logged in");
                return ResponseEntity.status(401).body(response);
            }

            Optional<User> senderOpt = authService.findUserByEmail(senderEmail);
            if (senderOpt.isEmpty()) {
                response.put("success", false);
                response.put("error", "User not found");
                return ResponseEntity.status(401).body(response);
            }

            User sender = senderOpt.get();

            if (sender.getStatus() != User.UserStatus.ACTIVE) {
                response.put("success", false);
                response.put("error", "User account is not active");
                return ResponseEntity.status(401).body(response);
            }

            boolean deleted = messageService.deleteMessage(messageId, sender);
            if (deleted) {
                response.put("success", true);
                response.put("message", "Message deleted successfully");
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("error", "Message not found or you don't have permission to delete it");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}