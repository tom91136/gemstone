package net.kurobako.gemstone.isa.nvidia

import TensorMemory.*
import VMExecution.*
import VMIR.*
import VMIR.Matrix.*
import VMIR.Tcgen05.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The tcgen05 operations on tensor memory. They complete as they issue, so tcgen05.wait, the tcgen05 fences and the
  * mbarrier tcgen05.commit arrives on order nothing further. Allocation, loads and stores are warp-collective; copies,
  * shifts, products and commits run once for each issuing thread.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private object VMTensorMemory {

  private def located(context: VMContext, lane: WorkgroupLane)(fault: Fault): Fault =
    VMWriteBuffer.located(fault, context.pc, lane)

  /** The CTA of the issuing CTA's pair at `index`, 0 for the even one and 1 for the odd one. */
  def pairCta(context: VMContext, index: Int): Either[Fault, VMWorkgroup] = {
    val rank    = (context.workgroup.clusterRank & ~1) + index
    val cluster = context.workgroup.cluster
    (if (rank < cluster.length) cluster(rank) else null) match {
      case null               => Left(Fault.Backend(s"cta_group::2 needs CTA $rank of the pair in the cluster"))
      case found: VMWorkgroup => Right(found)
    }
  }

  private def ctas(context: VMContext, pair: Boolean): Either[Fault, Vec[VMWorkgroup]] =
    if (pair) Vec(0, 1).traverse(pairCta(context, _)) else Right(Vec(context.workgroup))

  private def byte(owner: VMWorkgroup, offset: Long): Long =
    owner.readShared(VMAddressSpace.SharedBase + (offset & 0x3ffffL), 8)

  private def chunk(owner: VMWorkgroup, offset: Long): Array[Byte] =
    Array.tabulate(16)(index => byte(owner, offset + index).toByte)

  def execute(instruction: Instruction, context: VMContext, effects: VMWriteBuffer): Either[Fault, Step] =
    (instruction: @unchecked) match {
      case Instruction.TensorMemoryAllocate(destination, columns) =>
        warps(context) { (lane, _) =>
          val state  = context.workgroup.tensorMemory
          val target = context.read(lane, destination)
          val count  = context.read(lane, columns)
          if (!state.permitted)
            Left(Fault.Backend("tcgen05.alloc after the CTA relinquished its allocation permit"))
          else if (count < Granule || count > Columns || java.lang.Long.bitCount(count) != 1)
            Left(Fault.Backend(s"tcgen05.alloc of $count columns, which is not a power of two from 32 to 512"))
          else state.allocate((count / Granule).toInt) match {
            case Some(start) => Right(effects.stageShared(target, 32, (start * Granule).toLong))
            case None        =>
              Left(Fault.Backend(
                s"tcgen05.alloc of $count columns waits forever: granules 0x${state.taken.toHexString} are taken"
              ))
          }
        }
      case Instruction.TensorMemoryDeallocate(address, columns) =>
        warps(context) { (lane, _) =>
          val state = context.workgroup.tensorMemory
          val raw   = context.read(lane, address)
          val count = context.read(lane, columns)
          granules(column(raw), count) match {
            case Some(mask) if TensorMemory.lane(raw) == 0 && state.free(mask) => Completed
            case _                                                             =>
              Left(Fault.Backend(s"tcgen05.dealloc of $count columns at 0x${raw.toHexString}, which are not allocated"))
          }
        }
      case Instruction.TensorMemoryRelinquish =>
        context.workgroup.tensorMemory.relinquish()
        Advanced
      case Instruction.TensorMemoryClaim(destination, claimed, granules) =>
        Right(issuers(context).foreach { lanes =>
          val count = context.read(lanes.head, granules)
          val start =
            if (count < 1 || count > Granules || java.lang.Long.bitCount(count) != 1) None
            else context.workgroup.tensorMemory.allocate(count.toInt)
          lanes.foreach { lane =>
            effects.stageRegister(lane, destination, start.fold(0L)(_.toLong))
            effects.stage(VMWrite.Predicate(lane, claimed, start.nonEmpty))
          }
        }).map(_ => Step.Advance)
      case Instruction.TensorMemoryRelease(mask) =>
        Right(issuers(context).foreach(lanes =>
          context.workgroup.tensorMemory.retain(context.read(lanes.head, mask))
        )).map(_ => Step.Advance)
      case Instruction.TensorMemoryLoad(movement, destinations, address, reduction) =>
        warps(context)((lane, base) =>
          load(context, effects, base, movement, destinations, context.read(lane, address), reduction)
        )
      case Instruction.TensorMemoryStore(movement, sources, address) =>
        warps(context)((lane, base) => store(context, base, movement, sources, context.read(lane, address)))
      case Instruction.TensorMemoryCopy(operation, address, descriptor) =>
        LaneFaults.everyLane(context)(lane =>
          copy(context, operation, context.read(lane, address), context.read(lane, descriptor)).leftMap(located(
            context,
            lane
          ))
        ).map(_ => Step.Advance)
      case Instruction.TensorMemoryShift(address, pair) =>
        LaneFaults.everyLane(context)(lane =>
          shift(context, context.read(lane, address), pair).leftMap(located(context, lane))
        )
          .map(_ => Step.Advance)
      case Instruction.TensorMemoryCommit(barrier, mask) =>
        LaneFaults.everyLane(context)(lane =>
          commit(context, context.read(lane, barrier), mask.map(context.read(lane, _)))
        )
          .map(_ => Step.Advance)
    }

  /** The active lanes of each warp with any, for operations a warp issues once on its uniform datapath. */
  private def issuers(context: VMContext): Vec[Vec[WorkgroupLane]] =
    WorkgroupLane.warpBases.filter(base => base < context.workgroup.laneCount).toVec.flatMap { base =>
      val bits = context.active.warpBits(base)
      Option.when(bits != 0L)(
        Vec.from((0 until 32).collect {
          case lane if (bits & (1L << lane)) != 0L =>
            WorkgroupLane.fromIndex(base + lane)
        })
      )
    }

  /** Runs `function` once per participating warp, with its first lane and its base. */
  private inline def warps(context: VMContext)(inline function: (
      WorkgroupLane,
      WorkgroupLane.WarpBase
  ) => Either[Fault, Unit])
      : Either[Fault, Step] =
    VMTensor.participating(context).flatMap(_.traverse_(base => function(WorkgroupLane.fromIndex(base), base)))
      .map(_ => Step.Advance)

  /** The absolute lanes and columns of the registers of the threads of the warp at `base` that move `movement` at
    * `address`, the column of half `half` where two 16-bit columns pack into a register.
    */
  private class Cells(base: WorkgroupLane.WarpBase, movement: Movement, address: Long) {
    private val partition = (base / 32) % 4

    private def from(thread: Int): Long =
      if (movement.shape == DataShape.Lanes16Bits32x2 && thread >= 16) address + movement.split else address

    def lane(thread: Int, register: Int): Int = TensorMemory.lane(from(thread)) + movement.shape.lane(thread, register)

    def column(thread: Int, register: Int, half: Int): Int = {
      val logical = movement.shape.column(thread, register)
      TensorMemory.column(from(thread)) + (if (movement.packed) 2 * logical + half else logical)
    }

    /** Why register `register` of thread `thread` falls outside the warp's lanes or the tensor memory, if it does. */
    def fault(thread: Int, register: Int): Option[Fault] = {
      val row  = lane(thread, register)
      val last = column(thread, register, if (movement.packed) 1 else 0)
      if (row / 32 != partition)
        Some(Fault.Backend(s"warp ${base / 32} cannot reach tensor memory lane $row, outside lanes ${32 *
            partition} to ${32 * partition + 31}"))
      else if (column(thread, register, 0) >= Columns)
        Some(Fault.Backend(s"tensor memory column ${column(thread, register, 0)} is out of range"))
      else if (last >= Columns) Some(Fault.Backend(s"tensor memory column $last is out of range"))
      else None
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def load(
      context: VMContext,
      effects: VMWriteBuffer,
      base: WorkgroupLane.WarpBase,
      movement: Movement,
      destinations: Vec[Register],
      address: Long,
      reduction: Option[Reduction]
  ): Either[Fault, Unit] = {
    val state = context.workgroup.tensorMemory
    val cells = Cells(base, movement, address)
    WarpLane.values.filter(thread => movement.threads.includes(thread)).traverse_ { thread =>
      val target               = WorkgroupLane.inWarp(base, thread)
      val loaded               = Array.ofDim[Long](destinations.size)
      var fault: Option[Fault] = None
      var register             = 0
      while (fault.isEmpty && register < destinations.size) {
        fault = cells.fault(thread, register)
        if (fault.isEmpty) {
          val lane  = cells.lane(thread, register)
          val value =
            if (movement.packed)
              (state.read(lane, cells.column(thread, register, 0)) & 0xffffL) |
                ((state.read(lane, cells.column(thread, register, 1)) & 0xffffL) << 16)
            else state.read(lane, cells.column(thread, register, 0))
          effects.stageRegister(target, destinations(register), value)
          loaded(register) = value
        }
        register += 1
      }
      fault.toLeft(reduction.foreach(reduced =>
        effects.stageRegister(target, reduced.destination, reduce(reduced, loaded))
      ))
    }
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def combineAll(values: Array[Long])(inline combine: (Long, Long) => Long): Long = {
    var result = values(0)
    var index  = 1
    while (index < values.length) {
      result = combine(result, values(index))
      index += 1
    }
    result
  }

  private def reduce(reduction: Reduction, values: Array[Long]): Long = reduction.kind match {
    case ReductionKind.Float(absolute, propagate) =>
      combineAll(values)((left, right) =>
        VMArithmetic.extremum(left, right, 32, reduction.maximum, propagate, absolute, xorSign = false)
      )
    case ReductionKind.Signed =>
      if (reduction.maximum)
        combineAll(values)((left, right) =>
          math.max(Bits.signExtend(left, 32), Bits.signExtend(right, 32)) & 0xffffffffL
        )
      else
        combineAll(values)((left, right) =>
          math.min(Bits.signExtend(left, 32), Bits.signExtend(right, 32)) & 0xffffffffL
        )
    case ReductionKind.Unsigned =>
      if (reduction.maximum) combineAll(values)(math.max(_, _)) else combineAll(values)(math.min(_, _))
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def store(
      context: VMContext,
      base: WorkgroupLane.WarpBase,
      movement: Movement,
      sources: Vec[Value],
      address: Long
  ): Either[Fault, Unit] = {
    val state = context.workgroup.tensorMemory
    val cells = Cells(base, movement, address)
    WarpLane.values.filter(thread => movement.threads.includes(thread)).traverse_ { thread =>
      val source               = WorkgroupLane.inWarp(base, thread)
      var fault: Option[Fault] = None
      var register             = 0
      while (fault.isEmpty && register < sources.size) {
        val bits = context.read(source, sources(register))
        fault = cells.fault(thread, register)
        if (fault.isEmpty) {
          val lane = cells.lane(thread, register)
          if (movement.packed) {
            state.write(lane, cells.column(thread, register, 0), bits & 0xffffL)
            state.write(lane, cells.column(thread, register, 1), (bits >>> 16) & 0xffffL)
          } else state.write(lane, cells.column(thread, register, 0), bits)
        }
        register += 1
      }
      fault.toLeft(())
    }
  }

  private def copy(context: VMContext, operation: Copy, address: Long, descriptor: Long): Either[Fault, Unit] =
    for {
      layout  <- SharedDescriptor.decode(descriptor).leftMap(Fault.Backend(_))
      targets <- ctas(context, operation.pair)
      _       <- Either.cond(
        lane(address) + operation.replication.lanes(operation.shape.rows - 1).max < Lanes &&
          column(address) + operation.shape.bytes / 4 <= Columns,
        (),
        Fault.Backend(s"tcgen05.cp at 0x${address.toHexString} falls outside tensor memory")
      )
    } yield targets.foreach { owner =>
      (0 until operation.shape.rows).foreach { row =>
        (0 until operation.shape.bytes / 16).foreach { part =>
          val bytes = operation.packing.unpack(chunk(owner, layout.major(row, part)))
          operation.replication.lanes(row).foreach { offset =>
            (0 until 4).foreach { word =>
              owner.tensorMemory.write(
                lane(address) + offset,
                column(address) + 4 * part + word,
                LittleEndian.decode(bytes, 4 * word, 4)
              )
            }
          }
        }
      }
    }

  /** Moves 31 rows of eight columns down by one row, as tcgen05.shift and `.ashift` do. */
  private def shiftRows(state: State, firstLane: Int, firstColumn: Int, rows: Int, columns: Int): Unit =
    (rows - 2 to 0 by -1).foreach { row =>
      (0 until columns).foreach(offset =>
        state.write(firstLane + row + 1, firstColumn + offset, state.read(firstLane + row, firstColumn + offset))
      )
    }

  private def shift(context: VMContext, address: Long, pair: Boolean): Either[Fault, Unit] =
    if (lane(address) % 32 != 0 || column(address) + 8 > Columns)
      Left(Fault.Backend(s"tcgen05.shift at 0x${address.toHexString} needs a lane aligned to 32"))
    else
      ctas(context, pair).map(_.foreach(owner =>
        shiftRows(owner.tensorMemory, lane(address), column(address), rows = 32, columns = 8)
      ))

  private def commit(context: VMContext, barrier: Long, mask: Option[Long]): Either[Fault, Unit] = mask match {
    case None => VMAsync.mbarrier(context, barrier).flatMap(_.arrive(count = 1L, expectedBytes = 0L)).map(_ => ())
    case Some(ranks) =>
      (0 until 16).filter(rank => (ranks & (1L << rank)) != 0L).toVec.traverse_ { rank =>
        VMAsync.mbarrier(
          context,
          VMAddressSpace.clusterAddress(barrier, rank)
        ).flatMap(_.arrive(count = 1L, expectedBytes = 0L)).map(_ => ())
      }
  }

  /** The operands of one tcgen05.mma gathered from shared and tensor memory, positioned at an element of D by [[at]]: A
    * row by row, B column by column, C and the scale factors as D's placement reads them.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  class Gathered(
      val product: Product,
      left: Array[Long],
      right: Array[Long],
      addend: Array[Long],
      scalesA: Array[Long],
      scalesB: Array[Long],
      selections: Array[Long],
      val enabled: Long
  ) extends TensorMemoryOperands[Long] {
    private val terms  = left.length / product.m
    private val n      = product.n
    private val k      = product.k
    private val slots  = Array.ofDim[Long](TensorSemantics.Slots)
    private val vector = product.multiply.scaling.fold(1)(_.vector.raw)
    private var row    = 0
    private var column = 0

    def at(index: Int): Unit = {
      row = index / n
      column = index % n
    }

    def a(index: Int): Long                              = left(row * terms + index)
    def b(index: Int): Long                              = right(column * k + index)
    def c: Long                                          = addend(row * n + column)
    def selection(index: Int): Long                      = selections(row * terms + index)
    def scaleA(block: Int): Long                         = scalesA(row * vector + block)
    def scaleB(block: Int): Long                         = scalesB((row * n + column) * vector + block)
    def bind(name: String, value: Long): Long            = value
    def hold(slot: Int, name: String, value: Long): Long = {
      slots(slot) = value
      value
    }
    def held(slot: Int): Long = slots(slot)
  }

  /** tcgen05.mma for each issuing thread: each element of D is `element` of its operands. */
  inline def multiply(context: VMContext, operation: Multiply)(
      inline element: (Product, TensorMemoryOperands[Long]) => Long
  ): Either[Fault, Unit] =
    LaneFaults.everyLane(context) { lane =>
      gather(context, lane, operation).map { operands =>
        val results = Array.tabulate(operands.product.m * operands.product.n) { index =>
          operands.at(index)
          element(operands.product, operands)
        }
        scatter(context, lane, operation, operands.product, results)
      }.flatten.leftMap(located(context, lane))
    }

  /** `count` words of tensor memory, word `index` at lane `laneAt(index)` and column `columnAt(index)` of
    * `owner(index)` and passed through `value`, or the first that falls outside the tensor memory.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private inline def tensorWords(count: Int)(
      inline owner: Int => VMWorkgroup,
      inline laneAt: Int => Int,
      inline columnAt: Int => Int
  )(inline value: (Int, Long) => Long): Either[Fault, Array[Long]] = {
    val words  = Array.ofDim[Long](count)
    var failed = -1
    var index  = 0
    while (failed < 0 && index < count) {
      val laneIndex   = laneAt(index)
      val columnIndex = columnAt(index)
      if (laneIndex >= 0 && laneIndex < Lanes && columnIndex >= 0 && columnIndex < Columns)
        words(index) = value(index, owner(index).tensorMemory.read(laneIndex, columnIndex))
      else failed = index
      index += 1
    }
    if (failed < 0) Right(words)
    else Left(Fault.Backend(s"tensor memory lane ${laneAt(failed)} column ${columnAt(failed)} is out of range"))
  }

  /** The encoding of element `index` of a row or column held in containers of `bits` bits from `word` on, signed
    * integers sign-extended.
    */
  private def code(element: Element, container: Long): Long = element match {
    case Element.S8  => Bits.signExtend(container, 8)
    case Element.S32 => Bits.signExtend(container, 32)
    case other       => other.code(container)
  }

  private def packing(element: Element): Packing = element match {
    case Element.E2M3 | Element.E3M2 => Packing.Six
    case Element.E2M1Byte            => Packing.Four
    case _                           => Packing.Bytes
  }

  /** The elements of a matrix in shared memory, K-major unless `transposed`, each 16-byte chunk of 6- or 4-bit elements
    * unpacked once for the elements it holds.
    */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private class SharedMatrix(layout: SharedDescriptor, element: Element, transposed: Boolean) {
    private val bits                  = container(element)
    private val perChunk              = 128 / bits
    private val packed                = packing(element)
    private var unpacked: Array[Byte] = Array.emptyByteArray
    private var owner: AnyRef         = this
    private var at                    = -1L

    /** Element `k` of row or column `index` in `cta`'s shared memory. */
    def apply(cta: VMWorkgroup, index: Int, k: Int): Long = {
      val chunk = if (transposed) layout.minor(index / perChunk, k) else layout.major(index, k / perChunk)
      val slot  = if (transposed) index % perChunk else k % perChunk
      val raw   = packed match {
        case Packing.Bytes =>
          val first = chunk + slot * bits / 8
          if (bits == 4) (byte(cta, first) >>> (4 * (slot % 2))) & 0xfL
          else LittleEndian.decode((bits / 8).unsafe[Int in 0 ~ 8])(offset => byte(cta, first + offset).toByte)
        case other =>
          if ((owner ne cta) || at != chunk) {
            unpacked = other.unpack(VMTensorMemory.chunk(cta, chunk))
            owner = cta
            at = chunk
          }
          unpacked(slot) & 0xffL
      }
      code(element, raw)
    }
  }

  private def gather(context: VMContext, lane: WorkgroupLane, operation: Multiply): Either[Fault, Gathered] =
    for {
      product <- TensorMemory.product(operation, context.read(lane, operation.descriptor)).leftMap(Fault.Backend(_))
      pair    <- ctas(context, operation.pair)
      _       <- Either.cond(
        !product.sparse || product.m == 256 || (product.m == 128 && !operation.pair),
        (),
        Fault.Backend("sparse tcgen05.mma is implemented for the four-partition datapath layouts only")
      )
      bLayout <- SharedDescriptor.decode(context.read(lane, operation.b)).leftMap(Fault.Backend(_))
      m        = product.m
      n        = product.n
      k        = product.k
      packed   = if (product.sparse) k / 2 else k
      multiply = product.multiply
      half     = if (operation.pair) 2 else 1
      place    = TensorMemory.placement(operation, product)
      dAddress = context.read(lane, operation.d)
      zero <- zeroColumns(context, lane, operation, product)
      bMatrix = SharedMatrix(bLayout, multiply.b.element, product.transposeB)
      right   = Array.tabulate(n * k) { index =>
        val column = index / k
        if (zero(column)) 0L else bMatrix(pair(column / (n / half)), column % (n / half) + zero.shift, index % k)
      }
      left <-
        if (operation.aInTensorMemory) {
          val aAddress = context.read(lane, operation.a)
          val bits     = container(multiply.a.element)
          tensorWords(m * packed)(
            index => pair(place.cta(index / packed)),
            index => TensorMemory.lane(aAddress) + place.lane(index / packed, 0),
            index => column(aAddress) + index % packed * bits / 32
          )((index, word) => code(multiply.a.element, word >>> (index % packed * bits % 32)))
        } else
          SharedDescriptor.decode(context.read(lane, operation.a)).leftMap(Fault.Backend(_)).map { aLayout =>
            val aMatrix = SharedMatrix(aLayout, multiply.a.element, product.transposeA)
            Array.tabulate(m * packed) { index =>
              val row = index / packed
              aMatrix(pair(row / (m / half)), row % (m / half), index % packed)
            }
          }
      enabled = context.read(lane, operation.enable) & 1L
      addend <-
        if (enabled == 0L) Right(Array.ofDim[Long](m * n))
        else
          tensorWords(m * n)(
            index => pair(place.cta(index / n)),
            index => TensorMemory.lane(dAddress) + place.lane(index / n, index % n),
            index => column(dAddress) + place.column(index % n)
          )((_, word) => code(multiply.c.element, word))
      scales <- operation.scales.fold(Right((Array.emptyLongArray, Array.emptyLongArray)))(addresses =>
        blockScales(context, lane, addresses, product, pair, place)
      )
      selections <- operation.metadata.filter(_ => product.sparse).fold(Right(Array.emptyLongArray))(address =>
        sparsity(context.read(lane, address), product, pair, place)
      )
    } yield {
      if (operation.shiftA) {
        val aAddress = context.read(lane, operation.a)
        pair.foreach(owner =>
          shiftRows(
            owner.tensorMemory,
            TensorMemory.lane(aAddress),
            column(aAddress),
            m / half,
            k * container(multiply.a.element) / 32
          )
        )
      }
      Gathered(product, left, right, addend, scales._1, scales._2, selections, enabled)
    }

  /** The B columns a `.ws` product's zero-column mask descriptor zeroes, and the shift of the columns it reads. */
  private class ZeroColumns(columns: Array[Boolean], val shift: Int) {
    def apply(column: Int): Boolean = columns(column)
  }

  private def zeroColumns(context: VMContext, lane: WorkgroupLane, operation: Multiply, product: Product)
      : Either[Fault, ZeroColumns] = operation.zeroColumns match {
    case None        => Right(ZeroColumns(Array.fill(product.n)(false), 0))
    case Some(value) =>
      val bits  = context.read(lane, value)
      val shift = ((bits >>> 56) & 0x3fL).toInt
      if (!Bits.isSet(bits, 39)) Right(ZeroColumns(Array.fill(product.n)(false), shift))
      else {
        val masks  = if (product.m == 128) 1 else if (product.m == 64) 2 else 4
        val width  = product.n / masks
        val skip   = ((bits >>> 40) & 0xffL).toInt + 1
        val use    = ((bits >>> 48) & 0xffL).toInt + 1
        val zeroed = Array.tabulate(product.n) { column =>
          val mask  = column / width
          val start = ((bits >>> (8 * mask)) & 0xffL).toInt
          val first = Bits.isSet(bits, 32 + mask)
          // runs of zeroed columns are skip long and of read ones use long, the first shortened by the start count
          @annotation.tailrec
          def at(position: Int, value: Boolean, run: Int): Boolean =
            if (position < run) value else at(position - run, !value, if (!value) skip else use)
          at(column % width, first, (if (first) skip else use) - start)
        }
        Right(ZeroColumns(zeroed, shift))
      }
  }

  /** The 2-bit index that places each packed element of each row of a sparse A, from the metadata at `address`: rows of
    * 16- and 32-bit elements interleave by eight across the lanes of their partition, a column per sparsity selector,
    * and narrower ones take a lane each over two columns.
    */
  private def sparsity(address: Long, product: Product, pair: Vec[VMWorkgroup], place: TensorMemory.Placement)
      : Either[Fault, Array[Long]] = {
    val element                 = product.multiply.a.element
    val terms                   = product.k / 2
    val wide                    = element.bits >= 16
    val perChunk                = packedPerChunk(element).raw
    def chunk(index: Int): Int  = index                        % terms / perChunk
    def local(index: Int): Int  = place.lane(index / terms, 0) % 32
    def offset(index: Int): Int =
      if (wide) (local(index) / 16) * 16 + (chunk(index) / 4) * 8 + local(index) % 8 else local(index)
    def nibble(index: Int): Int = if (wide) (local(index) % 16 / 8) * 4 + chunk(index) % 4 else chunk(index) % 8
    tensorWords(product.m * terms)(
      index => pair(place.cta(index / terms)),
      index => TensorMemory.lane(address) + 32 * (place.lane(index / terms, 0) / 32) + offset(index),
      index => column(address) + (if (wide) product.selector else chunk(index) / 8)
    ) { (index, bits) =>
      val metadata = (bits >>> (4 * nibble(index))) & 0xfL
      val slot     = index % terms % perChunk
      MatrixOperands.sparseSelection(element, metadata, slot)
    }
  }

  private def blockScales(
      context: VMContext,
      lane: WorkgroupLane,
      addresses: (a: Value, b: Value),
      product: Product,
      pair: Vec[VMWorkgroup],
      place: TensorMemory.Placement
  ): Either[Fault, (Array[Long], Array[Long])] = {
    val vector                                       = product.multiply.scaling.fold(1)(_.vector.raw)
    val n                                            = product.n
    val aAddress                                     = context.read(lane, addresses.a)
    val bAddress                                     = context.read(lane, addresses.b)
    def scale(word: Long, id: Int, block: Int): Long = (word >>> (8 * ((id + block) % 4))) & 0xffL
    for {
      a <- tensorWords(product.m * vector)(
        index => pair(place.cta(index / vector)),
        index => TensorMemory.lane(aAddress) + 32 * (place.lane(index / vector, 0) / 32) + index / vector % 128 % 32,
        index => column(aAddress) + index / vector                                                        % 128 / 32
      )((index, word) => scale(word, product.scaleIds.a, index % vector))
      b <- tensorWords(product.m * n * vector)(
        index => pair(place.cta(index / vector / n)),
        index =>
          TensorMemory.lane(bAddress) + 32 * (place.lane(index / vector / n, index / vector % n) / 32) +
            index / vector                         % n % 32,
        index => column(bAddress) + index / vector % n / 32
      )((index, word) => scale(word, product.scaleIds.b, index % vector))
    } yield (a, b)
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def scatter(
      context: VMContext,
      lane: WorkgroupLane,
      operation: Multiply,
      product: Product,
      results: Array[Long]
  ): Either[Fault, Unit] = {
    val dAddress = context.read(lane, operation.d)
    val masks    = operation.disabled.map(context.read(lane, _))
    val place    = TensorMemory.placement(operation, product)
    val bits     = if (product.multiply.d.element == Element.F16) 0xffffL else 0xffffffffL
    val n        = product.n
    ctas(context, operation.pair).flatMap { pair =>
      var fault: Option[Fault] = None
      var index                = 0
      while (fault.isEmpty && index < results.length) {
        val row      = index / n
        val cta      = place.cta(row)
        val target   = TensorMemory.lane(dAddress) + place.lane(row, index % n)
        val word     = cta * 4 + target / 32
        val disabled = masks.nonEmpty && word < masks.size && Bits.isSet(masks(word), target % 32)
        val written  = column(dAddress) + place.column(index % n)
        if (target >= Lanes || written >= Columns)
          fault = Some(Fault.Backend(s"tcgen05.mma writes tensor memory lane $target column $written, out of range"))
        else if (!disabled) pair(cta).tensorMemory.write(target, written, results(index) & bits)
        index += 1
      }
      fault.toLeft(())
    }
  }
}
