import sbt._

object Dependencies {

  object Telegram {
    val bot4s = "com.bot4s" %% "telegram-core" % "5.6.1"
  }

  object Cats {
    val core = "org.typelevel" %% "cats-core" % "2.1.1"
    val effects = "org.typelevel" %% "cats-effect" % "2.2.0"
  }

  object Sttp {
    val softwareMill = "com.softwaremill.sttp" %% "async-http-client-backend-cats" % "1.7.2"
  }

  object UnitTest {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.2.3" % Test
    val scalaMock = "org.scalamock" %% "scalamock" % "4.4.0" % Test
    val catsHelperTestKil = "com.evolutiongaming" %% "cats-helper-testkit" % "2.8.0"
  }

  object Database {
    val doobieVersion = "0.9.0"
    val doobie = "org.tpolecat" %% "doobie-core" % doobieVersion
    val doobiePostgres = "org.tpolecat" %% "doobie-postgres"  % doobieVersion
    val doobieHikari = "org.tpolecat" %% "doobie-hikari" % doobieVersion
    val flywayCore = "org.flywaydb" % "flyway-core" % "6.2.4"
  }

  object Config {
    val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.0"
  }

  object Log {
    val logback = "ch.qos.logback" % "logback-classic" % "1.1.3"
  }

  object Http4s {
   val http4sVersion = "0.23.9"
   val dsl =  "org.http4s" %% "http4s-dsl" % http4sVersion
   val client = "org.http4s" %% "http4s-blaze-client" % http4sVersion
  }

  object Utils {
    val jsoup = "org.jsoup" % "jsoup" % "1.13.1"
    val evoCatsHelper = "com.evolutiongaming" %% "cats-helper" % "2.8.0"
  }


}
