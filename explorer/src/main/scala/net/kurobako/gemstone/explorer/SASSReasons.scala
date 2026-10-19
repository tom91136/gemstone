package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.catalogue.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.semantics.render.*

/** Why SASS forms outside compute execution have no definition, from the catalogue's structure: the mnemonic, the form
  * the disassembler tables name and the kinds of its operand fields.
  */
private object SASSReasons {

  /** Ray tracing's tree traversal unit, which the recovered tables name only by internal opcode. */
  private val traversal = Set("ttucctl_", "ttuclose_", "ttugo_", "ttuld_", "ttumacrofuse_", "ttuopen_", "ttust_")

  /** Trap-handler, preemption and debugger state: interrupt enables, the active-mask save area, returns from traps and
    * the barrier and CTA-id restores of a resumed CTA.
    */
  private val privileged = Set("IDE", "RAM", "SAM", "RTT", "SHINT", "R2B", "SETCTAID")

  /** Per-launch memory configuration: the local-memory window and the shared-memory carve-out. */
  private val configuration = Set("GETLMEMBASE", "SETLMEMBASE", "USETSHMSZ")

  private val privilegedForms = Set("ide_", "rtt_", "r2b_", "ucgabar_get_", "ucgabar_set_", "nanotrap_")

  /** Forms the recovered tables list that no public PTX instruction lowers to, such as HMMA addressing its accumulator
    * through a uniform register as `RF[URn]`.
    */
  private val unexposed = Set("genmetadata_", "gather_bitextract_", "hmma_x8_indexedRF_", "hmma_sparse_indexedRF_")

  /** Why `form` has no definition, where `failures` are why its instances lower to none, or why none can be written. */
  def unavailable(
      entry: View.Entry,
      form: View.Form,
      database: Option[NVDisasm.Database],
      failures: Either[String, Vec[Unrendered]]
  ): Unavailable =
    outside(entry, form).orElse(NvidiaReasons.excluded(entry.mnemonic, form.name)).getOrElse(
      failures.fold(
        {
          case deprecated if deprecated.contains(" is deprecated") =>
            Unavailable.NotModelled("The encoding tables deprecate this form, so no toolchain emits it.")
          case failure => impossible(form.name, database, failure).getOrElse(Unavailable.NotInstantiated(failure))
        },
        NvidiaReasons.of(entry.mnemonic, _)
      ) match {
        case reason @ (Unavailable.NotImplemented(_) | Unavailable.NotInstantiated(_)) =>
          classified(entry, form.name, database).getOrElse(reason)
        case reason => reason
      }
    )

  private def outside(entry: View.Entry, form: View.Form): Option[Unavailable] = {
    val fields = form.syntax.collect { case View.Token.Slot(name, _) => name.takeWhile(_ != ':').takeWhile(_ != '(') }
    def prefixed(set: Set[String]) = set.exists(prefix => form.name.startsWith(prefix))
    NvidiaReasons.outside(entry.mnemonic).orElse(structural(entry, form, fields, prefixed(_)))
  }

  private def structural(entry: View.Entry, form: View.Form, fields: Vec[String], prefixed: Set[String] => Boolean)
      : Option[Unavailable] =
    if (fields.contains("CX") || entry.mnemonic == "UCLEA")
      Some(Unavailable.NotModelled(
        "It reads or addresses a constant bank selected by a uniform register, a bindless constant buffer of the " +
          "graphics pipeline that compute toolchains do not emit."
      ))
    else if (prefixed(traversal))
      Some(Unavailable.NotModelled("It drives the RT cores' tree traversal unit, used by ray tracing."))
    else if (
      fields.contains("V") || form.name.startsWith("mov_indexedRF") || form.name == "VOTE.VTG" ||
      entry.mnemonic == "CSMTEST"
    )
      Some(Unavailable.NotModelled(
        "It belongs to the graphics pipeline's vertex, pixel or coverage state."
      ))
    else if (privileged(entry.mnemonic) || prefixed(privilegedForms))
      Some(Unavailable.NotModelled(
        "It reads or sets trap-handler, preemption or debugger state that kernels compiled for compute do not use."
      ))
    else if (configuration(entry.mnemonic))
      Some(Unavailable.NotModelled(
        "It reads or moves the local-memory window or resizes the shared-memory carve-out, which the VM fixes for " +
          "each launch."
      ))
    else if (prefixed(unexposed) || Set("GATHER", "SCATTER", "SPMETADATA", "VSEL2", "VSEL4")(entry.mnemonic))
      Some(Unavailable.NotModelled("No public PTX instruction lowers to it, so no toolchain emits it."))
    else if (entry.mnemonic == "DCHK")
      Some(Unavailable.NotModelled(
        "ptxas checks double-precision division and square roots with MUFU.RCP64H, MUFU.RSQ64H and DFMA instead " +
          "(CUDA 11.8, sm_35 to sm_70), so no toolchain emits it."
      ))
    else if (entry.mnemonic == "IMADSP")
      Some(Unavailable.NotModelled(
        "Kepler's IMADSP takes its operand formats from a register at run time; ptxas lowers mul24 and mad24 to IMAD " +
          "and IMUL instead, so no toolchain emits it."
      ))
    else None

  /** Why no instance of a form is legal, where the condition its instances fail reads only what no instance can vary:
    * required modifiers whose every combination was tried, qualifiers the form fixes and operands it fixes to RZ.
    */
  private def impossible(formName: String, database: Option[NVDisasm.Database], failure: String)
      : Option[Unavailable] = for {
    tables <- database
    form   <- tables.byForm.get(formName)
    fields   = NVDisasm.Syntax.fields(form.syntax)
    choices  = SASSModifiers.of(tables, fields)
    required = choices.filter(_.required)
    if required.map(_.values.size.toLong).product * form.opcodes.size <= SASSModifiers.Attempts
    failed = form.conditions.filter(condition => failure.endsWith(condition.message))
    if failed.nonEmpty
    varied = choices.filterNot(_.required).map(_.field.binding).toSet
    if failed.forall(condition =>
      references(condition.expression).forall(name =>
        fields.find(_.binding == name).exists(field =>
          field.category == NVDisasm.FieldKind.ZeroRegister ||
            field.category == NVDisasm.FieldKind.ZeroUniformRegister ||
            (!field.category.isOperand && !varied(name))
        )
      )
    )
  } yield Unavailable.NotModelled(
    s"The encoding tables reject every instance the form can take: ${failed.head.message.stripSuffix("!").stripSuffix(".")}."
  )

  private def references(expression: ConditionExpression): Set[String] = {
    import ConditionExpression.*
    expression match {
      case Field(name, _)                  => Set(name)
      case Unary(_, value)                 => references(value)
      case Binary(_, left, right)          => references(left) ++ references(right)
      case Call(_, arguments)              => arguments.toSet.flatMap(references(_))
      case Conditional(condition, yes, no) => references(condition) ++ references(yes) ++ references(no)
      case _                               => Set.empty
    }
  }

  /** Warp state only trap handlers and debuggers move: masks of active, killed and exited lanes, the trap return state,
    * the return PC and the call-return stack pointer.
    */
  private val TrapState = Set("CBU_STATE_NONBAR", "ATEXIT_PCONLY", "RPCONLY", "PC_REG", "BarmdBAR", "BarmdWARP")

  private val TrapMnemonics = Set("SETCRSPTR", "GETCRSPTR", "NANOTRAP")

  /** Instructions whose final operand is a code address. */
  private val CodeTargets =
    Set("BRA", "BRX", "JMP", "JMX", "CAL", "JCAL", "CALL", "PRET", "SSY", "PBK", "PCNT", "PLONGJMP")

  /** Why the VM leaves out a form none of whose instances it lowers, from the form's encoding class, its operand kinds
    * or its instruction.
    */
  private def classified(
      entry: View.Entry,
      formName: String,
      database: Option[NVDisasm.Database]
  ): Option[Unavailable] =
    database.flatMap(_.byForm.get(formName)).flatMap { form =>
      val fields = NVDisasm.Syntax.fields(form.syntax, includeMarkedOptionals = true)
      if (form.alternate)
        Some(Unavailable.NotModelled(
          "It is an alternate assembler spelling, such as a raw .REL offset, of an encoding the disassembler prints " +
            "in another form, which is classified there."
        ))
      else if (
        TrapMnemonics(entry.mnemonic) || fields.exists(field =>
          TrapState(field.kind) ||
            field.kind.startsWith("CBU_STATE") && !database.exists(_.vocabulary(field).exists(_.name == "B1"))
        )
      )
        Some(Unavailable.NotModelled(
          "It moves warp state that trap handlers and debuggers use, such as the active, killed and exited lane " +
            "masks, the trap return PC or the call-return stack pointer; compilers do not emit it for kernels."
        ))
      else if (CodeTargets(entry.mnemonic) && fields.exists(_.category == NVDisasm.FieldKind.ConstantBank))
        Some(Unavailable.NotModelled(
          "It reads its code address from constant memory, which compilers do not emit; they load function " +
            "addresses with LDC and branch or call through a register."
        ))
      else
        entry.mnemonic match {
          case "BPT" =>
            Some(Unavailable.NotModelled(
              "Debugger, pause and drain breakpoints; __trap and __brkpt compile to BPT.TRAP, which the VM runs."
            ))
          case "KILL" =>
            Some(Unavailable.NotModelled(
              "It discards a pixel shader's fragment; compute kernels end threads with EXIT."
            ))
          case "LONGJMP" | "PLONGJMP" | "PEXIT" =>
            Some(Unavailable.NotModelled(
              "It saves or restores the hardware control stack for exception-style unwinding, which no CUDA " +
                "compiler emits."
            ))
          case _ => None
        }
    }
}
