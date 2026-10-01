package net.kurobako.gemstone.isa.spirv.v1_6.graphics

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.graphics.GraphicsProtocol.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** A graphics submission is one ordered, cancellable completion, including all of its shader work. */
object GraphicsRuntime {
  def validate(submission: GraphicsSubmission, size: AllocationId => Option[Long]): Either[String, Set[AllocationId]] =
    GraphicsValidation.submission(submission, size)
  def validateDescriptorBindings(
      bindings: Vec[DescriptorBinding],
      argumentCount: Int,
      size: AllocationId => Option[Long]
  ): Either[String, Set[AllocationId]] =
    GraphicsValidation.descriptorBindings(bindings, argumentCount, size)

  def withDescriptorBindings[A](
      bindings: Vec[DescriptorBinding],
      memory: Memory,
      control: TaskControl
  )(execute: (Memory, Vec[GraphicsRange], Vec[KernelTask.PointerRelocation]) => Either[Fault, A]): Either[Fault, A] =
    GraphicsAccess.scoped(memory, control) { access =>
      access.prepare(
        bindings.view.flatMap(_.descriptor.image).filter(_.format.compression != GraphicsCompression.None).map(_.memory)
      )
      val images      = ShaderDescriptors(access)
      val descriptors = bindings.map(images.prepare(_))
      execute(access, descriptors, images.relocations)
    }
  def execute(
      submission: GraphicsSubmission,
      memory: Memory,
      control: TaskControl,
      workgroups: Workgroups,
      handler: Event => Either[Fault, Unit]
  ): Either[Fault, GraphicsStatistics] =
    GraphicsAccess.scoped(memory, control) { access =>
      val images = ImageOperations(access)
      val draws  = DrawExecution(access, workgroups, handler)
      val ranges = Vector.newBuilder[GraphicsRange]
      submission.commands.foreach(command =>
        GraphicsResources.foreach(command)(
          range => { val _ = ranges.addOne(range) },
          _ => (),
          _ => ()
        )
      )
      access.prepare(ranges.result())
      def executeCommand(command: GraphicsCommand): Long = command match {
        case GraphicsCommand.ClearColor(image, area, bits) => images.clearColor(image, area, bits.toArray); 0L
        case GraphicsCommand.ClearDepthStencil(image, area, depth, stencil, d, s) =>
          images.clearDepthStencil(image, area, depth, stencil, d, s); 0L
        case GraphicsCommand.CopyImage(source, destination, regions) =>
          regions.foreach(images.copy(source, destination, _)); 0L
        case GraphicsCommand.CopyBufferImage(buffer, image, regions, toImage) =>
          regions.foreach(images.copyBuffer(buffer, image, _, toImage)); 0L
        case GraphicsCommand.Resolve(source, destination, regions) =>
          regions.foreach(images.resolve(source, destination, _)); 0L
        case GraphicsCommand.ResolveAttachment(source, destination, area, depth, stencil) =>
          images.resolveAttachment(source, destination, area, depth, stencil); 0L
        case GraphicsCommand.Draw(draw)                                 => draws.execute(draw)
        case GraphicsCommand.Blit(source, destination, regions, linear) =>
          regions.foreach(images.blit(source, destination, _, linear)); 0L
      }
      val samples = submission.commands.foldLeft(0L)((sum, command) => Math.addExact(sum, executeCommand(command)))
      Right(GraphicsStatistics(samples.unsafe[Long >= 0]))
    }
}
