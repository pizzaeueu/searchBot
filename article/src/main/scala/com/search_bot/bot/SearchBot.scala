package com.search_bot.bot

import cats.MonadError
import cats.effect.{Async, ContextShift}
import cats.syntax.all._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.models.Message
import com.search_bot.domain.Messages
import com.search_bot.service.MessageService
import com.softwaremill.sttp.asynchttpclient.cats.AsyncHttpClientCatsBackend

abstract class AbstractBot[F[_] : Async : ContextShift](val token: String)
  extends TelegramBot(token, AsyncHttpClientCatsBackend())

class SearchBot[F[_] : Async : ContextShift](
  token: String,
  service: MessageService[F]
)(implicit F: MonadError[F, Throwable])extends AbstractBot[F](token) with Polling[F] {
  override def receiveMessage(msg: Message): F[Unit] = for {
      message <- F.pure(Messages.of(msg))
      response <- service.handle(message)
      _ <- {
        msg.text.fold(unit) { _ =>
          request(response.message).void
        }
      }
    } yield ()
}
