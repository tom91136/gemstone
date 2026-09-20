package net.kurobako.gemstone.server.transport

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.execution.{ExecutionService, ProgramRegistry}
import net.kurobako.gemstone.memory.GuestAddress

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, DataInputStream, DataOutputStream, IOException}
import scala.util.Using

final class ExecutionRPCIOSuite extends munit.FunSuite {
  private final class FixedConnection(executionService: ExecutionService)
      extends ExecutionRPCProtocol.SessionConnection {
    def service(id: ExecutionRPCProtocol.SessionId) = Right(executionService)
    def close(): Unit                               = ()
  }

  private final class RejectEmptyWrites extends ByteArrayOutputStream {
    override def write(value: Array[Byte], offset: Int, length: Int): Unit = {
      if (length <= 0) throw IOException("socket implementation rejects empty writes")
      super.write(value, offset, length)
    }
  }

  private object MetadataExecutor extends ProgramExecutor {
    override def load(id: ProgramId, format: ProgramFormat, image: Vec[Byte])                         = Right(())
    override def unload(id: ProgramId)                                                                = Right(())
    override def launch(dispatch: KernelDispatch, memory: ExecutionMemory, control: ExecutionControl) = Right(())
    override def catalog(id: ProgramId)                                                               = Right(
      ProgramCatalog(
        Vec(ProgramKernel(KernelName("layout"), LinkageName("_layout"))),
        Vec(
          ProgramGlobalMetadata.validated(GlobalName("state"), Some(GuestAddress(0x1000L)), 24L, Vec(0, 16))
            .fold(message => fail(message), value => value)
        )
      )
    )
    override def kernel(id: ProgramId, name: KernelName) = Right(
      KernelMetadata.validated(
        24,
        2,
        Vec(
          KernelParameterMetadata(8, 0, KernelParameterKind.Allocation, containsPointers = false),
          KernelParameterMetadata(12, 8, KernelParameterKind.Scalar, containsPointers = true),
          KernelParameterMetadata(4, 20, KernelParameterKind.Local, containsPointers = false)
        )
      ).fold(message => fail(message), value => value)
    )
  }

  private def exchange[A](
      operation: Operation[A],
      expectedClient: ClientId,
      sentClient: Long = 7L,
      outputBytes: ByteArrayOutputStream = ByteArrayOutputStream()
  )(service: ExecutionService): Either[ExecutionFailure, A] = {
    val (operationId, payload) = Wire.Protocol[Operation].encodePayload(operation, ExecutionRPC.Limits)
      .fold(message => fail(message), value => value)
    val requestBytes = ByteArrayOutputStream()
    val request      = DataOutputStream(requestBytes)
    request.writeLong(ExecutionRPC.Schema.fingerprint)
    request.writeLong(sentClient)
    request.writeLong(1L)
    request.writeLong(2L)
    request.writeLong(operationId)
    request.writeInt(payload.length)
    request.write(payload)

    assert(ExecutionRPCProtocol.exchange(
      DataInputStream(ByteArrayInputStream(requestBytes.toByteArray)),
      DataOutputStream(outputBytes),
      expectedClient,
      FixedConnection(service)
    ))
    val response = DataInputStream(ByteArrayInputStream(outputBytes.toByteArray))
    assertEquals(response.readInt(), ExecutionRPC.ResponseMagic)
    assertEquals(response.readLong(), ExecutionRPC.Schema.fingerprint)
    val status = response.readInt()
    val body   = response.readNBytes(response.readInt())
    status match {
      case 0 => operation.replyCodec.decode(body, ExecutionRPC.Limits)
          .left.map(message => ExecutionFailure.InvalidRequest(message))
      case 1 => summon[Wire.Codec[ExecutionFailure]].decode(body, ExecutionRPC.Limits)
          .fold(message => Left(ExecutionFailure.InvalidRequest(message)), error => Left(error))
      case value => Left(ExecutionFailure.InvalidRequest(s"invalid status: $value"))
    }
  }

  for (identified <- Seq(true, false))
    test(s"execution response avoids empty socket writes, identified=$identified") {
      val client = ClientId(7L)
      Using.resource(ExecutionService(
        ProgramRegistry.validated(Vec.empty).fold(message => fail(message), value => value)
      )) { service =>
        val response = exchange(
          Operation.Ping,
          expectedClient = client,
          sentClient = if identified then client.value else client.value + 1L,
          outputBytes = RejectEmptyWrites()
        )(service)
        assertEquals(response.isRight, identified)
      }
    }

  test("kernel metadata response carries the complete neutral launch layout") {
    Using.resource(ExecutionService(MetadataExecutor)) { service =>
      val program  = ProgramId(1L)
      val response = exchange(Operation.DescribeKernel(program, KernelName("layout")), ClientId(7L))(service)
      assertEquals(response, MetadataExecutor.kernel(program, KernelName("layout")))
    }
  }

  test("program catalog response carries neutral kernel and global symbols") {
    Using.resource(ExecutionService(MetadataExecutor)) { service =>
      val program  = ProgramId(1L)
      val response = exchange(Operation.DescribeProgram(program), ClientId(7L))(service)
      assertEquals(response, MetadataExecutor.catalog(program))
    }
  }

}
