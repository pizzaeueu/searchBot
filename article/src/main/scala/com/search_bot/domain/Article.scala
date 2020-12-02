package com.search_bot.domain

object Article {

  //todo: Add smart constructor to case classes

  final case class ArticleId(value: Int) extends AnyVal

  final case class ChatId(value: String) extends AnyVal

  final case class MessageId(value: String) extends AnyVal

  final case class ArticleWords(value: List[String])

  final case class Article(id: ArticleId, chatId: ChatId, messageId: MessageId, words: ArticleWords)

}
