package com.search_bot.service

import com.evolutiongaming.catshelper.MonadThrowable
import org.jsoup.Jsoup

import scala.util.Try

trait HtmlParser[F[_]] {
  def getWords(html: String): F[List[String]]
}

object HtmlParser {
  def htmlParser[F[_] : MonadThrowable]: HtmlParser[F] = new HtmlParser[F] {
    override def getWords(html: String): F[List[String]] = implicitly[MonadThrowable[F]].fromTry(
      Try(Jsoup.parse(html).body().text().split("\\W+").distinct.toList)
    )
  }
}
