package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static pro.sky.telegrambot.constants.Constants.START;

@Service
@Slf4j
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;

    private final NotificationTaskRepository notificationTaskRepository;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskRepository notificationTaskRepository) {
        this.telegramBot = telegramBot;
        this.notificationTaskRepository = notificationTaskRepository;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            log.info("Processing update: {}", update);
//          получаем команду от пользователя
            String userMessageText = update.message().text();
//          получаем уникальный идентификатор чата, из которого отправлено сообщение
            Long userChatId = update.message().chat().id();
//          получаем имя пользователя
            String userName = update.message().chat().firstName();
//          отпраляем ответ пользователю на его команду "/start"
            if (userMessageText.equals(START)) {
                log.info("The \"{}\" command was received", userMessageText);
                sendMessage(userChatId,
                        "Привет, " + userName + "! Добро пожаловать в самый лучший в мире чат-бот!");
            }
//          создаем паттерн для распознавания даты и текста напоминания
            Pattern pattern = Pattern.compile("([0-9\\.\\:\\s]{16})(\\s)([\\W+]+)");
//          выполняем поиск совпадений с паттерном
            Matcher matcher = pattern.matcher(userMessageText);

//          если команда соответствует паттерну, вычленяем нужные нам фрагменты, формируем обьект и сохраняем его в БД
            if (matcher.matches()) {
                log.info("A command corresponding to the pattern was received. Command: \"{}\"", userMessageText);
                String dateTimeStr = matcher.group(1);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
                String notificationMessage = matcher.group(3);
                notificationTaskRepository.save(new NotificationTask(userChatId, notificationMessage, dateTime));
                log.info("Notification created and saved to database");
                sendMessage(userChatId, "Уведомление \"" + userMessageText + "\" создано!");
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    //  метод для формирования ответа и отправки его пользователю
    private void sendMessage(Long chatId, String responseMessage) {
        SendMessage sendMessage = new SendMessage(chatId, responseMessage);
        telegramBot.execute(sendMessage);
        log.info("The message \"{}\" was sent to the user", responseMessage);
    }

}
