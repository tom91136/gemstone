package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.BooleanAsAttrPresenceCodec
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.string.*
import org.scalajs.dom

/** Instruction catalogue, register files and an assembler bench for PTX, SASS, AMDGPU and SPIR-V. */
object Explorer {
  enum Page(val title: String) derives CanEqual {
    case Program      extends Page("Program")
    case Instructions extends Page("Instructions")
    case Registers    extends Page("Registers")
    case Translator   extends Page("Assembler")
  }

  // the results list renders only the rows near the viewport, so every row must have this height
  private val RowHeight = 22
  private val Overscan  = 20
  private val open      = htmlAttr("open", BooleanAsAttrPresenceCodec)

  def main(arguments: Array[String]): Unit = {
    val _ = renderOnDomContentLoaded(dom.document.getElementById("explorer"), application)
  }

  def application: HtmlElement = {
    val page    = Var(Page.Program)
    val targets = Language.values.toVec.map(language => language -> Var(language.model.defaultTarget)).toMap
    div(
      cls := "explorer",
      headerTag(
        h1("Gemstone instruction explorer"),
        navTag(Page.values.toVec.map(value =>
          button(
            cls <-- page.signal.map(current => if (current == value) "active" else ""),
            onClick.mapTo(value) --> page,
            value.title
          )
        ))
      ),
      child <-- page.signal.map {
        case Page.Program      => ProgramPage(targets)
        case Page.Instructions => instructions(targets, () => page.set(Page.Program))
        case Page.Registers    => registers(targets)
        case Page.Translator   => translator(targets)
      }
    )
  }

  private def targetSelect(model: Model, target: Var[Option[String]]): HtmlElement =
    if (model.targets.isEmpty) span(cls := "target-fixed", "all versions")
    else
      select(
        onChange.mapToValue.map(value => Option.when(value.nonEmpty)(value)) --> target,
        Option.when(model.anyTarget)(option(value := "", "all targets", selected <-- target.signal.map(_.isEmpty))),
        model.targets.map(name => option(value := name, name, selected <-- target.signal.map(_.contains(name))))
      )

  private def rank(entry: View.Entry, needle: String): Int =
    if (entry.key == needle) 0 else if (entry.key.startsWith(needle)) 1 else 2

  private def instructions(
      targets: Map[Language, Var[Option[String]]],
      onProgramPage: () => Unit
  ): HtmlElement = {
    val query    = Var("")
    val enabled  = Var(Language.values.toSet)
    val chosen   = Var(Option.empty[View.Entry])
    val selected = Signal.combineSeq(Language.values.toVec.map(language => targets(language).signal.map(language -> _)))
    val results  = query.signal.combineWith(enabled.signal, selected).map { (text, enabled, selected) =>
      val needle = text.trim.toLowerCaseRoot
      selected.filter((language, _) => enabled(language)).toVec
        .flatMap((language, target) => language.model.entries(target).filter(_.searchText.contains(needle)))
        .map(entry => ((rank(entry, needle), entry.key, entry.language.ordinal), entry)).sortBy(_._1).map(_._2)
    }
    val viewport = Var((0.0, 0.0))
    val shown    = results.combineWith(viewport.signal).map { (found, top, visible) =>
      val first = math.min(found.size, math.max(0, (top / RowHeight).toInt - Overscan))
      val last  = math.min(found.size, ((top + visible) / RowHeight).toInt + 1 + Overscan)
      (total = found.size, first = first, rows = found.slice(first, last))
    }
    div(
      cls := "page instructions",
      asideTag(
        input(
          typ         := "search",
          placeholder := "Search mnemonics and forms",
          controlled(value <-- query.signal, onInput.mapToValue --> query)
        ),
        Language.values.toVec.map(language =>
          div(
            cls := "filter",
            label(
              input(
                typ := "checkbox",
                checked <-- enabled.signal.map(_(language)),
                onInput.mapToChecked --> (on => enabled.update(set => if (on) set + language else set - language))
              ),
              language.title
            ),
            targetSelect(language.model, targets(language))
          )
        ),
        p(cls := "count", child.text <-- results.map(found => s"${found.size} instructions")),
        ul(
          cls := "results",
          inContext { list =>
            def measure(): Unit = viewport.set((list.ref.scrollTop, list.ref.clientHeight.toDouble))
            Seq(
              onMountCallback(_ => measure()),
              onScroll --> (_ => measure()),
              windowEvents(_.onResize) --> (_ => measure())
            )
          },
          child <-- shown.map(window => li(cls := "spacer", height := s"${window.first * RowHeight}px")),
          children <-- shown.map(_.rows).split(identity)((entry, _, _) =>
            li(
              cls <-- chosen.signal.map(current => if (current.contains(entry)) "chosen" else ""),
              onClick.mapTo(Some(entry)) --> chosen,
              span(cls := "badge", entry.language.title),
              entry.mnemonic
            )
          ),
          child <-- shown.map(window =>
            li(cls := "spacer", height := s"${(window.total - window.first - window.rows.size) * RowHeight}px")
          )
        )
      ),
      mainTag(child <--
        chosen.signal.map(_.fold(div(cls := "empty", "Select an instruction."))(entry(_, targets, onProgramPage))))
    )
  }

  private def entry(
      value: View.Entry,
      targets: Map[Language, Var[Option[String]]],
      onProgramPage: () => Unit
  ): HtmlElement = div(
    cls := "entry",
    h2(span(cls := "badge", value.language.title), value.mnemonic),
    p(s"${value.formNames.size} ${if (value.formNames.size == 1) "form" else "forms"}"),
    value.forms.zipWithIndex.map((form, index) => this.form(value, form, index < 3, targets, onProgramPage))
  )

  private def syntax(tokens: Vec[View.Token]): HtmlElement = pre(
    cls := "syntax",
    tokens.map {
      case View.Token.Text(value)        => span(value)
      case View.Token.Slot(name, detail) => span(cls := "slot", title := detail, name)
    }
  )

  private def form(
      entry: View.Entry,
      value: View.Form,
      expanded: Boolean,
      targets: Map[Language, Var[Option[String]]],
      onProgramPage: () => Unit
  ): HtmlElement = {
    val opened = Var(expanded)
    detailsTag(
      inContext(details =>
        onToggle.map(_ => details.ref.hasAttribute("open")) --> opened.updater[Boolean]((was, now) => was || now)
      ),
      cls  := "form",
      open := expanded,
      summaryTag(value.name),
      syntax(value.syntax),
      Option.when(value.properties.nonEmpty)(
        table(cls := "properties", value.properties.map((name, detail) => tr(th(name), td(detail))))
      ),
      Option.when(value.operands.nonEmpty)(
        table(
          cls := "operands",
          thead(tr(th("#"), th("Operand"), th("Kind"), th("Detail"))),
          tbody(value.operands.zipWithIndex.map((operand, index) =>
            tr(
              td(index.toString),
              td(operand.name),
              td(operand.kind),
              td(operand.detail, operand.domain.map(domain => DomainView.operand(domain)))
            )
          ))
        )
      ),
      Option.when(value.choices.nonEmpty)(
        div(
          cls := "choices",
          h4("Modifiers and enumerants"),
          dl(value.choices.flatMap(choice => Vec(dt(choice.name), dd(choice.values.mkString(" | ")))))
        )
      ),
      value.encoding.map(encoding =>
        div(cls := "encoding", h4(s"Encoding (${encoding.width} bits)"), BitDiagram(encoding))
      ),
      Option.when(value.constraints.nonEmpty)(
        detailsTag(
          cls := "constraints",
          summaryTag(s"${value.constraints.size} encoding constraints"),
          ul(value.constraints.map(li(_)))
        )
      ),
      div(
        cls := "semantics",
        h4("Semantics"),
        child <-- opened.signal.distinct.map(if (_)
          MachineSemanticsPanel.render(
            entry.language.model.semanticsOf(entry, value),
            entry.language,
            () => targets(entry.language).now(),
            Some(onProgramPage)
          )
        else emptyNode)
      )
    )
  }

  private def registers(targets: Map[Language, Var[Option[String]]]): HtmlElement = {
    val language = Var(Language.SASS)
    div(
      cls := "page registers",
      div(
        cls := "toolbar",
        Language.values.toVec.map(value =>
          button(
            cls <-- language.signal.map(current => if (current == value) "active" else ""),
            onClick.mapTo(value) --> language,
            value.title
          )
        ),
        child <-- language.signal.map(value => targetSelect(value.model, targets(value)))
      ),
      child <-- language.signal.flatMapSwitch(value => targets(value).signal.map(value -> _)).map {
        (value, target) =>
          div(value.model.registers(target).map(file =>
            sectionTag(
              cls := "register-file",
              h3(s"${file.name}${if (file.registers.isEmpty) "" else s" (${file.registers.size})"}"),
              p(file.description),
              div(
                cls := "registers",
                file.registers.map(register =>
                  span(
                    cls   := "register",
                    title := Vec(
                      register.encoding.map(code => s"encoding $code"),
                      Option.when(register.note.nonEmpty)(register.note)
                    ).flatten.mkString("; "),
                    register.name,
                    Option.when(register.note.nonEmpty)(span(cls := "note", register.note))
                  )
                )
              )
            )
          ))
      }
    )
  }

  private def translator(targets: Map[Language, Var[Option[String]]]): HtmlElement = {
    val language         = Var(Language.SASS)
    val source           = Var(SASSModel.binaryExample)
    val output           = Var[Either[String, String]](Right(""))
    def assemble(): Unit = {
      val current = language.now()
      output.set(current.model.assemble(source.now(), targets(current).now()))
    }
    def disassemble(): Unit = {
      val current = language.now()
      val target  = targets(current).now()
      output.set(Hex.parse(source.now()).flatMap(current.model.disassemble(_, target)))
    }
    div(
      cls := "page translator",
      div(
        cls := "toolbar",
        Language.values.toVec.map(value =>
          button(
            cls <-- language.signal.map(current => if (current == value) "active" else ""),
            onClick --> { _ =>
              language.set(value)
              source.set(if (value.model.binary) value.model.binaryExample else value.model.assemblyExample)
              output.set(Right(""))
            },
            value.title
          )
        ),
        child <-- language.signal.map(value => targetSelect(value.model, targets(value)))
      ),
      div(
        cls := "panes",
        div(
          cls := "pane",
          textArea(spellCheck := false, controlled(value <-- source.signal, onInput.mapToValue --> source)),
          div(
            cls := "actions",
            child <-- language.signal.map(value =>
              if (value.model.binary)
                span(
                  button("Disassemble", onClick --> (_ => disassemble())),
                  button("Assemble", onClick --> (_ => assemble())),
                  span(cls := "hint", "Bytes are hex pairs in memory order or 0x-prefixed little-endian words.")
                )
              else span(button("Parse and validate", onClick --> (_ => assemble())))
            )
          )
        ),
        div(
          cls := "pane",
          pre(
            cls <-- output.signal.map(value => if (value.isLeft) "output error" else "output"),
            child.text <-- output.signal.map(_.merge)
          )
        )
      )
    )
  }
}
