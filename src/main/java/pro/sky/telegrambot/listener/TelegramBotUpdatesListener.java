package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public class TelegramBotUpdatesListener implements UpdatesListener {

    private final Logger logger = LoggerFactory.getLogger(TelegramBotUpdatesListener.class);

    private final TelegramBot telegramBot;

    public TelegramBotUpdatesListener(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.forEach(update -> {
            logger.info("Processing update: {}", update);

//          получаем запрос от клиента и кладем в переменную
            String updateText = update.message().text();
//          получаем уникальный идентификатор чата и кладем в переменную
            Long chatId = update.message().chat().id();

//          формирование ответа вынесено в отдельный метод responseMaker()
            switch (updateText) {
                case "/start":
                    responseMaker(chatId, "Привет, пользователь!");
                    break;
                case "/help":
                    responseMaker(chatId, "Чем тебе помочь?");
                    break;
                default:
                    responseMaker(chatId, "Я не знаю такой команды...");
                    break;
            }
        });
        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    private void responseMaker(Long chatId, String responseMessage) {
//        формируем отправляемое сообщение
        SendMessage sendMessage = new SendMessage(chatId, responseMessage);
//        отправляем ответ с сообщением клиенту
        telegramBot.execute(sendMessage);
    }

}
