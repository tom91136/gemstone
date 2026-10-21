package net.kurobako.gemstone.graphics

import GraphicsProtocol.*
import ShaderMetadata.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Shader preparation and rendering supplied by the application to an execution session. */
trait GraphicsBackend {
  def prepare(shader: ShaderStage): Either[Fault, PreparedShader]
  def validate(submission: GraphicsSubmission, size: AllocationId => Option[Int > 0]): Either[String, Set[AllocationId]]
  def validateDescriptorBindings(
      bindings: Vec[DescriptorBinding],
      argumentCount: Int >= 0,
      size: AllocationId => Option[Int > 0]
  ): Either[String, Set[AllocationId]]
  def withDescriptorBindings[A](
      bindings: Vec[DescriptorBinding],
      memory: AddressSpace,
      control: TaskControl
  )(execute: (AddressSpace, Vec[GraphicsRange], Vec[KernelTask.PointerRelocation]) => Either[Fault, A])
      : Either[Fault, A]
  def execute(submission: GraphicsSubmission, context: TaskContext): Either[Fault, GraphicsStatistics]
}
