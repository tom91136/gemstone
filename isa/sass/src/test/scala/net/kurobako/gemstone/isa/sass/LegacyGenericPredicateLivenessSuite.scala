package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

/** Static CFG checks for the deadness proof of generic LEA auxiliary predicates. */
class LegacyGenericPredicateLivenessSuite extends munit.FunSuite {
  for (sm <- Vec.nonEmpty(50, 60)) {
    test(s"sm$sm retains independent explicit loads between generic address halves") {
      def function(destination: String, opcode: String = "LDG.E"): DecodedFunction = DecodedFunction(
        "binary_search",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ"), Some("@P0")),
          SASSFixture.instruction(8, opcode, Vec.nonEmpty(destination, "[R2]"), Some("@P0")),
          SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15"), Some("@P0")),
          SASSFixture.instruction(24, "LD.E", Vec.nonEmpty("R8", "[R8]", "P2"), Some("@P0")),
          SASSFixture.instruction(32, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function("R5"), sm)
      assertEquals(rewritten(sm, function("R5")).instructions(1), function("R5").instructions(1))
      Vec.nonEmpty("R4", "R8").foreach(destination => assertRejects(function(destination), sm))
      assertRejects(function("R3", "LDG.E.64"), sm)
      assertRewrites(function("R14", "LDG.E.64"), sm)
    }

    test(s"sm$sm retains scoreboard and block barriers before generic memory consumption") {
      val function = DecodedFunction(
        "scan_by_key_barriers",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R5", "RZ")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P0", "R9", "R5", "RZ", "R16")),
          SASSFixture.instruction(16, "DEPBAR.LE", Vec.nonEmpty("SB5", "3")),
          SASSFixture.instruction(24, "STS", Vec.nonEmpty("[R29]", "R7")),
          SASSFixture.instruction(32, "LDS.U.32", Vec.nonEmpty("R22", "[R26]")),
          SASSFixture.instruction(40, "BAR.SYNC", Vec.nonEmpty("0")),
          SASSFixture.instruction(48, "MEMBAR.CTA", Vec.empty),
          SASSFixture.instruction(56, "LD.E", Vec.nonEmpty("R14", "[R8]", "P0")),
          SASSFixture.instruction(64, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function, sm)
      val guarded = function.copy(instructions =
        function.instructions.updated(
          5,
          SASSFixture.instruction(40, "BAR.SYNC", Vec.nonEmpty("0"), Some("@P0"))
        )
      )
      assertRejects(guarded, sm)
      val clobbered = function.copy(instructions =
        function.instructions.updated(
          4,
          SASSFixture.instruction(32, "LDS.U.64", Vec.nonEmpty("R8", "[R26]"))
        )
      )
      assertRejects(clobbered, sm)
    }

    test(s"sm$sm deadness follows a guarded predicate replacement on the consuming lanes") {
      def function(changedGuard: Boolean): DecodedFunction = DecodedFunction(
        "guarded_replacement",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(16, "LD.E", Vec.nonEmpty("R16", "[R8]", "P2")),
          SASSFixture.instruction(24, "BRA", Vec.nonEmpty("0x20")),
          SASSFixture.instruction(32, "ISETP.EQ.AND", Vec.nonEmpty("P2", "PT", "RZ", "RZ", "PT"), Some("@!P0")),
          if (changedGuard)
            SASSFixture.instruction(40, "ISETP.EQ.AND", Vec.nonEmpty("P0", "PT", "RZ", "RZ", "PT"))
          else SASSFixture.instruction(40, "NOP", Vec.empty),
          SASSFixture.instruction(48, "SEL", Vec.nonEmpty("R18", "RZ", "RZ", "P2"), Some("@!P0")),
          SASSFixture.instruction(56, "ISETP.EQ.AND", Vec.nonEmpty("P2", "PT", "RZ", "RZ", "PT")),
          SASSFixture.instruction(64, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function(false), sm)
      assertRejects(function(true), sm)
      val original = function(false)
      Vec(Some("@P0"), None).foreach { guard =>
        val escaping = original.copy(instructions =
          original.instructions.updated(
            6,
            SASSFixture.instruction(48, "SEL", Vec.nonEmpty("R18", "RZ", "RZ", "P2"), guard)
          )
        )
        assertRejects(escaping, sm)
      }
      Vec.nonEmpty(
        SASSFixture.instruction(40, "SHFL.IDX", Vec.nonEmpty("P0", "R17", "R24", "31", "31")),
        SASSFixture.instruction(40, "LOP3.LUT.NZ", Vec.nonEmpty("P0", "R17", "R24", "R25", "R26", "0x80"))
      ).foreach { overwrite =>
        assertRejects(original.copy(instructions = original.instructions.updated(5, overwrite)), sm)
      }
      val entered = original.copy(instructions =
        original.instructions :+
          SASSFixture.instruction(72, "BRA", Vec.nonEmpty("0x30"))
      )
      assertRejects(entered, sm)
    }

    test(s"sm$sm keeps shuffle and logic effects separate from auxiliary predicate uses") {
      def function(destination: String): DecodedFunction = DecodedFunction(
        "scan_shuffle",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(16, "SHFL.IDX", Vec.nonEmpty("PT", destination, "R24", "31", "31")),
          SASSFixture.instruction(24, "LOP32I.AND", Vec.nonEmpty("R22", "R18", "31")),
          SASSFixture.instruction(32, "LD.E", Vec.nonEmpty("R16", "[R8]", "P2")),
          SASSFixture.instruction(40, "SHFL.IDX", Vec.nonEmpty("P2", "R17", "R24", "31", "31")),
          SASSFixture.instruction(48, "SEL", Vec.nonEmpty("R18", "RZ", "RZ", "P2")),
          SASSFixture.instruction(56, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function("R17"), sm)
      Vec.nonEmpty("R8", "R9").foreach(destination => assertRejects(function(destination), sm))
    }

    test(s"sm$sm preserves generic address provenance through a diamond") {
      def function(bridge: DecodedInstruction, bypass: Boolean = false): DecodedFunction = DecodedFunction(
        "generic_diamond",
        Vec.nonEmpty(
          if (bypass) SASSFixture.instruction(0, "BRA", Vec.nonEmpty("0x40"), Some("@P3"))
          else SASSFixture.instruction(0, "NOP", Vec.empty),
          SASSFixture.instruction(8, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(24, "ST.E.64", Vec.nonEmpty("[R8]", "R16", "P2")),
          SASSFixture.instruction(32, "BRA", Vec.nonEmpty("0x38"), Some("@P0")),
          bridge,
          SASSFixture.instruction(48, "BRA", Vec.nonEmpty("0x40")),
          SASSFixture.instruction(56, "NOP", Vec.empty),
          SASSFixture.instruction(64, "ST.E.64", Vec.nonEmpty("[R8]", "R18", "P2")),
          SASSFixture.instruction(72, "EXIT", Vec.empty)
        )
      )
      val independent = SASSFixture.instruction(40, "MOV", Vec.nonEmpty("R18", "RZ"))
      assertRewrites(function(independent), sm)
      assertRejects(function(independent, bypass = true), sm)
      assertRejects(function(SASSFixture.instruction(40, "MOV", Vec.nonEmpty("R8", "RZ"))), sm)
      assertRejects(
        function(SASSFixture.instruction(40, "ISETP.EQ.AND", Vec.nonEmpty("P2", "PT", "RZ", "RZ", "PT"))),
        sm
      )
      val guarded = function(independent).copy(instructions =
        function(independent).instructions.updated(
          8,
          SASSFixture.instruction(64, "ST.E.64", Vec.nonEmpty("[R8]", "R18", "P2"), Some("@P2"))
        )
      )
      assertRejects(guarded, sm)
    }

    test(s"sm$sm reconvergence setup preserves the generic address carry dependency") {
      def function(setup: String): DecodedFunction = DecodedFunction(
        "carry_setup",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(8, setup, Vec.nonEmpty("0x28")),
          SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(24, "ST.E.64", Vec.nonEmpty("[R8]", "R16", "P2")),
          SASSFixture.instruction(32, "SYNC", Vec.empty),
          SASSFixture.instruction(40, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function("SSY"), sm)
      assertRejects(function("BRA"), sm)
    }

    test(s"sm$sm break edges retain predicate provenance at their join") {
      def function(replace: Boolean): DecodedFunction = DecodedFunction(
        "break_join",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "PBK", Vec.nonEmpty("0x40")),
          SASSFixture.instruction(8, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(24, "ST.E.64", Vec.nonEmpty("[R8]", "R16", "P2")),
          SASSFixture.instruction(32, "BRA", Vec.nonEmpty("0x38"), Some("@P0")),
          if (replace) SASSFixture.instruction(40, "ISETP.EQ.AND", Vec.nonEmpty("P2", "PT", "RZ", "RZ", "PT"))
          else SASSFixture.instruction(40, "MOV", Vec.nonEmpty("R18", "RZ")),
          SASSFixture.instruction(48, "BRK", Vec.empty),
          SASSFixture.instruction(56, "BRK", Vec.empty),
          SASSFixture.instruction(64, "ST.E.64", Vec.nonEmpty("[R8]", "R18", "P2")),
          SASSFixture.instruction(72, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function(false), sm)
      assertRejects(function(true), sm)
    }

    test(s"sm$sm generic memory accepts uncached scalar and vector widths") {
      for (suffix <- Vec.nonEmpty("U8", "S8", "U16", "S16", "64", "128")) {
        val function = DecodedFunction(
          "generic_width",
          Vec.nonEmpty(
            SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
            SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
            SASSFixture.instruction(16, s"LD.E.$suffix", Vec.nonEmpty("R16", "[R8]", "P2")),
            SASSFixture.instruction(24, "EXIT", Vec.empty)
          )
        )
        assertRewrites(function, sm)
      }
    }

    test(s"sm$sm generic address proof retains independent legacy logical and extrema results") {
      for (
        (opcode, operands) <- Vec(
          "LOP.AND.NZ" -> Vec.nonEmpty("P0", "R16", "R6", "1"),
          "LOP3.LUT"   -> Vec.nonEmpty("R16", "R6", "R7", "R10", "0xfe"),
          "IMNMX"      -> Vec.nonEmpty("R16", "R6", "R7", "!PT"),
          "MEMBAR.CTA" -> Vec.empty[String]
        )
      ) {
        val function = DecodedFunction(
          "independent_effects",
          Vec.nonEmpty(
            SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
            SASSFixture.instruction(8, opcode, operands),
            SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
            SASSFixture.instruction(24, opcode, operands),
            SASSFixture.instruction(32, "ST.E", Vec.nonEmpty("[R8]", "R16", "P2")),
            SASSFixture.instruction(40, "EXIT", Vec.empty)
          )
        )
        assertRewrites(function, sm)
        if (operands.nonEmpty) {
          val destination = if (opcode == "LOP.AND.NZ") 1 else 0
          val clobber     = SASSFixture.instruction(24, opcode, operands.updated(destination, "R8"))
          assertRejects(function.copy(instructions = function.instructions.updated(3, clobber)), sm)
        }
      }
    }

    test(s"sm$sm generic address halves preserve shared sources and permit high-only initialization") {
      val function = DecodedFunction(
        "initialized_high",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R4", "RZ")),
          SASSFixture.instruction(8, "MOV", Vec.nonEmpty("R15", "RZ")),
          SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R4", "RZ", "R15")),
          SASSFixture.instruction(24, "LD.E", Vec.nonEmpty("R8", "[R8]", "P2")),
          SASSFixture.instruction(32, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function, sm)
      assertRejects(
        function.copy(instructions =
          function.instructions.updated(
            1,
            SASSFixture.instruction(8, "MOV", Vec.nonEmpty("R4", "RZ"))
          )
        ),
        sm
      )
      assertRewrites(
        function.copy(instructions =
          function.instructions.updated(
            1,
            SASSFixture.instruction(8, "MOV", Vec.nonEmpty("R4", "R4"))
          )
        ),
        sm
      )
    }
  }

  test("sm60 generic address proof permits independent IADD3 while preserving address and carry dependencies") {
    def function(destination: String, opcode: String = "IADD3"): DecodedFunction = DecodedFunction(
      "scan_by_key",
      Vec.nonEmpty(
        SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R6", "0x200"), Some("@!P1")),
        SASSFixture.instruction(8, opcode, Vec.nonEmpty(destination, "R16", "R5", "R14")),
        SASSFixture.instruction(16, "LEA.HI.X", Vec.nonEmpty("P3", "R9", "R6", "RZ", "R7"), Some("@!P1")),
        SASSFixture.instruction(24, "IADD3", Vec.nonEmpty("R5", "R20", "R5", "R18")),
        SASSFixture.instruction(32, "ST.E.CG.128", Vec.nonEmpty("[R8]", "R4", "P3"), Some("@!P1")),
        SASSFixture.instruction(40, "EXIT", Vec.empty)
      )
    )
    assertRewrites(function("R5"), 60)
    Vec.nonEmpty("R6", "R8", "R5.CC").foreach(destination => assertRejects(function(destination), 60))
    assertRejects(function("R5", "IADD3.X"), 60)
  }

  test("sm60 generic address proof retains independent extended integer comparisons") {
    def function(destination: String): DecodedFunction = DecodedFunction(
      "copy_if",
      Vec.nonEmpty(
        SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R2.CC", "R3", "0x100"), Some("@!P0")),
        SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P5", "R3", "R3", "RZ", "R5"), Some("@!P0")),
        SASSFixture.instruction(16, "IADD", Vec.nonEmpty("RZ.CC", "-RZ", "R31")),
        SASSFixture.instruction(24, "ISETP.NE.X.AND", Vec.nonEmpty(destination, "PT", "R4", "RZ", "PT")),
        SASSFixture.instruction(32, "ST.E.CG.64", Vec.nonEmpty("[R2]", "R6", "P5"), Some("@!P0")),
        SASSFixture.instruction(40, "EXIT", Vec.empty)
      )
    )
    assertRewrites(function("P2"), 60)
    Vec.nonEmpty("P0", "P5").foreach(destination => assertRejects(function(destination), 60))
  }

  for (crossing <- Vec.nonEmpty(false, true))
    test(s"guarded address proof rejects changed guards and external entry: branch=$crossing") {
      val producer = Vec.nonEmpty(
        SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R12.CC", "R10", "RZ"), Some("@P0")),
        SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P1", "R13", "R10", "RZ", "R11"), Some("@P0")),
        SASSFixture.instruction(16, "LD.E.CG", Vec.nonEmpty("R4", "[R12]", "P1"), Some("@P0")),
        SASSFixture.instruction(24, "SEL", Vec.nonEmpty("R4", "RZ", "RZ", "P1"), Some("@!P0")),
        SASSFixture.instruction(32, "ISETP.EQ.AND", Vec.nonEmpty("P1", "PT", "RZ", "RZ", "PT")),
        SASSFixture.instruction(40, "EXIT", Vec.empty)
      )
      val entered = if (crossing) producer :+ SASSFixture.instruction(48, "BRA", Vec.nonEmpty("0x18"))
      else producer.take(3) ++
        Vec.nonEmpty(SASSFixture.instruction(20, "ISETP.EQ.AND", Vec.nonEmpty("P0", "PT", "RZ", "RZ", "PT"))) ++
        producer.drop(3)
      intercept[IllegalArgumentException](VMAddressAnalysis.rewrite(50, DecodedFunction("guarded", entered)))
    }

  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm$sm generic address proof retains global memory invalidation barriers") {
      def function(guard: Option[String]): DecodedFunction = DecodedFunction(
        "scan",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R2.CC", "R3", "0x100")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P5", "R3", "R3", "RZ", "R5")),
          SASSFixture.instruction(16, "MEMBAR.GL.IVALLD", Vec.empty, guard),
          SASSFixture.instruction(24, "ST.E.CG.U8", Vec.nonEmpty("[R2]", "R6", "P5")),
          SASSFixture.instruction(32, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function(None), sm)
      assertRejects(function(Some("@P5")), sm)
    }

  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm$sm generic address proof permits independent special-register reads") {
      def function(destination: String): DecodedFunction = DecodedFunction(
        "scan",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R2.CC", "R3", "0x100")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P5", "R3", "R3", "RZ", "R5")),
          SASSFixture.instruction(16, "S2R", Vec.nonEmpty(destination, "SR_LANEID")),
          SASSFixture.instruction(24, "ST.E.CG", Vec.nonEmpty("[R2]", "R6", "P5")),
          SASSFixture.instruction(32, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function("R7"), sm)
      Vec.nonEmpty("R2", "R3").foreach(destination => assertRejects(function(destination), sm))
    }

  for (sm <- Vec.nonEmpty(50, 60)) {
    test(s"sm$sm retains a predicated shared store without exposing the auxiliary predicate") {
      def function(guard: String): DecodedFunction = DecodedFunction(
        "guarded_store",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "LEA", Vec.nonEmpty("R8.CC", "R6", "RZ"), Some("@!P1")),
          SASSFixture.instruction(8, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R6", "RZ", "R7"), Some("@!P1")),
          SASSFixture.instruction(16, "STS", Vec.nonEmpty("[0x2c]", "R15"), Some(guard)),
          SASSFixture.instruction(24, "ST.E.CG.64", Vec.nonEmpty("[R8]", "R14", "P2"), Some("@!P1")),
          SASSFixture.instruction(32, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function("@!P1"), sm)
      assertRejects(function("@P2"), sm)
    }

    test(s"sm$sm unconditional proven SYNC skips another path's auxiliary predicate read") {
      def function(guard: Option[String]): DecodedFunction = DecodedFunction(
        "sync_paths",
        Vec.nonEmpty(
          SASSFixture.instruction(0, "SSY", Vec.nonEmpty("0x40")),
          SASSFixture.instruction(8, "BRA", Vec.nonEmpty("0x30"), Some("@P1")),
          SASSFixture.instruction(16, "LEA", Vec.nonEmpty("R8.CC", "R6", "RZ")),
          SASSFixture.instruction(24, "LEA.HI.X", Vec.nonEmpty("P2", "R9", "R6", "RZ", "R7")),
          SASSFixture.instruction(32, "ST.E.CG.64", Vec.nonEmpty("[R8]", "R14", "P2")),
          SASSFixture.instruction(40, "SYNC", Vec.empty, guard),
          SASSFixture.instruction(48, "SEL", Vec.nonEmpty("R12", "R6", "R7", "P2")),
          SASSFixture.instruction(56, "SYNC", Vec.empty),
          SASSFixture.instruction(64, "ISETP.EQ.AND", Vec.nonEmpty("P2", "PT", "RZ", "RZ", "PT")),
          SASSFixture.instruction(72, "EXIT", Vec.empty)
        )
      )
      assertRewrites(function(None), sm)
      assertRejects(function(Some("@P3")), sm)
    }
  }

  private val listing = Map(
    50 -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm50,
    60 -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm60
  )

  private def rewritten(sm: Int, function: DecodedFunction): DecodedFunction =
    VMAddressAnalysis.rewrite(sm, function)

  private def isHigh(instruction: DecodedInstruction): Boolean =
    instruction.mnemonic.raw == "LEA" &&
      instruction.modifiers == Vec.nonEmpty(Instruction.Modifier.HI, Instruction.Modifier.X)

  private def baseline(sm: Int): (DecodedFunction, Int, Operand) = {
    val function = ListingDecoder.parse(
      listing(sm),
      "global_load",
      sm
    ).fold(fail(_), _.head)
    val memoryIndex = function.instructions.indexWhere(instruction =>
      instruction.mnemonic.raw == "LD" && instruction.modifiers == Vec.nonEmpty(Instruction.Modifier.E)
    )
    assert(memoryIndex >= 0)
    val highIndex = function.instructions.indexWhere(isHigh(_))
    assert(highIndex >= 0 && highIndex < memoryIndex)
    (function, memoryIndex, function.instructions(highIndex).operands.head)
  }

  private def instruction(
      start: Int,
      index: Int,
      opcode: String,
      operands: Vec[String] = Vec.empty,
      predicate: Option[String] = None
  ): DecodedInstruction =
    SASSFixture.instruction(start + index * 8, opcode, operands, predicate)

  private def typedInstruction(
      start: Int,
      index: Int,
      opcode: String,
      operands: Vec[Operand],
      guard: Option[Instruction.Guard] = None
  ): DecodedInstruction = {
    val parts = Vec.from(opcode.split('.'))
    DecodedInstruction(
      (start + index * 8).unsafe[Int >= 0],
      Instruction.decode(parts.head, parts.drop(1).map(Instruction.Modifier(_)), operands, guard)
        .fold(error => fail(error), identity),
      None,
      None
    )
  }

  private def predicateGuard(operand: Operand): Option[Instruction.Guard] = operand match {
    case value: Operand.Predicate => Some(Instruction.Guard(value.copy(negated = false)))
    case other                    => fail(s"expected auxiliary predicate, found $other")
  }

  private def withAuxiliary(auxiliary: Operand): Vec[Operand] =
    Vec.nonEmpty(auxiliary) ++ SASSFixture.operands("PT, RZ, RZ, PT")

  private def functionWithTail(
      original: DecodedFunction,
      memoryIndex: Int,
      tail: Vec[DecodedInstruction]
  ): DecodedFunction =
    original.copy(instructions = original.instructions.take(memoryIndex + 1) ++ tail)

  private def assertRewrites(function: DecodedFunction, sm: Int): Unit = {
    val result = rewritten(sm, function)
    val high   = result.instructions.find(isHigh(_)).getOrElse(fail("missing rewritten LEA.HI.X"))
    // The rewrite replaces LEA.HI.X's auxiliary-predicate destination with
    // the actual high address register while dropping the padding immediate.
    assert(high.operands.head match {
      case Operand.Register(RegisterBank.R, _) => true
      case _                                   => false
    })
  }

  private def assertRejects(function: DecodedFunction, sm: Int): Unit = {
    val _ = intercept[IllegalArgumentException](rewritten(sm, function))
  }

  for (sm <- Vec.nonEmpty(50, 60)) test(s"sm_$sm distinguishes vote predicate outputs from inputs") {
    val (original, memoryIndex, auxiliary) = baseline(sm)
    val start                              = original.instructions(memoryIndex).address.raw + 8
    val always                             = SASSFixture.operand("PT")
    Vec.nonEmpty(Vec.empty[Operand], Vec.nonEmpty(SASSFixture.operand("RZ"))).foreach { prefix =>
      val killed = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          typedInstruction(start, 0, "VOTE.ANY", prefix ++ Vec.nonEmpty(auxiliary, always)),
          typedInstruction(start, 1, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          instruction(start, 2, "EXIT")
        )
      )
      assertRewrites(killed, sm)
      val read = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          typedInstruction(start, 0, "VOTE.ANY", prefix ++ Vec.nonEmpty(always, auxiliary)),
          instruction(start, 1, "EXIT")
        )
      )
      assertRejects(read, sm)
    }
  }

  for (sm <- Vec.nonEmpty(50, 60))
    test(s"sm_$sm permits control-flow paths only when auxiliary P is dead on every successor") {
      val (original, memoryIndex, auxiliary) = baseline(sm)
      val firstTailAddress                   = original.instructions(memoryIndex).address.raw + 8

      // Both sides of the conditional branch reach the unconditional definition.
      val branchToKill = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "BRA", Vec.nonEmpty(f"0x${firstTailAddress + 16}%x"), Some("@P1")),
          instruction(firstTailAddress, 1, "NOP"),
          typedInstruction(firstTailAddress, 2, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 3, "EXIT")
        )
      )
      assertRewrites(branchToKill, sm)

      // A later independently verified address sequence replaces the old auxiliary value.
      val highIndex   = original.instructions.indexWhere(isHigh(_))
      val replacement = Vec.nonEmpty(
        original.instructions(highIndex - 1),
        original.instructions(highIndex),
        original.instructions(memoryIndex)
      ).zipWithIndex.map { case (item, index) => item.copy(address = (firstTailAddress + index * 8).unsafe[Int >= 0]) }
      assertRewrites(
        functionWithTail(
          original,
          memoryIndex,
          replacement :+ instruction(firstTailAddress, 3, "EXIT")
        ),
        sm
      )

      // Both SYNC successors are safe: fallthrough and the SSY reconvergence target reach the kill.
      val syncToKill = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 16}%x")),
          instruction(firstTailAddress, 1, "SYNC"),
          typedInstruction(firstTailAddress, 2, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 3, "EXIT")
        )
      )
      assertRewrites(syncToKill, sm)

      // One branch successor reads the old P before the kill; an EXIT on the other path is safe.
      val branchToRead = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "BRA", Vec.nonEmpty(f"0x${firstTailAddress + 24}%x"), Some("@P1")),
          instruction(firstTailAddress, 1, "EXIT"),
          instruction(firstTailAddress, 2, "NOP"),
          typedInstruction(firstTailAddress, 3, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          typedInstruction(firstTailAddress, 4, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 5, "EXIT")
        )
      )
      assertRejects(branchToRead, sm)

      // SYNC may reconverge at the alternate SSY target, so checking only its fallthrough is unsafe.
      val syncToRead = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 32}%x")),
          instruction(firstTailAddress, 1, "SYNC"),
          typedInstruction(firstTailAddress, 2, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 3, "EXIT"),
          typedInstruction(firstTailAddress, 4, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          typedInstruction(firstTailAddress, 5, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 6, "EXIT")
        )
      )
      assertRejects(syncToRead, sm)

      // A branch can skip the inner SSY; its lexical position does not prove
      // that the outer reconvergence target is unreachable from SYNC.
      val skippedSetup = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 64}%x")),
          instruction(firstTailAddress, 1, "BRA", Vec.nonEmpty(f"0x${firstTailAddress + 32}%x"), Some("@P1")),
          instruction(firstTailAddress, 2, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 48}%x")),
          instruction(firstTailAddress, 3, "BRA", Vec.nonEmpty(f"0x${firstTailAddress + 32}%x")),
          instruction(firstTailAddress, 4, "SYNC"),
          typedInstruction(firstTailAddress, 5, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          typedInstruction(firstTailAddress, 6, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 7, "EXIT"),
          typedInstruction(firstTailAddress, 8, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          instruction(firstTailAddress, 9, "EXIT")
        )
      )
      assertRejects(skippedSetup, sm)

      // A dominating inner setup remains active after a false predicated SYNC.
      // Taking that earlier SYNC skips the consumer and reaches the same kill.
      // The unrelated outer target must not be treated as an immediate successor.
      val nestedSetup = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 64}%x")),
          instruction(firstTailAddress, 1, "SSY", Vec.nonEmpty(f"0x${firstTailAddress + 40}%x")),
          instruction(firstTailAddress, 2, "SYNC", predicate = Some("@P1")),
          instruction(firstTailAddress, 3, "SYNC"),
          instruction(firstTailAddress, 4, "NOP"),
          typedInstruction(firstTailAddress, 5, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 6, "EXIT"),
          instruction(firstTailAddress, 7, "NOP"),
          typedInstruction(firstTailAddress, 8, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          instruction(firstTailAddress, 9, "EXIT")
        )
      )
      assertRewrites(nestedSetup, sm)

      // Reject reentry while its token is still active.
      val repeatedSetup = nestedSetup.copy(instructions = nestedSetup.instructions.map { item =>
        if (item.address.raw == firstTailAddress + 16)
          item.rewrite(typedInstruction(
            item.address.raw,
            0,
            "BRA",
            Vec.nonEmpty(Operand.Bits(BigInt(firstTailAddress + 8), 0)),
            guard = item.guard
          ).operation)
        else item
      })
      assertRejects(repeatedSetup, sm)

      // Branching on the auxiliary predicate itself exposes its numeric value.
      val predicateBranch = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          typedInstruction(
            firstTailAddress,
            0,
            "BRA",
            Vec.nonEmpty(Operand.Bits(BigInt(firstTailAddress + 16), 0)),
            predicateGuard(auxiliary)
          ),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(predicateBranch, sm)

      // P2R observes predicate state outside an explicit predicate operand.
      val predicateBankRead = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "P2R", Vec.nonEmpty("R8", "PR", "RZ", "0x7f")),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(predicateBankRead, sm)

      // A predicated definition is not a definite kill on paths where its guard is false.
      val predicatedKill = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          typedInstruction(
            firstTailAddress,
            0,
            "ISETP.EQ.AND",
            withAuxiliary(auxiliary),
            Some(Instruction.Guard(Operand.Predicate(Some(1), uniform = false, negated = false)))
          ),
          typedInstruction(firstTailAddress, 1, "MOV", SASSFixture.operands("R8, RZ"), predicateGuard(auxiliary)),
          typedInstruction(firstTailAddress, 2, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 3, "EXIT")
        )
      )
      assertRejects(predicatedKill, sm)

      // Keep rejecting malformed/unknown direct targets after the memory consumer.
      val unknownTarget = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "BRA", Vec.nonEmpty("0xdeadbeef")),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(unknownTarget, sm)

      // Legacy .S suffixes also transfer control; ordinary fallthrough would
      // incorrectly treat the following definition as a guaranteed kill.
      val implicitTransfer = functionWithTail(
        original,
        memoryIndex,
        Vec.nonEmpty(
          instruction(firstTailAddress, 0, "NOP.S"),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(implicitTransfer, sm)
    }
}
