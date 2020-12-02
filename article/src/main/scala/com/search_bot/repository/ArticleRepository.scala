package com.search_bot.repository

import cats.MonadError
import cats.effect.{Async, Resource}
import com.search_bot.domain.Article.Article
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.{LogHandler, Read, Transactor}


trait ArticleRepository[F[_]] {

  def getByUrl(url: String): F[Option[Article]]

  def getAll(): F[List[Int]]
}

object ArticleRepository {
  implicit val han = LogHandler.jdkLogHandler
  def postgresRepository[F[_] : Async](resource: Resource[F, Transactor[F]])
                                      (implicit F: MonadError[F, Throwable]): F[ArticleRepository[F]] = F.pure(
    new ArticleRepository[F] {
      override def getByUrl(url: String): F[Option[Article]] = {
        resource.use { xa =>
          Queries.getByUrl(url).transact(xa).onSqlException(F.raiseError(new RuntimeException("Error")))
        }
      }

      override def getAll(): F[List[Int]] =
        resource.use { xa =>
          Queries.getAll().transact(xa)
        }
    }
  )

  object Queries {
    def getByUrl[A: Read](url: String) = {
      sql"Select id from articles where url = $url".query[Article].option
    }

    def getAll[A: Read]() = {
      sql"Select id from articles".query[Int].to[List]
    }
  }

}
