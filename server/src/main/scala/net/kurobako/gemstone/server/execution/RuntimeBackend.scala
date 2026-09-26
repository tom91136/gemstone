package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.isa.{GlobalName, KernelMetadata, KernelName, ProgramSymbols}
import net.kurobako.gemstone.isa.spirv.v1_6.Validation
import net.kurobako.gemstone.vm.{
  Event,
  Fault,
  KernelTask,
  Memory,
  MemoryRegion,
  Observer,
  Result,
  Scheduler,
  TaskControl
}
import net.kurobako.gemstone.{NonNegativeLong, PositiveLong, Vec}

trait RuntimeBackend {
  def encoding: String
  def load(services: RuntimeBackend.Services, image: Vec[Byte]): Either[RuntimeBackend.Error, RuntimeBackend.Program]
}

object RuntimeBackend {
  final case class Services(observer: Observer[Event], scheduler: Scheduler)

  enum Error(val message: String) {
    case Invalid(detail: String)     extends Error(detail)
    case NotFound(detail: String)    extends Error(detail)
    case Unsupported(detail: String) extends Error(detail)
    case Failed(detail: String)      extends Error(detail)
    case Execution(fault: Fault)     extends Error(fault.message)
  }

  trait Program {
    // Backends that expose program-scoped function pointers can bind their
    // service identity before the program becomes visible to launches.
    def bindProgramId(id: Long): Unit = ()
    def symbols: Either[Error, ProgramSymbols]
    def kernel(name: KernelName): Either[Error, KernelMetadata]
    def globals: Option[Program.Globals]
    def execute(task: KernelTask, memory: Memory, control: TaskControl): Either[Error, Result]
    def close(): Unit
  }

  object Program {
    trait Globals {
      def image(name: GlobalName): Either[Error, Globals.Image]

      def bind(
          name: GlobalName,
          region: MemoryRegion,
          offset: NonNegativeLong,
          regionSize: PositiveLong,
          memory: Memory
      ): Either[Error, Unit]

      def write(
          name: GlobalName,
          offset: NonNegativeLong,
          bytes: Vec[Byte]
      ): Either[Error, Unit]
    }

    object Globals {
      final case class Image(size: NonNegativeLong, initial: Vec[Byte])
    }
  }

  val defaults: List[RuntimeBackend] = List(
    PTXRuntimeBackend,
    HSARuntimeBackend,
    SPIRVRuntimeBackend(Validation.Environment.OpenCLCompute),
    SPIRVRuntimeBackend(Validation.Environment.VulkanCompute, "spirv-vulkan-binary")
  )
}
