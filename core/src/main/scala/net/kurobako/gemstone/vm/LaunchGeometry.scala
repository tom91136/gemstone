package net.kurobako.gemstone.vm

import LaunchGeometry.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

object LaunchGeometry {
  object Dimensions {
    val One: Dimensions = Dimensions(1, 1, 1)
  }

  case class Dimensions(
      x: Int > 0,
      y: Int > 0,
      z: Int > 0
  ) {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(x.toLong, y.toLong, z.toLong)

    def volume: Either[NonEmpty[String], Long > 0] =
      try Right(x.toLong.widen[Long > 0] * y.toLong.widen[Long > 0] * z.toLong.widen[Long > 0])
      catch {
        case _: ArithmeticException =>
          Left(s"dimension volume overflows 64 bits: ($x, $y, $z)")
      }

    def volumeInt: Either[NonEmpty[String], Int > 0] =
      volume.flatMap(size =>
        if (size.raw <= Int.MaxValue.toLong) Right(size.toIntExact)
        else Left(s"dimension volume exceeds Int range: $size")
      )

    def coordinates(index: Long): Either[NonEmpty[String], Coordinates] =
      volume.flatMap { size =>
        Either.cond(
          index >= 0L && index < size.raw, {
            val coordinateX  = (index        % x.raw.toLong).toInt
            val coordinateYZ = index / x.raw.toLong
            val coordinateY  = (coordinateYZ % y.raw.toLong).toInt
            val coordinateZ  = (coordinateYZ / y.raw.toLong).toInt
            Coordinates(coordinateX.unsafe[Int >= 0], coordinateY.unsafe[Int >= 0], coordinateZ.unsafe[Int >= 0])
          },
          s"linear index $index is outside dimensions $this"
        )
      }
  }

  object Coordinates {
    val origin: Coordinates = Coordinates(0, 0, 0)
  }

  case class Coordinates(x: Int >= 0, y: Int >= 0, z: Int >= 0)

  case class GlobalDimensions(
      x: Long > 0,
      y: Long > 0,
      z: Long > 0
  )

  case class LongCoordinates(x: Long >= 0, y: Long >= 0, z: Long >= 0)

  enum Axis {
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
  }

  def forLaunch(
      groupSize: Dimensions,
      groupCount: Dimensions,
      globalSize: Option[GlobalDimensions] = None,
      workDim: Int in 1 ~ 3 = 3,
      globalOffset: Coordinates = Coordinates.origin
  ): Either[NonEmpty[String], LaunchGeometry] =
    for {
      padded <- paddedExtent(groupSize, groupCount)
      exact = globalSize.getOrElse(padded)
      _ <- Either.cond[NonEmpty[String], Unit](
        exact.x.raw <= padded.x.raw && exact.y.raw <= padded.y.raw && exact.z.raw <= padded.z.raw,
        (),
        s"global size $exact exceeds padded dispatch size $padded"
      )
    } yield LaunchGeometry(groupSize, groupCount, workDim, globalOffset, exact)

  def virtual(
      groupSize: Dimensions,
      groupCount: Dimensions,
      workDim: Int in 1 ~ 3 = 3,
      globalOffset: Coordinates = Coordinates.origin
  ): Either[NonEmpty[String], LaunchGeometry] =
    paddedExtent(groupSize, groupCount).map(extent =>
      LaunchGeometry(groupSize, groupCount, workDim, globalOffset, extent)
    )

  private def paddedExtent(groupSize: Dimensions, groupCount: Dimensions): Either[NonEmpty[String], GlobalDimensions] =
    try
      Right(
        GlobalDimensions(
          groupSize.x.toLong.widen[Long > 0] * groupCount.x.toLong.widen[Long > 0],
          groupSize.y.toLong.widen[Long > 0] * groupCount.y.toLong.widen[Long > 0],
          groupSize.z.toLong.widen[Long > 0] * groupCount.z.toLong.widen[Long > 0]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"padded launch extent overflows 64 bits: $groupSize * $groupCount")
    }
}

case class LaunchGeometry private (
    groupSize: Dimensions,
    groupCount: Dimensions,
    workDim: Int in 1 ~ 3,
    globalOffset: Coordinates,
    globalExtent: GlobalDimensions
) {
  def localCoordinates(index: Int): Either[NonEmpty[String], Coordinates] =
    groupSize.coordinates(index.toLong)

  def groupCoordinates(index: Long): Either[NonEmpty[String], Coordinates] =
    groupCount.coordinates(index)

  def activeGroupSize(group: Coordinates): Either[NonEmpty[String], Dimensions] =
    for {
      activeX <- activeSize(Axis.X, group, group.x, groupSize.x, groupCount.x, globalExtent.x)
      activeY <- activeSize(Axis.Y, group, group.y, groupSize.y, groupCount.y, globalExtent.y)
      activeZ <- activeSize(Axis.Z, group, group.z, groupSize.z, groupCount.z, globalExtent.z)
    } yield Dimensions(activeX, activeY, activeZ)

  def globalCoordinatesLong(
      group: Coordinates,
      local: Coordinates
  ): Either[NonEmpty[String], LongCoordinates] =
    try
      Right(
        LongCoordinates(
          globalOffset.x.toLong.widen[Long >= 0] +
            group.x.toLong.widen[Long >= 0] * groupSize.x.toLong.widen[Long > 0] + local.x.toLong.widen[Long >= 0],
          globalOffset.y.toLong.widen[Long >= 0] +
            group.y.toLong.widen[Long >= 0] * groupSize.y.toLong.widen[Long > 0] + local.y.toLong.widen[Long >= 0],
          globalOffset.z.toLong.widen[Long >= 0] +
            group.z.toLong.widen[Long >= 0] * groupSize.z.toLong.widen[Long > 0] + local.z.toLong.widen[Long >= 0]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"global coordinate overflows Long range: group=$group, local=$local")
    }

  def containsGlobal(coordinates: LongCoordinates): Boolean =
    contains(coordinates.x.raw, globalOffset.x.raw.toLong, globalExtent.x.raw) &&
      contains(coordinates.y.raw, globalOffset.y.raw.toLong, globalExtent.y.raw) &&
      contains(coordinates.z.raw, globalOffset.z.raw.toLong, globalExtent.z.raw)

  private def activeSize(
      axis: Axis,
      group: Coordinates,
      groupIndex: Int >= 0,
      nominalSize: Int > 0,
      groupLimit: Int > 0,
      globalSize: Long > 0
  ): Either[NonEmpty[String], Int > 0] =
    if (groupIndex >= groupLimit)
      Left(s"work-group coordinate $group is outside group count $groupCount")
    else
      try {
        val origin    = (groupIndex.toLong.widen[Long >= 0] * nominalSize.toLong.widen[Long > 0]).raw
        val remaining = Math.subtractExact(globalSize.raw, origin)
        math.min(nominalSize.raw.toLong, remaining).refine[Long > 0].map(_.toIntExact).left.map { _ =>
          s"work-group coordinate $group has no active work-items on axis $axis"
        }
      } catch {
        case _: ArithmeticException =>
          Left(s"active size overflows on axis $axis for work-group coordinate $group")
      }

  private def contains(value: Long, offset: Long, extent: Long): Boolean =
    value >= offset && value - offset < extent
}
