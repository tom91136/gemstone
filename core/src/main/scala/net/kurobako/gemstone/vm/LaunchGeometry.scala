package net.kurobako.gemstone.vm

import LaunchGeometry.*

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final case class LaunchGeometry private (
    groupSize: Dimensions,
    groupCount: Dimensions,
    workDim: Int :| Interval.Closed[1, 3],
    globalOffset: Coordinates,
    globalExtent: GlobalDimensions
) {
  def localCoordinates(index: Int): Either[String, Coordinates] =
    groupSize.coordinates(index.toLong)

  def groupCoordinates(index: Long): Either[String, Coordinates] =
    groupCount.coordinates(index)

  def activeGroupSize(group: Coordinates): Either[String, Dimensions] =
    for {
      activeX <- activeSize(Axis.X, group, group.x, groupSize.x, groupCount.x, globalExtent.x)
      activeY <- activeSize(Axis.Y, group, group.y, groupSize.y, groupCount.y, globalExtent.y)
      activeZ <- activeSize(Axis.Z, group, group.z, groupSize.z, groupCount.z, globalExtent.z)
    } yield Dimensions(activeX, activeY, activeZ)

  def globalCoordinates(
      group: Coordinates,
      local: Coordinates
  ): Either[String, Coordinates] =
    try
      Right(
        Coordinates(
          Math.addExact(globalOffset.x, Math.addExact(Math.multiplyExact(group.x, groupSize.x), local.x))
            .refineUnsafe[Positive0],
          Math.addExact(globalOffset.y, Math.addExact(Math.multiplyExact(group.y, groupSize.y), local.y))
            .refineUnsafe[Positive0],
          Math.addExact(globalOffset.z, Math.addExact(Math.multiplyExact(group.z, groupSize.z), local.z))
            .refineUnsafe[Positive0]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"global coordinate overflows Int range: group=$group, local=$local")
    }

  def globalCoordinatesLong(
      group: Coordinates,
      local: Coordinates
  ): Either[String, LongCoordinates] =
    try
      Right(
        LongCoordinates(
          Math.addExact(
            globalOffset.x.toLong,
            Math.addExact(Math.multiplyExact(group.x.toLong, groupSize.x), local.x.toLong)
          ).refineUnsafe[Positive0],
          Math.addExact(
            globalOffset.y.toLong,
            Math.addExact(Math.multiplyExact(group.y.toLong, groupSize.y), local.y.toLong)
          ).refineUnsafe[Positive0],
          Math.addExact(
            globalOffset.z.toLong,
            Math.addExact(Math.multiplyExact(group.z.toLong, groupSize.z), local.z.toLong)
          ).refineUnsafe[Positive0]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"global coordinate overflows Long range: group=$group, local=$local")
    }

  def containsGlobal(coordinates: Coordinates): Boolean =
    containsGlobal(
      LongCoordinates(
        coordinates.x.toLong.refineUnsafe[Positive0],
        coordinates.y.toLong.refineUnsafe[Positive0],
        coordinates.z.toLong.refineUnsafe[Positive0]
      )
    )

  def containsGlobal(coordinates: LongCoordinates): Boolean =
    contains(coordinates.x, globalOffset.x.toLong, globalExtent.x) &&
      contains(coordinates.y, globalOffset.y.toLong, globalExtent.y) &&
      contains(coordinates.z, globalOffset.z.toLong, globalExtent.z)

  private def activeSize(
      axis: Axis,
      group: Coordinates,
      groupIndex: Int,
      nominalSize: Int,
      groupLimit: Int,
      globalSize: Long
  ): Either[String, Int :| Positive] =
    if (groupIndex >= groupLimit)
      Left(s"work-group coordinate $group is outside group count $groupCount")
    else
      try {
        val origin    = Math.multiplyExact(groupIndex.toLong, nominalSize.toLong)
        val remaining = Math.subtractExact(globalSize, origin)
        math.min(nominalSize.toLong, remaining).toInt.refineEither[Positive].left.map { _ =>
          s"work-group coordinate $group has no active work-items on axis $axis"
        }
      } catch {
        case _: ArithmeticException =>
          Left(s"active size overflows on axis $axis for work-group coordinate $group")
      }

  private def contains(value: Long, offset: Long, extent: Long): Boolean =
    value >= offset && value - offset < extent
}

object LaunchGeometry {
  final case class Dimensions(
      x: Int :| Positive,
      y: Int :| Positive,
      z: Int :| Positive
  ) {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(
        x.toLong.refineUnsafe[Positive],
        y.toLong.refineUnsafe[Positive],
        z.toLong.refineUnsafe[Positive]
      )

    def volume: Either[String, Long :| Positive] =
      try Math.multiplyExact(Math.multiplyExact(x.toLong, y.toLong), z.toLong).refineEither[Positive]
      catch {
        case _: ArithmeticException =>
          Left(s"dimension volume overflows 64 bits: ($x, $y, $z)")
      }

    def volumeInt: Either[String, Int :| Positive] =
      volume.flatMap(size =>
        if (size <= Int.MaxValue.toLong) size.toInt.refineEither[Positive]
        else Left(s"dimension volume exceeds Int range: $size")
      )

    def coordinates(index: Long): Either[String, Coordinates] =
      volume.flatMap { size =>
        Either.cond(
          index >= 0L && index < size, {
            val coordinateX  = (index        % x.toLong).toInt
            val coordinateYZ = index / x.toLong
            val coordinateY  = (coordinateYZ % y.toLong).toInt
            val coordinateZ  = (coordinateYZ / y.toLong).toInt
            for {
              refinedX <- coordinateX.refineEither[Positive0]
              refinedY <- coordinateY.refineEither[Positive0]
              refinedZ <- coordinateZ.refineEither[Positive0]
            } yield Coordinates(refinedX, refinedY, refinedZ)
          },
          s"linear index $index is outside dimensions $this"
        ).flatten
      }

    def *(other: Dimensions): Either[String, Dimensions] =
      try {
        val productX = Math.multiplyExact(x, other.x)
        val productY = Math.multiplyExact(y, other.y)
        val productZ = Math.multiplyExact(z, other.z)
        for {
          refinedX <- productX.refineEither[Positive]
          refinedY <- productY.refineEither[Positive]
          refinedZ <- productZ.refineEither[Positive]
        } yield Dimensions(refinedX, refinedY, refinedZ)
      } catch {
        case _: ArithmeticException =>
          Left(s"dimension product overflows Int range: $this * $other")
      }
  }

  object Dimensions {
    val One: Dimensions = Dimensions(1, 1, 1)
  }

  final case class Coordinates(x: Int :| Positive0, y: Int :| Positive0, z: Int :| Positive0)

  final case class GlobalDimensions(
      x: Long :| Positive,
      y: Long :| Positive,
      z: Long :| Positive
  )

  final case class LongCoordinates(x: Long :| Positive0, y: Long :| Positive0, z: Long :| Positive0)

  enum Axis {
    case X, Y, Z

    def select(value: Coordinates): Int :| Positive0 = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }

    def select(value: Dimensions): Int :| Positive = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }
  }

  def forLaunch(
      groupSize: Dimensions,
      groupCount: Dimensions,
      globalSize: Option[GlobalDimensions] = None,
      workDim: Int :| Interval.Closed[1, 3] = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, LaunchGeometry] =
    for {
      padded <- paddedExtent(groupSize, groupCount)
      exact = globalSize.getOrElse(padded)
      _ <- Either.cond(
        exact.x <= padded.x && exact.y <= padded.y && exact.z <= padded.z,
        (),
        s"global size $exact exceeds padded dispatch size $padded"
      )
    } yield LaunchGeometry(groupSize, groupCount, workDim, globalOffset, exact)

  def virtual(
      groupSize: Dimensions,
      groupCount: Dimensions,
      workDim: Int :| Interval.Closed[1, 3] = 3,
      globalOffset: Coordinates = Coordinates(0, 0, 0)
  ): Either[String, LaunchGeometry] =
    paddedExtent(groupSize, groupCount).map(extent =>
      LaunchGeometry(groupSize, groupCount, workDim, globalOffset, extent)
    )

  private def paddedExtent(groupSize: Dimensions, groupCount: Dimensions): Either[String, GlobalDimensions] =
    try
      Right(
        GlobalDimensions(
          Math.multiplyExact(groupSize.x.toLong, groupCount.x.toLong).refineUnsafe[Positive],
          Math.multiplyExact(groupSize.y.toLong, groupCount.y.toLong).refineUnsafe[Positive],
          Math.multiplyExact(groupSize.z.toLong, groupCount.z.toLong).refineUnsafe[Positive]
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"padded launch extent overflows 64 bits: $groupSize * $groupCount")
    }
}
