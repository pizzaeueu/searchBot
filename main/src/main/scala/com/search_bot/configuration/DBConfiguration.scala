package com.search_bot.configuration

import cats.effect.{Async, Resource}
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import doobie.{ExecutionContexts, Transactor}
import doobie.hikari.HikariTransactor

object DBConfiguration {

  def getDbConnectionResource[F[_]: Async](
      config: DatabaseConfig
  ): Resource[F, Transactor[F]] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](10)
      xa <- HikariTransactor.newHikariTransactor[F](
        driverClassName = config.driver,
        url = config.url,
        user = config.user,
        pass = config.password,
        connectEC = ce,
      )
    } yield xa
  }

}
