package net.kurobako.gemstone.isa.amdgpu

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable

/** The facts a profiler reads from an observed launch: each issue's lanes and text, and each lane's accesses. */
class ProfileSuite extends Suite {
  private val gfx906  = Target("gfx906")
  private val gfx1030 = Target("gfx1030")
  private val gfx942  = Target("gfx942")
  private val gfx1200 = Target("gfx1200")
  private val buffer  = AllocationId(1L)
  private val base    = RegionMemory.Base

  private case class Access(lane: Int, space: Space, kind: AccessKind, address: Long, bytes: Int) derives CanEqual

  private class Result(val stream: ProfileStream) extends ProfileStream.Visitor {
    val issues: mutable.ArrayBuffer[ProfileStream.Issue]         = mutable.ArrayBuffer.empty
    val accesses: mutable.ArrayBuffer[Access]                    = mutable.ArrayBuffer.empty
    val async: mutable.ArrayBuffer[(Int, AsyncKind, Long, Long)] = mutable.ArrayBuffer.empty
    stream.foreach(this)

    override def issue(issue: ProfileStream.Issue): Unit = issues += issue

    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      accesses += Access(lane, space, kind, address, bytes)

    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
      async += ((lane, kind, target, bytes))

    def laneIndices(issue: ProfileStream.Issue): Vector[Int] = issue.lanes.toSeq.map(_.raw).toVector

    def text(issue: ProfileStream.Issue): String = stream.instructions(issue.instruction).text

    def issued(prefix: String): Vector[ProfileStream.Issue] =
      issues.toVector.filter(text(_).startsWith(prefix))

    def lanes(prefix: String): Vector[Int] = issued(prefix).flatMap(laneIndices(_))
  }

  /** `text` run by a workgroup of `threads` lanes with s[2:3] the address of a 16 KiB buffer. */
  private def run(
      text: String,
      target: Target = gfx906,
      threads: Int > 0 = 64,
      groupSegmentSize: Int >= 0 = 0,
      computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1,
      dynamicSegmentBytes: Int >= 0 = 0
  ): Result = {
    val wave32   = target.isa.isRDNA
    val assembly = f"""s_mov_b32 s2, 0x${base & 0xffffffffL}%x
                      |s_mov_b32 s3, 0x${base >>> 32}%x
                      |$text
                      |s_endpgm""".stripMargin
    val mode   = TargetMode(target, if (wave32) 32 else 64)
    val code   = asm.Assembler.encode(asm.Parser.parse(assembly).value, mode).value
    val source = codeKernel(
      target = target,
      groupSegmentSize = groupSegmentSize,
      computePgmRsrc1 = computePgmRsrc1,
      kernelCodeProperties = if (wave32) EncodingConstants.KernelCodeProperty.ENABLE_WAVEFRONT_SIZE32 else 0,
      code = code
    )
    val task = KernelTask
      .validated(
        "kernel",
        testDimensions(threads),
        testDimensions(1),
        Vec.empty,
        groupSegmentBytes = dynamicSegmentBytes
      )
      .value
    val memory = RegionMemory()
    memory.allocate(buffer, 16384)
    val recorder = ProfileRecorder()
    val result   = executeKernel(compileKernel(source), task, memory, TaskControl(observer = Some(recorder)))
    assertEquals(result, Right(()))
    Result(recorder.stream)
  }

  test("a coalesced global load reports consecutive addresses for every lane") {
    val result = run("v_lshlrev_b32 v1, 2, v0\nglobal_load_dword v2, v1, s[2:3]")
    assertEquals(result.text(result.issued("global_load_dword").head), "global_load_dword v2, v1, s[2:3]")
    assertEquals(result.issued("global_load_dword").head.subgroupSize.raw, 64)
    assertEquals(result.lanes("global_load_dword"), Vector.range(0, 64))
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4))
    )
  }

  test("a strided global load reports the stride in each lane's address") {
    val result = run("v_lshlrev_b32 v1, 7, v0\nglobal_load_dword v2, v1, s[2:3]")
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Read, base + lane * 128L, 4))
    )
  }

  test("a wide global store reports its width") {
    val result = run("v_lshlrev_b32 v1, 4, v0\nglobal_store_dwordx4 v1, v[4:7], s[2:3]")
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Write, base + lane * 16L, 16))
    )
  }

  test("a scalar load reports one read from the wave's first lane") {
    val result = run("s_load_dwordx2 s[8:9], s[2:3], 0x10\ns_waitcnt lgkmcnt(0)")
    assertEquals(result.accesses.toVector, Vector(Access(0, Space.Global, AccessKind.Read, base + 0x10, 8)))
  }

  test("an LDS store reports the offset within the workgroup's window") {
    val result = run("v_lshlrev_b32 v1, 2, v0\nds_write_b32 v1, v2 offset:16", groupSegmentSize = 512)
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Shared, AccessKind.Write, 16L + lane * 4L, 4))
    )
  }

  test("a paired LDS read reports both elements of each lane") {
    val result = run("v_lshlrev_b32 v1, 3, v0\nds_read2_b32 v[2:3], v1 offset0:1 offset1:3", groupSegmentSize = 1024)
    assertEquals(
      result.accesses.toVector.take(4),
      Vector(
        Access(0, Space.Shared, AccessKind.Read, 4L, 4),
        Access(0, Space.Shared, AccessKind.Read, 12L, 4),
        Access(1, Space.Shared, AccessKind.Read, 12L, 4),
        Access(1, Space.Shared, AccessKind.Read, 20L, 4)
      )
    )
    assertEquals(result.accesses.size, 128)
  }

  test("a global atomic reports every lane as an atomic on one address") {
    val result = run("v_mov_b32 v1, 0\nv_mov_b32 v2, 1\nglobal_atomic_add v1, v2, s[2:3]")
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Atomic, base, 4))
    )
  }

  test("an LDS atomic reports the shared space") {
    val result = run("v_mov_b32 v1, 8\nv_mov_b32 v2, 1\nds_add_u32 v1, v2", groupSegmentSize = 64)
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Shared, AccessKind.Atomic, 8L, 4))
    )
  }

  test("a private access reports the lane's own offset") {
    val result = run("v_mov_b32 v1, 24\nscratch_store_dword v1, v2, off")
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Local, AccessKind.Write, 24L, 4))
    )
  }

  test("a flat access reports the space its address resolved to") {
    val assembly =
      """v_lshlrev_b32 v5, 2, v0
        |v_add_u32 v3, s2, v5
        |v_mov_b32 v4, s3
        |flat_load_dword v6, v[3:4]
        |v_add_u32 v7, 32, v5
        |v_mov_b32 v8, 0xffff0000
        |flat_store_dword v[7:8], v6""".stripMargin
    val result = run(assembly, groupSegmentSize = 512)
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4)) ++
        Vector.tabulate(64)(lane => Access(lane, Space.Shared, AccessKind.Write, 32L + lane * 4L, 4))
    )
  }

  test("a divergent branch reports only the lanes EXEC enables") {
    val assembly =
      """v_lshlrev_b32 v1, 2, v0
        |v_cmp_gt_u32 vcc, 16, v0
        |s_and_saveexec_b64 s[4:5], vcc
        |global_load_dword v2, v1, s[2:3]
        |s_mov_b64 exec, s[4:5]
        |global_load_dword v3, v1, s[2:3] offset:256""".stripMargin
    val result = run(assembly)
    assertEquals(result.lanes("v_lshlrev_b32"), Vector.range(0, 64))
    val loads = result.issued("global_load_dword")
    assertEquals(loads.map(result.laneIndices(_)), Vector(Vector.range(0, 16), Vector.range(0, 64)))
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(16)(lane => Access(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4)) ++
        Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Read, base + 256L + lane * 4L, 4))
    )
  }

  test("a wave32 workgroup reports lanes by index within the workgroup") {
    val result = run("v_lshlrev_b32 v1, 2, v0\nglobal_load_dword v2, v1, s[2:3]", target = gfx1030)
    val loads  = result.issued("global_load_dword")
    assertEquals(loads.map(_.subgroupSize.raw), Vector(32, 32))
    assertEquals(loads.map(_.unit.raw), Vector(0, 1))
    assertEquals(loads.map(result.laneIndices(_)), Vector(Vector.range(0, 32), Vector.range(32, 64)))
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(64)(lane => Access(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4))
    )
  }

  test("an LDS DMA load reports the transfer with its read and its LDS write") {
    val result = run(
      "v_lshlrev_b32 v1, 2, v0\ns_mov_b32 m0, 64\nglobal_load_lds_dword v1, s[2:3] offset:8",
      target = gfx942,
      threads = 4,
      groupSegmentSize = 512
    )
    assertEquals(result.async.toVector, Vector.tabulate(4)(lane => (lane, AsyncKind.Transfer, -1L, 4L)))
    assertEquals(
      result.accesses.toVector,
      Vector.tabulate(4)(lane =>
        Vector(
          Access(lane, Space.Global, AccessKind.Read, base + 8L + lane * 4L, 4),
          Access(lane, Space.Shared, AccessKind.Write, 72L + lane * 4L, 4)
        )
      ).flatten
    )
  }

  test("a kernel built from lowered instructions names its instructions without assembly text") {
    val operations = Vec(VMIR.Instruction.SMove(5, VMIR.InstructionSource.Constant(42L)), VMIR.Instruction.End)
    val recorder   = ProfileRecorder()
    val result     =
      executeKernel(irKernel(operations), singleTask(), RegionMemory(), TaskControl(observer = Some(recorder)))
    assertEquals(result, Right(()))
    assertEquals(recorder.stream.instructions.size, 2)
    assert(recorder.stream.instructions.forall(_.text.nonEmpty))
  }

  test("a launch reports the descriptor's VGPR allocation and LDS for each target") {
    val wave32 =
      run("s_nop 0", target = gfx1030, computePgmRsrc1 = 3, groupSegmentSize = 512, dynamicSegmentBytes = 256)
    assertEquals(wave32.stream.resources, Some(KernelResources(64, 32, Some(32), 768L)))
    assert(Profiler.run(wave32.stream, Architecture.gfx1030).occupancy.isDefined)
    val wave64 = run("s_nop 0", target = gfx906, computePgmRsrc1 = 7, groupSegmentSize = 512, dynamicSegmentBytes = 256)
    assertEquals(wave64.stream.resources, Some(KernelResources(64, 64, Some(32), 768L)))
    val cdna = run("s_nop 0", target = gfx942, computePgmRsrc1 = 3, groupSegmentSize = 512)
    assertEquals(cdna.stream.resources, Some(KernelResources(64, 64, Some(32), 512L)))
  }

  test("s_waitcnt reports each counter it constrains under the GFX9 layout") {
    val result = run("s_waitcnt vmcnt(40) expcnt(2) lgkmcnt(1)")
    assertEquals(
      result.async.toVector,
      Vector(
        (0, AsyncKind.WaitCounter, 0L, 40L),
        (0, AsyncKind.WaitCounter, 1L, 2L),
        (0, AsyncKind.WaitCounter, 2L, 1L)
      )
    )
  }

  test("s_waitcnt reports a six-bit vmcnt and skips unnamed counters under the RDNA layout") {
    val result = run("s_waitcnt vmcnt(40) lgkmcnt(33)", target = gfx1030, threads = 32)
    assertEquals(
      result.async.toVector,
      Vector((0, AsyncKind.WaitCounter, 0L, 40L), (0, AsyncKind.WaitCounter, 2L, 33L))
    )
  }

  test("split s_waitcnt forms with a null sdst wait on their immediate under the GFX10 layout") {
    val result = run(
      "s_waitcnt_vmcnt null, 40\ns_waitcnt_expcnt null, 2\ns_waitcnt_lgkmcnt null, 1\ns_waitcnt_vscnt null, 0",
      target = gfx1030,
      threads = 32
    )
    assertEquals(
      result.async.toVector,
      Vector(
        (0, AsyncKind.WaitCounter, 0L, 40L),
        (0, AsyncKind.WaitCounter, 1L, 2L),
        (0, AsyncKind.WaitCounter, 2L, 1L),
        (0, AsyncKind.WaitCounter, 3L, 0L)
      )
    )
  }

  test("split s_waitcnt forms add the sdst register to their immediate") {
    val result = run("s_mov_b32 s1, 5\ns_waitcnt_vmcnt s1, 3\ns_waitcnt_expcnt s1, 0", target = gfx1030, threads = 32)
    assertEquals(
      result.async.toVector,
      Vector((0, AsyncKind.WaitCounter, 0L, 8L), (0, AsyncKind.WaitCounter, 1L, 5L))
    )
  }

  test("split s_waitcnt forms wrap the sum in their counter's field width") {
    val result = run("s_mov_b32 s1, 60\ns_waitcnt_vmcnt s1, 10\ns_waitcnt_expcnt s1, 7", target = gfx1030, threads = 32)
    assertEquals(
      result.async.toVector,
      Vector((0, AsyncKind.WaitCounter, 0L, 6L), (0, AsyncKind.WaitCounter, 1L, 3L))
    )
  }

  test("GFX12 counter waits report single and packed counts") {
    val result = run(
      "s_wait_loadcnt 0x3\ns_wait_dscnt 0x2\ns_wait_loadcnt_dscnt 0x0105\ns_wait_storecnt_dscnt 0x0306",
      target = gfx1200,
      threads = 32
    )
    assertEquals(
      result.async.toVector,
      Vector(
        (0, AsyncKind.WaitCounter, 0L, 3L),
        (0, AsyncKind.WaitCounter, 2L, 2L),
        (0, AsyncKind.WaitCounter, 0L, 1L),
        (0, AsyncKind.WaitCounter, 2L, 5L),
        (0, AsyncKind.WaitCounter, 3L, 3L),
        (0, AsyncKind.WaitCounter, 2L, 6L)
      )
    )
  }
}
