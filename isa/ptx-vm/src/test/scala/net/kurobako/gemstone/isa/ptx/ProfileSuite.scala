package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchObserver.*
import scala.collection.mutable

/** The facts a profiler reads from an observed launch: each issue's lanes and text, and each lane's accesses. */
class ProfileSuite extends Suite {
  private val base = RegionMemory.Base

  private case class Seen(lane: Int, space: Space, kind: AccessKind, address: Long, bytes: Int) derives CanEqual
  private case class Step(lane: Int, kind: AsyncKind, target: Long, bytes: Long) derives CanEqual

  private class Result(val stream: ProfileStream) extends ProfileStream.Visitor {
    val issues: mutable.ArrayBuffer[ProfileStream.Issue] = mutable.ArrayBuffer.empty
    val accesses: mutable.ArrayBuffer[Seen]              = mutable.ArrayBuffer.empty
    val steps: mutable.ArrayBuffer[Step]                 = mutable.ArrayBuffer.empty

    override def issue(issue: ProfileStream.Issue): Unit = issues += issue
    override def access(lane: Int >= 0, space: Space, kind: AccessKind, address: Long, bytes: Int >= 0): Unit =
      accesses += Seen(lane, space, kind, address, bytes)
    override def asynchronous(lane: Int >= 0, kind: AsyncKind, target: Long, bytes: Long): Unit =
      steps += Step(lane, kind, target, bytes)
    stream.foreach(this)

    def data: Vector[Seen] = accesses.toVector.filter(_.space ne Space.Constant)

    def laneIndices(issue: ProfileStream.Issue): Vector[Int] = issue.lanes.toSeq.map(_.raw).toVector

    def text(issue: ProfileStream.Issue): String = stream.instructions(issue.instruction).text

    def issued(prefix: String): Vector[ProfileStream.Issue] = issues.toVector.filter(text(_).startsWith(prefix))
  }

  private def run(
      body: String,
      threads: Int > 0 = 32,
      declarations: String = "",
      target: String = "sm_80",
      cluster: Option[LaunchGeometry.Dimensions] = None,
      dynamicShared: Int >= 0 = 0
  ): Result = {
    val kernel = compileKernel(s""".version 8.6
      |.target $target
      |.address_size 64
      |.visible .entry profile(.param .u64 buffer) {
      |  $declarations
      |  .reg .b32 %r<16>;
      |  .reg .b64 %rd<8>;
      |  .reg .pred %p<4>;
      |  ld.param.u64 %rd0, [buffer];
      |  mov.u32 %r0, %tid.x;
      |  $body
      |  ret;
      |}""".stripMargin)
    val memory   = DeviceMemory(16384)
    val recorder = ProfileRecorder()
    val result   = execute(
      kernel,
      1,
      threads,
      Map(("buffer": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      control = TaskControl(observer = Some(recorder)),
      cluster = cluster,
      dynamicSharedBytes = dynamicShared
    )
    assertEquals(result, Right(()))
    Result(recorder.stream)
  }

  private def every(count: Int)(make: Int => Seen): Vector[Seen] = Vector.tabulate(count)(make)

  test("a coalesced global load reports consecutive addresses for every lane") {
    val result = run("""mul.wide.u32 %rd1, %r0, 4;
                       |add.u64 %rd2, %rd0, %rd1;
                       |ld.global.u32 %r1, [%rd2];""".stripMargin)
    val load = result.issued("ld.global.u32").head
    assertEquals(result.text(load), "ld.global.u32 %r1, [%rd2];")
    assertEquals(load.subgroupSize.raw, 32)
    assertEquals(result.laneIndices(load), Vector.range(0, 32))
    assertEquals(result.data, every(32)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4)))
    assertEquals(result.steps.toVector, Vector.empty[Step])
  }

  test("a kernel parameter read is a constant read of the parameter block by every lane") {
    val result = run("")
    val reads  = result.accesses.toVector
    assertEquals(reads.map(_.lane), Vector.range(0, 32))
    assertEquals(
      reads.map(access => (access.space, access.kind, access.bytes)).distinct,
      Vector((Space.Constant, AccessKind.Read, 8))
    )
    assertEquals(reads.map(_.address).distinct.size, 1)
  }

  test("a kernel parameter read stays constant while a global load in the same kernel is global") {
    val result = run("""ld.param.u64 %rd5, [buffer];
                       |mul.wide.u32 %rd1, %r0, 4;
                       |add.u64 %rd2, %rd0, %rd1;
                       |ld.global.u32 %r1, [%rd2];""".stripMargin)
    assertEquals(result.data, every(32)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4)))
    assertEquals(
      result.accesses.toVector.collect { case access if access.space eq Space.Constant => access.kind }.distinct,
      Vector(AccessKind.Read)
    )
  }

  test("a generic store into a child parameter buffer records a global write") {
    val memory   = DeviceMemory(4)
    val recorder = ProfileRecorder()
    val source   = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.extern .func (.param .b64 buffer) cudaGetParameterBuffer(.param .b64 align, .param .b64 size);
      |.extern .func cudaLaunchDevice();
      |.visible .entry parent() {
      |  .reg .b64 %function, %buffer;
      |  .param .b64 align, size, result, target, pointer, stream;
      |  .param .b32 shared;
      |  .param .align 4 .b8 grid[12], block[12];
      |  st.param.b64 [align], 8;
      |  st.param.b64 [size], 16;
      |  call (result), cudaGetParameterBuffer, (align, size);
      |  ld.param.b64 %buffer, [result];
      |  st.u32 [%buffer+8], 42;
      |  mov.b64 %function, child;
      |  st.param.b64 [target], %function;
      |  st.param.b64 [pointer], %buffer;
      |  st.param.b64 [stream], 0;
      |  st.param.b32 [shared], 0;
      |  st.param.b32 [grid], 1;
      |  st.param.b32 [grid+4], 1;
      |  st.param.b32 [grid+8], 1;
      |  st.param.b32 [block], 1;
      |  st.param.b32 [block+4], 1;
      |  st.param.b32 [block+8], 1;
      |  call cudaLaunchDevice, (target, pointer, grid, block, shared, stream);
      |  ret;
      |}
      |.visible .entry child() {
      |  ret;
      |}""".stripMargin
    val result = execute(
      compileKernel(source),
      1,
      1,
      Map.empty,
      memory,
      control = TaskControl(observer = Some(recorder))
    )
    assertEquals(result, Right(()))
    val writes = Result(recorder.stream).accesses.toVector.filter(_.kind == AccessKind.Write)
    assertEquals(writes.filter(_.space eq Space.Constant), Vector.empty[Seen])
    assert(writes.exists(_.space eq Space.Global))
  }

  test("a strided global load reports the stride in each lane's address") {
    val result = run("""mul.wide.u32 %rd1, %r0, 128;
                       |add.u64 %rd2, %rd0, %rd1;
                       |ld.global.u32 %r1, [%rd2];""".stripMargin)
    assertEquals(
      result.data,
      every(32)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 128L, 4))
    )
  }

  test("a vector load reports one access of its full width per lane") {
    val result = run("""mul.wide.u32 %rd1, %r0, 16;
                       |add.u64 %rd2, %rd0, %rd1;
                       |ld.global.v4.u32 {%r1, %r2, %r3, %r4}, [%rd2];""".stripMargin)
    assertEquals(
      result.data,
      every(32)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 16L, 16))
    )
  }

  test("a global store reports writes") {
    val result = run("""mul.wide.u32 %rd1, %r0, 8;
                       |add.u64 %rd2, %rd0, %rd1;
                       |st.global.u64 [%rd2+8], %rd1;""".stripMargin)
    assertEquals(
      result.data,
      every(32)(lane => Seen(lane, Space.Global, AccessKind.Write, base + 8L + lane * 8L, 8))
    )
  }

  test("a shared store reports offsets within the workgroup's shared window") {
    val result = run(
      """mov.u32 %r2, smem;
        |shl.b32 %r3, %r0, 2;
        |add.u32 %r4, %r2, %r3;
        |st.shared.u32 [%r4+16], %r0;""".stripMargin,
      declarations = ".shared .align 4 .b8 smem[512];"
    )
    assertEquals(
      result.data,
      every(32)(lane => Seen(lane, Space.Shared, AccessKind.Write, 16L + lane * 4L, 4))
    )
  }

  test("a generic access reports the space its address selects") {
    val result = run(
      """mov.u32 %r2, smem;
        |cvta.shared.u64 %rd1, %r2;
        |cvta.to.global.u64 %rd2, %rd0;
        |mul.wide.u32 %rd3, %r0, 4;
        |add.u64 %rd4, %rd1, %rd3;
        |add.u64 %rd5, %rd2, %rd3;
        |st.u32 [%rd4+64], %r0;
        |ld.u32 %r1, [%rd5];""".stripMargin,
      declarations = ".shared .align 4 .b8 smem[512];"
    )
    assertEquals(
      result.data,
      every(32)(lane => Seen(lane, Space.Shared, AccessKind.Write, 64L + lane * 4L, 4)) ++
        every(32)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4))
    )
  }

  test("a local store reports a private address") {
    val result = run(
      """mov.u32 %r2, 7;
        |st.local.u32 [lbuf+8], %r2;
        |ld.local.u32 %r3, [lbuf+8];""".stripMargin,
      declarations = ".local .align 4 .b8 lbuf[32];"
    )
    val local = result.data
    assertEquals(local.map(_.space).distinct, Vector(Space.Local))
    assertEquals(local.map(_.bytes).distinct, Vector(4))
    assertEquals(local.map(_.address).distinct.size, 1)
    assertEquals(local.collect { case seen if seen.kind eq AccessKind.Write => seen.lane }, Vector.range(0, 32))
    assertEquals(local.collect { case seen if seen.kind eq AccessKind.Read => seen.lane }, Vector.range(0, 32))
  }

  test("a global atomic reports every lane as an atomic on one address") {
    val result = run("atom.global.add.u32 %r1, [%rd0], 1;")
    assertEquals(result.data, every(32)(lane => Seen(lane, Space.Global, AccessKind.Atomic, base, 4)))
  }

  test("a shared atomic reports the shared space") {
    val result = run(
      """mov.u32 %r2, smem;
        |atom.shared.add.u32 %r1, [%r2+8], 1;""".stripMargin,
      declarations = ".shared .align 4 .b8 smem[64];"
    )
    assertEquals(result.data, every(32)(lane => Seen(lane, Space.Shared, AccessKind.Atomic, 8L, 4)))
  }

  test("a compare and swap reports an atomic") {
    val result = run("atom.global.cas.b32 %r1, [%rd0], 0, 1;")
    assertEquals(result.data, every(32)(lane => Seen(lane, Space.Global, AccessKind.Atomic, base, 4)))
  }

  test("the arms of a divergent branch issue for their own lanes") {
    val result = run("""setp.lt.u32 %p0, %r0, 8;
                       |@%p0 bra TAKEN;
                       |st.global.u32 [%rd0], %r0;
                       |bra DONE;
                       |TAKEN:
                       |st.global.u32 [%rd0+4], %r0;
                       |DONE:""".stripMargin)
    val stores = result.issued("st.global.u32").map(result.laneIndices(_)).toSet
    assertEquals(stores, Set(Vector.range(0, 8), Vector.range(8, 32)))
    val accesses = result.data.sortBy(_.lane)
    assertEquals(accesses.take(8), every(8)(lane => Seen(lane, Space.Global, AccessKind.Write, base + 4L, 4)))
    assertEquals(
      accesses.drop(8),
      (8 until 32).toVector.map(lane => Seen(lane, Space.Global, AccessKind.Write, base, 4))
    )
  }

  private val rejoining = """setp.lt.u32 %p0, %r0, 8;
                            |@%p0 bra TAKEN;
                            |st.global.u32 [%rd0], %r0;
                            |bra DONE;
                            |TAKEN:
                            |st.global.u32 [%rd0+4], %r0;
                            |DONE:
                            |st.global.u32 [%rd0+8], %r0;""".stripMargin

  test("lanes rejoin after a divergent branch, so the code after it issues once for the warp") {
    val stores = run(rejoining).issued("st.global.u32").map(_.lanes.size)
    assertEquals(stores.init.toSet, Set(8, 24))
    assertEquals(stores.size, 3)
    assertEquals(stores.last, 32)
  }

  test("the barriers a divergent branch adds are not counted as issued by the hardware") {
    val result = run(rejoining)
    val added  = result.issues.filter(issue => result.text(issue).startsWith("Convergence("))
    assert(added.nonEmpty)
    assert(added.forall(!_.begins))
    assertEquals(Profiler.run(result.stream, Architecture.sm89).total.issues, 9L)
  }

  test("a predicated access reports only the lanes whose predicate holds") {
    val result = run("""setp.ge.u32 %p0, %r0, 24;
                       |mul.wide.u32 %rd1, %r0, 4;
                       |add.u64 %rd2, %rd0, %rd1;
                       |@%p0 ld.global.u32 %r1, [%rd2];""".stripMargin)
    val load = result.issued("@").head
    assertEquals(result.laneIndices(load), Vector.range(24, 32))
    assertEquals(
      result.data,
      (24 until 32).toVector.map(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4))
    )
  }

  test("lanes of several warps are indexed within the workgroup") {
    val result = run(
      """mul.wide.u32 %rd1, %r0, 4;
                       |add.u64 %rd2, %rd0, %rd1;
                       |ld.global.u32 %r1, [%rd2];""".stripMargin,
      threads = 96
    )
    val load = result.issued("ld.global.u32").head
    assertEquals(result.laneIndices(load), Vector.range(0, 96))
    assertEquals(result.data, every(96)(lane => Seen(lane, Space.Global, AccessKind.Read, base + lane * 4L, 4)))
  }

  test("an async copy reports its reads, writes and transfer for each lane") {
    val result = run(
      """mul.wide.u32 %rd1, %r0, 16;
        |add.u64 %rd2, %rd0, %rd1;
        |mov.u32 %r2, smem;
        |shl.b32 %r3, %r0, 4;
        |add.u32 %r4, %r2, %r3;
        |cp.async.ca.shared.global [%r4], [%rd2], 16;
        |cp.async.commit_group;
        |cp.async.wait_all;""".stripMargin,
      declarations = ".shared .align 16 .b8 smem[512];"
    )
    val perLane = result.data.groupBy(_.lane)
    assertEquals(perLane.keySet, (0 until 32).toSet)
    for ((lane, accesses) <- perLane)
      assertEquals(
        accesses,
        Vector(
          Seen(lane, Space.Global, AccessKind.Read, base + lane * 16L, 16),
          Seen(lane, Space.Shared, AccessKind.Write, lane * 16L, 16)
        )
      )
    assertEquals(
      result.steps.toVector.sortBy(_.lane),
      Vector.tabulate(32)(lane =>
        Vector(
          Step(lane, AsyncKind.Transfer, target = DefaultTarget, bytes = 16L),
          Step(lane, AsyncKind.Commit, target = 0L, bytes = 0L),
          Step(lane, AsyncKind.WaitGroups, target = 0L, bytes = 0L)
        )
      ).flatten
    )
  }

  test("a bulk copy reports its transfer on the mbarrier it completes, and the arrival and waits on that barrier") {
    val result = run(
      """setp.eq.u32 %p0, %r0, 0;
        |@%p0 mbarrier.init.shared::cta.b64 [bar], 1;
        |fence.mbarrier_init.release.cluster;
        |bar.sync 0;
        |@!%p0 bra LOADED;
        |cp.async.bulk.shared::cluster.global.mbarrier::complete_tx::bytes [a], [%rd0], 256, [bar];
        |mbarrier.arrive.expect_tx.release.cta.shared::cta.b64 %token, [bar], 256;
        |LOADED:
        |mbarrier.try_wait.parity.acquire.cta.shared::cta.b64 %p1, [bar], 0, 1000;
        |@!%p1 bra LOADED;""".stripMargin,
      threads = 64,
      declarations = ".shared .align 128 .b8 a[256];\n.shared .align 8 .b64 bar;\n.reg .u64 %token;",
      target = "sm_90"
    )
    assertEquals(
      result.data,
      Vector(
        Seen(0, Space.Global, AccessKind.Read, base, 256),
        Seen(0, Space.Shared, AccessKind.Write, 0L, 256)
      )
    )
    val barrier = 256L
    assertEquals(
      result.steps.toVector.filter(_.kind ne AsyncKind.Wait),
      Vector(Step(0, AsyncKind.Transfer, barrier, 256L), Step(0, AsyncKind.Arrive, barrier, 256L))
    )
    val waits = result.steps.toVector.filter(_.kind eq AsyncKind.Wait)
    assertEquals(waits.map(_.lane).toSet, (0 until 64).toSet)
    assertEquals(waits.map(step => (step.target, step.bytes)).distinct, Vector((barrier, 0L)))
  }

  test("cp.async groups commit and wait by count, bulk groups in their own class") {
    val result = run(
      """mov.u32 %r2, smem;
        |cp.async.commit_group;
        |cp.async.wait_group 1;
        |cp.async.bulk.commit_group;
        |cp.async.bulk.wait_group 0;
        |cp.async.wait_all;""".stripMargin,
      declarations = ".shared .align 16 .b8 smem[512];",
      target = "sm_90"
    )
    assertEquals(
      result.steps.toVector.filter(_.lane == 0),
      Vector(
        Step(0, AsyncKind.Commit, target = 0L, bytes = 0L),
        Step(0, AsyncKind.WaitGroups, target = 0L, bytes = 1L),
        Step(0, AsyncKind.Commit, target = 1L, bytes = 0L),
        Step(0, AsyncKind.WaitGroups, target = 1L, bytes = 0L),
        Step(0, AsyncKind.WaitGroups, target = 0L, bytes = 0L)
      )
    )
    assertEquals(result.steps.size, 32 * 5)
  }

  test("mbarrier expect_tx and complete_tx report their bytes on the barrier") {
    val result = run(
      """setp.eq.u32 %p0, %r0, 0;
        |@%p0 mbarrier.init.shared::cta.b64 [bar], 1;
        |fence.mbarrier_init.release.cluster;
        |bar.sync 0;
        |@%p0 mbarrier.expect_tx.relaxed.cta.shared::cta.b64 [bar], 128;
        |@%p0 mbarrier.complete_tx.relaxed.cta.shared::cta.b64 [bar], 64;""".stripMargin,
      threads = 64,
      declarations = ".shared .align 8 .b64 bar;",
      target = "sm_90"
    )
    assertEquals(
      result.steps.toVector.filter(_.kind ne AsyncKind.Wait),
      Vector(
        Step(0, AsyncKind.Expect, target = 0L, bytes = 128L),
        Step(0, AsyncKind.Complete, target = 0L, bytes = 64L)
      )
    )
  }

  test("a PTX launch reports static and dynamic shared bytes with no registers") {
    val result = run("", declarations = ".shared .align 4 .b8 smem[512];", dynamicShared = 256)
    assertEquals(result.stream.resources, Some(KernelResources(32, 32, None, 768L)))
    assert(Profiler.run(result.stream, Architecture.sm89).occupancy.isDefined)
  }

  test("register uses carry the declared width: 64-bit registers take two units, predicates none") {
    val result = run("setp.eq.u32 %p0, %r0, 0;\n  mov.u64 %rd1, %rd0;\n  add.u32 %r1, %r0, 1;")
    val units  = result.stream.instructions.flatMap(_.use.units.toVector).toMap
    assertEquals(units, Map("%rd0" -> 2, "%rd1" -> 2, "%p0" -> 0))
    assert(Profiler.run(result.stream, Architecture.sm89).pressure.exists(_.peak >= 4))
  }

  test("each PTX instruction reports the pipe that executes it") {
    val result = run(
      """add.f32 %f1, %f2, %f3;
        |fma.rn.f32 %f4, %f1, %f2, %f3;
        |mul.lo.s32 %r4, %r0, %r0;
        |add.s32 %r5, %r0, 1;
        |add.f64 %d1, %d2, %d3;
        |ex2.approx.f32 %f5, %f2;
        |ld.global.u32 %r7, [%rd0];""".stripMargin,
      declarations = ".reg .f32 %f<8>;\n  .reg .f64 %d<4>;"
    )
    assertEquals(
      result.stream.instructions.map(instruction => (instruction.text, instruction.pipe)).toVector,
      Vector[(String, Option[Pipe])](
        ("ld.param.u64 %rd0, [buffer];", None),
        ("mov.u32 %r0, %tid.x;", None),
        ("add.f32 %f1, %f2, %f3;", Some(Pipe.Fma)),
        ("fma.rn.f32 %f4, %f1, %f2, %f3;", Some(Pipe.Fma)),
        ("mul.lo.s32 %r4, %r0, %r0;", Some(Pipe.Fma)),
        ("add.s32 %r5, %r0, 1;", Some(Pipe.Alu)),
        ("add.f64 %d1, %d2, %d3;", Some(Pipe.Fp64)),
        ("ex2.approx.f32 %f5, %f2;", Some(Pipe.Mufu)),
        ("ld.global.u32 %r7, [%rd0];", None),
        ("ret;", None)
      )
    )
  }
}
