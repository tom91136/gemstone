package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.explorer.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Liveness
import net.kurobako.gemstone.flow.Loop
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.BlockAnalysis
import net.kurobako.gemstone.profile.Pipe
import net.kurobako.gemstone.profile.PipelineSimulator
import net.kurobako.gemstone.profile.StaticProfile
import net.kurobako.gemstone.vm.KernelResources
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Schedule

/** Cycles per iteration of the loop or block of an AMDGPU kernel that holds a window, as [[BlockAnalysis]] gives them
  * for the kernel with the window rewritten and laid out again. The launch is not run: one wave issues the region once
  * with every lane active, so a region that reaches memory or has more than one path is not costed.
  */
object AMDGPUBlockCost {

  /** What a dispatch decides and the code object does not hold. */
  case class Launch(workgroupLanes: Int > 0, dynamicShared: Long >= 0, placement: Option[PipelineSimulator.Placement])

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

  private val Lane         = raw"v(\d+)".r
  private val memoryTraits = Set("smrd", "vmem", "store", "waits-valu")
  private val relative     = Set("s_getpc_b64", "s_call_b64", "s_subvector_loop_begin", "s_subvector_loop_end")

  def of(image: Vec[Byte], function: String, targetName: String, launch: Launch): Either[String, AMDGPUBlockCost] =
    for {
      target       <- targetName.refine[Target].left.map(_.raw)
      architecture <- Architecture.named(targetName).filter(calibrated(_))
        .toRight(s"no calibrated pipeline model for $targetName")
      name      <- function.refine[NonEmpty[String]].left.map(_ => s"no AMDGPU kernel '$function'")
      original  <- static(image, function)
      parsed    <- each(original.text)(instruction(_))
      granule   <- AMDGPUModel.registerGranule(image, function)
      resources <- AMDGPUModel.staticResources(image, function, launch.workgroupLanes, launch.dynamicShared)
    } yield new AMDGPUBlockCost(image, name, target, architecture, launch, original, parsed, granule, resources)

  private def calibrated(architecture: Architecture): Boolean =
    (architecture.branchRedirect.map(_.source) ++ architecture.issueOrder.map(_.source) ++
      architecture.pipeLatency.values.map(_.source)).exists {
      case Architecture.Source.Probed(_, _, _, _) => true
      case _                                      => false
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

  private def innermost(graph: ControlFlowGraph, block: Int): Option[Loop] =
    Option.when(graph.reducible)(graph.loops.filter(_.body.contains(block)).minByOption(_.body.size)).flatten

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
    parsed: Vec[Instruction],
    granule: Int,
    resources: KernelResources
) {
  import AMDGPUBlockCost.*

  private def bytes(text: Vec[String]): Either[String, Vec[Byte]] =
    AMDGPUModel.assemble(text.mkString("", "\n", "\n"), Some(target.raw)).flatMap(Hex.parse(_))

  private def end(index: Int): Either[String, Int] =
    original.offsets.lift(index + 1).toRight(s"no instruction follows ${original.text(index)}")

  private def retargeted(index: Int, boundary: Int, shift: Int): Either[String, String] = {
    val instruction = parsed(index)
    if (relative(instruction.opcode.raw))
      Left(s"${original.text(index)} is relative to the program counter, so the kernel is not laid out again")
    else if (!branch(instruction)) Right(original.text(index))
    else
      instruction.operands match {
        case Vec(Operand.IntegerLiteral(value, _)) =>
          def moved(offset: Int) = if (offset >= boundary) offset + shift else offset
          val from               = original.offsets(index)
          val to                 = from + 4 + 4 * value.toShort
          val words              = (moved(to) - moved(from) - 4) / 4
          Instruction.decode(instruction.definition, Vec(Operand.IntegerLiteral(BigInt(words), 10)), instruction.mods)
            .map(Printer.render(_)).left.map(reason => s"${original.text(index)}: $reason")
        case _ => Left(s"${original.text(index)} has no literal target")
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
    text <-
      if (shift == 0) Right(original.text.patch(window.first, replacement, length))
      else
        each(Vec.from(original.text.indices))(retargeted(_, boundary, shift))
          .map(_.patch(window.first, replacement, length))
    code     <- bytes(text)
    replaced <- CodeObject.decode(image).flatMap(_.replaceKernel(function, code)).left.map(_.message.raw)
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

  private def memory(text: String): Either[String, Unit] = instruction(text).flatMap { parsed =>
    val lds = parsed.mods.exists(_.name == "lds")
    Either.cond(
      !Scheduling.timing(target, parsed.opcode.raw, lds).exists(_.traits.exists(memoryTraits)),
      (),
      s"no recorded memory traffic for $text"
    )
  }

  private def costed(laid: Static, anchor: Int, shift: Int): Either[String, Cost] = {
    val graph  = laid.graph
    val block  = graph.blocks.indexWhere(block => block.first <= anchor && anchor <= block.last)
    val loop   = innermost(graph, block)
    val blocks = loop.fold(Vec(block))(_.body.sorted)
    val header = loop.fold(block)(_.header)
    val kind   = loop.fold(BlockAnalysis.Kind.Block)(_ => BlockAnalysis.Kind.Loop)
    val order  = blocks.flatMap(index => Vec.from(graph.blocks(index).first to graph.blocks(index).last))
    val inside = blocks.toSet
    val paths  = loop.nonEmpty && blocks.exists(index => graph.blocks(index).successors.count(inside) != 1)
    val label  = s"$function/${kind.name}@${BlockAnalysis.hex(laid.offsets(graph.blocks(header).first).toLong)}"
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
    val registers = resources.registersPerLane.map(count => (count.raw + grown).max(0).unsafe[Int >= 0])
    for {
      _        <- Either.cond(!paths, (), s"the loop at $label has more than one path and no recorded issue counts")
      _        <- each(order.map(laid.text(_)))(memory(_))
      stream   <- StaticProfile.stream(function, graph, rows, order, resources.copy(registersPerLane = registers))
      analysis <- BlockAnalysis.of(stream, architecture, launch.placement)
      regions  <- analysis.select(Some(label))
      timing   <- regions.head.status match {
        case BlockAnalysis.Status.Executed(timing) => Right(timing)
        case BlockAnalysis.Status.Withheld(reason) => Left(s"$label is withheld: $reason")
        case BlockAnalysis.Status.NotExecuted      => Left(s"$label is not executed")
      }
    } yield Cost(timing.block.cyclesPerIteration, timing.block.source, shift)
  }

  /** The cost of the region holding `window` once `replacement` stands in the window's place. A region that the
    * replacement empties costs nothing.
    */
  def cost(window: Window, replacement: Vec[String]): Either[String, Cost] = rewritten(window, replacement).flatMap {
    (laid, shift) =>
      val graph   = original.graph
      val home    = graph.blocks(window.block)
      val members = innermost(graph, window.block).fold(Vec(window.block))(_.body.sorted).flatMap(index =>
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
      )(costed(laid, _, shift))
  }
}
