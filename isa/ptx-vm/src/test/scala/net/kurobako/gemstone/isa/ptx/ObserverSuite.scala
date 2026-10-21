package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.collection.mutable
import scala.util.Using

class ObserverSuite extends munit.FunSuite {
  private val source = """.version 8.0
                         |.target sm_80
                         |.address_size 64
                         |.visible .entry store(.param .u64 output) {
                         |  .reg .b32 %r;
                         |  .reg .b64 %rd;
                         |  ld.param.u64 %rd, [output];
                         |  mov.u32 %r, 42;
                         |  st.global.u32 [%rd], %r;
                         |  ret;
                         |}
                         |""".stripMargin

  test("an observer sees each PTX instruction ordinal with the cohort executing it") {
    val memory = ArrayMemory()
    val output = memory.allocate(8L)
    Using.resource(PTXBackend().load(Vec.from(source.getBytes(StandardCharsets.UTF_8))).value) { program =>
      val task =
        KernelTask.validated(
          "store",
          Dimensions(2, 1, 1),
          Dimensions.One,
          Vec(KernelTask.Argument.Memory(output, 0L))
        ).value
      val seen                     = mutable.ArrayBuffer.empty[(Long, Vec[Int])]
      val observer: LaunchObserver = (at, state) =>
        state match {
          case cohort: CohortView =>
            seen += ((at.instruction.fold(-1L)(_.raw), cohort.lanes.map(_.raw)))
            Right(())
          case other => Left(Fault.InvalidRequest(s"unexpected state $other"))
        }
      assertEquals(
        program.execute(task, TaskContext(memory, TaskControl(observer = Some(observer)), _ => Right(()))),
        Right(())
      )
      assertEquals(seen.map(_._1).distinct.toVector, Vector(0L, 1L, 2L, 3L))
      val lanes = Vec(0, 1)
      assert(seen.forall(_._2 == lanes), seen)
    }
  }
}
