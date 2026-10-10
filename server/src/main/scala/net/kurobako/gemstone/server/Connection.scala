package net.kurobako.gemstone.server

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
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
        var identity = Option.empty[(ClientId, SessionId)]
        @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        var lease = Option.empty[clients.Lease]
        @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
        var negotiated = false

        // The first accepted request binds the connection to its client and process session, as journalled.
        def session(header: Header): Either[ExecutionFailure, ExecutionSession] =
          for {
            _ <- Either.cond(
              identity.forall(_ == (header.client, header.session)),
              (),
              ExecutionFailure.InvalidRequest("a connection cannot change client or process identity")
            )
            _ <- clients.accept(header.client)
            _ = identity = Some((header.client, header.session))
            _ = recording.foreach(_.accepted(header.client))
            bound <- lease match {
              case Some(existing) => Right(existing)
              case None           =>
                try clients.acquire(header.client, header.session).map { acquired =>
                    lease = Some(use(acquired))
                    acquired
                  }
                catch { case NonFatal(error) => Left(ExecutionFailure.Failed(throwables.detail(error))) }
            }
            service <- bound.service
          } yield service

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
            session(header).flatMap { service =>
              Wire.Protocol[ExecutionRPC.Operation].decodePayload(header.operation, payload, ExecutionRPC.Limits)
                .left.map(message => ExecutionFailure.InvalidRequest(s"invalid execution request: $message"))
                .flatMap(decoded => ExecutionHandler(decoded.value, service))
            }
          }
          writeResponse(output, result)
          recording.foreach(_.response())
          request.isRight
        }

        def reject(detail: String): Unit = {
          recording.foreach(_.failed(s"connection exchange failed: $detail"))
          scribe.debug(s"RPC connection exchange failed: $detail")
        }

        def exchange(): Boolean = try {
          recording.foreach(_.begin())
          // Idle connections have no deadline. Once the first byte arrives, bound all remaining frame reads.
          channel.readTimeout(0)
          val first = input.read()
          if (first < 0) false
          else {
            channel.readTimeout(timeoutMillis.widen[Int >= 0])
            val magic = (first << 24) | (input.readUnsignedByte() << 16) | (input.readUnsignedByte() << 8) |
              input.readUnsignedByte()
            magic match {
              case Control.Magic =>
                val operation = input.readInt()
                val size      = input.readInt()
                require(size == (if (operation == Control.ReleaseClient) 8 else 0), "invalid control request size")
                operation match {
                  case Control.RegisterClient => Control.writeResponse(output, operation, clients.register().raw)
                  case Control.ReleaseClient  =>
                    clients.release(clientId(input.readLong()))
                    Control.writeResponse(output, operation)
                  case Control.Shutdown => Control.writeResponse(output, operation); shutdown()
                  case Control.Finish   =>
                    lease.foreach(_.close())
                    lease = None
                    recording.foreach(_.close())
                    Control.writeResponse(output, operation)
                  case other => reject(s"unknown control operation $other")
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
              case value => reject(f"invalid request magic: 0x$value%08x"); false
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
