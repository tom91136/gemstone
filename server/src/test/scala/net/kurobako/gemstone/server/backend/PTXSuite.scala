package net.kurobako.gemstone.server.backend

import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class PTXSuite extends munit.FunSuite {
  private object EmptyMemory extends AddressSpace {
    override def address(
        region: AllocationId,
        offset: Long >= 0
    ): Either[Fault, GuestAddress] = Left(Fault.Memory("unexpected address lookup"))

    override def locate(
        address: GuestAddress,
        size: Int >= 0
    ): Either[Fault, AddressSpace.Location] = Left(Fault.Memory("unexpected address lookup"))

    override def read(
        region: AllocationId,
        offset: Long >= 0,
        size: Int >= 0
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: AllocationId,
        offset: Long >= 0,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Memory("unexpected write"))
  }

  test("loads and executes PTX through the typed runtime boundary") {
    val source  = ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry empty() { ret; }\n"
    val program = PTX()
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
    ).fold(message => fail(message), value => value)
    program.execute(task, EmptyMemory, TaskControl())
      .fold(error => fail(error.message), value => value)

    val cancelled = TaskControl()
    cancelled.cancel()
    assertEquals(
      program.execute(task, EmptyMemory, cancelled),
      Left(ExecutionFailure.Failed("execution was cancelled"))
    )

    program.close()
    assertEquals(program.global("unused").left.toOption.map(_.message: String), Some("program is closed"))
    assertEquals(program.execute(task, EmptyMemory, TaskControl()), Left(ExecutionFailure.Failed("program is closed")))
    assertEquals(program.symbols, Left(ExecutionFailure.Failed("program is closed")))
    assertEquals(program.kernel("empty"), Left(ExecutionFailure.Failed("program is closed")))
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
    Using.resource(ExecutionSession(Backends.default().programs)) { service =>
      val allocation = service.registerAllocation(16L, AllocationKind.Shared).toOption.get
      val output     = service.registerAllocation(4L, AllocationKind.Shared).toOption.get
      val program    =
        service.load(PTX.encoding, Vec.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get
      val task = KernelTask.validated(
        "aliases",
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Memory(allocation, 4L),
          KernelTask.Argument.Memory(output, 0L)
        )
      ).toOption.get

      val completion = service.launch(program, task).toOption.get
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
