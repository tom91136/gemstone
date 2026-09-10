package net.kurobako.gemstone.abi

import net.kurobako.gemstone.Col
import net.kurobako.gemstone.binary.LittleEndian

import scala.reflect.ClassTag

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
  given CanRead[Read] with      {}
  given CanRead[ReadWrite] with {}
}

sealed trait CanWrite[Mode <: Access]
object CanWrite {
  given CanWrite[Write] with     {}
  given CanWrite[ReadWrite] with {}
}

/** Evidence that an extent describes addressable storage. Opaque pointers deliberately have no instance. */
sealed trait CanDereference[Size <: Extent]
object CanDereference {
  given CanDereference[One] with                                           {}
  given CanDereference[NulTerminated] with                                 {}
  given [Name <: String & Singleton]: CanDereference[CountedBy[Name]] with {}
  given [Count <: Int & Singleton]: CanDereference[Fixed[Count]] with      {}
}

/** Runtime validation for the storage shape promised by an extent. */
sealed trait ExtentEvidence[A, Size <: Extent] {
  private[gemstone] def validateInput(elements: Col[A]): Unit
  private[gemstone] def validateOutput(length: Int): Unit
  private[gemstone] def validateSnapshot(elements: Array[Any]): Unit = ()
}

object ExtentEvidence {
  given [A]: ExtentEvidence[A, One] with {
    private[gemstone] def validateInput(elements: Col[A]): Unit =
      require(elements.length == 1, s"One requires exactly one element, found ${elements.length}")
    private[gemstone] def validateOutput(length: Int): Unit =
      require(length == 1, s"One requires exactly one element, found $length")
  }

  given [A, Name <: String & Singleton]: ExtentEvidence[A, CountedBy[Name]] with {
    private[gemstone] def validateInput(elements: Col[A]): Unit = ()
    private[gemstone] def validateOutput(length: Int): Unit     =
      require(length >= 0, s"CountedBy requires a non-negative length, found $length")
  }

  given [A, Count <: Int & Singleton](using count: ValueOf[Count]): ExtentEvidence[A, Fixed[Count]] with {
    private def validate(length: Int): Unit = {
      require(count.value > 0, s"Fixed requires a positive literal extent, found ${count.value}")
      require(length == count.value, s"Fixed[${count.value}] requires exactly ${count.value} elements, found $length")
    }
    private[gemstone] def validateInput(elements: Col[A]): Unit = validate(elements.length)
    private[gemstone] def validateOutput(length: Int): Unit     = validate(length)
  }

  given ExtentEvidence[Byte, NulTerminated] with {
    private[gemstone] def validateInput(elements: Col[Byte]): Unit = {
      require(elements.nonEmpty, "NulTerminated requires at least the terminator")
      require(elements(elements.length - 1) == 0, "NulTerminated input must end in a NUL byte")
    }
    private[gemstone] def validateOutput(length: Int): Unit =
      require(length > 0, s"NulTerminated requires at least the terminator, found length $length")
    override private[gemstone] def validateSnapshot(elements: Array[Any]): Unit =
      require(elements.last.asInstanceOf[Byte] == 0, "NulTerminated output must end in a NUL byte")
  }
}

/** A process-neutral pointer capability. The native ABI renders this as `A *`; the JVM dispatcher supplies bounded,
  * copied storage and never exposes the caller's numeric host address.
  */
final class Pointer[A, Mode <: Access, Size <: Extent] private[gemstone] (
    private val elements: Array[Any],
    private val initialized: Array[Boolean],
    private val extent: ExtentEvidence[A, Size],
    private val elementTag: ClassTag[A]
) {
  def length: Int = elements.length

  def read(index: Int = 0)(using CanRead[Mode], CanDereference[Size]): A = {
    require(index >= 0 && index < elements.length, s"pointer index $index is outside [0, ${elements.length})")
    require(initialized(index), s"attempted to read uninitialized pointer element $index")
    elements(index).asInstanceOf[A]
  }

  def write(value: A, index: Int = 0)(using CanWrite[Mode], CanDereference[Size]): Unit = {
    require(index >= 0 && index < elements.length, s"pointer index $index is outside [0, ${elements.length})")
    elements(index) = value
    initialized(index) = true
  }

  private[gemstone] def snapshot(using CanDereference[Size]): Option[Col[A]] =
    Option.when(initialized.forall(identity)) {
      extent.validateSnapshot(elements)
      Col.tabulate(elements.length)(index => elements(index).asInstanceOf[A])(using elementTag)
    }
}

object Pointer {
  private def copy[A](elements: Col[A]): Array[Any] =
    Array.tabulate[Any](elements.length)(elements(_))

  private[gemstone] def input[A, Size <: Extent](elements: Col[A])(using
      extent: ExtentEvidence[A, Size],
      elementTag: ClassTag[A]
  ): Pointer[A, Read, Size] = {
    extent.validateInput(elements)
    Pointer[A, Read, Size](copy(elements), Array.fill(elements.length)(true), extent, elementTag)
  }

  private[gemstone] def output[A, Size <: Extent](length: Int)(using
      extent: ExtentEvidence[A, Size],
      elementTag: ClassTag[A]
  ): Pointer[A, Write, Size] = {
    extent.validateOutput(length)
    Pointer[A, Write, Size](Array.ofDim[Any](length), Array.fill(length)(false), extent, elementTag)
  }

  private[gemstone] def inputOutput[A, Size <: Extent](elements: Col[A])(using
      extent: ExtentEvidence[A, Size],
      elementTag: ClassTag[A]
  ): Pointer[A, ReadWrite, Size] = {
    extent.validateInput(elements)
    Pointer[A, ReadWrite, Size](copy(elements), Array.fill(elements.length)(true), extent, elementTag)
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

/** Explicit nullability for pointer-shaped ABI values. ABI derivation rejects non-pointer-shaped elements. */
enum Nullable[+A] {
  case Null
  case NonNull(value: A)

  def toOption: Option[A] = this match {
    case Null           => None
    case NonNull(value) => Some(value)
  }
}

sealed trait CVoid
type OpaquePointer = Pointer[CVoid, ReadWrite, Opaque]

/** Process-neutral bytes for a C typedef, enum, record, or other by-value ABI type. The literal C spelling keeps
  * unrelated vendor types distinct even when they have the same machine representation.
  *
  * The representation is deliberately opaque to API implementations except at an ABI codec boundary. This makes
  * generated surfaces executable on the JVM and Scala Native without pretending that every vendor record is a machine
  * word.
  */
final class CValue[Name <: String & Singleton] private (val bytes: Col[Byte]) {
  def byteSize: Int = bytes.length

  /** Decode scalar enum, flag, and handle-shaped values. Records larger than a machine word remain byte-oriented. */
  def toUnsignedLong: Long = {
    require(byteSize <= 8, s"a $byteSize-byte C value cannot be decoded as a machine word")
    LittleEndian.decode(bytes, 0, byteSize)
  }

  override def equals(other: Any): Boolean = other match {
    case value: CValue[?] => Col.sameElements(bytes, value.bytes)
    case _                => false
  }

  override def hashCode(): Int = bytes.hashCode()

  override def toString: String = s"CValue(${bytes.length} bytes)"
}

object CValue {
  def fromBytes[Name <: String & Singleton](bytes: Col[Byte]): CValue[Name] = {
    require(bytes.nonEmpty, "a by-value C representation cannot be empty")
    CValue(Col.from(bytes))
  }

  def fromUnsignedLong[Name <: String & Singleton](value: Long, byteSize: Int): CValue[Name] = {
    require(byteSize >= 1 && byteSize <= 8, s"scalar C value size must be in [1, 8], found $byteSize")
    CValue(LittleEndian.toCol(value, byteSize))
  }
}

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
    CallbackData(capabilityId)
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
  private final class Remote[Name <: String & Singleton, Args <: Tuple, Result](
      invocation: Args => Either[CallbackFailure, Result]
  ) extends CallbackTypedef[Name, Args, Result] {
    override def apply(arguments: Args): Either[CallbackFailure, Result] = invocation(arguments)
  }

  private[gemstone] def remote[Name <: String & Singleton, Args <: Tuple, Result](
      invocation: Args => Either[CallbackFailure, Result]
  ): CallbackTypedef[Name, Args, Result] = Remote(invocation)
}

/** A fixed-size C array embedded in an ABI declaration. */
sealed trait CArray[Element, Count <: Int & Singleton]

/** Declarative native-only method body. API derivation validates the target as a C identifier and emits a forwarding
  * wrapper. Reaching it as ordinary JVM code is always a programming error.
  */
inline def delegate[Name <: String & Singleton](arguments: Any*): Nothing =
  throw UnsupportedOperationException("a native delegate cannot execute on the JVM")

/** Declarative, correctly typed native stub body. ABI derivation rejects every other concrete method body. */
inline def unsupported: Nothing = throw UnsupportedOperationException("API operation is not implemented")
