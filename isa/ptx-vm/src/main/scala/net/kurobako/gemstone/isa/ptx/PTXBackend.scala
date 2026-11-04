package net.kurobako.gemstone.isa.ptx

import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.ptx.Instruction as PTXInstruction
import net.kurobako.gemstone.vm.*

object PTXBackend {
  val format: ProgramFormat = ProgramFormat.PTXText
}

class PTXBackend(
    workgroups: Workgroups = Workgroups.Sequential,
    decode: Vec[Byte] => Either[ISA.DecodeError, Program] = ISA.decode(_),
    retainReleased: Boolean = true,
    text: PTXInstruction => Option[String]
) extends Backend {
  override val format: ProgramFormat = PTXBackend.format

  private def admit(program: Program): Either[Fault, Program] = {
    def reject(issues: NonEmpty[Vec[Validation.Issue]]) =
      Left(Fault.InvalidRequest(issues.map(_.message.raw).distinct.mkString("; ").unsafe))
    // ptxas accepts forms the specification omits, so only availability is enforced at load
    Validation.validate(program, forms = false) match {
      case ValidationOutcome.Accepted           => Right(program)
      case ValidationOutcome.Rejected(issues)   => reject(issues)
      case ValidationOutcome.Incomplete(issues) => reject(issues)
    }
  }

  // Each case of a conformance run loads its module afresh and linking dominates the load, so the immutable product is
  // shared between loads and released images stay retained while memory allows; runtime state remains per load.
  private val images = ImageCache[Fault, LoadedProgram.Prepared](retainReleased)

  override def load(image: Vec[Byte]): Either[Fault, Backend.Program] =
    images.acquire(ImageKey(image)) {
      decode(image).left.map(error => Fault.InvalidRequest(error.message)).flatMap(admit(_))
        .flatMap(LoadedProgram.prepare(_, text))
    }.map(lease => LoadedProgram.from(lease.value, workgroups, () => lease.release()))
}
