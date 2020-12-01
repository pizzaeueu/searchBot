package com.search_bot.controller

import cats.effect.{Async, ContextShift}
import com.search_bot.bot.SearchBot

trait MessageListenController[F[_]] {
  def listen: F[Unit]
}

object MessageListenController {

  def bot4sController[F[_] : Async : ContextShift](token: String): MessageListenController[F] = new MessageListenController[F] {
    val bot = new SearchBot[F](token)
    override def listen: F[Unit] = bot.run()
  }

}
