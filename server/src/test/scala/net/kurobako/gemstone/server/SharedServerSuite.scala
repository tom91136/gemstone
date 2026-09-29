package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CyclicBarrier
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.transport.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

/** Socket-level isolation coverage shared by both server implementations. */
class SharedServerSuite extends munit.FunSuite {

  private def control[A](
      endpoint: Endpoint,
      operation: Int,
      client: Option[ClientId] = None
  )(read: DataInputStream => A): A =
    Using.resource(transport.connect(endpoint, 10000)) { socket =>
      val output = DataOutputStream(socket.output)
      Control.writeRequest(output, operation, client)
      val bytes = Control.readResponse(DataInputStream(socket.input), operation)
      read(DataInputStream(java.io.ByteArrayInputStream(bytes)))
    }
  private def register(client: connections.Client, size: Long > 0): AllocationId =
    client.call(Operation.RegisterAllocation(AllocationKind.Shared, size)).fold(
      error => fail(error.message),
      value => value
    )

  private def int(bytes: Vec[Byte]): Int = ByteBuffer.wrap(Vec.toArray(bytes)).getInt()
  test("shared clients cannot access each other's allocations or identify as each other") {
    Using.Manager { use =>
      val server = use(Server.start(Server.Settings(parallelism = 2)))
      val a      = control(server.endpoint, protocol.Control.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val b = control(server.endpoint, protocol.Control.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val first  = use(connections.Client(server.endpoint, a))
      val second = use(connections.Client(server.endpoint, b))
      assert(a != b)
      val firstAllocation = register(first, 4L)
      assert(first.call(Operation.ReadAllocation(
        firstAllocation,
        0L,
        4
      )).isRight)
      assert(second.call(Operation.ReadAllocation(
        firstAllocation,
        0L,
        4
      )).isLeft)
      val secondAllocation = register(second, 4L)
      assert(second.call(Operation.ReadAllocation(
        secondAllocation,
        0L,
        4
      )).isRight)
      assertEquals(control(server.endpoint, protocol.Control.ReleaseClient, Some(a))(_.readInt()), 0)
      assertEquals(
        first.call(Operation.ReadAllocation(firstAllocation, 0L, 4)),
        Left(ExecutionFailure.NotFound("client session was closed"))
      )
      assert(second.call(Operation.ReadAllocation(
        secondAllocation,
        0L,
        4
      )).isRight)
    }.get
  }

  for (count <- Vector[Int > 0](3, 5, 9))
    test(s"$count simultaneous clients retain independent server-issued allocation state") {
      scala.util.Using.Manager { use =>
        val server  = use(Server.start(Server.Settings(parallelism = 2)))
        val clients = Vector.fill(count.raw)(
          use(connections.Client(
            server.endpoint,
            control(server.endpoint, protocol.Control.RegisterClient)(input =>
              ClientId.either(input.readLong()).fold(message => fail(message), value => value)
            )
          ))
        )
        val executor = use(threads.Pool(count.raw)).executor
        val written  = CyclicBarrier(count.raw)
        type RoundResult = (expected: Int, write: Either[ExecutionFailure, Unit], read: Either[ExecutionFailure, Int])
        val results = Vector.newBuilder[CompletableFuture[Either[ExecutionFailure, Vector[RoundResult]]]]
        clients.zipWithIndex.foreach { case (client, index) =>
          results += CompletableFuture.supplyAsync(
            () =>
              client.call(Operation.RegisterAllocation(AllocationKind.Shared, 4L)) match {
                case Left(error)       => Left(error)
                case Right(allocation) => Right(Vector.tabulate(3) { round =>
                    val expected = index * 97 + round
                    val bytes    = Vec.unsafeWrapArray(ByteBuffer.allocate(4).putInt(expected).array())
                    val write    = client.call(Operation.WriteAllocation(allocation, 0L, bytes))
                    val _        = written.await(10, TimeUnit.SECONDS)
                    (
                      expected,
                      write,
                      client.call(Operation.ReadAllocation(
                        allocation,
                        0L,
                        4
                      )).map(int(_))
                    )
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
      val server = use(Server.start(Server.Settings(parallelism = 2)))
      val client = control(server.endpoint, protocol.Control.RegisterClient)(input =>
        ClientId.either(input.readLong()).fold(message => fail(message), value => value)
      )
      val first      = use(connections.Client(server.endpoint, client, 42L, 17L))
      val same       = use(connections.Client(server.endpoint, client, 42L, 17L))
      val reused     = use(connections.Client(server.endpoint, client, 42L, 18L))
      val other      = use(connections.Client(server.endpoint, client, 43L, 17L))
      val allocation = register(first, 4L)
      val value      = Vec.unsafeWrapArray(ByteBuffer.allocate(4).putInt(123).array())
      assert(first.call(Operation.WriteAllocation(allocation, 0L, value)).isRight)
      assertEquals(
        same.call(Operation.ReadAllocation(allocation, 0L, 4)).map(int(_)),
        Right(123)
      )
      assert(reused.call(Operation.ReadAllocation(allocation, 0L, 4)).isLeft)
      assert(other.call(Operation.ReadAllocation(allocation, 0L, 4)).isLeft)
      val reusedAllocation = register(reused, 4L)
      assertEquals(
        reused.call(Operation.ReadAllocation(
          reusedAllocation,
          0L,
          4
        )).map(int(_)),
        Right(0)
      )
      first.close()
      assertEquals(
        same.call(Operation.ReadAllocation(allocation, 0L, 4)).map(int(_)),
        Right(123)
      )
    }.get
  }

}
