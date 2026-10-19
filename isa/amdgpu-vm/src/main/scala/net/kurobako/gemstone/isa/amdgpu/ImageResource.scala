package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*

/** Image resource descriptors: decoding, texel addressing, and the conversions of texels a descriptor's format and
  * destination selects define; the functions over prelude types are what definitions call and execution runs.
  */
private[isa] object ImageResource {
  import ResourceGeneration.*

  inline val Line    = 8
  inline val Plane   = 9
  inline val Volume  = 10
  inline val Cube    = 11
  inline val Array1D = 12
  inline val Array2D = 13

  inline val RowAlignment   = 256L
  inline val LevelAlignment = 256L

  private case class BitField(offset: Int, width: Int) {
    def of(word: Long): Long = (word >>> offset) & ((1L << width) - 1L)
  }

  private val LegacyDataFormat   = BitField(offset = 20, width = 6)
  private val LegacyNumberFormat = BitField(offset = 26, width = 4)
  private val GFX10Format        = BitField(offset = 20, width = 9)
  private val GFX11Format        = BitField(offset = 20, width = 8)
  private val GFX12Format        = BitField(offset = 17, width = 8)

  private val BaseHigh       = BitField(offset = 0, width = 8)
  private val Kind           = BitField(offset = 28, width = 4)
  private val Selects        = BitField(offset = 0, width = 12)
  private val Tiling         = BitField(offset = 20, width = 5)
  private val Width          = BitField(offset = 0, width = 14)
  private val Width12        = BitField(offset = 0, width = 12)
  private val Height         = BitField(offset = 14, width = 14)
  private val Height16       = BitField(offset = 14, width = 16)
  private val WidthHigh      = BitField(offset = 30, width = 2)
  private val BaseLevel      = BitField(offset = 12, width = 4)
  private val LastLevel      = BitField(offset = 16, width = 4)
  private val BaseLevelGFX12 = BitField(offset = 25, width = 5)
  private val LastLevelGFX12 = BitField(offset = 15, width = 5)
  private val Depth          = BitField(offset = 0, width = 13)
  private val DepthGFX12     = BitField(offset = 0, width = 14)
  private val BaseArrayLow   = BitField(offset = 0, width = 13)
  private val LastArrayLow   = BitField(offset = 13, width = 13)
  private val BaseArray      = BitField(offset = 16, width = 13)
  private val BaseArrayGFX12 = BitField(offset = 16, width = 14)
  private val Pitch8         = BitField(offset = 13, width = 14)
  private val Pitch9         = BitField(offset = 13, width = 16)
  private val PitchHighGFX12 = BitField(offset = 14, width = 2)
  private val PitchHigh      = BitField(offset = 13, width = 1)

  /** The selects of a store that leaves each channel where it is. */
  val IdentitySelects = 0xfacL

  /** The data format dword 1 names. */
  def format(generation: ResourceGeneration, word1: Long): TexelFormat = generation match {
    case GFX8 | GFX9 => TexelFormat.legacy(LegacyDataFormat.of(word1).toInt, LegacyNumberFormat.of(word1).toInt)
    case GFX10       => TexelFormat.unified(generation, GFX10Format.of(word1).toInt)
    case GFX11       => TexelFormat.unified(generation, GFX11Format.of(word1).toInt)
    case GFX12       => TexelFormat.unified(generation, GFX12Format.of(word1).toInt)
  }

  /** The linear image `words` describes on `isa`, or why execution cannot read it. */
  def decode(isa: ISA, words: Array[Long]): Either[String, ImageResource] = {
    val generation      = ResourceGeneration.of(isa)
    val base            = ((words(0) & 0xffffffffL) | (BaseHigh.of(words(1)) << 32)) << 8
    val kind            = Kind.of(words(3)).toInt
    val selects         = Selects.of(words(3))
    val format          = this.format(generation, words(1))
    val (width, height) = generation match {
      case GFX8 | GFX9   => (Width.of(words(2)) + 1L, Height.of(words(2)) + 1L)
      case GFX10 | GFX11 =>
        ((WidthHigh.of(words(1)) | (Width12.of(words(2)) << 2)) + 1L, Height.of(words(2)) + 1L)
      case GFX12 => ((WidthHigh.of(words(1)) | (Width.of(words(2)) << 2)) + 1L, Height16.of(words(2)) + 1L)
    }
    val (baseLevel, lastLevel) = generation match {
      case GFX12 => (BaseLevelGFX12.of(words(1)).toInt, LastLevelGFX12.of(words(3)).toInt)
      case _     => (BaseLevel.of(words(3)).toInt, LastLevel.of(words(3)).toInt)
    }
    val tiling = Tiling.of(words(3))
    // GFX8 linear-aligned images use tiling index 8; later generations name the linear swizzle mode 0
    val linear     = if (generation == GFX8) tiling == 8L else tiling == 0L
    val depthField = generation match {
      case GFX12 => DepthGFX12.of(words(4))
      case _     => Depth.of(words(4))
    }
    val (baseArray, lastArray) = generation match {
      case GFX8  => (BaseArrayLow.of(words(5)), LastArrayLow.of(words(5)))
      case GFX9  => (BaseArrayLow.of(words(5)), depthField)
      case GFX12 => (BaseArrayGFX12.of(words(4)), depthField)
      case _     => (BaseArray.of(words(4)), depthField)
    }
    val customPitch = isa match {
      case ISA.RDNA2 | ISA.RDNA3 | ISA.RDNA3_5 | ISA.GFX11_7 | ISA.RDNA4 | ISA.CDNA5 => kind == Line || kind == Plane
      case _                                                                         => false
    }
    val texelBytes = format.layout.bytes.toLong
    val pitch      = generation match {
      case GFX8                 => (Pitch8.of(words(4)) + 1L) * texelBytes
      case GFX9                 => (Pitch9.of(words(4)) + 1L) * texelBytes
      case GFX12 if customPitch => ((depthField | (PitchHighGFX12.of(words(4)) << 14)) + 1L) * texelBytes
      case _ if customPitch     => ((depthField | (PitchHigh.of(words(4)) << 13)) + 1L) * texelBytes
      case _                    => 0L
    }
    val depth = if (kind == Volume) depthField + 1L else 1L
    if (!linear) Left(s"tiled image layouts (swizzle mode $tiling) are not implemented")
    else if (!format.valid) Left("the image descriptor names no data format execution implements")
    else if (kind < Line || kind > Array2D) Left(s"image resource type $kind is not implemented")
    else
      Right(ImageResource(
        base,
        format,
        width,
        height,
        depth,
        kind,
        baseLevel,
        lastLevel,
        baseArray,
        lastArray,
        if (pitch < width * texelBytes) 0L else pitch,
        selects
      ))
  }

  /** The dimension count an instruction's DIM field implies, for addresses on GFX10 onwards. */
  def coordinates(dimension: ImageDimension): Int = dimension match {
    case ImageDimension.Image1d          => 1
    case ImageDimension.Image2d          => 2
    case ImageDimension.Image1dArray     => 2
    case ImageDimension.Image2dMsaa      => 3
    case ImageDimension.Image2dMsaaArray => 4
    case _                               => 3
  }

  /** The address of the texel an image address names, or -1 outside `resource`: its coordinates from `c0`, as many as
    * the instruction's `dimension` takes or else the descriptor's type, then the level when `mip`.
    */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def locate(
      resource: ImageResource,
      dimension: Option[ImageDimension],
      mip: Boolean,
      c0: Long,
      c1: Long,
      c2: Long,
      c3: Long,
      c4: Long
  ): Long = {
    val level =
      if (!mip) 0
      else
        dimension.fold(resource.coordinates)(coordinates(_)) match {
          case 1 => c1.toInt
          case 2 => c2.toInt
          case 3 => c3.toInt
          case _ => c4.toInt
        }
    resource.kind match {
      case Line    => resource.texel(level, c0, 0L, 0L)
      case Array1D => resource.texel(level, c0, 0L, c1)
      case Plane   => resource.texel(level, c0, c1, 0L)
      case _       => resource.texel(level, c0, c1, c2)
    }
  }

  /** Channel `component` of the texel `low` and `high` hold, unconverted: its bits, sign-extended when `signed`, as the
    * 32-bit value a register receives or the 16 bits a D16 half does when `half`; zero past the format's channels.
    */
  def channel(format: TexelFormat, component: Int, low: Long, high: Long, signed: Boolean, half: Boolean): Long =
    if (component >= format.layout.channels) 0L
    else {
      val width = format.layout.widths(component)
      val bits  = TexelFormat.channel(format.layout, component, low, high)
      val value = if (signed) Bits.low32(Bits.signExtend(bits, width)).toLong else bits
      if (half) Bits.low16(value).toLong else value
    }

  private inline def bits(value: UInt[32]): Long = toRaw[UInt[32]](value)

  /** The bytes of a texel of the format dword 1 names. */
  def texelBytes(generation: ResourceGeneration, word1: UInt[32]): UInt[32] =
    fromRaw[UInt[32]](format(generation, bits(word1)).layout.bytes.toLong)

  /** [[TexelFormat.component]] `component` of a texel of the format dword 1 names, through the selects of dword 3. */
  def component(
      generation: ResourceGeneration,
      word1: UInt[32],
      word3: UInt[32],
      low: UInt[64],
      high: UInt[64],
      component: Int,
      half: Boolean
  ): UInt[32] = fromRaw[UInt[32]](
    TexelFormat.component(
      format(generation, bits(word1)),
      TexelFormat.select(bits(word3), component),
      toRaw[UInt[64]](low),
      toRaw[UInt[64]](high),
      half
    )
  )

  /** [[channel]] `component` of a texel of the format dword 1 names. */
  def unconverted(
      generation: ResourceGeneration,
      word1: UInt[32],
      low: UInt[64],
      high: UInt[64],
      component: Int,
      signed: Boolean,
      half: Boolean
  ): UInt[32] = fromRaw[UInt[32]](
    channel(
      format(generation, bits(word1)),
      component,
      toRaw[UInt[64]](low),
      toRaw[UInt[64]](high),
      signed,
      half
    )
  )

  /** [[TexelFormat.packed]] in the format dword 1 names, through the selects of dword 3. */
  def packed(
      generation: ResourceGeneration,
      word1: UInt[32],
      word3: UInt[32],
      x: UInt[32],
      y: UInt[32],
      z: UInt[32],
      w: UInt[32],
      part: Int
  ): UInt[64] = fromRaw[UInt[64]](
    TexelFormat.packed(
      format(generation, bits(word1)),
      Selects.of(bits(word3)),
      Array(bits(x), bits(y), bits(z), bits(w)),
      converted = true,
      part
    )
  )

  /** The bits of the channels x to w in the format dword 1 names, unconverted. */
  def packedBits(
      generation: ResourceGeneration,
      word1: UInt[32],
      x: UInt[32],
      y: UInt[32],
      z: UInt[32],
      w: UInt[32],
      part: Int
  ): UInt[64] = fromRaw[UInt[64]](
    TexelFormat.packed(
      format(generation, bits(word1)),
      IdentitySelects,
      Array(bits(x), bits(y), bits(z), bits(w)),
      converted = false,
      part
    )
  )

  /** [[TexelFormat.widen]] in the format dword 1 names. */
  def widened(generation: ResourceGeneration, word1: UInt[32], value: UInt[16]): UInt[32] =
    fromRaw[UInt[32]](
      TexelFormat.widen(format(generation, bits(word1)).number, toRaw[UInt[16]](value))
    )
}

/** An image resource descriptor (T#), eight dwords, of a linear image, as `generation` lays it out.
  *
  * Execution reads linear images only: level 0 rows are `pitch` bytes apart, the descriptor's PITCH on GFX8 and GFX9
  * and, on RDNA2 onwards, the DEPTH and PITCH_MSB fields of a one- or two-dimensional image without layers; otherwise,
  * and for later levels, rows are 256-byte aligned. A level's slices follow each other and each level starts 256-byte
  * aligned after the last. Coordinates and levels outside the image read zero and drop stores.
  */
private[isa] case class ImageResource(
    base: Long,
    format: TexelFormat,
    width: Long,
    height: Long,
    depth: Long,
    kind: Int,
    baseLevel: Int,
    lastLevel: Int,
    baseArray: Long,
    lastArray: Long,
    pitch: Long,
    selects: Long
) {
  import ImageResource.*

  def texelBytes: Int = format.layout.bytes

  def volume: Boolean  = kind == Volume
  def layered: Boolean = kind == Array1D || kind == Array2D || kind == Cube

  def levelWidth(level: Int): Long  = Math.max(1L, width >>> level)
  def levelHeight(level: Int): Long = if (kind == Array1D || kind == Line) 1L else Math.max(1L, height >>> level)
  def levelDepth(level: Int): Long  = if (volume) Math.max(1L, depth >>> level) else 1L

  /** The slices a level holds: its depth, or the layers from the first. */
  def slices(level: Int): Long = if (volume) levelDepth(level) else if (layered) lastArray + 1L else 1L

  private def rowBytes(level: Int): Long =
    if (level == 0 && pitch != 0L) pitch else (levelWidth(level) * texelBytes).alignUp(RowAlignment)

  private def levelBytes(level: Int): Long = rowBytes(level) * levelHeight(level) * slices(level)

  private def levelOffset(level: Int): Long = (0 until level).foldLeft(0L)((offset, previous) =>
    (offset + levelBytes(previous)).alignUp(LevelAlignment)
  )

  /** The address of texel (`x`, `y`, `slice`) of `level` counted from the base level, or -1 outside the image. */
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def texel(level: Int, x: Long, y: Long, slice: Long): Long = {
    val absolute = baseLevel + level
    val layer    = if (layered) baseArray + slice else slice
    val limit    = if (volume) levelDepth(absolute) else if (layered) lastArray + 1L else 1L
    if (
      level < 0 || absolute > lastLevel || x < 0L || y < 0L || slice < 0L || x >= levelWidth(absolute) ||
      y >= levelHeight(absolute) || layer >= limit
    ) -1L
    else
      base + levelOffset(absolute) + (layer * levelHeight(absolute) + y) * rowBytes(absolute) + x * texelBytes
  }

  /** What GET_RESINFO returns for component `component` at `level`: width, height, depth or layers, and levels. */
  def information(level: Int, component: Int): Long = {
    val absolute = baseLevel + level
    component match {
      case 0 => levelWidth(absolute)
      case 1 => if (kind == Array1D) slices(absolute) else if (kind == Line) 0L else levelHeight(absolute)
      case 2 =>
        if (volume) levelDepth(absolute)
        else if (kind == Array2D) slices(absolute)
        else if (kind == Cube) slices(absolute) / 6L else 0L
      case _ => (lastLevel - baseLevel + 1).toLong
    }
  }

  /** The coordinates an address names for this kind of image, before any mip level. */
  def coordinates: Int = kind match {
    case Line            => 1
    case Plane | Array1D => 2
    case _               => 3
  }
}
