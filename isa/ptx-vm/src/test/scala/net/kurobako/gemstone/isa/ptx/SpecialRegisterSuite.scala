package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.Suite

// PTX ISA 13.3 chapter 10, Special Registers.
class SpecialRegisterSuite extends Suite {
  private val soleClusterIds = Vec(0L, 1L)

  private val reads = Vec(
    "mov.u64 %v, %clock64;",
    "mov.u32 %w, %clock; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %clock_hi; cvt.u64.u32 %v, %w;",
    "add.u64 %v, %v, 1; mul.lo.u64 %v, %v, %v; mov.u64 %v, %clock64;",
    "mov.u32 %w, %clock; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %smid; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %nsmid; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %warpid; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %nwarpid; cvt.u64.u32 %v, %w;",
    "mov.u64 %v, %globaltimer;",
    "mov.u32 %w, %globaltimer_lo; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %globaltimer_hi; cvt.u64.u32 %v, %w;",
    "mov.u64 %v, %globaltimer;",
    "mov.u32 %w, %cluster_ctarank; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %cluster_nctarank; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %clusterid.x; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %nclusterid.x; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %cluster_ctaid.x; cvt.u64.u32 %v, %w;",
    "mov.u32 %w, %cluster_nctaid.x; cvt.u64.u32 %v, %w;",
    "mov.u64 %v, %current_graph_exec;",
    "mov.u32 %w, %ctaid.x; cvt.u64.u32 %v, %w;"
  )

  private val source = s""".version 8.0
    |.target sm_90
    |.address_size 64
    |.visible .entry special(.param .u64 output) {
    |  .reg .u32 %w, %index;
    |  .reg .u64 %v, %p, %offset;
    |  ld.param.u64 %p, [output];
    |  mov.u32 %index, %ntid.x;
    |  mov.u32 %w, %ctaid.x;
    |  mul.lo.u32 %index, %index, %w;
    |  mov.u32 %w, %tid.x;
    |  add.u32 %index, %index, %w;
    |  mul.wide.u32 %offset, %index, ${reads.size * 8};
    |  add.u64 %p, %p, %offset;
    |${reads.zipWithIndex.map((read, index) => s"  $read st.global.u64 [%p+${index * 8}], %v;").mkString("\n")}
    |  ret;
    |}""".stripMargin

  private val blocks  = 2
  private val threads = 96

  private lazy val records: Vec[Vec[Long]] = {
    val memory = DeviceMemory((blocks * threads * reads.size * 8).unsafe[Int >= 0])
    val result = execute(
      compileKernel(source),
      blocks.unsafe[Int > 0],
      threads.unsafe[Int > 0],
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory
    )
    assert(result.isRight, result.left.toOption.map(_.message).getOrElse(""))
    Vec.tabulate(blocks * threads)(thread =>
      Vec.tabulate(reads.size)(field =>
        memory.readLittleEndian(memory.start.offset((thread * reads.size + field).toLong * 8), 8).toOption.get
      )
    )
  }

  test("clock64 and clock count up between reads; clock is the low and clock_hi the high word of clock64") {
    records.foreach { record =>
      assert(record(3) > record(0), record)
      assert(((record(4) - record(1)) & 0xffffffffL) > 0L, record)
      assert((record(0) & 0xffffffffL) < record(1) && record(1) < (record(3) & 0xffffffffL), record)
      assert(record(2) >= (record(0) >>> 32) && record(2) <= (record(3) >>> 32), record)
    }
  }

  test("smid is below nsmid, and warpid identifies each warp of a CTA below nwarpid") {
    records.foreach { record =>
      assert(record(6) > 0L && record(5) < record(6), record)
      assert(record(8) > 0L && record(7) < record(8), record)
    }
    records.grouped(threads).foreach { block =>
      val warps = block.grouped(32).map(_.map(_(7)).distinct).toVector
      warps.foreach(ids => assertEquals(ids.size, 1))
      assertEquals(warps.flatten.distinct.size, threads / 32)
    }
  }

  test("globaltimer is a nanosecond timer that does not run backwards; its words read separately") {
    val now = platform.epochMillis * 1000000L
    records.foreach { record =>
      assert(record(9) > 0L && record(12) >= record(9), record)
      assert(math.abs(record(9) - now) < 3600L * 1000000000L, record)
      assert(record(11) >= (record(9) >>> 32) && record(11) <= (record(12) >>> 32), record)
    }
  }

  test("a CTA outside an explicit cluster is the only CTA of its own cluster") {
    records.foreach { record =>
      assertEquals(record.slice(13, 15), soleClusterIds)
      assertEquals(record(15), record(20))
      assertEquals(record(16), blocks.toLong)
      assertEquals(record.slice(17, 19), soleClusterIds)
    }
  }

  test("current_graph_exec is zero outside a device graph") {
    records.foreach(record => assertEquals(record(19), 0L))
  }

  for (
    (register, tpe) <- Vec(
      "%gridid"                     -> "u64",
      "%envreg1"                    -> "b32",
      "%pm0"                        -> "u32",
      "%pm3_64"                     -> "u64",
      "%total_smem_size"            -> "u32",
      "%aggr_smem_size"             -> "u32",
      "%reserved_smem_offset_begin" -> "b32",
      "%reserved_smem_offset_1"     -> "b32",
      "%tid"                        -> "u32"
    )
  )
    test(s"$register, which the VM does not model, is rejected when the kernel is compiled") {
      val rejected = VMCompiler.compile(s""".version 8.0
        |.target sm_90
        |.address_size 64
        |.visible .entry special(.param .u64 output) {
        |  .reg .$tpe %v;
        |  .reg .u64 %p;
        |  ld.param.u64 %p, [output];
        |  mov.$tpe %v, $register;
        |  st.global.$tpe [%p], %v;
        |  ret;
        |}""".stripMargin)
      assert(rejected.left.exists(_.message.raw.contains(register)), rejected)
    }

  test("%is_explicit_cluster reports whether the launch named a cluster shape") {
    for (directive <- Vec("", ".reqnctapercluster 1, 1, 1")) {
      val kernel = compileKernel(s""".version 8.0
        |.target sm_90
        |.address_size 64
        |.visible .entry special(.param .u64 output) $directive {
        |  .reg .pred %q;
        |  .reg .u64 %p;
        |  ld.param.u64 %p, [output];
        |  mov.pred %q, %is_explicit_cluster;
        |  @%q st.global.u32 [%p], 1;
        |  ret;
        |}""".stripMargin)
      val memory = DeviceMemory(4)
      assertEquals(
        execute(kernel, 1, 1, Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)), memory),
        Right(())
      )
      assertEquals(memory.readInt32(memory.start), Right(if (directive.isEmpty) 0 else 1))
    }
  }
}
