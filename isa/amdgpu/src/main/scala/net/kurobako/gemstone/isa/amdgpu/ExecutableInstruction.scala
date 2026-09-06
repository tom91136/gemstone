package net.kurobako.gemstone.isa.amdgpu

private[amdgpu] enum ScalarBranchCondition {
  case Always, SccZero, SccNonZero, VccZero, VccNonZero, ExecZero, ExecNonZero
}

private[amdgpu] enum InstructionSource {
  case Scalar(index: Int)
  case VectorRegister(index: Int)
  case Constant(value: Long)

  /** A literal dword embedded in the instruction stream.
    *
    * Literals share the raw integer representation of [[Constant]], but are kept distinct because floating-point
    * instructions promote them from binary32 to their operand width rather than treating the dword as an integer value.
    */
  case Literal(value: Long)
}

private[amdgpu] enum SubDwordSelection(val width: Int, val shift: Int) {
  case Byte0 extends SubDwordSelection(8, 0)
  case Byte1 extends SubDwordSelection(8, 8)
  case Byte2 extends SubDwordSelection(8, 16)
  case Byte3 extends SubDwordSelection(8, 24)
  case Word0 extends SubDwordSelection(16, 0)
  case Word1 extends SubDwordSelection(16, 16)
  case DWord extends SubDwordSelection(32, 0)
}

private[amdgpu] enum SubDwordDestinationUnused {
  case Pad, SignExtend, Preserve
}

enum DecodedInstruction {
  case SLoad(dest: Int, count: Int, base: Int, offset: Int, scalarOffset: Option[Int])
  case SMove(dest: Int, src: InstructionSource)
  case SMoveImmediate(dest: Int, value: Int)
  case SMultiplyImmediate(dest: Int, value: Int)
  case SMove64(dest: Int, src: InstructionSource)

  /** Set a hardware register (for example the flat-scratch base). */
  case SSetReg(hwreg: Int, src: Int)
  case SGetPc(dest: Int, offset: Int)
  case SSetPc(src: InstructionSource)
  case SSwapPc(dest: Int, src: InstructionSource)

  /** Save the current EXEC mask and update EXEC with a 32- or 64-bit mask. */
  case SAndSaveExec(dest: Int, src: InstructionSource, width: Int)
  case SAndn2SaveExec(dest: Int, src: InstructionSource, width: Int)
  case SOrSaveExec(dest: Int, src: InstructionSource, width: Int)
  case SXorSaveExec(dest: Int, src: InstructionSource, width: Int)
  case SUnary(opcode: String, dest: Int, src: InstructionSource)
  case SBinary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case SBinary64(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case SSetGprIdxOn
  case SCSelect(dest: Int, srcA: InstructionSource, srcB: InstructionSource, width: Int)
  case SCmpEqI32(src: Int, immediate: Int)
  case SCmpImmediate(opcode: String, src: Int, immediate: Int)
  case SAddImmediate(dest: Int, immediate: Int)
  case SCmp(opcode: String, srcA: InstructionSource, srcB: InstructionSource)
  case VMove(dest: Int, src: InstructionSource)

  /** Read a VGPR through the scalar M0 register (v_movrels_b32). */
  case VMoveRelative(dest: Int, src: InstructionSource)

  /** Write a VGPR through the scalar M0 register (v_movreld_b32). */
  case VMoveRelativeDestination(dest: Int, src: InstructionSource)
  case VNot(dest: Int, src: InstructionSource)
  case VSwap(dest: Int, src: InstructionSource)
  case VBitReverse(dest: Int, src: InstructionSource)
  case VBitFind(opcode: String, dest: Int, src: InstructionSource)
  case VConvert(opcode: String, dest: Int, src: InstructionSource)
  case VMoveDPP(
      dest: Int,
      src: Int,
      control: Int,
      rowMask: Int,
      bankMask: Int,
      boundControl: Boolean,
      fetchInactive: Boolean
  )
  case VCndMask(
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      condition: InstructionSource,
      absA: Boolean,
      absB: Boolean,
      negateA: Boolean,
      negateB: Boolean
  )
  case VReadFirstLane(dest: Int, src: InstructionSource)
  case VReadLane(dest: Int, src: InstructionSource, lane: InstructionSource)
  case VWriteLane(dest: Int, src: InstructionSource, lane: InstructionSource)
  case VMaskBitCount(dest: Int, mask: InstructionSource, base: InstructionSource, high: Boolean)
  case VBitCount(dest: Int, src: InstructionSource, base: InstructionSource)
  case VBinary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource, clamp: Boolean)

  /** Two packed f16 products accumulated into an f32 destination. */
  case VDot2F32F16(dest: Int, srcA: InstructionSource, srcB: InstructionSource, accumulate: Boolean)

  /** Four unsigned byte products accumulated into a u32 destination. */
  case VDot4U32U8(dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VBinaryF32Modified(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      absA: Boolean,
      absB: Boolean,
      negateA: Boolean,
      negateB: Boolean,
      clamp: Boolean
  )
  case VUnaryF32Modified(
      opcode: String,
      dest: Int,
      src: InstructionSource,
      absolute: Boolean,
      negate: Boolean,
      clamp: Boolean
  )
  case VUnaryF64Modified(
      opcode: String,
      dest: Int,
      src: InstructionSource,
      absolute: Boolean,
      negate: Boolean
  )
  case VTrigPreopF64(
      dest: Int,
      src: InstructionSource,
      segment: InstructionSource,
      absolute: Boolean,
      negate: Boolean
  )
  case VFlatLoadD16(dest: Int, address: Int, offset: Int, high: Boolean)
  case VMadMkF32(dest: Int, srcA: InstructionSource, multiplier: Long, addend: InstructionSource)

  /** Fused multiply-add with an inline literal addend: dst = srcA * srcB + literal. */
  case VMadAkF32(dest: Int, srcA: InstructionSource, srcB: InstructionSource, addend: Long)
  case VBinary64(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case VBinary64Modified(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      absA: Boolean,
      absB: Boolean,
      negateA: Boolean,
      negateB: Boolean
  )
  case VBinarySubDword(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      destSelection: SubDwordSelection,
      destUnused: SubDwordDestinationUnused,
      srcASelection: SubDwordSelection,
      srcBSelection: SubDwordSelection,
      srcASignExtend: Boolean,
      srcBSignExtend: Boolean
  )
  case VPackedBinary(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcAHighForHigh: Boolean,
      srcBHighForHigh: Boolean
  )
  case VPackB32F16(dest: Int, srcA: InstructionSource, srcB: InstructionSource)
  case VPackedFmaF16(
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      lowSelection: Int,
      highSelection: Int,
      negateLow: Int,
      negateHigh: Int,
      clamp: Boolean
  )

  /** FMA with mixed f32/f16 inputs and an f16 result in one half of the destination VGPR.
    *
    * `opSelectionHi` selects which inputs are converted from f16; for those inputs `opSelection` selects the low or
    * high half of the source VGPR. The source modifier bit convention is the FP16 input convention, where bit 0 is
    * negate and bit 1 is absolute (rather than packed neg_hi).
    */
  case VFmaMixF16(
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean,
      high: Boolean
  )

  /** FMA with mixed f32/f16 inputs and an f32 result. */
  case VFmaMixF32(
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      opSelection: Int,
      opSelectionHi: Int,
      negate: Int,
      absolute: Int,
      clamp: Boolean
  )
  case VAddCarry(
      dest: Int,
      carryDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      carryIn: Option[InstructionSource]
  )
  case VSubCarry(
      dest: Int,
      carryDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      carryIn: Option[InstructionSource]
  )
  case VSubReverseCarry(
      dest: Int,
      carryDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      carryIn: Option[InstructionSource]
  )
  case VMadF32(
      opcode: String,
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      absA: Boolean,
      absB: Boolean,
      absC: Boolean,
      negateA: Boolean,
      negateB: Boolean,
      negateC: Boolean,
      clamp: Boolean
  )
  case VMadF64(
      dest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource,
      negateA: Boolean,
      negateB: Boolean,
      negateC: Boolean
  )
  case VTernary(opcode: String, dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VMadU64U32(dest: Int, carryDest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VMadI64I32(dest: Int, carryDest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VFloat64Add(dest: Int, srcA: InstructionSource, srcB: InstructionSource, negateB: Boolean)
  case VCompare(opcode: String, srcA: InstructionSource, srcB: InstructionSource)
  case VCompareSubDword(
      opcode: String,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcASelection: SubDwordSelection,
      srcBSelection: SubDwordSelection,
      srcASignExtend: Boolean,
      srcBSignExtend: Boolean,
      destination: Option[Int]
  )
  case VCompareTo(
      dest: Int,
      opcode: String,
      srcA: InstructionSource,
      srcB: InstructionSource,
      absA: Boolean,
      absB: Boolean,
      negateA: Boolean,
      negateB: Boolean
  )
  case VDivScaleF32(
      dest: Int,
      conditionDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  )
  case VDivScaleF64(
      dest: Int,
      conditionDest: Int,
      srcA: InstructionSource,
      srcB: InstructionSource,
      srcC: InstructionSource
  )
  case VDivFmasF64(dest: Int, srcA: InstructionSource, srcB: InstructionSource, srcC: InstructionSource)
  case VDivFixupF32(
      dest: Int,
      quotient: InstructionSource,
      denominator: InstructionSource,
      numerator: InstructionSource
  )
  case VDivFixupF64(
      dest: Int,
      quotient: InstructionSource,
      denominator: InstructionSource,
      numerator: InstructionSource
  )
  case VShiftLeft64(dest: Int, amount: InstructionSource, src: InstructionSource)
  case VShiftRight64(dest: Int, amount: InstructionSource, src: InstructionSource)
  case VShiftRightArithmetic64(dest: Int, amount: InstructionSource, src: InstructionSource)
  case GlobalLoad(dest: Int, address: Int, scalarAddress: Option[Int], bytes: Int, offset: Int)
  case GlobalLoadSigned(dest: Int, address: Int, scalarAddress: Option[Int], bytes: Int, offset: Int)
  case GlobalLoadD16(dest: Int, address: Int, scalarAddress: Option[Int], offset: Int, high: Boolean)
  case GlobalStore(address: Int, scalarAddress: Option[Int], src: Int, bytes: Int, offset: Int)
  case GlobalStoreD16High(address: Int, scalarAddress: Option[Int], src: Int, bytes: Int, offset: Int)
  case GlobalAtomicAdd(dest: Int, address: Int, scalarAddress: Option[Int], src: Int, bytes: Int, offset: Int)
  case GlobalAtomicInc(
      dest: Int,
      address: Int,
      scalarAddress: Option[Int],
      src: Int,
      bytes: Int,
      offset: Int,
      returnValue: Boolean
  )
  case GlobalAtomicCompareSwap(
      dest: Int,
      address: Int,
      scalarAddress: Option[Int],
      compareSrc: Int,
      swapSrc: Int,
      bytes: Int,
      offset: Int
  )
  case FlatLoad(dest: Int, address: Int, bytes: Int, offset: Int)
  case FlatLoadSigned(dest: Int, address: Int, bytes: Int, offset: Int)
  case FlatStore(address: Int, src: Int, bytes: Int, offset: Int)
  case FlatStoreD16High(address: Int, src: Int, bytes: Int, offset: Int)
  case BufferCacheInvalidate(opcode: String)
  case BufferLoad(dest: Int, address: Option[Int], resource: Int, scalarOffset: Option[Int], bytes: Int, offset: Int)
  case BufferLoadD16(
      dest: Int,
      address: Option[Int],
      resource: Int,
      scalarOffset: Option[Int],
      bytes: Int,
      offset: Int,
      high: Boolean
  )
  case BufferStore(address: Option[Int], resource: Int, scalarOffset: Option[Int], src: Int, bytes: Int, offset: Int)
  case DSRead(dest: Int, address: Int, bytes: Int, offsetA: Int, offsetB: Option[Int])
  case DSSignedRead(dest: Int, address: Int, bytes: Int, offsetA: Int, offsetB: Option[Int])
  case DSReadD16(dest: Int, address: Int, bytes: Int, offset: Int, high: Boolean)
  case DSSignedReadD16(dest: Int, address: Int, bytes: Int, offset: Int, high: Boolean)
  case DSPermute(dest: Int, address: Int, src: Int, offset: Int)
  case DSBPermute(dest: Int, address: Int, src: Int, offset: Int)
  case DSSwizzle(dest: Int, src: Int, control: Int)
  case DSAtomic(opcode: String, dest: Int, address: Int, src: Int, bytes: Int, offset: Int)
  case DSWrite(address: Int, srcA: Int, srcB: Option[Int], bytes: Int, offsetA: Int, offsetB: Option[Int])
  case DSWriteD16High(address: Int, src: Int, offset: Int)
  case DSWriteD16HighByte(address: Int, src: Int, offset: Int)
  case BranchRelative(condition: ScalarBranchCondition, displacement: Int)
  case Branch(condition: ScalarBranchCondition, target: Int)
  case Barrier
  case Wait
  case End
}

/** Primitive instruction selected for the execution hot path. Complex operations currently use [[Fallback]] while their
  * packed layouts are introduced incrementally; this keeps preparation and execution behavior identical during the
  * migration.
  */
private[amdgpu] opaque type ExecutableInstruction = Long

private[amdgpu] object ExecutableInstruction {
  private inline val TagMask       = 0xffL
  private inline val DestinationAt = 8
  private inline val SourceKindAt  = 16
  private inline val SourceValueAt = 18
  private inline val SourceMask    = 0xffffffffL

  inline val FallbackTag              = 0
  inline val VMoveTag                 = 1
  inline val GlobalLoadTag            = 2
  inline val GlobalStoreTag           = 3
  inline val VBinaryTag               = 4
  val Fallback: ExecutableInstruction = 0L

  def apply(decoded: DecodedInstruction): ExecutableInstruction = decoded match {
    case DecodedInstruction.VMove(dest, source) => VMove(dest, source)
    case DecodedInstruction.GlobalLoad(dest, address, scalarAddress, bytes, offset)
        if memoryPackable(bytes, offset) =>
      GlobalLoad(dest, address, scalarAddress, bytes, offset)
    case DecodedInstruction.GlobalStore(address, scalarAddress, src, bytes, offset)
        if memoryPackable(bytes, offset) =>
      GlobalStore(address, scalarAddress, src, bytes, offset)
    case DecodedInstruction.VBinary(opcode, dest, srcA, srcB, clamp) =>
      VBinary.pack(opcode, dest, srcA, srcB, clamp).getOrElse(Fallback)
    case _ => Fallback
  }

  def compile(decoded: IterableOnce[DecodedInstruction]): Array[ExecutableInstruction] = {
    val builder = Array.newBuilder[Long]
    decoded.iterator.foreach(instruction => builder += apply(instruction))
    builder.result()
  }

  inline def tag(instruction: ExecutableInstruction): Int              = (instruction & TagMask).toInt
  inline def vMoveDestination(instruction: ExecutableInstruction): Int = ((instruction >>> DestinationAt) & 0xffL).toInt
  inline def vMoveSourceKind(instruction: ExecutableInstruction): Int  = ((instruction >>> SourceKindAt) & 0x3L).toInt
  inline def vMoveSourceValue(instruction: ExecutableInstruction): Long = (instruction >>> SourceValueAt) & SourceMask

  inline def memoryFirstRegister(instruction: ExecutableInstruction): Int  = ((instruction >>> 8) & 0xffL).toInt
  inline def memorySecondRegister(instruction: ExecutableInstruction): Int = ((instruction >>> 16) & 0xffL).toInt
  inline def memoryScalarAddress(instruction: ExecutableInstruction): Int = (((instruction >>> 24) & 0x1ffL) - 1L).toInt
  inline def memoryBytes(instruction: ExecutableInstruction): Int         = ((instruction >>> 33) & 0x1fL).toInt
  inline def memoryOffset(instruction: ExecutableInstruction): Int        = ((instruction >>> 38) & 0x1fffL).toInt

  inline def binaryOpcode(instruction: ExecutableInstruction): Int      = ((instruction >>> 8) & 0x3fL).toInt
  inline def binaryDestination(instruction: ExecutableInstruction): Int = ((instruction >>> 14) & 0xffL).toInt
  inline def binarySourceA(instruction: ExecutableInstruction): Int     = ((instruction >>> 22) & 0x1ffL).toInt
  inline def binarySourceB(instruction: ExecutableInstruction): Int     = ((instruction >>> 31) & 0x1ffL).toInt
  inline def binaryClamp(instruction: ExecutableInstruction): Boolean   = ((instruction >>> 40) & 1L) != 0L

  private def memoryPackable(bytes: Int, offset: Int): Boolean =
    bytes >= 1 && bytes <= 8 && offset >= 0 && offset <= 0x1fff

  private def memory(
      tag: Int,
      first: Int,
      second: Int,
      scalarAddress: Option[Int],
      bytes: Int,
      offset: Int
  ): ExecutableInstruction = {
    require(first >= 0 && first <= 255, s"first VGPR must be in [0, 255], found $first")
    require(second >= 0 && second <= 255, s"second VGPR must be in [0, 255], found $second")
    require(scalarAddress.forall(index => index >= 0 && index <= 255), s"invalid scalar address $scalarAddress")
    require(bytes >= 1 && bytes <= 8, s"packed memory width must be in [1, 8], found $bytes")
    require(offset >= 0 && offset <= 0x1fff, s"packed memory offset must be in [0, 8191], found $offset")
    tag.toLong |
      (first.toLong << 8) |
      (second.toLong << 16) |
      ((scalarAddress.fold(0)(index => index + 1).toLong & 0x1ffL) << 24) |
      (bytes.toLong << 33) |
      ((offset.toLong & 0x1fffL) << 38)
  }

  object VMove {
    def apply(dest: Int, source: InstructionSource): ExecutableInstruction = {
      require(dest >= 0 && dest <= 255, s"VGPR destination must be in [0, 255], found $dest")
      val (kind, value) = source match {
        case InstructionSource.Scalar(index) =>
          require(index >= 0 && index <= 255, s"SGPR source must be in [0, 255], found $index")
          0L -> index.toLong
        case InstructionSource.VectorRegister(index) =>
          require(index >= 0 && index <= 255, s"VGPR source must be in [0, 255], found $index")
          1L -> index.toLong
        case InstructionSource.Constant(raw) => 2L -> (raw & SourceMask)
        case InstructionSource.Literal(raw)  => 2L -> (raw & SourceMask)
      }
      VMoveTag.toLong |
        (dest.toLong << DestinationAt) |
        (kind << SourceKindAt) |
        (value << SourceValueAt)
    }

    def unapply(instruction: ExecutableInstruction): Option[(Int, InstructionSource)] =
      if (tag(instruction) != VMoveTag) None
      else
        vMoveSourceKind(instruction) match {
          case 0 => Some(vMoveDestination(instruction) -> InstructionSource.Scalar(vMoveSourceValue(instruction).toInt))
          case 1 =>
            Some(vMoveDestination(instruction) -> InstructionSource.VectorRegister(vMoveSourceValue(instruction).toInt))
          case 2 => Some(vMoveDestination(instruction) -> InstructionSource.Constant(vMoveSourceValue(instruction)))
          case _ => None
        }
  }

  object GlobalLoad {
    def apply(
        dest: Int,
        address: Int,
        scalarAddress: Option[Int],
        bytes: Int,
        offset: Int
    ): ExecutableInstruction = memory(GlobalLoadTag, dest, address, scalarAddress, bytes, offset)

    def unapply(instruction: ExecutableInstruction): Option[(Int, Int, Option[Int], Int, Int)] =
      Option.when(tag(instruction) == GlobalLoadTag)(
        (
          memoryFirstRegister(instruction),
          memorySecondRegister(instruction),
          Option.when(memoryScalarAddress(instruction) >= 0)(memoryScalarAddress(instruction)),
          memoryBytes(instruction),
          memoryOffset(instruction)
        )
      )
  }

  object GlobalStore {
    def apply(
        address: Int,
        scalarAddress: Option[Int],
        src: Int,
        bytes: Int,
        offset: Int
    ): ExecutableInstruction = memory(GlobalStoreTag, address, src, scalarAddress, bytes, offset)

    def unapply(instruction: ExecutableInstruction): Option[(Int, Option[Int], Int, Int, Int)] =
      Option.when(tag(instruction) == GlobalStoreTag)(
        (
          memoryFirstRegister(instruction),
          Option.when(memoryScalarAddress(instruction) >= 0)(memoryScalarAddress(instruction)),
          memorySecondRegister(instruction),
          memoryBytes(instruction),
          memoryOffset(instruction)
        )
      )
  }

  object VBinary {
    inline val AddU32    = 1
    inline val MulLoU32  = 2
    inline val MulHiU32  = 3
    inline val AndB32    = 4
    inline val OrB32     = 5
    inline val XorB32    = 6
    inline val LshlB32   = 7
    inline val LshrB32   = 8
    inline val SubU32    = 9
    inline val SubrevU32 = 10
    inline val AddF32    = 11
    inline val MulF32    = 12
    inline val MacF32    = 13
    inline val SubF32    = 14
    inline val MinF32    = 15
    inline val MaxF32    = 16
    inline val AddF16    = 17
    inline val MulF16    = 18
    inline val MinF16    = 19
    inline val MaxF16    = 20
    inline val AddU16    = 21
    inline val MulLoU16  = 22
    inline val LshlB16   = 23
    inline val LshrB16   = 24
    inline val AshrI16   = 25
    inline val AshrI32   = 26
    inline val MacF16    = 27

    private def opcodeId(opcode: String): Int = opcode match {
      case "v_add_u32"     => AddU32
      case "v_mul_lo_u32"  => MulLoU32
      case "v_mul_hi_u32"  => MulHiU32
      case "v_and_b32"     => AndB32
      case "v_or_b32"      => OrB32
      case "v_xor_b32"     => XorB32
      case "v_lshlrev_b32" => LshlB32
      case "v_lshrrev_b32" => LshrB32
      case "v_sub_u32"     => SubU32
      case "v_subrev_u32"  => SubrevU32
      case "v_add_f32"     => AddF32
      case "v_mul_f32"     => MulF32
      case "v_mac_f32"     => MacF32
      case "v_sub_f32"     => SubF32
      case "v_min_f32"     => MinF32
      case "v_max_f32"     => MaxF32
      case "v_add_f16"     => AddF16
      case "v_mul_f16"     => MulF16
      case "v_min_f16"     => MinF16
      case "v_max_f16"     => MaxF16
      case "v_fmac_f16"    => MacF16
      case "v_add_u16"     => AddU16
      case "v_mul_lo_u16"  => MulLoU16
      case "v_lshlrev_b16" => LshlB16
      case "v_lshrrev_b16" => LshrB16
      case "v_ashrrev_i16" => AshrI16
      case "v_ashrrev_i32" => AshrI32
      case _               => 0
    }

    def opcodeName(opcode: Int): String = opcode match {
      case AddU32    => "v_add_u32"
      case MulLoU32  => "v_mul_lo_u32"
      case MulHiU32  => "v_mul_hi_u32"
      case AndB32    => "v_and_b32"
      case OrB32     => "v_or_b32"
      case XorB32    => "v_xor_b32"
      case LshlB32   => "v_lshlrev_b32"
      case LshrB32   => "v_lshrrev_b32"
      case SubU32    => "v_sub_u32"
      case SubrevU32 => "v_subrev_u32"
      case AddF32    => "v_add_f32"
      case MulF32    => "v_mul_f32"
      case MacF32    => "v_mac_f32"
      case SubF32    => "v_sub_f32"
      case MinF32    => "v_min_f32"
      case MaxF32    => "v_max_f32"
      case AddF16    => "v_add_f16"
      case MulF16    => "v_mul_f16"
      case MinF16    => "v_min_f16"
      case MaxF16    => "v_max_f16"
      case MacF16    => "v_fmac_f16"
      case AddU16    => "v_add_u16"
      case MulLoU16  => "v_mul_lo_u16"
      case LshlB16   => "v_lshlrev_b16"
      case LshrB16   => "v_lshrrev_b16"
      case AshrI16   => "v_ashrrev_i16"
      case AshrI32   => "v_ashrrev_i32"
      case other     => s"unknown-vbinary-$other"
    }

    private def register(source: InstructionSource): Option[Int] = source match {
      case InstructionSource.Scalar(index) if index >= 0 && index <= 255         => Some(index)
      case InstructionSource.VectorRegister(index) if index >= 0 && index <= 255 => Some(0x100 | index)
      case _                                                                     => None
    }

    def pack(
        opcode: String,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        clamp: Boolean
    ): Option[ExecutableInstruction] = for {
      operation = opcodeId(opcode)
      _     <- Option.when(operation != 0)(())
      left  <- register(srcA)
      right <- register(srcB)
      _     <- Option.when(dest >= 0 && dest <= 255)(())
    } yield VBinaryTag.toLong |
      (operation.toLong << 8) |
      (dest.toLong << 14) |
      (left.toLong << 22) |
      (right.toLong << 31) |
      (if (clamp) 1L << 40 else 0L)

    def apply(
        opcode: String,
        dest: Int,
        srcA: InstructionSource,
        srcB: InstructionSource,
        clamp: Boolean
    ): ExecutableInstruction =
      pack(opcode, dest, srcA, srcB, clamp).getOrElse(
        throw IllegalArgumentException(s"VBinary is not packable: $opcode $dest $srcA $srcB clamp=$clamp")
      )

    private def source(value: Int): InstructionSource =
      if ((value & 0x100) == 0) InstructionSource.Scalar(value)
      else InstructionSource.VectorRegister(value & 0xff)

    def unapply(
        instruction: ExecutableInstruction
    ): Option[(String, Int, InstructionSource, InstructionSource, Boolean)] =
      Option.when(tag(instruction) == VBinaryTag)(
        (
          opcodeName(binaryOpcode(instruction)),
          binaryDestination(instruction),
          source(binarySourceA(instruction)),
          source(binarySourceB(instruction)),
          binaryClamp(instruction)
        )
      )
  }
}
