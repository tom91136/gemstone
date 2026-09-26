package net.kurobako.gemstone.vm

import LaunchGeometry.*

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong, WorkDimension}

final case class LaunchGeometry private (
    groupSize: Dimensions,
    groupCount: Dimensions,
    workDim: WorkDimension,
    globalOffset: Coordinates,
    globalExtent: GlobalDimensions
) {
  def localCoordinates(index: Int): Either[String, Coordinates] =
    groupSize.coordinates(index.toLong)

  def groupCoordinates(index: Long): Either[String, Coordinates] =
    groupCount.coordinates(index)

  def activeGroupSize(group: Coordinates): Either[String, Dimensions] =
    for {
      activeX <- activeSize(Axis.X, group, group.x.raw, groupSize.x.raw, groupCount.x.raw, globalExtent.x.raw)
      activeY <- activeSize(Axis.Y, group, group.y.raw, groupSize.y.raw, groupCount.y.raw, globalExtent.y.raw)
      activeZ <- activeSize(Axis.Z, group, group.z.raw, groupSize.z.raw, groupCount.z.raw, globalExtent.z.raw)
    } yield Dimensions(activeX, activeY, activeZ)

  def globalCoordinatesLong(
      group: Coordinates,
      local: Coordinates
  ): Either[String, LongCoordinates] =
    try
      Right(
        LongCoordinates(
          NonNegativeLong.unsafe(
            Math.addExact(
              globalOffset.x.raw,
              Math.addExact(Math.multiplyExact(group.x.raw.toLong, groupSize.x.raw.toLong), local.x.raw.toLong)
            )
          ),
          NonNegativeLong.unsafe(
            Math.addExact(
              globalOffset.y.raw,
              Math.addExact(Math.multiplyExact(group.y.raw.toLong, groupSize.y.raw.toLong), local.y.raw.toLong)
            )
          ),
          NonNegativeLong.unsafe(
            Math.addExact(
              globalOffset.z.raw,
              Math.addExact(Math.multiplyExact(group.z.raw.toLong, groupSize.z.raw.toLong), local.z.raw.toLong)
            )
          )
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
      groupIndex: Int,
      nominalSize: Int,
      groupLimit: Int,
      globalSize: Long
  ): Either[String, PositiveInt] =
    if (groupIndex >= groupLimit)
      Left(s"work-group coordinate $group is outside group count $groupCount")
    else
      try {
        val origin    = Math.multiplyExact(groupIndex.toLong, nominalSize.toLong)
        val remaining = Math.subtractExact(globalSize, origin)
        PositiveInt.either(math.min(nominalSize.toLong, remaining).toInt).left.map { _ =>
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
      x: PositiveInt,
      y: PositiveInt,
      z: PositiveInt
  ) {
    def toGlobalDimensions: GlobalDimensions =
      GlobalDimensions(x.toLong, y.toLong, z.toLong)

    def volume: Either[String, PositiveLong] =
      try Right(PositiveLong.unsafe(Math.multiplyExact(
          Math.multiplyExact(x.raw.toLong, y.raw.toLong),
          z.raw.toLong
        )))
      catch {
        case _: ArithmeticException =>
          Left(s"dimension volume overflows 64 bits: ($x, $y, $z)")
      }

    def volumeInt: Either[String, PositiveInt] =
      volume.flatMap(size =>
        if (size.raw <= Int.MaxValue.toLong) PositiveInt.either(size.raw.toInt)
        else Left(s"dimension volume exceeds Int range: $size")
      )

    def coordinates(index: Long): Either[String, Coordinates] =
      volume.flatMap { size =>
        Either.cond(
          index >= 0L && index < size.raw, {
            val coordinateX  = (index        % x.raw.toLong).toInt
            val coordinateYZ = index / x.raw.toLong
            val coordinateY  = (coordinateYZ % y.raw.toLong).toInt
            val coordinateZ  = (coordinateYZ / y.raw.toLong).toInt
            for {
              refinedX <- NonNegativeInt.either(coordinateX)
              refinedY <- NonNegativeInt.either(coordinateY)
              refinedZ <- NonNegativeInt.either(coordinateZ)
            } yield Coordinates(refinedX, refinedY, refinedZ)
          },
          s"linear index $index is outside dimensions $this"
        ).flatten
      }
  }

  object Dimensions {
    val One: Dimensions = Dimensions(PositiveInt.One, PositiveInt.One, PositiveInt.One)
  }

  final case class Coordinates(x: NonNegativeInt, y: NonNegativeInt, z: NonNegativeInt)

  object Coordinates {
    val origin: Coordinates = Coordinates(NonNegativeInt.Zero, NonNegativeInt.Zero, NonNegativeInt.Zero)
  }

  final case class GlobalDimensions(
      x: PositiveLong,
      y: PositiveLong,
      z: PositiveLong
  )

  final case class LongCoordinates(x: NonNegativeLong, y: NonNegativeLong, z: NonNegativeLong)

  enum Axis {
    case X, Y, Z

    def select(value: Coordinates): NonNegativeInt = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }

    def select(value: Dimensions): PositiveInt = this match {
      case X => value.x
      case Y => value.y
      case Z => value.z
    }
  }

  def forLaunch(
      groupSize: Dimensions,
      groupCount: Dimensions,
      globalSize: Option[GlobalDimensions] = None,
      workDim: WorkDimension = WorkDimension.Three,
      globalOffset: Coordinates = Coordinates.origin
  ): Either[String, LaunchGeometry] =
    for {
      padded <- paddedExtent(groupSize, groupCount)
      exact = globalSize.getOrElse(padded)
      _ <- Either.cond(
        exact.x.raw <= padded.x.raw && exact.y.raw <= padded.y.raw && exact.z.raw <= padded.z.raw,
        (),
        s"global size $exact exceeds padded dispatch size $padded"
      )
    } yield LaunchGeometry(groupSize, groupCount, workDim, globalOffset, exact)

  def virtual(
      groupSize: Dimensions,
      groupCount: Dimensions,
      workDim: WorkDimension = WorkDimension.Three,
      globalOffset: Coordinates = Coordinates.origin
  ): Either[String, LaunchGeometry] =
    paddedExtent(groupSize, groupCount).map(extent =>
      LaunchGeometry(groupSize, groupCount, workDim, globalOffset, extent)
    )

  private def paddedExtent(groupSize: Dimensions, groupCount: Dimensions): Either[String, GlobalDimensions] =
    try
      Right(
        GlobalDimensions(
          PositiveLong.unsafe(Math.multiplyExact(groupSize.x.raw.toLong, groupCount.x.raw.toLong)),
          PositiveLong.unsafe(Math.multiplyExact(groupSize.y.raw.toLong, groupCount.y.raw.toLong)),
          PositiveLong.unsafe(Math.multiplyExact(groupSize.z.raw.toLong, groupCount.z.raw.toLong))
        )
      )
    catch {
      case _: ArithmeticException =>
        Left(s"padded launch extent overflows 64 bits: $groupSize * $groupCount")
    }
}
