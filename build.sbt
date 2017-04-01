name := TorusBuild.NamePrefix + "root"

version := "1.0"

scalaVersion := "2.11.8"

lazy val common = project.settings(Common.settings: _*)

lazy val fileHandler = project
    .settings(Common.settings: _*)
    .settings(libraryDependencies ++= Dependencies.fileHandlerDependencies)
    .dependsOn(common)

lazy val root = (project in file(".")).aggregate(common, fileHandler)
    