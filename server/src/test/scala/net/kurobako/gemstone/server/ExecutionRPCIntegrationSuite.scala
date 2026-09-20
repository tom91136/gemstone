package net.kurobako.gemstone.server

import net.kurobako.gemstone.{NoNul, Vec}
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.launcher.Environment
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.server.transport.{ClientId, RPCPort}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketTimeoutException
import java.nio.file.Path
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration.*
import scala.util.Using

/** Cross-language RPC coverage shared by the JVM and Scala Native servers. */
final class ExecutionRPCIntegrationSuite extends munit.FunSuite {
  override val munitTimeout: Duration = 2.minutes

  private object TestBackend extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]) =
      Either.cond(
        format == ProgramFormat.SPIRVBinary && image == Vec[Byte](3, 2, 35, 7),
        (),
        ExecutionFailure.InvalidRequest("unexpected module")
      )
    override def unload(id: ProgramId)  = Right(())
    override def catalog(id: ProgramId) = Right(
      ProgramCatalog(Vec(ProgramKernel(KernelName("add"), LinkageName("add.kd"))), Vec.empty)
    )
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) =
      dispatch.arguments match {
        case Vec(
              KernelArgument.Allocation(id, offset),
              KernelArgument.Scalar(bytes),
              KernelArgument.Local(64L)
            ) => memory.write(id, offset, bytes)
        case _ => Left(ExecutionFailure.InvalidRequest("unexpected arguments"))
      }
  }

  private final case class ProfileBackend(observed: AtomicReference[Option[ExecutionLimits]]) extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte]) = TestBackend.load(id, format, image)
    override def unload(id: ProgramId)                                        = TestBackend.unload(id)
    override def catalog(id: ProgramId)                                       = TestBackend.catalog(id)
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = {
      observed.set(Some(control.limits))
      TestBackend.launch(dispatch, memory, control)
    }
  }

  test("native execution client round-trips program, allocation, dispatch, and completion") {
    Using.resource(GemstoneRPCServer.start(TestBackend)) { server =>
      assertEquals(server.usage, ServerUsage(0))
      runClient("gemstone_execution_rpc_smoke_client", server.port, server.client)
      assert(server.usage.executionRequests > 0)
    }
  }

  test("an explicit server execution profile is applied to each dispatch") {
    val limits   = ExecutionLimits(maxInstructions = 17L, quantum = 3)
    val observed = AtomicReference(Option.empty[ExecutionLimits])
    Using.resource(GemstoneRPCServer.start(ProfileBackend(observed), limits)) { server =>
      runClient("gemstone_execution_rpc_smoke_client", server.port, server.client)
      assertEquals(observed.get(), Some(limits))
    }
  }

  test("execution connections survive an idle memory connection during a long dispatch") {
    Using.resource(GemstoneRPCServer.start(
      TestBackend,
      ExecutionLimits(maxInstructions = 1L, quantum = 1),
      50
    )) { server =>
      runClient(
        "gemstone_execution_rpc_smoke_client",
        server.port,
        server.client,
        Map("GEMSTONE_EXECUTION_HOLD_ALLOCATION" -> "1")
      )
    }
  }

  test("independent native processes have isolated execution capability namespaces") {
    Using.resource(GemstoneRPCServer.start(TestBackend)) { server =>
      val environment = Map("GEMSTONE_EXECUTION_HOLD_ALLOCATION" -> "1")
      val clients     = Vector.fill(2)(Future(runClient(
        "gemstone_execution_rpc_smoke_client",
        server.port,
        server.client,
        environment
      )))
      Await.result(Future.sequence(clients), 1.minute)
    }
  }

  test("a lost execution session is never silently reconnected") {
    val client      = ClientId(42L)
    val listener    = ServerSocket(0, 1, InetAddress.getLoopbackAddress)
    val reconnected = AtomicBoolean(false)
    val failure     = AtomicReference(Option.empty[Throwable])
    val server      = Thread { () =>
      def serveOne(): Unit =
        Using.resource(listener.accept()) { socket =>
          val input  = DataInputStream(socket.getInputStream)
          val output = DataOutputStream(socket.getOutputStream)
          val header = input.readNBytes(ExecutionRPC.RequestHeaderBytes)
          if (header.length != ExecutionRPC.RequestHeaderBytes)
            throw IllegalStateException(
              s"expected ${ExecutionRPC.RequestHeaderBytes} request header bytes, received ${header.length}"
            )
          val payloadSize = java.nio.ByteBuffer
            .wrap(header, ExecutionRPC.RequestPayloadSizeOffset, java.lang.Integer.BYTES)
            .getInt()
          val payload = input.readNBytes(payloadSize)
          if (payload.length != payloadSize)
            throw IllegalStateException(s"expected $payloadSize request payload bytes, received ${payload.length}")
          output.writeInt(ExecutionRPC.ResponseMagic)
          output.writeLong(ExecutionRPC.Schema.fingerprint)
          output.writeInt(0)
          output.writeInt(java.lang.Long.BYTES)
          output.writeLong(1L)
          output.flush()
        }
      try {
        serveOne()
        listener.setSoTimeout(500)
        try {
          serveOne()
          reconnected.set(true)
        } catch {
          case _: SocketTimeoutException => ()
        }
      } catch {
        case scala.util.control.NonFatal(error) => failure.set(Some(error))
      }
    }
    server.start()
    // The listener is managed by Using; the finally joins the associated thread even when an assertion fails.
    try Using.resource(listener) { _ =>
        val port = RPCPort.either(listener.getLocalPort).fold(message => fail(message), value => value)
        runClient("gemstone_execution_rpc_session_loss_client", port, client)
        server.join()
        failure.get().foreach(error => throw error)
        assert(!reconnected.get())
      }
    finally server.join()
  }

  private def runClient(
      name: String,
      port: RPCPort,
      client: ClientId,
      extraEnvironment: Map[String, String] = Map.empty
  ): Unit = {
    val result = NativeTestProcess.run(
      Vector(executionBuild.resolve(name).toString.refineUnsafe[NoNul]).refineUnsafe[MinLength[1]],
      NativeTestProcess.root,
      Environment(
        extraEnvironment ++ Map(
          "GEMSTONE_RPC_PORT"   -> port.value.toString,
          "GEMSTONE_RPC_CLIENT" -> client.value.toString
        ),
        Set.empty
      ),
      1.minute
    )
    assertEquals(result.exit, 0, result.diagnostic)
  }

  private lazy val executionBuild: Path = NativeTestProcess.nativeDirectory
}
