package net.kurobako.gemstone.server.backend

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.graphics.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.graphics.ShaderMetadata.*
import net.kurobako.gemstone.isa.spirv.v1_6.*
import net.kurobako.gemstone.isa.spirv.v1_6.graphics.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*

class Graphics(workgroups: Workgroups = Workgroups.Sequential) extends GraphicsBackend {
  override def prepare(
      image: Vec[Byte],
      entry: NonEmpty[String],
      stage: ShaderStageKind,
      constants: Vec[SpecializationConstant]
  ): Either[ExecutionFailure, PreparedShader] =
    ShaderPreparation.prepare(image, entry, stage, constants.map(value => value.specId -> value.value).toMap)
      .left.map(ExecutionFailure.fromFault(_))
  override def validate(
      submission: GraphicsSubmission,
      size: AllocationId => Option[Long]
  ): Either[String, Set[AllocationId]] =
    GraphicsRuntime.validate(submission, size)
  override def validateDescriptorBindings(
      bindings: Vec[DescriptorBinding],
      argumentCount: Int,
      size: AllocationId => Option[Long]
  ): Either[String, Set[AllocationId]] = GraphicsRuntime.validateDescriptorBindings(bindings, argumentCount, size)
  override def withDescriptorBindings[A](
      bindings: Vec[DescriptorBinding],
      memory: Memory,
      control: TaskControl
  )(execute: (Memory, Vec[GraphicsRange], Vec[KernelTask.PointerRelocation]) => Either[ExecutionFailure, A])
      : Either[ExecutionFailure, A] =
    GraphicsRuntime.withDescriptorBindings(bindings, memory, control) { (prepared, ranges, relocations) =>
      Right(execute(prepared, ranges, relocations))
    }.left.map(ExecutionFailure.fromFault(_)).flatten
  override def execute(
      submission: GraphicsSubmission,
      memory: Memory,
      control: TaskControl,
      handler: Event => Either[Fault, Unit]
  ): Either[ExecutionFailure, GraphicsStatistics] =
    GraphicsRuntime.execute(submission, memory, control, workgroups, handler).left.map(ExecutionFailure.fromFault(_))
}
