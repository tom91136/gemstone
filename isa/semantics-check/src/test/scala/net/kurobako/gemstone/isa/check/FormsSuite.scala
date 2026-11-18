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

  test("a branch is refused: what it writes is not a register") {
    assertEquals(at(0x80), Left("@!P0 BRA 0x4e0 writes guard, which is not a register"))
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
