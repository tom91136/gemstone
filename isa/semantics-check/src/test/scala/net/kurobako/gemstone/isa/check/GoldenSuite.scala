package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Equivalence.Verdict
import net.kurobako.gemstone.isa.check.Proof.Result
import net.kurobako.gemstone.isa.check.Proof.Source
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.semantics.render.Verdict as Solved
import net.kurobako.gemstone.testing.*
import scala.reflect.ClassTag

/** Rewrites whose verdict is known: identities and strength reductions a compiler makes, and off-by-one or modal
  * rewrites it must not make. Every pair runs through the differential filter and then the solver, and the pairs that
  * differ are also put to the solver alone so that a filter refusing them early cannot hide an encoding fault.
  */
abstract class GoldenSuite[H, L <: Lookup](protected val reference: Reference[H, L], generate: () => Vec[Case])(using
    ClassTag[L]
) extends Suite {
  protected def executor: (Case, Vec[Long], H) => Either[String, Vec[Long]] = Equivalence.vm(reference)

  protected val lane: Vec[Case] = generate()

  protected val a: Operand    = Operand.Location("a")
  protected val b: Operand    = Operand.Location("b")
  protected val c: Operand    = Operand.Location("c")
  protected val t: Operand    = Operand.Location("t")
  protected val mode: Operand = Operand.Location("mode")
  protected val zero: Operand = Operand.Immediate(0L)

  protected def exact(instance: String): Case =
    lane.find(_.instance == instance).getOrElse(fail(s"no instance $instance"))

  protected def step(kase: Case, operands: Vec[Operand], results: Vec[String]): Step = {
    val parameters = kase.definition.parameters
    val outputs    = kase.definition.results
    if (operands.size != parameters.size)
      fail(s"${kase.instance} takes ${parameters.size} operands, not ${operands.size}")
    if (results.size != outputs.size) fail(s"${kase.instance} has ${outputs.size} results, not ${results.size}")
    Step(kase, parameters.map(_.name).zip(operands).toMap, outputs.map(_.name).zip(results).toMap)
  }

  protected def only(location: String): LiveOut = LiveOut(Vec(location), memory = false)

  protected def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  protected def prove(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): Result =
    Proof.prove(reference, executor, lane, left, right, liveOut)

  /** The solver alone on the two sequences, as the proof composes them. */
  protected def solve(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): Solved =
    Equivalence.validate(left.iterator ++ right.iterator, liveOut)
      .fold(
        fail(_),
        types =>
          Bitwuzla.equivalence(
            Proof.compose(left, liveOut, types),
            Proof.compose(right, liveOut, types),
            liveOut.memory,
            Proof.Limit
          )
      )

  protected def proved(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): Unit =
    prove(left, right, liveOut) match {
      case Result.Proved(report) => assert(report.filtered > 0)
      case other                 => fail(s"expected a proof, got $other")
    }

  protected def refuted(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): Unit =
    prove(left, right, liveOut) match {
      case Result.Rejected(counterexample, Source.Filter) => assert(counterexample.inputs.nonEmpty)
      case Result.Rejected(counterexample, Source.Solver) =>
        assert(counterexample.inputs.nonEmpty)
        Equivalence.replay(reference, executor, left, right, liveOut, counterexample.inputs.toMap) match {
          case Verdict.Rejected(_) => ()
          case other               => fail(s"expected the model to be rejected on replay, got $other")
        }
      case other => fail(s"expected a refutation, got $other")
    }

  /** A pair the solver must tell apart, whatever the filter made of it. */
  protected def toldApart(left: Vec[Step], right: Vec[Step], liveOut: LiveOut): String =
    solve(left, right, liveOut) match {
      case Solved.Different(model) =>
        assert(model.nonEmpty)
        model
      case other => fail(s"expected a difference, got $other")
    }
}

class SASSGoldenSuite
    extends GoldenSuite[NvidiaHarness, Lookup.Sass](SASSReference, () => SASSGenerator.describe("sm_75")) {
  private def add3(destination: String, x: Operand, y: Operand, z: Operand): Step =
    step(exact("IADD3 R0, R4, R8, R12"), Vec(x, y, z), Vec(destination))

  private def add(destination: String, x: Operand, y: Operand): Step =
    step(exact("IADD R0, R4, R8"), Vec(x, y), Vec(destination))

  private def increment(destination: String, x: Operand): Step =
    step(exact("IADD R0, R4, 0x1"), Vec(x), Vec(destination))

  private def multiplyByOne(destination: String, x: Operand, y: Operand): Step =
    step(exact("IMAD R0, R4, 0x1, R8"), Vec(x, y), Vec(destination))

  private def fadd(destination: String, x: Operand, y: Operand): Step =
    faddWith(destination, "FADD R0, R4, R8", x, y)

  private def faddWith(destination: String, instance: String, x: Operand, y: Operand): Step =
    step(exact(instance), Vec(x, y), Vec(destination))

  private def fmul(destination: String, x: Operand, y: Operand): Step =
    step(exact("FMUL R0, R4, R8"), Vec(x, y), Vec(destination))

  private def ffma(destination: String, x: Operand, y: Operand, z: Operand): Step =
    step(exact("FFMA R0, R4, R8, R12"), Vec(x, y, z), Vec(destination))

  private val two: Operand = Operand.Immediate(0x40000000L)

  test("a commuted integer sum is proved equal") {
    installed()
    proved(Vec(add3("d", a, b, zero)), Vec(add3("d", b, a, zero)), only("d"))
  }

  test("a multiply by one with an addend is the sum") {
    installed()
    proved(Vec(multiplyByOne("d", a, b)), Vec(add("d", a, b)), only("d"))
  }

  test("an off-by-one sum is refuted") {
    installed()
    refuted(Vec(increment("d", a)), Vec(add("d", a, zero)), only("d"))
  }

  test("a multiply by two with no addend is the sum of an operand with itself") {
    installed()
    val doubled = step(exact("IMAD R0, R4, R8, R12"), Vec(a, Operand.Immediate(2L), zero), Vec("d"))
    proved(Vec(doubled), Vec(add("d", a, a)), only("d"))
  }

  test("adding zero is multiplying by one") {
    installed()
    proved(Vec(add("d", a, zero)), Vec(multiplyByOne("d", a, zero)), only("d"))
  }

  test("a multiply by two with an addend of one is not the sum of an operand with itself") {
    installed()
    val doubled = step(exact("IMAD R0, R4, R8, R12"), Vec(a, Operand.Immediate(2L), Operand.Immediate(1L)), Vec("d"))
    refuted(Vec(doubled), Vec(add("d", a, a)), only("d"))
  }

  test("a commuted floating-point sum is proved equal in every bit") {
    installed()
    proved(Vec(fadd("d", a, b)), Vec(fadd("d", b, a)), only("d"))
  }

  test("a product by two is the sum of an operand with itself, denormals included") {
    installed()
    proved(Vec(fmul("d", a, two)), Vec(fadd("d", a, a)), only("d"))
  }

  test("flushing denormals differs from the plain sum") {
    installed()
    val plain   = Vec(fadd("d", a, b))
    val flushed = Vec(faddWith("d", "FADD.FTZ R0, R4, R8", a, b))
    refuted(plain, flushed, only("d"))
    toldApart(plain, flushed, only("d"))
  }

  test("rounding down differs from rounding to nearest") {
    installed()
    val plain = Vec(fadd("d", a, b))
    val down  = Vec(faddWith("d", "FADD.RM R0, R4, R8", a, b))
    refuted(plain, down, only("d"))
    toldApart(plain, down, only("d"))
  }

  test("saturating differs from the plain sum") {
    installed()
    val plain     = Vec(fadd("d", a, b))
    val saturated = Vec(faddWith("d", "FADD.SAT R0, R4, R8", a, b))
    refuted(plain, saturated, only("d"))
    toldApart(plain, saturated, only("d"))
  }

  test("a fused multiply-add differs from a product and a sum") {
    installed()
    val fused   = Vec(ffma("d", a, b, c))
    val unfused = Vec(fmul("t", a, b), fadd("d", t, c))
    refuted(fused, unfused, only("d"))
    val _ = toldApart(fused, unfused, only("d"))
  }
}

class AMDGPUGoldenSuite
    extends GoldenSuite[AMDGPUHarness, Lookup.Spellings](
      AMDGPUReference,
      () =>
        AMDGPUGenerator.cases(
          Set("v_add_u32", "v_add3_u32", "v_add_f32", "v_mul_f32", "v_fma_f32").contains
        )
    ) {
  private def add3(destination: String, x: Operand, y: Operand, z: Operand): Step =
    step(exact("v_add3_u32_e64 v0, v1, v2, v3"), Vec(x, y, z), Vec(destination))

  private def add(destination: String, x: Operand, y: Operand): Step =
    step(exact("v_add_u32_e32 v0, v1, v2"), Vec(x, y), Vec(destination))

  private def vadd(destination: String, x: Operand, y: Operand): Step =
    step(exact("v_add_f32_e32 v0, v1, v2"), Vec(x, y, mode), Vec(destination))

  private def vmul(destination: String, x: Operand, y: Operand): Step =
    step(exact("v_mul_f32_e32 v0, v1, v2"), Vec(x, y, mode), Vec(destination))

  private def vfma(destination: String, x: Operand, y: Operand, z: Operand): Step =
    step(exact("v_fma_f32_e64 v0, v1, v2, v3"), Vec(x, y, z, mode), Vec(destination))

  private def sum3(x: Operand, y: Operand, z: Operand): Vec[Step] =
    Vec(add("t", x, y), add("d", t, z))

  test("a commuted integer sum is proved equal") {
    installed()
    proved(Vec(add("d", a, b)), Vec(add("d", b, a)), only("d"))
  }

  test("a three-operand sum is two sums") {
    installed()
    proved(sum3(a, b, c), Vec(add3("d", a, b, c)), only("d"))
  }

  test("an off-by-one sum is refuted") {
    installed()
    refuted(Vec(add("d", a, b)), Vec(add3("d", a, b, Operand.Immediate(0x1234L))), only("d"))
  }

  test("a fused multiply-add differs from a product and a sum, as the solver and 2000 filter runs see") {
    installed()
    val fused   = Vec(vfma("d", a, b, c))
    val unfused = Vec(vmul("t", a, b), vadd("d", t, c))
    val _       = toldApart(fused, unfused, only("d"))
    // AMDGPU registers are typed as integers, so the filter samples no float edge values; the sum needs more runs
    Equivalence.check(reference, executor, fused, unfused, only("d"), 2000) match {
      case Verdict.Rejected(counterexample) =>
        assert(counterexample.inputs.nonEmpty)
        Equivalence.replay(reference, executor, fused, unfused, only("d"), counterexample.inputs.toMap) match {
          case Verdict.Rejected(_) => ()
          case other               => fail(s"expected the filter to reject its own model, got $other")
        }
      case other => fail(s"expected the filter to tell them apart, got $other")
    }
  }

  test("a floating-point sum that survives the filter is refused for its rounding mode") {
    prove(Vec(vadd("d", a, b)), Vec(vadd("d", a, b)), only("d")) match {
      case Result.Refused(reason) =>
        assert(reason.contains("v_add_f32"), reason)
        assert(reason.contains("rounding"), reason)
      case other => fail(s"expected a refusal, got $other")
    }
  }

  test("addition is not commutative under two NaNs, as the rendered NaN propagation says") {
    installed()
    val model = toldApart(Vec(vadd("d", a, b)), Vec(vadd("d", b, a)), only("d"))
    assert(model.contains("a"), model)
    assert(model.contains("b"), model)
  }
}
