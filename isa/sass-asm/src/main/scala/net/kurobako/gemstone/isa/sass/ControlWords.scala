package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.flow.ControlFlowGraph
import net.kurobako.gemstone.vm.RegisterUse
import scala.collection.mutable
import upickle.default.*

/** Regenerates the scheduling fields of SASS code from a table harvested from ptxas output.
  *
  * A path is a run of blocks that each fall through to the next, so its instructions issue in order. Along a path the
  * stalls from a fixed-latency producer to a consumer sum to at least the least distance ptxas left between that pair;
  * a variable-latency producer sets a write barrier that the first reader or overwriter of its result waits on; a form
  * that reads its sources late sets a read barrier that the first overwriter of a source waits on. A path leaves
  * through a transfer that waits on every pending barrier and issues once each result of the path is older than the
  * largest distance the table holds for its producer, so a block a branch enters assumes nothing pending. The reuse
  * flags are cleared.
  */
object ControlWords {

  val Barriers = 6

  /** How often ptxas must have issued one form after another for their least distance to count. */
  val Sampled = 8

  /** One program in this many is held out of the harvest, chosen by its file name. */
  val HeldOut = 5

  def heldOut(program: String): Boolean = Math.floorMod(program.hashCode, HeldOut) == 0

  private val MaxStall  = 15
  private val Ungrouped = 1 to 11
  private val Departure = ""

  /** What scheduling reads of one instruction: its form, the registers it reads with how each is used (`g` as the
    * guard, else the position of the operand that names it, `h` after the position for the high word of a wide
    * operand), the registers it writes, whether it leaves its block by more than falling through, whether another
    * instruction of the function writes one of the registers it reads, and whether it is the function's first.
    */
  case class Step(
      form: String,
      reads: Vec[String],
      kinds: Vec[String],
      guarded: Boolean,
      writes: Vec[String],
      transfer: Boolean,
      overwritten: Boolean,
      entry: Boolean
  )

  case class Path(first: Int, steps: Vec[Step])

  /** `latency` is keyed by producer form, bank and ordinal of the written register, then by consumer form and use, with
    * a use's operand slot not a latency of its own: a lookup takes the least distance the row holds for the consumer
    * form's slots, and keeps a guard, a high word and an unknown use apart. `settle` is the least distance from a
    * barrier's setter to its first waiter, with the forms that set a write barrier as its keys and `/r` after a form
    * for its read barrier. `late` holds the forms that set a read barrier. `spacing` is the least distance between the
    * latest instruction of one form and a later instruction of another, whatever their registers, for pairs seen
    * `Sampled` times and never one cycle apart. `idle` is the stall between two adjacent forms that no register
    * explains. An unseen pair of `latency` or `idle` takes its row's maximum.
    */
  case class Table(
      target: String,
      toolkit: String,
      sources: Int,
      instructions: Int,
      latency: Map[String, Map[String, Int]],
      settle: Map[String, Int],
      late: Set[String],
      spacing: Map[String, Map[String, Int]],
      idle: Map[String, Map[String, Int]]
  ) derives ReadWriter {
    private def least(row: Map[String, Int]): Map[String, Int] =
      row.foldLeft(Map.empty[String, Int]) { (found, entry) =>
        val key = useKey(entry._1)
        found.updated(key, math.min(entry._2, found.getOrElse(key, entry._2)))
      }

    private val rows: Map[String, Int]     = latency.map((producer, row) => producer -> row.values.max)
    private val worst: Int                 = (rows.values ++ Seq(1)).max
    private val uses                       = latency.map((producer, row) => producer -> least(row))
    private val idleRows: Map[String, Int] = idle.map((form, row) => form -> row.values.max)
    private val idleWorst: Int             = (idleRows.values ++ Seq(1)).max

    def rowMaximum(producer: String): Int = math.min(MaxStall, rows.getOrElse(producer, worst))

    def distance(producer: String, consumer: String): Int =
      math.min(MaxStall, uses.get(producer).fold(worst)(row => row.getOrElse(useKey(consumer), rows(producer))))

    def spaced(form: String, later: String): Int = spacing.get(form).flatMap(_.get(later)).getOrElse(0)

    def idleStall(form: String, next: String): Int = {
      val known = if (idle.contains(form)) form else form.takeWhile(_ != '@')
      math.min(MaxStall, idle.get(known).fold(idleWorst)(row => row.getOrElse(next, idleRows(known))))
    }

    def settles(form: String): Int = math.min(MaxStall, settle.getOrElse(form, (settle.values ++ Seq(1)).max))

    def setsWriteBarrier(form: String): Boolean = settle.contains(form)

    def readsLate(form: String): Boolean = late(form)
  }

  lazy val tables: Map[String, Table] =
    resources.json[Vector[Table]](getClass, "control-words.json").map(table => table.target -> table).toMap

  private def formOf(instruction: DecodedInstruction): String =
    instruction.machine.fold(instruction.mnemonic.raw)(_.form.split("__").head)

  private def predicate(index: Int, uniform: Boolean): String = s"${if (uniform) "UP" else "P"}$index"

  private def mentions(operand: Operand): Vec[String] = operand match {
    case Operand.Register(bank, index)                                        => Vec(s"${bank.spelling}$index")
    case Operand.Predicate(Some(index), uniform, _)                           => Vec(predicate(index, uniform))
    case Operand.Group(values)                                                => values.flatMap(mentions)
    case Operand.ConstantBank(_, offset)                                      => mentions(offset)
    case Operand.Address(base = base, index = index, descriptor = descriptor) =>
      Vec.from(base.toSeq ++ index.toSeq ++ descriptor.toSeq).flatMap(mentions)
    case Operand.Unary(value = value) => mentions(value)
    case Operand.Qualified(value, _)  => mentions(value)
    case _                            => Vec.empty
  }

  private def lower(register: String): Option[String] = {
    val bank = register.takeWhile(!_.isDigit)
    register.drop(bank.length).toIntOption.filter(_ > 0).map(index => s"$bank${index - 1}")
  }

  /** The registers `instruction` reads in time, each with its use. A guarded write also counts in `use` as a read of
    * its destination, which is no dependency in time, so a register a guarded instruction writes and names once is left
    * out.
    */
  private def reads(instruction: DecodedInstruction, use: RegisterUse): Vec[(String, String)] = {
    val positions = instruction.operands.map(mentions)
    val guard     = instruction.guard.flatMap(g => g.predicate.index.map(predicate(_, g.predicate.uniform)))
    def named(register: String): Int      = positions.count(_.contains(register))
    def pseudo(register: String): Boolean =
      instruction.guard.nonEmpty && use.writes.contains(register) &&
        (if (named(register) == 0) lower(register).forall(named(_) <= 1) else named(register) == 1)
    use.reads.filterNot(pseudo).map { register =>
      val direct = positions.lastIndexWhere(_.contains(register))
      val high   = lower(register).fold(-1)(low => positions.lastIndexWhere(_.contains(low)))
      register -> (
        if (guard.contains(register)) "g" else if (direct >= 0) s"$direct" else if (high >= 0) s"${high}h" else "?"
      )
    }
  }

  /** The paths of a function in layout order. */
  def paths(graph: ControlFlowGraph, uses: Vec[RegisterUse], instructions: Vec[DecodedInstruction]): Vec[Path] = {
    val blocks                     = graph.blocks
    def falls(index: Int): Boolean =
      index + 1 < blocks.size && !blocks(index).open && blocks(index).successors.contains(index + 1)
    val writers                         = uses.flatMap(_.writes).groupMapReduce(identity)(_ => 1)(_ + _)
    def step(block: Int, at: Int): Step = {
      val instruction = instructions(at)
      val read        = reads(instruction, uses(at))
      val written     = uses(at).writes.toSet
      val leaving     = at == blocks(block).last && (blocks(block).open || blocks(block).successors != Vec(block + 1))
      val looping     = leaving && blocks(block).successors == Vec(block) && blocks(block).first == at
      Step(
        formOf(instruction) + (if (looping) "@self" else ""),
        read.map(_._1),
        read.map(_._2),
        instruction.guard.nonEmpty,
        uses(at).writes,
        leaving || instruction.mnemonic.raw == "CALL",
        read.map(_._1).exists(register =>
          writers.getOrElse(register, 0) > (if (written(register)) 1 else 0)
        ),
        at == 0
      )
    }
    val starts = blocks.indices.filter(index => index == 0 || !falls(index - 1))
    Vec.from(starts.zipWithIndex.map { (start, order) =>
      val end = if (order + 1 < starts.size) starts(order + 1) else blocks.size
      Path(
        blocks(start).first,
        Vec.from((start until end).flatMap(block => (blocks(block).first to blocks(block).last).map(step(block, _))))
      )
    })
  }

  private def idleKey(step: Step): String = if (step.entry) s"${step.form}@entry" else step.form

  /** The class of a latency row's use: a guard, an unknown use, the high word of a wide read, or a register slot. A
    * register is read at the same latency whichever slot names it, so the slots of a consumer form share one distance.
    */
  private def useKey(consumer: String): String = {
    val slash = consumer.lastIndexOf('/')
    val kind  = consumer.drop(slash + 1)
    val use   = if (kind.endsWith("h")) "h" else if (kind == "g" || kind == "?") kind else ""
    s"${consumer.take(slash + 1)}$use"
  }

  private def bank(register: String): String = register.takeWhile(!_.isDigit)

  private def producerKey(step: Step, register: String): String = {
    val bankOf = bank(register)
    s"${step.form}/$bankOf${step.writes.filter(bank(_) == bankOf).indexOf(register)}"
  }

  private case class Producer(key: String, time: Int, barrier: Int)

  private case class LateReader(time: Int, sources: Set[String], barrier: Int)

  /** The scheduling state along one path. Times are issue times from the head of the path. */
  private class Walk {
    val writers: mutable.Map[String, List[Producer]] = mutable.Map.empty
    val pending: Array[Option[(String, Int)]]        = Array.fill(Barriers)(None)
    val late: mutable.ArrayBuffer[LateReader]        = mutable.ArrayBuffer.empty
    val recent: mutable.Map[String, Int]             = mutable.Map.empty
    val latest: Array[Int]                           = Array(0)

    /** The forms issued within a stall field's reach of the latest instruction, with their latest issue time. */
    def near: Iterable[(String, Int)] = recent.filter((_, time) => latest(0) - time < MaxStall)

    val cleared: Array[Int] = Array.fill(Barriers)(Int.MinValue)

    def waiting(producer: Producer): Boolean = producer.barrier >= 0 && producer.time >= cleared(producer.barrier)

    def blockers(step: Step): Vec[LateReader] = Vec.from(late.filter(reader => step.writes.exists(reader.sources)))

    /** The barriers `step` must wait on. */
    def mask(step: Step): Int = {
      val registers = (step.reads ++ step.writes).flatMap(writers.getOrElse(_, Nil)).filter(waiting).map(_.barrier)
      val sources   = blockers(step).filter(reader => reader.time >= cleared(reader.barrier))
      val leaving   = if (step.transfer) (0 until Barriers).filter(pending(_).nonEmpty) else Seq.empty[Int]
      (registers ++ sources.map(_.barrier) ++ leaving).foldLeft(0)((bits, barrier) => bits | (1 << barrier))
    }

    /** The least issue time the table allows `step`. */
    def need(
        table: Table,
        step: Step,
        waits: Int,
        departing: Boolean,
        spacing: Boolean = true,
        drained: Boolean = false
    ): Int = {
      val fixed = step.reads.zip(step.kinds).flatMap((register, kind) =>
        writers.getOrElse(register, Nil).filter(producer => producer.barrier < 0 && !drained)
          .map(producer => producer.time + table.distance(producer.key, s"${step.form}/$kind"))
      )
      val settled = (0 until Barriers).filter(barrier => (waits & (1 << barrier)) != 0)
        .flatMap(pending(_)).map((form, time) => time + table.settles(form))
      val leaving =
        if (!departing || !step.transfer || drained) Seq.empty[Int]
        else writers.values.flatMap(_.filter(_.barrier < 0)).map(producer =>
          producer.time + table.rowMaximum(producer.key) - table.idleStall(idleKey(step), Departure)
        )
      val apart =
        if (spacing) near.map((form, time) => time + table.spaced(form, step.form)) else Iterable.empty[Int]
      (fixed ++ settled ++ leaving ++ apart).foldLeft(0)(math.max)
    }

    /** A stall of zero is `DRAIN` in the encoding tables: the warp waits for every fixed-latency result. */
    def drain(): Unit = writers.mapValuesInPlace((_, producers) => producers.filter(_.barrier >= 0))

    def commit(step: Step, time: Int, scheduling: Scheduling): Unit = {
      (0 until Barriers).filter(barrier => (scheduling.waitMask.raw & (1 << barrier)) != 0)
        .foreach { barrier =>
          pending(barrier) = None
          cleared(barrier) = time
        }
      late.filterInPlace(reader => reader.time >= cleared(reader.barrier))
      recent(step.form) = time
      latest(0) = time
      val written = scheduling.writeBarrier.fold(-1)(_.raw)
      step.writes.foreach { register =>
        val producer = Producer(producerKey(step, register), time, written)
        writers(register) = if (step.guarded) producer :: writers.getOrElse(register, Nil) else List(producer)
      }
      if (written >= 0) pending(written) = Some((step.form, time))
      val read = scheduling.readBarrier.fold(-1)(_.raw)
      if (read >= 0) {
        pending(read) = Some((s"${step.form}/r", time))
        late += LateReader(time, step.reads.toSet, read)
      }
    }
  }

  private def times(schedule: Vec[Scheduling]): Vec[Int] =
    Vec.from(schedule.scanLeft(0)((time, scheduling) => time + scheduling.stall.raw).take(schedule.size))

  private def scheduling(stall: Int, yields: Boolean, write: Int, read: Int, waits: Int): Scheduling = Scheduling(
    stall.unsafe[Int in 0 ~ 15],
    yields,
    Option.when(write >= 0)(write.unsafe[Int in 0 ~ 5]),
    Option.when(read >= 0)(read.unsafe[Int in 0 ~ 5]),
    waits.unsafe[Int in 0 ~ 0x3f],
    0.unsafe[Int in 0 ~ 0xf]
  )

  /** Whether ptxas marks a stall as the end of an issue group: a wait of three or more cycles on a register, or a stall
    * outside `Ungrouped`, which the encoding names only as the end of a group.
    */
  private def yields(stall: Int, idle: Int): Boolean = !Ungrouped.contains(stall) || stall >= 3 && stall > idle

  /** The scheduling of each instruction of `path`, or the instruction whose requirement no stall can meet. */
  def regenerate(table: Table, path: Path): Either[String, Vec[Scheduling]] = {
    val steps  = path.steps
    val walk   = Walk()
    val issued = Array.fill(steps.size)(0)
    val masks  = Array.fill(steps.size)(0)
    val writes = Array.fill(steps.size)(-1)
    val reads  = Array.fill(steps.size)(-1)
    val failed = steps.indices.find { index =>
      val step  = steps(index)
      val idle  = if (index == 0) 1 else table.idleStall(idleKey(steps(index - 1)), step.form)
      val waits = walk.mask(step)
      val base  = if (index == 0) 0 else issued(index - 1) + idle
      if (index > 0 && idle == 0 && walk.need(table, step, waits, departing = true, drained = true) <= base)
        walk.drain()
      val time  = math.max(base, walk.need(table, step, waits, departing = true))
      val free  = (0 until Barriers).filter(barrier => walk.pending(barrier).isEmpty || (waits & (1 << barrier)) != 0)
      val write = if (table.setsWriteBarrier(step.form) && step.writes.nonEmpty) free.headOption.getOrElse(0) else -1
      val read  =
        if (table.readsLate(step.form) && step.overwritten)
          free.find(_ != write).getOrElse((write + 1) % Barriers)
        else -1
      issued(index) = time
      masks(index) = waits
      writes(index) = write
      reads(index) = read
      walk.commit(step, time, scheduling(0, yields = false, write, read, waits))
      index > 0 && time - issued(index - 1) > MaxStall
    }
    failed.fold {
      Right(Vec.from(steps.indices.map { index =>
        val idle =
          table.idleStall(idleKey(steps(index)), if (index + 1 < steps.size) steps(index + 1).form else Departure)
        val stall = if (index + 1 < steps.size) issued(index + 1) - issued(index) else idle
        scheduling(stall, yields(stall, idle), writes(index), reads(index), masks(index))
      }))
    }(index => Left(s"instruction ${path.first + index} of form ${steps(index).form} needs a stall above $MaxStall"))
  }

  /** Where `schedule` is less conservative than the table requires of `path`, as instruction index and reason. With
    * `harvested` only the register distances and barrier waits ptxas itself keeps are required; otherwise also the idle
    * stalls, the spacing, the barriers each form sets and the way a path leaves.
    */
  def violations(
      table: Table,
      path: Path,
      schedule: Vec[Scheduling],
      harvested: Boolean = false
  ): Vec[(Int, String)] = {
    val steps  = path.steps
    val issued = times(schedule)
    val walk   = Walk()
    Vec.from(steps.indices.flatMap { index =>
      val step  = steps(index)
      val waits =
        if (harvested) walk.mask(step.copy(writes = Vec.empty[String], transfer = false)) else walk.mask(step)
      val missing = waits & ~schedule(index).waitMask.raw
      val need    = walk.need(table, step, schedule(index).waitMask.raw | waits, !harvested, !harvested)
      val idle    =
        if (index == 0 || harvested) 0 else issued(index - 1) + table.idleStall(idleKey(steps(index - 1)), step.form)
      val unset = !harvested && table.setsWriteBarrier(step.form) && step.writes.nonEmpty &&
        schedule(index).writeBarrier.isEmpty
      val unread =
        !harvested && table.readsLate(step.form) && step.overwritten && schedule(index).readBarrier.isEmpty
      walk.commit(step, issued(index), schedule(index))
      if (schedule(index).stall.raw == 0) walk.drain()
      Seq(
        Option.when(missing != 0)(s"misses a wait on barriers ${missing.toBinaryString}"),
        Option.when(issued(index) < need)(s"issues at ${issued(index)}, ${need - issued(index)} before its registers"),
        Option.when(issued(index) < idle)(s"issues at ${issued(index)}, ${idle - issued(index)} inside the idle stall"),
        Option.when(unset)("sets no write barrier"),
        Option.when(unread)("sets no read barrier")
      ).flatten.map(reason => (path.first + index, s"${step.form} $reason"))
    })
  }

  /** The dependencies of `path` whose stalls in `schedule` sum to less than in `reference`, as producer and consumer.
    */
  def shorter(path: Path, schedule: Vec[Scheduling], reference: Vec[Scheduling]): Vec[(Int, Int)] = {
    val steps   = path.steps
    val ours    = times(schedule)
    val theirs  = times(reference)
    val writers = mutable.Map.empty[String, Int]
    Vec.from(steps.indices.flatMap { index =>
      val found = steps(index).reads.flatMap(writers.get).distinct
        .filter(producer => ours(index) - ours(producer) < theirs(index) - theirs(producer))
      steps(index).writes.foreach(writers(_) = index)
      found.map(producer => (path.first + producer, path.first + index))
    })
  }

  /** Accumulates the table from paths ptxas scheduled. `classes` runs over every path before `distances`, and
    * `distances` over every path before `registers` is read for `idles`.
    */
  class Harvest {
    private val writeForms = mutable.Set.empty[String]
    private val readForms  = mutable.Set.empty[String]
    private val latency    = mutable.Map.empty[String, mutable.Map[String, Int]]
    private val settle     = mutable.Map.empty[String, Int]
    private val idleStalls = mutable.Map.empty[(String, String), Array[Int]]
    private val spacing    = mutable.Map.empty[(String, String), (Int, Int)]
    private val counted    = Array(0)

    private def least(map: mutable.Map[String, Int], key: String, value: Int): Unit =
      map(key) = math.min(map.getOrElse(key, Int.MaxValue), value)

    def classes(path: Path, schedule: Vec[Scheduling]): Unit = {
      counted(0) += path.steps.size
      path.steps.zip(schedule).foreach { (step, scheduling) =>
        if (scheduling.writeBarrier.nonEmpty) writeForms += step.form
        if (scheduling.readBarrier.nonEmpty) readForms += step.form
      }
    }

    /** The table of register distances and barrier classes, without idle stalls. */
    def registers: Table = Table(
      "",
      "",
      0,
      counted(0),
      latency.map((producer, row) => producer -> row.toMap).toMap,
      writeForms.map(form => form -> settle.getOrElse(form, MaxStall)).toMap ++
        readForms.map(form => s"$form/r" -> settle.getOrElse(s"$form/r", MaxStall)),
      readForms.toSet,
      spacing.toSeq.filter((_, found) => found._2 >= Sampled && found._1 > 1).groupMap(_._1._1)((pair, found) =>
        pair._2 -> found._1
      )
        .map((form, row) => form -> row.toMap),
      Map.empty
    )

    def distances(path: Path, schedule: Vec[Scheduling]): Unit = {
      val issued = times(schedule)
      val walk   = Walk()
      path.steps.indices.foreach { index =>
        val step = path.steps(index)
        val time = issued(index)
        step.reads.zip(step.kinds).foreach((register, kind) =>
          walk.writers.getOrElse(register, Nil).headOption.filter(_.barrier < 0).foreach(producer =>
            least(latency.getOrElseUpdate(producer.key, mutable.Map.empty), s"${step.form}/$kind", time - producer.time)
          )
        )
        (0 until Barriers).filter(barrier => (schedule(index).waitMask.raw & (1 << barrier)) != 0)
          .flatMap(walk.pending(_)).foreach((form, set) => least(settle, form, time - set))
        walk.near.foreach { (form, issued) =>
          val (distance, count) = spacing.getOrElse((form, step.form), (Int.MaxValue, 0))
          spacing((form, step.form)) = (math.min(distance, time - issued), count + 1)
        }
        walk.commit(step, time, schedule(index))
        if (schedule(index).stall.raw == 0) walk.drain()
      }
    }

    private def idle(form: String, next: String, stall: Int): Unit =
      idleStalls.getOrElseUpdate((form, next), Array.fill(MaxStall + 1)(0))(stall) += 1

    /** Counts the stalls that the distances of `table` do not explain. */
    def idles(table: Table, path: Path, schedule: Vec[Scheduling]): Unit = {
      val issued = times(schedule)
      val walk   = Walk()
      path.steps.indices.foreach { index =>
        val step = path.steps(index)
        if (index > 0) {
          val stall = schedule(index - 1).stall.raw
          val need  =
            walk.need(table, step, schedule(index).waitMask.raw, departing = false, spacing = false) - issued(index - 1)
          if (stall > need || stall == 0) idle(idleKey(path.steps(index - 1)), step.form, stall)
        }
        walk.commit(step, issued(index), schedule(index))
        if (schedule(index).stall.raw == 0) walk.drain()
      }
      path.steps.lastOption.zip(schedule.lastOption).foreach((step, scheduling) =>
        idle(idleKey(step), Departure, scheduling.stall.raw)
      )
    }

    /** Each pair of forms with the count of each idle stall ptxas gave it. */
    def idleCounts: Map[(String, String), Vec[Int]] = idleStalls.map((pair, counts) => pair -> Vec.from(counts)).toMap

    /** The table, with the idle stall ptxas gave each pair most often, or the longest it gave a form that leaves. */
    def table(target: String, toolkit: String, sources: Int): Table =
      registers.copy(
        target = target,
        toolkit = toolkit,
        sources = sources,
        idle = idleStalls.toSeq.groupMap(_._1._1)((pair, counts) =>
          pair._2 -> (if (pair._2 == Departure) counts.lastIndexWhere(_ > 0) else counts.lastIndexOf(counts.max))
        )
          .map((form, row) => form -> row.toMap)
      )
  }

  /** The control bits of `scheduling` laid over the encoding `word` of a Volta or later instruction. */
  def encode(word: BigInt, scheduling: Scheduling): BigInt = {
    val control = scheduling.stall.raw | ((if (scheduling.yieldHint) 0 else 1) << 4) |
      (scheduling.writeBarrier.fold(7)(_.raw) << 5) | (scheduling.readBarrier.fold(7)(_.raw) << 8) |
      (scheduling.waitMask.raw << 11) | (scheduling.reuse.raw << 17)
    (word & ~(BigInt(0x1fffff) << 105)) | (BigInt(control) << 105)
  }
}
