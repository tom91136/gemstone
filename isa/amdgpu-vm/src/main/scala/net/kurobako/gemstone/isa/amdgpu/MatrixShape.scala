package net.kurobako.gemstone.isa.amdgpu

private[isa] object MatrixShape {
  private val Named =
    raw"v_(s?)(?:mfma|wmma|mfmac|wmmac)(?:_scale)?_([a-z0-9]+)_(\d+)x(\d+)x(\d+)(?:_(\d+)b)?_?([a-z0-9_]+)".r

  /** The shape of the matrix instruction `mnemonic` on `isa` in a wave of `waveSize` lanes, the formats of an f8f6f4
    * instruction's A and B and the signedness of integer WMMA elements given by their selectors.
    */
  def of(
      mnemonic: String,
      isa: ISA,
      waveSize: Int,
      formatA: Int = 0,
      formatB: Int = 0,
      signedA: Boolean = true,
      signedB: Boolean = true
  ): Either[String, MatrixShape] = {
    val layout = isa match {
      case ISA.CDNA1 | ISA.CDNA2 | ISA.CDNA3 | ISA.CDNA4 => Some(MatrixLayout.Core)
      case ISA.RDNA3 | ISA.RDNA3_5 | ISA.GFX11_7         => Some(MatrixLayout.Wave11)
      case ISA.RDNA4                                     => Some(MatrixLayout.Wave12)
      case _                                             => None
    }
    val ocp                                     = isa == ISA.CDNA4 || isa == ISA.RDNA4
    def scaled(code: Int): Option[MatrixFormat] = code match {
      case 0 => Some(MatrixFormat.FP8)
      case 1 => Some(MatrixFormat.BF8)
      case 2 => Some(MatrixFormat.FP6)
      case 3 => Some(MatrixFormat.BF6)
      case 4 => Some(MatrixFormat.FP4)
      case _ => None
    }
    def single(name: String, signed: Boolean): Option[MatrixFormat] = name match {
      case "f64"              => Some(MatrixFormat.F64)
      case "f32"              => Some(MatrixFormat.F32)
      case "xf32"             => Some(MatrixFormat.XF32)
      case "f16"              => Some(MatrixFormat.F16)
      case "bf16" | "bf16_1k" => Some(MatrixFormat.BF16)
      case "i32"              => Some(MatrixFormat.I32)
      case "i8"               => Some(MatrixFormat.I8)
      case "iu8"              => Some(if (signed) MatrixFormat.I8 else MatrixFormat.U8)
      case "iu4"              => Some(if (signed) MatrixFormat.I4 else MatrixFormat.U4)
      case "fp8" if ocp       => Some(MatrixFormat.FP8)
      case "bf8" if ocp       => Some(MatrixFormat.BF8)
      case "fp8"              => Some(MatrixFormat.FP8FNUZ)
      case "bf8"              => Some(MatrixFormat.BF8FNUZ)
      case _                  => None
    }
    // gfx908 names fold the block count into the operand: a register of f32, two of f16, one of i8 or of pre-1k bf16
    def legacyItems(name: String): Option[Int] = name match {
      case "f32"     => Some(1)
      case "bf16"    => Some(2)
      case "f16"     => Some(4)
      case "i8"      => Some(4)
      case "bf16_1k" => Some(4)
      case "f64"     => Some(1)
      case _         => None
    }
    (mnemonic, layout) match {
      case (Named(prefix, output, m, n, k, explicit, input), Some(selected)) =>
        val (rows, columns, depth) = (m.toInt, n.toInt, k.toInt)
        val sparse                 = prefix == "s"
        val operands               = input.split('_').toVector
        val legacy                 = Option(explicit).isEmpty && !mnemonic.contains(s"x${k}_")
        val formats                = if (input == "f8f6f4") (scaled(formatA), scaled(formatB))
        else if (operands.size == 2 && input != "bf16_1k")
          (single(operands(0), signedA), single(operands(1), signedB))
        else (single(input, signedA), single(input, signedB))
        val blocks = Option(explicit).map(_.toInt).orElse(
          if (legacy && selected == MatrixLayout.Core) legacyItems(input).map(items => 64 * items / (rows * depth))
          else Some(1)
        )
        val accumulator = output match {
          case "f32"  => Some(MatrixFormat.F32)
          case "f64"  => Some(MatrixFormat.F64)
          case "i32"  => Some(MatrixFormat.I32)
          case "f16"  => Some(MatrixFormat.F16)
          case "bf16" => Some(MatrixFormat.BF16)
          case _      => None
        }
        (formats, blocks, accumulator) match {
          case ((Some(a), Some(b)), Some(count), Some(c)) if count >= 1 =>
            val shape = MatrixShape(
              selected,
              rows,
              columns,
              depth,
              count,
              a,
              b,
              c,
              if (selected == MatrixLayout.Core) 64 else waveSize,
              sparse
            )
            Right(if (isa == ISA.CDNA4) shape.split else shape)
          case _ => Left(s"$mnemonic has no matrix shape on $isa")
        }
      case _ => Left(s"$mnemonic has no matrix shape on $isa")
    }
  }

  /** The lane B is read from under BLGP `control`: a half or quarter of the wave broadcast, or a rotation by 16. */
  def laneGroup(lane: Int, control: Int): Int = control match {
    case 0       => lane
    case 1       => lane        % 32
    case 2       => lane        % 32 + 32
    case 3       => (lane + 16) % 64
    case quarter => lane        % 16 + (quarter - 4) * 16
  }

  extension (shape: MatrixShape) {

    /** gfx950 splits an operand of eight registers of 8- or 16-bit elements into two runs of K per lane, and lays a
      * sparse A out to match its B.
      */
    private def split: MatrixShape = {
      def run(items: Int, format: MatrixFormat): Int =
        if (items * format.width == 256 && (format.width == 8 || format.width == 16)) 128 / format.width else 0
      val chunkB = run(shape.bItems, shape.b)
      val chunkA = if (shape.sparse) chunkB / 2 else run(shape.aItems, shape.a)
      shape.copy(chunkA = chunkA, chunkB = chunkB)
    }
  }
}

/** The shape of a matrix instruction: `blocks` products of an M x K matrix A and a K x N matrix B added to M x N
  * matrices C into D, with the formats of A, B and C/D. A 4:2 sparse A holds K/2 elements per row, the two of each
  * group of four that an index selects. `chunkA` and `chunkB`, when non-zero, split the K elements a lane holds into
  * runs of that many, as gfx950 lays out its widest operands.
  */
private[isa] case class MatrixShape(
    layout: MatrixLayout,
    m: Int,
    n: Int,
    k: Int,
    blocks: Int,
    a: MatrixFormat,
    b: MatrixFormat,
    c: MatrixFormat,
    waveSize: Int = 64,
    sparse: Boolean = false,
    chunkA: Int = 0,
    chunkB: Int = 0
) {

  /** The K elements of A that registers hold. */
  def storedK: Int = if (sparse) k / 2 else k

  private def items(rows: Int, depth: Int, format: MatrixFormat): Int = layout match {
    case MatrixLayout.Core   => depth * rows * blocks / 64
    case MatrixLayout.Wave11 => depth
    case MatrixLayout.Wave12 =>
      val narrow = depth * 16 / 32
      if (waveSize == 64 && narrow * format.width > 32) narrow / 2 else narrow
  }

  /** The elements of A, of B and of C/D one lane holds. */
  val aItems: Int      = items(m, storedK, a)
  val bItems: Int      = items(n, k, b)
  val outputItems: Int = m * n * blocks / waveSize

  private def registers(items: Int, width: Int): Int = (items * width + 31) / 32

  def aRegisters: Int = registers(aItems, a.width)
  def bRegisters: Int = registers(bItems, b.width)

  /** Gfx11 keeps a 16-bit element of C/D in one half of each register. */
  def halfRegisters: Boolean = layout == MatrixLayout.Wave11 && c.width == 16

  def outputRegisters: Int = if (halfRegisters) outputItems else registers(outputItems, c.width)

  /** The bit of C/D's registers at which item `item` of a lane starts, in the low half on gfx11. */
  def outputBit(item: Int): Int = if (halfRegisters) item * 32 else item * c.width

  /** The block, row or column, and k of the element item `item` of `lane` holds in an operand of `rows` rows, depth
    * `depth` and `chunk`, as `block * rows * depth + row * depth + k`, or -1 where the lane holds none.
    */
  private def input(lane: Int, item: Int, rows: Int, depth: Int, format: MatrixFormat, chunk: Int): Int =
    layout match {
      case MatrixLayout.Core =>
        val count  = depth * rows * blocks / 64
        val run    = if (chunk == 0) count else chunk
        val groups = 64 / (rows * blocks)
        val block  = lane / rows                                                         % blocks
        val k      = run * groups * (item / run) + run * (lane / (rows * blocks)) + item % run
        (block * rows + lane % rows) * depth + k
      case MatrixLayout.Wave11 => lane % 16 * depth + item
      case MatrixLayout.Wave12 =>
        val narrow                            = depth * 16 / 32
        val run                               = if (format.width == 16) narrow / 2 else narrow
        def wave32(lane: Int, item: Int): Int =
          lane % 16 * depth +
            run * 2 *
            (item / run) +
            run *
            (lane / 16) + item % run
        if (waveSize == 32) wave32(lane, item)
        else if (narrow * format.width > 32) wave32(lane % 32, item + narrow / 2 * (lane / 32))
        else if (lane < 32) wave32(lane, item)
        else -1
    }

  /** The block, row and column of the element item `item` of `lane` holds in C and D, as `block * m * n + row * n +
    * column`.
    */
  private def output(lane: Int, item: Int): Int = layout match {
    case MatrixLayout.Core =>
      val height  = if (c == MatrixFormat.F64) 1 else 4
      val perItem = math.max(1, 64 / (n * m / height))
      val rows    = 64 / perItem / n
      val groups  = m / (height * rows)
      val group   = lane / n
      val block   = group % perItem + perItem * (item / (height * groups))
      val row     = item  % height + height * (group / perItem) + height * rows * (item / height % groups)
      (block * m + row) * n + lane % n
    case MatrixLayout.Wave11 => (lane / 16 + waveSize / 16 * item) * n + lane % 16
    case MatrixLayout.Wave12 =>
      val row = if (waveSize == 32) 8 * (lane / 16) + item else 8 * (lane / 16 % 2) + item + 4 * (lane / 32)
      row * n + lane % 16
  }

  private def located(rows: Int, depth: Int, items: Int, format: MatrixFormat, chunk: Int): (Array[Int], Array[Int]) = {
    val lanes = Array.fill(blocks * rows * depth)(-1)
    val held  = Array.fill(blocks * rows * depth)(-1)
    for {
      lane <- 0 until waveSize
      item <- 0 until items
      at = input(lane, item, rows, depth, format, chunk)
      if at >= 0 && lanes(at) < 0
    } {
      lanes(at) = lane
      held(at) = item
    }
    (lanes, held)
  }

  private lazy val aLocated = located(m, storedK, aItems, a, chunkA)
  private lazy val bLocated = located(n, k, bItems, b, chunkB)
  private lazy val outputs  =
    Array.tabulate(waveSize * outputItems)(index => output(index / outputItems, index % outputItems))

  /** Whether each group of 16 lanes holds all of A and B, as gfx11 requires; a lane reads them from its own group. */
  def replicated: Boolean = layout == MatrixLayout.Wave11

  /** The lane and item that hold each element of A, at index `(block * m + row) * storedK + k`, and of B, at index
    * `(block * n + column) * k + depth` for its depth.
    */
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def aLanes: Array[Int] = aLocated._1
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def aHeld: Array[Int] = aLocated._2
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def bLanes: Array[Int] = bLocated._1
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def bHeld: Array[Int] = bLocated._2

  /** The element of C and D that item `item` of `lane` holds, at index `lane * outputItems + item`, as `block * m * n +
    * row * n + column`.
    */
  @SuppressWarnings(Array("scalafix:MutableExposure"))
  def outputElements: Array[Int] = outputs

  /** The lane that holds element `k` of row `row` of block `block` of A, before broadcasts and replication. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def aLane(block: Int, row: Int, k: Int): Int = aLocated._1((block * m + row) * storedK + k)

  /** The item of its lane that holds element `k` of row `row` of block `block` of A. */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def aItem(block: Int, row: Int, k: Int): Int = aLocated._2((block * m + row) * storedK + k)

  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def bLane(block: Int, k: Int, column: Int): Int = bLocated._1((block * n + column) * this.k + k)
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def bItem(block: Int, k: Int, column: Int): Int = bLocated._2((block * n + column) * this.k + k)

  /** The lane `lane` reads element `k` of row `row` of block `block` of A from: in its own group of 16 lanes on gfx11,
    * and under a broadcast from block `blockId` of each group of `1 << blockBroadcast` blocks, as AMD's matrix
    * instruction calculator and the CDNA1 ISA define CBSZ and ABID.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def aSource(block: Int, row: Int, k: Int, lane: Int, blockBroadcast: Int, blockId: Int): Int =
    aLane(block = block >> blockBroadcast << blockBroadcast | blockId, row = row, k = k) +
      (if (replicated) lane / 16 * 16 else 0)

  /** The lane `lane` reads element `k` of column `column` of block `block` of B from, under the lane-group permutation
    * `laneGroups`.
    */
  // call sites name these arguments, which the argument checks enforce
  @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
  def bSource(block: Int, k: Int, column: Int, lane: Int, laneGroups: Int): Int =
    MatrixShape.laneGroup(
      bLane(block = block, k = k, column = column) + (if (replicated) lane / 16 * 16 else 0),
      laneGroups
    )

  /** The block, row and column of D that item `item` of `lane` holds. */
  def outputBlock(lane: Int, item: Int): Int  = outputs(lane * outputItems + item) / (m * n)
  def outputRow(lane: Int, item: Int): Int    = outputs(lane * outputItems + item) / n % m
  def outputColumn(lane: Int, item: Int): Int = outputs(lane * outputItems + item)     % n
}
