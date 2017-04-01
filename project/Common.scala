import sbt._
import Keys._


object Common {
  val appVersion = "1.0"

  lazy val copyDependencies = TaskKey[Unit]("copy-dependencies")

  def copyDepTask = copyDependencies <<= (update, crossTarget, scalaVersion) map {
    (updateReport, out, scalaVer) =>
      updateReport.allFiles foreach { srcPath =>
        val destPath = out / "lib" / srcPath.getName
        IO.copyFile(srcPath, destPath, preserveLastModified=true)
      }
  }

  val settings: Seq[Def.Setting[_]] = Seq(
    version := appVersion,
    scalaVersion := "2.11.8",
    copyDepTask,
    resolvers += "The New Motion Public Repo" at "http://nexus.thenewmotion.com/content/groups/public/",
    libraryDependencies ++= Dependencies.commonDependencies,
    exportJars := true
  )
}