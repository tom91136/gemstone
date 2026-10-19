package net.kurobako.gemstone.isa.nvidia

private[isa] object MatrixOperands {

  /** The 32 elements of `values` from `start` in steps of `stride`, one bit each, as a word. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def segment(values: Array[Long], start: Int, stride: Int): Long = {
    var word = 0L
    var bit  = 0
    while (bit < 32) {
      word |= values(start + bit * stride) << bit
      bit += 1
    }
    word
  }

  /** The 2-bit selection of packed element `slot` of a sparse chunk whose metadata nibble is `nibble`. */
  def sparseSelection(element: VMIR.Matrix.Element, nibble: Long, slot: Int): Long = element.bits.raw match {
    case 32 => nibble & 3L
    case 4  => (nibble >>> (2 * (slot / 2))) & 3L
    case _  => (nibble >>> (2 * slot)) & 3L
  }
}

/** The operands of one element of a matrix product as a sink presents them, each as its element type. */
private[isa] trait MatrixOperands[V] {

  /** Element `index` of the row of A, packed when A is sparse; a 32-bit segment of it for single-bit elements. */
  def a(index: Int): V

  /** Element `index` of the column of B, or a 32-bit segment of it. */
  def b(index: Int): V
  def c: V

  /** The 2-bit metadata index that places packed element `index` of a sparse A within its chunk. */
  def selection(index: Int): V

  /** The scale factor of block `block` of the row of A or the column of B. */
  def scaleA(block: Int): V
  def scaleB(block: Int): V

  /** `value` named `name`, so later steps read it rather than recompute it. */
  def bind(name: String, value: V): V

  /** `value` named `name` and held in `slot` for [[held]] to read back. */
  def hold(slot: Int, name: String, value: V): V
  def held(slot: Int): V
}
