package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit

final class SharedServerSuite extends munit.FunSuite {
  private val master = "a" * 64
  private def control[A](port: Int, operation: Int, token: Option[String] = None)(read: DataInputStream => A): A = {
    val socket = Socket("127.0.0.1", port)
    socket.setSoTimeout(10000)
    try {
      val output = DataOutputStream(socket.getOutputStream)
      output.writeInt(0x474d4330)
      output.write(master.getBytes(StandardCharsets.US_ASCII))
      output.writeInt(operation)
      token.foreach(value => output.write(value.getBytes(StandardCharsets.US_ASCII)))
      output.flush()
      read(DataInputStream(socket.getInputStream))
    } finally socket.close()
  }
  private final class Client(port: Int, token: String, pid: Long = 42L, incarnation: Long = 17L) extends AutoCloseable {
    private val socket = Socket("127.0.0.1", port)
    socket.setSoTimeout(10000)
    private val input                                                     = DataInputStream(socket.getInputStream)
    private val output                                                    = DataOutputStream(socket.getOutputStream)
    def request(operation: Int, payload: Array[Byte]): (Int, Array[Byte]) = {
      output.writeInt(0x474d4531)
      output.writeInt(3)
      output.write(token.getBytes(StandardCharsets.US_ASCII))
      // Identical process IDs and allocation IDs must remain isolated by client credentials.
      output.writeLong(pid)
      output.writeLong(incarnation)
      output.writeInt(operation)
      output.writeInt(payload.length)
      output.write(payload)
      output.flush()
      assertEquals(input.readInt(), 0x474d4631)
      assertEquals(input.readInt(), 3)
      val status  = input.readInt()
      val message = Array.ofDim[Byte](input.readInt())
      val bytes   = Array.ofDim[Byte](input.readInt())
      input.readFully(message)
      input.readFully(bytes)
      status -> bytes
    }
    override def close(): Unit = socket.close()
  }
  test("shared clients cannot access each other's allocations or attest for each other") {
    val server = GemstoneRPCServer.start(master)
    val a      = control(server.port, 1)(_.readUTF())
    val b      = control(server.port, 1)(_.readUTF())
    val first  = Client(server.port, a)
    val second = Client(server.port, b)
    try {
      assert(a != b)
      assertEquals(first.request(1, ByteBuffer.allocate(20).putLong(1L).putInt(1).putLong(4L).array())._1, 0)
      val read = ByteBuffer.allocate(20).putLong(1L).putLong(0L).putInt(4).array()
      assertEquals(first.request(4, read)._1, 0)
      assertEquals(control(server.port, 2, Some(b))(input => input.readLong() -> input.readLong()), 0L -> 0L)
      assert(second.request(4, read)._1 != 0)
      assertEquals(second.request(1, ByteBuffer.allocate(20).putLong(1L).putInt(1).putLong(4L).array())._1, 0)
      assertEquals(second.request(4, read)._1, 0)
      assertEquals(control(server.port, 3, Some(a))(_.readInt()), 0)
      val _ = intercept[java.io.EOFException](first.request(4, read))
      assertEquals(second.request(4, read)._1, 0)
    } finally {
      first.close()
      second.close()
      server.close()
    }
  }

  for (count <- Vector(3, 5, 9))
    test(s"$count simultaneous clients retain independent allocation state with colliding IDs") {
      scala.util.Using.Manager { use =>
        val server   = use(GemstoneRPCServer.start(master))
        val clients  = Vector.fill(count)(use(Client(server.port, control(server.port, 1)(_.readUTF()))))
        val executor = Executors.newFixedThreadPool(count)
        val written  = CyclicBarrier(count)
        try {
          val results = clients.zipWithIndex.map { case (client, index) =>
            CompletableFuture.supplyAsync(
              () => {
                assertEquals(client.request(1, ByteBuffer.allocate(20).putLong(1L).putInt(1).putLong(4L).array())._1, 0)
                (0 until 3).foreach { round =>
                  val expected = index * 97 + round
                  val write    = ByteBuffer.allocate(24).putLong(1L).putLong(0L).putInt(4).putInt(expected).array()
                  assertEquals(client.request(3, write)._1, 0)
                  val _      = written.await(10, TimeUnit.SECONDS)
                  val read   = ByteBuffer.allocate(20).putLong(1L).putLong(0L).putInt(4).array()
                  val result = client.request(4, read)
                  assertEquals(result._1, 0)
                  assertEquals(ByteBuffer.wrap(result._2).getInt(), expected)
                }
                true
              },
              executor
            )
          }
          results.foreach(result => assert(result.get(10, TimeUnit.SECONDS)))
        } finally { val _ = executor.shutdownNow() }
      }.get
    }

  test("process incarnation separates reused PIDs while same-process connections share state") {
    scala.util.Using.Manager { use =>
      val server   = use(GemstoneRPCServer.start(master))
      val token    = control(server.port, 1)(_.readUTF())
      val first    = use(Client(server.port, token, 42L, 17L))
      val same     = use(Client(server.port, token, 42L, 17L))
      val reused   = use(Client(server.port, token, 42L, 18L))
      val other    = use(Client(server.port, token, 43L, 17L))
      val register = ByteBuffer.allocate(20).putLong(1L).putInt(1).putLong(4L).array()
      val read     = ByteBuffer.allocate(20).putLong(1L).putLong(0L).putInt(4).array()
      assertEquals(first.request(1, register)._1, 0)
      assertEquals(
        first.request(3, ByteBuffer.allocate(24).putLong(1L).putLong(0L).putInt(4).putInt(123).array())._1,
        0
      )
      assertEquals(ByteBuffer.wrap(same.request(4, read)._2).getInt(), 123)
      assert(reused.request(4, read)._1 != 0)
      assert(other.request(4, read)._1 != 0)
      assertEquals(reused.request(1, register)._1, 0)
      assertEquals(ByteBuffer.wrap(reused.request(4, read)._2).getInt(), 0)
      first.close()
      assertEquals(ByteBuffer.wrap(same.request(4, read)._2).getInt(), 123)
    }.get
  }

}
