package net.kurobako.gemstone.tools.isa

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.v9_3 as ptx

/** Loaded only by the isolated runtime-boundary check, never by the normal ISA tools artifact. */
object NvidiaRuntimeCheck {
  def main(arguments: Array[String]): Unit = {
    require(arguments.isEmpty, "runtime boundary harness accepts no arguments")
    val loader = getClass.getClassLoader
    Vec(
      "net.kurobako.gemstone.isa.ptx.v9_3.asm.Assembler$",
      "net.kurobako.gemstone.isa.sass.asm.Assembler$"
    ).foreach { name =>
      val present = try {
        val _ = Class.forName(name, false, loader)
        true
      } catch {
        case _: ClassNotFoundException => false
      }
      require(!present, s"assembly class leaked into runtime harness: $name")
    }
    ptx.PtxRuntimeCheck.run()
    sass.SassRuntimeCheck.run()
    println("PTX memory store and binary-decoded SASS execution passed without asm")
  }
}
