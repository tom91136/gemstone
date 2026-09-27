package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.Constraint.Positive

import net.kurobako.gemstone.*

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.transport.ServerControl
import net.kurobako.gemstone.server.transport.Transport.*
import java.io.{DataInputStream, DataOutputStream, EOFException, IOException}
import java.net.{InetAddress, ServerSocket, Socket, SocketException}
import java.nio.ByteBuffer
import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}
import scala.jdk.CollectionConverters.*
import scala.util.Using

final class RPCReplaySuite extends munit.FunSuite {
  import JournalFixtures.*
  // The connection loop flag is mutable state confined to this final peer fixture.
  private final class Peer(handle: (Array[Byte], DataInputStream, DataOutputStream) => Unit) extends AutoCloseable {
    private val server   = ServerSocket(0, 32, InetAddress.getLoopbackAddress)
    private val stopping = AtomicBoolean(false)
    private val error    = AtomicReference(Option.empty[Throwable])
    private val sockets  = java.util.concurrent.ConcurrentHashMap.newKeySet[Socket]()
    private val threads  = java.util.concurrent.ConcurrentHashMap.newKeySet[Thread]()
    val registered       = AtomicInteger()
    val released         = AtomicInteger()
    val clients          = java.util.concurrent.ConcurrentHashMap.newKeySet[java.lang.Long]()
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
    def endpoint: RPCReplay.Target = {
      val port = RPCPort.either(server.getLocalPort).fold(message => fail(message), value => value)
      RPCReplay.Target(port, () => close())
    }
    private def serve(socket: Socket): Unit = {
      val input = DataInputStream(socket.getInputStream); val output = DataOutputStream(socket.getOutputStream)
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var active = true
      while (active) {
        val magic = input.readInt()
        if (magic == ServerControl.Magic) {
          input.readInt() match {
            case ServerControl.RegisterClient => output.writeLong(registered.incrementAndGet().toLong + 100L)
            case ServerControl.ReleaseClient  =>
              input.readLong(); released.incrementAndGet()
              output.writeInt(0)
            case value => throw IOException(s"bad control operation $value")
          }
          output.flush(); active = false
        } else {
          assertEquals(magic, ExecutionRPC.RequestMagic)
          val header = ExecutionRPC.RequestHeaderBytes.raw
          val prefix = Array.ofDim[Byte](header); ByteBuffer.wrap(prefix).putInt(magic)
          input.readFully(prefix, 4, header - 4)
          val extra = RPCJournal.Frames.integer(prefix, ExecutionRPC.RequestPayloadSizeOffset.raw)
          assert(extra >= 0 && extra <= 4096)
          val frame  = java.util.Arrays.copyOf(prefix, header + extra); input.readFully(frame, header, extra)
          val client = ByteBuffer.wrap(frame).getLong(ExecutionRPC.RequestClientOffset.raw)
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
        val a = w.connection(Object()); val b = w.connection(Object())
        a.request(execution(session = 77)); b.request(execution(session = 77))
        b.response(response()); a.response(response()); a.close(); b.close()
      }
      val arrivals = CountDownLatch(2)
      val peer     = Peer { (request, _, out) =>
        assertEquals(
          RPCJournal.Frames.normalized(RPCJournal.Kind.Request, request, RPCJournal.FrameByteSize(4096)).toSeq,
          RPCJournal.Frames.normalized(
            RPCJournal.Kind.Request,
            execution(session = 77),
            RPCJournal.FrameByteSize(4096)
          ).toSeq
        )
        arrivals.countDown(); val arrived = arrivals.await(2, TimeUnit.SECONDS); assert(arrived); out.write(response())
      }
      Using.resource(peer) { peer =>
        val result = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(result.requests.raw, 2); assertEquals(peer.registered.get(), 2);
        assertEquals(peer.clients.size(), 2)
        peer.verify()
      }
    }
  }

  test("completion timing permits earlier success and waits for recorded terminal results") {
    for (early <- Seq(false, true)) temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(query); c.response(completion(1)); c.request(query);
        c.response(completion(2)); c.close()
      }
      val calls = AtomicInteger()
      val peer  = Peer((_, _, out) => out.write(completion(if (early || calls.incrementAndGet() >= 4) 2 else 1)))
      Using.resource(peer) { peer =>
        val result = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(result.extraCompletionPolls.raw, if (early) 0L else 2L)
        peer.verify()
      }
    }
  }

  test("pending timing does not hide failures and terminal waits are bounded") {
    for (failed <- Seq(false, true)) temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object())
        c.request(query); c.response(completion(1))
        c.request(query); c.response(completion(2)); c.close()
      }
      val peer = Peer((_, _, out) => out.write(if (failed) completion(0) else completion(1)))
      Using.resource(peer) { peer =>
        val error = intercept[IOException](RPCReplay.run(
          RPCReplay.Options(file, schema, 1000),
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
        val c = w.connection(Object()); c.request(query); c.response(completion(0)); c.close()
      }
      val calls = AtomicInteger()
      val peer  = Peer((_, _, out) => out.write(if (calls.incrementAndGet() < 3) completion(1) else completion(0)))
      Using.resource(peer) { peer =>
        val report = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(report.extraCompletionPolls.raw, 2L)
        peer.verify()
      }
    }
  }

  test("real response mismatches fail and close the fresh endpoint") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(execution()); c.response(response()); c.close()
      }
      val closed = AtomicBoolean(false)
      val peer   = Peer((_, _, out) => out.write(response(1)))
      Using.resource(peer) { peer =>
        val e = intercept[IOException](RPCReplay.run(
          RPCReplay.Options(file, schema, 3000),
          _ =>
            RPCReplay.Target(peer.endpoint.port, () => { closed.set(true); peer.close() })
        ))
        assert(e.getMessage.contains("frame differs"), e.getMessage); assert(closed.get()); peer.verify()
      }
    }
  }
}
