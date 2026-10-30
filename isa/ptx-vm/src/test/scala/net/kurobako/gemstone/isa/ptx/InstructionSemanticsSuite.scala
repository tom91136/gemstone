package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.Suite

class InstructionSemanticsSuite extends Suite {
  private def show(instruction: String, registers: (String, String)*): Either[String, String] =
    InstructionSemantics.describe("op", instruction, Vec.from(registers)).map(ScalaBackend.show(_)).left.map(_.message)

  test("instructions render as definitions over their registers at the types they are declared as") {
    assertEquals(
      show("add.s32 dest, srcA, srcB;", "dest" -> "s32", "srcA" -> "s32", "srcB" -> "s32"),
      Right("def op(srcA: SInt[32], srcB: SInt[32]): (dest: SInt[32]) =\n  (dest = srcA + srcB)")
    )
    assertEquals(
      Vector(
        show("mul.hi.u64 d, a, b;", "d"    -> "u64", "a"  -> "u64", "b" -> "u64"),
        show("add.f32 d, a, b;", "d"       -> "f32", "a"  -> "f32", "b" -> "f32"),
        show("mad.lo.s32 d, a, 3, c;", "d" -> "s32", "a"  -> "s32", "c" -> "s32"),
        show("setp.lt.s32 p, a, b;", "p"   -> "pred", "a" -> "s32", "b" -> "s32"),
        show("selp.b32 d, a, b, c;", "d"   -> "b32", "a"  -> "b32", "b" -> "b32", "c" -> "pred"),
        show("cvt.rzi.s32.f32 d, a;", "d"  -> "s32", "a"  -> "f32")
      ),
      Vector(
        Right("def op(a: UInt[64], b: UInt[64]): (d: UInt[64]) =\n  (d = multiplyHigh(a, b))"),
        Right(
          "def op(a: Float32, b: Float32): (d: Float32) =\n  (d = FloatArithmetic.canonical(FloatArithmetic.add(a, b)))"
        ),
        Right("def op(a: SInt[32], c: SInt[32]): (d: UInt[32]) =\n  (d = a.to[UInt[32]] * 3 + c.to[UInt[32]])"),
        Right("def op(a: SInt[32], b: SInt[32]): (p: Bool) =\n  (p = a < b)"),
        Right("def op(a: UInt[32], b: UInt[32], c: Bool): (d: UInt[32]) =\n  (d = if (c) a else b)"),
        Right(
          "def op(a: Float32): (d: SInt[32]) =\n" +
            "  (d = Conversions.toSigned(a, rounding = VMIR.ConversionRounding.RZI, width = 32))"
        )
      )
    )
  }

  test("the carry flag is state each instruction that reads or writes it takes or returns") {
    assertEquals(
      show("add.cc.u32 d, a, b;", "d" -> "u32", "a" -> "u32", "b" -> "u32"),
      Right(
        "def op(a: UInt[32], b: UInt[32]): (d: UInt[32], `CC.CF`: Bool) =\n" +
          "  (\n    d = a + b + 0,\n    `CC.CF` = (a.to[UInt[64]] + b.to[UInt[64]] + 0 >> 32) != UInt[64](0)\n  )"
      )
    )
    assertEquals(
      show("addc.cc.u32 a, a, b;", "a" -> "u32", "b" -> "u32"),
      Right(
        """def op(a: UInt[32], b: UInt[32], `CC.CF`: Bool): (`CC.CF`: Bool, a: UInt[32]) = {
          |  val v0 = `CC.CF`.to[UInt[1]].to[UInt[32]]
          |  (
          |    `CC.CF` = (a.to[UInt[64]] + b.to[UInt[64]] + v0.to[UInt[64]] >> 32) != UInt[64](0),
          |    a = a + b + v0
          |  )
          |}""".stripMargin
      )
    )
  }

  test("memory, special registers and warp collectives read the lane's contexts") {
    assertEquals(
      show("ld.global.u32 d, [addr+8];", "d" -> "u32", "addr" -> "u64"),
      Right(
        "def op(addr: UInt[64])(using memory: Memory): (d: UInt[32]) =\n  (d = memory.loadGlobal[UInt[32]](addr + 8))"
      )
    )
    assertEquals(
      show("add.s32 %r0, %r1, %tid.x;", "%r0" -> "s32", "%r1" -> "s32"),
      Right(
        "def op(`%r1`: SInt[32])(using launch: Launch): (`%r0`: UInt[32]) =\n" +
          "  (`%r0` = `%r1`.to[UInt[32]] + launch.threadIdX())"
      )
    )
    assertEquals(
      show("vote.sync.ballot.b32 d, p, m;", "d" -> "b32", "p" -> "pred", "m" -> "b32"),
      Right("def op(p: Bool, m: UInt[32])(using warp: Warp): (d: UInt[32]) =\n  (d = warp.ballot(p, m))")
    )
  }

  test("instructions without staged semantics or that do not lower are not described") {
    assertEquals(show("bar.sync 0;"), Left("lowers to BarrierSync, with no staged semantics"))
    assertEquals(
      show("add.s32 d, a;", "d" -> "s32", "a" -> "s32"),
      Left("8:3: add.s32 expects 3 operands, found 2")
    )
  }
}
