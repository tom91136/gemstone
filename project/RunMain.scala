import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import sbt.*
import sbt.Keys.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

object RunMain {
  def main(
      project: ProjectReference,
      mainClass: String,
      arguments: Def.Initialize[Task[Seq[String]]]
  ): Def.Initialize[Task[Unit]] = Def.task {
    Def.uncached {
      val converter = fileConverter.value
      val classpath = (project / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      (project / Compile / run / runner).value.run(mainClass, classpath, arguments.value, streams.value.log).get
    }
  }

  /** Runs `mainClass` to write `directory` for `isa`, unless the class path's contents and the arguments are those the
    * directory was last written from.
    */
  def generated(
      project: ProjectReference,
      mainClass: String,
      directory: Def.Initialize[Path],
      isa: String
  ): Def.Initialize[Task[Unit]] = Def.task {
    Def.uncached {
      val converter = fileConverter.value
      val classpath = (project / Compile / fullClasspath).value.map(entry => converter.toPath(entry.data))
      val output    = directory.value
      val stamp     = output.resolveSibling(s"${output.getFileName}.stamp")
      val key       = fingerprint(classpath, Seq(mainClass, isa))
      val current   = Files.isRegularFile(stamp) && Files.readString(stamp) == key && Files.isDirectory(output)
      if (current) streams.value.log.info(s"$output is up to date")
      else {
        val _ = Files.deleteIfExists(stamp)
        (project / Compile / run / runner).value
          .run(mainClass, classpath, Seq(output.toString, isa), streams.value.log).get
        val _ = Files.writeString(stamp, key)
      }
    }
  }

  private def fingerprint(classpath: Seq[Path], arguments: Seq[String]): String = {
    val digest                    = MessageDigest.getInstance("SHA-256")
    def text(value: String): Unit = digest.update(value.getBytes(StandardCharsets.UTF_8))
    def content(file: Path): Unit = Using.resource(Files.newInputStream(file)) { in =>
      val buffer = Array.ofDim[Byte](1 << 16)
      Iterator.continually(in.read(buffer)).takeWhile(_ >= 0).foreach(digest.update(buffer, 0, _))
    }
    arguments.foreach(text(_))
    classpath.foreach { entry =>
      if (Files.isDirectory(entry))
        Using.resource(Files.walk(entry))(_.iterator.asScala.filter(Files.isRegularFile(_)).toVector.sorted).foreach {
          file =>
            text(entry.relativize(file).toString)
            content(file)
        }
      else if (Files.isRegularFile(entry)) {
        text(entry.getFileName.toString)
        content(entry)
      }
    }
    digest.digest().map(byte => f"$byte%02x").mkString
  }
}
