package net.kurobako.gemstone.isa.nvidia

import TensorMap.*
import VMExecution.*
import VMIR.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.vm.*

/** Asynchronous copies, mbarrier transactions and cluster addressing. Copies complete as they issue: data moves
  * immediately and the completion mechanism they name observes it at once.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private object VMAsync {

  private inline def forEveryLane(context: VMContext)(inline function: WorkgroupLane => Either[Fault, Unit])
      : Either[Fault, Step] = LaneFaults.everyLane(context)(function) match {
    case Left(fault) => Left(fault)
    case _           => Advanced
  }

  private def located(context: VMContext, lane: WorkgroupLane)(fault: Fault): Fault =
    VMWriteBuffer.located(fault, context.pc, lane)

  /** The CTA holding a shared address and the address within that CTA's own window. */
  private def sharedTarget(context: VMContext, address: Long, size: Long, what: String)
      : Either[Fault, (VMWorkgroup, Long)] =
    if (!VMAddressSpace.isShared(address, size.toInt.max(1)))
      Left(Fault.Backend(s"$what is not shared: 0x${address.toHexString}"))
    else context.workgroup.sharedOwner(address) match {
      case null               => Left(Fault.Backend(s"$what names a CTA outside the cluster: 0x${address.toHexString}"))
      case owner: VMWorkgroup => Right(owner -> VMAddressSpace.sharedAddress(address))
    }

  def mbarrier(context: VMContext, address: Long): Either[Fault, MBarrier] =
    if ((address & 7L) != 0) Left(Fault.Misaligned(s"misaligned shared mbarrier address: $address"))
    else sharedTarget(context, address, 8, "mbarrier").flatMap((owner, canonical) =>
      owner.mbarriers.get(canonical).toRight(Fault.Backend(s"uninitialised mbarrier at shared address $address"))
    )

  private def readShared(owner: VMWorkgroup, start: Long, count: Int): Array[Byte] = {
    val bytes = Array.ofDim[Byte](count)
    owner.readSharedBytes(start, bytes, 0, count)
    bytes
  }

  /** Bytes at a generic address: parameter buffers, shared memory or device memory. */
  private def readGeneric(context: VMContext, lane: WorkgroupLane, address: Long, size: Int >= 0)
      : Either[Fault, Array[Byte]] =
    if (size == 0) Right(Array.emptyByteArray)
    else context.workgroup.bufferBytes(address, size) match {
      case Some(bytes)                                    => Right(bytes)
      case None if VMAddressSpace.isShared(address, size) =>
        sharedTarget(context, address, size.raw, "tensor map").map((owner, start) => readShared(owner, start, size))
      case None =>
        context.device.memory.read(GuestAddress(address), size).map(Vec.toArray(_)).leftMap(located(context, lane))
    }

  private def writeGeneric(context: VMContext, lane: WorkgroupLane, address: Long, bytes: Array[Byte])
      : Either[Fault, Unit] =
    if (VMAddressSpace.isShared(address, bytes.length))
      sharedTarget(context, address, bytes.length, "tensor map").map((owner, start) =>
        owner.writeSharedBytes(start, bytes, 0, bytes.length)
      )
    else context.device.memory.write(GuestAddress(address), Vec.unsafeWrapArray(bytes)).leftMap(located(context, lane))

  /** The cluster ranks a multicast mask names, or this CTA's alone. */
  private def destinations(context: VMContext, mask: Option[Long]): Either[Fault, Vec[VMWorkgroup]] = mask match {
    case None        => Right(Vec(context.workgroup))
    case Some(value) =>
      val cluster = context.workgroup.cluster
      Vec.from((0 until 16).filter(rank => (value & (1L << rank)) != 0L)).traverse(rank =>
        Option.when(rank < cluster.length)(cluster(rank)).flatMap(Option(_)).map(_.nn).toRight(
          Fault.Backend(s"multicast mask 0x${value.toHexString} names a CTA outside the cluster")
        )
      )
  }

  /** The CTA of `cta`'s pair whose rank has the low bit `parity`. */
  private def peer(cta: VMWorkgroup, parity: Int): Either[Fault, VMWorkgroup] = {
    val rank = (cta.clusterRank & ~1) | parity
    (if (rank < cta.cluster.length) cta.cluster(rank) else null) match {
      case null               => Left(Fault.Backend(s"cta_group::2 needs CTA $rank of the pair in the cluster"))
      case found: VMWorkgroup => Right(found)
    }
  }

  private def bulkAligned(target: Long, origin: Long, bytes: Long): Boolean =
    (target & 15L) == 0 &&
      (origin & 15L) == 0 &&
      (bytes & 15L) == 0 && bytes >= 0 && bytes <= MBarrier.MaximumCount

  private def complete(owner: VMWorkgroup, barrier: Long, bytes: Long): Either[Fault, Unit] =
    owner.mbarriers.get(barrier).toRight(Fault.Backend(s"uninitialised mbarrier at shared address $barrier"))
      .flatMap(found => MBarrier.transactionBytes(bytes).flatMap(found.completeBytes(_)))

  def execute(instruction: Instruction, context: VMContext): Either[Fault, Step] = (instruction: @unchecked) match {
    // .ignore_oob leaves the ignored destination bytes indeterminate, so they keep their contents
    case Instruction.AsyncBulkCopy(
          destination,
          source,
          size,
          barrier,
          sharedSource,
          multicast,
          ignored,
          clusterDestination
        ) =>
      forEveryLane(context) { lane =>
        val target = context.read(lane, destination)
        val origin = context.read(lane, source)
        val bytes  = context.read(lane, size)
        val left   = ignored.fold(0L)(bounds => context.read(lane, bounds.left))
        val right  = ignored.fold(0L)(bounds => context.read(lane, bounds.right))
        for {
          _ <- Either.cond(
            !(clusterDestination && sharedSource) || context.explicitCluster,
            (),
            Fault.IllegalInstruction(
              "a bulk copy of shared::cta into shared::cluster memory needs a launch with a cluster"
            )
          )
          _ <- Either.cond(
            bulkAligned(target, origin, bytes) &&
              left >= 0 && left <= 15 && right >= 0 && right <= 15 && left + right <= bytes,
            (),
            Fault.Backend(
              s"invalid bulk copy: destination=$target, source=$origin, size=$bytes, ignored=$left+$right"
            )
          )
          (owner, start)   <- sharedTarget(context, target, bytes, "bulk copy destination")
          (_, barrierWord) <- sharedTarget(context, context.read(lane, barrier), 8, "bulk copy mbarrier")
          kept = (bytes - left - right).toInt
          data <-
            if (sharedSource)
              sharedTarget(context, origin + left, kept, "bulk copy source").map((from, at) =>
                readShared(from, at, kept)
              )
            else readGeneric(context, lane, origin + left, kept.unsafe[Int >= 0])
          targets <-
            if (multicast.isEmpty) Right(Vec(owner)) else destinations(context, multicast.map(context.read(lane, _)))
          _ <- targets.traverse_ { cta =>
            cta.writeSharedBytes(start + left.toInt, data, 0, data.length)
            complete(cta, barrierWord, bytes)
          }
        } yield ()
      }
    case Instruction.AsyncBulkReduce(destination, source, size, barrier, operator, element) =>
      forEveryLane(context) { lane =>
        val target                                                 = context.read(lane, destination)
        val origin                                                 = context.read(lane, source)
        val bytes                                                  = context.read(lane, size)
        val width                                                  = element.bytes
        def combine(old: Long, operand: Long): Either[Fault, Long] =
          operator(element, old, operand).toRight(Fault.Backend(s"bulk reduction $operator has no $element form"))
        for {
          _ <- Either.cond(
            bulkAligned(target, origin, bytes),
            (),
            Fault.Backend(s"invalid bulk reduction: destination=$target, source=$origin, size=$bytes")
          )
          (from, at) <- sharedTarget(context, origin, bytes, "bulk reduction source")
          data = readShared(from, at, bytes.toInt)
          _ <- barrier match {
            case Some(word) =>
              for {
                (owner, start)   <- sharedTarget(context, target, bytes, "bulk reduction destination")
                (_, barrierWord) <- sharedTarget(context, context.read(lane, word), 8, "bulk reduction mbarrier")
                _                <- Vec.from(0 until bytes.toInt by width).traverse_ { offset =>
                  combine(
                    owner.readShared(start + offset, element.width),
                    LittleEndian.decode(data, offset, element.size)
                  )
                    .map(owner.writeShared(start + offset, element.width, _))
                }
                _ <- complete(owner, barrierWord, bytes)
              } yield ()
            case None =>
              Vec.from(0 until bytes.toInt by width).traverse_ { offset =>
                val operand = LittleEndian.decode(data, offset, element.size)
                combine(0L, operand).flatMap(_ =>
                  context.device.memory.atomicModifyValue(GuestAddress(target + offset), width.unsafe[Int in 1 ~ 8])(
                    old =>
                      operator(element, old, operand).getOrElse(old)
                  ).map(_ => ()).leftMap(located(context, lane))
                )
              }
          }
        } yield ()
      }
    case Instruction.AsyncBulkFill(address, size) =>
      forEveryLane(context) { lane =>
        val bytes = context.read(lane, size)
        sharedTarget(context, context.read(lane, address), bytes, "bulk fill").flatMap((owner, start) =>
          Either.cond(
            bytes >= 0 && (bytes & 7L) == 0,
            owner.writeSharedBytes(start, Array.ofDim(bytes.toInt), 0, bytes.toInt),
            Fault.Backend(s"invalid bulk fill size $bytes")
          )
        )
      }
    case Instruction.AsyncTensor(
          operation,
          map,
          coordinates,
          shared,
          barrier,
          multicast,
          offsets,
          im2col,
          gather,
          pair
        ) =>
      forEveryLane(context) { lane =>
        for {
          bytes      <- readGeneric(context, lane, context.read(lane, map), TensorMap.Bytes)
          descriptor <- TensorMap.decode(bytes)
          _          <- Either.cond(
            (if (gather) coordinates.size == 5 && descriptor.rank == 2
             else coordinates.size == descriptor.rank.raw) && im2col == (descriptor.mode != Mode.Tile),
            (),
            Fault.Backend(s"tensor copy does not match its ${descriptor.rank}D ${descriptor.mode} tensor map")
          )
          // the interleaved layouts are not verified on hardware, so a copy in the plain layout would be silently wrong
          _ <- Either.cond(
            operation == TensorMap.Operation.Prefetch || descriptor.interleave == 0,
            (),
            Fault.Backend(s"tensor maps with interleave ${descriptor.interleave} are not modelled")
          )
          _ <- operation match {
            case TensorMap.Operation.Reduce(_) if descriptor.element.packed =>
              Left(Fault.Backend(s"tensor reductions have no ${descriptor.element} form"))
            case TensorMap.Operation.Store if descriptor.element == Element.B4x16P64 =>
              Left(Fault.Backend(s"tensor stores have no ${descriptor.element} form"))
            case _ => Right(())
          }
          copy <- TensorCopy.of(
            descriptor,
            coordinates.map(context.read(lane, _).toInt.toLong),
            offsets.map(context.read(lane, _) & 0xffffL),
            gather
          )
          _ <- operation match {
            case TensorMap.Operation.Prefetch => Right(())
            case TensorMap.Operation.Load     =>
              for {
                (owner, start) <-
                  sharedTarget(context, context.read(lane, shared), copy.bytes, "tensor copy destination")
                (signalled, barrierWord) <- barrier.toRight(Fault.Backend("tensor load without an mbarrier"))
                  .flatMap(word => sharedTarget(context, context.read(lane, word), 8, "tensor copy mbarrier"))
                data    <- copy.load(context.device.memory).leftMap(located(context, lane))
                targets <-
                  if (multicast.isEmpty) Right(Vec(owner))
                  else destinations(context, multicast.map(context.read(lane, _)))
                _ <- targets.traverse_ { cta =>
                  copy.place(data, start)((offset, from, count) => cta.writeSharedBytes(offset, data, from, count))
                  (if (pair) peer(cta, signalled.clusterRank & 1) else Right(cta))
                    .flatMap(complete(_, barrierWord, copy.bytes))
                }
              } yield ()
            case TensorMap.Operation.Store | TensorMap.Operation.Reduce(_) =>
              sharedTarget(context, context.read(lane, shared), copy.bytes, "tensor copy source").flatMap {
                (owner, start) =>
                  val data = Array.ofDim[Byte](copy.bytes.toInt)
                  copy.place(data, start)((offset, from, count) => owner.readSharedBytes(offset, data, from, count))
                  copy.store(context.device.memory, data, operation).leftMap(located(context, lane))
              }
          }
        } yield ()
      }
    case Instruction.TensorMapReplace(address, field, index, value) =>
      forEveryLane(context) { lane =>
        val target = context.read(lane, address)
        for {
          _ <-
            Either.cond((target & 63L) == 0, (), Fault.Misaligned(s"misaligned tensor map at 0x${target.toHexString}"))
          bytes <- readGeneric(context, lane, target, TensorMap.Bytes)
          _     <- TensorMap.replace(bytes, field, index, context.read(lane, value))
          _     <- writeGeneric(context, lane, target, bytes)
        } yield ()
      }
    case Instruction.TensorMapCopy(destination, source) =>
      forEveryLane(context)(lane =>
        readGeneric(context, lane, context.read(lane, source), TensorMap.Bytes)
          .flatMap(writeGeneric(context, lane, context.read(lane, destination), _))
      )
    case Instruction.AsyncSharedStore(address, sources, width, barrier) =>
      forEveryLane(context) { lane =>
        val bytes = width.toScalarByteWidth.toLong * sources.size
        for {
          (owner, start)   <- sharedTarget(context, context.read(lane, address), bytes, "asynchronous store")
          (_, barrierWord) <- sharedTarget(context, context.read(lane, barrier), 8, "asynchronous store mbarrier")
          _                <- Either.cond(
            context.workgroup.sharedOwner(context.read(lane, barrier)) eq owner,
            (),
            Fault.Backend("asynchronous store and its mbarrier lie in different CTAs")
          )
          _ = sources.zipWithIndex.foreach((source, index) =>
            owner.writeShared(start + index * width.toScalarByteWidth, width, context.read(lane, source))
          )
          _ <- complete(owner, barrierWord, bytes)
        } yield ()
      }
    case Instruction.AsyncSharedReduce(address, source, width, operation, barrier) =>
      forEveryLane(context) { lane =>
        for {
          (owner, start) <- sharedTarget(
            context,
            context.read(lane, address),
            width.toScalarByteWidth.toLong,
            "asynchronous reduction"
          )
          (_, barrierWord) <- sharedTarget(context, context.read(lane, barrier), 8, "asynchronous reduction mbarrier")
          _ = owner.writeShared(
            start,
            width,
            operation(owner.readShared(start, width), context.read(lane, source), width)
          )
          _ <- complete(owner, barrierWord, width.toScalarByteWidth.toLong)
        } yield ()
      }
    case Instruction.MBarrierTransaction(address, bytes, finished) =>
      forEveryLane(context)(lane =>
        for {
          barrier <- mbarrier(context, context.read(lane, address))
          count   <- MBarrier.transactionBytes(context.read(lane, bytes))
          _       <- if (finished) barrier.completeBytes(count) else barrier.expectBytes(count)
        } yield ()
      )
    case Instruction.ClusterMap(destination, address, rank) =>
      forEveryLane(context) { lane =>
        val raw    = context.read(lane, address)
        val target = context.read(lane, rank)
        if (!VMAddressSpace.isShared(raw)) Left(Fault.Backend(s"mapa of a non-shared address 0x${raw.toHexString}"))
        else if (target < 0 || target >= context.workgroup.cluster.length)
          Left(Fault.Backend(s"mapa names rank $target outside a ${context.workgroup.cluster.length}-CTA cluster"))
        else Right(context.effects.stageRegister(lane, destination, VMAddressSpace.clusterAddress(raw, target.toInt)))
      }
    case Instruction.ClusterRankOf(destination, address) =>
      forEveryLane(context) { lane =>
        val raw = context.read(lane, address)
        if (!VMAddressSpace.isShared(raw))
          Left(Fault.Backend(s"getctarank of a non-shared address 0x${raw.toHexString}"))
        else {
          val rank = VMAddressSpace.clusterRank(raw)
          Right(context.effects.stageRegister(
            lane,
            destination,
            (if (rank < 0) context.workgroup.clusterRank else rank).toLong
          ))
        }
      }
  }

  private trait Visit {
    def apply(offset: Long, address: Long, count: Int, inside: Boolean): Unit
  }

  private trait Move {
    def apply(offset: Long, from: Int, count: Int): Unit
  }

  private object TensorCopy {

    /** The copy with dimension 0 counted in units of packed elements, or why its box does not split into units. */
    def of(descriptor: Descriptor, coordinates: Vec[Long], offsets: Vec[Long], gather: Boolean)
        : Either[Fault, TensorCopy] = {
      val values = descriptor.element.values
      val whole  = coordinates(0) % values == 0 && descriptor.dimensions(0) % values == 0 &&
        descriptor.traversal(0) == 1 &&
        (if (descriptor.mode == Mode.Tile) descriptor.box(0) % values == 0 else descriptor.channels % values == 0)
      // the inner coordinate of a tile must be a multiple of 16 bytes
      if (descriptor.mode == Mode.Tile && !gather && (coordinates(0) * descriptor.element.bytes) % 16 != 0)
        Left(Fault.IllegalInstruction(s"tensor tile coordinate ${coordinates(0)} is not a multiple of 16 bytes"))
      else if (values == 1) Right(TensorCopy(descriptor, coordinates, offsets, gather))
      else if (!whole)
        Left(Fault.Backend(s"tensor copy of ${descriptor.element} elements does not split into units of $values"))
      else
        Right(TensorCopy(
          descriptor.copy(
            dimensions = descriptor.dimensions.updated(0, descriptor.dimensions(0) / values),
            box = descriptor.box.updated(0, descriptor.box(0) / values),
            channels = descriptor.channels / values
          ),
          coordinates.updated(0, coordinates(0) / values),
          offsets,
          gather
        ))
    }
  }

  /** One box transfer: the global elements a tensor copy visits, in shared-memory order. Elements move in units of
    * `global` bytes in global memory and `local` bytes in shared memory, which differ only for padded packed types.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private case class TensorCopy(descriptor: Descriptor, coordinates: Vec[Long], offsets: Vec[Long], gather: Boolean) {
    private val global = descriptor.element.bytes
    private val local  = descriptor.element.sharedBytes
    private val wide   = descriptor.mode == Mode.Im2colWide || descriptor.mode == Mode.Im2colWide128
    // the wide modes take wHalo and wOffset where im2col takes its offsets
    private val halo   = if (wide) offsets.headOption.getOrElse(0L).toInt else 0
    private val shift  = if (wide) offsets.lift(1).getOrElse(0L).toInt else 0
    private val pixels = if (descriptor.mode == Mode.Im2colWide128) 128 else descriptor.pixels
    // im2col::w::128 loads its halo after every 32 pixels
    private val halos = if (descriptor.mode == Mode.Im2colWide128) 4 * halo else halo

    val bytes: Long = descriptor.mode match {
      case Mode.Tile if gather => 4L * descriptor.boxElements(0) * local
      case Mode.Tile           => product(descriptor.boxElements, 0, local.toLong)
      case _                   => descriptor.channels.toLong * (pixels + halos) * local
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
    private def runs(visit: Visit): Unit = descriptor.mode match {
      case Mode.Tile => tile(visit)
      case _         => columns(visit)
    }

    // .tile::gather4 and .tile::scatter4 take a box row from each of four rows of a 2D tensor
    private def tile(visit: Visit): Unit = {
      val counts  = descriptor.boxElements
      val rank    = descriptor.rank
      val rows    = if (gather) 4L else product(counts, 1, 1L)
      val stride0 = descriptor.traversal(0)
      var row     = 0L
      while (row < rows) {
        var rest    = row
        var base    = descriptor.address
        var outside = false
        if (gather) {
          val coordinate = coordinates(1 + row.toInt)
          outside = coordinate < 0 || coordinate >= descriptor.dimensions(1)
          base += coordinate * descriptor.strides(0)
        } else {
          var axis = 1
          while (axis < rank) {
            val index = rest % counts(axis)
            rest /= counts(axis)
            val coordinate = coordinates(axis) + index * descriptor.traversal(axis)
            if (coordinate < 0 || coordinate >= descriptor.dimensions(axis)) outside = true
            base += coordinate * descriptor.strides(axis - 1)
            axis += 1
          }
        }
        val offset = row * counts(0) * local
        var column = 0
        while (column < counts(0)) {
          val coordinate = coordinates(0) + column.toLong * stride0
          val inside     = !outside && coordinate >= 0 && coordinate < descriptor.dimensions(0)
          var length     = 1
          if (stride0 == 1)
            while (
              column + length < counts(0) &&
              (!outside && coordinate + length >= 0 && coordinate + length < descriptor.dimensions(0)) == inside
            ) length += 1
          visit(offset + column.toLong * local, base + coordinate * global, length, inside)
          column += length
        }
        row += 1
      }
    }

    /** Pixels advance through the bounding box W first, then H and D, and then to the next image; the wide modes walk W
      * alone, its corners and start moved by wOffset, and follow the pixels with their halo, the pixels the walk
      * reaches next: after the last pixel for im2col::w and after each 32 for im2col::w::128, where the halo for each
      * 32 interleave.
      */
    private def columns(visit: Visit): Unit = {
      val rank                   = descriptor.rank
      val spatial                = rank - 2
      val walked                 = if (wide) 1 else spatial
      val adjust                 = if (wide) Vec.empty[Long] else offsets
      def lower(axis: Int): Long = descriptor.lower(axis).toLong + (if (axis == 0) shift else 0)
      def upper(axis: Int): Long = descriptor.upper(axis).toLong + (if (axis == 0) shift else 0)
      val position = Array.tabulate(spatial)(axis => coordinates(axis + 1) + (if (axis == 0) shift else 0))
      var image    = coordinates(rank - 1)
      val walks    = pixels + halo
      val images   = Array.ofDim[Long](walks)
      val places   = Array.ofDim[Long](walks * spatial)
      var step     = 0
      while (step < walks) {
        images(step) = image
        Array.copy(position, 0, places, step * spatial, spatial)
        var axis  = 0
        var carry = true
        while (carry && axis < walked) {
          position(axis) += descriptor.traversal(axis + 1)
          if (position(axis) > descriptor.dimensions(axis + 1) - 1 + upper(axis)) {
            position(axis) = lower(axis)
            axis += 1
          } else carry = false
        }
        if (carry) image += 1
        step += 1
      }
      var pixel = 0
      while (pixel < pixels + halos) {
        val walk =
          if (pixel < pixels || descriptor.mode != Mode.Im2colWide128) pixel
          else 32 * ((pixel - pixels) % 4 + 1) + (pixel - pixels) / 4
        var base   = descriptor.address
        var inside = images(walk) >= 0 && images(walk) < descriptor.dimensions(rank - 1)
        base += images(walk) * descriptor.strides(rank - 2)
        var axis = 0
        while (axis < spatial) {
          val coordinate = places(walk * spatial + axis) + adjust.lift(axis).getOrElse(0L)
          if (coordinate < 0 || coordinate >= descriptor.dimensions(axis + 1)) inside = false
          base += coordinate * descriptor.strides(axis)
          axis += 1
        }
        val channel = coordinates(0)
        val offset  = pixel.toLong * descriptor.channels * local
        var column  = 0
        while (column < descriptor.channels) {
          val within = inside && channel + column >= 0 && channel + column < descriptor.dimensions(0)
          var length = 1
          while (
            column + length < descriptor.channels &&
            (inside && channel + column + length >= 0 && channel + column + length < descriptor.dimensions(0)) == within
          ) length += 1
          visit(offset + column.toLong * local, base + (channel + column) * global, length, within)
          column += length
        }
        pixel += 1
      }
    }

    /** The box read from global memory in shared order, out-of-bounds elements filled. */
    def load(memory: DeviceHeap): Either[Fault, Array[Byte]] = {
      val data                   = Array.ofDim[Byte](bytes.toInt)
      var failure: Option[Fault] = None
      val fill                   =
        if (descriptor.nanFill && descriptor.element.floating) descriptor.element.outOfBoundsNaN else 0L
      runs { (offset, address, count, inside) =>
        if (failure.isEmpty) {
          if (inside) memory.read(GuestAddress(address), (count * global).unsafe[Int >= 0]) match {
            case Left(fault)                         => failure = Some(fault)
            case Right(value) if global.raw == local =>
              val _ = value.copyToArray(data, offset.toInt)
            // padded units leave their padding as zero
            case Right(value) =>
              (0 until count).foreach(unit =>
                value.slice(unit * global, (unit + 1) * global).copyToArray(data, offset.toInt + unit * local): Unit
              )
          }
          else if (fill != 0L)
            (0 until count).foreach(index =>
              LittleEndian.write(fill, data, offset.toInt + index * global, descriptor.element.size)
            )
        }
      }
      failure.toLeft(data)
    }

    /** Writes or reduces the in-bounds elements of a box held in shared order into global memory. */
    def store(memory: DeviceHeap, data: Array[Byte], operation: TensorMap.Operation): Either[Fault, Unit] = {
      var failure: Option[Fault] = None
      runs { (offset, address, count, inside) =>
        if (failure.isEmpty && inside) operation match {
          case TensorMap.Operation.Reduce(operator) =>
            var index = 0
            while (failure.isEmpty && index < count) {
              val operand = LittleEndian.decode(data, offset.toInt + index * global, descriptor.element.size)
              operator(descriptor.element, 0L, operand) match {
                case None =>
                  failure = Some(Fault.Backend(s"tensor reduction $operator has no ${descriptor.element} form"))
                case Some(_) =>
                  memory.atomicModifyValue(
                    GuestAddress(address + index.toLong * global),
                    global.unsafe[Int in 1 ~ 8]
                  )(old => operator(descriptor.element, old, operand).getOrElse(old)).left.foreach(fault =>
                    failure = Some(fault)
                  )
              }
              index += 1
            }
          case _ =>
            val bytes =
              if (global.raw == local) data.slice(offset.toInt, offset.toInt + count * global)
              else Array.concat((0 until count).map(unit => packSixBits(data, offset.toInt + unit * local))*)
            memory.write(GuestAddress(address), Vec.unsafeWrapArray(bytes)).left.foreach(fault => failure = Some(fault))
        }
      }
      failure.toLeft(())
    }

    // .b6p2x16 stores keep the low 6 bits of each of 16 bytes, packed from the least significant bit
    private def packSixBits(data: Array[Byte], from: Int): Array[Byte] = {
      val packed = Array.ofDim[Byte](12)
      (0 until 96).foreach { bit =>
        if (((data(from + bit / 6) >> (bit % 6)) & 1) != 0) packed(bit / 8) =
          (packed(bit / 8) | (1 << (bit % 8))).toByte
      }
      packed
    }

    /** Places the box's bytes at their swizzled shared offsets from `start`, one 16-byte chunk at a time. */
    def place(data: Array[Byte], start: Long)(move: Move): Unit = {
      val chunk = if (descriptor.swizzle == 3 && descriptor.atomicity == 2) 8 else 16
      var at    = 0
      while (at < data.length) {
        val count = math.min(chunk, data.length - at)
        move(start + TensorMap.swizzled(descriptor, start, at.toLong), at, count)
        at += count
      }
    }
  }
}
