package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*

/** Consumers of SPIR-V semantics by operand shape: a family that builds ops to execute, closures to fold constants, or
  * terms to describe. Each shape receives the operand ids and the kernel, so one definition per instruction serves
  * every consumer. The non-inline queries resolve static facts that a semantics specialises on.
  */
trait SPIRVSinks[V, R] {

  /** The component width of a numeric scalar, vector or matrix type, and 1 for a Boolean one. */
  def width(typeId: Int): Int in 1 ~ 64

  /** The component width of a floating-point scalar, vector or matrix type. */
  def floatingWidth(typeId: Int): Int in (16, 32, 64)

  /** The encoding of the components of an 8-bit floating-point scalar or vector type, where it is one. */
  def minifloat(typeId: Int): Option[Minifloat] = None

  def typeOf(value: Int): Int

  /** The lane count of a scalar or vector type. */
  def lanes(typeId: Int): Int

  /** Member `index` of a structure type, or the pointee of a pointer type. */
  def memberType(typeId: Int, index: Int): Int

  /** The rounding decorating `result`, or `default`. */
  def rounding(result: Int, default: IEEE754.Rounding): IEEE754.Rounding

  /** Whether a float-to-integer conversion into `result` saturates. */
  def saturates(result: Int): Boolean

  /** Whether the components of a numeric type are floating-point. */
  def floating(typeId: Int): Boolean = false

  /** Whether the components of an integer type are signed. */
  def signed(typeId: Int): Boolean = false

  inline def unary(ids: OperandIds)(inline kernel: V => V): R
  inline def binary(ids: OperandIds)(inline kernel: (V, V) => V): R
  inline def ternary(ids: OperandIds)(inline kernel: (V, V, V) => V): R
  inline def quaternary(ids: OperandIds)(inline kernel: (V, V, V, V) => V): R

  /** One operand repeated across the lanes of the result type. */
  inline def broadcast(ids: OperandIds)(inline kernel: V => V): R

  /** The lanes of the result type from a scalar operand, the kernel's second operand being the lane index. */
  inline def indexed(ids: OperandIds)(inline kernel: (V, V) => V): R

  /** Two results per lane, as a structure of the first results then the second. */
  inline def structure(ids: OperandIds)(inline first: V => V, inline second: V => V): R
  inline def binaryStructure(ids: OperandIds)(inline first: (V, V) => V, inline second: (V, V) => V): R

  /** Two results per lane: the first are the result, and the second are stored through the last operand, a pointer. */
  inline def store(ids: OperandIds)(inline first: V => V, inline second: V => V): R
  inline def binaryStore(ids: OperandIds)(inline first: (V, V) => V, inline second: (V, V) => V): R

  /** A scalar reduction: `step` folds each lane into an accumulator from `initial`, which `finish` maps to the result.
    */
  inline def fold(ids: OperandIds)(inline initial: V, inline step: (V, V) => V, inline finish: V => V): R
  inline def binaryFold(ids: OperandIds)(inline initial: V, inline step: (V, V, V) => V, inline finish: V => V): R

  /** As [[binaryFold]], finished with a third, scalar operand. */
  inline def accumulatedFold(ids: OperandIds)(
      inline initial: V,
      inline step: (V, V, V) => V,
      inline finish: (V, V) => V
  ): R

  /** A matrix or matrix-vector product, each result lane folding the step from zero over a row and a column. */
  inline def product(ids: OperandIds)(inline step: (V, V, V) => V): R

  /** A cooperative matrix multiply-accumulate of an M by K and a K by N matrix into an M by N one, each held whole in
    * row-major lanes: result lane (i, j) is `finish` of the fold of `step` from `initial` over left lane (i, k) and
    * right lane (k, j) for each k, and lane (i, j) of the third operand.
    */
  inline def matrixAccumulate(ids: OperandIds)(
      inline initial: V,
      inline step: (V, V, V) => V,
      inline finish: (V, V) => V
  ): R

  /** An outer product of two vectors, column by column. */
  inline def outerProduct(ids: OperandIds)(inline kernel: (V, V) => V): R

  /** A cross product of three- or four-lane vectors: result lane `i` is the kernel of the lanes `i + 1` of the first
    * operand and `i + 2` of the second, then `i + 2` of the first and `i + 1` of the second, modulo three. A fourth
    * lane is 0.
    */
  inline def cross(ids: OperandIds)(inline kernel: (V, V, V, V) => V): R

  /** A fold of `step` from `initial` over the lanes of operand `dotted` and the second operand, then each result lane
    * from the fold, the lanes of the first two operands and the first lane of a third operand, or 0 without one.
    */
  inline def projection(ids: OperandIds, dotted: Int)(
      inline initial: V,
      inline step: (V, V, V) => V,
      inline element: (V, V, V, V) => V
  ): R

  /** A normalisation of the first operand: `scale` folds its lanes from 0, `norm` folds them from 0 with the scale, and
    * each result lane is `element` of its operand lane, the scale and the norm.
    */
  inline def normalised(ids: OperandIds)(
      inline scale: (V, V) => V,
      inline norm: (V, V, V) => V,
      inline element: (V, V, V) => V
  ): R

  /** Bit-field extraction over base, offset and count, which may fault where `inRange` of the offset and count is 0
    * rather than extract.
    */
  inline def bitFieldExtract(ids: OperandIds)(inline inRange: (V, V) => V, inline kernel: (V, V, V) => V): R

  /** Bit-field insertion over base, inserted value, offset and count, faulting as [[bitFieldExtract]] may. */
  inline def bitFieldInsert(ids: OperandIds)(inline inRange: (V, V) => V, inline kernel: (V, V, V, V) => V): R

  /** A float-to-unsigned conversion, which may fault on a lane `representable` maps to 0 rather than clamp it. */
  inline def unsignedConversion(ids: OperandIds)(inline representable: V => V, inline kernel: V => V): R

  /** An atomic read-modify-write over pointer, scope, semantics and an operand; the kernel maps the previous value and
    * the operand to the stored value. Integer operations accept a pointer into any storage class.
    */
  inline def atomic(ids: OperandIds, anyStorage: Boolean)(inline kernel: (V, V) => V): R

  /** As [[atomic]] over pointer, scope and semantics alone. */
  inline def atomicUpdate(ids: OperandIds)(inline kernel: V => V): R

  /** A compare-exchange over pointer, scope, equal and unequal semantics, value and comparator; the kernel maps the
    * previous value, the value and the comparator to the stored value.
    */
  inline def compareExchange(ids: OperandIds)(inline kernel: (V, V, V) => V): R

  /** A comparison of the addresses of two pointers. */
  inline def addresses(ids: OperandIds)(inline kernel: (V, V) => V): R

  /** A function of the addresses of two pointers and the stride of their element type. */
  inline def addressDifference(ids: OperandIds)(inline kernel: (V, V, V) => V): R

  /** A load of halves at offset and pointer, `aligned` padding three-lane vectors to four, each converted. */
  inline def halfLoad(ids: OperandIds, aligned: Boolean)(inline convert: V => V): R

  /** A store of the lanes of data at offset and pointer as converted halves. */
  inline def halfStore(ids: OperandIds, aligned: Boolean)(inline convert: V => V): R
}
