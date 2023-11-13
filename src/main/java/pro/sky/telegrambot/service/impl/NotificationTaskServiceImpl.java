package pro.sky.telegrambot.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pro.sky.telegrambot.model.NotificationTask;
import pro.sky.telegrambot.repository.NotificationTaskRepository;
import pro.sky.telegrambot.service.NotificationTaskService;

import java.time.LocalDateTime;
import java.util.Collection;

// сервис для работы с БД уведомлений
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationTaskServiceImpl implements NotificationTaskService {
    private final NotificationTaskRepository notificationTaskRepository;

    @Override
    public void save(NotificationTask nt) {
        notificationTaskRepository.save(nt);
    }

    @Override
    public void delete(NotificationTask nt) {
        notificationTaskRepository.delete(nt);
    }

    @Override
    public Collection<NotificationTask> findAllByDateTimeEquals(LocalDateTime now) {
        return notificationTaskRepository.findAllByDateTimeEquals(now);
    }
}
