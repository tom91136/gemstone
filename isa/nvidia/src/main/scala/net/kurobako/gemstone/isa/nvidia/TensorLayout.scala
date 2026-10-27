package net.kurobako.gemstone.isa.nvidia

import TensorMapping.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** The tensor copy of a tensor map, as the primitives its semantics calls and the functions rendered semantics calls in
  * their place. A transfer holds what the descriptor says about a copy, as the facts that follow its tensor map, then
  * the runs of box elements the copy visits, then the box itself, which loads fill and stores read.
  */
private[isa] object TensorLayout {

  /** What a transfer records of its copy. A flag is 1 where it holds and 0 where it does not or the copy moves no data.
    */
  enum Fact derives CanEqual {

    /** The copy reduces elements that have no form of its operator. */
    case Unreducible

    /** The bytes of the box in shared memory. */
    case Bytes

    /** The runs of contiguous box elements. */
    case Runs

    /** The rank of the tensor map. */
    case Rank

    /** The elements of a packed unit. */
    case Values

    /** The interleave of the tensor map. */
    case Interleave

    /** The ordinal of the mode of the tensor map. */
    case Mode

    /** The ordinal of the element type of the tensor map. */
    case Element

    /** The bytes of a unit in global memory. */
    case Global

    /** The bytes of a unit in shared memory. */
    case Local

    /** The bits an element outside the tensor takes, or 0 where it takes zeros. */
    case Fill

    /** The swizzle of the tensor map. */
    case Swizzle

    /** The swizzle atomicity of the tensor map. */
    case Atomicity
  }

  /** What a transfer names in words. */
  enum Text derives CanEqual {
    case Mode, Element
  }

  trait Move {
    def apply(offset: Long, from: Int, count: Int): Unit
  }

  private val facts    = Fact.values.length
  private val runWords = 4

  private def flag(holds: Boolean): Long = if (holds) 1L else 0L

  /** 1 where a copy of `coordinates` coordinates has the shape its tensor map's rank asks for, and an im2col mode
    * exactly when the copy is im2col.
    */
  inline def matches[V](
      inline algebra: Algebra[V],
      rank: V,
      mode: V,
      coordinates: Int,
      gather: Boolean,
      im2col: Boolean
  ): V = {
    val shape =
      if (!gather) algebra.equal(rank, algebra.literal(coordinates.toLong))
      else if (coordinates == 5) algebra.equal(rank, algebra.literal(2L))
      else algebra.literal(0L)
    val tile = algebra.literal(TensorMap.Mode.Tile.ordinal.toLong)
    algebra.and(shape, if (im2col) algebra.notEqual(mode, tile) else algebra.equal(mode, tile))
  }

  /** 1 where the tensor map is not interleaved. */
  inline def plain[V](inline algebra: Algebra[V], interleave: V): V = algebra.equal(interleave, algebra.literal(0L))

  /** 1 where the elements of a tensor map are packed. */
  inline def packed[V](inline algebra: Algebra[V], values: V): V =
    algebra.lessThan(algebra.literal(1L), values, signed = false)

  /** 1 where the element code names `.b4x16_p64`, which a store cannot write. */
  inline def unstorable[V](inline algebra: Algebra[V], code: V): V =
    algebra.equal(code, algebra.literal(TensorMap.Element.B4x16P64.code.toLong))

  /** 1 where the inner coordinate of a tile is a multiple of 16 bytes, if the copy needs one. */
  inline def aligned[V](inline algebra: Algebra[V], mode: V, bytes: V, coordinate: V, gather: Boolean): V =
    if (gather) algebra.literal(1L)
    else
      algebra.or(
        algebra.notEqual(mode, algebra.literal(TensorMap.Mode.Tile.ordinal.toLong)),
        algebra.equal(
          algebra.and(algebra.multiply(coordinate, bytes), algebra.literal(15L)),
          algebra.literal(0L)
        )
      )

  /** 1 where the box splits into the units of packed elements that dimension 0 is counted in. The units are powers of
    * two, so a multiple of one is a clear low bit mask.
    */
  inline def splits[V](
      inline algebra: Algebra[V],
      values: V,
      mode: V,
      coordinate: V,
      dimension: V,
      traversal: V,
      box: V,
      channels: V
  ): V = {
    val mask               = algebra.subtract(values, algebra.literal(1L))
    def clear(value: V): V = algebra.equal(algebra.and(value, mask), algebra.literal(0L))
    algebra.select(
      algebra.equal(values, algebra.literal(1L)),
      algebra.literal(1L),
      algebra.and(
        algebra.and(
          algebra.and(clear(coordinate), clear(dimension)),
          algebra.equal(traversal, algebra.literal(1L))
        ),
        algebra.select(
          algebra.equal(mode, algebra.literal(TensorMap.Mode.Tile.ordinal.toLong)),
          clear(box),
          clear(channels)
        )
      )
    )
  }

  private def encode(recorded: Map[Fact, Long], runs: Array[Long], box: Int): Array[Byte] = {
    val bytes = Array.ofDim[Byte]((facts + runs.length) * 8 + box)
    Fact.values.foreach(fact => LittleEndian.write(recorded.getOrElse(fact, 0L), bytes, fact.ordinal * 8, 8))
    runs.indices.foreach(index => LittleEndian.write(runs(index), bytes, (facts + index) * 8, 8))
    bytes
  }

  /** The transfer of the copy that `coordinates` and `offsets` give for `map`, whose runs and box are empty for a copy
    * that fails a check or moves no data.
    */
  def transfer(
      map: Array[Byte],
      coordinates: Vec[Long],
      offsets: Vec[Long],
      copy: VMIR.Instruction.AsyncTensor
  ): Array[Byte] = TensorMap.decode(map) match {
    case Left(_)           => encode(Map.empty, Array.emptyLongArray, 0)
    case Right(descriptor) => described(descriptor, coordinates, offsets, copy)
  }

  private def described(
      descriptor: TensorMap.Descriptor,
      coordinates: Vec[Long],
      offsets: Vec[Long],
      copy: VMIR.Instruction.AsyncTensor
  ): Array[Byte] = {
    val element   = descriptor.element
    val gather    = copy.gather
    val operation = copy.operation
    val mode      = descriptor.mode.ordinal.toLong
    val values    = element.values.toLong
    val recorded  = Map(
      Fact.Rank       -> descriptor.rank.raw.toLong,
      Fact.Values     -> values,
      Fact.Interleave -> descriptor.interleave.toLong,
      Fact.Mode       -> mode,
      Fact.Element    -> element.ordinal.toLong,
      Fact.Global     -> element.bytes.raw.toLong,
      Fact.Local      -> element.sharedBytes.toLong,
      Fact.Fill       -> (if (descriptor.nanFill && element.floating) element.outOfBoundsNaN else 0L),
      Fact.Swizzle    -> descriptor.swizzle.toLong,
      Fact.Atomicity  -> descriptor.atomicity.toLong
    )
    if (matches(Execute, descriptor.rank.raw.toLong, mode, coordinates.size, gather, copy.im2col) == 0L)
      encode(recorded, Array.emptyLongArray, 0)
    else {
      val isPlain   = plain(Execute, descriptor.interleave.toLong) != 0L
      val isAligned = aligned(Execute, mode, element.bytes.raw.toLong, coordinates(0), gather) != 0L
      val isSplit   = splits(
        algebra = Execute,
        values = values,
        mode = mode,
        coordinate = coordinates(0),
        dimension = descriptor.dimensions(0),
        traversal = descriptor.traversal(0).toLong,
        box = descriptor.box(0).toLong,
        channels = descriptor.channels.toLong
      ) != 0L
      val unsupported = operation match {
        case TensorMap.Operation.Reduce(_) => packed(Execute, values) != 0L
        case TensorMap.Operation.Store     => unstorable(Execute, element.code.raw.toLong) != 0L
        case _                             => false
      }
      if (operation == TensorMap.Operation.Prefetch || !isPlain || unsupported || !isAligned || !isSplit)
        encode(recorded, Array.emptyLongArray, 0)
      else {
        val raw     = VMAsync.TensorCopy(descriptor, coordinates, offsets, gather)
        val counted = raw.counted
        val runs    = mutable.ArrayBuilder.ofLong()
        counted.runs { (offset, address, count, inside) =>
          runs += offset
          runs += address
          runs += count.toLong
          runs += flag(inside)
        }
        val table       = runs.result()
        val unreducible = operation match {
          case TensorMap.Operation.Reduce(operator) =>
            operator(element, old = 0L, operand = 0L).isEmpty && table.grouped(runWords).exists(_(3) != 0L)
          case _ => false
        }
        encode(
          recorded ++ Map(
            Fact.Unreducible -> flag(unreducible),
            Fact.Bytes       -> counted.bytes,
            Fact.Runs        -> (table.length / runWords).toLong
          ),
          table,
          counted.bytes.toInt
        )
      }
    }
  }

  /** What `transfer` records as `fact`. */
  def fact(transfer: Array[Byte], fact: Fact): Long = LittleEndian.decode(transfer, fact.ordinal * 8, 8)

  /** The name of the mode or element type `transfer` records. */
  def text(transfer: Array[Byte], text: Text): String = text match {
    case Text.Mode    => TensorMap.Mode.fromOrdinal(fact(transfer, Fact.Mode).toInt).toString
    case Text.Element => TensorMap.Element.fromOrdinal(fact(transfer, Fact.Element).toInt).toString
  }

  private def boxStart(transfer: Array[Byte]): Int = (facts + fact(transfer, Fact.Runs).toInt * runWords) * 8

  private def runWord(transfer: Array[Byte], run: Int, word: Int): Long =
    LittleEndian.decode(transfer, (facts + run * runWords + word) * 8, 8)

  /** The run `run` of box elements read from global memory into the box, or filled where it lies outside the tensor. */
  def loadRun(memory: DeviceHeap, transfer: Array[Byte], run: Int): Either[Fault, Unit] = {
    val offset = boxStart(transfer) + runWord(transfer, run, 0).toInt
    val count  = runWord(transfer, run, 2).toInt
    val global = fact(transfer, Fact.Global).toInt
    val local  = fact(transfer, Fact.Local).toInt
    val fill   = fact(transfer, Fact.Fill)
    if (runWord(transfer, run, 3) != 0L)
      memory.read(GuestAddress(runWord(transfer, run, 1)), (count * global).unsafe[Int >= 0]).map { value =>
        if (global == local) {
          val _ = value.copyToArray(transfer, offset)
        } else
          // padded units leave their padding as zero
          (0 until count).foreach(unit =>
            value.slice(unit * global, (unit + 1) * global).copyToArray(transfer, offset + unit * local): Unit
          )
      }
    else {
      if (fill != 0L)
        (0 until count).foreach(index =>
          LittleEndian.write(fill, transfer, offset + index * global, global.unsafe[Int in 0 ~ 8])
        )
      Right(())
    }
  }

  /** The run `run` of box elements written to global memory, unless it lies outside the tensor. */
  def storeRun(memory: DeviceHeap, transfer: Array[Byte], run: Int): Either[Fault, Unit] =
    if (runWord(transfer, run, 3) == 0L) Right(())
    else {
      val offset = boxStart(transfer) + runWord(transfer, run, 0).toInt
      val count  = runWord(transfer, run, 2).toInt
      val global = fact(transfer, Fact.Global).toInt
      val local  = fact(transfer, Fact.Local).toInt
      val bytes  =
        if (global == local) transfer.slice(offset, offset + count * global)
        else Array.concat((0 until count).map(unit => packSixBits(transfer, offset + unit * local))*)
      memory.write(GuestAddress(runWord(transfer, run, 1)), Vec.unsafeWrapArray(bytes))
    }

  /** The run `run` of box elements reduced into global memory by `operator`, unless it lies outside the tensor. */
  def reduceRun(memory: DeviceHeap, transfer: Array[Byte], run: Int, operator: TensorMap.Reduction)
      : Either[Fault, Unit] =
    if (runWord(transfer, run, 3) == 0L) Right(())
    else {
      val element = TensorMap.Element.fromOrdinal(fact(transfer, Fact.Element).toInt)
      val offset  = boxStart(transfer) + runWord(transfer, run, 0).toInt
      val address = runWord(transfer, run, 1)
      Vec.from(0 until runWord(transfer, run, 2).toInt).traverse_ { index =>
        val operand = LittleEndian.decode(transfer, offset + index * element.bytes, element.size)
        memory.atomicModifyValue(
          GuestAddress(address + index.toLong * element.bytes),
          element.bytes.unsafe[Int in 1 ~ 8]
        )(old => operator(element, old, operand).getOrElse(old)).map(_ => ())
      }
    }

  // .b6p2x16 stores keep the low 6 bits of each of 16 bytes, packed from the least significant bit
  private def packSixBits(data: Array[Byte], from: Int): Array[Byte] = {
    val packed = Array.ofDim[Byte](12)
    (0 until 96).foreach { bit =>
      if (((data(from + bit / 6) >> (bit % 6)) & 1) != 0) packed(bit / 8) = (packed(bit / 8) | (1 << (bit % 8))).toByte
    }
    packed
  }

  /** Moves the box one 16-byte chunk at a time to its swizzled shared offsets from `start`, `move` taking each chunk's
    * shared offset, its place in `transfer` and its length.
    */
  def place(transfer: Array[Byte], start: Long)(move: Move): Unit = {
    val swizzle   = fact(transfer, Fact.Swizzle)
    val atomicity = fact(transfer, Fact.Atomicity)
    val size      = Execute.chunk(swizzle, atomicity).toInt
    val bytes     = fact(transfer, Fact.Bytes)
    val base      = boxStart(transfer)
    (0 until bytes.toInt by size).foreach { at =>
      move(
        TensorMapping.placed(Execute, swizzle, atomicity, start, at.toLong),
        base + at,
        Execute.length(bytes, at.toLong, size.toLong).toInt
      )
    }
  }

  /** 1 where a `tensormap.replace.tile.elemtype` immediate names an element type, otherwise 0. */
  private def elementTypeOf(value: Long): Long = flag(TensorMap.accepts(TensorMap.Field.ElementType, value))

  val ElementType: Primitive[Primitive.Unary] =
    Primitive("net.kurobako.gemstone.isa.nvidia.TensorLayout.elementType", elementTypeOf(_))

  private def element(code: UInt[64], tf32: UInt[64]): Option[TensorMap.Element] =
    TensorMap.Element.decode(toRaw(code).toInt, toRaw(tf32) != 0L)

  /** The values per unit of the element type `code` and `tf32` name, or 0 where they name none. */
  def elementValues(code: UInt[64], tf32: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](element(code, tf32).fold(0L)(_.values.toLong))

  /** The bytes in global memory of the element type `code` and `tf32` name, or 0 where they name none. */
  def elementBytes(code: UInt[64], tf32: UInt[64]): UInt[64] =
    fromRaw[UInt[64]](element(code, tf32).fold(0L)(_.bytes.raw.toLong))

  def elementType(value: UInt[64]): UInt[64] = fromRaw[UInt[64]](elementTypeOf(toRaw(value)))
}
