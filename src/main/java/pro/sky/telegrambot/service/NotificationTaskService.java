package pro.sky.telegrambot.service;

import pro.sky.telegrambot.model.NotificationTask;

import java.time.LocalDateTime;
import java.util.Collection;

public interface NotificationTaskService {
    void save(NotificationTask nt);

    void delete(NotificationTask nt);

    Collection<NotificationTask> findAllByDateTimeEquals(LocalDateTime now);
}
