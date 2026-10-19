package net.kurobako.gemstone.isa.check

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** Sources for an ISA's cases: objects holding the definitions, renamed so each is unique, and suites of type `suite`
  * that check each against execution, in chunks so no source grows too large to compile quickly.
  */
object Emit {
  private val Chunk = 200

  /** Beyond this many parameters a definition takes its inputs as one vector, as the JVM caps a method's parameters. */
  private val Positional = 120

  /** The nesting beyond which a split definition computes a subterm in a method of its own, so that a matrix product's
    * chain of fused multiply-adds over K spreads over methods whose frames the compiler computes cheaply.
    */
  private val Depth = 12

  /** The prefix of the names of the subterms [[shallow]] binds, which no operand's name takes. */
  private val Hoisted = "lifted"

  /** `term` with each call, operator or machine operation nested deeper than [[Depth]] bound by `bind` first. */
  private def shallow(term: Term, bind: Term => Term): Term = {
    // a bound subterm is computed eagerly, so a branch or a let body stays where it is
    def go(term: Term): (term: Term, height: Int) = term match {
      case Term.Select(_, _, _) | Term.Let(_, _, _) => (term, 1)
      case _                                        =>
        val done    = term.children.map(go(_))
        val rest    = mutable.Queue.from(done.map(_.term))
        val rebuilt = term.mapChildren(_ => rest.dequeue())
        val height  = done.map(_.height).maxOption.getOrElse(0) + 1
        if (height > Depth) (bind(rebuilt), 1) else (rebuilt, height)
    }
    go(term).term
  }

  /** A definition without effects with each subterm nested deeper than [[Depth]] computed by a local method, which the
    * compiler lifts into a method of its own taking the values it reads, so that no one method's frame grows with the
    * whole computation.
    */
  private def split(definition: Definition): String = {
    val taken =
      (definition.parameters.map(_.name) ++ definition.body.collect {
        case Definition.Statement(Definition.Target.Binding(name), _) => name
      }).toSet
    val names = Iterator.from(0).map(index => s"$Hoisted$index").filterNot(taken)
    // each subterm bound ahead of the statement it came from
    val statements             = Vec.newBuilder[Definition.Statement]
    def bind(term: Term): Term = {
      val name = names.next()
      statements += Definition.Statement(Definition.Target.Method(name), term)
      Term.Variable(name)
    }
    definition.body.foreach {
      case Definition.Statement(Definition.Target.Binding(name), term) => statements +=
          Definition.Statement(Definition.Target.Binding(name), shallow(term, bind(_)))
      case effect => statements += effect
    }
    val results = definition.results.map((result, tpe, value) => (result, tpe, shallow(value, bind(_))))
    ScalaBackend.show(definition.copy(body = statements.result(), results = results))
  }

  private def splittable(definition: Definition, above: Int): Boolean =
    above != Int.MaxValue && definition.parameters.size <= Positional && ScalaBackend.show(definition).length > above &&
      definition.body.forall {
        case Definition.Statement(Definition.Target.Effect, _) => false
        case statement                                         => !statement.term.hasEffect
      }

  private def signature(definition: Definition): ScalaBackend.Signature =
    if (definition.parameters.size <= Positional) ScalaBackend.Signature.Positional else ScalaBackend.Signature.Vector

  private def shown(definition: Definition, above: Int): String =
    if (splittable(definition, above)) split(definition) else ScalaBackend.print(definition, signature(definition))

  private def quoted(text: String): String =
    text.flatMap {
      case '"'          => "\\\""
      case '\\'         => "\\\\"
      case '\n'         => "\\n"
      case c if c < ' ' => f"\\u${c.toInt}%04x"
      case c            => c.toString
    }.mkString("\"", "", "\"")

  private def tpe(value: Type): String = value match {
    case Type.Integer(width, signed)  => s"Type.Integer($width, signed = $signed)"
    case Type.Float(width)            => s"Type.Float($width)"
    case Type.Encoded(format)         => s"Type.Encoded(net.kurobako.gemstone.binary.Minifloat.$format)"
    case Type.Bool                    => "Type.Bool"
    case Type.Ranged(base, intervals) =>
      s"Type.Ranged(${tpe(base)}, Vec(${intervals.map((low, high) => s"(${low}L, ${high}L)").mkString(", ")}))"
  }

  /** A case's share of a chunk: its operand tables grow a suite's constructor, which the JVM caps at 64 KiB, and a
    * split definition's methods the compiler's heap.
    */
  private def weight(value: Case, above: Int): Int = {
    val operands = value.definition.parameters.size + value.definition.results.size
    val tables   = math.max(1, (operands + 5) / 6)
    if (splittable(value.definition, above)) math.max(tables, ScalaBackend.show(value.definition).length / SplitShare)
    else tables
  }

  /** The characters of a split definition that weigh as one case, so a chunk holds some 400,000 of them. */
  private val SplitShare = 2000

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def chunks(cases: Vec[Case], above: Int): Vec[Vec[Case]] = {
    val finished = Vec.newBuilder[Vec[Case]]
    val current  = mutable.ArrayBuffer.empty[Case]
    var total    = 0
    cases.foreach { value =>
      val share = weight(value, above)
      if (current.nonEmpty && total + share > Chunk) {
        finished += current.toVec
        current.clear()
        total = 0
      }
      current += value
      total += share
    }
    if (current.nonEmpty) finished += current.toVec
    finished.result()
  }

  /** The sources checking `cases`; each definition without effects longer than `above` characters is split by
    * [[split]].
    */
  def sources(prefix: String, suite: String, cases: Vec[Case], above: Int = Int.MaxValue)
      : Vec[(name: String, source: String)] =
    chunks(cases, above).zipWithIndex.flatMap { (chunk, index) =>
      val holder  = s"$prefix$index"
      val renamed = chunk.zipWithIndex.map((value, offset) =>
        value.copy(definition = value.definition.copy(name = s"d$offset"))
      )
      val refined = Option.when(renamed.exists(_.definition.parameters.exists(parameter =>
        parameter.tpe != parameter.tpe.unranged
      )))("net.kurobako.gemstone.constraint.unsafe")
      val imports =
        (renamed.flatMap(value => ScalaBackend.imports(value.definition, signature(value.definition))) ++
          refined).distinct.map("import " + _).mkString("\n")
      val definitions =
        renamed.map(value => shown(value.definition, above).linesIterator.map("  " + _).mkString("\n")).mkString("\n\n")
      val checks = renamed.map { value =>
        val definition = value.definition
        val arguments  =
          if (definition.parameters.size > Positional) Vec("inputs")
          else definition.parameters.zipWithIndex.map((parameter, position) =>
            s"${ScalaPrinter.identifier(parameter.name)} = ${ScalaBackend.fromRaw(parameter.tpe, s"inputs($position)")}"
          )
        // a tuple of more than 22 elements has no named accessors
        val results = definition.results.indices.map(position =>
          if (definition.results.size > 22) s"toRaw(result.toTuple($position))"
          else s"toRaw(result.toTuple._${position + 1})"
        )
        val call   = s"$holder.${definition.name}(${arguments.mkString(", ")})"
        val lambda =
          s"(${if (arguments.isEmpty) "_" else "inputs"}, ${if (definition.contexts.isEmpty) "_" else "harness"}) =>" +
            (if (definition.contexts.isEmpty) "" else "\n    import harness.given")
        s"""  check(
           |    ${quoted(value.instance)},
           |    ${value.target.fold("None")(target => s"Some(${quoted(target)})")},
           |    Vec(${definition.parameters.map((name, value) => s"(${quoted(name)}, ${tpe(value)})").mkString(", ")}),
           |    Vec(${definition.results.map((name, value, _) => s"(${quoted(name)}, ${tpe(value)})").mkString(", ")}),
           |    Lookup.${value.lookup.constructor}(${quoted(value.lookup.encoded)})
           |  ) { $lambda
           |    ${if (results.isEmpty) call else s"val result = $call"}
           |    Vec[Long](${results.mkString(", ")})
           |  }""".stripMargin
      }.mkString("\n\n")
      Vec(
        (
          holder,
          s"""package net.kurobako.gemstone.isa.check.generated
             |
             |import net.kurobako.gemstone.collection.*
             |import net.kurobako.gemstone.isa.check.*
             |import net.kurobako.gemstone.semantics.render.*
             |$imports
             |import scala.language.implicitConversions
             |
             |object $holder {
             |$definitions
             |}
             |
             |class ${holder}Suite extends $suite {
             |$checks
             |}
             |""".stripMargin
        )
      )
    }.toVec
}
