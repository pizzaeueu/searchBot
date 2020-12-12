package com.search_bot.db

import cats.effect.Sync
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import org.flywaydb.core.Flyway
import cats.syntax.all._

object Configuration {
  private class FlywayMigrator[F[_]: Sync](config: DatabaseConfig) {
    def migrate(): F[Int] =
      for {
        config <- migrationConfig(config)
        res <- Sync[F].delay(config.migrate())
      } yield res

    private def migrationConfig(config: DatabaseConfig): F[Flyway] = {
      Sync[F].delay(
        Flyway
          .configure()
          .dataSource(config.url, config.user, config.password)
          .locations(config.migrationLocation)
          .load()
      )
    }
  }

  def migrate[F[_]: Sync](config: DatabaseConfig): F[Int] =
    new FlywayMigrator[F](config).migrate()

}
