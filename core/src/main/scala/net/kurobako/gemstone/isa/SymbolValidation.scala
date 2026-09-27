package net.kurobako.gemstone.isa

import net.kurobako.gemstone.Constraint.{NonNegative, Positive}

import net.kurobako.gemstone.abi.ABI
import net.kurobako.gemstone.Vec

/** Layout checks shared by ISA symbol tables and their wire projections. */
private[gemstone] object SymbolValidation {
  private val PointerByteWidth = ABI.MachineWordSize.toLong

  private def monotonic[A](values: Vec[A])(ordered: (A, A) => Boolean): Boolean =
    values.zip(values.drop(1)).forall { case (previous, current) => ordered(previous, current) }

  def globalPointerOffsets(size: NonNegative[Long], pointerOffsets: Vec[NonNegative[Int]]): Either[String, Unit] =
    for {
      _ <- Either.cond(
        monotonic(pointerOffsets)(_ < _),
        (),
        "program-global pointer offsets must be sorted and unique"
      )
      _ <- Either.cond(
        pointerOffsets.forall(offset =>
          size.raw >= PointerByteWidth && offset.raw.toLong <= size.raw - PointerByteWidth
        ),
        (),
        "program-global pointer offset lies outside its 64-bit storage slot"
      )
    } yield ()

  def kernelParameters(
      argumentBlockSize: NonNegative[Int],
      explicitParameterCount: NonNegative[Int],
      offsetsAndWidths: Vec[(NonNegative[Int], Positive[Int])]
  ): Either[String, Unit] =
    for {
      _ <- Either.cond(
        explicitParameterCount.raw <= offsetsAndWidths.size,
        (),
        "explicit parameter count exceeds the parameter count"
      )
      _ <- Either.cond(
        offsetsAndWidths.forall { case (offset, width) =>
          offset.raw.toLong + width.raw.toLong <= argumentBlockSize.raw.toLong
        },
        (),
        "kernel parameter lies outside the argument block"
      )
      _ <- Either.cond(
        monotonic(offsetsAndWidths) { case ((previousOffset, previousWidth), (offset, _)) =>
          previousOffset.raw.toLong + previousWidth.raw.toLong <= offset.raw.toLong
        },
        (),
        "kernel parameters must be ordered and non-overlapping"
      )
    } yield ()
}
