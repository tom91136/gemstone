package net.kurobako.gemstone.isa.amdgpu

import EncodingConstants.*
import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

/** Cooperative dispatches keep every workgroup resident, so workgroups can wait on each other through GWS barriers and
  * semaphores or by spinning on memory. Other dispatches run workgroups to completion one after another.
  */
class CooperativeDispatchSuite extends munit.FunSuite {

  private case class GridSync(target: Target, userSgprs: Int, privateSegmentBuffer: Boolean, code: Vec[Int])

  // HIP cooperative groups, ROCm 7.1.1:
  //   __global__ void k(int *o, int *acc) {
  //     cg::grid_group g = cg::this_grid(); atomicAdd(acc, 1); g.sync(); o[g.thread_rank()] = *acc;
  //   }
  // hipcc -O3 --offload-arch=<target> --cuda-device-only --no-gpu-bundle-output -c, then llvm-objdump -d up to
  // s_endpgm. gfx9 and gfx10 synchronise one wave per workgroup with ds_gws_barrier on resource 0; gfx11 and gfx12
  // count arrivals in the runtime's multigrid sync record behind hidden_multigrid_sync_arg and spin with s_sleep.
  private val grids = Seq(
    GridSync(
      Target("gfx906"),
      6,
      privateSegmentBuffer = true,
      Vec(
        0xc0060282, 0x00000010, 0x80009004, 0x82018005, 0x7e0a0280, 0xbe8c017e, 0xbf8cc07f, 0xbf0a0a06,
        0x8502928c, 0x80000200, 0x82018001, 0xdc50801e, 0x04040005, 0xdc488000, 0x03000005, 0xc00a0002,
        0x00000000, 0xc0020242, 0x00000018, 0xd28c0006, 0x0001000c, 0xd28d0006, 0x00020c0d, 0x7d940c80,
        0xbe84206a, 0xbf880005, 0xbe8c0d0c, 0x7e0c020c, 0xbf8cc07f, 0xdd088000, 0x00020605, 0x87fe047e,
        0xd2020005, 0x040a0300, 0x7d940a80, 0xbf8c0070, 0xe0fc0000, 0x00000000, 0xbf8a0000, 0xbe84206a,
        0xbf880009, 0x9209090b, 0x92090a09, 0x8109c109, 0x7e0a0209, 0xbefc0080, 0xbf800000, 0xd93b0000,
        0x00000005, 0xbf8c0000, 0x87fe047e, 0x7e0a0280, 0xbf8a0000, 0xdc508000, 0x06020005, 0x9202080b,
        0x81020702, 0x92020a02, 0x200e0890, 0x81020602, 0xd2850007, 0x00020e02, 0x260808ff, 0x0000ffff,
        0x68040507, 0xd1e80201, 0x04060902, 0x260406ff, 0x0000ffff, 0xd1e80200, 0x04020501, 0x7e020305,
        0x7e040201, 0xd28f0000, 0x00020082, 0x32000000, 0x38020302, 0xbf8c0f70, 0xdc708000, 0x007f0600,
        0xbf810000
      )
    ),
    GridSync(
      Target("gfx90a"),
      6,
      privateSegmentBuffer = true,
      Vec(
        0xc0060282, 0x00000010, 0x80009004, 0x82018005, 0x7e040280, 0xbe8c017e, 0xbf8cc07f, 0xbf0a0a06,
        0x8502928c, 0x80000200, 0x82018001, 0xdc50801e, 0x01040002, 0xdc488000, 0x03000002, 0xc00a0002,
        0x00000000, 0xc0020242, 0x00000018, 0xd28c0004, 0x0001000c, 0xd28d0004, 0x0002080d, 0x7d940880,
        0xbe84206a, 0xbf880005, 0xbe8c0d0c, 0x7e08020c, 0xbf8cc07f, 0xdd088000, 0x00020402, 0x87fe047e,
        0x260400ff, 0x000003ff, 0xd1c80004, 0x02291500, 0xd1c80000, 0x02292900, 0xd2020005, 0x04020902,
        0x7d940a80, 0xbf8c0070, 0xe0fc0000, 0x00000000, 0xbf8a0000, 0xbe84206a, 0xbf880009, 0x9209090b,
        0x92090a09, 0x8109c109, 0x7e0c0209, 0xbefc0080, 0xbf800000, 0xd93b0000, 0x00000006, 0xbf8c0000,
        0x87fe047e, 0x7e0a0280, 0xbf8a0000, 0xdc508000, 0x06020005, 0x9202080b, 0x81020702, 0x92020a02,
        0x200e0290, 0x81020602, 0xd2850007, 0x00020e02, 0x260202ff, 0x0000ffff, 0x68000107, 0x260606ff,
        0x0000ffff, 0xd1e80200, 0x04120300, 0xd1e80200, 0x040a0700, 0x7e020305, 0xd28f0000, 0x00020082,
        0x7e040201, 0x32000000, 0x38020302, 0xbf8c0f70, 0xdc708000, 0x007f0600, 0xbf810000
      )
    ),
    GridSync(
      Target("gfx1030"),
      6,
      privateSegmentBuffer = true,
      Vec(
        0xf4040282, 0xfa000010, 0x80009004, 0x82018005, 0x7e0a0280, 0xbe89037e, 0xd7650006, 0x00010009,
        0xbf8cc07f, 0xbf0a0a06, 0x8502928c, 0x80000200, 0x82018001, 0xbfa10001, 0xdc30801e, 0x03040005,
        0xdc288000, 0x04000005, 0xbfa10001, 0xf4080002, 0xfa000000, 0xf4000102, 0xfa000018, 0xbe85037e,
        0x7da40c80, 0xbf880005, 0xbe890f09, 0x7e0c0209, 0xbf8cc07f, 0xdcc88000, 0x00020605, 0x887e057e,
        0xd7720005, 0x040a0300, 0xbe85037e, 0xbf8c0070, 0xbbfd0000, 0xe1c80000, 0x00000000, 0xe1c40000,
        0x00000000, 0xbf8a0000, 0x7da40a80, 0xbf880008, 0x9304040b, 0xbefc0380, 0x93040a04, 0x8104c104,
        0x7e0a0204, 0xd8760000, 0x00000005, 0xbf8c0000, 0x887e057e, 0x7e0e0280, 0xbf8a0000, 0x2c0a0690,
        0xdc308000, 0x08020007, 0x9302080b, 0x81020702, 0x93020a02, 0x81020602, 0xd5767d05, 0x040a0a02,
        0x360406ff, 0x0000ffff, 0xd5767d01, 0x04060505, 0x360408ff, 0x0000ffff, 0xd5767d00, 0x04020501,
        0x7e020307, 0xd6ff0000, 0x00020082, 0xd70f6a00, 0x00020000, 0x50020201, 0xbf8c3f70, 0xdc708000,
        0x007d0800, 0xbf810000
      )
    ),
    GridSync(
      Target("gfx1100"),
      13,
      privateSegmentBuffer = false,
      Vec(
        0xf4040100, 0xf8000010, 0x80069000, 0x82078001, 0x7e020280, 0xbe88007e, 0xbe89007e, 0xd71f0002,
        0x00010008, 0xbf89fc07, 0xbf0a040d, 0x9802928c, 0xbf870009, 0x80020206, 0x82038007, 0xbf850001,
        0xdc52001e, 0x03000001, 0xdc4a0000, 0x04020001, 0xf4080000, 0xf8000000, 0x7d940480, 0xbfa50006,
        0xbe881808, 0xbf870009, 0x7e040208, 0xbf89fc07, 0xdcd60000, 0x00020201, 0x8c7e097e, 0x360200ff,
        0x000003ff, 0xd6100002, 0x02291500, 0xd6100000, 0x02292900, 0xbe88007e, 0xbf890007, 0xbc7c0000,
        0xe0b00000, 0x00000000, 0xe0ac0000, 0x00000000, 0xbfbd0000, 0xd6580005, 0x04020501, 0xbf870001,
        0x7d940a80, 0xbfa50041, 0xf4040183, 0xf8000058, 0x7e0e0280, 0xbe89007e, 0xbe8a007e, 0xd71f0006,
        0x00010009, 0xbf89fc07, 0xdc520028, 0x05060007, 0x7d940c80, 0xbfa50005, 0xbe891809, 0xbf870009,
        0x7e100209, 0xdcd64020, 0x08060807, 0x8c7e0a7e, 0xbf8903f7, 0x7e120508, 0x4a100ac1, 0xbf8700a2,
        0x4a0c0c09, 0xbe89007e, 0x360e0cff, 0x0000ffff, 0xbf870001, 0xd4cd007e, 0x00021107, 0x8d09097e,
        0xbfa5000e, 0x7e0a0280, 0xbe8a0080, 0xbf830001, 0xdc524020, 0x07060005, 0xbf8903f7, 0x3a0e0d07,
        0xbf8704a1, 0x7c920eff, 0x0000ffff, 0x8c0a0a6a, 0x917e0a7e, 0xbfa6fff5, 0x8c7e0a7e, 0xbe893009,
        0xbfa50012, 0xbe89007e, 0xbf870099, 0xd71f0006, 0x00010009, 0x7c940c80, 0x8b0a6a7e, 0xbf870009,
        0xbefe000a, 0xbfa50009, 0x4c0a0aff, 0x00010000, 0xbe891809, 0x7e0c0280, 0xbf870002, 0xd72c0005,
        0x00001305, 0xdcd60020, 0x00060506, 0x8c7e087e, 0x7e100280, 0xbc7c0000, 0xbfbd0000, 0x320e0690,
        0xdc520000, 0x09020008, 0x96020f05, 0xbf870499, 0x81020e02, 0x96020402, 0xbf870499, 0x81020d02,
        0xd6fe7c05, 0x04020e02, 0x360006ff, 0x0000ffff, 0xbf8700a1, 0xd6fe7c06, 0x040a0105, 0x360008ff,
        0x0000ffff, 0xd6fe7c02, 0x04060106, 0x7e060308, 0xbf870091, 0xd73c0000, 0x00020482, 0xd7006a00,
        0x00020000, 0xbf870002, 0x40020201, 0xbf8903f7, 0xdc6a0000, 0x007c0900, 0xbfb00000
      )
    ),
    GridSync(
      Target("gfx1201"),
      2,
      privateSegmentBuffer = false,
      Vec(
        0xf4002100, 0xf8000010, 0x7e020280, 0xa9869000, 0xbe830080, 0xbe88007e, 0xbe89007e, 0xd71f0002,
        0x00010008, 0xbfc70000, 0xbf0a0475, 0x9802928c, 0xbf870009, 0xa9820206, 0xbf850001, 0xee050000,
        0x00000004, 0x00001e01, 0xee048002, 0x00000003, 0x00000001, 0xf4004000, 0xf8000000, 0x7d940480,
        0xbfa50007, 0xbe881808, 0xbf88fffe, 0x7e040208, 0xbfc70000, 0xee0d4002, 0x01080000, 0x00000001,
        0x8c7e097e, 0x360200ff, 0x000003ff, 0xd6100002, 0x02291500, 0xd6100000, 0x02292900, 0xbe88007e,
        0xbfc00000, 0xbfc10000, 0xee0ac07c, 0x00080000, 0x00000000, 0xbfc70000, 0xbe804ec1, 0xbf94ffff,
        0xd6580005, 0x04020501, 0xbf870001, 0x7d940a80, 0xbfa5004a, 0xf4002183, 0xf8000058, 0x7e0e0280,
        0xbe89007e, 0xbe8a007e, 0xbf88fffe, 0xd71f0006, 0x00010009, 0xbfc70000, 0xee050006, 0x00000005,
        0x00002807, 0x7d940c80, 0xbfa50006, 0xbe891809, 0xbf88fffe, 0x7e100209, 0xee0d4006, 0x04180008,
        0x00002007, 0x8c7e0a7e, 0xbfc00000, 0x7e120508, 0x4a100ac1, 0xbf88f1ff, 0xbf8700a2, 0x4a0c0c09,
        0xbe89007e, 0x360e0cff, 0x0000ffff, 0xbf870001, 0xd4cd007e, 0x00021107, 0xbf88fffe, 0x8d09097e,
        0xbfa5000f, 0x7e0a0280, 0xbe8a0080, 0xbf830001, 0xee050006, 0x00080007, 0x00002005, 0xbfc00000,
        0x3a0e0d07, 0xbf8704a1, 0x7c920eff, 0x0000ffff, 0x8c0a0a6a, 0x917e0a7e, 0xbfa6fff4, 0x8c7e0a7e,
        0xbf88fffe, 0xbe893009, 0xbfa50014, 0xbe89007e, 0xbf88fffe, 0xd71f0006, 0x00010009, 0xbf8704a1,
        0x7c940c80, 0x8b0a6a7e, 0xbefe000a, 0xbfa5000b, 0x4c0a0aff, 0x00010000, 0xbe891809, 0x7e0c0280,
        0xbf88fffe, 0xbf870002, 0xd72c0005, 0x00001305, 0xee0d4006, 0x02880000, 0x00002006, 0xbf88fffe,
        0x8c7e087e, 0x7e0e0280, 0xbfc10000, 0xbe804ec1, 0xbf94ffff, 0x320a0890, 0xee050002, 0x00000008,
        0x00000007, 0x85029073, 0x8b03ff73, 0x0000ffff, 0x96020205, 0xbf870499, 0x81020302, 0x96020402,
        0xbf870499, 0x81027502, 0xd6fe7c05, 0x04020a02, 0x360008ff, 0x0000ffff, 0xbf8700a1, 0xd6fe7c04,
        0x040a0105, 0x360006ff, 0x0000ffff, 0xd6fe7c00, 0x04060104, 0x7e020307, 0xbf870091, 0x3e000082,
        0xd7006a00, 0x00020000, 0xbf870002, 0x40020201, 0xbfc00000, 0xee06807c, 0x04000000, 0x00000000,
        0xbfb00000
      )
    )
  )

  private inline val GroupSize    = 96
  private inline val KernargBytes = 272
  private val Limits              = TaskControl.Limits(4000000L)

  private def scalar(size: Int)(fill: Array[Byte] => Unit): KernelTask.Argument = {
    val value = Array.ofDim[Byte](size)
    fill(value)
    KernelTask.Argument.Scalar(Vec.unsafeWrapArray(value).unsafe)
  }

  /** Each work-item's output and the arrival count after `groups` workgroups of [[GroupSize]] run `grid`. */
  private def gridSync(grid: GridSync, groups: Int, cooperative: Boolean)(using
      munit.Location
  ): Either[Fault, (Vec[Int], Int)] = {
    val memory = RegionMemory()
    val output = AllocationId(1L)
    val count  = AllocationId(2L)
    val info   = AllocationId(3L)
    val total  = groups * GroupSize
    memory.allocate(output, total * 4)
    memory.allocate(count, 4)
    // HIP's MGSyncInfo: mgs, grid_id, num_grids, prev_sum, all_sum, the grid's two sync words and num_wg
    memory.allocate(info, 48)
    memory.putInt(info, 12, 1)
    memory.putInt(info, 24, groups)
    memory.putInt(info, 40, groups)
    val source = codeKernel(
      target = grid.target,
      kernargSize = KernargBytes,
      computePgmRsrc2 = (grid.userSgprs << 1) | 0x380 | (2 << 11),
      kernelCodeProperties = (if (grid.privateSegmentBuffer) 0x1 else 0) | 0x8 |
        (if (grid.target.isa.isRDNA) KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0),
      code = words(grid.code*)
    )
    // code object V5 implicit arguments: block counts, group sizes, remainders, global offsets and dimensions
    val hidden = scalar(88) { value =>
      LittleEndian.write(groups.toLong, value, 0, 4)
      LittleEndian.write(1L, value, 4, 4)
      LittleEndian.write(1L, value, 8, 4)
      LittleEndian.write(GroupSize.toLong, value, 12, 2)
      LittleEndian.write(1L, value, 14, 2)
      LittleEndian.write(1L, value, 16, 2)
      LittleEndian.write(1L, value, 64, 2)
    }
    val task = KernelTask.validated(
      "k",
      testDimensions(GroupSize.unsafe[Int > 0]),
      testDimensions(groups.unsafe[Int > 0]),
      Vec(
        KernelTask.Argument.Memory(output, 0L),
        KernelTask.Argument.Memory(count, 0L),
        hidden,
        KernelTask.Argument.Memory(info, 0L),
        scalar(KernargBytes - 112)(_ => ())
      ),
      cooperative = cooperative
    ).value
    executeKernel(compileKernel(source), task, memory, TaskControl(Limits)).map(_ =>
      (Vec.tabulate(total)(index => memory.getInt(output, (index * 4).unsafe[Int >= 0])), memory.getInt(count, 0))
    )
  }

  for (grid <- grids) {
    test(s"${grid.target} grid.sync() in a cooperative dispatch waits for every workgroup") {
      Seq(1, 3, 5).foreach { groups =>
        val total = groups * GroupSize
        assertEquals(gridSync(grid, groups, cooperative = true), Right((Vec.fill(total)(total), total)), groups)
      }
    }
    test(s"${grid.target} grid.sync() outside a cooperative dispatch cannot wait for later workgroups") {
      val result = gridSync(grid, 3, cooperative = false)
      assert(result.isLeft, result)
    }
  }

  private val gfx906 = Target("gfx906")

  /** One lane in each of `groups` gfx906 workgroups runs `text` with s0 holding its workgroup ID and s[2:3] the address
    * of a 64-byte buffer; the buffer's dwords afterwards.
    */
  private def lanes(text: String, groups: Int, cooperative: Boolean = true)(using
      munit.Location
  ): Either[Fault, Vec[Int]] = {
    val memory = RegionMemory()
    val buffer = AllocationId(1L)
    memory.allocate(buffer, 64)
    val base     = RegionMemory.Base
    val assembly = f"""s_mov_b32 s2, 0x${base & 0xffffffffL}%x
                      |s_mov_b32 s3, 0x${base >>> 32}%x
                      |v_mov_b32 v0, 0
                      |$text""".stripMargin
    val code   = asm.Assembler.encode(asm.Parser.parse(assembly).value, gfx906, 64).value
    val source = codeKernel(target = gfx906, computePgmRsrc2 = 0x80, code = code)
    val task   = KernelTask.validated(
      "kernel",
      testDimensions(1),
      testDimensions(groups.unsafe[Int > 0]),
      Vec.empty,
      cooperative = cooperative
    ).value
    executeKernel(compileKernel(source), task, memory, TaskControl(Limits)).map(_ =>
      Vec.tabulate(16)(index => memory.getInt(buffer, (index * 4).unsafe[Int >= 0]))
    )
  }

  private val Wait = "s_waitcnt vmcnt(0) expcnt(0) lgkmcnt(0)"

  /** Workgroups other than `releaser` wait with ds_gws_sema_p on resource `waits`, then copy dword 0 to dword 4 plus
    * their workgroup ID; `releaser` stores 7 to dword 0 first and then runs `release`.
    */
  private def semaphore(releaser: Int, waits: Int, release: String): String =
    s"""s_cmp_eq_u32 s0, $releaser
       |s_cbranch_scc1 release
       |s_mov_b32 m0, 0
       |ds_gws_sema_p offset:$waits gds
       |$Wait
       |global_load_dword v2, v0, s[2:3] glc
       |s_waitcnt vmcnt(0)
       |s_lshl_b32 s1, s0, 2
       |v_mov_b32 v3, s1
       |global_store_dword v3, v2, s[2:3] offset:16
       |s_endpgm
       |release:
       |v_mov_b32 v2, 7
       |global_store_dword v0, v2, s[2:3]
       |s_waitcnt vmcnt(0)
       |$release
       |$Wait
       |s_endpgm
       |""".stripMargin

  test("ds_gws_sema_p waits for a later workgroup's ds_gws_sema_v") {
    val text = semaphore(releaser = 1, waits = 1, release = "s_mov_b32 m0, 0\nds_gws_sema_v offset:1 gds")
    assertEquals(lanes(text, 2).map(_.take(5)), Right(Vec(7, 0, 0, 0, 7)))
  }

  test("ds_gws_sema_br releases its count of waves on the resource M0[21:16] plus the offset names") {
    val text = semaphore(
      releaser = 2,
      waits = 3,
      release = "s_mov_b32 m0, 0x20000\nv_mov_b32 v4, 2\nds_gws_sema_br v4 offset:1 gds"
    )
    assertEquals(lanes(text, 3).map(_.slice(4, 7)), Right(Vec(7, 7, 0)))
  }

  // workgroups start in order, so the last one releases waves that have already queued
  test("ds_gws_sema_release_all releases every queued wave") {
    val text = semaphore(releaser = 2, waits = 5, release = "s_mov_b32 m0, 0\nds_gws_sema_release_all offset:5 gds")
    assertEquals(lanes(text, 3).map(_.slice(4, 7)), Right(Vec(7, 7, 0)))
  }

  // __ockl_grid_sync on gfx803 retries ds_gws_barrier while TRAPSTS.MEM_VIOL, which hardware sets on declining it
  test("a GWS barrier's retry loop reads TRAPSTS.MEM_VIOL clear") {
    val text = s"""s_mov_b32 m0, 0
                  |v_mov_b32 v4, 1
                  |retry:
                  |s_setreg_imm32_b32 hwreg(HW_REG_TRAPSTS, 8, 1), 0
                  |ds_gws_barrier v4 gds
                  |$Wait
                  |s_getreg_b32 s4, hwreg(HW_REG_TRAPSTS, 8, 1)
                  |s_cmp_lg_u32 s4, 0
                  |s_cbranch_scc1 retry
                  |v_mov_b32 v2, 5
                  |s_lshl_b32 s1, s0, 2
                  |v_mov_b32 v3, s1
                  |global_store_dword v3, v2, s[2:3]
                  |s_endpgm
                  |""".stripMargin
    assertEquals(lanes(text, 2).map(_.take(2)), Right(Vec(5, 5)))
  }

  test("ds_gws_init sets a semaphore's count") {
    val text = s"""s_mov_b32 m0, 0
                  |v_mov_b32 v4, 1
                  |ds_gws_init v4 offset:6 gds
                  |$Wait
                  |ds_gws_sema_p offset:6 gds
                  |$Wait
                  |v_mov_b32 v2, 1
                  |global_store_dword v0, v2, s[2:3]
                  |s_endpgm
                  |""".stripMargin
    assertEquals(lanes(text, 1).map(_.head), Right(1))
  }

  test("a wave waiting on a semaphore no wave releases faults") {
    val text = s"""s_mov_b32 m0, 0
                  |ds_gws_sema_p offset:7 gds
                  |$Wait
                  |s_endpgm
                  |""".stripMargin
    val result = lanes(text, 2)
    assert(result.left.exists(_.message.contains("GWS")), result)
  }

  test("waves that pass a semaphore and wait on it again are not deadlocked") {
    def player(label: String, first: String, second: String, slot: Int) =
      s"""$label:
         |$first gds
         |$Wait
         |$second gds
         |$Wait
         |s_sub_u32 s4, s4, 1
         |s_cmp_eq_u32 s4, 0
         |s_cbranch_scc0 $label
         |v_mov_b32 v2, ${slot + 1}
         |global_store_dword v0, v2, s[2:3] offset:${slot * 4}
         |s_endpgm
         |""".stripMargin
    val text = "s_mov_b32 m0, 0\ns_mov_b32 s4, 3\ns_cmp_eq_u32 s0, 0\ns_cbranch_scc0 pong\n" +
      player("ping", "ds_gws_sema_p offset:1", "ds_gws_sema_v offset:2", 0) +
      player("pong", "ds_gws_sema_v offset:1", "ds_gws_sema_p offset:2", 1)
    assertEquals(lanes(text, 2).map(_.take(2)), Right(Vec(1, 2)))
  }

  test("GWS operations outside a cooperative dispatch fault") {
    val text   = semaphore(releaser = 1, waits = 1, release = "s_mov_b32 m0, 0\nds_gws_sema_v offset:1 gds")
    val result = lanes(text, 2, cooperative = false)
    assert(result.left.exists(_.message.contains("cooperative")), result)
  }

  private val spin = """s_cmp_eq_u32 s0, 0
                       |s_cbranch_scc0 set
                       |spin:
                       |global_load_dword v2, v0, s[2:3] glc
                       |s_waitcnt vmcnt(0)
                       |v_cmp_eq_u32 vcc, 0, v2
                       |s_cbranch_vccnz spin
                       |v_mov_b32 v2, 2
                       |global_store_dword v0, v2, s[2:3] offset:4
                       |s_endpgm
                       |set:
                       |v_mov_b32 v2, 1
                       |global_store_dword v0, v2, s[2:3]
                       |s_endpgm
                       |""".stripMargin

  test("a workgroup spinning without s_sleep lets a later workgroup of a cooperative dispatch run") {
    assertEquals(lanes(spin, 2).map(_.take(2)), Right(Vec(1, 2)))
  }

  test("a workgroup spinning on a later workgroup outside a cooperative dispatch exhausts its budget") {
    assertEquals(lanes(spin, 2, cooperative = false), Left(Fault.BudgetExceeded(Limits.maxInstructions)))
  }
}
