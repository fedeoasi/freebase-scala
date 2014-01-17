package com.github.fedeoasi

import java.io.{FileOutputStream, BufferedReader, InputStreamReader, FileInputStream}
import java.util.zip.GZIPInputStream

object Main {
  val filmsFilename = "/tmp/freebase-2014-01-12-00-00-films.gz"
  val labelsFilename = "/tmp/freebase-2014-01-12-00-00-labels.gz"

  def main(args: Array[String]) {
    val filmsReader = getBufferedReader(filmsFilename)
    val movieSet = foldLines[Set[String]](filmsReader, Set.empty[String]) {
      (set, line) => {
        set + line.split("\t")(0)
      }
    }
    filmsReader.close

    val labelsReader = getBufferedReader(labelsFilename)
    val output = new FileOutputStream("output.txt")
    foldLines(labelsReader, (output, movieSet)) {
      case ((o, set), line) =>
        val split = line.split("\t")
        if(set.contains(split(0))) {
          o.write((split(0) + "\t" + split(2) + "\n").getBytes)
        }
        (o, set)
    }
    output.close
  }

  def foldLines[T](input: BufferedReader, initialValue: T)(f: (T, String) => T): T = {
    var line = input.readLine()
    var acc = initialValue
    while(line != null) {
      acc = f(acc, line)
      line = input.readLine()
    }
    acc
  }

  def getBufferedReader(filename: String): BufferedReader = {
    val fileStream = new FileInputStream(filename);
    val gzipStream = new GZIPInputStream(fileStream);
    val decoder = new InputStreamReader(gzipStream, "UTF-8");
    new BufferedReader(decoder);
  }
}
