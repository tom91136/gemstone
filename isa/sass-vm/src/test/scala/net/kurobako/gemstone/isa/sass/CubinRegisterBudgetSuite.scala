package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.either.*

class CubinRegisterBudgetSuite extends Suite {
  private val image: Vec[Byte] = Vec.unsafeWrapArray(
    getClass.getResourceAsStream("/net/kurobako/gemstone/isa/sass/controlwords/branches.sm_89.cubin").readAllBytes()
  )
  private val target = Target.parse("sm_89").value

  // Highest general register index an sm_89 card accepts, measured on an RTX 4060 Ti by patching EIATTR_REGCOUNT.
  private val Boundaries = Vector(36 -> 33, 37 -> 34, 38 -> 35, 39 -> 36, 40 -> 37, 41 -> 38, 42 -> 39)

  private def withRegisters(count: Int): Vec[Byte] = {
    val record = Vec[Byte](0x04, 0x2f, 0x08, 0x00, 0x13, 0x00, 0x00, 0x00)
    val at     = image.indexOfSlice(record)
    assert(at >= 0, "sumloop has no EIATTR_REGCOUNT record")
    image.updated(at + 8, count.toByte)
  }

  private def code(insn: String): Vec[Byte] = {
    val text    = s"LDC.64 R2, c[0x0][0x160];\n$insn;\nEXIT;\n"
    val program = Parser.parseProgram(text).fold(e => fail(e.toString), identity)
    new Assembler(target).encode(program).fold(e => fail(e.toString), identity)
  }

  test("every register operand, of any width, obeys the kernel register budget") {
    Boundaries.foreach { (count, highest) =>
      val image      = withRegisters(count)
      val pairBase   = (highest - 1) / 2 * 2
      val quadBase   = (highest - 3) / 4 * 4
      val candidates = Vector(
        s"MOV R$highest, R2"                           -> true,
        s"MOV R${highest + 1}, R2"                     -> false,
        s"IADD3 R$highest, R2, R4, RZ"                 -> true,
        s"IADD3 R${highest + 1}, R2, R4, RZ"           -> false,
        s"IMAD.WIDE.U32 R$pairBase, R2, R4, RZ"        -> true,
        s"IMAD.WIDE.U32 R${pairBase + 2}, R2, R4, RZ"  -> false,
        s"LDG.E.64 R$pairBase, [R2.64]"                -> true,
        s"LDG.E.64 R${pairBase + 2}, [R2.64]"          -> false,
        s"STG.E.64 [R2.64], R$pairBase"                -> true,
        s"STG.E.64 [R2.64], R${pairBase + 2}"          -> false,
        s"LDG.E.128 R$quadBase, [R2.64]"               -> true,
        s"LDG.E.128 R${quadBase + 4}, [R2.64]"         -> false,
        s"DADD R$pairBase, R$pairBase, R2"             -> true,
        s"DADD R${pairBase + 2}, R${pairBase + 2}, R2" -> false
      )
      val declared = Cubin.decode(image).value.kernels.find(_.name == "sumloop").getOrElse(fail("missing sumloop"))
      assertEquals(declared.registerCount.raw, count)
      candidates.foreach { (insn, legal) =>
        val relinked = Cubin.replaceKernel(image, "sumloop", code(insn)).map(_ => ())
        assertEquals(relinked.isRight, legal, s"$insn with $count registers")
      }
    }
  }
}
