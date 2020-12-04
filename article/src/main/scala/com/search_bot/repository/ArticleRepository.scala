package com.search_bot.repository

import cats.MonadError
import cats.effect.{Async, Resource}
import com.search_bot.domain.Article.Article
import com.search_bot.error.Errors.ServiceError
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{LogHandler, Read, Transactor}


trait ArticleRepository[F[_]] {

  def getByUrl(url: String): F[Option[Article]]

  def getAll(): F[List[String]]

  def getByKeywordForChat(word: String, chatId: Long): F[List[Article]]


}

object ArticleRepository {
  implicit val han = LogHandler.jdkLogHandler
  def postgresRepository[F[_] : Async](resource: Resource[F, Transactor[F]])
                                      (implicit F: MonadError[F, Throwable]): ArticleRepository[F] =
    new ArticleRepository[F] {
      override def getByUrl(url: String): F[Option[Article]] = {
        resource.use { xa =>
          Queries.getByUrl(url).transact(xa).onSqlException(F.raiseError(new RuntimeException("Error")))
        }
      }

      override def getAll(): F[List[String]] =
        resource.use { xa =>
          Queries.getAll().transact(xa)
        }

      override def getByKeywordForChat(word: String, chatId: Long): F[List[Article]] = {
        resource.use { xa =>
          Queries.getByKeywordForChat(word, chatId).transact(xa)
        }
      }
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
        .query[Article].to[List]
    }
  }

}
