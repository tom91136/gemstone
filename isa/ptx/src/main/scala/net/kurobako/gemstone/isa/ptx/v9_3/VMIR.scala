package net.kurobako.gemstone.isa.ptx.v9_3

import Program.*

import net.kurobako.gemstone.Vec

import net.kurobako.gemstone.binary.{BitWidth as BinaryBitWidth, ByteAlignedBitWidth, LittleEndian, ScalarByteWidth}
import net.kurobako.gemstone.isa.KernelName
import net.kurobako.gemstone.memory.GuestAddress
import net.kurobako.gemstone.vm.LaunchGeometry.Axis

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

private[v9_3] object VMIR {

  type BitWidth = BitWidth.T
  object BitWidth extends RefinedType[Int, Interval.Closed[8, 64] & Multiple[8]] {
    extension (width: BitWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asByteAlignedBitWidth: ByteAlignedBitWidth = ByteAlignedBitWidth.assume(width.value)
    }
  }

  type FloatWidth = FloatWidth.T
  object FloatWidth extends RefinedType[Int, In[(16, 32, 64)]] {
    extension (width: FloatWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asByteAlignedBitWidth: ByteAlignedBitWidth = ByteAlignedBitWidth.assume(width.value)
    }
  }

  type PartWidth = PartWidth.T
  object PartWidth extends RefinedType[Int, In[(16, 32)]] {
    extension (width: PartWidth) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def asBitWidth: BinaryBitWidth = BinaryBitWidth.assume(width.value)

      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      private[v9_3] def combined: BitWidth = BitWidth.assume(width.value * 2)
    }
  }

  enum Value {
    case Register(name: RegisterName)
    case RegisterSlot(index: Int)
    case Predicate(name: RegisterName)
    case PredicateSlot(index: Int)
    case Function(name: FunctionName)
    case Symbol(name: SymbolName)
    case Immediate(value: Long)
    case Address(base: Value, offset: Long)
    case LaneId
    case ActiveMask
    case LaneMaskGreaterEqual
    case LaneMaskGreater
    case LaneMaskLessEqual
    case LaneMaskLess
    case LaneMaskEqual
    case ThreadId(axis: Axis)
    case BlockId(axis: Axis)
    case BlockDim(axis: Axis)
    case GridDim(axis: Axis)
  }

  enum IntegerOperation {
    case Add
    case Subtract
    case BitwiseOr
    case BitwiseXor
    case BitwiseAnd
    case MultiplyLow
    case MultiplyHigh(signed: Boolean)
    case Divide(signed: Boolean)
    case Remainder(signed: Boolean)
    case MultiplyWideUnsigned16
    case MultiplyWideSigned16
    case MultiplyWideUnsigned32
    case MultiplyWideSigned32
    case Minimum(signed: Boolean)
    case Maximum(signed: Boolean)
    case ShiftLeft
    case ShiftRightLogical
    case ShiftRightArithmetic
  }

  enum IntegerUnaryOperation {
    case Negate
    case Absolute
    case BitwiseNot
    case PopulationCount
    case LeadingZeroCount
    case BitReverse
  }

  enum PredicateOperation {
    case And
    case Or
    case Xor
  }

  enum VoteOperation {
    case All
    case Any
    case Uniform
  }

  enum BitMaskMode(val mnemonic: String) {
    case Clamp extends BitMaskMode("clamp")
    case Wrap  extends BitMaskMode("wrap")
  }

  enum BytePermuteMode(val suffix: String) {
    case Default extends BytePermuteMode("")
    case F4E     extends BytePermuteMode("f4e")
    case B4E     extends BytePermuteMode("b4e")
    case RC8     extends BytePermuteMode("rc8")
    case ECL     extends BytePermuteMode("ecl")
    case ECR     extends BytePermuteMode("ecr")
    case RC16    extends BytePermuteMode("rc16")
  }

  enum ConversionType(val mnemonic: String, val width: BitWidth, val kind: Char) {
    case B8   extends ConversionType("b8", BitWidth(8), 'b')
    case S8   extends ConversionType("s8", BitWidth(8), 's')
    case U8   extends ConversionType("u8", BitWidth(8), 'u')
    case B16  extends ConversionType("b16", BitWidth(16), 'b')
    case S16  extends ConversionType("s16", BitWidth(16), 's')
    case U16  extends ConversionType("u16", BitWidth(16), 'u')
    case F16  extends ConversionType("f16", BitWidth(16), 'f')
    case BF16 extends ConversionType("bf16", BitWidth(16), 'f')
    case B32  extends ConversionType("b32", BitWidth(32), 'b')
    case S32  extends ConversionType("s32", BitWidth(32), 's')
    case U32  extends ConversionType("u32", BitWidth(32), 'u')
    case F32  extends ConversionType("f32", BitWidth(32), 'f')
    case B64  extends ConversionType("b64", BitWidth(64), 'b')
    case S64  extends ConversionType("s64", BitWidth(64), 's')
    case U64  extends ConversionType("u64", BitWidth(64), 'u')
    case F64  extends ConversionType("f64", BitWidth(64), 'f')
  }

  enum ConversionRounding(val mnemonic: String) {
    case RN  extends ConversionRounding("rn")
    case RNI extends ConversionRounding("rni")
    case RZ  extends ConversionRounding("rz")
    case RZI extends ConversionRounding("rzi")
    case RM  extends ConversionRounding("rm")
    case RMI extends ConversionRounding("rmi")
    case RP  extends ConversionRounding("rp")
    case RPI extends ConversionRounding("rpi")
  }

  enum Comparison {
    case Ordered
    case Unordered
    case Equal
    case EqualUnordered
    case NotEqual
    case NotEqualUnordered
    case LessThan
    case LessThanUnordered
    case LessThanOrEqual
    case LessThanOrEqualUnordered
    case GreaterThan
    case GreaterThanUnordered
    case GreaterThanOrEqual
    case GreaterThanOrEqualUnordered
  }

  enum FloatOperation {
    case Add
    case Subtract
    case Multiply
    case Divide
    case Minimum
    case Maximum
    case CopySign
  }

  enum FloatUnaryOperation {
    case Negate
    case Absolute
    case Reciprocal
    case SquareRoot
    case ReciprocalSquareRoot
    case Exponent2
    case Log2
    case Sine
    case Cosine
    case Tanh
  }

  enum IntegerMultiplyAddOperation {
    case Low(operandWidth: BitWidth, resultWidth: BitWidth, carryIn: Boolean, carryOut: Boolean)
    case High(operandWidth: BitWidth, resultWidth: BitWidth, carryIn: Boolean, carryOut: Boolean)
  }

  private def atomicBinaryOpcode(operation: IntegerOperation, width: BitWidth): String = operation match {
    case IntegerOperation.Maximum(false) => s"max.u$width"
    case IntegerOperation.BitwiseAnd     => s"and.b$width"
    case IntegerOperation.BitwiseOr      => s"or.b$width"
    case IntegerOperation.BitwiseXor     => s"xor.b$width"
    case other                           => s"binary.$other"
  }

  enum Instruction(private val opcodeText: String) {
    final val opcode: Opcode = Opcode.applyUnsafe(opcodeText)

    case LoadParameter(
        destinations: Vec[RegisterName],
        parameter: ParameterName,
        offset: Int :| Positive0,
        elementWidth: BitWidth,
        signed: Boolean
    ) extends Instruction(s"ld.param.${if (signed) "s" else "u"}$elementWidth")

    case StoreParameter(parameter: ParameterName, offset: Int :| Positive0, source: Value, elementWidth: BitWidth)
        extends Instruction(s"st.param.u$elementWidth")

    case LoadParameterIndirect(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.param.${if (signed) "s" else "u"}$width")

    case LoadParameterIndirectVector(
        destinations: Vec[RegisterName],
        address: Value,
        width: BitWidth,
        signed: Boolean
    ) extends Instruction(s"ld.param.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case Move(destination: RegisterName, source: Value, width: BitWidth)
        extends Instruction(s"mov.u$width")

    case MovePredicate(destination: RegisterName, source: Value) extends Instruction("mov.pred")

    case MoveSplit(destinations: Vec[RegisterName], source: Value, partWidth: PartWidth)
        extends Instruction(s"mov.b${partWidth.value * 2}")

    case MoveCombine(destination: RegisterName, sources: Vec[Value], partWidth: PartWidth)
        extends Instruction(s"mov.b${partWidth.value * 2}")

    case IntegerUnary(operation: IntegerUnaryOperation, destination: RegisterName, source: Value, width: BitWidth)
        extends Instruction(operation match {
          case IntegerUnaryOperation.Negate           => s"neg.b$width"
          case IntegerUnaryOperation.Absolute         => s"abs.b$width"
          case IntegerUnaryOperation.BitwiseNot       => s"not.b$width"
          case IntegerUnaryOperation.PopulationCount  => s"popc.b$width"
          case IntegerUnaryOperation.LeadingZeroCount => s"clz.b$width"
          case IntegerUnaryOperation.BitReverse       => s"brev.b$width"
        })

    case BitFind(
        destination: RegisterName,
        source: Value,
        width: BitWidth,
        signed: Boolean,
        shiftAmount: Boolean
    ) extends Instruction(s"bfind.${if (shiftAmount) "shiftamt." else ""}${if (signed) "s" else "u"}$width")

    case DotProduct4(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        leftSigned: Boolean,
        rightSigned: Boolean
    ) extends Instruction(s"dp4a.${if (leftSigned) "s" else "u"}32.${if (rightSigned) "s" else "u"}32")

    case IntegerBinary(
        operation: IntegerOperation,
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth
    ) extends Instruction(
          operation match {
            case IntegerOperation.Add                    => s"add.u$width"
            case IntegerOperation.Subtract               => s"sub.u$width"
            case IntegerOperation.BitwiseOr              => s"or.b$width"
            case IntegerOperation.BitwiseAnd             => s"and.b$width"
            case IntegerOperation.BitwiseXor             => s"xor.b$width"
            case IntegerOperation.MultiplyLow            => s"mul.lo.u$width"
            case IntegerOperation.MultiplyHigh(signed)   => s"mul.hi.${if (signed) "s" else "u"}$width"
            case IntegerOperation.Divide(signed)         => s"div.${if (signed) "s" else "u"}$width"
            case IntegerOperation.Remainder(signed)      => s"rem.${if (signed) "s" else "u"}$width"
            case IntegerOperation.MultiplyWideUnsigned16 => "mul.wide.u16"
            case IntegerOperation.MultiplyWideSigned16   => "mul.wide.s16"
            case IntegerOperation.MultiplyWideUnsigned32 => "mul.wide.u32"
            case IntegerOperation.MultiplyWideSigned32   => "mul.wide.s32"
            case IntegerOperation.Minimum(signed)        => s"min.${if (signed) "s" else "u"}$width"
            case IntegerOperation.Maximum(signed)        => s"max.${if (signed) "s" else "u"}$width"
            case IntegerOperation.ShiftLeft              => s"shl.b$width"
            case IntegerOperation.ShiftRightLogical      => s"shr.u$width"
            case IntegerOperation.ShiftRightArithmetic   => s"shr.s$width"
          }
        )

    case FunnelShift(
        destination: RegisterName,
        low: Value,
        high: Value,
        amount: Value,
        left: Boolean,
        clamp: Boolean
    ) extends Instruction(s"shf.${if (left) "l" else "r"}.${if (clamp) "clamp" else "wrap"}.b32")

    case IntegerAddWithCarry(
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    ) extends Instruction(s"add${if (carryIn) "c" else ""}${if (carryOut) ".cc" else ""}.u$width")

    case IntegerSubWithBorrow(
        destination: RegisterName,
        left: Value,
        right: Value,
        width: BitWidth,
        borrowIn: Boolean,
        borrowOut: Boolean
    ) extends Instruction(s"sub${if (borrowIn) "c" else ""}${if (borrowOut) ".cc" else ""}.u$width")

    case MultiplyAdd(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        operation: IntegerMultiplyAddOperation
    ) extends Instruction(operation match {
          case IntegerMultiplyAddOperation.Low(operandWidth, _, _, _)  => s"mad.lo.u$operandWidth"
          case IntegerMultiplyAddOperation.High(operandWidth, _, _, _) => s"mad.hi.u$operandWidth"
        })

    case FloatBinary(
        operation: FloatOperation,
        destination: RegisterName,
        left: Value,
        right: Value,
        width: FloatWidth
    ) extends Instruction(s"${operation.toString.map(_.toLower)}.f$width")

    case FloatBinaryPacked(
        operation: FloatOperation,
        destination: RegisterName,
        left: Value,
        right: Value,
        bf16: Boolean
    ) extends Instruction(
          s"${operation.toString.map(_.toLower)}.${if (bf16) "bf16x2" else "f16x2"}"
        )

    case FloatUnary(operation: FloatUnaryOperation, destination: RegisterName, source: Value, width: FloatWidth)
        extends Instruction(operation match {
          case FloatUnaryOperation.Negate               => s"neg.f$width"
          case FloatUnaryOperation.Absolute             => s"abs.f$width"
          case FloatUnaryOperation.Reciprocal           => s"rcp.f$width"
          case FloatUnaryOperation.SquareRoot           => s"sqrt.f$width"
          case FloatUnaryOperation.ReciprocalSquareRoot => s"rsqrt.f$width"
          case FloatUnaryOperation.Exponent2            => s"ex2.f$width"
          case FloatUnaryOperation.Log2                 => s"lg2.f$width"
          case FloatUnaryOperation.Sine                 => s"sin.f$width"
          case FloatUnaryOperation.Cosine               => s"cos.f$width"
          case FloatUnaryOperation.Tanh                 => s"tanh.f$width"
        })

    case FusedMultiplyAdd(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        width: FloatWidth
    ) extends Instruction(s"fma.f$width")

    case FusedMultiplyAddBf16(destination: RegisterName, left: Value, right: Value, addend: Value)
        extends Instruction("fma.rn.bf16")

    case FusedMultiplyAddPacked(
        destination: RegisterName,
        left: Value,
        right: Value,
        addend: Value,
        bf16: Boolean
    ) extends Instruction(s"fma.rn.${if (bf16) "bf16x2" else "f16x2"}")

    case ShuffleDown(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.down.b$width")

    case ShuffleUp(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        delta: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.up.b$width")

    case ShuffleIndex(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.idx.b$width")

    case ShuffleBfly(
        destination: RegisterName,
        validity: Option[RegisterName],
        source: Value,
        index: Value,
        clamp: Value,
        memberMask: Value,
        width: BitWidth
    ) extends Instruction(s"shfl.sync.bfly.b$width")

    case VoteBallot(destination: RegisterName, predicate: RegisterName, memberMask: Value)
        extends Instruction("vote.sync.ballot.b32")

    case VotePredicate(
        destination: RegisterName,
        predicate: RegisterName,
        memberMask: Value,
        operation: VoteOperation
    ) extends Instruction(s"vote.sync.${operation.toString.map(_.toLower)}.pred")

    case BitFieldExtract(
        destination: RegisterName,
        source: Value,
        start: Value,
        widthValue: Value,
        width: BitWidth,
        signed: Boolean
    ) extends Instruction(s"bfe.${if (signed) "s" else "u"}$width")

    case BitFieldInsert(
        destination: RegisterName,
        insertValue: Value,
        baseValue: Value,
        bitPosition: Value,
        bitLength: Value,
        width: BitWidth
    ) extends Instruction(s"bfi.b$width")

    case BitMask(destination: RegisterName, start: Value, widthValue: Value, mode: BitMaskMode)
        extends Instruction(s"bmsk.${mode.mnemonic}.b32")

    case BytePermute(
        destination: RegisterName,
        left: Value,
        right: Value,
        control: Value,
        mode: BytePermuteMode
    ) extends Instruction(s"prmt.b32${if (mode == BytePermuteMode.Default) "" else s".${mode.suffix}"}")

    case Select(
        destination: RegisterName,
        whenTrue: Value,
        whenFalse: Value,
        predicate: RegisterName,
        width: BitWidth
    ) extends Instruction(s"selp.b$width")

    case Convert(
        destination: RegisterName,
        source: Value,
        sourceType: ConversionType,
        destinationType: ConversionType,
        rounding: Option[ConversionRounding]
    ) extends Instruction(
          s"cvt.${rounding.map(_.mnemonic + ".").getOrElse("")}${destinationType.mnemonic}.${sourceType.mnemonic}"
        )

    case LoadGlobal(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.global.${if (signed) "s" else "u"}$width")

    case LoadGeneric(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.generic.${if (signed) "s" else "u"}$width")

    case DiscardGlobalLoad(address: Value, width: BitWidth)
        extends Instruction(s"ld.global.nc.u$width")

    case LoadGlobalVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.global.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadGenericVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.generic.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreGlobal(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.global.u$width")

    case StoreGeneric(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.generic.u$width")

    case StoreGlobalVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.global.v${sources.size}.u$width")

    case StoreGenericVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.generic.v${sources.size}.u$width")

    case LoadShared(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.shared.${if (signed) "s" else "u"}$width")

    case LoadSharedVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.shared.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case LoadLocal(destination: RegisterName, address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.local.${if (signed) "s" else "u"}$width")

    case LoadLocalVector(destinations: Vec[RegisterName], address: Value, width: BitWidth, signed: Boolean)
        extends Instruction(s"ld.local.v${destinations.size}.${if (signed) "s" else "u"}$width")

    case StoreShared(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.shared.u$width")

    case StoreSharedVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.shared.v${sources.size}.u$width")

    case StoreLocal(address: Value, source: Value, width: BitWidth)
        extends Instruction(s"st.local.u$width")

    case StoreLocalVector(address: Value, sources: Vec[Value], width: BitWidth)
        extends Instruction(s"st.local.v${sources.size}.u$width")

    /** Synchronous model of the PTX asynchronous shared-memory copy. The emulator applies the copy at this instruction;
      * commit/wait operations below remain ordered no-ops because there is no separate copy engine.
      */
    case AsyncCopy(destination: Value, source: Value, size: Int :| Positive)
        extends Instruction("cp.async")

    case AsyncCommit extends Instruction("cp.async.commit_group")

    case AsyncWait extends Instruction("cp.async.wait_group")

    /** Cache prefetch hints have no architecturally visible effect in the emulator; preserve them as ordered no-ops so
      * generated kernels remain loadable and predication is still honoured.
      */
    case Prefetch extends Instruction("prefetch")

    case SetPredicate(
        destination: RegisterName,
        comparison: Comparison,
        kind: Char,
        left: Value,
        right: Value,
        width: BitWidth
    ) extends Instruction("setp")

    case PredicateCompare(
        destination: RegisterName,
        comparison: Comparison,
        left: RegisterName,
        right: RegisterName
    ) extends Instruction("setp.pred")

    case PredicateUnary(destination: RegisterName, source: RegisterName)
        extends Instruction("not.pred")

    case PredicateBinary(
        operation: PredicateOperation,
        destination: RegisterName,
        left: RegisterName,
        right: RegisterName
    ) extends Instruction(s"${operation.toString.map(_.toLower)}.pred")

    case Branch(target: Pc) extends Instruction("bra")

    case BarrierSync(id: Int :| Positive0) extends Instruction("bar.sync")

    case BarrierReduce(
        destination: RegisterName,
        id: Int :| Positive0,
        predicate: RegisterName,
        operation: PredicateOperation
    ) extends Instruction(s"bar.red.${operation.toString.map(_.toLower)}.pred")

    case AtomicAdd(destination: RegisterName, address: Value, source: Value, width: BitWidth, global: Option[Boolean])
        extends Instruction(s"atom.${global.fold("")(if (_) "global." else "shared.")}add.u$width")

    case AtomicExchange(
        destination: RegisterName,
        address: Value,
        source: Value,
        width: BitWidth,
        global: Option[Boolean]
    ) extends Instruction(s"atom.${global.fold("")(if (_) "global." else "shared.")}exch.b$width")

    case AtomicBinary(
        destination: RegisterName,
        address: Value,
        source: Value,
        width: BitWidth,
        global: Option[Boolean],
        operation: IntegerOperation
    ) extends Instruction(
          s"atom.${global.fold("")(if (_) "global." else "shared.")}${atomicBinaryOpcode(operation, width)}"
        )

    case AtomicMin(destination: RegisterName, address: Value, source: Value, width: BitWidth, global: Option[Boolean])
        extends Instruction(s"atom.${global.fold("")(if (_) "global." else "shared.")}min.u$width")

    case AtomicAddFloat(destination: RegisterName, address: Value, source: Value, width: FloatWidth)
        extends Instruction(s"atom.global.add.f$width")

    case WarpReduce(
        destination: RegisterName,
        source: Value,
        memberMask: Value,
        operation: IntegerOperation,
        width: BitWidth
    ) extends Instruction("redux.sync")

    case MemoryBarrier extends Instruction("membar")

    case WarpBarrier(memberMask: Value) extends Instruction("bar.warp.sync")

    case Call(function: Value, returnParameters: Vec[ParameterName], parameters: Vec[ParameterName])
        extends Instruction("call")

    case Trap extends Instruction("trap")

    case Return extends Instruction("ret")
  }

  final case class ExecutionPredicate(name: RegisterName, negated: Boolean)

  final case class PredicatedInstruction(instruction: Instruction, predication: Option[ExecutionPredicate]) {
    def opcode: Opcode = instruction.opcode
  }

  final case class KernelParameter(name: ParameterName, widthBytes: Int :| Positive, pointer: Boolean = false)
  final case class Argument(bytes: Vec[Byte] :| MinLength[1])

  final case class DeviceFunction(
      name: FunctionName,
      parameters: Vec[ParameterName],
      parameterWidths: Vec[Int :| Positive],
      parameterOffsets: Vec[Int :| Positive0],
      returnParameters: Vec[ParameterName],
      program: ExecutableProgram[PredicatedInstruction],
      sharedSymbols: Map[SymbolName, Long]
  )

  object Argument {
    def apply(value: Long): Argument =
      Argument(LittleEndian.toVec(value, ScalarByteWidth(8)).refineUnsafe[MinLength[1]])
  }

  final case class Kernel(
      name: KernelName,
      parameters: Vec[KernelParameter],
      program: ExecutableProgram[PredicatedInstruction],
      functions: Map[FunctionName, DeviceFunction] = Map.empty,
      sharedSymbols: Map[SymbolName, Long] = Map.empty,
      globalSymbols: Map[SymbolName, GuestAddress] = Map.empty
  ) {
    def instructions: Vec[Instruction] = program.instructions.map(_.instruction)
  }

  type Pc = Pc.T
  object Pc extends RefinedType[Int, Positive0] {
    val Entry: Pc = Pc(0)

    extension (pc: Pc) {
      @SuppressWarnings(Array("scalafix:GemstoneNoAssume"))
      def next: Option[Pc] =
        Option.when(pc.value != Int.MaxValue)(Pc.assume(pc.value + 1))
    }
  }

  final case class ExecutableProgram[Instruction](instructions: Vec[Instruction]) {
    def size: Int = instructions.size

    def at(pc: Pc): Option[Instruction] = instructions.lift(pc.value)
  }

}
