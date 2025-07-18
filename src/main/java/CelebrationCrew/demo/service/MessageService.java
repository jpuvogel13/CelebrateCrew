package CelebrationCrew.demo.service;

import CelebrationCrew.demo.dto.MessageDTO;
import CelebrationCrew.demo.entity.Message;
import CelebrationCrew.demo.entity.User;
import CelebrationCrew.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    /**
     * Save a new message
     */
    public Message saveMessage(User sender, User receiver, String messageText, String eventType, LocalDateTime eventDate) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setMessageText(messageText);
        message.setEventType(eventType);
        message.setEventDate(eventDate);

        return messageRepository.save(message);
    }

    /**
     * Update existing message text for specific event
     */
    public Message updateExistingMessage(User sender, User receiver, String messageText, String eventType, LocalDateTime eventDate) {
        // Use existing method to find messages by sender, event type and date
        List<Message> candidateMessages = messageRepository.findBySenderAndEventTypeAndEventDate(sender, eventType, eventDate);

        // Filter by receiver
        Message existingMessage = candidateMessages.stream()
                .filter(msg -> msg.getReceiver().equals(receiver))
                .findFirst()
                .orElse(null);

        if (existingMessage != null) {
            existingMessage.setMessageText(messageText.trim());
            existingMessage.setSentAt(LocalDateTime.now()); // Update timestamp
            return messageRepository.save(existingMessage);
        }
        return null;
    }

    /**
     * Check if a message already exists for the given combination
     */
    public boolean messageExists(User sender, User receiver, String eventType, LocalDateTime eventDate) {
        return messageRepository.existsBySenderAndReceiverAndEventTypeAndEventDate(
                sender, receiver, eventType, eventDate);
    }

    /**
     * Get all messages sent by a user as DTOs
     */
    public List<MessageDTO> getMessagesBySender(User sender) {
        return messageRepository.findBySenderOrderBySentAtDesc(sender)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get all messages received by a user as DTOs
     */
    public List<MessageDTO> getMessagesByReceiver(User receiver) {
        return messageRepository.findByReceiverOrderBySentAtDesc(receiver)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get messages for a specific event type
     */
    public List<Message> getMessagesByEventType(String eventType) {
        return messageRepository.findByEventTypeOrderBySentAtDesc(eventType);
    }

    /**
     * Get messages for today's events
     */
    public List<MessageDTO> getTodaysEventMessages() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return messageRepository.findTodaysEventMessages(startOfDay, endOfDay)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get messages by sender for today's events
     */
    public List<MessageDTO> getTodaysEventMessagesBySender(User sender) {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);
        return messageRepository.findTodaysEventMessagesBySender(sender, startOfDay, endOfDay)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get messages between two users
     */
    public List<Message> getMessagesBetweenUsers(User sender, User receiver) {
        return messageRepository.findBySenderAndReceiverOrderBySentAtDesc(sender, receiver);
    }

    /**
     * Delete a message by ID (only if the current user is the sender)
     */
    public boolean deleteMessage(Long messageId, User currentUser) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && message.getSender().equals(currentUser)) {
            messageRepository.delete(message);
            return true;
        }
        return false;
    }

    /**
     * Update a message (only if the current user is the sender)
     */
    public Message updateMessage(Long messageId, String newMessageText, User currentUser) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && message.getSender().equals(currentUser)) {
            if (newMessageText != null && newMessageText.trim().length() <= 100) {
                message.setMessageText(newMessageText.trim());
                return messageRepository.save(message);
            }
        }
        return null;
    }

    /**
     * Convert Message entity to DTO
     */
    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getEmail(),
                message.getSender().getEmail(), // Using email as name since User doesn't have getName()
                message.getReceiver().getEmail(),
                message.getReceiver().getEmail(), // Using email as name since User doesn't have getName()
                message.getMessageText(),
                message.getEventType(),
                message.getEventDate(),
                message.getSentAt()
        );
    }
}