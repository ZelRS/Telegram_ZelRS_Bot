package pro.sky.telegrambot.scheduler;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;

//класс для шедулинга по БД на предмет сообщений, соответствующих актуальному времени.
@Component
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskScheduler {
    private final TelegramBot telegramBot;

    private final NotificationTaskService notificationTaskService;


    // каждую минуту делаем из БД выборку записей, соответствующих настоящему времени
    @Scheduled(cron = "0 0/1 * * * *")
    public void sendTaskNotification() {
        log.info("Scheduled method for fetching messages by current time is running");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> actual = notificationTaskService.findAllByDateTimeEquals(now);
        log.info("The following sample was obtained: {}", actual);
        // вызывается метод для отправки уведомлений в чаты
        sendNotify(actual);
    }

    /* метод проходит по списку всех, вытащенных записей из БД, и высылает уведомления в соответствующие чаты.
     * отправленные сообщения удаляются из БД */
    private void sendNotify(Collection<NotificationTask> actual) {
        for (NotificationTask nt : actual) {
            SendResponse response = telegramBot.execute(new SendMessage(nt.getChatId(),
                    "Внимание! Сработало напоминание! \n\"" + nt.getNotificationMessage() + "\""));
            log.info("The message \"{}\" was sent to chat with id={}", nt.getNotificationMessage(), nt.getChatId());
            if (response.isOk()) {
                notificationTaskService.delete(nt);
                log.info("The message \"{}\" was deleted from DB", nt.getNotificationMessage());
            }
        }
    }
}
