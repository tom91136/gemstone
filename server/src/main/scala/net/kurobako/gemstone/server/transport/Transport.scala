package net.kurobako.gemstone.server.transport

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.DataInputStream

object Transport {
  type ClientId = ClientId.T
  object ClientId extends RefinedType[Long, Positive]

  type RPCPort = RPCPort.T
  object RPCPort extends RefinedType[Int, Interval.Closed[1, 65535]]

  /** Reads exactly `size` bytes, failing with `failure` when the stream ends early. */
  def readExactly(input: DataInputStream, size: Int, failure: String => Throwable): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw failure(s"expected $size bytes, received ${result.length}")
    result
  }

  /** Parses a client identity, rejecting a non-positive wire value. */
  def clientId(value: Long): ClientId =
    ClientId.either(value).fold(
      message => throw IllegalArgumentException(s"invalid client identity: $message"),
      identity
    )
}
