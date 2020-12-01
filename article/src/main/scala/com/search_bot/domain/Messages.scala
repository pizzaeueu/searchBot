package com.search_bot.domain

object Messages {

  sealed trait TelegramMessage

  case object ScanArticle extends TelegramMessage

  case object GetArticle extends TelegramMessage

}
