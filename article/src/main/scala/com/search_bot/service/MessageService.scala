package com.search_bot.service

import cats.MonadError
import com.bot4s.telegram.methods.ForwardMessage
import com.search_bot.domain.Messages.{GetArticle, ScanArticle, TelegramMessage}
import com.search_bot.domain.Responses.{FailHandleMessage, SuccessfullyRetrieve, SuccessfullySave, TelegramResponse}
import com.search_bot.repository.ArticleRepository

trait MessageService[F[_]] {
  def handle(message: Option[TelegramMessage]): F[TelegramResponse]
}

object MessageService {
  def messageService[F[_]](articleRepo: ArticleRepository[F])(implicit F: MonadError[F, Throwable]): F[MessageService[F]] = F.pure {
    case Some(m@ScanArticle(_)) => scanArticle(m)
    case Some(m@GetArticle(_)) => getArticle(m)
    //case _ => F.pure(FailHandleMessage())
  }

  private def scanArticle[F[_]](message: ScanArticle)(implicit F: MonadError[F, Throwable]): F[TelegramResponse] =
    F.pure(SuccessfullySave(ForwardMessage(message.msg.chat.id, message.msg.chat.id, Some(true), message.msg.messageId)))
  private def getArticle[F[_]](message: GetArticle)(implicit F: MonadError[F, Throwable]): F[TelegramResponse] =
    F.pure(SuccessfullySave(ForwardMessage(message.msg.chat.id, message.msg.chat.id, Some(true), message.msg.messageId)))
}
