package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Buffer and image resources: the instruction under test is LLVM's encoding (llvm-mc 20, ROCm 7.1.1), the code around
  * it Gemstone's assembly. RDNA2 expectations were measured on a gfx1036 (run as gfx1030) through HIP with descriptors
  * built on the host; GCN5 and later generations follow the ISA documents and Mesa's descriptor definitions.
  */
class AMDGPUResourceSuite extends munit.FunSuite {

  private val Identity = 0xfacL

  private def float(value: Float): Int = java.lang.Float.floatToRawIntBits(value)

  /** `lanes` lanes of `program` on `target`, `{data}` and `{out}` spelling the two allocations' addresses as `lo,hi`
    * register pairs: the input `data` and a 1 KiB output, both returned after the run.
    */
  private def run(target: Target, lanes: Int, data: Seq[Int], program: Vec[Byte])(using
      munit.Location
  )
      : (Vec[Int], Vec[Int]) = {
    val memory = RegionMemory()
    val input  = AllocationId(1L)
    val output = AllocationId(2L)
    memory.allocate(input, 4096)
    memory.allocate(output, 1024)
    data.zipWithIndex.foreach((value, index) => memory.putInt(input, (index * 4).unsafe[Int >= 0], value))
    val wave32 = target.isa.isRDNA
    val source = codeKernel(
      target = target,
      groupSegmentSize = 4096,
      kernelCodeProperties = if (wave32) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0,
      code = program
    )
    val task = KernelTask.validated("kernel", Dimensions(lanes.unsafe[Int > 0], 1, 1), Dimensions.One, Vec.empty).value
    assertEquals(executeKernel(compileKernel(source), task, memory), Right(()))
    def words(id: AllocationId, count: Int) =
      Vec.tabulate(count)(index => memory.getInt(id, (index * 4).unsafe[Int >= 0]))
    (words(input, 1024), words(output, 256))
  }

  private def assemble(target: Target, text: String)(using munit.Location): Vec[Byte] = {
    val waveSize = if (target.isa.isRDNA) 32 else 64
    asm.Assembler.encode(asm.Parser.parse(text).value, target, waveSize.unsafe[Int in (32, 64)]).value
  }

  private def address(allocation: Long): Long = RegionMemory.Base + (allocation - 1L) * RegionMemory.Stride

  /** Moves of a buffer descriptor of the input into s[8:11] and the output address into s[14:15]. */
  private def descriptor(word1: Long, records: Long, word3: Long): String = {
    val base = address(1L)
    val out  = address(2L)
    f"""s_mov_b32 s8, 0x${base & 0xffffffffL}%x
       |s_mov_b32 s9, 0x${(base >>> 32) | word1}%x
       |s_mov_b32 s10, 0x$records%x
       |s_mov_b32 s11, 0x$word3%x
       |s_mov_b32 s14, 0x${out & 0xffffffffL}%x
       |s_mov_b32 s15, 0x${out >>> 32}%x
       |""".stripMargin
  }

  private def bytes(values: Int*): Vec[Byte] = Vec.from(values.map(_.toByte))

  /** `before`, the instruction, then each lane's v[2:5] stored to the output at 16 bytes a lane. */
  private def program(target: Target, before: String, instruction: Vec[Byte]): Vec[Byte] =
    assemble(target, before) ++ instruction ++ assemble(
      target,
      s"""v_lshlrev_b32 v6, 4, v0
        |${store(target)} v6, v[2:5], s[14:15]
        |s_endpgm
        |""".stripMargin
    )

  private def store(target: Target): String = target.isa match {
    case ISA.RDNA3 | ISA.RDNA3_5 | ISA.RDNA4 => "global_store_b128"
    case _                                   => "global_store_dwordx4"
  }

  private def lane(output: Vec[Int], index: Int, count: Int = 4): Vec[Int] = output.slice(index * 4, index * 4 + count)

  private val ascending = Seq.tabulate(1024)(0x1000 + _)

  // GFX10 unified 32_FLOAT, X Y Z W selects, OOB_SELECT in bits 28-29
  private def rdna(oob: Long, format: Long = 22L, selects: Long = Identity): Long = selects | (format << 12) |
    (oob << 28)

  private val clear = "v_mov_b32 v2, -1\nv_mov_b32 v3, -1\nv_mov_b32 v4, -1\nv_mov_b32 v5, -1\n"

  test("RDNA2 raw buffers check offset, scalar offset and size against NUM_RECORDS per dword") {
    val gfx1030 = Target("gfx1030")
    // buffer_load_dword v2, v1, s[8:11], s12 offen with 20 records and a scalar offset of 8
    val (_, out) = run(
      gfx1030,
      5,
      ascending,
      program(
        gfx1030,
        descriptor(0, 20, rdna(3)) + clear + "s_mov_b32 s12, 8\nv_lshlrev_b32 v1, 2, v0\n",
        bytes(0x00, 0x10, 0x30, 0xe0, 0x01, 0x02, 0x02, 0x0c)
      )
    )
    assertEquals((0 until 5).map(lane(out, _, 1).head), Seq(0x1002, 0x1003, 0x1004, 0, 0))
    // buffer_load_dwordx4 v[2:5], v1, s[8:11], 0 offen: each dword past the 20 bytes reads zero
    val (_, wide) = run(
      gfx1030,
      4,
      ascending,
      program(
        gfx1030,
        descriptor(0, 22, rdna(3)) + clear + "v_lshlrev_b32 v1, 2, v0\n",
        bytes(0x00, 0x10, 0x38, 0xe0, 0x01, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(wide, 2), Vec(0x1002, 0x1003, 0x1004, 0))
    assertEquals(lane(wide, 3), Vec(0x1003, 0x1004, 0, 0))
  }

  test("RDNA2 structured buffers check the index and offset plus size against the stride") {
    val gfx1030 = Target("gfx1030")
    // buffer_load_dword v2, v[0:1], s[8:11], s12 idxen offen over 5 records of 8 bytes
    val instruction                                         = bytes(0x00, 0x30, 0x30, 0xe0, 0x00, 0x02, 0x02, 0x0c)
    def load(offset: Int, scalar: Int, oob: Long): Vec[Int] = {
      val (_, out) = run(
        gfx1030,
        6,
        ascending,
        program(
          gfx1030,
          descriptor(8L << 16, 5, rdna(oob)) + clear + s"v_mov_b32 v1, $offset\ns_mov_b32 s12, $scalar\n",
          instruction
        )
      )
      Vec.tabulate(6)(lane(out, _, 1).head)
    }
    assertEquals(load(offset = 4, scalar = 0, oob = 0), Vec(0x1001, 0x1003, 0x1005, 0x1007, 0x1009, 0))
    assertEquals(load(offset = 6, scalar = 0, oob = 0), Vec(0, 0, 0, 0, 0, 0))
    // the scalar offset is outside the stride check
    assertEquals(load(offset = 0, scalar = 8, oob = 0), Vec(0x1002, 0x1004, 0x1006, 0x1008, 0x100a, 0))
    assertEquals(load(offset = 6, scalar = 0, oob = 1).take(2), Vec(0x10020000, 0x10040000))
    // OOB_SELECT 3 without swizzling checks the offset alone against the bytes
    assertEquals(load(offset = 0, scalar = 0, oob = 3), Vec(0x1000, 0x1002, 0x1004, 0x1006, 0x1008, 0x100a))
    assertEquals(load(offset = 4, scalar = 0, oob = 3), Vec(0, 0, 0, 0, 0, 0))
  }

  test("RDNA2 swizzled buffers interleave elements across the index stride, and ADD_TID indexes by lane") {
    val gfx1030 = Target("gfx1030")
    // buffer_load_dword v2, v[0:1], s[8:11], s12 idxen offen: index 3 * lane + 1, offset 20, stride 12, 8 indices
    val (_, swizzled) = run(
      gfx1030,
      4,
      Seq.tabulate(1024)(0x10000 + _),
      program(
        gfx1030,
        descriptor((12L << 16) | (1L << 31), 4096, rdna(1)) + clear +
          "v_mad_u32_u24 v0, v0, 3, 1\nv_mov_b32 v1, 20\ns_mov_b32 s12, 0\n",
        bytes(0x00, 0x30, 0x30, 0xe0, 0x00, 0x02, 0x02, 0x0c)
      )
    )
    // the store indexes the output by the replaced v0
    assertEquals(Vec(1, 4, 7, 10).map(lane(swizzled, _, 1).head - 0x10000), Vec(0x29, 0x2c, 0x2f, 0x42))
    // buffer_load_dword v2, off, s[8:11], 0 offset:4 with ADD_TID and an 8-byte stride
    val (_, threads) = run(
      gfx1030,
      4,
      ascending,
      program(
        gfx1030,
        descriptor(8L << 16, 4096, rdna(1) | (1L << 23)) + clear,
        bytes(0x04, 0x00, 0x30, 0xe0, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals((0 until 4).map(lane(threads, _, 1).head), Seq(0x1001, 0x1003, 0x1005, 0x1007))
  }

  test("GCN5 buffers check an index against NUM_RECORDS and a raw offset with the scalar offset") {
    val gfx906 = Target("gfx906")
    // 32-bit UINT data format, as LLVM's raw buffer descriptors carry
    val word3 = Identity | (4L << 15) | (4L << 12)
    // buffer_load_dword v2, v[0:1], s[8:11], 0 idxen offen: 4 records of 8 bytes, offset 12 past the stride
    val (_, structured) = run(
      gfx906,
      5,
      ascending,
      program(
        gfx906,
        descriptor(8L << 16, 4, word3) + clear + "v_mov_b32 v1, 12\n",
        bytes(0x00, 0x30, 0x50, 0xe0, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals((0 until 5).map(lane(structured, _, 1).head), Seq(0x1003, 0x1005, 0x1007, 0x1009, 0))
    // buffer_load_dword v2, v1, s[8:11], s12 offen: 16 bytes, scalar offset 8
    val (_, raw) = run(
      gfx906,
      5,
      ascending,
      program(
        gfx906,
        descriptor(0, 16, word3) + clear + "s_mov_b32 s12, 8\nv_lshlrev_b32 v1, 2, v0\n",
        bytes(0x00, 0x10, 0x50, 0xe0, 0x01, 0x02, 0x02, 0x0c)
      )
    )
    assertEquals((0 until 5).map(lane(raw, _, 1).head), Seq(0x1002, 0x1003, 0, 0, 0))
  }

  test("RDNA2 format loads convert channels, apply selects, repeat missing channels and read SEL_1 outside") {
    val gfx1030 = Target("gfx1030")
    // buffer_load_format_xyzw v[2:5], v0, s[8:11], 0 idxen
    val load = bytes(0x00, 0x20, 0x0c, 0xe0, 0x00, 0x02, 0x02, 0x80)
    def first(format: Long, records: Long, selects: Long, oob: Long, word: Int): Vec[Int] = {
      val (_, out) = run(
        gfx1030,
        1,
        Seq(word),
        program(gfx1030, descriptor(16L << 16, records, rdna(oob, format, selects)) + clear, load)
      )
      lane(out, 0)
    }
    // 8_8_8_8_UNORM
    assertEquals(
      first(format = 56, records = 1, Identity, 1, 0x80ff7f01),
      Vec(float(1f / 255), float(127f / 255), float(1f), float(128f / 255))
    )
    // 16_16_UNORM repeats x and y into z and w
    assertEquals(first(format = 23, records = 1, Identity, 1, 0x1000), Vec(0x3d800080, 0, 0x3d800080, 0))
    // 2_10_10_10_UNORM: x is bits 0-9 and y bits 10-19
    assertEquals(first(format = 50, records = 1, Identity, 1, 0x1000), Vec(0, 0x3b802008, 0, 0))
    // a 4-byte texel past 2 bytes of records reads zero, but SEL_1 reads one
    assertEquals(
      first(format = 50, records = 2, 4 | (5 << 3) | (0 << 6) | (1 << 9), 3, 0x1000),
      Vec(0, 0, 0, float(1f))
    )
    // buffer_load_format_d16_xyzw v[2:3], v0, s[8:11], 0 idxen: binary16 results, packed
    val (_, half) = run(
      gfx1030,
      1,
      Seq(0x80ff7f01),
      program(
        gfx1030,
        descriptor(16L << 16, 1, rdna(1, 56)) + clear,
        bytes(0x00, 0x20, 0x0c, 0xe2, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(half, index = 0, count = 2), Vec(0x37f81c04, 0x38043c00))
    // 32-bit integers saturate into 16 bits, 32-bit floats truncate and saturate
    val (_, saturated) = run(
      gfx1030,
      1,
      Seq(0x80ff7f01, 0x12345678, float(1.5f), float(-70000f)),
      program(
        gfx1030,
        descriptor(16L << 16, 1, rdna(1, 75)) + clear,
        bytes(0x00, 0x20, 0x0c, 0xe2, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(saturated, index = 0, count = 2), Vec(0xffffffff, 0xffffffff))
    val (_, floats) = run(
      gfx1030,
      1,
      Seq(0x80ff7f01, 0x12345678, float(1.5f), float(-70000f)),
      program(
        gfx1030,
        descriptor(16L << 16, 1, rdna(1, 77)) + clear,
        bytes(0x00, 0x20, 0x0c, 0xe2, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(floats, index = 0, count = 2), Vec(0x00008000, 0xfbff3e00))
  }

  test("RDNA2 format stores round, saturate and place channels through the inverse of the selects") {
    val gfx1030 = Target("gfx1030")
    // buffer_store_format_xyzw v[2:5], v0, s[8:11], 0 idxen
    val store = bytes(0x00, 0x20, 0x1c, 0xe0, 0x00, 0x02, 0x02, 0x80)
    def stored(format: Long, selects: Long, values: Seq[Int]): Vec[Int] = {
      val moves       = values.zipWithIndex.map((value, index) => f"v_mov_b32 v${index + 2}, 0x$value%x").mkString("\n")
      val (memory, _) = run(
        gfx1030,
        1,
        Seq(0x1000, 0x1001, 0x1002, 0x1003),
        program(gfx1030, descriptor(16L << 16, 1, rdna(1, format, selects)) + moves + "\n", store)
      )
      memory.take(4)
    }
    val signs = Seq(float(0.5f), float(1.5f), float(-0.25f), 0)
    assertEquals(stored(56, Identity, signs).head, 0x0000ff80)
    assertEquals(stored(57, Identity, signs).head, 0x00e07f40)
    // 16_16_16_16_FLOAT truncates toward zero
    assertEquals(
      stored(71, Identity, Seq(float(0.999f), float(127.5f), float(300.7f), float(-300.7f))).take(2),
      Vec(0x57f83bfd, 0xdcb25cb2)
    )
    assertEquals(
      stored(71, Identity, Seq(float(65504f), float(70000f), float(1e-8f), float(-0f))).take(2),
      Vec(0x7bff7bff, 0x80000000)
    )
    // 8_8_8_8_UINT and 16_16_16_16_SINT saturate
    assertEquals(stored(60, Identity, Seq(0, 1, 255, 256)).head, 0xffff0100)
    assertEquals(stored(70, Identity, Seq(0x7fffffff, 0x80000000, 1000, -1000)).take(2), Vec(0x80007fff, 0xfc1803e8))
    // 8_UNORM rounds half away from zero
    assertEquals(stored(1, Identity, Seq(float(0.5f), 0, 0, 0)).head & 0xff, 0x80)
    // Y X W Z selects swap the stored pairs
    assertEquals(stored(75, 5 | (4 << 3) | (7 << 6) | (6 << 9), Seq(11, 22, 33, 44)), Vec(22, 11, 44, 33))
  }

  test("typed buffer loads read the instruction's format, on GCN5 its data and numeric formats") {
    val gfx906 = Target("gfx906")
    // tbuffer_load_format_xyzw v[2:5], v1, s[8:11], 0 format:[BUF_DATA_FORMAT_32_32_32,BUF_NUM_FORMAT_UINT] offen
    val (_, out) = run(
      gfx906,
      1,
      ascending,
      program(
        gfx906,
        descriptor(0, 64, Identity | (4L << 15) | (4L << 12)) + clear + "v_mov_b32 v1, 4\n",
        bytes(0x00, 0x90, 0x69, 0xea, 0x01, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(out, 0), Vec(0x1001, 0x1002, 0x1003, 0x1001))
  }

  test("RDNA2 scalar buffer loads read zero for dwords past the buffer") {
    val gfx1030 = Target("gfx1030")
    // s_buffer_load_dwordx2 s[16:17], s[8:11], s12
    def loaded(word1: Long, records: Long, offset: Int): Vec[Int] = {
      val (_, out) = run(
        gfx1030,
        1,
        ascending,
        assemble(gfx1030, descriptor(word1, records, rdna(3)) + s"s_mov_b32 s12, $offset\n") ++
          bytes(0x04, 0x04, 0x24, 0xf4, 0x00, 0x00, 0x00, 0x18) ++ assemble(
            gfx1030,
            """v_mov_b32 v2, s16
              |v_mov_b32 v3, s17
              |v_mov_b32 v1, 0
              |global_store_dwordx2 v1, v[2:3], s[14:15]
              |s_endpgm
              |""".stripMargin
          )
      )
      out.take(2)
    }
    assertEquals(loaded(0, 8, 0), Vec(0x1000, 0x1001))
    assertEquals(loaded(0, 8, 4), Vec(0x1001, 0))
    assertEquals(loaded(0, 6, 4), Vec(0, 0))
    // a stride counts records in strides
    assertEquals(loaded(8L << 16, 2, 12), Vec(0x1003, 0))
  }

  test("buffer atomics address by index and offset, and skip lanes out of range") {
    val gfx1030 = Target("gfx1030")
    // buffer_atomic_add v2, v1, s[8:11], 0 offen glc over 10 bytes
    val (memory, out) = run(
      gfx1030,
      4,
      ascending,
      program(
        gfx1030,
        descriptor(0, 10, rdna(3)) + clear + "v_mov_b32 v2, 1\nv_lshlrev_b32 v1, 2, v0\n",
        bytes(0x00, 0x50, 0xc8, 0xe0, 0x01, 0x02, 0x02, 0x80)
      )
    )
    assertEquals((0 until 4).map(lane(out, _, 1).head), Seq(0x1000, 0x1001, 0, 0))
    assertEquals(memory.take(4), Vec(0x1001, 0x1002, 0x1002, 0x1003))
    // LLVM gfx90a, struct.buffer.atomic.add.i32: buffer_atomic_add v2, v0, s[8:11], 0 idxen glc
    val gfx90a              = Target("gfx90a")
    val (indexed, returned) = run(
      gfx90a,
      3,
      ascending,
      program(
        gfx90a,
        descriptor(8L << 16, 2, Identity | (4L << 15) | (4L << 12)) + clear + "v_mov_b32 v2, 5\n",
        bytes(0x00, 0x60, 0x08, 0xe1, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals((0 until 3).map(lane(returned, _, 1).head), Seq(0x1000, 0x1002, 0))
    assertEquals(indexed.take(5), Vec(0x1005, 0x1001, 0x1007, 0x1003, 0x1004))
  }

  test("buffer atomics subtract with a clamp or a condition and compare floats as global atomics do") {
    val gfx1030 = Target("gfx1030")
    val gfx1201 = Target("gfx1201")
    val clamped = Seq(5, 3, 0x7fffffff, 2)
    def updated(target: Target, data: Seq[Int], setup: String, instruction: Vec[Byte]): (Vec[Int], Vec[Int]) = {
      val (memory, out) = run(target, 4, data, program(target, descriptor(0, 16, rdna(3)) + clear + setup, instruction))
      (memory.take(4), Vec.tabulate(4)(lane(out, _, 1).head))
    }
    val subtract = "v_mov_b32 v2, 3\nv_lshlrev_b32 v1, 2, v0\n"
    // buffer_atomic_csub v2, v1, s[8:11], 0 offen glc clamps at zero
    assertEquals(
      updated(gfx1030, clamped, subtract, bytes(0x00, 0x50, 0xd0, 0xe0, 0x01, 0x02, 0x02, 0x80)),
      (Vec(2, 0, 0x7ffffffc, 0), Vec.from(clamped))
    )
    // clang gfx1201, llvm.amdgcn.raw.buffer.atomic.cond.sub.u32: buffer_atomic_cond_sub_u32 v2, v1, s[8:11], null offen
    // th:TH_ATOMIC_RETURN, which subtracts only where it does not wrap
    assertEquals(
      updated(
        gfx1201,
        clamped,
        subtract,
        bytes(0x7c, 0x00, 0x14, 0xc4, 0x02, 0x10, 0x90, 0x40, 0x01, 0x00, 0x00, 0x00)
      ),
      (Vec(2, 0, 0x7ffffffc, 2), Vec.from(clamped))
    )
    // buffer_atomic_sub_clamp_u32 v2, v1, s[8:11], null offen th:TH_ATOMIC_RETURN
    assertEquals(
      updated(
        gfx1201,
        clamped,
        subtract,
        bytes(0x7c, 0xc0, 0x0d, 0xc4, 0x02, 0x10, 0x90, 0x40, 0x01, 0x00, 0x00, 0x00)
      ),
      (Vec(2, 0, 0x7ffffffc, 0), Vec.from(clamped))
    )
    // buffer_atomic_fcmpswap v[2:3], v1, s[8:11], 0 offen glc swaps in v2 where memory equals v3 as a float, so -0.0
    // matches 0.0 and NaN matches nothing
    val floats = Seq(float(0.0f), float(1.5f), float(Float.NaN), float(-0.0f))
    assertEquals(
      updated(
        gfx1030,
        floats,
        s"v_mov_b32 v2, ${float(7.0f)}\nv_mov_b32 v3, 0\nv_lshlrev_b32 v1, 2, v0\n",
        bytes(0x00, 0x50, 0xf8, 0xe0, 0x01, 0x02, 0x02, 0x80)
      ),
      (Vec(float(7.0f), float(1.5f), float(Float.NaN), float(7.0f)), Vec.from(floats))
    )
    // buffer_atomic_fcmpswap_x2 v[2:5], v1, s[8:11], 0 offen glc swaps 2.5 for 1.0
    val (doubles, _) = run(
      gfx1030,
      2,
      Seq(0, 0x3ff00000, 0, 0x40000000),
      program(
        gfx1030,
        descriptor(0, 16, rdna(3)) + clear +
          "v_mov_b32 v2, 0\nv_mov_b32 v3, 0x40040000\nv_mov_b32 v4, 0\nv_mov_b32 v5, 0x3ff00000\nv_lshlrev_b32 v1, 3, v0\n",
        bytes(0x00, 0x50, 0x78, 0xe1, 0x01, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(doubles.take(4), Vec(0, 0x40040000, 0, 0x40000000))
  }

  test("GCN5 buffer stores from LDS read M0 plus the offset plus four bytes a lane") {
    val gfx906 = Target("gfx906")
    // buffer_store_lds_dword s[8:11], 0 offset:4 lds through a descriptor whose ADD_TID strides lanes by 4 bytes
    val (memory, _) = run(
      gfx906,
      4,
      ascending,
      assemble(
        gfx906,
        descriptor(4L << 16, 16, Identity | (4L << 15) | (4L << 12) | (1L << 23)) +
          """s_mov_b32 m0, 0x100
            |v_lshlrev_b32 v6, 2, v0
            |v_add_u32 v7, 0x500, v0
            |ds_write_b32 v6, v7 offset:260
            |""".stripMargin
      ) ++ bytes(0x04, 0x00, 0xf5, 0xe0, 0x00, 0x00, 0x02, 0x80) ++ assemble(gfx906, "s_endpgm\n")
    )
    assertEquals(memory.take(6), Vec(0x1000, 0x500, 0x501, 0x502, 0x503, 0x1005))
  }

  test("CDNA buffer loads into LDS write M0 plus the offset plus four bytes a lane") {
    val gfx90a = Target("gfx90a")
    // buffer_load_dword v1, s[8:11], 0 offen lds, which LLVM selects for raw.ptr.buffer.load.lds
    val (_, out) = run(
      gfx90a,
      4,
      ascending,
      assemble(
        gfx90a,
        descriptor(0, 4096, Identity | (4L << 15) | (4L << 12)) +
          "s_mov_b32 m0, 0x100\nv_lshlrev_b32 v1, 3, v0\n"
      ) ++
        bytes(0x00, 0x10, 0x51, 0xe0, 0x01, 0x00, 0x02, 0x80) ++ assemble(
          gfx90a,
          """v_lshlrev_b32 v6, 2, v0
            |ds_read_b32 v2, v6 offset:256
            |global_store_dword v6, v2, s[14:15]
            |s_endpgm
            |""".stripMargin
        )
    )
    assertEquals(out.take(4), Vec(0x1000, 0x1002, 0x1004, 0x1006))
  }

  test("a TFE buffer load writes a zero status after its data") {
    val gfx1030 = Target("gfx1030")
    // buffer_load_dword v[2:3], v1, s[8:11], 0 offen tfe
    val (_, out) = run(
      gfx1030,
      1,
      ascending,
      program(
        gfx1030,
        descriptor(0, 64, rdna(3)) + clear + "v_mov_b32 v1, 4\n",
        bytes(0x00, 0x10, 0x30, 0xe0, 0x01, 0x02, 0x82, 0x80)
      )
    )
    assertEquals(lane(out, 0), Vec(0x1001, 0, -1, -1))
  }

  /** Moves of a linear 2D image descriptor of the input, `width` by `height` texels, into s[16:23]. */
  private def image(words: Seq[Long]): String =
    words.zipWithIndex.map((word, index) => f"s_mov_b32 s${16 + index}, 0x$word%x").mkString("", "\n", "\n")

  private def rdnaImage(format: Long, width: Long, height: Long, pitch: Long, kind: Long = 9): Seq[Long] = {
    val base = address(1L) >>> 8
    Seq(
      base & 0xffffffffL,
      (base >>> 32) | (format << 20) | (((width - 1) & 3) << 30),
      ((width - 1) >>> 2) | ((height - 1) << 14),
      Identity | (kind << 28),
      pitch - 1,
      0,
      0,
      0
    )
  }

  test("RDNA2 image loads, stores, queries and atomics address a linear image by its pitch") {
    val gfx1030 = Target("gfx1030")
    // 8_8_8_8_UINT, 3 by 2 texels, rows 4 texels apart
    val descriptor  = image(rdnaImage(format = 60, width = 3, height = 2, pitch = 4)) + descriptorOutput
    val texels      = Seq(0x04030201, 0x08070605, 0x0c0b0a09, 0, 0x14131211, 0x18171615, 0x1c1b1a19, 0)
    val coordinates =
      "v_and_b32 v1, 1, v0\nv_lshrrev_b32 v0, 1, v0\nv_mov_b32 v7, v0\nv_mov_b32 v0, v1\nv_mov_b32 v1, v7\n"
    // lane l reads texel (l % 2, l / 2): image_load v[2:5], v[0:1], s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm
    val (_, out) = run(
      gfx1030,
      8,
      texels,
      imageProgram(gfx1030, descriptor + clear + coordinates, bytes(0x08, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x00))
    )
    assertEquals(out.take(4), Vec(1, 2, 3, 4))
    assertEquals(lane(out, 1), Vec(5, 6, 7, 8))
    assertEquals(lane(out, 3), Vec(0x15, 0x16, 0x17, 0x18))
    assertEquals(lane(out, 4), Vec(0, 0, 0, 0))
    // image_store v[2:5], v[0:1], s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm saturates 300 to 255
    val (stored, _) = run(
      gfx1030,
      1,
      texels,
      imageProgram(
        gfx1030,
        descriptor + "v_mov_b32 v0, 2\nv_mov_b32 v1, 1\nv_mov_b32 v2, 300\nv_mov_b32 v3, 7\n" +
          "v_mov_b32 v4, 8\nv_mov_b32 v5, 9\n",
        bytes(0x08, 0x1f, 0x20, 0xf0, 0x00, 0x02, 0x04, 0x00)
      )
    )
    assertEquals(stored(6), 0x090807ff)
    // image_get_resinfo v[2:5], v0, s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm
    val (_, info) = run(
      gfx1030,
      1,
      texels,
      imageProgram(gfx1030, descriptor + "v_mov_b32 v0, 0\n", bytes(0x08, 0x1f, 0x38, 0xf0, 0x00, 0x02, 0x04, 0x00))
    )
    assertEquals(info.take(4), Vec(3, 2, 0, 1))
    // 32_UINT: image_atomic_add v2, v[0:1], s[16:23] dmask:0x1 dim:SQ_RSRC_IMG_2D unorm glc
    val (added, old) = run(
      gfx1030,
      1,
      texels,
      imageProgram(
        gfx1030,
        image(rdnaImage(20, 3, 2, 4)) + descriptorOutput + clear +
          "v_mov_b32 v0, 1\nv_mov_b32 v1, 1\nv_mov_b32 v2, 5\n",
        bytes(0x08, 0x31, 0x44, 0xf0, 0x00, 0x02, 0x04, 0x00)
      )
    )
    assertEquals(old.head, 0x18171615)
    assertEquals(added(5), 0x1817161a)
    // 32_FLOAT: image_atomic_fcmpswap v[2:3], v[0:1], s[16:23] dmask:0x3 dim:SQ_RSRC_IMG_2D glc swaps in v2 where the
    // texel equals v3 as a float
    val (swapped, previous) = run(
      gfx1030,
      1,
      Seq(0, 0, 0, 0, 0, float(-0.0f)),
      imageProgram(
        gfx1030,
        image(rdnaImage(22, 3, 2, 4)) + descriptorOutput + clear +
          s"v_mov_b32 v0, 1\nv_mov_b32 v1, 1\nv_mov_b32 v2, ${float(7.0f)}\nv_mov_b32 v3, 0\n",
        bytes(0x08, 0x23, 0x74, 0xf0, 0x00, 0x02, 0x04, 0x00)
      )
    )
    assertEquals(previous.head, float(-0.0f))
    assertEquals(swapped(5), float(7.0f))
  }

  test("RDNA2 image addresses take layers, depth slices, mip levels and 16-bit coordinates, and D16 halves") {
    val gfx1030 = Target("gfx1030")
    val texels  = Seq.tabulate(1024)(index => index * 0x01010101 + 0x04030201)
    def load(words: Seq[Long], setup: String, instruction: Vec[Byte]): Vec[Int] = {
      val (_, out) =
        run(gfx1030, 1, texels, imageProgram(gfx1030, image(words) + descriptorOutput + clear + setup, instruction))
      out.take(4)
    }
    def unpacked(word: Int): Vec[Int] = Vec.tabulate(4)(byte => (word >>> (8 * byte)) & 0xff)
    val coordinates                   = "v_mov_b32 v0, 1\nv_mov_b32 v1, 1\nv_mov_b32 v2, 1\n"
    // 8_8_8_8_UINT 2 by 2 by 2 as layers and as depth, rows 256 bytes apart: image_load v[2:5], v[0:2], s[16:23]
    // dmask:0xf dim:SQ_RSRC_IMG_2D_ARRAY unorm, and dim:SQ_RSRC_IMG_3D
    val layers = rdnaImage(format = 60, width = 2, height = 2, pitch = 2, kind = 13)
    assertEquals(
      load(layers, coordinates, bytes(0x28, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x00)),
      unpacked(texels(193))
    )
    val volume = rdnaImage(format = 60, width = 2, height = 2, pitch = 2, kind = 10)
    assertEquals(
      load(
        volume,
        "v_mov_b32 v0, 1\nv_mov_b32 v1, 0\nv_mov_b32 v2, 1\n",
        bytes(0x10, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x00)
      ),
      unpacked(texels(129))
    )
    // two levels of a 4 by 2 image; level 1 starts 256-byte aligned after level 0: image_load_mip v[2:5], v[0:2],
    // s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm
    val levels =
      rdnaImage(format = 60, width = 4, height = 2, pitch = 64).updated(3, Identity | (1L << 16) | (9L << 28))
    assertEquals(
      load(
        levels,
        "v_mov_b32 v0, 1\nv_mov_b32 v1, 0\nv_mov_b32 v2, 1\n",
        bytes(0x08, 0x1f, 0x04, 0xf0, 0x00, 0x02, 0x04, 0x00)
      ),
      unpacked(texels(129))
    )
    assertEquals(
      load(
        levels,
        "v_mov_b32 v0, 2\nv_mov_b32 v1, 0\nv_mov_b32 v2, 1\n",
        bytes(0x08, 0x1f, 0x04, 0xf0, 0x00, 0x02, 0x04, 0x00)
      ),
      Vec(0, 0, 0, 0)
    )
    // image_load v[2:5], v0, s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm a16 packs x and y into v0
    val plane = rdnaImage(format = 60, width = 3, height = 2, pitch = 4)
    assertEquals(
      load(plane, "v_mov_b32 v0, 0x10001\n", bytes(0x08, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x40)),
      unpacked(texels(5))
    )
    // image_load v[2:3], v[0:1], s[16:23] dmask:0xf dim:SQ_RSRC_IMG_2D unorm d16 packs 16-bit integers
    val texel = unpacked(texels(5))
    assertEquals(
      load(plane, coordinates, bytes(0x08, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x80)).take(2),
      Vec(texel(0) | (texel(1) << 16), texel(2) | (texel(3) << 16))
    )
    // image_store v[2:3], v[0:1], s[16:23] dmask:0x3 dim:SQ_RSRC_IMG_2D unorm writes zero to the channels it omits
    val (stored, _) = run(
      gfx1030,
      1,
      texels,
      imageProgram(
        gfx1030,
        image(plane) + descriptorOutput + coordinates + "v_mov_b32 v2, 7\nv_mov_b32 v3, 9\n",
        bytes(0x08, 0x13, 0x20, 0xf0, 0x00, 0x02, 0x04, 0x00)
      )
    )
    assertEquals(stored(5), 0x00000907)
  }

  test("GCN5 and RDNA4 image loads take GCN5's descriptor pitch and RDNA4's listed address registers") {
    val gfx906 = Target("gfx906")
    val base   = address(1L) >>> 8
    // 8_8_8_8 UINT, 3 by 2 texels, PITCH 4: image_load v[2:5], v[0:1], s[16:23] dmask:0xf unorm
    val gcn = Seq(
      base & 0xffffffffL,
      (base >>> 32) | (10L << 20) | (4L << 26),
      2L | (1L << 14),
      Identity | (9L << 28),
      3L << 13,
      0,
      0,
      0
    )
    val texels   = Seq(0x04030201, 0x08070605, 0x0c0b0a09, 0, 0x14131211, 0x18171615, 0x1c1b1a19, 0)
    val (_, out) = run(
      gfx906,
      1,
      texels,
      imageProgram(
        gfx906,
        image(gcn) + descriptorOutput + clear + "v_mov_b32 v0, 1\nv_mov_b32 v1, 1\n",
        bytes(0x00, 0x1f, 0x00, 0xf0, 0x00, 0x02, 0x04, 0x00)
      )
    )
    assertEquals(out.take(4), Vec(0x15, 0x16, 0x17, 0x18))
    // GFX12 moves FORMAT to bits 17-24 and widens WIDTH: image_load v[2:5], [v0, v1], s[16:23] dmask:0xf
    // dim:SQ_RSRC_IMG_2D
    val gfx1200 = Target("gfx1200")
    val rdna4   = Seq(
      base & 0xffffffffL,
      (base >>> 32) | (46L << 17) | (2L << 30),
      0L | (1L << 14),
      Identity | (9L << 28),
      3L,
      0,
      0,
      0
    )
    val (_, next) = run(
      gfx1200,
      1,
      texels,
      imageProgram(
        gfx1200,
        image(rdna4) + descriptorOutput + clear + "v_mov_b32 v0, 2\nv_mov_b32 v1, 1\n",
        bytes(0x01, 0x00, 0xc0, 0xd3, 0x02, 0x20, 0x00, 0x00, 0x00, 0x01, 0x00, 0x00)
      )
    )
    assertEquals(next.take(4), Vec(0x19, 0x1a, 0x1b, 0x1c))
    // 32_UINT: image_atomic_add_uint v3, [v1, v2], s[4:11] dmask:0x1 dim:SQ_RSRC_IMG_2D th:TH_ATOMIC_RETURN
    val counter = Seq(
      base & 0xffffffffL,
      (base >>> 32) | (20L << 17) | (2L << 30),
      0L | (1L << 14),
      Identity | (9L << 28),
      3L,
      0,
      0,
      0
    )
    val (added, old) = run(
      gfx1200,
      1,
      texels,
      imageProgram(
        gfx1200,
        image(counter) + descriptorOutput +
          "s_mov_b64 s[4:5], s[16:17]\ns_mov_b64 s[6:7], s[18:19]\ns_mov_b64 s[8:9], s[20:21]\ns_mov_b64 s[10:11], s[22:23]\n" +
          clear + "v_mov_b32 v1, 1\nv_mov_b32 v2, 1\nv_mov_b32 v3, 5\n",
        bytes(0x01, 0x00, 0x43, 0xd0, 0x03, 0x08, 0x10, 0x00, 0x01, 0x02, 0x00, 0x00)
      )
    )
    assertEquals(added(5), 0x1817161a)
    assertEquals(old.take(4), Vec(1, 0x18171615, -1, -1))
  }

  test("RDNA4 VBUFFER loads and atomics and RDNA3 buffer encodings address as RDNA2 does") {
    val gfx1200 = Target("gfx1200")
    // GFX11 unified 32_FLOAT is 22 too: buffer_load_b32 v2, v0, s[8:11], null idxen offset:4
    val (_, out) = run(
      gfx1200,
      3,
      ascending,
      program(
        gfx1200,
        descriptor(8L << 16, 2, rdna(1)) + clear,
        bytes(0x7c, 0x00, 0x05, 0xc4, 0x02, 0x10, 0x80, 0x80, 0x00, 0x04, 0x00, 0x00)
      )
    )
    assertEquals((0 until 3).map(lane(out, _, 1).head), Seq(0x1001, 0x1003, 0))
    // buffer_atomic_add_u32 v2, v0, s[8:11], null idxen th:TH_ATOMIC_RETURN
    val (memory, returned) = run(
      gfx1200,
      2,
      ascending,
      program(
        gfx1200,
        descriptor(8L << 16, 2, rdna(1)) + clear + "v_mov_b32 v2, 3\n",
        bytes(0x7c, 0x40, 0x0d, 0xc4, 0x02, 0x10, 0x90, 0x80, 0x00, 0x00, 0x00, 0x00)
      )
    )
    assertEquals((0 until 2).map(lane(returned, _, 1).head), Seq(0x1000, 0x1002))
    assertEquals(memory.take(3), Vec(0x1003, 0x1001, 0x1005))
    // LLVM gfx1100: buffer_load_b32 v2, v0, s[8:11], 0 idxen offset:4
    val gfx1100    = Target("gfx1100")
    val (_, rdna3) = run(
      gfx1100,
      3,
      ascending,
      program(gfx1100, descriptor(8L << 16, 2, rdna(1)) + clear, bytes(0x04, 0x00, 0x50, 0xe0, 0x00, 0x02, 0x82, 0x80))
    )
    assertEquals((0 until 3).map(lane(rdna3, _, 1).head), Seq(0x1001, 0x1003, 0))
  }

  test("RDNA4 scalar buffer prefetches are hints") {
    // clang gfx1200, __builtin_amdgcn_s_buffer_prefetch_data: s_buffer_prefetch_data s[0:3], 0x0, null, 31
    assertEquals(decoded(Target("gfx1200"), 0xc0, 0xe7, 0x04, 0xf4, 0x00, 0x00, 0x00, 0xf8), VMIR.Instruction.Wait)
  }

  test("GFX8 D16 format loads write one half to a register") {
    val gfx803 = Target("gfx803")
    // buffer_load_format_d16_xy v[2:3], v0, s[8:11], 0 idxen over 16_16_UNORM
    val (_, out) = run(
      gfx803,
      1,
      Seq(0xffff0000),
      program(
        gfx803,
        descriptor(16L << 16, 1, Identity | (5L << 15)) + clear,
        bytes(0x00, 0x20, 0x24, 0xe0, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(out, index = 0, count = 2), Vec(0, 0x3c00))
  }

  test("gfx810 D16 format loads pack both halves into one register") {
    val gfx810 = Target("gfx810")
    // llc -mtriple=amdgcn -mcpu=gfx810, llvm.amdgcn.struct.buffer.load.format.v2f16: unlike gfx803 it selects
    // buffer_load_format_d16_xy v0, v0, s[0:3], 0 idxen with no repacking; here into v2 over 16_16_UNORM
    val (_, out) = run(
      gfx810,
      1,
      Seq(0xffff0000),
      program(
        gfx810,
        descriptor(16L << 16, 1, Identity | (5L << 15)) + clear,
        bytes(0x00, 0x20, 0x24, 0xe0, 0x00, 0x02, 0x02, 0x80)
      )
    )
    assertEquals(lane(out, index = 0, count = 2), Vec(0x3c000000, -1))
  }

  private val descriptorOutput: String = {
    val out = address(2L)
    f"s_mov_b32 s14, 0x${out & 0xffffffffL}%x\ns_mov_b32 s15, 0x${out >>> 32}%x\n"
  }

  /** `before`, the image instruction, then lane `l`'s v[2:5] stored at 16 bytes a lane by `l`, kept in v7. */
  private def imageProgram(target: Target, before: String, instruction: Vec[Byte]): Vec[Byte] =
    assemble(target, "v_mov_b32 v8, v0\n" + before) ++ instruction ++ assemble(
      target,
      s"""v_lshlrev_b32 v6, 4, v8
        |${store(target)} v6, v[2:5], s[14:15]
        |s_endpgm
        |""".stripMargin
    )
}
