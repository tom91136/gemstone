package net.kurobako.gemstone.isa.ptx

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXBackendSuite extends munit.FunSuite {
  test("loads and executes PTX through the typed runtime boundary") {
    val source  = ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry empty() { ret; }\n"
    val program = PTXBackend()
      .load(
        Vec.from(source.getBytes(StandardCharsets.UTF_8))
      )
      .fold(error => fail(error.message), value => value)

    assertEquals(program.symbols.map(_.kernels.map(_.name)), Right(Vec[NonEmpty[String]]("empty")))
    assert(program.kernel("empty").isRight)

    val task = KernelTask.validated(
      "empty",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).value
    program.execute(task, RejectingMemory(), TaskControl(), _ => Right(()))
      .fold(error => fail(error.message), value => value)

    val cancelled = TaskControl()
    cancelled.cancel()
    assertEquals(program.execute(task, RejectingMemory(), cancelled, _ => Right(())), Left(Fault.Cancelled))
    assertEquals(program.global("unused").left.map(_.message: String), Left("PTX global 'unused' is not present"))
    program.close()
  }

  private def loadModule(version: String, target: String, instruction: String) = PTXBackend().load(Vec.from(
    s""".version $version
       |.target $target
       |.address_size 64
       |.visible .entry newer() {
       |  .reg .b64 %rd<4>;
       |  .reg .b32 %r<4>;
       |  $instruction
       |  ret;
       |}
       |""".stripMargin.getBytes(StandardCharsets.UTF_8)
  ))

  test("modules using instructions newer than their declared version or target are rejected at load") {
    def load(version: String, target: String) = loadModule(version, target, "clmad.lo.u64 %rd0, %rd1, %rd2, %rd3;")
    for ((version, target, required) <- Vec(("9.2", "sm_100", "PTX ISA 9.3"), ("9.3", "sm_70", "sm_80")))
      load(version, target) match {
        case Left(Fault.InvalidRequest(detail)) => assert(detail.raw.contains(required), detail)
        case other                              => fail(s"expected rejection, found $other")
      }
    load("9.3", "sm_100").fold(error => fail(error.message), _.close())
  }

  test("toolchain modifier orders load") {
    for (
      source <- Vec(
        "ldmatrix.sync.aligned.x4.m8n8.shared.b16 {%r0, %r1, %r2, %r3}, [%rd0];",
        "ldmatrix.sync.aligned.x4.trans.m8n8.shared.b16 {%r0, %r1, %r2, %r3}, [%rd0];",
        "ldmatrix.sync.aligned.m8n8.x4.shared.b16 {%r0, %r1, %r2, %r3}, [%rd0];",
        "bfi.b32 %r0, %r1, %r2, 0, 8;",
        "redux.sync.add.s32 %r0, %r1, -1;",
        "mad.lo.cc.u32 %r0, %r1, %r2, %r3;"
      )
    ) loadModule("9.3", "sm_100", source).fold(error => fail(s"$source: ${error.message}"), _.close())
  }

  test("compiler output outside the documented syntax forms loads") {
    for (
      source <- Vec(
        "mov.v2.u64 %rd0, { 0, 0 };",
        "mov.v2.u32 %rd0, { 0, -1 };",
        "fence.proxy.async.shared::cta;"
      )
    ) loadModule("9.3", "sm_100", source).fold(error => fail(s"$source: ${error.message}"), _.close())
  }

  test("two arguments for the same allocation preserve pointer identity") {
    val source =
      """|.version 8.0
         |.target sm_80
         |.address_size 64
         |.visible .entry aliases(
         |  .param .u64 left,
         |  .param .u64 right,
         |  .param .u64 output
         |) {
         |  .reg .pred %p;
         |  .reg .b32 %r;
         |  .reg .b64 %rd<3>;
         |  ld.param.u64 %rd0, [left];
         |  ld.param.u64 %rd1, [right];
         |  ld.param.u64 %rd2, [output];
         |  setp.eq.u64 %p, %rd0, %rd1;
         |  @%p bra same;
         |  mov.u32 %r, 0;
         |  bra store;
         |same:
         |  mov.u32 %r, 1;
         |store:
         |  st.global.u32 [%rd2], %r;
         |  ret;
         |}
         |""".stripMargin
    Using.resource(ExecutionSession(List(PTXBackend()))) { service =>
      val allocation = service.registerAllocation(16).value
      val output     = service.registerAllocation(4).value
      val program    =
        service.load(ProgramEncoding.PTXText, Vec.from(source.getBytes(StandardCharsets.UTF_8))).value
      val task = KernelTask.validated(
        "aliases",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Memory(output, 0L)
        )
      ).value

      val completion = service.launch(program, task).value
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(
        service.read(output, 0L, 4).map(bytes =>
          LittleEndian.decode(bytes, 0, 4)
        ),
        Right(1L)
      )
    }
  }
}
