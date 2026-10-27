package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.semantics.prelude.*

/** The pure functions of image sampling. A float is the low 32 bits of a word and a double its 64 bits, a flag is 0 or
  * 1, and an index that names no texel is -1.
  */
private[isa] object Sampling {
  val SingleOne: Long = 0x3f800000L

  private def single(word: Long): Float = IEEE754.binary32ToFloat(Binary32.low(word))

  private def double(bits: Long): Double = java.lang.Double.longBitsToDouble(bits)

  private def bits(value: Double): Long = java.lang.Double.doubleToRawLongBits(value)

  private def flag(value: Boolean): Long = if (value) 1L else 0L

  private def word(value: Float): Long = IEEE754.floatToBinary32(value).raw & 0xffffffffL

  private def hypot(a: Double, b: Double): Double = Math.hypot(a, b)

  /** The float `word` in double precision. */
  def widen(value: UInt[64]): UInt[64] = fromRaw[UInt[64]](bits(single(toRaw(value)).toDouble))

  /** The integer coordinate `value` as a float. */
  def converted(value: UInt[64]): UInt[64] = fromRaw[UInt[64]](word(toRaw(value).toInt.toFloat))

  /** The float `value` divided by the float `divisor`. */
  def divided(value: UInt[64], divisor: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](word(single(toRaw(value)) / single(toRaw(divisor))))

  /** The float `value` over the float `divisor`, in double precision. */
  def reference(value: UInt[64], divisor: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits((single(toRaw(value)) / single(toRaw(divisor))).toDouble))

  /** 1 where the float `value` is finite. */
  def finite(value: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](flag(java.lang.Double.isFinite(single(toRaw(value)).toDouble)))

  /** The index `value` addresses in `size` texels under address mode `mode`, or -1 for the border. */
  def address(value: UInt[64], size: UInt[64], mode: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](ImageOps.sampleAddress(toRaw(value), toRaw(size), toRaw(mode).toInt).getOrElse(-1L))

  /** The coordinate in texels of an image of `extent` texels, moved by `offset`; `unnormalised` ones are already. */
  def texelSpace(coordinate: UInt[64], extent: UInt[64], offset: UInt[64], unnormalised: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](
      bits(
        (double(toRaw(coordinate)).toFloat * (if (toRaw(unnormalised) != 0L) 1f else toRaw(extent).toFloat) +
          toRaw(offset).toFloat).toDouble
      )
    )

  /** The texel a nearest filter takes. */
  def nearest(coordinate: UInt[64]): UInt[64] = fromRaw[UInt[64]](Math.floor(double(toRaw(coordinate))).toLong)

  /** The first of the texels a linear filter takes. */
  def lowest(coordinate: UInt[64]): UInt[64] = fromRaw[UInt[64]](Math.floor(double(toRaw(coordinate)) - 0.5).toLong)

  /** The weight of the texel after `lowest`, which is that of `coordinate` past the centre of `lowest`. */
  def fraction(coordinate: UInt[64], lowest: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(double(toRaw(coordinate)) - 0.5 - toRaw(lowest)))

  /** The array layer the float `value` names among `count`. */
  def layer(value: UInt[64], count: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](Math.max(0L, Math.min(toRaw(count) - 1, Math.rint(single(toRaw(value)).toDouble).toLong)))

  /** The components a texel of `format` holds, which the rest of a border colour completes. */
  def components(format: UInt[64]): UInt[64] = fromRaw[UInt[64]](ImageOps.texelComponents(toRaw(format).toInt).toLong)

  /** Component `lane` of the border colour of `colour`, of a texel of `components`. */
  def border(colour: UInt[64], components: UInt[64], lane: UInt[64]): UInt[64] = {
    val (c, n, l) = (toRaw(colour), toRaw(components), toRaw(lane))
    fromRaw[UInt[64]](bits(
      if (l >= n) { if (l == 3L) 1.0 else 0.0 }
      else if (
        c >= ImageDescriptor.BorderColour.FloatOpaqueWhite ||
        (l == 3L && c >= ImageDescriptor.BorderColour.FloatOpaqueBlack)
      )
        1.0
      else 0.0
    ))
  }

  /** 1 where the depth passes `operation` against the reference, which a 16-bit depth format clamps. */
  def compared(operation: UInt[64], reference: UInt[64], depth: UInt[64], format: UInt[64]): UInt[64] = {
    val limit = double(toRaw(reference))
    fromRaw[UInt[64]](bits(ImageOps.sampleCompare(
      toRaw(operation).toInt,
      if (toRaw(format) == ImageDescriptor.Format.D16Unorm) Math.max(0.0, Math.min(1.0, limit)) else limit,
      double(toRaw(depth))
    )))
  }

  /** The texels `a` to `d` of a bilinear filter, weighted by `x` and `y`. */
  def bilinear(a: UInt[64], b: UInt[64], c: UInt[64], d: UInt[64], x: UInt[64], y: UInt[64]): UInt[64] = {
    val (fx, fy) = (double(toRaw(x)), double(toRaw(y)))
    fromRaw[UInt[64]](bits(
      (1 - fy) *
        ((1 - fx) * double(toRaw(a)) + fx * double(toRaw(b))) +
        fy *
        ((1 - fx) * double(toRaw(c)) + fx * double(toRaw(d)))
    ))
  }

  /** `a` and `b` weighted by `fraction`. */
  def lerp(a: UInt[64], b: UInt[64], fraction: UInt[64]): UInt[64] = {
    val f = double(toRaw(fraction))
    fromRaw[UInt[64]](bits((1 - f) * double(toRaw(a)) + f * double(toRaw(b))))
  }

  /** `a` where `fraction` is zero, otherwise `a` and `b` weighted by it. */
  def mixed(a: UInt[64], b: UInt[64], fraction: UInt[64]): UInt[64] = {
    val f = double(toRaw(fraction))
    fromRaw[UInt[64]](if (f == 0) toRaw(a) else bits(double(toRaw(a)) * (1 - f) + double(toRaw(b)) * f))
  }

  /** The running sum of the `taps` of a cube tap, which weights each of them equally, or the value of a single tap. */
  def accumulated(sum: UInt[64], value: UInt[64], taps: UInt[64], cube: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](
      if (toRaw(cube) != 0L) bits(double(toRaw(sum)) + double(toRaw(value)) / toRaw(taps).toInt) else toRaw(value)
    )

  private def projected(x: Long, y: Long, z: Long): CubeMap.Projection =
    CubeMap.project(double(x), double(y), double(z))

  /** The face of the cube a direction meets. */
  def face(x: UInt[64], y: UInt[64], z: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](projected(toRaw(x), toRaw(y), toRaw(z)).face.toLong)

  /** The horizontal coordinate of a direction on its face. */
  def faceS(x: UInt[64], y: UInt[64], z: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(projected(toRaw(x), toRaw(y), toRaw(z)).s))

  /** The vertical coordinate of a direction on its face. */
  def faceT(x: UInt[64], y: UInt[64], z: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(projected(toRaw(x), toRaw(y), toRaw(z)).t))

  private def tapsOf(
      face: UInt[64],
      x: UInt[64],
      y: UInt[64],
      size: UInt[64],
      linear: UInt[64]
  ): Vector[CubeMap.Texel] =
    CubeMap.taps(toRaw(face).toInt, toRaw(x), toRaw(y), toRaw(size), toRaw(linear) != 0L)

  /** The texels a tap of a cube takes, which a linear filter may take from the neighbours of its face. */
  def cubeTaps(face: UInt[64], x: UInt[64], y: UInt[64], size: UInt[64], linear: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](tapsOf(face, x, y, size, linear).size.toLong)

  /** The face of texel `index` of a tap. */
  def cubeFace(face: UInt[64], x: UInt[64], y: UInt[64], size: UInt[64], linear: UInt[64], index: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](tapsOf(face, x, y, size, linear)(toRaw(index).toInt).face.toLong)

  /** The column of texel `index` of a tap. */
  def cubeX(face: UInt[64], x: UInt[64], y: UInt[64], size: UInt[64], linear: UInt[64], index: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](tapsOf(face, x, y, size, linear)(toRaw(index).toInt).x)

  /** The row of texel `index` of a tap. */
  def cubeY(face: UInt[64], x: UInt[64], y: UInt[64], size: UInt[64], linear: UInt[64], index: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](tapsOf(face, x, y, size, linear)(toRaw(index).toInt).y)

  private def faceGradient(
      x: UInt[64],
      y: UInt[64],
      z: UInt[64],
      dx: UInt[64],
      dy: UInt[64],
      dz: UInt[64]
  ): (Double, Double) = {
    val direction = Array(double(toRaw(x)), double(toRaw(y)), double(toRaw(z)))
    CubeMap.gradient(
      CubeMap.project(direction(0), direction(1), direction(2)).face,
      direction,
      Array(double(toRaw(dx)), double(toRaw(dy)), double(toRaw(dz)))
    )
  }

  /** The gradient of the horizontal coordinate on the face of a direction, from its gradient. */
  def gradientS(x: UInt[64], y: UInt[64], z: UInt[64], dx: UInt[64], dy: UInt[64], dz: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(faceGradient(x, y, z, dx, dy, dz)._1))

  /** The gradient of the vertical coordinate on the face of a direction, from its gradient. */
  def gradientT(x: UInt[64], y: UInt[64], z: UInt[64], dx: UInt[64], dy: UInt[64], dz: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(faceGradient(x, y, z, dx, dy, dz)._2))

  /** The gradient moved to texels of an image of `extent`; `unnormalised` ones are already. */
  def scaled(gradient: UInt[64], extent: UInt[64], unnormalised: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(double(toRaw(gradient)) * (if (toRaw(unnormalised) != 0L) 1.0 else toRaw(extent).toDouble)))

  /** The level of detail of the gradients across and down the texels, as log2 of the larger length. */
  def detail(ax: UInt[64], ay: UInt[64], az: UInt[64], bx: UInt[64], by: UInt[64], bz: UInt[64]): UInt[64] = {
    def length(x: UInt[64], y: UInt[64], z: UInt[64]) =
      hypot(hypot(double(toRaw(x)), double(toRaw(y))), double(toRaw(z)))
    fromRaw[UInt[64]](bits(Math.log(Math.max(length(ax, ay, az), length(bx, by, bz))) / Math.log(2.0)))
  }

  /** The sampler bias, which the shader's own moves before the device clamps it. */
  def bias(sampler: UInt[64], shader: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(Math.max(-16.0, Math.min(16.0, single(toRaw(sampler)).toDouble + double(toRaw(shader))))))

  /** The level of detail clamped to the range of the sampler. */
  def lambda(lowest: UInt[64], highest: UInt[64], base: UInt[64], bias: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(Math.max(
      single(toRaw(lowest)).toDouble,
      Math.min(single(toRaw(highest)).toDouble, double(toRaw(base)) + double(toRaw(bias)))
    )))

  /** The level of detail within the `levels` of the image. */
  def selected(lambda: UInt[64], levels: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(Math.max(0.0, Math.min(toRaw(levels) - 1.0, double(toRaw(lambda))))))

  /** Level `pass` the sampler takes: level 0 under `mode` 0, the nearest level under 1, then the two around it. */
  def level(selected: UInt[64], levels: UInt[64], mode: UInt[64], pass: UInt[64]): UInt[64] = {
    val low = Math.floor(double(toRaw(selected))).toInt
    fromRaw[UInt[64]](toRaw(mode) match {
      case 0L => 0L
      case 1L => Math.floor(double(toRaw(selected)) + 0.5).toInt.toLong
      case _  => if (toRaw(pass) == 0L) low.toLong else Math.min(low + 1, toRaw(levels) - 1)
    })
  }

  /** The weight of the second level the sampler takes. */
  def between(selected: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(double(toRaw(selected)) - Math.floor(double(toRaw(selected))).toInt))

  /** 1 where `value` is above zero. */
  def positive(value: UInt[64]): UInt[64] = fromRaw[UInt[64]](flag(double(toRaw(value)) > 0))

  /** 1 where the sampler range of levels admits a level of detail without derivatives: it ends at 0 or starts above. */
  def bounded(lowest: UInt[64], highest: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](flag(single(toRaw(highest)) <= 0 || single(toRaw(lowest)) > 0))

  /** 1 where the sampler range of levels holds one level of detail: it is empty, or ends at 0. */
  def fixed(lowest: UInt[64], highest: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](flag(single(toRaw(lowest)).toDouble == single(toRaw(highest)).toDouble ||
      single(toRaw(highest)) <= 0))

  /** 1 where `format` holds integers. */
  def integral(format: UInt[64]): UInt[64] = fromRaw[UInt[64]](flag(ImageOps.imageIsInteger(toRaw(format).toInt)))

  /** The component `value` as a word of the result, an integer if `integral` is set and otherwise a float. */
  def packed(integral: UInt[64], value: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](
      if (toRaw(integral) != 0L) double(toRaw(value)).toLong & 0xffffffffL else word(double(toRaw(value)).toFloat)
    )

  /** The level of detail a query reports, within the `levels` of the image and the range of the sampler. */
  def clamped(lowest: UInt[64], highest: UInt[64], levels: UInt[64], lod: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(Math.max(
      0.0,
      Math.min(
        toRaw(levels) - 1.0,
        Math.max(single(toRaw(lowest)).toDouble, Math.min(single(toRaw(highest)).toDouble, double(toRaw(lod))))
      )
    )))

  /** The sampler bias a query adds. */
  def queried(sampler: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](bits(Math.max(-16.0, Math.min(16.0, single(toRaw(sampler)).toDouble))))

  /** The level a query reports: the nearest one if the sampler picks the nearest mip level. */
  def reported(mipmap: UInt[64], clamped: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](
      if (toRaw(mipmap) == ImageDescriptor.MipmapMode.Nearest)
        bits(Math.max(0.0, Math.ceil(double(toRaw(clamped)) + 0.5) - 1.0))
      else toRaw(clamped)
    )
}
