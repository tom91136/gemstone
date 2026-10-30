package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class UnixTransportSuite extends Suite {
  private def withPair(timeout: Int > 0 = 5000)(body: (Channel, Channel) => Unit): Unit = files.temporary { directory =>
    Using.Manager { use =>
      val path     = directory.resolve("transport.sock")
      val listener = use(UnixTransport.listen(path))
      val client   = use(UnixTransport.connect(path, timeout))
      val peer     = use(listener.accept())
      body(client, peer)
    }.get
  }

  private def pending[A](future: Future[A]): Unit = {
    val _ = intercept[TimeoutException](future.get(100, TimeUnit.MILLISECONDS))
  }

  private def closed[A](future: Future[A]): Unit = {
    val error = intercept[ExecutionException](future.get(5, TimeUnit.SECONDS))
    error.getCause match {
      case _: IOException => ()
      case other          => fail(s"expected an I/O failure after close, got $other")
    }
  }

  test("read deadlines preserve the connection and later readiness notifications") {
    withPair() { (client, peer) =>
      client.readTimeout(20)
      for (value <- 1 to 4) {
        val _ = intercept[SocketTimeoutException](client.input.read())
        peer.output.write(value)
        assertEquals(client.input.read(), value)
      }
      assert(!client.isClosed)
    }
  }

  test("zero read timeout waits while idle, then permits reuse and close interrupts the next wait") {
    withPair() { (client, peer) =>
      client.readTimeout(0)
      Using.resource(threads.Pool(1)) { pool =>
        try {
          val read: Callable[Int] = () => client.input.read()
          val first               = pool.executor.submit(read)
          pending(first)
          peer.output.write(123)
          assertEquals(first.get(5, TimeUnit.SECONDS), 123)
          val second = pool.executor.submit(read)
          pending(second)
          client.close()
          client.close()
          closed(second)
          assert(client.isClosed)
        } finally client.close()
      }
    }
  }

  test("close interrupts independent blocked read and backpressured write operations") {
    withPair() { (client, _) =>
      client.readTimeout(0)
      Using.resource(threads.Pool(2)) { pool =>
        try {
          val started               = CountDownLatch(2)
          val read: Callable[Int]   = () => { started.countDown(); client.input.read() }
          val write: Callable[Unit] = () => {
            started.countDown()
            client.output.write(Array.ofDim[Byte](2 * 1024 * 1024))
          }
          val reader = pool.executor.submit(read)
          val writer = pool.executor.submit(write)
          assert(started.await(5, TimeUnit.SECONDS))
          pending(reader)
          pending(writer)
          client.close()
          closed(reader)
          closed(writer)
        } finally client.close()
      }
    }
  }

  test("simultaneous reads and writes preserve both payloads after backpressure") {
    withPair() { (client, peer) =>
      Using.resource(threads.Pool(4)) { pool =>
        try {
          val clientBytes                = Array.tabulate[Byte](2 * 1024 * 1024)(index => (index * 13).toByte)
          val peerBytes                  = Array.tabulate[Byte](clientBytes.length)(index => (index * 29 + 7).toByte)
          val started                    = CountDownLatch(2)
          val sendClient: Callable[Unit] = () => { started.countDown(); client.output.write(clientBytes) }
          val sendPeer: Callable[Unit]   = () => { started.countDown(); peer.output.write(peerBytes) }
          val clientWriter               = pool.executor.submit(sendClient)
          val peerWriter                 = pool.executor.submit(sendPeer)
          assert(started.await(5, TimeUnit.SECONDS))
          pending(clientWriter)
          pending(peerWriter)
          val receiveClient: Callable[Array[Byte]] = () => {
            val result = Array.ofDim[Byte](peerBytes.length)
            DataInputStream(client.input).readFully(result)
            result
          }
          val receivePeer: Callable[Array[Byte]] = () => {
            val result = Array.ofDim[Byte](clientBytes.length)
            DataInputStream(peer.input).readFully(result)
            result
          }
          val clientReader = pool.executor.submit(receiveClient)
          val peerReader   = pool.executor.submit(receivePeer)
          clientWriter.get(5, TimeUnit.SECONDS)
          peerWriter.get(5, TimeUnit.SECONDS)
          assertEquals(java.util.Arrays.mismatch(clientReader.get(5, TimeUnit.SECONDS), peerBytes), -1)
          assertEquals(java.util.Arrays.mismatch(peerReader.get(5, TimeUnit.SECONDS), clientBytes), -1)
        } finally { client.close(); peer.close() }
      }
    }
  }

  test("a backpressured write respects its finite deadline") {
    withPair(timeout = 30) { (client, _) =>
      val _ = intercept[SocketTimeoutException](client.output.write(Array.ofDim[Byte](2 * 1024 * 1024)))
      assert(!client.isClosed)
    }
  }

  test("failed connects do not prevent a subsequent listener and connection") {
    files.temporary { directory =>
      val path = directory.resolve("missing.sock")
      for (_ <- 0 until 4) {
        val _ = intercept[IOException](UnixTransport.connect(path, 1000))
      }
      Using.Manager { use =>
        val listener = use(UnixTransport.listen(path))
        val client   = use(UnixTransport.connect(path, 1000))
        val peer     = use(listener.accept())
        peer.output.write(17)
        assertEquals(client.input.read(), 17)
      }.get
    }
  }
}
