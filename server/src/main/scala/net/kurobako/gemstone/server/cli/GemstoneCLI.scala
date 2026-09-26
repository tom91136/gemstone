package net.kurobako.gemstone.server.cli

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.launcher.EnvironmentNames
import net.kurobako.gemstone.launcher.Launcher
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.GemstoneRPCServer
import net.kurobako.gemstone.server.execution.ExecutionService
import net.kurobako.gemstone.server.recording.{RPCJournal, RPCReplay}
import net.kurobako.gemstone.server.transport.ServerControl
import net.kurobako.gemstone.server.transport.Transport.*
import net.kurobako.gemstone.vm.TaskLimits

import cats.syntax.all.*
import com.monovore.decline.{Command, Opts}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path}
import java.nio.file.attribute.{PosixFileAttributeView, PosixFilePermissions}
import scala.util.Using

object GemstoneCLI {
  private enum Action {
    case Serve(portFile: Option[Path], endpointFile: Option[Path], record: Option[Path])
    case Inspect(archive: Path)
    case Replay(archive: Path)
    case Shutdown(port: RPCPort)
    case Launch(options: Launcher.Options)
  }

  private val archive      = Opts.argument[String]("archive").map(Path.of(_))
  private val serveOptions = (
    Opts.option[String]("port-file", help = "Write the listening port to this file").map(Path.of(_)).orNone,
    Opts.option[String]("endpoint-file", help = "Write the endpoint and client identity to this file")
      .map(Path.of(_)).orNone,
    Opts.option[String]("record", help = "Record RPC traffic to this journal").map(Path.of(_)).orNone
  ).tupled
    .map { case (portFile, endpointFile, record) => Action.Serve(portFile, endpointFile, record) }
  private val command = Command("gemstone", "Run or inspect the Gemstone execution server")(
    Opts.subcommands(
      Command("run", "Run a process with a Gemstone compute API")(Launcher.options.map(Action.Launch(_))),
      Command("serve", "Run the execution server")(serveOptions),
      Command("inspect", "Inspect an RPC journal")(archive.map(Action.Inspect(_))),
      Command("replay", "Replay an RPC journal")(archive.map(Action.Replay(_))),
      Command("shutdown", "Shut down a server")(
        Opts.option[Int]("port", help = "Listening port")
          .mapValidated(value => RPCPort.either(value).leftMap(message => s"invalid port: $message").toValidatedNel)
          .map(Action.Shutdown(_))
      )
    )
  )

  def main(arguments: Array[String]): Unit = command.parse(Vec.from(arguments)) match {
    case Left(help) =>
      Console.err.println(help)
      sys.exit(if (help.errors.isEmpty) 0 else 2)
    case Right(action) => action match {
        case Action.Inspect(file) => Console.println(RPCJournal.inspect(file))
        case Action.Replay(file)  =>
          val protocolIdentity = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity)
            .fold(
              message => throw IllegalStateException(s"invalid execution protocol identity: $message"),
              value => value
            )
          Console.println(RPCReplay.run(RPCReplay.Options(file, protocolIdentity), replayEndpoint(_)))
        case Action.Shutdown(port)  => ServerControl.shutdown(port)
        case Action.Launch(options) =>
          val status = Launcher.run(options)
          if (status != 0) sys.exit(status)
        case Action.Serve(portFile, endpointFile, record) => serve(portFile, endpointFile, record)
      }
  }

  private def replayEndpoint(header: RPCJournal.Header): RPCReplay.Target = {
    val profile = header.executionProfile.getOrElse(throw IllegalArgumentException("capture has no execution profile"))
    val environment = (sys.env - EnvironmentNames.Record).updated(
      EnvironmentNames.ExecutionParallelism,
      profile.parallelism.toString
    )
    val limits  = TaskLimits(profile.maxInstructions, profile.quantum)
    val running = GemstoneRPCServer.start(limits, environment)
    RPCReplay.Target(running.port, () => running.close())
  }

  private def serve(portFile: Option[Path], endpointFile: Option[Path], record: Option[Path]): Unit = {
    val environment = record.fold(sys.env)(path => sys.env.updated(EnvironmentNames.Record, path.toString))
    val limits      = ExecutionService.requireLimits(environment)
    Using.resource(GemstoneRPCServer.start(limits, environment)) { running =>
      portFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(file, s"${running.port.value}\n", StandardCharsets.US_ASCII)
        restrictToOwner(file)
      }
      endpointFile.foreach { file =>
        Option(file.getParent).foreach(Files.createDirectories(_))
        Files.writeString(
          file,
          s"""{"port":${running.port.value},"client":${running.client.value}}""",
          StandardCharsets.US_ASCII
        )
        restrictToOwner(file)
      }
      Console.println(s"gemstone RPC server listening on 127.0.0.1:${running.port.value}")
      Console.out.flush()
      running.await()
    }
  }

  private def restrictToOwner(file: Path): Unit =
    Option(Files.getFileAttributeView(file, classOf[PosixFileAttributeView])).foreach { view =>
      view.setPermissions(PosixFilePermissions.fromString("rw-------"))
    }
}
