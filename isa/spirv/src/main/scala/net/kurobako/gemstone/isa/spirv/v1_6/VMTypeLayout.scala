package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicIntegerArray
import java.util.concurrent.atomic.AtomicReferenceArray
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*
import scala.collection.concurrent.TrieMap

/** Immutable type definitions and their memoized storage layout. */
private class VMTypeLayout(ir: VMIR.Program) {
  private val typeLookup = {
    val bound = ir.types.keys.view.maxOption.fold(0L)(id => id.toLong + 1L)
    val dense =
      if (bound > 0L && bound <= 1000000L) Array.fill[Option[RuntimeType]](bound.toInt)(None)
      else Array.empty[Option[RuntimeType]]
    ir.types.foreach { case (id, value) =>
      if (id >= 0 && id < dense.length) dense(id) = Some(value)
    }
    dense
  }

  // Scalar/vector shapes are immutable. Keep their tuples alongside the dense type lookup instead of
  // rebuilding Option/tuple pairs on every arithmetic instruction.
  private def integerShapeOf(value: RuntimeType): Option[(Int in 1 ~ 64, Int)] = value match {
    case RuntimeType.Integer(width, _)            => Some((width, 1))
    case RuntimeType.VectorType(component, count) => runtimeType(component) match {
        case Some(RuntimeType.Integer(width, _)) => Some((width, count.raw))
        case _                                   => None
      }
    case _ => None
  }
  private def floatingShapeOf(value: RuntimeType): Option[(Int in (16, 32, 64), Int)] = value match {
    case RuntimeType.Floating(width)              => Some((width, 1))
    case RuntimeType.VectorType(component, count) => runtimeType(component) match {
        case Some(RuntimeType.Floating(width)) => Some((width, count.raw))
        case _                                 => None
      }
    case _ => None
  }
  private val integerShapes  = typeLookup.map(_.flatMap(integerShapeOf(_)))
  private val floatingShapes = typeLookup.map(_.flatMap(floatingShapeOf(_)))

  def integerShape(typeId: Int): Option[(Int in 1 ~ 64, Int)] =
    if (typeId >= 0 && typeId < integerShapes.length) integerShapes(typeId)
    else runtimeType(typeId).flatMap(integerShapeOf(_))

  def floatingShape(typeId: Int): Option[(Int in (16, 32, 64), Int)] =
    if (typeId >= 0 && typeId < floatingShapes.length) floatingShapes(typeId)
    else runtimeType(typeId).flatMap(floatingShapeOf(_))

  def memberMatrixLayouts: Map[(Int, Int), MatrixLayout] = ir.memberMatrixLayouts
  private val matrixStorageTypes                         = TrieMap.empty[Int, Boolean]
  private val matrixLeafOffsets              = TrieMap.empty[(Int, Option[MatrixLayout]), Vector[(Long >= 0, Int)]]
  def hasMatrixStorage(typeId: Int): Boolean =
    matrixStorageTypes.getOrElseUpdate(
      typeId,
      runtimeType(typeId) match {
        case Some(RuntimeType.Structure(members)) => members.zipWithIndex.exists { case (member, index) =>
            memberMatrixLayouts.contains(typeId -> index) || hasMatrixStorage(member)
          }
        case Some(RuntimeType.Array(element, _)) => hasMatrixStorage(element)
        case _                                   => false
      }
    )

  // Enumerate logical scalar lanes in column order and map them to their physical bytes.
  // Loading/storing only these leaves preserves padding and supports row-major column views.
  // Collection traversal supplies nonnegative indices; retain that proof through offset arithmetic.
  private def scaled(index: Int >= 0, stride: Int >= 0): Long >= 0 =
    index.toLong.widen[Long >= 0] * stride.toLong.widen[Long >= 0]

  def matrixStorageLeaves(typeId: Int, layout: Option[MatrixLayout]): Vector[(Long >= 0, Int)] =
    matrixLeafOffsets.getOrElseUpdate(
      typeId -> layout, {
        def shifted(element: Int, context: Option[MatrixLayout], base: Long >= 0): Vector[(Long >= 0, Int)] =
          matrixStorageLeaves(element, context).map { case (offset, scalar) =>
            (base + offset) -> scalar
          }
        runtimeType(typeId) match {
          case Some(RuntimeType.Structure(members)) => members.zipWithIndex.toVector.flatMap { case (member, index) =>
              shifted(
                member,
                memberMatrixLayouts.get(typeId -> index),
                structMemberOffset(typeId, members, index).toLong.widen[Long >= 0]
              )
            }
          case Some(RuntimeType.Array(element, count)) =>
            val stride = ir.arrayStrides.get(typeId).fold(storageTypeSize(element, layout))(_.widen[Int >= 0])
            Vector.tabulate(Math.toIntExact(count.raw))(index =>
              shifted(element, layout, scaled(index.unsafe[Int >= 0], stride))
            ).flatten
          case Some(RuntimeType.MatrixType(column, columns)) =>
            runtimeType(column) match {
              case Some(RuntimeType.VectorType(component, rows)) =>
                val scalarBytes = typeSize(component)
                val stride      = layout.fold(typeSize(column))(_.stride.widen[Int >= 0])
                if (stride.raw <= 0) throw IllegalArgumentException("invalid matrix stride")
                Vector.tabulate(columns.raw, rows.raw) { (columnIndex, rowIndex) =>
                  val c      = columnIndex.unsafe[Int >= 0]
                  val r      = rowIndex.unsafe[Int >= 0]
                  val offset = if (layout.exists(_.rowMajor)) scaled(r, stride) + scaled(c, scalarBytes)
                  else scaled(c, stride) + scaled(r, scalarBytes)
                  offset -> component
                }.flatten
              case _ => throw IllegalArgumentException("invalid matrix column type")
            }
          case Some(RuntimeType.VectorType(component, count)) =>
            val stride = layout.filter(_.rowMajor).fold(typeSize(component))(_.stride.widen[Int >= 0])
            Vector.tabulate(count.raw)(index => scaled(index.unsafe[Int >= 0], stride) -> component)
          case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
              Some(RuntimeType.Pointer(_, _)) => Vector((0L: Long >= 0) -> typeId)
          case value => throw IllegalArgumentException(s"unsupported matrix storage leaf $value")
        }
      }
    )

  def runtimeType(id: Int): Option[RuntimeType] =
    if (id >= 0 && id < typeLookup.length) typeLookup(id) else ir.types.get(id)

  private val denseTypeSizeCache                            = AtomicIntegerArray(typeLookup.length)
  private val overflowTypeSizeCache: TrieMap[Int, Int >= 0] = TrieMap.empty

  def typeSize(id: Int): Int >= 0 =
    if (id >= 0 && id < typeLookup.length) {
      val cached = denseTypeSizeCache.get(id)
      if (cached != 0) (cached - 1).unsafe[Int >= 0]
      else {
        val size = typeSizeUncached(id)
        // Zero is the empty cache slot; the largest valid size cannot use the size + 1 representation.
        if (size.raw == Int.MaxValue) overflowTypeSizeCache.getOrElseUpdate(id, size)
        else {
          val _ = denseTypeSizeCache.compareAndSet(id, 0, size.raw + 1)
          size
        }
      }
    } else overflowTypeSizeCache.getOrElseUpdate(id, typeSizeUncached(id))

  private val typeAlignmentCache: TrieMap[Int, Int > 0] = TrieMap.empty
  private val denseScalarLeafCounts = AtomicReferenceArray[Either[Fault, Int >= 0] | Null](typeLookup.length)
  private val scalarLeafCountCache: TrieMap[Int, Either[Fault, Int >= 0]] = TrieMap.empty

  def scalarMemoryWidth(typeId: Int): Either[Fault, Int in 1 ~ 8] = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)              => Right(1)
    case Some(RuntimeType.Integer(width, _)) => Right(width.toScalarByteWidth)
    case Some(RuntimeType.Floating(width))   => Right(width.scalarByteWidth)
    case _                                   =>
      val size = typeSize(typeId)
      size.raw.refine[Int in 1 ~ 8].leftMap(_ =>
        Fault.InvalidTask(s"SPIR-V scalar type %$typeId has invalid byte width $size")
      )
  }

  def storageTypeSize(typeId: Int, layout: Option[MatrixLayout]): Int >= 0 =
    (runtimeType(typeId), layout) match {
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
        val stride: Int >= 0 = ir.arrayStrides.get(typeId).fold(storageTypeSize(element, layout))(_.widen[Int >= 0])
        (stride.toLong * count).toIntExact
      case _ => typeSize(typeId)
    }

  private def typeSizeUncached(typeId: Int): Int >= 0 = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)              => 1
    case Some(RuntimeType.Integer(width, _)) => width.toScalarByteWidth.nonNegative
    case Some(RuntimeType.Floating(width))   => width.scalarByteWidth
    case Some(RuntimeType.MultisampledTexture(_, _)) | Some(RuntimeType.InputAttachment(
          _,
          _
        )) | Some(RuntimeType.Event) | Some(RuntimeType.StorageImage(
          _,
          _,
          _,
          _,
          _,
          _
        )) | Some(RuntimeType.Texture(_, _, _, _)) | Some(RuntimeType.CubeTexture(
          _,
          _
        )) | Some(RuntimeType.Sampler) | Some(RuntimeType.SampledImage(_)) => 8
    case Some(RuntimeType.VectorType(component, n)) =>
      typeSize(component) * (if (n.raw == 3 && ir.vulkanEntries.isEmpty) 4: Int >= 0 else n)
    case Some(RuntimeType.MatrixType(column, count)) => typeSize(column) * count
    case Some(RuntimeType.Array(element, n))         =>
      val stride: Int >= 0 = ir.arrayStrides.get(typeId).fold(typeSize(element))(identity)
      (stride.toLong * n).toIntExact
    case Some(RuntimeType.Structure(members)) => structSize(typeId, members)
    case Some(RuntimeType.Pointer(_, _))      => 8
    case Some(RuntimeType.Void)               => 0
    case Some(RuntimeType.Function(_, _))     => 0
    case None                                 => throw IllegalArgumentException(s"SPIR-V type %$typeId is undefined")
  }

  // Struct layout carries offset/alignment state and uses the packed-layout fast exit.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def structSize(structureType: Int, members: Vec[Int]): Int >= 0 = {
    if (ir.packedStructures.contains(structureType))
      return members.foldLeft[Int >= 0](0)((size, member) => size + typeSize(member))
    var offset: Int >= 0 = 0
    var extent: Int >= 0 = 0
    var align: Int > 0   = 1
    var index            = 0
    while (index < members.size) {
      val memberAlign = typeAlignment(members(index))
      offset = ir.memberOffsets.getOrElse(structureType -> index, alignUp(offset, memberAlign))
      offset = offset + storageTypeSize(members(index), memberMatrixLayouts.get(structureType -> index))
      extent = extent.max(offset)
      align = align.max(memberAlign)
      index += 1
    }
    alignUp(extent, align)
  }

  // Struct member layout carries offset/alignment state through one ordered scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def structMemberOffset(
      structureType: Int,
      members: Vec[Int],
      selected: Int
  ): Int >= 0 =
    if (ir.packedStructures.contains(structureType))
      members.take(selected).foldLeft[Int >= 0](0)((size, member) => size + typeSize(member))
    else ir.memberOffsets.get(structureType -> selected).getOrElse {
      var offset: Int >= 0 = 0
      var index            = 0
      while (index < selected) {
        offset = ir.memberOffsets.getOrElse(structureType -> index, alignUp(offset, typeAlignment(members(index))))
        offset = offset + storageTypeSize(members(index), memberMatrixLayouts.get(structureType -> index))
        index += 1
      }
      alignUp(offset, typeAlignment(members(selected)))
    }

  def typeAlignment(typeId: Int): Int > 0 =
    typeAlignmentCache.getOrElseUpdate(typeId, typeAlignmentUncached(typeId))

  private def typeAlignmentUncached(typeId: Int): Int > 0 = runtimeType(typeId) match {
    case Some(RuntimeType.Structure(_)) if ir.packedStructures.contains(typeId) => 1
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(_)).maxOption.getOrElse(1: Int > 0)
    case Some(RuntimeType.MatrixType(column, _))        => typeAlignment(column)
    case Some(RuntimeType.Array(element, _))            => typeAlignment(element)
    case Some(RuntimeType.VectorType(component, count)) =>
      typeAlignment(component) * (if (count.raw == 3) 4: Int > 0 else count)
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => (1: Int > 0).max(typeSize(typeId))
  }

  def scalarWidth(typeId: Int): ScalarBitWidth = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => 8
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width
    case Some(RuntimeType.Event)                    => 64
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  def pointerPointee(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  def scalarLeafCount(typeId: Int): Either[Fault, Int >= 0] =
    if (typeId >= 0 && typeId < denseScalarLeafCounts.length())
      denseScalarLeafCounts.get(typeId) match {
        case null =>
          val count = VMIR.scalarLeafCount(ir.types, typeId)
          val _     = denseScalarLeafCounts.compareAndSet(typeId, null, count)
          count
        case count => count
      }
    else scalarLeafCountCache.getOrElseUpdate(typeId, VMIR.scalarLeafCount(ir.types, typeId))

  def pointerLeafOffsets(
      typeId: Int,
      baseOffset: Long >= 0
  ): Either[Fault, Vec[(Long >= 0, Int)]] =
    try pointerLeafOffsetsUnchecked(typeId, baseOffset)
    catch {
      case _: ArithmeticException =>
        Left(Fault.InvalidTask(s"SPIR-V pointer leaf offsets overflow for type %$typeId at base ${baseOffset.raw}"))
    }

  private def pointerLeafOffsetsUnchecked(
      typeId: Int,
      baseOffset: Long >= 0
  ): Either[Fault, Vec[(Long >= 0, Int)]] = runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, _))      => Right(Vec(baseOffset -> typeId))
    case Some(RuntimeType.Structure(members)) =>
      members.zipWithIndex.foldLeft[Either[Fault, Vec[(Long >= 0, Int)]]](Right(Vec.empty)) {
        case (result, (member, index)) =>
          result.flatMap(existing =>
            pointerLeafOffsetsUnchecked(
              member,
              baseOffset + structMemberOffset(typeId, members, index).toLong.widen[Long >= 0]
            ).map(
              existing ++ _
            )
          )
      }
    case Some(RuntimeType.Array(element, count)) =>
      if (count.raw > Int.MaxValue.toLong)
        Left(Fault.InvalidTask(
          s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"
        ))
      else
        (0 until count.toIntExact.raw).foldLeft[Either[Fault, Vec[(Long >= 0, Int)]]](Right(Vec.empty)) {
          case (result, index) =>
            result.flatMap { existing =>
              val stride = ir.arrayStrides.get(typeId).fold(typeSize(element))(_.widen[Int >= 0])
              pointerLeafOffsetsUnchecked(element, baseOffset + scaled(index.unsafe[Int >= 0], stride)).map(existing ++
                _)
            }
        }
    case Some(_) => Right(Vec.empty)
    case None    =>
      Left(Fault.InvalidTask(s"SPIR-V aggregate member type %$typeId is undefined"))
  }

  def alignUp(value: Int >= 0, alignment: Int > 0): Int >= 0 = {
    val remainder       = value % alignment
    val extra: Int >= 0 = if (remainder.raw == 0) 0 else 1
    (value / alignment + extra) * alignment
  }
}
