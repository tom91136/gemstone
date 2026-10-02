package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

class LoadedProgramSuite extends munit.FunSuite {

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
