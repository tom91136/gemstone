package net.kurobako.gemstone.semantics.render

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import scala.collection.mutable

/** Definitions as Scala functions from the operands and state they read to the named tuple of those they write, over
  * [[prelude]].
  */
object ScalaBackend extends Backend {
  import Definition.*

  private val Prelude = "net.kurobako.gemstone.semantics.prelude."

  /** Imported by name, as the whole of it would make the prelude's functions ambiguous. */
  private val Constraint = "net.kurobako.gemstone.constraint."

  private val Unused = "scala.annotation.unused"

  /** How a definition takes its parameters: one by one, or all as the inputs of a vector, for a count the JVM's limit
    * on a method's parameters would not allow.
    */
  enum Signature derives CanEqual {
    case Positional
    case Vector
  }

  /** The operands and bindings, and the contexts, that the terms of `definition` read. */
  private def reads(definition: Definition): (names: Set[String], contexts: Set[String]) = {
    val terms = (definition.requirements ++ definition.body.map(_.term) ++ definition.results.map(_.value)).map(
      _.expand
    )
    (terms.flatMap(_.variables).toSet, terms.flatMap(_.contexts.map(_._1)).toSet)
  }

  /** Whether `definition` marks a parameter it never reads, which a name must keep, as unused. */
  private def annotates(definition: Definition, signature: Signature): Boolean = {
    val read = reads(definition)
    (signature == Signature.Positional && definition.parameters.exists(parameter => !read.names(parameter.name))) ||
    definition.contexts.exists((context, _) => !read.contexts(context))
  }

  /** What `definition`, taking its parameters as `signature` says, refers to, fully qualified. */
  def imports(definition: Definition, signature: Signature = Signature.Positional): Vec[String] = {
    val found                   = mutable.LinkedHashSet(s"$Prelude*")
    def visit(term: Term): Unit = term match {
      case Term.Primitive(owner = owner, arguments = arguments) =>
        if (!owner.startsWith(Prelude)) found += owner
        arguments.foreach(visit(_))
      case Term.Context(arguments = arguments) => arguments.foreach(visit(_))
      case Term.Each(_, source, body)          =>
        visit(source)
        body.foreach(step => visit(step.value))
      case Term.When(condition, body) =>
        visit(condition)
        body.foreach(step => visit(step.value))
      case Term.Constant(code)         => found ++= ScalaPrinter.owners(code)
      case Term.Unary(_, value)        => visit(value)
      case Term.Binary(_, left, right) =>
        visit(left)
        visit(right)
      case Term.Call(_, arguments)                     => arguments.foreach(visit(_))
      case Term.Select(condition, whenTrue, whenFalse) =>
        visit(condition)
        visit(whenTrue)
        visit(whenFalse)
      case _ => ()
    }
    if (
      definition.parameters.exists(parameter => parameter.tpe != parameter.tpe.unranged) ||
      definition.results.exists(result => result.tpe != result.tpe.unranged)
    )
      found ++= Vec(s"${Constraint}in", s"$Constraint~")
    definition.contexts.foreach((_, tpe) => found += tpe)
    if (annotates(definition, signature)) found += Unused
    definition.requirements.foreach(visit(_))
    definition.body.foreach(statement => visit(statement.term))
    definition.results.foreach(result => visit(result.value))
    found.toVec
  }

  def show(definition: Definition): String = print(definition, Signature.Positional)

  /** `raw`, the `Long` execution holds, as a value of `tpe`, which `.unsafe` checks against its range if it has one.
    */
  def fromRaw(tpe: Type, raw: String): String = {
    val value = s"fromRaw[${ScalaPrinter.show(tpe.unranged)}]($raw)"
    if (tpe == tpe.unranged) value else s"$value.unsafe[${ScalaPrinter.show(tpe)}]"
  }

  /** `definition` alone, taking its parameters as `signature` says. */
  def print(definition: Definition, signature: Signature): String = {
    import definition.*
    val read                                          = reads(definition)
    def declared(name: String, used: Boolean): String = (if (used) "" else "@unused ") + ScalaPrinter.identifier(name)
    val using                                         =
      contexts.map((context, tpe) => s"${declared(context, read.contexts(context))}: ${Term.simple(tpe)}")
    val result =
      if (results.isEmpty) "Unit"
      else results.map((result, tpe, _) => s"${ScalaPrinter.identifier(result)}: ${ScalaPrinter.show(tpe)}").mkString(
        "(",
        ", ",
        ")"
      )
    def named(local: String): String = if (read.names(local)) ScalaPrinter.identifier(local) else "_"
    val taken                        = signature match {
      case Signature.Positional =>
        parameters.map((parameter, tpe) =>
          s"${declared(parameter, read.names(parameter))}: ${ScalaPrinter.show(tpe)}"
        ).mkString(", ")
      case Signature.Vector => "inputs: Vec[Long]"
    }
    val header =
      s"def ${ScalaPrinter.identifier(name)}($taken)${
          if (using.isEmpty) "" else using.mkString("(using ", ", ", ")")
        }: $result"
    val tuple =
      results.map((result, _, value) => s"${ScalaPrinter.identifier(result)} = ${ScalaPrinter.show(value)}") match {
        case Vec()       => "()"
        case Vec(single) => s"($single)"
        case values      => values.mkString("(\n    ", ",\n    ", "\n  )")
      }
    val unpacked = signature match {
      case Signature.Positional => Vec.empty[String]
      case Signature.Vector     =>
        parameters.zipWithIndex.map((parameter, position) =>
          s"val ${named(parameter.name)}: ${ScalaPrinter.show(parameter.tpe)} = " +
            fromRaw(parameter.tpe, s"inputs($position)")
        )
    }
    val lines = unpacked ++ requirements.map(requirement => s"require(${ScalaPrinter.show(requirement)})") ++
      body.flatMap {
        case Statement(Target.Binding(binding), value) =>
          Vec(s"val ${named(binding)} = ${ScalaPrinter.show(value)}")
        case Statement(Target.Method(binding), value) =>
          val method = s"${ScalaPrinter.identifier(binding)}Of"
          Vec(s"def $method = ${ScalaPrinter.show(value)}", s"val ${named(binding)} = $method")
        case Statement(Target.Effect, value) => Vec(ScalaPrinter.show(value).replace("\n", "\n  "))
      }
    if (lines.isEmpty) s"$header =\n  $tuple" else (lines :+ tuple).mkString(s"$header = {\n  ", "\n  ", "\n}")
  }

  def source(definition: Definition): String =
    imports(definition).map(path => s"import $path").mkString("", "\n", s"\n\n${show(definition)}")
}
