package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*

object Naming {

  object Name {
    def apply(name: String, tpe: Type): Name = Name(name, tpe, name, 0)

    /** `name` with its type, where a name `operand+part` stands for a later register of `operand`. */
    def spelt(name: String, tpe: Type): Name = {
      val at   = name.lastIndexOf('+')
      val part = name.drop(at + 1)
      if (at > 0 && part.nonEmpty && part.forall(_.isDigit)) Name(name, tpe, name.take(at), part.toInt)
      else Name(name, tpe)
    }
  }

  /** A declared name with its type. `operand` names the operand the value belongs to, as the operand order of a
    * definition names it, and `part` is the value's place within it: a later register of an operand follows the
    * operand's first.
    */
  case class Name(name: String, tpe: Type, operand: String, part: Int)
}

/** How an ISA names and types the operands of its rendered semantics, `O` being what the ISA identifies an operand by,
  * such as a register slot or an id.
  */
trait Naming[-O] {

  /** The scalar values `operand` stands for, in lane order; none where it has no name of its own, as a temporary. */
  def resolve(operand: O): Vec[Naming.Name]
}
