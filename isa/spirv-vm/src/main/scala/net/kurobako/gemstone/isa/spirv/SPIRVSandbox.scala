package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.spirv.asm.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable

/** Runs small SPIR-V modules through the real loader, op table and interpreter, recording the invocation state the
  * explorer sandbox shows: program counter, label, predecessor and call depth at every issue, the transitions that park
  * and resume invocations, and the final memory. It is deliberately not a second interpreter.
  */
object SPIRVSandbox {

  /** How much memory an allocation parameter gets before its initial bytes grow it. */
  val AllocationBytes: Long > 0 = (64L * 1024L).unsafe[Long > 0]

  /** One issue as an observer saw it. `values` are the result ids the module declares, in id order. */
  case class Step(
      issued: Long,
      function: Int,
      pc: Int,
      label: Int,
      predecessor: Int,
      callDepth: Int,
      ordinal: Int,
      values: Vec[(Int, InvocationView.Value)]
  )

  /** One launch: its issues, its parking and resuming transitions, final register values and memory, and the fault that
    * ended it, if any. `allocations` names the memory arguments in parameter order.
    */
  case class Result(
      steps: Vec[Step],
      transitions: Vec[MachineEvent],
      values: Vec[(Int, InvocationView.Value)],
      memory: ArrayMemory,
      allocations: Vec[AllocationId],
      failure: Option[Fault],
      complete: Boolean
  ) {
    def pcs: Vec[Int] = steps.map(_.pc)

    def bytes(index: Int): Vec[Byte] = allocations.lift(index).fold(Vec.empty[Byte])(memory.bytes)

    def blocked: Vec[MachineEvent] = transitions.filter(_.kind match {
      case MachineEvent.Kind.Blocked(_) => true
      case _                            => false
    })

    def resumed: Vec[MachineEvent] = transitions.filter(_.kind == MachineEvent.Kind.Resumed)
  }

  /** Assemble and run `source` as `entry`. `allocations` and `scalars` are the initial bytes of the kernel's parameters
    * in order; an allocation the caller omits starts zeroed.
    */
  def run(
      source: String,
      entry: String,
      allocations: Vec[Vec[Byte]] = Vec.empty,
      scalars: Vec[Vec[Byte]] = Vec.empty,
      group: Dimensions = Dimensions.One,
      count: Dimensions = Dimensions.One,
      environment: Validation.Environment = Validation.Environment.OpenCLCompute
  ): Either[Fault, Result] =
    for {
      module  <- Parser.parse(source).left.map(error => invalid(error.diagnostic.raw))
      image   <- ISA.encode(module).left.map(error => invalid(error.message.raw))
      program <- load(module, image, environment)
      result  <- try runLoaded(program, module, entry, allocations, scalars, group, count)
      finally program.close()
    } yield result

  private def invalid(message: String): Fault =
    Fault.InvalidRequest(message.refine[NonEmpty[String]].fold(_ => "SPIR-V sandbox error", identity))

  private def load(
      module: Module,
      image: Vec[Byte],
      environment: Validation.Environment
  ): Either[Fault, Backend.Program] = environment match {
    case Validation.Environment.OpenCLCompute | Validation.Environment.VulkanCompute =>
      SPIRVBackend(environment).load(image)
    case _ => LoadedProgram.load(module, environment).map(program => program: Backend.Program)
  }

  private def runLoaded(
      program: Backend.Program,
      module: Module,
      entry: String,
      allocations: Vec[Vec[Byte]],
      scalars: Vec[Vec[Byte]],
      group: Dimensions,
      count: Dimensions
  ): Either[Fault, Result] =
    for {
      kernel    <- entry.refine[NonEmpty[String]].left.map(message => Fault.InvalidRequest(message))
      metadata  <- program.kernel(kernel)
      arguments <- arguments(metadata, allocations, scalars)
      task      <- KernelTask.validated(kernel, group, count, arguments._1)
        .left.map(detail => invalid(detail.raw))
      recorder = Recorder(resultIds(module))
      outcome = program.execute(task, TaskContext(arguments._2, TaskControl(observer = Some(recorder)), _ => Right(())))
    } yield Result(
      steps = Vec.from(recorder.steps),
      transitions = Vec.from(recorder.transitions),
      values = Vec.from(recorder.steps).lastOption.fold(Vec.empty[(Int, InvocationView.Value)])(_.values),
      memory = arguments._2,
      allocations = arguments._3,
      failure = outcome.left.toOption,
      complete = outcome.isRight
    )

  /** The kernel's arguments and the memory they name, with initial bytes in place. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def arguments(
      metadata: KernelMetadata,
      allocations: Vec[Vec[Byte]],
      scalars: Vec[Vec[Byte]]
  ): Either[Fault, (Vec[KernelTask.Argument], ArrayMemory, Vec[AllocationId])] = {
    val memory     = ArrayMemory()
    val values     = Vec.newBuilder[KernelTask.Argument]
    val regions    = Vec.newBuilder[AllocationId]
    var allocation = 0
    var scalar     = 0
    var index      = 0
    var failure    = Option.empty[Fault]
    while (index < metadata.parameters.size && failure.isEmpty) {
      val parameter = metadata.parameters(index)
      parameter.kind match {
        case KernelMetadata.ParameterKind.Allocation =>
          val initial = allocations.lift(allocation).getOrElse(Vec.empty[Byte])
          val size    = math.max(AllocationBytes.raw, initial.size.toLong).unsafe[Long > 0]
          val region  = memory.allocate(size)
          if (initial.nonEmpty)
            memory.write(region, 0L, initial) match {
              case Left(fault) => failure = Some(fault)
              case Right(_)    => ()
            }
          values += KernelTask.Argument.Memory(region, 0L, Some(size))
          regions += region
          allocation += 1
        case KernelMetadata.ParameterKind.Local =>
          values += KernelTask.Argument.Local(parameter.width.widen[Long > 0])
        case KernelMetadata.ParameterKind.Scalar =>
          val bytes = scalars.lift(scalar).getOrElse(Vec.empty[Byte])
          val width = parameter.width.raw
          if (bytes.size > width)
            failure = Some(Fault.InvalidRequest(s"scalar parameter $scalar holds $width bytes, received ${bytes.size}"))
          else {
            values += KernelTask.Argument.Scalar(KernelTask.zeroExtended(bytes, width).unsafe[NonEmpty[Vec[Byte]]])
            scalar += 1
          }
      }
      index += 1
    }
    failure match {
      case Some(fault)                           => Left(fault)
      case None if allocation < allocations.size =>
        Left(Fault.InvalidRequest(
          s"kernel takes $allocation allocation parameter(s), received ${allocations.size}"
        ))
      case None if scalar < scalars.size =>
        Left(Fault.InvalidRequest(s"kernel takes $scalar scalar parameter(s), received ${scalars.size}"))
      case None => Right((values.result(), memory, regions.result()))
    }
  }

  /** Every id the module binds a result to, so observation never scans `idBound`. */
  private def resultIds(module: Module): Set[Int] =
    module.instructions.flatMap { instruction =>
      val shapes = instruction.definition.shapes
      if (shapes.size >= 2 && shapes(0).kind == OperandKind.IdResultType && shapes(1).kind == OperandKind.IdResult)
        Some(Operand.id(instruction.operands(1)))
      else if (shapes.headOption.exists(_.kind == OperandKind.IdResult))
        Some(Operand.id(instruction.operands.head))
      else None
    }.toSet

  private object Recorder {
    def apply(resultIds: Set[Int]): Recorder = new Recorder(resultIds)
  }

  private class Recorder(resultIds: Set[Int]) extends LaunchObserver {
    val steps: mutable.ArrayBuffer[Step]               = mutable.ArrayBuffer.empty
    val transitions: mutable.ArrayBuffer[MachineEvent] = mutable.ArrayBuffer.empty

    def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      state match {
        case view: InvocationView =>
          val values = Vec.from(resultIds.toVector.sorted.flatMap(id => view.value(id).map(value => id -> value)))
          steps += Step(
            state.issued,
            view.function,
            view.pc,
            view.label,
            view.predecessor,
            view.callDepth,
            view.unit.ordinal.raw,
            values
          )
        case _ => ()
      }
      val _ = at
      Right(())
    }

    override def transition(event: MachineEvent): Unit =
      transitions += event
  }
}
