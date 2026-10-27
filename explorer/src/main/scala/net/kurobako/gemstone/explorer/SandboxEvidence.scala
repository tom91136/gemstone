package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*

/** The before/after evidence from a machine sandbox run, or the typed reason it could not run. The values are rendered
  * by the UI as PC/instruction, register and memory evidence; tests read them without a browser DOM.
  */
object SandboxEvidence {

  /** How many bytes of an allocation the panel shows, and how many changed bytes it lists. */
  val MemoryBytes: Int = 64
  val ChangeLimit: Int = 16

  case class Executed(
      pc: Long,
      text: String,
      label: Option[Int] = None,
      predecessor: Option[Int] = None
  ) derives CanEqual
  case class ChangedByte(offset: Int, before: Int, after: Int) derives CanEqual
  case class Memory(index: Int, total: Int, head: Vec[Byte], changed: Vec[ChangedByte], hidden: Int)
      derives CanEqual

  case class Report(
      language: Language,
      kernel: String,
      complete: Boolean,
      fault: Option[String],
      executed: Vec[Executed],
      registers: Vec[RegisterValue],
      memory: Vec[Memory]
  )

  def report(run: SandboxRun): Report =
    Report(
      language = run.program.language,
      kernel = run.kernel.raw,
      complete = run.complete,
      fault = run.failure.map(_.description.raw),
      executed = run.pcs.zipWithIndex.map { (pc, index) =>
        val step = run.spirvSteps.lift(index)
        Executed(
          pc,
          run.lines.find(_.address == pc).fold("<no listing at this address>")(_.text),
          step.map(_.label),
          step.map(_.predecessor)
        )
      },
      registers = run.registers,
      memory = run.allocations.zipWithIndex.map((_, index) =>
        memory(index, run.program.allocations.lift(index).getOrElse(Vec.empty), run.bytes(index))
      )
    )

  /** The typed case name the panel shows, independent of the detail. */
  def failureKind(failure: SandboxFailure): String = failure match {
    case SandboxFailure.Unavailable(_, _) => "Unavailable"
    case SandboxFailure.Assembly(_, _)    => "Assembly"
    case SandboxFailure.Patch(_, _)       => "Patch"
    case SandboxFailure.Load(_, _)        => "Load"
    case SandboxFailure.Setup(_, _)       => "Setup"
    case SandboxFailure.Launch(_, _)      => "Launch"
  }

  /** An ISA's instruction address, as the Program page writes it. */
  def address(language: Language, value: Long): String = language match {
    case Language.PTX | Language.SPIRV   => value.toString
    case Language.SASS | Language.AMDGPU => f"0x$value%04x"
  }

  /** A register value in the width the ISA would print. */
  def bits(value: Long): String =
    if (value >>> 32 == 0L) f"0x$value%08x" else f"0x$value%016x"

  /** Bytes as two-digit hexadecimal words. */
  def hex(bytes: Vec[Byte]): Vec[String] = bytes.map(byte => f"${byte & 0xff}%02x")

  private def memory(index: Int, before: Vec[Byte], after: Vec[Byte]): Memory = {
    val initial = before ++ Vec.fill(math.max(0, after.size - before.size))(0.toByte)
    val common  = math.min(initial.size, after.size)
    val changed = Vec.from((0 until common).flatMap { offset =>
      val old = initial(offset) & 0xff
      val now = after(offset) & 0xff
      Option.when(old != now)(ChangedByte(offset, old, now))
    })
    Memory(
      index = index,
      total = after.size,
      head = after.take(MemoryBytes),
      changed = changed.take(ChangeLimit),
      hidden = math.max(0, changed.size - ChangeLimit)
    )
  }
}
