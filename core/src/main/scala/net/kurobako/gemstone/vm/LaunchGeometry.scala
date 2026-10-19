package net.kurobako.gemstone.vm

import LaunchGeometry.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.rpc.Wire.*

object LaunchGeometry {
  object Dimensions {
    val One: Dimensions = Dimensions(1, 1, 1)
  }

  case class Dimensions(
      x: Int > 0,
      y: Int > 0,
      z: Int > 0
  ) derives Codec, CanEqual {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(x, y, z)

    def volume: Either[NonEmpty[String], Long > 0] =
      try Right(x.widen[Long > 0] * y.widen[Long > 0] * z.widen[Long > 0])
      catch {
        case _: ArithmeticException =>
          Left(s"dimension volume overflows 64 bits: ($x, $y, $z)")
      }

    def volumeInt: Either[NonEmpty[String], Int > 0] =
      volume.flatMap(size =>
        if (size <= Int.MaxValue.toLong) Right(size.toIntExact)
        else Left(s"dimension volume exceeds Int range: $size")
      )

    /** Coordinates of a linear `index` below the volume, with x varying fastest. */
    def coordinates(index: Long >= 0): Coordinates = {
      val width: Long > 0  = x.widen[Long > 0]
      val height: Long > 0 = y.widen[Long > 0]
      val rows             = index / width
      Coordinates((index % width).toIntExact, (rows % height).toIntExact, (rows / height).toIntExact)
    }
  }

  object Coordinates {
    val origin: Coordinates = Coordinates(0, 0, 0)
  }

  case class Coordinates(x: Int >= 0, y: Int >= 0, z: Int >= 0) derives Codec

  case class GlobalDimensions(
      x: Long > 0,
      y: Long > 0,
      z: Long > 0
  ) derives Codec

  case class LongCoordinates(x: Long >= 0, y: Long >= 0, z: Long >= 0)

  enum Axis derives CanEqual {
    case X, Y, Z

    def select(value: Coordinates): Int >= 0 = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }

    def select(value: Dimensions): Int > 0 = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }

    def select(value: GlobalDimensions): Long > 0 = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }
  }

  def forLaunch(
      groupSize: Dimensions,
      groupCount: Dimensions,
      globalSize: Option[GlobalDimensions] = None,
      workDim: Int in 1 ~ 3 = 3,
      globalOffset: Coordinates = Coordinates.origin
  ): Either[NonEmpty[String], LaunchGeometry] =
    for {
      padded      <- paddedExtent(groupSize, groupCount)
      groupVolume <- groupSize.volumeInt
      groupTotal  <- groupCount.volume
      exact = globalSize.getOrElse(padded)
      _ <- Either.cond[NonEmpty[String], Unit](
        exact.x <= padded.x && exact.y <= padded.y && exact.z <= padded.z,
        (),
        s"global size $exact exceeds padded dispatch size $padded"
      )
    } yield LaunchGeometry(groupSize, groupCount, workDim, globalOffset, exact, groupVolume, groupTotal)

  private def paddedExtent(groupSize: Dimensions, groupCount: Dimensions): Either[NonEmpty[String], GlobalDimensions] =
    try
      Right(
        GlobalDimensions(
          groupSize.x.widen[Long > 0] * groupCount.x.widen[Long > 0],
          groupSize.y.widen[Long > 0] * groupCount.y.widen[Long > 0],
          groupSize.z.widen[Long > 0] * groupCount.z.widen[Long > 0]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"padded launch extent overflows 64 bits: $groupSize * $groupCount")
    }
}

/** A validated launch: `groupVolume` work-items per group, `groupTotal` groups, and a global extent that every group's
  * padded origin stays below. Coordinate queries take indices below those volumes.
  */
case class LaunchGeometry private (
    groupSize: Dimensions,
    groupCount: Dimensions,
    workDim: Int in 1 ~ 3,
    globalOffset: Coordinates,
    globalExtent: GlobalDimensions,
    groupVolume: Int > 0,
    groupTotal: Long > 0
) {

  /** Every work-item of every group lies inside the global extent. */
  val uniform: Boolean =
    globalExtent.x.raw == groupSize.x.toLong * groupCount.x &&
      globalExtent.y.raw == groupSize.y.toLong * groupCount.y &&
      globalExtent.z.raw == groupSize.z.toLong * groupCount.z

  def localCoordinates(index: Int >= 0): Coordinates = groupSize.coordinates(index.widen[Long >= 0])

  def groupCoordinates(index: Long >= 0): Coordinates = groupCount.coordinates(index)

  def activeGroupSize(group: Coordinates): Either[NonEmpty[String], Dimensions] =
    for {
      activeX <- activeSize(Axis.X, group)
      activeY <- activeSize(Axis.Y, group)
      activeZ <- activeSize(Axis.Z, group)
    } yield Dimensions(activeX, activeY, activeZ)

  def globalCoordinates(group: Coordinates, local: Coordinates): LongCoordinates =
    LongCoordinates(global(Axis.X, group, local), global(Axis.Y, group, local), global(Axis.Z, group, local))

  def containsGlobal(coordinates: LongCoordinates): Boolean =
    contains(coordinates.x, globalOffset.x.toLong, globalExtent.x) &&
      contains(coordinates.y, globalOffset.y.toLong, globalExtent.y) &&
      contains(coordinates.z, globalOffset.z.toLong, globalExtent.z)

  /** Whether `local` within `group` lies inside the global extent. */
  def contains(group: Coordinates, local: Coordinates): Boolean =
    uniform || (inside(Axis.X, group, local) && inside(Axis.Y, group, local) && inside(Axis.Z, group, local))

  /** Whether every work-item of `group` lies inside the global extent. */
  def isFull(group: Coordinates): Boolean =
    uniform || (full(Axis.X, group) && full(Axis.Y, group) && full(Axis.Z, group))

  /** Bit `i` is set when local linear index `start + i` of `group` lies inside the global extent, for `i < count`. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def liveLanes(group: Coordinates, start: Int >= 0, count: Int in 1 ~ 64): Long =
    if (isFull(group)) Bits.mask(count)
    else {
      var mask           = 0L
      var lane: Int >= 0 = 0
      while (lane < count) {
        if (contains(group, localCoordinates(start + lane))) mask |= 1L << lane
        lane += 1
      }
      mask
    }

  private def global(axis: Axis, group: Coordinates, local: Coordinates): Long >= 0 =
    axis.select(globalOffset).widen[Long >= 0] +
      axis.select(group).widen[Long >= 0] * axis.select(groupSize).widen[Long >= 0] +
      axis.select(local).widen[Long >= 0]

  private def origin(axis: Axis, group: Coordinates): Long =
    axis.select(group).toLong * axis.select(groupSize)

  private def inside(axis: Axis, group: Coordinates, local: Coordinates): Boolean =
    origin(axis, group) + axis.select(local) < axis.select(globalExtent)

  private def full(axis: Axis, group: Coordinates): Boolean =
    origin(axis, group) + axis.select(groupSize) <= axis.select(globalExtent)

  private def activeSize(axis: Axis, group: Coordinates): Either[NonEmpty[String], Int > 0] = {
    val remaining = axis.select(globalExtent).raw - origin(axis, group)
    if (remaining <= 0L) Left(s"work-group coordinate $group has no active work-items on axis $axis")
    else Right(math.min(axis.select(groupSize).toLong, remaining).toInt.unsafe[Int > 0])
  }

  private def contains(value: Long, offset: Long, extent: Long): Boolean =
    value >= offset && value - offset < extent
}
