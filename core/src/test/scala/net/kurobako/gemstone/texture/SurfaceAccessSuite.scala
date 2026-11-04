package net.kurobako.gemstone.texture

import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.texture.TextureFixtures.*

class SurfaceAccessSuite extends Suite {
  private def surface = descriptor(U8x4, width = 4, height = 2, dimensions = 2, kind = ObjectKind.Surface)
  private def memory  = memoryOf((0 until 32)*)

  private def row(values: Seq[Int]): Vector[Int] = values.toVector

  test("loads address texels by byte offset, row, and slice") {
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 4, 1, 0), 4, BoundaryMode.Trap, memory).map(_.map(_ & 0xff).toVector),
      Right(row(20 to 23))
    )
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 0, 0, 0), 16, BoundaryMode.Trap, memory).map(_.length),
      Right(16)
    )
  }

  test("3D surfaces step by slice pitch") {
    val volume = descriptor(U8x1, width = 2, height = 2, depth = 2, dimensions = 3, kind = ObjectKind.Surface)
    assertEquals(
      SurfaceAccess.load(
        SurfacePosition(volume, 1, 1, 1),
        1,
        BoundaryMode.Trap,
        memoryOf((0 until 8)*)
      ).map(_.toVector),
      Right(Vector(7.toByte))
    )
  }

  test("1D surfaces have one row and one slice") {
    val strip = descriptor(U8x1, 8, kind = ObjectKind.Surface)
    assertEquals(
      SurfaceAccess.load(SurfacePosition(strip, 3, 0, 0), 1, BoundaryMode.Trap, memoryOf((0 until 8)*)).map(_.toVector),
      Right(Vector(3.toByte))
    )
    assertEquals(
      SurfaceAccess.load(SurfacePosition(strip, 3, 1, 0), 1, BoundaryMode.Trap, memoryOf((0 until 8)*)),
      Left(TexelFault.OutOfBounds(3, 1, 0))
    )
    assertEquals(
      SurfaceAccess.load(
        SurfacePosition(strip, 3, 5, 5),
        1,
        BoundaryMode.Clamp,
        memoryOf((0 until 8)*)
      ).map(_.toVector),
      Right(Vector(3.toByte))
    )
  }

  test("trap faults outside the surface") {
    Vector((16L, 0L), (14L, 0L), (-1L, 0L), (0L, 2L), (0L, -1L)).foreach { (x, y) =>
      assertEquals(
        SurfaceAccess.load(SurfacePosition(surface, x, y, 0), 4, BoundaryMode.Trap, memory),
        Left(TexelFault.OutOfBounds(x, y, 0)),
        s"($x, $y)"
      )
    }
    val volume = descriptor(U8x1, width = 2, height = 2, depth = 2, dimensions = 3, kind = ObjectKind.Surface)
    assertEquals(
      SurfaceAccess.load(SurfacePosition(volume, 0, 0, 2), 1, BoundaryMode.Trap, memoryOf((0 until 8)*)),
      Left(TexelFault.OutOfBounds(0, 0, 2))
    )
  }

  test("clamp moves to the nearest texel") {
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 100, 100, 0), 4, BoundaryMode.Clamp, memory).map(_.map(_ &
        0xff).toVector),
      Right(row(28 to 31))
    )
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, -8, -3, 5), 4, BoundaryMode.Clamp, memory).map(_.map(_ &
        0xff).toVector),
      Right(row(0 to 3))
    )
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 12, 0, 0), 4, BoundaryMode.Clamp, memory).map(_.map(_ &
        0xff).toVector),
      Right(row(12 to 15))
    )
  }

  test("zero reads zeros and drops stores") {
    val backing = memory
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 99, 0, 0), 4, BoundaryMode.Zero, backing).map(_.toVector),
      Right(Vector.fill(4)(0.toByte))
    )
    assertEquals(
      SurfaceAccess.store(SurfacePosition(surface, 99, 0, 0), Array.fill(4)(1.toByte), BoundaryMode.Zero, backing),
      Right(())
    )
    assertEquals(backing.writes.get, 0)
  }

  test("stores write in range and clamp or trap outside") {
    val backing = memory
    assertEquals(
      SurfaceAccess.store(
        SurfacePosition(surface, 4, 1, 0),
        Array(9, 9, 9, 9).map(_.toByte),
        BoundaryMode.Trap,
        backing
      ),
      Right(())
    )
    assertEquals(backing.bytes.slice(20, 24).toVector, Vector.fill(4)(9.toByte))
    assertEquals(
      SurfaceAccess.store(
        SurfacePosition(surface, 40, 0, 0),
        Array(7, 7, 7, 7).map(_.toByte),
        BoundaryMode.Clamp,
        backing
      ),
      Right(())
    )
    assertEquals(backing.bytes.slice(12, 16).toVector, Vector.fill(4)(7.toByte))
    assertEquals(
      SurfaceAccess.store(
        SurfacePosition(surface, 40, 0, 0),
        Array(7, 7, 7, 7).map(_.toByte),
        BoundaryMode.Trap,
        backing
      ),
      Left(TexelFault.OutOfBounds(40, 0, 0))
    )
  }

  test("unmapped surfaces fault with the address") {
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 0, 1, 0), 4, BoundaryMode.Trap, memoryOf(1, 2, 3)),
      Left(TexelFault.Unmapped(Base + 16))
    )
    assertEquals(
      SurfaceAccess.store(
        SurfacePosition(surface, 0, 1, 0),
        Array(1, 2, 3, 4).map(_.toByte),
        BoundaryMode.Trap,
        memoryOf(1, 2, 3)
      ),
      Left(TexelFault.Unmapped(Base + 16))
    )
  }

  test("wider accesses than a texel stay inside the row") {
    assertEquals(SurfaceAccess.load(SurfacePosition(surface, 0, 0, 0), 16, BoundaryMode.Trap, memory).isRight, true)
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 4, 0, 0), 16, BoundaryMode.Trap, memory),
      Left(TexelFault.OutOfBounds(4, 0, 0))
    )
    assertEquals(
      SurfaceAccess.load(SurfacePosition(surface, 4, 0, 0), 16, BoundaryMode.Clamp, memory).map(_.toVector.head),
      Right(0.toByte)
    )
  }
}
