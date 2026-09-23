package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.isa.{GlobalName, KernelMetadata, KernelName}
import net.kurobako.gemstone.vm.{Observer, Scheduler}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

final class VMLoadedProgramSuite extends munit.FunSuite {
  private val machine = VirtualMachine(Observer.discard, Scheduler.Sequential)

  test("exposes module global metadata without requiring an entry point") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.global .align 4 .u32 metadata_probe[4];\n"
    val program  = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded   = machine.load(program).fold(error => fail(error.message), value => value)
    val actual   = machine.global(loaded, GlobalName("metadata_probe")).fold(error => fail(error.message), identity)
    val expected = 16L.refineUnsafe[Positive0] -> Vec.from(Array.ofDim[Byte](16))
    assertEquals(actual, expected)
  }

  test("exposes aligned PTX kernel parameter offsets") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry layout(" +
        ".param .u8 first, .param .align 8 .u32 second, .param .u16 third) { ret; }\n"
    val program = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded  = machine.load(program).fold(error => fail(error.message), value => value)

    val actual   = machine.kernel(loaded, KernelName("layout")).fold(error => fail(error.message), identity)
    val expected =
      KernelMetadata.validated(
        14,
        3,
        Vec(
          KernelMetadata.Parameter(1, 0, KernelMetadata.ParameterKind.Scalar, containsPointers = false),
          KernelMetadata.Parameter(4, 8, KernelMetadata.ParameterKind.Scalar, containsPointers = false),
          KernelMetadata.Parameter(2, 12, KernelMetadata.ParameterKind.Scalar, containsPointers = false)
        )
      ).fold(message => fail(message), value => value)
    assertEquals(actual, expected)
  }

  test("exposes PTX pointer parameters as allocations") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n" +
        ".visible .entry pointer(.param .u64 .ptr .global output, .param .u64 bits) { ret; }\n"
    val program  = Parser.decode(source).fold(error => fail(error.message), value => value)
    val loaded   = machine.load(program).fold(error => fail(error.message), value => value)
    val metadata = machine.kernel(loaded, KernelName("pointer")).fold(error => fail(error.message), identity)
    assertEquals(
      metadata.parameters.map(_.kind),
      Vec(KernelMetadata.ParameterKind.Memory, KernelMetadata.ParameterKind.Scalar)
    )
  }
}
