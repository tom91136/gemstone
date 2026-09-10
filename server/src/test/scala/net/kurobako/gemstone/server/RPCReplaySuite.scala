package net.kurobako.gemstone.server

import java.io.{DataInputStream, DataOutputStream, EOFException, IOException}
import java.net.{InetAddress, ServerSocket, Socket, SocketException}
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.{CountDownLatch, TimeUnit}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicInteger, AtomicReference}

final class RPCReplaySuite extends munit.FunSuite {
  import JournalFixtures.*
  private final class Peer(handle: (Array[Byte], DataInputStream, DataOutputStream) => Unit) extends AutoCloseable {
    private val server   = ServerSocket(0, 32, InetAddress.getLoopbackAddress)
    private val stopping = AtomicBoolean(false)
    private val error    = AtomicReference(Option.empty[Throwable])
    private val sockets  = java.util.concurrent.ConcurrentHashMap.newKeySet[Socket]()
    private val threads  = java.util.concurrent.ConcurrentHashMap.newKeySet[Thread]()
    val registered       = AtomicInteger()
    val released         = AtomicInteger()
    val tokens           = java.util.concurrent.ConcurrentHashMap.newKeySet[String]()
    private val acceptor = Thread(() =>
      while (!stopping.get())
        try {
          val socket = server.accept(); socket.setSoTimeout(3000); sockets.add(socket)
          val thread = Thread(() =>
            try serve(socket)
            catch {
              case _: EOFException => (); case _: SocketException if stopping.get() => ();
              case e: Throwable    => val _ = error.compareAndSet(None, Some(e))
            } finally { sockets.remove(socket); socket.close() }
          )
          threads.add(thread); thread.start()
        } catch { case _: SocketException if stopping.get() => () }
    )
    acceptor.start()
    def endpoint: RPCReplay.Endpoint        = RPCReplay.Endpoint(server.getLocalPort, schema, () => close())
    private def serve(socket: Socket): Unit = {
      val input  = DataInputStream(socket.getInputStream); val output = DataOutputStream(socket.getOutputStream)
      var active = true
      while (active) {
        val magic = input.readInt()
        if (magic == 0x474d4330) {
          val master = Array.ofDim[Byte](64); input.readFully(master)
          assertEquals(String(master, StandardCharsets.US_ASCII), schema)
          input.readInt() match {
            case 1 => output.writeUTF(f"${registered.incrementAndGet()}%064x")
            case 3 =>
              val token = Array.ofDim[Byte](64); input.readFully(token); released.incrementAndGet();
              output.writeInt(0)
            case value => throw IOException(s"bad control operation $value")
          }
          output.flush(); active = false
        } else {
          val header = if (magic == RPCFrames.ExecutionRequest) 96 else 184
          val prefix = Array.ofDim[Byte](header); ByteBuffer.wrap(prefix).putInt(magic)
          input.readFully(prefix, 4, header - 4)
          val extra = if (magic == RPCFrames.ExecutionRequest) RPCFrames.integer(prefix, 92)
          else RPCFrames.integer(prefix, 176) + RPCFrames.integer(prefix, 180)
          assert(extra >= 0 && extra <= 4096)
          val frame  = java.util.Arrays.copyOf(prefix, header + extra); input.readFully(frame, header, extra)
          val offset = RPCFrames.tokenOffset(frame)
          val token  = String(frame.slice(offset, offset + 64), StandardCharsets.US_ASCII)
          assert(token != credential); tokens.add(token)
          handle(frame, input, output); output.flush()
        }
      }
    }
    def verify(): Unit         = error.get().foreach(e => throw AssertionError("scripted replay peer failed", e))
    override def close(): Unit =
      if (stopping.compareAndSet(false, true)) {
        server.close()
        val iterator = sockets.iterator(); while (iterator.hasNext) iterator.next().close()
        acceptor.join(4000)
        val workers = threads.iterator();
        while (workers.hasNext) { val thread = workers.next(); thread.join(4000); assert(!thread.isAlive) }
        assert(!acceptor.isAlive)
      }
  }

  test("replay preserves multiple live connections, surrogate clients and response order") {
    temporary { file =>
      capture(file) { w =>
        val a = w.connection(Object()); val b = w.connection(Object())
        a.request(execution(0, session = 77)); b.request(execution(0, session = 77))
        b.response(response()); a.response(response()); a.close(); b.close()
      }
      val arrivals = CountDownLatch(2)
      val peer     = Peer { (request, _, out) =>
        assertEquals(
          RPCFrames.normalized(RPCJournal.Kind.Request, request, 4096).toSeq,
          RPCFrames.normalized(RPCJournal.Kind.Request, execution(0, session = 77), 4096).toSeq
        )
        arrivals.countDown(); val arrived = arrivals.await(2, TimeUnit.SECONDS); assert(arrived); out.write(response())
      }
      try {
        val result = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(result.requests, 2); assertEquals(peer.registered.get(), 2); assertEquals(peer.tokens.size(), 2)
        peer.verify()
      } finally peer.close()
    }
  }

  test("recorded callbacks and nested RPCs replay without invoking host callback code") {
    temporary { file =>
      val owner = Object()
      capture(file) { w =>
        val outer = w.connection(owner); val nested = w.connection(owner)
        outer.request(cuda); outer.callbackRequest(callback)
        nested.request(execution()); nested.response(response())
        outer.callbackReply(callbackReply); outer.response(response(magic = RPCFrames.CUDAResponse))
        nested.close(); outer.close(); w.clientClosed(owner)
      }
      val nestedCalls = AtomicInteger()
      val replies     = AtomicInteger()
      val peer        = Peer((request, input, out) =>
        if (RPCFrames.magic(request) == RPCFrames.CUDARequest) {
          out.write(callback); out.flush()
          val received = Array.ofDim[Byte](callbackReply.length); input.readFully(received)
          assertEquals(received.toSeq, callbackReply.toSeq); replies.incrementAndGet()
          assertEquals(nestedCalls.get(), 1)
          out.write(response(magic = RPCFrames.CUDAResponse))
        } else { nestedCalls.incrementAndGet(); out.write(response()) }
      )
      try {
        val session = RPCReplay.open(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        try {
          assertEquals(session.next.map(_.kind), Some(RPCJournal.Kind.Request))
          assertEquals(session.step().map(_.sequence), Some(0L))
          assertEquals(session.run().callbacks, 1)
        } finally session.close()
        assertEquals(replies.get(), 1); assertEquals(peer.registered.get(), 1); assertEquals(peer.released.get(), 1)
        peer.verify()
      } finally peer.close()
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
      try {
        val result = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(result.extraCompletionPolls, if (early) 0L else 2L)
        peer.verify()
      } finally peer.close()
    }
  }

  test("pending timing does not hide failures and terminal waits are bounded") {
    for (failed <- Seq(false, true)) temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object())
        c.request(query); c.response(completion(1))
        c.request(query); c.response(completion(2)); c.close()
      }
      val peer = Peer((_, _, out) => out.write(if (failed) response(4) else completion(1)))
      try {
        val error = intercept[IOException](RPCReplay.run(RPCReplay.Options(file, schema, 1000), _ => peer.endpoint))
        assert(error.getMessage.contains(if (failed) "frame differs" else "completion remained pending"))
        peer.verify()
      } finally peer.close()
    }
  }

  test("recorded terminal failures wait through pending replies and compare the actual status") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(query); c.response(response(4)); c.close()
      }
      val calls = AtomicInteger()
      val peer  = Peer((_, _, out) => out.write(if (calls.incrementAndGet() < 3) completion(1) else response(4)))
      try {
        val report = RPCReplay.run(RPCReplay.Options(file, schema, 3000), _ => peer.endpoint)
        assertEquals(report.extraCompletionPolls, 2L)
        peer.verify()
      } finally peer.close()
    }
  }

  test("real response mismatches fail and close the fresh endpoint") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(execution()); c.response(response()); c.close()
      }
      val closed = AtomicBoolean(false)
      val peer   = Peer((_, _, out) => out.write(response(1)))
      try {
        val e = intercept[IOException](RPCReplay.run(
          RPCReplay.Options(file, schema, 3000),
          _ =>
            RPCReplay.Endpoint(peer.endpoint.port, schema, () => { closed.set(true); peer.close() })
        ))
        assert(e.getMessage.contains("frame differs")); assert(closed.get()); peer.verify()
      } finally peer.close()
    }
  }
}
