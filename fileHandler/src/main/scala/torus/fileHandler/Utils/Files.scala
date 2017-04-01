package torus.fileHandler.Utils

import java.io.File

case class FileInfo(name: String, path: String)

object Files {

  def listFiles(f: File): Array[File] = {
    val these = f.listFiles
    these ++ these.filter(_.isDirectory).flatMap(listFiles)
  }

}