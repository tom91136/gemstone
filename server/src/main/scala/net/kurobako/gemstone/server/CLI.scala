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
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.recording.*
import scala.util.Using
import scala.util.control.NonFatal

object CLI {
  private given Argument[NonEmpty[Set[API]]] = Argument.from("apis")(parseAPIs(_).toValidatedNel)

  private given Argument[Endpoint] = Argument.from("endpoint")(Endpoint.parse(_).toValidatedNel)
  private given Argument[Bind]     = Argument.from("listen address")(Bind.parse(_).toValidatedNel)
  private given Argument[Path]     = Argument.from("path")(configuration.path("path", _).toValidatedNel)

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
    sys.env.get(env.NativeDirectory).filter(_.nonEmpty)
      .fold[Either[String, Path]](Right(Path.of("native-build")))(configuration.path(env.NativeDirectory, _))
      .map(Launch.Options(apis.getOrElse(API.defaults), serverEndpoint, _, command._1, command._2))
      .toValidatedNel
  }

  private def parseAPIs(value: String): Either[String, NonEmpty[Set[API]]] = {
    val names = value.split(",", -1).toVector
    if (names.exists(_.isEmpty)) Left("--apis must be a comma-separated non-empty API list")
    else names.traverse(name => API(name)).flatMap(_.toSet.refine[NonEmpty[Set[API]]])
  }

  private enum Action {
    case Serve(bind: Bind, endpointFile: Option[Path], record: Option[Path])
    case Inspect(archive: Path)
    case Replay(archive: Path)
    case Shutdown(endpoint: Endpoint)
    case Run(options: Launch.Options)
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
          case Action.Serve(bind, endpointFile, record) => serve(bind, endpointFile, record)
        }
      catch {
        case NonFatal(error) => Console.err.println(s"gemstone: ${throwables.detail(error)}"); sys.exit(86)
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
}
