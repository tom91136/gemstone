package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Equivalence.Verdict
import net.kurobako.gemstone.isa.check.Proof.Cover
import net.kurobako.gemstone.isa.check.Proof.Result
import net.kurobako.gemstone.isa.check.Proof.Source
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Verdict as Solved
import net.kurobako.gemstone.testing.*
import scala.reflect.ClassTag

/** Proofs over a lane's cases: what the solver decides is checked against the VMs, and the report names what it used.
  */
abstract class ProofSuite[H, L <: Lookup](reference: Reference[H, L], generate: () => Vec[Case])(using ClassTag[L])
    extends Suite {
  private val executor = Equivalence.vm(reference)
  private val marker   = 0x1234L

  protected val lane: Vec[Case] = generate()

  protected val a: Operand    = Operand.Location("a")
  protected val b: Operand    = Operand.Location("b")
  protected val c: Operand    = Operand.Location("c")
  protected val t: Operand    = Operand.Location("t")
  protected val zero: Operand = Operand.Immediate(0L)
  protected val one: Operand  = Operand.Immediate(1L)

  protected def sum(destination: String, left: Operand, right: Operand): Step

  protected def sum3(destination: String, first: Operand, second: Operand, third: Operand): Step

  protected def fsum(destination: String, left: Operand, right: Operand): Step

  /** `destination` as one where `source` equals `sought`, else zero. */
  protected def matches(destination: String, source: Operand, sought: Operand): Vec[Step]

  protected def otherLookup(lookup: Lookup): Lookup

  private def register(tpe: Type): Boolean = tpe match {
    case Type.Integer(32, _) | Type.Float(32) => true
    case _                                    => false
  }

  protected def named(mnemonic: String, arity: Int): Case =
    lane.find(kase =>
      kase.definition.name == mnemonic && kase.definition.results.size == 1 &&
        kase.definition.parameters.size == arity &&
        kase.definition.parameters.forall(parameter => register(parameter.tpe))
    ).getOrElse(fail(s"no $mnemonic with $arity register operands and one result"))

  protected def exact(instance: String): Case =
    lane.find(_.instance == instance).getOrElse(fail(s"no instance $instance"))

  protected def step(kase: Case, operands: Vec[Operand], results: Vec[String]): Step = {
    val parameters = kase.definition.parameters
    val outputs    = kase.definition.results
    if (operands.size != parameters.size)
      fail(s"${kase.instance} takes ${parameters.size} operands, not ${operands.size}")
    if (results.size != outputs.size)
      fail(s"${kase.instance} has ${outputs.size} results, not ${results.size}")
    Step(kase, parameters.map(_.name).zip(operands).toMap, outputs.map(_.name).zip(results).toMap)
  }

  protected def prove(
      left: Vec[Step],
      right: Vec[Step],
      liveOut: LiveOut,
      solver: Proof.Solver = Proof.bitwuzla,
      cases: Vec[Case] = lane,
      runs: Int = DifferentialSuite.Runs
  ): Result = Proof.prove(reference, executor, cases, left, right, liveOut, solver, runs)

  protected val unreachable: Proof.Solver = (_, _, _) => fail("the solver was called")

  protected def only(location: String): LiveOut = LiveOut(Vec(location), memory = false)

  protected def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def tampered(kase: Case): Case = kase.copy(definition =
    kase.definition.copy(results =
      kase.definition.results.map(result => (result.name, result.tpe, Term.Binary("+", result.value, Term.Literal(1L))))
    )
  )

  test("two additions are proved equal to a three-operand addition") {
    installed()
    prove(Vec(sum("t", a, b), sum("d", t, c)), Vec(sum3("d", a, b, c)), only("d")) match {
      case Result.Proved(report) => assert(report.filtered > 0)
      case other                 => fail(s"expected a proof, got $other")
    }
  }

  test("a proof names each instruction it relies on with its differential coverage") {
    installed()
    val add   = sum("t", a, b)
    val three = sum3("d", a, b, c)
    prove(Vec(add, sum("d", t, c)), Vec(three), only("d")) match {
      case Result.Proved(report) =>
        assertEquals(
          report.instructions,
          Vec(add.kase, three.kase).distinctBy(_.instance).map(kase =>
            Cover(kase.instance, kase.target, DifferentialSuite.Runs)
          )
        )
      case other => fail(s"expected a proof, got $other")
    }
  }

  test("a difference the filter does not sample is refuted by the solver and replayed") {
    installed()
    val left  = matches("d", a, Operand.Immediate(marker))
    val right = Vec(sum("d", zero, zero))
    Equivalence.check(reference, executor, left, right, only("d")) match {
      case Verdict.NotRejected(_) => ()
      case other                  => fail(s"expected the filter to miss it, got $other")
    }
    prove(left, right, only("d")) match {
      case Result.Rejected(Counterexample(Difference.Value("d", _, _), inputs), Source.Solver) =>
        assertEquals(inputs.find(_._1 == "a").map(_._2), Some(marker))
        Equivalence.replay(reference, executor, left, right, only("d"), inputs.toMap) match {
          case Verdict.Rejected(_) => ()
          case other               => fail(s"expected the model to be rejected on replay, got $other")
        }
      case other => fail(s"expected a refutation by the solver, got $other")
    }
  }

  test("a sequence the filter rejects does not reach the solver") {
    prove(Vec(sum("d", a, b)), Vec(sum("t", b, one), sum("d", a, t)), only("d"), unreachable) match {
      case Result.Rejected(Counterexample(Difference.Value("d", _, _), _), Source.Filter) => ()
      case other => fail(s"expected a rejection by the filter, got $other")
    }
  }

  test("a hand-built case is refused by name") {
    val built = tampered(sum("d", a, b).kase)
    val step  = sum("d", a, b).copy(kase = built)
    prove(Vec(step), Vec(sum("d", b, a)), only("d"), unreachable) match {
      case Result.Refused(reason) => assert(reason.contains(built.instance), reason)
      case other                  => fail(s"expected a refusal, got $other")
    }
  }

  test("an unavailable solver proves nothing") {
    prove(Vec(sum("d", a, b)), Vec(sum("d", b, a)), only("d"), (_, _, _) => Solved.Unavailable) match {
      case Result.Unavailable(filtered) => assert(filtered > 0)
      case other                        => fail(s"expected unavailable, got $other")
    }
  }

  test("floating-point sequences are undecided with the filter's runs") {
    prove(Vec(fsum("d", a, b)), Vec(fsum("d", a, b)), only("d")) match {
      case Result.Undecided(reason, filtered) =>
        assert(reason.nonEmpty)
        assert(filtered > 0)
      case other => fail(s"expected undecided, got $other")
    }
  }

  test("the solver and the VMs disagreeing is reported as such") {
    installed()
    val broken = tampered(sum("d", a, b).kase)
    val left   = sum("d", a, b).copy(kase = broken)
    prove(Vec(left), Vec(sum("d", a, b)), only("d"), cases = lane :+ broken) match {
      case Result.Disagreement(model, filtered) =>
        assert(model.nonEmpty)
        assert(filtered > 0)
      case other => fail(s"expected a disagreement, got $other")
    }
  }

  test("regenerated cases are equal, so membership is structural") {
    val regenerated = generate()
    assertEquals(regenerated.size, lane.size)
    regenerated.zip(lane).foreach((now, before) => assertEquals(now, before))
  }

  test("a case with another definition or lookup is not the lane's") {
    val kase = sum("d", a, b).kase
    assertNotEquals(tampered(kase), kase)
    assertNotEquals(kase.copy(lookup = otherLookup(kase.lookup)), kase)
  }
}

class SASSProofSuite
    extends ProofSuite[NvidiaHarness, Lookup.Sass](SASSReference, () => SASSGenerator.describe("sm_75")) {
  protected def sum(destination: String, left: Operand, right: Operand): Step =
    step(named("IADD3", 3), Vec(left, right, zero), Vec(destination))

  protected def sum3(destination: String, first: Operand, second: Operand, third: Operand): Step =
    step(named("IADD3", 3), Vec(first, second, third), Vec(destination))

  protected def fsum(destination: String, left: Operand, right: Operand): Step =
    step(named("FADD", 2), Vec(left, right), Vec(destination))

  protected def matches(destination: String, source: Operand, sought: Operand): Vec[Step] = Vec(
    step(exact("ISETP.EQ.AND P0, P1, R0, R4, P2"), Vec(source, sought, one), Vec("unequal", "equal")),
    step(exact("SEL R0, R4, R8, P0"), Vec(one, zero, Operand.Location("equal")), Vec(destination))
  )

  protected def otherLookup(lookup: Lookup): Lookup = lookup match {
    case sass: Lookup.Sass => sass.copy(operands = Map.empty)
    case other             => other
  }

  test("memory is not compared where the ISA does not observe it") {
    assertEquals(
      prove(Vec(sum("d", a, b)), Vec(sum("d", b, a)), LiveOut(Vec("d"), memory = true)),
      Result.Refused("memory is not observed on this ISA")
    )
  }
}

class AMDGPUProofSuite
    extends ProofSuite[AMDGPUHarness, Lookup.Spellings](
      AMDGPUReference,
      () =>
        AMDGPUGenerator.cases(
          Set("v_add_u32", "v_add3_u32", "v_add_f32", "s_cmp_eq_u32", "s_cselect_b32", "global_store_dword").contains
        )
    ) {
  private val mode  = Operand.Location("mode")
  private val x     = Operand.Location("x")
  private val y     = Operand.Location("y")
  private val first = Operand.Location("first")
  private val other = Operand.Location("other")

  protected def sum(destination: String, left: Operand, right: Operand): Step =
    step(exact("v_add_u32_e32 v0, v1, v2"), Vec(left, right), Vec(destination))

  protected def sum3(destination: String, first: Operand, second: Operand, third: Operand): Step =
    step(exact("v_add3_u32_e64 v0, v1, v2, v3"), Vec(first, second, third), Vec(destination))

  protected def fsum(destination: String, left: Operand, right: Operand): Step =
    step(exact("v_add_f32_e32 v0, v1, v2"), Vec(left, right, mode), Vec(destination))

  protected def matches(destination: String, source: Operand, sought: Operand): Vec[Step] = Vec(
    step(exact("s_cmp_eq_u32 s0, s1"), Vec(source, sought), Vec("equal")),
    step(exact("s_cselect_b32 s0, s1, s2"), Vec(one, zero, Operand.Location("equal")), Vec(destination))
  )

  protected def otherLookup(lookup: Lookup): Lookup = lookup match {
    case spellings: Lookup.Spellings => spellings.copy(operands = Map.empty)
    case other                       => other
  }

  // no AMDGPU store renders as a plain machine access, so the lane gains a hand-built store with the real one's operands
  private val handBuilt: Case = {
    val real  = exact("global_store_dword v[0:1], v2, off")
    val write = Term.Context(
      RenderMachine.Machine.contextType,
      RenderMachine.Machine.context,
      "store",
      Vec(Type.unsigned(32)),
      Vec(Term.Constant("Space.Global"), Term.Variable("ADDR"), Term.Variable("DATA")),
      Vec(None, None, None),
      None,
      effect = true
    )
    real.copy(definition =
      real.definition.copy(requirements = Vec.empty, body = Vec(Definition.Statement(Definition.Target.Effect, write)))
    )
  }

  private val built  = lane :+ handBuilt
  private val memory = LiveOut(Vec.empty, memory = true)

  private def store(address: Operand, data: Operand): Step = step(handBuilt, Vec(address, data), Vec.empty)

  private def bump(term: Term): Term = term match {
    case write: Term.Context if write.method == "store" =>
      write.copy(arguments = write.arguments.updated(2, Term.Binary("+", write.arguments(2), Term.Literal(1L))))
    case _ => term.mapChildren(bump(_))
  }

  private def corrupted(kase: Case): Case = kase.copy(definition =
    kase.definition.copy(body = kase.definition.body.map(statement => statement.copy(term = bump(statement.term))))
  )

  test("stores to one address in different orders are rejected by the filter") {
    prove(
      Vec(store(first, x), store(first, y)),
      Vec(store(first, y), store(first, x)),
      memory,
      unreachable,
      built
    ) match {
      case Result.Rejected(Counterexample(Difference.Memory(_, _), _), Source.Filter) => ()
      case other => fail(s"expected a memory difference from the filter, got $other")
    }
  }

  test("stores to addresses that may alias in different orders are not proved equal") {
    installed()
    val left  = Vec(store(first, x), store(other, y))
    val right = Vec(store(other, y), store(first, x))
    prove(left, right, memory, cases = built, runs = 1) match {
      case Result.Proved(report) => fail(s"proved equal: $report")
      case Result.Rejected(Counterexample(Difference.Memory(_, _), inputs), Source.Solver) =>
        assertEquals(inputs.find(_._1 == "first").map(_._2), inputs.find(_._1 == "other").map(_._2))
      case other => fail(s"expected a refutation by the solver, got $other")
    }
  }

  test("a model that leaves memory free and shows no difference is undecided") {
    installed()
    val broken = corrupted(handBuilt)
    val left   = store(first, x).copy(kase = broken)
    prove(Vec(left), Vec(store(first, x)), memory, cases = built :+ broken) match {
      case Result.Undecided(reason, filtered) =>
        assert(reason.contains("memory"), reason)
        assert(filtered > 0)
      case other => fail(s"expected undecided, got $other")
    }
  }
}
