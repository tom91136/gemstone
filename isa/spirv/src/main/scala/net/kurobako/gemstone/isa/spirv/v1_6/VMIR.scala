package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

private object VMIR {
  import Operand.*

  enum RuntimeType {
    case Void
    case Bool
    case Integer(width: ScalarBitWidth, signed: Boolean)
    case Floating(width: Int in (16, 32, 64))
    case VectorType(component: Int, count: Int > 0)
    case Array(element: Int, count: Long >= 0)
    case Structure(members: Vec[Int])
    case Pointer(storageClass: Int, pointee: Int)
    case Function(result: Int, parameters: Vec[Int])
    case Event
    case StorageImage(
        sampledType: Int,
        format: Int,
        dimensions: Int = 2,
        texelBuffer: Boolean = false,
        arrayed: Boolean = false,
        cube: Boolean = false
    )
    case Texture(sampledType: Int, dimensions: Int = 2, texelBuffer: Boolean = false, arrayed: Boolean = false)
    case Sampler
    case CubeTexture(sampledType: Int, arrayed: Boolean)
    case SampledImage(imageType: Int)
    case MatrixType(column: Int, count: Int in 2 ~ 4)
    case InputAttachment(sampledType: Int, multisampled: Boolean)
    case MultisampledTexture(sampledType: Int, arrayed: Boolean = false)
  }

  case class MatrixLayout(stride: Int > 0, rowMajor: Boolean)

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
      arrayStrides: Map[Int, Int > 0],
      memberOffsets: Map[(Int, Int), Int >= 0],
      memberMatrixLayouts: Map[(Int, Int), MatrixLayout],
      packedStructures: Set[Int],
      byValueParameters: Set[Int],
      saturatedConversions: Set[Int],
      roundingModes: Map[Int, Int],
      extendedInstructionSets: Map[Int, NonEmpty[String]],
      entryPoints: Map[NonEmpty[String], Int],
      entryPointInterfaces: Map[NonEmpty[String], Set[Int]],
      vulkanEntries: Set[NonEmpty[String]],
      graphicsEntries: Set[NonEmpty[String]],
      subgroupSizes: Map[Int, Int],
      workGroupSizes: Map[Int, LaunchGeometry.Dimensions],
      builtinWorkGroupSize: Option[LaunchGeometry.Dimensions],
      workGroupSizeHints: Map[Int, LaunchGeometry.Dimensions],
      functions: Map[Int, Function],
      functionNames: Map[Int, String],
      collectiveFunctions: Set[Int]
  )

  // Quantization retains a float32 representation but flushes values smaller
  // than the minimum normal half to signed zero, as OpQuantizeToF16 requires.
  def quantizeToF16(bits: Long): Long = {
    val value      = IEEE754.binary32ToFloat(Binary32.low(bits))
    val half: Long = if (Math.abs(value) < Math.scalb(1.0f, -14)) (bits >>> 16) & 0x8000L
    else IEEE754.floatToBinary16(value).raw
    IEEE754.floatToBinary32(IEEE754.binary16ToFloat(Binary16.low(half.toLong))).raw.toLong
  }

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
      case Some(_: RuntimeType.Texture | _: RuntimeType.StorageImage | _: RuntimeType.CubeTexture |
          _: RuntimeType.MultisampledTexture | _: RuntimeType.InputAttachment | _: RuntimeType.SampledImage | RuntimeType.Sampler)
          if opcode == "OpUndef" =>
        // Opaque values are represented by resource addresses. An undefined
        // handle can be carried through dead code without allocating a resource.
        Right(Constant.NullPointer(typeId, typeId, 0))
      case _ =>
        scalarLeafCount(types, typeId) match {
          case Right(count)                     => Right(Constant.Bits(typeId, Vec.fill(count.raw)(0L)))
          case Left(Fault.InvalidTask(message)) =>
            Left(Fault.InvalidTask(s"$opcode %${id(destination)} has invalid result type: $message"))
          case Left(error) => Left(error)
        }
    }
  }

  def scalarLeafCount(
      types: scala.collection.Map[Int, RuntimeType],
      typeId: Int
  ): Either[Fault, Int >= 0] = types.get(typeId) match {
    case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
        Some(RuntimeType.Event) => Right(1)
    case Some(RuntimeType.Pointer(_, _))                => Right(1)
    case Some(RuntimeType.VectorType(component, count)) =>
      scalarLeafCount(types, component).flatMap(value => checkedLeafProduct(typeId, value, count.toLong.widen[Long >= 0]))
    case Some(RuntimeType.MatrixType(column, count)) =>
      scalarLeafCount(types, column).flatMap(value => checkedLeafProduct(typeId, value, count.toLong.widen[Long >= 0]))
    case Some(RuntimeType.Array(element, count)) =>
      scalarLeafCount(types, element).flatMap(value => checkedLeafProduct(typeId, value, count))
    case Some(RuntimeType.Structure(members)) =>
      members.foldLeft[Either[Fault, Int >= 0]](Right(0)) { (total, member) =>
        total.flatMap(left =>
          scalarLeafCount(types, member).flatMap(right => checkedLeafCount(typeId, left.raw.toLong + right.raw))
        )
      }
    case value => Left(Fault.InvalidTask(s"SPIR-V type %$typeId has no scalar data leaves: $value"))
  }

  private def checkedLeafProduct(typeId: Int, leaves: Int >= 0, count: Long >= 0): Either[Fault, Int >= 0] =
    if (leaves.raw == 0) Right(0)
    else if (count.raw > Int.MaxValue.toLong / leaves.raw)
      Left(Fault.InvalidTask(s"SPIR-V type %$typeId has an invalid scalar leaf count: $leaves * ${count.raw}"))
    else checkedLeafCount(typeId, leaves.raw.toLong * count.raw)

  private def checkedLeafCount(typeId: Int, count: Long): Either[Fault, Int >= 0] =
    Either.cond(
      count >= 0L && count <= Int.MaxValue.toLong,
      count.toInt.unsafe[Int >= 0],
      Fault.InvalidTask(s"SPIR-V type %$typeId has an invalid scalar leaf count: $count")
    )
}
