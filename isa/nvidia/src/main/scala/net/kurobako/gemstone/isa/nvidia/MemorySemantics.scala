package net.kurobako.gemstone.isa.nvidia

import VMIR.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*

/** NVIDIA memory semantics: the address of each element an instruction accesses and the value it loads or stores. The
  * sinks perform the accesses themselves.
  */
private[isa] object MemorySemantics {

  /** A load of `width` bits, extended to the register. */
  inline def load[V, R](inline algebra: Algebra[V], width: ScalarBitWidth, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      address: Value,
      access: MemoryAccess
  ): R =
    if (signed) sinks.load(destination, address, width, access)(
      (base, _) => base,
      value => algebra.signExtend(value, bits(width))
    )
    else
      sinks.load(destination, address, width, access)((base, _) => base, value => algebra.truncate(value, bits(width)))

  /** A load of consecutive `width`-bit elements, extended to their registers. */
  inline def loadVector[V, R](inline algebra: Algebra[V], width: ScalarBitWidth, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destinations: Vec[Register],
      address: Value,
      access: MemoryAccess
  ): R =
    if (signed) sinks.loadVector(destinations, address, width, access)(
      element(algebra, width, _, _),
      value => algebra.signExtend(value, bits(width))
    )
    else sinks.loadVector(destinations, address, width, access)(
      element(algebra, width, _, _),
      value => algebra.truncate(value, bits(width))
    )

  /** A load of consecutive `width`-bit elements of a lane's `.param` storage from `offset`, extended to their
    * registers.
    */
  inline def loadParameter[V, R](inline algebra: Algebra[V], width: ScalarBitWidth, signed: Boolean)(
      inline sinks: NvidiaSinks[V, R],
      destinations: Vec[Register],
      parameter: Parameter,
      offset: Int >= 0
  ): R =
    if (signed)
      sinks.loadParameter(destinations, parameter, offset, width)(
        element(algebra, width, _, _),
        value => algebra.signExtend(value, bits(width))
      )
    else
      sinks.loadParameter(destinations, parameter, offset, width)(
        element(algebra, width, _, _),
        value => algebra.truncate(value, bits(width))
      )

  /** A store of the low `width` bits to a lane's `.param` storage at `offset`. */
  inline def storeParameter[V, R](inline algebra: Algebra[V], width: ScalarBitWidth)(
      inline sinks: NvidiaSinks[V, R],
      parameter: Parameter,
      offset: Int >= 0,
      source: Value
  ): R = sinks.storeParameter(parameter, offset, source, width)(
    (base, _) => base,
    value => algebra.truncate(value, bits(width))
  )

  /** A store of the low `width` bits. */
  inline def store[V, R](
      inline algebra: Algebra[V],
      width: ScalarBitWidth
  )(inline sinks: NvidiaSinks[V, R], address: Value, source: Value, access: MemoryAccess): R =
    sinks.store(address, source, width, access)((base, _) => base, value => algebra.truncate(value, bits(width)))

  /** A store of the low `width` bits of each element, consecutively. */
  inline def storeVector[V, R](inline algebra: Algebra[V], width: ScalarBitWidth)(
      inline sinks: NvidiaSinks[V, R],
      address: Value,
      sources: Vec[Value],
      access: MemoryAccess
  ): R = sinks.storeVector(address, sources, width, access)(
    element(algebra, width, _, _),
    value => algebra.truncate(value, bits(width))
  )

  /** An atomic read-modify-write of the low `width` bits, applied when the write buffer commits. Floating-point
    * addition is NVIDIA's, as [[FloatArithmetic.atomicAdd]] computes it.
    */
  inline def atomic[V, R](inline algebra: Algebra[V], operation: AtomicOperation, width: ScalarBitWidth)(
      inline sinks: NvidiaSinks[V, R],
      fetched: Option[Register],
      address: Value,
      source: Value,
      space: MemorySpace
  ): R = (operation, width.raw) match {
    case (AtomicOperation.FloatAdd, 16) =>
      sinks.atomic(fetched, address, source, width, space)((old, operand) =>
        algebra.primitive(algebra.truncate(old, 16), algebra.truncate(operand, 16))((x: Float16, y: Float16) =>
          FloatArithmetic.atomicAdd(x, y)
        )
      )
    case (AtomicOperation.FloatAdd, 32) =>
      sinks.atomic(fetched, address, source, width, space)((old, operand) =>
        algebra.primitive(algebra.truncate(old, 32), algebra.truncate(operand, 32))((x: Float32, y: Float32) =>
          FloatArithmetic.atomicAdd(x, y)
        )
      )
    case (AtomicOperation.FloatAdd, _) =>
      sinks.atomic(fetched, address, source, width, space)((old, operand) =>
        algebra.primitive(old, operand)((x: Float64, y: Float64) => FloatArithmetic.atomicAdd(x, y))
      )
    case (AtomicOperation.PackedHalfAdd, _) =>
      sinks.atomic(fetched, address, source, width, space)((old, operand) =>
        algebra.primitive(algebra.truncate(old, 32), algebra.truncate(operand, 32))(
          FloatArithmetic.atomicAddPackedHalf(_, _)
        )
      )
    case _ => operation.staged(algebra, width)(sinks.atomic(fetched, address, source, width, space))
  }

  /** `atom.cas`: the value stored over the old value, which is `value` where the old value equals `comparator` in
    * `width` bits. The exchange applies when the write buffer commits.
    */
  inline def compareExchange[V, R](inline algebra: Algebra[V], width: ScalarBitWidth)(
      inline sinks: NvidiaSinks[V, R],
      destination: Register,
      address: Value,
      comparator: Value,
      source: Value,
      space: MemorySpace
  ): R = sinks.compareExchange(
    destination = destination,
    address = address,
    comparator = comparator,
    source = source,
    width = width,
    space = space
  )(AtomicOperation.exchanged(algebra)(bits(width), _, _, _))

  inline def element[V](inline algebra: Algebra[V], width: ScalarBitWidth, base: V, index: V): V =
    algebra.add(base, algebra.multiply(index, algebra.literal(width.toScalarByteWidth.toLong)))

  inline def bits(width: ScalarBitWidth): Int in 1 ~ 64 = width.widen[Int in 1 ~ 64]
}
