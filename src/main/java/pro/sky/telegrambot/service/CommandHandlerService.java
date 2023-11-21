package pro.sky.telegrambot.service;

public interface CommandHandlerService {
    void handleCommand(Long chatId, String userName, String command);
}
