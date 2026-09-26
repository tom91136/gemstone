package net.kurobako.gemstone.isa

import net.kurobako.gemstone.abi.ABI

import net.kurobako.gemstone.Vec

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

/** Layout checks shared by ISA symbol tables and their wire projections. */
private[gemstone] object SymbolValidation {
  private val PointerByteWidth = ABI.MachineWordSize.toLong

  private def monotonic[A](values: Vec[A])(ordered: (A, A) => Boolean): Boolean =
    values.zip(values.drop(1)).forall { case (previous, current) => ordered(previous, current) }

  def globalPointerOffsets(size: Long, pointerOffsets: Vec[Int :| Positive0]): Either[String, Unit] =
    for {
      _ <- Either.cond(
        monotonic(pointerOffsets)(_ < _),
        (),
        "program-global pointer offsets must be sorted and unique"
      )
      _ <- Either.cond(
        pointerOffsets.forall(offset => size >= PointerByteWidth && offset.toLong <= size - PointerByteWidth),
        (),
        "program-global pointer offset lies outside its 64-bit storage slot"
      )
    } yield ()

  def kernelParameters(
      argumentBlockSize: Int :| Positive0,
      explicitParameterCount: Int :| Positive0,
      offsetsAndWidths: Vec[(Int :| Positive0, Int :| Positive)]
  ): Either[String, Unit] =
    for {
      _ <- Either.cond(
        explicitParameterCount <= offsetsAndWidths.size,
        (),
        "explicit parameter count exceeds the parameter count"
      )
      _ <- Either.cond(
        offsetsAndWidths.forall { case (offset, width) => offset.toLong + width.toLong <= argumentBlockSize.toLong },
        (),
        "kernel parameter lies outside the argument block"
      )
      _ <- Either.cond(
        monotonic(offsetsAndWidths) { case ((previousOffset, previousWidth), (offset, _)) =>
          previousOffset.toLong + previousWidth.toLong <= offset.toLong
        },
        (),
        "kernel parameters must be ordered and non-overlapping"
      )
    } yield ()
}
