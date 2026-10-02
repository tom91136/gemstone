package net.kurobako.gemstone.server

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.vm.*
import scala.util.Using
import scala.util.control.NonFatal

object Connection {
  def serve(
      channel: Channel,
      clients: Clients,
      timeoutMillis: Int > 0,
      journal: Option[Journal.Writer],
      shutdown: () => Unit
  ): Unit =
    Using.resource(channel) { channel =>
      Using.Manager { use =>
        val buffered       = BufferedInputStream(channel.input)
        val bufferedOutput = BufferedOutputStream(channel.output)
        val recording      = journal.map(writer => use(Recording(writer, buffered, bufferedOutput)))
        // The channel owns the streams. Frames flush explicitly; closing a buffered
        // wrapper after a failed write would retry an already-partial response.
        val input  = DataInputStream(recording.fold[java.io.InputStream](buffered)(_.input))
        val output = DataOutputStream(recording.fold[java.io.OutputStream](bufferedOutput)(_.output))
        @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        var bound = Option.empty[(ClientId, SessionId)]
        @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        var acquired = Option.empty[clients.Lease]

        @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        var negotiated = false

        def accept(header: Header): Either[ExecutionFailure, AnyRef] =
          if (bound.exists(_ != (header.client, header.session)))
            Left(ExecutionFailure.InvalidRequest("a connection cannot change client or process identity"))
          else clients.accept(header.client).map { owner =>
            bound = Some((header.client, header.session))
            owner
          }

        def session(header: Header): Either[ExecutionFailure, ExecutionSession] =
          try {
            val lease = acquired match {
              case Some(lease) => Right(lease)
              case None        => clients.acquire(header.client, header.session).map { lease =>
                  acquired = Some(use(lease))
                  lease
                }
            }
            lease.flatMap(_.service)
          } catch { case NonFatal(error) => Left(ExecutionFailure.Failed(throwables.detail(error))) }

        def execution(): Boolean = {
          // Only malformed input is translated here. Output and recording failures leave the connection.
          val request = try {
            val header = readHeader(input)
            Right(header -> readPayload(input, header))
          } catch {
            case error: IllegalArgumentException => Left(ExecutionFailure.InvalidRequest(throwables.detail(error)))
            case error: EOFException             => Left(ExecutionFailure.InvalidRequest(throwables.detail(error)))
          }
          val result = request.flatMap { (header, payload) =>
            accept(header).flatMap { owner =>
              recording.foreach(_.accepted(owner))
              session(header).flatMap { service =>
                Wire.Protocol[ExecutionRPC.Operation].decodePayload(header.operation, payload, ExecutionRPC.Limits)
                  .left.map(ExecutionFailure.InvalidRequest(_))
                  .flatMap(decoded => ExecutionHandler(decoded.value, service))
              }
            }
          }
          writeResponse(output, result)
          recording.foreach(_.response())
          request.isRight
        }

        def exchange(): Boolean = try {
          recording.foreach(_.begin())
          // Idle connections have no deadline. Once the first byte arrives, bound all remaining frame reads.
          channel.readTimeout(0)
          val first = input.read()
          if (first < 0) false
          else {
            channel.readTimeout(timeoutMillis.raw)
            val magic = (first << 24) | (input.readUnsignedByte() << 16) | (input.readUnsignedByte() << 8) |
              input.readUnsignedByte()
            magic match {
              case Control.Magic =>
                val operation = input.readInt()
                val size      = input.readInt()
                require(size == (if (operation == Control.ReleaseClient) 8 else 0), "invalid control request size")
                operation match {
                  case Control.RegisterClient => Control.writeResponse(output, operation, clients.register().toLong)
                  case Control.ReleaseClient  =>
                    clients.release(clientId(input.readLong()))
                    Control.writeResponse(output, operation)
                  case Control.Shutdown => Control.writeResponse(output, operation); shutdown()
                  case Control.Finish   =>
                    acquired.foreach(_.close())
                    acquired = None
                    recording.foreach(_.close())
                    Control.writeResponse(output, operation)
                  case other => throw IllegalArgumentException(s"unknown control operation $other")
                }
                false
              case ExecutionRPC.HelloMagic =>
                require(!negotiated, "execution schema already negotiated")
                negotiated = recording match {
                  case Some(capture) => capture.unrecorded(acceptHello(input, output))
                  case None          => acceptHello(input, output)
                }
                negotiated
              case ExecutionRPC.RequestMagic =>
                require(negotiated, "execution request precedes schema negotiation")
                execution()
              case value => throw IllegalArgumentException(f"invalid request magic: 0x$value%08x")
            }
          }
        } catch {
          case NonFatal(_) if channel.isClosed => false
          case NonFatal(error)                 =>
            recording.foreach(_.failed(s"connection exchange failed: ${throwables.detail(error)}"))
            scribe.debug("RPC connection exchange failed", error)
            false
        }
        while (exchange()) ()
      }.get
    }
}
