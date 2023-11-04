package pro.sky.telegrambot.service;

public interface NotificationTaskService {
    void notificationMaker(Long userChatId, String userMessageText);
}
