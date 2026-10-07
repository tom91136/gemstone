/* ETC2 mode extraction adapted from SwiftShader ETC_Decoder.cpp, Copyright 2016 The SwiftShader Authors.

 * Licensed under the Apache License, Version 2.0. See native/vulkan/third_party/swiftshader/LICENSE.txt.
 */
package net.kurobako.gemstone.isa.spirv.graphics

import net.kurobako.gemstone.graphics.GraphicsProtocol.*

/** ETC2/EAC blocks decoded to linear RGBA, including exact 11-bit EAC normalisation. */
private object CompressedTexels {
  private val distances = Array(3, 6, 11, 16, 23, 32, 41, 64)
  private val intensity = Array(
    Array(2, 8, -2, -8),
    Array(5, 17, -5, -17),
    Array(9, 29, -9, -29),
    Array(13, 42, -13, -42),
    Array(18, 60, -18, -60),
    Array(24, 80, -24, -80),
    Array(33, 106, -33, -106),
    Array(47, 183, -47, -183)
  )
  private val modifiers = Array(
    Array(-3, -6, -9, -15, 2, 5, 8, 14),
    Array(-3, -7, -10, -13, 2, 6, 9, 12),
    Array(-2, -5, -8, -13, 1, 4, 7, 12),
    Array(-2, -4, -6, -13, 1, 3, 5, 12),
    Array(-3, -6, -8, -12, 2, 5, 7, 11),
    Array(-3, -7, -9, -11, 2, 6, 8, 10),
    Array(-4, -7, -8, -11, 3, 6, 7, 10),
    Array(-3, -5, -8, -11, 2, 4, 7, 10),
    Array(-2, -6, -8, -10, 1, 5, 7, 9),
    Array(-2, -5, -8, -10, 1, 4, 7, 9),
    Array(-2, -4, -8, -10, 1, 3, 7, 9),
    Array(-2, -5, -7, -10, 1, 4, 6, 9),
    Array(-3, -4, -7, -10, 2, 3, 6, 9),
    Array(-1, -2, -3, -10, 0, 1, 2, 9),
    Array(-4, -6, -8, -9, 3, 5, 7, 8),
    Array(-3, -5, -7, -9, 2, 4, 6, 8)
  )
  private def clamp(value: Int, low: Int = 0, high: Int = 255): Int = math.max(low, math.min(high, value))
  private def channel(data: Array[Byte], offset: Int, x: Int, y: Int, signed: Boolean, eac: Boolean): Float = {
    val raw        = data(offset) & 255
    val base       = if (signed) math.max(-127, data(offset).toInt) else raw
    val multiplier = (data(offset + 1) & 255) >>> 4
    val selectors  = (2 until 8).foldLeft(0L)((value, byte) => (value << 8) | (data(offset + byte) & 255))
    val selector   = ((selectors >>> (45 - (x * 4 + y) * 3)) & 7).toInt
    val modifier   = modifiers(data(offset + 1) & 15)(selector)
    if (!eac) clamp(base + modifier * multiplier).toFloat / 255f
    else {
      val value = base * 8 + (if (signed) 0 else 4) + modifier * (if (multiplier == 0) 1 else multiplier * 8)
      if (signed) clamp(value, -1023, 1023).toFloat / 1023f else clamp(value, 0, 2047).toFloat / 2047f
    }
  }
  def decode(format: GraphicsFormat, data: Array[Byte]): Array[Float] = {
    import GraphicsCompression.*
    val result = Array.tabulate(64)(i => if (i % 4 == 3) 1f else 0f)
    val eac    = Set(EacR, EacRg, EacSignedR, EacSignedRg)(format.compression)
    if (eac) {
      val signed     = format.compression == EacSignedR || format.compression == EacSignedRg
      val components = if (format.compression == EacRg || format.compression == EacSignedRg) 2 else 1
      for {
        y    <- 0 until 4
        x    <- 0 until 4
        lane <- 0 until components
      } result((y * 4 + x) * 4 + lane) = channel(data, lane * 8, x, y, signed, true)
    } else {
      val offset                                        = if (format.compression == EtcRgba) 8 else 0
      val b                                             = Array.tabulate(8)(i => data(offset + i) & 255)
      def field(byte: Int, shift: Int, width: Int): Int = (b(byte) >>> shift) & ((1 << width) - 1)
      def extend(value: Int, width: Int): Int           = (value << (8 - width)) | (value >>> (2 * width - 8))
      def four(value: Int): Int                         = value * 17
      val differential                                  = (b(3) & 2) != 0
      val punchthrough                                  = format.compression == EtcPunchthrough && !differential
      val base                                          = Array.tabulate(3)(i => b(i) >>> 3)
      val second                                        = Array.tabulate(3)(i => base(i) + ((b(i) & 7) << 29 >> 29))
      val mode                                          = if (!differential && format.compression != EtcPunchthrough) 0
      else if (second(0) < 0 || second(0) > 31) 2
      else if (second(1) < 0 || second(1) > 31) 3 else if (second(2) < 0 || second(2) > 31) 4 else 1
      val palette = if (mode == 2) {
        val a = Array(four((field(0, 3, 2) << 2) | field(0, 0, 2)), four(b(1) >>> 4), four(b(1) & 15))
        val c = Array(four(b(2) >>> 4), four(b(2) & 15), four(b(3) >>> 4))
        val d = distances((field(3, 2, 2) << 1) | field(3, 0, 1))
        Array(a, c.map(_ + d), c, c.map(_ - d))
      } else if (mode == 3) {
        val a = Array(
          four(field(0, 3, 4)),
          four((field(0, 0, 3) << 1) | field(1, 4, 1)),
          four((field(1, 3, 1) << 3) | (field(1, 0, 2) << 1) | field(2, 7, 1))
        )
        val c = Array(four(field(2, 3, 4)), four((field(2, 0, 3) << 1) | field(3, 7, 1)), four(field(3, 3, 4)))
        def packed(v: Array[Int]): Int = (v(0) << 16) | (v(1) << 8) | v(2)
        val d = distances((field(3, 2, 1) << 2) | (field(3, 0, 1) << 1) | (if (packed(a) >= packed(c)) 1 else 0))
        Array(a.map(_ + d), a.map(_ - d), c.map(_ + d), c.map(_ - d))
      } else Array.empty[Array[Int]]
      val planar = if (mode == 4) Array(
        Array(
          extend(field(0, 1, 6), 6),
          extend((field(0, 0, 1) << 6) | field(1, 1, 6), 7),
          extend((field(1, 0, 1) << 5) | (field(2, 3, 2) << 3) | (field(2, 0, 2) << 1) | field(3, 7, 1), 6)
        ),
        Array(
          extend((field(3, 2, 5) << 1) | field(3, 0, 1), 6),
          extend(field(4, 1, 7), 7),
          extend((field(4, 0, 1) << 5) | field(5, 3, 5), 6)
        ),
        Array(
          extend((field(5, 0, 3) << 3) | field(6, 5, 3), 6),
          extend((field(6, 0, 5) << 2) | field(7, 6, 2), 7),
          extend(field(7, 0, 6), 6)
        )
      )
      else Array.empty[Array[Int]]
      for {
        y <- 0 until 4
        x <- 0 until 4
      } {
        val bit         = x * 4 + y
        val selector    = (((b(5 - bit / 8) >>> (bit % 8)) & 1) << 1) | ((b(7 - bit / 8) >>> (bit % 8)) & 1)
        val transparent = punchthrough && mode != 4 && selector == 2
        val colour      = if (mode == 4) Array.tabulate(3)(i =>
          ((x * (planar(1)(i) - planar(0)(i)) + y * (planar(2)(i) - planar(0)(i)) + 2) >> 2) + planar(0)(i)
        )
        else if (mode >= 2) palette(selector)
        else {
          val half     = if ((b(3) & 1) != 0) y / 2 else x / 2
          val table    = if (half == 0) b(3) >>> 5 else field(3, 2, 3)
          val modifier = if (punchthrough && (selector == 0 || selector == 2)) 0 else intensity(table)(selector)
          Array.tabulate(3)(i =>
            (if (mode == 0) four(if (half == 0) b(i) >>> 4 else b(i) & 15)
             else extend(if (half == 0) base(i) else second(i), 5)) + modifier
          )
        }
        for (lane <- 0 until 3) {
          val value = if (transparent) 0.0 else clamp(colour(lane)).toDouble / 255
          result((y * 4 + x) * 4 + lane) =
            (if (!format.srgb) value
             else if (value <= .04045) value / 12.92 else math.pow((value + .055) / 1.055, 2.4)).toFloat
        }
        result((y * 4 + x) * 4 + 3) =
          if (transparent) 0f else if (format.compression == EtcRgba) channel(data, 0, x, y, false, false) else 1f
      }
    }
    result
  }
}
