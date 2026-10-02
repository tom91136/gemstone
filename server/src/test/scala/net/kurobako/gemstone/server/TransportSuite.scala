package net.kurobako.gemstone.server

import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import scala.util.Using

class TransportSuite extends munit.FunSuite {

  test("endpoints preserve escaped Unix paths and distinguish bind port zero from a destination") {
    val endpoint = Endpoint.Unix(Path.of("/tmp/socket with # and % and λ"))
    assertEquals(Endpoint.parse(endpoint.toString), Right(endpoint))
    assert(Bind.parse("tcp://127.0.0.1:0").isRight)
    assert(Endpoint.parse("tcp://127.0.0.1:0").isLeft)
    for (
      value <- Seq(
        "unix:relative",
        "unix://host/tmp/socket",
        "unix:///tmp/a%00b",
        "tcp://host:1/path",
        "tcp://user@host:1",
        "tcp://host:65536",
        "tcp://host:1?x",
        "ws://host:1"
      )
    )
      assert(Endpoint.parse(value).isLeft, value)
  }

  for (unix <- Seq(false, true)) {
    test(s"RPC registration, allocation transfer, finish and revocation work over unix=$unix") {
      files.temporary { directory =>
        val bind = if (unix) Bind.Unix(directory.resolve("socket with space")) else Bind.Tcp()
        Using.Manager { use =>
          val server     = use(Server.start(Server.Settings(parallelism = 1, bind = bind)))
          val id         = Control.register(server.endpoint)
          val client     = use(connections.Client(server.endpoint, id))
          val allocation = client.call(Operation.RegisterAllocation(AllocationKind.Shared, 20000L))
            .fold(error => fail(error.message), identity)
          val bytes = Vec.from(Array.tabulate[Byte](20000)(index => (index * 13).toByte))
          assertEquals(client.call(Operation.WriteAllocation(allocation, 0L, bytes)), Right(()))
          assertEquals(client.call(Operation.ReadAllocation(allocation, 0L, 20000)), Right(bytes))
          client.finish()
          val next = use(connections.Client(server.endpoint, id))
          assert(
            next.call(Operation.ReadAllocation(allocation, 0L, 1)).isLeft,
            "finish must release the previous session before acknowledgement"
          )
          assertEquals(next.call(Operation.Ping), Right(()))
          Control.release(server.endpoint, id)
          assert(next.call(Operation.Ping).isLeft, "release revokes an already connected client")
        }.get
        if (unix) assert(!Files.exists(directory.resolve("socket with space")))
      }
    }

    test(s"server shutdown interrupts an idle connection over unix=$unix") {
      files.temporary { directory =>
        val bind   = if (unix) Bind.Unix(directory.resolve("socket")) else Bind.Tcp()
        val server = Server.start(Server.Settings(parallelism = 1, bind = bind))
        Using.resource(SocketTransport.connect(server.endpoint, 2000)) { channel =>
          try {
            // A completed handshake proves the server accepted this connection before shutdown.
            protocol.hello(DataInputStream(channel.input), DataOutputStream(channel.output))
            val closed = CompletableFuture.runAsync(() => server.close())
            closed.get(5L, TimeUnit.SECONDS)
            assertEquals(channel.input.read(), -1)
          } finally server.close()
        }
      }
    }

    test(s"recording replays across transports from unix=$unix") {
      files.temporary { directory =>
        val archive = directory.resolve("capture.rpc")
        val bind    = if (unix) Bind.Unix(directory.resolve("record.sock")) else Bind.Tcp()
        Using.resource(Server.start(Server.Settings(parallelism = 1, bind = bind, record = Some(archive)))) { server =>
          Using.resource(connections.Client(server.endpoint, server.client)) { client =>
            assertEquals(client.call(Operation.Ping), Right(()))
            client.finish()
          }
        }
        val target = if (unix) Bind.Tcp() else Bind.Unix(directory.resolve("replay.sock"))
        val report = Replay.run(
          Replay.Options(archive, Journal.ProtocolIdentity(ExecutionRPC.Identity)),
          _ => {
            val server = Server.start(Server.Settings(parallelism = 1, bind = target))
            Replay.Target(server.endpoint, () => server.close())
          }
        )
        assertEquals(report.requests.raw, 1)
        assert(Journal.inspect(archive).replayable)
      }
    }
  }

  test("Unix listeners preserve existing paths, including paths replaced after binding") {
    files.temporary { directory =>
      val path = directory.resolve("socket")
      Files.writeString(path, "existing")
      val _ = intercept[IOException](SocketTransport.listen(Bind.Unix(path)))
      assertEquals(Files.readString(path), "existing")
      Files.delete(path)
      val listener = SocketTransport.listen(Bind.Unix(path))
      val moved    = directory.resolve("moved")
      Files.move(path, moved)
      Files.writeString(path, "replacement")
      listener.close()
      listener.close()
      assertEquals(Files.readString(path), "replacement")
    }
  }

  test("transport preserves ping and allocation write/read/release results") {
    Using.Manager { use =>
      val server = use(Server.start(Server.Settings(parallelism = 2)))
      val client = use(connections.Client(server.endpoint, server.client))
      assertEquals(client.call(Operation.Ping), Right(()))
      val allocation = client.call(Operation.RegisterAllocation(AllocationKind.Shared, 8L))
        .fold(error => fail(error.message), identity)
      assertEquals(client.call(Operation.WriteAllocation(allocation, 2L, Vec[Byte](11, 22, 33))), Right(()))
      assertEquals(
        client.call(Operation.ReadAllocation(allocation, 0L, 8)),
        Right(Vec[Byte](0, 0, 11, 22, 33, 0, 0, 0))
      )
      assertEquals(client.call(Operation.ReleaseAllocation(allocation)), Right(()))
      client.call(Operation.ReadAllocation(allocation, 0L, 1)) match {
        case Left(ExecutionFailure.NotFound(_)) => ()
        case other                              => fail(s"expected released allocation failure, found $other")
      }
    }.get
  }

  test("a server with the default backends rejects an unregistered program encoding") {
    Using.Manager { use =>
      val server = use(Server.start(Server.Settings(parallelism = 2)))
      val client = use(connections.Client(server.endpoint, server.client))
      assertEquals(
        client.call(Operation.LoadProgram("not-registered", Vec.empty)),
        Left(ExecutionFailure.Unsupported("unknown program encoding: not-registered"))
      )
    }.get
  }

}
