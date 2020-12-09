package com.search_bot

import cats.effect.{Async, ConcurrentEffect, ContextShift, Resource}
import com.evolutiongaming.catshelper.CatsHelper._
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.bot.SearchBot
import com.search_bot.configuration.SearchBotConfiguration
import com.search_bot.dao.HtmlReader
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.{HtmlParser, MessageService}
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import org.http4s.client.blaze.BlazeClientBuilder

import scala.concurrent.ExecutionContext

object Program {

  def dsl[F[_]: Async: ContextShift: ConcurrentEffect: MonadThrowable](implicit
      ec: ExecutionContext
  ): Resource[F, Unit] = {
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken.toResource
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig.toResource
      clientResource <- BlazeClientBuilder[F](ec).resource
      parser = HtmlParser.htmlParser[F]
      articleReader = HtmlReader.http4sClientReader[F](clientResource, parser)
      _ <- db.Configuration.migrate(databaseConfig).toResource
      transactor <- SearchBotConfiguration.getDbConnectionResource(
        databaseConfig
      )
      articleRepo = ArticleRepository.postgresRepository(transactor)
      messageService = MessageService.messageService(articleRepo, articleReader)
      server = AsyncHttpClientCatsBackend()
      bot <- SearchBot.make(botTokenConfig.botToken, server, messageService)
      _ <- bot.run().toResource
    } yield ()
  }
}
