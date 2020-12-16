package com.search_bot.configuration

import cats.effect.Sync
import cats.syntax.all._
import com.evolutiongaming.catshelper.MonadThrowable
import pureconfig.ConfigSource
import com.search_bot.domain.Bot.BotToken
import pureconfig.generic.auto._

object SearchBotConfiguration {
  final case class DatabaseConfig(
      driver: String,
      url: String,
      user: String,
      password: String,
      migrationLocation: String
  )

  def getBotToken[F[_]: Sync: MonadThrowable] = {
    Sync[F]
      .delay(ConfigSource.default.at("bot").loadOrThrow[BotToken])
  }

  def getDatabaseConfig[F[_]: Sync: MonadThrowable] = {
    Sync[F]
      .delay(ConfigSource.default.at("db").load[DatabaseConfig])
      .flatMap[DatabaseConfig] {
        case Right(value) =>
          MonadThrowable.summon.pure(value)
        case Left(err) =>
          MonadThrowable.summon.raiseError(
            new RuntimeException(err.prettyPrint())
          )
      }
  }

}
