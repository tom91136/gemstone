package net.kurobako.gemstone.server.transport

import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.rpc.ExecutionRPC
import net.kurobako.gemstone.rpc.ExecutionRPC.Operation
import net.kurobako.gemstone.rpc.Wire
import net.kurobako.gemstone.server.execution.ExecutionService

import cats.syntax.all.*

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.charset.StandardCharsets
import scala.compiletime.error

private[server] object ExecutionRPCProtocol {
  val RequestIdentityBytesAfterMagic =
    ExecutionRPC.RequestClientOffset + java.lang.Long.BYTES - java.lang.Integer.BYTES
  val RequestClientOffsetAfterMagic = ExecutionRPC.RequestClientOffset - java.lang.Integer.BYTES
  private val MaximumErrorTextBytes = ExecutionRPC.MaximumTextBytes: Int

  opaque type SessionId = (Long, Long)

  object SessionId {
    inline def apply(inline high: Long, inline low: Long): SessionId =
      inline if high == 0L && low == 0L then error("execution session identity cannot be the zero sentinel")
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
      if (payloadSize < 0 || payloadSize > ExecutionRPC.MaximumPayload)
        throw IllegalArgumentException(s"invalid execution payload size: $payloadSize")
      val payload   = readExactly(input, payloadSize)
      val validated = for {
        _ <- Either.cond(
          fingerprint == ExecutionRPC.Schema.fingerprint,
          (),
          ExecutionFailure.InvalidRequest("execution RPC schema differs from the server")
        )
        _ <- Either.cond(
          client == expectedClient.value,
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
                case Right(decoded) => dispatch(decoded.value, service)
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
        s"malformed execution request: ${Option(error.getMessage).getOrElse(error.getClass.getName)}"
      ))
    )

  private def dispatch(
      operation: Operation[?],
      service: ExecutionService
  ): Either[ExecutionFailure, Array[Byte]] = operation match {
    case Operation.Ping => summon[Wire.Codec[Unit]].encode((), ExecutionRPC.Limits)
        .leftMap(message => ExecutionFailure.Failed(message))
    case request @ Operation.RegisterAllocation(kind, size) =>
      encode(request, service.registerAllocation(size, kind))
    case request @ Operation.RegisterAllocationAddress(allocation, nativeBase) =>
      encode(request, service.registerAllocationAddress(allocation, nativeBase))
    case request @ Operation.ReleaseAllocation(allocation) =>
      encode(request, service.releaseAllocation(allocation))
    case request @ Operation.WriteAllocation(allocation, offset, bytes) =>
      encode(request, service.write(allocation, offset, bytes))
    case request @ Operation.ReadAllocation(allocation, offset, size) =>
      encode(request, service.read(allocation, offset, size))
    case request @ Operation.LoadProgram(format, image) =>
      encode(request, service.load(format, image))
    case request @ Operation.UnloadProgram(program) =>
      encode(request, service.unload(program))
    case request @ Operation.DescribeProgram(program) =>
      encode(request, service.catalog(program))
    case request @ Operation.DescribeKernel(program, name) =>
      encode(request, service.kernel(program, name))
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
          pointerRelocations
        ) =>
      val created = KernelDispatch.validated(
        program = program,
        kernelName = kernelName,
        groupSize = groupSize,
        groupCount = groupCount,
        arguments = arguments,
        globalSize = Some(globalSize),
        privateSegmentBytes = privateSegmentBytes,
        groupSegmentBytes = groupSegmentBytes,
        pointerRelocations = pointerRelocations,
        workDim = workDim,
        globalOffset = globalOffset
      ).leftMap(message => ExecutionFailure.InvalidRequest(message))
      val result = created match {
        case Left(error)     => Left(error)
        case Right(dispatch) => service.launch(dispatch)
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
    if (payload.length > ExecutionRPC.MaximumPayload)
      throw IllegalArgumentException(s"execution response exceeds ${ExecutionRPC.MaximumPayload} bytes")
    output.writeInt(ExecutionRPC.ResponseMagic)
    output.writeLong(ExecutionRPC.Schema.fingerprint)
    output.writeInt(status)
    output.writeInt(payload.length)
    if payload.nonEmpty then output.write(payload)
    output.flush()
  }

  private def bounded(error: ExecutionFailure): ExecutionFailure = {
    val detail = truncateUtf8(error.message, MaximumErrorTextBytes)
    error match {
      case _: ExecutionFailure.InvalidRequest => ExecutionFailure.InvalidRequest(detail)
      case _: ExecutionFailure.NotFound       => ExecutionFailure.NotFound(detail)
      case _: ExecutionFailure.Unsupported    => ExecutionFailure.Unsupported(detail)
      case _: ExecutionFailure.Failed         => ExecutionFailure.Failed(detail)
    }
  }

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

  private def readExactly(input: DataInputStream, size: Int): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw IllegalArgumentException(s"expected $size bytes, received ${result.length}")
    result
  }
}
