package net.kurobako.gemstone.tools.isa

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.isa.amdgpu.*
import net.kurobako.gemstone.string.*
import scala.util.control.NonFatal

object CLI {
  enum Request {
    case Assemble(format: Assembly.Format, input: Path, output: Path)
    case Disassemble(format: Assembly.Format, input: Path, output: Option[Path], section: Option[String])
  }

  private val format = (
    Opts.option[String]("isa", help = "spirv, amdgpu, sass, or ptx (textual)"),
    Opts.option[String]("target", help = "AMDGPU or SASS processor, e.g. gfx1030 or sm_89").orNone,
    Opts.option[Int]("wave-size", help = "AMDGPU wave size: 32 (default) or 64")
      .mapValidated(_.refine[Int in (32, 64)].leftMap(_ => "--wave-size must be 32 or 64").toValidatedNel).orNone
  ).tupled.mapValidated { (isa, target, waveSize) =>
    if (waveSize.nonEmpty && isa != "amdgpu") "--wave-size is only used with --isa amdgpu".invalidNel
    else (isa, target) match {
      case ("spirv", None)         => Assembly.Format.SPIRV.validNel
      case ("ptx", None)           => Assembly.Format.PTX.validNel
      case ("amdgpu", Some(value)) =>
        value.refine[Target].leftMap(_.toString).map(target =>
          Assembly.Format.AMDGPU(target, waveSize.getOrElse(32))
        ).toValidatedNel
      case ("sass", Some(value))      => sass.Target.parse(value).map(Assembly.Format.SASS(_)).toValidatedNel
      case ("sass", None)             => "SASS requires --target".invalidNel
      case ("amdgpu", None)           => "AMDGPU requires --target".invalidNel
      case ("spirv" | "ptx", Some(_)) => "--target is only used with --isa amdgpu or sass".invalidNel
      case _                          => s"unknown ISA '$isa'".invalidNel
    }
  }

  private val input  = Opts.argument[String]("input").map(Path.of(_))
  private val output = Opts.option[String]("output", short = "o", help = "Output file (required for assembly)")
    .map(Path.of(_))

  private val assemble: Opts[Request] = (
    format,
    input,
    output.orNone.mapValidated(_.toValidNel("assembly requires --output"))
  ).mapN(Request.Assemble(_, _, _))

  private val disassemble: Opts[Request] = (
    format,
    input,
    output.orNone,
    Opts.option[String]("section", help = "Executable ELF section to disassemble").orNone
  ).tupled.mapValidated { (format, input, output, section) =>
    (format, section) match {
      case (Assembly.Format.AMDGPU(_, _) | Assembly.Format.SASS(_), _) | (_, None) =>
        Request.Disassemble(format, input, output, section).validNel
      case _ => "--section requires disassemble --isa amdgpu or sass".invalidNel
    }
  }

  val command: Command[Request] = Command("gemstone-isa", "Assemble and disassemble GPU instructions")(
    Opts.subcommand("assemble", "Assemble text into an ISA image")(assemble).orElse(
      Opts.subcommand("disassemble", "Print an ISA image as canonical assembly")(disassemble)
    )
  )

  def main(args: Array[String]): Unit = command.parse(args.toList) match {
    case Left(help) =>
      Console.err.println(help)
      if (help.errors.nonEmpty) sys.exit(2)
    case Right(request) =>
      try {
        val (result, destination) = request match {
          case Request.Assemble(format, input, output) =>
            val bytes = Vec.unsafeWrapArray(Files.readAllBytes(input))
            (Assembly.text(bytes).flatMap(Assembly.assemble(_, format)), Some(output))
          case Request.Disassemble(format, input, output, section) =>
            val bytes = Vec.unsafeWrapArray(Files.readAllBytes(input))
            val text  = Assembly.disassemble(bytes, format, section)
            (text.flatMap(value => UTF8.encode(value + "\n").leftMap(Assembly.Error.TextEncoding(_))), output)
        }
        result match {
          case Left(error) =>
            Console.err.println(error)
            sys.exit(1)
          case Right(output) => destination match {
              case Some(path) => val _ = Files.write(path, Vec.toArray(output))
              case None       => Console.out.write(Vec.toArray(output))
            }
        }
      } catch {
        case NonFatal(error) =>
          Console.err.println(error.getMessage)
          sys.exit(1)
      }
  }
}
