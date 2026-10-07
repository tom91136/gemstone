package net.kurobako.gemstone.server

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermissions

object PrivateFile {

  /** Restrict a file holding client identities or captured payloads to its owner. */
  def restrict(file: Path): Unit =
    Option(Files.getFileAttributeView(file, classOf[PosixFileAttributeView])).foreach { view =>
      view.setPermissions(PosixFilePermissions.fromString("rw-------"))
    }
}
