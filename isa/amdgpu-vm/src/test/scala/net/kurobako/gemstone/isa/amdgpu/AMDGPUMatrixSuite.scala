package net.kurobako.gemstone.isa.amdgpu

import VMIR.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.annotation.tailrec
import scala.util.Random

class AMDGPUMatrixSuite extends Suite {

  // Register-to-element formulas AMD's matrix instruction calculator (1.3.2) prints with --detail-instruction, for one
  // instruction of each layout family: (lane, item) to (block, row or column, k) of A or B, and to (block, row, column)
  // of D.
  @FunctionalInterface
  private type Layout = (Int, Int) => (Int, Int, Int)

  private class Formula(
      val target: String,
      val mnemonic: String,
      val waveSize: Int,
      val a: Layout,
      val b: Layout,
      val d: Layout
  )

  private val formulas = Vec(
    // A block: floor(lane / 32), i: lane % 32; D i: 8 * floor(GPR / 4) % 32 + 4 * floor(lane / 32) + GPR % 4
    Formula(
      "gfx908",
      "v_mfma_f32_32x32x1f32",
      64,
      (lane, _) => (lane / 32, lane % 32, 0),
      (lane, _) => (lane / 32, lane % 32, 0),
      (lane, item) => (item / 16, 8 * (item % 16 / 4) % 32 + 4 * (lane / 32) + item % 4, lane % 32)
    ),
    // A k: 4 * floor(lane / 16) + GPR_bits / 16 item; D i: 4 * floor(lane / 16) + GPR % 4
    Formula(
      "gfx942",
      "v_mfma_f32_16x16x16_f16",
      64,
      (lane, item) => (0, lane % 16, 4 * (lane / 16) + item),
      (lane, item) => (0, lane % 16, 4 * (lane / 16) + item),
      (lane, item) => (0, 4 * (lane / 16) + item % 4, lane % 16)
    ),
    // f64 D: the calculator places a block's rows four lanes apart
    Formula(
      "gfx90a",
      "v_mfma_f64_4x4x4f64",
      64,
      (lane, _) => (lane / 4 % 4, lane % 4, lane / 16),
      (lane, _) => (lane / 4 % 4, lane % 4, lane / 16),
      (lane, _) => (lane / 4 % 4, lane / 16, lane % 4)
    ),
    // A k: 8 * floor(lane / 16) + 4 * GPR + floor(GPR_bits / 8)
    Formula(
      "gfx942",
      "v_mfma_f32_16x16x32_fp8_fp8",
      64,
      (lane, item) => (0, lane % 16, 8 * (lane / 16) + item),
      (lane, item) => (0, lane % 16, 8 * (lane / 16) + item),
      (lane, item) => (0, 4 * (lane / 16) + item % 4, lane % 16)
    ),
    // gfx11: every 16 lanes hold all of A; D i: 2 * GPR + floor(lane / 16)
    Formula(
      "gfx1100",
      "v_wmma_f32_16x16x16_f16",
      32,
      (lane, item) => (0, lane % 16, item),
      (lane, item) => (0, lane % 16, item),
      (lane, item) => (0, 2 * item + lane / 16, lane % 16)
    ),
    // gfx12: A k: 8 * floor(GPR / 2) + 4 * floor(lane / 16) + 2 * (GPR % 2) + floor(GPR_bits / 16)
    Formula(
      "gfx1201",
      "v_wmma_f32_16x16x16_f16",
      32,
      (lane, item) => (0, lane % 16, 8 * (item / 4) + 4 * (lane / 16) + item % 4),
      (lane, item) => (0, lane % 16, 8 * (item / 4) + 4 * (lane / 16) + item % 4),
      (lane, item) => (0, 8 * (lane / 16) + item, lane % 16)
    ),
    // gfx12 wave64 8-bit: A k: 4 * floor(lane / 32) + 8 * (floor(lane / 16) % 2) + GPR_bits / 8
    Formula(
      "gfx1201",
      "v_wmma_i32_16x16x16_iu8",
      64,
      (lane, item) => (0, lane % 16, 4 * (lane / 32) + 8 * (lane / 16 % 2) + item),
      (lane, item) => (0, lane % 16, 4 * (lane / 32) + 8 * (lane / 16 % 2) + item),
      (lane, item) => (0, 8 * (lane / 16 % 2) + 4 * (lane / 32) + item, lane % 16)
    )
  )

  private def shape(target: String, mnemonic: String, waveSize: Int): MatrixShape =
    MatrixShape.of(mnemonic, target.unsafe[Target].isa, waveSize).fold(fail(_), identity)

  for (formula <- formulas)
    test(s"${formula.target} ${formula.mnemonic} wave${formula.waveSize} lays elements out as AMD's calculator does") {
      val matrix = shape(formula.target, formula.mnemonic, formula.waveSize)
      for {
        lane <- 0 until formula.waveSize
        item <- 0 until matrix.aItems
      } {
        val (block, row, k) = formula.a(lane, item)
        assertEquals(matrix.aLane(block = block, row = row, k = k) % 16, lane % 16, clues(lane, item))
        assertEquals(matrix.aItem(block = block, row = row, k = k), item, clues(lane, item))
        val (columnBlock, column, depth) = formula.b(lane, item)
        assertEquals(matrix.bItem(block = columnBlock, k = depth, column = column), item, clues(lane, item))
      }
      for {
        lane <- 0 until formula.waveSize
        item <- 0 until matrix.outputItems
      } assertEquals(
        (matrix.outputBlock(lane, item), matrix.outputRow(lane, item), matrix.outputColumn(lane, item)),
        formula.d(lane, item),
        clues(lane, item)
      )
    }

  test("gfx950 splits eight registers of fp8 into two runs of K, as the CDNA4 ISA's F8F6F4 tables lay them out") {
    // CDNA4 ISA 7.1.5.1: lanes 0-15 hold k 0-15 in v0-v3 and k 64-79 in v4-v7; FP4 holds k 0-31 in v0-v3
    val fp8 = MatrixShape.of("v_mfma_f32_16x16x128_f8f6f4", ISA.CDNA4, 64, formatB = 4).value
    assertEquals((fp8.aLane(block = 0, row = 3, k = 70), fp8.aItem(block = 0, row = 3, k = 70)), (3, 22))
    assertEquals((fp8.aLane(block = 0, row = 3, k = 21), fp8.aItem(block = 0, row = 3, k = 21)), (19, 5))
    assertEquals((fp8.bLane(block = 0, k = 33, column = 7), fp8.bItem(block = 0, k = 33, column = 7)), (23, 1))
    assertEquals((fp8.aRegisters, fp8.bRegisters), (8, 4))
    val fp6 = MatrixShape.of("v_mfma_f32_32x32x64_f8f6f4", ISA.CDNA4, 64, formatA = 2, formatB = 1).value
    assertEquals(
      (fp6.aLane(block = 0, row = 5, k = 40), fp6.aItem(block = 0, row = 5, k = 40), fp6.aRegisters),
      (37, 8, 6)
    )
    assertEquals((fp6.bLane(block = 0, k = 40, column = 5), fp6.bItem(block = 0, k = 40, column = 5)), (5, 24))
  }

  /** An element of `format` drawn from small dyadic values every format holds exactly. */
  private def draw(format: MatrixFormat, random: Random, integral: Boolean): Long = format match {
    case MatrixFormat.I8 | MatrixFormat.I4 => (random.nextInt(15) - 7).toLong & ((1L << format.width) - 1L)
    case MatrixFormat.U8 | MatrixFormat.U4 => random.nextInt(16).toLong
    case other                             =>
      val value = (random.nextInt(9) - 4) * 0.5
      encode(other, if (integral) math.rint(value) else value)
  }

  private def encode(format: MatrixFormat, value: Double): Long = format match {
    case MatrixFormat.F64                     => java.lang.Double.doubleToRawLongBits(value)
    case MatrixFormat.F32 | MatrixFormat.XF32 => java.lang.Float.floatToRawIntBits(value.toFloat) & 0xffffffffL
    case MatrixFormat.F16                     => IEEE754.floatToBinary16(value.toFloat).toLong
    case MatrixFormat.I32                     => value.toLong & 0xffffffffL
    case other                                => minifloat(other).fromDouble(value)
  }

  private def decode(format: MatrixFormat, bits: Long): Double = format match {
    case MatrixFormat.F64                     => java.lang.Double.longBitsToDouble(bits)
    case MatrixFormat.F32 | MatrixFormat.XF32 => java.lang.Float.intBitsToFloat(bits.toInt).toDouble
    case MatrixFormat.F16                     => IEEE754.binary16ToFloat(Binary16.low(bits)).toDouble
    case MatrixFormat.I32                     => bits.toInt.toDouble
    case MatrixFormat.I8                      => bits.toByte.toDouble
    case MatrixFormat.U8 | MatrixFormat.U4    => bits.toDouble
    case MatrixFormat.I4                      => (((bits & 15L) ^ 8L) - 8L).toDouble
    case other                                => minifloat(other).toDouble(bits)
  }

  private def minifloat(format: MatrixFormat): Minifloat = format match {
    case MatrixFormat.BF16    => Minifloat.BFloat16
    case MatrixFormat.FP8     => Minifloat.E4M3
    case MatrixFormat.BF8     => Minifloat.E5M2
    case MatrixFormat.FP8FNUZ => Minifloat.E4M3FNUZ
    case MatrixFormat.BF8FNUZ => Minifloat.E5M2FNUZ
    case MatrixFormat.FP6     => Minifloat.E2M3
    case MatrixFormat.BF6     => Minifloat.E3M2
    case _                    => Minifloat.E2M1
  }

  private def bits(registers: Array[Array[Long]], lane: Int, bit: Int, width: Int): Long = (0 until width).foldLeft(0L)(
    (value, offset) =>
      value | (((registers((bit + offset) / 32)(lane) >>> ((bit + offset) % 32)) & 1L) << offset)
  )

  private def place(registers: Array[Array[Long]], lane: Int, bit: Int, width: Int, value: Long): Unit =
    (0 until width).foreach { offset =>
      val at = bit + offset
      registers(at / 32)(lane) =
        (registers(at / 32)(lane) & ~(1L << (at % 32))) | (((value >>> offset) & 1L) << (at % 32))
    }

  /** Every lane's registers of the vector and accumulator files, after the instruction has run. */
  private class Ended(end: Long, lanes: Int) extends LaunchObserver {
    val vectors: Array[Array[Long]] = Array.ofDim[Long](256, lanes)

    def instruction(at: Fault.Location, state: ArchitecturalState): Either[Fault, Unit] = {
      (at.instruction, state) match {
        case (Some(offset), view: WaveView) if offset.raw == end =>
          for {
            index <- 0 until 256
            lane  <- 0 until lanes
          } vectors(index)(lane) = view.vector(index.unsafe[Int in 0 ~ 255], lane.unsafe[Int in 0 ~ 63])
        case _ => ()
      }
      Right(())
    }
  }

  /** `text` run by a complete wave with each VGPR and AGPR holding `registers` gives, then every VGPR, with the AGPRs
    * the accumulator bank of D names copied from v128.
    */
  private def run(target: String, text: String, waveSize: Int, registers: Map[(Char, Int), Array[Long]])
      : Array[Array[Long]] = {
    val selected = target.unsafe[Target]
    val code     = (for {
      program <- asm.Parser.parse(text).left.map(_.diagnostic.raw)
      bytes   <-
        asm.Assembler.encode(program, TargetMode(selected, waveSize.unsafe[Int in (32, 64)])).left.map(_.toString)
    } yield bytes).fold(fail(_), identity)
    val instruction = VMCompiler.decodeProgram(code, TargetMode(selected, waveSize.unsafe[Int in (32, 64)]))
      .fold(error => fail(error.raw), _.operations.head)
    val seeds = registers.toVec.flatMap { case ((file, index), values) =>
      val target = if (file == 'a') 255 else index
      Vec.tabulate(waveSize)(lane =>
        Instruction.VWriteLane(
          target.unsafe[VectorRegisterIndex],
          InstructionSource.Constant(values(lane)),
          InstructionSource.Constant(lane.toLong)
        )
      ) ++
        (if (file == 'a')
           Vec(Instruction.VAccumulatorWrite(
             index.unsafe[VectorRegisterIndex],
             InstructionSource.VectorRegister(255.unsafe[VectorRegisterIndex])
           ))
         else Vec.empty[Instruction])
    }
    val copies = Vec.tabulate(64)(index =>
      Instruction.VAccumulatorRead((128 + index).unsafe[VectorRegisterIndex], index.unsafe[VectorRegisterIndex])
    )
    val operations = seeds ++ Vec(instruction) ++ copies ++ Vec(Instruction.End)
    val ended      = Ended((operations.size - 1) * 4L, waveSize)
    val source     = codeKernel(
      target = selected,
      kernelCodeProperties = if (waveSize == 32) EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0
    )
    val task =
      KernelTask.validated("kernel", Dimensions(waveSize.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(
      executeKernel(irKernel(operations, source), task, RegionMemory(), TaskControl(observer = Some(ended))),
      Right(())
    )
    ended.vectors
  }

  /** A matrix instruction on random exactly representable elements against the exact product the layouts imply. */
  private def checkProduct(
      target: String,
      mnemonic: String,
      modifiers: String = "",
      waveSize: Int = 64,
      formatA: Int = 0,
      formatB: Int = 0,
      signedA: Boolean = true,
      signedB: Boolean = true,
      accumulators: Boolean = false,
      negate: Int = 0,
      high: Boolean = false,
      clamp: Boolean = false,
      scales: Option[(Int, Int)] = None,
      indexSet: Int = 0,
      broadcast: (Int, Int, Int) = (0, 0, 0)
  )(using munit.Location): Unit = {
    val matrix = MatrixShape.of(mnemonic, target.unsafe[Target].isa, waveSize, formatA, formatB, signedA, signedB).value
    val random = Random(mnemonic.hashCode ^ modifiers.hashCode)
    val integral = matrix.c == MatrixFormat.BF16
    val a        = Array.ofDim[Long](matrix.aRegisters, waveSize)
    val b        = Array.ofDim[Long](matrix.bRegisters, waveSize)
    val c        = Array.ofDim[Long](matrix.outputRegisters, waveSize)
    val index    = Array.ofDim[Long](1, waveSize)
    val scale    = Array.ofDim[Long](2, waveSize)
    for (lane <- 0 until waveSize) {
      (0 until matrix.aItems).foreach(item =>
        place(a, lane, item * matrix.a.width, matrix.a.width, draw(matrix.a, random, integral))
      )
      (0 until matrix.bItems).foreach(item =>
        place(b, lane, item * matrix.b.width, matrix.b.width, draw(matrix.b, random, integral))
      )
      (0 until matrix.outputItems).foreach { item =>
        val value =
          if (clamp) 2147483600.0 + random.nextInt(32) else (random.nextInt(17) - 8).toDouble
        place(c, lane, matrix.outputBit(item) + (if (high) 16 else 0), matrix.c.width, encode(matrix.c, value))
      }
      index(0)(lane) = (0 until 8).foldLeft(0L) { (word, pair) =>
        val first  = random.nextInt(3)
        val second = first + 1 + random.nextInt(3 - first)
        word | ((first | (second << 2)).toLong << (4 * pair))
      }
      scale(0)(lane) = (0 until 4).foldLeft(0L)((word, byte) => word | ((125 + random.nextInt(5)).toLong << (8 * byte)))
      scale(1)(lane) = (0 until 4).foldLeft(0L)((word, byte) => word | ((125 + random.nextInt(5)).toLong << (8 * byte)))
    }
    def range(file: Char, base: Int, count: Int): String =
      if (count == 1) s"$file$base" else s"$file[$base:${base + count - 1}]"
    val aBase    = 0
    val bBase    = 16
    val dBase    = if (accumulators) 0 else 32
    val dFile    = if (accumulators) 'a' else 'v'
    val operands = Vec(
      range(dFile, dBase, matrix.outputRegisters),
      range('v', aBase, matrix.aRegisters),
      range('v', bBase, matrix.bRegisters),
      if (matrix.sparse) "v100" else range(dFile, dBase, matrix.outputRegisters)
    ) ++ (if (scales.nonEmpty) Vec("v101", "v102") else Vec.empty[String])
    val text  = s"$mnemonic ${operands.mkString(", ")} $modifiers".trim
    val seeds = (0 until matrix.aRegisters).map(register => ('v', aBase + register) -> a(register)) ++
      (0 until matrix.bRegisters).map(register => ('v', bBase + register) -> b(register)) ++
      (0 until matrix.outputRegisters).map(register => (dFile, dBase + register) -> c(register)) ++
      Seq(('v', 100) -> index(0), ('v', 101) -> scale(0), ('v', 102) -> scale(1))
    val vectors = run(target, text, waveSize, seeds.toMap)
    val result = Array.tabulate(matrix.outputRegisters)(register => vectors((if (accumulators) 128 else 32) + register))
    val (cbsz, abid, blgp) = broadcast
    for {
      lane <- 0 until waveSize
      item <- 0 until matrix.outputItems
    } {
      val (block, row, column) =
        (matrix.outputBlock(lane, item), matrix.outputRow(lane, item), matrix.outputColumn(lane, item))
      val bit                                  = matrix.outputBit(item) + (if (high) 16 else 0)
      val addend                               = decode(matrix.c, bits(c, lane, bit, matrix.c.width))
      def product(sum: Double, k: Int): Double = {
        val aLane = matrix.aSource(block = block, row = row, k = k, lane = lane, blockBroadcast = cbsz, blockId = abid)
        val aItem = matrix.aItem(block = block, row = row, k = k)
        val left  = decode(matrix.a, bits(a, aLane, aItem * matrix.a.width, matrix.a.width))
        val dense =
          if (matrix.sparse)
            4 * (k / 2) + ((index(0)(aLane) >>> (2 * matrix.aItems * indexSet + 2 * aItem)) & 3L).toInt
          else k
        val bLane = matrix.bSource(block = block, k = dense, column = column, lane = lane, laneGroups = blgp)
        val right =
          decode(
            matrix.b,
            bits(b, bLane, matrix.bItem(block = block, k = dense, column = column) * matrix.b.width, matrix.b.width)
          )
        val (scaleA, scaleB) = scales.fold((0, 0)) { (first, second) =>
          (
            ((scale(0)(row + matrix.m * (dense / 32)) >>> (8 * first)) & 0xffL).toInt - 127,
            ((scale(1)(column + matrix.n * (dense / 32)) >>> (8 * second)) & 0xffL).toInt - 127
          )
        }
        sum + math.scalb(if ((negate & 1) != 0) -left else left, scaleA) *
          math.scalb(if ((negate & 2) != 0) -right else right, scaleB)
      }
      @tailrec def accumulate(sum: Double, k: Int): Double =
        if (k == matrix.storedK) sum else accumulate(product(sum, k), k + 1)
      val exact    = accumulate(if ((negate & 4) != 0) -addend else addend, 0)
      val expected =
        if (matrix.c == MatrixFormat.I32)
          if (clamp) math.max(Int.MinValue.toDouble, math.min(Int.MaxValue.toDouble, exact))
          else exact.toLong.toInt.toDouble
        else exact
      assertEquals(decode(matrix.c, bits(result, lane, bit, matrix.c.width)), expected, clues(text, lane, item))
    }
  }

  // hipcc (ROCm 7.1.1, -O2) emits these from conformance/unified/src/hip/matrix.hip, and rocBLAS's Tensile kernels the
  // unmodified forms.
  test("gfx908 MFMA, f32 broadcasts and lane permutations included") {
    checkProduct("gfx908", "v_mfma_f32_32x32x1f32", "cbsz:1 abid:1 blgp:3", accumulators = true, broadcast = (1, 1, 3))
    checkProduct("gfx908", "v_mfma_f32_16x16x1f32", "cbsz:2 abid:3", accumulators = true, broadcast = (2, 3, 0))
    checkProduct("gfx908", "v_mfma_f32_4x4x1f32", "cbsz:4 abid:9 blgp:5", accumulators = true, broadcast = (4, 9, 5))
    checkProduct("gfx908", "v_mfma_f32_4x4x4f16", "cbsz:3 abid:5 blgp:7", accumulators = true, broadcast = (3, 5, 7))
    for (
      mnemonic <- Vec(
        "v_mfma_f32_32x32x2f32",
        "v_mfma_f32_16x16x4f32",
        "v_mfma_f32_32x32x4f16",
        "v_mfma_f32_16x16x4f16",
        "v_mfma_f32_32x32x8f16",
        "v_mfma_f32_16x16x16f16",
        "v_mfma_i32_32x32x4i8",
        "v_mfma_i32_16x16x4i8",
        "v_mfma_i32_4x4x4i8",
        "v_mfma_i32_32x32x8i8",
        "v_mfma_i32_16x16x16i8",
        "v_mfma_f32_32x32x2bf16",
        "v_mfma_f32_16x16x2bf16",
        "v_mfma_f32_4x4x2bf16",
        "v_mfma_f32_32x32x4bf16",
        "v_mfma_f32_16x16x8bf16"
      )
    ) checkProduct("gfx908", mnemonic, accumulators = true)
  }

  test("gfx90a MFMA in VGPRs and AGPRs, bf16_1k and f64") {
    val banks = Vec(false, true)
    for {
      mnemonic <- Vec(
        "v_mfma_f32_32x32x4bf16_1k",
        "v_mfma_f32_16x16x4bf16_1k",
        "v_mfma_f32_4x4x4bf16_1k",
        "v_mfma_f32_32x32x8bf16_1k",
        "v_mfma_f32_16x16x16bf16_1k",
        "v_mfma_f64_16x16x4f64",
        "v_mfma_f64_4x4x4f64",
        "v_mfma_f32_32x32x8f16",
        "v_mfma_i32_32x32x8i8"
      )
      accumulators <- banks
    } checkProduct("gfx90a", mnemonic, accumulators = accumulators)
  }

  test("gfx942 MFMA: xf32, FNUZ fp8 and bf8, double-depth i8 and f64 negation") {
    for (
      mnemonic <- Vec(
        "v_mfma_f32_16x16x8_xf32",
        "v_mfma_f32_32x32x4_xf32",
        "v_mfma_f32_16x16x32_fp8_fp8",
        "v_mfma_f32_16x16x32_fp8_bf8",
        "v_mfma_f32_16x16x32_bf8_fp8",
        "v_mfma_f32_32x32x16_bf8_bf8",
        "v_mfma_i32_16x16x32_i8",
        "v_mfma_i32_32x32x16_i8",
        "v_mfma_i32_32x32x4_2b_i8",
        "v_mfma_f32_32x32x1_2b_f32",
        "v_mfma_f32_16x16x16_bf16",
        "v_mfma_f64_4x4x4_4b_f64"
      )
    ) checkProduct("gfx942", mnemonic)
    checkProduct("gfx942", "v_mfma_f64_16x16x4_f64", "blgp:5", negate = 5)
    checkProduct("gfx942", "v_mfma_f32_16x16x4_f32", "blgp:2", broadcast = (0, 0, 2))
  }

  test("gfx942 and gfx950 SMFMAC select two of each four elements of A by index, in each index set") {
    for (target <- Vec("gfx942", "gfx950")) {
      checkProduct(target, "v_smfmac_f32_16x16x32_f16")
      checkProduct(target, "v_smfmac_f32_16x16x32_f16", "abid:2", indexSet = 2)
      checkProduct(target, "v_smfmac_f32_32x32x16_bf16")
      checkProduct(target, "v_smfmac_i32_16x16x64_i8", "abid:1", indexSet = 1)
      checkProduct(target, "v_smfmac_i32_32x32x32_i8")
      checkProduct(target, "v_smfmac_f32_16x16x64_fp8_bf8")
      checkProduct(target, "v_smfmac_f32_32x32x32_bf8_fp8")
    }
    checkProduct("gfx950", "v_smfmac_f32_16x16x64_f16", "abid:1", indexSet = 1)
    checkProduct("gfx950", "v_smfmac_f32_32x32x32_bf16")
    checkProduct("gfx950", "v_smfmac_i32_16x16x128_i8")
    checkProduct("gfx950", "v_smfmac_f32_32x32x64_fp8_fp8")
  }

  test("gfx950 MFMA: double-rate f16, bf16 and i8, OCP fp8, and f8f6f4 in every format pairing") {
    for (
      mnemonic <- Vec(
        "v_mfma_f32_16x16x32_f16",
        "v_mfma_f32_32x32x16_f16",
        "v_mfma_f32_16x16x32_bf16",
        "v_mfma_f32_32x32x16_bf16",
        "v_mfma_i32_16x16x64_i8",
        "v_mfma_i32_32x32x32_i8",
        "v_mfma_f32_16x16x32_fp8_fp8",
        "v_mfma_f32_32x32x16_bf8_fp8"
      )
    ) checkProduct("gfx950", mnemonic)
    for {
      formatA <- 0 until 5
      formatB <- 0 until 5
    } checkProduct(
      "gfx950",
      "v_mfma_f32_16x16x128_f8f6f4",
      s"cbsz:$formatA blgp:$formatB",
      formatA = formatA,
      formatB = formatB
    )
  }

  test("gfx950 scaled MFMA applies E8M0 scales by row and column over each 32 elements of K") {
    checkProduct(
      "gfx950",
      "v_mfma_scale_f32_16x16x128_f8f6f4",
      "op_sel:[1,0,0] op_sel_hi:[0,1,0] blgp:4",
      formatB = 4,
      scales = Some((1, 2))
    )
    checkProduct(
      "gfx950",
      "v_mfma_scale_f32_32x32x64_f8f6f4",
      "op_sel:[0,1,0] op_sel_hi:[0,1,0] cbsz:2 blgp:1",
      formatA = 2,
      formatB = 1,
      scales = Some((0, 3))
    )
  }

  test("gfx11 WMMA in wave32 and wave64: f16 and bf16 results in either half, iu8 and iu4 signedness and clamp") {
    for (waveSize <- Vec(32, 64)) {
      checkProduct("gfx1100", "v_wmma_f32_16x16x16_f16", waveSize = waveSize)
      checkProduct(
        "gfx1100",
        "v_wmma_f32_16x16x16_bf16",
        "neg_lo:[1,0,0] neg_hi:[1,0,0]",
        waveSize = waveSize,
        negate = 1
      )
      checkProduct("gfx1100", "v_wmma_f16_16x16x16_f16", "op_sel:[0,0,1]", waveSize = waveSize, high = true)
      checkProduct("gfx1100", "v_wmma_bf16_16x16x16_bf16", waveSize = waveSize)
      checkProduct("gfx1100", "v_wmma_i32_16x16x16_iu8", "neg_lo:[1,0,0]", waveSize = waveSize, signedB = false)
      checkProduct(
        "gfx1100",
        "v_wmma_i32_16x16x16_iu8",
        "neg_lo:[0,1,0] clamp",
        waveSize = waveSize,
        signedA = false,
        clamp = true
      )
      checkProduct("gfx1100", "v_wmma_i32_16x16x16_iu4", "neg_lo:[1,1,0]", waveSize = waveSize)
    }
  }

  test("gfx12 WMMA and SWMMAC in wave32 and wave64") {
    for (waveSize <- Vec(32, 64)) {
      checkProduct("gfx1201", "v_wmma_f32_16x16x16_f16", "neg_lo:[0,0,1]", waveSize = waveSize, negate = 4)
      checkProduct("gfx1201", "v_wmma_f32_16x16x16_bf16", waveSize = waveSize)
      checkProduct("gfx1201", "v_wmma_f16_16x16x16_f16", waveSize = waveSize)
      checkProduct("gfx1201", "v_wmma_bf16_16x16x16_bf16", waveSize = waveSize)
      checkProduct("gfx1201", "v_wmma_i32_16x16x16_iu8", "neg_lo:[1,1,0] clamp", waveSize = waveSize, clamp = true)
      checkProduct("gfx1201", "v_wmma_i32_16x16x16_iu4", "neg_lo:[1,0,0]", waveSize = waveSize, signedB = false)
      checkProduct("gfx1201", "v_wmma_i32_16x16x32_iu4", "neg_lo:[1,1,0]", waveSize = waveSize)
      checkProduct("gfx1201", "v_wmma_f32_16x16x16_fp8_bf8", waveSize = waveSize)
      checkProduct("gfx1201", "v_wmma_f32_16x16x16_bf8_bf8", waveSize = waveSize)
      checkProduct("gfx1201", "v_swmmac_f32_16x16x32_f16", waveSize = waveSize)
      checkProduct("gfx1201", "v_swmmac_bf16_16x16x32_bf16", "op_sel:[1,0,0]", waveSize = waveSize, indexSet = 1)
      checkProduct("gfx1201", "v_swmmac_i32_16x16x32_iu8", "neg_lo:[1,0,0]", waveSize = waveSize, signedB = false)
      checkProduct("gfx1201", "v_swmmac_i32_16x16x64_iu4", "neg_lo:[1,1,0]", waveSize = waveSize)
      checkProduct("gfx1201", "v_swmmac_f32_16x16x32_fp8_fp8", waveSize = waveSize)
    }
  }

  test("element arithmetic: products join a binary32 sum one rounding at a time, in K order") {
    // 1 + 2^-24 is a tie, which rounds to 1 each time, where the exact sum 1 + 2^-23 is representable
    val half = fromRaw[Float32](java.lang.Float.floatToRawIntBits(math.scalb(1.0f, -12)) & 0xffffffffL)
    val one  = fromRaw[Float32](java.lang.Float.floatToRawIntBits(1.0f) & 0xffffffffL)
    val sum  = Floats.fma(half, half, Floats.fma(half, half, one))
    assertEquals(java.lang.Float.intBitsToFloat(toRaw(sum).toInt), 1.0f)
  }

  test("element arithmetic: narrow formats widen exactly, FNUZ's NaN code and denormals included") {
    def widened(value: Float32): Float = java.lang.Float.intBitsToFloat(toRaw(value).toInt)
    assert(widened(MatrixArithmetic.widen(fromRaw[Float8E4M3FNUZ](0x80L))).isNaN)
    assertEquals(widened(MatrixArithmetic.widen(fromRaw[Float8E4M3FNUZ](0x01L))), math.scalb(1.0f, -10))
    assertEquals(widened(MatrixArithmetic.widen(fromRaw[Float8E5M2](0x7cL))), Float.PositiveInfinity)
    assert(widened(MatrixArithmetic.widen(fromRaw[Float8E4M3](0x7fL))).isNaN)
    assertEquals(widened(MatrixArithmetic.widen(fromRaw[Float4E2M1](0x9L))), -0.5f)
    assertEquals(widened(MatrixArithmetic.widen(fromRaw[Float6E3M2](0x1fL))), 28.0f)
    // XF32 keeps 10 fraction bits, rounding to nearest even
    val tie = MatrixArithmetic.widen(MatrixArithmetic.roundTf32(fromRaw[Float32](0x3f803000L)))
    assertEquals(toRaw(tie), 0x3f804000L)
    val even = MatrixArithmetic.widen(MatrixArithmetic.roundTf32(fromRaw[Float32](0x3f801000L)))
    assertEquals(toRaw(even), 0x3f800000L)
  }

  test("element arithmetic: scaled products round once into the binary32 sum, and a NaN scale gives NaN") {
    val random                        = Random(7)
    def single(value: Float): Float32 =
      fromRaw[Float32](java.lang.Float.floatToRawIntBits(value) & 0xffffffffL)
    for (_ <- 0 until 2000) {
      val sum    = random.nextFloat() * math.pow(2, random.nextInt(40) - 20).toFloat
      val left   = (random.nextInt(15) - 7) * 0.5f
      val right  = (random.nextInt(15) - 7) * 0.25f
      val scaleA = 100 + random.nextInt(60)
      val scaleB = 100 + random.nextInt(60)
      val result = MatrixArithmetic.accumulateScaled(
        MatrixArithmetic.scaled(single(left), fromRaw[ScaleE8M0](scaleA.toLong)),
        MatrixArithmetic.scaled(single(right), fromRaw[ScaleE8M0](scaleB.toLong)),
        single(sum)
      )
      // Scala.js converts many negative doubles beyond 2^64 to the wrong BigDecimal; magnitudes convert exactly
      def exactly(value: Double): java.math.BigDecimal = {
        val magnitude = java.math.BigDecimal(math.abs(value))
        if (value < 0.0) magnitude.negate() else magnitude
      }
      val exact = exactly(sum.toDouble).add(
        exactly(math.scalb(left.toDouble, scaleA - 127)).multiply(exactly(math.scalb(right.toDouble, scaleB - 127)))
      )
      // the nearest binary32, ties to even, by exact comparison
      val guess   = exact.doubleValue.toFloat
      val rounded = Vec(Math.nextDown(guess), guess, Math.nextUp(guess)).minBy(candidate =>
        (BigDecimal(exact.subtract(exactly(candidate.toDouble)).abs), java.lang.Float.floatToRawIntBits(candidate) & 1)
      )
      assertEquals(java.lang.Float.intBitsToFloat(toRaw(result).toInt), rounded, clues(sum, left, right))
    }
    assert(
      java.lang.Double.longBitsToDouble(
        toRaw(MatrixArithmetic.scaled(single(1.0f), fromRaw[ScaleE8M0](0xffL)))
      ).isNaN
    )
  }

  test("element arithmetic: integer sums wrap to 32 bits, or saturate under clamp") {
    val large = fromRaw[SInt[64]](Int.MaxValue.toLong + 5L)
    assertEquals(toRaw(MatrixArithmetic.result(large, clamp = false)), Int.MinValue.toLong + 4L)
    assertEquals(toRaw(MatrixArithmetic.result(large, clamp = true)), Int.MaxValue.toLong)
  }
}
