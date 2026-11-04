package net.kurobako.gemstone.texture

/** Raw surface access in texel bytes, which no format conversion touches. */
object SurfaceAccess {
  private def located(position: SurfacePosition, size: Int, mode: BoundaryMode): Either[TexelFault, Option[Long]] = {
    val SurfacePosition(d, x, y, z)            = position
    val height                                 = d.height.toLong
    val depth                                  = d.depth.toLong
    val last                                   = d.widthBytes - size
    val inside                                 = x >= 0 && x <= last && y >= 0 && y < height && z >= 0 && z < depth
    def at(cx: Long, cy: Long, cz: Long): Long = d.base + cz * d.slicePitch + cy * d.pitch + cx
    mode match {
      case _ if inside        => Right(Some(at(x, y, z)))
      case BoundaryMode.Trap  => Left(TexelFault.OutOfBounds(x, y, z))
      case BoundaryMode.Zero  => Right(None)
      case BoundaryMode.Clamp =>
        Right(Some(at(
          TextureSampler.resolve(AddressMode.Clamp, x, last + 1),
          TextureSampler.resolve(AddressMode.Clamp, y, height),
          TextureSampler.resolve(AddressMode.Clamp, z, depth)
        )))
    }
  }

  /** `size` bytes from `position`; lower-dimensional surfaces have one row and one slice. */
  def load(
      position: SurfacePosition,
      size: Int,
      mode: BoundaryMode,
      memory: TexelMemory
  ): Either[TexelFault, Array[Byte]] =
    located(position, size, mode).flatMap {
      case None          => Right(Array.ofDim[Byte](size))
      case Some(address) => memory.read(address, size).toRight(TexelFault.Unmapped(address))
    }

  def store(position: SurfacePosition, bytes: Array[Byte], mode: BoundaryMode, memory: TexelMemory)
      : Either[TexelFault, Unit] =
    located(position, bytes.length, mode).flatMap {
      case None          => Right(())
      case Some(address) => Either.cond(memory.write(address, bytes), (), TexelFault.Unmapped(address))
    }
}
