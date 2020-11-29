package com.search_bot.persistence

import cats.effect.concurrent.Ref
import com.search_bot.domain.Article.{Article, ArticleId}
import cats._
import cats.syntax.all._


trait ArticleRepo[F[_]] {
  def save(article: Article): F[Article]

  def getAll(): F[Vector[Article]]
}

object ArticleRepo {

  def inMemoryDsl[F[_]](state: Ref[F, Vector[Article]])(implicit F: MonadError[F, Throwable]): ArticleRepo[F] = new ArticleRepo[F] {
    private def nextId: F[Int] = getAll().map(_.size)

    override def save(article: Article): F[Article] =
      nextId
        .map(newId => article.copy(id = ArticleId(newId)))
        .flatMap { created =>
          state.modify(s => (s :+ created) -> created)
        }

    override def getAll(): F[Vector[Article]] = state.get
  }

}
