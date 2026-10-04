package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
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
  test("module shared symbols and dynamic aliases retain generic and explicit access") {
    val source = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.shared .align 4 .u32 first[2];
      |.shared .align 16 .u32 second[4];
      |.shared .align 8 .u64 third;
      |.extern .shared .align 16 .b8 dynamicA[];
      |.extern .shared .align 32 .b8 dynamicB[];
      |.visible .entry shared_access() {
      | .reg .u64 %a, %b, %c, %da, %db, %value, %alignment;
      | .reg .pred %bad;
      | cvta.shared.u64 %a, first;
      | cvta.shared.u64 %b, second;
      | cvta.shared.u64 %c, third;
      | cvta.shared.u64 %da, dynamicA;
      | cvta.shared.u64 %db, dynamicB;
      | st.u32 [%a+4], 17;
      | st.shared.u32 [second+12], 42;
      | st.u64 [%c], 84;
      | st.u32 [%da+12], 96;
      | ld.shared.u32 %value, [first+4];
      | setp.ne.u64 %bad, %value, 17;
      | @%bad trap;
      | ld.u32 %value, [%b+12];
      | setp.ne.u64 %bad, %value, 42;
      | @%bad trap;
      | ld.shared.u64 %value, [third];
      | setp.ne.u64 %bad, %value, 84;
      | @%bad trap;
      | ld.shared.u32 %value, [dynamicB+12];
      | setp.ne.u64 %bad, %value, 96;
      | @%bad trap;
      | setp.ne.u64 %bad, %da, %db;
      | @%bad trap;
      | and.b64 %alignment, %b, 15;
      | setp.ne.u64 %bad, %alignment, 0;
      | @%bad trap;
      | and.b64 %alignment, %da, 31;
      | setp.ne.u64 %bad, %alignment, 0;
      | @%bad trap;
      | ret;
      |}""".stripMargin
    val loaded = Parser.decode(source).left.map(_.message).flatMap(program =>
      LoadedProgram.load(program).left.map(_.message)
    ).fold(message => fail(message), identity)
    val task = KernelTask.validated(
      kernelName = "shared_access",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions.One,
      arguments = Vec.empty,
      groupSegmentBytes = 64
    ).toOption.get
    assertEquals(loaded.execute(task, EmptyMemory(), TaskControl()), Right(()))
  }

  test("module shared allocations cannot escape the shared aperture") {
    val bytes = VMAddressSpace.SharedLimit -
      VMAddressSpace.ModuleSharedBase + 1L
    val parsed = Parser.decode(s".shared .b8 oversized[$bytes];").toOption.get
    assert(LoadedProgram.load(parsed).left.exists(_.message.contains("shared address aperture")))
  }

  test("a device function memory failure identifies its function") {
    val source = """.version 8.0
      |.target sm_80
      |.address_size 64
      |.visible .func broken() {
      | .reg .u64 %address;
      | .reg .u32 %value;
      | mov.u64 %address, 0x100000000;
      | ld.global.u32 %value, [%address];
      | ret;
      |}
      |.visible .entry caller() { call broken; ret; }
      |""".stripMargin
    val loaded = Parser.decode(source).left.map(_.message).flatMap(program =>
      LoadedProgram.load(program).left.map(_.message)
    ).fold(message => fail(message), identity)
    val task = KernelTask.validated(
      "caller",
      LaunchGeometry.Dimensions.One,
      LaunchGeometry.Dimensions.One,
      Vec.empty
    ).toOption.get
    assert(loaded.execute(task, EmptyMemory(), TaskControl()).left.exists(_.message.contains("broken: pc")))
  }

  for (requested <- Vec[Int >= 0](0, 64, 160))
    test(s"dynamic shared size reports the launch request $requested separately from static declarations") {
      val source = s""".version 8.0
        |.target sm_80
        |.address_size 64
        |.shared .align 32 .u8 staticData[48];
        |.extern .shared .align 64 .u8 dynamicData[];
        |.visible .func (.param .u32 answer) dynamic_size() {
        | .reg .u32 %size;
        | mov.u32 %size, %dynamic_smem_size;
        | st.param.u32 [answer], %size;
        | ret;
        |}
        |.visible .entry dynamic_access() {
        | .reg .u32 %size, %value;
        | .reg .u64 %address, %offset;
        | .reg .pred %bad, %empty;
        | .param .u32 returned;
        | call (returned), dynamic_size;
        | ld.param.u32 %size, [returned];
        | setp.ne.u32 %bad, %size, $requested;
        | @%bad trap;
        | setp.eq.u32 %empty, %size, 0;
        | @%empty bra done;
        | sub.u32 %value, %size, 4;
        | cvt.u64.u32 %offset, %value;
        | cvta.shared.u64 %address, dynamicData;
        | add.u64 %address, %address, %offset;
        | st.u32 [%address], 42;
        | ld.shared.u32 %value, [%address];
        | setp.ne.u32 %bad, %value, 42;
        | @%bad trap;
        |done:
        | ret;
        |}""".stripMargin
      val loaded = Parser.decode(source).left.map(_.message).flatMap(program =>
        LoadedProgram.load(program).left.map(_.message)
      ).fold(message => fail(message), identity)
      val task = KernelTask.validated(
        kernelName = "dynamic_access",
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions.One,
        arguments = Vec.empty,
        groupSegmentBytes = requested
      ).toOption.get
      assertEquals(loaded.execute(task, EmptyMemory(), TaskControl()), Right(()))
    }

}
