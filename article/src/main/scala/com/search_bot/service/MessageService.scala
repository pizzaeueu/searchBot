package com.search_bot.service

import cats.effect.Sync
import cats.syntax.all._
import com.bot4s.telegram.methods.SendMessage
import com.evolutiongaming.catshelper.MonadThrowable
import com.search_bot.dao.HtmlReader
import com.search_bot.domain.Article._
import com.search_bot.domain.Messages.{
  CommandNotSupported,
  GetArticle,
  ScanArticle,
  TelegramMessage
}
import com.search_bot.domain.Responses.{
  FailHandleMessage,
  SuccessfullySave,
  TelegramResponse
}
import com.search_bot.repository.ArticleRepository

trait MessageService[F[_]] {
  def handle(message: TelegramMessage): F[TelegramResponse]
}

object MessageService {
  def messageService[F[_]: Sync: MonadThrowable](
      articleRepo: ArticleRepository[F],
      articleReader: HtmlReader[F]
  ): MessageService[F] = {
    case ScanArticle(url, chatId) =>
      scanArticle(url, chatId, articleReader, articleRepo)
    case GetArticle(keyword, chatId) =>
      getArticle(keyword, chatId, articleRepo).recoverWith { case err =>
        generateError(err, chatId.toLong)
      }
    case CommandNotSupported(chatId, command) =>
      implicitly[MonadThrowable[F]].pure(
        FailHandleMessage(
          SendMessage(chatId, s"command $command not supported")
        )
      )
  }

  private def scanArticle[F[_]: Sync: MonadThrowable](
      url: String,
      chatId: Long,
      reader: HtmlReader[F],
      articleRepo: ArticleRepository[F]
  ): F[TelegramResponse] = {
    val out: F[TelegramResponse] = for {
      keywords <- reader.retrieveKeywords(url)
      article = Article(
        ArticleUrl(url),
        ChatId(chatId),
        MessageId(97.toString),
        ArticleWords(keywords)
      )
      _ <- articleRepo.saveArticle(article)
    } yield SuccessfullySave(SendMessage(chatId, "Saved"))

    out
  }

  private def getArticle[F[_]: MonadThrowable](
      keyword: String,
      chatId: Long,
      articleRepo: ArticleRepository[F]
  ): F[TelegramResponse] =
    for {
      article <- articleRepo
        .getByKeywordForChat(keyword, chatId)
        .flatMap[Article] {
          case x if x.nonEmpty => implicitly[MonadThrowable[F]].pure(x.head)
          case _ =>
            implicitly[MonadThrowable[F]].raiseError(
              new RuntimeException(
                s"Article with $keyword keyword wasn't found"
              )
            )
        }
      response <- implicitly[MonadThrowable[F]].pure(
        SuccessfullySave(SendMessage(chatId, article.url.value))
      )
    } yield response

  private def generateError[F[_]: MonadThrowable](
      err: Throwable,
      chatId: Long
  ): F[TelegramResponse] =
    implicitly[MonadThrowable[F]].pure(
      FailHandleMessage(SendMessage(chatId, err.getMessage))
    )

}
