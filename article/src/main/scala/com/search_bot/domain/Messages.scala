package com.search_bot.domain
import cats.implicits._
import com.bot4s.telegram.models.Message

object Messages {

  sealed trait TelegramMessage {
    val msg: Message
  }
  case class ScanArticle(msg: Message) extends TelegramMessage
  case class GetArticle(msg: Message) extends TelegramMessage

  def of(message: Message):Option[TelegramMessage] = message.text.flatMap {
    case s if s.startsWith("/scan") => ScanArticle(message).some
    case s if s.startsWith("/find") => GetArticle(message).some
    case _ => None
  }

}
