package pro.sky.telegrambot.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity(name = "notification_task")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class NotificationTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "chat_id")
    private Long chatId;
    @Column(name = "notification_message")
    private String notificationMessage;
    @Column(name = "date_time")
    private LocalDateTime dateTime;

    public NotificationTask(Long chatId, String notificationMessage, LocalDateTime dateTime) {
        this.chatId = chatId;
        this.notificationMessage = notificationMessage;
        this.dateTime = dateTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NotificationTask that = (NotificationTask) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
