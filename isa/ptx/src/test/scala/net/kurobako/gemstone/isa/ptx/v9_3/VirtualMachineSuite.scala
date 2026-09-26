package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.{NonEmptyString, NonEmptyVec, NonNegativeInt, NonNegativeLong, PositiveInt, PositiveLong}

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.*

final class VirtualMachineSuite extends munit.FunSuite {
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

  test("loads isolated program state and executes a kernel task") {
    val source  = ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry empty() { ret; }\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val machine = VirtualMachine(Observer.discard, Scheduler.Sequential)
    val first   = machine.load(program).fold(error => fail(error.message), value => value)
    val second  = machine.load(program).fold(error => fail(error.message), value => value)
    assert(!(first eq second))

    val task = KernelTask.validated(
      KernelName("empty"),
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).fold(message => fail(message), value => value)
    val result = machine.execute(first, task, EmptyMemory, TaskControl.default)
      .fold(error => fail(error.message), value => value)
    assert(result.instructions.raw > 0L)
  }
}
