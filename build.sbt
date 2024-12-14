import Dependencies.Libraries

val scala3Version = "3.5.2"

lazy val commonSettings = Seq(
  organization := "org.kapunga.spjallama",
  scalaVersion := scala3Version,
  version := "0.0.1-SNAPSHOT",
)

lazy val root = (project in file("."))
  .settings(name := "spjallama")
  .settings(commonSettings *)
  .aggregate(core, client)

lazy val core = (project in file("core"))
  .settings(
    libraryDependencies ++= Libraries.cats ++ Libraries.circe ++ Libraries.sconfig
  )
  .settings(commonSettings *)

lazy val client = (project in file("client"))
  .settings(
    libraryDependencies ++= Libraries.circe ++ Libraries.sttpClient ++ Libraries.scalaTest
  )
  .settings(commonSettings *)
  .dependsOn(core)

lazy val examples = (project in file("examples"))
  .settings(
    libraryDependencies ++= Libraries.fs2
  )
  .settings(commonSettings *)
  .settings(publish / skip := true)
  .dependsOn(core, client)
