package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.Col.*

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
import java.nio.charset.StandardCharsets
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

final class CUDAEmulator(kernelExecution: CUDAKernelExecution = CUDAKernelExecution.Unsupported)
    extends CUDA {
  private final case class PendingCallback(context: Long, run: () => Either[CallbackFailure, Unit])
  private final case class OwnedAllocation(bytes: Array[Byte], context: Long)
  private final case class State(
      allocations: Map[Long, OwnedAllocation],
      nextAddress: Long,
      contexts: Set[Long],
      nextContext: Long,
      modules: Map[Long, Long],
      functions: Map[Long, Long],
      pendingCallbacks: Queue[PendingCallback]
  )

  private val state = AtomicReference(State(Map.empty, 0x100000000L, Set.empty, 1L, Map.empty, Map.empty, Queue.empty))
  private val currentContext = ThreadLocal.withInitial[java.lang.Long](() => 0L)
  private val contextStack   = ThreadLocal.withInitial[Col[Long]](() => Col.empty)

  private[gemstone] def withContext[A](context: Long)(body: => A): A = {
    val previous = currentContext.get()
    currentContext.set(context)
    try body
    finally currentContext.set(previous)
  }

  private def liveContext(current: State): Option[Long] =
    Option(currentContext.get().longValue()).filter(current.contexts.contains)

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
    else if (current.nextContext <= 0L) current -> CUDAStatus.Unknown
    else {
      val handle = current.nextContext
      context.write(Context(handle))
      contextStack.set(contextStack.get() :+ currentContext.get().longValue())
      currentContext.set(handle)
      current.copy(contexts = current.contexts + handle, nextContext = handle + 1L) -> CUDAStatus.Success
    }
  }

  override def cuCtxDestroy_v2(context: Context): CUDAStatus = {
    val callbacks = drainCallbacks(context.handle)
    if (!callbacks.isSuccess) callbacks
    else
      update { current =>
        if (!current.contexts.contains(context.handle)) current -> CUDAStatus.InvalidContext
        else {
          current.modules.collect { case (module, owner) if owner == context.handle => CUDAModule(module) }
            .foreach(kernelExecution.cuModuleUnload)
          contextStack.set(contextStack.get().filterNot(_ == context.handle))
          if (currentContext.get().longValue() == context.handle) {
            val stack = contextStack.get()
            currentContext.set(stack.lastOption.getOrElse(0L))
            if (stack.nonEmpty) contextStack.set(stack.init)
          }
          val updated = state.get()
          updated.copy(
            contexts = updated.contexts - context.handle,
            allocations = updated.allocations.filterNot(_._2.context == context.handle),
            modules = updated.modules.filterNot(_._2 == context.handle),
            functions = updated.functions.filterNot(_._2 == context.handle)
          ) -> CUDAStatus.Success
        }
      }
  }

  override def cuCtxSynchronize(): CUDAStatus =
    liveContext(state.get()).fold(CUDAStatus.InvalidContext)(drainCallbacks)

  override def cuLaunchHostFunc(hStream: Stream, fn: CUhostFn, userData: CallbackData): CUDAStatus = synchronized {
    val current = state.get()
    val owner   = liveContext(current)
    if (owner.isEmpty) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default) CUDAStatus.InvalidValue
    else {
      state.set(current.copy(pendingCallbacks =
        current.pendingCallbacks.enqueue(PendingCallback(owner.get, () => fn(Tuple1(userData))))
      ))
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
    val owner   = liveContext(current)
    if (owner.isEmpty) CUDAStatus.InvalidContext
    else if (hStream != Stream.Default || flags.toLong != 0L) CUDAStatus.InvalidValue
    else {
      state.set(
        current.copy(pendingCallbacks =
          current.pendingCallbacks.enqueue(PendingCallback(
            owner.get,
            () => callback((hStream, CUDAStatus.Success, userData))
          ))
        )
      )
      CUDAStatus.Success
    }
  }

  private def drainCallbacks(context: Long): CUDAStatus = {
    def take(): Option[PendingCallback] = update { current =>
      val callbacks = current.pendingCallbacks.iterator.toCol
      callbacks.indexWhere(_.context == context) match {
        case -1    => current -> None
        case index =>
          val callback  = callbacks(index)
          val remaining = Queue.from(callbacks.patch(index, Nil, 1))
          current.copy(pendingCallbacks = remaining) -> Some(callback)
      }
    }
    @tailrec
    def loop(): CUDAStatus = take() match {
      case None           => CUDAStatus.Success
      case Some(callback) =>
        callback.run() match {
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
    synchronized {
      val current = state.get()
      liveContext(current).fold(CUDAStatus.InvalidContext) { owner =>
        kernelExecution.cuModuleLoadData(image, (bytesize, initial) => allocate(bytesize, initial), free) match {
          case CUDAResult.Success(value) =>
            module.write(value)
            state.set(state.get().copy(modules = state.get().modules.updated(value.handle, owner)))
            CUDAStatus.Success
          case CUDAResult.Failure(error) => CUDAStatus(error.code)
        }
      }
    }

  override def cuModuleGetGlobal_v2(
      devicePointer: Out[DevicePtr],
      bytes: Out[SizeT],
      module: CUDAModule,
      name: FunctionName
  ): CUDAStatus = if (!state.get().modules.get(module.handle).contains(currentContext.get().longValue()))
    CUDAStatus.InvalidContext
  else kernelExecution.cuModuleGetGlobal(module, name) match {
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
  ): CUDAStatus = synchronized {
    val current = state.get()
    if (!current.modules.get(module.handle).contains(currentContext.get().longValue())) CUDAStatus.InvalidContext
    else kernelExecution.cuModuleGetFunction(module, name) match {
      case CUDAResult.Success(value) =>
        function.write(value)
        state.set(current.copy(functions =
          current.functions.updated(value.handle.handle, currentContext.get().longValue())
        ))
        CUDAStatus.Success
      case CUDAResult.Failure(error) => CUDAStatus(error.code)
    }
  }

  override def cuModuleUnload(module: CUDAModule): CUDAStatus = synchronized {
    val current = state.get()
    if (!current.modules.get(module.handle).contains(currentContext.get().longValue())) CUDAStatus.InvalidContext
    else kernelExecution.cuModuleUnload(module) match {
      case CUDAResult.Success(_) =>
        val updated = state.get()
        state.set(updated.copy(modules = updated.modules - module.handle))
        CUDAStatus.Success
      case CUDAResult.Failure(error) => CUDAStatus(error.code)
    }
  }

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
  ): CUDAStatus = if (!state.get().functions.get(function.handle).contains(currentContext.get().longValue()))
    CUDAStatus.InvalidContext
  else {
    val context = currentContext.get().longValue()
    status(kernelExecution.cuLaunchKernel(
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
      DeviceMemory(context)
    ))
  }

  override def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAStatus = synchronized {
    status(checkedSize(bytesize).flatMap { size =>
      val current  = state.get()
      val overlaps = current.allocations.exists { case (base, allocation) =>
        val end        = Math.addExact(pointer.address, size.toLong)
        val currentEnd = Math.addExact(base, allocation.bytes.length.toLong)
        pointer.address < currentEnd && base < end
      }
      val owner = liveContext(current)
      if (owner.isEmpty) failure(201, "no current CUDA context")
      else if (size == 0 || overlaps)
        failure(1, s"invalid managed allocation at 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
      else {
        state.set(current.copy(
          allocations =
            current.allocations.updated(pointer.address, OwnedAllocation(Array.ofDim[Byte](size), owner.get))
        ))
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

  override def __cudaGemstoneResolveFunctions(
      result: Out[ModuleImage],
      module: CUDAModule,
      names: ModuleImage
  ): CUDAStatus = synchronized {
    val current = state.get()
    if (!current.modules.get(module.handle).contains(currentContext.get().longValue())) CUDAStatus.InvalidContext
    else {
      val input                            = Col.toArray(names.bytes)
      def uint32(offset: Int): Option[Int] = Option.when(offset >= 0 && input.length - offset >= 4)(
        (input(offset) & 0xff) << 24 | (input(offset + 1) & 0xff) << 16 |
          (input(offset + 2) & 0xff) << 8 | (input(offset + 3) & 0xff)
      )
      val decoded = uint32(0).filter(count => count >= 0 && count <= 4096).flatMap { count =>
        def loop(index: Int, offset: Int, values: Col[FunctionName]): Option[Col[FunctionName]] =
          if (index == count) Option.when(offset == input.length)(values)
          else
            uint32(offset) match {
              case Some(size) if size > 0 && size <= 4096 && offset <= input.length - 4 - size =>
                val value = String(input, offset + 4, size, StandardCharsets.UTF_8)
                Option.when(value.indexOf('\u0000') < 0)(FunctionName(value)).flatMap(name =>
                  loop(index + 1, offset + 4 + size, values :+ name)
                )
              case _ => None
            }
        loop(0, 4, Col.empty)
      }
      decoded.fold(CUDAStatus.InvalidValue) { requested =>
        val resolved = requested.foldLeft[CUDAResult[Col[KernelFunction]]](CUDAResult.Success(Col.empty)) {
          case (CUDAResult.Success(values), name) => kernelExecution.cuModuleGetFunction(module, name).map(values :+ _)
          case (failure @ CUDAResult.Failure(_), _) => failure
        }
        resolved match {
          case CUDAResult.Failure(error)     => CUDAStatus(error.code)
          case CUDAResult.Success(functions) =>
            val size = functions.foldLeft(4L)((total, function) =>
              Math.addExact(total, Math.addExact(12L, Math.multiplyExact(function.parameterWidths.size.toLong, 4L)))
            )
            if (size > Int.MaxValue) CUDAStatus.InvalidValue
            else {
              val bytes                                = Array.ofDim[Byte](size.toInt)
              def put32(offset: Int, value: Int): Unit = {
                bytes(offset) = (value >>> 24).toByte; bytes(offset + 1) = (value >>> 16).toByte
                bytes(offset + 2) = (value >>> 8).toByte; bytes(offset + 3) = value.toByte
              }
              def put64(offset: Int, value: Long): Unit = {
                put32(offset, (value >>> 32).toInt); put32(offset + 4, value.toInt)
              }
              put32(0, functions.size)
              val _ = functions.foldLeft(4) { (offset, function) =>
                put64(offset, function.handle.handle)
                put32(offset + 8, function.parameterWidths.size)
                function.parameterWidths.zipWithIndex.foreach { case (width, index) =>
                  put32(offset + 12 + index * 4, width)
                }
                offset + 12 + function.parameterWidths.size * 4
              }
              result.write(ModuleImage(Col.unsafeFromArray(bytes)))
              state.set(current.copy(functions =
                current.functions ++ functions.iterator.map(function =>
                  function.handle.handle -> currentContext.get().longValue()
                )
              ))
              CUDAStatus.Success
            }
        }
      }
    }
  }

  override def cudaGetDeviceCount(count: Out[Int]): CUDAStatus = cuDeviceGetCount(count)

  override def cudaMalloc(pointer: Out[DevicePtr], bytesize: SizeT): CUDAStatus = {
    ensureRuntimeContext()
    writeOutcome(pointer, allocate(bytesize))
  }

  override def cudaFree(pointer: DevicePtr): CUDAStatus = status(free(pointer))

  private def ensureRuntimeContext(): Unit = synchronized {
    if (liveContext(state.get()).isEmpty) {
      val current = state.get()
      val handle  = current.nextContext
      currentContext.set(handle)
      state.set(current.copy(contexts = current.contexts + handle, nextContext = handle + 1L))
    }
  }

  private def allocate(bytesize: SizeT): CUDAResult[DevicePtr] = allocate(bytesize, Array.empty[Byte])

  private def allocate(bytesize: SizeT, initial: Array[Byte]): CUDAResult[DevicePtr] = synchronized {
    checkedSize(bytesize).flatMap { size =>
      val current = state.get()
      val owner   = liveContext(current)
      if (owner.isEmpty) failure(201, "no current CUDA context")
      else if (size == 0) failure(1, "zero-byte allocation")
      else {
        val base  = current.nextAddress
        val span  = (size.toLong + 0xfffL) & ~0xfffL
        val bytes = Array.ofDim[Byte](size)
        Array.copy(initial, 0, bytes, 0, Math.min(initial.length, bytes.length))
        state.set(
          current.copy(
            nextAddress = Math.addExact(current.nextAddress, span.max(0x1000L)),
            allocations = current.allocations.updated(base, OwnedAllocation(bytes, owner.get))
          )
        )
        success(DevicePtr(base))
      }
    }
  }

  private def free(pointer: DevicePtr): CUDAResult[Unit] = synchronized {
    val current = state.get()
    if (current.allocations.get(pointer.address).exists(_.context == currentContext.get().longValue())) {
      state.set(current.copy(allocations = current.allocations.removed(pointer.address)))
      success(())
    } else failure(1, s"invalid device pointer: 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
  }

  private def checkedSize(size: SizeT): CUDAResult[Int] =
    if (size.toLong > Int.MaxValue.toLong) failure(2, s"allocation exceeds emulator limit: ${size.toLong}")
    else success(size.toLong.toInt)

  private def resolve(
      pointer: DevicePtr,
      size: Int,
      context: Long = currentContext.get().longValue()
  ): CUDAResult[(Array[Byte], Int)] = {
    val address = pointer.address
    state.get().allocations.iterator.collectFirst {
      case (base, allocation)
          if allocation.context == context && address >= base &&
            ByteRange.contains(allocation.bytes.length, address - base, size.toLong) =>
        allocation.bytes -> (address - base).toInt
    } match {
      case Some(resolved) => success(resolved)
      case None => failure(1, s"invalid device range at 0x${java.lang.Long.toUnsignedString(address, 16)} + $size")
    }
  }

  private final class DeviceMemory(context: Long) extends CheckedMemory {
    override val allocation: Allocation =
      Allocation(GuestAddress(0L), Int.MaxValue, AllocationKind.Device, "CUDA allocation registry")

    override def readInt8(address: GuestAddress): Either[MemoryError, Byte] = CUDAEmulator.this.synchronized {
      resolve(DevicePtr(address.value), 1, context).map { case (bytes, offset) => bytes(offset) } match {
        case CUDAResult.Success(value) => Right(value)
        case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 1, error.message))
      }
    }

    override def readInt32(address: GuestAddress): Either[MemoryError, Int] = CUDAEmulator.this.synchronized {
      resolve(DevicePtr(address.value), 4, context).map { case (bytes, offset) =>
        LittleEndian.decode(bytes, offset, 4).toInt
      } match {
        case CUDAResult.Success(value) => Right(value)
        case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
      }
    }

    override def writeInt32(address: GuestAddress, value: Int): Either[MemoryError, Unit] =
      CUDAEmulator.this.synchronized {
        resolve(DevicePtr(address.value), 4, context).map { case (bytes, offset) =>
          LittleEndian.write(value.toLong, bytes, offset, 4)
        } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
        }
      }

    override def writeInt8(address: GuestAddress, value: Byte): Either[MemoryError, Unit] =
      CUDAEmulator.this.synchronized {
        resolve(DevicePtr(address.value), 1, context).map { case (bytes, offset) =>
          bytes(offset) = value
        } match {
          case CUDAResult.Success(())    => Right(())
          case CUDAResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 1, error.message))
        }
      }

    override def readBytes(address: GuestAddress, size: Int): Either[MemoryError, Col[Byte]] =
      CUDAEmulator.this.synchronized {
        require(size >= 0, s"memory byte count must be non-negative, found $size")
        resolve(DevicePtr(address.value), size, context) match {
          case CUDAResult.Success((bytes, offset)) =>
            Right(Col.unsafeFromArray(java.util.Arrays.copyOfRange(bytes, offset, offset + size)))
          case CUDAResult.Failure(_) => super.readBytes(address, size)
        }
      }

    override def writeBytes(address: GuestAddress, bytes: Col[Byte]): Either[MemoryError, Unit] =
      CUDAEmulator.this.synchronized {
        resolve(DevicePtr(address.value), bytes.size, context) match {
          case CUDAResult.Success((destination, offset)) =>
            val _ = bytes.copyToArray(destination, offset, bytes.size)
            Right(())
          case CUDAResult.Failure(_) => super.writeBytes(address, bytes)
        }
      }

    override def readLittleEndian(address: GuestAddress, size: Int): Either[MemoryError, Long] =
      CUDAEmulator.this.synchronized {
        resolve(DevicePtr(address.value), size, context) match {
          case CUDAResult.Success((bytes, offset)) => Right(LittleEndian.decode(bytes, offset, size))
          case CUDAResult.Failure(_)               => super.readLittleEndian(address, size)
        }
      }

    override def writeLittleEndian(address: GuestAddress, value: Long, size: Int): Either[MemoryError, Unit] =
      CUDAEmulator.this.synchronized {
        resolve(DevicePtr(address.value), size, context) match {
          case CUDAResult.Success((bytes, offset)) =>
            LittleEndian.write(value, bytes, offset, size)
            Right(())
          case CUDAResult.Failure(_) => super.writeLittleEndian(address, value, size)
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
