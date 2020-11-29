import Dependencies._

ThisBuild / organization := "com.search_bot"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.0.1-SNAPSHOT"

lazy val searchBot  =
  project.in(file(".")).aggregate(domain, persistence, controller, main).settings(
    run := {
      (run in main in Compile).evaluated
    }
  )

lazy val persistence =
  project
    .in(file("persistence"))
    .settings(commonSettings).dependsOn(domain)


lazy val domain =
  project
    .in(file("domain"))
    .settings(commonSettings)


lazy val main =
  project
    .in(file("main"))
    .settings(commonSettings)
    .dependsOn(controller)


lazy val controller =
  project
    .in(file("controller"))
    .settings(commonSettings)
    .settings(
      libraryDependencies ++= Seq(
        Telegram.bot4s,
        Sttp.softwareMill
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
