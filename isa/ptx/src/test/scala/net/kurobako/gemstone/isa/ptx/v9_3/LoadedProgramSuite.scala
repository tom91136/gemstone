package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

class LoadedProgramSuite extends munit.FunSuite {
  private class BoundMemory(region: AllocationId, storage: ArrayCheckedMemory) extends AddressSpace {
    override def address(allocation: AllocationId, offset: Long >= 0): Either[Fault, GuestAddress] =
      Right(storage.allocation.start.offset(offset.raw))
    override def locate(address: GuestAddress, size: Int >= 0): Either[Fault, AddressSpace.Location] =
      Right(AddressSpace.Location.Within(region, (address.raw - storage.allocation.start.raw).unsafe[Long >= 0]))
    override def read(allocation: AllocationId, offset: Long >= 0, size: Int >= 0): Either[Fault, Vec[Byte]] =
      storage.readBytes(storage.allocation.start.offset(offset.raw), size).left.map(error =>
        Fault.Memory(error.message)
      )
    override def write(allocation: AllocationId, offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
      storage.writeBytes(storage.allocation.start.offset(offset.raw), bytes).left.map(error =>
        Fault.Memory(error.message)
      )
  }

  test("exposes module global metadata without requiring an entry point") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.global .align 4 .u32 metadata_probe[4];\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val actual  =
      loaded.global("metadata_probe").fold(error => fail(error.message), identity)
    assertEquals(actual.size, 16L: Long >= 0)
    assertEquals(actual.initial, Vec.from(Array.ofDim[Byte](16)))
  }

  test("keeps global integer initializer suffix, radix, and truncation behavior") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".global .u8 values[4] = {255, 0xffu, 0b100000010, 18446744073709551617};\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val global  = loaded.global("values").fold(error => fail(error.message), identity)

    assertEquals(global.initial.map(_.toInt), Vec(-1, -1, 2, 1))
  }

  test("keeps floating initializer bit-pattern and decimal conversion behavior") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".global .f32 values[2] = {0f3f800000, 1.5};\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val global  = loaded.global("values").fold(error => fail(error.message), identity)

    assertEquals(global.initial.map(byte => byte & 0xff), Vec(0, 0, 128, 63, 0, 0, 192, 63))
  }

  for (reverse <- Vec(false, true))
    test(s"global pointer initializers bind in either order and are applied once: reverse=$reverse") {
      val source   = ".global .u64 table[3] = {7, generic(values)+4, 9}; .global .u32 values[2] = {31, 42};"
      val program  = Parser.decode(source).toOption.get
      val loaded   = LoadedProgram.load(program).toOption.get
      val region   = AllocationId(1L)
      val storage  = ArrayCheckedMemory.allocate(64)
      val memory   = BoundMemory(region, storage)
      val bindings = Vec(("table": NonEmpty[String], 0L: Long >= 0), ("values": NonEmpty[String], 32L: Long >= 0))
      (if (reverse) bindings.reverse else bindings).foreach { case (name, offset) =>
        val initial = loaded.global(name).toOption.get.initial
        assertEquals(memory.write(region, offset, initial), Right(()))
        assertEquals(loaded.bindGlobal(name, region, offset, 64L, memory), Right(()))
      }
      assertEquals(memory.readValue(region, 0L, 8), Right(7L))
      assertEquals(memory.readValue(region, 8L, 8), Right(storage.allocation.start.raw + 36L))
      assertEquals(memory.readValue(region, 16L, 8), Right(9L))
      assertEquals(memory.readValue(GuestAddress(memory.readValue(region, 8L, 8).toOption.get), 4), Right(42L))
      assertEquals(memory.writeValue(region, 8L, 0L, 8), Right(()))
      bindings.foreach { case (name, offset) =>
        assertEquals(loaded.bindGlobal(name, region, offset, 64L, memory), Right(()))
      }
      assertEquals(memory.readValue(region, 8L, 8), Right(0L))
    }

  test("function pointer initializers retain indirect callees and stable addresses across entries") {
    val program = Parser.decode(""".version 8.0
      .target sm_80
      .address_size 64
      .const .u64 functions[2] = {first, second};
      .global .u32 result[2];
      .func first() { st.global.u32 [result], 42; ret; }
      .func second() { st.global.u32 [result+4], 84; ret; }
      .visible .entry a() {
        .reg .u64 %pointer;
        ld.const.u64 %pointer, [functions];
        call %pointer, ();
        ret;
      }
      .visible .entry b() {
        .reg .u64 %pointer;
        ld.const.u64 %pointer, [functions+8];
        call %pointer, ();
        ret;
      }""").toOption.get
    val loaded   = LoadedProgram.load(program).fold(error => fail(error.message), identity)
    val region   = AllocationId(1L)
    val storage  = ArrayCheckedMemory.allocate(64)
    val memory   = BoundMemory(region, storage)
    val bindings = Vec(("functions": NonEmpty[String], 0L: Long >= 0), ("result": NonEmpty[String], 32L: Long >= 0))
    bindings.foreach { (name, offset) =>
      assertEquals(memory.write(region, offset, loaded.global(name).toOption.get.initial), Right(()))
      assertEquals(loaded.bindGlobal(name, region, offset, 64L, memory), Right(()))
    }
    Vec[NonEmpty[String]]("a", "b").foreach { name =>
      val task = KernelTask.validated(
        kernelName = name,
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions.One,
        arguments = Vec.empty
      ).toOption.get
      assertEquals(loaded.execute(task, memory, TaskControl()), Right(()))
    }
    assertEquals(memory.readValue(region, 32L, 4), Right(42L))
    assertEquals(memory.readValue(region, 36L, 4), Right(84L))
    assertEquals(memory.writeValue(region, 0L, 0L, 8), Right(()))
    assertEquals(loaded.bindGlobal("functions", region, 0L, 64L, memory), Right(()))
    assertEquals(memory.readValue(region, 0L, 8), Right(0L))
  }

  test("invalid global initializers fail rather than silently shifting array elements") {
    for (
      source <- Vec(
        ".global .u64 table[2] = {generic(missing), 9};",
        ".global .u32 values[1] = {1, 2};"
      )
    ) {
      val program = Parser.decode(source).toOption.get
      assert(LoadedProgram.load(program).isLeft)
    }
  }

  test("exposes aligned PTX kernel parameter offsets") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry layout(" +
        ".param .u8 first, .param .align 8 .u32 second, .param .u16 third) { ret; }\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)

    val actual   = loaded.kernel("layout").fold(error => fail(error.message), identity)
    val expected =
      KernelMetadata.validated(
        14,
        3,
        Vec(
          KernelMetadata.Parameter(
            1,
            0,
            KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          ),
          KernelMetadata.Parameter(
            4,
            8,
            KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          ),
          KernelMetadata.Parameter(
            2,
            12,
            KernelMetadata.ParameterKind.Scalar,
            containsPointers = false
          )
        )
      ).fold(message => fail(message), value => value)
    assertEquals(actual, expected)
  }

  test("exposes PTX pointer parameters as allocations") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry pointer(.param .u64 .ptr .global output, .param .u64 bits) { ret; }\n"
    val program  = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded   = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val metadata = loaded.kernel("pointer").fold(error => fail(error.message), identity)
    assertEquals(
      metadata.parameters.map(_.kind),
      Vec(KernelMetadata.ParameterKind.Allocation, KernelMetadata.ParameterKind.Scalar)
    )
  }
}
