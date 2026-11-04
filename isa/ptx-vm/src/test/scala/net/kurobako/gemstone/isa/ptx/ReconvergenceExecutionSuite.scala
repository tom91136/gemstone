package net.kurobako.gemstone.isa.ptx

import execution.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.profile.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class ReconvergenceExecutionSuite extends Suite {
  private def run(
      body: String,
      threads: Int,
      words: Int,
      registers: String = "",
      recorder: Option[ProfileRecorder] = None
  ): Vec[Int] = {
    val kernel = compileKernel(s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry probe(.param .u64 data) {
      |  .reg .u64 %base, %address;
      |  .reg .u32 %t, %i, %v, %w, %old, %sum;
      |  .reg .pred %p, %q, %skip, %more;
      |  $registers
      |  ld.param.u64 %base, [data];
      |  mov.u32 %t, %tid.x;
      |  mul.wide.u32 %address, %t, 4;
      |  add.u64 %address, %address, %base;
      |  $body
      |  ret;
      |}""".stripMargin)
    val memory = DeviceMemory((words * 4).unsafe[Int >= 0])
    val result = execute(
      kernel,
      1,
      threads.unsafe[Int > 0],
      Map(("data": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      control = TaskControl(observer = recorder)
    )
    assertEquals(result, Right(()))
    memory.ints(from = 0, count = words)
  }

  private def spinLock(threads: Int): Vec[Int] = run(
    """SPIN:
      |atom.global.cas.b32 %old, [%base], 0, 1;
      |setp.ne.u32 %p, %old, 0;
      |@%p bra SPIN;
      |ld.global.u32 %sum, [%base+4];
      |add.u32 %sum, %sum, %t;
      |st.global.u32 [%base+4], %sum;
      |atom.global.exch.b32 %old, [%base], 0;""".stripMargin,
    threads = threads,
    words = 2
  )

  test("32 lanes of one warp take turns at a spin lock") {
    assertEquals(spinLock(32), Vec(0, 496))
  }

  test("two warps take turns at a spin lock") {
    assertEquals(spinLock(64), Vec(0, 2016))
  }

  test("lanes that skip a barrier's setup pass its synchronise at once") {
    val seen = run(
      """setp.lt.u32 %skip, %t, 16;
        |mov.u32 %v, 10;
        |@%skip bra.uni JOIN;
        |and.b32 %w, %t, 1;
        |setp.eq.u32 %p, %w, 0;
        |@%p bra EVEN;
        |add.u32 %v, %v, 1;
        |bra JOIN;
        |EVEN:
        |add.u32 %v, %v, 2;
        |JOIN:
        |st.global.u32 [%address], %v;""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(seen, Vec.tabulate(32)(lane => if (lane < 16) 10 else if (lane % 2 == 0) 12 else 11))
  }

  test("lanes holding a completed barrier's token pass its synchronise at once") {
    val seen = run(
      """mov.u32 %i, 0;
        |mov.u32 %v, 0;
        |LOOP:
        |setp.eq.u32 %p, %i, 1;
        |setp.lt.u32 %q, %t, 16;
        |and.pred %skip, %p, %q;
        |@%skip bra.uni JOIN;
        |and.b32 %w, %t, 1;
        |setp.eq.u32 %p, %w, 0;
        |@%p bra EVEN;
        |add.u32 %v, %v, 1;
        |bra JOIN;
        |EVEN:
        |add.u32 %v, %v, 2;
        |JOIN:
        |add.u32 %i, %i, 1;
        |setp.lt.u32 %more, %i, 3;
        |@%more bra.uni LOOP;
        |st.global.u32 [%address], %v;""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(
      seen,
      Vec.tabulate(32)(lane => (if (lane % 2 == 0) 2 else 1) * (if (lane < 16) 2 else 3))
    )
  }

  test("lanes that exit early leave the rest to synchronise and shuffle among themselves") {
    val seen = run(
      """setp.lt.u32 %p, %t, 8;
        |@%p bra DONE;
        |bar.warp.sync -1;
        |mul.lo.u32 %v, %t, 7;
        |shfl.sync.idx.b32 %w, %v, 8, 31, -1;
        |xor.b32 %w, %w, %t;
        |st.global.u32 [%address], %w;
        |DONE:""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(seen, Vec.tabulate(32)(lane => if (lane < 8) 0 else lane ^ 56))
  }

  test("lanes that exit late leave the rest to synchronise and shuffle among themselves") {
    val seen = run(
      """setp.lt.u32 %p, %t, 8;
        |@%p bra WORK;
        |bar.warp.sync -1;
        |mul.lo.u32 %v, %t, 7;
        |shfl.sync.idx.b32 %w, %v, 8, 31, -1;
        |xor.b32 %w, %w, %t;
        |st.global.u32 [%address], %w;
        |bra DONE;
        |WORK:
        |mov.u32 %i, 0;
        |SPIN:
        |add.u32 %i, %i, 1;
        |setp.lt.u32 %q, %i, 50;
        |@%q bra.uni SPIN;
        |DONE:""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(seen, Vec.tabulate(32)(lane => if (lane < 8) 0 else lane ^ 56))
  }

  test("lanes that leave a loop on different iterations rejoin after it") {
    val seen = run(
      """mov.u32 %i, 0;
        |LOOP:
        |add.u32 %i, %i, 1;
        |setp.lt.u32 %p, %i, %t;
        |@%p bra LOOP;
        |st.global.u32 [%address], %i;""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(seen, Vec.tabulate(32)(lane => lane.max(1)))
  }

  test("a loop that breaks out on one of two conditions runs every lane to its own exit") {
    val seen = run(
      """mov.u32 %i, 0;
        |mov.u32 %v, 0;
        |LOOP:
        |setp.eq.u32 %p, %i, %t;
        |@%p bra DONE;
        |setp.eq.u32 %q, %i, 20;
        |@%q bra DONE;
        |add.u32 %v, %v, 3;
        |add.u32 %i, %i, 1;
        |bra.uni LOOP;
        |DONE:
        |st.global.u32 [%address], %v;""".stripMargin,
      threads = 32,
      words = 32
    )
    assertEquals(seen, Vec.tabulate(32)(lane => 3 * lane.min(20)))
  }

  test("lanes that leave a bottom-test loop after different trip counts rejoin after it") {
    val recorder = ProfileRecorder()
    val seen     = run(
      """and.b32 %w, %t, 3;
        |mov.u32 %i, 0;
        |mov.u32 %v, 0;
        |bra.uni TEST;
        |BODY:
        |add.u32 %i, %i, 1;
        |add.u32 %v, %v, 5;
        |TEST:
        |setp.lt.u32 %p, %i, %w;
        |@%p bra BODY;
        |mul.lo.u32 %sum, %v, 7;
        |st.global.u32 [%address], %sum;""".stripMargin,
      threads = 32,
      words = 32,
      recorder = Some(recorder)
    )
    assertEquals(seen, Vec.tabulate(32)(lane => (lane & 3) * 35))
    val stream = recorder.stream
    val issues = Vector.newBuilder[(String, Int)]
    stream.foreach(new ProfileStream.Visitor {
      override def issue(issue: ProfileStream.Issue): Unit =
        issues += stream.instructions(issue.instruction).text -> issue.lanes.toSeq.size
    })
    val after = issues.result().filter(_._1.startsWith("mul.lo.u32"))
    assertEquals(after.map(_._2), Vector(32))
  }

  test("lanes take turns at a spin lock in a bottom-test loop") {
    val seen = run(
      """bra.uni TEST;
        |SPIN:
        |atom.global.exch.b32 %old, [%base], 0;
        |TEST:
        |atom.global.cas.b32 %old, [%base], 0, 1;
        |setp.ne.u32 %p, %old, 0;
        |@%p bra SPIN;
        |ld.global.u32 %sum, [%base+4];
        |add.u32 %sum, %sum, %t;
        |st.global.u32 [%base+4], %sum;
        |atom.global.exch.b32 %old, [%base], 0;""".stripMargin,
      threads = 32,
      words = 2
    )
    assertEquals(seen, Vec(0, 496))
  }

  test("sequential and nested branches rejoin with every lane and share token slots") {
    val recorder = ProfileRecorder()
    val body     = """mov.u32 %v, 0;
      |and.b32 %w, %t, 1;
      |setp.eq.u32 %p, %w, 0;
      |@%p bra J0;
      |add.u32 %v, %v, 1;
      |J0:
      |add.u32 %v, %v, 100;
      |and.b32 %w, %t, 2;
      |setp.eq.u32 %p, %w, 0;
      |@%p bra J1;
      |add.u32 %v, %v, 2;
      |J1:
      |add.u32 %v, %v, 200;
      |and.b32 %w, %t, 4;
      |setp.eq.u32 %p, %w, 0;
      |@%p bra J2;
      |and.b32 %w, %t, 8;
      |setp.eq.u32 %q, %w, 0;
      |@%q bra J3;
      |add.u32 %v, %v, 4;
      |J3:
      |add.u32 %v, %v, 300;
      |J2:
      |add.u32 %v, %v, 400;
      |st.global.u32 [%address], %v;""".stripMargin
    val kernel = compileKernel(s""".version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .entry probe(.param .u64 data) {
      |  .reg .u64 %base, %address;
      |  .reg .u32 %t, %v, %w;
      |  .reg .pred %p, %q;
      |  ld.param.u64 %base, [data];
      |  mov.u32 %t, %tid.x;
      |  mul.wide.u32 %address, %t, 4;
      |  add.u64 %address, %address, %base;
      |  $body
      |  ret;
      |}""".stripMargin)
    val tokens = kernel.entry.routine.registerNames.count(_.raw.startsWith("convergence token"))
    assertEquals(tokens, 2)
    val seen                     = run(body, threads = 32, words = 32, recorder = Some(recorder))
    def expected(lane: Int): Int = {
      def bit(mask: Int, add: Int) = if ((lane & mask) != 0) add else 0
      bit(1, 1) + 100 + bit(2, 2) + 200 + (if ((lane & 4) != 0) bit(8, 4) + 300 else 0) + 400
    }
    assertEquals(seen, Vec.tabulate(32)(expected))
    val stream = recorder.stream
    val issues = Vector.newBuilder[(String, Int)]
    stream.foreach(new ProfileStream.Visitor {
      override def issue(issue: ProfileStream.Issue): Unit =
        issues += stream.instructions(issue.instruction).text -> issue.lanes.toSeq.size
    })
    val after = issues.result().filter(_._1.contains(", 100;")).map(_._2) ++
      issues.result().filter(_._1.contains(", 200;")).map(_._2) ++
      issues.result().filter(_._1.contains(", 400;")).map(_._2)
    assertEquals(after, Vector(32, 32, 32))
  }
}
