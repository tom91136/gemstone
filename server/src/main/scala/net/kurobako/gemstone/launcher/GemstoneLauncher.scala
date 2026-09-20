package net.kurobako.gemstone.launcher

import net.kurobako.gemstone.{NoNul, Vec}

import java.nio.file.{Files, Path}
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import cats.syntax.all.*
import com.monovore.decline.{Argument, Opts}
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.util.Using

private[gemstone] object GemstoneLauncher {
  private[gemstone] type Executable = Executable.T
  private[gemstone] object Executable extends RefinedType[String, MinLength[1] & NoNul]

  private[gemstone] final case class Options(
      apis: Set[ComputeAPI],
      layout: RuntimeLayout,
      executable: Executable,
      arguments: Vec[String :| NoNul]
  )

  private[launcher] final class RunningProcess(val process: Process) extends AutoCloseable {
    private val stopping = AtomicBoolean(false)
    private val shutdownHook = Thread(() => stop(), "gemstone-launcher-cleanup")
    Runtime.getRuntime.addShutdownHook(shutdownHook)

    private def stop(): Unit = {
      if (stopping.compareAndSet(false, true)) {
        val descendants = process.descendants()
        try descendants.forEach { descendant =>
          val _ = descendant.destroyForcibly()
        }
        finally descendants.close()
        val _ = process.destroyForcibly()
        try {
          val _ = process.waitFor(2, TimeUnit.SECONDS)
        } catch case _: InterruptedException => Thread.currentThread().interrupt()
      }
    }

    override def close(): Unit = {
      try Runtime.getRuntime.removeShutdownHook(shutdownHook)
      catch case _: IllegalStateException => ()
      stop()
    }
  }

  private given Argument[Set[ComputeAPI]] = Argument.from("apis") { value =>
    ComputeAPI.parseAll(value).leftMap(_.message).toValidatedNel
  }

  private val invocation = Opts.arguments[String]("command").mapValidated { command =>
    (for {
      executable <- Executable.either(command.head)
        .leftMap(_ => "command executable must be non-empty and contain no NUL characters")
      arguments <- command.tail.zipWithIndex.traverse { case (argument, index) =>
        argument.refineEither[NoNul]
          .leftMap(_ => s"command argument ${index + 1} contains a NUL character")
      }
    } yield executable -> Vec.from(arguments)).toValidatedNel
  }

  private[gemstone] val options = (
    Opts.option[Set[ComputeAPI]]("apis", help = "Comma-separated APIs to provide").orNone,
    invocation
  ).mapN { (apis, command) =>
    val directory = sys.env.get("GEMSTONE_NATIVE_DIRECTORY").filter(_.nonEmpty).map(Path.of(_))
      .getOrElse(Path.of("native-build"))
    Options(
      apis.getOrElse(ComputeAPI.defaults(RuntimePlatform.current)),
      RuntimeLayout(directory),
      command._1,
      command._2
    )
  }

  private[gemstone] def run(options: Options): Int = {
    val layout  = options.layout.absolute
    val missing = options.apis.iterator.map(layout.library).filterNot(Files.isRegularFile(_)).toVector
    if (missing.nonEmpty) {
      Console.err.println(s"gemstone: runtime provider is not a regular file: ${missing.head}")
      2
    } else {
      val launched = GemstoneProcess.run(options.apis, layout, sys.env) { environment =>
        val builder = ProcessBuilder((options.executable.value +: options.arguments)*).inheritIO()
        environment.applyTo(builder.environment())
        Using.resource(RunningProcess(builder.start()))(_.process.waitFor())
      }
      launched match {
        case Left(error)   => Console.err.println(s"gemstone: ${error.message}"); 86
        case Right(status) => status
      }
    }
  }
}
