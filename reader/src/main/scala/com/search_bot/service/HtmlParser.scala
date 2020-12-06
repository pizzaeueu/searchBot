package com.search_bot.service

import cats.MonadError
import org.jsoup.Jsoup

import scala.util.Try

trait HtmlParser[F[_]] {
  def getWords(html: String):F[List[String]]
}

object HtmlParser {
  def htmlParser[F[_]](implicit F: MonadError[F, Throwable]): HtmlParser[F] = new HtmlParser[F] {
    override def getWords(html: String): F[List[String]] = F.fromTry(
      Try(Jsoup.parse(html).body().text().split("\\W+").distinct.toList)
    )
  }
}
