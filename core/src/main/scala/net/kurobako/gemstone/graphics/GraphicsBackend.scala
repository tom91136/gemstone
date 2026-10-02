package net.kurobako.gemstone.graphics

import GraphicsProtocol.*
import ShaderMetadata.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Shader preparation and rendering supplied by the application to an execution session. */
trait GraphicsBackend {
  def prepare(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, PreparedShader]
  def validate(submission: GraphicsSubmission, size: AllocationId => Option[Long]): Either[String, Set[AllocationId]]
  def validateDescriptorBindings(
      bindings: Vec[DescriptorBinding],
      argumentCount: Int,
      size: AllocationId => Option[Long]
  ): Either[String, Set[AllocationId]]
  def withDescriptorBindings[A](
      bindings: Vec[DescriptorBinding],
      memory: Memory,
      control: TaskControl
  )(execute: (Memory, Vec[GraphicsRange], Vec[KernelTask.PointerRelocation]) => Either[ExecutionFailure, A])
      : Either[ExecutionFailure, A]
  def execute(
      submission: GraphicsSubmission,
      memory: Memory,
      control: TaskControl,
      handler: Event => Either[Fault, Unit]
  ): Either[ExecutionFailure, GraphicsStatistics]
}
