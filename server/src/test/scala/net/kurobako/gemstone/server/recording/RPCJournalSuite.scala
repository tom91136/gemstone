package net.kurobako.gemstone.server.recording

import net.kurobako.gemstone.execution.{CompletionId, CompletionStatus}
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.transport.RPCPort

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{DataInputStream, DataOutputStream, IOException, RandomAccessFile}
import java.nio.ByteBuffer
import java.nio.file.{Files, Path}
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import scala.util.Using

private[server] object JournalFixtures {
  private final class TemporaryJournal(val directory: Path, val file: Path) extends AutoCloseable {
    override def close(): Unit = {
      Using.resource(Files.list(directory))(_.forEach { path =>
        val _ = Files.deleteIfExists(path)
      })
      val _ = Files.deleteIfExists(directory)
    }
  }

  private def temporaryJournal(): TemporaryJournal = {
    val directory = Files.createTempDirectory("gemstone-rpc-journal-")
    TemporaryJournal(directory, directory.resolve("recording.gmj"))
  }

  val schema = RPCJournal.ProtocolIdentity.either(ExecutionRPC.Identity)
    .fold(message => throw IllegalStateException(message), value => value)
  private val ping = Wire.Protocol[Operation].encodePayload(Operation.Ping, ExecutionRPC.Limits)
    .fold(message => throw IllegalArgumentException(message), value => value)._1
  def execution(operation: Long = ping, payload: Array[Byte] = Array.emptyByteArray, session: Long = 42): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.RequestHeaderBytes + payload.length
    ).putInt(RPCFrames.ExecutionRequest).putLong(ExecutionRPC.Schema.fingerprint)
      .putLong(23L).putLong(session).putLong(17)
      .putLong(operation).putInt(payload.length).put(payload).array()
  def response(
      value: Int = 0,
      payload: Array[Byte] = Array.emptyByteArray,
      magic: Int = RPCFrames.ExecutionResponse
  ): Array[Byte] =
    ByteBuffer.allocate(
      ExecutionRPC.ResponseHeaderBytes + payload.length
    ).putInt(magic).putLong(ExecutionRPC.Schema.fingerprint)
      .putInt(value).putInt(payload.length).put(payload).array()
  def query: Array[Byte] = {
    val (id, payload) = Wire.Protocol[Operation]
      .encodePayload(Operation.QueryCompletion(CompletionId(1)), ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    execution(id, payload)
  }
  def completion(value: Int): Array[Byte] = {
    val status = value match {
      case 1 => CompletionStatus.Pending
      case 2 => CompletionStatus.Succeeded
      case _ => CompletionStatus.Failed(net.kurobako.gemstone.execution.ExecutionFailure.Failed("failed"))
    }
    val payload = summon[Wire.Codec[CompletionStatus]].encode(status, ExecutionRPC.Limits)
      .fold(message => throw IllegalArgumentException(message), value => value)
    response(payload = payload)
  }
  def temporary[A](body: Path => A): A =
    Using.resource(temporaryJournal())(temporary => body(temporary.file))
  def capture(file: Path)(body: RPCJournal.Writer => Unit): Unit =
    Using.resource(RPCJournal.Writer(file, RPCJournal.Header(schema)))(body)
}

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
        assertEquals(reader.summary.frames, 4)
        assertEquals(
          reader.events.filter(_.kind == RPCJournal.Kind.Request).map(_.client).toSet,
          Set(RPCJournal.RecordedClientId(1L))
        )
        val first = reader.frame(reader.events.head)
        assertEquals(first.toSeq, RPCFrames.normalized(RPCJournal.Kind.Request, execution(), 4096).toSeq)
        first(0) = 0
        assertEquals(RPCFrames.magic(reader.frame(reader.events.head)), RPCFrames.ExecutionRequest)
      }
      val _ = intercept[IOException](RPCJournal.Writer(file, RPCJournal.Header(schema)))
    }
  }
  test("execution profile survives the archive and reaches the fresh endpoint factory") {
    temporary { file =>
      val profile = RPCJournal.ExecutionProfile(2000000000L, 8192, 8)
      val writer  = RPCJournal.Writer(file, RPCJournal.Header(schema, executionProfile = Some(profile)))
      writer.close()
      val observed = AtomicReference(Option.empty[RPCJournal.ExecutionProfile])
      val closed   = AtomicBoolean(false)
      assertEquals(
        RPCReplay.run(
          RPCReplay.Options(file, schema),
          header => {
            observed.set(header.executionProfile); RPCReplay.Endpoint(RPCPort(1), () => closed.set(true))
          }
        ).events,
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
      assert(summary.reason.exists(_.value.contains("incomplete")))
      val started = AtomicBoolean(false)
      val _       = intercept[IOException](RPCReplay.run(
        RPCReplay.Options(file, schema),
        _ => { started.set(true); RPCReplay.Endpoint(RPCPort(1), () => ()) }
      ))
      assert(!started.get())
    }
    temporary { file =>
      val frame   = execution(3, Array.ofDim[Byte](5))
      val maximum = (frame.length - 1).refineEither[Interval.Closed[1, 2147479551]]
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
      val writer = RPCJournal.Writer(file, RPCJournal.Header(schema, RPCJournal.Limits(maxEvents = 2)))
      val c      = writer.connection(Object())
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
