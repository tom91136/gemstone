package net.kurobako.gemstone.isa.ptx

import cats.syntax.all.*
import net.kurobako.gemstone.benchmarks.*
import net.kurobako.gemstone.benchmarks.BenchmarkArguments.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** One warpgroup (`wgmma`) or one CTA (`tcgen05.mma`) multiplies matrices of ones, so D is the product count times K.
  */
object PTXTensorBenchmark {
  private enum Op derives CanEqual { case Wgmma, Tcgen05 }

  val benchmark: Case = (
    choice("op", "wgmma" -> Op.Wgmma, "tcgen05" -> Op.Tcgen05),
    iterations(5),
    positive("products", "Matrix products per launch", 1)
  ).mapN((op, iterations, products) => () => run(op, iterations, products))

  private val Lanes: Int > 0 = 128

  private val wgmmaRegisters = 32

  private def stores(registers: Seq[String], address: String): String =
    registers.zipWithIndex.map((register, index) => s"st.global.b32 [$address+${index * 4}], $register;")
      .mkString("\n      ")

  // 64 rows of A and 64 columns of B in 16 core matrices of 8 rows by 16 bytes each, K-major without swizzling
  private val wgmmaDescriptor = (8L << 16) | (16L << 32)

  private val wgmma = {
    val registers = (0 until wgmmaRegisters).map(index => s"%d$index")
    val zeros     = registers.map(register => s"mov.f32 $register, 0f00000000;").mkString("\n      ")
    s""".version 8.7
    .target sm_90a
    .address_size 64
    .visible .entry wgmma(.param .u64 output, .param .u32 products, .param .u64 descriptor) {
      .reg .b32 %i, %n, %s, %t, %q, %d<$wgmmaRegisters>;
      .reg .b64 %w, %da, %db, %o, %desc;
      .reg .pred %p, %more;
      .shared .align 1024 .b8 tile[4096];
      ld.param.u64 %o, [output];
      ld.param.u32 %n, [products];
      ld.param.u64 %desc, [descriptor];
      mov.u32 %t, %tid.x;
      mov.u32 %s, tile;
      shl.b32 %q, %t, 5;
      add.u32 %q, %s, %q;
      st.shared.v4.b32 [%q], {0x3c003c00, 0x3c003c00, 0x3c003c00, 0x3c003c00};
      st.shared.v4.b32 [%q+16], {0x3c003c00, 0x3c003c00, 0x3c003c00, 0x3c003c00};
      bar.sync 0;
      cvt.u64.u32 %da, %s;
      and.b64 %da, %da, 0x3ffff;
      shr.u64 %da, %da, 4;
      add.u64 %db, %da, 128;
      or.b64 %da, %da, %desc;
      or.b64 %db, %db, %desc;
      $zeros
      setp.ge.u32 %p, %n, 0;
      mov.u32 %i, 0;
      fence.proxy.async;
    loop:
      wgmma.fence.sync.aligned;
      wgmma.mma_async.sync.aligned.m64n64k16.f32.f16.f16 ${registers.mkString(
        "{",
        ", ",
        "}"
      )}, %da, %db, %p, -1, 1, 0, 0;
      wgmma.commit_group.sync.aligned;
      wgmma.wait_group.sync.aligned 0;
      add.u32 %i, %i, 1;
      setp.lt.u32 %more, %i, %n;
      @%more bra loop;
      mul.wide.u32 %w, %t, ${wgmmaRegisters * 4};
      add.u64 %o, %o, %w;
      ${stores(registers, "%o")}
      ret;
    }"""
  }

  private val tcgenColumns = 64

  // 128-byte swizzle (2) with the sm_100 version bit, 16 bytes between K chunks and 1 KiB between groups of 8 rows
  private val tcgenDescriptor = (1L << 16) | (64L << 32) | (1L << 46) | (2L << 61)

  // bf16 data in both operands, f32 D; M is 128 and N is 64
  private val tcgenInstruction = (1L << 4) | (1L << 7) | (1L << 10) | ((tcgenColumns.toLong >>> 3) << 17) | (8L << 24)

  private val tcgen05 = {
    val registers = (0 until 8).map(index => s"%r$index")
    s""".version 8.7
    .target sm_100a
    .address_size 64
    .visible .entry mma(.param .u64 output, .param .u64 adesc, .param .u64 bdesc, .param .u32 idesc,
      .param .u32 products) {
      .shared .align 1024 .b8 tiles[32768];
      .shared .align 8 .b64 bar;
      .shared .align 16 .b32 slot;
      .reg .u64 %out, %at, %offset, %smem64;
      .reg .b64 %ad, %bd;
      .reg .u32 %t, %warp, %base, %i, %n, %v, %smem, %row, %col, %cell;
      .reg .b32 %idesc, %taddr, %r<8>;
      .reg .pred %first, %leader, %loop, %done, %acc;
      ld.param.u64 %out, [output];
      ld.param.b64 %ad, [adesc];
      ld.param.b64 %bd, [bdesc];
      ld.param.b32 %idesc, [idesc];
      ld.param.u32 %n, [products];
      mov.u32 %t, %tid.x;
      shr.u32 %warp, %t, 5;
      setp.eq.u32 %first, %warp, 0;
      setp.eq.u32 %leader, %t, 0;
      mov.u32 %smem, tiles;
      shr.u32 %v, %smem, 4;
      and.b32 %v, %v, 0x3fff;
      cvt.u64.u32 %smem64, %v;
      add.u64 %ad, %ad, %smem64;
      add.u64 %bd, %bd, %smem64;
      add.u64 %bd, %bd, 1024;
      shl.b32 %i, %t, 4;
    fill:
      add.u32 %row, %smem, %i;
      st.shared.v4.b32 [%row], {0x3f803f80, 0x3f803f80, 0x3f803f80, 0x3f803f80};
      add.u32 %i, %i, 2048;
      setp.lt.u32 %loop, %i, 32768;
      @%loop bra fill;
      @%first tcgen05.alloc.cta_group::1.sync.aligned.shared::cta.b32 [slot], $tcgenColumns;
      @%leader mbarrier.init.shared::cta.b64 [bar], 1;
      fence.proxy.async.shared::cta;
      fence.mbarrier_init.release.cluster;
      tcgen05.fence::before_thread_sync;
      bar.sync 0;
      tcgen05.fence::after_thread_sync;
      ld.shared.b32 %taddr, [slot];
      mov.u32 %i, 0;
    product:
      setp.ne.u32 %acc, %i, 0;
      @%leader tcgen05.mma.cta_group::1.kind::f16 [%taddr], %ad, %bd, %idesc, %acc;
      add.u32 %i, %i, 1;
      setp.lt.u32 %loop, %i, %n;
      @%loop bra product;
      @%leader tcgen05.commit.cta_group::1.mbarrier::arrive::one.shared::cluster.b64 [bar];
    wait:
      mbarrier.try_wait.parity.shared::cta.b64 %done, [bar], 0;
      @!%done bra wait;
      tcgen05.fence::after_thread_sync;
      shl.b32 %base, %warp, 21;
      add.u32 %base, %taddr, %base;
      mov.u32 %col, 0;
    read:
      add.u32 %row, %base, %col;
      tcgen05.ld.sync.aligned.32x32b.x8.b32 ${registers.mkString("{", ", ", "}")}, [%row];
      tcgen05.wait::ld.sync.aligned;
      mad.lo.u32 %cell, %t, $tcgenColumns, %col;
      mul.wide.u32 %offset, %cell, 4;
      add.u64 %at, %out, %offset;
      ${stores(registers, "%at")}
      add.u32 %col, %col, 8;
      setp.lt.u32 %loop, %col, $tcgenColumns;
      @%loop bra read;
      tcgen05.fence::before_thread_sync;
      bar.sync 0;
      @%first tcgen05.relinquish_alloc_permit.cta_group::1.sync.aligned;
      @%first tcgen05.dealloc.cta_group::1.sync.aligned.b32 %taddr, $tcgenColumns;
      ret;
    }"""
  }

  private def run(op: Op, iterations: Int > 0, products: Int > 0): BenchmarkSample = {
    val (source, words, expected) = op match {
      case Op.Wgmma   => (wgmma, Lanes.raw * wgmmaRegisters, -16f * products.raw)
      case Op.Tcgen05 => (tcgen05, Lanes.raw * tcgenColumns, 16f * products.raw)
    }
    val kernel   = VMCompiler.compile(source).fold(error => throw IllegalArgumentException(error.message), identity)
    val geometry = LaunchGeometry.forLaunch(Dimensions(Lanes, 1, 1), Dimensions(1, 1, 1))
      .fold(error => throw IllegalArgumentException(error), identity)
    val memory = BenchmarkMemory(words * 4)
    val output = memory.ids(0)
    val base   = Map(
      ("output": NonEmpty[String])   -> Argument.fromLong(memory.base(output).raw),
      ("products": NonEmpty[String]) -> Argument.fromLong(products.toLong)
    )
    val parameters = op match {
      case Op.Wgmma   => base + (("descriptor": NonEmpty[String]) -> Argument.fromLong(wgmmaDescriptor))
      case Op.Tcgen05 =>
        base ++ Map(
          ("adesc": NonEmpty[String]) -> Argument.fromLong(tcgenDescriptor),
          ("bdesc": NonEmpty[String]) -> Argument.fromLong(tcgenDescriptor),
          ("idesc": NonEmpty[String]) -> Argument.fromLong(tcgenInstruction)
        )
    }
    val sample = Measurement.repeatSucceeding(iterations)(VMInterpreter.execute(kernel, geometry, parameters, memory))
    (0 until words).foreach { index =>
      if (java.lang.Float.intBitsToFloat(memory.getInt(output, index * 4)) != expected)
        throw IllegalStateException(s"unexpected result at index $index")
    }
    sample
  }
}
