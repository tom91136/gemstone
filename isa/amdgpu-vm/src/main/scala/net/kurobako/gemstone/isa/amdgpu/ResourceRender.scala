package net.kurobako.gemstone.isa.amdgpu

import IntegerSemantics.*
import VMIR.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*
import net.kurobako.gemstone.semantics.render.*
import scala.collection.mutable

/** Renders buffer and image accesses for one lane as execution makes them. Descriptor dwords are the operands they are
  * read from; the addresses, range checks and texel conversions are the [[BufferResource]], [[ImageResource]] and
  * [[TexelFormat]] functions execution runs; texels move through global or private memory, and an image's texel address
  * is a machine operation of [[Images]]. An access out of range reads zero and writes nothing. What an access reads is
  * bound before it writes registers, which may be the ones it read.
  */
private class ResourceRender(naming: RenderContext.Names, isa: ISA) {
  import RenderContext.*
  import Term.*

  private val generation = ResourceGeneration.of(isa)

  private val Dword = Type.unsigned(32)
  private val Pair  = Type.unsigned(64)
  private val Skip  = Constant("()")

  private val Images       = ("net.kurobako.gemstone.isa.amdgpu.Images", "images")
  private val Architecture = Constant(s"net.kurobako.gemstone.isa.amdgpu.ISA.$isa")

  /** The steps of one access in order, each temporary bound under its name and kept a binding of its own, before the
    * registers it reads may be written.
    */
  private class Bindings {
    private val built = mutable.ArrayBuffer.empty[Term.Step]

    def bind(name: String, value: Term): Term = {
      naming.keep(name)
      built += Term.Step(name, value)
      Variable(name)
    }

    def +=(step: Term.Step): Unit = built += step

    def ++=(steps: Term.Steps): Unit = built ++= steps.all

    def result: Option[Term.Steps] = Some(Term.Steps(Vec.from(built)))
  }

  private def loadSized(space: Space, at: Term, bytes: Term): Term = Term.context(
    (space.context, space.name),
    "load",
    Vec.empty,
    Vec((at, Some(space.address)), (bytes, Some(Dword))),
    Some(Pair)
  )

  private def storeSized(space: Space, at: Term, bytes: Term, value: Term): Term = Term.context(
    (space.context, space.name),
    "store",
    Vec.empty,
    Vec((at, Some(space.address)), (bytes, Some(Dword)), (value, Some(Pair))),
    None,
    effect = true
  )

  private def at(address: Term, offset: Int): Term = if (offset == 0) address else Render.add(address, Literal(offset))

  private def when(condition: Term, effect: Term): Term.Step = Term.Step("_", Select(condition, effect, Skip))

  private def scalarElements(steps: Bindings, words: Vec[Term], start: Term, size: Int, count: Int): Term = {
    val fixed = generation
    steps.bind(
      "elements",
      Render.primitive(words(1), words(2), words(3), start)(
        (word1: UInt[32], word2: UInt[32], word3: UInt[32], start: UInt[32]) =>
          BufferResource.scalarElements(fixed, word1, word2, word3, start, size = size, count = count)
      )
    )
  }

  private def descriptor(first: ScalarRegisterIndex, count: Int): Vec[Term] =
    Vec.tabulate(count)(index => naming.scalar(first.offsetBy(index.unsafe[Int >= 0])))

  private def register(bank: RegisterBank, first: VectorRegisterIndex, offset: Int): Term = {
    val index = first.offsetBy(offset.unsafe[Int >= 0])
    bank match {
      case RegisterBank.Vector      => naming.vector(index)
      case RegisterBank.Accumulator => naming.accumulator(index)
    }
  }

  /** Where a lane's access lands: private memory at `scratch`'s address where its condition holds and global memory at
    * `address` where its negation does, where `valid` holds of the bytes from an offset, or anywhere unless `checked`.
    */
  private class Located(
      val scratch: Option[(condition: Term, other: Term, address: Term)],
      val address: Term,
      val valid: IndexedTerm,
      val checked: Boolean = true
  ) {

    /** `bytes`, one to eight, from `offset`. */
    def read(offset: Int, bytes: Int): Term = {
      val loaded = load(Space.Global, at(address, offset), bytes)
      val global = if (checked) Select(valid(offset), loaded, Literal(0L)) else loaded
      scratch.fold(global)(found =>
        Select(found.condition, load(Space.Private, at(found.address, offset), bytes), global)
      )
    }

    def write(offset: Int, bytes: Int, value: Term): Term.Steps =
      if (!checked) Term.Steps(Term.Step("_", store(Space.Global, at(address, offset), value, bytes)))
      else
        Term.Steps(scratch.map(found =>
          when(found.condition, store(Space.Private, at(found.address, offset), value, bytes))
        ).toVec) :+
          when(
            scratch.fold(valid(offset))(found => Render.and(found.other, valid(offset))),
            store(Space.Global, at(address, offset), value, bytes)
          )

    /** A texel of `bytes`, a term, as its low and high 64 bits, where `whole` holds of one in range. */
    def texel(steps: Bindings, bytes: Term, whole: Term): (Term, Term) = {
      val wide                                   = Render.lessThan(Literal(8L), bytes, signed = false)
      def low(space: Space, address: Term): Term =
        loadSized(space, address, Render.minimum(bytes, Literal(8L), signed = false))
      def high(space: Space, address: Term): Term =
        loadSized(space, at(address, 8), Render.subtract(bytes, Literal(8L)))
      def either(scratched: Term => Term, global: Term): Term =
        scratch.fold(global)(found => Select(found.condition, scratched(found.address), global))
      (
        steps.bind("low", either(low(Space.Private, _), Select(whole, low(Space.Global, address), Literal(0L)))),
        steps.bind(
          "high",
          either(
            found => Select(wide, high(Space.Private, found), Literal(0L)),
            Select(Render.and(whole, wide), high(Space.Global, address), Literal(0L))
          )
        )
      )
    }

    /** A texel of a fixed number of bytes as its low and high 64 bits. */
    def texel(steps: Bindings, bytes: Int): (Term, Term) = bytes match {
      case 12 => (steps.bind("low", read(offset = 0, bytes = 8)), steps.bind("high", read(offset = 8, bytes = 4)))
      case 16 => (steps.bind("low", read(offset = 0, bytes = 8)), steps.bind("high", read(offset = 8, bytes = 8)))
      case _  => (steps.bind("low", read(0, bytes)), Literal(0L))
    }

    /** Writes a texel of `bytes`, a term, from its low and high 64 bits where `whole` holds. */
    def put(bytes: Term, whole: Term, low: Term, high: Term): Term.Steps = {
      val wide = Render.lessThan(Literal(8L), bytes, signed = false)
      def parts(space: Space, address: Term, condition: Term): Term.Steps = Term.Steps(
        when(condition, storeSized(space, address, Render.minimum(bytes, Literal(8L), signed = false), low)),
        when(Render.and(condition, wide), storeSized(space, at(address, 8), Render.subtract(bytes, Literal(8L)), high))
      )
      scratch.fold(parts(Space.Global, address, whole))(found =>
        parts(Space.Private, found.address, found.condition) ++
          parts(Space.Global, address, Render.and(found.other, whole))
      )
    }
  }

  /** A lane's buffer access as execution locates it, its temporaries bound in `steps`: through a null descriptor,
    * wave-interleaved private memory.
    */
  private class Buffered(address: MemoryAddress.Buffer, steps: Bindings) {
    val words: Vec[Term] = descriptor(address.resource, 4)
    private val fixed    = generation
    private val indexed  = address.index.nonEmpty
    private val start    = address.vector.fold(Literal(address.offset.toLong))(vector =>
      if (address.offset == 0) naming.vector(vector)
      else Render.displaced(naming.vector(vector), Literal(address.offset.toLong))
    )
    private val added = (address.scalar, address.scalarConstant) match {
      case (Some(scalar), 0)        => naming.scalar(scalar)
      case (None, constant)         => Literal(constant.toLong & 0xffffffffL)
      case (Some(scalar), constant) =>
        Render.truncate(Render.add(naming.scalar(scalar), Literal(constant.toLong)), 32)
    }
    private val within = Render.truncate(start, 32)
    private val base   = steps.bind("base", Render.primitive(words(0), words(1))(BufferResource.base))
    private val index  = steps.bind(
      "index",
      Render.primitive(words(3), address.index.fold(Literal(0L))(naming.vector(_)), Lane)(
        (word3: UInt[32], vector: UInt[32], lane: UInt[32]) =>
          BufferResource.index(fixed, word3, indexed = indexed, vector, lane)
      )
    )

    /** The bytes in range of an access of `bytes` checked in `component`-byte parts. */
    def valid(bytes: Int, component: Int): Term = {
      val part = math.min(component, bytes)
      steps.bind(
        "inRange",
        Render.primitive(words(1), words(2), words(3), index, within, added)(
          (
              word1: UInt[32],
              word2: UInt[32],
              word3: UInt[32],
              index: UInt[64],
              offset: UInt[32],
              scalarOffset: UInt[32]
          ) =>
            BufferResource.valid(
              fixed,
              word1,
              word2,
              word3,
              indexed = indexed,
              index,
              offset,
              scalarOffset,
              bytes = bytes,
              component = part
            )
        )
      )
    }

    /** Whether a whole texel of the format the descriptor names is in range. */
    def texelValid: Term = steps.bind(
      "inRange",
      Render.primitive(words(1), words(2), words(3), index, within, added)(
        (
            word1: UInt[32],
            word2: UInt[32],
            word3: UInt[32],
            index: UInt[64],
            offset: UInt[32],
            scalarOffset: UInt[32]
        ) =>
          BufferResource.texelValid(fixed, word1, word2, word3, indexed = indexed, index, offset, scalarOffset)
      )
    )

    /** Where it lands, the bytes from an offset in range where `valid` holds of it. */
    def located(valid: IndexedTerm): Located = Located(
      Some((
        Render.equal(base, Literal(0L)),
        Render.notEqual(base, Literal(0L)),
        steps.bind(
          "private",
          Render.interleavedAddress(start, added, Literal(naming.waveSize.toLong))
        )
      )),
      steps.bind(
        "address",
        Render.add(
          Render.add(base, added),
          Render.primitive(words(1), words(3), index, within)(
            (word1: UInt[32], word3: UInt[32], index: UInt[64], offset: UInt[32]) =>
              BufferResource.bufferOffset(fixed, word1, word3, indexed = indexed, index, offset)
          )
        )
      ),
      valid
    )

    /** Where an access of `bytes` checked in `component`-byte parts lands; an ADDR64 access is not checked. */
    def located(bytes: Int, component: Int): Located =
      if (address.wide) {
        // the address names its VGPRs as the operand's first register and the one after
        val pair = address.vector.fold(Literal(0L))(first =>
          Render.or(naming.vector(first), Render.shiftLeft(naming.vector(first.offsetBy(1)), Literal(32L)))
        )
        val wide = steps.bind(
          "address",
          at(Render.primitive(words(0), words(1), pair, added)(BufferResource.wideAddress), address.offset)
        )
        Located(None, wide, _ => Literal(1L), checked = false)
      } else {
        val inRange = valid(bytes, component)
        located(offset => Render.lessThan(Literal(offset.toLong), inRange, signed = false))
      }
  }

  /** Where a lane's image access lands: the texel's address from [[Images]]. */
  private def texel(address: ImageAddress, steps: Bindings): Located = {
    val dimension =
      address.dimension.fold("None")(found => s"Some(net.kurobako.gemstone.isa.amdgpu.ImageDimension.$found)")
    val found = steps.bind(
      "texel",
      Term.context(
        Images,
        "texel",
        Vec.empty,
        Vec((Architecture, None)) ++ descriptor(address.resource, 8).map(word => (word, Some(Dword))) ++
          Vec.tabulate(5)(index => (coordinate(address, index), Some(Dword))) ++
          Vec((Constant(s"dimension = $dimension"), None), (Constant(s"mip = ${address.mip}"), None)),
        Some(Pair)
      )
    )
    Located(None, found, _ => Render.notEqual(found, Literal(-1L)))
  }

  /** Coordinate `index` of an image address, zero past its words. */
  private def coordinate(address: ImageAddress, index: Int): Term =
    if (index >= address.words.size * (if (address.a16) 2 else 1)) Literal(0L)
    else if (address.a16)
      Render.truncate(
        Render.shiftRightLogical(naming.vector(address.words(index / 2)), Literal(16L * (index % 2))),
        16
      )
    else naming.vector(address.words(index))

  /** The components into the registers from `first` as `packing` places them, in order, each over what the last left.
    */
  private def components(first: VectorRegisterIndex, values: Vec[Term], packing: Packing, bank: RegisterBank)
      : Term.Steps =
    Term.Steps(values.zipWithIndex.map { (value, index) =>
      packing match {
        case Packing.Dword | Packing.Unpacked =>
          Term.Step(naming.name(register(bank, first, index)), value)
        case Packing.Packed =>
          val target = register(bank, first, index / 2)
          Term.Step(naming.name(target), Render.mergeHalf(target, value, index % 2 == 1))
        case Packing.High =>
          Term.Step(
            naming.name(register(bank, first, 0)),
            Render.mergeHalf(register(bank, first, 0), value, high = true)
          )
      }
    })

  /** Component `index` of the registers from `first`, as a store reads it under `packing`. */
  private def stored(first: VectorRegisterIndex, index: Int, packing: Packing, bank: RegisterBank): Term =
    packing match {
      case Packing.Dword    => register(bank, first, index)
      case Packing.Unpacked => Render.truncate(register(bank, first, index), 16)
      case Packing.Packed   =>
        Render.truncate(
          Render.shiftRightLogical(
            register(bank, first, index / 2),
            Literal(16L * (index % 2))
          ),
          16
        )
      case Packing.High => Render.truncate(Render.shiftRightLogical(register(bank, first, 0), Literal(16L)), 16)
    }

  /** The bytes of a texel of the format a buffer descriptor's dword 3 names. */
  private def texelBytes(steps: Bindings, word3: Term): Term = {
    val fixed = generation
    steps.bind("bytes", Render.primitive(word3)((word3: UInt[32]) => BufferResource.texelBytes(fixed, word3)))
  }

  /** The accesses this renders: buffer loads, stores and atomics, typed buffer access, buffer loads into and stores
    * from LDS, scalar buffer loads and image access, and those with a TFE status.
    */
  def access(instruction: Instruction): Option[Term.Steps] = {
    val steps = Bindings()
    instruction match {
      case Instruction.Load(dest, address: MemoryAddress.Buffer, bytes, signed, placement, bank, _) =>
        val located = Buffered(address, steps).located(bytes, 4)
        if (bytes > 4) {
          val parts = Vec.tabulate(bytes / 4)(index => steps.bind(s"part$index", located.read(index * 4, 4)))
          parts.zipWithIndex.foreach((part, index) =>
            steps += Term.Step(naming.name(register(bank, dest, index)), part)
          )
        } else {
          val value = Render.loaded(
            steps.bind("data", located.read(0, bytes)),
            (bytes * 8).unsafe[Int in 1 ~ 64],
            signed
          )
          val target = register(bank, dest, 0)
          steps +=
            (placement match {
              case LoadPlacement.Full => Term.Step(naming.name(target), Render.truncate(value, 32))
              case LoadPlacement.Low  =>
                Term.Step(naming.name(target), Render.mergeHalf(target, value, high = false))
              case LoadPlacement.High =>
                Term.Step(naming.name(target), Render.mergeHalf(target, value, high = true))
              case LoadPlacement.LowZeroingHigh =>
                Term.Step(naming.name(target), Render.zeroedHalf(value, high = false))
              case LoadPlacement.HighZeroingLow =>
                Term.Step(naming.name(target), Render.zeroedHalf(value, high = true))
            })
        }
        steps.result
      case Instruction.Store(src, address: MemoryAddress.Buffer, bytes, high, bank) =>
        val located = Buffered(address, steps).located(bytes, 4)
        if (bytes > 4)
          (0 until bytes / 4).foreach(index => steps ++= located.write(index * 4, 4, register(bank, src, index)))
        else {
          val value = if (high) Render.high16(register(bank, src, 0)) else register(bank, src, 0)
          steps ++= located.write(0, bytes, Render.truncate(value, (bytes * 8).unsafe[Int in 1 ~ 64]))
        }
        steps.result
      case Instruction.FormatLoad(dest, address: MemoryAddress.Buffer, format, count, packing, bank) =>
        val buffered = Buffered(address, steps)
        val word3    = buffered.words(3)
        val half     = packing != Packing.Dword
        val fixed    = generation
        val values   = format match {
          case Some(named) =>
            val bytes       = math.max(named.layout.bytes, 1)
            val (low, high) = buffered.located(bytes, bytes).texel(steps, bytes)
            Vec.tabulate(count) { index =>
              val select =
                Render.primitive(word3)((word3: UInt[32]) => BufferResource.destination(word3, component = index))
              Render.primitive(select, low, high)((select: UInt[32], low: UInt[64], high: UInt[64]) =>
                TexelFormat.converted(named.layout, named.number, select, low, high, half = half)
              )
            }
          case None =>
            val bytes       = texelBytes(steps, word3)
            val whole       = buffered.texelValid
            val (low, high) = buffered.located(_ => whole).texel(steps, bytes, whole)
            Vec.tabulate(count)(index =>
              Render.primitive(word3, low, high)((word3: UInt[32], low: UInt[64], high: UInt[64]) =>
                BufferResource.component(fixed, word3, low, high, index = index, half = half)
              )
            )
        }
        steps ++= components(dest, values, packing, bank)
        steps.result
      case Instruction.FormatStore(src, address: MemoryAddress.Buffer, format, count, packing, bank) =>
        val fixed                                         = generation
        def read(index: Int, widened: Term => Term): Term =
          if (index >= count) Literal(0L)
          else if (packing == Packing.Dword) stored(src, index, packing, bank)
          else widened(stored(src, index, packing, bank))
        format match {
          case Some(named) if !named.valid => ()
          case Some(named)                 =>
            val buffered = Buffered(address, steps)
            val word3    = buffered.words(3)
            val bytes    = math.max(named.layout.bytes, 1)
            val values   = Vec.tabulate(4)(index =>
              read(index, Render.primitive(_)((value: UInt[16]) => TexelFormat.widened(named.number, value)))
            )
            def packed(part: Int): Term = Render.primitive(word3, values(0), values(1), values(2), values(3))(
              (word3: UInt[32], x: UInt[32], y: UInt[32], z: UInt[32], w: UInt[32]) =>
                TexelFormat.pack(named.layout, named.number, word3, x, y, z, w, part = part)
            )
            val located = buffered.located(bytes, bytes)
            if (bytes > 8)
              steps ++= located.write(offset = 0, bytes = 8, packed(0)) ++
                located.write(8, bytes - 8, Render.truncate(packed(1), ((bytes - 8) * 8).unsafe[Int in 1 ~ 64]))
            else steps ++= located.write(0, bytes, Render.truncate(packed(0), (bytes * 8).unsafe[Int in 1 ~ 64]))
          case None =>
            val buffered = Buffered(address, steps)
            val word3    = buffered.words(3)
            val values   = Vec.tabulate(4)(index =>
              read(
                index,
                half =>
                  Render.primitive(word3, half)((word3: UInt[32], value: UInt[16]) =>
                    BufferResource.widened(fixed, word3, value)
                  )
              )
            )
            def packed(part: Int): Term = Render.primitive(word3, values(0), values(1), values(2), values(3))(
              (word3: UInt[32], x: UInt[32], y: UInt[32], z: UInt[32], w: UInt[32]) =>
                BufferResource.packed(fixed, word3, x, y, z, w, part = part)
            )
            val formatted = Render.primitive(word3)((word3: UInt[32]) => BufferResource.formatted(fixed, word3))
            val bytes     = texelBytes(steps, word3)
            val whole     = Render.and(buffered.texelValid, formatted)
            val located   = buffered.located(_ => whole)
            steps ++= Located(
              located.scratch.map(found => (Render.and(found.condition, formatted), found.other, found.address)),
              located.address,
              located.valid
            ).put(bytes, whole, packed(0), packed(1))
        }
        steps.result
      case Instruction.LoadLocal(address: MemoryAddress.Buffer, bytes, _, offset) =>
        val buffered = Buffered(address, steps)
        val inRange  = buffered.valid(bytes, 4)
        val located  = buffered.located(_ => Render.lessThan(Literal(0L), inRange, signed = false))
        val target   = Render.ldsOffset(Render.add(
          Render.add(naming.m0, Literal(offset.toLong)),
          Render.multiply(Lane, Literal(math.max(4, bytes).toLong))
        ))
        if (bytes > 8)
          (0 until bytes / 4).foreach(index =>
            steps += Term.Step("_", store(Space.Local, at(target, index * 4), located.read(index * 4, 4), 4))
          )
        else steps += Term.Step("_", store(Space.Local, target, located.read(0, bytes), bytes))
        steps.result
      case Instruction.StoreLocal(address: MemoryAddress.Buffer, offset) =>
        val data = steps.bind(
          "data",
          load(
            Space.Local,
            Render.ldsOffset(Render.add(
              Render.add(naming.m0, Literal(offset.toLong)),
              Render.multiply(Lane, Literal(4L))
            )),
            4
          )
        )
        steps ++= Buffered(address, steps).located(bytes = 4, component = 4).write(offset = 0, bytes = 4, data)
        steps.result
      case Instruction.SBufferLoad(dest, count, resource, offset, scalarOffset, elementBytes, signed) =>
        val words  = descriptor(resource, 4)
        val within = Render.truncate(Render.add(Literal(offset), scalarOffset.fold(Literal(0L))(naming.scalar(_))), 32)
        val size   = elementBytes
        val start  = if (size == 4) Render.scalarDword(within) else within
        val total  = count
        val kept   = scalarElements(steps, words, start, size = size, count = total)
        val base   = steps.bind("address", Render.add(Render.primitive(words(0), words(1))(BufferResource.base), start))
        val parts  = Vec.tabulate(total) { index =>
          val element = load(Space.Global, at(base, index * size), size)
          val value   =
            if (signed) Render.truncate(Render.signExtend(element, (size * 8).unsafe[Int in 1 ~ 64]), 32) else element
          steps.bind(
            s"part$index",
            Select(Render.lessThan(Literal(index.toLong), kept, signed = false), value, Literal(0L))
          )
        }
        parts.zipWithIndex.foreach((part, index) =>
          steps += naming.scalarWrite(dest.offsetBy(index.unsafe[Int >= 0]))(part)
        )
        steps.result
      case Instruction.SStore(src, count, address) =>
        val (target, kept) = scalarLocated(address, 4, count, steps)
        (0 until count).foreach { index =>
          val write = store(Space.Global, at(target, index * 4), naming.scalar(src.offsetBy(index.unsafe[Int >= 0])), 4)
          steps += kept.fold(Term.Step("_", write))(found =>
            when(Render.lessThan(Literal(index.toLong), found, signed = false), write)
          )
        }
        steps.result
      case Instruction.Image(operation, data, address, mask, packing, bank) =>
        image(steps, operation, data, address, mask, packing, bank)
        steps.result
      case Instruction.WithStatus(access, status) =>
        this.access(access).map(_ :+ Term.Step(naming.name(naming.vector(status)), Literal(0L)))
      case _ => None
    }
  }

  private def image(
      steps: Bindings,
      operation: ImageOperation,
      data: VectorRegisterIndex,
      address: ImageAddress,
      mask: Int,
      packing: Packing,
      bank: RegisterBank
  ): Unit = {
    val words   = descriptor(address.resource, 8)
    val enabled = Vec.from((0 until 4).filter(component => (mask & (1 << component)) != 0))
    val fixed   = generation
    val half    = packing != Packing.Dword
    def bytes   =
      steps.bind("bytes", Render.primitive(words(1))((word1: UInt[32]) => ImageResource.texelBytes(fixed, word1)))
    operation match {
      case ImageOperation.Dimensions =>
        val level                              = steps.bind("level", coordinate(address, 0))
        val machine: Vec[(Term, Option[Type])] = Vec((Architecture, None)) ++ words.map(word => (word, Some(Dword)))
        steps ++= components(
          data,
          enabled.map(component =>
            Term.context(
              Images,
              "information",
              Vec.empty,
              machine ++ Vec((level, Some(Dword)), (Constant(s"component = $component"), None)),
              Some(Dword)
            )
          ),
          packing,
          bank
        )
      case ImageOperation.Load(converted, signed) =>
        val located     = texel(address, steps)
        val (low, high) = located.texel(steps, bytes, located.valid(0))
        steps ++= components(
          data,
          enabled.map(component =>
            if (converted)
              Render.primitive(words(1), words(3), low, high)(
                (word1: UInt[32], word3: UInt[32], low: UInt[64], high: UInt[64]) =>
                  ImageResource.component(fixed, word1, word3, low, high, component = component, half = half)
              )
            else
              Render.primitive(words(1), low, high)((word1: UInt[32], low: UInt[64], high: UInt[64]) =>
                ImageResource.unconverted(fixed, word1, low, high, component = component, signed = signed, half = half)
              )
          ),
          packing,
          bank
        )
      case ImageOperation.Store(converted) =>
        val located = texel(address, steps)
        val values  = Vec.tabulate(4) { component =>
          enabled.indexOf(component) match {
            case -1   => Literal(0L)
            case read =>
              val value = stored(data, read, packing, bank)
              if (packing == Packing.Dword || !converted) value
              else
                Render.primitive(words(1), value)((word1: UInt[32], value: UInt[16]) =>
                  ImageResource.widened(fixed, word1, value)
                )
          }
        }
        def packed(part: Int): Term =
          if (converted)
            Render.primitive(words(1), words(3), values(0), values(1), values(2), values(3))(
              (word1: UInt[32], word3: UInt[32], x: UInt[32], y: UInt[32], z: UInt[32], w: UInt[32]) =>
                ImageResource.packed(fixed, word1, word3, x, y, z, w, part = part)
            )
          else
            Render.primitive(words(1), values(0), values(1), values(2), values(3))(
              (word1: UInt[32], x: UInt[32], y: UInt[32], z: UInt[32], w: UInt[32]) =>
                ImageResource.packedBits(fixed, word1, x, y, z, w, part = part)
            )
        steps ++= located.put(bytes, located.valid(0), packed(0), packed(1))
    }
  }

  /** `located` with the addresses an atomic of `bytes` accesses required aligned, as [[AtomicAlignment]] says. */
  private def aligned(located: Located, bytes: Int, steps: Bindings): Located = {
    val global = (located.checked, located.scratch) match {
      case (false, _)                  => Literal(1L)
      case (true, None)                => located.valid(0)
      case (true, Some((_, other, _))) => Render.and(other, located.valid(0))
    }
    Located(
      located.scratch.map((condition, other, address) =>
        (
          condition,
          other,
          steps.bind(
            "private",
            Render.primitive(condition, address)((accessed: Boolean, at: UInt[64]) =>
              AtomicAlignment.where(accessed, at, bytes)
            )
          )
        )
      ),
      steps.bind(
        "address",
        Render.primitive(global, located.address)((accessed: Boolean, at: UInt[64]) =>
          AtomicAlignment.where(accessed, at, bytes)
        )
      ),
      located.valid,
      located.checked
    )
  }

  /** A read-modify-write of `bytes` at a buffer or image address: the old value, the value `update` stores over it in
    * range, and the old value into `dest` when the instruction returns it.
    */
  def exchanged(
      address: MemoryAddress,
      dest: VectorRegisterIndex,
      bytes: Int in (4, 8),
      returnValue: Boolean,
      update: Term => Term,
      bank: RegisterBank = RegisterBank.Vector
  ): Option[Term.Steps] = {
    val steps   = Bindings()
    val located = address match {
      case buffer: MemoryAddress.Buffer => Some(aligned(Buffered(buffer, steps).located(bytes, bytes), bytes, steps))
      case MemoryAddress.Texel(image)   => Some(texel(image, steps))
      case _                            => None
    }
    located.flatMap { found =>
      val old = steps.bind("data", found.read(0, bytes))
      steps ++= found.write(0, bytes, update(old))
      if (returnValue)
        if (bytes == 4) steps += Term.Step(naming.name(register(bank, dest, 0)), old)
        else if (bank == RegisterBank.Vector) steps += Term.Step(naming.name(naming.vectorPair(dest)), old)
        else {
          steps += Term.Step(naming.name(register(bank, dest, 0)), Render.truncate(old, 32))
          steps += Term.Step(naming.name(register(bank, dest, 1)), Render.shiftRightLogical(old, Literal(32L)))
        }
      steps.result
    }
  }

  /** Where a scalar access of `count` elements of `size` bytes lands, its address bound in `steps`, and for a buffer
    * how many of the elements are in range.
    */
  private def scalarLocated(address: ScalarAddress, size: Int, count: Int, steps: Bindings): (Term, Option[Term]) = {
    val added = address.scalarOffset.fold(Literal(0L))(naming.scalar(_))
    if (!address.buffer)
      (
        steps.bind(
          "address",
          Render.scalarDword(Render.scalarAddress(naming.scalarPair(address.base), Literal(address.offset), added))
        ),
        None
      )
    else {
      val words = descriptor(address.base, 4)
      val start = Render.scalarDword(Render.truncate(Render.add(Literal(address.offset), added), 32))
      val kept  = scalarElements(steps, words, start, size = size, count = count)
      (steps.bind("address", Render.add(Render.primitive(words(0), words(1))(BufferResource.base), start)), Some(kept))
    }
  }

  /** A scalar read-modify-write of `bytes`: the old value, zero for a buffer element out of range, the value `update`
    * stores over it in range, and the old value into the data SGPRs when the instruction returns it.
    */
  def scalarExchanged(
      address: ScalarAddress,
      data: ScalarRegisterIndex,
      bytes: Int in (4, 8),
      returnValue: Boolean,
      update: Term => Term
  ): Option[Term.Steps] = {
    val steps          = Bindings()
    val (target, kept) = scalarLocated(address, bytes, 1, steps)
    val inRange        = kept.map(found => Render.lessThan(Literal(0L), found, signed = false))
    val read           = load(Space.Global, target, bytes)
    val old            = steps.bind("data", inRange.fold(read)(Select(_, read, Literal(0L))))
    val write          = store(Space.Global, target, update(old), bytes)
    steps += inRange.fold(Term.Step("_", write))(when(_, write))
    if (returnValue)
      steps +=
        (if (bytes == 8) Term.Step(naming.name(naming.scalarPair(data)), old) else naming.scalarWrite(data)(old))
    steps.result
  }
}
