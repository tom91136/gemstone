package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*
import scala.util.Using

class FaultLocationSuite extends munit.FunSuite {
  test("a faulting instruction reports its kernel, function, ordinal, workgroup and lane") {
    val source = """.version 8.0
                   |.target sm_80
                   |.address_size 64
                   |.visible .entry overrun(.param .u64 output) {
                   |  .reg .b32 %r;
                   |  .reg .b64 %rd;
                   |  ld.param.u64 %rd, [output];
                   |  mov.u32 %r, 42;
                   |  st.global.u32 [%rd], %r;
                   |  ret;
                   |}
                   |""".stripMargin
    val memory = ArrayMemory()
    val output = memory.allocate(2L)
    Using.resource(PTXBackend().load(Vec.from(source.getBytes(StandardCharsets.UTF_8))).value) { program =>
      val task =
        KernelTask.validated(
          "overrun",
          Dimensions.One,
          Dimensions.One,
          Vec(KernelTask.Argument.Memory(output, 0L))
        ).value
      assertEquals(
        program.execute(task, memory, TaskControl(), _ => Right(())).left.map(_.location),
        Left(Some(Fault.Location("overrun", Some("overrun"), Some(2L), Some(Coordinates.origin), Some(0))))
      )
    }
  }
}
