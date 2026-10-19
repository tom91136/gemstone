package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.vm.*

object NvidiaHarness {
  private val Kernel: NonEmpty[String] = "reference".unsafe[NonEmpty[String]]

  /** The byte at `offset` of the constant bank of the run `seed` selects. */
  def constant(seed: Long, offset: Long): Byte = SeededMemory.byte(seed, offset)

  /** `instructions` as the only routine of a kernel of one block of `lanes` threads, and what `read` takes of the
    * cohort at the end of the routine, the one instruction with a source, or why the run did not reach it.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def run[A](
      instructions: Vec[VMIR.PredicatedInstruction],
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      lanes: Int > 0,
      memory: AddressSpace
  )(read: CohortView => A): Either[String, A] = {
    val routine = VMIR.Routine(
      Kernel,
      VMIR.ExecutableProgram(
        instructions,
        Vec.tabulate(instructions.size)(pc => if (pc == instructions.size - 1) 0L else VMIR.ExecutableProgram.NoSource)
      ),
      registerCount,
      predicateCount
    )
    var captured = Option.empty[A]
    // the observer sees the cohort NVIDIA execution exposes as the open architectural state of the launch
    val observer: LaunchObserver = (at, state) =>
      (at.instruction, state) match {
        case (Some(_), view: CohortView) =>
          captured = Some(read(view))
          Right(())
        case _ => Right(())
      }
    LaunchGeometry.forLaunch(LaunchGeometry.Dimensions(lanes, 1, 1), LaunchGeometry.Dimensions.One).left.map(_.raw)
      .flatMap(geometry =>
        VMInterpreter.execute(
          VMIR.Kernel(VMIR.Entry(routine, Vec.empty), Kernel),
          geometry,
          Map.empty,
          memory,
          TaskControl(observer = Some(observer))
        ).left.map(_.message.raw).flatMap(_ => captured.toRight("the run did not reach its end"))
      )
  }

  /** `instructions` for the lane `lane` alone, which `selector` marks by comparing `identity` with the lane. */
  def only(
      selector: VMIR.Predicate,
      identity: VMIR.Value,
      lane: Int,
      instructions: Vec[VMIR.Instruction]
  ): Vec[VMIR.PredicatedInstruction] =
    VMIR.PredicatedInstruction(
      VMIR.Instruction.SetPredicate(
        selector,
        VMIR.Comparison.Equal,
        VMIR.NumericFormat.Unsigned(32),
        identity,
        VMIR.Value.Immediate(lane.toLong)
      ),
      None
    ) +: instructions.map(instruction =>
      VMIR.PredicatedInstruction(instruction, Some(VMIR.ExecutionPredicate(selector, negated = false)))
    )

  /** The register writes of `settings`, each lane's in turn. */
  def laneSetup(
      selector: VMIR.Predicate,
      identity: VMIR.Value,
      lanes: Int,
      settings: Map[(Int, VMIR.Register), Long]
  ): Vec[VMIR.PredicatedInstruction] = Vec.tabulate(lanes) { lane =>
    val writes = settings.toVec.collect { case ((owner, slot), bits) if owner == lane => (slot, bits) }
    if (writes.isEmpty) Vec.empty[VMIR.PredicatedInstruction]
    else only(
      selector,
      identity,
      lane,
      writes.map((slot, bits) => VMIR.Instruction.Move(slot, VMIR.Value.Immediate(bits), 64))
    )
  }.flatten
}

/** One lane alone in its warp, block and grid, as the reference launches it, and the constant bank it reads, whose
  * bytes follow the seed. Warp collectives see only the lane itself.
  */
class NvidiaHarness(val seed: Long) {

  /** `raw` as the prelude type `T`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.asInstanceOf"))
  private def value[T](raw: Long)(using typing: Type.Reified[T]): T =
    if (typing.tpe == Type.Bool) (raw != 0L).asInstanceOf[T] else raw.asInstanceOf[T]

  private class Lane extends Warp {
    def laneId(): UInt[32]                                = UInt[32](0)
    def warpId(): UInt[32]                                = UInt[32](0)
    def lanes(): UInt[32]                                 = UInt[32](1)
    def activeMask(): UInt[32]                            = UInt[32](1)
    def liveMask(): UInt[32]                              = UInt[32](1)
    def laneMaskEqual(): UInt[32]                         = UInt[32](1)
    def laneMaskLess(): UInt[32]                          = UInt[32](0)
    def laneMaskLessEqual(): UInt[32]                     = UInt[32](1)
    def laneMaskGreater(): UInt[32]                       = UInt[32](0xfffffffeL)
    def laneMaskGreaterEqual(): UInt[32]                  = UInt[32](0xffffffffL)
    def members(mask: UInt[32]): UInt[32]                 = mask & 1L
    def participants(mask: UInt[32]): UInt[32]            = alone(mask & 1L, mask)
    def ballot(predicate: Bool, mask: UInt[32]): UInt[32] = if (predicate) mask & 1L else UInt[32](0)

    def shuffle[T](value: T, lane: UInt[32]): T =
      if (lane == UInt[32](0)) value else throw IllegalArgumentException(s"lane ${lane.raw} is not in the warp")

    def leader[T](value: T): T = value

    def matchAny[T](value: T, members: UInt[32]): UInt[32] = alone(members & 1L, members)
    def matchAll[T](value: T, members: UInt[32]): UInt[32] = alone(members & 1L, members)

    private def alone[T](value: T, members: UInt[32]): T =
      if ((members & 1L) != UInt[32](0)) value else throw IllegalArgumentException("the lane does not participate")

    def reduceAdd[T](value: T, members: UInt[32])(using Type.Reified[T]): T     = alone(value, members)
    def reduceAnd[T](value: T, members: UInt[32])(using Type.Reified[T]): T     = alone(value, members)
    def reduceOr[T](value: T, members: UInt[32])(using Type.Reified[T]): T      = alone(value, members)
    def reduceXor[T](value: T, members: UInt[32])(using Type.Reified[T]): T     = alone(value, members)
    def reduceMinimum[T](value: T, members: UInt[32])(using Type.Reified[T]): T = alone(value, members)
    def reduceMaximum[T](value: T, members: UInt[32])(using Type.Reified[T]): T = alone(value, members)

    def reduceMinimumNumber[T](value: T, members: UInt[32])(using Type.Reified[T]): T = alone(value, members)
    def reduceMaximumNumber[T](value: T, members: UInt[32])(using Type.Reified[T]): T = alone(value, members)
  }

  private class Single extends Launch {
    def threadIdX(): UInt[32]          = UInt[32](0)
    def threadIdY(): UInt[32]          = UInt[32](0)
    def threadIdZ(): UInt[32]          = UInt[32](0)
    def blockIdX(): UInt[32]           = UInt[32](0)
    def blockIdY(): UInt[32]           = UInt[32](0)
    def blockIdZ(): UInt[32]           = UInt[32](0)
    def blockDimX(): UInt[32]          = UInt[32](1)
    def blockDimY(): UInt[32]          = UInt[32](1)
    def blockDimZ(): UInt[32]          = UInt[32](1)
    def gridDimX(): UInt[32]           = UInt[32](1)
    def gridDimY(): UInt[32]           = UInt[32](1)
    def gridDimZ(): UInt[32]           = UInt[32](1)
    def dynamicSharedBytes(): UInt[32] = UInt[32](0)
    def multiprocessorId(): UInt[32]   = UInt[32](0)
    def clusterCtaRank(): UInt[32]     = UInt[32](0)
    def clusterCtaCount(): UInt[32]    = UInt[32](1)
    def clusterCtaIdX(): UInt[32]      = UInt[32](0)
    def clusterCtaIdY(): UInt[32]      = UInt[32](0)
    def clusterCtaIdZ(): UInt[32]      = UInt[32](0)
    def clusterShapeX(): UInt[32]      = UInt[32](1)
    def clusterShapeY(): UInt[32]      = UInt[32](1)
    def clusterShapeZ(): UInt[32]      = UInt[32](1)
    def clusterIdX(): UInt[32]         = UInt[32](0)
    def clusterIdY(): UInt[32]         = UInt[32](0)
    def clusterIdZ(): UInt[32]         = UInt[32](0)
    def clusterCountX(): UInt[32]      = UInt[32](1)
    def clusterCountY(): UInt[32]      = UInt[32](1)
    def clusterCountZ(): UInt[32]      = UInt[32](1)
    def explicitCluster(): UInt[32]    = UInt[32](0)

    def cancelledCta(value: UInt[64], high: UInt[64]): UInt[64] =
      if ((high & 1L) != UInt[64](0)) value else throw IllegalArgumentException("the cancellation failed")

    // Counters are never staged into a checked definition.
    def clock(): UInt[64]       = throw UnsupportedOperationException("clock")
    def globalTimer(): UInt[64] = throw UnsupportedOperationException("globalTimer")
  }

  private class Bank extends Constants {
    def load[T](bank: UInt[32], offset: UInt[32])(using typing: Type.Reified[T]): T = {
      if (bank.raw != InstructionSemantics.ConstantBank.toLong)
        throw IllegalArgumentException(s"bank ${bank.raw} is not bound")
      val bytes = typing.tpe.bits / 8
      if (offset.raw + bytes > InstructionSemantics.ConstantBytes) throw IllegalArgumentException("past the bank")
      value((0 until bytes).foldLeft(0L)((raw, index) =>
        raw | ((NvidiaHarness.constant(seed, offset.raw + index) & 0xffL) << (8 * index))
      ))
    }
  }

  given Warp      = Lane()
  given Launch    = Single()
  given Constants = Bank()
}
