package pro.sky.telegrambot.service.impl;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.KeyboardButton;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.request.SendMessage;
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

import static pro.sky.telegrambot.constants.CommandConstants.CREATE_NOTIFICATION;
import static pro.sky.telegrambot.constants.CommandConstants.START;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandHandlerServiceImpl implements CommandHandlerService {
    private final NotificationTaskService notificationTaskService;

    private final TelegramBotConfiguration tbc;

    private final TelegramBot telegramBot;

    /* метод проверяет пришедшую от пользователя команду и, опираясь на нее, возвращает необходимую строку
     * метод processUpdate() класса TelegramBotUpdatesListener */
    public void handleCommand(Long chatId, String userName, String command) {
        log.info("The \"{}\" command was received", command);
        switch (command) {
            case START:
                SendMessage sendStartMsg = new SendMessage(chatId, tbc.getStartMsg())/*.parseMode(HTML)*/;

                setButton(sendStartMsg, CREATE_NOTIFICATION);

                telegramBot.execute(sendStartMsg);
                log.info("The message \"{}\" was sent to chat with id={}", tbc.getStartMsg(), chatId);
                break;
            case CREATE_NOTIFICATION:
                SendMessage sendNotificationMsg = new SendMessage(chatId, tbc.getNotifyMsg())/*.parseMode(HTML)*/;

                telegramBot.execute(sendNotificationMsg);
                log.info("The message \"{}\" was sent to chat with id={}", tbc.getNotifyMsg(), chatId);
                break;
            default:
                String defMsg = handleCreateTaskCommand(chatId, command);
                telegramBot.execute(new SendMessage(chatId, defMsg));
                log.info("The message \"{}\" was sent to chat with id={}", defMsg, chatId);
                break;
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

    private void setButton(SendMessage sendMessage, String buttonText) {
        sendMessage.replyMarkup(new ReplyKeyboardMarkup(new KeyboardButton(buttonText)));
    }
}
