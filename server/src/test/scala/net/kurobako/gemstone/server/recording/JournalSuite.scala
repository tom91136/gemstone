package net.kurobako.gemstone.server.recording

import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import net.kurobako.gemstone.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.rpc.*
import net.kurobako.gemstone.server.Transport.*
import net.kurobako.gemstone.server.protocol.*
import scala.util.Using

class JournalSuite extends munit.FunSuite {
  import journal.*
  test("archive preserves sequenced frames and normalises client identities") {
    temporary { file =>
      capture(file) { writer =>
        val generic = writer.connection(client)
        for (_ <- 0 until 2) { generic.request(execution()); generic.response(response()) }
        generic.close()
        writer.clientClosed(client)
      }
      Using.resource(Journal.open(file)) { reader =>
        assert(reader.summary.replayable)
        assertEquals(reader.events.size, 6)
        assertEquals(reader.summary.frames.raw, 4)
        assertEquals(
          reader.events.filter(_.kind == Journal.Kind.Request).map(_.client).toSet,
          Set(Journal.RecordedClientId(1L))
        )
        val first = reader.frame(reader.events.head)
        assertEquals(
          first.toSeq,
          Journal.Frames.normalised(
            Journal.Kind.Request,
            execution(),
            Journal.FrameByteSize(4096)
          ).toSeq
        )
        first(0) = 0
        assertEquals(Frame.magic(reader.frame(reader.events.head)), ExecutionRPC.RequestMagic)
      }
      val _ = intercept[IOException](Journal.Writer(file, Journal.Header(profile)))
    }
  }
  test("distinct clients get distinct recorded ids and a reconnecting client keeps its id") {
    temporary { file =>
      val other = ClientId(9L)
      capture(file) { writer =>
        writer.clientClosed(ClientId(7L))
        for (owner <- List(client, client, other)) {
          val connection = writer.connection(owner)
          connection.request(execution()); connection.response(response()); connection.close()
        }
        writer.clientClosed(client)
        writer.clientClosed(other)
      }
      Using.resource(Journal.open(file)) { reader =>
        assert(reader.summary.replayable)
        assertEquals(
          reader.events.filter(_.kind == Journal.Kind.Request).map(_.client.raw).toList,
          List(1L, 1L, 2L)
        )
        assertEquals(reader.events.filter(_.kind == Journal.Kind.ClientClose).map(_.client.raw).toList, List(1L, 2L))
      }
    }
  }

  test("execution profile survives the archive and reaches the fresh endpoint factory") {
    temporary { file =>
      val profile = Journal.ExecutionProfile(2000000000L, 8)
      val writer  = Journal.Writer(file, Journal.Header(profile))
      writer.close()
      val observed = AtomicReference(Option.empty[Journal.ExecutionProfile])
      val closed   = AtomicBoolean(false)
      assertEquals(
        Replay.run(
          Replay.Options(file),
          header => {
            observed.set(Some(header.executionProfile))
            Replay.Target(Endpoint.Tcp("127.0.0.1", Port(1)), () => closed.set(true))
          }
        ).events.raw,
        0
      )
      assertEquals(observed.get(), Some(profile)); assert(closed.get())
    }
  }

  test("truncated and corrupt archives fail instead of replaying a prefix") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(client); c.request(execution()); c.response(response()); c.close()
      }
      val original = Files.readAllBytes(file)
      Using.resource(RandomAccessFile(file.toFile, "rw"))(_.setLength(original.length - 1L))
      val _ = intercept[IOException](Journal.open(file))
      Files.write(file, original)
      val corrupt = original.clone()
      corrupt(corrupt.length / 2) = (corrupt(corrupt.length / 2) ^ 1).toByte
      Files.write(file, corrupt)
      val _ = intercept[IOException](Journal.open(file))
    }
  }
  test("unfinished exchanges and capture limits are explicitly unreplayable") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(client); c.request(execution()); c.close()
      }
      val summary = Journal.inspect(file)
      assert(!summary.replayable)
      assert(summary.reason.exists(_.raw.contains("incomplete")))
      val started = AtomicBoolean(false)
      val _       = intercept[IOException](Replay.run(
        Replay.Options(file),
        _ => { started.set(true); Replay.Target(Endpoint.Tcp("127.0.0.1", Port(1)), () => ()) }
      ))
      assert(!started.get())
    }
    temporary { file =>
      val frame   = execution("Ping", Array.ofDim[Byte](5))
      val maximum = (frame.length - 1).refine[Journal.FrameByteSize]
        .fold(message => fail(message), value => value)
      val writer = Journal.Writer(
        file,
        Journal.Header(profile, Journal.Limits(maxFrameBytes = maximum))
      )
      val connection = writer.connection(client)
      val _          = intercept[IOException](connection.request(frame))
      writer.close()
      assert(!Journal.inspect(file).replayable)
    }
  }
  test("frame bounds include headers and lifecycle ordering is validated") {
    temporary { file =>
      val writer =
        Journal.Writer(file, Journal.Header(profile, Journal.Limits(maxEvents = 2)))
      val c = writer.connection(client)
      c.request(execution()); c.response(response())
      val _ = intercept[IOException](c.close())
      writer.close()
      assert(!Journal.inspect(file).replayable)
    }
    temporary { file =>
      val writer = Journal.Writer(file, Journal.Header(profile))
      val c      = writer.connection(client)
      val _      = intercept[IOException](c.response(response()))
      writer.close()
      assert(!Journal.inspect(file).replayable)
    }
  }
  test("replay rejects a different stored schema before starting a server") {
    temporary { file =>
      Using.resource(Journal.Writer(file, Journal.Header(profile, schema = ExecutionRPC.Schema.canonical + "changed")))(
        _ => ()
      )
      val started = java.util.concurrent.atomic.AtomicBoolean(false)
      val _       = intercept[IOException] {
        Replay.run(
          Replay.Options(file),
          _ => {
            started.set(true)
            throw IllegalStateException("must not start")
          }
        )
      }
      assert(!started.get())
    }
  }

  test("the full schema counts against the archive byte budget before a file is published") {
    temporary { file =>
      val header = Journal.Header(profile, Journal.Limits(maxArchiveBytes = Journal.ArchiveByteSize(5000L)))
      val _      = intercept[IOException](Journal.Writer(file, header))
      assert(!Files.exists(file))
    }
  }

  test("stored requests must carry a normalised client field") {
    val maximum = Journal.Limits().maxFrameBytes
    val frame   = Journal.Frames.normalised(Journal.Kind.Request, execution(), maximum)
    Journal.Frames.validate(kind = Journal.Kind.Request, bytes = frame, maximum = maximum, normalised = true)
    val _ = intercept[IOException] {
      Journal.Frames.validate(kind = Journal.Kind.Response, bytes = frame, maximum = maximum, normalised = true)
    }
    frame(ExecutionRPC.RequestClientOffset.raw) = 1
    Journal.Frames.validate(kind = Journal.Kind.Request, bytes = frame, maximum = maximum, normalised = false)
    val _ = intercept[IOException] {
      Journal.Frames.validate(kind = Journal.Kind.Request, bytes = frame, maximum = maximum, normalised = true)
    }
  }
}
