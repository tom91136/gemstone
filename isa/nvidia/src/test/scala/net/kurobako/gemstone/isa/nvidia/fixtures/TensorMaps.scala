package net.kurobako.gemstone.isa.nvidia.fixtures

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.nvidia.*

/** Tensor maps encoded as the CUDA driver facade encodes them, for kernels that take one as a parameter. */
object TensorMaps {

  /** A tiled map over a tensor whose dimension 0 is contiguous; `strides` are the byte strides of dimensions 1 up. */
  def tiled(
      address: Long,
      element: TensorMap.Element,
      dimensions: Vec[Long],
      strides: Vec[Long],
      box: Vec[Int],
      traversal: Vec[Int] = Vec.empty,
      swizzle: Int = 0,
      nanFill: Boolean = false
  ): Array[Byte] = {
    val bytes                                                        = Array.ofDim[Byte](TensorMap.Bytes)
    def set(field: TensorMap.Field, ordinal: Int, value: Long): Unit = replace(bytes, field, ordinal, value)
    set(TensorMap.Field.GlobalAddress, 0, address)
    set(TensorMap.Field.Rank, 0, dimensions.size - 1L)
    set(TensorMap.Field.ElementType, 0, element.instruction.toLong)
    set(TensorMap.Field.Swizzle, 0, swizzle.toLong)
    set(TensorMap.Field.FillMode, 0, if (nanFill) 1L else 0L)
    dimensions.zipWithIndex.foreach((size, index) => set(TensorMap.Field.GlobalDimension, index, size))
    strides.zipWithIndex.foreach((stride, index) => set(TensorMap.Field.GlobalStride, index, stride))
    box.zipWithIndex.foreach((size, index) => set(TensorMap.Field.BoxDimension, index, size.toLong))
    box.indices.foreach(index =>
      set(TensorMap.Field.ElementStride, index, traversal.lift(index).getOrElse(1).toLong)
    )
    bytes
  }

  def replace(bytes: Array[Byte], field: TensorMap.Field, ordinal: Int, value: Long): Unit = {
    assert(TensorMap.accepts(field, value), s"$field $value")
    TensorMap.update(bytes, field, ordinal, value)
  }

  /** An im2col map: corners are given for W, then H, then D, W alone for the wide modes. */
  def im2col(
      address: Long,
      element: TensorMap.Element,
      dimensions: Vec[Long],
      strides: Vec[Long],
      lower: Vec[Int],
      upper: Vec[Int],
      channels: Int,
      pixels: Int,
      traversal: Vec[Int] = Vec.empty,
      mode: TensorMap.Mode = TensorMap.Mode.Im2col
  ): Array[Byte] = {
    val bytes = tiled(address, element, dimensions, strides, Vec.fill(dimensions.size)(1), traversal)
    LittleEndian.write(mode.ordinal.toLong, bytes, 0x40, 4)
    LittleEndian.write(channels.toLong, bytes, 0x48, 4)
    LittleEndian.write(pixels.toLong, bytes, 0x4c, 4)
    lower.zipWithIndex.foreach((corner, index) => LittleEndian.write(corner.toLong, bytes, 0x50 + index * 4, 4))
    upper.zipWithIndex.foreach((corner, index) => LittleEndian.write(corner.toLong, bytes, 0x5c + index * 4, 4))
    bytes
  }

  /** The logical box offset whose bytes a 128-byte-period swizzle places at shared offset `offset` from an aligned
    * start; each swizzle is its own inverse.
    */
  def unswizzle(swizzle: Int, offset: Long): Long =
    if (swizzle == 0) offset else MatrixDescriptor.swizzled(offset, 16 << swizzle)
}
