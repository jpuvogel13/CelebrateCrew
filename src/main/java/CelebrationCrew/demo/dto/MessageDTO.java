package CelebrationCrew.demo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageDTO {
    private Long id;
    private String senderEmail;
    private String senderName;
    private String receiverEmail;
    private String receiverName;
    private String messageText;
    private String eventType;
    private LocalDateTime eventDate;
    private LocalDateTime sentAt;

    // Constructor for converting from Message entity
    public MessageDTO(Long id, String senderEmail, String senderName, String receiverEmail,
                      String receiverName, String messageText, String eventType,
                      LocalDateTime eventDate, LocalDateTime sentAt) {
        this.id = id;
        this.senderEmail = senderEmail;
        this.senderName = senderName;
        this.receiverEmail = receiverEmail;
        this.receiverName = receiverName;
        this.messageText = messageText;
        this.eventType = eventType;
        this.eventDate = eventDate;
        this.sentAt = sentAt;
    }
}