package net.kurobako.gemstone.cuda

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

opaque type CudaModule = Long

object CudaModule {
  val Null: CudaModule                            = 0L
  def apply(handle: Long): CudaModule             = handle
  extension (module: CudaModule) def handle: Long = module
}

opaque type CudaFunction = Long

object CudaFunction {
  val Null: CudaFunction                              = 0L
  def apply(handle: Long): CudaFunction               = handle
  extension (function: CudaFunction) def handle: Long = function
}

final case class HostInput(bytes: IArray[Byte])
final case class HostOutput(bytes: IArray[Byte])

/** A copied CUDA module image. The native boundary currently accepts textual PTX and never retains the caller's
  * pointer.
  */
final case class ModuleImage(bytes: IArray[Byte])

final case class FunctionName(value: String) {
  require(value.nonEmpty, "CUDA function name must not be empty")
  require(!value.exists(_ == '\u0000'), "CUDA function name must not contain NUL")
}

/** The logical result of `cuModuleGetFunction`. Parameter widths are returned to the C facade so it can safely copy the
  * otherwise untyped `void **kernelParams` array at launch time.
  */
final case class KernelFunction(handle: CudaFunction, parameterWidths: IArray[Byte])

final case class KernelArgument(bytes: IArray[Byte])

/** Fully copied launch data; no native process pointer crosses the RPC boundary. */
final case class KernelLaunch(
    gridX: UInt32,
    gridY: UInt32,
    gridZ: UInt32,
    blockX: UInt32,
    blockY: UInt32,
    blockZ: UInt32,
    sharedMemoryBytes: UInt32,
    arguments: IArray[KernelArgument]
)

final case class CudaError(code: Int, message: String)

enum CudaResult[+A] {
  case Success(value: A)
  case Failure(error: CudaError)
}

object CudaResult {
  extension [A](result: CudaResult[A]) {
    def map[B](function: A => B): CudaResult[B] = result match {
      case Success(value) => Success(function(value))
      case Failure(error) => Failure(error)
    }

    def flatMap[B](function: A => CudaResult[B]): CudaResult[B] = result match {
      case Success(value) => function(value)
      case Failure(error) => Failure(error)
    }
  }
}
