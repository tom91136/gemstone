package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Atomic updates for checked-in generator outputs; unchanged files keep their timestamps. */
object GeneratedFiles {
  def writeBytes(path: Path, content: Array[Byte]): Unit =
    if (!Files.isRegularFile(path) || !java.util.Arrays.equals(Files.readAllBytes(path), content)) {
      val _                        = Files.createDirectories(path.getParent)
      given Using.Releasable[Path] = temporary => { val _ = Files.deleteIfExists(temporary); () }
      Using.resource(Files.createTempFile(path.getParent, s".${path.getFileName}.", ".tmp")) { temporary =>
        val _ = Files.write(temporary, content)
        try {
          val _ = Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        } catch {
          case _: AtomicMoveNotSupportedException =>
            val _ = Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
        }
      }
    }

  def write(path: Path, content: String): Unit = writeBytes(path, content.getBytes(StandardCharsets.UTF_8))

  def writeScala(path: Path, content: String): Unit = write(path, GeneratedScalaFormatter(path, content))

  def removeUnexpected(directory: Path, expected: Set[Path], include: Path => Boolean): Unit =
    if (Files.isDirectory(directory)) {
      val normalised = expected.map(_.toAbsolutePath.normalize)
      val unexpected = Using.resource(Files.walk(directory)) { paths =>
        paths.toList.asScala.view.filter(path => Files.isRegularFile(path) && include(path))
          .map(_.toAbsolutePath.normalize).filterNot(normalised).toVector
      }
      unexpected.foreach(path => Files.delete(path))
    }
}
