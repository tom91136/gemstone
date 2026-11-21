package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu
import net.kurobako.gemstone.isa.amdgpu.AMDGPUReference
import net.kurobako.gemstone.isa.check.Equivalence.*
import net.kurobako.gemstone.isa.check.Proof.Result
import net.kurobako.gemstone.isa.check.Proof.Source
import net.kurobako.gemstone.isa.sass
import net.kurobako.gemstone.semantics.Space
import net.kurobako.gemstone.semantics.render.*
import net.kurobako.gemstone.testing.*
import scala.reflect.ClassTag

/** Proofs over loads and stores of global memory as decoded: memory is an input both sequences share and is live out,
  * with the bytes each sequence loads and stores.
  */
class MemoryProofSuite extends Suite {
  private val gfx906 = "gfx906".refine[amdgpu.Target].toOption.get

  private def installed(): Unit =
    assume(Bitwuzla.executable.isDefined, "no bitwuzla binary on GEMSTONE_BITWUZLA_EXE or PATH")

  private def parse(line: String) = sass.asm.Parser.parseProgram(s"$line;").toOption.flatMap(_.statements.collectFirst {
    case sass.asm.Program.Statement.Operation(operation, _, _) => operation
  }).getOrElse(fail(s"$line does not parse"))

  private def nvidia(lines: String*): Vec[Step] =
    Vec.from(lines).map(line => SASSForms.of("sm_89", parse(line)).fold(fail(_), identity))

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
    LiveOut(Vec.from(live), memory = true)
  )

  private def proved(result: Result, mnemonic: String): Unit = result match {
    case Result.Proved(report) =>
      assert(report.filtered > 0)
      val covers = report.instructions.filter(_.instance.startsWith(mnemonic))
      assert(covers.nonEmpty, report)
      covers.foreach(cover => assert(cover.checked.exists(_.startsWith(mnemonic)), cover))
    case other => fail(s"expected a proof, got $other")
  }

  private def rejected(result: Result): Unit = result match {
    case Result.Rejected(_, _) => ()
    case other                 => fail(s"expected a refutation, got $other")
  }

  private def bySolver(result: Result): Unit = result match {
    case Result.Rejected(_, Source.Solver) => ()
    case other                             => fail(s"expected a refutation by the solver, got $other")
  }

  private def byFilter(result: Result): Unit = result match {
    case Result.Rejected(Counterexample(Difference.Memory(_, _), _), Source.Filter) => ()
    case other => fail(s"expected a memory difference from the filter, got $other")
  }

  private val low   = "global_store_dword v[2:3], v1, off"
  private val high  = "global_store_dword v[2:3], v4, off offset:4"
  private val other = "global_store_dword v[4:5], v6, off"

  test("AMDGPU stores at offsets of one base that cannot overlap are proved equal in either order") {
    installed()
    proved(prove(AMDGPUReference, amd(low, high), amd(high, low)), "global_store_dword")
  }

  test("AMDGPU stores whose addresses may alias are told apart in either order") {
    installed()
    rejected(prove(AMDGPUReference, amd(low, other), amd(other, low)))
  }

  test("AMDGPU stores at offsets of one base that overlap in part are told apart in either order") {
    val near = "global_store_dword v[2:3], v4, off offset:2"
    prove(AMDGPUReference, amd(low, near), amd(near, low)) match {
      case Result.Rejected(Counterexample(Difference.Memory(_, _), _), _) => ()
      case result                                                         => fail(s"expected a refutation, got $result")
    }
  }

  test("deleting an AMDGPU store is rejected") {
    val sum = "v_add_u32 v7, v1, v4"
    byFilter(prove(AMDGPUReference, amd(sum, "global_store_dword v[2:3], v7, off"), amd(sum), "v7"))
  }

  test("an AMDGPU load moved across a store that may alias is rejected") {
    installed()
    val load = "global_load_dword v6, v[4:5], off"
    rejected(prove(AMDGPUReference, amd(low, load), amd(load, low), "v6"))
  }

  test("an AMDGPU load moved across a store it cannot overlap is proved equal") {
    installed()
    val load = "global_load_dword v6, v[2:3], off offset:4"
    proved(prove(AMDGPUReference, amd(low, load), amd(load, low), "v6"), "global_load_dword")
  }

  test("a dword store is not proved equal to the stores of its halves, which may fault where it does not") {
    installed()
    val halves = amd(
      "global_store_short v[2:3], v1, off",
      "v_lshrrev_b32 v7, 16, v1",
      "global_store_short v[2:3], v7, off offset:2"
    )
    prove(AMDGPUReference, amd(low), halves) match {
      case Result.Undecided(reason, filtered) =>
        assert(reason.contains("the width of an access"), reason)
        assert(filtered > 0)
      case other => fail(s"expected undecided, got $other")
    }
  }

  test("an AMDGPU replay starts from the memory it is given") {
    val load   = "global_load_dword v6, v[2:3], off"
    val left   = amd(load, "v_mov_b32 v7, v6")
    val right  = amd(load, "v_cmp_eq_u32 vcc, 7, v6", "v_cndmask_b32 v7, v6, v8, vcc")
    val inputs = Map("v2" -> 0x1000L, "v8" -> 9L)
    val word   =
      Map(Space.Global -> Map(0x1000L -> 7.toByte, 0x1001L -> 0.toByte, 0x1002L -> 0.toByte, 0x1003L -> 0.toByte))
    def replay(memory: Map[Space, Map[Long, Byte]]) = Equivalence.replay(
      AMDGPUReference,
      Equivalence.vm(AMDGPUReference),
      left,
      right,
      LiveOut(Vec("v7"), memory = true),
      inputs,
      memory
    )
    replay(Map.empty) match {
      case Equivalence.Verdict.NotRejected(_) => ()
      case other                              => fail(s"expected the sequences to agree on zeroed memory, got $other")
    }
    replay(word) match {
      case Equivalence.Verdict.Rejected(_) => ()
      case other                           => fail(s"expected the seeded memory to tell them apart, got $other")
    }
  }

  test("SASS sequences that differ only where memory holds one value are told apart by the solver's memory") {
    installed()
    val load = "LDG.E R7, [R2.64]"
    val pick = nvidia(load, "ISETP.EQ.U32.AND P0, PT, R7, 0xdeadbeef, PT", "SEL R8, R9, R7, P0")
    bySolver(prove(SASSReference, nvidia(load, "MOV R8, R7"), pick, "R8"))
  }

  test("a load whose result is dead is not deleted: it reads bytes the other sequence does not") {
    val move = "v_mov_b32 v6, v1"
    byFilter(prove(AMDGPUReference, amd("global_load_dword v6, v[2:3], off", move), amd(move), "v6"))
  }

  test("a load moved across a store to its address reads other bytes, though its result is overwritten") {
    installed()
    val clear = "v_mov_b32 v6, 0"
    val load  = "global_load_dword v6, v[2:3], off"
    byFilter(prove(AMDGPUReference, amd(low, load, clear), amd(load, clear, low), "v6"))
  }

  test("a SASS load moved across a store to its address reads other bytes, though its result is overwritten") {
    installed()
    val clear = "MOV R7, RZ"
    val load  = "LDG.E R7, [R2.64]"
    byFilter(prove(SASSReference, nvidia(first, load, clear), nvidia(load, clear, first), "R7"))
  }

  test("a store of what was just loaded is not the absence of both: it writes bytes the other does not") {
    val move = "v_mov_b32 v7, v1"
    val back = amd("global_load_dword v6, v[2:3], off", "global_store_dword v[2:3], v6, off", move)
    byFilter(prove(AMDGPUReference, amd(move), back, "v7"))
  }

  test("accesses of LDS, flat and scratch memory, and atomics, stay refused") {
    Vec("ds_read_b32 v1, v2", "ds_write_b32 v2, v1", "flat_load_dword v1, v[2:3]", "ds_add_u32 v4, v3").foreach(text =>
      assert(AMDGPUForms.of(gfx906, text).left.exists(_.contains("is not a plain memory access")), text)
    )
    val atomics = AMDGPUGenerator.cases(Set("global_atomic_add", "global_atomic_cmpswap").contains, _ == "gfx906")
    assert(atomics.nonEmpty)
    atomics.foreach { kase =>
      val form = AMDGPUForms.of(gfx906, kase.instance)
      assert(form.left.exists(_.contains("is refused")), form.fold(identity, _ => s"${kase.instance} is admitted"))
    }
  }

  private val first  = "STG.E [R2.64], R5"
  private val second = "STG.E [R2.64+0x4], R6"

  test("SASS stores at offsets of one base that cannot overlap are proved equal in either order") {
    installed()
    proved(prove(SASSReference, nvidia(first, second), nvidia(second, first)), "STG")
  }

  test("SASS stores whose addresses may alias are told apart in either order") {
    installed()
    val apart = "STG.E [R8.64], R6"
    rejected(prove(SASSReference, nvidia(first, apart), nvidia(apart, first)))
  }

  test("deleting a SASS store is rejected") {
    val sum = "IADD3 R4, R5, R6, RZ"
    prove(SASSReference, nvidia(sum, "STG.E [R2.64], R4"), nvidia(sum), "R4") match {
      case Result.Rejected(_, Source.Filter) => ()
      case other                             => fail(s"expected a refutation by the filter, got $other")
    }
  }

  test("a SASS load moved across a store that may alias is rejected") {
    installed()
    val load = "LDG.E R7, [R8.64]"
    rejected(prove(SASSReference, nvidia(first, load), nvidia(load, first), "R7"))
  }

  test("a SASS load moved across a store it cannot overlap is proved equal") {
    installed()
    val load = "LDG.E R7, [R2.64+0x4]"
    proved(prove(SASSReference, nvidia(first, load), nvidia(load, first), "R7"), "LDG")
  }

  test("a guarded SASS store is not its unguarded form, and stores under complementary guards are one store") {
    installed()
    rejected(prove(SASSReference, nvidia("@P0 STG.E [R2.64], R5"), nvidia(first)))
    proved(prove(SASSReference, nvidia("@P0 STG.E [R2.64], R5", "@!P0 STG.E [R2.64], R5"), nvidia(first)), "@P")
    rejected(prove(SASSReference, nvidia("@P0 STG.E [R2.64], R5", "@!P0 STG.E [R2.64], R6"), nvidia(first)))
  }

  test("a guarded SASS load reads nothing where its guard fails") {
    installed()
    val load = "LDG.E R7, [R2.64]"
    rejected(prove(SASSReference, nvidia(s"@P0 $load"), nvidia(load), "R7"))
    proved(prove(SASSReference, nvidia(s"@P0 $load", s"@!P0 $load"), nvidia(load), "R7"), "@!P")
    val fails = "ISETP.NE.U32.AND P0, PT, R1, R1, PT"
    proved(prove(SASSReference, nvidia(fails, s"@P0 $load"), nvidia(fails), "R7", "P0"), "@P")
    rejected(prove(SASSReference, nvidia(fails, s"@!P0 $load"), nvidia(fails), "R7", "P0"))
  }

  private def refusal(text: String): String =
    SASSForms.of("sm_89", parse(text)).fold(identity, _ => fail(s"$text is admitted"))

  test("SASS accesses of shared memory and atomics stay refused") {
    Vec("LDS R7, [R5.X4]", "STS [R5.X4], R0", "ATOMG.E.AND P0, R0, desc[UR4][R8.64], R12").foreach(text =>
      assert(refusal(text).nonEmpty, text)
    )
  }

  test("wide SASS loads and stores are admitted") {
    Vec("LDG.E.64 R4, [R2.64]", "LDG.E.128 R4, [R2.64]", "STG.E.64 [R4.64], R2", "STG.E.128 [R4.64], R8").foreach(
      text =>
        assert(SASSForms.of("sm_89", parse(text)).isRight, SASSForms.of("sm_89", parse(text)))
    )
  }

  test("a wide SASS load is not the loads of its parts, which fault where it does not") {
    installed()
    val halves = nvidia("LDG.E R4, [R2.64]", "LDG.E R5, [R2.64+0x4]")
    val pairs  = nvidia("LDG.E.64 R4, [R2.64]", "LDG.E.64 R6, [R2.64+0x8]")
    rejected(prove(SASSReference, nvidia("LDG.E.64 R4, [R2.64]"), halves, "R4", "R5"))
    rejected(prove(SASSReference, nvidia("LDG.E.128 R4, [R2.64]"), pairs, "R4", "R5", "R6", "R7"))
  }

  test("a wide SASS store is not the stores of its parts, which fault where it does not") {
    installed()
    val halves = nvidia("STG.E [R2.64], R4", "STG.E [R2.64+0x4], R5")
    val pairs  = nvidia("STG.E.64 [R2.64], R4", "STG.E.64 [R2.64+0x8], R6")
    rejected(prove(SASSReference, nvidia("STG.E.64 [R2.64], R4"), halves))
    rejected(prove(SASSReference, nvidia("STG.E.128 [R2.64], R4"), pairs))
  }

  test("a wide SASS load moved across a store it cannot overlap is proved equal, across one it may overlap rejected") {
    installed()
    val store = "STG.E [R2.64+0x8], R9"
    val live  = Vec("R4", "R5", "R6", "R7")
    val apart = "LDG.E.128 R4, [R2.64+0x10]"
    val near  = "LDG.E.128 R4, [R2.64]"
    proved(prove(SASSReference, nvidia(store, apart), nvidia(apart, store), live*), "LDG")
    rejected(prove(SASSReference, nvidia(store, near), nvidia(near, store), live*))
  }

  test(
    "a wide SASS store moved across a load it cannot overlap is proved equal, across one it overlaps in part rejected"
  ) {
    installed()
    val store = "STG.E.64 [R2.64], R4"
    val apart = "LDG.E R9, [R2.64+0x8]"
    val part  = "LDG.E R9, [R2.64+0x4]"
    proved(prove(SASSReference, nvidia(store, apart), nvidia(apart, store), "R9"), "STG")
    rejected(prove(SASSReference, nvidia(store, part), nvidia(part, store), "R9"))
  }

  test("two wide SASS stores at the same address are told apart by their widths") {
    installed()
    rejected(prove(SASSReference, nvidia("STG.E.64 [R2.64], R4"), nvidia("STG.E.128 [R2.64], R4")))
  }

  test("a constant read at an offset a register holds stays refused, though it lowers to a load") {
    assert(refusal("LDC.U8 R0, c[0x3][R4]").contains("at an offset a register holds"))
  }

  test("the SASS lane checks plain global loads and stores, wide ones among them, and no atomic") {
    val cases = SASSGenerator.describe("sm_86", Set("LDG", "STG", "ATOMG").contains).map(_.instance)
    assert(cases.exists(_.startsWith("LDG")) && cases.exists(_.startsWith("STG")), cases)
    for {
      mnemonic <- Vec("LDG", "STG")
      width    <- Vec("64", "128")
    }
      assert(
        cases.exists(instance => instance.startsWith(mnemonic) && instance.matches(raw".*\.$width .*")),
        (mnemonic, width)
      )
    assertEquals(cases.filter(_.startsWith("ATOMG")), Vec.empty[String])
  }
}
