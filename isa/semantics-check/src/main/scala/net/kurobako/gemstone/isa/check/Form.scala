package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** One instruction of a sequence an equivalence check compares: its rendered case, where each parameter and result of
  * the definition lives, and the cases of the differential lane that stand behind it. A location is a 32-bit register
  * unit or a flag, as the live sets name them, so a wide operand is the units it spans, low first, and a span aliases
  * the registers under it.
  */
case class Form(
    kase: Case,
    parameters: Map[String, Form.Operand],
    results: Map[String, Vec[String]],
    lane: Form.Lane
) {
  import Form.*

  /** The values of the definition's parameters, in order, read from the locations `held` gives. */
  def inputs(held: String => Long): Vec[Long] = kase.definition.parameters.map(parameter =>
    parameters(parameter.name) match {
      case Operand.Location(name)    => parameter.tpe.represent(held(name))
      case Operand.Immediate(value)  => value
      case Operand.Joined(locations) =>
        parameter.tpe.represent(locations.zipWithIndex.foldLeft(0L) { case (joined, (name, index)) =>
          joined | ((held(name) & UnitMask) << (index * UnitBits))
        })
    }
  )

  /** Each location the definition's results are written to with what it then holds, given the results in order. */
  def outputs(values: Vec[Long]): Vec[(String, Long)] = kase.definition.results.zip(values).flatMap { (result, value) =>
    results(result.name) match {
      case Vec(single) => Vec(single -> value)
      case several     => several.zipWithIndex.map((name, index) => name -> ((value >>> (index * UnitBits)) & UnitMask))
    }
  }
}

// the three-argument apply builds a form with an empty lane set and is used only by FormSuite; the ISA renderers call the four-argument Form constructor
@SuppressWarnings(Array("bonsai:test-only-member"))
object Form {
  val UnitBits = 32

  /** The type of one unit, and of two joined. */
  val Unit: Type       = Type.unsigned(32)
  val Pair: Type       = Type.unsigned(64)
  private val UnitMask = 0xffffffffL

  enum Operand derives CanEqual {
    case Location(name: String)
    case Immediate(value: Long)
    case Joined(locations: Vec[String])
  }

  /** The lane cases behind an instruction, none where its case is itself one of the lane's. They are found when a proof
    * asks for them: describing every instance of a mnemonic costs far more than admitting one instruction.
    */
  final class Lane(found: => Vec[Case]) {
    lazy val cases: Vec[Case] = found
  }

  /** A lane case as a step of its own, each result in one location. */
  def apply(kase: Case, parameters: Map[String, Operand], results: Map[String, String]): Form =
    Form(kase, parameters, results.map((name, location) => name -> Vec(location)), Lane(Vec.empty))

  /** The operand that reads `units`. */
  def read(units: Vec[String]): Operand = units match {
    case Vec(single) => Operand.Location(single)
    case several     => Operand.Joined(several)
  }

  /** Whether one location can hold values of both types: the same type, or integers of one width, whose sign each use
    * converts.
    */
  def compatible(left: Type, right: Type): Boolean = (left, right) match {
    case (Type.Integer(first, _), Type.Integer(second, _)) => first == second
    case _                                                 => left == right
  }
}
