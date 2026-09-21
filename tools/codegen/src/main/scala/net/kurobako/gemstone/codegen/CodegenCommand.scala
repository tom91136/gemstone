package net.kurobako.gemstone.codegen

import com.monovore.decline.Command

object CodegenCommand {
  def run[A](command: Command[A], arguments: Array[String])(handler: A => Unit): Unit =
    command.parse(arguments.toSeq).fold(help => throw IllegalArgumentException(help.toString), handler)
}
