package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import scala.collection.mutable

@scala.annotation.publicInBinary
private[isa] object Probe {

  /** A matrix element's operands: C first, then the elements of A's row and B's column in turn. */
  class Operands(at: Int => Long) extends MatrixOperands[Long] {
    private val slots                                    = mutable.HashMap.empty[Int, Long]
    def a(index: Int): Long                              = at(1 + 2 * index)
    def b(index: Int): Long                              = at(2 + 2 * index)
    def c: Long                                          = at(0)
    def selection(index: Int): Long                      = 0L
    def scaleA(block: Int): Long                         = 0L
    def scaleB(block: Int): Long                         = 0L
    def bind(name: String, value: Long): Long            = value
    def hold(slot: Int, name: String, value: Long): Long = {
      slots(slot) = value
      value
    }
    def held(slot: Int): Long = slots(slot)
  }

  /** A warpgroup element's operands as [[Operands]] takes them, and D kept. */
  class Scaled(at: Int => Long) extends Operands(at) with WarpgroupOperands[Long] {
    def scale: Long = 1L
  }

  class TensorOperands(at: Int => Long) extends Operands(at) with TensorMemoryOperands[Long] {
    def enabled: Long = 1L
  }
}

/** Sinks that run each kernel once on `inputs`, ignoring the instruction's operands, and return every output in order.
  * Inputs follow the kernel's parameters: the operands then the incoming flag, the shuffle's lane, selector, bounds and
  * lane count, the election's members and lane, every operand a reduction folds, and an access's base, index and raw
  * value.
  */
private class Probe(inputs: Long*) extends NvidiaSinks[Long, Vector[Long]] {
  def at(index: Int): Long = inputs(index)

  inline def unary(destination: Register, source: Value)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def binary(destination: Register, left: Value, right: Value)(inline kernel: (Long, Long) => Long)
      : Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def ternary(destination: Register, first: Value, second: Value, third: Value)(
      inline kernel: (Long, Long, Long) => Long
  ): Vector[Long] = Vector(kernel(at(0), at(1), at(2)))
  inline def quaternary(destination: Register, first: Value, second: Value, third: Value, fourth: Value)(
      inline kernel: (Long, Long, Long, Long) => Long
  ): Vector[Long] = Vector(kernel(at(0), at(1), at(2), at(3)))
  inline def carryIn(destination: Register, left: Value, right: Value)(inline result: (Long, Long, Long) => Long)
      : Vector[Long] = Vector(result(at(0), at(1), at(2)))
  inline def carryIn(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long
  ): Vector[Long] = Vector(result(at(0), at(1), at(2), at(3)))
  inline def carryInOut(destination: Register, left: Value, right: Value)(
      inline result: (Long, Long, Long) => Long,
      inline carry: (Long, Long, Long, Long) => Long
  ): Vector[Long] = {
    val value = result(at(0), at(1), at(2))
    Vector(value, carry(at(0), at(1), at(2), value))
  }
  inline def carryInOut(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long,
      inline carry: (Long, Long, Long, Long, Long) => Long
  ): Vector[Long] = {
    val value = result(at(0), at(1), at(2), at(3))
    Vector(value, carry(at(0), at(1), at(2), at(3), value))
  }
  inline def produceCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long) => Long,
      inline carries: (Long, Long, Long) => Long
  ): Vector[Long] = Vector(result(at(0), at(1), at(2)), carries(at(0), at(1), at(2)))
  inline def consumeCarries(destination: Register, first: Value, second: Value, third: Value)(
      inline result: (Long, Long, Long, Long) => Long
  ): Vector[Long] = Vector(result(at(0), at(1), at(2), at(3)))
  inline def test(destination: Predicate, source: Value)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def compare(destination: Predicate, left: Value, right: Value)(inline kernel: (Long, Long) => Long)
      : Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def compare(destination: Predicate, left: Value, right: Value, other: Predicate)(
      inline kernel: (Long, Long, Long) => Long
  ): Vector[Long] = Vector(kernel(at(0), at(1), at(2)))
  inline def predicate(destination: Predicate, source: Predicate)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def predicate(destination: Predicate, left: Predicate, right: Predicate)(
      inline kernel: (Long, Long) => Long
  ): Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def select(destination: Register, guard: Predicate, whenTrue: Value, whenFalse: Value)(
      inline kernel: (Long, Long, Long) => Long
  ): Vector[Long] = Vector(kernel(at(0), at(1), at(2)))
  inline def elements(destinations: Vec[Register], sources: Vec[Value])(inline kernel: Long => Long): Vector[Long] =
    inputs.toVector.map(kernel(_))
  inline def split(low: Option[Register], high: Option[Register], source: Value)(
      inline lowHalf: Long => Long,
      inline highHalf: Long => Long
  ): Vector[Long] = Vector(lowHalf(at(0)), highHalf(at(0)))
  inline def broadcast(destination: Register, source: Value)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def writeFlag(flag: VMConditionCodes.Flag, source: Value)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def readFlag(destination: Register, flag: VMConditionCodes.Flag)(inline kernel: Long => Long): Vector[Long] =
    Vector(kernel(at(0)))
  inline def testFlag(destination: Predicate, flag: VMConditionCodes.Flag)(inline kernel: Long => Long)
      : Vector[Long] = Vector(kernel(at(0)))
  inline def shuffle(
      destination: Register,
      validity: Option[Predicate],
      operand: Value,
      selector: Value,
      bounds: Value
  )(
      inline source: (Long, Long, Long, Long) => Long,
      inline valid: (Long, Long, Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(source(at(0), at(1), at(2), at(3)), valid(at(0), at(1), at(2), at(3)))
  inline def ballot(destination: Register, voter: Predicate, memberMask: Value)(inline kernel: (Long, Long) => Long)
      : Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def vote(destination: Predicate, voter: Predicate, memberMask: Value)(inline kernel: (Long, Long) => Long)
      : Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def elect(elected: Option[Register], leader: Predicate, memberMask: Value)(
      inline choose: Long => Long,
      inline isLeader: (Long, Long) => Long
  ): Vector[Long] = {
    val chosen = choose(at(0))
    Vector(chosen, isLeader(at(1), chosen))
  }
  inline def reduceFloat(
      destination: Register,
      source: Value,
      memberMask: Value,
      maximum: Boolean,
      propagateNaN: Boolean
  )(
      inline element: Long => Long,
      inline combine: (Long, Long) => Long
  ): Vector[Long] =
    Vector(inputs.tail.foldLeft(element(at(0)))((accumulated, value) => combine(accumulated, element(value))))
  inline def matching(
      found: Option[Register],
      predicate: Option[Predicate],
      source: Value,
      memberMask: Value,
      width: Int in 1 ~ 64,
      all: Boolean
  )(inline holds: Long => Long): Vector[Long] = Vector(holds(at(0)))
  inline def reduce(
      destination: Register,
      source: Value,
      memberMask: Value,
      operation: IntegerOperation,
      width: Int in 1 ~ 64
  )(
      inline element: Long => Long,
      inline combine: (Long, Long) => Long
  ): Vector[Long] =
    Vector(inputs.tail.foldLeft(element(at(0)))((accumulated, value) => combine(accumulated, element(value))))
  inline def cancelledCta(destination: Register, low: Value, high: Value)(inline kernel: (Long, Long) => Long)
      : Vector[Long] = Vector(kernel(at(0), at(1)))
  inline def load(destination: Register, address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def loadVector(destinations: Vec[Register], address: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def loadParameter(destinations: Vec[Register], parameter: Parameter, offset: Int >= 0, width: ScalarBitWidth)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def store(address: Value, source: Value, width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def storeVector(address: Value, sources: Vec[Value], width: ScalarBitWidth, access: MemoryAccess)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def storeParameter(parameter: Parameter, offset: Int >= 0, source: Value, width: ScalarBitWidth)(
      inline element: (Long, Long) => Long,
      inline value: Long => Long
  ): Vector[Long] = Vector(element(at(0), at(1)), value(at(2)))
  inline def compareExchange(
      destination: Register,
      address: Value,
      comparator: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  )(inline kernel: (Long, Long, Long) => Long): Vector[Long] = Vector(kernel(at(0), at(1), at(2)))
  inline def multiplyMatrices(operation: Matrix.Operation.Multiply)(
      inline element: MatrixOperands[Long] => Long
  ): Vector[Long] = Vector(element(Probe.Operands(at(_))))
  inline def multiplyWarpgroup(operation: Matrix.Operation.WarpgroupMultiply)(
      inline element: WarpgroupOperands[Long] => Long
  ): Vector[Long] = Vector(element(Probe.Scaled(at(_))))

  inline def multiplyTensorMemory(operation: Tcgen05.Multiply)(
      inline element: (TensorMemory.Product, TensorMemoryOperands[Long]) => Long
  ): Vector[Long] = operation.descriptor match {
    case Value.Immediate(bits) =>
      TensorMemory.product(operation, bits).toOption.fold(Vector.empty[Long])(product =>
        Vector(element(product, Probe.TensorOperands(at(_))))
      )
    case _ => Vector.empty
  }
  inline def atomic(
      fetched: Option[Register],
      address: Value,
      source: Value,
      width: ScalarBitWidth,
      space: MemorySpace
  ): Applied[Vector[Long]] = Applied(at(0), at(1))(Vector(_))
}
