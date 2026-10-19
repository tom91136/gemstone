package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*

/** Consumers of NVIDIA semantics, one method per operand shape: each takes an instruction's operands and the kernels
  * over lane values of representation `V`, and runs them on every active lane or renders them under the operands'
  * names. Semantics take the family as an inline parameter bound to a concrete one, so each call expands into that
  * family's code. Predicates and condition flags read as 1 or 0, and a predicate or flag destination takes any non-zero
  * result as true.
  */
private[isa] trait NvidiaSinks[V, R] extends Any {
  inline def unary(destination: Register, source: Value)(inline kernel: V => V): R
  inline def binary(destination: Register, left: Value, right: Value)(inline kernel: (V, V) => V): R
  inline def ternary(destination: Register, first: Value, second: Value, third: Value)(
      inline kernel: (V, V, V) => V
  ): R
  inline def quaternary(destination: Register, first: Value, second: Value, third: Value, fourth: Value)(
      inline kernel: (V, V, V, V) => V
  ): R

  /** The operands and the lane's carry flag in; with a carry kernel, its result becomes the next carry and sees the
    * result.
    */
  inline def carryIn(destination: Register, left: Value, right: Value)(inline result: (V, V, V) => V): R
  inline def carryIn(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (V, V, V, V) => V
  ): R
  inline def carryInOut(destination: Register, left: Value, right: Value)(
      inline result: (V, V, V) => V,
      inline carry: (V, V, V, V) => V
  ): R
  inline def carryInOut(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (V, V, V, V) => V,
      inline carry: (V, V, V, V, V) => V
  ): R

  /** IADD3's carry chain: `produce` sets the lane's carry count, `consume` reads it as the fourth operand and leaves it
    * undefined.
    */
  inline def produceCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (V, V, V) => V,
      inline carries: (V, V, V) => V
  ): R
  inline def consumeCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (V, V, V, V) => V
  ): R

  inline def test(destination: Predicate, source: Value)(inline kernel: V => V): R
  inline def compare(destination: Predicate, left: Value, right: Value)(inline kernel: (V, V) => V): R

  /** A comparison of two values combined with a third predicate, which reads as 1 or 0. */
  inline def compare(destination: Predicate, left: Value, right: Value, other: Predicate)(
      inline kernel: (V, V, V) => V
  ): R
  inline def predicate(destination: Predicate, source: Predicate)(inline kernel: V => V): R
  inline def predicate(destination: Predicate, left: Predicate, right: Predicate)(inline kernel: (V, V) => V): R

  /** The guard predicate first, then both candidate values. */
  inline def select(destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value)(
      inline kernel: (V, V, V) => V
  ): R

  /** Each element of a vector, every source read before any destination is written. */
  inline def elements(destinations: Vec[Register], sources: Vec[Value])(inline kernel: V => V): R

  /** One operand into two halves, both computed before either is written; an absent half is not written. */
  inline def split(low: Option[Register], high: Option[Register], source: Value)(
      inline lowHalf: V => V,
      inline highHalf: V => V
  ): R

  /** The value of each warp's lowest active lane, written to every live lane of the warp. */
  inline def broadcast(destination: Register, source: Value)(inline kernel: V => V): R

  inline def writeFlag(flag: VMConditionCodes.Flag, source: Value)(inline kernel: V => V): R

  /** A condition flag, which must be defined. */
  inline def readFlag(destination: Register, flag: VMConditionCodes.Flag)(inline kernel: V => V): R
  inline def testFlag(destination: Predicate, flag: VMConditionCodes.Flag)(inline kernel: V => V): R

  /** A warp shuffle over the lane's index in its warp, the selector, the bounds operand and the number of lanes from
    * the warp's first: the lane in the warp to read, whether the exchange is valid, and the value written from the
    * read.
    */
  inline def shuffle(
      destination: Register,
      validity: Option[Predicate],
      operand: Value,
      selector: Value,
      bounds: Value
  )(
      inline source: (V, V, V, V) => V,
      inline valid: (V, V, V, V) => V,
      inline value: V => V
  ): R

  /** The predicates of the participating lanes as bits, and the mask of those lanes. */
  inline def ballot(destination: Register, voter: Predicate, memberMask: Value)(inline kernel: (V, V) => V): R
  inline def vote(destination: Predicate, voter: Predicate, memberMask: Value)(inline kernel: (V, V) => V): R

  /** From the mask of participating lanes, the elected lane; then from a lane's index and that lane, whether the lane
    * is the one elected. The elected lane's index goes to `elected` when present.
    */
  inline def elect(elected: Option[Register], leader: Predicate, memberMask: Value)(
      inline choose: V => V,
      inline isLeader: (V, V) => V
  ): R

  /** A fold of the participating lanes' binary32 operands to their minimum or maximum, each first passing through
    * `element`.
    */
  inline def reduceFloat(
      destination: Register,
      source: Value,
      memberMask: Value,
      maximum: Boolean,
      propagateNaN: Boolean
  )(
      inline element: V => V,
      inline combine: (V, V) => V
  ): R

  /** `match.sync`: the participating lanes whose `width`-bit `source` equals this lane's, or with `all` every
    * participating lane where all match and none otherwise; `holds` turns that mask into `predicate`.
    */
  inline def matching(
      found: Option[Register],
      predicate: Option[Predicate],
      source: Value,
      memberMask: Value,
      width: Int in 1 ~ 64,
      all: Boolean
  )(inline holds: V => V): R

  /** A fold over the participating lanes in lane order, each operand first passing through `element`; `combine` is
    * `operation` on `width`-bit values.
    */
  inline def reduce(
      destination: Register,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline element: V => V,
      inline combine: (V, V) => V
  ): R

  /** A coordinate from a cluster cancellation response, whose high word must report success. */
  inline def cancelledCta(destination: Register, low: Value, high: Value)(inline kernel: (V, V) => V): R

  /** Memory accesses: each element's address from the base address and the element's index, and the value loaded or
    * stored.
    */
  inline def load(destination: Register, address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R
  inline def loadVector(destinations: Vec[Register], address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R
  inline def loadParameter(destinations: Vec[Register], parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R
  inline def store(address: Value, source: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R
  inline def storeVector(address: Value, sources: Vec[Value], width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R
  inline def storeParameter(parameter: Parameter, offset: Int >= 0, source: Value, width: ScalarBitWidth)(
      inline element: (V, V) => V,
      inline value: V => V
  ): R

  /** An atomic read-modify-write of `width` bits, from the old value and the operand to the value stored; the old value
    * goes to `fetched` when present.
    */
  inline def atomic(
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ): Sink.Binary[V, R]

  /** `mma` on every participating warp, whose 32 lanes must all be active: each element of D is `element` of its
    * operands, the element of C, A's row and B's column with any sparse metadata and scale factors.
    */
  inline def multiplyMatrices(operation: Matrix.Operation.Multiply)(inline element: MatrixOperands[V] => V): R

  /** `wgmma.mma_async` on every participating warpgroup, whose 128 lanes must all be active: each element of D is
    * `element` of its operands, as [[multiplyMatrices]] presents them, and the predicate that keeps D.
    */
  inline def multiplyWarpgroup(operation: Matrix.Operation.WarpgroupMultiply)(
      inline element: WarpgroupOperands[V] => V
  ): R

  /** A tcgen05.mma, each element of D `element` of its operands under the product its instruction descriptor gives. */
  inline def multiplyTensorMemory(operation: Tcgen05.Multiply)(
      inline element: (TensorMemory.Product, TensorMemoryOperands[V]) => V
  ): R

  /** `atom.cas`: from the old value, the comparator and the value, the value stored. */
  inline def compareExchange(
      destination: Register,
      address: Value,
      comparator: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  )(inline kernel: (V, V, V) => V): R
}
