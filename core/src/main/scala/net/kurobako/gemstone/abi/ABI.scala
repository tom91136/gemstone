package net.kurobako.gemstone.abi

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.Vec.*
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

import scala.reflect.ClassTag
import scala.util.NotGiven

object ABI {

  final class EndsInNul
  object EndsInNul {
    inline given Constraint[Vec[Byte], EndsInNul] with {
      override inline def test(inline bytes: Vec[Byte]): Boolean = bytes.nonEmpty && bytes.last == 0.toByte
      override inline def message: String                        = "Should be non-empty and end in a NUL byte"
    }
  }

  type NulTerminatedBytes = NulTerminatedBytes.T
  object NulTerminatedBytes extends RefinedType[Vec[Byte], EndsInNul]

  /** C pointer access is part of the API type system rather than an unchecked convention in the generated marshaller.
    */
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
  private[gemstone] sealed trait ExtentEvidence[A, Size <: Extent] {
    private[gemstone] def validateInput(elements: Vec[A]): Either[PointerError, Unit]
    private[gemstone] def validateOutput(length: Int): Either[PointerError, Unit]
    @scala.annotation.nowarn("msg=unused explicit parameter")
    private[gemstone] def validateSnapshot(_elements: Array[A]): Either[PointerError, Unit] = Right(())
  }

  private[gemstone] object ExtentEvidence {
    given [A]: ExtentEvidence[A, One] with {
      private[gemstone] def validateInput(elements: Vec[A]): Either[PointerError, Unit] =
        validateOutput(elements.length)
      private[gemstone] def validateOutput(length: Int): Either[PointerError, Unit] =
        Either.cond(
          length == 1,
          (),
          PointerError.InvalidExtent(s"One requires exactly one element, found $length")
        )
    }

    given [A, Name <: String & Singleton]: ExtentEvidence[A, CountedBy[Name]] with {
      private[gemstone] def validateInput(elements: Vec[A]): Either[PointerError, Unit] = Right(())
      private[gemstone] def validateOutput(length: Int): Either[PointerError, Unit]     = Right(())
    }

    given [A, Count <: Int & Singleton](using
        count: ValueOf[Count],
        positive: scala.compiletime.ops.int.>[Count, 0] =:= true
    ): ExtentEvidence[A, Fixed[Count]] with {
      private def validate(length: Int): Either[PointerError, Unit] =
        Either.cond(
          length == count.value,
          (),
          PointerError.InvalidExtent(
            s"Fixed[${count.value}] requires exactly ${count.value} elements, found $length"
          )
        )
      private[gemstone] def validateInput(elements: Vec[A]): Either[PointerError, Unit] =
        validate(elements.length)
      private[gemstone] def validateOutput(length: Int): Either[PointerError, Unit] = validate(length)
    }

    given ExtentEvidence[Byte, NulTerminated] with {
      private[gemstone] def validateInput(elements: Vec[Byte]): Either[PointerError, Unit] = Right(())
      private[gemstone] def validateOutput(length: Int): Either[PointerError, Unit]        =
        Either.cond(
          length > 0,
          (),
          PointerError.InvalidExtent("NulTerminated requires at least the terminator")
        )
      override private[gemstone] def validateSnapshot(elements: Array[Byte]): Either[PointerError, Unit] =
        Either.cond(
          elements.last == 0,
          (),
          PointerError.InvalidExtent("NulTerminated output must end in a NUL byte")
        )
    }
  }

  enum PointerError(val message: String) {
    case InvalidExtent(detail: String) extends PointerError(detail)
    case OutOfBounds(index: Int :| Positive0, length: Int :| Positive0)
        extends PointerError(s"pointer index $index is outside [0, $length)")
  }

  /** A process-neutral pointer capability. The native ABI renders this as `A *`; the JVM dispatcher supplies bounded,
    * copied storage and never exposes the caller's numeric host address.
    */
  final class Pointer[A, Mode <: Access, Size <: Extent] private[gemstone] (
      private val mutableElements: Array[A],
      private val mutableInitialized: Array[Boolean],
      private val extent: ExtentEvidence[A, Size],
      private val elementTag: ClassTag[A]
  ) {
    def length: Int :| Positive0 = mutableElements.length.refineUnsafe[Positive0]

    def read(index: Int :| Positive0 = 0)(using
        CanRead[Mode],
        CanDereference[Size]
    ): Either[PointerError, A] =
      if (index >= mutableElements.length) Left(PointerError.OutOfBounds(index, length))
      else Right(mutableElements(index))

    def write(value: A, index: Int :| Positive0 = 0)(using
        CanWrite[Mode],
        CanDereference[Size]
    ): Either[PointerError, Unit] =
      if (index >= mutableElements.length) Left(PointerError.OutOfBounds(index, length))
      else {
        mutableElements(index) = value
        mutableInitialized(index) = true
        Right(())
      }

    private[gemstone] def snapshot(using CanDereference[Size]): Either[PointerError, Option[Vec[A]]] =
      if (!mutableInitialized.forall(value => value)) Right(None)
      else
        extent.validateSnapshot(mutableElements).map(_ => Some(Vec.from(mutableElements)(using elementTag)))
  }

  object Pointer {
    private def copy[A: ClassTag](elements: Vec[A]): Array[A] =
      Array.tabulate[A](elements.length)(elements(_))

    private[gemstone] def input[A, Size <: Extent](elements: Vec[A])(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A],
        notNulTerminated: NotGiven[Size =:= NulTerminated]
    ): Either[PointerError, Pointer[A, Read, Size]] =
      extent.validateInput(elements)
        .map(_ => Pointer[A, Read, Size](copy(elements), Array.fill(elements.length)(true), extent, elementTag))

    private[gemstone] def output[A, Size <: Extent](length: Int :| Positive0)(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A]
    ): Either[PointerError, Pointer[A, Write, Size]] =
      extent.validateOutput(length)
        .map(_ => Pointer[A, Write, Size](Array.ofDim[A](length), Array.fill(length)(false), extent, elementTag))

    private[gemstone] def inputOutput[A, Size <: Extent](elements: Vec[A])(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A],
        notNulTerminated: NotGiven[Size =:= NulTerminated]
    ): Either[PointerError, Pointer[A, ReadWrite, Size]] =
      extent.validateInput(elements)
        .map(_ => Pointer[A, ReadWrite, Size](copy(elements), Array.fill(elements.length)(true), extent, elementTag))

    private[gemstone] def cString(elements: NulTerminatedBytes): CString = {
      val bytes  = elements.value
      val extent = summon[ExtentEvidence[Byte, NulTerminated]]
      Pointer[Byte, Read, NulTerminated](copy(bytes), Array.fill(bytes.length)(true), extent, summon[ClassTag[Byte]])
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

  /** A by-value ABI value wider than this is not a machine word. */
  type MachineWordBytes = 8
  final val MachineWordSize: Int = 8

  /** Process-neutral bytes for a C typedef, enum, record, or other by-value ABI type. The literal C spelling keeps
    * unrelated vendor types distinct even when they have the same machine representation.
    *
    * The representation is deliberately opaque to API implementations except at an ABI codec boundary. This makes
    * generated surfaces executable on the JVM and Scala Native without pretending that every vendor record is a machine
    * word.
    */
  sealed class CValue[Name <: String & Singleton] private[abi] (val bytes: Vec[Byte] :| MinLength[1]) {
    def byteSize: Int :| Positive = bytes.positiveSize

    /** Narrow a by-value ABI value to the machine-word representation used by enums, flags, and handles. */
    def scalar: Option[ScalarCValue[Name]] =
      bytes.refineFurtherOption[MaxLength[MachineWordBytes]].map(value => ScalarCValue.fromBytes(value))

    override def equals(other: Any): Boolean = other match {
      case value: CValue[?] => Vec.sameElements(bytes, value.bytes)
      case _                => false
    }

    override def hashCode(): Int = bytes.hashCode()

    override def toString: String = s"CValue(${bytes.length} bytes)"
  }

  final class ScalarCValue[Name <: String & Singleton] private[abi] (
      val scalarBytes: Vec[Byte] :| (MinLength[1] & MaxLength[MachineWordBytes])
  ) extends CValue[Name](scalarBytes) {
    def scalarByteSize: Int :| Interval.Closed[1, MachineWordBytes] =
      scalarBytes.length.refineUnsafe[Interval.Closed[1, MachineWordBytes]]

    def toUnsignedLong: Long = LittleEndian.decode(scalarBytes, 0, ScalarByteWidth.fromPositive(scalarByteSize))
  }

  object ScalarCValue {
    def fromBytes[Name <: String & Singleton](
        bytes: Vec[Byte] :| (MinLength[1] & MaxLength[MachineWordBytes])
    ): ScalarCValue[Name] = {
      val copied: Vec[Byte] = Vec.from(bytes)
      ScalarCValue(copied.refineUnsafe[MinLength[1] & MaxLength[MachineWordBytes]])
    }
  }

  object CValue {
    def fromBytes[Name <: String & Singleton](bytes: Vec[Byte] :| MinLength[1]): CValue[Name] = {
      val copied: Vec[Byte] = Vec.from(bytes)
      CValue(copied.refineUnsafe[MinLength[1]])
    }

    def fromUnsignedLong[Name <: String & Singleton](
        value: Long,
        byteSize: Int :| Interval.Closed[1, MachineWordBytes]
    ): ScalarCValue[Name] = {
      val bytes: Vec[Byte] = LittleEndian.toVec(value, ScalarByteWidth.fromPositive(byteSize))
      ScalarCValue(bytes.refineUnsafe[MinLength[1] & MaxLength[MachineWordBytes]])
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

  /** A typed, process-neutral C callback capability. The native endpoint owns the actual function pointer and any user
    * data; a JVM implementation invokes it like an ordinary tupled Scala function and receives failures explicitly.
    */
  trait Callback[-Args <: Tuple, +Result] extends (Args => Either[CallbackFailure, Result])

  /** A named C callback typedef. Scala retains the callable argument/result types while C declarations retain the
    * vendor's exact typedef name.
    */
  trait CallbackTypedef[Name <: String & Singleton, -Args <: Tuple, +Result] extends Callback[Args, Result]

  /** A fixed-size C array embedded in an ABI declaration. */
  sealed trait CArray[Element, Count <: Int & Singleton]

  /** Declarative, correctly typed default for generated API declarations. */
  inline def unsupported: Nothing = throw UnsupportedOperationException("API operation is not implemented")

}
