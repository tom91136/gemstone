package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class CubinFP64BudgetSuite extends Suite {
  private val image: Vec[Byte] = Vec.unsafeWrapArray(
    getClass.getResourceAsStream("/net/kurobako/gemstone/isa/sass/controlwords/branches.sm_89.cubin").readAllBytes()
  )
  private val target = Target.parse("sm_89").value
  private val module = Cubin.decode(image).value
  private val kernel = module.kernels.find(_.name == "sumloop").getOrElse(fail("missing sumloop"))

  private def code(text: String): Vec[Byte] = {
    val program = Parser.parseProgram(text).fold(e => fail(e.toString), identity)
    new Assembler(target).encode(program).fold(e => fail(e.toString), identity)
  }

  private def budget(registerCount: Int, base: Int): String =
    s"FP64 register pair R$base/R${base + 1} exceeds the $registerCount-register budget of sumloop"

  test("a double-precision pair inside the kernel register budget relinks") {
    val text =
      "LDC.64 R2, c[0x0][0x160];\n" ++
        "DADD R34, R34, R30;\n" ++
        "STG.E [R2], R34;\n" ++
        "EXIT;\n"
    assertEquals(Cubin.replaceKernel(image, "sumloop", code(text)).map(_ => ()), Right(()))
  }

  test("a double-precision pair past the kernel register budget is rejected") {
    val text =
      "LDC.64 R2, c[0x0][0x160];\n" ++
        "DADD R36, R36, R30;\n" ++
        "STG.E [R2], R34;\n" ++
        "EXIT;\n"
    assertEquals(
      Cubin.replaceKernel(image, "sumloop", code(text)).left.map(_.raw),
      Left(budget(kernel.registerCount.raw, 36))
    )
  }

  test("all double-precision opcodes share the register budget") {
    val opcodes = Vector("DMUL", "DFMA")
    opcodes.foreach { opcode =>
      val operands = if (opcode == "DFMA") "R36, R36, R30, R28" else "R36, R36, R30"
      val text     =
        "LDC.64 R2, c[0x0][0x160];\n" ++
          s"$opcode $operands;\n" ++
          "STG.E [R2], R34;\n" ++
          "EXIT;\n"
      assertEquals(
        Cubin.replaceKernel(image, "sumloop", code(text)).left.map(_.raw),
        Left(budget(kernel.registerCount.raw, 36))
      )
    }
  }
}
