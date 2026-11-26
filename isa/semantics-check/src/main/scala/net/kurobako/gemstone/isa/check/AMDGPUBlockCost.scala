package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.ProfileStream
import net.kurobako.gemstone.profile.StaticProfile
import net.kurobako.gemstone.vm.KernelResources
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Schedule

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
  case class Cost(cycles: Double, source: Architecture.Source, shift: Int, noise: Double = 0.0)

  private case class Static(
      graph: ControlFlowGraph,
      text: Vec[String],
      offsets: Vec[Int],
      pipes: Vec[Option[Pipe]],
      schedules: Vec[Option[Schedule]],
      uses: Vec[RegisterUse]
  )

  private val Lane         = raw"v(\d+)".r
  private val memoryTraits = Set("smrd", "vmem", "store", "waits-valu")
  private val exec         = ImplicitState.units(ImplicitState.Exec, 64).toSet

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

  private def static(image: Vec[Byte], function: String): Either[String, Static] = for {
    code   <- AMDGPUModel.staticCode(image, function)
    timing <- AMDGPUModel.staticTiming(image, function)
  } yield Static(code._1, code._3, timing.map(_.offset), timing.map(_.pipe), timing.map(_.schedule), code._2)

  private def instruction(text: String): Either[String, Instruction] =
    Parser.parse(text).left.map(_.diagnostic.raw).flatMap(_.statements match {
      case Vec(Program.Statement.Instruction(value)) => Right(value)
      case _                                         => Left(s"not a single instruction: $text")
    })

  private case class Move(origin: Int, size: Int, start: Int, boundary: Int, shift: Int, base: Int) {
    def apply(address: Long): Long =
      if (address < origin || address >= origin + size) address
      else base + (if (address >= boundary) address + shift else address)
  }

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

  private def refused(index: Int): String =
    s"${original.text(index)} is relative to the program counter, so the kernel is not laid out again"

  private lazy val decoded: Vec[Option[Instruction]] = Vec.tabulate(original.text.size)(index =>
    instruction(original.text(index)).toOption.orElse(
      Binary.decodeStatementAt(kernel, start(index).unsafe[Int >= 0], TargetMode(target)).toOption.collect {
        case DecodedStatement(Program.Statement.Instruction(value), _) => value
      }
    )
  )

  private lazy val anchors: Either[String, Vec[AMDGPUAnchors.Anchor]] =
    AMDGPUAnchors.of(target, decoded, original.offsets, kernel.size, original.uses, original.graph).left.map(refused)

  private def reassembled(index: Int, operands: Vec[Operand]): Either[String, Vec[Byte]] =
    decoded(index).toRight(refused(index)).flatMap(value =>
      Instruction.decode(value.definition, operands, value.mods).left.map(_ => refused(index))
        .flatMap(changed => bytes(Vec(Printer.render(changed))))
        .filterOrElse(_.size == span(index).size, refused(index))
    )

  private def readdressed(index: Int, literal: Int): Either[String, Vec[Byte]] =
    if (span(index).size == 8)
      Right(span(index).take(4) ++ Vec.tabulate(4)(byte => (literal >>> (8 * byte)).toByte))
    else
      decoded(index).toRight(refused(index))
        .flatMap(value => reassembled(index, value.operands.init :+ Operand.IntegerLiteral(BigInt(literal), 10)))

  private def reanchored(window: Window, move: Move): Either[String, Map[Int, Vec[Byte]]] = anchors.flatMap { found =>
    def held(index: Int)                      = window.first <= index && index <= window.last
    def split(address: Long)                  = address > move.start && address < move.boundary
    def from(index: Int)                      = original.offsets(index).toLong
    def members(anchor: AMDGPUAnchors.Anchor) = anchor match {
      case AMDGPUAnchors.Anchor.Label(at, _)              => Vec(at)
      case AMDGPUAnchors.Anchor.Address(at, low, high, _) => Vec(at, low, high)
    }
    val blocked = found.find(anchor => members(anchor).exists(held) || split(anchor.target))
    blocked.map(anchor => refused(anchor.at)).toLeft(()).flatMap(_ =>
      BlockRegion.each(found) { anchor =>
        val at       = anchor.at
        val before   = anchor.target - from(at) - 4
        val distance = move(anchor.target) - move(from(at)) - 4
        if (distance == before) Right(Vec.empty[(Int, Vec[Byte])])
        else
          anchor match {
            case AMDGPUAnchors.Anchor.Label(_, _) =>
              val words = distance / 4
              Either.cond(distance % 4 == 0 && words.toShort.toLong == words, words, refused(at)).flatMap(words =>
                decoded(at).toRight(refused(at)).flatMap(value =>
                  reassembled(at, value.operands.init :+ Operand.IntegerLiteral(BigInt(words), 10))
                )
              ).map(code => Vec(at -> code))
            case AMDGPUAnchors.Anchor.Address(_, low, high, _) =>
              // the high addition leaves its carry in SCC, which a displacement of the same sign keeps
              Either.cond((distance < 0) == (before < 0), (), refused(at)).flatMap(_ =>
                for {
                  lower <- readdressed(low, distance.toInt)
                  upper <- readdressed(high, (distance >> 32).toInt)
                } yield Vec(low -> lower, high -> upper)
              )
          }
      }.map(_.flatten.toMap)
    )
  }

  private def placement(size: Int): Either[String, Int] = {
    def at(code: CodeObject) = code.executableSections.left.map(_.message.raw).flatMap(sections =>
      sections.flatMap(_.symbols).find(_.name.raw == function.raw).map(_.offset.raw)
        .toRight(s"no code symbol for $function")
    )
    for {
      code    <- CodeObject.decode(image).left.map(_.message.raw)
      before  <- at(code)
      changed <- code.replaceKernel(function, Vec.fill(size)(0.toByte)).left.map(_.message.raw)
      after   <- at(changed)
    } yield after - before
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
    changed <-
      if (shift == 0) Right(Map.empty[Int, Vec[Byte]])
      else
        placement(kernel.size + shift).flatMap(base =>
          reanchored(
            window,
            Move(original.offsets(0), kernel.size, original.offsets(window.first), boundary, shift, base)
          )
        )
    pieces = Vec.tabulate(original.text.size)(index =>
      if (index == window.first) after
      else if (window.first < index && index <= window.last) Vec.empty[Byte]
      else changed.getOrElse(index, span(index))
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

  private[check] def laidOut(window: Window, replacement: Vec[String]): Either[String, (Vec[Int], Vec[String])] =
    rewritten(window, replacement).map((laid, _) => (laid.offsets, laid.text))

  private def code(static: Static): RecordedIssues.Code =
    RecordedIssues.Code(static.text, static.offsets, static.uses, index => reaches(static.text(index)))

  private def reaches(text: String): Either[String, Boolean] = instruction(text).map { parsed =>
    val lds = parsed.mods.exists(_.name == "lds")
    Scheduling.timing(target, parsed.opcode.raw, lds).exists(_.traits.exists(memoryTraits))
  }

  private def memory(text: String): Either[String, Unit] =
    reaches(text).flatMap(found => Either.cond(!found, (), s"no recorded memory traffic for $text"))

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
          _      <- BlockRegion.each(region.order.map(laid.text(_)))(memory(_))
          stream <- StaticProfile.stream(function, graph, rows, region.order, occupied)
        } yield stream
      case Launch.Recorded(recorded, _) =>
        RecordedIssues.of(window, count, code(original), code(laid), exec)
          .flatMap(StaticProfile.replay(recorded, function, graph, rows, _, occupied))
    }
    for {
      stream <- issued
      timing <- BlockRegion.timing(stream, architecture, launch.placement, region.label)
    } yield Cost(timing.block.cyclesPerIteration, timing.block.source, shift, timing.block.noise)
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
