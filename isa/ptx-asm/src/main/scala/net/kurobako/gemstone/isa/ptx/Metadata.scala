package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*

object Metadata {
  enum OperandConstraint derives CanEqual {
    case Register
    case Address
    case VectorOperand
    case Symbol
    case Value
    case Unknown
  }

  type ModifierGroup = ModifierGroup.Type
  object ModifierGroup extends Newtype[NonEmpty[Set[Modifier]]] {
    def apply(
        first: Modifier,
        rest: Modifier*
    ): ModifierGroup = (first +: rest).toSet.unsafe[ModifierGroup]
  }

  /** One normalised alternative from an instruction's syntax rubric; `unorderedModifiers` may appear in any order among
    * themselves.
    */
  case class InstructionForm(
      operands: Vec[OperandConstraint],
      requiredModifierGroups: Vec[ModifierGroup],
      optionalModifiers: Set[Modifier],
      unorderedModifiers: Set[Modifier],
      complete: Boolean
  )

  /** One PTX documentation section that defines the owning instruction; `modifierTargets` raises the minimum target for
    * instructions carrying every modifier of a combination.
    */
  case class Entry(
      introduced: Version,
      minimumTarget: Target,
      modifierTargets: Map[Set[Modifier], Target],
      forms: Vec[InstructionForm]
  )

  private val Constraints = "RAVSXU"

  /** Reads the generated text of one definition: an entry per line, holding the version, the minimum target, the
    * modifier targets and the forms separated by spaces. A form holds its operand constraints, required modifier
    * groups, optional and unordered modifiers and completeness separated by semicolons.
    */
  def parse(text: String): Metadata = {
    def modifier(name: String): Modifier        = Modifier(name.unsafe[NonEmpty[String]])
    def modifiers(names: String): Vec[Modifier] = Vec.from(names.split(',')).filter(_.nonEmpty).map(modifier(_))
    def target(value: String): Target           = Target(value.toInt.unsafe[Int >= 10])
    def form(text: String): InstructionForm     = {
      val fields = text.split(";", -1)
      InstructionForm(
        Vec.from(fields(0).view).map(code => OperandConstraint.fromOrdinal(Constraints.indexOf(code))),
        Vec.from(fields(1).split('/')).filter(_.nonEmpty).map(group => modifiers(group).toSet.unsafe[ModifierGroup]),
        modifiers(fields(2)).toSet,
        modifiers(fields(3)).toSet,
        fields(4) == "1"
      )
    }
    def entry(line: String): Entry = {
      val fields          = line.split(' ')
      val version         = fields(0).split('.')
      val modifierTargets = Vec.from(fields(2).split(',')).filter(_.nonEmpty).map { value =>
        val parts = value.split('=')
        Vec.from(parts(0).split('+')).map(modifier(_)).toSet -> target(parts(1))
      }
      Entry(
        Version(version(0).toInt.unsafe[Int > 0], version(1).toInt.unsafe[Int >= 0]),
        target(fields(1)),
        modifierTargets.toMap,
        Vec.from(fields.drop(3)).map(form(_))
      )
    }
    Metadata(Vec.from(text.split('\n')).map(entry(_)).unsafe[NonEmpty[Vec[Entry]]])
  }
}

/** Specification metadata attached to one generated instruction definition. */
case class Metadata(entries: NonEmpty[Vec[Metadata.Entry]])
