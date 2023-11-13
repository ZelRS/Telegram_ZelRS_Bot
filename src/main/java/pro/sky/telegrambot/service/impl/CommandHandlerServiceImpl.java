package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.configuration.TelegramBotConfiguration;
import pro.sky.telegrambot.exception.DateTimeException;
import pro.sky.telegrambot.exception.IncorrectParseCreateCommandException;
import pro.sky.telegrambot.exception.TextException;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.service.CommandHandlerService;
import pro.sky.telegrambot.service.NotificationTaskService;
import pro.sky.telegrambot.util.MessageUtil;

import static pro.sky.telegrambot.constants.CommandConstants.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerServiceImpl implements CommandHandlerService {
    private final NotificationTaskService notificationTaskService;

    private final TelegramBotConfiguration tbc;

    /* метод проверяет пришедшую от пользователя команду и, опираясь на нее, возвращает необходимую строку
     * метод processUpdate() класса TelegramBotUpdatesListener */
    public String handleCommand(Long chatId, String userName, String command) {
        switch (command) {
            case START:
                log.info("The \"{}\" command was received", command);
                return tbc.getStartMsg();
            case HELP:
                log.info("The \"{}\" command was received", command);
                return tbc.getHelpMsg();
            case NOTIFICATION:
                log.info("The \"{}\" command was received", command);
                return tbc.getNotifyMsg();
            default:
                log.info("The \"{}\" command was received", command);
                // дефолтный вариант вызывает метод для проверки, формирования и сохранения уведомления в БД
                return handleCreateTaskCommand(chatId, command);
        }
    }

    /* метод для проверки, формирования и сохранения уведомления в БД
     * (если сообщение не будет соответствовать паттерну уведомления, бот отправит пользователю сообщение о том,
     * что он не знает такой команды)*/
    private String handleCreateTaskCommand(Long chatId, String command) {
        try {
            NotificationTask nt = MessageUtil.parseCreateCommand(chatId, command);
            notificationTaskService.save(nt);
            log.info("Notification created and saved to database");
            return tbc.getSuccessMsg();
        } catch (DateTimeException e) {
            log.info("The user entered an incorrect date and time");
            return tbc.getExceptionDateTimeMsg();
        } catch (TextException e) {
            log.info("The user did not enter text");
            return tbc.getExceptionTextMsg();
        } catch (IncorrectParseCreateCommandException e) {
            log.info("The user entered an unknown command");
            return tbc.getExceptionUnknownMsg();
        }
    }
}
