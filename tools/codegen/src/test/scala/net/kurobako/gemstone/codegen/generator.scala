package net.kurobako.gemstone.codegen

import java.nio.file.Path
import net.kurobako.gemstone.testing.*

private object generator {
  def inDirectory[A](body: Path => A): A = files.temporary(directory => output.onFailure(body(directory)))
}
