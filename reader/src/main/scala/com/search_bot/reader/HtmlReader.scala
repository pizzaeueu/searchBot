package com.search_bot.reader

import cats.syntax.all._
import com.search_bot.service.HtmlParser
import org.http4s.client.Client
import cats.Monad

trait HtmlReader[F[_]] {
  def retrieveKeywords(url: String): F[List[String]]
}

object HtmlReader {
  def of[F[_]: Monad](
      client: Client[F],
      parser: HtmlParser[F]
  ): HtmlReader[F] = new HtmlReader[F] {
    override def retrieveKeywords(url: String): F[List[String]] =
      for {
        html <- client.expect[String](url)(null)
        words <- parser.getWords(html)
      } yield words
  }
}
