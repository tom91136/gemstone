package net.kurobako.gemstone.server

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.execution.*
import net.kurobako.gemstone.memory.AllocationKind

import cats.syntax.either.*

import java.io.DataInputStream
import java.io.DataOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

import scala.reflect.ClassTag

private[server] object ExecutionRPCProtocol {
  val RequestMagic = 0x474d4531

  private val ResponseMagic    = 0x474d4631
  private val ProtocolVersion  = 3
  private val TokenBytes       = 64
  private val MaximumPayload   = 128 * 1024 * 1024
  private val MaximumName      = 4096
  private val MaximumArguments = 4096

  final case class SessionId(high: Long, low: Long)

  trait SessionConnection extends AutoCloseable {
    def service(session: SessionId): Either[ExecutionFailure, ExecutionService]
  }

  private enum Operation(val code: Int) {
    case Attest                           extends Operation(0)
    case RegisterAllocation               extends Operation(1)
    case ReleaseAllocation                extends Operation(2)
    case WriteAllocation                  extends Operation(3)
    case ReadAllocation                   extends Operation(4)
    case LoadProgram                      extends Operation(5)
    case UnloadProgram                    extends Operation(6)
    case Launch                           extends Operation(7)
    case QueryCompletion                  extends Operation(8)
    case ReleaseCompletion                extends Operation(9)
    case CompletionDirtyRanges            extends Operation(10)
    case LaunchWithRelocations            extends Operation(11)
    case LaunchWithGeometry               extends Operation(12)
    case LaunchWithGeometryAndRelocations extends Operation(13)
    case CompletionPointerRelocations     extends Operation(14)
    case WriteProgramGlobal               extends Operation(15)
  }

  def exchange(
      input: DataInputStream,
      output: DataOutputStream,
      expectedToken: Array[Byte],
      connection: SessionConnection,
      onAuthenticated: () => Unit = () => ()
  ): Boolean =
    try {
      val version       = input.readInt()
      val token         = readExactly(input, TokenBytes)
      val session       = SessionId(input.readLong(), input.readLong())
      val operationCode = input.readInt()
      val payloadSize   = input.readInt()
      if (payloadSize < 0 || payloadSize > MaximumPayload)
        throw IllegalArgumentException(s"invalid execution payload size: $payloadSize")
      val bytes  = readExactly(input, payloadSize)
      val result = for {
        _ <- Either.cond(
          version == ProtocolVersion,
          (),
          ExecutionFailure.InvalidRequest("unsupported protocol version")
        )
        _ <- Either.cond(
          MessageDigest.isEqual(token, expectedToken),
          (),
          ExecutionFailure.InvalidRequest("authentication failed")
        )
        _ = onAuthenticated()
        _ <- Either.cond(
          session.high != 0L || session.low != 0L,
          (),
          ExecutionFailure.InvalidRequest("execution session identity is the zero sentinel")
        )
        service   <- connection.service(session)
        operation <- Operation.values.find(_.code == operationCode).toRight(
          ExecutionFailure.InvalidRequest(s"unknown execution operation: $operationCode")
        )
        payload = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
        response <- dispatch(operation, payload, service)
        _        <- Either.cond(
          !payload.hasRemaining,
          (),
          ExecutionFailure.InvalidRequest(s"execution request has ${payload.remaining} trailing bytes")
        )
      } yield response
      write(output, result)
      true
    } catch {
      case error: Throwable =>
        writeMalformed(output, error)
        false
    }

  def writeMalformed(output: DataOutputStream, error: Throwable): Unit =
    write(output, Left(ExecutionFailure.InvalidRequest(s"malformed execution request: ${error.getMessage}")))

  private def dispatch(
      operation: Operation,
      payload: ByteBuffer,
      service: ExecutionService
  ): Either[ExecutionFailure, Col[Byte]] = operation match {
    case Operation.Attest             => Right(Col.empty)
    case Operation.RegisterAllocation =>
      for {
        id   <- allocationId(payload.getLong())
        kind <- allocationKind(payload.getInt())
        _    <- service.registerAllocation(id, payload.getLong(), kind)
      } yield Col.empty
    case Operation.ReleaseAllocation =>
      allocationId(payload.getLong()).flatMap(service.releaseAllocation).map(_ => Col.empty)
    case Operation.WriteAllocation =>
      for {
        id <- allocationId(payload.getLong())
        offset = payload.getLong()
        bytes <- byteArray(payload)
        _     <- service.write(id, offset, bytes)
      } yield Col.empty
    case Operation.ReadAllocation =>
      for {
        id <- allocationId(payload.getLong())
        offset = payload.getLong()
        size   = payload.getInt()
        bytes <- service.read(id, offset, size)
      } yield bytes
    case Operation.LoadProgram =>
      for {
        format <- programFormat(payload.getInt())
        image  <- byteArray(payload)
        id     <- service.load(format, image)
      } yield longBytes(id.toLong)
    case Operation.UnloadProgram =>
      programId(payload.getLong()).flatMap(service.unload).map(_ => Col.empty)
    case Operation.WriteProgramGlobal =>
      for {
        program <- programId(payload.getLong())
        name    <- string(payload)
        offset = payload.getLong()
        bytes <- byteArray(payload)
        _     <- service.writeGlobal(program, name, offset, bytes)
      } yield Col.empty
    case Operation.Launch =>
      decodeDispatch(payload, includeRelocations = false, includeGeometry = false).flatMap(service.launch).map(id =>
        longBytes(id.toLong)
      )
    case Operation.LaunchWithRelocations =>
      decodeDispatch(payload, includeRelocations = true, includeGeometry = false).flatMap(service.launch).map(id =>
        longBytes(id.toLong)
      )
    case Operation.LaunchWithGeometry =>
      decodeDispatch(payload, includeRelocations = false, includeGeometry = true).flatMap(service.launch).map(id =>
        longBytes(id.toLong)
      )
    case Operation.LaunchWithGeometryAndRelocations =>
      decodeDispatch(payload, includeRelocations = true, includeGeometry = true).flatMap(service.launch).map(id =>
        longBytes(id.toLong)
      )
    case Operation.QueryCompletion =>
      completionId(payload.getLong()).flatMap(service.completion).flatMap {
        case CompletionStatus.Pending       => Right(intBytes(1))
        case CompletionStatus.Succeeded     => Right(intBytes(2))
        case CompletionStatus.Failed(error) => Left(error)
      }
    case Operation.ReleaseCompletion =>
      completionId(payload.getLong()).flatMap(service.releaseCompletion).map(_ => Col.empty)
    case Operation.CompletionDirtyRanges =>
      completionId(payload.getLong()).flatMap(service.completionDirtyRanges).flatMap(dirtyRangeBytes)
    case Operation.CompletionPointerRelocations =>
      completionId(payload.getLong()).flatMap(service.completionPointerRelocations).flatMap(pointerRelocationBytes)
  }

  private def decodeDispatch(
      payload: ByteBuffer,
      includeRelocations: Boolean,
      includeGeometry: Boolean
  ): Either[ExecutionFailure, KernelDispatch] = for {
    program    <- programId(payload.getLong())
    name       <- string(payload)
    groupSize  <- dimensions(payload)
    groupCount <- dimensions(payload)
    globalSize <- dimensions(payload)
    workDim      = if (includeGeometry) payload.getInt() else 3
    globalOffset =
      if (includeGeometry) Coordinates(payload.getInt(), payload.getInt(), payload.getInt())
      else Coordinates(0, 0, 0)
    privateSegmentBytes = payload.getInt()
    groupSegmentBytes   = payload.getInt()
    pointerRelocations <-
      if (includeRelocations)
        decodeCollection(payload.getInt(), "pointer relocation")(decodeRelocation(payload))
      else Right(Col.empty[PointerRelocation])
    count = payload.getInt()
    arguments <- decodeCollection(count, "kernel argument")(decodeArgument(payload))
    dispatch  <- KernelDispatch.create(
      program,
      name,
      groupSize,
      groupCount,
      arguments,
      Some(globalSize),
      privateSegmentBytes,
      groupSegmentBytes,
      pointerRelocations,
      workDim,
      globalOffset
    )
      .leftMap(ExecutionFailure.InvalidRequest.apply)
  } yield dispatch

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def decodeCollection[A: ClassTag](
      count: Int,
      description: String
  )(decode: => Either[ExecutionFailure, A]): Either[ExecutionFailure, Col[A]] =
    if (count < 0 || count > MaximumArguments)
      Left(ExecutionFailure.InvalidRequest(s"invalid $description count: $count"))
    else {
      val result = Col.newBuilder[A]
      var index  = 0
      var error  = Option.empty[ExecutionFailure]
      while (index < count && error.isEmpty) {
        decode match {
          case Right(value) => result += value
          case Left(value)  => error = Some(value)
        }
        index += 1
      }
      error.toLeft(result.result())
    }

  private def decodeRelocation(payload: ByteBuffer): Either[ExecutionFailure, PointerRelocation] = for {
    sourceAllocation = payload.getLong()
    sourceArgument   = payload.getInt()
    _                = payload.getInt()
    sourceOffset     = payload.getLong()
    target <- allocationId(payload.getLong())
    targetOffset = payload.getLong()
    source <- if (sourceAllocation != 0L && sourceArgument == -1)
      allocationId(sourceAllocation).map(value => (Some(value), None))
    else if (sourceAllocation == 0L && sourceArgument >= 0)
      Right((None, Some(sourceArgument)))
    else
      Left(ExecutionFailure.InvalidRequest("pointer relocation must have exactly one source"))
    _ <- Either.cond(
      sourceOffset >= 0L && targetOffset >= 0L,
      (),
      ExecutionFailure.InvalidRequest("pointer relocation offsets must be non-negative")
    )
  } yield PointerRelocation(source._1, source._2, sourceOffset, target, targetOffset)

  private def decodeArgument(payload: ByteBuffer): Either[ExecutionFailure, KernelArgument] = payload.getInt() match {
    case 1 => byteArray(payload).map(KernelArgument.Scalar.apply)
    case 2 => allocationId(payload.getLong()).map(KernelArgument.Allocation(_, payload.getLong()))
    case 3 =>
      val size = payload.getLong()
      Either.cond(size >= 0L, KernelArgument.Local(size), ExecutionFailure.InvalidRequest(s"invalid local size: $size"))
    case value => Left(ExecutionFailure.InvalidRequest(s"unknown kernel argument kind: $value"))
  }

  private def dimensions(payload: ByteBuffer): Either[ExecutionFailure, Dimensions] =
    Dimensions.create(
      payload.getInt(),
      payload.getInt(),
      payload.getInt()
    ).leftMap(ExecutionFailure.InvalidRequest.apply)

  private def allocationKind(value: Int): Either[ExecutionFailure, AllocationKind] = value match {
    case 1 => Right(AllocationKind.Device)
    case 2 => Right(AllocationKind.Shared)
    case 3 => Right(AllocationKind.Host)
    case 4 => Right(AllocationKind.Managed)
    case _ => Left(ExecutionFailure.InvalidRequest(s"unknown allocation kind: $value"))
  }

  private def programFormat(value: Int): Either[ExecutionFailure, ProgramFormat] = value match {
    case 1 => Right(ProgramFormat.PTX)
    case 2 => Right(ProgramFormat.AMDGPUCodeObject)
    case 3 => Right(ProgramFormat.SPIRV)
    case 4 => Right(ProgramFormat.AMDGPURawKernel)
    case _ => Left(ExecutionFailure.InvalidRequest(s"unknown program format: $value"))
  }

  private def allocationId(value: Long): Either[ExecutionFailure, AllocationId] =
    AllocationId.fromLong(value).leftMap(ExecutionFailure.InvalidRequest.apply)

  private def programId(value: Long): Either[ExecutionFailure, ProgramId] =
    ProgramId.fromLong(value).leftMap(ExecutionFailure.InvalidRequest.apply)

  private def completionId(value: Long): Either[ExecutionFailure, CompletionId] =
    CompletionId.fromLong(value).leftMap(ExecutionFailure.InvalidRequest.apply)

  private def byteArray(payload: ByteBuffer): Either[ExecutionFailure, Col[Byte]] = {
    val size = payload.getInt()
    if (size < 0 || size > payload.remaining)
      Left(ExecutionFailure.InvalidRequest(s"invalid byte-array size: $size"))
    else {
      val bytes = Array.ofDim[Byte](size)
      payload.get(bytes)
      Right(Col.unsafeFromArray(bytes))
    }
  }

  private def string(payload: ByteBuffer): Either[ExecutionFailure, String] =
    byteArray(payload).flatMap { bytes =>
      Either.cond(
        bytes.nonEmpty && bytes.length <= MaximumName,
        String(Col.toArray(bytes), StandardCharsets.UTF_8),
        ExecutionFailure.InvalidRequest(s"invalid kernel-name size: ${bytes.length}")
      )
    }

  private def intBytes(value: Int): Col[Byte] =
    Col.unsafeFromArray(ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array())

  private def longBytes(value: Long): Col[Byte] =
    Col.unsafeFromArray(ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN).putLong(value).array())

  private def dirtyRangeBytes(ranges: Col[DirtyRange]): Either[ExecutionFailure, Col[Byte]] =
    Either.cond(
      ranges.length <= (MaximumPayload - 4) / 20, {
        val output = ByteBuffer.allocate(4 + ranges.length * 20).order(ByteOrder.BIG_ENDIAN)
        output.putInt(ranges.length)
        ranges.foreach { range =>
          output.putLong(range.allocation.toLong)
          output.putLong(range.offset)
          output.putInt(range.size)
        }
        Col.unsafeFromArray(output.array())
      },
      ExecutionFailure.Failed(s"completion has too many dirty ranges: ${ranges.length}")
    )

  private def pointerRelocationBytes(relocations: Col[PointerRelocation]): Either[ExecutionFailure, Col[Byte]] =
    Either.cond(
      relocations.length <= (MaximumPayload - 4) / 32 && relocations.forall(relocation =>
        relocation.sourceAllocation.isDefined && relocation.sourceArgument.isEmpty
      ), {
        val output = ByteBuffer.allocate(4 + relocations.length * 32).order(ByteOrder.BIG_ENDIAN)
        output.putInt(relocations.length)
        relocations.foreach { relocation =>
          output.putLong(relocation.sourceAllocation.get.toLong)
          output.putLong(relocation.sourceOffset)
          output.putLong(relocation.target.toLong)
          output.putLong(relocation.targetOffset)
        }
        Col.unsafeFromArray(output.array())
      },
      ExecutionFailure.Failed("completion contains a non-allocation pointer relocation")
    )

  private def write(output: DataOutputStream, result: Either[ExecutionFailure, Col[Byte]]): Unit = {
    val (status, message, payload) = result match {
      case Right(bytes) => (0, Array.emptyByteArray, Col.toArray(bytes))
      case Left(error)  => (failureCode(error), error.message.getBytes(StandardCharsets.UTF_8), Array.emptyByteArray)
    }
    output.writeInt(ResponseMagic)
    output.writeInt(ProtocolVersion)
    output.writeInt(status)
    output.writeInt(message.length)
    output.writeInt(payload.length)
    output.write(message)
    output.write(payload)
    output.flush()
  }

  private def failureCode(error: ExecutionFailure): Int = error match {
    case _: ExecutionFailure.InvalidRequest => 1
    case _: ExecutionFailure.NotFound       => 2
    case _: ExecutionFailure.Unsupported    => 3
    case _: ExecutionFailure.Failed         => 4
  }

  private def readExactly(input: DataInputStream, size: Int): Array[Byte] = {
    val result = input.readNBytes(size)
    if (result.length != size) throw IllegalArgumentException(s"expected $size bytes, received ${result.length}")
    result
  }
}
