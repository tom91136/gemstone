package net.kurobako.gemstone.explorer

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.sass.*
import net.kurobako.gemstone.isa.sass.asm.*
import scala.collection.mutable

/** SASS instruction addresses are byte offsets within the kernel's code section, so a listing decodes the cubin and
  * reports each instruction at its own address.
  */
private object SASSListing {
  def lines(image: Vec[Byte]): Either[String, Vec[ProgramLine]] =
    Cubin.decode(image).left.map(error => s"invalid cubin: $error").flatMap { module =>
      Target.of(module.sm).flatMap { target =>
        val lines  = Vec.newBuilder[ProgramLine]
        val listed = module.kernels.foldLeft[Either[String, Unit]](Right(())) { (result, kernel) =>
          result.flatMap(_ => append(lines, module, kernel, target))
        }
        listed.map(_ => lines.result())
      }
    }

  /** Appends the instructions of `kernel`, or why its code section or one of its instructions cannot be read. */
  private def append(
      lines: mutable.Builder[ProgramLine, Vec[ProgramLine]],
      module: Cubin.Module,
      kernel: Cubin.Kernel,
      target: Target
  ): Either[String, Unit] = {
    val name = kernel.name.raw
    CodeSection.decode(Vec.toArray(module.sections(kernel.section).bytes), target)
      .left.map(error => s"$name sm_${module.sm}: $error")
      .flatMap(_.instructions.foldLeft[Either[String, Unit]](Right(())) { (result, instruction) =>
        result.flatMap { _ =>
          Printer.print(instruction.operation)
            .left.map(error => s"$name sm_${module.sm} at 0x${instruction.address.raw.toHexString}: $error")
            .map { text =>
              val _ = lines += ProgramLine(
                Some(name),
                instruction.address.raw.toLong,
                text,
                instruction.mnemonic.raw.takeWhile(_ != '.')
              )
            }
        }
      })
  }
}
