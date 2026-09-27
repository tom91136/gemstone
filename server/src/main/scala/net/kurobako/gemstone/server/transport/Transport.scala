package net.kurobako.gemstone.server.transport

import java.io.DataInputStream

import scala.compiletime.{constValue, error}

object Transport {
  opaque type ClientId = Long

  object ClientId {
    inline def either(value: Long): Either[String, ClientId] =
      if (value > 0L) Right(value) else Left(s"client id must be strictly positive: $value")

    inline def unsafe(value: Long): ClientId =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Long & Singleton](inline value: A): ClientId =
      inline if (constValue[A] > 0L) value else error("client id must be strictly positive")

    extension (value: ClientId) {
      inline def raw: Long = value
    }
  }

  opaque type RPCPort = Int

  object RPCPort {
    inline def either(value: Int): Either[String, RPCPort] =
      if (value >= 1 && value <= 65535) Right(value) else Left(s"RPC port must be between 1 and 65535: $value")

    inline def unsafe(value: Int): RPCPort =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: Int & Singleton](inline value: A): RPCPort =
      inline if (constValue[A] >= 1 && constValue[A] <= 65535) value
      else error("RPC port must be between 1 and 65535")

    extension (value: RPCPort) {
      inline def raw: Int = value
    }
  }

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
