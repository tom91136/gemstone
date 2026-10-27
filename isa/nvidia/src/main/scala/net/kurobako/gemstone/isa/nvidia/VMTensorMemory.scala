package net.kurobako.gemstone.isa.nvidia

import TensorMemory.*
import VMExecution.*
import VMIR.*
import VMIR.Matrix.*
import VMIR.Tcgen05.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** The tensor memory state behind the machine semantics of tcgen05.mma: a product's operands gathered from shared and
  * tensor memory, and where its elements go. Products complete as they issue, so tcgen05.wait and the tcgen05 fences
  * order nothing further, and run once for each issuing thread.
  */
@SuppressWarnings(Array("scalafix:DisableSyntax.null"))
private object VMTensorMemory {

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

  def chunk(owner: VMWorkgroup, offset: Long): Array[Byte] =
    Array.tabulate(16)(index => byte(owner, offset + index).toByte)

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
      val enabled: Long,
      target: Target
  ) extends TensorMemoryOperands[Long] {
    private val terms  = left.length / product.m
    private val n      = product.n
    private val k      = product.k
    private val slots  = Array.ofDim[Long](TensorSemantics.Slots)
    private val vector = product.multiply.scaling.fold(1)(_.vector.raw)
    private var row    = 0
    private var column = 0

    def size: Int = product.m * n

    def at(index: Int): Unit = {
      row = index / n
      column = index % n
    }

    def store(index: Int, value: Long): Either[Fault, Unit] = target.store(index, value)

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

  def gather(context: VMContext, lane: WorkgroupLane, operation: Multiply): Either[Fault, Gathered] =
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
      Gathered(
        product,
        left,
        right,
        addend,
        scales._1,
        scales._2,
        selections,
        enabled,
        Target(
          pair,
          TensorMemory.placement(operation, product),
          context.read(lane, operation.d),
          operation.disabled.map(context.read(lane, _)),
          product.n,
          if (product.multiply.d.element == Element.F16) 0xffffL else 0xffffffffL
        )
      )
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

  /** Where the elements of D go: the CTAs of the pair, the datapath layout and D's address, the lanes of the tensor
    * memory a disabled-lane mask excludes, D's row width and the bits of an element.
    */
  class Target(
      pair: Vec[VMWorkgroup],
      place: TensorMemory.Placement,
      address: Long,
      masks: Vec[Long],
      n: Int,
      bits: Long
  ) {

    /** Element `index` of D, row by row, stored unless its lane is disabled; faults where it falls outside. */
    def store(index: Int, value: Long): Either[Fault, Unit] = {
      val row     = index / n
      val cta     = place.cta(row)
      val target  = TensorMemory.lane(address) + place.lane(row, index % n)
      val written = column(address) + place.column(index % n)
      val word    = cta * 4 + target / 32
      if (target >= Lanes || written >= Columns)
        Left(Fault.Backend(s"tcgen05.mma writes tensor memory lane $target column $written, out of range"))
      else {
        if (!(masks.nonEmpty && word < masks.size && Bits.isSet(masks(word), target % 32)))
          pair(cta).tensorMemory.write(target, written, value & bits)
        Completed
      }
    }
  }
}
