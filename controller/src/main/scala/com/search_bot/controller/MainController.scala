package com.search_bot.controller

import cats.Monad
import cats.effect.{Async, ContextShift}
import cats.syntax.functor._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods.ForwardMessage
import com.bot4s.telegram.models.Message
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend

import scala.concurrent.ExecutionContext

trait MainController[F[_]] {
  def getMessage(): F[Unit]
}

abstract class ExampleBot[F[_] : Async : ContextShift](val token: String)
  extends TelegramBot(token, AsyncHttpClientCatsBackend())

object MainController {

  def bot4sDsl[F[_] : Async : ContextShift](implicit ec: ExecutionContext): MainController[F] = new MainController[F] {

    class EchoBot[F[_] : Async : ContextShift](token: String) extends ExampleBot[F](token) with Polling[F] {

      override def receiveMessage(msg: Message): F[Unit] = {
        println(msg)
        msg.text.fold(unit) { text =>
          request(ForwardMessage(msg.chat.id, msg.chat.id, Some(true), msg.messageId)).void
          //request(SendMessage(msg.source, text.reverse)).void
        }
      }
    }


    override def getMessage(): F[Unit] = new EchoBot[F]("1479629990:AAHvjnrEYTkGGEF35uDN9FcUV_m5-bJIjBw").run
  }

}
