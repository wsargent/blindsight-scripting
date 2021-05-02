package com.tersesystems.blindsight.groovy

import java.io.{FileNotFoundException, IOException, Reader, StringReader}
import java.nio.file.{Files, Path}
import java.nio.file.attribute.FileTime
import java.util.concurrent.atomic.AtomicReference

class FileConditionSource(val path: Path) extends ConditionSource {

  if (!Files.exists(path)) throw new FileNotFoundException(path.toAbsolutePath.toString)

  private val lastModified = new AtomicReference[FileTime](Files.getLastModifiedTime(path))

  override def isInvalid: Boolean = {
    try {
      val newTime = Files.getLastModifiedTime(path)
      if (newTime.compareTo(lastModified.get) > 0) {
        lastModified.set(newTime)
        true
      }
      else false
    } catch {
      case e: IOException =>
        //e.printStackTrace()
        true
    }
  }

  override def getReader: Reader = {
    try Files.newBufferedReader(path)
    catch {
      case e: IOException =>
        //e.printStackTrace()
        new StringReader("def evaluate() { false }")
    }
  }
}