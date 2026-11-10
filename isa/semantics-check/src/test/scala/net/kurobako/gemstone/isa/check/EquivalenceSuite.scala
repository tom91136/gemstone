package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Equivalence.Verdict
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import scala.reflect.ClassTag

/** Golden pairs for the differential filter: equivalent pairs survive, different pairs give a counterexample. */
abstract class EquivalenceSuite[H, L <: Lookup](reference: Reference[H, L], cases: Vec[Case])(using ClassTag[L])
    extends Suite {
  private val executor = Equivalence.vm(reference)

  protected val a      = Operand.Location("a")
  protected val b      = Operand.Location("b")
  private val zero     = Operand.Immediate(0L)
  private val one      = Operand.Immediate(1L)
  private val negative = Operand.Immediate(0x80000000L)

  protected def sum(destination: String, left: Operand, right: Operand): Step

  protected def fsum(destination: String, left: Operand, right: Operand): Step

  private def register(tpe: Type): Boolean = tpe match {
    case Type.Integer(32, _) | Type.Float(32) => true
    case _                                    => false
  }

  protected def named(mnemonic: String, arity: Int): Case =
    cases.find(kase =>
      kase.definition.name == mnemonic && kase.definition.results.size == 1 &&
        kase.definition.parameters.size == arity &&
        kase.definition.parameters.forall(parameter => register(parameter.tpe))
    ).getOrElse(fail(s"no $mnemonic with $arity register operands and one result"))

  protected def exact(instance: String): Case =
    cases.find(_.instance == instance).getOrElse(fail(s"no instance $instance"))

  protected def step(kase: Case, operands: Vec[Operand], results: Vec[String]): Step = {
    val parameters = kase.definition.parameters
    val outputs    = kase.definition.results
    if (operands.size != parameters.size)
      fail(s"${kase.instance} takes ${parameters.size} operands, not ${operands.size}")
    if (results.size != outputs.size)
      fail(s"${kase.instance} has ${outputs.size} results, not ${results.size}")
    Step(kase, parameters.map(_.name).zip(operands).toMap, outputs.map(_.name).zip(results).toMap)
  }

  protected def survives(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): Unit =
    Equivalence.check(reference, executor, left, right, liveOut) match {
      case Verdict.NotRejected(_) => ()
      case other                  => fail(s"expected not rejected, got $other")
    }

  protected def refused(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): String =
    Equivalence.check(reference, executor, left, right, liveOut) match {
      case Verdict.Refused(reason) => reason
      case other                   => fail(s"expected refused, got $other")
    }

  private def withBody(step: Step, statement: Definition.Statement): Step =
    step.copy(kase = step.kase.copy(definition = step.kase.definition.copy(body = Vec(statement))))

  private def withoutDefinition(step: Step): Step =
    step.copy(kase = step.kase.copy(definition = step.kase.definition.copy(body = Vec.empty, results = Vec.empty)))

  private def retyped(step: Step, tpe: Type): Step = {
    val parameters = step.kase.definition.parameters
    val first      = parameters.head.name
    val updated    = parameters.map(parameter => if (parameter.name == first) (name = first, tpe = tpe) else parameter)
    step.copy(kase = step.kase.copy(definition = step.kase.definition.copy(parameters = updated)))
  }

  private def call(
      owner: (contextType: String, context: String),
      method: String,
      result: Option[Type],
      effect: Boolean = false
  ): Term =
    Term.context(owner, MachineCall(method, Vec.empty, Vec.empty), result, effect)

  private def differs(left: Vec[Step], right: Vec[Step], liveOut: LiveOut, runs: Int = 64): Counterexample =
    Equivalence.check(reference, executor, left, right, liveOut, runs) match {
      case Verdict.Rejected(counterexample @ Counterexample(Difference.Value("d", _, _), _)) => counterexample
      case other => fail(s"expected a value difference on d, got $other")
    }

  test("commuted sums survive") {
    survives(Vec(sum("d", a, b)), Vec(sum("d", b, a)), LiveOut(Vec("d"), memory = false))
  }

  test("sum with a zero operand survives") {
    survives(
      Vec(sum("t", a, b), sum("d", Operand.Location("t"), zero)),
      Vec(sum("d", a, b)),
      LiveOut(Vec("d"), memory = false)
    )
  }

  test("off-by-one rewrite is rejected") {
    differs(
      Vec(sum("d", a, b)),
      Vec(sum("t", b, one), sum("d", a, Operand.Location("t"))),
      LiveOut(Vec("d"), memory = false)
    )
  }

  test("adding one to a zero sum is rejected") {
    differs(Vec(sum("d", a, zero)), Vec(sum("d", a, one)), LiveOut(Vec("d"), memory = false))
  }

  test("signed zero differs only on negative zero") {
    val counterexample =
      differs(Vec(fsum("d", a, zero)), Vec(fsum("d", a, negative)), LiveOut(Vec("d"), memory = false), runs = 256)
    assertEquals(counterexample.inputs.find(_._1 == "a").map(_._2), Some(0x80000000L))
  }

  test("atomic update is refused") {
    val store  = call(RenderMachine.Machine, "store", Some(RenderMachine.Word), effect = true)
    val update = withBody(sum("d", a, b), Definition.Statement(Definition.Target.Binding("old"), store))
    assert(refused(Vec(update), Vec(sum("d", a, b)), LiveOut(Vec("d"), memory = false)).contains("atomic update"))
  }

  test("barrier is refused") {
    val park    = call(RenderMachine.Machine, "park", None, effect = true)
    val barrier = withBody(sum("d", a, b), Definition.Statement(Definition.Target.Effect, park))
    assert(refused(Vec(barrier), Vec(sum("d", a, b)), LiveOut(Vec("d"), memory = false)).contains("Machine.park"))
  }

  test("lane call outside the machine is refused") {
    val gather = call(RenderMachine.Lanes, "gather", Some(RenderMachine.Word))
    val lanes  = withBody(sum("d", a, b), Definition.Statement(Definition.Target.Binding("lanes"), gather))
    assert(refused(Vec(lanes), Vec(sum("d", a, b)), LiveOut(Vec("d"), memory = false)).contains("Lanes.gather"))
  }

  test("plain machine load is accepted") {
    val load   = call(RenderMachine.Machine, "load", Some(RenderMachine.Word))
    val loaded = withBody(sum("d", a, b), Definition.Statement(Definition.Target.Binding("loaded"), load))
    survives(Vec(loaded), Vec(sum("d", b, a)), LiveOut(Vec("d"), memory = false))
  }

  test("step without a definition is refused") {
    val hollow = withoutDefinition(sum("d", a, b))
    assert(refused(Vec(hollow), Vec(sum("d", a, b)), LiveOut(Vec("d"), memory = false)).contains("has no definition"))
  }

  test("location used at two types is refused") {
    val reason = refused(
      Vec(sum("d", a, zero)),
      Vec(retyped(sum("d", a, zero), Type.Float(64))),
      LiveOut(Vec("d"), memory = false)
    )
    assert(reason.startsWith("a is used as"), reason)
  }
}

class SASSEquivalenceSuite
    extends EquivalenceSuite[NvidiaHarness, Lookup.Sass](SASSReference, SASSGenerator.describe("sm_75")) {
  protected def sum(destination: String, left: Operand, right: Operand): Step =
    step(named("IADD3", 3), Vec(left, right, Operand.Immediate(0L)), Vec(destination))

  protected def fsum(destination: String, left: Operand, right: Operand): Step =
    step(named("FADD", 2), Vec(left, right), Vec(destination))

  test("memory live-out is refused where memory is not observed") {
    assertEquals(
      refused(Vec(sum("d", a, b)), Vec(sum("d", b, a)), LiveOut(Vec("d"), memory = true)),
      "memory is not observed on this ISA"
    )
  }
}

class AMDGPUEquivalenceSuite
    extends EquivalenceSuite[AMDGPUHarness, Lookup.Spellings](
      AMDGPUReference,
      AMDGPUGenerator.cases(Set("v_add_u32", "v_add_f32").contains)
    ) {
  private val mode = Operand.Location("mode")

  protected def sum(destination: String, left: Operand, right: Operand): Step =
    step(exact("v_add_u32_e32 v0, v1, v2"), Vec(left, right), Vec(destination))

  protected def fsum(destination: String, left: Operand, right: Operand): Step =
    step(exact("v_add_f32_e32 v0, v1, v2"), Vec(left, right, mode), Vec(destination))

  test("memory live-out is compared where memory is observed") {
    survives(Vec(sum("d", a, b)), Vec(sum("d", b, a)), LiveOut(Vec("d"), memory = true))
  }
}
