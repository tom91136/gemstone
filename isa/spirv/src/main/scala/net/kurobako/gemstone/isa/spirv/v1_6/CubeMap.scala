package net.kurobako.gemstone.isa.spirv.v1_6

// Vulkan cube coordinates use +X,-X,+Y,-Y,+Z,-Z face order. Ties prefer Z, then Y.
private object CubeMap {
  case class Projection(face: Int, s: Double, t: Double)
  case class Texel(face: Int, x: Long, y: Long)

  private def components(face: Int, x: Double, y: Double, z: Double): (Double, Double, Double) = face match {
    case 0 => (-z, -y, x)
    case 1 => (z, -y, -x)
    case 2 => (x, z, y)
    case 3 => (x, -z, -y)
    case 4 => (x, -y, z)
    case 5 => (-x, -y, -z)
  }
  def project(x: Double, y: Double, z: Double): Projection = {
    val ax   = Math.abs(x); val ay = Math.abs(y); val az = Math.abs(z)
    val face = if (az >= ax && az >= ay) { if (z >= 0) 4 else 5 }
    else if (ay >= ax) { if (y >= 0) 2 else 3 }
    else if (x >= 0) 0 else 1
    onFace(face, x, y, z)
  }
  private def onFace(face: Int, x: Double, y: Double, z: Double): Projection = {
    val (s, t, r) = components(face, x, y, z)
    val major     = if (r == 0) 1.0 else r
    Projection(face, .5 * (s / major + 1), .5 * (t / major + 1))
  }
  def gradient(face: Int, coord: Array[Double], derivative: Array[Double]): (Double, Double) = {
    val (s, t, r)    = components(face, coord(0), coord(1), coord(2))
    val (ds, dt, dr) = components(face, derivative(0), derivative(1), derivative(2))
    if (r == 0) (0.0, 0.0) else ((ds - s / r * dr) / (2 * r), (dt - t / r * dr) / (2 * r))
  }
  private def direction(face: Int, s: Double, t: Double): (Double, Double, Double) = face match {
    case 0 => (1, -t, -s)
    case 1 => (-1, -t, s)
    case 2 => (s, 1, t)
    case 3 => (s, -1, -t)
    case 4 => (s, -t, 1)
    case 5 => (-s, -t, -1)
  }
  private def clamp(v: Long, size: Long): Long = Math.max(0L, Math.min(size - 1, v))
  // At an edge, rotate the integer texel location onto its adjacent face. Use
  // the boundary direction for coordinates, avoiding perspective compression.
  private def edge(face: Int, x: Long, y: Long, size: Long): Texel = {
    val s            = 2.0 * (x + .5) / size - 1; val t = 2.0 * (y + .5) / size - 1
    val (dx, dy, dz) = direction(face, s, t)
    val neighbor     = project(dx, dy, dz).face
    val (bx, by, bz) = direction(face, Math.max(-1.0, Math.min(1.0, s)), Math.max(-1.0, Math.min(1.0, t)))
    val p            = onFace(neighbor, bx, by, bz)
    Texel(neighbor, clamp(Math.floor(p.s * size).toLong, size), clamp(Math.floor(p.t * size).toLong, size))
  }
  def taps(face: Int, x: Long, y: Long, size: Long, linear: Boolean): Vector[Texel] = {
    val outsideX = x < 0 || x >= size; val outsideY = y < 0 || y >= size
    val inside   = Texel(face, clamp(x, size), clamp(y, size))
    if (!linear || (!outsideX && !outsideY)) Vector(inside)
    else if (outsideX && outsideY) Vector(inside, edge(face, x, inside.y, size), edge(face, inside.x, y, size))
    else Vector(edge(face, x, y, size))
  }
}
