package net.kurobako.gemstone.server

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicInteger
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.rpc.ExecutionRPC.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.recording.*
import net.kurobako.gemstone.testing.*
import scala.util.Using

class ConnectionSuite extends Suite {
  private def frame: Array[Byte] = {
    val (operation, payload) =
      Wire.Protocol[Operation].encodePayload(Operation.EnableHostMemory, ExecutionRPC.Limits).toOption.get
    val bytes  = ByteArrayOutputStream()
    val output = DataOutputStream(bytes)
    val schema = ExecutionRPC.Schema.canonical.getBytes(StandardCharsets.UTF_8)
    output.writeInt(ExecutionRPC.HelloMagic)
    output.writeInt(schema.length)
    output.write(schema)
    output.write(connections.requestFrame(client = 1L, process = 1L, incarnation = 1L, operation, payload))
    bytes.toByteArray
  }

  private class StreamsChannel(val input: InputStream, val output: OutputStream) extends Channel {
    def readTimeout(value: Int >= 0): Unit = ()
    def isClosed: Boolean                  = false
    def close(): Unit                      = ()
  }

  for (recording <- List(false, true))
    test(s"a response write failure closes the connection without attempting a second response, recording=$recording") {
      val attempts = AtomicInteger()
      class FailingOutput extends OutputStream {
        private val helloBytes               = AtomicInteger()
        override def write(value: Int): Unit =
          if (helloBytes.incrementAndGet() > 8) { attempts.incrementAndGet(); throw IOException("broken output") }
      }
      val output = FailingOutput()
      files.temporary { directory =>
        val archive = directory.resolve("capture.msgpack")
        Using.resource(Journal.Writer(archive, Journal.Header(Journal.ExecutionProfile(1000L, 1)))) {
          writer =>
            Using.resource(Clients(() => ExecutionSession(Nil), client => writer.clientClosed(client))) { clients =>
              Connection.serve(
                StreamsChannel(ByteArrayInputStream(frame), output),
                clients,
                1000,
                Option.when(recording)(writer),
                () => ()
              )
              assertEquals(attempts.get(), 1)
            }
        }
        if (recording) Using.resource(Journal.open(archive)) { reader =>
          assert(!reader.summary.replayable)
          assertEquals(reader.events.count(_.kind == Journal.Kind.Response), 0)
        }
      }
    }

  test("a session factory failure is an execution failure, not malformed input") {
    val output = ByteArrayOutputStream()
    Using.resource(Clients(() => throw IllegalStateException("factory failed"))) { clients =>
      Connection.serve(StreamsChannel(ByteArrayInputStream(frame), output), clients, 1000, None, () => ())
    }
    val input = DataInputStream(ByteArrayInputStream(output.toByteArray))
    assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
    assertEquals(input.readInt(), 0)
    assertEquals(input.readInt(), ExecutionRPC.ResponseMagic)
    assertEquals(input.readInt(), 1)
    val error = summon[Wire.Codec[ExecutionFailure]].decode(input.readNBytes(input.readInt()), ExecutionRPC.Limits)
    assertEquals(error, Right(ExecutionFailure.Failed("factory failed")))
    assertEquals(input.read(), -1)
  }

  for (unix <- Seq(false, true)) test(s"idle connections survive but incomplete frames time out unix=$unix") {
    files.temporary { directory =>
      val bind = if (unix) Bind.Unix(directory.resolve("socket")) else Bind.Tcp()
      Using.resource(Server.start(Server.Settings(parallelism = 1, requestTimeoutMillis = 100, bind = bind))) { server =>
        Using.resource(Sockets.connect(server.endpoint, 5000)) { socket =>
          Thread.sleep(200)
          socket.output.write(frame)
          socket.output.flush()
          val input = DataInputStream(socket.input)
          assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
          assertEquals(input.readInt(), 0)
          assertEquals(input.readInt(), ExecutionRPC.ResponseMagic)
          assertEquals(input.readInt(), 0)
          input.readNBytes(input.readInt())
          socket.output.write(0x47)
          socket.output.flush()
          assertEquals(input.read(), -1)
        }
      }
    }
  }

  test("the first accepted request binds identity even when its session construction fails") {
    val (operation, payload) =
      Wire.Protocol[Operation].encodePayload(Operation.EnableHostMemory, ExecutionRPC.Limits).toOption.get
    val factories = AtomicInteger()
    val output    = ByteArrayOutputStream()
    Using.resource(Clients { () =>
      if (factories.incrementAndGet() == 1) throw IllegalStateException("factory failed")
      ExecutionSession(Nil)
    }) { clients =>
      Connection.serve(
        StreamsChannel(
          ByteArrayInputStream(frame ++
            connections.requestFrame(client = 1L, process = 2L, incarnation = 1L, operation, payload)),
          output
        ),
        clients,
        1000,
        None,
        () => ()
      )
    }
    val input = DataInputStream(ByteArrayInputStream(output.toByteArray))
    assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
    assertEquals(input.readInt(), 0)
    val failures = List.fill(2) {
      assertEquals(input.readInt(), ExecutionRPC.ResponseMagic)
      assertEquals(input.readInt(), 1)
      summon[Wire.Codec[ExecutionFailure]].decode(input.readNBytes(input.readInt()), ExecutionRPC.Limits)
    }
    assertEquals(
      failures,
      List(
        Right(ExecutionFailure.Failed("factory failed")),
        Right(ExecutionFailure.InvalidRequest("a connection cannot change client or process identity"))
      )
    )
    assertEquals(factories.get(), 1)
  }

  test("recording retains the request and response when session construction fails") {
    files.temporary { directory =>
      val archive = directory.resolve("capture.msgpack")
      Using.resource(Journal.Writer(archive, Journal.Header(Journal.ExecutionProfile(1000L, 1)))) { writer =>
        Using.resource(Clients(
          () => throw IllegalStateException("factory failed"),
          client => writer.clientClosed(client)
        )) { clients =>
          Connection.serve(
            StreamsChannel(ByteArrayInputStream(frame), ByteArrayOutputStream()),
            clients,
            1000,
            Some(writer),
            () => ()
          )
        }
      }
      Using.resource(Journal.open(archive)) { reader =>
        assert(reader.summary.replayable)
        assertEquals(reader.summary.frames.raw, 2)
        assertEquals(reader.events.count(_.kind == Journal.Kind.Request), 1)
        assertEquals(reader.events.count(_.kind == Journal.Kind.Response), 1)
      }
    }
  }
  private def helloFrame(schema: String): Array[Byte] = {
    val bytes = schema.getBytes(StandardCharsets.UTF_8)
    ByteBuffer.allocate(
      8 + bytes.length
    ).putInt(ExecutionRPC.HelloMagic).putInt(bytes.length).put(bytes).array()
  }

  private def serveBytes(bytes: Array[Byte], factories: AtomicInteger): Array[Byte] = {
    val output = ByteArrayOutputStream()
    Using.resource(Clients { () => factories.incrementAndGet(); ExecutionSession(Nil) }) { clients =>
      Connection.serve(StreamsChannel(ByteArrayInputStream(bytes), output), clients, 1000, None, () => ())
    }
    output.toByteArray
  }

  test("schema mismatch is rejected before a session can be created") {
    val factories = AtomicInteger()
    // Same operations, different field representation.
    val changed = ExecutionRPC.Schema.canonical.replace("i64", "i32")
    assertNotEquals(changed, ExecutionRPC.Schema.canonical)
    val result = serveBytes(helloFrame(changed) ++ frame, factories)
    val input  = DataInputStream(ByteArrayInputStream(result))
    assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
    assertEquals(input.readInt(), 1)
    assertEquals(input.read(), -1)
    assertEquals(factories.get(), 0)
  }

  test("schema negotiation is required once and cannot be repeated") {
    val factories = AtomicInteger()
    val hello     = helloFrame(ExecutionRPC.Schema.canonical)
    assertEquals(serveBytes(frame.drop(hello.length), factories).length, 0)
    assertEquals(serveBytes(hello ++ hello, factories).length, 8)
    assertEquals(factories.get(), 0)
  }

  test("one schema negotiation permits multiple ordered requests") {
    val factories = AtomicInteger()
    val hello     = helloFrame(ExecutionRPC.Schema.canonical)
    val response  = serveBytes(frame ++ frame.drop(hello.length), factories)
    val input     = DataInputStream(ByteArrayInputStream(response))
    assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
    assertEquals(input.readInt(), 0)
    for (_ <- 0 until 2) {
      assertEquals(input.readInt(), ExecutionRPC.ResponseMagic)
      assertEquals(input.readInt(), 0)
      assertEquals(input.readNBytes(input.readInt()).toVector, Vector(0x90.toByte))
    }
    assertEquals(input.read(), -1)
    assertEquals(factories.get(), 1)
  }

  test("schema lengths are bounded before reading their bytes") {
    val factories = AtomicInteger()
    for (size <- Seq(-1, 0, ExecutionRPC.MaximumSchemaBytes.raw + 1)) {
      val prefix = ByteBuffer.allocate(8).putInt(ExecutionRPC.HelloMagic).putInt(size).array()
      assertEquals(serveBytes(prefix, factories).length, 0)
    }
    assertEquals(factories.get(), 0)
  }

  test("unknown string tags fail without executing an operation") {
    val factories = AtomicInteger()
    val request   = frame.clone()
    val tagOffset = helloFrame(ExecutionRPC.Schema.canonical).length + ExecutionRPC.RequestHeaderBytes
    request(tagOffset) = 'X'.toByte
    val input = DataInputStream(ByteArrayInputStream(serveBytes(request, factories)))
    assertEquals(input.readInt(), ExecutionRPC.HelloResponseMagic)
    assertEquals(input.readInt(), 0)
    assertEquals(input.readInt(), ExecutionRPC.ResponseMagic)
    assertEquals(input.readInt(), 1)
    val error = summon[Wire.Codec[ExecutionFailure]].decode(input.readNBytes(input.readInt()), ExecutionRPC.Limits)
    assert(error.exists {
      case ExecutionFailure.InvalidRequest(message) => message.contains("unknown Operation operation tag")
      case _                                        => false
    })
    assertEquals(input.read(), -1)
  }

  test("schema negotiation does not consume the recorded request frame budget") {
    files.temporary { directory =>
      val archive = directory.resolve("capture.msgpack")
      val header  = Journal.Header(
        Journal.ExecutionProfile(1000L, 1),
        Journal.Limits(maxFrameBytes = Journal.FrameByteSize(128))
      )
      Using.resource(Journal.Writer(archive, header)) { writer =>
        Using.resource(Clients(() => ExecutionSession(Nil), client => writer.clientClosed(client))) { clients =>
          Connection.serve(
            StreamsChannel(ByteArrayInputStream(frame), ByteArrayOutputStream()),
            clients,
            1000,
            Some(writer),
            () => ()
          )
        }
      }
      Using.resource(Journal.open(archive)) { reader =>
        assert(reader.summary.replayable)
        assertEquals(reader.summary.frames.raw, 2)
        assertEquals(reader.summary.header.schema, ExecutionRPC.Schema.canonical)
      }
    }
  }

}
