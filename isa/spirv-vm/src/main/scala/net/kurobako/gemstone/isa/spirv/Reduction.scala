package net.kurobako.gemstone.isa.spirv

private object Reduction {
  val Row: Long    = 1L
  val Column: Long = 2L
  val Both: Long   = 3L
  val Block: Long  = 4L

  /** The reduction of the `rows` by `columns` matrix, whose lanes are in row order, into `results` lanes, or why `mask`
    * names none. A row reduction repeats the value of each row across its result row, a column reduction repeats the
    * value of each column down its result column, both reduce the matrix to one value, and a block reduction combines
    * the lanes of each 2 by 2 block, in column order, into one result lane.
    */
  def of(rows: Int, columns: Int, mask: Long, results: Int): Either[String, Reduction] = {
    val grouped: Option[(Vector[Vector[Int]], Int => Int)] = mask match {
      case Row if results % rows == 0 =>
        Some((Vector.tabulate(rows)(row => Vector.tabulate(columns)(row * columns + _)), _ / (results / rows)))
      case Column if results % columns == 0 =>
        Some((Vector.tabulate(columns)(column => Vector.tabulate(rows)(_ * columns + column)), _ % columns))
      case Both => Some((Vector(Vector.tabulate(rows * columns)(identity)), _ => 0))
      case Block if rows % 2 == 0 && columns % 2 == 0 && results == rows / 2 * (columns / 2) =>
        Some((
          Vector.tabulate(results) { block =>
            val (row, column) = (block / (columns / 2) * 2, block % (columns / 2) * 2)
            Vector((row, column), (row + 1, column), (row, column + 1), (row + 1, column + 1)).map((r, c) =>
              r * columns + c
            )
          },
          identity
        ))
      case _ => None
    }
    grouped.toRight(s"reduce $mask does not reduce a $rows by $columns matrix to $results lanes").map {
      (groups, group) =>
        val calls =
          groups.flatMap(lanes => lanes.tail.zipWithIndex.map((lane, at) => (if (at == 0) lanes.head else -1, lane)))
        val last   = groups.scanLeft(0)((count, lanes) => count + lanes.size - 1)
        val values = groups.indices.map(at => if (groups(at).size == 1) -groups(at).head - 1 else last(at + 1) - 1)
        Reduction(
          calls.map(_._1).toArray,
          calls.map(_._2).toArray,
          Array.tabulate(results)(lane => values(group(lane)))
        )
    }
  }
}

/** The calls of a SPV_NV_cooperative_matrix2 reduction, which folds the lanes of each group in order. Call `i` combines
  * the lane `first(i)`, or the value call `i - 1` returned where that is negative, with the lane `second(i)`. Result
  * lane `i` is the value call `results(i)` returned, or lane `-results(i) - 1` of the matrix where its group is a
  * single lane.
  */
private class Reduction(val first: Array[Int], val second: Array[Int], val results: Array[Int]) {
  def calls: Int = first.length
}
