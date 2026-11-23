package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import scala.annotation.tailrec
import scala.collection.mutable

object PipelineSimulator {
  enum Stall derives CanEqual {
    case Dependency, PipeBusy, IssueSlot, Scheduled
  }

  /** What an op asks of the memory system per issue: global sectors, shared wavefronts and warp load instructions. */
  case class Traffic(sectors: Double = 0.0, wavefronts: Double = 0.0, loads: Double = 0.0)

  object Traffic {
    val none: Traffic = Traffic()
  }

  /** The bytes an op occupies, `end` exclusive. */
  case class Extent(start: Long, end: Long)

  case class Op(
      pipe: Option[Pipe],
      lanes: Int,
      reads: Vec[String],
      writes: Vec[String],
      latency: Architecture.Sourced[Int],
      issue: Int,
      stall: Option[Architecture.Sourced[Int]] = None,
      traffic: Traffic = Traffic.none,
      backEdge: Boolean = false,
      rate: Option[Architecture.Sourced[Double]] = None,
      span: Option[Extent] = None
  )

  object Stream {
    private case class Repeat(body: Vec[Op]) extends Stream {
      def op(position: Long): Option[Op] = Some(body((position % body.size).toInt))
      def period: Option[Int]            = Some(body.size)
      def overlapped: Stream             = this
    }

    private case class Once(ops: Vec[Op]) extends Stream {
      def op(position: Long): Option[Op] = Option.when(position < ops.size)(ops(position.toInt))
      def period: Option[Int]            = None
      def overlapped: Stream             = Repeat(ops)
    }

    def repeat(body: Vec[Op]): Stream = Repeat(body)

    def once(ops: Vec[Op]): Stream = Once(ops)
  }

  trait Stream {
    def op(position: Long): Option[Op]
    def period: Option[Int]

    /** The stream of successive waves through one slot: each wave runs the stream once and a new one follows. */
    def overlapped: Stream
  }

  case class Entry(
      warp: Int,
      iteration: Long,
      index: Int,
      dispatch: Long,
      issue: Long,
      retire: Long,
      stalls: Map[Stall, Long]
  )

  case class Block(
      entries: Vec[Entry],
      cyclesPerIteration: Double,
      binding: ProfileReport.Binding,
      pressure: Vec[(ProfileReport.Binding, Double)],
      source: Architecture.Source,
      steady: Boolean
  )

  object Placement {
    def even(subgroups: Int, partitions: Int): Placement =
      Placement(
        Vec.tabulate(partitions)(partition =>
          subgroups / partitions + (if (partition < subgroups % partitions) 1 else 0)
        )
      )
  }

  /** Resident warps on each partition, in partition order. */
  case class Placement(warpsPerPartition: Vec[Int])

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Warp() {
    var position: Long                             = 0L
    var dispatch: Long                             = 0L
    var next: Long                                 = 0L
    var done: Boolean                              = false
    var stalls: Map[Stall, Long]                   = Map.empty
    val ready: mutable.Map[String, Long]           = mutable.Map.empty[String, Long]
    var restarts: Map[ProfileReport.Binding, Long] = Map.empty
    var scalarFrom: Long                           = 0L
    var vectorFrom: Long                           = 0L
    val condition: mutable.Map[String, Long]       = mutable.Map.empty[String, Long]
  }

  private type Signature = (
      Int,
      Vec[(Long, Long, Long, Long, Map[String, Long], Map[String, Long], Map[ProfileReport.Binding, Long])],
      Map[ProfileReport.Binding, Long],
      Map[ProfileReport.Binding, Vec[Long]]
  )

  private case class Mark(time: Long, positions: Vec[Long], busy: Map[ProfileReport.Binding, Long], issued: Long)

  private case class Window(length: Long, passes: Long, busy: Map[ProfileReport.Binding, Long], issued: Long)

  private case class Claim(resource: ProfileReport.Binding, cycles: Long, sources: Vec[Architecture.Source])

  // stalls leave small bubbles in a resource that still bounds the block
  private val Saturated = 0.9

  private def ordinal(resource: ProfileReport.Binding): Int = resource match {
    case ProfileReport.Binding.Issue            => 0
    case ProfileReport.Binding.Arithmetic(pipe) => 1 + pipe.ordinal
    case ProfileReport.Binding.LoadStore        => Pipe.values.length + 1
    case ProfileReport.Binding.Shared           => Pipe.values.length + 2
    case ProfileReport.Binding.Loads            => Pipe.values.length + 3
    case ProfileReport.Binding.Path             => Pipe.values.length + 4
  }

  private def ahead[K](times: scala.collection.Map[K, Long], now: Long): Map[K, Long] =
    times.view.collect { case (key, time) if time > now => key -> (time - now) }.toMap

  private case class Residency(count: Int, active: Int, scheduled: Architecture.IssueOrder)

  private def residency(architecture: Architecture, occupancy: Option[Occupancy], placement: Option[Placement]) = {
    val partitions = architecture.limits.partitions.value
    val resident   = placement.getOrElse(Placement.even(occupancy.fold(1)(_.subgroups), partitions)).warpsPerPartition
    val active     = resident.count(_ > 0)
    val order      = architecture.issueOrder.map(_.value).getOrElse(Architecture.IssueOrder.RoundRobin)
    // A spread placement models the same waves on every active partition; their mix comes from
    // several workgroups, so the single-partition age order is only applied when one is active.
    Residency(resident.maxOption.getOrElse(1), active, if (active == 1) order else Architecture.IssueOrder.RoundRobin)
  }

  /** Cycles of one settle before it gives up: a stream whose state has not repeated by then has no steady state to
    * report, and its marks would grow without bound.
    */
  val SettleLimit: Long = 1200000L

  /** The cycles per iteration of a stream that waves run once. Waves in lockstep through one pipe finish in an order
    * that one more instruction can change, so with several of them the cost is the steady state of waves that follow
    * one another, which depends only on what the pipes and the dependencies allow. The entries stay those of one pass.
    */
  def simulate(
      architecture: Architecture,
      occupancy: Option[Occupancy],
      stream: Stream,
      placement: Option[Placement] = None,
      limit: Long = SettleLimit
  ): Block = {
    val block   = pass(architecture, occupancy, stream, placement, limit)
    val waves   = residency(architecture, occupancy, placement)
    val overlap = stream.period.isEmpty && waves.count > 1 && waves.scheduled == Architecture.IssueOrder.RoundRobin
    if (!overlap) block
    else {
      val steady = pass(architecture, occupancy, stream.overlapped, placement, limit)
      block.copy(
        cyclesPerIteration = steady.cyclesPerIteration,
        binding = steady.binding,
        pressure = steady.pressure,
        source = steady.source
      )
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def pass(
      architecture: Architecture,
      occupancy: Option[Occupancy],
      stream: Stream,
      placement: Option[Placement],
      limit: Long
  ): Block = {
    val rates      = architecture.throughput
    val partitions = architecture.limits.partitions.value
    val waves      = residency(architecture, occupancy, placement)
    val count      = waves.count
    val active     = waves.active
    val scheduled  = waves.scheduled
    val slots      = math.max(1, (rates.issues.value / partitions).toInt)
    val warps      = Vec.fill(count)(Warp())
    val busyUntil  = mutable.Map.empty[ProfileReport.Binding, Long]
    val queued     = mutable.Map.empty[ProfileReport.Binding, Vec[Long]]
    val busyCycles = mutable.Map.empty[ProfileReport.Binding, Long]
    val timeline   = mutable.ArrayBuffer.empty[Entry]
    val sources    = mutable.Set(rates.issues.source, architecture.limits.partitions.source)
    val spans      = stream.period.fold(Vec.empty[Extent])(p => Vec.tabulate(p)(stream.op(_).flatMap(_.span)).flatten)
    val redirect   = architecture.branchRedirect.map { crossing =>
      val within = for {
        line   <- architecture.fetchLine
        inside <- architecture.lineRedirect
        if spans.nonEmpty && stream.period.contains(spans.size)
        if spans.map(_.start / line.value).min == spans.map(span => (span.end - 1) / line.value).max
      } yield Architecture.Sourced(inside.value, inside.source)
      within.getOrElse(crossing)
    }
    val marks       = mutable.Map.empty[Signature, Mark]
    var issuedSlots = 0L

    def claim(
        resource: ProfileReport.Binding,
        demand: Double,
        rate: Architecture.Sourced[Double],
        sharers: Int,
        also: Vec[Architecture.Source]
    ): Option[Claim] =
      Option.when(demand > 0.0 && rate.value > 0.0)(
        Claim(resource, math.ceil(demand * sharers / rate.value).toLong, rate.source +: also)
      )

    def claims(op: Op): Vec[Claim] = Vec(
      op.pipe.flatMap { pipe =>
        val shared = architecture.sharedPipes.get(pipe)
        claim(
          ProfileReport.Binding.Arithmetic(pipe),
          op.lanes.toDouble,
          op.rate.getOrElse(rates.rate(pipe)),
          if (shared.isDefined) active else partitions,
          Vec.from(shared)
        )
      },
      claim(
        ProfileReport.Binding.LoadStore,
        op.traffic.sectors,
        rates.loadStore,
        active,
        Vec(ProfileReport.sectorSourceOf(architecture))
      ),
      claim(
        ProfileReport.Binding.Shared,
        op.traffic.wavefronts,
        rates.shared,
        active,
        Vec(ProfileReport.conflictSourceOf(architecture))
      ),
      rates.loads.flatMap(claim(ProfileReport.Binding.Loads, op.traffic.loads, _, active, Vec.empty))
    ).flatten

    def depth(resource: ProfileReport.Binding): Int = resource match {
      case ProfileReport.Binding.Arithmetic(pipe) => architecture.pipeQueue.get(pipe).fold(0)(_.value)
      case _                                      => 0
    }

    def accepts(demand: Claim, t: Long): Boolean =
      busyUntil.getOrElse(demand.resource, 0L) <= t ||
        queued.getOrElse(demand.resource, Vec.empty[Long]).count(_ > t) < depth(demand.resource)

    def cycle(t: Long, start: Int): Int = {
      var used = 0
      var next = start
      for (i <- 0 until count) {
        val index = (start + i) % count
        val warp  = warps(index)
        if (!warp.done) {
          stream.op(warp.position) match {
            case None     => warp.done = true
            case Some(op) =>
              val cost  = math.min(op.issue, slots)
              val held  = warp.next > t || (if (op.pipe.isDefined) warp.vectorFrom else warp.scalarFrom) > t
              val gated = architecture.conditionLatency.filter(_ => op.backEdge)
              val ready = op.reads.forall(r => warp.ready.getOrElse(r, 0L) <= t) &&
                (gated.isEmpty || op.reads.forall(r => warp.condition.getOrElse(r, 0L) <= t))
              val demands = claims(op)
              val free    = demands.forall(accepts(_, t))
              if (!held && ready && free && used + cost <= slots) {
                val starts =
                  demands.map(demand => demand.resource -> math.max(t, busyUntil.getOrElse(demand.resource, 0L)))
                val begin = op.pipe.flatMap(pipe =>
                  starts.collectFirst {
                    case (ProfileReport.Binding.Arithmetic(`pipe`), start) => start
                  }
                ).getOrElse(t)
                val latency              = op.latency.value.toLong
                val (iteration, opIndex) = stream.period match {
                  case Some(p) => (warp.position / p, (warp.position % p).toInt)
                  case None    => (0L, warp.position.toInt)
                }
                demands.zip(starts).foreach { case (demand, (_, begins)) =>
                  busyUntil(demand.resource) = begins + demand.cycles
                  if (depth(demand.resource) > 0)
                    queued(demand.resource) = queued.getOrElse(demand.resource, Vec.empty[Long]).filter(_ > t) :+ begins
                  busyCycles(demand.resource) = busyCycles.getOrElse(demand.resource, 0L) + demand.cycles
                  sources ++= demand.sources
                }
                op.writes.foreach(r => warp.ready(r) = math.max(warp.ready.getOrElse(r, 0L), begin + latency))
                architecture.conditionLatency.foreach(condition =>
                  op.writes.foreach(r => warp.condition(r) = t + condition.value)
                )
                gated.foreach(condition => sources += condition.source)
                op.pipe.foreach { pipe =>
                  val after = architecture.scalarAfter.get(pipe)
                  after.foreach(a => sources += a.source)
                  warp.scalarFrom = t + after.fold(0L)(_.value.toLong)
                  val queue = architecture.pipeQueue.get(pipe)
                  queue.foreach(q => sources += q.source)
                  val cycles = demands.collectFirst {
                    case demand if demand.resource == ProfileReport.Binding.Arithmetic(pipe) => demand.cycles
                  }
                  warp.vectorFrom = t + queue.flatMap(_ => cycles).fold(1L)(c => math.max(1L, c - 1L))
                }
                sources += op.latency.source
                op.stall.foreach(stall => sources += stall.source)
                val taken = redirect.filter(_ => op.backEdge)
                taken.foreach(redirect => sources += redirect.source)
                val restarted = op.pipe.filter(pipe => architecture.pipeRestart.contains(pipe) && op.rate.isEmpty)
                restarted.foreach { pipe =>
                  val binding = ProfileReport.Binding.Arithmetic(pipe)
                  if (!warp.restarts.contains(binding))
                    warp.restarts = warp.restarts.updated(binding, begin + latency)
                }
                timeline += Entry(index, iteration, opIndex, warp.dispatch, t, begin + latency, warp.stalls)
                warp.position += 1
                val hold = math.max(1, op.stall.fold(1)(_.value))
                warp.next = t + taken.fold(hold)(_.value).toLong
                if (op.backEdge) {
                  if (count == 1)
                    warp.restarts.foreach { (binding, gate) =>
                      val restart = binding match {
                        case ProfileReport.Binding.Arithmetic(pipe) => architecture.pipeRestart.get(pipe)
                        case _                                      => None
                      }
                      restart.foreach { cycles =>
                        val drain = math.ceil(cycles.value / math.max(1, active).toDouble).toLong
                        busyUntil(binding) = busyUntil.getOrElse(binding, 0L).max(gate) + drain
                        sources += cycles.source
                      }
                    }
                  warp.restarts = Map.empty
                }
                warp.dispatch = t + 1
                warp.stalls = Map.empty
                used += cost
                issuedSlots += cost
                next = (index + 1) % count
              } else {
                val reason =
                  if (held) Stall.Scheduled
                  else if (!ready) Stall.Dependency
                  else if (!free) Stall.PipeBusy
                  else Stall.IssueSlot
                warp.stalls = warp.stalls.updated(reason, warp.stalls.getOrElse(reason, 0L) + 1L)
              }
          }
        }
      }
      next
    }

    @tailrec
    def settle(t: Long, start: Int): Window = stream.period match {
      case Some(_) if t > limit => Window(0L, 0L, Map.empty, 0L)
      case Some(p)              =>
        val key = (
          start,
          warps.map(warp =>
            (
              warp.position % p,
              math.max(0L, warp.next - t),
              math.max(0L, warp.scalarFrom - t),
              math.max(0L, warp.vectorFrom - t),
              ahead(warp.ready, t),
              ahead(warp.condition, t),
              ahead(warp.restarts, t)
            )
          ),
          ahead(busyUntil, t),
          queued.view.mapValues(_.filter(_ > t).map(_ - t)).filter(_._2.nonEmpty).toMap
        )
        marks.get(key) match {
          case Some(mark) =>
            Window(
              length = t - mark.time,
              passes = warps.zip(mark.positions).map((warp, before) => (warp.position - before) / p).sum,
              busy =
                busyCycles.toMap.map((resource, cycles) => resource -> (cycles - mark.busy.getOrElse(resource, 0L))),
              issued = issuedSlots - mark.issued
            )
          case None =>
            marks(key) = Mark(t, warps.map(_.position), busyCycles.toMap, issuedSlots)
            val next = cycle(t, start)
            settle(t + 1, if (scheduled == Architecture.IssueOrder.OldestFirst) 0 else next)
        }
      case None if warps.forall(_.done) =>
        Window(
          length = (timeline.map(_.retire) ++ busyUntil.values).maxOption.getOrElse(0L),
          passes = count.toLong,
          busy = busyCycles.toMap,
          issued = issuedSlots
        )
      case None =>
        val next = cycle(t, start)
        settle(t + 1, if (scheduled == Architecture.IssueOrder.OldestFirst) 0 else next)
    }

    val window    = settle(0L, 0)
    val length    = window.length.toDouble
    val resources =
      window.busy.collect { case (resource, cycles) if cycles > 0L => resource }.toVector.sortBy(ordinal(_))
    val pressure: Vec[(ProfileReport.Binding, Double)] =
      (ProfileReport.Binding.Issue, window.issued / (slots * length)) +: Vec.from(resources).map(resource =>
        (resource, window.busy(resource) / length)
      )
    val (top, peak) = pressure.maxBy(_._2)
    Block(
      entries = Vec.from(timeline),
      cyclesPerIteration = length / window.passes / active,
      binding = if (peak >= Saturated) top else ProfileReport.Binding.Path,
      pressure = pressure,
      source = sources.max,
      steady = stream.period.isDefined
    )
  }
}
