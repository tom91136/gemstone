package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*

/** Shared image conversion entry point for shader execution and the graphics runtime. Integer colours carry raw 32-bit
  * scalar bits; floating colours carry IEEE binary32 bits.
  */
object ImageTexelCodec {
  def texelBytes(format: Int): Int = ImageFormats.integer(format) match {
    case Some(layout)                                      => layout.texelBytes
    case None if ImageFormats.narrow(format).isDefined     => ImageFormats.narrow(format).get.texelBytes.raw
    case None if ImageFormats.normalised(format).isDefined => ImageFormats.normalised(format).get.texelBytes
    case None                                              => format match {
        case ImageDescriptor.Format.DecodedRgb32f => 12
        case ImageFormat.Rgba32f                  => 16
        case ImageFormat.Rg32f | ImageDescriptor.Format.D32SfloatS8Depth | ImageDescriptor.Format.D32SfloatS8Stencil =>
          8
        case ImageFormat.R32f                => 4
        case ImageDescriptor.Format.D16Unorm => 2
        case ImageDescriptor.Format.S8Uint   => 1
        case _                               => 0
      }
  }

  def integer(format: Int): Boolean = ImageFormats.integer(format).nonEmpty
  def signed(format: Int): Boolean  = ImageFormats.integer(format).exists(_.signed) ||
    ImageFormats.normalised(format).exists(_.signed)
  def floating(format: Int): Boolean = floatComponents(format) != 0 ||
    ImageFormats.narrow(format).exists(_.encoding != NarrowEncoding.Unorm)
  def floatComponents(format: Int): Int                                        = ImageFormats.floatComponents(format)
  private def bits(bytes: Array[Byte], offset: Int, count: Int in 1 ~ 8): Long = (0 until count.raw).foldLeft(0L)(
    (value, byte) =>
      value | ((bytes(offset + byte).toLong & 255L) << (8 * byte))
  )
  private def put(bytes: Array[Byte], offset: Int, count: Int in 1 ~ 8, value: Long): Unit =
    (0 until count.raw).foreach(byte =>
      bytes(offset + byte) = (value >>> (8 * byte)).toByte
    )

  def read(bytes: Array[Byte], offset: Int, format: Int, lane: Int): Double = {
    val floats = floatComponents(format)
    if (floats != 0) {
      if (lane >= floats) { if (lane == 3) 1.0 else 0.0 }
      else java.lang.Float.intBitsToFloat(bits(bytes, offset + lane * 4, 4).toInt).toDouble
    } else ImageFormats.narrow(format) match {
      case Some(layout) =>
        if (lane >= layout.components) { if (lane == 3) 1.0 else 0.0 }
        else layout.decode(
          bits(
            bytes,
            offset + (if (layout.encoding == NarrowEncoding.Half) lane * 2 else 0),
            layout.elementBytes
          ),
          lane
        )
      case None => ImageFormats.normalised(format) match {
          case Some(layout) =>
            if (lane >= layout.components) { if (lane == 3) 1.0 else 0.0 }
            else
              layout.decode(bits(bytes, offset + layout.offset(lane), layout.componentBytes.widen[Int in 1 ~ 8]), lane)
          case None => throw IllegalArgumentException(s"image format $format has no floating conversion")
        }
    }
  }
  def readInteger(bytes: Array[Byte], offset: Int, format: Int, lane: Int): Int = {
    val layout = ImageFormats.integer(format).getOrElse(throw IllegalArgumentException("not an integer image"))
    if (lane >= layout.components) { if (lane == 3) 1 else 0 }
    else layout.decode(
      bits(bytes, offset + layout.offset(lane), if (layout.packed) 4 else layout.componentBytes),
      lane
    ).toInt
  }
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def write(bytes: Array[Byte], offset: Int, format: Int, values: Array[Int], mask: Int): Unit = {
    def value(lane: Int): Double = java.lang.Float.intBitsToFloat(values(lane)).toDouble
    val floats                   = floatComponents(format)
    if (floats != 0) (0 until floats).foreach(lane =>
      if ((mask & (1 << lane)) != 0) put(bytes, offset + lane * 4, 4, values(lane).toLong)
    )
    else ImageFormats.integer(format) match {
      case Some(layout) =>
        if (layout.packed) {
          val lanes = Array.tabulate[Long](4)(lane =>
            if ((mask & (1 << lane)) != 0) values(lane).toLong else readInteger(bytes, offset, format, lane).toLong
          )
          put(bytes, offset, 4, layout.encodePacked(lanes))
        } else (0 until layout.components).foreach(lane =>
          if ((mask & (1 << lane)) != 0)
            put(bytes, offset + layout.offset(lane), layout.componentBytes, layout.encode(values(lane).toLong))
        )
      case None => ImageFormats.narrow(format) match {
          case Some(layout) =>
            if (layout.encoding == NarrowEncoding.SharedExponent) {
              val channels =
                Array.tabulate(3)(lane => if (value(lane).isNaN) 0.0 else Math.max(0.0, Math.min(65408.0, value(lane))))
              val maximum  = channels.max
              var exponent = if (maximum == 0) 0 else Math.max(0, Math.getExponent(maximum) + 16)
              if (Math.floor(Math.scalb(maximum, 24 - exponent) + 0.5) == 512) exponent += 1
              val packed = (0 until 3).foldLeft(exponent.toLong << 27)((word, lane) =>
                word | (Math.floor(Math.scalb(channels(lane), 24 - exponent) + 0.5).toLong << (lane * 9))
              )
              put(bytes, offset, 4, packed)
            } else if (layout.encoding == NarrowEncoding.Half) {
              (0 until layout.components).foreach(lane =>
                if ((mask & (1 << lane)) != 0) put(bytes, offset + lane * 2, 2, layout.encodeLane(value(lane), lane))
              )
            } else {
              val packed = (0 until layout.components).foldLeft(bits(bytes, offset, layout.texelBytes)) { (word, lane) =>
                if ((mask & (1 << lane)) == 0) word
                else {
                  val limit = Bits.mask(layout.widths(lane).widen[Int in 1 ~ 64])
                  (word & ~(limit << layout.shifts(lane))) |
                    (layout.encodeLane(value(lane), lane) << layout.shifts(lane))
                }
              }
              put(bytes, offset, layout.texelBytes, packed)
            }
          case None =>
            val layout = ImageFormats.normalised(
              format
            ).getOrElse(throw IllegalArgumentException(s"unsupported image format $format"))
            (0 until layout.components).foreach(lane =>
              if ((mask & (1 << lane)) != 0)
                put(
                  bytes,
                  offset + layout.offset(lane),
                  layout.componentBytes.widen[Int in 1 ~ 8],
                  layout.encode(value(lane), lane)
                )
            )
        }
    }
  }
}
