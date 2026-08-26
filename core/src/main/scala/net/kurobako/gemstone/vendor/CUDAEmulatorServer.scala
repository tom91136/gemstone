package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.cuda.Context
import net.kurobako.gemstone.cuda.CudaFunction
import net.kurobako.gemstone.cuda.CudaModule
import net.kurobako.gemstone.cuda.CudaError as RpcCudaError
import net.kurobako.gemstone.cuda.CudaResult
import net.kurobako.gemstone.cuda.Device
import net.kurobako.gemstone.cuda.DevicePtr
import net.kurobako.gemstone.cuda.HostInput
import net.kurobako.gemstone.cuda.HostOutput
import net.kurobako.gemstone.cuda.FunctionName
import net.kurobako.gemstone.cuda.KernelFunction
import net.kurobako.gemstone.cuda.KernelLaunch
import net.kurobako.gemstone.cuda.ModuleImage
import net.kurobako.gemstone.cuda.SizeT
import net.kurobako.gemstone.cuda.UInt8
import net.kurobako.gemstone.cuda.UInt32
import net.kurobako.gemstone.memory.Allocation
import net.kurobako.gemstone.memory.AllocationKind
import net.kurobako.gemstone.memory.CheckedMemory
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.memory.MemoryError

import scala.collection.mutable

/** JVM/Native logical CUDA server used by both in-process tests and the future isolated transport. */
trait CudaKernelExecution {
  def cuModuleLoadData(image: ModuleImage): CudaResult[CudaModule]
  def cuModuleGetFunction(module: CudaModule, name: FunctionName): CudaResult[KernelFunction]
  def cuModuleUnload(module: CudaModule): CudaResult[Unit]
  def cuLaunchKernel(function: CudaFunction, launch: KernelLaunch, memory: CheckedMemory): CudaResult[Unit]
}

object CudaKernelExecution {
  val Unsupported: CudaKernelExecution = new CudaKernelExecution {
    override def cuModuleLoadData(image: ModuleImage): CudaResult[CudaModule] = failure("cuModuleLoadData")
    override def cuModuleGetFunction(module: CudaModule, name: FunctionName): CudaResult[KernelFunction] =
      failure("cuModuleGetFunction")
    override def cuModuleUnload(module: CudaModule): CudaResult[Unit] = failure("cuModuleUnload")
    override def cuLaunchKernel(
        function: CudaFunction,
        launch: KernelLaunch,
        memory: CheckedMemory
    ): CudaResult[Unit] = failure("cuLaunchKernel")

    private def failure[A](name: String): CudaResult[A] =
      CudaResult.Failure(RpcCudaError(801, s"$name is not implemented"))
  }
}

final class CUDAEmulatorServer(kernelExecution: CudaKernelExecution = CudaKernelExecution.Unsupported) extends CUDA {
  private val allocations = mutable.Map.empty[Long, Array[Byte]]
  private var nextAddress = 0x100000000L
  private var contextLive = false

  override def cuInit(flags: UInt32): CudaResult[Unit] = success(())

  override def cuDriverGetVersion(): CudaResult[Int] = success(3020)

  override def cuDeviceGetCount(): CudaResult[Int] = success(1)

  override def cuDeviceGet(ordinal: Int): CudaResult[Device] =
    if (ordinal == 0) success(Device(0)) else failure(101, s"invalid device ordinal: $ordinal")

  override def cuCtxCreate_v2(flags: UInt32, device: Device): CudaResult[Context] = synchronized {
    if (device.ordinal != 0) failure(101, s"invalid device ordinal: ${device.ordinal}")
    else {
      contextLive = true
      success(Context(1L))
    }
  }

  override def cuCtxDestroy_v2(context: Context): CudaResult[Unit] = synchronized {
    if (!contextLive || context.handle != 1L) failure(201, "invalid CUDA context")
    else {
      contextLive = false
      success(())
    }
  }

  override def cuCtxSynchronize(): CudaResult[Unit] = success(())

  override def cuMemAlloc_v2(bytesize: SizeT): CudaResult[DevicePtr] = allocate(bytesize)

  override def cuMemFree_v2(pointer: DevicePtr): CudaResult[Unit] = free(pointer)

  override def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CudaResult[Unit] = synchronized {
    resolve(destination, source.bytes.length).map { case (allocation, offset) =>
      source.bytes.iterator.zipWithIndex.foreach { case (byte, index) => allocation(offset + index) = byte }
    }
  }

  override def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CudaResult[HostOutput] = synchronized {
    checkedSize(byteCount).flatMap(size => resolve(source, size)).map { case (allocation, offset) =>
      HostOutput(IArray.unsafeFromArray(allocation.slice(offset, offset + byteCount.toLong.toInt)))
    }
  }

  override def cuMemcpyDtoD_v2(
      destination: DevicePtr,
      source: DevicePtr,
      byteCount: SizeT
  ): CudaResult[Unit] = synchronized {
    checkedSize(byteCount).flatMap { size =>
      resolve(source, size).flatMap { case (sourceAllocation, sourceOffset) =>
        resolve(destination, size).map { case (destinationAllocation, destinationOffset) =>
          Array.copy(sourceAllocation, sourceOffset, destinationAllocation, destinationOffset, size)
        }
      }
    }
  }

  override def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CudaResult[Unit] = synchronized {
    checkedSize(count).flatMap(size => resolve(destination, size)).map { case (allocation, offset) =>
      java.util.Arrays.fill(allocation, offset, offset + count.toLong.toInt, value.toInt.toByte)
    }
  }

  override def cuModuleLoadData(image: ModuleImage): CudaResult[CudaModule] = kernelExecution.cuModuleLoadData(image)

  override def cuModuleGetFunction(module: CudaModule, name: FunctionName): CudaResult[KernelFunction] =
    kernelExecution.cuModuleGetFunction(module, name)

  override def cuModuleUnload(module: CudaModule): CudaResult[Unit] = kernelExecution.cuModuleUnload(module)

  override def cuLaunchKernel(function: CudaFunction, launch: KernelLaunch): CudaResult[Unit] =
    kernelExecution.cuLaunchKernel(function, launch, deviceMemory)

  override def cudaGetDeviceCount(): CudaResult[Int] = cuDeviceGetCount()

  override def cudaMalloc(bytesize: SizeT): CudaResult[DevicePtr] = allocate(bytesize)

  override def cudaFree(pointer: DevicePtr): CudaResult[Unit] = free(pointer)

  private def allocate(bytesize: SizeT): CudaResult[DevicePtr] = synchronized {
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

  private def free(pointer: DevicePtr): CudaResult[Unit] = synchronized {
    if (allocations.remove(pointer.address).isDefined) success(())
    else failure(1, s"invalid device pointer: 0x${java.lang.Long.toUnsignedString(pointer.address, 16)}")
  }

  private def checkedSize(size: SizeT): CudaResult[Int] =
    if (size.toLong > Int.MaxValue.toLong) failure(2, s"allocation exceeds emulator limit: ${size.toLong}")
    else success(size.toLong.toInt)

  private def resolve(pointer: DevicePtr, size: Int): CudaResult[(Array[Byte], Int)] = {
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

    override def readInt32(address: GuestAddress): Either[MemoryError, Int] = CUDAEmulatorServer.this.synchronized {
      resolve(DevicePtr(address.value), 4).map { case (bytes, offset) =>
        (bytes(offset) & 0xff) |
          ((bytes(offset + 1) & 0xff) << 8) |
          ((bytes(offset + 2) & 0xff) << 16) |
          ((bytes(offset + 3) & 0xff) << 24)
      } match {
        case CudaResult.Success(value) => Right(value)
        case CudaResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
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
          case CudaResult.Success(())    => Right(())
          case CudaResult.Failure(error) => Left(MemoryError.InvalidAddress(address, 4, error.message))
        }
      }

    override def free(): Unit = ()
  }
  private def success[A](value: A): CudaResult[A]                   = CudaResult.Success(value)
  private def failure[A](code: Int, message: String): CudaResult[A] = CudaResult.Failure(RpcCudaError(code, message))
}
