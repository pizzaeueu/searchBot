CREATE TABLE IF NOT EXISTS articles
(
    url       varchar not null,
    chatId    bigint,
    words     varchar[],
    PRIMARY KEY (url, chatId)
);
