package com.search_bot

import cats.{FlatMap, Monad}
import cats.effect.{Async, ContextShift}
import com.search_bot.configuration.SearchBotConfiguration
import com.search_bot.controller.MessageListenController
import cats.syntax.all._

import scala.concurrent.ExecutionContext

object Program {

  def dsl[F[_]: Async: ContextShift](implicit ec: ExecutionContext): F[Unit] = {
    for {
      botTokenConfig <- SearchBotConfiguration.getBotToken
      _ <- MessageListenController.bot4sController(botTokenConfig.botToken).listen
    } yield ()
    //MessageListenController.bot4sController("1479629990:AAHvjnrEYTkGGEF35uDN9FcUV_m5-bJIjBw").listen
  }

}
