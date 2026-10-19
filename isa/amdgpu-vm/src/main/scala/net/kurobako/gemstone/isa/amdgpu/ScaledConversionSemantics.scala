package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** gfx950's scaled conversions (CDNA4 ISA, 12.11) and its stochastic conversions to binary16 and bfloat16, over the
  * element conversions of [[Minifloats]].
  */
private object ScaledConversionSemantics {
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  inline def convert[V, R](inline algebra: Algebra[V], instruction: Instruction.VScaledConvert)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val selection = instruction.selection
    val modifiers = instruction.modifiers
    val dest      = instruction.dest
    val dwords    = instruction.conversion.dwords
    val sources   = Vec((instruction.srcA, dwords.a), (instruction.srcB, dwords.b), (instruction.srcC, dwords.c))
      .filter((_, count) => count > 0)
    instruction.conversion match {
      case ScaledConversion.Widen(format, side) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, old) =>
          val element = widened(
            algebra,
            field(algebra, inputs(0), offset = 8 * (selection & 3), width = 8),
            inputs(1),
            format,
            side
          )
          IndexedSeq(
            if (side == Minifloats.Wide.Single) element
            else placed(algebra, old(0), element, if ((selection & 8) != 0) 16 else 0, 16)
          )
        }
      case ScaledConversion.WidenPair(format, side) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, _) =>
          val packed =
            if (format.width == 8) field(algebra, inputs(0), offset = 16 * (selection & 1), width = 16)
            else field(algebra, inputs(0), offset = 8 * (selection & 3), width = 8)
          val low  = widened(algebra, field(algebra, packed, offset = 0, width = format.width), inputs(1), format, side)
          val high = widened(
            algebra,
            field(algebra, packed, offset = format.width, width = format.width),
            inputs(1),
            format,
            side
          )
          if (side == Minifloats.Wide.Single) IndexedSeq(low, high)
          else IndexedSeq(algebra.pack(low, high))
        }
      case ScaledConversion.NarrowPair(side, format) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, old) =>
          def narrowedPair(first: V, second: V, scale: V): IndexedSeq[V] = {
            val mode = if (format.width == 8) sinks.modeRegister else algebra.literal(0L)
            val pair = algebra.or(
              narrowed(algebra, first, scale, mode, side, format),
              algebra.shiftLeft(
                narrowed(algebra, second, scale, mode, side, format),
                algebra.literal(format.width.toLong)
              )
            )
            IndexedSeq(
              if (format.width == 8) placed(algebra, old(0), pair, if ((selection & 8) != 0) 16 else 0, 16)
              else placed(algebra, old(0), pair, 8 * ((selection >> 2) & 3), 8)
            )
          }
          if (side == Minifloats.Wide.Single)
            narrowedPair(
              modified(algebra, inputs(0), modifiers, index = 0, width = 32),
              modified(algebra, inputs(1), modifiers, index = 1, width = 32),
              inputs(2)
            )
          else {
            val halves = modified(algebra, inputs(0), modifiers, index = 0, width = 16)
            narrowedPair(
              field(algebra, halves, offset = 0, width = 16),
              field(algebra, halves, offset = 16, width = 16),
              inputs(1)
            )
          }
        }
      case ScaledConversion.NarrowStochastic(side, format) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, old) =>
          val value =
            if (side == Minifloats.Wide.Single) modified(algebra, inputs(0), modifiers, index = 0, width = 32)
            else modified(
              algebra,
              field(algebra, inputs(0), offset = if ((selection & 1) != 0) 16 else 0, width = 16),
              modifiers,
              index = 0,
              width = 16
            )
          val code = algebra.primitive(value, inputs(1), inputs(2), sinks.modeRegister)(
            (x: UInt[32], seed: UInt[32], scale: Float32, mode: UInt[32]) =>
              Minifloats.stochasticScaled(x, seed, scale, mode, side, format)
          )
          IndexedSeq(placed(algebra, old(0), code, 8 * ((selection >> 2) & 3), 8))
        }
      case ScaledConversion.NarrowStochasticPair(side) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, old) =>
          def stochasticPair(first: V, second: V, seed: V, scale: V): IndexedSeq[V] = {
            val pair = algebra.or(
              stochastic(algebra, first, seed, scale, side, Minifloat.E2M1),
              algebra.shiftLeft(
                stochastic(algebra, second, algebra.primitive(seed)(Minifloats.prng), scale, side, Minifloat.E2M1),
                algebra.literal(4L)
              )
            )
            IndexedSeq(placed(algebra, old(0), pair, 8 * ((selection >> 2) & 3), 8))
          }
          if (side == Minifloats.Wide.Single) stochasticPair(inputs(0), inputs(1), inputs(2), inputs(3))
          else {
            val halves = modified(algebra, inputs(0), modifiers, index = 0, width = 16)
            stochasticPair(
              field(algebra, halves, offset = 0, width = 16),
              field(algebra, halves, offset = 16, width = 16),
              inputs(1),
              inputs(2)
            )
          }
        }
      case ScaledConversion.WidenBlock(format, side) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, _) =>
          def element(index: Int): V =
            widened(algebra, sixBitField(algebra, inputs, 6 * index), inputs(6), format, side)
          val results = IndexedSeq.newBuilder[V]
          var index   = 0
          while (index < 32)
            if (side == Minifloats.Wide.Single) {
              results += element(index)
              index += 1
            } else {
              results += algebra.pack(element(index), element(index + 1))
              index += 2
            }
          results.result()
        }
      case ScaledConversion.NarrowBlock(side, format, interleaved, stochastic) =>
        sinks.block(dest, dwords.dest, sources) { (inputs, _) =>
          val scale = inputs.last
          val codes = IndexedSeq.newBuilder[V]
          var seed  = inputs(dwords.a)
          var index = 0
          while (index < 32) {
            val element =
              if (interleaved) inputs((index & 1) * 16 + index / 2)
              else if (dwords.a == 32) inputs(index)
              else field(algebra, inputs(index / 2), offset = 16 * (index & 1), width = 16)
            codes += (
              if (stochastic) {
                if (index > 0) seed = algebra.primitive(seed)(Minifloats.prng)
                this.stochastic(algebra, element, seed, scale, side, format)
              } else narrowed(algebra, element, scale, algebra.literal(0L), side, format)
            )
            index += 1
          }
          packed6(algebra, codes.result())
        }
    }
  }

  /** The 6-bit `codes` packed into six dwords from the low bits of the first. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def packed6[V](inline algebra: Algebra[V], codes: IndexedSeq[V]): IndexedSeq[V] = {
    def shifted(index: Int, word: Int): V = {
      val offset = 6 * index - 32 * word
      if (offset >= 0) algebra.shiftLeft(codes(index), algebra.literal(offset.toLong))
      else algebra.shiftRightLogical(codes(index), algebra.literal((-offset).toLong))
    }
    val words = IndexedSeq.newBuilder[V]
    var word  = 0
    while (word < 6) {
      var index  = 32 * word / 6
      var packed = shifted(index, word)
      index += 1
      while (6 * index < 32 * word + 32) {
        packed = algebra.or(packed, shifted(index, word))
        index += 1
      }
      words += packed
      word += 1
    }
    words.result()
  }

  /** V_CVT_SR_F16_F32 and V_CVT_SR_BF16_F32 into the half `high` names; the other half keeps its value. */
  inline def stochasticHalf[V, R](inline algebra: Algebra[V], instruction: Instruction.VConvertStochasticHalf)(
      inline sinks: AMDGPUSinks[V, R]
  ): R = {
    val side = instruction.side
    sinks.vectorFloat(instruction.dest, instruction.srcA, instruction.srcB, instruction.modifiers, 32)((x, seed, old) =>
      placed(
        algebra,
        old,
        algebra.primitive(x, seed)((value: Float32, bits: UInt[32]) => Minifloats.stochasticWide(value, bits, side)),
        if (instruction.high) 16 else 0,
        16
      )
    )
  }

  private inline def field[V](inline algebra: Algebra[V], value: V, offset: Int, width: Int): V =
    algebra.field(value, offset, width.unsafe[Int in 1 ~ 64], signed = false)

  /** The six bits from bit `offset` of the dwords `inputs` concatenates. */
  private inline def sixBitField[V](inline algebra: Algebra[V], inputs: IndexedSeq[V], offset: Int): V = {
    val (word, bit) = (offset / 32, offset % 32)
    if (bit + 6 <= 32) field(algebra, inputs(word), offset = bit, width = 6)
    else
      algebra.and(
        algebra.or(
          algebra.shiftRightLogical(inputs(word), algebra.literal(bit.toLong)),
          algebra.shiftLeft(inputs(word + 1), algebra.literal((32 - bit).toLong))
        ),
        algebra.literal(63L)
      )
  }

  /** `old` with its `width` bits from `offset` replaced by `value`'s. */
  private inline def placed[V](inline algebra: Algebra[V], old: V, value: V, offset: Int, width: Int): V =
    algebra.placeField(old, value, offset, width.unsafe[Int in 1 ~ 64], 32)

  /** Source `index` under its ABS and NEG bits, a binary32 or, at 16 bits, each binary16 or bfloat16 half. */
  private inline def modified[V](
      inline algebra: Algebra[V],
      value: V,
      modifiers: SourceModifiers,
      index: Int,
      width: Int
  ): V = {
    val sign      = if (width == 32) 0x80000000L else 0x80008000L
    val magnitude =
      if ((modifiers.absolute & (1 << index)) != 0) algebra.and(value, algebra.literal(~sign & 0xffffffffL)) else value
    if ((modifiers.negate & (1 << index)) != 0) algebra.xor(magnitude, algebra.literal(sign)) else magnitude
  }

  private inline def widened[V](
      inline algebra: Algebra[V],
      code: V,
      scale: V,
      format: Minifloat,
      side: Minifloats.Wide
  ): V =
    algebra.primitive(code, scale)((x: UInt[32], y: Float32) => Minifloats.widenScaled(x, y, format, side))

  private inline def narrowed[V](
      inline algebra: Algebra[V],
      value: V,
      scale: V,
      mode: V,
      side: Minifloats.Wide,
      format: Minifloat
  ): V =
    algebra.primitive(value, scale, mode)((x: UInt[32], y: Float32, z: UInt[32]) =>
      Minifloats.narrowScaled(x, y, z, side, format)
    )

  private inline def stochastic[V](
      inline algebra: Algebra[V],
      value: V,
      seed: V,
      scale: V,
      side: Minifloats.Wide,
      format: Minifloat
  ): V =
    algebra.primitive(value, seed, scale, algebra.literal(0L))((x: UInt[32], y: UInt[32], z: Float32, w: UInt[32]) =>
      Minifloats.stochasticScaled(x, y, z, w, side, format)
    )
}
