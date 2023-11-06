package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.constants.Constants.NOTIFICATION_PATTERN;

/*  данный сервис работает с поступаемыми от пользователя командами, если выясняется
 *  их соответствие нижеуказанному паттерну.
 *  Запись сохраняется в БД.
 * После чего в определенные даты и время высылаются оповещения в нужные чаты */
@Service
@Slf4j
public class NotificationTaskServiceImpl implements NotificationTaskService {
    //  паттерн для распознавания даты и текста напоминания
    private static final Pattern pattern = Pattern.compile(NOTIFICATION_PATTERN);

    private final NotificationTaskRepository notificationTaskRepository;
    private final TelegramBot telegramBot;

    public NotificationTaskServiceImpl(NotificationTaskRepository notificationTaskRepository, TelegramBot telegramBot) {
        this.notificationTaskRepository = notificationTaskRepository;
        this.telegramBot = telegramBot;
    }

    public void notificationMaker(Long userChatId, String userMessageText) {
        //  выполняем поиск совпадений с паттерном
        Matcher matcher = pattern.matcher(userMessageText);

        //  если команда соответствует паттерну, вычленяем нужные нам фрагменты, формируем обьект и сохраняем его в БД
        if (matcher.matches()) {
            log.info("A command corresponding to the pattern was received. Command: \"{}\"", userMessageText);

            String dateTimeStr = matcher.group(1) + matcher.group(2) + matcher.group(3);
            String notificationMessage = matcher.group(5);
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            notificationTaskRepository.save(new NotificationTask(userChatId, notificationMessage, dateTime));
            log.info("Notification created and saved to database");

            telegramBot.execute(new SendMessage(userChatId,
                    "Уведомление \"" + userMessageText + "\" создано!"));
        }
    }

    //  с помощью шедулинга каждую минуту делаем из БД выборку записей, соответствующих настоящему времени
    @Scheduled(cron = "0 0/1 * * * *")
    public void run() {
        log.info("Scheduled method for fetching messages by current time is running");
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        Collection<NotificationTask> actual = notificationTaskRepository.findAllByDateTimeEquals(now);
        log.info("The following sample was obtained: {}", actual);
        sendNotify(actual);
    }

    //  метод проходит по списку всех, вытащенных записей из БД, и высылает уведомления в соответствующие чаты
    private void sendNotify(Collection<NotificationTask> actual) {
        for (NotificationTask nt : actual) {
            telegramBot.execute(new SendMessage(nt.getChatId(),
                    "Внимание! Сработало напоминание! \n\"" + nt.getNotificationMessage() + "\""));
            log.info("The message \"{}\" was sent to chat with id={}", nt.getNotificationMessage(), nt.getChatId());
        }
    }
}
