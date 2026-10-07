package net.kurobako.gemstone.codestyle

import scala.meta.*
import scalafix.v1.*

/** PTX and SASS runtime sources must not reference their `asm` packages, so the runtimes load without them. */
class AsmBoundary extends SemanticRule("AsmBoundary") {
  private val roots   = Vector("net/kurobako/gemstone/isa/ptx/", "net/kurobako/gemstone/isa/sass/")
  private val runtime = "(^|/)isa/(ptx|sass)/src/".r
  private val exempt  = "/asm/|/src/(generated/)?test/".r

  private def isAsm(symbol: String): Boolean = roots.exists(root => symbol.startsWith(root + "asm/"))

  private def path(input: Input): String = input match {
    case file: Input.File        => file.path.toString
    case file: Input.VirtualFile => file.path
    case _                       => ""
  }

  override def fix(using document: SemanticDocument): Patch = {
    val source = path(document.input)
    if (runtime.findFirstIn(source).isEmpty || exempt.findFirstIn(source).nonEmpty) Patch.empty
    else document.tree.collect { case name: Name if isAsm(name.symbol.value) => name }
      .groupBy(_.pos.startLine).values.map(_.head)
      .map(name => lint("asm-reference", "runtime code must not reference the asm package", name.pos)).asPatch
  }
}
