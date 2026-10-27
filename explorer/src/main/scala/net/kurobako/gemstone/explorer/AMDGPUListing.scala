package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.isa.amdgpu.asm.*
import net.kurobako.gemstone.isa.amdgpu.asm.Program.*

/** AMDGPU instruction addresses are byte offsets within the code object's text section, which is the offset the VM's
  * faults report. The shared text holds every kernel's code, so a listing walks it from its start and stops at the
  * first byte that is not an instruction.
  */
private object AMDGPUListing {

  def lines(image: Vec[Byte], target: Option[String]): Either[String, Vec[ProgramLine]] = for {
    name     <- target.orElse(AMDGPUModel.defaultTarget).toRight("no AMDGPU target selected")
    resolved <- name.refine[Target].left.map(_.raw)
    object_  <- CodeObject.decode(image).left.map(_.message.raw)
    sections <- object_.executableSections.left.map(_.message.raw)
    text     <- sections.find(_.name == ".text").orElse(sections.headOption).toRight("the code object has no text")
  } yield walk(text.bytes, TargetMode(resolved, TargetDefaults.waveSize(resolved)))

  /** Lists the text section's instructions, each at its own offset, stopping where the bytes stop decoding. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def walk(text: Vec[Byte], mode: TargetMode): Vec[ProgramLine] = {
    val bytes  = Vec.toArray(text)
    val lines  = Vec.newBuilder[ProgramLine]
    var offset = 0
    var going  = true
    while (going && offset <= bytes.length - 4)
      Binary.decodeStatementAt(bytes, offset.unsafe[Int >= 0], mode) match {
        case Right(decoded) if offset + decoded.size <= bytes.length =>
          statement(decoded.statement).foreach((text, mnemonic) =>
            lines += ProgramLine(None, offset.toLong, text, mnemonic)
          )
          offset += decoded.size
        case _ => going = false
      }
    lines.result()
  }

  /** How a statement is written and the catalogue key its instruction has, when it is one. */
  private def statement(value: Statement): Option[(text: String, mnemonic: String)] = value match {
    case Statement.Instruction(instruction)     => Some((Printer.render(instruction), instruction.opcode.raw))
    case Statement.InstructionPair(left, right) =>
      Some((s"${Printer.render(left)} :: ${Printer.render(right)}", left.opcode.raw))
    case _ => None
  }
}
