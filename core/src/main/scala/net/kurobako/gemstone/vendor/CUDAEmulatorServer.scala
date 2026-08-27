package net.kurobako.gemstone.vendor

import net.kurobako.gemstone.cuda.CUDA
import net.kurobako.gemstone.cuda.Context
import net.kurobako.gemstone.cuda.CUDAFunction
import net.kurobako.gemstone.cuda.CUDAModule
import net.kurobako.gemstone.cuda.CUDAError as RPCCUDAError
import net.kurobako.gemstone.cuda.CUDAResult
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

final class CUDAEmulatorServer(kernelExecution: CUDAKernelExecution = CUDAKernelExecution.Unsupported) extends CUDA {
  private val allocations = mutable.Map.empty[Long, Array[Byte]]
  private var nextAddress = 0x100000000L
  private var contextLive = false

  override def cuInit(flags: UInt32): CUDAResult[Unit] = success(())

  override def cuDriverGetVersion(): CUDAResult[Int] = success(3020)

  override def cuDeviceGetCount(): CUDAResult[Int] = success(1)

  override def cuDeviceGet(ordinal: Int): CUDAResult[Device] =
    if (ordinal == 0) success(Device(0)) else failure(101, s"invalid device ordinal: $ordinal")

  override def cuCtxCreate_v2(flags: UInt32, device: Device): CUDAResult[Context] = synchronized {
    if (device.ordinal != 0) failure(101, s"invalid device ordinal: ${device.ordinal}")
    else {
      contextLive = true
      success(Context(1L))
    }
  }

  override def cuCtxDestroy_v2(context: Context): CUDAResult[Unit] = synchronized {
    if (!contextLive || context.handle != 1L) failure(201, "invalid CUDA context")
    else {
      contextLive = false
      success(())
    }
  }

  override def cuCtxSynchronize(): CUDAResult[Unit] = success(())

  override def cuMemAlloc_v2(bytesize: SizeT): CUDAResult[DevicePtr] = allocate(bytesize)

  override def cuMemFree_v2(pointer: DevicePtr): CUDAResult[Unit] = free(pointer)

  override def cuMemcpyHtoD_v2(destination: DevicePtr, source: HostInput): CUDAResult[Unit] = synchronized {
    resolve(destination, source.bytes.length).map { case (allocation, offset) =>
      source.bytes.iterator.zipWithIndex.foreach { case (byte, index) => allocation(offset + index) = byte }
    }
  }

  override def cuMemcpyDtoH_v2(source: DevicePtr, byteCount: SizeT): CUDAResult[HostOutput] = synchronized {
    checkedSize(byteCount).flatMap(size => resolve(source, size)).map { case (allocation, offset) =>
      HostOutput(IArray.unsafeFromArray(allocation.slice(offset, offset + byteCount.toLong.toInt)))
    }
  }

  override def cuMemcpyDtoD_v2(
      destination: DevicePtr,
      source: DevicePtr,
      byteCount: SizeT
  ): CUDAResult[Unit] = synchronized {
    checkedSize(byteCount).flatMap { size =>
      resolve(source, size).flatMap { case (sourceAllocation, sourceOffset) =>
        resolve(destination, size).map { case (destinationAllocation, destinationOffset) =>
          Array.copy(sourceAllocation, sourceOffset, destinationAllocation, destinationOffset, size)
        }
      }
    }
  }

  override def cuMemsetD8_v2(destination: DevicePtr, value: UInt8, count: SizeT): CUDAResult[Unit] = synchronized {
    checkedSize(count).flatMap(size => resolve(destination, size)).map { case (allocation, offset) =>
      java.util.Arrays.fill(allocation, offset, offset + count.toLong.toInt, value.toInt.toByte)
    }
  }

  override def cuModuleLoadData(image: ModuleImage): CUDAResult[CUDAModule] = kernelExecution.cuModuleLoadData(image)

  override def cuModuleGetFunction(module: CUDAModule, name: FunctionName): CUDAResult[KernelFunction] =
    kernelExecution.cuModuleGetFunction(module, name)

  override def cuModuleUnload(module: CUDAModule): CUDAResult[Unit] = kernelExecution.cuModuleUnload(module)

  override def cuLaunchKernel(function: CUDAFunction, launch: KernelLaunch): CUDAResult[Unit] =
    kernelExecution.cuLaunchKernel(function, launch, deviceMemory)

  override def __cudaGemstoneManagedRegister(pointer: DevicePtr, bytesize: SizeT): CUDAResult[Unit] = synchronized {
    checkedSize(bytesize).flatMap { size =>
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
    }
  }

  override def __cudaGemstoneManagedUnregister(pointer: DevicePtr): CUDAResult[Unit] = free(pointer)

  override def cudaGetDeviceCount(): CUDAResult[Int] = cuDeviceGetCount()

  override def cudaMalloc(bytesize: SizeT): CUDAResult[DevicePtr] = allocate(bytesize)

  override def cudaFree(pointer: DevicePtr): CUDAResult[Unit] = free(pointer)

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
  private def success[A](value: A): CUDAResult[A]                   = CUDAResult.Success(value)
  private def failure[A](code: Int, message: String): CUDAResult[A] = CUDAResult.Failure(RPCCUDAError(code, message))
}
