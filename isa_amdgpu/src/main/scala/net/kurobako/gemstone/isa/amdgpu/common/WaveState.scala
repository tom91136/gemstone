package net.kurobako.gemstone.isa.amdgpu.common

import net.kurobako.gemstone.execution.Effects
import net.kurobako.gemstone.execution.ExecutionFault
import net.kurobako.gemstone.execution.LaneMask
import net.kurobako.gemstone.execution.Pc
import net.kurobako.gemstone.execution.Write

/** A write to AMD architectural state. Values are captured while evaluating an instruction and committed only after
  * every destination has been validated, so every source read observes the pre-instruction wave state.
  */
enum AMDWrite extends Write {
  case Sgpr(index: Int, value: Int)
  case Vgpr(index: Int, lane: Int, value: Int)
  case Agpr(index: Int, lane: Int, value: Int)
  case Exec(value: LaneMask)
  case Vcc(value: LaneMask)
  case Scc(value: Boolean)
  case M0(value: Int)
}

/** Mutable state for one AMD wave. Mutation is deliberately restricted to atomic [[Effects]] commits. */
final class WaveState private (
    val target: Target,
    val laneCount: Int,
    private var pcValue: Pc,
    private val scalarRegisters: Array[Int],
    private val vectorRegisters: Array[Int],
    private val accumulatorRegisters: Array[Int],
    private var execValue: LaneMask,
    private var vccValue: LaneMask,
    private var sccValue: Boolean,
    private var m0Value: Int
) {
  val sgprCount: Int = scalarRegisters.length
  val vgprCount: Int = vectorRegisters.length / laneCount
  val agprCount: Int = accumulatorRegisters.length / laneCount

  def pc: Pc         = pcValue
  def exec: LaneMask = execValue
  def vcc: LaneMask  = vccValue
  def scc: Boolean   = sccValue
  def m0: Int        = m0Value

  def readSgpr(index: Int): Either[ExecutionFault, Int] =
    scalarRegisters.lift(index).toRight(ExecutionFault.InvalidState(s"SGPR index $index is out of range"))

  def readVgpr(index: Int, lane: Int): Either[ExecutionFault, Int] =
    flatIndex(index, lane, vgprCount, "VGPR").map(flat => vectorRegisters(flat))

  def readAgpr(index: Int, lane: Int): Either[ExecutionFault, Int] =
    flatIndex(index, lane, agprCount, "AGPR").map(flat => accumulatorRegisters(flat))

  /** Commit is all-or-nothing. Duplicate destinations are rejected instead of making write order observable. */
  def commit(effects: Effects[AMDWrite]): Either[ExecutionFault, Unit] = {
    val writes    = effects.snapshot
    val duplicate = writes.groupBy(destination).collectFirst { case (name, values) if values.size > 1 => name }
    duplicate match {
      case Some(name) =>
        effects.clear()
        Left(ExecutionFault.InvalidState(s"multiple writes to $name in one AMD instruction"))
      case None => effects.commit(validate, applyWrite)
    }
  }

  /** PC control is applied by the scheduler after a successful effects commit. */
  private[amdgpu] def setPc(value: Pc): Unit = pcValue = value

  private def validate(write: AMDWrite): Either[ExecutionFault, Unit] = write match {
    case AMDWrite.Sgpr(index, _)          => validRegister(index, sgprCount, "SGPR")
    case AMDWrite.Vgpr(index, lane, _)    => validLaneRegister(index, lane, vgprCount, "VGPR")
    case AMDWrite.Agpr(index, lane, _)    => validLaneRegister(index, lane, agprCount, "AGPR")
    case AMDWrite.Exec(value)             => validMask(value, "EXEC")
    case AMDWrite.Vcc(value)              => validMask(value, "VCC")
    case AMDWrite.Scc(_) | AMDWrite.M0(_) => Right(())
  }

  private def applyWrite(write: AMDWrite): Unit = write match {
    case AMDWrite.Sgpr(index, value)       => scalarRegisters(index) = value
    case AMDWrite.Vgpr(index, lane, value) => vectorRegisters(index * laneCount + lane) = value
    case AMDWrite.Agpr(index, lane, value) => accumulatorRegisters(index * laneCount + lane) = value
    case AMDWrite.Exec(value)              => execValue = value
    case AMDWrite.Vcc(value)               => vccValue = value
    case AMDWrite.Scc(value)               => sccValue = value
    case AMDWrite.M0(value)                => m0Value = value
  }

  private def destination(write: AMDWrite): String = write match {
    case AMDWrite.Sgpr(index, _)       => s"s$index"
    case AMDWrite.Vgpr(index, lane, _) => s"v$index:$lane"
    case AMDWrite.Agpr(index, lane, _) => s"a$index:$lane"
    case AMDWrite.Exec(_)              => "exec"
    case AMDWrite.Vcc(_)               => "vcc"
    case AMDWrite.Scc(_)               => "scc"
    case AMDWrite.M0(_)                => "m0"
  }

  private def flatIndex(index: Int, lane: Int, count: Int, kind: String): Either[ExecutionFault, Int] =
    validLaneRegister(index, lane, count, kind).map(_ => index * laneCount + lane)

  private def validLaneRegister(index: Int, lane: Int, count: Int, kind: String): Either[ExecutionFault, Unit] =
    validRegister(index, count, kind).flatMap { _ =>
      if (lane >= 0 && lane < laneCount) Right(())
      else Left(ExecutionFault.InvalidState(s"lane index $lane is outside a $laneCount-lane wave"))
    }

  private def validRegister(index: Int, count: Int, kind: String): Either[ExecutionFault, Unit] =
    if (index >= 0 && index < count) Right(())
    else Left(ExecutionFault.InvalidState(s"$kind index $index is out of range"))

  private def validMask(value: LaneMask, name: String): Either[ExecutionFault, Unit] =
    if (value.subtract(WaveState.laneMask(laneCount)).isEmpty) Right(())
    else Left(ExecutionFault.InvalidState(s"$name contains lanes outside a $laneCount-lane wave"))
}

object WaveState {
  def create(
      target: Target,
      laneCount: Int,
      sgprCount: Int,
      vgprCount: Int,
      agprCount: Int = 0,
      pc: Pc = Pc.Entry
  ): Either[ExecutionFault, WaveState] =
    for {
      active <- LaneMask.all(laneCount)
      _      <- validCount("SGPR", sgprCount)
      _      <- validLaneRegisterCount("VGPR", vgprCount, laneCount)
      _      <- validLaneRegisterCount("AGPR", agprCount, laneCount)
    } yield new WaveState(
      target,
      laneCount,
      pc,
      new Array[Int](sgprCount),
      new Array[Int](vgprCount * laneCount),
      new Array[Int](agprCount * laneCount),
      active,
      LaneMask.Empty,
      false,
      0
    )

  private def validCount(kind: String, count: Int): Either[ExecutionFault, Unit] =
    if (count >= 0) Right(())
    else Left(ExecutionFault.InvalidProgram(s"$kind count must not be negative: $count"))

  private def validLaneRegisterCount(kind: String, count: Int, laneCount: Int): Either[ExecutionFault, Unit] =
    if (count < 0) Left(ExecutionFault.InvalidProgram(s"$kind count must not be negative: $count"))
    else if (count > Int.MaxValue / laneCount)
      Left(ExecutionFault.InvalidProgram(s"$kind storage size overflows: $count registers x $laneCount lanes"))
    else Right(())

  private def laneMask(laneCount: Int): LaneMask = LaneMask.all(laneCount).fold(_ => LaneMask.Empty, identity)
}
