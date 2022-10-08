package com.search_bot.repository

import cats.effect.Async
import cats.effect.Ref
import com.search_bot.domain.Article.Article
import doobie.{LogHandler, Transactor}
import doobie.implicits._
import doobie.postgres.implicits._

trait ArticleRepository[F[_]] {

  def getByKeywordForChat(word: String, chatId: Long): F[List[Article]]

  def saveArticle(article: Article): F[Int]

  def getByUrlForChat(url: String, chatId: Long): F[Option[Article]]

}

object ArticleRepository {
  implicit val logger = LogHandler.jdkLogHandler

  def of[F[_]: Async](
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

      override def getByUrlForChat(url: String,
                                   chatId: Long): F[Option[Article]] =
        Queries.getByUrlForChat(url, chatId).transact(transactor)

      object Queries {

        def getByKeywordForChat(searchWord: String, chatId: Long) = {
          sql"SELECT url, chatId, words FROM articles WHERE words @> ARRAY[$searchWord]::varchar[] AND chatId = $chatId"
            .query[Article]
            .to[List]
        }

        def insertArticle(article: Article) = {
          sql"insert into articles (url, chatid, words) VALUES (${article.url}, ${article.chatId}, ${article.words});".update.run
        }

        def getByUrlForChat(url: String, chatId: Long) = {
          sql"SELECT url, chatId, words FROM articles WHERE url = $url and chatId=$chatId"
            .query[Article]
            .option
        }
      }
    }

  def inMemory[F[_]: Async](state: Ref[F, Vector[Article]]) =
    new ArticleRepository[F] {
      import cats.syntax.all._

      override def getByKeywordForChat(word: String,
                                       chatId: Long): F[List[Article]] =
        state.get.map { articles =>
          articles
            .filter(article => article.words.value.contains(word))
            .filter(article => article.chatId.value == chatId)
            .toList
        }

      override def saveArticle(article: Article): F[Int] =
        state.modify(articles => (articles :+ article) -> 1)

      override def getByUrlForChat(url: String,
                                   chatId: Long): F[Option[Article]] =
        state.get.map { articles =>
          articles
            .filter(article => article.chatId.value == chatId)
            .find(article => article.url.value == url)
        }
    }

}
