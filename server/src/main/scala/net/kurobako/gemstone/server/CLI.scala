package net.kurobako.gemstone.server

import cats.syntax.all.*
import com.monovore.decline.Argument
import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.PosixFileAttributeView
import java.nio.file.attribute.PosixFilePermissions
import net.kurobako.gemstone.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.environment as env
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.recording.*
import scala.util.Using
import scala.util.control.NonFatal

object CLI {
  private given Argument[Set[API]] = Argument.from("apis") { value =>
    parseAPIs(value).toValidatedNel
  }

  private given Argument[Endpoint] = Argument.from("endpoint")(Endpoint.parse(_).toValidatedNel)
  private given Argument[Bind]     = Argument.from("listen address")(Bind.parse(_).toValidatedNel)

  private val invocation = Opts.arguments[String]("command").mapValidated { command =>
    (for {
      executable <- Launch.Executable.either(command.head)
        .leftMap(_ => "command executable must be non-empty and contain no NUL characters")
      _ <- Launch.validateArguments(command.tail)
    } yield executable -> Vec.from(command.tail)).toValidatedNel
  }

  val launchOptions: Opts[Launch.Options] = (
    Opts.option[Set[API]]("apis", help = "Comma-separated APIs to provide").orNone,
    Opts.option[Endpoint]("server", help = "Connect to an existing Gemstone server").orNone,
    invocation
  ).mapN { (apis, serverEndpoint, command) =>
    val directory = sys.env.get(env.NativeDirectory).filter(_.nonEmpty).map(Path.of(_))
      .getOrElse(Path.of("native-build"))
    Launch.Options(
      apis.getOrElse(API.defaults),
      serverEndpoint,
      directory,
      command._1,
      command._2
    )
  }

  private def parseAPIs(value: String): Either[String, Set[API]] = {
    val names = value.split(",", -1).toVector
    if (names.exists(_.isEmpty)) Left("--apis must be a comma-separated non-empty API list")
    else names.traverse(name => API(name)).map(_.toSet)
  }

  private enum Action {
    case Serve(bind: Bind, endpointFile: Option[Path], record: Option[Path])
    case Inspect(archive: Path)
    case Replay(archive: Path)
    case Shutdown(endpoint: Endpoint)
    case Run(options: Launch.Options)
  }

  private val archive      = Opts.argument[String]("archive").map(Path.of(_))
  private val serveOptions = (
    Opts.option[Bind](
      "listen",
      help = "TCP or Unix socket address; TCP port 0 chooses a free port"
    ).withDefault(Bind.Tcp()),
    Opts.option[String]("endpoint-file", help = "Write the endpoint and client identity to this file")
      .map(Path.of(_)).orNone,
    Opts.option[String]("record", help = "Record RPC traffic to this journal").map(Path.of(_)).orNone
  ).tupled
    .map { case (bind, endpointFile, record) => Action.Serve(bind, endpointFile, record) }
  private val command = Command("gemstone", "Run or inspect the Gemstone execution server")(
    Opts.subcommands(
      Command("run", "Run a process with a Gemstone compute API")(launchOptions.map(Action.Run(_))),
      Command("serve", "Run the execution server")(serveOptions),
      Command("inspect", "Inspect an RPC journal")(archive.map(Action.Inspect(_))),
      Command("replay", "Replay an RPC journal")(archive.map(Action.Replay(_))),
      Command("shutdown", "Shut down a server")(
        Opts.option[Endpoint]("server", help = "Server endpoint")
          .map(Action.Shutdown(_))
      )
    )
  )

  def main(arguments: Array[String]): Unit = command.parse(Vec.from(arguments)) match {
    case Left(help) =>
      Console.err.println(help)
      sys.exit(if (help.errors.isEmpty) 0 else 2)
    case Right(action) => try action match {
          case Action.Inspect(file) => Console.println(Journal.inspect(file))
          case Action.Replay(file)  =>
            val protocolIdentity = Journal.ProtocolIdentity(ExecutionRPC.Identity)
            Console.println(Replay.run(Replay.Options(file, protocolIdentity), replayEndpoint(_)))
          case Action.Shutdown(endpoint) => protocol.Control.shutdown(endpoint)
          case Action.Run(options)       =>
            val status = Launch.validate(options) match {
              case Left(error) => Console.err.println(s"gemstone: $error"); 2
              case Right(_)    =>
                Launch.run(options).fold(error => { Console.err.println(s"gemstone: $error"); 86 }, identity)
            }
            if (status != 0) sys.exit(status)
          case Action.Serve(bind, endpointFile, record) => serve(bind, endpointFile, record)
        }
      catch {
        case NonFatal(error) => Console.err.println(s"gemstone: ${throwables.detail(error)}"); sys.exit(86)
      }
  }

  private def replayEndpoint(header: Journal.Header): Replay.Target = {
    val profile = header.executionProfile.getOrElse(throw IllegalArgumentException("capture has no execution profile"))
    val values  = (sys.env - env.Record)
      .updated(env.ExecutionMaxInstructions, profile.maxInstructions.toString)
      .updated(env.ExecutionParallelism, profile.parallelism.toString)
    val settings = Server.Settings.fromEnvironment(values)
      .fold(message => throw IllegalArgumentException(message), identity)
    val running = Server.start(settings)
    Replay.Target(running.endpoint, () => running.close())
  }

  private def serve(bind: Bind, endpointFile: Option[Path], record: Option[Path]): Unit = {
    val environment = record.fold(sys.env)(path => sys.env.updated(env.Record, path.toString))
    val settings    =
      Server.Settings.fromEnvironment(environment).fold(message => throw IllegalArgumentException(message), identity)
    Using.resource(Server.start(settings.copy(bind = bind))) { running =>
      endpointFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(
          file,
          s"""{"endpoint":${ujson.Str(running.endpoint.toString).render()},"client":${running.client}}""",
          StandardCharsets.US_ASCII
        )
        restrictToOwner(file)
      }
      Console.println(s"gemstone RPC server listening on ${running.endpoint}")
      Console.out.flush()
      running.await()
    }
  }

  private def restrictToOwner(file: Path): Unit =
    Option(Files.getFileAttributeView(file, classOf[PosixFileAttributeView])).foreach { view =>
      view.setPermissions(PosixFilePermissions.fromString("rw-------"))
    }
}
