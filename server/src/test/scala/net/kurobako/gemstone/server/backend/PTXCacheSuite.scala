package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXCacheSuite extends munit.FunSuite {
  private val source =
    """|.version 8.0
       |.target sm_80
       |.address_size 64
       |.global .align 4 .u32 shared_value;
       |.visible .entry read_global(.param .u64 output) {
       |  .reg .b64 %rd;
       |  .reg .b32 %r;
       |  ld.param.u64 %rd, [output];
       |  ld.global.u32 %r, [shared_value];
       |  st.global.u32 [%rd], %r;
       |  ret;
       |}
       |""".stripMargin
  private val image = Vec.from(source.getBytes(StandardCharsets.UTF_8))

  test("concurrent identical PTX loads keep bindings and runtime lifetime independent") {
    val backend  = PTX()
    val executor = Executors.newFixedThreadPool(2)
    try
      Using.Manager { use =>
        val firstSession  = use(ExecutionSession(List(backend)))
        val secondSession = use(ExecutionSession(List(backend)))
        val firstLoad     = executor.submit(() => firstSession.load(PTX.encoding, image))
        val secondLoad    = executor.submit(() => secondSession.load(PTX.encoding, image))
        val first         = firstLoad.get().fold(error => fail(error.message), identity)
        val second        = secondLoad.get().fold(error => fail(error.message), identity)

        assert(firstSession.load(PTX.encoding, Vec.from(".entry {".getBytes(StandardCharsets.UTF_8))).isLeft)
        val oversizedGlobal =
          ".version 8.0\n.target sm_80\n.address_size 64\n.global .u32 huge[2147483647];"
        assert(firstSession.load(
          PTX.encoding,
          Vec.from(oversizedGlobal.getBytes(StandardCharsets.UTF_8))
        ).isLeft)

        val firstGlobal  = firstSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val secondGlobal = secondSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val firstOutput  = firstSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        val secondOutput = secondSession.registerAllocation(4L, AllocationKind.Shared).toOption.get
        assertEquals(firstSession.write(firstGlobal, 0L, LittleEndian.toVec(17, 4)), Right(()))
        assertEquals(secondSession.write(secondGlobal, 0L, LittleEndian.toVec(29, 4)), Right(()))
        assertEquals(firstSession.bindGlobal(first, "shared_value", firstGlobal, 0L), Right(()))
        assertEquals(secondSession.bindGlobal(second, "shared_value", secondGlobal, 0L), Right(()))

        def launch(session: ExecutionSession, program: ProgramId, output: AllocationId): Unit = {
          val task = KernelTask.validated(
            "read_global",
            LaunchGeometry.Dimensions.One,
            LaunchGeometry.Dimensions.One,
            Vec(KernelTask.Argument.Memory(output, 0L))
          ).fold(message => fail(message), identity)
          val completion = session.launch(program, task).toOption.get
          assertEquals(session.completion(completion), Right(CompletionStatus.Succeeded))
        }

        launch(firstSession, first, firstOutput)
        assertEquals(firstSession.read(firstOutput, 0L, 4).map(LittleEndian.decode(_, 0, 4)), Right(17L))
        assertEquals(firstSession.unload(first), Right(()))
        launch(secondSession, second, secondOutput)
        assertEquals(secondSession.read(secondOutput, 0L, 4).map(LittleEndian.decode(_, 0, 4)), Right(29L))
      }.get
    finally {
      val _ = executor.shutdownNow()
    }
  }
}
