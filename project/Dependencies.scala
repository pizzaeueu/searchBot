import sbt._

object Dependencies {

  object Telegram {
    val bot4s = "com.bot4s" %% "telegram-core" % "4.4.0-RC2"
  }

  object Cats {
    val core = "org.typelevel" %% "cats-core" % "2.1.1"
    val effects = "org.typelevel" %% "cats-effect" % "2.2.0"
  }

  object Sttp {
    val softwareMill = "com.softwaremill.sttp" %% "async-http-client-backend-cats" % "1.7.2"
  }

  object Test {
    val scalaTest = "org.scalatest" %% "scalatest" % "3.2.3"
  }

  object Database {
    val doobie = "org.tpolecat" %% "doobie-core" % "0.9.0"
    val doobiePostgres = "org.tpolecat" %% "doobie-postgres"  % "0.9.0"
  }

  object Config {
    val pureConfig = "com.github.pureconfig" %% "pureconfig" % "0.14.0"
  }


}
