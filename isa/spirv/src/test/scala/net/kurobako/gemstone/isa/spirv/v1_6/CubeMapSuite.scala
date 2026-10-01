package net.kurobako.gemstone.isa.spirv.v1_6

class CubeMapSuite extends munit.FunSuite {
  private val axes =
    Vector((1.0, 0.0, 0.0), (-1.0, 0.0, 0.0), (0.0, 1.0, 0.0), (0.0, -1.0, 0.0), (0.0, 0.0, 1.0), (0.0, 0.0, -1.0))
  for ((axis, face) <- axes.zipWithIndex) test(s"cube face $face center and radial derivative") {
    val (x, y, z) = axis
    assertEquals(CubeMap.project(x, y, z), CubeMap.Projection(face, .5, .5))
    assertEquals(CubeMap.project(x * 7, y * 7, z * 7), CubeMap.Projection(face, .5, .5))
    assertEquals(CubeMap.gradient(face, Array(x, y, z), Array(x, y, z)), (0.0, 0.0))
  }
  test("cube ties are deterministic and zero direction is finite") {
    assertEquals(CubeMap.project(1, 1, 1).face, 4)
    assertEquals(CubeMap.project(1, 1, -1).face, 5)
    assertEquals(CubeMap.project(1, -1, 0).face, 3)
    val p = CubeMap.project(0, 0, 0)
    assert(p.s.isFinite && p.t.isFinite)
  }
  for {
    size <- Seq(1L, 3L, 4L, 17L)
    i    <- 0L until size
  } test(s"cube edge rotations size=$size coordinate=$i") {
    val n        = size - 1
    val expected = Vector(
      Vector((4, n, i), (5, 0L, i), (2, n, n - i), (3, n, i)),
      Vector((5, n, i), (4, 0L, i), (2, 0L, i), (3, 0L, n - i)),
      Vector((1, i, 0L), (0, n - i, 0L), (5, n - i, 0L), (4, i, 0L)),
      Vector((1, n - i, n), (0, i, n), (4, i, n), (5, n - i, n)),
      Vector((1, n, i), (0, 0L, i), (2, i, n), (3, i, 0L)),
      Vector((0, n, i), (1, 0L, i), (2, n - i, 0L), (3, n - i, n))
    )
    for {
      face       <- 0 until 6
      (xy, side) <- Vector((-1L, i), (size, i), (i, -1L), (i, size)).zipWithIndex
    } {
      val e = expected(face)(side)
      assertEquals(CubeMap.taps(face, xy._1, xy._2, size, true), Vector(CubeMap.Texel(e._1, e._2, e._3)))
      assertEquals(
        CubeMap.taps(face, xy._1, xy._2, size, false),
        Vector(CubeMap.Texel(face, Math.max(0L, Math.min(n, xy._1)), Math.max(0L, Math.min(n, xy._2))))
      )
    }
  }
  for {
    face <- 0 until 6
    x    <- Seq(-1L, 4L)
    y    <- Seq(-1L, 4L)
  } test(s"cube corner averages three incident faces $face $x $y") {
    val taps = CubeMap.taps(face, x, y, 4, true)
    assertEquals(taps.size, 3)
    assertEquals(taps.map(_.face).distinct.size, 3)
    assert(taps.forall(t => (t.x == 0 || t.x == 3) && (t.y == 0 || t.y == 3)))
  }
  for ((axis, face) <- axes.zipWithIndex) test(s"cube projected gradient face=$face") {
    val c = Array(axis._1 * 4 + .2, axis._2 * 4 + .3, axis._3 * 4 + .4)
    val d = Array(.7, -.2, .3)
    val g = CubeMap.gradient(face, c, d)
    val e = 1e-5
    val a = CubeMap.project(c(0) - e * d(0), c(1) - e * d(1), c(2) - e * d(2))
    val b = CubeMap.project(c(0) + e * d(0), c(1) + e * d(1), c(2) + e * d(2))
    assertEqualsDouble(g._1, (b.s - a.s) / (2 * e), 1e-8)
    assertEqualsDouble(g._2, (b.t - a.t) / (2 * e), 1e-8)
  }
}
