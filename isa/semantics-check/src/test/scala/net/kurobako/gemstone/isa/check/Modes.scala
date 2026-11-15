package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.toVec
import net.kurobako.gemstone.isa.amdgpu.ModeRegister
import net.kurobako.gemstone.semantics.render.*

/** The floating-point modes an instruction has, and whether its rendered definition shows each of them. A proof may
  * only rest on an instruction whose modes the definition applies: a mode the renderer dropped is not assumed, and two
  * sequences are never judged equal under one.
  */
object Modes {

  enum Mode(val label: String) derives CanEqual {
    case Rounding extends Mode("rounding")
    case Flush    extends Mode("flush")
    case Saturate extends Mode("saturate")
  }

  /** Why `kase` cannot stand in a proof, or nothing. */
  def check(kase: Case): Either[String, Unit] = {
    val missing = requiredModes(kase).filterNot(renderedModes(kase))
    if (missing.isEmpty) Right(()) else Left(s"${kase.instance}: ${missingLabels(missing)} not rendered")
  }

  private def missingLabels(missing: Set[Mode]): String = missing.toVec.sortBy(_.ordinal) match {
    case Vec(single) => s"${single.label} is"
    case several     => s"${several.map(_.label).mkString(" and ")} are"
  }

  private def requiredModes(kase: Case): Set[Mode] =
    if (kase.target.exists(_.startsWith("gfx"))) amdModes(kase.definition)
    else modifiers(kase.instance)

  private val FlushNames    = Set("FTZ")
  private val SaturateNames = Set("SAT")
  private val RoundingNames = Set("RM", "RP", "RZ")

  /** The modes a SASS instance names as modifiers, such as `FADD.FTZ`. */
  private def modifiers(instance: String): Set[Mode] = {
    val head = instance.split(' ').toVec.find(token => !token.startsWith("@")).getOrElse(instance)
    val name = head.takeWhile(_ != '.')
    head.drop(name.length + 1).split('.').toVec.filter(_.nonEmpty).flatMap {
      case modifier if FlushNames(modifier)    => Some(Mode.Flush)
      case modifier if SaturateNames(modifier) => Some(Mode.Saturate)
      case modifier if RoundingNames(modifier) => Some(Mode.Rounding)
      case _                                   => None
    }.toSet
  }

  private val AmdArithmetic = Set("add", "sub", "mul", "fma", "fmac", "mad", "div")

  private val FloatOwners = Set("Floats", "FloatArithmetic")

  private val ToNearest = Set("RN", "RNI")

  /** An AMDGPU instruction whose definition reads MODE depends on its rounding and denormal fields. */
  private def amdModes(definition: Definition): Set[Mode] =
    if (definition.parameters.exists(_.name == "mode") && arithmetic(definition))
      Set(Mode.Rounding, Mode.Flush)
    else Set.empty

  private def arithmetic(definition: Definition): Boolean =
    terms(definition).exists(term =>
      contains(term) {
        case Term.Primitive(owner = owner, method = method) =>
          FloatOwners(Term.simple(owner)) && AmdArithmetic(method)
        case _ => false
      }
    )

  private def renderedModes(kase: Case): Set[Mode] =
    if (kase.target.exists(_.startsWith("gfx"))) {
      val known = knownMasks(kase.definition)
      Set(
        Option.when(arithmeticPrimitives(kase.definition).forall(roundedByMode(_, known)))(Mode.Rounding),
        Option.when((reachedModes(kase.definition, known) & ModeRegister.DenormalMask) != 0)(Mode.Flush)
      ).flatten
    } else {
      val all = terms(kase.definition)
      Set(
        Option.when(all.exists(term => contains(term)(flushes)))(Mode.Flush),
        Option.when(all.exists(term => contains(term)(saturates)))(Mode.Saturate),
        Option.when(all.exists(term => contains(term)(rounds)))(Mode.Rounding)
      ).flatten
    }

  private def terms(definition: Definition): Vec[Term] =
    definition.requirements ++ definition.body.map(_.term) ++ definition.results.map(_.value)

  private def contains(term: Term)(found: Term => Boolean): Boolean =
    found(term) || term.children.exists(contains(_)(found))

  /** A subnormal flushed to zero of its sign: a selection that keeps only the sign bit of its value. */
  private def flushes(term: Term): Boolean = term match {
    case Term.Select(_, Term.Binary("&", _, Term.Literal(mask)), _) => signMask(mask)
    case Term.Select(_, _, Term.Binary("&", _, Term.Literal(mask))) => signMask(mask)
    case _                                                          => false
  }

  private def signMask(mask: Long): Boolean =
    mask == 0x8000L || mask == 0x80000000L || mask == 0x8000000000000000L

  private def saturates(term: Term): Boolean = term match {
    case Term.Primitive(method = "saturate") => true
    case Term.Call("saturate", _)            => true
    case _                                   => false
  }

  /** A rounding the definition applies other than to nearest, even. */
  private def rounds(term: Term): Boolean = term match {
    case Term.Primitive(method = method, arguments = arguments) if method == "fusedMultiplyAdd" =>
      arguments.exists {
        case Term.Constant(code) => !ToNearest(code.substring(code.lastIndexOf('.') + 1))
        case _                   => false
      }
    case _ => false
  }

  private def arithmeticPrimitives(definition: Definition): Vec[Term.Primitive] =
    terms(definition).flatMap(primitives).filter(primitive =>
      FloatOwners(Term.simple(primitive.owner)) && AmdArithmetic(primitive.method)
    )

  private def primitives(term: Term): Vec[Term.Primitive] = term match {
    case primitive: Term.Primitive => primitive +: primitive.children.flatMap(primitives)
    case _                         => term.children.flatMap(primitives)
  }

  /** Whether the primitive's trailing argument carries the FP_ROUND field of its width, so MODE decides its rounding.
    */
  private def roundedByMode(primitive: Term.Primitive, known: Map[String, Long]): Boolean = {
    val field = if (primitive.result == Type.Float(32)) ModeRegister.Round32Mask else ModeRegister.Round16And64Mask
    primitive.arguments.lastOption.exists(argument => (reached(argument, known) & field) == field)
  }

  /** The bits of the MODE register each name of the definition may depend on. */
  private def knownMasks(definition: Definition): Map[String, Long] =
    definition.body.foldLeft[Map[String, Long]](Map("mode" -> -1L)) { (masks, statement) =>
      val value = reached(statement.term, masks)
      statement.target match {
        case Definition.Target.Binding(name) => masks.updated(name, value)
        case Definition.Target.Method(name)  => masks.updated(name, value)
        case Definition.Target.Effect        => masks
      }
    }

  /** The bits of the MODE register the definition's results may read. */
  private def reachedModes(definition: Definition, known: Map[String, Long]): Long = {
    val outputs = definition.results.map(_.value) ++ definition.body.collect {
      case Definition.Statement(Definition.Target.Effect, term) => term
    }
    outputs.foldLeft(0L)((mask, term) => mask | reached(term, known))
  }

  private def reached(term: Term, known: Map[String, Long]): Long = term match {
    case Term.Variable(name)                                   => known.getOrElse(name, 0L)
    case Term.Literal(_) | Term.Constant(_)                    => 0L
    case Term.Binary("&", left, Term.Literal(value))           => reached(left, known) & value
    case Term.Binary("|" | "^", left, right)                   => reached(left, known) | reached(right, known)
    case Term.Binary("<<", left, Term.Literal(value))          => reached(left, known) << value
    case Term.Binary(">>u" | ">>s", left, Term.Literal(value)) => reached(left, known) >>> value
    case _ => term.children.foldLeft(0L)((mask, child) => mask | reached(child, known))
  }
}
