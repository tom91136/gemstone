package net.kurobako.gemstone.benchmarks

/** One bounded, deterministic neighbouring-cell flux calculation. */
object CloverleafFluxFixture {
  val rowStride = 32
  val dt        = 0.125
  val inputs    = 6
  val arrays    = 8

  def length(cells: Int): Int = Math.addExact(cells, rowStride + 1)

  def value(array: Int, index: Int): Double =
    0.5 + array.toDouble * 0.125 + (index % 17).toDouble * 0.03125

  def expectedX(index: Int): Double =
    0.25 * dt * value(0, index) *
      (value(2, index) + value(2, index + rowStride) + value(4, index) + value(4, index + rowStride))

  def expectedY(index: Int): Double =
    0.25 * dt * value(1, index) *
      (value(3, index) + value(3, index + 1) + value(5, index) + value(5, index + 1))

  def verify(index: Int, x: Double, y: Double): Long = {
    if (
      !java.lang.Double.isFinite(x) || !java.lang.Double.isFinite(y) ||
      math.abs(x - expectedX(index)) > 1.0e-10 || math.abs(y - expectedY(index)) > 1.0e-10
    )
      throw IllegalStateException(s"CloverLeaf flux mismatch at $index: $x/$y")
    java.lang.Double.doubleToRawLongBits(x) ^ java.lang.Double.doubleToRawLongBits(y)
  }

}
