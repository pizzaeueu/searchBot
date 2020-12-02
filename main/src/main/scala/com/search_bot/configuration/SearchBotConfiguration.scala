package com.search_bot.configuration

import cats.MonadError
import cats.effect.{Async, Blocker, ContextShift, Resource, Sync}
import cats.syntax.all._
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import doobie.implicits._

object SearchBotConfiguration {

  case class BotToken(botToken: String)
  case class DatabaseConfig(driver: String, url: String, user: String, password: String, migrationLocation: String)

  def getBotToken[F[_]: Sync](implicit F: MonadError[F, Throwable]) = {
    Sync[F].delay(ConfigSource.default.at("bot").load[BotToken]).flatMap[BotToken] {
      case Right(value) => F.pure(value)
      case Left(err) => F.raiseError(new RuntimeException(err.prettyPrint()))
    }
  }

  def getDatabaseConfig[F[_]: Sync](implicit F: MonadError[F, Throwable]) = {
    Sync[F].delay(ConfigSource.default.at("db").load[DatabaseConfig]).flatMap[DatabaseConfig] {
      case Right(value) => F.pure(value) //value.pure[F]
      case Left(err) => F.raiseError(new RuntimeException(err.prettyPrint()))
    }
  }

  //todo move
  def getDbConnectionResource[F[_]: ContextShift: Async](config: DatabaseConfig)
                                                        (implicit F: MonadError[F, Throwable]): Resource[F, Transactor[F]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      be <- Blocker[F]
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = config.driver,
        url = config.url,
        user = config.user,
        pass = config.password,
        connectEC = ce,
        blocker = be,
      )
    } yield xa
  }

}
