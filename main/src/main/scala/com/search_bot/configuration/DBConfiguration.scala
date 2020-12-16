package com.search_bot.configuration

import cats.effect.{Async, Blocker, ContextShift, Resource}
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor

object DBConfiguration {

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
