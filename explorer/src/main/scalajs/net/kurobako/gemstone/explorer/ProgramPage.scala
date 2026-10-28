package net.kurobako.gemstone.explorer

import com.raquo.laminar.api.L.*
import com.raquo.laminar.codecs.StringAsIsCodec
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Loads a compiled GPU program, runs it on the real VM, and shows what it did: the instructions it ran, the semantics
  * of the one a step selects, and the occupancy of the launch over its issue stream.
  */
object ProgramPage {

  private val minimum = htmlAttr("min", StringAsIsCodec)
  private val maximum = htmlAttr("max", StringAsIsCodec)

  def apply(targets: Map[Language, Var[Option[String]]]): HtmlElement = {
    val language   = Var(Language.PTX)
    val source     = Var(Examples.mandelbrot)
    val kernel     = Var("mandelbrot")
    val launchText = Var("<<<[4,1,1],[8,8,1]>>>")
    val status     = Var[Either[String, String]](Right(""))
    val loaded     = Var(Option.empty[ProgramRun.Loaded])
    val trace      = Var(Trace.empty)
    val rows       = Var(Vec.empty[Row])
    val liveness   = Var(Liveness.empty)
    val selected   = Var(Option.empty[Long])

    def target: Option[String] = targets(language.now()).now()

    def load(): Unit = {
      loaded.now().foreach(_.close())
      loaded.set(None)
      trace.set(Trace.empty)
      rows.set(Vec.empty)
      liveness.set(Liveness.empty)
      selected.set(None)
      image(language.now(), source.now(), target)
        .flatMap(bytes => ProgramRun.load(ProgramListing.Image(language.now(), bytes, target))) match {
        case Left(error)    => status.set(Left(error))
        case Right(program) =>
          loaded.set(Some(program))
          // The dropdown follows the architecture the image names, so the listing decodes it as it was compiled.
          program.image.target.foreach(value => targets(program.image.language).set(Some(value)))
          kernel.set(program.kernels.headOption.fold("")(_.raw))
          status.set(
            program.listing match {
              case Left(error)    => Left(s"loaded ${program.kernels.size} kernels, but no listing: $error")
              case Right(listing) =>
                Right(s"loaded ${program.kernels.size} kernels and listed ${listing.lines.size} instructions")
            }
          )
      }
    }

    def run(): Unit = {
      val request = for {
        program <- loaded.now().toRight("load a program first")
        name    <- kernel.now().refine[NonEmpty[String]].left.map(_ => "name a kernel")
        config  <- launch(launchText.now())
      } yield (program, name, config)
      request.flatMap((program, name, config) =>
        ProgramRun.run(program, name, config.lanes, config.groups)
      ) match {
        case Left(error)   => status.set(Left(error))
        case Right(result) =>
          trace.set(result.trace)
          rows.set(Timeline.layout(result.trace))
          liveness.set(Liveness.of(result.trace))
          selected.set(result.trace.steps.headOption.map(_.issue))
          val stopped   = result.failure.fold("")(fault => s", stopped: ${fault.description.raw}")
          val truncated = if (result.complete) "" else s", recorded the first ${result.trace.steps.size} issues"
          status.set(Right(s"${result.trace.steps.size} issues over ${rows.now().size} workgroups$stopped$truncated"))
      }
    }

    def seek(issue: Long): Unit = {
      val last = math.max(0L, trace.now().issues - 1L)
      selected.set(Some(math.max(0L, math.min(issue, last))))
    }

    def move(by: Long): Unit = seek(selected.now().getOrElse(0L) + by)

    div(
      cls := "page program",
      div(
        cls := "toolbar",
        Language.values.toVec.map(value =>
          button(
            cls <-- language.signal.map(current => if (current == value) "active" else ""),
            onClick --> { _ =>
              loaded.now().foreach(_.close())
              loaded.set(None)
              trace.set(Trace.empty)
              rows.set(Vec.empty)
              liveness.set(Liveness.empty)
              selected.set(None)
              language.set(value)
              source.set(if (value == Language.PTX) Examples.ptx else example(value))
              status.set(Right(""))
            },
            value.title
          )
        ),
        child <-- language.signal.map(value => targetSelect(value, targets(value))),
        label(
          cls := "field",
          "kernel",
          input(
            cls := "kernel",
            controlled(value <-- kernel.signal, onInput.mapToValue --> kernel)
          )
        ),
        label(
          cls := "field",
          "launch",
          input(
            cls         := "launch",
            placeholder := "<<<[4,1,1],[8,8,1]>>>",
            title       := "grid dimensions, then lanes per workgroup; the second bracket may be omitted",
            controlled(value <-- launchText.signal, onInput.mapToValue --> launchText)
          )
        ),
        button("Load", onClick --> (_ => load())),
        button(cls := "primary", "Run", onClick --> (_ => run()))
      ),
      div(
        cls := "status-line",
        cls <-- status.signal.map(_.fold(_ => "error", _ => "ok")),
        child.text <-- status.signal.map(_.merge)
      ),
      div(
        cls := "program-panes",
        div(
          cls := "pane source",
          textArea(
            spellCheck := false,
            controlled(value <-- source.signal, onInput.mapToValue --> source)
          )
        ),
        div(
          cls := "pane listing",
          child <-- loaded.signal.combineWith(selected.signal, trace.signal).map {
            case (Some(program), choice, recorded) =>
              val current = choice.flatMap(recorded.stepAt)
              div(
                cls := "lines",
                program.lines.map { line =>
                  val here = current.exists(step =>
                    step.location.instruction.map(_.raw).contains(line.address) &&
                      (step.location.function.isEmpty || step.location.function == line.function)
                  )
                  div(
                    cls := (if (here) "line current" else "line"),
                    span(cls := "address", SandboxEvidence.address(program.image.language, line.address)),
                    span(cls := "text", line.text)
                  )
                }
              )
            case (None, _, _) => div(cls := "empty", "Load a program to list its instructions.")
          }
        )
      ),
      div(
        cls := "timeline-pane",
        child <-- liveness.signal.combineWith(trace.signal).map { (live, recorded) =>
          if (recorded.issues == 0L) emptyNode else LivenessView.render(recorded, live)(seek(_))
        },
        child <-- rows.signal.combineWith(trace.signal).map { (bands, recorded) =>
          if (bands.isEmpty) div(cls := "empty", "Run a program to draw its occupancy over time.")
          else TimelineView.render(recorded, bands)(seek(_))
        },
        legendBar
      ),
      child <-- selected.signal.combineWith(trace.signal, loaded.signal, liveness.signal).map {
        case (Some(issue), recorded, program, live) =>
          val step = recorded.stepAt(issue)
          div(
            cls := "step-view",
            div(
              cls := "step-controls",
              button("\u25c0", onClick --> (_ => move(-1L))),
              input(
                typ     := "range",
                minimum := "0",
                maximum := math.max(0L, recorded.issues - 1L).toString,
                controlled(
                  value <-- selected.signal.map(_.getOrElse(0L).toString),
                  onInput.mapToValue --> ((text: String) => text.toLongOption.foreach(seek(_)))
                )
              ),
              button("\u25b6", onClick --> (_ => move(1L))),
              span(cls := "step-count", s"issue $issue of ${recorded.issues}")
            ),
            step.fold(div(cls := "empty", "No issue recorded."))(value => detail(value, program, live, recorded))
          )
        case (None, _, _, _) => emptyNode
      }
    )
  }

  private def example(language: Language): String = language match {
    case Language.PTX    => Examples.mandelbrot
    case Language.SASS   => SassExample.cubin
    case Language.AMDGPU => AmdgpuExample.codeObject
    case Language.SPIRV  => SpirvExample.module
  }

  private def targetSelect(language: Language, target: Var[Option[String]]): HtmlElement =
    if (language.model.targets.isEmpty) span(cls := "target-fixed", "all versions")
    else
      select(
        onChange.mapToValue.map(value => Option.when(value.nonEmpty)(value)) --> target,
        Option.when(language.model.anyTarget)(
          option(value := "", "all targets", selected <-- target.signal.map(_.isEmpty))
        ),
        language.model.targets.map(name =>
          option(value := name, name, selected <-- target.signal.map(_.contains(name)))
        )
      )

  /** The bytes of `source`: text for PTX, an assembled module for SPIR-V assembly, hex for the binary ISAs. */
  private def image(language: Language, source: String, target: Option[String]): Either[String, Vec[Byte]] =
    language match {
      case Language.PTX   => Right(Vec.from(source.getBytes(StandardCharsets.UTF_8)))
      case Language.SPIRV =>
        if (source.linesIterator.exists(_.trim.startsWith("Op"))) SPIRVModel.assemble(source, target).flatMap(Hex.parse)
        else Hex.parse(source)
      case Language.SASS | Language.AMDGPU => Hex.parse(source)
    }

  private def dimensions(text: String): Either[String, Dimensions] = text.split(',').map(_.trim).toVec match {
    case Vec(x, y, z) =>
      for {
        first  <- positive(x)
        second <- positive(y)
        third  <- positive(z)
      } yield Dimensions(first, second, third)
    case _ => Left(s"expected three comma-separated positive dimensions, got '$text'")
  }

  /** A CUDA-style launch: the grid of workgroups first, optionally followed by the lanes of each workgroup. */
  private def launch(text: String): Either[String, Launch] = text.trim match {
    case LaunchSyntax(groups, lanes) =>
      for {
        count <- dimensions(groups)
        size  <- dimensions(lanes)
      } yield Launch(count, size)
    case GridSyntax(groups) =>
      dimensions(groups).map(count => Launch(count, Dimensions(1, 1, 1)))
    case _ => Left("expected <<<[gridX,gridY,gridZ]>>> or <<<[gridX,gridY,gridZ],[lanesX,lanesY,lanesZ]>>>")
  }

  private val LaunchSyntax = """<<<\s*\[([^\]]*)]\s*,\s*\[([^\]]*)]\s*>>>""".r
  private val GridSyntax   = """<<<\s*\[([^\]]*)]\s*>>>""".r

  private case class Launch(groups: Dimensions, lanes: Dimensions)

  private def positive(text: String): Either[String, Int > 0] =
    text.toIntOption.toRight(s"'$text' is not a number").flatMap(_.refine[Int > 0].left.map(_.raw))

  /** What a step ran, where it was, what it read and wrote, and what the instruction it ran means. */
  private def detail(
      step: TraceStep,
      program: Option[ProgramRun.Loaded],
      live: Liveness,
      recorded: Trace
  ): HtmlElement = {
    val line      = program.flatMap(_.listing.toOption).flatMap(_.find(step.location))
    val liveNames = live.liveAt(recorded, step.issue.toInt)
    div(
      cls := "step-detail",
      div(
        cls := "step-head",
        span(cls := "badge", step.location.kernel.raw),
        code(line.fold("<no listing for this instruction>")(_.text))
      ),
      table(
        cls := "step-facts",
        tbody(
          tr(th("workgroup"), td(coordinates(step.location.workgroup))),
          tr(th("unit"), td(s"${step.unit.ordinal.raw} of ${step.unit.lanes.raw} lanes")),
          tr(th("lane"), td(step.location.lane.fold("all lanes")(_.raw.toString))),
          tr(th("function"), td(step.location.function.fold("-")(_.raw))),
          tr(
            th("where"),
            td(step.location.instruction.fold("-")(value =>
              SandboxEvidence.address(programLanguage(program), value.raw)
            ))
          ),
          tr(
            th("live"),
            td(if (live.reported) s"${liveNames.size} registers per lane" else "not reported by this ISA's view")
          ),
          tr(th("reads"), td(registerNames(step.use.reads))),
          tr(th("writes"), td(registerNames(step.use.writes)))
        )
      ),
      liveRegisters(live, liveNames),
      values(step.values),
      line.fold(div(cls := "empty", "The program's listing has no instruction here."))(value =>
        definition(programLanguage(program), boundary(program), value.mnemonic)
      )
    )
  }

  private def registerNames(found: Vec[String]): String =
    if (found.isEmpty) "-" else found.mkString(", ")

  /** The registers the unit holds live just before the step, which is the pressure the instruction adds to. */
  private def liveRegisters(live: Liveness, names: Vec[String]): HtmlElement =
    if (!live.reported) div(cls := "empty", "This ISA's view reports no register uses.")
    else if (names.isEmpty) div(cls := "live-registers", h4("Live registers"), p(cls := "empty", "None."))
    else
      div(
        cls := "live-registers",
        h4(s"Live registers (${names.size})"),
        div(cls := "names", names.map(name => span(cls := "register", name)))
      )

  /** The machine's registers at the step, one column per lane of the unit. */
  private def values(found: Vec[RegisterValue]): HtmlElement =
    if (found.isEmpty) div(cls := "empty", "This ISA's view names no registers to show.")
    else {
      val names = found.map(_.name).distinct
      val lanes = found.map(_.lane).distinct
      val cells = found.reverse.map(value => (value.name, value.lane) -> value.bits).toMap
      div(
        cls := "values",
        h4("Values"),
        table(
          thead(tr(th(""), lanes.map(lane => th(if (lane < 0) "wave" else s"lane $lane")))),
          tbody(names.map { name =>
            tr(
              th(name),
              lanes.map { lane =>
                td(cells.get((name, lane)).fold("-")(bits(_)))
              }
            )
          })
        )
      )
    }

  private def bits(value: Long): String =
    if (value >>> 32 == 0L) f"0x$value%08x" else f"0x$value%016x"

  private def coordinates(value: Option[Coordinates]): String =
    value.fold("-")(point => s"(${point.x.raw}, ${point.y.raw}, ${point.z.raw})")

  private def programLanguage(program: Option[ProgramRun.Loaded]): Language =
    program.fold(Language.PTX)(_.image.language)

  private def boundary(program: Option[ProgramRun.Loaded]): Option[String] = program.flatMap(_.image.target)

  /** The catalogue's semantics for `mnemonic`, as the instructions page renders them. */
  private def definition(language: Language, target: Option[String], mnemonic: String): HtmlElement = {
    val entries = language.model.entries(target)
    entries.find(_.key == mnemonic.toLowerCaseRoot) match {
      case None => div(cls := "semantics", h4("Semantics"), p(cls := "pending", s"$mnemonic is not in the catalogue."))
      case Some(entry) =>
        entry.forms.headOption match {
          case None =>
            div(cls := "semantics", h4("Semantics"), p(cls := "pending", "The catalogue has no form for this."))
          case Some(form) =>
            div(
              cls := "semantics",
              h4(s"Semantics of ${entry.mnemonic}"),
              MachineSemanticsPanel.render(
                language.model.semanticsOf(entry, form),
                language,
                () => target,
                laneLimit = Some(1)
              )
            )
        }
    }
  }

  private val legendBar: HtmlElement = div(
    cls := "legend-items",
    span(cls := "legend-item"),
    span(cls := "segment running", "running"),
    span(cls := "segment blocked barrier", "barrier"),
    span(cls := "segment blocked memory", "memory"),
    span(cls := "segment blocked collective", "collective"),
    span(cls := "segment blocked members", "lanes"),
    span(cls := "segment retired", "finished")
  )
}
