package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.isa.check.Form.Operand
import net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.isa.sass.Harness
import net.kurobako.gemstone.isa.sass.LoadedProgram
import net.kurobako.gemstone.isa.sass.fixtures.Fixture
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.Workgroups

class FormsSuite extends Suite {
  private val gfx906 = "gfx906".refine[amdgpu.Target].toOption.get

  private lazy val divergent: Vec[sass.DecodedInstruction] = {
    val kernel  = Harness.load(89, Fixture.listing("divergentSm89"), "divergent")
    val program =
      LoadedProgram(kernel.module(Vec(24)), Map(kernel.name -> kernel.functions), Workgroups.Sequential, Harness.text)
    program.staticCode(kernel.name).fold(error => fail(error.message), _._3)
  }

  private def at(address: Int): Either[String, Form] = SASSForms.of("sm_89", divergent(address / 16).operation)

  /** The bank and offset of each constant read of a form's definition. */
  private def reads(form: Form): Vec[(Long, Long)] = {
    def literal(term: Term): Long = term match {
      case Term.Literal(value)                    => value
      case Term.Call(_, Vec(Term.Literal(value))) => value
      case other                                  => fail(s"${other.show} is not a literal")
    }
    val definition = form.kase.definition
    (definition.body.map(_.term) ++ definition.results.map(_.value)).flatMap(Rejection.terms(_)).collect {
      case Term.Context(method = "load", arguments = Vec(bank, offset)) => (literal(bank), literal(offset))
    }
  }

  test("a constant-bank operand is a read of the instruction's own bank and offset") {
    val form = at(0x00).fold(fail(_), identity)
    assertEquals(form.kase.instance, "IMAD.MOV.U32 R1, RZ, RZ, c[0][0x28]")
    assertEquals(reads(form), Vec((0L, 0x28L)))
    assertEquals(form.parameters, Map.empty[String, Operand])
    assertEquals(form.results, Map("R1" -> Vec("R1")))
  }

  test("the cases behind a constant form are the checked cases of its mnemonic that read a constant") {
    val form = at(0x00).fold(fail(_), identity)
    assert(form.lane.cases.nonEmpty)
    form.lane.cases.foreach(kase => assert(kase.instance.startsWith("IMAD") && kase.instance.contains("c[0x3]"), kase))
  }

  test("a wide product binds the registers it reads and each register of the pair it writes") {
    val form = at(0x40).fold(fail(_), identity)
    assertEquals(form.parameters, Map("R6" -> Operand.Location("R6"), "R3" -> Operand.Location("R3")))
    assertEquals(form.results, Map("R2" -> Vec("R2"), "R3" -> Vec("R3")))
    assertEquals(reads(form), Vec((0L, 0x160L)))
  }

  test("a form with no rendering is refused with the reason") {
    assertEquals(at(0x500), Left("BSYNC B0 has no rendering: lowers to Convergence, with no staged semantics"))
    assertEquals(at(0x540), Left("EXIT has no rendering: lowers to Return, with no staged semantics"))
    assertEquals(
      AMDGPUForms.of(gfx906, "s_waitcnt 49279").left.map(_.takeWhile(_ != ':')),
      Left("s_waitcnt 49279 has no rendering")
    )
  }

  test("a form that reaches shared memory, launch state or a uniform register is refused by its own definition") {
    assertEquals(at(0x10).left.map(_.contains("is refused: Launch.")), Left(true))
    assertEquals(at(0x30).left.map(_.contains("is refused: Warp.")), Left(true))
    assertEquals(
      AMDGPUForms.of(gfx906, "ds_read_b32 v1, v2").left.map(_.contains("is refused: LocalMemory.")),
      Left(true)
    )
  }

  /** The widths in bits of the machine's plain loads and stores of a form's definition, in order. */
  private def accesses(form: Form): Vec[(String, Int)] = {
    val definition = form.kase.definition
    (definition.body.map(_.term) ++ definition.results.map(_.value)).flatMap(Rejection.terms(_)).collect {
      case Term.Context(contextType = owner, method = method, typeArguments = Vec(tpe))
          if owner == RenderMachine.Machine.contextType => (method, tpe.bits)
    }
  }

  test("a load or store of global memory is the machine's plain access at the instruction's own address") {
    val load = at(0x50).fold(fail(_), identity)
    assertEquals(load.kase.instance, "LDG.E.CONSTANT R5, [R2.64]")
    assertEquals(accesses(load), Vec(("load", 32)))
    assertEquals(load.parameters, Map("R2" -> Operand.Joined(Vec("R2", "R3"))))
    assertEquals(load.results, Map("R5" -> Vec("R5")))
    assert(load.lane.cases.nonEmpty)
    load.lane.cases.foreach(kase => assert(kase.instance.startsWith("LDG"), kase))
    val store = at(0x530).fold(fail(_), identity)
    assertEquals(accesses(store), Vec(("store", 32)))
    assertEquals(store.results, Map.empty[String, Vec[String]])
    val wide = AMDGPUForms.of(gfx906, "global_store_dwordx4 v0, v[1:4], s[16:17]").fold(fail(_), identity)
    assertEquals(accesses(wide), Vec.fill(4)(("store", 32)))
    val scalar = AMDGPUForms.of(gfx906, "s_load_dword s9, s[4:5], 4").fold(fail(_), identity)
    assertEquals(accesses(scalar), Vec(("load", 32)))
    assertEquals(scalar.parameters, Map("s[4:5]" -> Operand.Joined(Vec("s4", "s5"))))
  }

  test("a guarded branch is refused as a branch") {
    assertEquals(at(0x80), Left("@!P0 BRA 0x4e0 has no rendering: lowers to Branch, with no staged semantics"))
  }

  private def written(text: String): Either[String, Form] =
    sass.asm.Parser.parseProgram(s"$text;").toOption.flatMap(_.statements.collectFirst {
      case sass.asm.Program.Statement.Operation(operation, _, _) => operation
    }).toRight(s"$text does not parse").flatMap(SASSForms.of("sm_89", _))

  private def admitted(text: String): Form = written(text).fold(fail(_), identity)

  test("a guarded instruction reads its guard and what it writes, which it leaves where the guard fails") {
    val move = admitted("@P0 MOV R2, R3")
    assertEquals(
      move.parameters,
      Map("P0" -> Operand.Location("P0"), "R3" -> Operand.Location("R3"), "R2" -> Operand.Location("R2"))
    )
    assertEquals(move.results, Map("R2" -> Vec("R2")))
    move.kase.definition.results.map(_.value) match {
      case Vec(Term.Select(_, _, Term.Variable(kept))) => assertEquals(kept, "R2")
      case other                                       => fail(other.map(_.show).mkString)
    }
    val sum = admitted("@!P1 IADD3 R1, R2, R3, R4")
    assertEquals(sum.parameters.keySet, Set("P1", "R1", "R2", "R3", "R4"))
    assertEquals(sum.results, Map("R1" -> Vec("R1")))
    val compared = admitted("@P0 ISETP.GE.AND P2, PT, R1, R2, PT")
    assertEquals(compared.parameters.keySet, Set("P0", "P2", "R1", "R2"))
    assertEquals(compared.results, Map("P2" -> Vec("P2")))
    val own = admitted("@P0 ISETP.GE.AND P0, PT, R1, R2, PT")
    assertEquals(own.parameters.keySet, Set("P0", "R1", "R2"))
    assertEquals(own.results, Map("P0" -> Vec("P0")))
  }

  test("the cases behind a guarded form are the checked cases of its mnemonic under a guard of its sense") {
    val held = admitted("@P0 MOV R2, R3").lane.cases.map(_.instance)
    assert(held.nonEmpty && held.forall(_.matches(raw"@P\d MOV .*")), held)
    val failed = admitted("@!P1 IADD3 R1, R2, R3, R4").lane.cases.map(_.instance)
    assert(failed.nonEmpty && failed.forall(_.matches(raw"@!P\d IADD3 .*")), failed)
    val plain = admitted("MOV R2, R3").lane.cases.map(_.instance)
    assert(plain.nonEmpty && plain.forall(_.startsWith("MOV")), plain)
    assertEquals(
      written("@UP0 MOV R2, R3"),
      Left("@UP0 MOV R2, R3 has no checked case of @UP MOV with operands R, R")
    )
  }

  test("a guarded load or store makes its access only where the guard holds") {
    val load = admitted("@P0 LDG.E R5, [R2.64]")
    assertEquals(accesses(load), Vec(("load", 32)))
    assertEquals(
      load.parameters,
      Map("P0" -> Operand.Location("P0"), "R2" -> Operand.Joined(Vec("R2", "R3")), "R5" -> Operand.Location("R5"))
    )
    assertEquals(load.results, Map("R5" -> Vec("R5")))
    assertEquals(load.kase.definition.body, Vec.empty[Definition.Statement])
    load.kase.definition.results.map(_.value) match {
      case Vec(Term.Select(_, taken, Term.Variable(kept))) =>
        assertEquals(kept, "R5")
        assertEquals(Rejection.terms(taken).count { case _: Term.Context => true; case _ => false }, 1)
      case other => fail(other.map(_.show).mkString)
    }
    assert(load.lane.cases.nonEmpty && load.lane.cases.forall(_.instance.matches(raw"@P\d LDG.*")), load.lane.cases)
    val store = admitted("@!P0 STG.E [R2.64], R5")
    assertEquals(accesses(store), Vec(("store", 32)))
    assertEquals(store.results, Map.empty[String, Vec[String]])
    store.kase.definition.body.map(_.term) match {
      case Vec(Term.When(_, Vec(Term.Step(_, _: Term.Context)))) => ()
      case other                                                 => fail(other.map(_.show).mkString)
    }
    assert(
      store.lane.cases.nonEmpty && store.lane.cases.forall(_.instance.matches(raw"@!P\d STG.*")),
      store.lane.cases
    )
  }

  test("what is refused with no guard is refused under one for the same reason") {
    Vec(
      "BRA 0x20"                     -> "has no rendering: lowers to Branch, with no staged semantics",
      "CALL.REL.NOINC 0x10"          -> "has no rendering: lowers to Branch, with no staged semantics",
      "RET.REL.NODEC R20, 0x0"       -> "has no rendering: lowers to Branch, with no staged semantics",
      "BSSY B0, 0x20"                -> "has no rendering: lowers to Convergence, with no staged semantics",
      "BSYNC B0"                     -> "has no rendering: lowers to Convergence, with no staged semantics",
      "EXIT"                         -> "has no rendering: lowers to Return, with no staged semantics",
      "LDS R5, [R2]"                 -> "is refused: Memory.loadShared is not a plain memory access",
      "UMOV UR4, 0x1"                -> "is refused: Warp.leader is not a plain memory access",
      "LDG.E.64 R4, [R2.64]"         -> "is refused: Memory.loadGlobal is not a plain memory access",
      "LDC R5, c[0x3][R2]"           -> "reads a constant bank at an offset a register holds",
      "IADD3 R1, P1, P2, R2, R3, R4" -> "has no checked case of"
    ).foreach { (text, reason) =>
      assert(written(text).left.exists(_.contains(reason)), written(text))
      assert(written(s"@P0 $text").left.exists(_.contains(reason)), written(s"@P0 $text"))
      assert(written(s"@!P0 $text").left.exists(_.contains(reason)), written(s"@!P0 $text"))
    }
  }

  test("a guarded barrier, which lowers to a branch over its body, is refused") {
    assertEquals(
      written("@P0 BAR.SYNC 0x0"),
      Left("@P0 BAR.SYNC 0x0 has no rendering: lowers to Branch, with no staged semantics")
    )
  }

  test("every instruction of the kernel is admitted or refused with a reason") {
    divergent.foreach(decoded =>
      SASSForms.of("sm_89", decoded.operation).left.foreach(reason => assert(reason.nonEmpty))
    )
  }

  test("a carry form reads and writes both halves of VCC") {
    val form = AMDGPUForms.of(gfx906, "v_add_co_ci_u32 v3, vcc, v3, v1, vcc").fold(fail(_), identity)
    assertEquals(
      form.parameters,
      Map(
        "v3"  -> Operand.Location("v3"),
        "v1"  -> Operand.Location("v1"),
        "vcc" -> Operand.Joined(Vec("vcc_lo", "vcc_hi"))
      )
    )
    assertEquals(form.results, Map("v3" -> Vec("v3"), "vcc" -> Vec("vcc_lo", "vcc_hi")))
    assert(
      form.lane.cases.forall(_.instance.startsWith("v_add_co_ci_u32")) && form.lane.cases.nonEmpty,
      form.lane.cases
    )
  }

  test("a span binds the registers under it") {
    val form = AMDGPUForms.of(gfx906, "v_lshlrev_b64 v[0:1], 2, v[0:1]").fold(fail(_), identity)
    assertEquals(form.parameters, Map("v[0:1]" -> Operand.Joined(Vec("v0", "v1"))))
    assertEquals(form.results, Map("v[0:1]" -> Vec("v0", "v1")))
  }

  test("a literal binds its value") {
    val form = AMDGPUForms.of(gfx906, "s_and_b32 s4, s9, lit(0xffff)").fold(fail(_), identity)
    assertEquals(form.parameters.get("0x1234"), Some(Operand.Immediate(0xffffL)))
  }
}
