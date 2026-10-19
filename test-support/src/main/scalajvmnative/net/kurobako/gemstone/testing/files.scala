package net.kurobako.gemstone.testing

import java.nio.file.Files
import java.nio.file.Path
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Scoped temporary files. Use temporary for one lexical scope, or Directory with Using for a shared lifetime. */
object files {
  class Directory(prefix: String = "gemstone-test-") extends AutoCloseable {
    val path: Path             = Files.createTempDirectory(prefix)
    override def close(): Unit = Using.resource(Files.walk(path)) { paths =>
      paths.toList.asScala.toVector.sortBy(_.getNameCount).reverse.foreach(Files.delete(_))
    }
  }

  def temporary[A](body: Path => A): A = Using.resource(Directory())(directory => body(directory.path))

  def write(root: Path, name: String, content: String): Path = {
    val path = root.resolve(name)
    val _    = Files.createDirectories(path.getParent)
    Files.writeString(path, content)
  }

  def snapshot(root: Path): Map[String, String] = Using.resource(Files.walk(root)) { paths =>
    paths.toList.asScala.filter(Files.isRegularFile(_)).map { path =>
      root.relativize(path).toString -> Files.readString(path)
    }.toMap
  }
}
