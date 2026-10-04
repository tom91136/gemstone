package net.kurobako.gemstone

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import scala.reflect.ClassTag
import scala.util.NotGiven

object abi {

  /** A byte sequence that is non-empty and ends in a NUL terminator. */
  type NulTerminatedBytes = NulTerminatedBytes.Type
  object NulTerminatedBytes extends Newtype[Vec[Byte]] {
    override protected def validate(value: Vec[Byte]): Unit =
      require(value.nonEmpty && value.last == 0.toByte, "NUL-terminated bytes must be nonempty and end in a NUL byte")
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

  object CanRead {
    given CanRead[Read] with      {}
    given CanRead[ReadWrite] with {}
  }
  sealed trait CanRead[Mode <: Access]

  object CanWrite {
    given CanWrite[Write] with     {}
    given CanWrite[ReadWrite] with {}
  }
  sealed trait CanWrite[Mode <: Access]

  object CanDereference {
    given CanDereference[One] with                                           {}
    given CanDereference[NulTerminated] with                                 {}
    given [Name <: String & Singleton]: CanDereference[CountedBy[Name]] with {}
    given [Count <: Int & Singleton]: CanDereference[Fixed[Count]] with      {}
  }

  /** Evidence that an extent describes addressable storage. Opaque pointers deliberately have no instance. */
  sealed trait CanDereference[Size <: Extent]

  object ExtentEvidence {
    given [A]: ExtentEvidence[A, One] with {
      def validateInput(elements: Vec[A]): Either[PointerError, Unit] =
        validateOutput(elements.length)
      def validateOutput(length: Int): Either[PointerError, Unit] =
        Either.cond(
          length == 1,
          (),
          PointerError.InvalidExtent(s"One requires exactly one element, found $length")
        )
    }

    given [A, Name <: String & Singleton]: ExtentEvidence[A, CountedBy[Name]] with {
      def validateInput(elements: Vec[A]): Either[PointerError, Unit] = Right(())
      def validateOutput(length: Int): Either[PointerError, Unit]     = Right(())
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
      def validateInput(elements: Vec[A]): Either[PointerError, Unit] =
        validate(elements.length)
      def validateOutput(length: Int): Either[PointerError, Unit] = validate(length)
    }

    given ExtentEvidence[Byte, NulTerminated] with {
      def validateInput(elements: Vec[Byte]): Either[PointerError, Unit] = Right(())
      def validateOutput(length: Int): Either[PointerError, Unit]        =
        Either.cond(
          length > 0,
          (),
          PointerError.InvalidExtent("NulTerminated requires at least the terminator")
        )
      override def validateSnapshot(elements: Array[Byte]): Either[PointerError, Unit] =
        Either.cond(
          elements.last == 0,
          (),
          PointerError.InvalidExtent("NulTerminated output must end in a NUL byte")
        )
    }
  }

  /** Runtime validation for the storage shape promised by an extent. */
  sealed trait ExtentEvidence[A, Size <: Extent] {
    def validateInput(elements: Vec[A]): Either[PointerError, Unit]
    def validateOutput(length: Int): Either[PointerError, Unit]
    @scala.annotation.nowarn("msg=unused explicit parameter")
    def validateSnapshot(_elements: Array[A]): Either[PointerError, Unit] = Right(())
  }

  enum PointerError(val message: String) {
    case InvalidExtent(detail: String) extends PointerError(detail)
    case OutOfBounds(index: Int >= 0, length: Int >= 0)
        extends PointerError(s"pointer index $index is outside [0, $length)")
  }

  object Pointer {
    private def copy[A: ClassTag](elements: Vec[A]): Array[A] =
      Array.tabulate[A](elements.length)(elements(_))

    def input[A, Size <: Extent](elements: Vec[A])(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A],
        notNulTerminated: NotGiven[Size =:= NulTerminated]
    ): Either[PointerError, Pointer[A, Read, Size]] = extent.validateInput(elements).map(_ =>
      Pointer[A, Read, Size](
        mutableElements = copy(elements),
        mutableInitialized = Array.fill(elements.length)(true),
        extent = extent,
        elementTag = elementTag
      )
    )

    def output[A, Size <: Extent](length: Int >= 0)(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A]
    ): Either[PointerError, Pointer[A, Write, Size]] = extent.validateOutput(length.raw).map(_ =>
      Pointer[A, Write, Size](
        mutableElements = Array.ofDim[A](length.raw),
        mutableInitialized = Array.fill(length.raw)(false),
        extent = extent,
        elementTag = elementTag
      )
    )

    def inputOutput[A, Size <: Extent](elements: Vec[A])(using
        extent: ExtentEvidence[A, Size],
        elementTag: ClassTag[A],
        notNulTerminated: NotGiven[Size =:= NulTerminated]
    ): Either[PointerError, Pointer[A, ReadWrite, Size]] = extent.validateInput(elements).map(_ =>
      Pointer[A, ReadWrite, Size](
        mutableElements = copy(elements),
        mutableInitialized = Array.fill(elements.length)(true),
        extent = extent,
        elementTag = elementTag
      )
    )

    def cString(elements: NulTerminatedBytes): CString = {
      import NulTerminatedBytes.*
      Pointer[Byte, Read, NulTerminated](
        mutableElements = copy(elements.raw),
        mutableInitialized = Array.fill(elements.raw.length)(true),
        extent = summon[ExtentEvidence[Byte, NulTerminated]],
        elementTag = summon[ClassTag[Byte]]
      )
    }
  }

  /** A process-neutral pointer capability. The native ABI renders this as `A *`; the JVM dispatcher supplies bounded,
    * copied storage and never exposes the caller's numeric host address.
    */
  class Pointer[A, Mode <: Access, Size <: Extent] private (
      private val mutableElements: Array[A],
      private val mutableInitialized: Array[Boolean],
      private val extent: ExtentEvidence[A, Size],
      private val elementTag: ClassTag[A]
  ) {
    def length: Int >= 0 = mutableElements.length.unsafe[Int >= 0]

    private def checkedIndex(index: Int >= 0): Either[PointerError, Int] =
      if (index.raw >= mutableElements.length) Left(PointerError.OutOfBounds(index, length)) else Right(index.raw)

    def read(index: Int >= 0 = 0)(using
        CanRead[Mode],
        CanDereference[Size]
    ): Either[PointerError, A] = checkedIndex(index).map(mutableElements(_))

    def write(value: A, index: Int >= 0 = 0)(using
        CanWrite[Mode],
        CanDereference[Size]
    ): Either[PointerError, Unit] =
      checkedIndex(index).map { position =>
        mutableElements(position) = value
        mutableInitialized(position) = true
      }

    def snapshot(using CanDereference[Size]): Either[PointerError, Option[Vec[A]]] =
      if (!mutableInitialized.forall(value => value)) Right(None)
      else
        extent.validateSnapshot(mutableElements).map(_ => Some(Vec.from(mutableElements)(using elementTag)))
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

  object CValue {
    def unsafe[Name <: String & Singleton](bytes: NonEmpty[Vec[Byte]]): CValue[Name] = CValue(bytes)

    def fromUnsignedLong[Name <: String & Singleton](
        value: Long,
        byteSize: Int in 1 ~ 8
    ): ScalarCValue[Name] = {
      val bytes = LittleEndian.toNonEmptyVec(value, byteSize)
      ScalarCValue(bytes, byteSize)
    }
  }

  object ScalarCValue {
    def either[Name <: String & Singleton](bytes: NonEmpty[Vec[Byte]]): Either[String, ScalarCValue[Name]] =
      bytes.length.refine[Int in 1 ~ 8].map(size =>
        ScalarCValue(bytes, size)
      )

  }

  class ScalarCValue[Name <: String & Singleton] private[abi] (
      val scalarBytes: NonEmpty[Vec[Byte]],
      val scalarByteSize: Int in 1 ~ 8
  ) extends CValue[Name](scalarBytes) {
    def toUnsignedLong: Long =
      LittleEndian.decode(scalarBytes, 0, scalarByteSize.widen[Int in 0 ~ 8])
  }

  /** Process-neutral bytes for a C typedef, enum, record, or other by-value ABI type. The literal C spelling keeps
    * unrelated vendor types distinct even when they have the same machine representation.
    *
    * The representation is deliberately opaque to API implementations except at an ABI codec boundary. This makes
    * generated surfaces executable on the JVM and Scala Native without pretending that every vendor record is a machine
    * word.
    */
  sealed class CValue[Name <: String & Singleton] private[abi] (val bytes: NonEmpty[Vec[Byte]]) {
    def byteSize: Int > 0 = bytes.positiveSize

    /** Narrow a by-value ABI value to the machine-word representation used by enums, flags, and handles. */
    def scalar: Option[ScalarCValue[Name]] =
      Option.when(bytes.length <= MachineWordSize)(ScalarCValue(bytes, bytes.length.unsafe[Int in 1 ~ 8]))

    override def equals(other: Any): Boolean = other match {
      case value: CValue[?] => Vec.sameElements(bytes, value.bytes)
      case _                => false
    }

    override def hashCode(): Int = bytes.hashCode()

    override def toString: String = s"CValue(${bytes.length} bytes)"
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
