package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.render.*

/** The element of D of a tcgen05.mma product rendered as the lane algebra computes it, over operands read from the
  * product.
  */
private[isa] object TensorMemoryRender {

  /** The operands of element `index` of D in `product`, each read from it by `tensors`; A and B negated where the flags
    * of `negate` hold.
    */
  private class GatheredTerms(
      names: Names,
      multiply: Matrix.Operation.Multiply,
      tensors: (contextType: String, context: String),
      product: Term,
      enable: Term,
      index: Term,
      negate: Option[(a: Term, b: Term)]
  ) extends MatrixTerms(names, multiply) with TensorMemoryOperands[Term] {
    private def read(method: String, tpe: Type, position: Option[Int]): Term =
      access(method, Vec(tpe), tpe, position)
    private def readUntyped(method: String, tpe: Type, position: Option[Int]): Term =
      access(method, Vec.empty, tpe, position)
    private def access(method: String, typeArgs: Vec[Type], tpe: Type, position: Option[Int]): Term = Term.context(
      tensors,
      MachineCall(
        method,
        typeArgs,
        Vec((product, None), (index, Some(Type.unsigned(64)))) ++
          Vec.from(position).map(at => (Term.Constant(at.toString), None))
      ),
      Some(tpe)
    )
    private def negated(element: Matrix.Element, flag: Option[Term], value: Term): Term =
      flag.fold(value)(set => Render.select(set, TensorSemantics.negation(Render, element, value), value))
    override def a(at: Int): Term =
      negated(multiply.a.element, negate.map(_.a), read("a", MatrixTerms.elementType(multiply.a.element), Some(at)))
    override def b(at: Int): Term =
      negated(multiply.b.element, negate.map(_.b), read("b", MatrixTerms.elementType(multiply.b.element), Some(at)))
    override def c: Term                  = read("c", MatrixTerms.elementType(multiply.c.element), None)
    override def selection(at: Int): Term = readUntyped("selection", Type.unsigned(2), Some(at))
    override def scaleA(block: Int): Term = read("scaleA", scaleType, Some(block))
    override def scaleB(block: Int): Term = read("scaleB", scaleType, Some(block))
    def enabled: Term                     = enable
  }

  /** The steps that bind the intermediates of element `index` of D in `product`, and its value. */
  def element(
      names: Names,
      tensors: (contextType: String, context: String),
      product: Term,
      enable: Term,
      shape: TensorMemory.Product,
      index: Term,
      negate: Option[(a: Term, b: Term)] = None
  ): (steps: Term.Steps, value: Term) = {
    val operands = GatheredTerms(names, shape.multiply, tensors, product, enable, index, negate)
    val value    = TensorMemorySemantics.element(Render, shape, operands)
    (operands.steps, value)
  }

  /** The element math of the descriptors that share their steps, under the condition that selects them where the
    * descriptors split into more than one such class.
    */
  case class Dispatched(condition: Option[Term], steps: Term.Steps, value: Term)

  /** A term for the descriptors whose format codes, in the order of [[TensorMemory.Fields.formats]], are `codes`. */
  private case class Arm(codes: Vec[Long], term: Term)

  /** What makes the steps of two products differ in shape or type, so that no operand choice can merge them. */
  private case class Kind(
      steps: Vec[String],
      d: Matrix.Element,
      c: Matrix.Element,
      accumulation: Matrix.Accumulation,
      vector: Option[Int],
      saturate: Boolean,
      sparse: Boolean,
      depth: Int,
      bits: Int,
      integers: Option[(Matrix.Element, Matrix.Element)]
  ) derives CanEqual

  private def extract(descriptor: Term, field: TensorMemory.Field): Term = Term.truncate(
    if (field.offset == 0) descriptor else Term.Binary(">>u", descriptor, Term.Literal(field.offset.toLong)),
    field.width.unsafe[Int in 1 ~ 64]
  )

  private def either(terms: Vec[Term]): Term = terms.reduceLeft[Term](Term.Binary("|", _, _))
  private def both(terms: Vec[Term]): Term   = terms.reduceLeft[Term](Term.Binary("&", _, _))

  private def binaries(arms: Vec[Arm]): Option[(String, Vec[Arm], Vec[Arm])] = arms.head.term match {
    case Term.Binary(operator, _, _) =>
      val parts = arms.collect { case Arm(codes, Term.Binary(`operator`, left, right)) =>
        (Arm(codes, left), Arm(codes, right))
      }
      Option.when(parts.size == arms.size)((operator, parts.map(_._1), parts.map(_._2)))
    case _ => None
  }

  /** Choices among terms by the format codes, each tested through the term in `reads` for its field's index. */
  private class Choice(reads: Map[Int, Term]) {
    private def test(at: Int, code: Long): Term = Term.Binary("==", reads(at), Term.Literal(code))

    /** The term of the arm whose codes the descriptor has, sharing what the arms share, over the fields `varying`. */
    def apply(arms: Vec[Arm], varying: Vec[Int]): Term = {
      val head = arms.head.term
      if (arms.forall(_.term == head)) head
      else binaries(arms).fold(select(arms, varying))((operator, lefts, rights) =>
        Term.Binary(operator, apply(lefts, varying), apply(rights, varying))
      )
    }

    private def select(arms: Vec[Arm], varying: Vec[Int]): Term = {
      val live        = varying.filter(at => arms.exists(_.codes(at) != arms.head.codes(at)))
      val determining = live.find(at =>
        arms.groupBy(_.codes(at)).values.forall(group => group.forall(_.term == group.head.term))
      )
      val split    = determining.getOrElse(live.head)
      val rest     = live.filterNot(_ == split)
      val groups   = arms.zipWithIndex.groupBy(_._1.codes(split)).values.toVector.sortBy(_.head._2).map(_.map(_._1))
      val branches = groups.map { group =>
        (group.head.codes(split), if (determining.isDefined) group.head.term else apply(group, rest))
      }
      val codesOf = branches.groupMap(_._2)(_._1)
      val cases   = branches.map(_._2).distinct.map(value =>
        (either(Vec.from(codesOf(value).map(test(split, _)))), value)
      )
      cases.init.foldRight(cases.last._2)((entry, tail) => Term.Select(entry._1, entry._2, tail))
    }

    /** The condition that holds for the descriptors with the codes `members` and for none with those of `others`,
      * testing the fewest fields among `fields` that tell them apart.
      */
    def condition(members: Vec[Vec[Long]], others: Vec[Vec[Long]], fields: Vec[Int]): Term = {
      val subset = (1 to fields.size).view.flatMap(fields.combinations(_)).find { subset =>
        val projected = members.map(code => subset.map(code(_))).toSet
        !others.exists(code => projected(subset.map(code(_))))
      }.getOrElse(fields)
      val projected = members.map(code => subset.map(code(_))).distinct
      val codes     = subset.indices.map(position => projected.map(_(position)).distinct)
      if (codes.map(_.size).product == projected.size)
        both(Vec.from(subset.zip(codes).map((at, values) => either(values.map(test(at, _))))))
      else either(projected.map(code => both(subset.zip(code).map((at, value) => test(at, value)))))
    }
  }

  /** The element of D of a product whose instruction descriptor is `descriptor`: the descriptor fields that pick the
    * formats and negation, bound first, then for each class of products the kind's descriptors give, its element math
    * with the operand formats that differ chosen by those fields.
    */
  def dispatch(
      names: Names,
      tensors: (contextType: String, context: String),
      product: Term,
      enable: Term,
      descriptor: Term,
      multiply: Tcgen05.Multiply,
      index: Term
  ): (prelude: Term.Steps, classes: Vec[Dispatched]) = {
    val fields   = TensorMemory.Fields.formats(multiply)
    val variants = TensorMemory.variants(multiply)
    val active   = Vec.from(fields.indices).filter(at => variants.map(_.codes(at)).distinct.size > 1)
    val prelude  = Vec.newBuilder[Term.Step]
    def bound(field: TensorMemory.Field): Term = {
      val name = names.temporary()
      names.keep(name)
      prelude += Term.Step(name, extract(descriptor, field))
      Term.Variable(name)
    }
    val choice = Choice(active.map(at => (at, bound(fields(at)))).toMap)
    val negate = Option.when(variants.exists(!_.product.multiply.a.element.integer))(
      (a = bound(TensorMemory.Fields.NegateA), b = bound(TensorMemory.Fields.NegateB))
    )

    val rendered = variants.map { variant =>
      val (steps, value) = element(names, tensors, product, enable, variant.product, index, negate)
      (variant, steps, value)
    }
    def kind(entry: (TensorMemory.Variant, Term.Steps, Term)): Kind = {
      val multiply = entry._1.product.multiply
      Kind(
        entry._2.all.map(_.name),
        multiply.d.element,
        multiply.c.element,
        multiply.accumulation,
        multiply.scaling.map(_.vector.raw),
        multiply.saturate,
        multiply.sparsity.isDefined,
        multiply.b.rows,
        multiply.a.element.bits.raw,
        Option.when(multiply.a.element.integer)((multiply.a.element, multiply.b.element))
      )
    }
    val codes   = variants.map(_.codes)
    val classes = rendered.map(kind(_)).distinct.map { key =>
      val members = rendered.filter(kind(_) == key)
      val merged  = members.head._2.all.indices.map { at =>
        val arms = members.map(entry => Arm(entry._1.codes, entry._2.all(at).value))
        Term.Step(members.head._2.all(at).name, choice(arms, active))
      }
      (
        members.map(_._1.codes),
        Term.Steps(Vec.from(merged)),
        choice(members.map(entry => Arm(entry._1.codes, entry._3)), active)
      )
    }
    (
      Term.Steps(prelude.result()),
      classes.map { (members, steps, value) =>
        val taken     = members.toSet
        val condition = Option.when(classes.size > 1)(choice.condition(members, codes.filterNot(taken), active))
        Dispatched(condition, steps, value)
      }
    )
  }
}
