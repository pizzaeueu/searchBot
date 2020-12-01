package com.search_bot.controller

import cats.effect.{Async, ContextShift}
import com.search_bot.bot.SearchBot
import com.search_bot.service.MessageService

trait MessageListenController[F[_]] {
  def listen: F[Unit]
}

object MessageListenController {

  def bot4sController[F[_] : Async : ContextShift](
    token: String,
    messageService: MessageService[F]
  ): MessageListenController[F] = new MessageListenController[F] {
    val bot = new SearchBot[F](token, messageService)
    override def listen: F[Unit] = bot.run()
  }

}
