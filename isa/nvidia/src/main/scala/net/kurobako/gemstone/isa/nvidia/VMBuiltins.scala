package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import cats.syntax.all.*
import java.nio.ByteBuffer
import java.nio.ByteOrder
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** CUDA device-runtime calls, executed per lane on the caller's `.param` storage. */
private object VMBuiltins {

  // Attribute values of the emulated device, by cudaDeviceAttr ordinal.
  private def deviceAttribute(attribute: Int): Int = attribute match {
    case 1 | 2 | 3 => 1024
    case 4         => 64
    case 5         => Int.MaxValue
    case 6 | 7     => 65535
    case 8         => 49152
    case 10        => 32
    case 16        => EmulatedDevice.Multiprocessors
    case 39        => EmulatedDevice.WarpsPerMultiprocessor * 32
    case 41        => 1
    case 75        => 7
    case _         => 0
  }

  private inline val GridScope = 1L

  // device-side launches run where they are made, so every created stream is the same handle
  private inline val StreamHandle      = 0x7fffffffffffeL
  private inline val StreamNonBlocking = 1L
  private inline val InvalidValue      = 1L

  // The driver's opaque handle to the grid of a cooperative launch.
  private inline val GridHandle = 0x7ffffffffffffL

  def execute(
      context: VMContext,
      lane: WorkgroupLane,
      builtin: Builtin,
      returnParameters: Vec[Parameter],
      parameters: Vec[Parameter]
  ): Either[Fault, Unit] = {
    val storage                                             = context.lane(lane)
    def parameter(index: Int, size: Int in 0 ~ 8 = 8): Long =
      parameters.lift(index).fold(0L)(storage.readParameter(_, 0, size))
    def returnValue(value: Long): Unit = returnParameters.headOption.foreach(storage.writeParameter(_, 0, 64, value))
    def output(value: Long): Either[Fault, Unit] =
      if (parameters.nonEmpty) VMMemoryAccess.storeGeneric(context, lane, parameter(0), 32, value) else Right(())
    builtin match {
      case Builtin.GetDevice       => output(0L).map(_ => returnValue(0L))
      case Builtin.DeviceAttribute =>
        output(deviceAttribute(parameter(1, 4).toInt).toLong).map(_ => returnValue(0L))
      case Builtin.Occupancy       => output(1L).map(_ => returnValue(0L))
      case Builtin.NoError         => Right(returnValue(0L))
      case Builtin.Printf          => printf(context, lane, parameter(0), parameter(1)).map(returnValue(_))
      case Builtin.ParameterBuffer =>
        // The arguments are the requested alignment and the child launch's argument block size.
        val size = Math.max(1L, parameter(1)).min(Int.MaxValue.toLong).toInt
        Right(returnValue(context.workgroup.allocateBuffer(Array.ofDim[Byte](size))))
      case Builtin.Malloc =>
        heap(context).flatMap(ContextHeap.allocate(context.device.memory, _, parameter(0))).map(returnValue(_))
      case Builtin.AssertFail =>
        Left(Fault.Assertion(assertionMessage(
          context,
          lane,
          parameter(0),
          parameter(1),
          parameter(2, 4),
          parameter(3)
        )))
      case Builtin.Free              => heap(context).flatMap(ContextHeap.free(context.device.memory, _, parameter(0)))
      case Builtin.LaunchDevice      => launch(context, lane, parameters, parameter(_, _)).map(_ => returnValue(0L))
      case Builtin.ParameterBufferV2 =>
        parameterBufferV2(context, storage, parameters, parameter(_, _)).map(returnValue(_))
      case Builtin.StreamCreate =>
        if (parameter(1, 4) != StreamNonBlocking) Right(returnValue(InvalidValue))
        else VMMemoryAccess.storeGeneric(context, lane, parameter(0), 64, StreamHandle).map(_ => returnValue(0L))
      case Builtin.LaunchDeviceV2 => launchV2(context, parameter(0)).map(_ => returnValue(0L))
      case Builtin.GridHandle     =>
        Right(returnValue(if (parameter(0, 4) == GridScope && context.device.gridWorkspace != 0L) GridHandle else 0L))
    }
  }

  private def printf(context: VMContext, lane: WorkgroupLane, format: Long, arguments: Long): Either[Fault, Long] = {
    def load(address: Long, width: ScalarBitWidth): Either[Fault, Long] =
      VMMemoryAccess.loadValue(context, lane, MemoryAccess.Generic(false), address, width).map(_ => context.scalar.elem)
    val byte: VMPrintf.ReadByte = load(_, 8).map(_.toInt)
    VMPrintf.decode(
      byte,
      format,
      (offset, size) => load(arguments + offset, (size * 8).unsafe[ScalarBitWidth]),
      VMPrintf.string(byte, _, VMPrintf.MaxStringBytes)
    ).map {
      case Some(decoded) =>
        context.workgroup.prints += decoded.event
        decoded.arguments.toLong
      case None => -1L
    }
  }

  private def assertionMessage(
      context: VMContext,
      lane: WorkgroupLane,
      expression: Long,
      file: Long,
      line: Long,
      function: Long
  )
      : NonEmpty[String] = {
    def text(address: Long): String         = VMMemoryAccess.readString(context, lane, address)
    def triple(read: Axis => Value): String =
      Axis.values.map(axis => context.read(lane, read(axis))).mkString("[", ",", "]")
    s"${text(file)}:$line: ${text(function)}: block: ${triple(Value.BlockId(_))}, thread: ${triple(Value.ThreadId(_))} " +
      s"Assertion `${text(expression)}` failed."
  }

  private def heap(context: VMContext): Either[Fault, HeapRegion] =
    context.device.heap.toRight(
      Fault.Unsupported("device-side malloc needs the context's heap, which the launch lacks")
    )

  private def bufferRange(name: String, buffer: Long): Fault.Backend =
    Fault.Backend(s"$name parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range")

  private def positive(value: Long): Either[Fault, Int > 0] =
    value.toInt.refine[Int > 0].left.map(_ => Fault.Backend(s"invalid launch dimension: $value"))

  private def dimensions(storage: VMLane, parameter: Parameter): Either[Fault, Dimensions] = {
    def axis(index: Int): Either[Fault, Int > 0] =
      positive(storage.readParameter(parameter, (index * 4).unsafe[Int >= 0], 4))
    (axis(0), axis(1), axis(2)).mapN(Dimensions(_, _, _))
  }

  private inline def launch(
      context: VMContext,
      lane: WorkgroupLane,
      parameters: Vec[Parameter],
      inline parameter: (Int, Int in 0 ~ 8) => Long
  ): Either[Fault, Unit] =
    if (parameters.size < 6)
      Left(Fault.Backend(s"cudaLaunchDevice expects six parameters, found ${parameters.size}"))
    else {
      val target  = context.kernel.functionTable.name(parameter(0, 8))
      val storage = context.lane(lane)
      for {
        child <- target.toRight(Fault.Backend("cudaLaunchDevice target is not a lowered entry: <unknown>"))
          .flatMap(context.kernel.launchTarget)
        buffer = parameter(1, 8)
        block <-
          if (child.entry.blockSize == 0) Right(Array.emptyByteArray)
          else context.workgroup.bufferBytes(buffer, child.entry.blockSize).toRight(
            bufferRange("cudaLaunchDevice", buffer)
          )
        groupCount <- dimensions(storage, parameters(2))
        groupSize  <- dimensions(storage, parameters(3))
        _          <- run(context, child, block, groupCount, groupSize, parameter(4, 4))
      } yield ()
    }

  private def run(
      context: VMContext,
      child: Kernel,
      block: Array[Byte],
      groupCount: Dimensions,
      groupSize: Dimensions,
      sharedBytes: Long
  ): Either[Fault, Unit] =
    for {
      geometry <- LaunchGeometry.forLaunch(groupSize, groupCount).left.map(Fault.Backend(_))
      shared   <- sharedBytes.toInt.refine[Int >= 0].left.map(_ =>
        Fault.Backend("child dynamic shared size exceeds the supported range")
      )
      _ <- context.launch(
        child,
        geometry,
        block,
        context.device.copy(handler = event => { context.workgroup.prints += event; Right(()) }),
        shared
      )
    } yield ()

  // a V2 parameter buffer follows the launch it was requested for: function descriptor, grid, block and dynamic shared
  private inline val RequestBytes = 64

  private inline def parameterBufferV2(
      context: VMContext,
      storage: VMLane,
      parameters: Vec[Parameter],
      inline parameter: (Int, Int in 0 ~ 8) => Long
  ): Either[Fault, Long] =
    if (parameters.size < 4)
      Left(Fault.Backend(s"cudaGetParameterBufferV2 expects four parameters, found ${parameters.size}"))
    else {
      val descriptor = parameter(0, 8)
      for {
        target <- context.kernel.functionTable.name(descriptor).toRight(
          Fault.Backend(
            s"cudaGetParameterBufferV2 target 0x${java.lang.Long.toUnsignedString(descriptor, 16)} is not a function"
          )
        )
        child <- context.kernel.launchTarget(target)
      } yield {
        val request = ByteBuffer.allocate(RequestBytes + child.entry.blockSize).order(ByteOrder.LITTLE_ENDIAN)
        request.putLong(0, descriptor)
        for (axis <- 0 until 6)
          request.putInt(
            8 + axis * 4,
            storage.readParameter(parameters(1 + axis / 3), ((axis % 3) * 4).unsafe[Int >= 0], 4).toInt
          )
        request.putInt(32, parameter(3, 4).toInt)
        context.workgroup.allocateBuffer(request.array()) + RequestBytes
      }
    }

  private def launchV2(context: VMContext, buffer: Long): Either[Fault, Unit] =
    for {
      request <- context.workgroup.bufferBytes(buffer - RequestBytes, RequestBytes).toRight(
        Fault.Backend(
          s"cudaLaunchDeviceV2 parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range"
        )
      )
      header = ByteBuffer.wrap(request).order(ByteOrder.LITTLE_ENDIAN)
      target <- context.kernel.functionTable.name(
        header.getLong(0)
      ).toRight(Fault.Backend("cudaLaunchDeviceV2 parameter buffer has no target"))
      child <- context.kernel.launchTarget(target)
      block <-
        if (child.entry.blockSize == 0) Right(Array.emptyByteArray)
        else context.workgroup.bufferBytes(buffer, child.entry.blockSize).toRight(
          bufferRange("cudaLaunchDeviceV2", buffer)
        )
      groupCount <-
        (positive(header.getInt(8)), positive(header.getInt(12)), positive(header.getInt(16))).mapN(Dimensions(_, _, _))
      groupSize <- (
        positive(header.getInt(20)),
        positive(header.getInt(24)),
        positive(header.getInt(28))
      ).mapN(Dimensions(_, _, _))
      _ <- run(context, child, block, groupCount, groupSize, header.getInt(32).toLong)
    } yield ()
}
