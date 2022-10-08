package com.search_bot.bot

import cats.effect.{Resource, Sync}
import cats.syntax.all._
import com.bot4s.telegram.cats.{Polling, TelegramBot}
import com.bot4s.telegram.models.Message
import com.search_bot.domain.{BotToken, Messages}
import com.search_bot.service.MessageService
import sttp.client3.SttpBackend

object SearchBot {
  def make[F[_]: Sync](
      token: BotToken,
      server: SttpBackend[F, Any],
      service: MessageService[F]
  ): Resource[F, TelegramBot[F]] = {

    def create = new TelegramBot[F](token.botToken, server) with Polling[F] {
      override def receiveMessage(msg: Message): F[Unit] =
        for {
          response <- service.handle(Messages.of(msg))
          _ <- {
            msg.text.traverse_ { _ =>
              request(response.message).void
            }
          }
        } yield ()
    }

    Resource.make(Sync[F].delay(create))(client =>
      Sync[F].delay(client.shutdown()))
  }
}
