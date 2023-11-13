package pro.sky.telegrambot.listener;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.service.CommandHandlerService;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotUpdatesListener implements UpdatesListener {
    private final TelegramBot telegramBot;

    private final CommandHandlerService commandHandlerService;

    @PostConstruct
    public void init() {
        telegramBot.setUpdatesListener(this);
    }

    @Override
    public int process(List<Update> updates) {
        updates.stream()
                .filter(update -> update.message() != null)
                .forEach(this::processUpdate);

        return UpdatesListener.CONFIRMED_UPDATES_ALL;
    }

    /* метод вытаскивет у пришедшего апдейта необходимые данные и отправляет их в хэндлер,
     * затем формирует строку и отправлет необходимые данные в метод отправки ответа */
    private void processUpdate(Update update) {
        log.info("Processing update: {}", update);
        //  получаем команду от пользователя
        String text = update.message().text();
        //  получаем уникальный идентификатор чата, из которого отправлено сообщение
        Long chatId = update.message().chat().id();
        //  получаем имя пользователя
        String userName = update.message().chat().firstName();

        String rs = commandHandlerService.handleCommand(chatId, userName, text);

        sendMessage(chatId, rs);
    }

    // метод отправки ответа пользователю
    private void sendMessage(Long chatId, String text) {
        telegramBot.execute(new SendMessage(chatId, text));
        log.info("The message \"{}\" was sent to chat with id={}", text, chatId);
    }
}
