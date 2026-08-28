package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.Col

import net.kurobako.gemstone.abi.InOut
import net.kurobako.gemstone.abi.CallbackData
import net.kurobako.gemstone.abi.CallbackFailure
import net.kurobako.gemstone.abi.Nullable
import net.kurobako.gemstone.abi.OpaquePointer
import net.kurobako.gemstone.abi.Opaque
import net.kurobako.gemstone.abi.Out
import net.kurobako.gemstone.abi.Pointer
import net.kurobako.gemstone.abi.ReadWrite
import net.kurobako.gemstone.abi.InputBuffer
import net.kurobako.gemstone.abi.OutputBuffer
import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.cuda.Context
import net.kurobako.gemstone.cuda.CUDAFunction
import net.kurobako.gemstone.cuda.CUDAModule
import net.kurobako.gemstone.cuda.CUDAError as RPCCUDAError
import net.kurobako.gemstone.cuda.CUDAResult
import net.kurobako.gemstone.cuda.CUDAStatus
import net.kurobako.gemstone.cuda.CUctxCreateParams
import net.kurobako.gemstone.cuda.CUhostFn
import net.kurobako.gemstone.cuda.CUstreamCallback
import net.kurobako.gemstone.cuda.Device
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.FunctionName
import net.kurobako.gemstone.cuda.KernelFunction
import net.kurobako.gemstone.cuda.KernelLaunch
import net.kurobako.gemstone.cuda.KernelParameters
import net.kurobako.gemstone.cuda.ModuleImage
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.cuda.UInt8
import net.kurobako.gemstone.cuda.UInt32
import net.kurobako.gemstone.cuda.Stream
import net.kurobako.gemstone.memory.Allocation
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryError

import scala.collection.mutable

/** JVM/Native logical CUDA server used by both in-process tests and the future isolated transport. */
trait CUDAKernelExecution {
  def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule]
  def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction]
  def cuModuleUnload(module: CUDAModule): CUDAResult[Unit]
  def cuLaunchKernel(function: CUDAFunction, launch: KernelLaunch, memory: CheckedMemory): CUDAResult[Unit]
}

object CUDAKernelExecution {
  val Unsupported: CUDAKernelExecution = new CUDAKernelExecution {
    override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] = failure("cuModuleLoadData")
    override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] =
      failure("cuModuleGetFunction")
    override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] = failure("cuModuleUnload")
    override def cuLaunchKernel(
        function: CUDAFunction,
        launch: KernelLaunch,
        memory: CheckedMemory
    ): CUDAResult[Unit] = failure("cuLaunchKernel")

    private def failure[A](name: String): CUDAResult[A] =
      CUDAResult.Failure(RPCCUDAError(801, s"$name is not implemented"))
  }
}

final class CUDAEmulatorServer(kernelExecution: CUDAKernelExecution = CUDAKernelExecution.Unsupported)
    extends CUDA {
  private val allocations      = mutable.Map.empty[Long, Array[Byte]]
  private var nextAddress      = 0x100000000L
  private var contextLive      = false
  private val pendingCallbacks = mutable.ArrayDeque.empty[() => Either[CallbackFailure, Unit]]

  override def cuInit(flags: UInt32): CUDAStatus = CUDAStatus.Success

  override def cuDriverGetVersion(driverVersion: Out[Int]): CUDAStatus = {
    driverVersion.write(13030)
    CUDAStatus.Success
  }

  override def cuDeviceGetCount(count: Out[Int]): CUDAStatus = {
    count.write(1)
    CUDAStatus.Success
  }

  override def cuDeviceGet(device: Out[Device], ordinal: Int): CUDAStatus =
    if (ordinal == 0) {
      device.write(Device(0))
      CUDAStatus.Success
    } else CUDAStatus.InvalidDevice

  override def cuCtxCreate_v4(
      context: Out[Context],
      createParams: Nullable[InOut[CUctxCreateParams]],
      flags: UInt32,
      device: Device
  ): CUDAStatus = createContext(context, device)

  override def cuCtxCreate_v2(context: Out[Context], flags: UInt32, device: Device): CUDAStatus =
    createContext(context, device)

  private def createContext(context: Out[Context], device: Device): CUDAStatus = synchronized {
    if (device.ordinal != 0) CUDAStatus.InvalidDevice
    else {
      contextLive = true
      context.write(Context(1L))
      CUDAStatus.Success
    }
  }

  override def cuCtxDestroy_v2(context: Context): CUDAStatus = {
    val callbacks = drainCallbacks()
    if (!callbacks.isSuccess) callbacks
    else
      synchronized {
        if (!contextLive || context.handle != 1L) CUDAStatus.InvalidContext
        else {
          contextLive = false
          CUDAStatus.Success
        }
      }
  }

  override def cuCtxSynchronize(): CUDAStatus = drainCallbacks()

  override def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus = synchronized {
    if (!contextLive) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default) CUDAStatus.InvalidValue
    else {
      pendingCallbacks.append(() => fn(Tuple1(userData)))
      CUDAStatus.Success
    }
  }

  override def cuStreamAddCallback(
      hStream: Stream,
      callback: CUstreamCallback,
      userData: CallbackData,
      flags: UInt32
  ): CUDAStatus = synchronized {
    if (!contextLive) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default || flags.toLong != 0L) CUDAStatus.InvalidValue
    else {
      pendingCallbacks.append(() => callback((hStream, CUDAStatus.Success, userData)))
      CUDAStatus.Success
    }
  }

  private def drainCallbacks(): CUDAStatus = {
    var result = CUDAStatus.Success
    var next   = synchronized(pendingCallbacks.removeHeadOption())
    while (result.isSuccess && next.nonEmpty) {
      result = next.get() match {
        case Right(()) => CUDAStatus.Success
        case Left(_)   => CUDAStatus.Unknown
      }
      next = synchronized(pendingCallbacks.removeHeadOption())
    }
    result
  }

  override def cuMemAlloc_v2(pointer: Out[DevicePtr], bytesize: SizeT): CUDAStatus =
    writeOutcome(pointer, allocate(bytesize))

  override def cuMemFree_v2(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  override def cuMemcpyHtoD_v2(
      destination: DevicePtr,
      source: InputBuffer[Byte, "ByteCount"],
      byteCount: SizeT
  ): CUDAStatus = synchronized {
    val bytes = source.snapshot.getOrElse(Col.empty[Byte])
    if (bytes.length.toLong != byteCount.toLong) CUDAStatus.InvalidValue
    else status(resolve(destination, bytes.length).map { case (allocation, offset) =>
      var index = 0
      while (index < bytes.length) {
        allocation(offset + index) = bytes(index)
        index += 1
      }
    })
  }

  override def cuMemcpyDtoH_v2(
      destination: OutputBuffer[Byte, "ByteCount"],
      source: DevicePtr,
      byteCount: SizeT
  ): CUDAStatus = synchronized {
    if (destination.length.toLong != byteCount.toLong) CUDAStatus.InvalidValue
    else status(checkedSize(byteCount).flatMap(size => resolve(source, size)).map { case (allocation, offset) =>
      var index = 0
      while (index < byteCount.toLong.toInt) {
        destination.write(allocation(offset + index), index)
        index += 1
      }
    })
  }

  override def cuMemcpyDtoD_v2(
      destination: DevicePtr,
      source: DevicePtr,
      byteCount: SizeT
  ): CUDAStatus = synchronized {
    status(checkedSize(byteCount).flatMap { size =>
      resolve(source, size).flatMap { case (sourceAllocation, sourceOffset) =>
        resolve(destination, size).map { case (destinationAllocation, destinationOffset) =>
          Array.copy(sourceAllocation, sourceOffset, destinationAllocation, destinationOffset, size)
        }
      }
    })
  }

  override def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CUDAStatus = synchronized {
    status(checkedSize(count).flatMap(size => resolve(destination, size)).map { case (allocation, offset) =>
      java.util.Arrays.fill(allocation, offset, offset + count.toLong.toInt, value.toInt.toByte)
    })
  }

  override def cuModuleLoadData(module: Out[CUDAModule], image: ModuleImage): CUDAStatus =
    writeOutcome(module, kernelExecution.cuModuleLoadData(image))

  override def cuModuleGetFunction(
      function: Out[KernelFunction],
      module: CUDAModule,
      name: FunctionName
  ): CUDAStatus = writeOutcome(function, kernelExecution.cuModuleGetFunction(module, name))

  override def cuModuleUnload(module: CUDAModule): CUDAStatus = status(kernelExecution.cuModuleUnload(module))

  override def cuLaunchKernel(
      function: CUDAFunction,
      gridX: UInt32,
      gridY: UInt32,
      gridZ: UInt32,
      blockX: UInt32,
      blockY: UInt32,
      blockZ: UInt32,
      sharedMemoryBytes: UInt32,
      stream: Stream,
      parameters: KernelParameters,
      extra: Nullable[Pointer[OpaquePointer, ReadWrite, Opaque]]
  ): CUDAStatus = status(
    kernelExecution.cuLaunchKernel(
      function,
      KernelLaunch(
        gridX,
        gridY,
        gridZ,
        blockX,
        blockY,
        blockZ,
        sharedMemoryBytes,
        parameters.arguments
      ),
      deviceMemory
    )
  )

  override def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus = synchronized {
    status(checkedSize(bytesize).flatMap { size =>
      val overlaps = allocations.exists { case (base, allocation) =>
        val end        = Math.addExact(pointer.address, size.toLong)
        val currentEnd = Math.addExact(base, allocation.length.toLong)
        pointer.address < currentEnd && base < end
      }
      if (size == 0 || overlaps)
        failure(1, s"invalid managed allocation at 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
      else {
        allocations(pointer.address) = new Array[Byte](size)
        success(())
      }
    })
  }

  override def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  override def cudaGetDeviceCount(count: Out[Int]): CUDAStatus = cuDeviceGetCount(count)

  override def cudaMalloc(pointer: Out[DevicePtr], bytesize: SizeT): CUDAStatus =
    writeOutcome(pointer, allocate(bytesize))

  override def cudaFree(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  private def allocate(bytesize: SizeT): CUDAResult[DevicePtr] = synchronized {
    checkedSize(bytesize).flatMap { size =>
      if (size == 0) failure(1, "zero-byte allocation")
      else {
        val base = nextAddress
        val span = (size.toLong + 0xfffL) & ~0xfffL
        nextAddress = Math.addExact(nextAddress, span.max(0x1000L))
        allocations(base) = new Array[Byte](size)
        success(DevicePtr(base))
      }
    }
  }

  private def free(pointer: DevicePtr): CUDAResult[Unit] = synchronized {
    if (allocations.remove(pointer.address).isDefined) success(())
    else failure(1, s"invalid device pointer: 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
  }

  private def checkedSize(size: SizeT): CUDAResult[Int] =
    if (size.toLong > Int.MaxValue.toLong) failure(2, s"allocation exceeds emulator limit: ${size.toLong}")
    else success(size.toLong.toInt)

  private def resolve(pointer: DevicePtr, size: Int): CUDAResult[(Array[Byte], Int)] = {
    val address = pointer.address
    allocations.iterator.collectFirst {
      case (base, allocation)
          if address >= base && address - base <= allocation.length.toLong && size.toLong <= allocation.length.toLong - (address - base) =>
        allocation -> (address - base).toInt
    } match {
      case Some(resolved) => success(resolved)
      case None => failure(1, s"invalid device range at 0x${java.lang.Long.toUnsignedString(address, 16)} + $size")
    }
  }

  private val deviceMemory: CheckedMemory = new CheckedMemory {
    override val allocation: Allocation =
      Allocation(GuestAddress(0L), Int.MaxValue, AllocationKind.Device, "CUDA allocation registry")

    override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = CUDAEmulatorServer.this.synchronized {
      resolve(DevicePtr(address.value), 1).map { case (bytes, offset) => bytes(offset) } match {
        case CUDAResult.Success(value) => Right(value)
        case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 1, error.message))
      }
    }

    override def readInt32(address: GuestAddress): Either[MemoryError, Int] = CUDAEmulatorServer.this.synchronized {
      resolve(DevicePtr(address.value), 4).map { case (bytes, offset) =>
        (bytes(offset) & 0xff) |
          ((bytes(offset + 1) & 0xff) << 8) |
          ((bytes(offset + 2) & 0xff) << 16) |
          ((bytes(offset + 3) & 0xff) << 24)
      } match {
        case CUDAResult.Success(value) => Right(value)
        case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
      }
    }

    override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
      CUDAEmulatorServer.this.synchronized {
        resolve(DevicePtr(address.value), 4).map { case (bytes, offset) =>
          bytes(offset) = value.toByte
          bytes(offset + 1) = (value >>> 8).toByte
          bytes(offset + 2) = (value >>> 16).toByte
          bytes(offset + 3) = (value >>> 24).toByte
        } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
        }
      }

    override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
      CUDAEmulatorServer.this.synchronized {
        resolve(DevicePtr(address.value), 1).map { case (bytes, offset) => bytes(offset) = value } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 1, error.message))
        }
      }

    override def free(): Unit = ()
  }

  private def status[A](result: CUDAResult[A]): CUDAStatus = result match {
    case CUDAResult.Success(_)     => CUDAStatus.Success
    case CUDAResult.Failure(error) => CUDAStatus(error.code)
  }

  private def writeOutcome[A](output: Out[A], result: CUDAResult[A]): CUDAStatus = result match {
    case CUDAResult.Success(value) =>
      output.write(value)
      CUDAStatus.Success
    case CUDAResult.Failure(error) => CUDAStatus(error.code)
  }

  private def success[A](value: A): CUDAResult[A]                   = CUDAResult.Success(value)
  private def failure[A](code: Int, message: String): CUDAResult[A] = CUDAResult.Failure(RPCCUDAError(code, message))
}
