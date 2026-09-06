package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian

opaque type AllocationId = Long

object AllocationId {
  def fromLong(value: Long): Either[String, AllocationId] =
    Either.cond(value > 0L, value, s"allocation ID must be positive: $value")

  private[gemstone] def unsafe(value: Long): AllocationId = value

  extension (value: AllocationId) def toLong: Long = value
}

opaque type ProgramId = Long

object ProgramId {
  def fromLong(value: Long): Either[String, ProgramId] =
    Either.cond(value > 0L, value, s"program ID must be positive: $value")

  private[gemstone] def unsafe(value: Long): ProgramId = value

  extension (value: ProgramId) def toLong: Long = value
}

opaque type CompletionId = Long

object CompletionId {
  def fromLong(value: Long): Either[String, CompletionId] =
    Either.cond(value > 0L, value, s"completion ID must be positive: $value")

  private[gemstone] def unsafe(value: Long): CompletionId = value

  extension (value: CompletionId) def toLong: Long = value
}

enum ProgramFormat {
  case PTX
  case AMDGPUCodeObject
  case AMDGPURawKernel
  case SPIRV
}

final case class Dimensions private (x: Int, y: Int, z: Int)

extension (value: Dimensions) {
  def volume: Either[String, Long] =
    try Right(Math.multiplyExact(Math.multiplyExact(value.x.toLong, value.y.toLong), value.z.toLong))
    catch {
      case _: ArithmeticException => Left(s"dimension volume overflows 64 bits: (${value.x}, ${value.y}, ${value.z})")
    }

  def volumeInt: Either[String, Int] =
    value.volume.flatMap(size =>
      Either.cond(size <= Int.MaxValue.toLong, size.toInt, s"dimension volume exceeds Int range: $size")
    )

  def multiply(other: Dimensions): Either[String, Dimensions] =
    try
      Dimensions.create(
        Math.multiplyExact(value.x, other.x),
        Math.multiplyExact(value.y, other.y),
        Math.multiplyExact(value.z, other.z)
      )
    catch {
      case _: ArithmeticException =>
        Left(s"dimension product overflows Int range: $value * $other")
    }
}

object Dimensions {
  val One: Dimensions = Dimensions(1, 1, 1)

  def create(x: Int, y: Int, z: Int): Either[String, Dimensions] =
    Either.cond(x > 0 && y > 0 && z > 0, Dimensions(x, y, z), s"dimensions must be positive: ($x, $y, $z)")
}

final case class Coordinates(x: Int, y: Int, z: Int)

/** A global work-item coordinate or extent. Work-group dimensions remain Int-sized, but a padded global launch may
  * legitimately exceed Int.MaxValue when it is represented as a virtual dispatch.
  */
final case class LongCoordinates(x: Long, y: Long, z: Long)

enum Axis {
  case X, Y, Z

  def select(value: Coordinates): Int = this match {
    case X => value.x
    case Y => value.y
    case Z => value.z
  }

  def select(value: Dimensions): Int = this match {
    case X => value.x
    case Y => value.y
    case Z => value.z
  }
}

final case class LaunchGeometry private (
    groupSize: Dimensions,
    groupCount: Dimensions,
    globalSize: Dimensions,
    workDim: Int,
    globalOffset: Coordinates,
    private val globalSizeLong: LongCoordinates
) {
  def localCoordinates(index: Int): Coordinates = coordinates(index.toLong, groupSize)

  def groupCoordinates(index: Long): Coordinates = coordinates(index, groupCount)

  def globalCoordinates(group: Coordinates, local: Coordinates): Coordinates =
    Coordinates(
      Math.addExact(globalOffset.x, Math.addExact(Math.multiplyExact(group.x, groupSize.x), local.x)),
      Math.addExact(globalOffset.y, Math.addExact(Math.multiplyExact(group.y, groupSize.y), local.y)),
      Math.addExact(globalOffset.z, Math.addExact(Math.multiplyExact(group.z, groupSize.z), local.z))
    )

  def globalCoordinatesLong(group: Coordinates, local: Coordinates): LongCoordinates =
    LongCoordinates(
      Math.addExact(
        globalOffset.x.toLong,
        Math.addExact(Math.multiplyExact(group.x.toLong, groupSize.x), local.x.toLong)
      ),
      Math.addExact(
        globalOffset.y.toLong,
        Math.addExact(Math.multiplyExact(group.y.toLong, groupSize.y), local.y.toLong)
      ),
      Math.addExact(
        globalOffset.z.toLong,
        Math.addExact(Math.multiplyExact(group.z.toLong, groupSize.z), local.z.toLong)
      )
    )

  def containsGlobal(coordinates: Coordinates): Boolean =
    coordinates.x >= globalOffset.x && coordinates.x - globalOffset.x < globalSize.x &&
      coordinates.y >= globalOffset.y && coordinates.y - globalOffset.y < globalSize.y &&
      coordinates.z >= globalOffset.z && coordinates.z - globalOffset.z < globalSize.z

  def containsGlobal(coordinates: LongCoordinates): Boolean =
    coordinates.x >= globalOffset.x.toLong && coordinates.x - globalOffset.x.toLong < globalSizeLong.x &&
      coordinates.y >= globalOffset.y.toLong && coordinates.y - globalOffset.y.toLong < globalSizeLong.y &&
      coordinates.z >= globalOffset.z.toLong && coordinates.z - globalOffset.z.toLong < globalSizeLong.z

  /** The exact long-width padded global extent, including virtual launches larger than Int.MaxValue. */
  def globalSizeLongValue: LongCoordinates = globalSizeLong

  private def coordinates(index: Long, dimensions: Dimensions): Coordinates = {
    val volume = dimensions.volume.fold(message => throw IllegalArgumentException(message), identity)
    require(index >= 0L && index < volume, s"linear index $index is outside dimensions $dimensions")
    val x  = (index % dimensions.x.toLong).toInt
    val yz = index / dimensions.x.toLong
    val y  = (yz    % dimensions.y.toLong).toInt
    val z  = (yz / dimensions.y.toLong).toInt
    Coordinates(x, y, z)
  }
}

object LaunchGeometry {
  def create(
      groupSize: Dimensions,
      groupCount: Dimensions,
      globalSize: Option[Dimensions] = None,
      workDim: Int = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, LaunchGeometry] = for {
    padded <- groupSize.multiply(groupCount)
    exact = globalSize.getOrElse(padded)
    _ <- Either.cond(workDim >= 1 && workDim <= 3, (), s"work dimension must be between 1 and 3: $workDim")
    _ <- Either.cond(
      globalOffset.x >= 0 && globalOffset.y >= 0 && globalOffset.z >= 0,
      (),
      s"global offset must be non-negative: $globalOffset"
    )
    _ <- Either.cond(
      exact.x <= padded.x && exact.y <= padded.y && exact.z <= padded.z,
      (),
      s"global size $exact exceeds padded dispatch size $padded"
    )
  } yield LaunchGeometry(
    groupSize,
    groupCount,
    exact,
    workDim,
    globalOffset,
    LongCoordinates(exact.x.toLong, exact.y.toLong, exact.z.toLong)
  )

  /** Build a padded geometry when the per-axis global extent exceeds the legacy Int-sized Dimensions representation.
    * The legacy [[LaunchGeometry.globalSize]] field is saturated for diagnostics; execution uses the exact long-width
    * extent exposed through [[LaunchGeometry.containsGlobal(LongCoordinates)]].
    */
  def createVirtual(
      groupSize: Dimensions,
      groupCount: Dimensions,
      workDim: Int = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, LaunchGeometry] = for {
    _ <- Either.cond(workDim >= 1 && workDim <= 3, (), s"work dimension must be between 1 and 3: $workDim")
    _ <- Either.cond(
      globalOffset.x >= 0 && globalOffset.y >= 0 && globalOffset.z >= 0,
      (),
      s"global offset must be non-negative: $globalOffset"
    )
    x = Math.multiplyExact(groupSize.x.toLong, groupCount.x.toLong)
    y = Math.multiplyExact(groupSize.y.toLong, groupCount.y.toLong)
    z = Math.multiplyExact(groupSize.z.toLong, groupCount.z.toLong)
    legacy <- Dimensions.create(
      Math.min(x, Int.MaxValue.toLong).toInt,
      Math.min(y, Int.MaxValue.toLong).toInt,
      Math.min(z, Int.MaxValue.toLong).toInt
    )
  } yield LaunchGeometry(groupSize, groupCount, legacy, workDim, globalOffset, LongCoordinates(x, y, z))
}

enum KernelArgument {
  case Scalar(bytes: Col[Byte])
  case Allocation(id: AllocationId, offset: Long)
  case Local(size: Long)
}

/** A pointer-sized word in mirrored allocation memory that must be interpreted as an allocation pointer. */
final case class PointerRelocation(
    sourceAllocation: Option[AllocationId],
    sourceArgument: Option[Int],
    sourceOffset: Long,
    target: AllocationId,
    targetOffset: Long
)

final case class KernelDispatch private (
    program: ProgramId,
    kernelName: String,
    geometry: LaunchGeometry,
    privateSegmentBytes: Int,
    groupSegmentBytes: Int,
    arguments: Col[KernelArgument],
    pointerRelocations: Col[PointerRelocation]
)

object KernelDispatch {
  def create(
      program: ProgramId,
      kernelName: String,
      groupSize: Dimensions,
      groupCount: Dimensions,
      arguments: Col[KernelArgument],
      globalSize: Option[Dimensions] = None,
      privateSegmentBytes: Int = 0,
      groupSegmentBytes: Int = 0,
      pointerRelocations: Col[PointerRelocation] = Col.empty,
      workDim: Int = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, KernelDispatch] = for {
    _ <- Either.cond(
      kernelName.nonEmpty && !kernelName.contains('\u0000'),
      (),
      "kernel name must be non-empty and contain no NUL"
    )
    geometry <- LaunchGeometry.create(groupSize, groupCount, globalSize, workDim, globalOffset)
    _        <- Either.cond(
      privateSegmentBytes >= 0 && groupSegmentBytes >= 0,
      (),
      s"segment sizes must be non-negative: private=$privateSegmentBytes, group=$groupSegmentBytes"
    )
    _ <- Either.cond(
      pointerRelocations.forall(relocation =>
        (relocation.sourceAllocation.isDefined ^ relocation.sourceArgument.isDefined) &&
          relocation.sourceArgument.forall(index => index >= 0 && index < arguments.size) &&
          relocation.sourceOffset >= 0L && relocation.targetOffset >= 0L
      ),
      (),
      "pointer relocations must have one valid source, an in-range source argument, and non-negative offsets"
    )
  } yield KernelDispatch(
    program,
    kernelName,
    geometry,
    privateSegmentBytes,
    groupSegmentBytes,
    arguments,
    pointerRelocations
  )
}

enum ExecutionFailure {
  case InvalidRequest(detail: String)
  case NotFound(detail: String)
  case Unsupported(detail: String)
  case Failed(detail: String)

  def message: String = this match {
    case InvalidRequest(value) => value
    case NotFound(value)       => value
    case Unsupported(value)    => value
    case Failed(value)         => value
  }
}

enum CompletionStatus {
  case Pending
  case Succeeded
  case Failed(error: ExecutionFailure)
}

final case class DirtyRange(allocation: AllocationId, offset: Long, size: Int)

trait ExecutionMemory {
  def read(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Col[Byte]]
  def write(id: AllocationId, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit]

  /** Allocation-free override point for ISA memory operations up to 64 bits. */
  def readValue(id: AllocationId, offset: Long, size: Int): Either[ExecutionFailure, Long] = {
    require(size >= 1 && size <= 8, s"memory value size must be in [1, 8], found $size")
    read(id, offset, size).map(bytes => LittleEndian.decode(bytes, 0, size))
  }

  /** Allocation-free override point for ISA memory operations up to 64 bits. */
  def writeValue(id: AllocationId, offset: Long, value: Long, size: Int): Either[ExecutionFailure, Unit] = {
    require(size >= 1 && size <= 8, s"memory value size must be in [1, 8], found $size")
    write(id, offset, LittleEndian.toCol(value, size))
  }

  /** Records a process-neutral pointer written into mirrored allocation memory. */
  def recordPointerRelocation(relocation: PointerRelocation): Unit
}

trait LoadedProgram {

  /** Copy bytes into a named device global, when the program format exposes one. */
  def writeGlobal(name: String, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals are not writable"))

  def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit]
}

trait ProgramLoader {
  def format: ProgramFormat
  def load(image: Col[Byte]): Either[ExecutionFailure, LoadedProgram]
}

trait ProgramExecutor {
  def load(id: ProgramId, format: ProgramFormat, image: Col[Byte]): Either[ExecutionFailure, Unit]
  def unload(id: ProgramId): Either[ExecutionFailure, Unit]
  def writeGlobal(id: ProgramId, name: String, offset: Long, bytes: Col[Byte]): Either[ExecutionFailure, Unit] =
    Left(ExecutionFailure.Unsupported("program globals are not writable"))

  def launch(
      dispatch: KernelDispatch,
      memory: ExecutionMemory,
      control: ExecutionControl
  ): Either[ExecutionFailure, Unit]
}
