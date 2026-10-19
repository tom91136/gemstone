package net.kurobako.gemstone.isa.spirv

import VMMemory.*
import VMProgram.*
import cats.syntax.all.*
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.isa.*
import net.kurobako.gemstone.vm.*
import scala.annotation.tailrec
import scala.collection.concurrent
import scala.jdk.CollectionConverters.*
import scala.util.control.NonFatal

private object VMProgram {
  enum RuntimeType derives CanEqual {
    case Void
    case Bool
    case Integer(width: ScalarBitWidth, signed: Boolean)
    case Floating(width: Int in (16, 32, 64))
    case Float8(format: Minifloat)
    case VectorType(component: Int, count: Int > 0)
    case Array(element: Int, count: Long >= 0)
    case Structure(members: Vec[Int])
    case Pointer(storageClass: Int, pointee: Int)
    case Function(result: Int, parameters: Vec[Int])
    case Event
    case NamedBarrier
    case Pipe(access: Int)
    case ReserveId
    case DeviceEvent
    case Queue
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

  trait LeafVisitor { def apply(offset: Long >= 0, component: Int): Unit }

  /** A cooperative matrix type of `rows` by `columns` components of `component`, which every invocation in its scope
    * holds whole in row-major lanes, so its `use` and `scope` constrain only how it may be combined. The VM types its
    * values as vectors of `rows * columns` components; `nv` marks SPV_NV_cooperative_matrix's type.
    */
  case class CooperativeMatrix(component: Int, scope: Long, rows: Int, columns: Int, use: Long, nv: Boolean)

  object TensorLayout {
    val BlockSize: Int  = 0
    val Dimension: Int  = 5
    val Stride: Int     = 10
    val Offset: Int     = 15
    val Span: Int       = 20
    val ClampValue: Int = 25
    val Lanes: Int      = 26
  }

  /** An SPV_NV_tensor_addressing layout of 1 to 5 dimensions, whose values the VM types as vectors of
    * [[TensorLayout.Lanes]] 32-bit words of state.
    */
  case class TensorLayout(dimensions: Int in 1 ~ 5, clampMode: Long)

  object TensorView {
    val Dimension: Int = 0
    val Stride: Int    = 5
    val Clip: Int      = 10
    val Lanes: Int     = 14
  }

  /** An SPV_NV_tensor_addressing view, whose values the VM types as vectors of [[TensorView.Lanes]] 32-bit words. */
  case class TensorView(dimensions: Int in 1 ~ 5, hasDimensions: Boolean, permutation: Vec[Int])

  // the type id is read on every constant of the hot path, and hoisting it turns the patterns into nested ones
  @SuppressWarnings(Array("baumkuchen:common-factor"))
  enum Constant {
    case Bits(typeId: Int, lanes: Vec[Long])
    case NullPointer(typeId: Int, pointee: Int, storageClass: Int)

    /** The address of a global variable, or of another address constant, through a constant access chain. */
    case GlobalAddress(typeId: Int, base: Int, element: Option[Long], indexes: Vec[Long])

    /** A composite whose `pointers` lanes hold the addresses of the given variables or address constants. */
    case Composite(typeId: Int, lanes: Vec[Long], pointers: Vec[(Int, Int)])

    /** An `OpConstantSampler`, as the words of the `ImageDescriptor.SamplerWord` ABI. */
    case Sampler(typeId: Int, words: Vec[Long])

    /** An `OpConstantFunctionPointerINTEL`, whose token takes the program's function pointer namespace. */
    case FunctionPointer(typeId: Int, function: Int)

    def value: VMValue = this match {
      case Bits(typeId, lanes)                        => VMValue.Bits(typeId, Vec.toArray(lanes))
      case NullPointer(typeId, pointee, storageClass) => VMValue.Address(typeId, Pointer(0L, pointee, storageClass))
      // Global addresses are only known per dispatch, which replaces these placeholders.
      case GlobalAddress(typeId = typeId)    => VMValue.Address(typeId, Pointer(0L, typeId, 0))
      case Composite(typeId, lanes, _)       => VMValue.Bits(typeId, Vec.toArray(lanes))
      case Sampler(typeId, _)                => VMValue.Address(typeId, Pointer(0L, typeId, 0))
      case FunctionPointer(typeId, function) => VMValue.Bits(typeId, Array(function.toLong))
    }
  }

  /** A pointer word at `offset` in a global's initial image that addresses `target` plus `addend` bytes. */
  case class Fixup(offset: Int, target: Int, addend: Long)

  case class GlobalImage(variable: Int, global: Global, bytes: Vec[Byte], fixups: Vec[Fixup])

  case class Function(id: Int, parameters: Vec[(Int, Int)], start: Int, end: Int, labels: Map[Int, Int])
      derives CanEqual

  case class Global(pointerType: Int, storageClass: Int, initialiser: Option[Int])

  /** Decoded module tables, before layouts and execution state are derived from them. */
  case class Tables(
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
      subgroupSizes: Map[Int, Int > 0],
      cooperativeMatrices: Map[Int, CooperativeMatrix],
      tensorLayouts: Map[Int, TensorLayout],
      tensorViews: Map[Int, TensorView],
      workGroupSizes: Map[Int, LaunchGeometry.Dimensions],
      builtinWorkGroupSize: Option[LaunchGeometry.Dimensions],
      workGroupSizeHints: Map[Int, LaunchGeometry.Dimensions],
      functions: Map[Int, Function],
      names: Map[Int, String],
      collectiveFunctions: Set[Int],
      valueTypes: Map[Int, Int],
      untypedPointee: Int,
      idBound: Int
  )

  def scalarLeafCount(types: scala.collection.Map[Int, RuntimeType], typeId: Int): Either[Fault, Int >= 0] =
    leafCount(types, typeId, Set.empty)

  private def leafCount(types: scala.collection.Map[Int, RuntimeType], typeId: Int, enclosing: Set[Int])
      : Either[Fault, Int >= 0] =
    if (enclosing.contains(typeId)) Left(Fault.InvalidRequest(s"SPIR-V type %$typeId contains itself"))
    else {
      def nested(child: Int) = leafCount(types, child, enclosing + typeId)
      types.get(typeId) match {
        case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
            Some(RuntimeType.Float8(_)) |
            Some(RuntimeType.Event) | Some(RuntimeType.NamedBarrier) | Some(RuntimeType.ReserveId) |
            Some(RuntimeType.DeviceEvent) |
            Some(RuntimeType.Queue) | Some(RuntimeType.Pointer(_, _)) => Right(1)
        case Some(RuntimeType.VectorType(component, count)) =>
          nested(component).flatMap(value => leafProduct(typeId, value, count.widen[Long >= 0]))
        case Some(RuntimeType.MatrixType(column, count)) =>
          nested(column).flatMap(value => leafProduct(typeId, value, count.widen[Long >= 0]))
        case Some(RuntimeType.Array(element, count)) =>
          nested(element).flatMap(value => leafProduct(typeId, value, count))
        case Some(RuntimeType.Structure(members)) =>
          members.foldLeft[Either[Fault, Int >= 0]](Right(0)) { (total, member) =>
            total.flatMap(left => nested(member).flatMap(right => leafTotal(typeId, left.toLong + right)))
          }
        case value => Left(Fault.InvalidRequest(s"SPIR-V type %$typeId has no scalar data leaves: $value"))
      }
    }

  private def leafProduct(typeId: Int, leaves: Int >= 0, count: Long >= 0): Either[Fault, Int >= 0] =
    if (leaves == 0) Right(0)
    else if (count > Int.MaxValue.toLong / leaves)
      Left(Fault.InvalidRequest(s"SPIR-V type %$typeId has an invalid scalar leaf count: $leaves * $count"))
    else leafTotal(typeId, leaves.toLong * count)

  private def leafTotal(typeId: Int, count: Long): Either[Fault, Int >= 0] =
    count.refine[Long in 0L ~ 2147483647L].bimap(
      _ => Fault.InvalidRequest(s"SPIR-V type %$typeId has an invalid scalar leaf count: $count"),
      _.toIntExact.widen[Int >= 0]
    )

  // Opaque values are resource addresses; an undefined handle can flow through dead code without a resource.
  def zeroValue(
      types: scala.collection.Map[Int, RuntimeType],
      typeId: Int,
      destination: Int,
      opcode: String
  ): Either[Fault, Constant] = types.get(typeId) match {
    case Some(RuntimeType.Pointer(storageClass, pointee)) => Right(Constant.NullPointer(typeId, pointee, storageClass))
    case Some(_: RuntimeType.Texture | _: RuntimeType.StorageImage | RuntimeType.CubeTexture(_, _) |
        RuntimeType.MultisampledTexture(_, _) | RuntimeType.InputAttachment(_, _) | RuntimeType.SampledImage(_) |
        RuntimeType.Pipe(_) | RuntimeType.Sampler) if opcode == "OpUndef" =>
      Right(Constant.NullPointer(typeId, typeId, 0))
    case _ =>
      scalarLeafCount(types, typeId) match {
        case Right(count)                        => Right(Constant.Bits(typeId, Vec.fill(count)(0L)))
        case Left(Fault.InvalidRequest(message)) =>
          Left(Fault.InvalidRequest(s"$opcode %$destination has invalid result type: $message"))
        case Left(error) => Left(error)
      }
  }
}

/** One loaded module: decoded tables, eager storage layouts and globals, which [[LoadedProgram]] lowers and runs. */
// LoadedProgram sits above the ops and lowering that see this, which keeps them acyclic
@SuppressWarnings(Array("baumkuchen:single-implementation"))
private abstract class VMProgram(tables: Tables) extends Backend.Program {
  export tables.*

  val vulkan: Boolean = vulkanEntries.nonEmpty

  val usesNonUniformCollectives: Boolean = module.instructions.exists(VMCollectives.isNonUniform)

  private val typeTable: Array[Option[RuntimeType]] = {
    val table = Array.fill[Option[RuntimeType]](idBound)(None)
    types.foreach { case (id, value) => table(id) = Some(value) }
    table
  }

  def runtimeType(id: Int): Option[RuntimeType] = if (id >= 0 && id < idBound) typeTable(id) else None

  /** Whether `pointee` is the pointee of SPV_KHR_untyped_pointers' pointer types, the bytes a module's untyped pointers
    * address; their values may point at any type.
    */
  def untyped(pointee: Int): Boolean = untypedPointee >= 0 && pointee == untypedPointee

  private val integerShapes: Array[Option[(Int in 1 ~ 64, Int)]] = typeTable.map(_.flatMap {
    case RuntimeType.Integer(width, _)            => Some((width, 1))
    case RuntimeType.VectorType(component, count) => runtimeType(component) match {
        case Some(RuntimeType.Integer(width, _)) => Some((width, count))
        case _                                   => None
      }
    case _ => None
  })

  private val floatingShapes: Array[Option[(Int in (16, 32, 64), Int)]] = typeTable.map(_.flatMap {
    case RuntimeType.Floating(width)              => Some((width, 1))
    case RuntimeType.VectorType(component, count) => runtimeType(component) match {
        case Some(RuntimeType.Floating(width)) => Some((width, count.raw))
        case _                                 => None
      }
    case _ => None
  })

  def integerShape(typeId: Int): Option[(Int in 1 ~ 64, Int)] =
    if (typeId >= 0 && typeId < idBound) integerShapes(typeId) else None

  def float8Shape(typeId: Int): Option[(Minifloat, Int)] = runtimeType(typeId).flatMap {
    case RuntimeType.Float8(format)               => Some((format, 1))
    case RuntimeType.VectorType(component, count) => runtimeType(component).collect {
        case RuntimeType.Float8(format) => (format, count)
      }
    case _ => None
  }

  def floatingShape(typeId: Int): Option[(Int in (16, 32, 64), Int)] =
    if (typeId >= 0 && typeId < idBound) floatingShapes(typeId) else None

  /** Component width, rows and columns of a matrix type. */
  def matrixShape(typeId: Int): Option[(Int in (16, 32, 64), Int, Int)] = runtimeType(typeId) match {
    case Some(RuntimeType.MatrixType(column, columns)) =>
      floatingShape(column).map { case (width, rows) => (width, rows, columns) }
    case _ => None
  }

  /** Lane width and count of an integer, floating-point or Boolean scalar or vector; Booleans occupy 8-bit lanes. */
  def numericShape(typeId: Int): Option[(Int in 1 ~ 64, Int)] =
    integerShape(typeId)
      .orElse(floatingShape(typeId).map((width, count) => width.widen[Int in 1 ~ 64] -> count))
      .orElse(booleanLanes(typeId).map((8: Int in 1 ~ 64) -> _))

  /** Scalar Boolean or Boolean vector lane count. */
  def booleanLanes(typeId: Int): Option[Int] = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                                                                              => Some(1)
    case Some(RuntimeType.VectorType(component, count)) if runtimeType(component).contains(RuntimeType.Bool) =>
      Some(count)
    case _ => None
  }

  /** Types whose data layout is well founded: no type contains itself other than through a pointer. */
  private val acyclic: Array[Boolean] = {
    val state                           = Array.ofDim[Byte](idBound)
    def children(typeId: Int): Vec[Int] = runtimeType(typeId) match {
      case Some(RuntimeType.VectorType(component, _)) => Vec(component)
      case Some(RuntimeType.MatrixType(column, _))    => Vec(column)
      case Some(RuntimeType.Array(element, _))        => Vec(element)
      case Some(RuntimeType.Structure(members))       => members
      case _                                          => Vec.empty
    }
    // 0 unvisited, 1 in progress, 2 well founded, 3 recursive
    def visit(typeId: Int): Boolean =
      if (typeId < 0 || typeId >= idBound) true
      else state(typeId) match {
        case 1 => false
        case 2 => true
        case 3 => false
        case _ =>
          state(typeId) = 1
          val result = children(typeId).forall(visit(_))
          state(typeId) = if (result) 2 else 3
          result
      }
    Array.tabulate(idBound)(visit(_))
  }

  // Layout derivations memoise into arrays while the constructor runs; nothing is computed after construction.
  // A failed derivation stores a sentinel and is recomputed by the accessor so the original fault surfaces at use.
  private val sizes      = Array.fill(idBound)(-2)
  private val alignments = Array.fill(idBound)(0)

  def typeSize(id: Int): Int >= 0 =
    if (id >= 0 && id < idBound && sizes(id) >= 0) sizes(id).unsafe[Int >= 0] else typeSizeOf(id)

  def typeAlignment(id: Int): Int > 0 =
    if (id >= 0 && id < idBound && alignments(id) > 0) alignments(id).unsafe[Int > 0] else typeAlignmentOf(id)

  private def typeSizeOf(id: Int): Int >= 0 =
    if (id >= 0 && id < idBound && sizes(id) >= 0) sizes(id).unsafe[Int >= 0]
    else {
      val size = typeSizeUncached(id)
      if (id >= 0 && id < idBound) sizes(id) = size
      size
    }

  private def typeAlignmentOf(id: Int): Int > 0 =
    if (id >= 0 && id < idBound && alignments(id) > 0) alignments(id).unsafe[Int > 0]
    else {
      val alignment = typeAlignmentUncached(id)
      if (id >= 0 && id < idBound) alignments(id) = alignment
      alignment
    }

  private def recursive(typeId: Int): Boolean = typeId >= 0 && typeId < idBound && !acyclic(typeId)

  private def typeSizeUncached(typeId: Int): Int >= 0 =
    if (recursive(typeId)) throw IllegalArgumentException(s"SPIR-V type %$typeId contains itself")
    else runtimeType(typeId) match {
      case Some(RuntimeType.Bool)              => 1
      case Some(RuntimeType.Integer(width, _)) => width.toScalarByteWidth.widen[Int >= 0]
      case Some(RuntimeType.Floating(width))   => width.toFloatByteWidth
      case Some(RuntimeType.Float8(_))         => 1
      case Some(RuntimeType.MultisampledTexture(_, _) | RuntimeType.InputAttachment(_, _) | RuntimeType.Event |
          RuntimeType.NamedBarrier |
          RuntimeType.ReserveId | RuntimeType.DeviceEvent | RuntimeType.Queue | RuntimeType.Pipe(_) |
          _: RuntimeType.StorageImage | _: RuntimeType.Texture |
          RuntimeType.CubeTexture(
            _,
            _
          ) | RuntimeType.Sampler | RuntimeType.SampledImage(_) | RuntimeType.Pointer(_, _)) => 8
      case Some(RuntimeType.VectorType(component, n)) =>
        typeSizeOf(component) * (if (n == 3 && !vulkan && !cooperativeMatrices.contains(typeId)) 4: Int >= 0 else n)
      case Some(RuntimeType.MatrixType(column, count)) => typeSizeOf(column) * count
      case Some(RuntimeType.Array(element, n))         =>
        val stride: Int >= 0 = arrayStrides.get(typeId).fold(typeSizeOf(element))(_.widen[Int >= 0])
        (stride.widen[Long >= 0] * n.widen[Long >= 0]).toIntExact
      case Some(RuntimeType.Structure(members))                      => structSize(typeId, members)
      case Some(RuntimeType.Void) | Some(RuntimeType.Function(_, _)) => 0
      case None => throw IllegalArgumentException(s"SPIR-V type %$typeId is undefined")
    }

  private def typeAlignmentUncached(typeId: Int): Int > 0 =
    if (recursive(typeId)) throw IllegalArgumentException(s"SPIR-V type %$typeId contains itself")
    else runtimeType(typeId) match {
      case Some(RuntimeType.Structure(_)) if packedStructures.contains(typeId) => 1
      case Some(RuntimeType.Structure(members))    => members.map(typeAlignmentOf(_)).maxOption.getOrElse(1: Int > 0)
      case Some(RuntimeType.MatrixType(column, _)) => typeAlignmentOf(column)
      case Some(RuntimeType.Array(element, _))     => typeAlignmentOf(element)
      case Some(RuntimeType.VectorType(component, _)) if cooperativeMatrices.contains(typeId) =>
        typeAlignmentOf(component)
      case Some(RuntimeType.VectorType(component, count)) =>
        typeAlignmentOf(component) * (if (count == 3) 4: Int > 0 else count)
      case Some(RuntimeType.Pointer(_, _)) => 8
      case _                               => (1: Int > 0).max(typeSizeOf(typeId))
    }

  def storageTypeSize(typeId: Int, layout: Option[MatrixLayout]): Int >= 0 = (runtimeType(typeId), layout) match {
    case (Some(RuntimeType.MatrixType(column, columns)), Some(value)) =>
      val rows: Int > 0 = runtimeType(column) match {
        case Some(RuntimeType.VectorType(_, count)) => count
        case _                                      => throw IllegalArgumentException("invalid matrix column type")
      }
      val count: Int > 0 = if (value.rowMajor) rows else columns
      count * value.stride
    case (Some(RuntimeType.VectorType(component, count)), Some(value)) if value.rowMajor =>
      val previous: Int >= 0 = count
      (previous - 1) * value.stride + typeSize(component)
    case (Some(RuntimeType.Array(element, count)), Some(_)) =>
      val stride: Int >= 0 = arrayStrides.get(typeId).fold(storageTypeSize(element, layout))(_.widen[Int >= 0])
      (stride.widen[Long >= 0] * count.widen[Long >= 0]).toIntExact
    case _ => typeSize(typeId)
  }

  // Struct layout carries offset and alignment state; packed structures concatenate their members.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def memberOffsetTable(structureType: Int, members: Vec[Int]): Array[Int] = {
    val offsets          = Array.ofDim[Int](members.size + 1)
    var offset: Int >= 0 = 0
    var extent: Int >= 0 = 0
    var index            = 0
    while (index < members.size) {
      val member = members(index)
      offset =
        if (packedStructures.contains(structureType)) offset
        else memberOffsets.getOrElse(structureType -> index, offset.alignUp(typeAlignmentOf(member)))
      offsets(index) = offset
      offset =
        if (packedStructures.contains(structureType)) offset + typeSizeOf(member)
        else offset + storageTypeSize(member, memberMatrixLayouts.get(structureType -> index))
      extent = extent.max(offset)
      index += 1
    }
    offsets(members.size) = extent
    offsets
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val memberOffsetTables = Array.fill[Array[Int] | Null](idBound)(null)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def memberOffsetsOf(structureType: Int, members: Vec[Int]): Array[Int] =
    if (structureType >= 0 && structureType < idBound && memberOffsetTables(structureType) != null)
      memberOffsetTables(structureType).nn
    else {
      val table = memberOffsetTable(structureType, members)
      if (structureType >= 0 && structureType < idBound) memberOffsetTables(structureType) = table
      table
    }

  private def structSize(structureType: Int, members: Vec[Int]): Int >= 0 = {
    val extent = memberOffsetsOf(structureType, members)(members.size).unsafe[Int >= 0]
    if (packedStructures.contains(structureType)) extent
    else extent.alignUp(typeAlignmentOf(structureType))
  }

  def structMemberOffset(structureType: Int, members: Vec[Int], selected: Int): Int >= 0 =
    memberOffsetsOf(structureType, members)(selected).unsafe[Int >= 0]

  private val leafCounts = Array.tabulate[Either[Fault, Int >= 0]](idBound) { id =>
    if (acyclic(id)) VMProgram.scalarLeafCount(types, id)
    else Left(Fault.InvalidRequest(s"SPIR-V type %$id contains itself"))
  }

  def scalarLeafCount(typeId: Int): Either[Fault, Int >= 0] =
    if (typeId >= 0 && typeId < idBound) leafCounts(typeId) else VMProgram.scalarLeafCount(types, typeId)

  def scalarMemoryWidth(typeId: Int): Either[Fault, Int in 1 ~ 8] = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)              => Right(1)
    case Some(RuntimeType.Integer(width, _)) => Right(width.toScalarByteWidth)
    case Some(RuntimeType.Floating(width))   => Right(width.toFloatByteWidth)
    case Some(RuntimeType.Float8(_))         => Right(1)
    case _                                   =>
      val size = typeSize(typeId)
      size.refine[Int in 1 ~ 8].leftMap(_ =>
        Fault.InvalidRequest(s"SPIR-V scalar type %$typeId has invalid byte width $size")
      )
  }

  def scalarWidth(typeId: Int): ScalarBitWidth = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => 8
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width
    case Some(RuntimeType.Float8(_))                => 8
    case Some(RuntimeType.Event)                    => 64
    case Some(RuntimeType.NamedBarrier)             => 64
    case Some(RuntimeType.ReserveId)                => 64
    case Some(RuntimeType.DeviceEvent)              => 64
    case Some(RuntimeType.Queue)                    => 64
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  def pointerPointee(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  private val matrixStorage: Array[Boolean] = {
    val result                        = Array.fill[Option[Boolean]](idBound)(None)
    def compute(typeId: Int): Boolean = typeId >= 0 && typeId < idBound && result(typeId).getOrElse {
      val value = runtimeType(typeId) match {
        case Some(RuntimeType.Structure(members)) =>
          members.indices.exists(index => memberMatrixLayouts.contains(typeId -> index) || compute(members(index)))
        case Some(RuntimeType.Array(element, _)) => compute(element)
        case _                                   => false
      }
      result(typeId) = Some(value)
      value
    }
    Array.tabulate(idBound)(id => acyclic(id) && compute(id))
  }

  def hasMatrixStorage(typeId: Int): Boolean = typeId >= 0 && typeId < idBound && matrixStorage(typeId)

  // Collection traversal supplies nonnegative indices; retain that proof through offset arithmetic.
  private def scaled(index: Int >= 0, stride: Int >= 0): Long >= 0 =
    index.widen[Long >= 0] * stride.widen[Long >= 0]

  /** Visit logical scalar lanes in order with their physical byte offsets. Visiting only the leaves preserves padding
    * and supports row-major column views; the default layout is the dense data layout.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def visitLeaves(typeId: Int, layout: Option[MatrixLayout], base: Long >= 0)(visit: LeafVisitor): Unit =
    if (recursive(typeId)) throw IllegalArgumentException(s"SPIR-V type %$typeId contains itself")
    else runtimeType(typeId) match {
      case Some(RuntimeType.Structure(members)) =>
        var index = 0
        while (index < members.size) {
          visitLeaves(
            members(index),
            memberMatrixLayouts.get(typeId -> index),
            base + structMemberOffset(typeId, members, index).widen[Long >= 0]
          )(visit)
          index += 1
        }
      case Some(RuntimeType.Array(element, count)) =>
        val stride = arrayStrides.get(typeId).fold(storageTypeSize(element, layout))(_.widen[Int >= 0])
        var index  = 0L
        while (index < count) {
          visitLeaves(element, layout, base + index.unsafe[Long >= 0] * stride.widen[Long >= 0])(visit)
          index += 1L
        }
      case Some(RuntimeType.MatrixType(column, columns)) => runtimeType(column) match {
          case Some(RuntimeType.VectorType(component, rows)) =>
            val scalarBytes = typeSize(component)
            val stride      = layout.fold(typeSize(column))(_.stride.widen[Int >= 0])
            val rowMajor    = layout.exists(_.rowMajor)
            if (stride <= 0) throw IllegalArgumentException("invalid matrix stride")
            var c = 0
            while (c < columns) {
              var r = 0
              while (r < rows) {
                val major = if (rowMajor) r else c
                val minor = if (rowMajor) c else r
                visit(base + scaled(major.unsafe, stride) + scaled(minor.unsafe, scalarBytes), component)
                r += 1
              }
              c += 1
            }
          case _ => throw IllegalArgumentException("invalid matrix column type")
        }
      case Some(RuntimeType.VectorType(component, count)) =>
        val stride = layout.filter(_.rowMajor).fold(typeSize(component))(_.stride.widen[Int >= 0])
        var index  = 0
        while (index < count) {
          visit(base + scaled(index.unsafe, stride), component)
          index += 1
        }
      case Some(RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_) | RuntimeType.Pointer(_, _) |
          RuntimeType.Event | RuntimeType.NamedBarrier | RuntimeType.ReserveId | RuntimeType.DeviceEvent |
          RuntimeType.Queue) =>
        visit(base, typeId)
      case value => throw IllegalArgumentException(s"SPIR-V type %$typeId has no data leaves: $value")
    }

  def matrixStorageLeaves(typeId: Int, layout: Option[MatrixLayout]): Vector[(Long >= 0, Int)] = {
    val leaves = Vector.newBuilder[(Long >= 0, Int)]
    visitLeaves(typeId, layout, 0L)((offset, scalar) => leaves += offset -> scalar)
    leaves.result()
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private val pointerLeaves: Array[Either[Fault, Vec[(Long >= 0, Int)]] | Null] = Array.fill(idBound)(null)

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def pointerLeafOffsets(typeId: Int, baseOffset: Long >= 0): Either[Fault, Vec[(Long >= 0, Int)]] = {
    val leaves =
      if (typeId >= 0 && typeId < idBound && pointerLeaves(typeId) != null) pointerLeaves(typeId).nn
      else {
        val computed =
          try pointerLeafOffsetsUnchecked(typeId, 0L)
          catch {
            case _: ArithmeticException =>
              Left(Fault.InvalidRequest(s"SPIR-V pointer leaf offsets overflow for type %$typeId at base 0"))
          }
        if (typeId >= 0 && typeId < idBound) pointerLeaves(typeId) = computed
        computed
      }
    if (baseOffset == 0L) leaves
    else leaves.flatMap(_.traverse { case (offset, pointerType) =>
      Either.catchOnly[ArithmeticException](Math.addExact(offset, baseOffset)).bimap(
        _ => Fault.InvalidRequest(s"SPIR-V pointer leaf offsets overflow for type %$typeId at base $baseOffset"),
        sum => sum.unsafe[Long >= 0] -> pointerType
      )
    })
  }

  private def pointerLeafOffsetsUnchecked(typeId: Int, baseOffset: Long >= 0): Either[Fault, Vec[(Long >= 0, Int)]] =
    if (recursive(typeId)) Left(Fault.InvalidRequest(s"SPIR-V type %$typeId contains itself"))
    else runtimeType(typeId) match {
      case Some(RuntimeType.Pointer(_, _))      => Right(Vec(baseOffset -> typeId))
      case Some(RuntimeType.Structure(members)) =>
        Traversal.traverse(Vec.indices(members.nonNegativeSize))(index =>
          pointerLeafOffsetsUnchecked(
            members(index),
            baseOffset + structMemberOffset(typeId, members, index).widen[Long >= 0]
          )
        ).map(_.flatten)
      case Some(RuntimeType.Array(element, count)) =>
        if (count > Int.MaxValue.toLong)
          Left(Fault.InvalidRequest(s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"))
        else {
          val stride = arrayStrides.get(typeId).fold(typeSize(element))(_.widen[Int >= 0])
          Traversal.traverse(Vec.indices(count.toIntExact))(index =>
            pointerLeafOffsetsUnchecked(element, baseOffset + scaled(index, stride))
          ).map(_.flatten)
        }
      case Some(_) => Right(Vec.empty)
      case None    => Left(Fault.InvalidRequest(s"SPIR-V aggregate member type %$typeId is undefined"))
    }

  types.keys.filter(acyclic).foreach { id =>
    try {
      val _ = typeSizeOf(id)
      val _ = typeAlignmentOf(id)
      val _ = pointerLeafOffsets(id, 0L)
    } catch { case NonFatal(_) => () }
  }

  private val valueTypeTable: Array[Int] = {
    val table = Array.fill(idBound)(-1)
    valueTypes.foreach { case (id, typeId) => table(id) = typeId }
    table
  }

  private val scalarSlots: Array[Boolean] = valueTypeTable.map(typeId =>
    runtimeType(typeId).exists {
      case RuntimeType.Bool | RuntimeType.Integer(_, _) | RuntimeType.Floating(_) => true
      case _                                                                      => false
    }
  )

  /** The static result type of a value id, or -1. */
  def valueType(id: Int): Int = if (id >= 0 && id < idBound) valueTypeTable(id) else -1

  /** Whether the value lives unboxed in the scalar register bank. */
  def scalarSlot(id: Int): Boolean = id >= 0 && id < idBound && scalarSlots(id)

  val constants: Map[Int, VMValue] = constantData.view.mapValues(_.value).toMap

  lazy val functionPointerConstants: Vector[(Int, Int, Long)] =
    constantData.collect { case (id, Constant.FunctionPointer(typeId, function)) => (id, typeId, function.toLong) }
      .toVector

  // Zero identifies direct VM execution; published namespaces are bound once.
  private val functionPointerNamespace = java.util.concurrent.atomic.AtomicLong(0L)

  def ownsFunctionPointer(token: Long): Boolean = (token >>> 32) == functionPointerNamespace.get()

  /** The token of `function` in the program's function pointer namespace. */
  def functionPointer(function: Long): Long = (functionPointerNamespace.get() << 32) | function

  private val boundGlobals: concurrent.Map[Int, BoundGlobal] = ConcurrentHashMap[Int, BoundGlobal]().asScala

  override def global(name: NonEmpty[String]): Either[Fault, ProgramGlobal] =
    globalImage(name).flatMap { image =>
      ProgramGlobal.validated(image.bytes.length.toLong.unsafe[Long >= 0], image.bytes)
        .left.map(Fault.InvalidRequest(_))
    }

  override def bindGlobal(name: NonEmpty[String], binding: GlobalBinding, memory: AddressSpace): Either[Fault, Unit] =
    globalImage(name).flatMap { image =>
      val next = BoundGlobal(binding, memory)
      GlobalBinding.rebind(name, boundGlobals.putIfAbsent(image.variable, next), next).flatMap { first =>
        if (!first) Right(())
        else (for {
          _      <- memory.write(binding.region, binding.offset, image.bytes)
          _      <- relocate(image, None)
          images <- programImages
          _      <- images.traverse_(holder => relocate(holder, Some(image.variable)))
        } yield ()).left.map { error =>
          val _ = boundGlobals.remove(image.variable)
          error
        }
      }
    }

  /** Writes the pointer words of a bound global whose targets are bound, optionally only those addressing `target`. */
  private def relocate(image: GlobalImage, target: Option[Int]): Either[Fault, Unit] =
    boundGlobals.get(image.variable).fold[Either[Fault, Unit]](Right(())) { holder =>
      image.fixups.filter(fixup => target.forall(_ == fixup.target)).traverse_ { fixup =>
        boundGlobals.get(fixup.target).fold[Either[Fault, Unit]](Right(())) { bound =>
          for {
            from    <- shifted(bound.binding.offset, fixup.addend)
            address <- bound.memory.address(bound.binding.region, from)
            at      <- shifted(holder.binding.offset, fixup.offset.toLong)
            _       <- holder.memory.write(holder.binding.region, at, LittleEndian.toVec(address.raw, 8))
          } yield ()
        }
      }
    }

  private def shifted(base: Long >= 0, delta: Long): Either[Fault, Long >= 0] = (base.raw + delta).refine[Long >=
    0].leftMap(_ => Fault.InvalidRequest("global pointer initialiser precedes its target"))

  override def writeGlobal(name: NonEmpty[String], offset: Long >= 0, bytes: Vec[Byte]): Either[Fault, Unit] =
    globalVariable(name).flatMap { id =>
      boundGlobals.get(id).toRight(Fault.InvalidRequest(s"SPIR-V global '$name' is not bound")).flatMap(
        _.write(offset, bytes)(Fault.InvalidRequest(s"SPIR-V global '$name' write exceeds its storage"))
      )
    }

  override def symbols: Either[Fault, ProgramSymbols] =
    persistentGlobals.flatMap(_.traverse { image =>
      GlobalSymbol.validated(
        globalNames(image.variable).unsafe[NonEmpty[String]],
        None,
        image.bytes.length.toLong.unsafe[Long >= 0],
        Vec.empty
      ).leftMap(Fault.InvalidRequest(_))
    }).map(globals => ProgramSymbols(kernelNames.map(name => KernelSymbol(name, name)), Vec.from(globals)))

  override def bindFunctionPointerNamespace(id: ProgramId): Either[Fault, Unit] =
    if (id.raw > 0xffffffffL) Left(Fault.InvalidRequest("function pointer namespace must fit a 32-bit identity"))
    else if (functionPointerNamespace.compareAndSet(0L, id.raw) || functionPointerNamespace.get() == id.raw) Right(())
    else Left(Fault.InvalidRequest("function pointer namespace is already bound"))

  def kernelNames: Vec[NonEmpty[String]] = Vec.from(entryPoints.keys.toSeq.sorted)

  /** Unique OpNames identify globals; other globals are named by their id. */
  private lazy val globalNames: Map[Int, String] = {
    val named = globals.keys.flatMap(id => names.get(id).filter(_.nonEmpty).map(id -> _)).toMap
    val taken = named.values.groupBy(identity).view.mapValues(_.size).toMap
    globals.keys.map(id => id -> named.get(id).filter(taken(_) == 1).getOrElse(s"%$id")).toMap
  }

  private def globalVariable(name: String): Either[Fault, Int] =
    globalNames.collectFirst { case (id, `name`) => id }.toRight(Fault.NotFound(
      s"SPIR-V global '$name' is not present"
    ))

  private lazy val programImages: Either[Fault, Vector[GlobalImage]] =
    globalImages(storageClass => storageClass != StorageClass.Workgroup && storageClass != StorageClass.Private)

  /** CrossWorkgroup globals, which outlive a dispatch, and the globals their initialisers address. */
  private lazy val persistentGlobals: Either[Fault, Vector[GlobalImage]] = programImages.map { images =>
    val targets = images.map(image => image.variable -> image.fixups.map(_.target).toSet).toMap
    @tailrec def closure(selected: Set[Int]): Set[Int] = {
      val next = selected ++ selected.flatMap(targets.getOrElse(_, Set.empty))
      if (next == selected) selected else closure(next)
    }
    val selected = closure(images.collect {
      case image if image.global.storageClass == StorageClass.CrossWorkgroup => image.variable
    }.toSet)
    images.filter(image => selected(image.variable))
  }

  private def globalImage(name: String): Either[Fault, GlobalImage] =
    globalVariable(name).flatMap(id =>
      programImages.flatMap(_.find(_.variable == id).toRight(Fault.Memory(s"SPIR-V global '$name' has no storage")))
    )

  def workGroupSize(functionId: Int): Option[LaunchGeometry.Dimensions] =
    builtinWorkGroupSize.orElse(workGroupSizes.get(functionId))

  def globalImages(includeStorageClass: Int => Boolean): Either[Fault, Vector[GlobalImage]] =
    globals.toVector.filter { case (variable, global) =>
      !builtins.contains(variable) && includeStorageClass(global.storageClass)
    }.traverse((variable, global) => initialImage(variable, global))

  private def initialImage(variable: Int, global: Global): Either[Fault, GlobalImage] = {
    val size = typeSize(pointerPointee(global.pointerType)).raw
    def sized(bytes: Array[Byte], fixups: Vec[Fixup]): Either[Fault, GlobalImage] = Either.cond(
      bytes.length == size,
      GlobalImage(variable, global, Vec.unsafeWrapArray(bytes), fixups),
      Fault.InvalidRequest(s"SPIR-V global variable %$variable initialiser has ${bytes.length} bytes, expected $size")
    )
    global.initialiser match {
      case None => Right(GlobalImage(variable, global, Vec.unsafeWrapArray(Array.ofDim[Byte](size)), Vec.empty))
      case Some(initialiser) if addresses(initialiser) =>
        addressOf(initialiser).flatMap((target, addend) => sized(Array.ofDim[Byte](8), Vec(Fixup(0, target, addend))))
      case Some(initialiser) => constantData.get(initialiser) match {
          case Some(Constant.Composite(typeId, lanes, pointers)) =>
            for {
              bytes   <- encode(VMValue.Bits(typeId, Vec.toArray(lanes)))
              offsets <- leafOffsets(typeId)
              fixups  <- pointers.traverse((lane, target) =>
                addressOf(target).map((variable, addend) => Fixup(offsets(lane), variable, addend))
              )
              image <- sized(bytes, fixups)
            } yield image
          case _ => constants.get(initialiser) match {
              case None =>
                Left(Fault.InvalidRequest(s"SPIR-V global variable %$variable has undefined initialiser %$initialiser"))
              case Some(value) => encode(value).flatMap(sized(_, Vec.empty))
            }
        }
    }
  }

  /** Whether `id` is a global variable or a constant address of one. */
  private def addresses(id: Int): Boolean = globals.contains(id) ||
    (constantData.get(id) match {
      case Some(Constant.GlobalAddress(_, _, _, _)) => true
      case _                                        => false
    })

  /** The global variable that `id` addresses and the byte offset of the address within it. */
  def addressOf(id: Int): Either[Fault, (Int, Long)] =
    if (globals.contains(id)) Right(id -> 0L)
    else constantData.get(id) match {
      case Some(Constant.GlobalAddress(_, base, element, indexes)) =>
        for {
          start  <- addressOf(base)
          offset <- chainOffset(addressType(base), element, indexes)
        } yield start._1 -> (start._2 + offset)
      case _ => Left(Fault.InvalidRequest(s"SPIR-V value %$id is not a global address"))
    }

  private def addressType(id: Int): Int = globals.get(id).map(_.pointerType).getOrElse(constantData.get(id) match {
    case Some(Constant.GlobalAddress(typeId = typeId)) => typeId
    case _                                             => -1
  })

  private def chainOffset(pointerType: Int, element: Option[Long], indexes: Vec[Long]): Either[Fault, Long] = {
    def stride(typeId: Int, member: Int): Long =
      arrayStrides.get(typeId).fold(storageTypeSize(member, None).toLong)(_.raw.toLong)
    val pointee = pointerPointee(pointerType)
    indexes.foldLeft[Either[Fault, (Long, Int)]](Right(element.fold(0L)(_ * stride(pointerType, pointee)) -> pointee)) {
      case (Right((offset, typeId)), index) => runtimeType(typeId) match {
          case Some(RuntimeType.Structure(members)) if index >= 0 && index < members.size =>
            Right((offset + structMemberOffset(typeId, members, index.toInt), members(index.toInt)))
          case Some(RuntimeType.Array(member, _))         => Right((offset + index * stride(typeId, member), member))
          case Some(RuntimeType.VectorType(component, _)) =>
            Right((offset + index * typeSize(component), component))
          case _ => Left(Fault.InvalidRequest(s"a constant access chain cannot index SPIR-V type %$typeId"))
        }
      case (failure, _) => failure
    }.map(_._1)
  }

  private def leafOffsets(typeId: Int): Either[Fault, Vec[Int]] =
    try {
      val offsets = Vec.newBuilder[Int]
      visitLeaves(typeId, None, 0L)((offset, _) => offsets += offset.toIntExact)
      Right(offsets.result())
    } catch {
      case error: IllegalArgumentException => Left(Fault.InvalidRequest(throwables.detail(error)))
      case error: ArithmeticException      => Left(Fault.InvalidRequest(throwables.detail(error)))
    }

  /** Literal samplers, which every dispatch places beside its globals. */
  lazy val samplerConstants: Vector[(Int, Int, Vec[Long])] =
    constantData.collect { case (id, Constant.Sampler(typeId, words)) => (id, typeId, words) }.toVector

  private lazy val addressConstantIds: Vector[Int] = constantData.collect {
    case (id, Constant.GlobalAddress(_, _, _, _) | Constant.Composite(_, _, _)) => id
  }.toVector

  /** Address-valued constants resolved against the globals placed for one dispatch. */
  def addressConstants(placed: Map[Int, VMValue]): Either[Fault, Map[Int, VMValue]] = {
    def resolve(target: Int): Either[Fault, Option[Long]] = addressOf(target).map((variable, addend) =>
      placed.get(variable).collect { case VMValue.Address(_, pointer) => pointer.address + addend }
    )
    addressConstantIds.traverse { id =>
      constantData.get(id) match {
        case Some(Constant.GlobalAddress(typeId = typeId)) => resolve(id).map(_.flatMap(address =>
            runtimeType(typeId).collect { case RuntimeType.Pointer(storageClass, pointee) =>
              id -> VMValue.Address(typeId, Pointer(address, pointee, storageClass))
            }
          ))
        case Some(Constant.Composite(typeId, lanes, pointers)) =>
          val values = Vec.toArray(lanes)
          pointers.traverse_((lane, target) => resolve(target).map(_.foreach(values(lane) = _)))
            .map(_ => Some(id -> VMValue.Bits(typeId, values)))
        case _ => Right(None)
      }
    }.map(_.flatten.toMap)
  }

  def globalBinding(variable: Int): Option[GlobalBinding] = boundGlobals.get(variable).map(_.binding)

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def decode(typeId: Int, bytes: Array[Byte]): Either[Fault, VMValue] =
    scalarLeafCount(typeId).flatMap { count =>
      try {
        val lanes = Array.ofDim[Long](count)
        var used  = 0
        visitLeaves(typeId, None, 0L) { (offset, scalar) =>
          if (used >= lanes.length)
            throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too many scalar leaves")
          val width     = leafBytes(scalar)
          val start     = Math.min(offset.toIntExact, bytes.length)
          val available = Math.min(width, bytes.length - start).unsafe[Int in 0 ~ 8]
          lanes(used) = LittleEndian.decode(bytes, start, available)
          used += 1
        }
        if (used != lanes.length)
          Left(
            Fault.InvalidRequest(s"SPIR-V value of type %$typeId decoded $used scalar leaves, expected ${lanes.length}")
          )
        else Right(VMValue.Bits(typeId, lanes))
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidRequest(throwables.detail(error)))
        case error: ArithmeticException      => Left(Fault.InvalidRequest(throwables.detail(error)))
      }
    }

  def encode(value: VMValue): Either[Fault, Array[Byte]] = value match {
    case VMValue.Bits(typeId, lanes) =>
      try {
        val bytes = Array.ofDim[Byte](typeSize(typeId))
        writeLeaves(typeId, lanes, bytes, 0L)
        Right(bytes)
      } catch {
        case error: IllegalArgumentException => Left(Fault.InvalidRequest(throwables.detail(error)))
        case error: ArithmeticException      => Left(Fault.InvalidRequest(throwables.detail(error)))
      }
    case VMValue.Address(_, pointer) => Right(LittleEndian.toArray(pointer.address, 8))
  }

  /** Writes the scalar leaves of `typeId` at `base`; the lane count must match exactly. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def writeLeaves(typeId: Int, lanes: Array[Long], output: Array[Byte], base: Long >= 0): Unit = {
    var used = 0
    visitLeaves(typeId, None, base) { (offset, scalar) =>
      if (used >= lanes.length)
        throw IllegalArgumentException(s"SPIR-V value of type %$typeId has too few scalar leaves")
      LittleEndian.write(lanes(used), output, offset.toIntExact, leafBytes(scalar))
      used += 1
    }
    if (used != lanes.length)
      throw IllegalArgumentException(s"SPIR-V value of type %$typeId has ${lanes.length} scalar leaves, expected $used")
  }

  private def leafBytes(scalar: Int): Int in 1 ~ 8 = runtimeType(scalar) match {
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => scalarWidth(scalar).toScalarByteWidth
  }
}
