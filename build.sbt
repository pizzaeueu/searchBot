import Dependencies._

ThisBuild / organization := "com.search_bot"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.0.1-SNAPSHOT"

lazy val searchBot  =
  project.in(file(".")).aggregate(persistence, main, article).settings(
    run := {
      (run in main in Compile).evaluated
    }
  )

lazy val persistence =
  project
    .in(file("persistence"))
    .settings(commonSettings)

lazy val main =
  project
    .in(file("main"))
    .settings(commonSettings)
    .dependsOn(article)


lazy val article =
  project
    .in(file("article"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        Telegram.bot4s,
        Sttp.softwareMill,
        Config.pureConfig
      )
    )


lazy val commonSettings = Seq(
  libraryDependencies ++= Seq(
    Cats.core,
    Cats.effects,
    Dependencies.Test.scalaTest
  ),
  Compile / console / scalacOptions --= Seq(
    "-Wunused:_",
    "-Xfatal-warnings"
  )
)
