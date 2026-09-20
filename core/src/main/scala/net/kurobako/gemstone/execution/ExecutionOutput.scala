package net.kurobako.gemstone.execution

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.rpc.Wire.Codec

enum PrintArgument derives Codec {
  case Integer(bits: Vec[Long], width: Int, isSigned: Boolean)
  case Floating(bits: Vec[Long], width: Int)
  case Pointer(address: Long)
  case Text(value: String)
}

final case class PrintEvent(format: String, arguments: Vec[PrintArgument]) derives Codec

trait ExecutionOutput {
  def print(event: PrintEvent): Either[ExecutionFailure, Unit]
}

object ExecutionOutput {
  object Discard extends ExecutionOutput {
    override def print(event: PrintEvent): Either[ExecutionFailure, Unit] = Right(())
  }
}
