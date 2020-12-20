package com.search_bot.domain

import com.bot4s.telegram.methods.JsonRequest
import com.bot4s.telegram.models.Message

object Responses {

  val CommandListResponse =
    """
      |Hi there!
      |Search bot is the bot which helps you to find articles by keyword
      |
      |You can use /scan command in order to scan article
      |You can use /find command in order to find article by keyword
      |You can use /start or /help command to call this message
      |
      |e.g.
      |/scan {your_url}
      |/find {your_keyword}
      |
      |details: https://github.com/SamosadovArtem/searchBot
      |""".stripMargin

  sealed trait TelegramResponse {
    val message: JsonRequest[Message]
  }

  final case class SuccessfullySave(message: JsonRequest[Message])
      extends TelegramResponse

  final case class SuccessfullyRetrieve(message: JsonRequest[Message])
      extends TelegramResponse

  final case class ArticleNotFound(message: JsonRequest[Message])
      extends TelegramResponse

  final case class FailHandleMessage(message: JsonRequest[Message])
      extends TelegramResponse

  final case class UrlIsNotValid(message: JsonRequest[Message])
      extends TelegramResponse

  final case class ArticleAlreadyExists(message: JsonRequest[Message])
      extends TelegramResponse

  final case class CommandsList(message: JsonRequest[Message])
      extends TelegramResponse

}
