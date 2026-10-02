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
  enum Action {
    case Assemble, Disassemble
  }

  case class Request(
      action: Action,
      format: Assembly.Format,
      input: Path,
      output: Option[Path],
      section: Option[String]
  )

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

  private def options(action: Action): Opts[Request] = (
    format,
    Opts.argument[String]("input").map(Path.of(_)),
    Opts.option[String]("output", short = "o", help = "Output file (required for assembly)").map(Path.of(_)).orNone,
    Opts.option[String]("section", help = "Executable ELF section to disassemble").orNone
  ).mapN(Request(action, _, _, _, _)).mapValidated { request =>
    if (action == Action.Assemble && request.output.isEmpty) "assembly requires --output".invalidNel
    else if (
      request.section.nonEmpty &&
      (action != Action.Disassemble ||
        (request.format match {
          case Assembly.Format.AMDGPU(_, _) => false
          case _                            => true
        }))
    )
      "--section requires disassemble --isa amdgpu".invalidNel
    else request.validNel
  }

  val command: Command[Request] = Command("gemstone-isa", "Assemble and disassemble GPU instructions")(
    Opts.subcommand("assemble", "Assemble text into an ISA image")(options(Action.Assemble)).orElse(
      Opts.subcommand("disassemble", "Print an ISA image as canonical assembly")(options(Action.Disassemble))
    )
  )

  def main(args: Array[String]): Unit = command.parse(args.toList) match {
    case Left(help) =>
      Console.err.println(help)
      if (help.errors.nonEmpty) sys.exit(2)
    case Right(request) =>
      try {
        val bytes  = Vec.unsafeWrapArray(Files.readAllBytes(request.input))
        val result = request.action match {
          case Action.Assemble    => Assembly.text(bytes).flatMap(Assembly.assemble(_, request.format))
          case Action.Disassemble =>
            val text = (request.format, request.section) match {
              case (Assembly.Format.AMDGPU(target, waveSize), Some(section)) =>
                Assembly.disassembleCodeObject(bytes, target, Some(section), waveSize)
              case _ => Assembly.disassemble(bytes, request.format)
            }
            text.map(value => Vec.unsafeWrapArray((value + "\n").getBytes(StandardCharsets.UTF_8)))
        }
        result match {
          case Left(error) =>
            Console.err.println(error)
            sys.exit(1)
          case Right(output) => request.output match {
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
