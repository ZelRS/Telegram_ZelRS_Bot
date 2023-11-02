-- liquibase formatted sql

-- changeset Zelenin_Roman:1
CREATE TABLE notification_task
(
    id BIGINT PRIMARY KEY,
    chatId BIGINT CHECK (chatId > 0),
    responseMessage TEXT NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE
);

-- changeset Zelenin_Roman:2
ALTER TABLE notification_task RENAME COLUMN responsemessage TO notification_message;
ALTER TABLE notification_task RENAME COLUMN chatId  TO chat_id;



