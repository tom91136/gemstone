package net.kurobako.gemstone.isa.sass

import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.Instruction.{Texture as TextureOperation, *}
import net.kurobako.gemstone.isa.nvidia.VMIR.{Texture as Image, *}
import net.kurobako.gemstone.texture.*
import net.kurobako.gemstone.vm.*

/** Texture and surface instructions. The handle is the low 32 bits of a bindless object: a word of a constant bank
  * (`c[bank][4 * index]`, which holds a kernel argument) or a uniform register, and names an entry of the descriptor
  * table. `LL` and `LB` read the level or the bias from the register after the coordinates; with texel offsets that
  * register holds the packed offsets, and `LZ`, the short forms, and gathers read level zero.
  */
private object VMTextureCompiler {
  private enum Family derives CanEqual { case Sample, Fetch, Gather }

  private val SamplingModifiers = Set("P", "T", "NODEP", "SCR", "LL", "LZ", "LB", "NDV", "CL")
  private val GatherChannels    = Map("R" -> 0, "G" -> 1, "B" -> 2, "A" -> 3)
  private val SurfaceModifiers  =
    Set("D", "BA", "STRONG", "WEAK", "CTA", "SM", "SYS", "GPU", "CA", "CG", "CS", "LU", "CV", "WT", "WB")
  private val SurfaceTypes = Set("1D", "2D", "3D", "U8", "S8", "U16", "S16", "64", "128", "TRAP", "IGN", "32")
  private val SurfaceAccess: PartialFunction[String, (Int, Boolean)] = {
    case "U8"         => (1, false)
    case "S8"         => (1, true)
    case "U16"        => (2, false)
    case "S16"        => (2, true)
    case "64" | "U64" => (8, false)
    case "S64"        => (8, true)
    case "128"        => (16, false)
  }
  private val QueryProperties     = Vec(Image.Property.Width, Image.Property.Height, Image.Property.Depth)
  private val BitfieldOperands    = Vec(2, 3, 4)
  private val KeplerLoadModifiers =
    Set("B", "CA", "CG", "CS", "CV", "U8", "S8", "U16", "S16", "32", "64", "128", "Z", "TRAP", "CLAMP")
  private val KeplerStoreModifiers =
    Set("B", "CA", "CG", "CS", "CV", "U8", "S8", "U16", "S16", "32", "64", "128", "IGN", "TRAP", "CLAMP")
  private val KeplerClampModifiers = Set("SD", "PL", "BL", "R1", "R2", "R4", "R8", "R16")

  def lower(using context: VMCompilationContext): Boolean = {
    import context.*
    (operation.form: @unchecked) match {
      case _: Form.Tex if sm == 35 && keplerGlobalLoad => false
      case _: Form.Tex                                 =>
        sample(Family.Sample, short = false)
        true
      case _: Form.Texs =>
        sample(Family.Sample, short = true)
        true
      case _: Form.Tld =>
        sample(Family.Fetch, short = false)
        true
      case _: Form.Tlds =>
        sample(Family.Fetch, short = true)
        true
      case _: Form.Tld4 =>
        sample(Family.Gather, short = false)
        true
      case _: Form.Tld4s =>
        sample(Family.Gather, short = true)
        true
      case _: Form.Txq =>
        textureQuery()
        true
      case _: Form.Suld =>
        surfaceLoad()
        true
      case _: Form.Sust =>
        surfaceStore()
        true
      case _: Form.Sured =>
        surfaceReduce()
        true
      case _: Form.Suclamp =>
        keplerClamp()
        true
      case _: Form.Subfm =>
        keplerBitfield()
        true
      case _: Form.Sueau =>
        keplerAddressUpper()
        true
      case _: Form.Suldga =>
        keplerLoad()
        true
      case _: Form.Sustga =>
        keplerStore()
        true
      case _: Form.Imadsp if sm < 50 =>
        spaceMultiplyAdd()
        true
      case _ => false
    }
  }

  /** Kepler encodes read-only global loads as TEX with the access size where a texture would carry its handle. */
  private def keplerGlobalLoad(using context: VMCompilationContext): Boolean =
    context.operands.find(OperandQueries.integer(_).isDefined)
      .exists(index => context.number(index) * 4 < context.kernel.parameterBase.raw)

  private def requireOnly(allowed: Set[String])(using context: VMCompilationContext): Unit =
    context.modifiers.map(_.name).filterNot(allowed).headOption.foreach(name =>
      context.unsupported(s"unsupported texture modifier $name")
    )

  private def isGeneral(operand: Operand): Boolean = OperandQueries.isRegister(operand, allowZero = true)

  private def symbol(operand: Operand): Option[String] = operand match {
    case Operand.Symbol(name) => Some(name)
    case _                    => None
  }

  /** The handle at the front of `operands` and the operands after it. */
  private def handle(items: Vec[Operand])(using context: VMCompilationContext): (Value, Vec[Operand]) = {
    import context.*
    val head = items.headOption.getOrElse(unsupported("missing texture handle"))
    if (OperandQueries.isRegister(head, RegisterBank.UR)) {
      val tail = items.drop(1)
      // a uniform handle may be followed by an index bias, which no compiler sets
      val rest = tail.headOption.filter(OperandQueries.integer(_).isDefined).fold(tail) { bias =>
        if (number(bias) != 0L) unsupported("biased texture handle")
        tail.drop(1)
      }
      // the sampler half of a texture object lives in the same handle, so a separate sampler register must be unused
      val unpaired =
        rest.headOption.filter(OperandQueries.isRegister(_, RegisterBank.UR, allowZero = true)).fold(rest) {
          sampler =>
            if (!OperandQueries.isZero(sampler)) unsupported("separate sampler handle")
            rest.drop(1)
        }
      (register(head), unpaired)
    } else {
      val banked = sm >= 70
      val bank   = if (banked) number(head) else 0L
      val index  = number(items.lift(if (banked) 1 else 0).getOrElse(unsupported("missing texture index")))
      val offset = index * 4
      if (bank == 0L && offset < kernel.parameterBase.raw) unsupported("texture handle outside the kernel arguments")
      (VMConstantBank.static(bank, offset, 32), items.drop(if (banked) 2 else 1))
    }
  }

  private def dimensions(operand: Option[Operand])(using context: VMCompilationContext): Int =
    operand.flatMap(symbol(_)) match {
      case Some("1D") => 1
      case Some("2D") => 2
      case Some("3D") => 3
      case other      => context.unsupported(s"unsupported texture dimensionality $other")
    }

  private def components(mask: Option[Operand])(using context: VMCompilationContext): Vec[Int] = {
    val bits = mask match {
      case None                                                            => 0xf
      case Some(Operand.Symbol(names)) if names.forall("RGBA".contains(_)) =>
        names.map(name => 1 << "RGBA".indexOf(name)).sum
      case Some(value) if OperandQueries.integer(value).isDefined => context.number(value).toInt
      case other => context.unsupported(s"unsupported component mask $other")
    }
    Vec.from((0 until 4).filter(index => (bits & (1 << index)) != 0))
  }

  /** The destination of position `index` of the packed result, which fills the low pair before the high pair. */
  private def sink(destinations: Vec[Operand], index: Int)(using context: VMCompilationContext): Option[Register] = {
    import context.*
    val (base, offset) =
      if (destinations.size == 1) (destinations.head, index)
      else if (index < 2) (destinations(1), index)
      else (destinations.head, index - 2)
    Option.unless(OperandQueries.isZero(base))(destination(base, offset).slot)
  }

  private def coordinates(
      levelInSecond: Boolean,
      dimensions: Int,
      first: Operand,
      second: Option[Operand]
  )(using context: VMCompilationContext): Vec[Value] = {
    import context.*
    def at(offset: Int): Value = register(first, offset)
    second match {
      case None                           => Vec.tabulate(dimensions)(at(_))
      case Some(_) if dimensions == 1     => Vec(at(0))
      case Some(_) if levelInSecond       => Vec.tabulate(dimensions)(at(_))
      case Some(level) if dimensions == 2 => Vec(at(0), register(level))
      case Some(last)                     => Vec(at(0), at(1), register(last))
    }
  }

  private def sample(family: Family, short: Boolean)(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(
      SamplingModifiers ++ (if (family == Family.Gather) GatherChannels.keySet else Set.empty[String]) ++
        (if (family == Family.Fetch) Set("AOFFI") else Set.empty[String])
    )
    val offsets      = named("AOFFI")
    val predicate    = operands.headOption.filter { case Operand.Predicate(_, _, _) => true; case _ => false }
    val body         = operands.drop(predicate.size)
    val registers    = body.takeWhile(isGeneral(_))
    val destinations = registers.take(if (sm < 50) 1 else if (sm < 70) if (short) 2 else 1 else 2)
    val operandsOf   = registers.drop(destinations.size)
    val addresses    = if (offsets) operandsOf.take(1) else operandsOf.filterNot(OperandQueries.isZero)
    if (addresses.isEmpty || addresses.size > 2) unsupported("unsupported texture operand shape")
    val (target, rest) = handle(body.drop(registers.size))
    val dims           = if (family == Family.Gather) 2 else dimensions(rest.headOption)
    val mask           = if (family == Family.Gather) Vec(0, 1, 2, 3) else components(rest.lift(1))
    if (family == Family.Gather && rest.headOption.exists(symbol(_).isEmpty)) unsupported("unsupported gather operands")
    val level =
      Option.when(
        !short && !offsets && family != Family.Gather && (named("LL") || named("LB"))
      )(operandsOf.lift(1)).flatten
    val coords =
      coordinates(level.isDefined || (family == Family.Sample && !short), dims, addresses.head, addresses.lift(1))
    val shifted =
      if (!offsets) coords
      else {
        val packed = register(addresses.head, dims)
        coords.zipWithIndex.map { (coordinate, axis) =>
          val offset = temp()
          val result = temp()
          emit(BitFieldExtract(offset.slot, packed, Value.Immediate(4L * axis), Value.Immediate(4L), 32, true))
          emit(IntegerBinary(IntegerOperation.Add, result.slot, coordinate, offset, 32))
          result
        }
      }
    val slots =
      Vec.tabulate(4)(index => Option.when(mask.contains(index))(mask.indexOf(index)).flatMap(sink(destinations, _)))
    val op =
      if (family == Family.Gather) {
        val channel = modifiers.flatMap(modifier => GatherChannels.get(modifier.name)).headOption
          .getOrElse(unsupported("gather without a component"))
        Image.Operation.Gather(target, shifted, channel.unsafe[Int in 0 ~ 3], Image.Element.Native, slots)
      } else {
        val kind = if (family == Family.Sample) Image.Coordinates.Float else Image.Coordinates.Integer
        level.fold(Image.Operation.Sample(target, shifted, kind, Image.Element.Native, slots)) { operand =>
          Image.Operation.Sample(
            target,
            shifted :+ register(operand),
            kind,
            Image.Element.Native,
            slots,
            Image.Detail.Level
          )
        }
      }
    emit(TextureOperation(op))
    predicate.foreach(writePredicate(_, Value.Predicate(VMRegisters.Predicate.True.slot)))
  }

  private def textureQuery()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(SamplingModifiers)
    val registers    = operands.takeWhile(isGeneral(_))
    val destinations = registers.take(if (sm < 70) 1 else 2)
    val rest         = operands.drop(registers.size)
    if (!rest.headOption.flatMap(symbol(_)).contains("TEX_HEADER_DIMENSION")) unsupported("unsupported texture query")
    val (target, after) = handle(rest.drop(1))
    val mask            = components(after.headOption)
    if (mask.exists(_ > 2)) unsupported("unsupported texture query component")
    val base = destinations.last
    if (!OperandQueries.isZero(base))
      mask.zipWithIndex.foreach { (component, position) =>
        emit(TextureOperation(Image.Operation.Query(
          target,
          surface = false,
          QueryProperties(component),
          destination(base, position).slot
        )))
      }
  }

  private def surface(extra: Set[String] = Set.empty)(using
      context: VMCompilationContext
  ): (Int, BoundaryMode, Int, Boolean) = {
    import context.*
    requireOnly(SurfaceModifiers ++ SurfaceTypes ++ extra)
    val dims = modifiers.map(_.name).collectFirst { case "1D" => 1; case "2D" => 2; case "3D" => 3 }
      .getOrElse(unsupported("surface without dimensionality"))
    val mode =
      if (named("TRAP")) BoundaryMode.Trap else if (named("IGN")) BoundaryMode.Zero else BoundaryMode.Clamp
    val (bytes, signed) = modifiers.map(_.name).collectFirst(SurfaceAccess).getOrElse((4, false))
    (dims, mode, bytes, signed)
  }

  private val ReduceModifiers = Set("ADD", "MIN", "MAX", "AND", "OR", "XOR", "S32", "S64", "U32", "U64")

  private def surfaceAddress(operand: Operand, dims: Int)(using context: VMCompilationContext): Vec[Value] =
    operand match {
      case Operand.Address(Some(base), None, displacement, 1, None, None) if displacement == 0 =>
        Vec.tabulate(dims)(context.register(base, _))
      case other => context.unsupported(s"unsupported surface address $other")
    }

  private def words(bytes: Int): (Int, Int) = if (bytes <= 4) (bytes, 1) else (4, bytes / 4)

  private def surfaceLoad()(using context: VMCompilationContext): Unit = {
    import context.*
    val (dims, mode, bytes, signed) = surface()
    if (operands.size < 3 || !isGeneral(operands.head)) unsupported("unsupported surface load operands")
    val (target, _)    = handle(operands.drop(2))
    val (width, count) = words(bytes)
    val sinks          = Vec.tabulate(count)(index => sink(Vec(operands.head), index))
    emit(TextureOperation(Image.Operation.Load(
      target,
      surfaceAddress(operands(1), dims),
      width.unsafe[Int in (1, 2, 4, 8)],
      mode,
      sinks
    )))
    if (signed && !OperandQueries.isZero(operands.head)) {
      val result = destination(operands.head)
      emit(Convert(
        result.slot,
        result,
        if (bytes == 1) NumericFormat.Signed(8) else NumericFormat.Signed(16),
        NumericFormat.Signed(32),
        None
      ))
    }
  }

  private def surfaceStore()(using context: VMCompilationContext): Unit = {
    import context.*
    val (dims, mode, bytes, _) = surface()
    if (operands.size < 3 || !isGeneral(operands(1))) unsupported("unsupported surface store operands")
    val (target, _)    = handle(operands.drop(2))
    val (width, count) = words(bytes)
    emit(TextureOperation(Image.Operation.Store(
      target,
      surfaceAddress(operands.head, dims),
      width.unsafe[Int in (1, 2, 4, 8)],
      mode,
      Vec.tabulate(count)(register(operands(1), _))
    )))
  }

  private def surfaceReduce()(using context: VMCompilationContext): Unit = {
    import context.*
    val (dims, mode, bytes, _) = surface(ReduceModifiers)
    if (bytes != 4 && bytes != 8) unsupported("unsupported surface reduction width")
    if (operands.size < 3 || !isGeneral(operands(1))) unsupported("unsupported surface reduction operands")
    val (target, _) = handle(operands.drop(2))
    val signed      = named("S32") || named("S64")
    val update      = modifiers.map(_.name).collectFirst {
      case "ADD" => AtomicOperation.Add
      case "MIN" => if (signed) AtomicOperation.MinimumSigned else AtomicOperation.MinimumUnsigned
      case "MAX" => if (signed) AtomicOperation.MaximumSigned else AtomicOperation.MaximumUnsigned
      case "AND" => AtomicOperation.And
      case "OR"  => AtomicOperation.Or
      case "XOR" => AtomicOperation.Xor
    }.getOrElse(unsupported("unsupported surface reduction"))
    emit(TextureOperation(Image.Operation.Reduce(
      target,
      surfaceAddress(operands.head, dims),
      bytes.unsafe[Int in (4, 8)],
      update,
      mode,
      source(operands(1), if (bytes == 4) 32 else 64),
      None
    )))
  }

  private val OutOfBounds = 0x80000000L

  private def flagged(value: Value)(using context: VMCompilationContext): Value.Predicate =
    context.compare(
      Comparison.NotEqual,
      NumericFormat.Unsigned(32),
      context.binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(OutOfBounds)),
      Value.Immediate(0L)
    )

  /** The rounding of a Kepler byte coordinate down to a multiple of its element size, in bytes. */
  private def rounding()(using context: VMCompilationContext): Long =
    context.modifiers.map(_.name).collectFirst {
      case name if name.startsWith("R") && name.drop(1).forall(_.isDigit) && name.length > 1 => name.drop(1).toLong
    }.getOrElse(1L)

  /** Clamps a coordinate to the inclusive range from the immediate to the limit. The result keeps the clamped value in
    * its low 31 bits and sets bit 31 when the coordinate was outside the range, which the later address instructions
    * read.
    */
  private def keplerClamp()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(KeplerClampModifiers)
    requireOperands(5, "SUCLAMP operands")
    val coordinate = source(operands(2))
    val clamped    = binary(
      IntegerOperation.Minimum(signed = true),
      binary(IntegerOperation.Maximum(signed = true), coordinate, source(operands(4))),
      source(operands(3))
    )
    val outside = compare(Comparison.NotEqual, NumericFormat.Signed(32), clamped, coordinate)
    val aligned = binary(IntegerOperation.BitwiseAnd, clamped, Value.Immediate(0x7fffffffL & ~(rounding() - 1)))
    val result  =
      binary(IntegerOperation.BitwiseOr, aligned, select(Value.Immediate(OutOfBounds), Value.Immediate(0L), outside))
    writePredicate(operands.head, outside)
    store(operands(1), result)
  }

  /** The low byte of the first coordinate, which a surface address carries beside the upper word, and whether any
    * coordinate was out of range.
    */
  private def keplerBitfield()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(Set("3D"))
    requireOperands(5, "SUBFM operands")
    val coordinates = BitfieldOperands.map(index => source(operands(index)))
    val outside     = coordinates.map(flagged(_)).reduce(combine(PredicateOperation.Or, _, _))
    val low         = binary(IntegerOperation.BitwiseAnd, coordinates.head, Value.Immediate(0xffL))
    writePredicate(operands.head, outside)
    store(operands(1), low)
  }

  /** The upper word of a surface address: the surface base in units of 256 bytes plus the offset in the same units. */
  private def keplerAddressUpper()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(Set.empty)
    requireOperands(4, "SUEAU operands")
    val offset = binary(
      IntegerOperation.ShiftRightLogical,
      binary(IntegerOperation.BitwiseAnd, source(operands(1)), Value.Immediate(0x7fffffffL)),
      Value.Immediate(8L)
    )
    store(operands.head, binary(IntegerOperation.Add, source(operands(3)), offset))
  }

  /** The 40-bit address a register pair holds: the upper word in units of 256 bytes over the low byte of the first. */
  private def keplerAddress(operand: Operand)(using context: VMCompilationContext): Value = operand match {
    case Operand.Address(Some(base), None, displacement, 1, None, None) if displacement == 0 =>
      import context.*
      val high = binary(IntegerOperation.ShiftLeft, widen(register(base, 1)), Value.Immediate(8L), 64)
      val low  = widen(binary(IntegerOperation.BitwiseAnd, register(base), Value.Immediate(0xffL)))
      binary(IntegerOperation.BitwiseOr, high, low, 64)
    case other => context.unsupported(s"unsupported surface address $other")
  }

  private val KeplerSizes = Map(
    "U8"  -> (1, false),
    "S8"  -> (1, true),
    "U16" -> (2, false),
    "S16" -> (2, true),
    "32"  -> (4, false),
    "64"  -> (8, false),
    "128" -> (16, false)
  )

  private def keplerBytes()(using context: VMCompilationContext): (Int, Boolean) =
    context.modifiers.map(_.name).collectFirst(KeplerSizes).getOrElse(
      context.unsupported("surface access without a size")
    )

  private def keplerAccess(bytes: Int): (ScalarBitWidth, Int) =
    if (bytes == 1) (8, 1) else if (bytes == 2) (16, 1) else (32, bytes / 4)

  private def keplerBoundary()(using context: VMCompilationContext): BoundaryMode =
    if (context.named("TRAP")) BoundaryMode.Trap
    else if (context.named("Z") || context.named("IGN")) BoundaryMode.Zero
    else BoundaryMode.Clamp

  private def keplerLoad()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(KeplerLoadModifiers)
    requireOperands(4, "SULDGA operands")
    val (bytes, signed) = keplerBytes()
    val (width, count)  = keplerAccess(bytes)
    val outside         = predicateSource(operands(3))
    val target          = keplerAddress(operands(1))
    val sinks           = Vec.tabulate(count)(index => destination(operands.head, index))
    val boundary        = keplerBoundary()
    boundary match {
      case BoundaryMode.Trap => guarded(Trap, outside)
      case BoundaryMode.Zero => sinks.foreach(sink => emit(Move(sink.slot, Value.Immediate(0L), 32)))
      case _                 => ()
    }
    val load = VMMemoryCompiler.loadOf(sinks, target, width, signed, MemoryAccess.Global(false))
    if (boundary == BoundaryMode.Zero) guarded(load, outside, negated = true) else emit(load)
  }

  private def keplerStore()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOnly(KeplerStoreModifiers)
    requireOperands(4, "SUSTGA operands")
    val (bytes, _)     = keplerBytes()
    val (width, count) = keplerAccess(bytes)
    val outside        = predicateSource(operands(3))
    val target         = keplerAddress(operands.head)
    val values         = Vec.tabulate(count)(index => register(operands(2), index))
    val store          = VMMemoryCompiler.storeOf(target, values, width, MemoryAccess.Global(false))
    keplerBoundary() match {
      case BoundaryMode.Trap =>
        guarded(Trap, outside)
        emit(store)
      case BoundaryMode.Zero => guarded(store, outside, negated = true)
      case _                 => emit(store)
    }
  }

  private val Halves = Set("U16H0", "U16H1", "S16H0", "S16H1", "U32", "S32")

  private def half(value: Value, format: String)(using context: VMCompilationContext): Value = {
    import context.*
    format match {
      case "U32" | "S32" => value
      case "U16H0"       => binary(IntegerOperation.BitwiseAnd, value, Value.Immediate(0xffffL))
      case "U16H1"       => binary(IntegerOperation.ShiftRightLogical, value, Value.Immediate(16L))
      case "S16H0"       => convert(value, NumericFormat.Signed(16), NumericFormat.Signed(32))
      case _             => binary(IntegerOperation.ShiftRightArithmetic, value, Value.Immediate(16L))
    }
  }

  /** A multiply-add whose operands each take a half or the whole of a register. `.SD` names the layout the surface
    * address sequence uses: 16-bit multiplicands and a 32-bit addend.
    */
  private def spaceMultiplyAdd()(using context: VMCompilationContext): Unit = {
    import context.*
    requireOperands(4, "IMADSP operands")
    val names                 = modifiers.map(_.name)
    val (addend, left, right) = names match {
      case Vec("SD")                                           => ("U32", "U16H0", "U16H0")
      case Vec(c, a, b) if Halves(c) && Halves(a) && Halves(b) => (c, a, b)
      case _                                                   => unsupported("unsupported IMADSP formats")
    }
    val product = binary(
      IntegerOperation.MultiplyLow,
      half(source(operands(1)), left),
      half(source(operands(2)), right)
    )
    store(operands.head, binary(IntegerOperation.Add, product, half(source(operands(3)), addend)))
  }
}
