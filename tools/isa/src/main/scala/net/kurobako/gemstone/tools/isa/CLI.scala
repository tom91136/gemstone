package net.kurobako.gemstone.tools.isa

import cats.syntax.all.*
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.*
import scala.util.control.NonFatal

object CLI {
  enum Request {
    case Assemble(format: Assembly.Format, input: Path, output: Path)
    case Disassemble(format: Assembly.Format, input: Path, output: Option[Path], section: Option[String])
  }

  private val format = (
    Opts.option[String]("isa", help = "spirv, amdgpu, or ptx (textual; no cubin/SASS)"),
    Opts.option[String]("target", help = "AMDGPU processor, e.g. gfx1030").orNone,
    Opts.option[Int]("wave-size", help = "AMDGPU wave size: 32 (default) or 64").orNone
  ).tupled.mapValidated { (isa, target, waveSize) =>
    if (waveSize.exists(value => value != 32 && value != 64)) "--wave-size must be 32 or 64".invalidNel
    else if (waveSize.nonEmpty && isa != "amdgpu") "--wave-size is only used with --isa amdgpu".invalidNel
    else (isa, target) match {
      case ("spirv", None)         => Assembly.Format.SPIRV.validNel
      case ("ptx", None)           => Assembly.Format.PTX.validNel
      case ("amdgpu", Some(value)) =>
        Target.either(value).leftMap(_.toString).map(target =>
          Assembly.Format.AMDGPU(target, waveSize.getOrElse(32).unsafe[Int in (32, 64)])
        ).toValidatedNel
      case ("amdgpu", None)           => "AMDGPU requires --target".invalidNel
      case ("spirv" | "ptx", Some(_)) => "--target is only used with --isa amdgpu".invalidNel
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
      case (Assembly.Format.AMDGPU(_, _), _) | (_, None) =>
        Request.Disassemble(format, input, output, section).validNel
      case _ => "--section requires disassemble --isa amdgpu".invalidNel
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
            val text  = (format, section) match {
              case (Assembly.Format.AMDGPU(target, waveSize), Some(section)) =>
                Assembly.disassembleCodeObject(bytes, target, Some(section), waveSize)
              case _ => Assembly.disassemble(bytes, format)
            }
            (text.map(value => Vec.unsafeWrapArray((value + "\n").getBytes(StandardCharsets.UTF_8))), output)
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
