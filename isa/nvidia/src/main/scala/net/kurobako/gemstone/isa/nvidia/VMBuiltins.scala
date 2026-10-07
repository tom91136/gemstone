package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** CUDA device-runtime calls, executed per lane on the caller's `.param` storage. */
object VMBuiltins {

  // Attribute values of the emulated device, by cudaDeviceAttr ordinal.
  private def deviceAttribute(attribute: Int): Int = attribute match {
    case 1 | 2 | 3 | 39 => 1024
    case 4              => 64
    case 5              => Int.MaxValue
    case 6 | 7          => 65535
    case 8              => 49152
    case 10             => 32
    case 16             => 1
    case 41             => 1
    case 75             => 7
    case _              => 0
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
    def output(index: Int, value: Long): Either[Fault, Unit] =
      if (index < parameters.size) VMSemantics.storeGeneric(context, lane, parameter(index), 32, value) else Right(())
    builtin match {
      case Builtin.GetDevice       => output(0, 0L).map(_ => returnValue(0L))
      case Builtin.DeviceAttribute =>
        output(0, deviceAttribute(parameter(1, 4).toInt).toLong).map(_ => returnValue(0L))
      case Builtin.Occupancy       => output(0, 1L).map(_ => returnValue(0L))
      case Builtin.NoError         => Right(returnValue(0L))
      case Builtin.ParameterBuffer =>
        // The arguments are the requested alignment and the child launch's argument block size.
        val size = Math.max(1L, parameter(1)).min(Int.MaxValue.toLong).toInt
        Right(returnValue(context.workgroup.allocateBuffer(Array.ofDim[Byte](size))))
      case Builtin.Malloc =>
        parameter(0).refine[Long in 1L ~ 2147483647L].left.map(_ =>
          Fault.Backend(s"invalid malloc size: ${parameter(0)}")
        )
          .flatMap(size => context.memory.allocate(size.raw.toInt.unsafe[Int > 0]))
          .map(address => returnValue(address.raw))
      case Builtin.Free =>
        val _ = context.memory.free(GuestAddress(parameter(0)))
        Right(())
      case Builtin.LaunchDevice => launch(context, lane, parameters, parameter(_, _)).map(_ => returnValue(0L))
    }
  }

  private def dimensions(storage: VMLane, parameter: Parameter): Either[Fault, Dimensions] = {
    def axis(index: Int): Either[Fault, Int > 0] = {
      val value = storage.readParameter(parameter, (index * 4).unsafe[Int >= 0], 4)
      value.toInt.refine[Int > 0].left.map(_ => Fault.Backend(s"invalid launch dimension: $value"))
    }
    (axis(0), axis(1), axis(2)).mapN(Dimensions(_, _, _))
  }

  private def launch(
      context: VMContext,
      lane: WorkgroupLane,
      parameters: Vec[Parameter],
      parameter: (Int, Int in 0 ~ 8) => Long
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
          if (child.blockSize.raw == 0) Right(Array.emptyByteArray)
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
        _ <- VMInterpreter.launch(
          context.kernel.copy(entry = child),
          geometry,
          block,
          context.memory,
          context.control,
          context.workgroups,
          shared
        )
      } yield ()
    }
}
