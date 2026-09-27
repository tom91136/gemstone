package net.kurobako.gemstone.server.transport

import Transport.*

import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.ExecutionProtocol.*
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.execution.ExecutionService
import net.kurobako.gemstone.server.Throwables
import net.kurobako.gemstone.vm.KernelTask

import cats.syntax.all.*

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import scala.compiletime.error

object ExecutionRPCProtocol {
  val RequestIdentityBytesAfterMagic =
    ExecutionRPC.RequestClientOffset.raw + java.lang.Long.BYTES - java.lang.Integer.BYTES
  val RequestClientOffsetAfterMagic = ExecutionRPC.RequestClientOffset.raw - java.lang.Integer.BYTES
  private val MaximumErrorTextBytes = ExecutionRPC.MaximumTextBytes.raw

  opaque type SessionId = (Long, Long)

  object SessionId {
    inline def apply(inline high: Long, inline low: Long): SessionId =
      inline if (high == 0L && low == 0L) error("execution session identity cannot be the zero sentinel")
      else (high, low)

    def fromWire(high: Long, low: Long): Either[ExecutionFailure, SessionId] =
      Either.cond(
        high != 0L || low != 0L,
        (high, low),
        ExecutionFailure.InvalidRequest("execution session identity is the zero sentinel")
      )
  }

  trait SessionConnection extends AutoCloseable {
    def service(session: SessionId): Either[ExecutionFailure, ExecutionService]
  }

  def exchange(
      input: DataInputStream,
      output: DataOutputStream,
      expectedClient: ClientId,
      connection: SessionConnection,
      onAccepted: () => Unit = () => ()
  ): Boolean =
    try {
      val fingerprint = input.readLong()
      val client      = input.readLong()
      val sessionHigh = input.readLong()
      val sessionLow  = input.readLong()
      val operationId = input.readLong()
      val payloadSize = input.readInt()
      if (payloadSize < 0 || payloadSize > ExecutionRPC.MaximumPayload.raw)
        throw IllegalArgumentException(s"invalid execution payload size: $payloadSize")
      val payload   = readExactly(input, payloadSize, IllegalArgumentException(_))
      val validated = for {
        _ <- Either.cond(
          fingerprint == ExecutionRPC.Schema.fingerprint,
          (),
          ExecutionFailure.InvalidRequest("execution RPC schema differs from the server")
        )
        _ <- Either.cond(
          client == expectedClient.raw,
          (),
          ExecutionFailure.InvalidRequest("client identity changed")
        )
        session <- SessionId.fromWire(sessionHigh, sessionLow)
      } yield session
      val result = validated match {
        case Left(error)    => Left(error)
        case Right(session) =>
          onAccepted()
          connection.service(session) match {
            case Left(error)    => Left(error)
            case Right(service) =>
              Wire.Protocol[Operation].decodePayload(operationId, payload, ExecutionRPC.Limits) match {
                case Left(message)  => Left(ExecutionFailure.InvalidRequest(message))
                case Right(decoded) =>
                  try dispatch(decoded.value, service)
                  catch {
                    case scala.util.control.NonFatal(error) => Left(ExecutionFailure.Failed(Throwables.detail(error)))
                  }
              }
          }
      }
      write(output, result)
      true
    } catch {
      case scala.util.control.NonFatal(error) =>
        writeMalformed(output, error)
        false
    }

  def writeMalformed(output: DataOutputStream, error: Throwable): Unit =
    write(
      output,
      Left(ExecutionFailure.InvalidRequest(
        s"malformed execution request: ${Throwables.detail(error)}"
      ))
    )

  private def dispatch(
      operation: Operation[?],
      service: ExecutionService
  ): Either[ExecutionFailure, Array[Byte]] = operation match {
    case Operation.EnableHostMemory => encode(Operation.EnableHostMemory, service.enableHostMemory())
    case request @ Operation.PollHostMemory(completion)           => encode(request, service.pollHostMemory(completion))
    case request @ Operation.SupplyHostMemory(completion, ranges) =>
      encode(request, service.supplyHostMemory(completion, ranges))
    case Operation.Ping                                     => encode(Operation.Ping, Right(()))
    case request @ Operation.RegisterAllocation(kind, size) =>
      encode(request, service.registerAllocation(size, kind))
    case request @ Operation.RegisterAllocationAddress(allocation, nativeBase, hostBacked) =>
      encode(request, service.registerAllocationAddress(allocation, nativeBase, hostBacked))
    case request @ Operation.SupplyHostSync(completion, ticket, observed) =>
      encode(request, service.supplyHostSync(completion, ticket, observed))
    case request @ Operation.ReleaseAllocation(allocation) =>
      encode(request, service.releaseAllocation(allocation))
    case request @ Operation.WriteAllocation(allocation, offset, bytes) =>
      encode(request, service.write(allocation, offset, bytes))
    case request @ Operation.ReadAllocation(allocation, offset, size) =>
      encode(request, service.read(allocation, offset, size))
    case request @ Operation.LoadProgram(encoding, image) =>
      encode(request, service.load(encoding, image))
    case request @ Operation.DescribeSpecialization(image) =>
      encode(request, service.describeSpecialization(image))
    case request @ Operation.SpecializeImage(image, constants) =>
      encode(request, service.specializeImage(image, constants))
    case request @ Operation.UnloadProgram(program) =>
      encode(request, service.unload(program))
    case request @ Operation.DescribeProgram(program) =>
      encode(request, service.catalog(program))
    case request @ Operation.DescribeKernel(program, name) =>
      encode(request, service.kernel(program, name))
    case request @ Operation.DescribeImage(encoding, image, name) =>
      encode(request, service.describeImage(encoding, image, name))
    case request @ Operation.Blas(blas) =>
      encode(request, service.blas(blas))
    case request @ Operation.DescribeGlobal(program, name) =>
      encode(request, service.global(program, name))
    case request @ Operation.BindProgramGlobal(program, name, allocation, offset) =>
      encode(request, service.bindGlobal(program, name, allocation, offset))
    case request @ Operation.WriteProgramGlobal(program, name, offset, bytes) =>
      encode(request, service.writeGlobal(program, name, offset, bytes))
    case request @ Operation.Launch(
          program,
          kernelName,
          groupSize,
          groupCount,
          globalSize,
          workDim,
          globalOffset,
          privateSegmentBytes,
          groupSegmentBytes,
          arguments,
          pointerRelocations,
          discoverArgumentRelocations
        ) =>
      val created = KernelTask.validated(
        kernelName = kernelName,
        groupSize = groupSize,
        groupCount = groupCount,
        arguments = arguments,
        globalSize = Some(globalSize),
        privateSegmentBytes = privateSegmentBytes,
        groupSegmentBytes = groupSegmentBytes,
        pointerRelocations = pointerRelocations,
        workDim = workDim,
        globalOffset = globalOffset,
        discoverArgumentRelocations = discoverArgumentRelocations
      ).leftMap(message => ExecutionFailure.InvalidRequest(message))
      val result = created match {
        case Left(error) => Left(error)
        case Right(task) => service.launch(program, task)
      }
      encode(request, result)
    case request @ Operation.QueryCompletion(completion) =>
      encode(request, service.completion(completion))
    case request @ Operation.AwaitCompletion(completion) =>
      encode(request, service.awaitCompletion(completion))
    case request @ Operation.CompletionDirtyRanges(completion) =>
      encode(request, service.completionDirtyRanges(completion))
    case request @ Operation.CompletionPointerRelocations(completion) =>
      encode(request, service.completionPointerRelocations(completion))
    case request @ Operation.CompletionPrints(completion) =>
      encode(request, service.completionPrints(completion))
    case request @ Operation.ReleaseCompletion(completion) =>
      encode(request, service.releaseCompletion(completion))
  }

  private def encode[A](
      operation: Operation[A],
      result: Either[ExecutionFailure, A]
  ): Either[ExecutionFailure, Array[Byte]] =
    result.flatMap(value =>
      operation.replyCodec.encode(value, ExecutionRPC.Limits)
        .leftMap(message => ExecutionFailure.Failed(message))
    )

  private def write(output: DataOutputStream, result: Either[ExecutionFailure, Array[Byte]]): Unit = {
    val (status, payload) = result match {
      case Right(bytes) => 0 -> bytes
      case Left(error)  =>
        val encoded = summon[Wire.Codec[ExecutionFailure]].encode(bounded(error), ExecutionRPC.Limits)
          .fold(message => throw IllegalArgumentException(s"cannot encode execution failure: $message"), value => value)
        1 -> encoded
    }
    if (payload.length > ExecutionRPC.MaximumPayload.raw)
      throw IllegalArgumentException(s"execution response exceeds ${ExecutionRPC.MaximumPayload.raw} bytes")
    output.writeInt(ExecutionRPC.ResponseMagic)
    output.writeLong(ExecutionRPC.Schema.fingerprint)
    output.writeInt(status)
    output.writeInt(payload.length)
    if (payload.nonEmpty) output.write(payload)
    output.flush()
  }

  private def bounded(error: ExecutionFailure): ExecutionFailure =
    error.withDetail(truncateUtf8(error.message, MaximumErrorTextBytes))

  private def truncateUtf8(
      value: String,
      maximumBytes: Int
  ): String =
    if (value.getBytes(StandardCharsets.UTF_8).length <= maximumBytes) value
    else {
      val result = StringBuilder()
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var index = 0
      @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
      var bytes = 0
      while (index < value.length) {
        val codePoint = value.codePointAt(index)
        val width     =
          if (codePoint <= 0x7f) 1
          else if (codePoint <= 0x7ff) 2
          else if (codePoint <= 0xffff) 3
          else 4
        if (bytes + width > maximumBytes) index = value.length
        else {
          result.appendAll(Character.toChars(codePoint))
          bytes += width
          index += Character.charCount(codePoint)
        }
      }
      result.result()
    }

}
