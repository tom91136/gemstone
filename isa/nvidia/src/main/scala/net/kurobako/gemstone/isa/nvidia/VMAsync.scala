package net.kurobako.gemstone.isa.nvidia

import TensorMap.*
import TensorMapping.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** Asynchronous copies and mbarrier transactions. Copies complete as they issue: data moves immediately and the
  * completion mechanism they name observes it at once.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private object VMAsync {

  private def located(context: VMContext, lane: WorkgroupLane)(fault: Fault): Fault =
    VMWriteBuffer.located(fault, context.pc, lane)

  /** The CTA holding a shared address and the address within that CTA's own window. */
  def sharedTarget(context: VMContext, address: Long, size: Long, what: String)
      : Either[Fault, (VMWorkgroup, Long)] =
    if (!VMAddressSpace.isShared(address, size.toInt.max(1)))
      Left(Fault.Backend(s"$what is not shared: 0x${address.toHexString}"))
    else context.workgroup.sharedOwner(address) match {
      case null               => Left(Fault.Backend(s"$what names a CTA outside the cluster: 0x${address.toHexString}"))
      case owner: VMWorkgroup => Right(owner -> VMAddressSpace.sharedAddress(address))
    }

  def inCluster(context: VMContext, address: Long, size: Long): Boolean =
    !VMAddressSpace.isShared(address, size.toInt) || context.workgroup.sharedOwner(address) != null

  def mbarrier(context: VMContext, address: Long): Either[Fault, MBarrier] =
    if ((address & 7L) != 0) Left(Fault.Misaligned(s"misaligned shared mbarrier address: $address"))
    else sharedTarget(context, address, 8, "mbarrier").flatMap((owner, canonical) =>
      owner.mbarriers.get(canonical).toRight(Fault.Backend(s"uninitialised mbarrier at shared address $address"))
    )

  def readShared(owner: VMWorkgroup, start: Long, count: Int): Array[Byte] = {
    val bytes = Array.ofDim[Byte](count)
    owner.readSharedBytes(start, bytes, 0, count)
    bytes
  }

  /** Bytes at a generic address: parameter buffers, shared memory or device memory. */
  def readGeneric(context: VMContext, lane: WorkgroupLane, address: Long, size: Int >= 0)
      : Either[Fault, Array[Byte]] =
    if (size == 0) Right(Array.emptyByteArray)
    else context.workgroup.bufferBytes(address, size) match {
      case Some(bytes)                                    => Right(bytes)
      case None if VMAddressSpace.isShared(address, size) =>
        sharedTarget(context, address, size.raw, "tensor map").map((owner, start) => readShared(owner, start, size))
      case None =>
        context.device.memory.read(GuestAddress(address), size).map(Vec.toArray(_)).leftMap(located(context, lane))
    }

  /** The cluster ranks a multicast mask names. */
  def destinations(context: VMContext, mask: Long): Either[Fault, Vec[VMWorkgroup]] = {
    val cluster = context.workgroup.cluster
    Vec.from((0 until 16).filter(rank => (mask & (1L << rank)) != 0L)).traverse(rank =>
      Option.when(rank < cluster.length)(cluster(rank)).flatMap(Option(_)).map(_.nn).toRight(
        Fault.Backend(s"multicast mask 0x${mask.toHexString} names a CTA outside the cluster")
      )
    )
  }

  def complete(owner: VMWorkgroup, barrier: Long, bytes: Long): Either[Fault, Unit] =
    owner.mbarriers.get(barrier).toRight(Fault.Backend(s"uninitialised mbarrier at shared address $barrier"))
      .flatMap(found => MBarrier.transactionBytes(bytes).flatMap(found.completeBytes(_)))

  private def reduction(
      operator: TensorMap.Reduction,
      element: TensorMap.Element,
      old: Long,
      operand: Long
  ): Either[Fault, Long] =
    operator(element, old, operand).toRight(Fault.Backend(s"bulk reduction $operator has no $element form"))

  def reduceShared(
      owner: VMWorkgroup,
      start: Long,
      data: Array[Byte],
      copy: VMIR.Instruction.AsyncBulkReduce
  ): Either[Fault, Unit] = {
    val operator = copy.operator
    val element  = copy.element
    Vec.from(0 until data.length by element.bytes).traverse_ { offset =>
      reduction(
        operator,
        element,
        owner.readShared(start + offset, element.width),
        LittleEndian.decode(data, offset, element.size)
      ).map(owner.writeShared(start + offset, element.width, _))
    }
  }

  def reduceGlobal(
      context: VMContext,
      lane: WorkgroupLane,
      target: Long,
      data: Array[Byte],
      copy: VMIR.Instruction.AsyncBulkReduce
  ): Either[Fault, Unit] = {
    val operator = copy.operator
    val element  = copy.element
    Vec.from(0 until data.length by element.bytes).traverse_ { offset =>
      val operand = LittleEndian.decode(data, offset, element.size)
      reduction(operator, element, 0L, operand).flatMap(_ =>
        context.device.memory.atomicModifyValue(GuestAddress(target + offset), element.bytes.unsafe[Int in 1 ~ 8])(
          old => operator(element, old, operand).getOrElse(old)
        ).map(_ => ()).leftMap(located(context, lane))
      )
    }
  }

  def storeMasked(
      context: VMContext,
      lane: WorkgroupLane,
      destination: Long,
      source: Long,
      bytes: Long,
      mask: Long
  ): Unit = (0L until bytes).foreach { offset =>
    if ((mask & (1L << (offset.toInt & 15))) != 0L)
      context.effects.stageMemory(
        lane,
        GuestAddress(destination + offset),
        8,
        context.workgroup.readShared(source + offset, 8)
      )
  }

  private[isa] trait Visit {
    def apply(offset: Long, address: Long, count: Int, inside: Boolean): Unit
  }

  /** One box transfer: the global elements a tensor copy visits, in shared-memory order. Elements move in units of
    * `global` bytes in global memory and `local` bytes in shared memory, which differ only for padded packed types.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private[isa] case class TensorCopy(
      descriptor: Descriptor,
      coordinates: Vec[Long],
      offsets: Vec[Long],
      gather: Boolean
  ) {
    private val global      = descriptor.element.bytes
    private val local       = descriptor.element.sharedBytes
    private val wide        = descriptor.mode == Mode.Im2colWide || descriptor.mode == Mode.Im2colWide128
    private val wideBits    = if (wide) 1L else 0L
    private val wide128Bits = if (descriptor.mode == Mode.Im2colWide128) 1L else 0L

    // pixels, halo pixels and start shift of an im2col box
    private val extent: Array[Long] = TensorMapping.shape(
      Execute,
      wideBits,
      wide128Bits,
      descriptor.pixels.toLong,
      offsets.headOption.getOrElse(0L),
      offsets.lift(1).getOrElse(0L)
    )((pixels, halos, shift) => Array(pixels, halos, shift))
    private val pixels = extent(0)
    private val halos  = extent(1)
    private val shift  = extent(2)

    val bytes: Long = descriptor.mode match {
      case Mode.Tile if gather => 4L * descriptor.boxElements(0) * local
      case Mode.Tile           => product(descriptor.boxElements, 0, local.toLong)
      case _                   => descriptor.channels.toLong * (pixels + halos) * local
    }

    /** The copy with dimension 0 counted in units of packed elements, for a box that splits into them. */
    def counted: TensorCopy = {
      val values = descriptor.element.values
      if (values == 1) this
      else
        TensorCopy(
          descriptor.copy(
            dimensions = descriptor.dimensions.updated(0, descriptor.dimensions(0) / values),
            box = descriptor.box.updated(0, descriptor.box(0) / values),
            channels = descriptor.channels / values
          ),
          coordinates.updated(0, coordinates(0) / values),
          offsets,
          gather
        )
    }

    private def product(counts: Vec[Int], from: Int, seed: Long): Long = {
      var result = seed
      var axis   = from
      while (axis < counts.size) {
        result *= counts(axis)
        axis += 1
      }
      result
    }

    /** Each run of contiguous box elements: its offset in the box, its global address and element count, and whether it
      * lies within the tensor.
      */
    def runs(visit: Visit): Unit = descriptor.mode match {
      case Mode.Tile => tile(visit)
      case _         => columns(visit)
    }

    // .tile::gather4 and .tile::scatter4 take a box row from each of four rows of a 2D tensor
    private def tile(visit: Visit): Unit = {
      val counts = descriptor.boxElements
      val rank   = descriptor.rank.raw
      val rows   = TensorMapping.rows(Execute, gather, rank, counts(_).toLong)
      var row    = 0L
      while (row < rows) {
        TensorMapping.tileRow(
          Execute,
          gather,
          rank,
          row,
          descriptor.address,
          coordinates(_),
          descriptor.dimensions(_),
          axis => descriptor.strides(axis - 1),
          descriptor.traversal(_).toLong,
          counts(_).toLong,
          value => value
        )((base, inside) =>
          elements(visit, row, counts(0), base, inside, coordinates(0), descriptor.traversal(0).toLong)
        )
        row += 1
      }
    }

    private def columns(visit: Visit): Unit = {
      val rank  = descriptor.rank.raw
      var pixel = 0L
      while (pixel < pixels + halos) {
        TensorMapping.pixelRow(
          Execute,
          rank,
          wideBits,
          wide128Bits,
          pixel,
          pixels,
          shift,
          descriptor.address,
          coordinates(_),
          descriptor.dimensions(_),
          axis => descriptor.strides(axis - 1),
          descriptor.traversal(_).toLong,
          descriptor.lower(_).toLong,
          descriptor.upper(_).toLong,
          offsets.lift(_).getOrElse(0L),
          value => value
        )((base, inside) => elements(visit, pixel, descriptor.channels, base, inside, coordinates(0), 1L))
        pixel += 1
      }
    }

    // contiguous elements of a row that lie alike inside or outside the tensor make one run
    private def elements(
        visit: Visit,
        row: Long,
        columns: Int,
        base: Long,
        rowInside: Long,
        start: Long,
        step: Long
    ): Unit = {
      val dimension = descriptor.dimensions(0)
      var column    = 0
      while (column < columns) {
        val position = Execute.coordinate(start, column.toLong, step)
        val within   = Execute.inside(rowInside, position, dimension)
        var length   = 1
        if (step == 1L)
          while (
            column + length < columns &&
            Execute.inside(rowInside, position + length, dimension) == within
          ) length += 1
        visit(
          Execute.offset(row, columns.toLong, column.toLong, local.toLong),
          Execute.address(base, position, global.toLong),
          length,
          within != 0L
        )
        column += length
      }
    }
  }
}
