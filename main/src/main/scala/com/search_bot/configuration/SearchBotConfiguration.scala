package com.search_bot.configuration

import cats.MonadError
import cats.syntax.all._
import pureconfig.ConfigSource
import pureconfig.generic.auto._

object SearchBotConfiguration {

  case class BotToken(botToken: String)

  def getBotToken[F[_]](implicit F: MonadError[F, Throwable]) = {
    F.pure(ConfigSource.default.load[BotToken]).flatMap[BotToken] {
      case Right(value) => F.pure(value)
      case Left(err) => F.raiseError(new RuntimeException(err.prettyPrint()))
    }
  }

}
