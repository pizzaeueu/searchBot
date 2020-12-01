package com.search_bot.bot

import cats.effect.{Async, ContextShift}
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.methods.ForwardMessage
import com.bot4s.telegram.models.Message
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend
import cats.syntax.functor._

abstract class AbstractBot[F[_] : Async : ContextShift](val token: String)
  extends TelegramBot(token, AsyncHttpClientCatsBackend())

class SearchBot[F[_] : Async : ContextShift](token: String) extends AbstractBot[F](token) with Polling[F] {
  override def receiveMessage(msg: Message): F[Unit] = {
    println(msg)
    msg.text.fold(unit) { text =>
      request(ForwardMessage(msg.chat.id, msg.chat.id, Some(true), msg.messageId)).void
      //request(SendMessage(msg.source, text.reverse)).void
    }
  }
}
