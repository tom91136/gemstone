package net.kurobako.gemstone.abi

import net.kurobako.gemstone.{NonEmptyVec, NonNegativeInt, PositiveInt, Vec}
import net.kurobako.gemstone.NonEmptyVec.positiveSize
import net.kurobako.gemstone.binary.{LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.memory.MemoryValueByteWidth

import scala.reflect.ClassTag
import scala.util.NotGiven

object ABI {

  /** A byte sequence that is non-empty and ends in a NUL terminator. */
  opaque type NulTerminatedBytes = Vec[Byte]

  object NulTerminatedBytes {
    def either(bytes: Vec[Byte]): Either[String, NulTerminatedBytes] =
      if (!bytes.nonEmpty) Left("NUL-terminated bytes must not be empty")
      else if (bytes.last != 0.toByte) Left("NUL-terminated bytes must end in a NUL byte")
      else Right(bytes)

    def unsafe(bytes: Vec[Byte]): NulTerminatedBytes =
      either(bytes).fold(message => throw IllegalArgumentException(message), identity)

    extension (bytes: NulTerminatedBytes) {
      inline def raw: Vec[Byte] = bytes
    }
  }

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
    case OutOfBounds(index: NonNegativeInt, length: NonNegativeInt)
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
    def length: NonNegativeInt = NonNegativeInt.unsafe(mutableElements.length)

    private def checkedIndex(index: NonNegativeInt): Either[PointerError, Int] =
      if (index.raw >= mutableElements.length) Left(PointerError.OutOfBounds(index, length)) else Right(index.raw)

    def read(index: NonNegativeInt = NonNegativeInt.Zero)(using
        CanRead[Mode],
        CanDereference[Size]
    ): Either[PointerError, A] = checkedIndex(index).map(mutableElements(_))

    def write(value: A, index: NonNegativeInt = NonNegativeInt.Zero)(using
        CanWrite[Mode],
        CanDereference[Size]
    ): Either[PointerError, Unit] =
      checkedIndex(index).map { position =>
        mutableElements(position) = value
        mutableInitialized(position) = true
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

    private[gemstone] def output[A, Size <: Extent](length: NonNegativeInt)(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A]
    ): Either[PointerError, Pointer[A, Write, Size]] =
      extent.validateOutput(length.raw)
        .map(_ =>
          Pointer[A, Write, Size](Array.ofDim[A](length.raw), Array.fill(length.raw)(false), extent, elementTag)
        )

    private[gemstone] def inputOutput[A, Size <: Extent](elements: Vec[A])(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A],
        notNulTerminated: NotGiven[Size =:= NulTerminated]
    ): Either[PointerError, Pointer[A, ReadWrite, Size]] =
      extent.validateInput(elements)
        .map(_ => Pointer[A, ReadWrite, Size](copy(elements), Array.fill(elements.length)(true), extent, elementTag))

    private[gemstone] def cString(elements: NulTerminatedBytes): CString = {
      import NulTerminatedBytes.raw
      val bytes  = elements.raw
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
  final val MachineWordSize: Int = scala.compiletime.constValue[MachineWordBytes]

  /** Process-neutral bytes for a C typedef, enum, record, or other by-value ABI type. The literal C spelling keeps
    * unrelated vendor types distinct even when they have the same machine representation.
    *
    * The representation is deliberately opaque to API implementations except at an ABI codec boundary. This makes
    * generated surfaces executable on the JVM and Scala Native without pretending that every vendor record is a machine
    * word.
    */
  sealed class CValue[Name <: String & Singleton] private[abi] (val bytes: NonEmptyVec[Byte]) {
    def byteSize: PositiveInt = bytes.positiveSize

    /** Narrow a by-value ABI value to the machine-word representation used by enums, flags, and handles. */
    def scalar: Option[ScalarCValue[Name]] =
      Option.when(bytes.length <= MachineWordSize)(ScalarCValue.unsafe(bytes))

    override def equals(other: Any): Boolean = other match {
      case value: CValue[?] => Vec.sameElements(Vec.from(bytes.toVector), Vec.from(value.bytes.toVector))
      case _                => false
    }

    override def hashCode(): Int = bytes.toVector.hashCode()

    override def toString: String = s"CValue(${bytes.length} bytes)"
  }

  final class ScalarCValue[Name <: String & Singleton] private[abi] (
      val scalarBytes: NonEmptyVec[Byte]
  ) extends CValue[Name](scalarBytes) {
    def scalarByteSize: PositiveInt = scalarBytes.positiveSize

    def toUnsignedLong: Long =
      LittleEndian.decode(Vec.from(scalarBytes.toVector), 0, ScalarByteWidth.fromPositive(scalarByteSize.raw))
  }

  object ScalarCValue {
    def either[Name <: String & Singleton](bytes: NonEmptyVec[Byte]): Either[String, ScalarCValue[Name]] =
      MemoryValueByteWidth.either(bytes.length).map(_ => ScalarCValue(bytes))

    private[abi] def unsafe[Name <: String & Singleton](bytes: NonEmptyVec[Byte]): ScalarCValue[Name] =
      ScalarCValue(bytes)
  }

  object CValue {
    def unsafe[Name <: String & Singleton](bytes: NonEmptyVec[Byte]): CValue[Name] = CValue(bytes)

    def fromUnsignedLong[Name <: String & Singleton](
        value: Long,
        byteSize: MemoryValueByteWidth
    ): ScalarCValue[Name] = {
      val bytes: Vec[Byte] = LittleEndian.toVec(value, ScalarByteWidth.fromPositive(byteSize.raw))
      ScalarCValue(NonEmptyVec.unsafeFromVec(bytes))
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

  /** A named C callback typedef. Scala retains the callable argument and result types while C declarations retain the
    * vendor's exact typedef name.
    */
  trait CallbackTypedef[Name <: String & Singleton, -Args <: Tuple, +Result] extends Callback[Args, Result]

  /** A fixed-size C array embedded in an ABI declaration. */
  sealed trait CArray[Element, Count <: Int & Singleton]

  /** Declarative, correctly typed default for generated API declarations. */
  inline def unsupported: Nothing = throw UnsupportedOperationException("API operation is not implemented")

}
