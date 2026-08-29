package net.kurobako.gemstone.codegen

import java.nio.charset.StandardCharsets
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/** Atomic, no-op-when-current writes for checked-in generator outputs. */
object GeneratedFiles {
  def write(path: Path, content: String): Boolean = {
    val current = Option.when(Files.isRegularFile(path))(Files.readString(path, StandardCharsets.UTF_8))
    if (current.contains(content)) false
    else {
      val _         = Files.createDirectories(path.getParent)
      val temporary = Files.createTempFile(path.getParent, s".${path.getFileName}.", ".tmp")
      try {
        val _ = Files.writeString(temporary, content, StandardCharsets.UTF_8)
        try Files.move(temporary, path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING)
        catch {
          case _: AtomicMoveNotSupportedException =>
            Files.move(temporary, path, StandardCopyOption.REPLACE_EXISTING)
        }
        true
      } finally {
        val _ = Files.deleteIfExists(temporary)
      }
    }
  }
}
