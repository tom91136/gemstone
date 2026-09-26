package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.CompileTime

import net.kurobako.gemstone.Vec
import net.kurobako.gemstone.binary.{BitWidth, FloatingPointWidth}
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.vm.Fault

private[v1_6] object VMIR {
  import Operand.id

  /** A function name that carries no NUL character. May be empty. */
  opaque type FunctionName = String

  object FunctionName {

    inline def either(value: String): Either[String, FunctionName] =
      if (value.view.contains('\u0000')) Left("function name must not contain a NUL character") else Right(value)

    inline def unsafe(value: String): FunctionName =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NoNul[A]): FunctionName =
      value

    extension (value: FunctionName) {
      inline def raw: String = value
    }
  }

  /** A non-empty extended instruction-set name that carries no NUL character. */
  opaque type InstructionSetName = String

  object InstructionSetName {

    inline def either(value: String): Either[String, InstructionSetName] =
      if (value.isEmpty) Left("instruction-set name must not be empty")
      else if (value.view.contains('\u0000')) Left("instruction-set name must not contain a NUL character")
      else Right(value)

    inline def unsafe(value: String): InstructionSetName =
      either(value).fold(message => throw IllegalArgumentException(message), identity)

    inline def apply[A <: String & Singleton](inline value: A)(using CompileTime.NonEmptyNoNul[A]): InstructionSetName =
      value

    extension (value: InstructionSetName) {
      inline def raw: String = value
    }
  }

  enum RuntimeType {
    case Void
    case Bool
    case Integer(width: BitWidth, signed: Boolean)
    case Floating(width: FloatingPointWidth)
    case VectorType(component: Int, count: Int)
    case Array(element: Int, count: Long)
    case Structure(members: Vec[Int])
    case Pointer(storageClass: Int, pointee: Int)
    case Function(result: Int, parameters: Vec[Int])
    case Event
  }

  enum Constant {
    case Bits(typeId: Int, lanes: Vec[Long])
    case NullPointer(typeId: Int, pointee: Int, storageClass: Int)
  }

  final case class Function(
      id: Int,
      parameters: Vec[(Int, Int)],
      start: Int,
      end: Int,
      labels: Map[Int, Int]
  )

  final case class Global(pointerType: Int, storageClass: Int, initializer: Option[Int])

  final case class Program(
      module: Module,
      types: Map[Int, RuntimeType],
      constantData: Map[Int, Constant],
      globals: Map[Int, Global],
      descriptorBindings: Map[Int, (Int, Int)],
      builtins: Map[Int, Int],
      arrayStrides: Map[Int, Int],
      memberOffsets: Map[(Int, Int), Int],
      packedStructures: Set[Int],
      byValueParameters: Set[Int],
      saturatedConversions: Set[Int],
      roundingModes: Map[Int, Int],
      extendedInstructionSets: Map[Int, InstructionSetName],
      entryPoints: Map[KernelName, Int],
      entryPointInterfaces: Map[KernelName, Set[Int]],
      vulkanEntries: Set[KernelName],
      subgroupSizes: Map[Int, Int],
      workGroupSizes: Map[Int, (Int, Int, Int)],
      workGroupSizeHints: Map[Int, (Int, Int, Int)],
      functions: Map[Int, Function],
      functionNames: Map[Int, FunctionName],
      collectiveFunctions: Set[Int]
  )

  private[v1_6] def zeroValue(
      types: collection.Map[Int, RuntimeType],
      resultType: Operand,
      destination: Operand,
      opcode: String
  ): Either[Fault, Constant] = {
    val typeId = id(resultType)
    types.get(typeId) match {
      case Some(RuntimeType.Pointer(storageClass, pointee)) =>
        Right(Constant.NullPointer(typeId, pointee, storageClass))
      case _ =>
        scalarLeafCount(types, typeId) match {
          case Right(count)                     => Right(Constant.Bits(typeId, Vec.fill(count)(0L)))
          case Left(Fault.InvalidTask(message)) =>
            Left(Fault.InvalidTask(s"$opcode %${id(destination)} has invalid result type: $message"))
          case Left(error) => Left(error)
        }
    }
  }

  private[v1_6] def scalarLeafCount(
      types: collection.Map[Int, RuntimeType],
      typeId: Int
  ): Either[Fault, Int] = types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) => Right(1)
    case Some(RuntimeType.Pointer(_, _))                => Right(1)
    case Some(RuntimeType.VectorType(component, count)) =>
      scalarLeafCount(types, component).flatMap(value => checkedLeafCount(typeId, value.toLong * count.toLong))
    case Some(RuntimeType.Array(element, count)) =>
      scalarLeafCount(types, element).flatMap(value => checkedLeafCount(typeId, value.toLong * count))
    case Some(RuntimeType.Structure(members)) =>
      members.foldLeft[Either[Fault, Int]](Right(0)) { (total, member) =>
        total.flatMap(left =>
          scalarLeafCount(types, member).flatMap(right => checkedLeafCount(typeId, left.toLong + right))
        )
      }
    case value => Left(Fault.InvalidTask(s"SPIR-V type %$typeId has no scalar data leaves: $value"))
  }

  private def checkedLeafCount(typeId: Int, count: Long): Either[Fault, Int] =
    Either.cond(
      count >= 0L && count <= Int.MaxValue.toLong,
      count.toInt,
      Fault.InvalidTask(s"SPIR-V type %$typeId has an invalid scalar leaf count: $count")
    )
}
