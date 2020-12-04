package com.search_bot

import cats.MonadError
import cats.effect.{Async, ConcurrentEffect, ContextShift}
import cats.syntax.all._
import com.search_bot.configuration.SearchBotConfiguration
import com.search_bot.controller.MessageListenController
import com.search_bot.dao.HtmlReader
import com.search_bot.error.Errors.ServiceError
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.MessageService
import doobie.util.transactor.Transactor
import org.http4s.client.blaze.BlazeClientBuilder
import org.jsoup.Jsoup

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

object Program {

  def dsl[F[_] : Async : ContextShift: ConcurrentEffect](
    implicit ec: ExecutionContext,
    F: MonadError[F, Throwable]
  ): F[Unit] = {
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig
      clientResource = BlazeClientBuilder[F](global).resource
      client = HtmlReader.http4sClientReader[F](clientResource)
      res <- client.retrieveHtml("https://habr.com/ru/post/308562/")
      _ <- {
        println(Jsoup.parse(res).body().text())
        F.pure(())
      }
      migrate <- db.Configuration.migrate(databaseConfig)
      _ <- F.pure(println(s"$migrate migrations were applied"))
      connectionResource = SearchBotConfiguration.getDbConnectionResource(databaseConfig)
      articleRepo = ArticleRepository.postgresRepository(connectionResource)
      _ <- articleRepo.getByKeywordForChat("monad", 62227427).map(println)
      messageService = MessageService.messageService(articleRepo)(F)
      _ <- MessageListenController.bot4sController(botTokenConfig.botToken, messageService).listen
    } yield ()
  }

  def withTransactor[F[_] : Async : ContextShift](xa: Transactor[F])(
    implicit ec: ExecutionContext,
    F: MonadError[F, Throwable]
  ) = {
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig
      migrate <- db.Configuration.migrate(databaseConfig)
      _ <- F.pure(println(s"$migrate migrations were applied"))
      connectionResource = SearchBotConfiguration.getDbConnectionResource(databaseConfig)
      articleRepo = ArticleRepository.postgresRepository(connectionResource)
      _ <- articleRepo.getAll().map(println)
      messageService = MessageService.messageService(articleRepo)(F)
      _ <- MessageListenController.bot4sController(botTokenConfig.botToken, messageService).listen
    } yield ()
  }

}
