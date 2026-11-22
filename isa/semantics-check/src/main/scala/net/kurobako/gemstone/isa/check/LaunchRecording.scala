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

  /** The stream the VM records for `groups` workgroups of `lanes` lanes of `function`. `arguments` holds one
    * [[LaunchArgument]] for each explicit parameter of the kernel. Where the code object says whether a parameter is a
    * pointer, the argument must agree; an eight-byte SASS parameter a cubin does not mark as a pointer takes either.
    */
  def record(
      language: Language,
      image: Vec[Byte],
      function: String,
      lanes: Int > 0,
      groups: Int > 0,
      arguments: Vec[LaunchArgument]
  ): Either[String, ProfileStream] = for {
    name   <- function.refine[NonEmpty[String]].left.map(_ => s"no $language kernel '$function'")
    loaded <- ProgramRun.load(ProgramListing.Image(language, image, None))
    stream <- Using.resource(loaded)(launched(_, name, lanes, groups, arguments))
  } yield stream

  /** The kernel arguments `values` bind to over `memory`. */
  def bind(
      language: Language,
      image: Vec[Byte],
      function: String,
      memory: ArrayMemory,
      values: Vec[LaunchArgument]
  ): Either[String, Vec[KernelTask.Argument]] = for {
    name   <- function.refine[NonEmpty[String]].left.map(_ => s"no $language kernel '$function'")
    loaded <- ProgramRun.load(ProgramListing.Image(language, image, None))
    bound  <- Using.resource(loaded)(bound(_, name, memory, values))
  } yield bound

  private def launched(
      loaded: ProgramRun.Loaded,
      name: NonEmpty[String],
      lanes: Int > 0,
      groups: Int > 0,
      values: Vec[LaunchArgument]
  ): Either[String, ProfileStream] = {
    val memory = ArrayMemory()
    for {
      arguments <- bound(loaded, name, memory, values)
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
  }

  private def bound(
      loaded: ProgramRun.Loaded,
      name: NonEmpty[String],
      memory: ArrayMemory,
      values: Vec[LaunchArgument]
  ): Either[String, Vec[KernelTask.Argument]] = for {
    metadata <- loaded.program.kernel(name).left.map(_.description.raw)
    explicit = metadata.parameters.take(metadata.explicitParameterCount)
    _ <- Either.cond(explicit.size == values.size, (), s"$name takes ${explicit.size} arguments, not ${values.size}")
    arguments <- BlockRegion.each(explicit.zip(values).zipWithIndex)((pair, index) =>
      argument(loaded.image.language, memory, pair._1, pair._2).left.map(error => s"argument $index of $name: $error")
    )
  } yield arguments

  private def argument(
      language: Language,
      memory: ArrayMemory,
      parameter: KernelMetadata.Parameter,
      value: LaunchArgument
  ): Either[String, KernelTask.Argument] = (parameter.kind, value) match {
    case (KernelMetadata.ParameterKind.Scalar, LaunchArgument.Scalar(element, number)) =>
      Either.cond(
        element.width == parameter.width.raw,
        KernelTask.Argument.Scalar(element.bytes(number).unsafe[NonEmpty[Vec[Byte]]]),
        s"a ${parameter.width.raw}-byte scalar does not take ${element.name}"
      )
    case (KernelMetadata.ParameterKind.Scalar, LaunchArgument.Buffer(size, fill))
        if language == Language.SASS && parameter.width.raw == 8 =>
      Right(buffer(memory, size, fill))
    case (KernelMetadata.ParameterKind.Scalar, _) =>
      Left(s"a ${parameter.width.raw}-byte scalar takes a value, not a buffer")
    case (KernelMetadata.ParameterKind.Allocation, LaunchArgument.Buffer(size, fill)) =>
      Right(buffer(memory, size, fill))
    case (KernelMetadata.ParameterKind.Allocation, _) => Left("a pointer takes a buffer")
    case (KernelMetadata.ParameterKind.Local, LaunchArgument.Buffer(size, LaunchArgument.Fill.Zero)) =>
      Right(KernelTask.Argument.Local(size))
    case (KernelMetadata.ParameterKind.Local, _) => Left("a local allocation takes an unfilled buffer")
  }

  private def buffer(memory: ArrayMemory, size: Long > 0, fill: LaunchArgument.Fill): KernelTask.Argument.Memory = {
    val region = memory.allocate(size)
    val bytes  = LaunchArgument.contents(size.raw.toInt, fill)
    System.arraycopy(bytes, 0, memory.array(region), 0, bytes.length)
    KernelTask.Argument.Memory(region, 0L, Some(size))
  }
}
