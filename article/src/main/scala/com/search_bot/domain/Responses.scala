package com.search_bot.domain

import com.bot4s.telegram.methods.JsonRequest
import com.bot4s.telegram.models.Message

object Responses {

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

}
