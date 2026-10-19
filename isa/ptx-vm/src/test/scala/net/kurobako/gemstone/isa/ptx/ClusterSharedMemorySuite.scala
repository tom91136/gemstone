package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.isa.nvidia.fixtures.*
import net.kurobako.gemstone.memory.*

// Clusters as nvcc 13.3 lowers cooperative_groups::this_cluster() and cuda::ptx for sm_90: mapa, ld/atom on
// shared::cluster, barrier.cluster, getctarank and the %cluster* special registers.
class ClusterSharedMemorySuite extends munit.FunSuite {
  private def run(kernel: Kernel, blocks: Int, threads: Int, memory: DeviceMemory): Unit =
    assertEquals(
      execute(
        kernel,
        blocks.unsafe[Int > 0],
        threads.unsafe[Int > 0],
        Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
        memory
      ),
      Right(())
    )

  test("cluster CTAs read, update and locate each other's shared memory between cluster barriers") {
    val kernel = compileKernel(AsyncCases.exchangePtx)
    val memory = DeviceMemory(4 * 32 * 48)
    run(kernel, blocks = 4, threads = 32, memory)
    for {
      block <- 0 until 4
      lane  <- 0 until 32
    } {
      val peer = block ^ 1
      assertEquals(
        memory.ints((block * 32 + lane) * 48L, 12),
        Vec(
          peer * 1000 + lane,
          peer * 1000 + lane,
          peer * 1000 + lane,
          block * 1000 + lane + 1,
          peer  % 2,
          block % 2,
          2,
          block / 2,
          2,
          2,
          1,
          block % 2
        ),
        clues(block, lane)
      )
    }
  }

  test("asynchronous stores, reductions, multicast copies and remote arrivals complete on peer mbarriers") {
    val kernel = compileKernel(AsyncCases.asyncPtx)
    val memory = DeviceMemory(64 + 4 * 32)
    for (index <- 0 until 16) assertEquals(memory.writeInt32(memory.start.offset(index * 4L), index + 100), Right(()))
    run(kernel, blocks = 4, threads = 64, memory)
    // The multicast copy issued by each cluster's rank 0 delivers the same global bytes to both CTAs.
    for (block <- 0 until 4)
      assertEquals(memory.ints(64 + block * 32L, 4), Vec((block ^ 1) % 2 + 10, 5, 102, 103), clues(block))
  }

  test("cluster launches reject grids that are not whole clusters") {
    val kernel = compileKernel(""".version 8.6
      |.target sm_90
      |.address_size 64
      |.visible .entry odd(.param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(4)
    assert(
      execute(kernel, 3, 32, Map(("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)), memory).isLeft
    )
  }

  test("multicast tensor loads fill the box in every CTA the mask names and complete each one's mbarrier") {
    val kernel          = compileKernel(AsyncCases.multicastPtx)
    val (width, height) = (16, 8)
    val memory          = DeviceMemory(16 * 8 * 4 + 4 * 64 * 4)
    for (index <- 0 until width * height)
      assertEquals(memory.writeInt32(memory.start.offset(index * 4L), index + 1000), Right(()))
    val map = TensorMaps.tiled(
      memory.start.raw,
      TensorMap.Element.U32,
      Vec(width.toLong, height.toLong),
      Vec(width * 4L),
      Vec(16, 4)
    )
    val out = memory.start.raw + width * height * 4
    assertEquals(
      execute(
        kernel,
        4,
        64,
        Map(
          ("map": NonEmpty[String]) -> Argument(Vec.from(map).unsafe[NonEmpty[Vec[Byte]]]),
          ("out": NonEmpty[String]) -> Argument.fromLong(out)
        ),
        memory
      ),
      Right(())
    )
    val expected = Vec.tabulate(64) { index =>
      val (column, row) = (4 + index % 16, 1 + index / 16)
      if (column < width) row * width + column + 1000 else 0
    }
    for (block <- 0 until 4) assertEquals(memory.ints(width * height * 4L + block * 256L, 64), expected, clues(block))
  }

  // CUTLASS's Sm100MmaPeerBitMask (cute/arch/copy_sm100_tma.hpp) clears bit 24 of a shared::cta address to name the
  // even CTA's mbarrier, as a cluster CTA's shared addresses carry its rank from bit 24: ptxas 13.3 forms them with
  // ULEA from SR_CgaCtaId shifted left by 24.
  test("clearing bit 24 of an odd CTA's shared::cta address names the even CTA's shared memory") {
    val kernel = compileKernel(""".version 8.7
      |.target sm_100a
      |.address_size 64
      |.visible .entry peer(.param .u64 out)
      |.reqnctapercluster 2, 1, 1
      |{
      | .shared .align 8 .b64 bar;
      | .reg .u64 %out, %offset, %state;
      | .reg .u32 %rank, %addr, %done, %parity;
      | .reg .pred %p, %odd;
      | ld.param.u64 %out, [out];
      | mov.u32 %rank, %cluster_ctarank;
      | mbarrier.init.shared::cta.b64 [bar], 2;
      | barrier.cluster.arrive.release.aligned;
      | barrier.cluster.wait.acquire.aligned;
      | mbarrier.arrive.shared::cta.b64 %state, [bar];
      | and.b32 %parity, %rank, 1;
      | setp.ne.u32 %odd, %parity, 0;
      | mov.u32 %addr, bar;
      | and.b32 %addr, %addr, 0xFEFFFFFF;
      | @%odd mbarrier.arrive.release.cluster.shared::cluster.b64 _, [%addr];
      | barrier.cluster.arrive.release.aligned;
      | barrier.cluster.wait.acquire.aligned;
      | mbarrier.test_wait.parity.shared::cta.b64 %p, [bar], 0;
      | selp.u32 %done, 1, 0, %p;
      | mul.wide.u32 %offset, %rank, 4;
      | add.u64 %out, %out, %offset;
      | st.global.u32 [%out], %done;
      | ret;
      |}""".stripMargin)
    val memory = DeviceMemory(8)
    run(kernel, blocks = 2, threads = 1, memory)
    assertEquals(memory.ints(0, 2), Vec(1, 0))
  }

  test("cta_group::2 tensor loads through CUTLASS's peer-bit mask complete on the even CTA's mbarrier") {
    // CTA 0 records whether its mbarrier completed instead of waiting on it
    val masked = TensorCopyCases.pairPtx
      .replace("mapa.shared::cluster.u32 %even, %even, 0;", "and.b32 %even, %even, 0xFEFFFFFF;")
      .replace("@!%p bra WAIT;", "")
      .replace(
        "st.global.u32 [%out], %r;",
        "st.global.u32 [%out], %r;\n mbarrier.test_wait.parity.shared::cta.b64 %p, [bar], 0;\n" +
          " selp.u32 %r, 1, 0, %p;\n st.global.u32 [%out+256], %r;"
      )
    assert(masked.contains("0xFEFFFFFF") && !masked.contains("@!%p bra WAIT") && masked.contains("test_wait"))
    val memory = DeviceMemory(64 * 4 * 3)
    TensorCopyCases.rows(8).zipWithIndex.foreach((value, index) =>
      assertEquals(memory.writeInt32(memory.start.offset(index * 4L), value.toInt), Right(()))
    )
    assertEquals(
      execute(
        compileKernel(masked),
        2,
        32,
        Map(
          ("load": NonEmpty[String]) ->
            Argument(Vec.from(TensorCopyCases.pairMap(memory.start.raw)).unsafe[NonEmpty[Vec[Byte]]]),
          ("out": NonEmpty[String]) -> Argument.fromLong(memory.start.raw + 256)
        ),
        memory
      ),
      Right(())
    )
    assertEquals(memory.words(256, 64), TensorCopyCases.rows(8))
    assertEquals(memory.ints(512, 64), Vec.fill(32)(1) ++ Vec.fill(32)(0))
  }
}
