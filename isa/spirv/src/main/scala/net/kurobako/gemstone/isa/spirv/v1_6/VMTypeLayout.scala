package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
import VMMemory.*
import cats.syntax.all.*
import java.util.concurrent.atomic.AtomicIntegerArray
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
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

  val memberMatrixLayouts: Map[(Int, Int), VMMemory.MatrixLayout] = {
    val strides  = scala.collection.mutable.Map.empty[(Int, Int), Int]
    val rowMajor = scala.collection.mutable.Set.empty[(Int, Int)]
    ir.module.instructions.foreach {
      case Instruction.MemberDecorate(structure, member, decoration) =>
        val key = Operand.id(structure) -> Operand.id(member)
        if (Operand.unsigned(decoration) == Decoration.RowMajor) rowMajor += key
        if (Operand.unsigned(decoration) == Decoration.MatrixStride && decoration.words.length >= 2)
          strides.update(key, Math.toIntExact(decoration.words.apply(1).toUnsigned.raw))
      case _ => ()
    }
    strides.view.map { case (key, stride) => key -> VMMemory.MatrixLayout(stride, rowMajor.contains(key)) }.toMap
  }
  private val matrixStorageTypes             = TrieMap.empty[Int, Boolean]
  private val matrixLeafOffsets              = TrieMap.empty[(Int, Option[MatrixLayout]), Vector[(Long, Int)]]
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
  def matrixStorageLeaves(typeId: Int, layout: Option[MatrixLayout]): Vector[(Long, Int)] =
    matrixLeafOffsets.getOrElseUpdate(
      typeId -> layout, {
        def shifted(element: Int, context: Option[MatrixLayout], base: Long): Vector[(Long, Int)] =
          matrixStorageLeaves(element, context).map { case (offset, scalar) =>
            Math.addExact(base, offset) -> scalar
          }
        runtimeType(typeId) match {
          case Some(RuntimeType.Structure(members)) => members.zipWithIndex.toVector.flatMap { case (member, index) =>
              shifted(
                member,
                memberMatrixLayouts.get(typeId -> index),
                structMemberOffset(typeId, members, index).toLong
              )
            }
          case Some(RuntimeType.Array(element, count)) =>
            val stride = ir.arrayStrides.getOrElse(typeId, storageTypeSize(element, layout))
            Vector.tabulate(Math.toIntExact(count))(index => shifted(element, layout, index.toLong * stride)).flatten
          case Some(RuntimeType.MatrixType(column, columns)) =>
            runtimeType(column) match {
              case Some(RuntimeType.VectorType(component, rows)) =>
                val scalarBytes = typeSize(component)
                val stride      = layout.map(_.stride).getOrElse(typeSize(column))
                if (stride <= 0) throw IllegalArgumentException("invalid matrix stride")
                Vector.tabulate(columns, rows) { (c, r) =>
                  val offset = if (layout.exists(_.rowMajor)) r.toLong * stride + c.toLong * scalarBytes
                  else c.toLong * stride + r.toLong * scalarBytes
                  offset -> component
                }.flatten
              case _ => throw IllegalArgumentException("invalid matrix column type")
            }
          case Some(RuntimeType.VectorType(component, count)) =>
            val stride = layout.filter(_.rowMajor).map(_.stride).getOrElse(typeSize(component))
            Vector.tabulate(count)(index => index.toLong * stride -> component)
          case Some(RuntimeType.Bool) | Some(RuntimeType.Integer(_, _)) | Some(RuntimeType.Floating(_)) |
              Some(RuntimeType.Pointer(_, _)) => Vector(0L -> typeId)
          case value => throw IllegalArgumentException(s"unsupported matrix storage leaf $value")
        }
      }
    )

  def runtimeType(id: Int): Option[RuntimeType] =
    if (id >= 0 && id < typeLookup.length) typeLookup(id) else ir.types.get(id)

  private val denseTypeSizeCache                       = AtomicIntegerArray(typeLookup.length)
  private val overflowTypeSizeCache: TrieMap[Int, Int] = TrieMap.empty

  def typeSize(id: Int): Int =
    if (id >= 0 && id < typeLookup.length) {
      val cached = denseTypeSizeCache.get(id)
      if (cached != 0) cached - 1
      else {
        val size = typeSizeUncached(id)
        // Zero represents an empty slot, so unusual values retain the map fallback.
        if (size < 0 || size == Int.MaxValue) overflowTypeSizeCache.getOrElseUpdate(id, size)
        else {
          val _ = denseTypeSizeCache.compareAndSet(id, 0, size + 1)
          size
        }
      }
    } else overflowTypeSizeCache.getOrElseUpdate(id, typeSizeUncached(id))

  private val typeAlignmentCache: TrieMap[Int, Int]                  = TrieMap.empty
  private val scalarLeafCountCache: TrieMap[Int, Either[Fault, Int]] = TrieMap.empty

  def nonNegativeTypeSize(
      typeId: Int
  ): Either[Fault, Int >= 0] = {
    val size = typeSize(typeId)
    size.refine[Int >= 0]
      .leftMap(_ =>
        Fault.InvalidTask(s"SPIR-V type %$typeId has a negative byte size: $size")
      )
  }

  def scalarMemoryWidth(
      typeId: Int
  ): Either[Fault, Int where Between[1, 8]] = {
    val size = typeSize(typeId)
    size.refine[Int where Between[1, 8]]
      .leftMap(_ =>
        Fault.InvalidTask(
          s"SPIR-V scalar type %$typeId has invalid byte width $size"
        )
      )
  }

  def storageTypeSize(typeId: Int, layout: Option[MatrixLayout]): Int =
    (runtimeType(typeId), layout) match {
      case (Some(RuntimeType.MatrixType(column, columns)), Some(value)) =>
        val rows = runtimeType(column) match {
          case Some(RuntimeType.VectorType(_, count)) => count
          case _                                      => throw IllegalArgumentException("invalid matrix column type")
        }
        if (value.stride <= 0) throw IllegalArgumentException("invalid matrix stride")
        Math.multiplyExact(if (value.rowMajor) rows else columns, value.stride)
      case (Some(RuntimeType.VectorType(component, count)), Some(value)) if value.rowMajor =>
        Math.addExact(Math.multiplyExact(count - 1, value.stride), typeSize(component))
      case (Some(RuntimeType.Array(element, count)), Some(_)) =>
        Math.toIntExact(Math.multiplyExact(
          ir.arrayStrides.getOrElse(typeId, storageTypeSize(element, layout)).toLong,
          count
        ))
      case _ => typeSize(typeId)
    }

  private def typeSizeUncached(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)              => 1
    case Some(RuntimeType.Integer(width, _)) => width.raw / 8
    case Some(RuntimeType.Floating(width))   => width.raw / 8
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
      Math.multiplyExact(typeSize(component), if (n == 3 && ir.vulkanEntries.isEmpty) 4 else n)
    case Some(RuntimeType.MatrixType(column, count)) => Math.multiplyExact(typeSize(column), count)
    case Some(RuntimeType.Array(element, n))         =>
      Math.toIntExact(Math.multiplyExact(ir.arrayStrides.getOrElse(typeId, typeSize(element)).toLong, n))
    case Some(RuntimeType.Structure(members)) => structSize(typeId, members)
    case Some(RuntimeType.Pointer(_, _))      => 8
    case Some(RuntimeType.Void)               => 0
    case Some(RuntimeType.Function(_, _))     => 0
    case None                                 => throw IllegalArgumentException(s"SPIR-V type %$typeId is undefined")
  }

  // Struct layout carries offset/alignment state and uses the packed-layout fast exit.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def structSize(structureType: Int, members: Vec[Int]): Int = {
    if (ir.packedStructures.contains(structureType))
      return members.foldLeft(0)((size, member) => Math.addExact(size, typeSize(member)))
    var offset = 0
    var align  = 1
    var index  = 0
    while (index < members.size) {
      val memberAlign = typeAlignment(members(index))
      offset = ir.memberOffsets.getOrElse(structureType -> index, alignUp(offset, memberAlign))
      offset = Math.addExact(
        offset,
        storageTypeSize(members(index), memberMatrixLayouts.get(structureType -> index))
      )
      align = math.max(align, memberAlign)
      index += 1
    }
    alignUp(offset, align)
  }

  // Struct member layout carries offset/alignment state through one ordered scan.
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  def structMemberOffset(
      structureType: Int,
      members: Vec[Int],
      selected: Int
  ): Int =
    if (ir.packedStructures.contains(structureType))
      members.take(selected).foldLeft(0)((size, member) => Math.addExact(size, typeSize(member)))
    else ir.memberOffsets.get(structureType -> selected).getOrElse {
      var offset = 0
      var index  = 0
      while (index < selected) {
        offset = alignUp(offset, typeAlignment(members(index)))
        offset = Math.addExact(
          offset,
          storageTypeSize(members(index), memberMatrixLayouts.get(structureType -> index))
        )
        index += 1
      }
      alignUp(offset, typeAlignment(members(selected)))
    }

  def typeAlignment(typeId: Int): Int =
    typeAlignmentCache.getOrElseUpdate(typeId, typeAlignmentUncached(typeId))

  private def typeAlignmentUncached(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Structure(_)) if ir.packedStructures.contains(typeId) => 1
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(_)).maxOption.getOrElse(1)
    case Some(RuntimeType.MatrixType(column, _))        => typeAlignment(column)
    case Some(RuntimeType.Array(element, _))            => typeAlignment(element)
    case Some(RuntimeType.VectorType(component, count)) =>
      Math.multiplyExact(typeAlignment(component), if (count == 3) 4 else count)
    case Some(RuntimeType.Pointer(_, _)) => 8
    case _                               => math.max(1, typeSize(typeId))
  }

  def scalarWidth(typeId: Int): Int where Between[1, 64] = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => 8
    case Some(RuntimeType.Integer(width, _))        => width
    case Some(RuntimeType.Floating(width))          => width.bitWidth
    case Some(RuntimeType.Event)                    => 64
    case Some(RuntimeType.VectorType(component, _)) => scalarWidth(component)
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not scalar or vector: $value")
  }

  def pointerPointee(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, pointee)) => pointee
    case value => throw IllegalArgumentException(s"SPIR-V type %$typeId is not a pointer: $value")
  }

  def scalarLeafCount(typeId: Int): Either[Fault, Int] =
    scalarLeafCountCache.getOrElseUpdate(typeId, VMIR.scalarLeafCount(ir.types, typeId))

  def pointerLeafOffsets(
      typeId: Int,
      baseOffset: Long
  ): Either[Fault, Vec[(Long, Int)]] = runtimeType(typeId) match {
    case Some(RuntimeType.Pointer(_, _))      => Right(Vec(baseOffset -> typeId))
    case Some(RuntimeType.Structure(members)) =>
      members.zipWithIndex.foldLeft[Either[Fault, Vec[(Long, Int)]]](Right(Vec.empty)) {
        case (result, (member, index)) =>
          result.flatMap(existing =>
            pointerLeafOffsets(member, baseOffset + structMemberOffset(typeId, members, index)).map(
              existing ++ _
            )
          )
      }
    case Some(RuntimeType.Array(element, count)) =>
      if (count < 0L || count > Int.MaxValue.toLong)
        Left(Fault.InvalidTask(
          s"SPIR-V aggregate pointer array type %$typeId has invalid size $count"
        ))
      else
        (0 until count.toInt).foldLeft[Either[Fault, Vec[(Long, Int)]]](Right(Vec.empty)) {
          case (result, index) =>
            result.flatMap { existing =>
              val stride = ir.arrayStrides.getOrElse(typeId, typeSize(element))
              pointerLeafOffsets(element, baseOffset + index.toLong * stride).map(existing ++ _)
            }
        }
    case Some(_) => Right(Vec.empty)
    case None    =>
      Left(Fault.InvalidTask(s"SPIR-V aggregate member type %$typeId is undefined"))
  }

  def alignUp(value: Int, alignment: Int): Int =
    Math.multiplyExact((value + alignment - 1) / alignment, alignment)
}
