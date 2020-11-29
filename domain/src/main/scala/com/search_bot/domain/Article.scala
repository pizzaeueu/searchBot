package com.search_bot.domain

object Article {

  case class ArticleId(value: Int) extends AnyVal
  case class ChatId(value: String) extends AnyVal
  case class MessageId(value: String) extends AnyVal
  case class ArticleWords(value: Set[String]) extends AnyVal

  case class Article(id:ArticleId, chatId: ChatId, messageId: MessageId, words: ArticleWords)

}
