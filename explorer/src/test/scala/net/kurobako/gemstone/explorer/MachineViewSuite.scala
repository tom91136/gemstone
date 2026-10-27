package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

object MachineViewSuite {

  private def described(instance: String, name: String): Described =
    Described(instance, Definition(name, Vec.empty, Vec.empty, Vec.empty, Vec.empty, Vec.empty))

  private val Add  = described("add.u32 %r2, %r0, %r1", "add.u32")
  private val Copy = described("cp.async.bulk [%out], [%in], 16", "cp.async.bulk")
  private val Bra  = described("bra SKIP", "bra")

  private val FixtureEntry = View.Entry(
    Language.PTX,
    "fixture",
    Vec("Lane", "Machine", "Both", "Unavailable"),
    () => Vec.empty
  )

  private object FixtureModel extends Model {
    def targets: Vec[String]                                                          = Vec.empty
    def anyTarget: Boolean                                                            = false
    def defaultTarget: Option[String]                                                 = None
    def entries(target: Option[String]): Vec[View.Entry]                              = Vec(FixtureEntry)
    def registers(target: Option[String]): Vec[View.RegisterFile]                     = Vec.empty
    def binary: Boolean                                                               = false
    def assemble(source: String, target: Option[String]): Either[String, String]      = Left("no binary form")
    def disassemble(bytes: Vec[Byte], target: Option[String]): Either[String, String] = Left("no binary form")
    def assemblyExample: String                                                       = ""
    def binaryExample: String                                                         = ""

    protected def render(entry: View.Entry, form: View.Form): Either[Unavailable, Vec[Described]] =
      Left(Unavailable.Unclassified)

    override def semanticsOf(entry: View.Entry, form: View.Form): Semantics = form.name match {
      case "Lane"    => Semantics.Lane(Vec(Add))
      case "Machine" => Semantics.Machine(Vec(Copy, Bra))
      case "Both"    => Semantics.Both(Vec(Add), Vec(Copy))
      case _         => Semantics.Unavailable(Unavailable.Unclassified)
    }
  }

  private def form(name: String): View.Form = View.Form(name, Vec.empty, Vec.empty)

  private def parts(name: String): Vec[SemanticsParts.Part] =
    SemanticsParts.parts(FixtureModel.semanticsOf(FixtureEntry, form(name)))
}

class MachineViewSuite extends munit.FunSuite {
  import MachineViewSuite.*

  test("Semantics.Lane renders the existing per-lane definitions") {
    val found = parts("Lane")
    assertEquals(found.size, 1)
    found.headOption match {
      case Some(SemanticsParts.Part.Lane(descriptions)) => assertEquals(descriptions, Vec(Add))
      case other                                        => fail(s"expected lane semantics, found $other")
    }
  }

  test("Semantics.Machine renders one machine part with every definition") {
    val found = parts("Machine")
    assertEquals(found.size, 1)
    found.headOption match {
      case Some(SemanticsParts.Part.Machine(descriptions)) => assertEquals(descriptions, Vec(Copy, Bra))
      case other                                           => fail(s"expected machine semantics, found $other")
    }
  }

  test("Semantics.Both renders lane definitions before the machine definitions") {
    val found = parts("Both")
    assertEquals(found.size, 2)
    found.headOption match {
      case Some(SemanticsParts.Part.Lane(descriptions)) => assertEquals(descriptions, Vec(Add))
      case other                                        => fail(s"expected lane semantics first, found $other")
    }
    found(1) match {
      case SemanticsParts.Part.Machine(descriptions) => assertEquals(descriptions, Vec(Copy))
      case other                                     => fail(s"expected machine semantics second, found $other")
    }
  }

  test("Unavailable keeps the existing reason panel") {
    val found = parts("Unavailable")
    assertEquals(found.size, 1)
    found.headOption match {
      case Some(SemanticsParts.Part.UnavailableReason(Unavailable.Unclassified)) => ()
      case other => fail(s"expected unavailable semantics, found $other")
    }
  }

  test("Semantics.of takes lane and machine definitions from the form's instances") {
    val reason = Unavailable.Mechanics("lowers to Branch.")
    assertEquals(Semantics.of(Right(Vec(Add)), Vec.empty), Semantics.Lane(Vec(Add)))
    assertEquals(Semantics.of(Right(Vec(Add)), Vec(Copy)), Semantics.Both(Vec(Add), Vec(Copy)))
    assertEquals(Semantics.of(Left(reason), Vec(Bra)), Semantics.Machine(Vec(Bra)))
    assertEquals(Semantics.of(Left(reason), Vec.empty), Semantics.Unavailable(reason))
  }

  test("sandbox recipes are keyed by language and instruction name, case-insensitively") {
    val bra = SandboxRecipes.find(Language.SASS, "BRA").getOrElse(fail("missing SASS BRA recipe"))
    assertEquals(bra.program(Language.SASS, Some("sm_50")).target, Some("sm_89"))
    assert(SandboxRecipes.find(Language.PTX, "bra").nonEmpty)
    assert(SandboxRecipes.find(Language.SPIRV, "OpNop").nonEmpty)
    assert(SandboxRecipes.find(Language.SASS, "not-a-real-instruction").isEmpty)
  }

  test("sandbox success evidence shows PCs, registers and changed memory") {
    val recipe = SandboxRecipes.find(Language.SASS, "STG").getOrElse(fail("missing SASS STG recipe"))
    val run    = SandboxRunner.run(recipe.program(Language.SASS, Some("sm_50")))
      .fold(failure => fail(s"${failure.programPage}: ${failure.detail}"), identity)
    val report = SandboxEvidence.report(run)

    assert(report.complete)
    assertEquals(report.language, Language.SASS)
    assert(report.executed.nonEmpty)
    assert(report.executed.exists(_.text.contains("STG")))
    assert(report.registers.exists(value => value.name == "R4" && value.bits == 42L))
    assert(report.memory.exists(memory => memory.changed.exists(change => change.offset == 0 && change.after == 42)))
    assert(report.memory.exists(_.head.nonEmpty))
  }

  test("the PTX bulk copy recipe runs and shows PC and memory evidence") {
    val recipe = SandboxRecipes.find(Language.PTX, "cp.async.bulk").getOrElse(fail("missing PTX bulk copy recipe"))
    val run    = SandboxRunner.run(recipe.program(Language.PTX, Some("sm_80")))
      .fold(failure => fail(s"${failure.programPage}: ${failure.detail}"), identity)
    val report = SandboxEvidence.report(run)

    assert(report.complete)
    assert(report.executed.exists(_.text.contains("cp.async.bulk")))
    assert(report.memory.exists(_.changed.exists(change => change.offset == 0 && change.after == 1)))
  }

  test("SPIR-V control recipes run through the real sandbox") {
    Vec("OpBranchConditional", "OpControlBarrier").foreach { name =>
      val recipe = SandboxRecipes.find(Language.SPIRV, name).getOrElse(fail(s"missing SPIR-V $name recipe"))
      val run    = SandboxRunner.run(recipe.program(Language.SPIRV, None))
        .fold(failure => fail(s"${failure.programPage}: ${failure.detail}"), identity)
      assertEquals(run.failure, None, name)
      assert(run.complete, name)
      assert(run.pcs.nonEmpty, name)
    }
  }

  test("the AMDGPU barrier recipe runs") {
    val recipe = SandboxRecipes.find(Language.AMDGPU, "s_barrier").getOrElse(fail("missing AMDGPU barrier recipe"))
    val run    = SandboxRunner.run(recipe.program(Language.AMDGPU, None))
      .fold(failure => fail(s"${failure.programPage}: ${failure.detail}"), identity)
    assertEquals(run.failure, None)
    assert(run.complete)
    assert(run.pcs.nonEmpty)
  }

  test("sandbox failure evidence carries the typed reason and Program page fallback") {
    val failure = SandboxRunner.run(SandboxProgram(Language.SASS, "NOT_AN_INSTRUCTION R1", Some("sm_50")))
      .swap.toOption.getOrElse(fail("expected an assembly failure"))

    assertEquals(SandboxEvidence.failureKind(failure), "Assembly")
    assertEquals(failure.language, Language.SASS)
    assertEquals(failure.programPage, "Program page (SASS)")
  }

  test("every typed sandbox failure has a kind and a Program page fallback") {
    val failures = Vec(
      SandboxFailure.Unavailable(Language.PTX, "unavailable"),
      SandboxFailure.Assembly(Language.SASS, "assembly"),
      SandboxFailure.Patch(Language.AMDGPU, "patch"),
      SandboxFailure.Load(Language.SPIRV, "load"),
      SandboxFailure.Setup(Language.PTX, "setup"),
      SandboxFailure.Launch(Language.SASS, "launch")
    )
    assertEquals(
      failures.map(SandboxEvidence.failureKind),
      Vec("Unavailable", "Assembly", "Patch", "Load", "Setup", "Launch")
    )
    assert(failures.forall(_.programPage.startsWith("Program page (")))
  }
}
