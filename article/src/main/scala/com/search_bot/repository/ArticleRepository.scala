package com.search_bot.repository

import cats.effect.Async
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.domain.Article.Article
import doobie.{LogHandler, Transactor}
import doobie.implicits._
import doobie.postgres.implicits._

trait ArticleRepository[F[_]] {

  def getByKeywordForChat(word: String, chatId: Long): F[List[Article]]

  def saveArticle(article: Article): F[Int]

}

object ArticleRepository {
  implicit val logger = LogHandler.jdkLogHandler

  def of[F[_]: Async: MonadThrowable](
      transactor: Transactor[F]
  ): ArticleRepository[F] =
    new ArticleRepository[F] {

      override def getByKeywordForChat(
          word: String,
          chatId: Long
      ): F[List[Article]] =
        Queries.getByKeywordForChat(word, chatId).transact(transactor)

      override def saveArticle(article: Article): F[Int] =
        Queries.insertArticle(article).transact(transactor)
    }

  object Queries {

    def getByKeywordForChat(searchWord: String, chatId: Long) = {
      sql"SELECT url, chatId, words FROM articles WHERE words @> ARRAY[$searchWord]::varchar[] AND chatId = $chatId"
        .query[Article]
        .to[List]
    }

    def insertArticle(article: Article) = {
      sql"insert into articles (url, chatid, words) VALUES (${article.url}, ${article.chatId}, ${article.words});".update.run
    }
  }

}
