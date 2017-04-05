import sbt._
import Keys._

object Dependencies {
  val akkaV = "2.4.17"

  val commonDependencies : Seq[ModuleID] = Seq(
    "com.typesafe" % "config" % "1.2.1",
    "com.typesafe.akka" % "akka-actor_2.11" % "2.4.3",
    "com.typesafe.akka" %% "akka-http" % "10.0.5",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.0.5"
  )

  val fileHandlerDependencies : Seq[ModuleID] = Seq()

  val imageHandlerDependencies: Seq[ModuleID] = Seq(
    "org.apache.spark" % "spark-core_2.11" % "2.1.0",
    "org.apache.spark" % "spark-sql_2.11" % "2.1.0",
    "org.locationtech.geotrellis" % "geotrellis-spark_2.11" % "1.1.0-RC2"
  )

}