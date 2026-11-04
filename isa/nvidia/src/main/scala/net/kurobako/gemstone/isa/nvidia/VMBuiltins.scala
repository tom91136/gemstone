package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import cats.syntax.all.*
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
      case Builtin.Free         => heap(context).flatMap(ContextHeap.free(context.device.memory, _, parameter(0)))
      case Builtin.LaunchDevice => launch(context, lane, parameters, parameter(_, _)).map(_ => returnValue(0L))
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

  private def dimensions(storage: VMLane, parameter: Parameter): Either[Fault, Dimensions] = {
    def axis(index: Int): Either[Fault, Int > 0] = {
      val value = storage.readParameter(parameter, (index * 4).unsafe[Int >= 0], 4)
      value.toInt.refine[Int > 0].left.map(_ => Fault.Backend(s"invalid launch dimension: $value"))
    }
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
        child <- target.flatMap(context.kernel.entries.get).toRight(
          Fault.Backend(s"cudaLaunchDevice target is not a lowered entry: ${target.getOrElse("<unknown>")}")
        )
        buffer = parameter(1, 8)
        block <-
          if (child.blockSize == 0) Right(Array.emptyByteArray)
          else context.workgroup.bufferBytes(buffer, child.blockSize).toRight(
            Fault.Backend(
              s"cudaLaunchDevice parameter buffer 0x${java.lang.Long.toUnsignedString(buffer, 16)} is out of range"
            )
          )
        groupCount <- dimensions(storage, parameters(2))
        groupSize  <- dimensions(storage, parameters(3))
        geometry   <- LaunchGeometry.forLaunch(groupSize, groupCount).left.map(Fault.Backend(_))
        shared     <- parameter(4, 4).toInt.refine[Int >= 0].left.map(_ =>
          Fault.Backend("child dynamic shared size exceeds the supported range")
        )
        _ <- context.launch(
          context.kernel.copy(entry = child),
          geometry,
          block,
          context.device.copy(handler = event => { context.workgroup.prints += event; Right(()) }),
          shared
        )
      } yield ()
    }
}
