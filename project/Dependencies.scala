import sbt._
import Keys._

object Dependencies {
  val akkaV = "2.4.3"

  val commonDependencies : Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % "1.2.1",
    "com.typesafe.akka" %% "akka-actor" % akkaV,
    "com.typesafe.akka" %% "akka-stream" % akkaV,
    "com.typesafe.akka" %% "akka-http" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
  )

  val fileHandlerDependencies : Seq[ModuleID] = Seq()

}