package com.search_bot.dao

import cats.effect.{ConcurrentEffect, Resource}
import org.http4s.client.Client

trait HtmlReader[F[_]] {
  def retrieveHtml(url: String): F[String]
}

object HtmlReader {
  def http4sClientReader[F[_] : ConcurrentEffect](resource: Resource[F, Client[F]]): HtmlReader[F] = new HtmlReader[F] {
    override def retrieveHtml(url: String): F[String] = resource.use { client =>
      client.expect[String](url)
    }
  }
}
