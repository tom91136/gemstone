package net.kurobako.gemstone.isa.ptx

import execution.*
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.VMIR.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.vm.*

class IndexedBranchSuite extends Suite {
  private def switchSource(arms: Int, guard: String = "", select: Option[Long] = None): String = {
    val labels = (0 until arms).map(index => s"ARM$index").mkString(", ")
    val bodies = (0 until arms).map(index => s"ARM$index: add.u32 %v, %v, ${index + 1}; bra JOIN;").mkString("\n")
    s""".version 8.0
       |.target sm_80
       |.address_size 64
       |.visible .entry switch(.param .u64 output) {
       |  .reg .u32 %t, %i, %v;
       |  .reg .u64 %out, %offset;
       |  .reg .pred %p;
       |  ld.param.u64 %out, [output];
       |  mov.u32 %t, %tid.x;
       |  ${select.fold(s"rem.u32 %i, %t, ${arms + 1};")(index => s"mov.u32 %i, $index;")}
       |  mov.u32 %v, 100;
       |  setp.ne.u32 %p, %i, 1;
       |  table: .branchtargets $labels;
       |  $guard brx.idx %i, table;
       |  add.u32 %v, %v, 1000;
       |  bra JOIN;
       |$bodies
       |JOIN:
       |  mul.wide.u32 %offset, %t, 4;
       |  add.u64 %out, %out, %offset;
       |  st.global.u32 [%out], %v;
       |  ret;
       |}""".stripMargin
  }

  private def run(source: String, threads: Int, observer: Option[LaunchObserver] = None): Vec[Int] = {
    val kernel = compileKernel(source)
    val memory = DeviceMemory((threads * 4).unsafe[Int >= 0])
    val result = execute(
      kernel,
      1,
      threads.unsafe[Int > 0],
      Map(("output": NonEmpty[String]) -> Argument.fromLong(memory.start.raw)),
      memory,
      TaskControl(observer = observer)
    )
    assertEquals(result, Right(()))
    memory.ints(from = 0, count = threads)
  }

  private def steps(source: String): Int = {
    val count                    = AtomicInteger()
    val observer: LaunchObserver = (_, _) => {
      count.incrementAndGet()
      Right(())
    }
    val _ = run(source, 1, Some(observer))
    count.get
  }

  test("lanes take the arm their index selects and an index past the table falls through") {
    val arms = 4
    val seen = run(switchSource(arms), 64)
    assertEquals(
      seen,
      Vec.tabulate(64)(lane => if (lane % (arms + 1) < arms) 100 + lane % (arms + 1) + 1 else 1100)
    )
  }

  test("a table may name one label more than once") {
    val source =
      switchSource(3).replace("table: .branchtargets ARM0, ARM1, ARM2;", "table: .branchtargets ARM2, ARM1, ARM2;")
    val seen = run(source, 8)
    assertEquals(seen, Vec.tabulate(8)(lane => if (lane % 4 == 1) 102 else if (lane % 4 == 3) 1100 else 103))
  }

  test("a predicated jump sends only the lanes whose predicate holds through the table") {
    val seen = run(switchSource(3, guard = "@%p"), 12)
    assertEquals(seen, Vec.tabulate(12)(lane => Vec(101, 1100, 103, 1100)(lane % 4)))
  }

  test("lanes that left through different arms meet again at a barrier") {
    val source = switchSource(4).replace("JOIN:\n", "JOIN:\n  bar.sync 0;\n")
    assertEquals(run(source, 64), run(switchSource(4), 64))
  }

  test("an index with its top bit set is past the table") {
    assertEquals(run(switchSource(4, select = Some(0xffffffffL)), 4), Vec.fill(4)(1100))
  }

  test("lanes that left through different arms meet again at a warp shuffle") {
    val source = switchSource(4).replace("JOIN:\n", "JOIN:\n  shfl.sync.idx.b32 %v, %v, 0, 31, 0xffffffff;\n")
    assertEquals(run(source, 32), Vec.fill(32)(101))
  }

  test("the steps to dispatch do not grow with the table") {
    val small = steps(switchSource(2, select = Some(1L)))
    val large = steps(switchSource(16, select = Some(15L)))
    assertEquals(large, small)
  }
}
