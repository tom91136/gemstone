package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

object VMIR {

  /** A dense general-register slot within one routine. */
  type Register = Register.Type
  object Register extends Newtype[Int >= 0]

  /** A dense predicate slot within one routine. */
  type Predicate = Predicate.Type
  object Predicate extends Newtype[Int >= 0]

  /** A dense slot for per-lane `.param` storage within one routine. */
  type Parameter = Parameter.Type
  object Parameter extends Newtype[Int >= 0]

  enum ConvergenceAction { case Setup, Synchronise, Break }

  extension (width: Int in (16, 32)) {
    def combined: Int in (32, 64) = (width.raw * 2).unsafe[Int in (32, 64)]
  }

  enum Value {
    case Register(slot: VMIR.Register)
    case Predicate(slot: VMIR.Predicate)
    case Immediate(value: Long)
    case Address(base: Value, offset: Long)

    /** The generic address of the executing frame's local window. */
    case LocalBase
    case LaneId
    case DynamicSharedBytes
    case ActiveMask
    case LiveMask
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

  enum AddThreeCarryMode {
    case None
    case Produce
    case Consume
  }

  enum IntegerOperation {
    case Add
    case Subtract
    case AddSaturated
    case SubtractSaturated
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
    case LogicalNot
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

  enum ShuffleMode {
    case Up, Down, Index, Butterfly
  }

  enum BitMaskMode {
    case Clamp, Wrap
  }

  enum BytePermuteMode {
    case Default, F4E, B4E, RC8, ECL, ECR, RC16
  }

  enum NumericFormat(val width: ScalarBitWidth) {
    case Bits(bits: Int in (8, 16, 32, 64))     extends NumericFormat(bits.widen)
    case Signed(bits: Int in (8, 16, 32, 64))   extends NumericFormat(bits.widen)
    case Unsigned(bits: Int in (8, 16, 32, 64)) extends NumericFormat(bits.widen)
    case IEEE(bits: Int in (16, 32, 64))        extends NumericFormat(bits.widen)
    case BF16                                   extends NumericFormat(16)
  }

  enum ConversionRounding {
    case RN, RNI, RZ, RZI, RM, RMI, RP, RPI
  }

  /** Range limit applied to a conversion result: `Saturate` bounds floats to [0, 1] and integers to the destination
    * range, `Relu` clamps negative floats to zero.
    */
  enum ConversionClamp {
    case Saturate, Relu
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

  case class FloatControl(
      flushSubnormal: Boolean = false,
      saturate: Boolean = false,
      propagateNaN: Boolean = false,
      absolute: Boolean = false,
      xorSign: Boolean = false
  )

  enum PackedFloatFormat {
    case F16x2, BF16x2, F32x2
  }

  enum FloatOperation {
    case Add
    case Subtract
    case Multiply
    case Divide
    case Minimum
    case Maximum
    case MinimumNumber
    case MaximumNumber
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
    case Low(
        operandWidth: ScalarBitWidth,
        resultWidth: ScalarBitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    )
    case High(
        operandWidth: ScalarBitWidth,
        resultWidth: ScalarBitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    )
  }

  /** Memory space of an atomic or matrix access: explicit global/shared, or generic resolved from the address. */
  enum MemorySpace {
    case Generic, Global, Shared
  }

  /** Memory domain and visibility carried by ordinary load/store instructions. */
  enum MemoryAccess {
    case Global(system: Boolean)
    case Generic(system: Boolean)
    case Shared
    case Local

    def generic: Boolean = this match {
      case Generic(_) => true
      case _          => false
    }

    def systemScoped: Boolean = this match {
      case Global(true) | Generic(true) => true
      case _                            => false
    }
  }

  /** CUDA device-runtime entry points executed by the emulator rather than by guest code. */
  enum Builtin {
    case GetDevice
    case DeviceAttribute
    case Occupancy
    case NoError
    case ParameterBuffer
    case Malloc
    case Free
    case LaunchDevice
  }

  /** Warp-level matrix operand types and the operations over them. */
  object Matrix {
    import net.kurobako.gemstone.constraint.auto.*

    enum Element(val bits: Int in (8, 16, 32, 64), val packed: Int > 0) {
      case F16  extends Element(16, 2)
      case BF16 extends Element(16, 2)
      case TF32 extends Element(32, 1)
      case F32  extends Element(32, 1)
      case F64  extends Element(64, 1)
      case S8   extends Element(8, 4)
      case U8   extends Element(8, 4)
      case S32  extends Element(32, 1)

      def decode(value: Long): Double = this match {
        case F16        => IEEE754.toDouble(value, 16)
        case BF16       => IEEE754.bfloat16ToFloat(Binary16.low(value)).toDouble
        case TF32 | F32 => IEEE754.toDouble(value, 32)
        case F64        => IEEE754.binary64ToDouble(value)
        case S8         => value.toByte.toDouble
        case U8         => (value & 255L).toDouble
        case S32        => value.toInt.toDouble
      }
      def encode(value: Double): Long = this match {
        case F16        => IEEE754.fromDouble(value, 16)
        case F32 | TF32 => IEEE754.fromDouble(value, 32)
        case F64        => IEEE754.doubleToBinary64(value)
        case BF16       => IEEE754.doubleToBfloat16(value, IEEE754.Rounding.TowardZero).toLong
        case S8 | U8    => value.toLong & 255L
        case S32        => value.toLong & 0xffffffffL
      }
    }
    case class Shape(m: Int > 0, n: Int > 0, k: Int > 0)
    enum Role { case A, B, C }

    /** Element types, shape and layouts accepted by `mma` (or `wmma` with `wmma` set): D = A * B + C. */
    def supportsMultiply(
        shape: Shape,
        a: Element,
        b: Element,
        c: Element,
        d: Element,
        aColumnMajor: Boolean,
        bColumnMajor: Boolean,
        wmma: Boolean
    ): Boolean = {
      val integer = Set(Element.S8, Element.U8)(a) && Set(Element.S8, Element.U8)(b)
      val types   =
        if (integer) (!wmma || a == b) && c == Element.S32 && d == Element.S32
        else if (a == Element.F16) b == Element.F16 && Set(Element.F16, Element.F32)(c) &&
        Set(Element.F16, Element.F32)(d)
        else if (a == Element.BF16 || a == Element.TF32) b == a && c == Element.F32 && d == Element.F32
        else a == Element.F64 && b == Element.F64 && c == Element.F64 && d == Element.F64
      val m16n8  = shape.m.raw == 16 && shape.n.raw == 8
      val shapes =
        if (wmma) {
          if (a == Element.F64) shape == Shape(8, 8, 4)
          else if (a == Element.TF32) shape == Shape(16, 16, 8)
          else Set(Shape(16, 16, 16), Shape(8, 32, 16), Shape(32, 8, 16))(shape)
        } else if (shape == Shape(8, 8, 4) && a == Element.F16) true
        else !aColumnMajor && bColumnMajor && (
          if (a == Element.F64) shape == Shape(8, 8, 4) || (m16n8 && Set(4, 8, 16)(shape.k.raw))
          else if (a == Element.TF32) m16n8 && Set(4, 8)(shape.k.raw)
          else if (integer) m16n8 && Set(16, 32)(shape.k.raw)
          else m16n8 && Set(8, 16)(shape.k.raw)
        )
      types && (wmma || c == d) && shapes
    }
    case class Fragment(
        shape: Shape,
        role: Role,
        element: Element,
        wmma: Boolean,
        columnMajor: Boolean = false
    ) {
      def rows: Int > 0                       = if (role == Role.B) shape.k else shape.m
      def columns: Int > 0                    = if (role == Role.A) shape.k else shape.n
      private def mma884F16: Boolean          = !wmma && shape == Shape(8, 8, 4) && element != Element.F64
      def partitions: Int in (1, 4)           = if (mma884F16) 4 else 1
      def partition(lane: WarpLane): Int >= 0 = if (mma884F16) (lane.widen[Int >= 0] % 16) / 4 else 0
      def registers: Int >= 0                 = if (mma884F16) {
        role match {
          case Role.A | Role.B                  => 2
          case Role.C if element == Element.F32 => 8
          case Role.C                           => 4
        }
      } else if (wmma && element == Element.F16 && role != Role.C) 8
      else (rows * columns).widen[Int >= 0] / (element.packed * 32)

      // Return the logical matrix element held by a lane's unpacked fragment element.
      def coordinate(lane: WarpLane, index: Int >= 0): (Int >= 0, Int >= 0) = {
        val laneIndex: Int >= 0 = lane
        if (mma884F16) {
          val thread         = laneIndex % 4
          val high: Int >= 0 = if (lane.raw < 16) 0 else 4
          role match {
            case Role.A if columnMajor            => ((index % 4) + high, thread)
            case Role.A                           => (thread + high, index)
            case Role.B if columnMajor            => (index, thread + high)
            case Role.B                           => (thread, index + high)
            case Role.C if element == Element.F32 =>
              (
                (laneIndex   % 2) + ((index / 2)         % 2) * 2 + high,
                ((index / 4) % 2) * 4 + ((laneIndex / 2) % 2) * 2 + (index % 2)
              )
            case Role.C => (thread + high, index)
          }
        } else if (wmma) {
          val linear = (index * 32 + laneIndex) % (rows * columns)
          (linear / columns, linear % columns)
        } else {
          val group  = laneIndex / 4
          val thread = laneIndex % 4
          role match {
            case Role.C => (group + (index / 2) * 8, thread * 2 + index % 2)
            case Role.A =>
              val chunk: Int > 0 = if (element.bits.raw < 32) element.packed else 1
              (
                group + ((index / chunk) % 2) * 8,
                thread * chunk + index   % chunk + (index / (chunk * 2)) * (chunk * 4)
              )
            case Role.B =>
              val chunk: Int > 0 = if (element.bits.raw < 32) element.packed else 1
              (thread * chunk + index % chunk + (index / chunk) * (chunk * 4), group)
          }
        }
      }
    }
    enum Operation {
      case Load(
          fragment: Fragment,
          destinations: Vec[Register],
          address: Value,
          stride: Value,
          columnMajor: Boolean,
          space: MemorySpace
      )
      case Store(
          fragment: Fragment,
          sources: Vec[Value],
          address: Value,
          stride: Value,
          columnMajor: Boolean,
          space: MemorySpace
      )
      case Multiply(
          a: Fragment,
          b: Fragment,
          c: Fragment,
          d: Fragment,
          destinations: Vec[Register],
          left: Vec[Value],
          right: Vec[Value],
          addend: Vec[Value],
          saturate: Boolean
      )
      case LoadMatrix(destinations: Vec[Register], address: Value, transpose: Boolean, space: MemorySpace)
      case StoreMatrix(sources: Vec[Value], address: Value, transpose: Boolean, space: MemorySpace)
    }
  }

  enum Instruction {
    case Tensor(operation: Matrix.Operation)
    case ConvertTF32(destination: Register, source: Value)

    /** Per-lane `.param` storage; kernel parameters are loaded from the parameter block as memory. */
    case LoadParameter(
        destinations: Vec[Register],
        parameter: Parameter,
        offset: Int >= 0,
        elementWidth: ScalarBitWidth,
        signed: Boolean
    )
    case StoreParameter(parameter: Parameter, offset: Int >= 0, source: Value, elementWidth: ScalarBitWidth)
    case Move(destination: Register, source: Value, width: ScalarBitWidth)
    case MoveVector(destinations: Vec[Register], sources: Vec[Value], width: ScalarBitWidth)
    case UniformMove(destination: Register, source: Value)
    case MovePredicate(destination: Predicate, source: Value)
    case MoveSplit(destinations: Vec[Option[Register]], source: Value, partWidth: Int in (16, 32))
    case MoveCombine(destination: Register, sources: Vec[Value], partWidth: Int in (16, 32))
    case IntegerUnary(operation: IntegerUnaryOperation, destination: Register, source: Value, width: ScalarBitWidth)
    case BitFind(destination: Register, source: Value, width: ScalarBitWidth, signed: Boolean, shiftAmount: Boolean)
    case DotProduct4(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        leftSigned: Boolean,
        rightSigned: Boolean
    )
    case IntegerBinary(
        operation: IntegerOperation,
        destination: Register,
        left: Value,
        right: Value,
        width: ScalarBitWidth
    )
    case FunnelShift(destination: Register, low: Value, high: Value, amount: Value, left: Boolean, clamp: Boolean)
    case IntegerSubtractAdd(destination: Register, left: Value, right: Value, addend: Value, width: ScalarBitWidth)
    case IntegerAddWithCarry(
        destination: Register,
        left: Value,
        right: Value,
        width: ScalarBitWidth,
        carryIn: Boolean,
        carryOut: Boolean
    )
    case IntegerSubtractWithBorrow(
        destination: Register,
        left: Value,
        right: Value,
        width: ScalarBitWidth,
        borrowIn: Boolean,
        borrowOut: Boolean
    )
    case IntegerAddThree(
        destination: Register,
        first: Value,
        second: Value,
        third: Value,
        width: ScalarBitWidth,
        carryMode: AddThreeCarryMode,
        negateFirst: Boolean,
        negateSecond: Boolean,
        negateThird: Boolean
    )
    case WriteConditionFlag(flag: VMConditionCodes.Flag, value: Value)
    case ReadConditionFlag(destination: Register, flag: VMConditionCodes.Flag)
    case ReadConditionPredicate(destination: Predicate, flag: VMConditionCodes.Flag)
    case InvalidateConditionFlag(flag: VMConditionCodes.Flag)
    case InvalidateAddThreeCarry
    case MultiplyAdd(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        operation: IntegerMultiplyAddOperation
    )
    case FloatBinary(
        operation: FloatOperation,
        destination: Register,
        left: Value,
        right: Value,
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl()
    )
    case FloatBinaryPacked(
        operation: FloatOperation,
        destination: Register,
        left: Value,
        right: Value,
        format: PackedFloatFormat,
        control: FloatControl = FloatControl()
    )
    case FloatUnary(
        operation: FloatUnaryOperation,
        destination: Register,
        source: Value,
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl()
    )
    case FusedMultiplyAdd(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        width: Int in (16, 32, 64),
        control: FloatControl = FloatControl(),
        rounding: ConversionRounding = ConversionRounding.RN
    )
    case FusedMultiplyAddBf16(destination: Register, left: Value, right: Value, addend: Value)
    case FusedMultiplyAddPacked(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        bf16: Boolean,
        saturate: Boolean
    )
    case Shuffle(
        mode: ShuffleMode,
        destination: Register,
        validity: Option[Predicate],
        source: Value,
        lane: Value,
        clamp: Value,
        memberMask: Value,
        width: ScalarBitWidth
    )
    case VoteBallot(destination: Register, predicate: Predicate, memberMask: Value, negated: Boolean = false)
    case Elect(destination: Option[Register], predicate: Predicate, memberMask: Value)
    case VotePredicate(
        destination: Predicate,
        predicate: Predicate,
        memberMask: Value,
        operation: VoteOperation,
        negated: Boolean = false
    )
    case BitFieldExtract(
        destination: Register,
        source: Value,
        start: Value,
        widthValue: Value,
        width: ScalarBitWidth,
        signed: Boolean
    )
    case BitFieldInsert(
        destination: Register,
        insertValue: Value,
        baseValue: Value,
        bitPosition: Value,
        bitLength: Value,
        width: ScalarBitWidth
    )
    case BitMask(destination: Register, start: Value, widthValue: Value, mode: BitMaskMode)
    case BytePermute(destination: Register, left: Value, right: Value, control: Value, mode: BytePermuteMode)
    case Select(destination: Register, whenTrue: Value, whenFalse: Value, predicate: Predicate, width: ScalarBitWidth)
    case Convert(
        destination: Register,
        source: Value,
        sourceFormat: NumericFormat,
        destinationFormat: NumericFormat,
        rounding: Option[ConversionRounding],
        clamp: Option[ConversionClamp] = None
    )
    case Load(destination: Register, address: Value, width: ScalarBitWidth, signed: Boolean, access: MemoryAccess)
    case LoadVector(
        destinations: Vec[Register],
        address: Value,
        width: ScalarBitWidth,
        signed: Boolean,
        access: MemoryAccess
    )
    case Store(address: Value, source: Value, width: ScalarBitWidth, access: MemoryAccess)
    case StoreVector(address: Value, sources: Vec[Value], width: ScalarBitWidth, access: MemoryAccess)

    /** Synchronous model of an asynchronous shared-memory copy: there is no separate copy engine to commit or wait on.
      */
    case AsyncCopy(destination: Value, source: Value, size: Int > 0, sourceSize: Value)
    case AsyncBulkStore(destination: Value, source: Value, size: Value, byteMask: Value)
    case AsyncBulkCopy(destination: Value, source: Value, size: Value, barrier: Value)
    case ClusterBarrier(arrive: Boolean)
    case ClusterTryCancel(response: Value, barrier: Value)
    case ClusterCancelStatus(destination: Predicate, response: Value)
    case ClusterCancelCta(destination: Register, responseLow: Value, responseHigh: Value, axis: Axis)
    case MBarrierLoad(address: Value)
    case MBarrierInit(address: Value, count: Value)
    case MBarrierArrive(destination: Option[Register], address: Value, count: Value, expectedBytes: Value)
    case MBarrierTest(destination: Predicate, address: Value, phase: Value, parity: Boolean)
    case MBarrierInvalidate(address: Value)

    /** Hints and ordering-only operations without architecturally visible effect in the emulator. */
    case NoOp
    case SetPredicate(
        destination: Predicate,
        comparison: Comparison,
        format: NumericFormat,
        left: Value,
        right: Value,
        flushSubnormal: Boolean = false
    )
    case PredicateCompare(destination: Predicate, comparison: Comparison, left: Predicate, right: Predicate)
    case PredicateUnary(destination: Predicate, source: Predicate)
    case PredicateBinary(operation: PredicateOperation, destination: Predicate, left: Predicate, right: Predicate)
    case Branch(target: Pc)
    case BarrierSync(id: Value, count: Option[Value] = None)
    case BarrierArrive(id: Value, count: Value)
    case BarrierReduce(
        destination: Predicate,
        id: Value,
        predicate: Predicate,
        operation: PredicateOperation,
        count: Option[Value] = None,
        negated: Boolean = false
    )
    case Atomic(
        destination: Option[Register],
        address: Value,
        source: Value,
        width: ScalarBitWidth,
        space: MemorySpace,
        operation: AtomicOperation
    )
    case AtomicCompareExchange(
        destination: Register,
        address: Value,
        comparator: Value,
        source: Value,
        width: ScalarBitWidth,
        space: MemorySpace
    )
    case WarpReduce(
        destination: Register,
        source: Value,
        memberMask: Value,
        operation: IntegerOperation,
        width: ScalarBitWidth
    )
    case Fence
    case Convergence(token: Register, action: ConvergenceAction)
    case ReturnStackPush(address: Long)
    case ReturnStackPop(destination: Register)
    case WarpBarrier(memberMask: Value)
    case Call(function: Value, returnParameters: Vec[Parameter], parameters: Vec[Parameter])
    case CallBuiltin(builtin: Builtin, returnParameters: Vec[Parameter], parameters: Vec[Parameter])
    case Trap
    case Return
    case Exit
  }

  case class ExecutionPredicate(predicate: Predicate, negated: Boolean)

  case class PredicatedInstruction(instruction: Instruction, predication: Option[ExecutionPredicate])

  type Pc = Pc.Type
  object Pc extends Newtype[Int >= 0] {
    val Entry: Pc = Pc(0)
    extension (pc: Pc) {
      def toInt: Int       = pc.raw
      def next: Option[Pc] = Option.when(pc.toInt != Int.MaxValue)(Pc(pc.unwrap + 1))
    }
  }

  case class ExecutableProgram(instructions: Vec[PredicatedInstruction])

  /** Executable code, the register and predicate slots its lanes are sized for up front, its frame-local storage and,
    * when known, the source names of its predicate slots.
    */
  case class Routine(
      name: NonEmpty[String],
      program: ExecutableProgram,
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      localBytes: Int >= 0 = 0,
      predicateNames: Vec[NonEmpty[String]] = Vec.empty
  ) {
    def describe(predicate: Predicate): String =
      predicateNames.lift(predicate.raw).fold(s"predicate ${predicate.raw}")(name => s"register $name")
  }

  case class DeviceFunction(routine: Routine, parameters: Vec[Parameter], returnParameters: Vec[Parameter])

  case class KernelParameter(name: NonEmpty[String], offset: Int >= 0, widthBytes: Int > 0, pointer: Boolean = false)

  /** A launchable routine whose parameters occupy one byte block, initially `parameterBlock`. */
  case class Entry(routine: Routine, parameters: Vec[KernelParameter], parameterBlock: Vec[Byte] = Vec.empty) {
    def name: NonEmpty[String] = routine.name

    def blockSize: Int >= 0 =
      parameters.foldLeft(parameterBlock.nonNegativeSize)((end, parameter) =>
        end.max(parameter.offset + parameter.widthBytes.widen[Int >= 0])
      )
  }

  object Argument {
    def fromLong(value: Long): Argument = Argument(LittleEndian.toNonEmptyVec(value, 8))
  }

  case class Argument(bytes: NonEmpty[Vec[Byte]])

  // Code addresses occupy their own address space; zero remains the null function pointer.
  class FunctionTable(names: Set[NonEmpty[String]]) {
    private val addresses = names.toVector.sortBy(_.raw).zipWithIndex.map { case (name, index) =>
      name -> (index.toLong + 1L)
    }.toMap
    private val symbols = addresses.map { case (name, address) => address -> name }

    def address(name: NonEmpty[String]): Option[Long] = addresses.get(name)
    def name(address: Long): Option[NonEmpty[String]] = symbols.get(address)
  }

  case class Kernel(
      entry: Entry,
      isaName: NonEmpty[String],
      functions: Map[NonEmpty[String], DeviceFunction] = Map.empty,
      entries: Map[NonEmpty[String], Entry] = Map.empty,
      functionTable: FunctionTable = FunctionTable(Set.empty)
  ) {
    def name: NonEmpty[String] = entry.name
  }

}
