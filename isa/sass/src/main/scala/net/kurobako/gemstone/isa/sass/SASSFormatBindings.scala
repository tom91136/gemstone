package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.isa.sass.*
import scala.util.Try

/** Shared FORMAT field analysis and semantic binding refinement across decoding, assembly, and legality checks. */
object SASSFormatBindings {
  def number(spelling: String): Option[BigInt] = Try {
    val value = spelling.stripSuffix("*")
    if (value.startsWith("0x") || value.startsWith("0X")) BigInt(value.drop(2), 16)
    else if (value.startsWith("0b") || value.startsWith("0B")) BigInt(value.drop(2), 2)
    else BigInt(value)
  }.toOption

  def fields(nodes: Vec[NVDisasm.Syntax], includeMarkedOptionals: Boolean = false): Vec[NVDisasm.Syntax.Field] =
    nodes.flatMap {
      case field: NVDisasm.Syntax.Field             => Vec(field)
      case NVDisasm.Syntax.Group(_, _, children)    => fields(children, includeMarkedOptionals)
      case NVDisasm.Syntax.Optional(None, children) => fields(children, includeMarkedOptionals)
      case NVDisasm.Syntax.Optional(Some(_), children) if includeMarkedOptionals =>
        fields(children, includeMarkedOptionals)
      case _ => Vec.empty
    }

  def kindBindings(fields: Vec[NVDisasm.Syntax.Field]): Map[String, String] = fields.groupBy(_.kind).collect {
    case (kind, values) if values.map(_.binding).distinct.size == 1 => kind -> values.head.binding
  }

  def references(expression: NVDisasm.Expression, includeBaseForAttributes: Boolean): Set[String] = expression match {
    case NVDisasm.Expression.Reference(name, attribute, _) =>
      attribute.fold(Set(name)) { suffix =>
        Set(s"$name@$suffix") ++ (if (includeBaseForAttributes) Set(name) else Set.empty[String])
      }
    case NVDisasm.Expression.FixedFloat(value, _)    => references(value, includeBaseForAttributes)
    case NVDisasm.Expression.Relaxed(value)          => references(value, includeBaseForAttributes)
    case NVDisasm.Expression.Scale(value, _)         => references(value, includeBaseForAttributes)
    case NVDisasm.Expression.Multiply(value, _)      => references(value, includeBaseForAttributes)
    case NVDisasm.Expression.Identity(first, second) =>
      references(first, includeBaseForAttributes) ++ references(second, includeBaseForAttributes)
    case NVDisasm.Expression.FloatConvert(value, selector, _, _) =>
      references(value, includeBaseForAttributes) + selector
    case NVDisasm.Expression.Lookup(_, arguments) =>
      arguments.flatMap(argument => references(argument, includeBaseForAttributes)).toSet
    case _ => Set.empty
  }

  class Analysis(
      database: NVDisasm.Database,
      form: NVDisasm.Form,
      declaredFields: Option[Vec[NVDisasm.Syntax.Field]] = None,
      includeBaseForAttributes: Boolean = true
  ) {
    import NVDisasm.Syntax.*

    val declared: Vec[NVDisasm.Syntax.Field] = declaredFields.getOrElse(fields(form.syntax))

    val kindBindings: Map[String, String] = SASSFormatBindings.kindBindings(declared)

    val encodedNames: Set[String] = form.fields.flatMap(field =>
      references(field.expression, includeBaseForAttributes)
    ).toSet

    def encodedBinding(field: Field): String =
      if (encodedNames(field.binding) || !kindBindings.get(field.kind).contains(field.binding)) field.binding
      else if (encodedNames(field.kind)) field.kind
      else field.binding

    def vocabulary(field: Field): Vec[NVDisasm.SymbolValue] =
      database.byVocabulary.get(field.kind).fold(Vec.empty[NVDisasm.SymbolValue])(_.values)

    def defaultValues(field: Field): Vec[BigInt] = field.default.toVec.flatMap { spelling =>
      val aliases = vocabulary(field).filter(_.name == spelling).map(_.value)
      if (aliases.nonEmpty) aliases else number(spelling).toVec
    }.distinct

  }

}
