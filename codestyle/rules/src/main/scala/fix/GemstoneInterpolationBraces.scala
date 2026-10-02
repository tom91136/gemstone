package fix

import scala.meta.*
import scalafix.v1.*

class GemstoneInterpolationBraces extends SyntacticRule("GemstoneInterpolationBraces") {
  override def fix(using document: SyntacticDocument): Patch = document.tree.collect {
    case interpolation: Term.Interpolate if Set("s", "raw", "f")(interpolation.prefix.value) =>
      interpolation.args.zipWithIndex.collect {
        case (block @ Term.Block(List(name: Term.Name)), index)
            if block.pos.text.stripPrefix("{").stripSuffix("}").trim == name.pos.text &&
              !name.pos.text.startsWith("`") && canUnwrap(interpolation, block, name, index) =>
          Patch.lint(Diagnostic(
            "redundant-interpolation-braces",
            s"use $$${name.value} instead of bracing this interpolation",
            block.pos
          ))
      }.asPatch
  }.asPatch

  private def canUnwrap(interpolation: Term.Interpolate, block: Term.Block, name: Term.Name, index: Int): Boolean = {
    given Dialect = dialects.Scala3
    val source    = interpolation.pos.text
    val start     = block.pos.start - interpolation.pos.start
    val end       = block.pos.end - interpolation.pos.start
    val candidate = source.take(start) + name.pos.text + source.drop(end)
    // Reparse to catch identifier suffixes, Unicode and adjacent interpolation boundaries.
    candidate.parse[Term].toOption.exists {
      case parsed: Term.Interpolate =>
        parsed.prefix.structure == interpolation.prefix.structure &&
        parsed.parts.map(_.value) == interpolation.parts.map(_.value) &&
        parsed.args.map(_.structure) == interpolation.args.updated(index, name).map(_.structure)
      case _ => false
    }
  }
}
