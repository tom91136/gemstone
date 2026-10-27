package net.kurobako.gemstone.codegen

import com.monovore.decline.Command
import com.monovore.decline.Opts
import java.nio.file.Path

object CodegenCommand {
  def path(name: String): Opts[Path] = Opts.argument[String](name).map(Path.of(_))

  def run[A](command: Command[A], arguments: Array[String])(handler: A => Unit): Unit =
    command.parse(arguments.toSeq).fold(help => throw IllegalArgumentException(help.toString), handler)
}
