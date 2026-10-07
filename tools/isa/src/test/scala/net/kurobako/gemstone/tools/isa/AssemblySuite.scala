package net.kurobako.gemstone.tools.isa

import java.nio.charset.StandardCharsets
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.*

class AssemblySuite extends munit.FunSuite {
  private val gfx1030 = Assembly.Format.AMDGPU(amdgpu.Target("gfx1030"))
  private val sm89    = sass.Target.parse("sm_89").fold(fail(_), identity)

  private def roundTrip(source: String, format: Assembly.Format): Vec[Byte] = {
    val bytes = Assembly.assemble(source, format).fold(error => fail(error.toString), identity)
    val text  = Assembly.disassemble(bytes, format).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(text, format), Right(bytes))
    bytes
  }

  Vec(
    "OpCapability Shader\nOpMemoryModel Logical GLSL450\n%1 = OpTypeInt 32 0\n%2 = OpConstant %1 42" ->
      Assembly.Format.SPIRV,
    "s_mov_b32 s0, lit(1)\ns_add_u32 s1, s0, 2\ns_endpgm" -> gfx1030,
    "entry: MOV R3, R7; BRA entry;"                       -> Assembly.Format.SASS(sm89)
  ).foreach { (source, format) =>
    test(s"$format text round-trips through its binary form") {
      val _ = roundTrip(source, format)
    }
  }

  test("raw AMDGPU and SASS disassembly reports truncated instructions") {
    val amdgpuBytes = roundTrip("s_mov_b32 s0, lit(1)\ns_add_u32 s1, s0, 2\ns_endpgm", gfx1030)
    Assembly.disassemble(amdgpuBytes.dropRight(1), gfx1030) match {
      case Left(Assembly.Error.AMDGPUDecoding(offset, _)) => assertEquals(offset.raw, 12)
      case result => fail(s"expected truncation at the third instruction: $result")
    }
    val sassBytes = roundTrip("entry: MOV R3, R7; BRA entry;", Assembly.Format.SASS(sm89))
    assert(Assembly.disassemble(sassBytes.dropRight(1), Assembly.Format.SASS(sm89)).isLeft)
  }

  test("AMDGPU disassembly steps over a dual-issue pair into an image payload") {
    val gfx1100 = Assembly.Format.AMDGPU(amdgpu.Target("gfx1100"))
    val bytes   = roundTrip(
      "v_dual_add_f32 v255, v4, v2 :: v_dual_mul_dx9_zero_f32 v6, v1, v3\n" +
        "image_atomic_add v[4:5], [v32, v1, v2], s[96:103] dmask:3 dim:SQ_RSRC_IMG_CUBE glc\ns_endpgm",
      gfx1100
    )
    assertEquals(bytes.size, 24)
    assert(Assembly.disassemble(bytes, gfx1100).exists(_.contains(" :: ")))
    Assembly.disassemble(bytes.take(19), gfx1100) match {
      case Left(Assembly.Error.AMDGPUDecoding(offset, _)) => assertEquals(offset.raw, 8)
      case result => fail(s"expected truncation in the image payload after the pair: $result")
    }
  }

  private def executable(machine: Int, sections: Vec[(String, Vec[Byte])], sm: Option[Int] = None): Vec[Byte] =
    Vec.unsafeWrapArray(elfImage.write(
      machine,
      sections.map((name, code) => elfImage.Section(name, 1, Vec.toArray(code), flags = 6L)),
      imageType = 1,
      abiVersion = if (sm.isDefined) 7 else 0,
      flags = sm.getOrElse(0)
    ))

  test("code-object disassembly extracts executable bytes and requires a choice for multiple sections") {
    // Fixed SOPP END_PGM and NOP words, not produced by the codec under test.
    val end  = Vec(0x00, 0x00, 0x81, 0xbf).map(_.toByte)
    val nop  = Vec(0x03, 0x00, 0x80, 0xbf).map(_.toByte)
    val text = Assembly.disassemble(executable(224, Vec(".text" -> end)), gfx1030)
    assertEquals(text.flatMap(Assembly.assemble(_, gfx1030)), Right(end))
    val multiple = executable(224, Vec(".text" -> end, ".kernel" -> nop))
    assert(Assembly.disassemble(multiple, gfx1030).isLeft)
    val selected = Assembly.disassemble(multiple, gfx1030, Some(".kernel"))
    assertEquals(selected.flatMap(Assembly.assemble(_, gfx1030)), Right(nop))
    assert(Assembly.disassemble(multiple, gfx1030, Some(".missing")).isLeft)
    assert(Assembly.disassemble(executable(224, Vec.empty), gfx1030).isLeft)
    assert(Assembly.disassemble(Vec[Byte](0x7f, 'E'.toByte, 'L'.toByte, 'F'.toByte), gfx1030).isLeft)
  }

  test("cubin disassembly selects executable sections and checks the target") {
    val format = Assembly.Format.SASS(sm89)
    // Fixed SM89 EXIT bytes, independent of the assembler under test.
    val exit = Vec(0x4d, 0x79, 0, 0, 0, 0, 0, 0, 0, 0, 0x80, 3, 0, 0xea, 0x0f, 0).map(_.toByte)
    val text = Assembly.disassemble(executable(190, Vec(".text.kernel" -> exit), Some(89)), format)
    assertEquals(text.flatMap(Assembly.assemble(_, format)), Right(exit))
    val multiple = executable(190, Vec(".text.first" -> exit, ".text.second" -> exit), Some(89))
    assert(Assembly.disassemble(multiple, format).isLeft)
    val selected = Assembly.disassemble(multiple, format, Some(".text.second"))
    assertEquals(selected.flatMap(Assembly.assemble(_, format)), Right(exit))
    assert(Assembly.disassemble(multiple, format, Some(".missing")).isLeft)
    assert(Assembly.disassemble(executable(190, Vec(".text.kernel" -> exit), Some(80)), format).isLeft)
    assert(Assembly.disassemble(executable(190, Vec.empty, Some(89)), format).isLeft)
  }

  test("PTX is canonical text with a stable typed program") {
    val source = ".version 9.3\n.target sm_100\n.address_size 64\n.visible .entry kernel() { ret; }"
    val bytes  = roundTrip(source, Assembly.Format.PTX)
    val text   = String(Vec.toArray(bytes), StandardCharsets.UTF_8)
    assertEquals(ptx.Parser.parse(text), ptx.Parser.parse(source))
  }

  test("text decoding rejects malformed UTF-8 instead of replacing bytes") {
    assertEquals(Assembly.text(Vec(0xc3.toByte, 0x28.toByte)), Left(Assembly.Error.InvalidUTF8))
  }

  test("CLI requires explicit ISA, AMDGPU target, and binary output destination") {
    def parse(args: String*) = CLI.command.parse(args.toList)
    assert(parse("assemble", "--isa", "spirv", "input").isLeft)
    assert(parse("disassemble", "--isa", "amdgpu", "input").isLeft)
    assert(parse("disassemble", "--isa", "sass", "input").isLeft)
    assert(parse("disassemble", "--isa", "sass", "--target", "sm_89", "input").isRight)
    assert(parse("disassemble", "--isa", "sass", "--target", "sm_89", "--section", ".text.kernel", "input").isRight)
    assert(parse("disassemble", "--isa", "sass", "--target", "gfx1030", "input").isLeft)
    assert(parse("assemble", "--isa", "sass", "--target", "sm_89", "--output", "output", "input").isRight)
    assert(parse("disassemble", "--isa", "spirv", "--target", "gfx1030", "input").isLeft)
    assert(parse("disassemble", "--isa", "amdgpu", "--target", "typo", "input").isLeft)
    assert(parse("disassemble", "--isa", "amdgpu", "--target", "gfx1030", "input").isRight)
    assert(parse("disassemble", "--isa", "amdgpu", "--target", "gfx1200", "--wave-size", "64", "input").isRight)
    assert(parse("disassemble", "--isa", "amdgpu", "--target", "gfx1200", "--wave-size", "16", "input").isLeft)
    assert(parse("disassemble", "--isa", "spirv", "--wave-size", "64", "input").isLeft)
    assert(parse("assemble", "--isa", "spirv", "--output", "output", "input").isRight)
    assert(parse("disassemble", "--isa", "spirv", "--section", ".text", "input").isLeft)
    assert(parse(
      "assemble",
      "--isa",
      "amdgpu",
      "--target",
      "gfx1030",
      "--output",
      "output",
      "--section",
      ".text",
      "input"
    ).isLeft)
    assert(parse("disassemble", "--isa", "amdgpu", "--target", "gfx1030", "--section", ".text", "input").isRight)
  }

  test("CLI requests preserve mandatory assembly destinations and optional disassembly destinations") {
    assertEquals(
      CLI.command.parse(List("assemble", "--isa", "spirv", "input", "-o", "output")),
      Right(CLI.Request.Assemble(Assembly.Format.SPIRV, Path.of("input"), Path.of("output")))
    )
    assertEquals(
      CLI.command.parse(List("disassemble", "--isa", "ptx", "input")),
      Right(CLI.Request.Disassemble(Assembly.Format.PTX, Path.of("input"), None, None))
    )
    assertEquals(
      CLI.command.parse(List(
        "disassemble",
        "--isa",
        "amdgpu",
        "--target",
        "gfx1030",
        "input",
        "-o",
        "output",
        "--section",
        ".text"
      )),
      Right(CLI.Request.Disassemble(
        format = Assembly.Format.AMDGPU(amdgpu.Target("gfx1030")),
        input = Path.of("input"),
        output = Some(Path.of("output")),
        section = Some(".text")
      ))
    )
  }
}
