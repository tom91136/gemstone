package net.kurobako.gemstone.isa.ptx

import execution.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.ptx.Program.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.Suite
import scala.util.Using

/** Checks the 24 generated MMA884 variants against independently reconstructed lane coordinates and matrix results.
  * Fragment layouts:
  * https://docs.nvidia.com/cuda/archive/11.8.0/parallel-thread-execution/index.html#warp-level-matrix-instructions-mma-m8n8k4
  */
class Mma884Suite extends Suite {
  private val AOffset = 0
  private val BOffset = 256
  private val COffset = 512
  private val DOffset = 1536

  private def registers(
      prefix: String,
      count: Int
  ): String = (0 until count).map(i => s"%$prefix$i").mkString("{", ",", "}")

  private def source(body: String): String = s""".version 8.0
.target sm_90
.address_size 64
.visible .entry mma884(.param .u64 data) {
.reg .u64 %base,%ap,%bp,%cp,%dp,%offset;
.reg .u32 %lane;
.reg .b32 %a<2>,%b<2>,%r<8>,%d<8>,%o<8>;
.reg .f32 %f<8>;
.reg .pred %p;
ld.param.u64 %base,[data];
mov.u32 %lane,%laneid;
$body
ret;
}"""

  private def run(variant: Mma884Oracle.Variant, memory: DeviceMemory): Unit = {
    val entry = parsed.statements.collectFirst {
      case entry: Statement.Entry if entry.name == variant.entryName => entry
    }.fold(fail(s"missing MMA884 entry ${variant.entryName}"))(identity)
    val kernel = compileProgram(parsed, entry.name)
    val base   = memory.start.raw
    val alias  = variant.mode != Mma884Oracle.Mode.Plain
    assertEquals(
      execute(
        kernel,
        1,
        32,
        Map(
          ("pa": NonEmpty[String]) -> Argument.fromLong(base),
          ("pb": NonEmpty[String]) -> Argument.fromLong(base + BOffset),
          ("pc": NonEmpty[String]) -> Argument.fromLong(base + COffset),
          ("pd": NonEmpty[String]) -> Argument.fromLong(base + (if (alias) COffset else DOffset))
        ),
        memory
      ),
      Right(())
    )
  }

  private lazy val parsed = {
    val input =
      Option(getClass.getResourceAsStream(Mma884Oracle.resource)).fold(fail(s"missing ${Mma884Oracle.resource}"))(
        stream => Using.resource(stream)(value => String(value.readAllBytes(), StandardCharsets.UTF_8))
      )
    Parser.parse(input).fold(error => fail(error.message), identity)
  }

  private def halfBits(value: Int): Int = IEEE754.floatToBinary16(value.toFloat).raw.toInt & 0xffff
  private def write32(memory: DeviceMemory, offset: Int, value: Int): Unit =
    assertEquals(memory.writeInt32(memory.start.offset(offset.toLong), value), Right(()))
  private def read32(memory: DeviceMemory, offset: Int): Int =
    memory.readInt32(memory.start.offset(offset.toLong)).toOption.get

  private def tile(lane: Int): Int                                                       = lane % 16 / 4
  private def aCoordinate(lane: Int, item: Int, layout: Mma884Oracle.Layout): (Int, Int) = {
    val thread = lane % 4
    val high   = if (lane < 16) 0 else 4
    if (layout == Mma884Oracle.Layout.Col) (item % 4 + high, thread) else (thread + high, item)
  }
  private def bCoordinate(lane: Int, item: Int, layout: Mma884Oracle.Layout): (Int, Int) = {
    val thread = lane % 4
    val high   = if (lane < 16) 0 else 4
    if (layout == Mma884Oracle.Layout.Col) (item, thread + high) else (thread, item + high)
  }
  private def cCoordinate(lane: Int, item: Int, accum: Mma884Oracle.Accumulator): (Int, Int) =
    if (accum == Mma884Oracle.Accumulator.F32)
      ((lane & 1) + (item & 2) + (if (lane < 16) 0 else 4), (item & 4) + (lane & 2) + (item & 1))
    else (lane % 4 + (if (lane < 16) 0 else 4), item)

  test("MMA884 generated module covers all accumulator, layout, and alias combinations") {
    assertEquals(Mma884Oracle.variants.size, 24)
    assertEquals(Mma884Oracle.variants.map(_.entryName).distinct.size, 24)
    assertEquals(Mma884Oracle.variants.map(_.accum).distinct.size, 2)
    assertEquals(Mma884Oracle.variants.map(_.aLayout).distinct.size, 2)
    assertEquals(Mma884Oracle.variants.map(_.bLayout).distinct.size, 2)
    assertEquals(Mma884Oracle.variants.map(_.mode).distinct.size, 3)
  }

  Mma884Oracle.variants.foreach { variant =>
    val accum   = variant.accum
    val aLayout = variant.aLayout
    val bLayout = variant.bLayout
    val zeroC   = variant.mode == Mma884Oracle.Mode.ZeroAlias
    val alias   = variant.mode != Mma884Oracle.Mode.Plain
    test(s"mma m8n8k4 $accum A=$aLayout B=$bLayout mode=${variant.mode} has four specified lane partitions") {
      val memory  = DeviceMemory(4096)
      val aValues = Array.ofDim[Int](4, 8, 4)
      val bValues = Array.ofDim[Int](4, 4, 8)
      val aWords  = Array.fill(64)(0)
      val bWords  = Array.fill(64)(0)
      for {
        lane <- 0 until 32
        item <- 0 until 4
      } {
        val g        = tile(lane)
        val (ar, ac) = aCoordinate(lane, item, aLayout)
        val av       = (g * 5 + ar * 3 + ac * 2) % 7 - 3
        aValues(g)(ar)(ac) = av
        aWords(lane * 2 + item / 2) |= halfBits(av) << ((item % 2) * 16)
        val (br, bc) = bCoordinate(lane, item, bLayout)
        val bv       = (g * 3 + br * 2 + bc * 5) % 7 - 3
        bValues(g)(br)(bc) = bv
        bWords(lane * 2 + item / 2) |= halfBits(bv) << ((item % 2) * 16)
      }
      aWords.zipWithIndex.foreach((word, i) => write32(memory, AOffset + i * 4, word))
      bWords.zipWithIndex.foreach((word, i) => write32(memory, BOffset + i * 4, word))

      val cValues    = Array.ofDim[Int](4, 8, 8)
      val cRegisters = if (accum == Mma884Oracle.Accumulator.F32) 8 else 4
      val cWords     = Array.fill(32 * cRegisters)(0)
      for {
        lane <- 0 until 32
        item <- 0 until 8
      } {
        val g          = tile(lane)
        val (row, col) = cCoordinate(lane, item, accum)
        val value      =
          if (zeroC) 0 else if (accum == Mma884Oracle.Accumulator.F32) g * 19 + row * 3 - col * 2 else g * 7 + row - col
        cValues(g)(row)(col) = value
        if (accum == Mma884Oracle.Accumulator.F32)
          cWords(lane * cRegisters + item) = java.lang.Float.floatToRawIntBits(value.toFloat)
        else cWords(lane * cRegisters + item / 2) |= halfBits(value) << ((item % 2) * 16)
      }
      cWords.zipWithIndex.foreach((word, i) => write32(memory, COffset + i * 4, word))

      val dRegisters = cRegisters
      val dValues    = Array.ofDim[Int](4, 8, 8)
      for {
        g   <- 0 until 4
        row <- 0 until 8
        col <- 0 until 8
      }
        dValues(g)(row)(col) = cValues(g)(row)(col) +
          (0 until 4).map(k => aValues(g)(row)(k) * bValues(g)(k)(col)).sum
      val expected = Array.fill(32 * dRegisters)(0)
      for {
        lane <- 0 until 32
        item <- 0 until 8
      } {
        val (row, col) = cCoordinate(lane, item, accum)
        val value      = dValues(tile(lane))(row)(col)
        if (accum == Mma884Oracle.Accumulator.F32)
          expected(lane * dRegisters + item) = java.lang.Float.floatToRawIntBits(value.toFloat)
        else expected(lane * dRegisters + item / 2) |= halfBits(value) << ((item % 2) * 16)
      }

      run(variant, memory)
      val outputOffset = if (alias) COffset else DOffset
      expected.zipWithIndex.foreach((bits, i) => assertEquals(read32(memory, outputOffset + i * 4), bits))
    }
  }

  test("m8n8k4 F16 MMA rejects unsupported shapes, input types, and fragment lengths") {
    def compile(body: String) = VMCompiler.compile(source(body))
    assert(compile(
      s"mma.sync.aligned.m8n8k4.row.col.f32.bf16.bf16.f32 ${registers("f", 8)},${registers("a", 2)},${registers("b", 2)},${registers("f", 8)};"
    ).isLeft)
    assert(compile(
      s"mma.sync.aligned.m8n8k4.row.col.f32.f16.f16.f32 ${registers("f", 8)},${registers("a", 1)},${registers("b", 2)},${registers("f", 8)};"
    ).isLeft)
    assert(compile(
      s"mma.sync.aligned.m8n8k4.row.col.f16.f16.f16.f16 ${registers("r", 3)},${registers("a", 2)},${registers("b", 2)},${registers("r", 4)};"
    ).isLeft)
  }
}
