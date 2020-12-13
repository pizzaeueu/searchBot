package com.search_bot

import cats.effect.{Async, ConcurrentEffect, ContextShift, Resource}
import com.evolutiongaming.catshelper.CatsHelper._
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.bot.SearchBot
import com.search_bot.configuration.SearchBotConfiguration
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
      botTokenConfig <- SearchBotConfiguration.getBotToken.toResource
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig.toResource
      clientResource <- BlazeClientBuilder[F](ec).resource
      parser = HtmlParser.make
      articleReader = HtmlReader.of(clientResource, parser)
      _ <- db.Configuration.migrate(databaseConfig).toResource
      transactor <- SearchBotConfiguration.getDbConnectionResource(
        databaseConfig
      )
      articleRepo = ArticleRepository.of(transactor)
      messageService = MessageService.of(articleRepo, articleReader)
      server = AsyncHttpClientCatsBackend()
      bot <- SearchBot.make(botTokenConfig, server, messageService)
      _ <- bot.run().toResource
    } yield ()
  }
}
