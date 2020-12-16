package com.search_bot.service

import com.evolutiongaming.catshelper.MonadThrowable
import org.jsoup.Jsoup

import scala.util.Try

trait HtmlParser[F[_]] {
  def getWords(html: String): F[List[String]]
}

object HtmlParser {
  def make[F[_]: MonadThrowable]: HtmlParser[F] = new HtmlParser[F] {
    override def getWords(html: String): F[List[String]] =
      MonadThrowable.summon.fromTry(
        Try(Jsoup.parse(html).body().text().split("\\W+").distinct.toList)
      )
  }
}
