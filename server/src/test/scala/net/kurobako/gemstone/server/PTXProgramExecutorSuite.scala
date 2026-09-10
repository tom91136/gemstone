package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind

import java.nio.charset.StandardCharsets

final class PTXProgramExecutorSuite extends munit.FunSuite {
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
    val service    = ExecutionService(RegisteredProgramExecutor.default, _.run())
    val allocation = AllocationId.fromLong(1L).toOption.get
    val output     = AllocationId.fromLong(2L).toOption.get
    assertEquals(service.registerAllocation(allocation, 16L, AllocationKind.Shared), Right(()))
    assertEquals(service.registerAllocation(output, 4L, AllocationKind.Shared), Right(()))
    val program  = service.load(ProgramFormat.PTX, Col.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get
    val dispatch = KernelDispatch.create(
      program,
      "aliases",
      Dimensions.One,
      Dimensions.One,
      Col(
        KernelArgument.Allocation(allocation, 4L),
        KernelArgument.Allocation(allocation, 4L),
        KernelArgument.Allocation(output, 0L)
      )
    ).toOption.get

    val completion = service.launch(dispatch).toOption.get
    assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
    assertEquals(service.read(output, 0L, 4).map(bytes => LittleEndian.decode(bytes, 0, 4)), Right(1L))
  }
}
