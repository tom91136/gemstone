package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.render.*

/** Renders [[Semantics]] for the Instructions and Program pages.
  *
  * Lane and machine definitions both show their executable source, and a machine block is labelled as such. A machine
  * block has a sandbox action when a recipe exists. No VM work happens while the page renders: the button's handler is
  * the only path to [[SandboxRunner]].
  */
object MachineSemanticsPanel {

  def render(
      semantics: Semantics,
      language: Language,
      target: () => Option[String],
      onProgramPage: Option[() => Unit] = None,
      laneLimit: Option[Int] = None
  ): HtmlElement =
    div(
      cls := "semantics-body",
      SemanticsParts.parts(semantics).map {
        case SemanticsParts.Part.Lane(descriptions) =>
          lane(laneLimit.fold(descriptions)(descriptions.take))
        case SemanticsParts.Part.Machine(descriptions) =>
          machine(descriptions, language, target, onProgramPage)
        case SemanticsParts.Part.UnavailableReason(reason) =>
          unavailable(reason)
        case SemanticsParts.Part.NoOp(reason) =>
          noEffect(reason)
        case SemanticsParts.Part.LoadTime(reason) =>
          loadTime(reason)
      }
    )

  private def lane(descriptions: Vec[Described]): HtmlElement =
    div(
      cls := "lane-semantics",
      descriptions.map(item => div(pre(cls := "instance", item.instance), pre(ScalaBackend.source(item.definition))))
    )

  private def machine(
      descriptions: Vec[Described],
      language: Language,
      target: () => Option[String],
      onProgramPage: Option[() => Unit]
  ): HtmlElement = {
    val result = Var(Option.empty[Either[SandboxFailure, SandboxRun]])
    val recipe = descriptions.headOption.flatMap(item => SandboxRecipes.find(language, item.definition.name))
    div(
      cls := "machine-panel",
      descriptions.map(item =>
        div(
          span(cls := "badge kind", "machine"),
          pre(cls  := "instance", item.instance),
          pre(ScalaBackend.source(item.definition))
        )
      ),
      recipe.map(value => sandbox(value, language, target, result, onProgramPage))
    )
  }

  private def unavailable(reason: Unavailable): HtmlElement =
    p(cls := "pending", reason.summary, reason.detail.map(detail => span(cls := "detail", s" $detail")))

  private def noEffect(reason: String): HtmlElement =
    p(cls := "pending", s"No effect in emulation: $reason")

  private def loadTime(reason: String): HtmlElement =
    p(cls := "pending", s"Read when the module loads: $reason")

  private def sandbox(
      recipe: SandboxRecipe,
      language: Language,
      target: () => Option[String],
      result: Var[Option[Either[SandboxFailure, SandboxRun]]],
      onProgramPage: Option[() => Unit]
  ): HtmlElement =
    div(
      cls := "sandbox",
      div(
        cls := "sandbox-head",
        button(
          cls := "primary",
          "Run in sandbox",
          onClick --> { _ =>
            result.set(Some(SandboxRunner.run(recipe.program(language, target()))))
          }
        ),
        span(cls := "hint", recipe.summary)
      ),
      child <-- result.signal.map {
        case None              => emptyNode
        case Some(Left(value)) => failure(value, onProgramPage)
        case Some(Right(run))  => evidence(SandboxEvidence.report(run))
      }
    )

  private def failure(value: SandboxFailure, onProgramPage: Option[() => Unit]): HtmlElement =
    div(
      cls := "sandbox-failure",
      h6(s"Sandbox failed: ${SandboxEvidence.failureKind(value)}"),
      p(code(value.detail)),
      p(
        cls := "hint",
        s"${value.programPage} runs the language's sample on the full VM."
      ),
      onProgramPage.map(open => button("Open Program page", onClick --> (_ => open())))
    )

  private def evidence(report: SandboxEvidence.Report): HtmlElement =
    div(
      cls := "sandbox-run",
      p(
        cls := "sandbox-status",
        s"${report.kernel}: ${report.executed.size} issues" +
          (if (report.complete) "" else ", truncated")
      ),
      report.fault.map(fault => p(cls := "sandbox-fault", s"Stopped: $fault")),
      h6("PC and instruction evidence"),
      if (report.executed.isEmpty) p(cls := "empty", "No issues were recorded.")
      else
        div(
          cls := "pcs",
          report.executed.map(item =>
            div(
              cls := "pc",
              code(SandboxEvidence.address(report.language, item.pc)),
              span(cls := "pc-text", item.text),
              item.label.map(label => span(cls := "pc-meta", s"label $label")),
              item.predecessor.filter(_ >= 0).map(predecessor => span(cls := "pc-meta", s"from $predecessor"))
            )
          )
        ),
      registers(report.registers),
      memory(report.memory)
    )

  private def registers(found: Vec[RegisterValue]): HtmlElement =
    if (found.isEmpty) div(cls := "sandbox-registers", h6("Registers"), p(cls := "empty", "No registers recorded."))
    else
      div(
        cls := "sandbox-registers",
        h6(s"Registers (${found.size})"),
        table(
          thead(tr(th("Lane"), th("Register"), th("Value"))),
          tbody(
            found.map(value =>
              tr(
                td(if (value.lane < 0) "wave" else value.lane.toString),
                td(code(value.name)),
                td(code(SandboxEvidence.bits(value.bits)))
              )
            )
          )
        )
      )

  private def memory(found: Vec[SandboxEvidence.Memory]): HtmlElement =
    div(
      cls := "sandbox-memory",
      h6("Memory evidence"),
      if (found.isEmpty) p(cls := "empty", "The program has no allocation arguments.")
      else
        found.map(value =>
          div(
            cls := "allocation",
            h6(s"Allocation ${value.index} (${value.total} bytes)"),
            p(cls := "bytes", code(SandboxEvidence.hex(value.head).mkString(" "))),
            if (value.changed.isEmpty) p(cls := "hint", "No bytes changed from the initial allocation.")
            else
              p(
                cls := "changes",
                value.changed.zipWithIndex.map((change, index) =>
                  span(
                    if (index == 0) "" else ", ",
                    code(f"0x${change.offset}%04x: ${change.before}%02x -> ${change.after}%02x")
                  )
                ),
                Option.when(value.hidden > 0)(span(s", ${value.hidden} more changed bytes"))
              )
          )
        )
    )
}
