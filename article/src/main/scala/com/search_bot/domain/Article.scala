package com.search_bot.domain

object Article {

  final case class ArticleUrl(value: String) extends AnyVal

  final case class ChatId(value: Long) extends AnyVal

  final case class ArticleWords(value: List[String])

  final case class Article(
      url: ArticleUrl,
      chatId: ChatId,
      words: ArticleWords
  )

}
