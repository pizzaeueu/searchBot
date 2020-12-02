package com.search_bot.db

import cats.effect.Sync
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import org.flywaydb.core.Flyway

object Configuration {
  private class FlywayMigrator[F[_]: Sync](config: DatabaseConfig) {
    val flyway = Flyway
      .configure()
      .dataSource(config.url, config.user, config.password)
      .locations(config.migrationLocation)
      .load()
    def migrate(): F[Int] = Sync[F].delay(flyway.migrate())
  }

  def migrate[F[_]: Sync](config: DatabaseConfig):F[Int] = new FlywayMigrator[F](config).migrate()

}
