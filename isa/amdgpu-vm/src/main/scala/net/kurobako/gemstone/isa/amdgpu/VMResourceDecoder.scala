package net.kurobako.gemstone.isa.amdgpu

import BufferInstructionDefs.*
import DecodedRegisters.*
import ScalarMemoryInstructionDefs.*
import VMIR.*
import cats.syntax.either.*
import cats.syntax.traverse.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.amdgpu.domain.*
import net.kurobako.gemstone.vm.*

/** Lowers the buffer (MUBUF, MTBUF, VBUFFER), image (MIMG, VIMAGE) and scalar buffer encodings. */
private object VMResourceDecoder {
  private type Result[A] = Either[NonEmpty[String], A]

  extension (machine: InstructionDecoder.Decoded) {
    private def isSet(field: EncodingField): Boolean = machine.contains(field) && machine(field) != 0

    private def resourceField: EncodingField =
      if (machine.contains(EncodingField.SRSRC)) EncodingField.SRSRC else EncodingField.RSRC

    /** Whether an atomic returns the old value: GLC, SC0 on GFX940, or TH_ATOMIC_RETURN on GFX12. */
    private def returning: Boolean =
      if (machine.contains(EncodingField.GLC)) machine.isSet(EncodingField.GLC)
      else if (machine.contains(EncodingField.SC0)) machine.isSet(EncodingField.SC0)
      else (machine(EncodingField.TH) & 1L) != 0L
  }

  private def offsetRegister(
      index: Int,
      by: Int
  ): Result[VectorRegisterIndex] = (index + by).refine[VectorRegisterIndex].leftMap(_ =>
    s"v${index + by} exceeds the vector register bank"
  )

  private def sourceSlot(machine: InstructionDecoder.Decoded, target: Target): Option[Slot] =
    Src.decode(target.isa, machine(EncodingField.SOFFSET)).map(_.slot)

  /** The scalar offset an SOFFSET encodes: an SGPR, M0, the null register or an inline integer. VBUFFER's SOFFSET
    * domain lists no inline integers but the VM takes them as the source domain does.
    */
  private def scalarOffset(machine: InstructionDecoder.Decoded, target: Target)
      : Result[(Option[ScalarRegisterIndex], Int)] =
    machine.slot(EncodingField.SOFFSET).orElse(sourceSlot(machine, target)) match {
      case Some(Slot.Named("null"))  => Right((None, 0))
      case Some(Slot.Named("m0"))    => Right((Some(m0Register(target.isa)), 0))
      case Some(Slot.Integer(value)) => Right((None, value))
      case _ => machine.scalarOperand(EncodingField.SOFFSET).map(register => (Some(register), 0))
    }

  private val FormatName = "t?buffer_(load|store)_[a-z0-9_]*?(x|xy|xyz|xyzw)".r

  private def generation(target: Target): ResourceGeneration = ResourceGeneration.of(target.isa)

  /** D16 halves pack two to a register from GFX9 on and on gfx810 (Stoney), and occupy one register each on the other
    * GFX8 targets, as LLVM's unpacked-D16 feature lists them.
    */
  private def halves(target: Target, high: Boolean): Packing =
    if (high) Packing.High
    else if (generation(target) == ResourceGeneration.GFX8 && !Set("gfx810", "stoney")(target.name.raw))
      Packing.Unpacked
    else Packing.Packed

  private def bufferAtomic(opcode: Opcode): Option[VectorMemoryAccess] = opcode match {
    case BufferAtomicPkAddF16  => Some(VectorMemoryAccess.Atomic(AtomicOperation.PackedHalfAdd, 4))
    case BufferAtomicPkAddBf16 => Some(VectorMemoryAccess.Atomic(AtomicOperation.PackedBFloat16Add, 4))
    case BufferAtomicMinNumF32 => Some(VectorMemoryAccess.Atomic(AtomicOperation.FloatMinimum, 4))
    case BufferAtomicMaxNumF32 => Some(VectorMemoryAccess.Atomic(AtomicOperation.FloatMaximum, 4))
    case BufferAtomicMinNumF64 => Some(VectorMemoryAccess.Atomic(AtomicOperation.FloatMinimum, 8))
    case BufferAtomicMaxNumF64 => Some(VectorMemoryAccess.Atomic(AtomicOperation.FloatMaximum, 8))
    case BufferAtomicCsub | BufferAtomicCsubU32 | BufferAtomicSubClampU32 =>
      Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.SubtractClamp, 4))
    case BufferAtomicCondSubU32 => Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.ConditionalSubtract, 4))
    case BufferAtomicFcmpswap | BufferAtomicCmpswapF32 =>
      Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.FloatCompareSwap, 4))
    case BufferAtomicFcmpswapX2 => Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.FloatCompareSwap, 8))
    case BufferAtomicRsub       => Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.ReverseSubtract, 4))
    case BufferAtomicRsubX2     => Some(VectorMemoryAccess.Extended(ExtendedAtomicOperation.ReverseSubtract, 8))
    case other                  => VectorMemoryAccess.of(other)
  }

  private def loadBytes(access: VectorMemoryAccess): Option[(Int in 1 ~ 16, Boolean)] = access match {
    case VectorMemoryAccess.Load(bytes, signed, LoadPlacement.Full) => Some((bytes, signed))
    case _                                                          => None
  }

  /** A MUBUF, MTBUF or VBUFFER instruction other than cache maintenance. */
  def buffer(machine: InstructionDecoder.Decoded, target: Target): Result[Instruction] = {
    val opcode  = machine.definition
    val name    = opcode.mnemonic.raw
    val indexed = machine.isSet(EncodingField.IDXEN)
    val offset  = machine.isSet(EncodingField.OFFEN)
    val bank    = if (machine.isSet(EncodingField.ACC)) RegisterBank.Accumulator else RegisterBank.Vector
    val typed   = machine.contains(EncodingField.DFMT) || machine.format == EncodingFormat.MTBUF ||
      (machine.format == EncodingFormat.VBUFFER && name.startsWith("tbuffer"))
    val wide = machine.isSet(EncodingField.ADDR64)
    for {
      _ <- Either.cond[NonEmpty[String], Unit](
        !wide || (!indexed && !offset),
        (),
        s"$opcode ADDR64 with an index or offset VGPR is not defined"
      )
      first        <- machine.vector(EncodingField.VADDR)
      data         <- machine.vector(EncodingField.VDATA)
      resource     <- machine.scalar(machine.resourceField)
      soffset      <- scalarOffset(machine, target)
      offsetVector <-
        if (wide) Right(Some(first))
        else if (!offset) Right(None)
        else offsetRegister(first, if (indexed) 1 else 0).map(Some(_))
      address = MemoryAddress.Buffer(
        offsetVector,
        resource,
        soffset._1,
        machine(EncodingField.OFFSET).toInt,
        Option.when(indexed)(first),
        soffset._2,
        wide
      )
      format = Option.when(typed)(TexelFormat.instruction(
        generation(target),
        if (machine.contains(EncodingField.FORMAT)) machine(EncodingField.FORMAT).toInt else 0,
        if (machine.contains(EncodingField.DFMT)) machine(EncodingField.DFMT).toInt else 0,
        if (machine.contains(EncodingField.NFMT)) machine(EncodingField.NFMT).toInt else 0
      ))
      access <- name match {
        case _ if wide && (typed || name.contains("format") || machine.isSet(EncodingField.LDS)) =>
          Left[NonEmpty[String], Instruction](s"$opcode ADDR64 typed or LDS access is not implemented")
        case FormatName(direction, components) if name.contains("format") =>
          val count   = components.length.unsafe[Int in 1 ~ 4]
          val packing = if (!name.contains("d16")) Packing.Dword else halves(target, name.contains("_hi_"))
          if (direction == "load") Right(Instruction.FormatLoad(data, address, format, count, packing, bank))
          else Right(Instruction.FormatStore(data, address, format, count, packing, bank))
        case _ if opcode == BufferStoreLdsDword =>
          Right(Instruction.StoreLocal(address, machine(EncodingField.OFFSET).toInt))
        case _ if machine.isSet(EncodingField.LDS) =>
          bufferAtomic(opcode).flatMap(loadBytes(_)) match {
            case Some((bytes, signed)) =>
              Right(Instruction.LoadLocal(address, bytes, signed, machine(EncodingField.OFFSET).toInt))
            case None => Left[NonEmpty[String], Instruction](s"$opcode with LDS is not implemented")
          }
        case _ =>
          bufferAtomic(opcode) match {
            case Some(VectorMemoryAccess.Load(bytes, signed, placement)) =>
              Right(Instruction.Load(data, address, bytes, signed, placement, bank))
            case Some(VectorMemoryAccess.Store(bytes, high)) =>
              Right(Instruction.Store(data, address, bytes, high, bank))
            case Some(VectorMemoryAccess.Atomic(operation, bytes)) =>
              Right(Instruction.Atomic(operation, data, address, data, bytes, machine.returning, bank))
            case Some(VectorMemoryAccess.CompareSwap(bytes)) =>
              offsetRegister(data, bytes / 4)
                .map(Instruction.CompareSwap(data, address, data, _, bytes, machine.returning, bank))
            case Some(_) if bank == RegisterBank.Accumulator =>
              Left[NonEmpty[String], Instruction](s"$opcode accumulator register bank is not implemented")

            // a compare-swap's comparand is operand A; a lone operand is both
            case Some(VectorMemoryAccess.Extended(operation, bytes)) =>
              val paired = operation == ExtendedAtomicOperation.FloatCompareSwap
              offsetRegister(data, if (paired) bytes / 4 else 0).map(comparand =>
                Instruction.AtomicExtended(
                  operation,
                  Instruction.CompareSwap(data, address, data, comparand, bytes, machine.returning)
                )
              )
            case None => Left[NonEmpty[String], Instruction](s"$opcode ${machine.format} execution is not implemented")
          }
      }
      withStatus <- statusIfRequested(machine, access)
    } yield withStatus
  }

  private def statusIfRequested(machine: InstructionDecoder.Decoded, access: Instruction): Result[Instruction] =
    if (!machine.isSet(EncodingField.TFE)) Right(access)
    else statusRegister(access).map(Instruction.WithStatus(access, _))

  /** The register after a TFE load's data. */
  private def statusRegister(access: Instruction): Result[VectorRegisterIndex] = access match {
    case Instruction.Load(dest = dest, bytes = bytes) => offsetRegister(dest, (bytes + 3) / 4)
    case Instruction.FormatLoad(dest = dest, components = components, packing = packing) =>
      offsetRegister(dest, registers(components, packing))
    case Instruction.Image(data = data, mask = mask, packing = packing) =>
      offsetRegister(data, registers(Integer.bitCount(mask).max(1), packing))
    case other => Left(s"${other.productPrefix} with TFE is not implemented")
  }

  def registers(components: Int, packing: Packing): Int = packing match {
    case Packing.Packed => (components + 1) / 2
    case Packing.High   => 1
    case _              => components
  }

  /** S_BUFFER_LOAD on GFX8 onwards. */
  def scalarBuffer(
      machine: InstructionDecoder.Decoded,
      offset: Long,
      scalarOffset: Option[
        ScalarRegisterIndex
      ]
  ): Result[Instruction] = {
    val opcode = machine.definition
    for {
      shape <- opcode match {
        case SBufferLoadDword | SBufferLoadB32     => Right((1: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadDwordx2 | SBufferLoadB64   => Right((2: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadB96                        => Right((3: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadDwordx4 | SBufferLoadB128  => Right((4: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadDwordx8 | SBufferLoadB256  => Right((8: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadDwordx16 | SBufferLoadB512 => Right((16: Int in (1, 2, 3, 4, 8, 16), 4, false))
        case SBufferLoadU8                         => Right((1: Int in (1, 2, 3, 4, 8, 16), 1, false))
        case SBufferLoadI8                         => Right((1: Int in (1, 2, 3, 4, 8, 16), 1, true))
        case SBufferLoadU16                        => Right((1: Int in (1, 2, 3, 4, 8, 16), 2, false))
        case SBufferLoadI16                        => Right((1: Int in (1, 2, 3, 4, 8, 16), 2, true))
        case other => Left[NonEmpty[String], Nothing](s"$other SMEM execution is not implemented")
      }
      data     <- machine.scalar(EncodingField.SDATA)
      resource <- machine.scalar(EncodingField.SBASE)
    } yield Instruction.SBufferLoad(
      data,
      shape._1,
      resource,
      offset,
      scalarOffset,
      shape._2.unsafe[Int in (1, 2, 4)],
      shape._3
    )
  }

  private val Atomics: Map[String, AtomicOperation] = Map(
    "swap"        -> AtomicOperation.Exchange,
    "add"         -> AtomicOperation.Add,
    "add_uint"    -> AtomicOperation.Add,
    "sub"         -> AtomicOperation.Subtract,
    "sub_uint"    -> AtomicOperation.Subtract,
    "smin"        -> AtomicOperation.MinimumSigned,
    "min_int"     -> AtomicOperation.MinimumSigned,
    "umin"        -> AtomicOperation.MinimumUnsigned,
    "min_uint"    -> AtomicOperation.MinimumUnsigned,
    "smax"        -> AtomicOperation.MaximumSigned,
    "max_int"     -> AtomicOperation.MaximumSigned,
    "umax"        -> AtomicOperation.MaximumUnsigned,
    "max_uint"    -> AtomicOperation.MaximumUnsigned,
    "and"         -> AtomicOperation.And,
    "or"          -> AtomicOperation.Or,
    "xor"         -> AtomicOperation.Xor,
    "inc"         -> AtomicOperation.IncrementWrap,
    "inc_uint"    -> AtomicOperation.IncrementWrap,
    "dec"         -> AtomicOperation.DecrementWrap,
    "dec_uint"    -> AtomicOperation.DecrementWrap,
    "add_flt"     -> AtomicOperation.FloatAdd,
    "fmin"        -> AtomicOperation.FloatMinimum,
    "min_flt"     -> AtomicOperation.FloatMinimum,
    "fmax"        -> AtomicOperation.FloatMaximum,
    "max_flt"     -> AtomicOperation.FloatMaximum,
    "pk_add_f16"  -> AtomicOperation.PackedHalfAdd,
    "pk_add_bf16" -> AtomicOperation.PackedBFloat16Add
  )

  /** An image instruction that loads, stores, updates atomically or queries a resource without a sampler. */
  def image(machine: InstructionDecoder.Decoded, target: Target): Result[Instruction] = {
    val opcode = machine.definition
    val name   = opcode.mnemonic.raw.stripPrefix("image_")
    val mask   = machine(EncodingField.DMASK).toInt.unsafe[Int in 0 ~ 15]
    val a16    = machine.isSet(EncodingField.A16) ||
      (generation(target) == ResourceGeneration.GFX9 && machine.isSet(EncodingField.R128))
    val dimension = Option.when(machine.contains(EncodingField.DIM))(
      ImageDimension.fromOrdinal(machine(EncodingField.DIM).toInt)
    )
    val wide  = mask == 0x3 || mask == 0xf
    val swap  = name == "atomic_cmpswap" || name == "atomic_fcmpswap"
    val bytes = (if (wide && !swap || mask == 0xf) 8 else 4).unsafe[Int in (4, 8)]
    for {
      metadata <- ImageMetadata.forOpcode(opcode).orElse(Option.when(name.startsWith("atomic_"))(AtomicMetadata))
        .toRight[NonEmpty[String]](s"$opcode has no image metadata")
      data     <- machine.vector(EncodingField.VDATA)
      resource <- machine.scalar(machine.resourceField)
      count = dimension.fold(4 + (if (metadata.lodOrClampOrMip) 1 else 0))(dimension =>
        metadata.addressWords(dimension, a16, packedGradients = false)
      )
      words <- addressWords(machine, count)
      packing =
        if (!machine.isSet(EncodingField.D16)) Packing.Dword
        else halves(target, high = false)
      address = ImageAddress(words, resource, dimension, metadata.lodOrClampOrMip, a16)
      bank    = if (machine.isSet(EncodingField.ACC)) RegisterBank.Accumulator else RegisterBank.Vector
      access <-
        if (name.startsWith("atomic_"))
          imageAtomic(opcode, name.stripPrefix("atomic_"), data, address, bytes, machine).map(banked(_, bank))
        else imageOperation(opcode, name).map(Instruction.Image(_, data, address, mask, packing, bank))
      withStatus <- statusIfRequested(machine, access)
    } yield withStatus
  }

  /** An image atomic with its data and result in `bank`. */
  private def banked(access: Instruction, bank: RegisterBank): Instruction = access match {
    case atomic @ Instruction.Atomic(dest = _)           => atomic.copy(bank = bank)
    case exchange @ Instruction.CompareSwap(dest = _)    => exchange.copy(bank = bank)
    case Instruction.AtomicExtended(operation, exchange) =>
      Instruction.AtomicExtended(operation, exchange.copy(bank = bank))
    case other => other
  }

  private def imageOperation(opcode: Opcode, name: String): Result[ImageOperation] = name match {
    case "load" | "load_mip"                 => Right(ImageOperation.Load(converted = true, signed = false))
    case "load_pck" | "load_mip_pck"         => Right(ImageOperation.Load(converted = false, signed = false))
    case "load_pck_sgn" | "load_mip_pck_sgn" => Right(ImageOperation.Load(converted = false, signed = true))
    case "store" | "store_mip"               => Right(ImageOperation.Store(converted = true))
    case "store_pck" | "store_mip_pck"       => Right(ImageOperation.Store(converted = false))
    case "get_resinfo"                       => Right(ImageOperation.Dimensions)
    case _                                   => Left(s"$opcode execution is not implemented")
  }

  private def imageAtomic(
      opcode: Opcode,
      name: String,
      data: VectorRegisterIndex,
      address: ImageAddress,
      bytes: Int in (4, 8),
      machine: InstructionDecoder.Decoded
  ): Result[Instruction] =
    if (name == "cmpswap")
      offsetRegister(data, bytes / 4)
        .map(Instruction.CompareSwap(data, MemoryAddress.Texel(address), data, _, bytes, machine.returning))
    else if (name == "fcmpswap")
      offsetRegister(data, bytes / 4).map(comparand =>
        Instruction.AtomicExtended(
          ExtendedAtomicOperation.FloatCompareSwap,
          Instruction.CompareSwap(data, MemoryAddress.Texel(address), data, comparand, bytes, machine.returning)
        )
      )
    else if (name == "rsub")
      Right(Instruction.AtomicExtended(
        ExtendedAtomicOperation.ReverseSubtract,
        Instruction.CompareSwap(data, MemoryAddress.Texel(address), data, data, bytes, machine.returning)
      ))
    else
      Atomics.get(name).toRight[NonEmpty[String]](s"$opcode execution is not implemented").map { operation =>
        val width = operation match {
          case AtomicOperation.PackedHalfAdd | AtomicOperation.PackedBFloat16Add => 4.unsafe[Int in (4, 8)]
          case _                                                                 => bytes
        }
        Instruction.Atomic(operation, data, MemoryAddress.Texel(address), data, width, machine.returning)
      }

  /** The address of an atomic the LLVM dimension table names differently, as GFX12's `_uint` and `_int` forms. */
  private val AtomicMetadata = ImageMetadata(
    store = false,
    atomic = true,
    sampler = false,
    gather4 = false,
    numExtraArgs = 0,
    gradients = false,
    g16 = false,
    coordinates = true,
    lodOrClampOrMip = false,
    bvh = false
  )

  private val NonSequential =
    Vec(EncodingField.VADDRA, EncodingField.VADDRB, EncodingField.VADDRC, EncodingField.VADDRD)

  private val ImageAddresses =
    Vec(EncodingField.VADDR0, EncodingField.VADDR1, EncodingField.VADDR2, EncodingField.VADDR3, EncodingField.VADDR4)

  /** The address words: VIMAGE lists each, NSA lists those after the first, and otherwise they are consecutive. */
  private def addressWords(machine: InstructionDecoder.Decoded, count: Int): Result[Vec[VectorRegisterIndex]] =
    if (machine.contains(EncodingField.VADDR0))
      ImageAddresses.take(count).traverse[Result, VectorRegisterIndex](machine.vector(_))
    else
      machine.vector(EncodingField.VADDR).flatMap { first =>
        if (machine.format.isNonSequentialImage)
          NonSequential.filter(machine.contains(_)).take(count - 1)
            .traverse[Result, VectorRegisterIndex](machine.vector(_)).map(first +: _)
        else Right(Vec.tabulate((255 - first + 1).min(count))(index => first.offsetBy(index.unsafe[Int >= 0])))
      }
}
