package net.kurobako.gemstone.benchmarks

import net.kurobako.gemstone.constraint.*

/** One bounded, deterministic neighbouring-cell flux calculation over six input and two output arrays. */
object CloverleafFluxFixture {
  val rowStride: Int > 0 = 32
  val dt: Double         = 0.125

  private def value(array: Int, index: Int): Double =
    0.5 + array.toDouble * 0.125 + (index % 17).toDouble * 0.03125

  /** One allocation per array, inputs initialised. */
  def memory(cells: Int > 0): BenchmarkMemory = {
    val length = cells.raw + rowStride.raw + 1
    val memory = BenchmarkMemory(Vector.fill(8)(length * 8)*)
    for {
      array <- 0 until 6
      index <- 0 until length
    } memory.putLong(memory.ids(array), index * 8, java.lang.Double.doubleToRawLongBits(value(array, index)))
    memory
  }

  def verify(memory: BenchmarkMemory, cells: Int > 0): Unit = (0 until cells).foreach { index =>
    val x         = java.lang.Double.longBitsToDouble(memory.getLong(memory.ids(6), index * 8))
    val y         = java.lang.Double.longBitsToDouble(memory.getLong(memory.ids(7), index * 8))
    val expectedX = 0.25 * dt * value(0, index) *
      (value(2, index) + value(2, index + rowStride) + value(4, index) + value(4, index + rowStride))
    val expectedY = 0.25 * dt * value(1, index) *
      (value(3, index) + value(3, index + 1) + value(5, index) + value(5, index + 1))
    if (
      !java.lang.Double.isFinite(x) || !java.lang.Double.isFinite(y) ||
      math.abs(x - expectedX) > 1.0e-10 || math.abs(y - expectedY) > 1.0e-10
    )
      throw IllegalStateException(s"CloverLeaf flux mismatch at $index: $x/$y")
  }
}
