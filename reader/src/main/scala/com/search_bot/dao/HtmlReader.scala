package com.search_bot.dao

import cats.effect.{ConcurrentEffect, Resource}
import com.search_bot.service.HtmlParser
import org.http4s.client.Client
import cats.syntax.all._

trait HtmlReader[F[_]] {
  def retrieveKeywords(url: String): F[List[String]]
}

object HtmlReader {
  def http4sClientReader[F[_] : ConcurrentEffect](resource: Resource[F, Client[F]], parser: HtmlParser[F]): HtmlReader[F] = new HtmlReader[F] {
    override def retrieveKeywords(url: String): F[List[String]] = resource.use { client =>
      for {
        html <- client.expect[String](url)
        words <- parser.getWords(html)
      } yield words
    }
  }
}
