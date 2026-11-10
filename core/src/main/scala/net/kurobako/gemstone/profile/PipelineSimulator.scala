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

  case class Op(
      pipe: Option[Pipe],
      lanes: Int,
      reads: Vec[String],
      writes: Vec[String],
      latency: Architecture.Sourced[Int],
      issue: Int,
      stall: Option[Architecture.Sourced[Int]] = None,
      traffic: Traffic = Traffic.none
  )

  object Stream {
    private case class Repeat(body: Vec[Op]) extends Stream {
      def op(position: Long): Option[Op] = Some(body((position % body.size).toInt))
      def period: Option[Int]            = Some(body.size)
    }

    private case class Once(ops: Vec[Op]) extends Stream {
      def op(position: Long): Option[Op] = Option.when(position < ops.size)(ops(position.toInt))
      def period: Option[Int]            = None
    }

    def repeat(body: Vec[Op]): Stream = Repeat(body)

    def once(ops: Vec[Op]): Stream = Once(ops)
  }

  trait Stream {
    def op(position: Long): Option[Op]
    def period: Option[Int]
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

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class Warp() {
    var position: Long                   = 0L
    var dispatch: Long                   = 0L
    var next: Long                       = 0L
    var done: Boolean                    = false
    var stalls: Map[Stall, Long]         = Map.empty
    val ready: mutable.Map[String, Long] = mutable.Map.empty[String, Long]
  }

  private type Signature = (Int, Vec[(Long, Long, Map[String, Long])], Map[ProfileReport.Binding, Long])

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

  private def ahead[K](times: mutable.Map[K, Long], now: Long): Map[K, Long] =
    times.view.collect { case (key, time) if time > now => key -> (time - now) }.toMap

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def simulate(architecture: Architecture, occupancy: Option[Occupancy], stream: Stream): Block = {
    val rates       = architecture.throughput
    val partitions  = architecture.limits.partitions.value
    val subgroups   = occupancy.fold(1)(_.subgroups)
    val count       = (subgroups + partitions - 1) / partitions
    val slots       = math.max(1, (rates.issues.value / partitions).toInt)
    val warps       = Vec.fill(count)(Warp())
    val active      = math.min(partitions, subgroups)
    val busyUntil   = mutable.Map.empty[ProfileReport.Binding, Long]
    val busyCycles  = mutable.Map.empty[ProfileReport.Binding, Long]
    val timeline    = mutable.ArrayBuffer.empty[Entry]
    val sources     = mutable.Set(rates.issues.source, architecture.limits.partitions.source)
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
          rates.rate(pipe),
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
              val cost    = math.min(op.issue, slots)
              val held    = warp.next > t
              val ready   = op.reads.forall(r => warp.ready.getOrElse(r, 0L) <= t)
              val demands = claims(op)
              val free    = demands.forall(demand => busyUntil.getOrElse(demand.resource, 0L) <= t)
              if (!held && ready && free && used + cost <= slots) {
                val latency              = op.latency.value.toLong
                val (iteration, opIndex) = stream.period match {
                  case Some(p) => (warp.position / p, (warp.position % p).toInt)
                  case None    => (0L, warp.position.toInt)
                }
                demands.foreach { demand =>
                  busyUntil(demand.resource) = t + demand.cycles
                  busyCycles(demand.resource) = busyCycles.getOrElse(demand.resource, 0L) + demand.cycles
                  sources ++= demand.sources
                }
                op.writes.foreach(r => warp.ready(r) = math.max(warp.ready.getOrElse(r, 0L), t + latency))
                sources += op.latency.source
                op.stall.foreach(stall => sources += stall.source)
                timeline += Entry(index, iteration, opIndex, warp.dispatch, t, t + latency, warp.stalls)
                warp.position += 1
                warp.next = t + math.max(1, op.stall.fold(1)(_.value)).toLong
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
      case Some(p) =>
        val key = (
          start,
          warps.map(warp => (warp.position % p, math.max(0L, warp.next - t), ahead(warp.ready, t))),
          ahead(busyUntil, t)
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
            settle(t + 1, cycle(t, start))
        }
      case None if warps.forall(_.done) =>
        Window(
          length = (timeline.map(_.retire) ++ busyUntil.values).maxOption.getOrElse(0L),
          passes = count.toLong,
          busy = busyCycles.toMap,
          issued = issuedSlots
        )
      case None => settle(t + 1, cycle(t, start))
    }

    val window    = settle(0L, 0)
    val length    = window.length.toDouble
    val share     = active.toDouble / partitions
    val resources =
      window.busy.collect { case (resource, cycles) if cycles > 0L => resource }.toVector.sortBy(ordinal(_))
    def local(resource: ProfileReport.Binding): Boolean = resource match {
      case ProfileReport.Binding.Arithmetic(pipe) => !architecture.sharedPipes.contains(pipe)
      case _                                      => false
    }
    val pressure: Vec[(ProfileReport.Binding, Double)] =
      (ProfileReport.Binding.Issue, window.issued / (slots * length) * share) +: Vec.from(resources).map(resource =>
        (resource, window.busy(resource) / length * (if (local(resource)) share else 1.0))
      )
    val (top, peak) = pressure.maxBy(_._2)
    Block(
      entries = Vec.from(timeline),
      cyclesPerIteration = length / window.passes / math.min(partitions, subgroups),
      binding = if (peak >= Saturated) top else ProfileReport.Binding.Path,
      pressure = pressure,
      source = sources.max,
      steady = stream.period.isDefined
    )
  }
}
