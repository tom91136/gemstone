package net.kurobako.gemstone.texture

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*

object TexelFormat {
  def apply(kind: ChannelKind, width: Int, channels: Int): TexelFormat = TexelFormat(kind, Vector.fill(channels)(width))

  private def signExtend(raw: Long, width: Int): Long = (raw << (64 - width)) >> (64 - width)

  private def unsignedWidth(width: Int): Int in 1 ~ 32 = width.unsafe[Int in 1 ~ 32]

  private def signedWidth(width: Int): Int in 2 ~ 32 = width.unsafe[Int in 2 ~ 32]
}

/** Packed little-endian channels, x first, each a whole number of bytes wide. */
case class TexelFormat(kind: ChannelKind, bits: Vector[Int]) derives CanEqual {
  def channels: Int   = bits.length
  def texelBytes: Int = bits.sum / 8

  def offset(channel: Int): Int = bits.take(channel).sum / 8

  /** Widths hardware can read: 16 or 32-bit floats, 8, 16, or 32-bit integers, and 8 or 16-bit normalised values, all
    * channels of one width.
    */
  def isValid: Boolean = {
    val widths = kind match {
      case ChannelKind.Float                                             => Set(16, 32)
      case ChannelKind.Signed | ChannelKind.Unsigned                     => Set(8, 16, 32)
      case ChannelKind.UnsignedNormalised | ChannelKind.SignedNormalised => Set(8, 16)
    }
    channels >= 1 && channels <= 4 && bits.distinct.size == 1 && widths(bits.head)
  }

  /** The mode a read in `mode` decodes with. Hardware ignores normalisation for 32-bit integers. */
  def decodeMode(mode: ReadMode): ReadMode =
    if (kind.isInteger && bits.head == 32) ReadMode.ElementType else mode

  /** Whether a read in `mode` yields integers rather than floats. */
  def isIntegral(mode: ReadMode): Boolean = kind.isInteger && decodeMode(mode) == ReadMode.ElementType

  /** Whether `mode` and `srgb` are accepted for this format: normalised reads need 8 or 16-bit channels and sRGB needs
    * 8-bit unsigned ones.
    */
  def supports(mode: ReadMode, srgb: Boolean): Boolean =
    !(kind.isInteger && bits.head == 32 && mode == ReadMode.NormalisedFloat) &&
      (!srgb || (kind == ChannelKind.Unsigned && bits.head == 8 && mode == ReadMode.NormalisedFloat))

  def rawChannel(bytes: Array[Byte], start: Int, channel: Int): Long = {
    val width = bits(channel) / 8
    val from  = start + offset(channel)
    (0 until width).foldLeft(0L)((acc, i) => acc | ((bytes(from + i) & 0xffL) << (8 * i)))
  }

  /** Channel `channel` of the texel at `start`, in `mode`. sRGB applies to x, y, and z only. */
  def channelValue(bytes: Array[Byte], start: Int, channel: Int, mode: ReadMode, srgb: Boolean): Double = {
    import TexelFormat.*
    val raw   = rawChannel(bytes, start, channel)
    val width = bits(channel)
    val read  = decodeMode(mode)
    val value = kind match {
      case ChannelKind.Float =>
        if (width == 16) IEEE754.binary16ToFloat(Binary16.low(raw)).toDouble
        else java.lang.Float.intBitsToFloat(raw.toInt).toDouble
      case ChannelKind.Unsigned =>
        if (read == ReadMode.ElementType) raw.toDouble else Normalised.decodeUnsigned(raw, unsignedWidth(width))
      case ChannelKind.Signed =>
        if (read == ReadMode.ElementType) TexelFormat.signExtend(raw, width).toDouble
        else Normalised.decodeSigned(raw, signedWidth(width))
      case ChannelKind.UnsignedNormalised => Normalised.decodeUnsigned(raw, unsignedWidth(width))
      case ChannelKind.SignedNormalised   => Normalised.decodeSigned(raw, signedWidth(width))
    }
    if (srgb && channel < 3) SrgbTransfer.toLinear(value) else value
  }

  /** The four components of the texel at `start`: missing y and z read 0 and a missing w reads 1. */
  def texel(bytes: Array[Byte], start: Int, mode: ReadMode, srgb: Boolean): Vector[Double] =
    Vector.tabulate(4) { channel =>
      if (channel < channels) channelValue(bytes, start, channel, mode, srgb) else if (channel == 3) 1.0 else 0.0
    }

  /** Writes `values` into one texel, converting as a formatted store: integers saturate, normalised values round to
    * nearest with ties away from zero, and floats narrow to nearest even.
    */
  def encode(values: Vector[Double]): Vec[Byte] = {
    import TexelFormat.*
    val out = Array.ofDim[Byte](texelBytes)
    (0 until channels).foreach { channel =>
      val width = bits(channel)
      val value = values.lift(channel).getOrElse(0.0)
      val raw   = kind match {
        case ChannelKind.Float =>
          if (width == 16) IEEE754.floatToBinary16(value.toFloat).toLong
          else java.lang.Float.floatToRawIntBits(value.toFloat).toLong & 0xffffffffL
        case ChannelKind.Unsigned =>
          if (value.isNaN) 0L else math.rint(math.max(0.0, math.min(((1L << width) - 1).toDouble, value))).toLong
        case ChannelKind.Signed =>
          val high = ((1L << (width - 1)) - 1).toDouble
          if (value.isNaN) 0L else math.rint(math.max(-high - 1, math.min(high, value))).toLong
        case ChannelKind.UnsignedNormalised => Normalised.encodeUnsigned(value, unsignedWidth(width))
        case ChannelKind.SignedNormalised   => Normalised.encodeSigned(value, signedWidth(width))
      }
      val from = offset(channel)
      (0 until width / 8).foreach(i => out(from + i) = ((raw >>> (8 * i)) & 0xffL).toByte)
    }
    Vec.unsafeWrapArray(out)
  }
}
