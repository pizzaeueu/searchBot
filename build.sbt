import Dependencies._

ThisBuild / organization := "com.search_bot"
ThisBuild / scalaVersion := "2.12.9"
ThisBuild / version := "0.0.1-SNAPSHOT"

lazy val searchBot  =
  project.in(file(".")).aggregate(main, article).settings(
    run := {
      (run in main in Compile).evaluated
    }
  )

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
        Config.pureConfig,
        Database.doobie,
        Database.doobiePostgres,
        Database.doobieHikari,
        Log.logback
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

enablePlugins(FlywayPlugin)
libraryDependencies += "postgresql" % "postgresql" % "9.1-901.jdbc4"
flywayUrl := "jdbc:postgresql://localhost:5432/bot_db"
flywayUser := ""
flywayPassword := ""
flywayLocations += "db/migration"
