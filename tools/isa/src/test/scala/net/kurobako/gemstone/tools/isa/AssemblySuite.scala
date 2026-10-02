package net.kurobako.gemstone.tools.isa

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3 as ptx
import net.kurobako.gemstone.isa.spirv.v1_6 as spirv

class AssemblySuite extends munit.FunSuite {
  test("SPIR-V frontend retains typed opcodes and exact binary headers") {
    val source = "OpCapability Shader\nOpMemoryModel Logical GLSL450\n%1 = OpTypeInt 32 0\n%2 = OpConstant %1 42"
    val bytes  = Assembly.assemble(source, Assembly.Format.SPIRV).fold(error => fail(error.toString), identity)
    val text   = Assembly.disassemble(bytes, Assembly.Format.SPIRV).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(text, Assembly.Format.SPIRV), Right(bytes))
    val module = spirv.ISA.decode(bytes).fold(error => fail(error.toString), identity)
    assertEquals(
      module.instructions.map(_.definition),
      Vec(
        spirv.Instruction.Definition.Capability,
        spirv.Instruction.Definition.MemoryModel,
        spirv.Instruction.Definition.TypeInt,
        spirv.Instruction.Definition.Constant
      )
    )
  }

  test("AMDGPU frontend advances over literal words and reports the failing byte offset") {
    val format = Assembly.Format.AMDGPU(amdgpu.Target("gfx1030"))
    val source = "s_mov_b32 s0, lit(1)\ns_add_u32 s1, s0, 2\ns_endpgm"
    val bytes  = Assembly.assemble(source, format).fold(error => fail(error.toString), identity)
    assertEquals(bytes.size, 16)
    val text = Assembly.disassemble(bytes, format).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(text, format), Right(bytes))
    Assembly.disassemble(bytes.dropRight(1), format) match {
      case Left(Assembly.Error.AMDGPUDecoding(offset, _)) => assertEquals(offset.raw, 12)
      case result => fail(s"expected truncation at the third instruction: $result")
    }
  }

  test("AMDGPU frontend keeps dual-issue packets and image payloads aligned") {
    val format = Assembly.Format.AMDGPU(amdgpu.Target("gfx1100"))
    val source = "v_dual_add_f32 v255, v4, v2 :: v_dual_mul_dx9_zero_f32 v6, v1, v3\n" +
      "image_atomic_add v[4:5], [v32, v1, v2], s[96:103] dmask:3 dim:SQ_RSRC_IMG_CUBE glc\n" +
      "s_endpgm"
    val bytes = Assembly.assemble(source, format).fold(error => fail(error.toString), identity)
    assertEquals(bytes.size, 24)
    val text = Assembly.disassemble(bytes, format).fold(error => fail(error.toString), identity)
    assert(text.contains(" :: "))
    assertEquals(Assembly.assemble(text, format), Right(bytes))
    Assembly.disassemble(bytes.take(19), format) match {
      case Left(Assembly.Error.AMDGPUDecoding(offset, _)) => assertEquals(offset.raw, 8)
      case result => fail(s"expected truncated image payload after the pair: $result")
    }
  }

  test("AMDGPU frontend resolves labels and rejects malformed ELF images") {
    val format = Assembly.Format.AMDGPU(amdgpu.Target("gfx1030"))
    assert(Assembly.assemble("entry:\ns_endpgm", format).isRight)
    assert(Assembly.assemble(".text\ns_endpgm", format).isRight)
    assert(Assembly.assemble("s_branch missing", format).isLeft)
    assert(Assembly.disassemble(Vec[Byte](0x7f, 'E'.toByte, 'L'.toByte, 'F'.toByte), format).isLeft)
  }

  test("ELF disassembly extracts executable bytes and requires a choice for multiple sections") {
    val target = amdgpu.Target("gfx1030")
    val format = Assembly.Format.AMDGPU(target)
    // Fixed SOPP END_PGM and NOP words, not produced by the codec under test.
    val end    = Vec(0x00, 0x00, 0x81, 0xbf).map(_.toByte)
    val nop    = Vec(0x03, 0x00, 0x80, 0xbf).map(_.toByte)
    val single = elfImage(Vec(".text" -> end))
    val text   = Assembly.disassemble(single, format).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(text, format), Right(end))
    val multiple = elfImage(Vec(".text" -> end, ".kernel" -> nop))
    assert(Assembly.disassemble(multiple, format).isLeft)
    val selected =
      Assembly.disassembleCodeObject(multiple, target, Some(".kernel")).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(selected, format), Right(nop))
    assert(Assembly.disassembleCodeObject(multiple, target, Some(".missing")).isLeft)
    assert(Assembly.disassemble(elfImage(Vec.empty[(String, Vec[Byte])]), format).isLeft)
  }

  test("PTX is canonical text with a stable typed program") {
    val source = ".version 9.3\n.target sm_100\n.address_size 64\n.visible .entry kernel() { ret; }"
    val bytes  = Assembly.assemble(source, Assembly.Format.PTX).fold(error => fail(error.toString), identity)
    val text   = Assembly.disassemble(bytes, Assembly.Format.PTX).fold(error => fail(error.toString), identity)
    assertEquals(ptx.Parser.parse(text), ptx.Parser.parse(source))
    assertEquals(Vec.toArray(bytes).toSeq, text.getBytes(StandardCharsets.UTF_8).toSeq)
    assertEquals(Assembly.assemble(text, Assembly.Format.PTX), Right(bytes))
  }

  test("SASS frontend preserves machine encodings and legacy bundle controls") {
    Vec(35, 50, 89).foreach { sm =>
      val target = sass.Target.parse(s"sm_$sm").fold(fail(_), identity)
      val format = Assembly.Format.SASS(target)
      val bytes  = Assembly.assemble("entry: MOV R3, R7; BRA entry;", format)
        .fold(error => fail(error.toString), identity)
      val text = Assembly.disassemble(bytes, format).fold(error => fail(error.toString), identity)
      assertEquals(Assembly.assemble(text, format), Right(bytes))
      assert(Assembly.disassemble(bytes.dropRight(1), format).isLeft)
    }
  }

  test("cubin disassembly selects executable sections and checks the target") {
    val target = sass.Target.parse("sm_89").fold(fail(_), identity)
    val format = Assembly.Format.SASS(target)
    // Fixed SM89 EXIT bytes, independent of the assembler under test.
    val exit   = Vec(0x4d, 0x79, 0, 0, 0, 0, 0, 0, 0, 0, 0x80, 3, 0, 0xea, 0x0f, 0).map(_.toByte)
    val single = elfImage(Vec(".text.kernel" -> exit), Some(89))
    val text   = Assembly.disassemble(single, format).fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(text, format), Right(exit))
    val multiple = elfImage(Vec(".text.first" -> exit, ".text.second" -> exit), Some(89))
    assert(Assembly.disassemble(multiple, format).isLeft)
    val selected = Assembly.disassembleCubin(multiple, target, Some(".text.second"))
      .fold(error => fail(error.toString), identity)
    assertEquals(Assembly.assemble(selected, format), Right(exit))
    assert(Assembly.disassembleCubin(multiple, target, Some(".missing")).isLeft)
    assert(Assembly.disassemble(elfImage(Vec(".text.kernel" -> exit), Some(80)), format).isLeft)
    assert(Assembly.disassemble(elfImage(Vec.empty, Some(89)), format).isLeft)
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

  /** Minimal ELF64 fixture: null section, string table, then executable sections. */
  private def elfImage(sections: Vec[(String, Vec[Byte])], cudaSM: Option[Int] = None): Vec[Byte] = {
    val names        = "\u0000.shstrtab\u0000" + sections.map(_._1).mkString("\u0000") + "\u0000"
    val strings      = names.getBytes(StandardCharsets.UTF_8)
    val headerBytes  = 64
    val sectionBytes = 64
    val tableEnd     = headerBytes + (sections.size + 2) * sectionBytes
    val bytes        = Array.ofDim[Byte](tableEnd + strings.length + sections.map(_._2.size).sum)
    val buffer       = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
    // ELF64 header offsets; independent of the production ELF reader.
    buffer.putInt(0, 0x464c457f).put(4, 2.toByte).put(5, 1.toByte).put(6, 1.toByte)
      .putShort(16, 1.toShort).putShort(18, (if (cudaSM.isDefined) 190 else 224).toShort).putInt(20, 1)
      .put(8, (if (cudaSM.isDefined) 7 else 0).toByte).putInt(48, cudaSM.getOrElse(0))
      .putLong(40, headerBytes.toLong).putShort(52, headerBytes.toShort)
      .putShort(58, sectionBytes.toShort).putShort(60, (sections.size + 2).toShort).putShort(62, 1.toShort)
    buffer.putInt(headerBytes + sectionBytes, 1).putInt(headerBytes + sectionBytes + 4, 3)
      .putLong(
        headerBytes + sectionBytes + 24,
        tableEnd.toLong
      ).putLong(headerBytes + sectionBytes + 32, strings.length.toLong)
    Array.copy(strings, 0, bytes, tableEnd, strings.length)
    val _ = sections.zipWithIndex.foldLeft(tableEnd + strings.length) { case (offset, ((name, code), index)) =>
      val start = headerBytes + (index + 2) * sectionBytes
      buffer.putInt(start, names.indexOf(name)).putInt(start + 4, 1).putLong(start + 8, 6L)
        .putLong(start + 24, offset.toLong).putLong(start + 32, code.size.toLong).putLong(start + 48, 4L)
      Array.copy(Vec.toArray(code), 0, bytes, offset, code.size)
      offset + code.size
    }
    Vec.unsafeWrapArray(bytes)
  }
}
