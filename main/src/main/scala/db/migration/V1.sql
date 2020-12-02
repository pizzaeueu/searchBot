CREATE TABLE IF NOT EXISTS articles
(
    id        serial NOT NULL,
    chatId    varchar(32),
    messageId varchar(32),
    words     varchar[],
    PRIMARY KEY (id)
);
