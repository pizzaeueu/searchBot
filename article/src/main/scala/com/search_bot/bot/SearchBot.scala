package com.search_bot.bot

import cats.effect.{ContextShift, Resource, Sync}
import cats.syntax.all._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.models.Message
import com.search_bot.domain.Messages
import com.search_bot.service.MessageService
import com.softwaremill.sttp.SttpBackend

object SearchBot {
  def make[F[_]: Sync: ContextShift](
      token: String,
      server: SttpBackend[F, Nothing],
      service: MessageService[F]
  ): Resource[F, TelegramBot[F]] = {

    def create = new TelegramBot[F](token, server) with Polling[F] {
      override def receiveMessage(msg: Message): F[Unit] = for {
        response <- service.handle(Messages.of(msg))
        _ <- {
          msg.text.traverse_ { _ =>
            request(response.message).void
          }
        }
      } yield ()
    }

    Resource.make(Sync[F].delay(create))(client =>
      Sync[F].delay(client.shutdown())
    )
  }
}
