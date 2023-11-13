package pro.sky.telegrambot.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import pro.sky.telegrambot.exception.DateTimeException;
import pro.sky.telegrambot.exception.IncorrectParseCreateCommandException;
import pro.sky.telegrambot.exception.TextException;
import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// утилитный класс для проверки входящего сообщения на соответствие паттерну увдомления
@Slf4j
public class MessageUtil {
    private static final Pattern pattern = Pattern.compile("([0-9\\.\\s]{10})(\\s)([0-9\\:]{5})(\\s)([\\W0-9]+)");

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public static NotificationTask parseCreateCommand(Long chatId, String command) throws IncorrectParseCreateCommandException {
        if (StringUtils.hasText(command)) {
            Matcher matcher = pattern.matcher(command);
            if (matcher.matches()) {
                log.info("A command corresponding to the pattern was received. Command: \"{}\"", command);
                String dateTimeStr = matcher.group(1) + matcher.group(2) + matcher.group(3);
                String notificationText = matcher.group(5);
                LocalDateTime dateTime = LocalDateTime.parse(dateTimeStr, dtf);

                checkDateTime(dateTime);
                checkText(notificationText);

                return new NotificationTask(chatId, notificationText, dateTime);
            }
        }
        throw new IncorrectParseCreateCommandException("Incorrect command " + command);
    }

    // если дата или время отсутствует или уже прошло, выбрасывается ошибка
    private static void checkDateTime(LocalDateTime dateTime) throws DateTimeException {
        if (dateTime == null) {
            throw new DateTimeException("Your date and time must not be void");
        } else if (dateTime.isBefore(LocalDateTime.now())) {
            throw new DateTimeException("Your date and time must not earlier than " + LocalDateTime.now());
        }
    }

    // если текст сообщения отсутствует, выбрасывается ошибка
    private static void checkText(String text) throws TextException {
        if (!StringUtils.hasText(text)) {
            throw new TextException("Message text must not be void");
        }
    }
}
