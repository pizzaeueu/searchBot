package com.search_bot.configuration

import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import cats.syntax.all._
import com.evolutiongaming.catshelper.MonadThrowable
import doobie.hikari.HikariTransactor
import doobie.{ExecutionContexts, Transactor}
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
          implicitly[MonadThrowable[F]].pure(value)
        case Left(err) =>
          implicitly[MonadThrowable[F]].raiseError(
            new RuntimeException(err.prettyPrint())
          )
      }
  }

  //todo move
  def getDbConnectionResource[F[_]: ContextShift: Async: MonadThrowable](
      config: DatabaseConfig
  ): Resource[F, Transactor[F]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      be <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = config.driver,
        url = config.url,
        user = config.user,
        pass = config.password,
        connectEC = ce,
        blocker = be
      )
    } yield xa
  }

}
