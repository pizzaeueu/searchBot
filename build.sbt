import Dependencies._

enablePlugins(DockerComposePlugin)

ThisBuild / organization := "com.search_bot"
ThisBuild / scalaVersion := "2.13.10"
ThisBuild / version := "0.0.1-SNAPSHOT"
ThisBuild / scalafmtOnCompile := true
ThisBuild / scalafmtTestOnCompile := true

lazy val searchBot =
  project
    .in(file("."))
    .aggregate(main, article, reader)
    .settings(
      run := {
        (run in main in Compile).evaluated
      }
    )

lazy val main =
  project
    .in(file("main"))
    .settings(commonSettings)
    .dependsOn(article)
    .dependsOn(reader)
    .settings(
      libraryDependencies ++= Seq(
        Database.flywayCore,
        Config.pureConfig,
      )
    )

lazy val article =
  project
    .in(file("article"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        Telegram.bot4s,
        Sttp.softwareMill,
        Database.doobie,
        Database.doobiePostgres,
        Database.doobieHikari,
        Log.logback,
      )
    )
    .dependsOn(reader)

lazy val reader =
  project
    .in(file("reader"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        Http4s.dsl,
        Http4s.client,
        Utils.jsoup,
      )
    )

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Cats.core,
    Cats.effects,
    UnitTest.scalaTest,
    UnitTest.scalaMock,
    Utils.evoCatsHelper,
  ),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
)
