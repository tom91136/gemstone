package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.rpc.ExecutionProtocol.ExecutionFailure
import net.kurobako.gemstone.isa.{KernelMetadata, ProgramSymbols}
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.vm.{Event, KernelTask, Memory, MemoryRegion, Observer, Result, Scheduler, TaskControl}
import net.kurobako.gemstone.Vec

trait RuntimeBackend {
  def encoding: String
  def load(services: RuntimeBackend.Services, image: Vec[Byte]): Either[ExecutionFailure, RuntimeBackend.Program]
}

object RuntimeBackend {
  case class Services(observer: Observer[Event], scheduler: Scheduler)

  trait Program {
    // Backends that expose program-scoped function pointers can bind their
    // service identity before the program becomes visible to launches.
    def bindProgramId(id: Long): Unit = ()
    def symbols: Either[ExecutionFailure, ProgramSymbols]
    def kernel(name: NonEmptyString): Either[ExecutionFailure, KernelMetadata]
    def globals: Option[Program.Globals]
    def execute(task: KernelTask, memory: Memory, control: TaskControl): Either[ExecutionFailure, Result]
    def close(): Unit
  }

  object Program {
    trait Globals {
      def image(name: NonEmptyString): Either[ExecutionFailure, Globals.Image]

      def bind(
          name: NonEmptyString,
          region: MemoryRegion,
          offset: NonNegative[Long],
          regionSize: Positive[Long],
          memory: Memory
      ): Either[ExecutionFailure, Unit]

      def write(
          name: NonEmptyString,
          offset: NonNegative[Long],
          bytes: Vec[Byte]
      ): Either[ExecutionFailure, Unit]
    }

    object Globals {
      case class Image(size: NonNegative[Long], initial: Vec[Byte])
    }
  }

  val defaults: List[RuntimeBackend] = List(
    PTXRuntimeBackend,
    HSARuntimeBackend,
    SPIRVRuntimeBackend(Validation.Environment.OpenCLCompute),
    SPIRVRuntimeBackend(Validation.Environment.VulkanCompute, "spirv-vulkan-binary")
  )
}
