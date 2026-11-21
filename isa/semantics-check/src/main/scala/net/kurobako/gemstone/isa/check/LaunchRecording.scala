package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.vm.ArrayMemory
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.LaunchGeometry
import scala.util.Using

/** The launches a cost is read from. */
object LaunchRecording {

  /** The stream the VM records for `groups` workgroups of `lanes` lanes of `function`. `arguments` holds one value for
    * each parameter of the kernel: a scalar's value, or the size in bytes of a zeroed buffer. A cubin does not say
    * which eight-byte parameters are pointers, so each of those is a buffer.
    */
  def record(
      language: Language,
      image: Vec[Byte],
      function: String,
      lanes: Int > 0,
      groups: Int > 0,
      arguments: Vec[Long]
  ): Either[String, ProfileStream] = for {
    name   <- function.refine[NonEmpty[String]].left.map(_ => s"no $language kernel '$function'")
    loaded <- ProgramRun.load(ProgramListing.Image(language, image, None))
    stream <- Using.resource(loaded)(launched(_, name, lanes, groups, arguments))
  } yield stream

  private def launched(
      loaded: ProgramRun.Loaded,
      name: NonEmpty[String],
      lanes: Int > 0,
      groups: Int > 0,
      values: Vec[Long]
  ): Either[String, ProfileStream] = for {
    metadata <- loaded.program.kernel(name).left.map(_.description.raw)
    explicit = metadata.parameters.take(metadata.explicitParameterCount)
    _ <- Either.cond(explicit.size == values.size, (), s"$name takes ${explicit.size} arguments, not ${values.size}")
    memory = ArrayMemory()
    arguments <- BlockRegion.each(explicit.zip(values))(argument(loaded.image.language, memory, _, _))
    run       <- ProgramRun.runWithArguments(
      loaded,
      name,
      LaunchGeometry.Dimensions(lanes, 1, 1),
      LaunchGeometry.Dimensions(groups, 1, 1),
      memory,
      arguments
    )
    _ <- run.failure.map(fault => s"the launch of $name faults: ${fault.message.raw}").toLeft(())
  } yield run.stream

  private def argument(
      language: Language,
      memory: ArrayMemory,
      parameter: KernelMetadata.Parameter,
      value: Long
  ): Either[String, KernelTask.Argument] = parameter.kind match {
    case KernelMetadata.ParameterKind.Scalar if language != Language.SASS || parameter.width.raw != 8 =>
      val bytes = Vec.tabulate(parameter.width.raw)(byte => if (byte < 8) (value >>> (8 * byte)).toByte else 0.toByte)
      Right(KernelTask.Argument.Scalar(bytes.unsafe[NonEmpty[Vec[Byte]]]))
    case KernelMetadata.ParameterKind.Scalar | KernelMetadata.ParameterKind.Allocation =>
      value.refine[Long > 0].left.map(_.raw).map(size =>
        KernelTask.Argument.Memory(memory.allocate(size), 0L, Some(size))
      )
    case KernelMetadata.ParameterKind.Local =>
      value.refine[Long > 0].left.map(_.raw).map(KernelTask.Argument.Local(_))
  }
}
