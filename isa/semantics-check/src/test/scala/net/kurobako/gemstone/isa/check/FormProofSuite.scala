package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.isa.amdgpu.AMDGPUReference
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Proof.Result
import net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Verdict as Solved
import net.kurobako.gemstone.testing.*
import scala.reflect.ClassTag

/** Proofs over instructions as decoded: each step is the form of its own text. */
class FormProofSuite extends Suite {
  private val gfx906 = "gfx906".refine[amdgpu.Target].toOption.get

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def nvidia(lines: String*): Vec[Step] = Vec.from(lines).map { line =>
    val parsed = sass.asm.Parser.parseProgram(s"$line;").toOption.flatMap(_.statements.collectFirst {
      case sass.asm.Program.Statement.Operation(operation, _, _) => operation
    }).getOrElse(fail(s"$line does not parse"))
    SASSForms.of("sm_89", parsed).fold(fail(_), identity)
  }

  private def amd(lines: String*): Vec[Step] = Vec.from(lines).map(AMDGPUForms.of(gfx906, _).fold(fail(_), identity))

  private def prove[H, L <: Lookup: ClassTag](
      reference: Reference[H, L],
      left: Vec[Step],
      right: Vec[Step],
      live: String*
  ): Result = Proof.prove(
    reference,
    Equivalence.vm(reference),
    (left ++ right).flatMap(_.lane.cases),
    left,
    right,
    LiveOut(Vec.from(live), memory = false)
  )

  private val low  = "IMAD.MOV.U32 R2, RZ, RZ, c[0][0x160]"
  private val high = "IMAD.MOV.U32 R3, RZ, RZ, c[0][0x164]"

  test("a window that reads constants is proved equal to its reordering, naming the cases behind it") {
    installed()
    prove(SASSReference, nvidia(low, high), nvidia(high, low), "R2", "R3") match {
      case Result.Proved(report) =>
        assert(report.filtered > 0)
        assertEquals(report.instructions.map(_.instance), Vec(low, high))
        report.instructions.foreach(cover => assert(cover.checked.exists(_.contains("c[0x3]")), cover))
      case other => fail(s"expected a proof, got $other")
    }
  }

  test("a sum with a constant is proved equal to the constant read and then added") {
    installed()
    val left  = nvidia("IADD3 R4, R5, c[0][0x160], RZ")
    val right = nvidia("IMAD.MOV.U32 R4, RZ, RZ, c[0][0x160]", "IADD3 R4, R5, R4, RZ")
    prove(SASSReference, left, right, "R4") match {
      case Result.Proved(report) => assert(report.filtered > 0)
      case other                 => fail(s"expected a proof, got $other")
    }
  }

  test("sequences that differ in a constant-bank offset are not equivalent") {
    val left  = nvidia(low)
    val right = nvidia("IMAD.MOV.U32 R2, RZ, RZ, c[0][0x164]")
    prove(SASSReference, left, right, "R2") match {
      case Result.Rejected(_, _) => ()
      case other                 => fail(s"expected a refutation, got $other")
    }
  }

  test("the solver tells two constant-bank offsets apart, and two banks") {
    installed()
    def composed(line: String): Definition = {
      val steps = nvidia(line)
      val out   = LiveOut(Vec("R2"), memory = false)
      Proof.compose(steps, out, Equivalence.validate(steps.iterator, out).fold(fail(_), identity))
    }
    assertEquals(Proof.bitwuzla(composed(low), composed(low), false), Solved.Equal)
    Vec("IMAD.MOV.U32 R2, RZ, RZ, c[0][0x164]", "IMAD.MOV.U32 R2, RZ, RZ, c[2][0x160]").foreach { other =>
      Proof.bitwuzla(composed(low), composed(other), false) match {
        case Solved.Different(_) => ()
        case verdict             => fail(s"expected $other to differ, got $verdict")
      }
    }
  }

  test("sequences that read the same offset of two banks are not equivalent") {
    prove(SASSReference, nvidia(low), nvidia("IMAD.MOV.U32 R2, RZ, RZ, c[2][0x160]"), "R2") match {
      case Result.Rejected(_, _) => ()
      case other                 => fail(s"expected a refutation, got $other")
    }
  }

  private val carried = Vec("v2", "v3", "vcc_lo", "vcc_hi")

  test("a 64-bit sum through the carry in VCC is proved equal with its low operands swapped") {
    installed()
    val left  = amd("v_add_co_u32 v2, vcc, v4, v0", "v_add_co_ci_u32 v3, vcc, v3, v1, vcc")
    val right = amd("v_add_co_u32 v2, vcc, v0, v4", "v_add_co_ci_u32 v3, vcc, v3, v1, vcc")
    prove(AMDGPUReference, left, right, carried*) match {
      case Result.Proved(report) =>
        assert(report.filtered > 0)
        assertEquals(
          report.instructions.map(_.instance.takeWhile(_ != ' ')).distinct,
          Vec("v_add_co_u32", "v_add_co_ci_u32")
        )
      case other => fail(s"expected a proof, got $other")
    }
  }

  test("a high sum that drops the carry is not equivalent") {
    val left  = amd("v_add_co_u32 v2, vcc, v4, v0", "v_add_co_ci_u32 v3, vcc, v3, v1, vcc")
    val right = amd("v_add_co_u32 v2, vcc, v4, v0", "v_add_u32 v3, v3, v1")
    prove(AMDGPUReference, left, right, "v2", "v3") match {
      case Result.Rejected(_, _) => ()
      case other                 => fail(s"expected a refutation, got $other")
    }
  }

  test("a carry taken from another lane's bit is not proved") {
    installed()
    val left  = amd("v_addc_co_u32 v3, vcc, v3, v1, vcc")
    val right = amd("s_lshr_b64 vcc, vcc, 1", "v_addc_co_u32 v3, vcc, v3, v1, vcc")
    prove(AMDGPUReference, left, right, "v3") match {
      case Result.Proved(report) => fail(s"proved equal: $report")
      case _                     => ()
    }
  }

  test("a sequence that reads whole a mask a lane wrote is refused") {
    val steps = amd("v_cmp_lt_u32 vcc, v1, v2", "s_mov_b64 s[2:3], vcc")
    prove(AMDGPUReference, steps, steps, "s2", "s3") match {
      case Result.Refused(reason) => assertEquals(reason, "s_mov_b64 s[2:3], vcc reads vcc whole after a lane wrote it")
      case other                  => fail(s"expected a refusal, got $other")
    }
  }

  test("a span reads what an earlier instruction wrote to a register under it") {
    val shift = "v_lshlrev_b64 v[0:1], 2, v[0:1]"
    prove(AMDGPUReference, amd("v_add_u32 v0, v0, v2", shift), amd(shift), "v0", "v1") match {
      case Result.Rejected(_, _) => ()
      case other                 => fail(s"expected a refutation, got $other")
    }
  }

  test("a register read as signed and as unsigned is one location") {
    installed()
    val steps = amd("v_ashrrev_i32 v1, 31, v0", "v_add_u32 v2, v1, v0")
    prove(AMDGPUReference, steps, steps, "v1", "v2") match {
      case Result.Proved(_) => ()
      case other            => fail(s"expected a proof, got $other")
    }
  }

  test("a register written as signed is widened as unsigned by the instruction that reads it so") {
    installed()
    val left = amd(
      "v_add_u32 v2, v1, v1",
      "v_mov_b32 v4, -1",
      "v_mov_b32 v5, 0",
      "v_ashrrev_i32 v1, 31, v4",
      "v_add_co_u32 v3, vcc, v1, v5"
    )
    val right = amd("v_mov_b32 v5, 0", "v_add_co_u32 v3, vcc, v5, v5")
    prove(AMDGPUReference, left, right, "vcc_lo", "vcc_hi") match {
      case Result.Proved(_) => ()
      case other            => fail(s"expected a proof, got $other")
    }
  }
}
