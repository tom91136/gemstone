package net.kurobako.gemstone.server.execution

import net.kurobako.gemstone.*

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.*

import java.nio.charset.StandardCharsets

final class PTXRuntimeBackendSuite extends munit.FunSuite {
  private object EmptyMemory extends Memory with AddressSpace {
    override def address(
        region: MemoryRegion,
        offset: NonNegativeLong
    ): Either[Fault, GuestAddress] = Left(Fault.Memory("unexpected address lookup"))

    override def locate(
        address: GuestAddress,
        size: NonNegativeInt
    ): Either[Fault, AddressSpace.Location] = Left(Fault.Memory("unexpected address lookup"))

    override def read(
        region: MemoryRegion,
        offset: NonNegativeLong,
        size: NonNegativeInt
    ): Either[Fault, Vec[Byte]] = Left(Fault.Memory("unexpected read"))

    override def write(
        region: MemoryRegion,
        offset: NonNegativeLong,
        bytes: Vec[Byte]
    ): Either[Fault, Unit] = Left(Fault.Memory("unexpected write"))
  }

  test("loads and executes PTX through the typed runtime boundary") {
    val source  = ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry empty() { ret; }\n"
    val program = PTXRuntimeBackend
      .load(
        RuntimeBackend.Services(Observer.discard, Scheduler.Sequential),
        Vec.from(source.getBytes(StandardCharsets.UTF_8))
      )
      .fold(error => fail(error.message), value => value)

    assertEquals(program.symbols.map(_.kernels.map(_.name)), Right(Vec(KernelName("empty"))))
    assert(program.kernel(KernelName("empty")).isRight)
    assert(program.globals.nonEmpty)

    val task = KernelTask.validated(
      KernelName("empty"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).fold(message => fail(message), value => value)
    val result = program.execute(task, EmptyMemory, TaskControl.default)
      .fold(error => fail(error.message), value => value)
    assert(result.instructions.raw > 0L)

    program.close()
    assert(program.execute(task, EmptyMemory, TaskControl.default).isLeft)
  }
}
