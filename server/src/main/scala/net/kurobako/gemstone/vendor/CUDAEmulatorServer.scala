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
import net.kurobako.gemstone.binary.ByteRange
import net.kurobako.gemstone.binary.LittleEndian
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

import java.util.concurrent.atomic.AtomicReference
import scala.annotation.tailrec
import scala.collection.immutable.Queue

/** JVM/Native logical CUDA server used by both in-process tests and the future isolated transport. */
trait CUDAKernelExecution {
  def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule]
  def cuModuleLoadData(
      image: ModuleImage,
      allocateGlobal: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
      freeGlobal: DevicePtr => CUDAResult[Unit]
  ): CUDAResult[CUDAModule] = {
    val _ = (allocateGlobal, freeGlobal)
    cuModuleLoadData(image)
  }
  def cuModuleGetGlobal(
      module: CUDAModule,
      name: FunctionName
  ): CUDAResult[(DevicePtr, SizeT)]
  def cuModuleRegisterGlobal(
      module: CUDAModule,
      name: FunctionName,
      initial: Array[Byte],
      allocateGlobal: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
      freeGlobal: DevicePtr => CUDAResult[Unit]
  ): CUDAResult[Unit]
  def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction]
  def cuModuleUnload(module: CUDAModule): CUDAResult[Unit]
  def cuLaunchKernel(function: CUDAFunction, launch: KernelLaunch, memory: CheckedMemory): CUDAResult[Unit]
}

object CUDAKernelExecution {
  private object UnsupportedExecution extends CUDAKernelExecution {
    override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] = failure("cuModuleLoadData")
    override def cuModuleGetGlobal(
        module: CUDAModule,
        name: FunctionName
    ): CUDAResult[(DevicePtr, SizeT)] = failure("cuModuleGetGlobal")
    override def cuModuleRegisterGlobal(
        module: CUDAModule,
        name: FunctionName,
        initial: Array[Byte],
        allocateGlobal: (SizeT, Array[Byte]) => CUDAResult[DevicePtr],
        freeGlobal: DevicePtr => CUDAResult[Unit]
    ): CUDAResult[Unit] = failure("cuModuleRegisterGlobal")
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

  val Unsupported: CUDAKernelExecution = UnsupportedExecution
}

final class CUDAEmulatorServer(kernelExecution: CUDAKernelExecution = CUDAKernelExecution.Unsupported)
    extends CUDA {
  private type PendingCallback = () => Either[CallbackFailure, Unit]
  private final case class State(
      allocations: Map[Long, Array[Byte]],
      nextAddress: Long,
      contextLive: Boolean,
      pendingCallbacks: Queue[PendingCallback]
  )

  private val state = AtomicReference(State(Map.empty, 0x100000000L, false, Queue.empty))

  private def update[A](transition: State => (State, A)): A = synchronized {
    val (next, result) = transition(state.get())
    state.set(next)
    result
  }

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

  private def createContext(context: Out[Context], device: Device): CUDAStatus = update { current =>
    if (device.ordinal != 0) current -> CUDAStatus.InvalidDevice
    else {
      context.write(Context(1L))
      current.copy(contextLive = true) -> CUDAStatus.Success
    }
  }

  override def cuCtxDestroy_v2(context: Context): CUDAStatus = {
    val callbacks = drainCallbacks()
    if (!callbacks.isSuccess) callbacks
    else
      update { current =>
        if (!current.contextLive || context.handle != 1L) current -> CUDAStatus.InvalidContext
        else current.copy(contextLive = false)                    -> CUDAStatus.Success
      }
  }

  override def cuCtxSynchronize(): CUDAStatus = drainCallbacks()

  override def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus = synchronized {
    val current = state.get()
    if (!current.contextLive) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default) CUDAStatus.InvalidValue
    else {
      state.set(current.copy(pendingCallbacks = current.pendingCallbacks.enqueue(() => fn(Tuple1(userData)))))
      CUDAStatus.Success
    }
  }

  override def cuStreamAddCallback(
      hStream: Stream,
      callback: CUstreamCallback,
      userData: CallbackData,
      flags: UInt32
  ): CUDAStatus = synchronized {
    val current = state.get()
    if (!current.contextLive) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default || flags.toLong != 0L) CUDAStatus.InvalidValue
    else {
      state.set(
        current.copy(pendingCallbacks =
          current.pendingCallbacks.enqueue(() => callback((hStream, CUDAStatus.Success, userData)))
        )
      )
      CUDAStatus.Success
    }
  }

  private def drainCallbacks(): CUDAStatus = {
    def take(): Option[PendingCallback] = update { current =>
      current.pendingCallbacks.dequeueOption match {
        case Some((callback, remaining)) => current.copy(pendingCallbacks = remaining) -> Some(callback)
        case None                        => current                                    -> None
      }
    }
    @tailrec
    def loop(): CUDAStatus = take() match {
      case None           => CUDAStatus.Success
      case Some(callback) =>
        callback() match {
          case Right(()) => loop()
          case Left(_)   => CUDAStatus.Unknown
        }
    }
    loop()
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
      bytes.indices.foreach(index => allocation(offset + index) = bytes(index))
    })
  }

  override def cuMemcpyDtoH_v2(
      destination: OutputBuffer[Byte, "ByteCount"],
      source: DevicePtr,
      byteCount: SizeT
  ): CUDAStatus = synchronized {
    if (destination.length.toLong != byteCount.toLong) CUDAStatus.InvalidValue
    else status(checkedSize(byteCount).flatMap(size => resolve(source, size)).map { case (allocation, offset) =>
      (0 until byteCount.toLong.toInt).foreach(index => destination.write(allocation(offset + index), index))
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
    writeOutcome(
      module,
      kernelExecution.cuModuleLoadData(image, (bytesize, initial) => allocate(bytesize, initial), free)
    )

  override def cuModuleGetGlobal_v2(
      devicePointer: Out[DevicePtr],
      bytes: Out[SizeT],
      module: CUDAModule,
      name: FunctionName
  ): CUDAStatus = kernelExecution.cuModuleGetGlobal(module, name) match {
    case CUDAResult.Success((pointer, size)) =>
      devicePointer.write(pointer)
      bytes.write(size)
      CUDAStatus.Success
    case CUDAResult.Failure(error) => CUDAStatus(error.code)
  }

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
      val current  = state.get()
      val overlaps = current.allocations.exists { case (base, allocation) =>
        val end        = Math.addExact(pointer.address, size.toLong)
        val currentEnd = Math.addExact(base, allocation.length.toLong)
        pointer.address < currentEnd && base < end
      }
      if (size == 0 || overlaps)
        failure(1, s"invalid managed allocation at 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
      else {
        state.set(current.copy(allocations = current.allocations.updated(pointer.address, Array.ofDim[Byte](size))))
        success(())
      }
    })
  }

  override def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  override def __cudaGemstoneRegisterGlobal(
      module: CUDAModule,
      name: FunctionName,
      initial: ModuleImage
  ): CUDAStatus = status(
    kernelExecution.cuModuleRegisterGlobal(module, name, Col.toArray(initial.bytes), allocate, free)
  )

  override def cudaGetDeviceCount(count: Out[Int]): CUDAStatus = cuDeviceGetCount(count)

  override def cudaMalloc(pointer: Out[DevicePtr], bytesize: SizeT): CUDAStatus =
    writeOutcome(pointer, allocate(bytesize))

  override def cudaFree(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  private def allocate(bytesize: SizeT): CUDAResult[DevicePtr] = allocate(bytesize, Array.empty[Byte])

  private def allocate(bytesize: SizeT, initial: Array[Byte]): CUDAResult[DevicePtr] = synchronized {
    checkedSize(bytesize).flatMap { size =>
      if (size == 0) failure(1, "zero-byte allocation")
      else {
        val current = state.get()
        val base    = current.nextAddress
        val span    = (size.toLong + 0xfffL) & ~0xfffL
        val bytes   = Array.ofDim[Byte](size)
        Array.copy(initial, 0, bytes, 0, Math.min(initial.length, bytes.length))
        state.set(
          current.copy(
            nextAddress = Math.addExact(current.nextAddress, span.max(0x1000L)),
            allocations = current.allocations.updated(base, bytes)
          )
        )
        success(DevicePtr(base))
      }
    }
  }

  private def free(pointer: DevicePtr): CUDAResult[Unit] = synchronized {
    val current = state.get()
    if (current.allocations.contains(pointer.address)) {
      state.set(current.copy(allocations = current.allocations.removed(pointer.address)))
      success(())
    } else failure(1, s"invalid device pointer: 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
  }

  private def checkedSize(size: SizeT): CUDAResult[Int] =
    if (size.toLong > Int.MaxValue.toLong) failure(2, s"allocation exceeds emulator limit: ${size.toLong}")
    else success(size.toLong.toInt)

  private def resolve(pointer: DevicePtr, size: Int): CUDAResult[(Array[Byte], Int)] = {
    val address = pointer.address
    state.get().allocations.iterator.collectFirst {
      case (base, allocation)
          if address >= base && ByteRange.contains(allocation.length, address - base, size.toLong) =>
        allocation -> (address - base).toInt
    } match {
      case Some(resolved) => success(resolved)
      case None => failure(1, s"invalid device range at 0x${java.lang.Long.toUnsignedString(address, 16)} + $size")
    }
  }

  private final class DeviceMemory extends CheckedMemory {
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
        LittleEndian.decode(bytes, offset, 4).toInt
      } match {
        case CUDAResult.Success(value) => Right(value)
        case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
      }
    }

    override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
      CUDAEmulatorServer.this.synchronized {
        resolve(DevicePtr(address.value), 4).map { case (bytes, offset) =>
          LittleEndian.write(value.toLong, bytes, offset, 4)
        } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
        }
      }

    override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
      CUDAEmulatorServer.this.synchronized {
        resolve(DevicePtr(address.value), 1).map { case (bytes, offset) =>
          bytes(offset) = value
        } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 1, error.message))
        }
      }

    override def free(): Unit = ()
  }

  private val deviceMemory: CheckedMemory = DeviceMemory()

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
