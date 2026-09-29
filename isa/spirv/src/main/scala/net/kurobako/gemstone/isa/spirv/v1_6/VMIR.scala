package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VMIR {
  import Operand.*

  enum RuntimeType {
    case Void
    case Bool
    case Integer(width: Int where Between[1, 64], signed: Boolean)
    case Floating(width: OneOf[(16, 32, 64)])
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

  case class Function(
      id: Int,
      parameters: Vec[(Int, Int)],
      start: Int,
      end: Int,
      labels: Map[Int, Int]
  )

  case class Global(pointerType: Int, storageClass: Int, initializer: Option[Int])

  case class Program(
      module: Module,
      types: Map[Int, RuntimeType],
      constantData: Map[Int, Constant],
      globals: Map[Int, Global],
      descriptorBindings: Map[Int, (Int >= 0, Int >= 0)],
      builtins: Map[Int, Int],
      arrayStrides: Map[Int, Int],
      memberOffsets: Map[(Int, Int), Int],
      packedStructures: Set[Int],
      byValueParameters: Set[Int],
      saturatedConversions: Set[Int],
      roundingModes: Map[Int, Int],
      extendedInstructionSets: Map[Int, NonEmpty[String]],
      entryPoints: Map[NonEmpty[String], Int],
      entryPointInterfaces: Map[NonEmpty[String], Set[Int]],
      vulkanEntries: Set[NonEmpty[String]],
      subgroupSizes: Map[Int, Int],
      workGroupSizes: Map[Int, LaunchGeometry.Dimensions],
      builtinWorkGroupSize: Option[LaunchGeometry.Dimensions],
      workGroupSizeHints: Map[Int, LaunchGeometry.Dimensions],
      functions: Map[Int, Function],
      functionNames: Map[Int, String],
      collectiveFunctions: Set[Int]
  )

  def zeroValue(
      types: scala.collection.Map[Int, RuntimeType],
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

  def scalarLeafCount(
      types: scala.collection.Map[Int, RuntimeType],
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
