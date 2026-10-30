package net.kurobako.gemstone.server

import cats.syntax.all.*
import com.monovore.decline.Argument
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.string.*
import net.kurobako.gemstone.toolchain.workarounds
import scala.util.Using
import scala.util.control.NonFatal

object CLI {
  private given Argument[NonEmpty[Set[API]]] = Argument.from("apis")(parseAPIs(_).toValidatedNel)

  private given Argument[Endpoint] = Argument.from("endpoint")(Endpoint.parse(_).toValidatedNel)
  private given Argument[Bind]     = Argument.from("listen address")(Bind.parse(_).toValidatedNel)
  private given Argument[Path]     = Argument.from("path")(paths.parse("path", _).toValidatedNel)

  private val invocation = Opts.arguments[String]("command").mapValidated { command =>
    (for {
      executable <- command.head.refine[Launch.Executable]
        .leftMap(_ => "command executable must be non-empty and contain no NUL characters")
      arguments <- command.tail.traverse(_.refine[Launch.Argument])
    } yield executable -> Vec.from(arguments)).toValidatedNel
  }

  val launchOptions: Opts[Launch.Options] = (
    Opts.option[NonEmpty[Set[API]]]("apis", help = "Comma-separated APIs to provide").orNone,
    Opts.option[Endpoint]("server", help = "Connect to an existing Gemstone server").orNone,
    invocation
  ).tupled.mapValidated { (apis, serverEndpoint, command) =>
    sys.env.get(env.NativeDir).filter(_.nonEmpty)
      .fold[Either[String, Path]](Right(Path.of("native-build")))(paths.parse(env.NativeDir, _))
      .map(Launch.Options(apis.getOrElse(API.defaults), serverEndpoint, _, command._1, command._2))
      .toValidatedNel
  }

  private def parseAPIs(value: String): Either[String, NonEmpty[Set[API]]] = {
    val names = value.split(",", -1).toVector
    if (names.exists(_.isEmpty)) Left("--apis must be a comma-separated non-empty API list")
    else names.traverse(name => API(name)).flatMap(_.toSet.refine[NonEmpty[Set[API]]])
  }

  enum Action {
    case Serve(bind: Bind, endpointFile: Option[Path], record: Option[Path])
    case Inspect(archive: Path)
    case Replay(archive: Path)
    case Shutdown(endpoint: Endpoint)
    case Run(options: Launch.Options)
    case Assemble(format: Assembly.Format, input: Path, output: Path)
    case Disassemble(format: Assembly.Format, input: Path, output: Option[Path], section: Option[String])
  }

  private val formatOption = (
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
        value.refine[amdgpu.Target].leftMap(_.toString).map(target =>
          Assembly.Format.AMDGPU(target, waveSize.getOrElse(32))
        ).toValidatedNel
      case ("sass", Some(value))      => sass.Target.parse(value).map(Assembly.Format.SASS(_)).toValidatedNel
      case ("sass", None)             => "SASS requires --target".invalidNel
      case ("amdgpu", None)           => "AMDGPU requires --target".invalidNel
      case ("spirv" | "ptx", Some(_)) => "--target is only used with --isa amdgpu or sass".invalidNel
      case _                          => s"unknown ISA '$isa'".invalidNel
    }
  }

  private val inputArgument = Opts.argument[String]("input").map(Path.of(_))
  private val outputOption  = Opts.option[String]("output", short = "o", help = "Output file (required for assembly)")
    .map(Path.of(_))

  private val assembleOptions = (
    formatOption,
    inputArgument,
    outputOption.orNone.mapValidated(_.toValidNel("assembly requires --output"))
  ).mapN(Action.Assemble(_, _, _))

  private val disassembleOptions = (
    formatOption,
    inputArgument,
    outputOption.orNone,
    Opts.option[String]("section", help = "Executable ELF section to disassemble").orNone
  ).tupled.mapValidated { (format, input, output, section) =>
    (format, section) match {
      case (Assembly.Format.AMDGPU(_, _) | Assembly.Format.SASS(_), _) | (_, None) =>
        Action.Disassemble(format, input, output, section).validNel
      case _ => "--section requires disassemble --isa amdgpu or sass".invalidNel
    }
  }

  private val archive      = Opts.argument[Path]("archive")
  private val serveOptions = (
    Opts.option[Bind](
      "listen",
      help = "TCP or Unix socket address; TCP port 0 chooses a free port"
    ).withDefault(Bind.Tcp()),
    Opts.option[Path]("endpoint-file", help = "Write the endpoint and client identity to this file").orNone,
    Opts.option[Path]("record", help = "Record RPC traffic to this journal").orNone
  ).tupled
    .map { case (bind, endpointFile, record) => Action.Serve(bind, endpointFile, record) }
  val command: Command[Action] = Command("gemstone", "Run or inspect the Gemstone execution server")(
    Opts.subcommands(
      Command("run", "Run a process with a Gemstone compute API")(launchOptions.map(Action.Run(_))),
      Command("serve", "Run the execution server")(serveOptions),
      Command("inspect", "Inspect an RPC journal")(archive.map(Action.Inspect(_))),
      Command("replay", "Replay an RPC journal")(archive.map(Action.Replay(_))),
      Command("shutdown", "Shut down a server")(
        Opts.option[Endpoint]("server", help = "Server endpoint")
          .map(Action.Shutdown(_))
      ),
      Command("assemble", "Assemble text into an ISA image")(assembleOptions),
      Command("disassemble", "Print an ISA image as canonical assembly")(disassembleOptions)
    )
  )

  def main(arguments: Array[String]): Unit = {
    workarounds.install()
    command.parse(Vec.from(arguments)) match {
      case Left(help) =>
        Console.err.println(help)
        sys.exit(if (help.errors.isEmpty) 0 else 2)
      case Right(action) => try action match {
            case Action.Inspect(file)      => Console.println(Journal.inspect(file))
            case Action.Replay(file)       => Console.println(Replay.run(Replay.Options(file), replayEndpoint(_)))
            case Action.Shutdown(endpoint) => protocol.Control.shutdown(endpoint)
            case Action.Run(options)       =>
              val status = Launch.validate(options) match {
                case Left(error) => Console.err.println(s"gemstone: $error"); 2
                case Right(_)    =>
                  Launch.run(options).fold(error => { Console.err.println(s"gemstone: $error"); 86 }, identity)
              }
              if (status != 0) sys.exit(status)
            case Action.Serve(bind, endpointFile, record)           => serve(bind, endpointFile, record)
            case Action.Assemble(format, input, output)             => assemble(format, input, output)
            case Action.Disassemble(format, input, output, section) => disassemble(format, input, output, section)
          }
        catch {
          case NonFatal(error) => Console.err.println(s"gemstone: ${throwables.detail(error)}"); sys.exit(86)
        }
    }
  }

  private def settings(): Server.Settings =
    Server.Settings.fromEnvironment(sys.env).fold(message => throw IllegalArgumentException(message), identity)

  private def replayEndpoint(header: Journal.Header): Replay.Target = {
    val running = Server.start(
      Server.Settings.replay(sys.env, header.executionProfile)
        .fold(message => throw IllegalArgumentException(message), identity)
    )
    Replay.Target(running.endpoint, () => running.close())
  }

  private def serve(bind: Bind, endpointFile: Option[Path], record: Option[Path]): Unit = {
    val defaults = settings()
    Using.resource(Server.start(defaults.copy(bind = bind, record = record.orElse(defaults.record)))) { running =>
      endpointFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(
          file,
          s"""{"endpoint":${ujson.Str(running.endpoint.toString).render()},"client":${running.client}}""",
          StandardCharsets.US_ASCII
        )
        PrivateFile.restrict(file)
      }
      Console.println(s"gemstone RPC server listening on ${running.endpoint}")
      Console.out.flush()
      running.await()
    }
  }

  private def assemble(format: Assembly.Format, input: Path, output: Path): Unit =
    emit(Some(output))(Assembly.text(read(input)).flatMap(Assembly.assemble(_, format)))

  private def disassemble(format: Assembly.Format, input: Path, output: Option[Path], section: Option[String]): Unit =
    emit(output)(
      Assembly.disassemble(read(input), format, section)
        .flatMap(text => UTF8.encode(text + "\n").leftMap(Assembly.Error.TextEncoding(_)))
    )

  private def read(input: Path): Vec[Byte] = Vec.unsafeWrapArray(Files.readAllBytes(input))

  private def emit(destination: Option[Path])(result: => Either[Assembly.Error, Vec[Byte]]): Unit =
    try result match {
        case Left(error)  => Console.err.println(error); sys.exit(1)
        case Right(bytes) => destination match {
            case Some(path) => val _ = Files.write(path, Vec.toArray(bytes))
            case None       => Console.out.write(Vec.toArray(bytes))
          }
      }
    catch {
      case NonFatal(error) => Console.err.println(error.getMessage); sys.exit(1)
    }
}
