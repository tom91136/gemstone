package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.testing.Suite

class ModifierDomainSuite extends Suite {
  private val families = Set(
    "add",
    "mul",
    "mad",
    "fma",
    "setp",
    "selp",
    "and",
    "shl",
    "lop3",
    "cvt",
    "mov",
    "prmt",
    "ld",
    "st",
    "atom",
    "red",
    "bar",
    "vote.sync",
    "shfl.sync",
    "cp.async",
    "cp.async.bulk",
    "mbarrier.arrive",
    "ldmatrix",
    "mma",
    "mma.sp",
    "wmma.load",
    "wmma.mma",
    "tcgen05.mma",
    "tcgen05.ld",
    "fence",
    "tex"
  )

  private val sample = Instruction.definitions.filter(definition => families(definition.opcode.raw))

  private def modifier(name: String): Modifier = Modifier(name.unsafe[NonEmpty[String]])

  private def operand(constraint: Metadata.OperandConstraint): Operand = constraint match {
    case Metadata.OperandConstraint.Address       => Operand.Address(Vec(Operand.Register("%rd0", None)))
    case Metadata.OperandConstraint.VectorOperand => Operand.VectorLiteral(Vec(Operand.Register("%r0", None)))
    case Metadata.OperandConstraint.Symbol        => Operand.Symbol("target")
    case _                                        => Operand.Register("%r0", None)
  }

  private def outcome(definition: Instruction.Definition, form: ModifierForm, modifiers: Vec[Modifier]) = {
    val operands = form.operands.filter(!_.optional).map(syntax => operand(syntax.constraint))
    Instruction.decode(definition.opcode.raw, operands, modifiers).map(instruction =>
      Validation.validate(instruction, Version(99, 0), Target(999))
    )
  }

  private def invalidModifiers(result: Either[DecodeError, ValidationOutcome[Validation.Issue]]): Boolean =
    result.exists {
      case ValidationOutcome.Rejected(issues) =>
        issues.exists { case Validation.Issue.InvalidModifiers(_, _) => true; case _ => false }
      case _ => false
    }

  private def first(domain: ModifierDomain): Modifier = domain.values.toVec.minBy(_.name.raw)

  private def base(form: ModifierForm): Vec[Modifier] = form.required.map(first(_))

  test("every definition has syntax forms with typed slots") {
    assert(Instruction.definitions.forall(definition => ModifierDomains.forms(definition).nonEmpty))
    assert(ModifierDomains.all.exists(_.slot.contains(ModifierSlot.Type)))
    assertEquals(ModifierSlot("type"), Some(ModifierSlot.Type))
    assertEquals(ModifierSlot("sync"), None)
  }

  test("domains hold the modifier groups the metadata does") {
    val mismatches = Instruction.definitions.flatMap { definition =>
      val typed  = ModifierDomains.forms(definition).flatMap(_.required.map(_.values)).toSet
      val legacy = definition.meta.entries.raw.view.flatMap(_.forms).flatMap(_.requiredModifierGroups.map(_.raw)).toSet
      Option.when(!legacy.subsetOf(typed))(definition.toString)
    }
    assertEquals(mismatches, Vec.empty[String])
  }

  test("each value of each slot is accepted where the syntax allows it") {
    val rejected = sample.flatMap { definition =>
      ModifierDomains.forms(definition).take(3).flatMap { form =>
        form.required.zipWithIndex.flatMap { (domain, slot) =>
          domain.values.toVec.sortBy(_.name.raw).take(24).flatMap { value =>
            val modifiers = base(form).updated(slot, value)
            Option.when(ModifierDomains.diagnose(definition, modifiers).nonEmpty)(
              s"${definition.opcode} ${modifiers.map(_.name.raw).mkString(".")}"
            )
          }
        }
      }
    }
    assertEquals(rejected, Vec.empty[String])
  }

  test("a value of another slot is rejected where the syntax does not allow it") {
    val accepted = sample.flatMap { definition =>
      ModifierDomains.forms(definition).take(2).flatMap { form =>
        val foreign = ModifierDomains.all.view.filter(_.name != "type").flatMap(_.values).toSet --
          form.required.flatMap(_.values) -- form.optional.flatMap(_.values)
        foreign.toVec.sortBy(_.name.raw).take(8).flatMap { value =>
          val modifiers = base(form) :+ value
          Option.when(ModifierDomains.diagnose(definition, modifiers).isEmpty && form.unordered.isEmpty)(
            s"${definition.opcode} ${modifiers.map(_.name.raw).mkString(".")}"
          )
        }
      }
    }
    assertEquals(accepted, Vec.empty[String])
  }

  test("a modifier outside every slot is rejected with the unknown modifier named") {
    val bad = sample.flatMap { definition =>
      ModifierDomains.forms(definition).take(1).flatMap { form =>
        val modifiers = base(form) :+ modifier("not_a_modifier")
        ModifierDomains.diagnose(definition, modifiers) match {
          case Some(ModifierViolation.Unknown(found)) if found == modifier("not_a_modifier") => None
          case other => Some(s"${definition.opcode}: $other")
        }
      }
    }
    assertEquals(bad, Vec.empty[String])
  }

  test("an instruction with a modifier outside its slots is rejected by the validator with the slot in the message") {
    val result = Parser.parseStatement("add.s32.vendor %r0, %r1, %r2;").toOption.get
    result match {
      case Program.Statement.Instruction(value) =>
        Validation.validate(value, Version(9, 3), Target(100)) match {
          case ValidationOutcome.Rejected(issues) =>
            assert(issues.exists(_.message.raw.contains(".vendor is not a modifier")), issues)
          case other => fail(s"accepted: $other")
        }
      case other => fail(s"unexpected $other")
    }
  }

  test("a required slot left empty is reported as missing") {
    val definition = Instruction.definitions.find(_.opcode.raw == "add").get
    ModifierDomains.diagnose(definition, Vec.empty[Modifier]) match {
      case Some(ModifierViolation.Missing(domain)) => assert(domain.slot.nonEmpty && domain.values.nonEmpty, domain)
      case other                                   => fail(s"unexpected $other")
    }
  }

  test("slots out of order are reported as misplaced") {
    val definition = Instruction.definitions.find(_.opcode.raw == "mul").get
    ModifierDomains.diagnose(definition, Vec(Modifier.S32, Modifier.Wide)) match {
      case Some(ModifierViolation.Misplaced(found, expected)) =>
        assertEquals(found, Modifier.S32)
        assert(expected.exists(_.values.contains(Modifier.Wide)))
      case other => fail(s"unexpected $other")
    }
  }

  test("the typed model agrees with the metadata on rejected modifier sequences") {
    val disagreements = sample.flatMap { definition =>
      ModifierDomains.forms(definition).take(1).flatMap { form =>
        val modifiers = base(form) :+ modifier("not_a_modifier")
        val legacy    = invalidModifiers(outcome(definition, form, modifiers))
        Option.when(!legacy && outcome(definition, form, modifiers).isRight)(s"${definition.opcode} accepted")
      }
    }
    assertEquals(disagreements, Vec.empty[String])
  }
}
