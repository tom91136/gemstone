package external

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.ptx.*
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXDeviceSuite extends Suite {
  test("an embedder runs PTX through a device with only the public API") {
    val source = """.version 8.0
                   |.target sm_80
                   |.address_size 64
                   |.visible .entry answer(.param .u64 output) {
                   |  .reg .b32 %r;
                   |  .reg .b64 %rd;
                   |  ld.param.u64 %rd, [output];
                   |  mov.u32 %r, 42;
                   |  st.global.u32 [%rd], %r;
                   |  ret;
                   |}
                   |""".stripMargin
    val memory = ArrayMemory()
    val output = memory.allocate(4L)
    val device = Device(BackendRegistry(Vec(PTXBackend())), memory)
    Using.resource(device.load(ProgramFormat.PTXText, Vec.from(source.getBytes(StandardCharsets.UTF_8))).value) {
      program =>
        val task = KernelTask.validated(
          "answer",
          LaunchGeometry.Dimensions.One,
          LaunchGeometry.Dimensions.One,
          Vec(KernelTask.Argument.Memory(output, 0L))
        ).value
        assertEquals(device.launch(program, task), Right(Vec.empty[Event]))
    }
    assertEquals(memory.getInt(output, 0), 42)
  }
}
