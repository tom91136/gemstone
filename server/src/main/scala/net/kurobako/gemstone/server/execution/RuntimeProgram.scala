package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.GlobalName
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.isa.ProgramSymbols
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.Memory
import net.kurobako.gemstone.vm.MemoryRegion
import net.kurobako.gemstone.vm.Result
import net.kurobako.gemstone.vm.TaskControl

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

trait RuntimeProgram {
  def symbols: Either[RuntimeError, ProgramSymbols]
  def kernel(name: KernelName): Either[RuntimeError, KernelMetadata]
  def globals: Option[RuntimeProgram.Globals]
  def execute(task: KernelTask, memory: Memory, control: TaskControl): Either[RuntimeError, Result]
  def close(): Unit
}

object RuntimeProgram {
  trait Globals {
    def image(name: GlobalName): Either[RuntimeError, Globals.Image]

    def bind(
        name: GlobalName,
        region: MemoryRegion,
        offset: Long :| Positive0,
        regionSize: Long :| Positive,
        memory: Memory
    ): Either[RuntimeError, Unit]

    def write(
        name: GlobalName,
        offset: Long :| Positive0,
        bytes: Vec[Byte]
    ): Either[RuntimeError, Unit]
  }

  object Globals {
    final case class Image(size: Long :| Positive0, initial: Vec[Byte])
  }
}
