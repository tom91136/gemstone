package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec

trait BackendSession {
  def load(image: Vec[Byte]): Either[RuntimeError, RuntimeProgram]
  def close(): Unit
}
