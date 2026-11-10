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
  private val gfx90a  = Target("gfx90a")
  private val gfx1030 = Target("gfx1030")
  private val gfx1100 = Target("gfx1100")
  private val gfx942  = Target("gfx942")
  private val gfx1200 = Target("gfx1200")
  private val gfx1250 = Target("gfx1250")
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
      wave64: Boolean = false,
      groupSegmentSize: Int >= 0 = 0,
      computePgmRsrc1: Int = CodeObjectKernel.DefaultComputePgmRsrc1,
      dynamicSegmentBytes: Int >= 0 = 0,
      observed: Boolean = true
  ): Result = {
    val assembly = f"""s_mov_b32 s2, 0x${base & 0xffffffffL}%x
                      |s_mov_b32 s3, 0x${base >>> 32}%x
                      |$text
                      |s_endpgm""".stripMargin
    val wave32 = (target.isa.isRDNA || target.isa.isGFX11OrLater) && !wave64
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
    val result   =
      executeKernel(
        compileKernel(source),
        task,
        memory,
        TaskControl(observer = Option.when(observed)(recorder))
      )
    assertEquals(result, Right(()))
    Result(recorder.stream)
  }

  private def planned(result: Result, mnemonic: String): Long =
    Profiler.run(result.stream, Architecture.gfx1030).instructions.collect {
      case (instruction, counters) if instruction.text.startsWith(mnemonic) => counters.plannedCycles
    }.sum

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

  private def uses(result: Result, prefix: String): Vector[RegisterUse] =
    result.issued(prefix).map(issue => result.stream.instructions(issue.instruction).use)

  test("register reads and writes name each operand in ISA spelling with its width") {
    val result = run(
      """v_lshlrev_b32 v1, 2, v0
        |v_add_u32 v2, v1, v3
        |global_load_dwordx2 v[4:5], v1, s[2:3]
        |global_store_dwordx2 v1, v[4:5], s[2:3]""".stripMargin
    )
    assertEquals(uses(result, "v_lshlrev_b32"), Vector(RegisterUse(Vec("v0"), Vec("v1"))))
    assertEquals(uses(result, "v_add_u32"), Vector(RegisterUse(Vec("v1", "v3"), Vec("v2"))))
    assertEquals(
      uses(result, "global_load_dwordx2"),
      Vector(RegisterUse(Vec("s[2:3]", "v1"), Vec("v[4:5]"), Map("s[2:3]" -> 2, "v[4:5]" -> 2)))
    )
    assertEquals(
      uses(result, "global_store_dwordx2"),
      Vector(RegisterUse(Vec("s[2:3]", "v1", "v[4:5]"), Vec.empty, Map("s[2:3]" -> 2, "v[4:5]" -> 2)))
    )
  }

  test("an accumulating instruction reads its destination") {
    val result = run("v_fmac_f32 v5, v1, v2\nv_mac_f32 v6, v1, v2")
    assertEquals(uses(result, "v_fmac_f32"), Vector(RegisterUse(Vec("v1", "v2", "v5"), Vec("v5"))))
    assertEquals(uses(result, "v_mac_f32"), Vector(RegisterUse(Vec("v1", "v2", "v6"), Vec("v6"))))
  }

  test("a legacy flat address without a scalar base is a register pair") {
    val result = run(
      """v_lshlrev_b32 v5, 2, v0
        |v_add_u32 v3, s2, v5
        |v_mov_b32 v4, s3
        |flat_load_dword v6, v[3:4]""".stripMargin
    )
    assertEquals(
      uses(result, "flat_load_dword"),
      Vector(RegisterUse(Vec("v[3:4]"), Vec("v6"), Map("v[3:4]" -> 2)))
    )
  }

  test("a scratch access with a scalar base reads no vector address") {
    val result = run("s_mov_b32 s4, 24\nv_mov_b32 v2, 7\nscratch_store_dword off, v2, s4")
    assertEquals(uses(result, "scratch_store_dword"), Vector(RegisterUse(Vec("s4", "v2"), Vec.empty)))
  }

  test("a global address with a scalar base is one register") {
    val result = run(
      "v_lshlrev_b32 v1, 2, v0\nglobal_load_dword v2, v1, s[2:3]",
      target = gfx1030,
      threads = 32
    )
    assertEquals(
      uses(result, "global_load_dword"),
      Vector(RegisterUse(Vec("s[2:3]", "v1"), Vec("v2"), Map("s[2:3]" -> 2)))
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
    assertEquals(result.async.toVector, Vector.tabulate(4)(lane => (lane, AsyncKind.Transfer, DefaultTarget, 4L)))
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

  test("a launch records one control flow graph whose offsets join every issued address") {
    val result = run("s_branch 1\ns_mov_b32 s4, 1")
    val graphs = result.stream.graphs
    assertEquals(graphs.map(_.kernel).toVector, Vector("kernel"))
    val flow   = graphs.head
    val issued = result.issues.toVector.flatMap(issue => result.stream.instructions(issue.instruction).address)
    assert(issued.nonEmpty)
    assert(issued.forall { address =>
      val index = flow.offsets.indexOf(address)
      index >= 0 && flow.graph.blocks.exists(block => block.first <= index && index <= block.last)
    })
  }

  test("a launch without an observer runs and records no control flow") {
    val result = run("s_branch 1\ns_mov_b32 s4, 1", observed = false)
    assert(result.stream.graphs.isEmpty)
    assert(result.issues.isEmpty)
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

  test("planned cycles take the LLVM model's cycles on a gfx906 wave64") {
    assertEquals(planned(run("v_rcp_f32 v1, v0", target = gfx906), "v_rcp_f32"), 1L)
  }

  test("planned cycles take the LLVM model's cycles on a gfx1030 wave32") {
    val result = run("v_rcp_f32 v1, v0\nv_add_nc_u32 v2, v0, v0", target = gfx1030, threads = 32)
    assertEquals(planned(result, "v_rcp_f32"), 1L)
    assertEquals(planned(result, "v_add_nc_u32"), 1L)
  }

  test("planned cycles double on a gfx1030 wave64") {
    val result = run("v_rcp_f32 v1, v0\nv_add_nc_u32 v2, v0, v0", target = gfx1030, wave64 = true)
    assertEquals(planned(result, "v_rcp_f32"), 2L)
    assertEquals(planned(result, "v_add_nc_u32"), 2L)
  }

  test("planned cycles of SALU instructions are not doubled on a gfx1030 wave64") {
    val result = run("v_rcp_f32 v1, v0\ns_add_u32 s1, s0, s0", target = gfx1030, wave64 = true)
    assertEquals(planned(result, "v_rcp_f32"), 2L)
    assertEquals(planned(result, "s_add_u32"), 1L)
  }

  test("planned cycles of an lds load are twice those of the plain load on a gfx1030 wave32") {
    assertEquals(
      planned(
        run("v_lshlrev_b32 v1, 2, v0\nglobal_load_dword v2, v1, s[2:3]", target = gfx1030, threads = 32),
        "global_load_dword"
      ),
      1L
    )
    assertEquals(
      planned(
        run(
          "v_lshlrev_b32 v1, 2, v0\nglobal_load_dword v1, s[2:3] lds",
          target = gfx1030,
          threads = 32,
          groupSegmentSize = 256
        ),
        "global_load_dword"
      ),
      2L
    )
  }

  test("planned cycles of a buffer lds store take the LLVM model's cycles on a gfx906 wave64") {
    assertEquals(
      planned(
        run("buffer_store_lds_dword s[8:11], 0 offset:4 lds", target = gfx906, groupSegmentSize = 256),
        "buffer_store_lds_dword"
      ),
      1L
    )
  }

  test("s_nop charges its count plus one cycles within the field width of each ISA") {
    assertEquals(planned(run("s_nop 7", target = gfx906), "s_nop"), 8L)
    assertEquals(planned(run("s_nop 15", target = gfx1030, threads = 32), "s_nop"), 16L)
    assertEquals(planned(run("s_nop 127", target = gfx1200, threads = 32), "s_nop"), 128L)
  }

  test("s_delay_alu charges a VALU_DEP_1 consumer the producer latency less the gap on gfx1100") {
    val result = run(
      "v_add_f32 v1, v0, v0\ns_delay_alu instid0(VALU_DEP_1)\nv_mul_f32 v2, v1, v1",
      target = gfx1100,
      threads = 32
    )
    assertEquals(planned(result, "v_mul_f32"), 5L)
  }

  test("s_delay_alu charges a TRANS32_DEP_1 consumer the transcendental latency less the gap on gfx1100") {
    val result = run(
      "v_rcp_f32 v1, v0\ns_delay_alu instid0(TRANS32_DEP_1)\nv_mul_f32 v2, v1, v1",
      target = gfx1100,
      threads = 32
    )
    assertEquals(planned(result, "v_mul_f32"), 10L)
  }

  test("s_delay_alu SALU_CYCLE_1 adds one cycle to the VALU instruction it delays on gfx1100") {
    val result = run(
      "v_add_f32 v1, v0, v0\ns_delay_alu instid0(SALU_CYCLE_1)\nv_mul_f32 v2, v1, v1",
      target = gfx1100,
      threads = 32
    )
    assertEquals(planned(result, "v_mul_f32"), 2L)
  }

  test("a VOPD pair on gfx1250 issues once at the cycles of its halves") {
    val result = run("v_dual_mov_b32 v10, v2 :: v_dual_sub_nc_u32 v11, v3, v4", target = gfx1250, threads = 32)
    assertEquals(result.issued("v_dual").size, 1)
    assertEquals(planned(result, "v_dual"), 2L)
  }

  private def pipes(result: Result, prefix: String): Vector[Option[Pipe]] =
    result.issued(prefix).map(issue => result.stream.instructions(issue.instruction).pipe)

  test("a wave64 VALU fma reports the Fma pipe on gfx1030") {
    val result = run("v_fma_f32 v1, v0, v0, v0", target = gfx1030, wave64 = true)
    assertEquals(pipes(result, "v_fma_f32"), Vector(Some(Pipe.Fma)))
  }

  test("an f32 fma reports the Fma pipe on gfx906") {
    assertEquals(pipes(run("v_fma_f32 v1, v0, v0, v0"), "v_fma_f32"), Vector(Some(Pipe.Fma)))
  }

  test("an integer add reports the Alu pipe on gfx906") {
    assertEquals(pipes(run("v_add_u32 v1, v0, v0"), "v_add_u32"), Vector(Some(Pipe.Alu)))
  }

  test("a scalar add reports no pipe on gfx906") {
    assertEquals(pipes(run("s_add_u32 s4, s2, 1"), "s_add_u32"), Vector(None))
  }

  test("a reciprocal reports the Mufu pipe on gfx906") {
    assertEquals(pipes(run("v_rcp_f32 v1, v0"), "v_rcp_f32"), Vector(Some(Pipe.Mufu)))
  }

  test("an f64 add reports the Fp64 pipe on gfx906") {
    assertEquals(pipes(run("v_add_f64 v[1:2], v[0:1], v[0:1]"), "v_add_f64"), Vector(Some(Pipe.Fp64)))
  }

  test("a matrix multiply reports the Tensor pipe on gfx90a") {
    val result = run("v_mfma_f32_32x32x1f32 v[0:31], v0, v1, v[0:31]", target = gfx90a)
    assertEquals(pipes(result, "v_mfma"), Vector(Some(Pipe.Tensor)))
  }
}
