package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class LoadedProgramSuite extends Suite {
  private def bind(
      loaded: LoadedProgram,
      name: NonEmpty[String],
      region: AllocationId,
      offset: Long >= 0,
      memory: AddressSpace
  ): Either[Fault, Unit] =
    loaded.global(name)
      .flatMap(image => GlobalBinding.validated(name, region, offset, image.size, 64L))
      .flatMap(loaded.bindGlobal(name, _, memory))

  test("exposes module global metadata without requiring an entry point") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.global .align 4 .u32 metadata_probe[4];\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val actual  =
      loaded.global("metadata_probe").value
    assertEquals(actual.size, 16L: Long >= 0)
    assertEquals(actual.initial, Vec.from(Array.ofDim[Byte](16)))
  }

  test("keeps global integer initialiser suffix, radix, and truncation behaviour") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".global .u8 values[4] = {255, 0xffu, 0b100000010, 18446744073709551617};\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val global  = loaded.global("values").value

    assertEquals(global.initial.map(_.toInt), Vec(-1, -1, 2, 1))
  }

  test("keeps floating initialiser bit-pattern and decimal conversion behaviour") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".global .f32 values[2] = {0f3f800000, 1.5};\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val global  = loaded.global("values").value

    assertEquals(global.initial.map(byte => byte & 0xff), Vec(0, 0, 128, 63, 0, 0, 192, 63))
  }

  for (reverse <- Vec(false, true))
    test(s"global pointer initialisers bind in either order and are applied once: reverse=$reverse") {
      val source   = ".global .u64 table[3] = {7, generic(values)+4, 9}; .global .u32 values[2] = {31, 42};"
      val program  = Parser.parse(source).value
      val loaded   = LoadedProgram.load(program).value
      val region   = AllocationId(1L)
      val memory   = RegionMemory(region -> 64)
      val bindings = Vec(("table": NonEmpty[String], 0L: Long >= 0), ("values": NonEmpty[String], 32L: Long >= 0))
      (if (reverse) bindings.reverse else bindings).foreach { case (name, offset) =>
        val initial = loaded.global(name).value.initial
        assertEquals(memory.write(region, offset, initial), Right(()))
        assertEquals(bind(loaded, name, region, offset, memory), Right(()))
      }
      assertEquals(memory.readValue(region, 0L, 8), Right(7L))
      assertEquals(memory.readValue(region, 8L, 8), Right(memory.address(region, 36L).value.raw))
      assertEquals(memory.readValue(region, 16L, 8), Right(9L))
      assertEquals(memory.readValue(GuestAddress(memory.readValue(region, 8L, 8).value), 4), Right(42L))
      assertEquals(memory.writeValue(region, 8L, 0L, 8), Right(()))
      bindings.foreach { case (name, offset) =>
        assertEquals(bind(loaded, name, region, offset, memory), Right(()))
      }
      assertEquals(memory.readValue(region, 8L, 8), Right(0L))
    }

  test("function pointer initialisers retain indirect callees and stable addresses across entries") {
    val program = Parser.parse(""".version 8.0
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
      }""").value
    val loaded   = LoadedProgram.load(program).value
    val region   = AllocationId(1L)
    val memory   = RegionMemory(region -> 64)
    val bindings = Vec(("functions": NonEmpty[String], 0L: Long >= 0), ("result": NonEmpty[String], 32L: Long >= 0))
    bindings.foreach { (name, offset) =>
      assertEquals(memory.write(region, offset, loaded.global(name).value.initial), Right(()))
      assertEquals(bind(loaded, name, region, offset, memory), Right(()))
    }
    Vec[NonEmpty[String]]("a", "b").foreach { name =>
      val task = KernelTask.validated(
        kernelName = name,
        groupSize = LaunchGeometry.Dimensions.One,
        groupCount = LaunchGeometry.Dimensions.One,
        arguments = Vec.empty
      ).value
      assertEquals(loaded.execute(task, TaskContext(memory, TaskControl(), _ => Right(()))), Right(()))
    }
    assertEquals(memory.readValue(region, 32L, 4), Right(42L))
    assertEquals(memory.readValue(region, 36L, 4), Right(84L))
    assertEquals(memory.writeValue(region, 0L, 0L, 8), Right(()))
    assertEquals(bind(loaded, "functions", region, 0L, memory), Right(()))
    assertEquals(memory.readValue(region, 0L, 8), Right(0L))
  }

  test("invalid global initialisers fail rather than silently shifting array elements") {
    for (
      source <- Vec(
        ".global .u64 table[2] = {generic(missing), 9};",
        ".global .u32 values[1] = {1, 2};"
      )
    ) {
      val program = Parser.parse(source).value
      assert(LoadedProgram.load(program).isLeft)
    }
  }

  test("building a kernel reports a compile failure only for the kernel that cannot compile") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry fine() { ret; }\n" +
        ".visible .entry broken() { .reg .u32 %r<2>; not_an_instruction.u32 %r0; ret; }\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)

    assertEquals(loaded.build("fine"), Right(()))
    assertEquals(loaded.build("fine"), Right(()))
    loaded.build("broken") match {
      case Left(Fault.InvalidProgram(detail)) => assert(detail.contains("not_an_instruction"), detail)
      case other                              => fail(s"expected invalid program failure, got $other")
    }
    loaded.build("absent") match {
      case Left(Fault.NotFound(_)) => ()
      case other                   => fail(s"expected missing kernel failure, got $other")
    }
  }

  test("launching a kernel after building it reuses the compiled result") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry broken() { .reg .u32 %r<2>; not_an_instruction.u32 %r0; ret; }\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val task    = KernelTask.validated(
      kernelName = "broken",
      groupSize = LaunchGeometry.Dimensions.One,
      groupCount = LaunchGeometry.Dimensions.One,
      arguments = Vec.empty
    ).value
    val built    = loaded.build("broken")
    val launched = loaded.execute(task, TaskContext(DeviceMemory(16), TaskControl(), _ => Right(())))
    assert(built.isLeft)
    assert(built eq launched, s"$built was recompiled as $launched")
  }

  test("exposes aligned PTX kernel parameter offsets") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry layout(" +
        ".param .u8 first, .param .align 8 .u32 second, .param .u16 third) { ret; }\n"
    val program = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded  = LoadedProgram.load(program).fold(error => fail(error.message), value => value)

    val actual   = loaded.kernel("layout").value
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
      ).value
    assertEquals(actual, expected)
  }

  test("exposes PTX pointer parameters as allocations") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry pointer(.param .u64 .ptr .global output, .param .u64 bits) { ret; }\n"
    val program  = Parser.parse(source).fold(error => fail(error.message), value => value)
    val loaded   = LoadedProgram.load(program).fold(error => fail(error.message), value => value)
    val metadata = loaded.kernel("pointer").value
    assertEquals(
      metadata.parameters.map(_.kind),
      Vec(KernelMetadata.ParameterKind.Allocation, KernelMetadata.ParameterKind.Scalar)
    )
  }
}
