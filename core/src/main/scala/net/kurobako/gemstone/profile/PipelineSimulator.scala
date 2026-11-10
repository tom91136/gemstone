package net.kurobako.gemstone.profile

import net.kurobako.gemstone.collection.*
import scala.annotation.tailrec
import scala.collection.mutable

object PipelineSimulator {
  enum Stall derives CanEqual {
    case Dependency, PipeBusy, IssueSlot
  }

  case class Op(
      pipe: Option[Pipe],
      lanes: Int,
      reads: Vec[String],
      writes: Vec[String],
      latency: Architecture.Sourced[Int],
      issue: Int
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
    var done: Boolean                    = false
    var stalls: Map[Stall, Long]         = Map.empty
    val ready: mutable.Map[String, Long] = mutable.Map.empty[String, Long]
  }

  private type Signature = (Int, Vec[(Long, Map[String, Long])], Map[Pipe, Long])

  private case class Mark(time: Long, positions: Vec[Long], busy: Map[Pipe, Long], issued: Long)

  private case class Window(length: Long, passes: Long, busy: Map[Pipe, Long], issued: Long)

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
    val busyUntil   = mutable.Map.empty[Pipe, Long]
    val busyCycles  = mutable.Map.empty[Pipe, Long]
    val timeline    = mutable.ArrayBuffer.empty[Entry]
    val sources     = mutable.Set(rates.issues.source, architecture.limits.partitions.source)
    val marks       = mutable.Map.empty[Signature, Mark]
    var issuedSlots = 0L

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
              val ready = op.reads.forall(r => warp.ready.getOrElse(r, 0L) <= t)
              val free  = op.pipe.forall(pipe => busyUntil.getOrElse(pipe, 0L) <= t)
              if (ready && free && used + cost <= slots) {
                val latency              = op.latency.value.toLong
                val (iteration, opIndex) = stream.period match {
                  case Some(p) => (warp.position / p, (warp.position % p).toInt)
                  case None    => (0L, warp.position.toInt)
                }
                op.pipe.foreach { pipe =>
                  val rate   = rates.rate(pipe)
                  val cycles = math.ceil(op.lanes * partitions / rate.value).toLong
                  busyUntil(pipe) = t + cycles
                  busyCycles(pipe) = busyCycles.getOrElse(pipe, 0L) + cycles
                  sources += rate.source
                }
                op.writes.foreach(r => warp.ready(r) = math.max(warp.ready.getOrElse(r, 0L), t + latency))
                sources += op.latency.source
                timeline += Entry(index, iteration, opIndex, warp.dispatch, t, t + latency, warp.stalls)
                warp.position += 1
                warp.dispatch = t + 1
                warp.stalls = Map.empty
                used += cost
                issuedSlots += cost
                next = (index + 1) % count
              } else {
                val reason = if (!ready) Stall.Dependency else if (!free) Stall.PipeBusy else Stall.IssueSlot
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
        val key = (start, warps.map(warp => (warp.position % p, ahead(warp.ready, t))), ahead(busyUntil, t))
        marks.get(key) match {
          case Some(mark) =>
            Window(
              length = t - mark.time,
              passes = warps.zip(mark.positions).map((warp, before) => (warp.position - before) / p).sum,
              busy = busyCycles.toMap.map((pipe, cycles) => pipe -> (cycles - mark.busy.getOrElse(pipe, 0L))),
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

    val window = settle(0L, 0)
    val length = window.length.toDouble
    val pipes  = Vec.from(Pipe.values).filter(pipe => window.busy.getOrElse(pipe, 0L) > 0L)
    val pressure: Vec[(ProfileReport.Binding, Double)] =
      (ProfileReport.Binding.Issue, window.issued / (slots * length)) +: pipes.map(pipe =>
        (ProfileReport.Binding.Arithmetic(pipe), window.busy(pipe) / length)
      )
    val (top, peak) = pressure.maxBy(_._2)
    Block(
      entries = Vec.from(timeline),
      cyclesPerIteration = length / window.passes / math.min(partitions, subgroups),
      binding = if (peak >= 1.0) top else ProfileReport.Binding.Path,
      pressure = pressure,
      source = sources.max,
      steady = stream.period.isDefined
    )
  }
}
