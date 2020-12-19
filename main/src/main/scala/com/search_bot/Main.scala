package com.search_bot

import cats.effect.{IOApp, _}

import scala.concurrent.ExecutionContext

object Main extends IOApp {
  implicit val executionContext: ExecutionContext = ExecutionContext.global
  override def run(args: List[String]): IO[ExitCode] =
    Program
      .dsl[cats.effect.IO](args.contains("inMemory"))
      .use(_ => IO(ExitCode.Success))
}
