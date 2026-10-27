package net.kurobako.gemstone.server.recording

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.EOFException
import java.io.IOException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException
import java.nio.ByteBuffer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import scala.jdk.CollectionConverters.*
import scala.util.Using

class ReplaySuite extends munit.FunSuite {
  import journal.*
  // The connection loop flag is mutable state confined to this final peer fixture.
  private class Peer(handle: (Array[Byte], DataInputStream, DataOutputStream) => Unit) extends AutoCloseable {
    private val server               = ServerSocket(0, 32, InetAddress.getLoopbackAddress)
    private val stopping             = AtomicBoolean(false)
    private val error                = AtomicReference(Option.empty[Throwable])
    private val sockets              = java.util.concurrent.ConcurrentHashMap.newKeySet[Socket]()
    private val threads              = java.util.concurrent.ConcurrentHashMap.newKeySet[Thread]()
    val registered: AtomicInteger    = AtomicInteger()
    val released: AtomicInteger      = AtomicInteger()
    val clients: java.util.Set[Long] =
      java.util.concurrent.ConcurrentHashMap.newKeySet[Long]()
    private val acceptor = Thread(() =>
      while (!stopping.get())
        try {
          val socket = server.accept(); socket.setSoTimeout(3000); sockets.add(socket)
          val thread = Thread(() =>
            Using.resource(socket) { socket =>
              // The socket is managed by Using; the finally only maintains the peer's live-socket registry.
              try serve(socket)
              catch {
                case _: EOFException => (); case _: SocketException if stopping.get() => ();
                case e: Throwable    => val _ = error.compareAndSet(None, Some(e))
              } finally { val _ = sockets.remove(socket) }
            }
          )
          threads.add(thread); thread.start()
        } catch { case _: SocketException if stopping.get() => () }
    )
    acceptor.start()
    def endpoint: Replay.Target = {
      val port = server.getLocalPort.refine[Port].fold(message => fail(message), value => value)
      Replay.Target(Endpoint.Tcp("127.0.0.1", port), () => close())
    }
    private def serve(socket: Socket): Unit = {
      val input = DataInputStream(socket.getInputStream); val output = DataOutputStream(socket.getOutputStream)
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var active = true
      while (active) {
        val magic = input.readInt()
        if (magic == protocol.Control.Magic) {
          val operation = input.readInt()
          val size      = input.readInt()
          assertEquals(size, if (operation == Control.ReleaseClient) 8 else 0)
          operation match {
            case Control.RegisterClient =>
              Control.writeResponse(output, operation, registered.incrementAndGet().toLong + 100L)
            case Control.ReleaseClient =>
              input.readLong(); released.incrementAndGet()
              Control.writeResponse(output, operation)
            case Control.Finish => Control.writeResponse(output, operation)
            case value          => fail(s"bad control operation $value")
          }
          output.flush(); active = false
        } else if (magic == ExecutionRPC.HelloMagic) {
          assert(protocol.acceptHello(input, output))
        } else {
          assertEquals(magic, ExecutionRPC.RequestMagic)
          val header = ExecutionRPC.RequestHeaderBytes.raw
          val prefix = Array.ofDim[Byte](header); ByteBuffer.wrap(prefix).putInt(magic)
          input.readFully(prefix, 4, header - 4)
          val extra = Frame.integer(prefix, ExecutionRPC.RequestPayloadSizeOffset) +
            Frame.integer(prefix, ExecutionRPC.RequestTagSizeOffset)
          assert(extra >= 0 && extra <= 4096)
          val frame  = java.util.Arrays.copyOf(prefix, header + extra); input.readFully(frame, header, extra)
          val client = ByteBuffer.wrap(frame).getLong(ExecutionRPC.RequestClientOffset)
          assert(client != 23L); clients.add(client)
          handle(frame, input, output); output.flush()
        }
      }
    }
    def verify(): Unit         = error.get().foreach(e => throw AssertionError("scripted replay peer failed", e))
    override def close(): Unit =
      if (stopping.compareAndSet(false, true)) {
        server.close()
        sockets.asScala.view.foreach(_.close())
        acceptor.join(4000)
        threads.asScala.view.foreach { thread => thread.join(4000); assert(!thread.isAlive) }
        assert(!acceptor.isAlive)
      }
  }

  test("replay preserves multiple live connections, surrogate clients and response order") {
    temporary { file =>
      capture(file) { w =>
        Using.resource(w.connection(ClientId(1L))) { a =>
          Using.resource(w.connection(ClientId(2L))) { b =>
            a.request(execution(session = 77)); b.request(execution(session = 77))
            b.response(response()); a.response(response())
          }
        }
      }
      val arrivals = CountDownLatch(2)
      val peer     = Peer { (request, _, out) =>
        assertEquals(
          Journal.Frames.normalised(Journal.Kind.Request, request, Journal.FrameByteSize(4096)).toSeq,
          Journal.Frames.normalised(
            Journal.Kind.Request,
            execution(session = 77),
            Journal.FrameByteSize(4096)
          ).toSeq
        )
        arrivals.countDown(); val arrived = arrivals.await(2, TimeUnit.SECONDS); assert(arrived); out.write(response())
      }
      Using.resource(peer) { peer =>
        val result = Replay.run(Replay.Options(file, 3000), _ => peer.endpoint)
        assertEquals(result.requests.raw, 2); assertEquals(peer.registered.get(), 2);
        assertEquals(peer.clients.size(), 2)
        peer.verify()
      }
    }
  }

  test("completion timing permits earlier success and waits for recorded terminal results") {
    for (early <- Seq(false, true)) temporary { file =>
      capture(file) { w =>
        Using.resource(w.connection(client)) { c =>
          c.request(query); c.response(completion(1)); c.request(query);
          c.response(completion(2))
        }
      }
      val calls = AtomicInteger()
      val peer  = Peer((_, _, out) => out.write(completion(if (early || calls.incrementAndGet() >= 4) 2 else 1)))
      Using.resource(peer) { peer =>
        val result = Replay.run(Replay.Options(file, 3000), _ => peer.endpoint)
        assertEquals(result.extraCompletionPolls.raw, if (early) 0L else 2L)
        peer.verify()
      }
    }
  }

  test("pending timing does not hide failures and terminal waits are bounded") {
    for (failed <- Seq(false, true)) temporary { file =>
      capture(file) { w =>
        Using.resource(w.connection(client)) { c =>
          c.request(query); c.response(completion(1))
          c.request(query); c.response(completion(2))
        }
      }
      val peer = Peer((_, _, out) => out.write(if (failed) completion(0) else completion(1)))
      Using.resource(peer) { peer =>
        val error = intercept[IOException](Replay.run(
          Replay.Options(file, 1000),
          _ => peer.endpoint
        ))
        assert(
          error.getMessage.contains(if (failed) "frame differs" else "completion remained pending"),
          error.getMessage
        )
        peer.verify()
      }
    }
  }

  test("recorded terminal failures wait through pending replies and compare the actual status") {
    temporary { file =>
      capture(file) { w =>
        Using.resource(w.connection(client)) { c =>
          c.request(query); c.response(completion(0))
        }
      }
      val calls = AtomicInteger()
      val peer  = Peer((_, _, out) => out.write(if (calls.incrementAndGet() < 3) completion(1) else completion(0)))
      Using.resource(peer) { peer =>
        val report = Replay.run(Replay.Options(file, 3000), _ => peer.endpoint)
        assertEquals(report.extraCompletionPolls.raw, 2L)
        peer.verify()
      }
    }
  }

  test("response mismatches close the endpoint and preserve cleanup failures as suppressed") {
    temporary { file =>
      capture(file) { w =>
        Using.resource(w.connection(client)) { c =>
          c.request(execution()); c.response(response())
        }
      }
      val closed = AtomicBoolean(false)
      val peer   = Peer((_, _, out) => out.write(response(1)))
      Using.resource(peer) { peer =>
        val e = intercept[IOException](Replay.run(
          Replay.Options(file, 3000),
          _ =>
            Replay.Target(
              peer.endpoint.endpoint,
              () => { closed.set(true); peer.close(); throw IOException("cleanup failed") }
            )
        ))
        assert(e.getMessage.contains("frame differs"), e.getMessage)
        assertEquals(e.getSuppressed.toSeq.map(_.getMessage), Seq("cleanup failed"))
        assert(closed.get()); peer.verify()
      }
    }
  }
}
