
name := s"${TorusBuild.NamePrefix}root"
version := "1.0"
scalaVersion := "2.11.8"
organization in ThisBuild := "torus"

enablePlugins(sbtdocker.DockerPlugin)

lazy val common = project.settings(Common.settings: _*)

lazy val fileHandler = project
    .settings(Common.settings: _*)
    .settings(libraryDependencies ++= Dependencies.fileHandlerDependencies)
    .dependsOn(common)

lazy val imageHandler = project
  .settings(Common.settings: _*)
  .settings(libraryDependencies ++= Dependencies.imageHandlerDependencies)
  .dependsOn(common)

lazy val root = (project in file("."))
  .settings(mainClass in Compile := Some("torus.imageHandler.imageHandlerService"))
  .aggregate(common, imageHandler)
  .dependsOn(common, imageHandler)

dockerfile in docker := {
  
  val artifact: File = assembly.value
  val artifactTargetPath = s"/app/${artifact.name}"

  new Dockerfile {
    from("java")
    volume("/home/paul/john/", "/dataBIS")
    add(artifact, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
  }
}