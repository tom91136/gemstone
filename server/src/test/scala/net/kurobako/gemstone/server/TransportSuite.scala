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
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class TransportSuite extends Suite {

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
    assertEquals(
      Bind.parse("unix:///tmp/a%00b"),
      Left("invalid endpoint 'unix:///tmp/a%00b': Unix endpoint must not contain a NUL character")
    )
  }

  for (unix <- Seq(false, true)) {
    test(s"RPC registration, allocation transfer, finish and revocation work over unix=$unix") {
      files.temporary { directory =>
        val bind = if (unix) Bind.Unix(directory.resolve("socket with space")) else Bind.Tcp()
        Using.Manager { use =>
          val server     = use(Server.start(Server.Settings(parallelism = 1, bind = bind)))
          val id         = Control.register(server.endpoint)
          val client     = use(connections.Client(server.endpoint, id))
          val allocation = client.call(Operation.RegisterAllocation(20000L))
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
        Using.resource(Sockets.connect(server.endpoint, 2000)) { channel =>
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
          Replay.Options(archive),
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
      val _ = intercept[IOException](Sockets.listen(Bind.Unix(path)))
      assertEquals(Files.readString(path), "existing")
      Files.delete(path)
      val listener = Sockets.listen(Bind.Unix(path))
      val moved    = directory.resolve("moved")
      try {
        Files.move(path, moved)
        Files.writeString(path, "replacement")
      } finally {
        listener.close()
        listener.close()
      }
      assertEquals(Files.readString(path), "replacement")
    }
  }
}
