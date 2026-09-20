package net.kurobako.gemstone.isa.ptx.v9_3

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.KernelMetadata
import net.kurobako.gemstone.execution.KernelName
import net.kurobako.gemstone.execution.KernelParameterKind
import net.kurobako.gemstone.execution.KernelParameterMetadata
import net.kurobako.gemstone.execution.GlobalName
import net.kurobako.gemstone.execution.ProgramGlobal
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.nio.charset.StandardCharsets

final class ModuleDecoderSuite extends munit.FunSuite {
  test("exposes module global metadata without requiring an entry point") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.global .align 4 .u32 metadata_probe[4];\n"
    val loaded   = ModuleDecoder.decode(Vec.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get
    val actual   = loaded.global(GlobalName("metadata_probe")).fold(error => fail(error.toString), value => value)
    val expected =
      ProgramGlobal.validated(16L, Vec.from(Array.ofDim[Byte](16))).fold(message => fail(message), value => value)
    assertEquals(actual, expected)
  }

  test("exposes aligned PTX kernel parameter offsets") {
    val source =
      ".version 8.0\n.target sm_80\n.address_size 64\n.visible .entry layout(" +
        ".param .u8 first, .param .align 8 .u32 second, .param .u16 third) { ret; }\n"
    val loaded = ModuleDecoder.decode(Vec.from(source.getBytes(StandardCharsets.UTF_8))).toOption.get

    val actual   = loaded.kernel(KernelName("layout")).fold(error => fail(error.toString), value => value)
    val expected =
      KernelMetadata.validated(
        14,
        3,
        Vec(
          KernelParameterMetadata(1, 0, KernelParameterKind.Scalar, containsPointers = false),
          KernelParameterMetadata(4, 8, KernelParameterKind.Scalar, containsPointers = false),
          KernelParameterMetadata(2, 12, KernelParameterKind.Scalar, containsPointers = false)
        )
      ).fold(message => fail(message), value => value)
    assertEquals(actual, expected)
  }
}
