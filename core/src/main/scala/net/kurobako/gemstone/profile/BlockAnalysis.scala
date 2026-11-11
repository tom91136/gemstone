package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.profile.PipelineSimulator.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*

object BlockAnalysis {

  enum Kind derives CanEqual {
    case Block, Loop

    def name: String = this match {
      case Block => "block"
      case Loop  => "loop"
    }
  }

  enum Status derives CanEqual {
    case NotExecuted
    case Withheld(reason: String)
    case Executed(timing: Timing)
  }

  case class Member(address: Long, text: Option[String], issues: Long)

  case class Timing(block: PipelineSimulator.Block, unknownPipes: Vec[Long])

  case class Region(label: String, kind: Kind, kernel: String, members: Vec[Member], status: Status)

  private type Counted = (ProfileStream.Instruction, Profile.Counters)
  private type Lookup  = Map[(String, Long), Counted]
  private type Slot    = (Long, Option[Counted])

  private case class Span(kind: Kind, header: Int, blocks: Vec[Int], latches: Vec[Int])

  def hex(address: Long): String = s"0x${java.lang.Long.toHexString(address)}"

  def of(stream: ProfileStream, architecture: Architecture): Either[String, BlockAnalysis] =
    if (stream.graphs.isEmpty) Left("stream has no control flow graphs, so there are no blocks to analyse")
    else {
      val profile        = Profiler.run(stream, architecture)
      val lookup: Lookup = profile.instructions.flatMap { case (instruction, counters) =>
        instruction.address.map(address => (instruction.kernel.raw, address.raw) -> (instruction, counters))
      }.toMap
      Right(BlockAnalysis(stream.graphs.flatMap(graph => regionsOf(profile, lookup, graph))))
    }

  private def regionsOf(profile: Profile, lookup: Lookup, graph: ProfileStream.Graph): Vec[Region] = {
    val cfg   = graph.graph
    val loops =
      if (cfg.reducible) cfg.loops.map(loop => Span(Kind.Loop, loop.header, loop.body.sorted, loop.latches))
      else Vec.empty[Span]
    val inLoop   = loops.flatMap(_.blocks).toSet
    val unlooped = cfg.blocks.indices.filterNot(inLoop.contains)
    val straight = Vec.from(unlooped.map(block => Span(Kind.Block, block, Vec(block), Vec.empty)))
    (loops ++ straight).sortBy(_.header).map(span => regionOf(profile, lookup, graph, span))
  }

  private def regionOf(profile: Profile, lookup: Lookup, graph: ProfileStream.Graph, span: Span): Region = {
    val cfg   = graph.graph
    val slots = span.blocks.flatMap(block => cfg.blocks(block).first to cfg.blocks(block).last).map { index =>
      val address = graph.offsets(index)
      (address, lookup.get((graph.kernel, address)))
    }
    val members = slots.map { case (address, entry) =>
      Member(address, entry.map(_._1.text), entry.fold(0L)(_._2.issues))
    }
    val closing        = span.latches.map(block => graph.offsets(cfg.blocks(block).last)).toSet
    val label          = s"${graph.kernel}/${span.kind.name}@${hex(graph.offsets(cfg.blocks(span.header).first))}"
    val status: Status =
      if (!members.exists(_.issues > 0L)) Status.NotExecuted
      else if (!cfg.reducible) Status.Withheld("irreducible control flow")
      else timing(profile, span.kind, slots, members, closing).fold(Status.Withheld(_), Status.Executed(_))
    Region(label, span.kind, graph.kernel, members, status)
  }

  private def timing(
      profile: Profile,
      kind: Kind,
      slots: Vec[Slot],
      members: Vec[Member],
      closing: Set[Long]
  ): Either[String, Timing] = {
    val lowered = slots.map { case (address, entry) =>
      opOf(profile.architecture, address, entry, closing.contains(address))
    }
    for {
      _     <- lowered.collectFirst { case Left(reason) => reason }.toLeft(())
      _     <- unevenAt(members).toLeft(())
      block <- simulated(profile, kind, lowered.collect { case Right(op) => op })
    } yield Timing(block, slots.zip(lowered).collect { case ((address, _), Right(op)) if op.pipe.isEmpty => address })
  }

  private def unevenAt(members: Vec[Member]): Option[String] =
    members.find(_.issues != members.head.issues).map(member => s"uneven issue counts at ${hex(member.address)}")

  private def simulated(profile: Profile, kind: Kind, ops: Vec[Op]): Either[String, PipelineSimulator.Block] = {
    val stream = kind match {
      case Kind.Block => PipelineSimulator.Stream.once(ops)
      case Kind.Loop  => PipelineSimulator.Stream.repeat(ops)
    }
    val block  = PipelineSimulator.simulate(profile.architecture, profile.occupancy, stream)
    val cycles = block.cyclesPerIteration
    Either.cond(cycles.isFinite && cycles > 0.0, block, "no finite positive cycle count")
  }

  private def opOf(
      architecture: Architecture,
      address: Long,
      entry: Option[Counted],
      backEdge: Boolean
  ): Either[String, Op] =
    entry match {
      case None                          => Left(s"no profile entry at ${hex(address)}")
      case Some((instruction, counters)) =>
        for {
          schedule <- instruction.schedule.toRight(s"no schedule at ${hex(address)}")
          latency  <- latencyOf(architecture, address, instruction, counters, schedule)
        } yield Op(
          instruction.pipe,
          lanesOf(counters),
          instruction.use.reads,
          instruction.use.writes,
          latency,
          1,
          Some(stallOf(schedule)),
          trafficOf(counters),
          backEdge
        )
    }

  private def trafficOf(counters: Profile.Counters): Traffic = {
    val memory = counters.memory.filterNot(_._1._1 == Space.Constant)
    val issues = math.max(1L, counters.issues).toDouble
    Traffic(
      sectors = memory.values.map(_.sectors).sum / issues,
      wavefronts = memory.values.map(_.wavefronts).sum / issues,
      loads = if (memory.keys.exists(_._2 == AccessKind.Read)) 1.0 else 0.0
    )
  }

  private def stallOf(schedule: Schedule): Architecture.Sourced[Int] = schedule match {
    case encoded: Schedule.Encoded =>
      val (from, basis) = ProfileReport.encodedPlanning
      Architecture.Sourced(encoded.plannedCycles, Architecture.Source.Inherited(from, basis))
    case estimated: Schedule.Estimated =>
      val (_, basis) = ProfileReport.modelledPlanning
      Architecture.Sourced(estimated.plannedCycles, Architecture.Source.Estimate(basis))
  }

  private def memoryTier(
      architecture: Architecture,
      counters: Profile.Counters
  ): Option[Architecture.Sourced[Double]] = {
    val spaces = counters.memory.keys.map(_._1).toSet - Space.Constant
    val tiers  = Vec(
      Option.when(spaces.contains(Space.Shared))(architecture.latency.shared),
      Option.when(spaces.contains(Space.Global))(architecture.latency.global._1)
    ).flatten
    tiers.maxByOption(_.value)
  }

  private def tableLatency(
      architecture: Architecture,
      instruction: ProfileStream.Instruction,
      counters: Profile.Counters
  ): Option[Architecture.Sourced[Int]] = {
    def whole(latency: Architecture.Sourced[Double]) =
      Architecture.Sourced(math.round(latency.value).toInt, latency.source)
    val producer = NvidiaLatency.producer(instruction.text)
    val sm       = NvidiaLatency.sm(architecture)
    memoryTier(architecture, counters).map(whole(_))
      .orElse(for {
        sm      <- sm
        p       <- producer
        latency <- NvidiaLatency.measured(sm, p)
      } yield whole(latency))
      .orElse(for {
        sm      <- sm
        p       <- producer
        latency <- NvidiaLatency.fixed(sm, p)
      } yield latency)
      .orElse(instruction.pipe.flatMap(architecture.pipeLatency.get))
  }

  private def latencyOf(
      architecture: Architecture,
      address: Long,
      instruction: ProfileStream.Instruction,
      counters: Profile.Counters,
      schedule: Schedule
  ): Either[String, Architecture.Sourced[Int]] = tableLatency(architecture, instruction, counters) match {
    case Some(latency)                     => Right(latency)
    case None if variableLatency(schedule) => Left(s"variable latency at ${hex(address)} has no table latency")
    case None                              =>
      val source = Architecture.Source.Estimate("no table latency; the planned issue cycles as a floor")
      Right(Architecture.Sourced(schedule.plannedCycles, source))
  }

  private def variableLatency(schedule: Schedule): Boolean = schedule match {
    case Schedule.Encoded(_, barrier) => barrier
    case _                            => false
  }

  private def lanesOf(counters: Profile.Counters): Int =
    math.round(counters.activeLanes.toDouble / math.max(1L, counters.issues).toDouble).toInt
}

case class BlockAnalysis(regions: Vec[BlockAnalysis.Region]) {
  def select(label: Option[String]): Either[String, Vec[BlockAnalysis.Region]] = label match {
    case None       => Right(regions)
    case Some(name) =>
      val found = regions.filter(_.label == name)
      Either.cond(found.nonEmpty, found, s"no region $name, known regions: ${regions.map(_.label).mkString(", ")}")
  }
}
