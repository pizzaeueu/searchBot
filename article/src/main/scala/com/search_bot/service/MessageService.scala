package com.search_bot.service

import java.net.ConnectException

import cats.effect.Sync
import cats.syntax.all._
import com.bot4s.telegram.methods.SendMessage
import com.evolutiongaming.catshelper.{Log, MonadThrowable}
import com.search_bot.reader.HtmlReader
import com.search_bot.domain.Article._
import com.search_bot.domain.Messages.{
  CommandNotSupported,
  GetArticle,
  ScanArticle,
  TelegramMessage
}
import com.search_bot.domain.Responses.{
  ArticleAlreadyExists,
  ArticleNotFound,
  FailHandleMessage,
  SuccessfullySave,
  TelegramResponse,
  UrlIsNotValid
}
import com.search_bot.repository.ArticleRepository
import org.slf4j.LoggerFactory

trait MessageService[F[_]] {
  def handle(message: TelegramMessage): F[TelegramResponse]
}

object MessageService {

  def of[F[_]: Sync: MonadThrowable](
      articleRepo: ArticleRepository[F],
      articleReader: HtmlReader[F]
  ): MessageService[F] = new MessageService[F] {
    implicit val logger = Log[F](LoggerFactory.getLogger("MessageService"))

    override def handle(message: TelegramMessage): F[TelegramResponse] = {
      message match {
        case ScanArticle(url, chatId) =>
          scanArticle(url, chatId, articleReader, articleRepo).recoverWith {
            case _: ConnectException =>
              MonadThrowable.summon.pure(
                UrlIsNotValid(SendMessage(chatId, s"url $url is not valid")))
            case err => generateError(err, chatId.toLong)
          }
        case GetArticle(keyword, chatId) =>
          getArticle(keyword, chatId, articleRepo).handleErrorWith { err =>
            generateError(err, chatId.toLong)
          }
        case CommandNotSupported(chatId, command) =>
          MonadThrowable.summon.pure(
            FailHandleMessage(
              SendMessage(chatId, s"command $command not supported")
            )
          )
      }
    }
  }

  private def scanArticle[F[_]: Sync: MonadThrowable](
      url: String,
      chatId: Long,
      reader: HtmlReader[F],
      articleRepo: ArticleRepository[F]
  ): F[TelegramResponse] =
    for {
      keywords <- reader.retrieveKeywords(url)
      article = Article(
        ArticleUrl(url),
        ChatId(chatId),
        ArticleWords(keywords)
      )
      existed <- articleRepo.getByUrlForChat(url, chatId)
      message <- existed match {
        case Some(_) =>
          MonadThrowable.summon.pure(
            ArticleAlreadyExists(
              SendMessage(chatId, "Article has already saved")))
        case None =>
          articleRepo.saveArticle(article) *> MonadThrowable.summon.pure(
            SuccessfullySave(SendMessage(chatId, "Saved")))
      }
    } yield message

  private def getArticle[F[_]: MonadThrowable](
      keyword: String,
      chatId: Long,
      articleRepo: ArticleRepository[F]
  ): F[TelegramResponse] =
    for {
      article <- articleRepo
        .getByKeywordForChat(keyword, chatId)
      response <- article match {
        case articles if articles.nonEmpty =>
          val urls = articles.map(_.url.value).reduce(_ + "\n" + _)
          MonadThrowable.summon.pure(
            SuccessfullySave(SendMessage(chatId, urls))
          )
        case _ =>
          MonadThrowable.summon.pure(
            ArticleNotFound(
              SendMessage(chatId,
                          s"Article with keyword =  $keyword wasn't found"))
          )
      }
    } yield response

  private def generateError[F[_]: MonadThrowable: Sync](
      err: Throwable,
      chatId: Long
  )(implicit logger: Log[F]): F[TelegramResponse] =
    logger.error(s"Error During message handling: chatId $chatId, err: $err") *> MonadThrowable.summon
      .pure(
        FailHandleMessage(
          SendMessage(
            chatId,
            s"Unexpected Error during process. Please contact owner for details. Error Message - ${err.getMessage}"
          )
        )
      )

}
