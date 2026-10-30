package net.kurobako.gemstone.codegen

import generator.*
import java.nio.file.Files
import net.kurobako.gemstone.testing.Suite
import net.kurobako.gemstone.testing.files.*

class GeneratePTXDomainsSuite extends Suite {
  private def section(id: String, syntax: String): String =
    s"""<section id="$id"><h3>9.1. Arithmetic Instructions: add</h3>
       |<p>Syntax</p><div><pre>$syntax</pre></div>
       |<p>Examples</p><div><pre>add.u32 %r0, %r1, %r2;</pre></div></section>""".stripMargin

  private val padding = (1 to 169).map(index => section(s"padding-$index", "add.u32 d, a, b;")).mkString

  test("slots with several values become typed cases and single-value slots stay literal") {
    inDirectory { root =>
      val input =
        write(root, "ptx.html", section("fixture", "add{.sat}.type d, a, b;\n.type = { .s32, .u32 };") + padding)
      val output = root.resolve("output")
      GeneratePTXISA.main(Array("instructions", input.toString, output.toString, "9.3"))
      val slots =
        Files.readString(output.resolve("net/kurobako/gemstone/isa/ptx/ModifierSlot.scala")).replaceAll("\\s+", " ")
      assert(slots.contains("case Type extends ModifierSlot(\"type\")"), slots)
      assert(!slots.contains("\"sat\""), slots)
    }
  }
}
