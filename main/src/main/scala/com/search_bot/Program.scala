package com.search_bot

import cats.MonadError
import cats.effect.{Async, ContextShift}
import cats.syntax.all._
import com.search_bot.configuration.SearchBotConfiguration
import com.search_bot.controller.MessageListenController
import com.search_bot.repository.ArticleRepository
import com.search_bot.service.MessageService

import scala.concurrent.ExecutionContext

object Program {

  def dsl[F[_] : Async : ContextShift](
    implicit ec: ExecutionContext,
    F: MonadError[F, Throwable]
  ): F[Unit] = {
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken
      databaseConfig <- SearchBotConfiguration.getDatabaseConfig
      connectionResource <- SearchBotConfiguration.getDbConnectionResource(databaseConfig)
      articleRepo <- ArticleRepository.postgresRepository(connectionResource)
      messageService <- MessageService.messageService(articleRepo)(F)
      _ <- MessageListenController.bot4sController(botTokenConfig.botToken, messageService).listen
    } yield ()
  }

}
