package com.search_bot.service

import cats.MonadError
import cats.effect.Sync
import cats.syntax.all._
import com.bot4s.telegram.methods.{ForwardMessage, SendMessage}
import com.search_bot.dao.HtmlReader
import com.search_bot.domain.Article.{Article, ArticleUrl, ArticleWords, ChatId, MessageId}
import com.search_bot.domain.Messages.{CommandNotSupported, GetArticle, ScanArticle, TelegramMessage}
import com.search_bot.domain.Responses.{FailHandleMessage, SuccessfullySave, TelegramResponse}
import com.search_bot.repository.ArticleRepository

trait MessageService[F[_]] {
  def handle(message: TelegramMessage): F[TelegramResponse]
}

object MessageService {
  def messageService[F[_]: Sync](articleRepo: ArticleRepository[F], articleReader: HtmlReader[F])(implicit F: MonadError[F, Throwable]): MessageService[F] = {
    case ScanArticle(url, chatId) => scanArticle(url, chatId, articleReader, articleRepo)
    case GetArticle(keyword, chatId) => getArticle(keyword, chatId, articleRepo).recoverWith {
      case err => generateError(err, chatId.toLong)
    }
    case CommandNotSupported(chatId, command) => F.pure(FailHandleMessage(SendMessage(chatId, s"command $command not supported")))
  }

  private def scanArticle[F[_]: Sync](url: String, chatId: Long, reader: HtmlReader[F], articleRepo: ArticleRepository[F])(implicit F: MonadError[F, Throwable]): F[TelegramResponse] = {
    val out: F[TelegramResponse] = for {
      keywords <- reader.retrieveKeywords(url)
      article = Article(ArticleUrl(url), ChatId(chatId), MessageId(97.toString), ArticleWords(keywords))
      _ <- articleRepo.saveArticle(article)
    } yield SuccessfullySave(SendMessage(chatId, "Saved"))

    out
  }

  private def getArticle[F[_]](keyword: String, chatId: Long, articleRepo: ArticleRepository[F])(implicit F: MonadError[F, Throwable]): F[TelegramResponse] =
      for {
        article <- articleRepo.getByKeywordForChat(keyword, chatId).flatMap[Article] {
          case x if x.nonEmpty => F.pure(x.head)
          case _ => F.raiseError(new RuntimeException(s"Article with $keyword keyword wasn't found"))
        }
        response <- F.pure(SuccessfullySave(SendMessage(chatId, article.url.value)))
      } yield response

  private def generateError[F[_]](err: Throwable, chatId: Long)(implicit F: MonadError[F, Throwable]):F[TelegramResponse] =
    F.pure(FailHandleMessage(SendMessage(chatId, err.getMessage)))

      //SuccessfullySave(ForwardMessage(message.msg.chat.id, message.msg.chat.id, Some(true), message.msg.messageId))

}
