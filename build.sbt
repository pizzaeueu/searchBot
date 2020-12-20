import Dependencies._

enablePlugins(DockerComposePlugin)

ThisBuild / organization := "com.search_bot"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.0.1-SNAPSHOT"
scalafmtOnCompile in ThisBuild := true
scalafmtTestOnCompile in ThisBuild := true

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
    .settings(
      libraryDependencies ++= Seq(
        Database.flywayCore
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
        Config.pureConfig,
        Database.doobie,
        Database.doobiePostgres,
        Database.doobieHikari,
        Log.logback
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
        Utils.jsoup
      )
    )

lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Cats.core,
    Cats.effects,
    UnitTest.scalaTest,
    UnitTest.scalaMock,
    UnitTest.catsHelperTestKil,
    Utils.evoCatsHelper
  ),
  resolvers += Resolver.bintrayRepo("evolutiongaming", "maven"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
)
