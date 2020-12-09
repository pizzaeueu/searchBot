package com.search_bot.dao

import cats.effect.ConcurrentEffect
import cats.syntax.all._
import com.search_bot.service.HtmlParser
import org.http4s.client.Client

trait HtmlReader[F[_]] {
  def retrieveKeywords(url: String): F[List[String]]
}

object HtmlReader {
  def http4sClientReader[F[_] : ConcurrentEffect](client: Client[F], parser: HtmlParser[F]): HtmlReader[F] = new HtmlReader[F] {
    override def retrieveKeywords(url: String): F[List[String]] =
      for {
        html <- client.expect[String](url)
        words <- parser.getWords(html)
      } yield words
  }
}
