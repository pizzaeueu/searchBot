package com.search_bot

import cats.Monad
import cats.effect.{Async, ContextShift, ExitCode}
import com.search_bot.controller.MainController
import cats.syntax.all._

import scala.concurrent.ExecutionContext

object Program {

  def dsl[F[_] : Async : ContextShift: Monad](implicit ec: ExecutionContext): F[Unit] = MainController.bot4sDsl.getMessage()

}
