package net.kurobako.gemstone.abi

/** C pointer access is part of the API contract rather than an unchecked convention in the generated marshaller. */
sealed trait Access
sealed trait Read      extends Access
sealed trait Write     extends Access
sealed trait ReadWrite extends Access

/** The number of pointees reachable through a C pointer. */
sealed trait Extent
sealed trait One                                   extends Extent
sealed trait Opaque                                extends Extent
sealed trait NulTerminated                         extends Extent
sealed trait CountedBy[Name <: String & Singleton] extends Extent
sealed trait Fixed[Count <: Int & Singleton]       extends Extent

sealed trait CanRead[Mode <: Access]
object CanRead {
  given CanRead[Read]      = new CanRead[Read] {}
  given CanRead[ReadWrite] = new CanRead[ReadWrite] {}
}

sealed trait CanWrite[Mode <: Access]
object CanWrite {
  given CanWrite[Write]     = new CanWrite[Write] {}
  given CanWrite[ReadWrite] = new CanWrite[ReadWrite] {}
}

/** A process-neutral pointer capability. The native ABI renders this as `A *`; the JVM dispatcher supplies bounded,
  * copied storage and never exposes the caller's numeric host address.
  */
final class Pointer[A, Mode <: Access, Size <: Extent] private[gemstone] (
    private var elements: Vector[A],
    private var initialized: Boolean
) {
  def length: Int = elements.length

  def read(index: Int = 0)(using CanRead[Mode]): A = {
    require(initialized, "attempted to read an uninitialized output pointer")
    require(index >= 0 && index < elements.length, s"pointer index $index is outside [0, ${elements.length})")
    elements(index)
  }

  def write(value: A, index: Int = 0)(using CanWrite[Mode]): Unit = {
    require(index >= 0 && index < elements.length, s"pointer index $index is outside [0, ${elements.length})")
    elements = elements.updated(index, value)
    initialized = true
  }

  private[gemstone] def snapshot: Option[Vector[A]] = Option.when(initialized)(elements)
}

object Pointer {
  private[gemstone] def input[A, Size <: Extent](elements: IArray[A]): Pointer[A, Read, Size] = {
    require(elements.nonEmpty, "an input pointer must contain at least one element")
    new Pointer[A, Read, Size](elements.toVector, initialized = true)
  }

  private[gemstone] def output[A, Size <: Extent](length: Int): Pointer[A, Write, Size] = {
    require(length > 0, s"an output pointer must have positive length, found $length")
    new Pointer[A, Write, Size](Vector.fill(length)(null.asInstanceOf[A]), initialized = false)
  }

  private[gemstone] def inputOutput[A, Size <: Extent](elements: IArray[A]): Pointer[A, ReadWrite, Size] = {
    require(elements.nonEmpty, "an input/output pointer must contain at least one element")
    new Pointer[A, ReadWrite, Size](elements.toVector, initialized = true)
  }
}

type Ref[A, Mode <: Access] = Pointer[A, Mode, One]
type In[A]                  = Ref[A, Read]
type Out[A]                 = Ref[A, Write]
type InOut[A]               = Ref[A, ReadWrite]

type InputBuffer[A, Count <: String & Singleton]  = Pointer[A, Read, CountedBy[Count]]
type OutputBuffer[A, Count <: String & Singleton] = Pointer[A, Write, CountedBy[Count]]
type InOutBuffer[A, Count <: String & Singleton]  = Pointer[A, ReadWrite, CountedBy[Count]]
type CString                                      = Pointer[Byte, Read, NulTerminated]

/** Explicit nullability for pointer-shaped ABI values. */
type Nullable[A] = Option[A]

sealed trait CVoid
type OpaquePointer = Pointer[CVoid, ReadWrite, Opaque]

/** Nominal representation of a C typedef, enum, record, or other by-value ABI type. The literal C spelling keeps
  * unrelated vendor types distinct even when they have the same machine representation.
  */
sealed trait CValue[Name <: String & Singleton]

/** Why an invocation of a native callback could not complete. Exceptions and transport details never cross the API
  * boundary directly.
  */
enum CallbackFailure {
  case Unavailable(message: String)
  case Protocol(message: String)
  case Failed(message: String)
}

/** Opaque identity of native callback userdata. Its numeric native address never enters the JVM process. */
final class CallbackData private[gemstone] (private[gemstone] val capabilityId: Long) {
  override def toString: String = "CallbackData(<native>)"
}

object CallbackData {
  private[gemstone] def remote(capabilityId: Long): CallbackData = {
    require(capabilityId != 0L, "callback capability ID must be non-zero")
    new CallbackData(capabilityId)
  }
}

/** A typed, process-neutral C callback capability. The native endpoint owns the actual function pointer and any user
  * data; a JVM implementation invokes it like an ordinary tupled Scala function and receives failures explicitly.
  */
trait Callback[-Args <: Tuple, +Result] extends (Args => Either[CallbackFailure, Result])

/** A named C callback typedef. Scala retains the callable argument/result types while C declarations retain the
  * vendor's exact typedef name.
  */
trait CallbackTypedef[Name <: String & Singleton, -Args <: Tuple, +Result] extends Callback[Args, Result]

object CallbackTypedef {
  private[gemstone] def remote[Name <: String & Singleton, Args <: Tuple, Result](
      invocation: Args => Either[CallbackFailure, Result]
  ): CallbackTypedef[Name, Args, Result] = new CallbackTypedef[Name, Args, Result] {
    override def apply(arguments: Args): Either[CallbackFailure, Result] = invocation(arguments)
  }
}

/** A fixed-size C array embedded in an ABI declaration. */
sealed trait CArray[Element, Count <: Int & Singleton]

/** Declarative native-only method body. API derivation validates the target as a C identifier and emits a forwarding
  * wrapper. Reaching it as ordinary JVM code is always a programming error.
  */
inline def delegate[Name <: String & Singleton](arguments: Any*): Nothing =
  throw new UnsupportedOperationException("a native delegate cannot execute on the JVM")

/** Declarative, correctly typed native stub body. */
def unsupported: Nothing = throw new UnsupportedOperationException("API operation is not implemented")
