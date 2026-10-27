package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.semantics.prelude.*

/** The tensor copies of tensor maps, which rendered machine definitions prepare as a transfer and then move the runs of
  * box elements between global memory and the box it holds.
  */
private[isa] trait Tensors {

  /** The transfer of a copy through the tensor map `map`, whose `values` are the first `coordinates` of them its
    * coordinates and the rest its offsets.
    */
  def transfer(
      map: Array[Byte],
      im2col: Boolean,
      gather: Boolean,
      operation: TensorMap.Operation,
      coordinates: Int,
      values: UInt[64]*
  ): Array[Byte]
  def fact(transfer: Array[Byte], fact: TensorLayout.Fact): UInt[64]

  /** The `bytes`-byte little-endian word at byte `offset` of the tensor map `map`, from which its fields are built. */
  def word(map: Array[Byte], offset: Int, bytes: Int): UInt[64]

  /** A tcgen05.mma product with its operands read. */
  type Gathered

  /** The product the instruction descriptor `descriptor` gives a tcgen05.mma of `kind` writing D at `d` from A at `a`
    * and B at `b`, adding D where `enable` holds, with the registers of its metadata, scale factors, zero-column mask
    * and disabled lanes in `optional`, in that order, where it has them.
    */
  def product(
      kind: VMIR.Tcgen05.Kind,
      pair: Boolean,
      ws: Boolean,
      shiftA: Boolean,
      shift: Int,
      descriptor: UInt[64],
      d: UInt[64],
      a: UInt[64],
      b: UInt[64],
      enable: UInt[64],
      optional: UInt[64]*
  ): Gathered
  def size(product: Gathered): UInt[64]

  /** Element `index` of the row of A, the column of B or the metadata that places packed A, and block `index` of the
    * scale factors of A and B, for element `element` of D in `product`, as its type `T`; C is the element of D itself.
    */
  def a[T](product: Gathered, element: UInt[64], index: Int): T
  def b[T](product: Gathered, element: UInt[64], index: Int): T
  def c[T](product: Gathered, element: UInt[64]): T
  def selection(product: Gathered, element: UInt[64], index: Int): UInt[2]
  def scaleA[T](product: Gathered, element: UInt[64], index: Int): T
  def scaleB[T](product: Gathered, element: UInt[64], index: Int): T
  def store(product: Gathered, index: UInt[64], value: UInt[64]): Unit
  def indices(count: UInt[64]): Iterable[UInt[64]]

  /** The `count` contiguous box elements at `offset` in the box of `transfer`, read from global memory at `address`
    * into the box, or set to the bits `fill` where they lie outside the tensor.
    */
  def loadRun(
      transfer: Array[Byte],
      offset: UInt[64],
      address: UInt[64],
      count: UInt[64],
      inside: Bool,
      fill: UInt[64]
  ): Unit

  /** The `count` box elements at `offset` written to global memory at `address`, unless they lie outside the tensor. */
  def storeRun(transfer: Array[Byte], offset: UInt[64], address: UInt[64], count: UInt[64], inside: Bool): Unit

  /** The `count` box elements at `offset` reduced into global memory at `address`, unless they lie outside the tensor.
    */
  def reduceRun(
      transfer: Array[Byte],
      offset: UInt[64],
      address: UInt[64],
      count: UInt[64],
      inside: Bool,
      operator: TensorMap.Reduction
  ): Unit
}
