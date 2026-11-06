package net.kurobako.gemstone.texture

/** Texture reads: address modes, point and linear filtering, and format conversion over a [[TextureDescriptor]].
  *
  * Wrap and mirror only apply to normalised coordinates and behave as clamp otherwise. Linear filter weights are
  * quantised to 1/256 as on hardware. An integral result is never filtered. The number of coordinates given is the
  * number of axes sampled, so a 2D lookup of a 1D object reads row 0, and linear filtering blends 2, 4, or 8 texels,
  * with texels outside under [[AddressMode.Border]] contributing the border colour.
  */
object TextureSampler {
  private val WeightSteps = 256.0
  private val Footprint   = Vector((0, 1), (1, 1), (1, 0), (0, 0))

  /** The texel an in-range or addressed `index` names along an axis of `extent` texels, or -1 for the border. */
  def resolve(mode: AddressMode, index: Long, extent: Long): Long = mode match {
    case AddressMode.Wrap   => Math.floorMod(index, extent)
    case AddressMode.Mirror =>
      val wrapped = Math.floorMod(index, 2 * extent)
      if (wrapped >= extent) 2 * extent - 1 - wrapped else wrapped
    case AddressMode.Clamp  => Math.max(0L, Math.min(extent - 1, index))
    case AddressMode.Border => if (index >= 0 && index < extent) index else -1L
  }

  private def effective(d: TextureDescriptor, axis: Int, normalised: Boolean): AddressMode =
    d.address(axis) match {
      case AddressMode.Wrap | AddressMode.Mirror if !normalised => AddressMode.Clamp
      case mode                                                 => mode
    }

  private def toIndex(value: Double): Long =
    if (value.isNaN) 0L else Math.floor(value).toLong

  private def scaled(d: TextureDescriptor, axis: Int, coordinate: Double, normalised: Boolean): Double = {
    val c = if (coordinate.isNaN) 0.0 else coordinate
    if (normalised) c * d.extent(axis).toDouble else c
  }

  private def borderTexel(d: TextureDescriptor): Texel =
    Texel(d.border.map(_.toDouble), d.format.isIntegral(d.readMode))

  private def readTexel(
      d: TextureDescriptor,
      x: Long,
      y: Long,
      z: Long,
      memory: TexelMemory
  ): Either[TexelFault, Texel] = {
    val address = d.texelAddress(x, y, z)
    memory.read(address, d.format.texelBytes) match {
      case None        => Left(TexelFault.Unmapped(address))
      case Some(bytes) =>
        val values   = d.format.texel(bytes, 0, d.readMode, d.srgb)
        val integral = d.format.isIntegral(d.readMode)
        Right(Texel(if (integral) values else values.map(_.toFloat.toDouble), integral))
    }
  }

  private def fetchAt(d: TextureDescriptor, indices: Vector[Long], modes: Vector[AddressMode], memory: TexelMemory)
      : Either[TexelFault, Texel] = {
    val resolved = indices.zipWithIndex.map((index, axis) => resolve(modes(axis), index, d.extent(axis).toLong))
    if (resolved.exists(_ < 0)) Right(borderTexel(d))
    else readTexel(d, resolved(0), resolved.lift(1).getOrElse(0L), resolved.lift(2).getOrElse(0L), memory)
  }

  /** The texel at integer coordinates `indices` of mipmap level `level`, which address modes still apply to. */
  def fetch(d: TextureDescriptor, indices: IndexedSeq[Long], memory: TexelMemory, level: Int = 0)
      : Either[TexelFault, Texel] = {
    val chosen = d.mip.fold(Right(d))(d.atLevel(_, level, memory))
    chosen.flatMap(l =>
      fetchAt(
        l,
        indices.take(3).toVector,
        Vector.tabulate(indices.length.min(3))(effective(l, _, normalised = false)),
        memory
      )
    )
  }

  private def log2(value: Double): Double = Math.log(value) / Math.log(2.0)

  /** The level of detail `detail` asks for, biased and clamped to the chain, in levels from the base. */
  private def lambda(d: TextureDescriptor, chain: MipChain, detail: LevelOfDetail, axes: Int): Double = {
    val normalised                                   = d.normalisedCoordinates
    def length(gradient: IndexedSeq[Double]): Double = Math.sqrt(
      gradient.take(axes).zipWithIndex.map((g, axis) => Math.pow(scaled(d, axis, g, normalised), 2)).sum
    )
    val requested = detail match {
      case LevelOfDetail.Explicit(level)  => if (level.isNaN) 0.0 else level
      case LevelOfDetail.Gradient(dx, dy) => log2(Math.max(length(dx), length(dy)))
    }
    val biased = requested + chain.bias
    Math.max(
      0.0,
      Math.min(chain.levels - 1.0, Math.max(chain.minLod.toDouble, Math.min(chain.maxLod.toDouble, biased)))
    )
  }

  private def blend(from: Texel, to: Texel, weight: Double): Texel =
    Texel(
      Vector.tabulate(4)(lane => ((1.0 - weight) * from(lane) + weight * to(lane)).toFloat.toDouble),
      integral = false
    )

  private def sampleChain(
      d: TextureDescriptor,
      chain: MipChain,
      coordinates: IndexedSeq[Double],
      detail: LevelOfDetail,
      memory: TexelMemory
  ): Either[TexelFault, Texel] = {
    val lod                                       = lambda(d, chain, detail, coordinates.length.min(3))
    def at(level: Int): Either[TexelFault, Texel] = d.atLevel(chain, level, memory).flatMap { l =>
      val relative = coordinates.zipWithIndex.map((c, axis) =>
        if (d.normalisedCoordinates || axis >= 3) c else c * l.extent(axis).toDouble / d.extent(axis).toDouble
      )
      sampleLevel(l, relative, memory)
    }
    val lower  = Math.floor(lod).toInt
    val weight = quantise(lod - lower)
    if (chain.filter == FilterMode.Point || d.format.isIntegral(d.readMode))
      at(Math.max(0, Math.min(chain.levels - 1, Math.floor(lod + 0.5).toInt)))
    else if (lower >= chain.levels - 1 || weight == 0.0) at(lower)
    else
      for {
        near <- at(lower)
        far  <- at(lower + 1)
      } yield blend(near, far, weight)
  }

  /** Sampling at `coordinates`, normalised or not as the descriptor says. Unnormalised coordinates count texels of
    * level zero. A mipmapped texture reads the level or levels `detail` selects.
    */
  def sample(
      d: TextureDescriptor,
      coordinates: IndexedSeq[Double],
      memory: TexelMemory,
      detail: LevelOfDetail = LevelOfDetail.Explicit(0.0)
  ): Either[TexelFault, Texel] = d.mip match {
    case Some(chain) => sampleChain(d, chain, coordinates, detail, memory)
    case None        => sampleLevel(d, coordinates, memory)
  }

  private def sampleLevel(d: TextureDescriptor, coordinates: IndexedSeq[Double], memory: TexelMemory)
      : Either[TexelFault, Texel] = {
    val normalised = d.normalisedCoordinates
    val axes       = coordinates.length.min(3)
    val modes      = Vector.tabulate(axes)(effective(d, _, normalised))
    val texels     = Vector.tabulate(axes)(axis => scaled(d, axis, coordinates(axis), normalised))
    if (d.filter == FilterMode.Point || d.format.isIntegral(d.readMode))
      fetchAt(d, texels.map(toIndex(_)), modes, memory)
    else {
      val lows    = texels.map(t => toIndex(t - 0.5))
      val weights = texels.zip(lows).map((t, low) => quantise(t - 0.5 - low.toDouble))
      val corners = (0 until (1 << axes)).toVector
      val taps    = corners.map { corner =>
        val indices = lows.zipWithIndex.map((low, axis) => low + ((corner >> axis) & 1))
        val weight  = weights.zipWithIndex.map((w, axis) => if (((corner >> axis) & 1) == 1) w else 1.0 - w).product
        fetchAt(d, indices, modes, memory).map(texel => (weight, texel))
      }
      taps.collectFirst { case Left(fault) => fault } match {
        case Some(fault) => Left(fault)
        case None        =>
          val blended = Vector.tabulate(4)(lane => taps.collect { case Right((w, t)) => w * t(lane) }.sum)
          Right(Texel(blended.map(_.toFloat.toDouble), integral = false))
      }
    }
  }

  /** The four texels of the 2x2 footprint below and left of the sample point, in the order tld4 returns them: (i0, j1),
    * (i1, j1), (i1, j0), (i0, j0). Each lane is `component` of that texel.
    */
  def gather(d: TextureDescriptor, coordinates: IndexedSeq[Double], component: Int, memory: TexelMemory)
      : Either[TexelFault, Texel] = {
    val normalised = d.normalisedCoordinates
    val modes      = Vector.tabulate(2)(effective(d, _, normalised))
    val lows       =
      Vector.tabulate(2)(axis => toIndex(scaled(d, axis, coordinates.lift(axis).getOrElse(0.0), normalised) - 0.5))
    val taps = Footprint.map((i, j) => fetchAt(d, Vector(lows(0) + i, lows(1) + j), modes, memory))
    taps.collectFirst { case Left(fault) => fault } match {
      case Some(fault) => Left(fault)
      case None        =>
        val texels = taps.collect { case Right(texel) => texel }
        Right(Texel(texels.map(_(component)), texels.head.integral))
    }
  }

  private def quantise(fraction: Double): Double = Math.rint(fraction * WeightSteps) / WeightSteps
}
