package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.*

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.transport.Transport.*

import java.io.{IOException, RandomAccessFile}
import java.nio.file.Files
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import scala.util.Using

final class RPCJournalSuite extends munit.FunSuite {
  import JournalFixtures.*
  test("archive preserves sequenced frames and normalizes client identities") {
    temporary { file =>
      val owner = Object()
      capture(file) { writer =>
        val generic = writer.connection(owner)
        for (_ <- 0 until 2) { generic.request(execution()); generic.response(response()) }
        generic.close()
        writer.clientClosed(owner)
      }
      Using.resource(RPCJournal.open(file)) { reader =>
        assert(reader.summary.replayable)
        assertEquals(reader.events.size, 6)
        assertEquals(reader.summary.frames.raw, 4)
        assertEquals(
          reader.events.filter(_.kind == RPCJournal.Kind.Request).map(_.client).toSet,
          Set(RPCJournal.RecordedClientId(1L))
        )
        val first = reader.frame(reader.events.head)
        assertEquals(
          first.toSeq,
          RPCJournal.Frames.normalized(
            RPCJournal.Kind.Request,
            execution(),
            RPCJournal.FrameByteSize(4096)
          ).toSeq
        )
        first(0) = 0
        assertEquals(RPCJournal.Frames.magic(reader.frame(reader.events.head)), ExecutionRPC.RequestMagic)
      }
      val _ = intercept[IOException](RPCJournal.Writer(file, RPCJournal.Header(schema)))
    }
  }
  test("execution profile survives the archive and reaches the fresh endpoint factory") {
    temporary { file =>
      val profile =
        RPCJournal.ExecutionProfile(PositiveLong(2000000000L), PositiveInt(8192), PositiveInt(8))
      val writer = RPCJournal.Writer(file, RPCJournal.Header(schema, executionProfile = Some(profile)))
      writer.close()
      val observed = AtomicReference(Option.empty[RPCJournal.ExecutionProfile])
      val closed   = AtomicBoolean(false)
      assertEquals(
        RPCReplay.run(
          RPCReplay.Options(file, schema),
          header => {
            observed.set(header.executionProfile); RPCReplay.Target(RPCPort(1), () => closed.set(true))
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
        val c = w.connection(Object()); c.request(execution()); c.response(response()); c.close()
      }
      val original = Files.readAllBytes(file)
      Using.resource(RandomAccessFile(file.toFile, "rw"))(_.setLength(original.length - 1L))
      val _ = intercept[IOException](RPCJournal.open(file))
      Files.write(file, original)
      val corrupt = original.clone()
      corrupt(corrupt.length / 2) = (corrupt(corrupt.length / 2) ^ 1).toByte
      Files.write(file, corrupt)
      val _ = intercept[IOException](RPCJournal.open(file))
    }
  }
  test("unfinished exchanges and capture limits are explicitly unreplayable") {
    temporary { file =>
      capture(file) { w =>
        val c = w.connection(Object()); c.request(execution()); c.close()
      }
      val summary = RPCJournal.inspect(file)
      assert(!summary.replayable)
      assert(summary.reason.exists(_.raw.contains("incomplete")))
      val started = AtomicBoolean(false)
      val _       = intercept[IOException](RPCReplay.run(
        RPCReplay.Options(file, schema),
        _ => { started.set(true); RPCReplay.Target(RPCPort(1), () => ()) }
      ))
      assert(!started.get())
    }
    temporary { file =>
      val frame   = execution(3, Array.ofDim[Byte](5))
      val maximum = RPCJournal.FrameByteSize.either(frame.length - 1)
        .fold(message => fail(message), value => value)
      val writer = RPCJournal.Writer(
        file,
        RPCJournal.Header(schema, RPCJournal.Limits(maxFrameBytes = maximum))
      )
      val connection = writer.connection(Object())
      val _          = intercept[IOException](connection.request(frame))
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
  }
  test("frame bounds include headers and lifecycle ordering is validated") {
    temporary { file =>
      val writer =
        RPCJournal.Writer(file, RPCJournal.Header(schema, RPCJournal.Limits(maxEvents = PositiveInt(2))))
      val c = writer.connection(Object())
      c.request(execution()); c.response(response())
      val _ = intercept[IOException](c.close())
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
    temporary { file =>
      val writer = RPCJournal.Writer(file, RPCJournal.Header(schema))
      val c      = writer.connection(Object())
      val _      = intercept[IOException](c.response(response()))
      writer.close()
      assert(!RPCJournal.inspect(file).replayable)
    }
  }
}
