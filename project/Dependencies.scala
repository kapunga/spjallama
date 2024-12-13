import sbt.*

object Dependencies {
  object V {
    val circe = "0.14.10"
    val scalaTest = "3.2.19"
    val sttpClient = "4.0.0-M19"
  }

  object Libraries {
    val circe: Seq[ModuleID] = Seq(
      "io.circe" %% "circe-core" % V.circe,
      "io.circe" %% "circe-generic" % V.circe,
      "io.circe" %% "circe-parser" % V.circe
    )

    val scalaTest: Seq[ModuleID] =
      Seq("org.scalatest" %% "scalatest" % V.scalaTest % Test)

    val sttpClient: Seq[ModuleID] = Seq(
      "com.softwaremill.sttp.client4" %% "core" % V.sttpClient,
      "com.softwaremill.sttp.client4" %% "circe" % V.sttpClient
    )
  }
}
