package com.search_bot.error

object Errors {
  sealed trait ServiceError {
    val statusCode: Int
    val errorMessage: String
    val details: Option[Map[String, String]]
  }
  case class ArticleNotFond(
   errorMessage: String,
   statusCode: Int = 404,
   details: Option[Map[String, String]] = None) extends ServiceError

}
