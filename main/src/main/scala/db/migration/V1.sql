CREATE TABLE IF NOT EXISTS articles
(
    url       varchar not null,
    chatId    bigint,
    messageId varchar(64),
    words     varchar[],
    PRIMARY KEY (url, chatId)
);
