package net.kurobako.gemstone.server

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.rpc.ExecutionProtocol.{AllocationId, ExecutionFailure}
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.server.transport.{ClientId, ControlProtocol, RPCPort}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executors
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import scala.util.Using

/** Socket-level isolation coverage shared by both server implementations. */
final class SharedServerSuite extends munit.FunSuite {
  private final class ExecutorPool(size: Int :| Positive) extends AutoCloseable {
    val service                = Executors.newFixedThreadPool(size)
    override def close(): Unit = { val _ = service.shutdownNow() }
  }

  private def control[A](
      port: RPCPort,
      operation: Int,
      client: Option[ClientId] = None
  )(read: DataInputStream => A): A =
    Using.resource(Socket("127.0.0.1", port.value)) { socket =>
      socket.setSoTimeout(10000)
      val output = DataOutputStream(socket.getOutputStream)
      output.writeInt(ControlProtocol.Magic)
      output.writeInt(operation)
      client.foreach(id => output.writeLong(id.value))
      output.flush()
      read(DataInputStream(socket.getInputStream))
    }
  private def register(client: ExecutionRPCTestClient, size: Long :| Positive): AllocationId =
    client.call(Operation.RegisterAllocation(AllocationKind.Shared, size)).fold(
      error => fail(error.message),
      value => value
    )

  private def int(bytes: Vec[Byte]): Int = ByteBuffer.wrap(Vec.toArray(bytes)).getInt()
  test("shared clients cannot access each other's allocations or identify as each other") {
    Using.Manager { use =>
      val server = use(GemstoneRPCServer.start())
      val a      = control(server.port, ControlProtocol.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val b = control(server.port, ControlProtocol.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val first  = use(ExecutionRPCTestClient(server.port, a))
      val second = use(ExecutionRPCTestClient(server.port, b))
      assert(a != b)
      val firstAllocation = register(first, 4L)
      assert(first.call(Operation.ReadAllocation(firstAllocation, 0L, 4)).isRight)
      assertEquals(
        control(server.port, ControlProtocol.QueryUsage, Some(b))(input => input.readLong() -> input.readLong()),
        0L -> 0L
      )
      assert(second.call(Operation.ReadAllocation(firstAllocation, 0L, 4)).isLeft)
      val secondAllocation = register(second, 4L)
      assert(second.call(Operation.ReadAllocation(secondAllocation, 0L, 4)).isRight)
      assertEquals(control(server.port, ControlProtocol.ReleaseClient, Some(a))(_.readInt()), 0)
      val _ = intercept[java.io.EOFException](
        first.call(Operation.ReadAllocation(firstAllocation, 0L, 4))
      )
      assert(second.call(Operation.ReadAllocation(secondAllocation, 0L, 4)).isRight)
    }.get
  }

  for (count <- Vector[Int :| Positive](3, 5, 9))
    test(s"$count simultaneous clients retain independent server-issued allocation state") {
      scala.util.Using.Manager { use =>
        val server  = use(GemstoneRPCServer.start())
        val clients = Vector.fill(count)(
          use(ExecutionRPCTestClient(
            server.port,
            control(server.port, ControlProtocol.RegisterClient)(input =>
              ClientId.either(input.readLong()).fold(message => fail(message), value => value)
            )
          ))
        )
        val executor = use(ExecutorPool(count)).service
        val written  = CyclicBarrier(count)
        type RoundResult = (expected: Int, write: Either[ExecutionFailure, Unit], read: Either[ExecutionFailure, Int])
        val results = Vector.newBuilder[CompletableFuture[Either[ExecutionFailure, Vector[RoundResult]]]]
        clients.zipWithIndex.foreach { case (client, index) =>
          results += CompletableFuture.supplyAsync(
            () =>
              client.call(Operation.RegisterAllocation(AllocationKind.Shared, 4L)) match {
                case Left(error)       => Left(error)
                case Right(allocation) => Right(Vector.tabulate(3) { round =>
                    val expected = index * 97 + round
                    val bytes    = Vec.unsafeFromArray(ByteBuffer.allocate(4).putInt(expected).array())
                    val write    = client.call(Operation.WriteAllocation(allocation, 0L, bytes))
                    val _        = written.await(10, TimeUnit.SECONDS)
                    (expected, write, client.call(Operation.ReadAllocation(allocation, 0L, 4)).map(int(_)))
                  })
              },
            executor
          )
        }
        val completed = results.result().map(_.get(10, TimeUnit.SECONDS))
        completed.foreach {
          case Left(error)   => fail(error.message)
          case Right(rounds) => rounds.foreach { case (expected, write, read) =>
              assertEquals(write, Right(()))
              assertEquals(read, Right(expected))
            }
        }
      }.get
    }

  test("process incarnation separates reused PIDs while same-process connections share state") {
    scala.util.Using.Manager { use =>
      val server = use(GemstoneRPCServer.start())
      val client = control(server.port, ControlProtocol.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val first      = use(ExecutionRPCTestClient(server.port, client, 42L, 17L))
      val same       = use(ExecutionRPCTestClient(server.port, client, 42L, 17L))
      val reused     = use(ExecutionRPCTestClient(server.port, client, 42L, 18L))
      val other      = use(ExecutionRPCTestClient(server.port, client, 43L, 17L))
      val allocation = register(first, 4L)
      val value      = Vec.unsafeFromArray(ByteBuffer.allocate(4).putInt(123).array())
      assert(first.call(Operation.WriteAllocation(allocation, 0L, value)).isRight)
      assertEquals(same.call(Operation.ReadAllocation(allocation, 0L, 4)).map(int(_)), Right(123))
      assert(reused.call(Operation.ReadAllocation(allocation, 0L, 4)).isLeft)
      assert(other.call(Operation.ReadAllocation(allocation, 0L, 4)).isLeft)
      val reusedAllocation = register(reused, 4L)
      assertEquals(reused.call(Operation.ReadAllocation(reusedAllocation, 0L, 4)).map(int(_)), Right(0))
      first.close()
      assertEquals(same.call(Operation.ReadAllocation(allocation, 0L, 4)).map(int(_)), Right(123))
    }.get
  }

}
