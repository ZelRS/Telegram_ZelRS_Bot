-- liquibase formatted sql

-- changeset Zelenin_Roman:1
CREATE TABLE notification_task
(
    id SERIAL PRIMARY KEY,
    chat_id BIGINT CHECK (chat_id > 0),
    notification_message TEXT NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE
);



