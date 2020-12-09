package com.search_bot.domain
import cats.implicits._
import com.bot4s.telegram.models.Message

object Messages {

  sealed trait InputMessages { val command: String }
  case class Scan(command: String) extends InputMessages
  case class Find(command: String) extends InputMessages

  sealed trait TelegramMessage
  case class ScanArticle(url: String, chatId: Long) extends TelegramMessage
  case class GetArticle(keyword: String, chatId: Long) extends TelegramMessage
  case class CommandNotSupported(chatId: Long, command: String)
      extends TelegramMessage

  def of(message: Message): TelegramMessage = {
    message.text.flatMap(scanMessageText) match {
      case Some(Scan(url))     => ScanArticle(url, message.chat.id)
      case Some(Find(keyword)) => GetArticle(keyword, message.chat.id)
      case _                   => CommandNotSupported(message.chat.id, message.text.getOrElse(""))
    }
  }

  private def scanMessageText(text: String): Option[InputMessages] = {
    text match {
      case s if s.startsWith("/scan ") & s.length > 6 =>
        Scan(s.substring(6)).some
      case s if s.startsWith("/find ") & s.length > 6 =>
        Find(s.substring(6)).some
      case _ => none[InputMessages]
    }
  }

}
