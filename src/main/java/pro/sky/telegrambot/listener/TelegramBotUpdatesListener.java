package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.NotificationTaskService;

import javax.annotation.PostConstruct;
import java.util.List;

import static pro.sky.telegrambot.constants.Constants.*;

@Service
@Slf4j
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;
    private final NotificationTaskService notificationTaskService;

    public TelegramBotUpdatesListener(TelegramBot telegramBot, NotificationTaskService notificationTaskService) {
        this.telegramBot = telegramBot;
        this.notificationTaskService = notificationTaskService;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            log.info("Processing update: {}", update);
            //  получаем команду от пользователя
            String userMessageText = update.message().text();
            //  получаем уникальный идентификатор чата, из которого отправлено сообщение
            Long userChatId = update.message().chat().id();
            //  получаем имя пользователя
            String userName = update.message().chat().firstName();

            /*  вызывается метод проверки, является ли полученное сообщение тэгом, т.е. начинается на "/" и
             * если является - происходит формированием ответа в зависимости от команды после "/" */
            checkTag(userChatId, userName, userMessageText);

            /*  вызывается метод сервиса уведомлений, который проверит получаемое сообщение на соответствие паттерну
             *  уведомления и, если соответствие будет выявлено, сохранит в БД.
             *  Так же производится мониторинг акутальных записей и
             *  и рассылка уведомлений в нужное время в нужные чаты*/
            notificationTaskService.notificationMaker(userChatId, userMessageText);
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /* метод проверяется, что от пользователя пришло сообщение, которое начинается с тэга ("/")
     * и формирует ответ в зависимости от команды после тэга */
    private void checkTag(Long userChatId, String userName, String userMessageText) {
        if (userMessageText.startsWith("/")) {
            switch (userMessageText) {
                case START:
                    log.info("The \"{}\" command was received", userMessageText);
                    sendMessage(userChatId,
                            "Привет, " + userName + "! Добро пожаловать в самый лучший в мире чат-бот!");
                    break;
                case NOTIFICATION:
                    log.info("The \"{}\" command was received", userMessageText);
                    sendMessage(userChatId,
                            "Напишите уведомление в формате (чч.мм.гггг чч:мм Текст), "
                                    + " и мы отправим вам сообщение-напоминалку четко в срок");
                    break;
                case HELP:
                    log.info("The \"{}\" command was received", userMessageText);
                    sendMessage(userChatId,
                            "К сожалению, пока во мне есть только функционал создания напоминаний." +
                                    "Чтобы узнать подробности, введите команду /notify");
                    break;
                default:
                    sendMessage(userChatId,
                            "Я не знаю такой команды...");
            }
        }
    }

    //  метод для формирования ответа и отправки его пользователю
    private void sendMessage(Long chatId, String textToSend) {
        telegramBot.execute(new SendMessage(chatId, textToSend));
        log.info("The message \"{}\" was sent to chat with id={}", textToSend, chatId);
    }
}
