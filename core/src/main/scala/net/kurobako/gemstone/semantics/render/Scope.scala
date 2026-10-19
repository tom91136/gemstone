package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.collection.mutable

/** The names a rendering uses: the operands it asks `naming` for and the state it declares, each with the type it first
  * takes, in the order they first appear; its temporaries, `t0` on; and the intermediates its definition keeps as
  * bindings of their own. Each operand asked for is remembered, so the operands a rendering touches are known
  * afterwards.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.var"))
class Scope[O](naming: Naming[O]) {
  private val names     = mutable.LinkedHashMap.empty[String, Naming.Name]
  private val conflicts = mutable.LinkedHashSet.empty[String]
  private val keeps     = mutable.LinkedHashSet.empty[String]
  private val seen      = mutable.LinkedHashSet.empty[O]
  private var count     = 0

  /** `name`, declared with type `tpe`; one declared before with another type conflicts. */
  def declare(name: String, tpe: Type): String = declare(Naming.Name(name, tpe))

  def declare(named: Naming.Name): String = {
    if (names.getOrElseUpdate(named.name, named).tpe != named.tpe) conflicts += named.name
    named.name
  }

  /** The values `operand` stands for, declared. */
  def operands(operand: O): Vec[Naming.Name] = {
    seen += operand
    val found = naming.resolve(operand)
    found.foreach(declare(_))
    found
  }

  /** The value `operand` stands for, declared. */
  def operand(operand: O): Option[Naming.Name] = operands(operand).headOption

  /** Every operand asked for so far, named or not. */
  def touched: Set[O] = seen.toSet

  def temporary(): String = {
    count += 1
    s"t${count - 1}"
  }

  /** Each assignment to `name` stays a binding of its own in the definition. */
  def keep(name: String): Unit = { val _ = keeps += name }

  /** Every operand and piece of state declared so far, with its type: the operands in `order` first, each at its last
    * place there, then the others in the order they first appear. A later part of an operand, such as the second
    * register of a range, follows the operand's first.
    */
  def declared(order: Vec[String] = Vec.empty): Vec[(name: String, tpe: Type)] = {
    val ranks = order.zipWithIndex.toMap
    names.values.toVec.zipWithIndex
      .sortBy((found, index) => (ranks.getOrElse(found.operand, order.size), found.part, index))
      .map((found, _) => (found.name, found.tpe))
  }

  /** Names declared with more than one type. */
  def conflicting: Vec[String] = conflicts.toVec

  def kept: Set[String] = keeps.toSet
}
