package com.search_bot

import scala.concurrent.ExecutionContext

object Main extends App {

  import cats.effect._

  implicit val executionContext: ExecutionContext =
    ExecutionContext.global

  implicit val cs: ContextShift[IO] =
    IO.contextShift(executionContext)

  Program.dsl[cats.effect.IO].unsafeRunSync()

}
