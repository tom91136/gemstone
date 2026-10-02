package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import scala.jdk.CollectionConverters.*
import scala.util.Using

/** Atomic updates for checked-in generator outputs. */
object GeneratedFiles {
  def writeBytes(path: Path, content: Array[Byte]): Boolean =
    if (Files.isRegularFile(path) && java.util.Arrays.equals(Files.readAllBytes(path), content)) false
    else {
      val _                        = Files.createDirectories(path.getParent)
      given Using.Releasable[Path] = temporary => { val _ = Files.deleteIfExists(temporary); () }
      Using.resource(Files.createTempFile(path.getParent, s".${path.getFileName}.", ".tmp")) { temporary =>
        val _ = Files.write(temporary, content)
        try Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        catch {
          case _: AtomicMoveNotSupportedException =>
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
        }
        true
      }
    }

  def write(path: Path, content: String): Boolean = {
    val current = Option.when(Files.isRegularFile(path))(Files.readString(path, StandardCharsets.UTF_8))
    if (current.contains(content)) false
    else {
      val _                        = Files.createDirectories(path.getParent)
      given Using.Releasable[Path] = temporary => { val _ = Files.deleteIfExists(temporary); () }
      Using.resource(Files.createTempFile(path.getParent, s".${path.getFileName}.", ".tmp")) { temporary =>
        val _ = Files.writeString(temporary, content, StandardCharsets.UTF_8)
        try Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        catch {
          case _: AtomicMoveNotSupportedException =>
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
        }
        true
      }
    }
  }

  def removeUnexpected(directory: Path, expected: Set[Path], include: Path => Boolean): Unit =
    if (Files.isDirectory(directory)) {
      val normalized = expected.map(_.toAbsolutePath.normalize)
      val unexpected = Using.resource(Files.walk(directory)) { paths =>
        paths.toList.asScala.view.filter(path => Files.isRegularFile(path) && include(path))
          .map(_.toAbsolutePath.normalize).filterNot(normalized).toVector
      }
      unexpected.foreach(path => Files.delete(path))
    }
}
