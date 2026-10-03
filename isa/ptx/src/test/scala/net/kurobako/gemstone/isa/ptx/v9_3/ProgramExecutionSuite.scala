package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class ProgramExecutionSuite extends munit.FunSuite {
  private class EmptyMemory extends AddressSpace {
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

  test("loads isolated program state and executes a kernel task") {
    val source  = ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry empty() { ret; }\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)

    val first  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val second = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    assert(!(first eq second))

    val task = KernelTask.validated(
      "empty",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).fold(message => fail(message), value => value)
    first.execute(task, EmptyMemory(), TaskControl())
      .fold(error => fail(error.message), value => value)
  }
  test("cancellation during a backing read stays cancellation through PTX memory conversion") {
    class CancelledMemory extends EmptyMemory {
      override def readValue(address: GuestAddress, size: Int in 1 ~ 8): Either[Fault, Long] =
        Left(Fault.Cancelled)
    }
    val source = """.version 8.0
      .target sm_80
      .address_size 64
      .visible .entry read() {
        .reg .u64 %address;
        .reg .u32 %value;
        mov.u64 %address, 4096;
        ld.global.u32 %value, [%address];
        ret;
      }
      """
    val loaded = Parser.decode(source).left.map(_.message).flatMap(program =>
      LoadedProgram.load(program).left.map(_.message)
    ).fold(message => fail(message), identity)
    val task = KernelTask.validated(
      "read",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    assertEquals(loaded.execute(task, CancelledMemory(), TaskControl()), Left(Fault.Cancelled))
  }
}
