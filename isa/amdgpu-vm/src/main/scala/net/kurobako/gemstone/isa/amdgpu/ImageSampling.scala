package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.vm.*

/** Image sampling: the level a lod and the sampler pick, address modes, point and bilinear filtering across axes, mip
  * blending, depth comparison and gathers over a linear [[ImageResource]].
  *
  * Filter weights are quantised to 1/256. A sample of integer texels is never filtered. Texels past the image under a
  * border mode read the sampler's border colour, as transparent black, opaque black or opaque white.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var", "scalafix:DisableSyntax.null"))
private[isa] class ImageSampling(reader: TexelReader) {
  import SamplerResource.*

  private val footprint = Vec((0, 1), (1, 1), (1, 0), (0, 0))
  private val acc       = Array.ofDim[Double](4)
  private val blend     = Array.ofDim[Double](4)
  private val tap       = Array.ofDim[Double](4)
  private val tapRaw    = Array.ofDim[Long](4)

  private val base    = Array.ofDim[Long](3)
  private val linear  = Array.ofDim[Boolean](3)
  private val weights = Array.ofDim[Double](6)
  private val index   = Array.ofDim[Long](3)

  private var firstLevel  = 0
  private var secondLevel = 0
  private var mipWeight   = 0.0

  /** The axes a resource kind is addressed along, not counting layers, or 0 when sampling does not read it. */
  def axes(kind: Int): Int = kind match {
    case ImageResource.Line | ImageResource.Array1D  => 1
    case ImageResource.Plane | ImageResource.Array2D => 2
    case ImageResource.Volume                        => 3
    case _                                           => 0
  }

  private def quantise(fraction: Double): Double = Math.rint(fraction * 256.0) / 256.0

  private def extent(r: ImageResource, level: Int, axis: Int): Long = axis match {
    case 0 => r.levelWidth(r.baseLevel + level)
    case 1 => r.levelHeight(r.baseLevel + level)
    case _ => r.levelDepth(r.baseLevel + level)
  }

  private def border(s: SamplerResource, channel: Int): Double = s.borderColour match {
    case 1 => if (channel == 3) 1.0 else 0.0
    case 2 => 1.0
    case _ => 0.0
  }

  private def sliceCount(r: ImageResource): Long = if (r.layered) r.lastArray - r.baseArray + 1L else 1L

  /** The level's gradient length, the larger of the two directions, as a log2 of texels. */
  private def derivedLod(r: ImageResource, s: SamplerResource, q: SampleRequest, axes: Int): Double = {
    var along  = 0.0
    var across = 0.0
    var axis   = 0
    while (axis < axes) {
      val scale = if (isUnnormalised(s, q)) 1.0 else extent(r, 0, axis).toDouble
      val dx    = q.gradients(axis) * scale
      val dy    = q.gradients(3 + axis) * scale
      along += dx * dx
      across += dy * dy
      axis += 1
    }
    val rho = Math.sqrt(Math.max(along, across))
    if (rho > 0.0 && !rho.isInfinite) Math.log(rho) / Math.log(2.0) else if (rho.isInfinite) 64.0 else -64.0
  }

  private def lodOf(r: ImageResource, s: SamplerResource, q: SampleRequest, axes: Int, levels: Int): Double = {
    val requested = q.level match {
      case VMIR.SampleLevel.Zero  => 0.0
      case VMIR.SampleLevel.Level => q.lod
      case VMIR.SampleLevel.Bias  => derivedLod(r, s, q, axes) + Math.max(-16.0, Math.min(16.0, q.bias))
      case _                      => derivedLod(r, s, q, axes)
    }
    val biased  = (if (requested.isNaN) 0.0 else requested) + s.lodBias
    val floored = if (q.minimumLod.isNaN) biased else Math.max(biased, q.minimumLod)
    Math.min(Math.max(floored, s.minLod), Math.min(s.maxLod, (levels - 1).toDouble))
  }

  private def selectLevels(s: SamplerResource, lod: Double, levels: Int): Unit = {
    firstLevel = 0
    secondLevel = 0
    mipWeight = 0.0
    if (levels > 1) s.mipFilter match {
      case 1 => firstLevel = Math.max(0, Math.min(levels - 1, Math.ceil(lod + 0.5).toInt - 1)); secondLevel = firstLevel
      case 2 =>
        firstLevel = Math.max(0, Math.min(levels - 1, Math.floor(lod).toInt))
        secondLevel = Math.min(levels - 1, firstLevel + 1)
        mipWeight = if (secondLevel == firstLevel) 0.0 else quantise(lod - firstLevel.toDouble)
      case _ =>
    }
  }

  private def scaled(unnormalised: Boolean, value: Double, extent: Long, level: Int, mode: Int): Double = {
    val coordinate = if (value.isNaN) 0.0 else value
    val texels     =
      if (unnormalised) coordinate / (1L << level).toDouble else (coordinate.toFloat * extent.toFloat).toDouble
    SamplerResource.coordinate(mode, texels, extent.toDouble)
  }

  private def isUnnormalised(s: SamplerResource, q: SampleRequest): Boolean = s.forceUnnormalised || q.unnormalised

  /** The texel at (`x`, `y`, `z`) of `level`, or the border when an index is -1, in `tap` or, integers, `tapRaw`. */
  private def fetch(
      r: ImageResource,
      s: SamplerResource,
      level: Int,
      x: Long,
      y: Long,
      z: Long,
      slice: Long
  ): Fault | Null = {
    val integral = r.format.integral
    val outside  = x < 0L || y < 0L || z < 0L
    val address  =
      if (outside) -1L
      else if (r.volume) r.texel(level, x, y, z)
      else r.texel(level, x, y, slice)
    if (outside) {
      var channel = 0
      while (channel < 4) {
        val value = border(s, channel)
        tap(channel) = value
        tapRaw(channel) = if (integral) value.toLong else bits(value)
        channel += 1
      }
      null
    } else if (address < 0L) {
      java.util.Arrays.fill(tap, 0.0)
      java.util.Arrays.fill(tapRaw, 0L)
      null
    } else {
      val fault = reader.read(address, r.texelBytes)
      if (fault != null) fault
      else {
        var channel = 0
        while (channel < 4) {
          val raw = TexelFormat.component(
            r.format,
            TexelFormat.select(r.selects, channel),
            reader.low,
            reader.high,
            half = false
          )
          tapRaw(channel) = raw
          tap(channel) = if (integral) raw.toDouble else java.lang.Float.intBitsToFloat(raw.toInt).toDouble
          channel += 1
        }
        null
      }
    }
  }

  private def compared(s: SamplerResource, q: SampleRequest): Unit =
    if (q.compare) {
      val pass    = passes(s.depthCompare, q.reference, tap(0))
      val one     = if (pass) 1.0 else 0.0
      val encoded = bits(one)
      var channel = 0
      while (channel < 4) { tap(channel) = one; tapRaw(channel) = encoded; channel += 1 }
    }

  private def slice(r: ImageResource, q: SampleRequest): Long =
    if (r.layered)
      Math.max(0L, Math.min(sliceCount(r) - 1L, Math.floor((if (q.layer.isNaN) 0.0 else q.layer) + 0.5).toLong))
    else 0L

  /** The filtered texel of `level` in `out`. */
  private def sampleLevel(
      r: ImageResource,
      s: SamplerResource,
      q: SampleRequest,
      axes: Int,
      level: Int,
      filterXY: Boolean,
      filterZ: Boolean,
      out: Array[Double]
  ): Fault | Null = {
    val integral     = r.format.integral
    val layer        = slice(r, q)
    val unnormalised = isUnnormalised(s, q)
    var axis         = 0
    while (axis < 3) {
      if (axis < axes) {
        val size  = extent(r, level, axis)
        val mode  = s.clamp(axis)
        val point = scaled(unnormalised, q.coordinates(axis), size, r.baseLevel + level, mode)
        val wants = !integral && (if (axis < 2) filterXY else filterZ)
        linear(axis) = wants
        if (wants) {
          val shifted = point - 0.5
          val low     = Math.floor(shifted)
          base(axis) = low.toLong + q.offsets(axis)
          val weight = quantise(shifted - low)
          weights(2 * axis) = 1.0 - weight
          weights(2 * axis + 1) = weight
        } else {
          base(axis) = Math.floor(point).toLong + q.offsets(axis)
          weights(2 * axis) = 1.0
          weights(2 * axis + 1) = 0.0
        }
      } else {
        linear(axis) = false
        base(axis) = 0L
        weights(2 * axis) = 1.0
        weights(2 * axis + 1) = 0.0
      }
      axis += 1
    }
    java.util.Arrays.fill(out, 0.0)
    val extremum            = s.filterMode
    var first               = true
    val taps                = (if (linear(0)) 2 else 1) * (if (linear(1)) 2 else 1) * (if (linear(2)) 2 else 1)
    var which               = 0
    var fault: Fault | Null = null
    while (which < taps && fault == null) {
      var remaining = which
      var weight    = 1.0
      axis = 0
      while (axis < 3) {
        val digit = if (linear(axis)) { val d = remaining & 1; remaining >>= 1; d }
        else 0
        weight *= weights(2 * axis + digit)
        val unclamped = base(axis) + digit
        index(axis) = if (axis < axes) texel(s.clamp(axis), unclamped, extent(r, level, axis)) else 0L
        axis += 1
      }
      fault = fetch(r, s, level, index(0), index(1), index(2), layer)
      if (fault == null) {
        compared(s, q)
        if (integral) {
          var channel = 0
          while (channel < 4) { out(channel) = tap(channel); channel += 1 }
        } else if (extremum == 0) {
          var channel = 0
          while (channel < 4) { out(channel) += weight * tap(channel); channel += 1 }
        } else if (weight > 0.0) {
          var channel = 0
          while (channel < 4) {
            out(channel) =
              if (first) tap(channel)
              else if (extremum == 1) Math.min(out(channel), tap(channel)) else Math.max(out(channel), tap(channel))
            channel += 1
          }
          first = false
        }
      }
      which += 1
    }
    fault
  }

  private def gathered(
      r: ImageResource,
      s: SamplerResource,
      q: SampleRequest,
      axes: Int,
      level: Int
  ): Fault | Null = {
    val layer        = slice(r, q)
    val unnormalised = isUnnormalised(s, q)
    val lows         = Array.ofDim[Long](2)
    var axis         = 0
    while (axis < 2) {
      val point = scaled(unnormalised, q.coordinates(axis), extent(r, level, axis), r.baseLevel + level, s.clamp(axis))
      lows(axis) = Math.floor(point - 0.5).toLong + q.offsets(axis)
      axis += 1
    }
    var fault: Fault | Null = null
    var which               = 0
    while (which < 4 && fault == null) {
      val (dx, dy) = footprint(which)
      val x        = texel(s.clamp(0), lows(0) + dx, extent(r, level, 0))
      val y        = if (axes > 1) texel(s.clamp(1), lows(1) + dy, extent(r, level, 1)) else 0L
      fault = fetch(r, s, level, x, y, 0L, layer)
      if (fault == null) { compared(s, q); q.result(which) = tapRaw(q.gather) }
      which += 1
    }
    fault
  }

  private def bits(value: Double): Long = Bits.low32(java.lang.Float.floatToRawIntBits(value.toFloat)).toLong

  /** Samples `r` through `s` as `q` asks, leaving the four register values in `q.result`. */
  def sample(r: ImageResource, s: SamplerResource, q: SampleRequest): Fault | Null = {
    val dimensions = axes(r.kind)
    if (dimensions == 0) Fault.Unsupported("AMDGPU image sampling of a cube map is not implemented")
    else if (!r.format.valid) Fault.Unsupported("AMDGPU image sampling of a format execution does not convert")
    else {
      val levels = Math.max(1, r.lastLevel - r.baseLevel + 1)
      val lod    = lodOf(r, s, q, dimensions, levels)
      selectLevels(s, lod, levels)
      if (q.gather >= 0) {
        val nearest = if (s.mipFilter == 2 && mipWeight >= 0.5) secondLevel else firstLevel
        gathered(r, s, q, dimensions, nearest)
      } else {
        val filterXY = ((if (lod <= 0.0) s.magnifyFilter else s.minifyFilter) & 1) != 0
        val filterZ  = s.zFilter == 2
        val fault    = sampleLevel(r, s, q, dimensions, firstLevel, filterXY, filterZ, acc)
        if (fault != null) fault
        else {
          val integral = r.format.integral
          if (mipWeight > 0.0 && !integral) {
            val second = sampleLevel(r, s, q, dimensions, secondLevel, filterXY, filterZ, blend)
            if (second != null) second
            else {
              var channel = 0
              while (channel < 4) {
                acc(channel) = acc(channel) * (1.0 - mipWeight) + blend(channel) * mipWeight
                channel += 1
              }
              finish(q, integral)
            }
          } else finish(q, integral)
        }
      }
    }
  }

  private def finish(q: SampleRequest, integral: Boolean): Null = {
    var channel = 0
    while (channel < 4) {
      q.result(channel) = if (integral) acc(channel).toLong else bits(acc(channel))
      channel += 1
    }
    null
  }
}
