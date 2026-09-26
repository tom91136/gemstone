package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.{NonNegativeInt, NonNegativeLong, PositiveLong, Vec}
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.KernelName as ISAKernelName
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.rpc.ExecutionProtocol.CompletionStatus
import net.kurobako.gemstone.vm.{KernelTask, LaunchGeometry, MemoryRegion}

import java.nio.charset.StandardCharsets
import scala.util.Using

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
    Using.resource(ExecutionService(RuntimeBackend.defaults)) { service =>
      val allocation = service.registerAllocation(PositiveLong(16L), AllocationKind.Shared).toOption.get
      val output     = service.registerAllocation(PositiveLong(4L), AllocationKind.Shared).toOption.get
      val program    =
        service.load(PTXRuntimeBackend.encoding, Vec.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get
      val task = KernelTask.validated(
        ISAKernelName("aliases"),
        LaunchGeometry.Dimensions.One,
        LaunchGeometry.Dimensions.One,
        Vec(
          KernelTask.Argument.Memory(MemoryRegion(allocation.raw), NonNegativeLong(4L)),
          KernelTask.Argument.Memory(MemoryRegion(allocation.raw), NonNegativeLong(4L)),
          KernelTask.Argument.Memory(MemoryRegion(output.raw), NonNegativeLong.Zero)
        )
      ).toOption.get

      val completion = service.launch(program, task).toOption.get
      assertEquals(service.completion(completion), Right(CompletionStatus.Succeeded))
      assertEquals(
        service.read(output, NonNegativeLong.Zero, NonNegativeInt(4)).map(bytes =>
          LittleEndian.decode(bytes, 0, ScalarByteWidth.Dword)
        ),
        Right(1L)
      )
    }
  }
}
