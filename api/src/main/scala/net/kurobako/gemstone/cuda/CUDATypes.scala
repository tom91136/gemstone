package net.kurobako.gemstone.cuda

import net.kurobako.gemstone.Col

opaque type UInt8 = Int

object UInt8 {
  def apply(value: Int): UInt8 = {
    require(value >= 0 && value <= 0xff, s"unsigned byte out of range: $value")
    value
  }

  extension (value: UInt8) def toInt: Int = value
}

opaque type UInt32 = Long

object UInt32 {
  def apply(value: Long): UInt32 = {
    require(value >= 0L && value <= 0xffffffffL, s"unsigned int out of range: $value")
    value
  }

  extension (value: UInt32) def toLong: Long = value
}

opaque type SizeT = Long

object SizeT {
  def apply(value: Long): SizeT = {
    require(value >= 0L, s"negative size: $value")
    value
  }

  extension (value: SizeT) def toLong: Long = value
}

opaque type Device = Int

object Device {
  def apply(ordinal: Int): Device             = ordinal
  extension (device: Device) def ordinal: Int = device
}

opaque type DevicePtr = Long

object DevicePtr {
  val Null: DevicePtr                              = 0L
  def apply(address: Long): DevicePtr              = address
  extension (pointer: DevicePtr) def address: Long = pointer
}

opaque type Context = Long

object Context {
  val Null: Context                             = 0L
  def apply(handle: Long): Context              = handle
  extension (context: Context) def handle: Long = context
}

opaque type CUDAModule = Long

object CUDAModule {
  val Null: CUDAModule                            = 0L
  def apply(handle: Long): CUDAModule             = handle
  extension (module: CUDAModule) def handle: Long = module
}

opaque type CUDAFunction = Long

object CUDAFunction {
  val Null: CUDAFunction                              = 0L
  def apply(handle: Long): CUDAFunction               = handle
  extension (function: CUDAFunction) def handle: Long = function
}

opaque type Stream = Long

object Stream {
  val Default: Stream                         = 0L
  def apply(handle: Long): Stream             = handle
  extension (stream: Stream) def handle: Long = stream
}

final case class HostInput(bytes: Col[Byte])
final case class HostOutput(bytes: Col[Byte])

/** A copied CUDA module image. The native boundary currently accepts textual PTX and never retains the caller's
  * pointer.
  */
final case class ModuleImage(bytes: Col[Byte])

final case class FunctionName(value: String) {
  require(value.nonEmpty, "CUDA function name must not be empty")
  require(!value.exists(_ == '\u0000'), "CUDA function name must not contain NUL")
}

/** The logical result of `cuModuleGetFunction`. Parameter widths are returned to the C facade so it can safely copy the
  * otherwise untyped `void **kernelParams` array at launch time.
  */
final case class KernelFunction(handle: CUDAFunction, parameterWidths: Col[Int])

final case class KernelArgument(bytes: Col[Byte])
final case class KernelParameters(arguments: Col[KernelArgument])

/** Fully copied launch data; no native process pointer crosses the RPC boundary. */
final case class KernelLaunch(
    gridX: UInt32,
    gridY: UInt32,
    gridZ: UInt32,
    blockX: UInt32,
    blockY: UInt32,
    blockZ: UInt32,
    sharedMemoryBytes: UInt32,
    arguments: Col[KernelArgument]
)

final case class CUDAError(code: Int, message: String)

/** Status returned by the CUDA Driver and Runtime C APIs. Transport failures are represented separately by
  * `CUDAResult.Failure`; a successfully transported API call always returns one of these vendor status values.
  */
opaque type CUDAStatus = Int

object CUDAStatus {
  val Success: CUDAStatus             = 0
  val InvalidValue: CUDAStatus        = 1
  val InitializationError: CUDAStatus = 3
  val InvalidDevice: CUDAStatus       = 101
  val InvalidContext: CUDAStatus      = 201
  val NotSupported: CUDAStatus        = 801
  val Unknown: CUDAStatus             = 999

  def apply(code: Int): CUDAStatus = code

  extension (status: CUDAStatus) {
    def code: Int          = status
    def isSuccess: Boolean = status == Success
  }
}

enum CUDAResult[+A] {
  case Success(value: A)
  case Failure(error: CUDAError)
}

object CUDAResult {
  extension [A](result: CUDAResult[A]) {
    def map[B](function: A => B): CUDAResult[B] = result match {
      case Success(value) => Success(function(value))
      case Failure(error) => Failure(error)
    }

    def flatMap[B](function: A => CUDAResult[B]): CUDAResult[B] = result match {
      case Success(value) => function(value)
      case Failure(error) => Failure(error)
    }
  }
}
