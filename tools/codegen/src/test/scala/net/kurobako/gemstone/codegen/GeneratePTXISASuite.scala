package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.testing.files.*

class GeneratePTXISASuite extends munit.FunSuite {
  private def section(id: String, syntax: String, examples: String, notes: String = ""): String =
    s"""<section id="$id"><h3>9.1. Arithmetic Instructions: add</h3>
       |<p>Syntax</p><div><pre>$syntax</pre></div>
       |<p>PTX ISA Notes</p><p>$notes</p>
       |<p>Examples</p><div><pre>$examples</pre></div></section>""".stripMargin

  // The CLI rejects truncated documents. Repeat one simple section to meet its pinned-document sanity checks.
  private val padding = (1 to 169).map { index =>
    section(s"padding-$index", "add.u32 d, a, b;", List.fill(5)("add.u32 %r0, %r1, %r2;").mkString("\n"))
  }.mkString
  private val special = section(
    "fixture",
    "add{.sat}.type d, a, b;\n.type = { .s32, .u32 };",
    "// ignored comment\nExample 1: @!p add.u32 %r0,\n%r1, %r2;",
    "Introduced in PTX ISA version 7.2. Requires sm_80 or higher."
  )

  private def generate(mode: String, input: Path, output: Path): Unit =
    GeneratePTXISA.main(Array(mode, input.toString, output.toString, "9.3", "v9_3"))
  private def source(output: Path, name: String): String =
    Files.readString(output.resolve(s"net/kurobako/gemstone/isa/ptx/v9_3/$name.scala")).replaceAll("\\s+", " ")

  test("HTML syntax produces typed operands, modifier choices and target/version metadata") {
    inDirectory { root =>
      val input   = write(root, "ptx.html", special + padding)
      val output  = root.resolve("output")
      val sibling = write(output, "net/kurobako/gemstone/isa/sass/Instruction.scala", "// SASS sibling\n")
      generate("instructions", input, output)
      assertEquals(Files.readString(sibling), "// SASS sibling\n")
      val actual    = source(output, "Instruction")
      val modifiers = source(output, "Modifier")
      val opcodes   = source(output, "Opcode")
      assert(actual.contains("case Add("), actual.take(500))
      assert(actual.contains("dest: Operand, srcA: Operand, srcB: Operand"), actual.takeRight(1500))
      assert(actual.contains("Version(7, 2), Target(80)"))
      assert(actual.contains("Opcode.Add"))
      assert(actual.contains("ModifierGroup(Modifier.S32, Modifier.U32)"))
      assert(actual.contains("Set(Modifier.Sat)"))
      assert(modifiers.contains("case U32 extends Modifier(\"u32\")"))
      assert(modifiers.contains("case Unrecognized(spelling: NonEmpty[String])"))
      assert(opcodes.contains("case Add extends Opcode(\"add\")"))
      assert(actual.contains("val version: Version = Version(9, 3)"))
      val before = snapshot(output)
      generate("instructions", input, output)
      assertEquals(snapshot(output), before)
    }
  }

  test("documentation examples join continued lines and preserve predication") {
    inDirectory { root =>
      val input  = write(root, "ptx.html", special + padding)
      val output = root.resolve("output")
      generate("tests", input, output)
      val actual = source(output, "DocumentationSuite")
      assert(actual.contains("\"fixture\" -> \"@!p add.u32 %r0, %r1, %r2;\""), actual.take(700))
      assert(!actual.contains("Example 1:"))
      assert(!actual.contains("ignored comment"))
      assertEquals(" -> ".r.findAllIn(actual).size, 846)
    }
  }

  test("chained modifier definitions retain separate source and destination groups") {
    inDirectory { root =>
      val input = write(
        root,
        "ptx.html",
        section(
          "conversion",
          "cvt.dtype.atype d, a;\n.dtype = .atype = { .f32, .u32 };",
          "cvt.f32.f32 %r0, %r1;"
        ) + padding
      )
      val output = root.resolve("output")
      generate("instructions", input, output)
      val actual = source(output, "Instruction")
      val group  = "ModifierGroup(Modifier.F32, Modifier.U32)"
      assert(
        actual.filterNot(_.isWhitespace).contains(s"Vec($group,$group)".filterNot(_.isWhitespace)),
        actual.takeRight(2500)
      )
      assert(!actual.contains("Modifier.Dtype"))
    }
  }

  test("numeric cache sizes in qualified modifiers remain typed") {
    inDirectory { root =>
      val input = write(
        root,
        "ptx.html",
        section(
          "async-copy",
          "cp.async.ca.shared.global{.level}.type [dst], [src], size;\n.level = { .L2::64B, .L2::128B, .L2::256B };\n.type = { .b32 };",
          "cp.async.ca.shared.global.L2::128B [dst], [src], 16;"
        ) + padding
      )
      val output = root.resolve("output")
      generate("instructions", input, output)
      val modifiers = source(output, "Modifier")
      for (size <- List(64, 128, 256))
        assert(modifiers.contains(s"extends Modifier(\"L2::${size}B\")"))
      val instructions = source(output, "Instruction")
      assert(instructions.contains("Modifier.L2128B"))
    }
  }

  test("optional subqualifiers compose typed modifiers from their spec definition") {
    inDirectory { root =>
      val input = write(
        root,
        "ptx.html",
        section(
          "query-cancel",
          "clusterlaunchcontrol.query_cancel.get_first_ctaid{::dimension}.b32.b128 dest, response;\n::dimension = { ::x, ::y, ::z };",
          "clusterlaunchcontrol.query_cancel.get_first_ctaid::x.b32.b128 %r0, %wide;"
        ) + padding
      )
      val output = root.resolve("output")
      generate("instructions", input, output)
      val modifiers = source(output, "Modifier")
      for (axis <- List("x", "y", "z"))
        assert(modifiers.contains(s"extends Modifier(\"get_first_ctaid::$axis\")"))
    }
  }

  test("truncated documents and missing example corpora do not replace generated output") {
    inDirectory { root =>
      val input  = write(root, "ptx.html", special + padding)
      val output = root.resolve("output")
      generate("instructions", input, output)
      val before = snapshot(output)
      Files.writeString(input, special)
      val _ = intercept[IllegalStateException](generate("instructions", input, output))
      assertEquals(snapshot(output), before)
      Files.writeString(input, (1 to 170).map(i => section(s"empty-$i", "add.u32 d, a, b;", "")).mkString)
      val _ = intercept[IllegalStateException](generate("tests", input, output))
      assertEquals(snapshot(output), before)
    }
  }
}
