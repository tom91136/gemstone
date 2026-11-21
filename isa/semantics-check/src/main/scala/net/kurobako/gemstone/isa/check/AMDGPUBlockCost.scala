package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.KernelMetadata
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.profile.StaticProfile
import net.kurobako.gemstone.vm.ArrayMemory
import net.kurobako.gemstone.vm.KernelResources
import net.kurobako.gemstone.vm.KernelTask
import net.kurobako.gemstone.vm.LaunchGeometry
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Schedule
import scala.util.Using

/** Cycles per iteration of the loop or block of an AMDGPU kernel that holds a window, as
  * [[net.kurobako.gemstone.profile.BlockAnalysis]] gives them for the kernel with the window rewritten and laid out
  * again. The rewritten kernel is not run. A stated launch has one wave issue the region once with every lane active,
  * so a region that reaches memory or has more than one path is not costed. A recorded launch of the unmodified kernel
  * is issued again over the rewritten one, which holds for a rewrite that leaves the window's live-out state as it was:
  * every instruction outside the window then issues, and reaches memory, as recorded.
  */
object AMDGPUBlockCost {

  /** What a dispatch decides and the code object does not hold. */
  enum Launch {
    def placement: Option[PipelineSimulator.Placement]

    case Stated(workgroupLanes: Int > 0, dynamicShared: Long >= 0, placement: Option[PipelineSimulator.Placement])

    /** A launch of the unmodified kernel as the VM recorded it. */
    case Recorded(stream: ProfileStream, placement: Option[PipelineSimulator.Placement])
  }

  /** `shift` is how many bytes the code after the window moves, which the cost of its own region does not include. */
  case class Cost(cycles: Double, source: Architecture.Source, shift: Int)

  private case class Static(
      graph: ControlFlowGraph,
      text: Vec[String],
      offsets: Vec[Int],
      pipes: Vec[Option[Pipe]],
      schedules: Vec[Option[Schedule]],
      uses: Vec[RegisterUse]
  )

  private enum Step {
    case Deleted(index: Int)
    case Inserted(index: Int)
    case Kept(index: Int)
  }

  private val Lane         = raw"v(\d+)".r
  private val memoryTraits = Set("smrd", "vmem", "store", "waits-valu")
  private val memoryUnit   = "memory"
  private val exec         = ImplicitState.units(ImplicitState.Exec, 64).toSet
  private val relative     = Set("s_getpc_b64", "s_call_b64", "s_subvector_loop_begin", "s_subvector_loop_end")

  def of(image: Vec[Byte], function: String, targetName: String, launch: Launch): Either[String, AMDGPUBlockCost] =
    for {
      target       <- targetName.refine[Target].left.map(_.raw)
      architecture <- Architecture.named(targetName).filter(BlockRegion.calibrated(_))
        .toRight(s"no calibrated pipeline model for $targetName")
      name      <- function.refine[NonEmpty[String]].left.map(_ => s"no AMDGPU kernel '$function'")
      original  <- static(image, function)
      kernel    <- CodeObject.decode(image).flatMap(_.kernelCode(name)).left.map(_.message.raw)
      granule   <- AMDGPUModel.registerGranule(image, function)
      resources <- launch match {
        case Launch.Stated(lanes, shared, _) => AMDGPUModel.staticResources(image, function, lanes, shared)
        case Launch.Recorded(stream, _)      =>
          val offsets = original.offsets.map(_.toLong)
          for {
            _ <- Either.cond(
              stream.graphs.exists(graph => graph.kernel == function && graph.offsets == offsets),
              (),
              s"the recording is not a launch of this $function"
            )
            resources <- stream.resources.toRight("the recording holds no launch")
          } yield resources
      }
    } yield new AMDGPUBlockCost(image, name, target, architecture, launch, original, kernel, granule, resources)

  /** The stream the VM records for `groups` workgroups of `lanes` lanes of `function`. `arguments` holds one value for
    * each parameter of the kernel: a scalar's value, or the size in bytes of a zeroed buffer of its own.
    */
  def record(
      image: Vec[Byte],
      function: String,
      lanes: Int > 0,
      groups: Int > 0,
      arguments: Vec[Long]
  ): Either[String, ProfileStream] = for {
    name   <- function.refine[NonEmpty[String]].left.map(_ => s"no AMDGPU kernel '$function'")
    loaded <- ProgramRun.load(ProgramListing.Image(Language.AMDGPU, image, None))
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
    arguments <- each(explicit.zip(values))(argument(memory, _, _))
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
      memory: ArrayMemory,
      parameter: KernelMetadata.Parameter,
      value: Long
  ): Either[String, KernelTask.Argument] = parameter.kind match {
    case KernelMetadata.ParameterKind.Scalar =>
      val bytes = Vec.tabulate(parameter.width.raw)(byte => if (byte < 8) (value >>> (8 * byte)).toByte else 0.toByte)
      Right(KernelTask.Argument.Scalar(bytes.unsafe[NonEmpty[Vec[Byte]]]))
    case KernelMetadata.ParameterKind.Allocation =>
      value.refine[Long > 0].left.map(_.raw).map(size =>
        KernelTask.Argument.Memory(memory.allocate(size), 0L, Some(size))
      )
    case KernelMetadata.ParameterKind.Local =>
      value.refine[Long > 0].left.map(_.raw).map(KernelTask.Argument.Local(_))
  }

  private def static(image: Vec[Byte], function: String): Either[String, Static] = for {
    code   <- AMDGPUModel.staticCode(image, function)
    timing <- AMDGPUModel.staticTiming(image, function)
  } yield Static(code._1, code._3, timing.map(_.offset), timing.map(_.pipe), timing.map(_.schedule), code._2)

  private def instruction(text: String): Either[String, Instruction] =
    Parser.parse(text).left.map(_.diagnostic.raw).flatMap(_.statements match {
      case Vec(Program.Statement.Instruction(value)) => Right(value)
      case _                                         => Left(s"not a single instruction: $text")
    })

  private def each[A, B](values: Vec[A])(f: A => Either[String, B]): Either[String, Vec[B]] = {
    val found = values.map(f(_))
    found.collectFirst { case Left(reason) => reason }.toLeft(found.collect { case Right(value) => value })
  }

  private def branch(instruction: Instruction): Boolean = {
    val mnemonic = instruction.opcode.raw
    mnemonic == "s_branch" || mnemonic.startsWith("s_cbranch_")
  }

  private def pcRelative(instruction: Instruction): Boolean = relative(instruction.opcode.raw) || branch(instruction)

  private def units(use: RegisterUse, names: Vec[String]): Set[String] =
    names.flatMap(name => Liveness.units(name, use.unitsOf(name))).toSet

  private def vectors(uses: Vec[RegisterUse]): Int =
    uses.flatMap(use => (use.reads ++ use.writes).flatMap(name => Liveness.units(name, use.unitsOf(name))))
      .collect { case Lane(index) => index.toInt + 1 }.maxOption.getOrElse(0)
}

final class AMDGPUBlockCost private (
    image: Vec[Byte],
    function: NonEmpty[String],
    target: Target,
    architecture: Architecture,
    launch: AMDGPUBlockCost.Launch,
    original: AMDGPUBlockCost.Static,
    kernel: Vec[Byte],
    granule: Int,
    resources: KernelResources
) {
  import AMDGPUBlockCost.*

  private def bytes(text: Vec[String]): Either[String, Vec[Byte]] =
    AMDGPUModel.assemble(text.mkString("", "\n", "\n"), Some(target.raw)).flatMap(Hex.parse(_))

  private def end(index: Int): Either[String, Int] =
    original.offsets.lift(index + 1).toRight(s"no instruction follows ${original.text(index)}")

  private def start(index: Int): Int = original.offsets(index) - original.offsets(0)

  private def span(index: Int): Vec[Byte] =
    kernel.slice(start(index), original.offsets.lift(index + 1).fold(kernel.size)(_ - original.offsets(0)))

  private def refused(index: Int): Either[String, Vec[Byte]] =
    Left(s"${original.text(index)} is relative to the program counter, so the kernel is not laid out again")

  private def retargeted(index: Int, instruction: Instruction, boundary: Int, shift: Int): Either[String, Vec[Byte]] =
    instruction.operands match {
      case Vec(Operand.IntegerLiteral(value, _)) =>
        def moved(offset: Int) = if (offset >= boundary) offset + shift else offset
        val from               = original.offsets(index)
        val to                 = from + 4 + 4 * value.toShort
        val words              = (moved(to) - moved(from) - 4) / 4
        Instruction.decode(instruction.definition, Vec(Operand.IntegerLiteral(BigInt(words), 10)), instruction.mods)
          .map(Printer.render(_)).left.map(reason => s"${original.text(index)}: $reason")
          .flatMap(text => bytes(Vec(text)))
      case _ => Left(s"${original.text(index)} has no literal target")
    }

  private def relocated(index: Int, boundary: Int, shift: Int): Either[String, Vec[Byte]] =
    if (shift == 0) Right(span(index))
    else
      instruction(original.text(index)) match {
        case Right(value) if branch(value)     => retargeted(index, value, boundary, shift)
        case Right(value) if pcRelative(value) => refused(index)
        case Right(_)                          => Right(span(index))
        case Left(_)                           =>
          Binary.decodeStatementAt(kernel, start(index).unsafe[Int >= 0], TargetMode(target)) match {
            case Right(DecodedStatement(Program.Statement.Instruction(value), _)) =>
              if (pcRelative(value)) refused(index) else Right(span(index))
            case _ => Left(s"${original.text(index)} does not decode, so the kernel is not laid out again")
          }
      }

  private def rewritten(window: Window, replacement: Vec[String]): Either[String, (Static, Int)] = for {
    boundary <- end(window.last)
    length = window.last - window.first + 1
    before <- bytes(original.text.slice(window.first, window.last + 1))
    after  <- if (replacement.isEmpty) Right(Vec.empty[Byte]) else bytes(replacement)
    shift = after.size - before.size
    _ <- Either.cond(
      before.size == boundary - original.offsets(window.first),
      (),
      s"the window ${window.first}..${window.last} does not reassemble to its own size"
    )
    pieces <- each(Vec.from(original.text.indices))(index =>
      if (index == window.first) Right(after)
      else if (window.first < index && index <= window.last) Right(Vec.empty[Byte])
      else relocated(index, boundary, shift)
    )
    replaced <- CodeObject.decode(image).flatMap(_.replaceKernel(function, pieces.flatten)).left.map(_.message.raw)
    laid     <- static(replaced.image, function.raw)
    base  = laid.offsets.headOption.getOrElse(0) - original.offsets.headOption.getOrElse(0)
    lines = architecture.fetchLine.fold(1L)(_.value.toLong)
    _ <- Either.cond(base % lines == 0L, (), s"the rewritten kernel moves $base bytes, off its place in a fetch line")
    expected = original.offsets.patch(window.first, Vec.empty[Int], length).map(offset =>
      base + (if (offset >= boundary) offset + shift else offset)
    )
    kept = laid.offsets.patch(window.first, Vec.empty[Int], replacement.size)
    _ <- Either.cond(kept == expected, (), "the rewritten kernel does not keep the layout its instruction sizes give")
  } yield (laid, shift)

  private def reaches(text: String): Either[String, Boolean] = instruction(text).map { parsed =>
    val lds = parsed.mods.exists(_.name == "lds")
    Scheduling.timing(target, parsed.opcode.raw, lds).exists(_.traits.exists(memoryTraits))
  }

  private def memory(text: String): Either[String, Unit] =
    reaches(text).flatMap(found => Either.cond(!found, (), s"no recorded memory traffic for $text"))

  /** What the rewrite may have changed by the end of `step`, given what it may have changed before. A register, or
    * memory, counts as changed once a deleted, changed or inserted instruction writes it, or a kept one that reads a
    * changed value; it stays changed, since the register use does not say whether a later write replaces it whole. A
    * kept instruction that reaches memory is held to the registers its address reads, not the value it stores.
    */
  private def changed(window: Window, laid: Static, before: Set[String], step: Step): Either[String, Set[String]] =
    step match {
      case Step.Deleted(index) =>
        val use = original.uses(index)
        reaches(original.text(index)).map(found => before ++ units(use, use.writes) ++ Option.when(found)(memoryUnit))
      case Step.Inserted(index) =>
        val text    = laid.text(index)
        val written = units(laid.uses(index), laid.uses(index).writes)
        for {
          _ <- memory(text)
          _ <- Either.cond(
            !written.exists(exec),
            (),
            s"$text writes the lane mask, so the recorded lanes do not hold after it"
          )
        } yield before ++ written
      case Step.Kept(index) =>
        val text = laid.text(index)
        val use  = laid.uses(index)
        reaches(text).flatMap { found =>
          val deciding = if (found && use.address.nonEmpty) use.address else use.reads
          val read     = (units(use, deciding) ++ Option.when(found)(memoryUnit)).intersect(before)
          if (read.isEmpty) Right(before)
          else if (found)
            Left(
              s"$text reads ${Vec.from(read).sorted.mkString(", ")}, which the rewrite changes in the window " +
                s"${window.first}..${window.last}, so its recorded memory traffic does not hold"
            )
          else Right(before ++ units(use, use.writes))
        }
    }

  /** Where each issue of the recorded launch goes once `count` instructions stand in the window's place in `laid`. An
    * instruction outside the window keeps its issues and accesses. A window is straight-line with no write of the lane
    * mask, so every pass issues each of its instructions once for the same lanes: an instruction the replacement keeps
    * takes its issues and accesses with it, a changed or inserted one that does not reach memory issues once a pass,
    * and a deleted one issues nothing. A changed or inserted instruction that reaches memory or writes the lane mask,
    * and a kept one that reaches memory after reading what the rewrite changed, have no recorded traffic that holds.
    */
  private def inherited(window: Window, count: Int, laid: Static): Either[String, Map[Long, StaticProfile.Site]] = {
    val length = window.last - window.first + 1
    val kept   = BlockRegion.common(
      original.text.slice(window.first, window.last + 1),
      laid.text.slice(window.first, window.first + count)
    )
    val bounds = Vec.from(((-1, -1) +: kept :+ (length, count)).sliding(2))
    val steps  = bounds.flatMap { pair =>
      val (fromLeft, fromRight) = pair(0)
      val (toLeft, toRight)     = pair(1)
      Vec.from(fromLeft + 1 until toLeft).map(index => Step.Deleted(window.first + index)) ++
        Vec.from(fromRight + 1 until toRight).map(index => Step.Inserted(window.first + index)) ++
        Option.when(toLeft < length)(Step.Kept(window.first + toRight))
    }
    val outside = Vec.from(original.offsets.indices).filterNot(index => window.first <= index && index <= window.last)
      .map(index =>
        original.offsets(index).toLong ->
          StaticProfile.Site(Vec(if (index > window.last) index + count - length else index), Some(0))
      )
    val placed = kept.zipWithIndex.map { case ((left, right), at) =>
      val from  = if (at == 0) 0 else kept(at - 1)._2 + 1
      val until = if (at == kept.size - 1) count else right + 1
      left -> StaticProfile.Site(Vec.from(from until until).map(window.first + _), Some(right - from))
    }.toMap
    val unkept = StaticProfile.Site(Vec.from(window.first until window.first + count), None)
    val inside = Vec.from(0 until length).map(index =>
      original.offsets(window.first + index).toLong -> placed.getOrElse(
        index,
        if (kept.isEmpty && index == 0) unkept else StaticProfile.Site(Vec.empty, None)
      )
    )
    steps.foldLeft[Either[String, Set[String]]](Right(Set.empty))((found, step) =>
      found.flatMap(changed(window, laid, _, step))
    ).map(_ => (outside ++ inside).toMap)
  }

  private def costed(laid: Static, anchor: Int, shift: Int, window: Window, count: Int): Either[String, Cost] = {
    val graph  = laid.graph
    val region = BlockRegion.of(function.raw, graph, laid.offsets, anchor)
    val rows   = Vec.tabulate(laid.text.size)(index =>
      StaticProfile.Row(
        laid.offsets(index).toLong,
        laid.text(index),
        laid.pipes(index),
        laid.schedules(index),
        laid.uses(index)
      )
    )
    val grown     = vectors(laid.uses).alignUp(granule) - vectors(original.uses).alignUp(granule)
    val registers = resources.registersPerLane.map(held => (held.raw + grown).max(0).unsafe[Int >= 0])
    val occupied  = resources.copy(registersPerLane = registers)
    val issued    = launch match {
      case Launch.Stated(_, _, _) =>
        for {
          _ <- Either.cond(
            !region.paths,
            (),
            s"the loop at ${region.label} has more than one path and no recorded issue counts"
          )
          _      <- each(region.order.map(laid.text(_)))(memory(_))
          stream <- StaticProfile.stream(function, graph, rows, region.order, occupied)
        } yield stream
      case Launch.Recorded(recorded, _) =>
        inherited(window, count, laid).flatMap(StaticProfile.replay(recorded, function, graph, rows, _, occupied))
    }
    for {
      stream <- issued
      timing <- BlockRegion.timing(stream, architecture, launch.placement, region.label)
    } yield Cost(timing.block.cyclesPerIteration, timing.block.source, shift)
  }

  /** The cost of the region holding `window` once `replacement` stands in the window's place. A region that the
    * replacement empties costs nothing.
    */
  def cost(window: Window, replacement: Vec[String]): Either[String, Cost] = rewritten(window, replacement).flatMap {
    (laid, shift) =>
      val graph   = original.graph
      val home    = graph.blocks(window.block)
      val members = BlockRegion.innermost(graph, window.block).fold(Vec(window.block))(_.body.sorted).flatMap(index =>
        Vec.from(graph.blocks(index).first to graph.blocks(index).last)
      )
      val outside = (Vec.from(home.first to home.last) ++ members).filterNot(index =>
        window.first <= index && index <= window.last
      )
      val moved  = replacement.size - (window.last - window.first + 1)
      val anchor =
        if (replacement.nonEmpty) Some(window.first)
        else outside.headOption.map(index => if (index > window.last) index + moved else index)
      anchor.fold(
        cost(window, original.text.slice(window.first, window.last + 1)).map(whole => Cost(0.0, whole.source, shift))
      )(costed(laid, _, shift, window, replacement.size))
  }
}
