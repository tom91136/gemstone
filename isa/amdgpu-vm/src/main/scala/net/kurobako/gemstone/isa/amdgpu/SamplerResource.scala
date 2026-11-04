package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*

private[isa] object SamplerResource {
  private def field(word: Long, offset: Int, width: Int): Long = (word >>> offset) & ((1L << width) - 1L)

  private val Steps = 256.0

  /** The sampler `words` describe: the filters, address modes, level range, bias and depth comparison. GFX11 and later
    * widen the level range to 13 bits each.
    */
  def decode(generation: ResourceGeneration, words: Array[Long]): SamplerResource = {
    val wide   = generation == ResourceGeneration.GFX11 || generation == ResourceGeneration.GFX12
    val minLod = if (wide) field(words(1), offset = 0, width = 13) else field(words(1), offset = 0, width = 12)
    val maxLod = if (wide) field(words(1), offset = 13, width = 13) else field(words(1), offset = 12, width = 12)
    val bias   = Bits.signExtend(field(words(2), offset = 0, width = 14), 14).toDouble
    SamplerResource(
      clampX = field(words(0), 0, 3).toInt,
      clampY = field(words(0), 3, 3).toInt,
      clampZ = field(words(0), 6, 3).toInt,
      depthCompare = field(words(0), 12, 3).toInt,
      forceUnnormalised = field(words(0), 15, 1) != 0L,
      filterMode = field(words(0), 29, 2).toInt,
      minLod = minLod / Steps,
      maxLod = maxLod / Steps,
      lodBias = bias / Steps,
      magnifyFilter = field(words(2), 20, 2).toInt,
      minifyFilter = field(words(2), 22, 2).toInt,
      zFilter = field(words(2), 24, 2).toInt,
      mipFilter = field(words(2), 26, 2).toInt,
      borderColour = field(words(3), 30, 2).toInt
    )
  }

  /** The coordinate, in texels of an axis `extent` wide, a clamp mode addresses before it picks texels. */
  def coordinate(mode: Int, value: Double, extent: Double): Double = mode match {
    case 3 | 7 => Math.abs(value)
    case 4     => Math.max(0.0, Math.min(value, extent))
    case 5     => Math.min(Math.abs(value), extent)
    case _     => value
  }

  /** The texel an unclamped index names along an axis `extent` wide, or -1 for the border colour. */
  def texel(mode: Int, index: Long, extent: Long): Long = mode match {
    case 0 => Math.floorMod(index, extent)
    case 1 =>
      val wrapped = Math.floorMod(index, 2 * extent)
      if (wrapped >= extent) 2 * extent - 1 - wrapped else wrapped
    case 2 | 3 => Math.max(0L, Math.min(extent - 1, index))
    case _     => if (index >= 0L && index < extent) index else -1L
  }

  /** Whether a reference passes the depth comparison against a texel's depth. */
  def passes(function: Int, reference: Double, depth: Double): Boolean = function match {
    case 0 => false
    case 1 => reference < depth
    case 2 => reference == depth
    case 3 => reference <= depth
    case 4 => reference > depth
    case 5 => reference != depth
    case 6 => reference >= depth
    case _ => true
  }
}

/** An image sampler descriptor (S#), four dwords, as `generation` lays it out.
  *
  * Clamp modes name how a coordinate beyond the image is addressed: 0 wraps, 1 mirrors, 2 repeats the last texel, 3
  * mirrors once then repeats the last texel, 4 and 5 clamp the coordinate to the image, or mirror it once, and read the
  * border past the edge, and 6 and 7 read the border for every texel outside, with the coordinate not clamped or
  * mirrored once.
  */
private[isa] case class SamplerResource(
    clampX: Int,
    clampY: Int,
    clampZ: Int,
    depthCompare: Int,
    forceUnnormalised: Boolean,
    filterMode: Int,
    minLod: Double,
    maxLod: Double,
    lodBias: Double,
    magnifyFilter: Int,
    minifyFilter: Int,
    zFilter: Int,
    mipFilter: Int,
    borderColour: Int
) derives CanEqual {
  def clamp(axis: Int): Int = axis match {
    case 0 => clampX
    case 1 => clampY
    case _ => clampZ
  }
}
