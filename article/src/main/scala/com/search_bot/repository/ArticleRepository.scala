package com.search_bot.repository

import cats.effect.Async
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.domain.Article.Article
import doobie.{Read, Transactor}
import doobie.implicits._
import doobie.postgres.implicits._

trait ArticleRepository[F[_]] {

  def getByUrl(url: String): F[Option[Article]]

  def getAll(): F[List[String]]

  def getByKeywordForChat(word: String, chatId: Long): F[List[Article]]

  def saveArticle(article: Article): F[Int]

}

object ArticleRepository {

  def of[F[_]: Async: MonadThrowable](
      transactor: Transactor[F]
  ): ArticleRepository[F] =
    new ArticleRepository[F] {
      override def getByUrl(url: String): F[Option[Article]] =
        Queries
          .getByUrl(url)
          .transact(transactor)
          .onSqlException(
            implicitly[MonadThrowable[F]]
              .raiseError(new RuntimeException("Error"))
          )

      override def getAll(): F[List[String]] =
        Queries.getAll().transact(transactor)

      override def getByKeywordForChat(
          word: String,
          chatId: Long
      ): F[List[Article]] =
        Queries.getByKeywordForChat(word, chatId).transact(transactor)

      override def saveArticle(article: Article): F[Int] =
        Queries.insertArticle(article).transact(transactor)
    }

  object Queries {
    def getByUrl[A: Read](url: String) = {
      sql"Select url from articles where url = $url".query[Article].option
    }

    def getAll[A: Read]() = {
      sql"Select url from articles".query[String].to[List]
    }

    def getByKeywordForChat(searchWord: String, chatId: Long) = {
      sql"SELECT url, chatId, messageId, words FROM articles WHERE words @> ARRAY[$searchWord]::varchar[] AND chatId = $chatId"
        .query[Article]
        .to[List]
    }

    def insertArticle(article: Article) = {
      sql"insert into articles (url, chatid, messageid, words) VALUES (${article.url}, ${article.chatId}, 97, ${article.words});".update.run
    }
  }

}
