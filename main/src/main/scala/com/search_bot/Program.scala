package com.search_bot

import cats.effect.{Async, ConcurrentEffect, ContextShift, Resource}
import com.evolutiongaming.catshelper.CatsHelper._
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.bot.SearchBot
import com.search_bot.configuration.SearchBotConfiguration
import com.search_bot.configuration.SearchBotConfiguration.DatabaseConfig
import com.search_bot.domain.Bot.BotToken
import com.search_bot.reader.HtmlReader
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.{HtmlParser, MessageService}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

object Program {

  def dsl[F[_]: Async: ContextShift: ConcurrentEffect: MonadThrowable](
      implicit
      ec: ExecutionContext): Resource[F, Unit] = {
    for {
      (botTokenConfig, databaseConfig) <- loadConf()
      _ <- db.Configuration.migrate(databaseConfig).toResource
      articleRepo <- loadRepos(databaseConfig)
      messageService <- loadServices(articleRepo)
      server = AsyncHttpClientCatsBackend()
      bot <- SearchBot.make(botTokenConfig, server, messageService)
      _ <- bot.run().toResource
    } yield ()
  }

  def loadConf[F[_]: Async: MonadThrowable]()
    : Resource[F, (BotToken, SearchBotConfiguration.DatabaseConfig)] =
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken.toResource
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig.toResource
    } yield (botTokenConfig, databaseConfig)

  def loadRepos[F[_]: Async: ContextShift: ConcurrentEffect: MonadThrowable](
      config: DatabaseConfig): Resource[F, ArticleRepository[F]] =
    for {
      transactor <- SearchBotConfiguration.getDbConnectionResource(
        config
      )
      articleRepo = ArticleRepository.of(transactor)
    } yield articleRepo

  def loadServices[F[_]: Async: ContextShift: ConcurrentEffect: MonadThrowable](
      articleRepository: ArticleRepository[F])(
      implicit ec: ExecutionContext): Resource[F, MessageService[F]] =
    for {
      clientResource <- BlazeClientBuilder[F](ec).resource
      parser = HtmlParser.make
      articleReader = HtmlReader.of(clientResource, parser)
      messageService = MessageService.of(articleRepository, articleReader)
    } yield messageService
}
