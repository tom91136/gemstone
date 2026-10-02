package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.sass.asm.*
import net.kurobako.gemstone.isa.sass.fixtures.*

/** Static CFG checks for the deadness proof of generic LEA auxiliary predicates. */
class LegacyGenericPredicateLivenessSuite extends munit.FunSuite {
  private val listing = Map(
    50 -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm50,
    60 -> GenericMemoryFixtures.legacyGenericAddressGlobalLoadSm60
  )

  private def rewritten(sm: Int, function: DecodedFunction): DecodedFunction =
    VMAddressAnalysis.rewrite(sm, function)

  private def isHigh(instruction: DecodedInstruction): Boolean =
    instruction.mnemonic.raw == "LEA" &&
      instruction.modifiers == Vec(Instruction.Modifier.HI, Instruction.Modifier.X)

  private def baseline(sm: Int): (DecodedFunction, Int, Operand) = {
    val function = ListingDecoder.parse(
      listing(sm),
      "global_load",
      sm
    ).fold(fail(_), _.head)
    val memoryIndex = function.instructions.indexWhere(instruction =>
      instruction.mnemonic.raw == "LD" && instruction.modifiers == Vec(Instruction.Modifier.E)
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
    Vec(auxiliary) ++ SASSFixture.operands("PT, RZ, RZ, PT")

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

  for (sm <- Vec(50, 60))
    test(s"sm_$sm permits control-flow paths only when auxiliary P is dead on every successor") {
      val (original, memoryIndex, auxiliary) = baseline(sm)
      val firstTailAddress                   = original.instructions(memoryIndex).address.raw + 8

      // Both sides of the conditional branch reach the unconditional definition.
      val branchToKill = functionWithTail(
        original,
        memoryIndex,
        Vec(
          instruction(firstTailAddress, 0, "BRA", Vec(f"0x${firstTailAddress + 16}%x"), Some("@P1")),
          instruction(firstTailAddress, 1, "NOP"),
          typedInstruction(firstTailAddress, 2, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 3, "EXIT")
        )
      )
      assertRewrites(branchToKill, sm)

      // A later independently verified address sequence replaces the old auxiliary value.
      val highIndex   = original.instructions.indexWhere(isHigh(_))
      val replacement = Vec(
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
        Vec(
          instruction(firstTailAddress, 0, "SSY", Vec(f"0x${firstTailAddress + 16}%x")),
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
        Vec(
          instruction(firstTailAddress, 0, "BRA", Vec(f"0x${firstTailAddress + 24}%x"), Some("@P1")),
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
        Vec(
          instruction(firstTailAddress, 0, "SSY", Vec(f"0x${firstTailAddress + 32}%x")),
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
        Vec(
          instruction(firstTailAddress, 0, "SSY", Vec(f"0x${firstTailAddress + 64}%x")),
          instruction(firstTailAddress, 1, "BRA", Vec(f"0x${firstTailAddress + 32}%x"), Some("@P1")),
          instruction(firstTailAddress, 2, "SSY", Vec(f"0x${firstTailAddress + 48}%x")),
          instruction(firstTailAddress, 3, "BRA", Vec(f"0x${firstTailAddress + 32}%x")),
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
        Vec(
          instruction(firstTailAddress, 0, "SSY", Vec(f"0x${firstTailAddress + 64}%x")),
          instruction(firstTailAddress, 1, "SSY", Vec(f"0x${firstTailAddress + 40}%x")),
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

      // Reentering the setup without consuming its token can leave multiple
      // active tokens. Keep the conservative successor set for that shape.
      val repeatedSetup = nestedSetup.copy(instructions = nestedSetup.instructions.map { item =>
        if (item.address.raw == firstTailAddress + 16)
          item.rewrite(typedInstruction(
            item.address.raw,
            0,
            "BRA",
            Vec(Operand.Bits(BigInt(firstTailAddress + 8), 0)),
            guard = item.guard
          ).operation)
        else item
      })
      assertRejects(repeatedSetup, sm)

      // Branching on the auxiliary predicate itself exposes its numeric value.
      val predicateBranch = functionWithTail(
        original,
        memoryIndex,
        Vec(
          typedInstruction(
            firstTailAddress,
            0,
            "BRA",
            Vec(Operand.Bits(BigInt(firstTailAddress + 16), 0)),
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
        Vec(
          instruction(firstTailAddress, 0, "P2R", Vec("R8", "PR", "RZ", "0x7f")),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(predicateBankRead, sm)

      // A predicated definition is not a definite kill on paths where its guard is false.
      val predicatedKill = functionWithTail(
        original,
        memoryIndex,
        Vec(
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
        Vec(
          instruction(firstTailAddress, 0, "BRA", Vec("0xdeadbeef")),
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
        Vec(
          instruction(firstTailAddress, 0, "NOP.S"),
          typedInstruction(firstTailAddress, 1, "ISETP.EQ.AND", withAuxiliary(auxiliary)),
          instruction(firstTailAddress, 2, "EXIT")
        )
      )
      assertRejects(implicitTransfer, sm)
    }
}
