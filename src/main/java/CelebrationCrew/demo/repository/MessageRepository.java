package CelebrationCrew.demo.repository;

import CelebrationCrew.demo.entity.Message;
import CelebrationCrew.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    /**
     * Check if a message exists for the given combination
     */
    boolean existsBySenderAndReceiverAndEventTypeAndEventDate(
            User sender, User receiver, String eventType, LocalDateTime eventDate);

    /**
     * Find all messages sent by a user, ordered by sent date descending
     */
    List<Message> findBySenderOrderBySentAtDesc(User sender);

    /**
     * Find all messages received by a user, ordered by sent date descending
     */
    List<Message> findByReceiverOrderBySentAtDesc(User receiver);

    /**
     * Find messages between two users, ordered by sent date descending
     */
    List<Message> findBySenderAndReceiverOrderBySentAtDesc(User sender, User receiver);

    /**
     * Find messages by event type, ordered by sent date descending
     */
    List<Message> findByEventTypeOrderBySentAtDesc(String eventType);

    /**
     * Find messages by event date, ordered by sent date descending
     */
    List<Message> findByEventDateOrderBySentAtDesc(LocalDateTime eventDate);

    /**
     * Find messages by sender and event type
     */
    List<Message> findBySenderAndEventTypeOrderBySentAtDesc(User sender, String eventType);

    /**
     * Find messages by receiver and event type
     */
    List<Message> findByReceiverAndEventTypeOrderBySentAtDesc(User receiver, String eventType);

    /**
     * Find messages for upcoming events (within date range)
     */
    @Query("SELECT m FROM Message m WHERE m.eventDate BETWEEN :startDate AND :endDate ORDER BY m.eventDate ASC")
    List<Message> findMessagesInDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);


    @Query("Select m from Message m where m.receiver = :receiver AND m.eventType =:eventType")
    List<Message> findByReceiver(@Param("receiver") User receiver, @Param("eventType") String eventType);
    /**
     * Find messages by sender for specific event date and type
     */
    List<Message> findBySenderAndEventTypeAndEventDate(User sender, String eventType, LocalDateTime eventDate);

    /**
     * Count messages by sender
     */
    long countBySender(User sender);

    /**
     * Count messages by receiver
     */
    long countByReceiver(User receiver);

    /**
     * Find recent messages (last N days)
     */
    @Query("SELECT m FROM Message m WHERE m.sentAt >= :sinceDate ORDER BY m.sentAt DESC")
    List<Message> findRecentMessages(@Param("sinceDate") LocalDateTime sinceDate);

    /**
     * Find messages by event type and date range
     */
    @Query("SELECT m FROM Message m WHERE m.eventType = :eventType AND m.eventDate BETWEEN :startDate AND :endDate ORDER BY m.eventDate ASC")
    List<Message> findByEventTypeAndDateRange(@Param("eventType") String eventType,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    /**
     * Find messages sent in the last N hours
     */
    @Query("SELECT m FROM Message m WHERE m.sentAt >= :sinceTime ORDER BY m.sentAt DESC")
    List<Message> findMessagesInLastHours(@Param("sinceTime") LocalDateTime sinceTime);

    /**
     * Find all unique event types for a user's messages
     */
    @Query("SELECT DISTINCT m.eventType FROM Message m WHERE m.sender = :sender")
    List<String> findDistinctEventTypesBySender(@Param("sender") User sender);

    /**
     * Count messages by event type
     */
    long countByEventType(String eventType);

    /**
     * Find messages by multiple senders
     */
    List<Message> findBySenderInOrderBySentAtDesc(List<User> senders);

    /**
     * Find messages by multiple receivers
     */
    List<Message> findByReceiverInOrderBySentAtDesc(List<User> receivers);

    /**
     * Find messages for today's events using date range
     */
    @Query("SELECT m FROM Message m WHERE m.eventDate >= :startOfDay AND m.eventDate < :endOfDay ORDER BY m.sentAt DESC")
    List<Message> findTodaysEventMessages(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Find messages by sender for today's events using date range
     */
    @Query("SELECT m FROM Message m WHERE m.sender = :sender AND m.eventDate >= :startOfDay AND m.eventDate < :endOfDay ORDER BY m.sentAt DESC")
    List<Message> findTodaysEventMessagesBySender(@Param("sender") User sender, @Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}