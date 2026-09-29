package net.kurobako.gemstone.isa.spirv.v1_6

import VMIR.*
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

  private def typeSizeUncached(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Bool)                     => 1
    case Some(RuntimeType.Integer(width, _))        => width.raw / 8
    case Some(RuntimeType.Floating(width))          => width.raw / 8
    case Some(RuntimeType.Event)                    => 8
    case Some(RuntimeType.VectorType(component, n)) =>
      Math.multiplyExact(typeSize(component), if (n == 3) 4 else n)
    case Some(RuntimeType.Array(element, n)) =>
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
      offset = Math.addExact(offset, typeSize(members(index)))
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
    else ir.memberOffsets.getOrElse(
      structureType -> selected, {
        var offset = 0
        var index  = 0
        while (index < selected) {
          offset = alignUp(offset, typeAlignment(members(index)))
          offset = Math.addExact(offset, typeSize(members(index)))
          index += 1
        }
        alignUp(offset, typeAlignment(members(selected)))
      }
    )

  def typeAlignment(typeId: Int): Int =
    typeAlignmentCache.getOrElseUpdate(typeId, typeAlignmentUncached(typeId))

  private def typeAlignmentUncached(typeId: Int): Int = runtimeType(typeId) match {
    case Some(RuntimeType.Structure(_)) if ir.packedStructures.contains(typeId) => 1
    case Some(RuntimeType.Structure(members))           => members.map(typeAlignment(_)).maxOption.getOrElse(1)
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
