package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.flow.Window
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.profile.Architecture
import net.kurobako.gemstone.profile.StaticProfile
import net.kurobako.gemstone.vm.KernelResources
import net.kurobako.gemstone.vm.RegisterUse
import net.kurobako.gemstone.vm.Workgroups
import scala.collection.mutable

/** Cycles per iteration of the loop or block of an SASS kernel that holds a window, as
  * [[net.kurobako.gemstone.profile.BlockAnalysis]] gives them for the kernel with the window rewritten, laid out again,
  * and its control words regenerated. The rewritten kernel is not run. One warp issues the region once with every lane
  * active, so a region that reaches memory or has more than one path is not costed. The unmodified kernel is costed the
  * same way, so a window's cost before and after a rewrite differ by the rewrite alone.
  */
object SASSBlockCost {

  private val Granule = 8
  private val Lane    = raw"R(\d+)".r

  private[check] case class Static(
      graph: ControlFlowGraph,
      instructions: Vec[DecodedInstruction],
      uses: Vec[RegisterUse],
      timing: Vec[StaticTiming.Row]
  )

  def of(
      image: Vec[Byte],
      function: String,
      targetName: String,
      launch: AMDGPUBlockCost.Launch
  ): Either[String, SASSBlockCost] = for {
    target       <- Target.parse(targetName)
    architecture <- Architecture.named(targetName).filter(BlockRegion.calibrated(_))
      .toRight(s"no calibrated pipeline model for $targetName")
    name      <- function.refine[NonEmpty[String]].left.map(_ => s"no SASS kernel '$function'")
    module    <- Cubin.decode(image).left.map(_.raw)
    table     <- ControlWords.tables.get(s"sm_${module.sm}").toRight(s"no control word table for sm_${module.sm}")
    original  <- load(module, name, Map.empty)
    resources <- launch match {
      case AMDGPUBlockCost.Launch.Stated(lanes, shared, _) => staticResources(module, name, lanes, shared)
      case AMDGPUBlockCost.Launch.Recorded(stream, _)      =>
        val offsets = original.timing.map(_.offset)
        for {
          _ <- Either.cond(
            stream.graphs.exists(graph => graph.kernel == function && graph.offsets == offsets),
            (),
            s"the recording is not a launch of this $function"
          )
          resources <- stream.resources.toRight("the recording holds no launch")
        } yield resources
    }
  } yield new SASSBlockCost(module, name, target, architecture, table, launch, original, resources)

  private def staticResources(
      module: Cubin.Module,
      name: NonEmpty[String],
      lanes: Int > 0,
      shared: Long >= 0
  ): Either[String, KernelResources] = {
    val program = LoadedProgram(module, Map.empty, Workgroups.Sequential, _ => None)
    try program.staticResources(name, lanes, shared).left.map(_.message.raw)
    finally program.close()
  }

  private def load(
      module: Cubin.Module,
      name: NonEmpty[String],
      decoded: Map[NonEmpty[String], Vec[DecodedFunction]]
  ): Either[String, Static] = {
    val program =
      LoadedProgram(module, decoded, Workgroups.Sequential, instruction => Printer.print(instruction).toOption)
    try for {
      code   <- program.staticCode(name).left.map(_.message.raw)
      timing <- program.staticTiming(name).left.map(_.message.raw)
    } yield Static(code._1, code._3, code._2, timing)
    finally program.close()
  }

  private def registers(uses: Vec[RegisterUse]): Int = {
    val highest = uses.flatMap(use => use.reads ++ use.writes).collect { case Lane(index) => index.toInt + 1 }.maxOption
    (highest.getOrElse(0) + Granule - 1) / Granule * Granule
  }
}

final class SASSBlockCost private (
    module: Cubin.Module,
    name: NonEmpty[String],
    target: Target,
    architecture: Architecture,
    table: ControlWords.Table,
    launch: AMDGPUBlockCost.Launch,
    original: SASSBlockCost.Static,
    resources: KernelResources
) {
  import SASSBlockCost.*

  private val assembler    = new Assembler(target)
  private val disassembler = new Disassembler(target)

  private def text(instruction: DecodedInstruction): String =
    Printer.print(instruction.operation).getOrElse(instruction.mnemonic.raw)

  private def code(static: Static): RecordedIssues.Code =
    RecordedIssues.Code(
      static.instructions.map(text),
      static.timing.map(_.offset.toInt),
      static.uses,
      index => Right(static.timing(index).memory)
    )

  /** The kernel with `window` replaced, laid out again by the assembler. */
  private def relaid(window: Window, replacement: Vec[DecodedInstruction]): Either[String, Vec[DecodedInstruction]] = {
    val code = original.instructions
    val held = mutable.ArrayBuffer.from(code.slice(window.first, window.last + 1))
    def kept(instruction: DecodedInstruction): Option[DecodedInstruction] =
      instruction.encodingWord.flatMap(_ =>
        held.indexWhere(source =>
          source.encodingWord == instruction.encodingWord && source.operation == instruction.operation
        ) match {
          case -1    => None
          case index => Some(held.remove(index))
        }
      )
    def operation(instruction: DecodedInstruction, preserved: Option[DecodedInstruction]): Program.Statement =
      Program.Statement.Operation(instruction.operation, preserved)
    val fresh   = replacement.map(instruction => operation(instruction, kept(instruction)))
    val program = Program(
      Some(target),
      code.slice(0, window.first).map(instruction => operation(instruction, Some(instruction))) ++ fresh ++
        code.drop(window.last + 1).map(instruction => operation(instruction, Some(instruction)))
    )
    for {
      moved   <- assembler.relaid(program).left.map(_.toString)
      bytes   <- assembler.encode(moved).left.map(_.toString)
      decoded <- disassembler.decode(bytes).left.map(_.toString)
      found = decoded.statements.collect { case Program.Statement.Operation(_, Some(instruction), _) => instruction }
      _ <- Either.cond(
        found.size == code.size - (window.last - window.first + 1) + replacement.size,
        (),
        "the rewritten kernel does not keep its instruction count"
      )
    } yield found
  }

  /** `instructions` with the control words of every path regenerated, and the static view that gives. */
  private def scheduled(instructions: Vec[DecodedInstruction]): Either[String, Static] = {
    val function = Map(name -> Vec(DecodedFunction(name.raw, instructions)))
    load(module, name, function).flatMap { laid =>
      val schedule = mutable.HashMap.empty[Int, Scheduling]
      val failure  = ControlWords.paths(laid.graph, laid.uses, laid.instructions).iterator.map { path =>
        ControlWords.regenerate(table, path).map(words =>
          words.zipWithIndex.foreach((word, index) => schedule(path.first + index) = word)
        )
      }.collectFirst { case Left(reason) => reason }
      failure.toLeft(()).flatMap { _ =>
        val regenerated = laid.instructions.zipWithIndex.map((instruction, index) =>
          instruction.copy(scheduling = schedule.get(index).orElse(instruction.scheduling))
        )
        load(module, name, Map(name -> Vec(DecodedFunction(name.raw, regenerated))))
      }
    }
  }

  private def costed(
      laid: Static,
      anchor: Int,
      shift: Int,
      window: Window,
      count: Int
  ): Either[String, AMDGPUBlockCost.Cost] = {
    val offsets = laid.timing.map(_.offset.toInt)
    val region  = BlockRegion.of(name.raw, laid.graph, offsets, anchor)
    val rows    = Vec.tabulate(laid.instructions.size)(index =>
      StaticProfile.Row(
        laid.timing(index).offset,
        text(laid.instructions(index)),
        laid.timing(index).pipe,
        laid.timing(index).schedule,
        laid.uses(index)
      )
    )
    val grown    = registers(laid.uses) - registers(original.uses)
    val occupied = resources.copy(registersPerLane =
      resources.registersPerLane.map(count => (count.raw + grown).max(0).unsafe[Int >= 0])
    )
    val issued = launch match {
      case AMDGPUBlockCost.Launch.Stated(_, _, _) =>
        for {
          _ <- Either.cond(
            !region.paths,
            (),
            s"the loop at ${region.label} has more than one path and no recorded issue counts"
          )
          _ <- region.order.find(index => laid.timing(index).memory).toLeft(()).left.map(index =>
            s"${rows(index).text} reaches memory, and a stated launch records no traffic"
          )
          stream <- StaticProfile.stream(name, laid.graph, rows, region.order, occupied)
        } yield stream
      case AMDGPUBlockCost.Launch.Recorded(recorded, _) =>
        RecordedIssues.of(window, count, code(original), code(laid), Set.empty)
          .flatMap(StaticProfile.replay(recorded, name, laid.graph, rows, _, occupied))
    }
    for {
      stream <- issued
      timing <- BlockRegion.timing(stream, architecture, launch.placement, region.label)
    } yield AMDGPUBlockCost.Cost(timing.block.cyclesPerIteration, timing.block.source, shift, timing.block.noise)
  }

  /** The cost of the region holding `window` once `replacement` stands in the window's place. A region that the
    * replacement empties costs nothing.
    */
  def cost(window: Window, replacement: Vec[DecodedInstruction]): Either[String, AMDGPUBlockCost.Cost] = {
    val source  = original
    val length  = window.last - window.first + 1
    val graph   = source.graph
    val home    = graph.blocks(window.block)
    val members = BlockRegion.innermost(graph, window.block).fold(Vec(window.block))(_.body.sorted).flatMap(index =>
      Vec.from(graph.blocks(index).first to graph.blocks(index).last)
    )
    val outside = (Vec.from(home.first to home.last) ++ members).filterNot(index =>
      window.first <= index && index <= window.last
    )
    val moved  = replacement.size - length
    val anchor =
      if (replacement.nonEmpty) Some(window.first)
      else outside.headOption.map(index => if (index > window.last) index + moved else index)
    anchor.fold(
      cost(window, source.instructions.slice(window.first, window.last + 1)).map(whole =>
        AMDGPUBlockCost.Cost(0.0, whole.source, moved * 16)
      )
    ) { at =>
      for {
        code  <- relaid(window, replacement)
        laid  <- scheduled(code)
        found <- costed(laid, at, moved * 16, window, replacement.size)
      } yield found
    }
  }
}
