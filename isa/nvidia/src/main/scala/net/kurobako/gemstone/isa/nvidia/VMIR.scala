package net.kurobako.gemstone.isa.nvidia

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

private[isa] object VMIR {

  /** A dense general-register slot within one routine. */
  type Register = Register.Type
  object Register extends Newtype[Int >= 0]

  /** A dense predicate slot within one routine. */
  type Predicate = Predicate.Type
  object Predicate extends Newtype[Int >= 0]

  /** A dense slot for per-lane `.param` storage within one routine. */
  type Parameter = Parameter.Type
  object Parameter extends Newtype[Int >= 0]

  enum ConvergenceAction derives CanEqual { case Setup, Synchronise, Break }

  enum Value derives CanEqual {
    case Register(slot: VMIR.Register)
    case Predicate(slot: VMIR.Predicate)
    case Immediate(value: Long)
    case Address(base: Value, offset: Long)

    /** The generic address of the executing frame's local window. */
    case LocalBase

    /** Where the CTA's shared::cta window starts: a cluster CTA's addresses carry its rank from bit 24, as on the
      * hardware, so clearing a rank bit names another CTA's shared memory.
      */
    case SharedWindow
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

    /** The workgroup's instruction issue count, standing in for its multiprocessor's cycle counter. */
    case Clock(part: CounterPart)

    /** Nanoseconds since the Unix epoch on a clock every workgroup shares and that never runs backwards. */
    case GlobalTimer(part: CounterPart)

    /** The multiprocessor the workgroup runs on: workgroups are dealt to multiprocessors in launch order. */
    case MultiprocessorId
    case WarpId

    /** The workgroup's place in its cluster and the cluster's in the grid. */
    case ClusterCtaRank
    case ClusterCtaCount
    case ClusterCtaId(axis: Axis)
    case ClusterShape(axis: Axis)
    case ClusterId(axis: Axis)
    case ClusterCount(axis: Axis)

    /** Whether the launch named a cluster shape, as `%is_explicit_cluster` reports it. */
    case ExplicitCluster
  }

  /** The part of a 64-bit counter a read returns. */
  enum CounterPart derives CanEqual {
    case Whole
    case Low
    case High

    def of(value: Long): Long = this match {
      case Whole => value
      case Low   => value & 0xffffffffL
      case High  => value >>> 32
    }
  }

  enum AddThreeCarryMode derives CanEqual {
    case None
    case Produce
    case Consume
  }

  enum IntegerOperation derives CanEqual {
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

    /** The low or high 64 bits of the 128-bit carry-less product. */
    case CarrylessMultiply(high: Boolean)
  }

  enum IntegerUnaryOperation derives CanEqual {
    case Negate
    case Absolute
    case BitwiseNot
    case LogicalNot
    case PopulationCount
    case LeadingZeroCount
    case BitReverse
  }

  enum PredicateOperation derives CanEqual {
    case And
    case Or
    case Xor
  }

  /** Where a barrier's reduction of its participants' predicates lands: one predicate combined by `operation`, or the
    * number of true predicates.
    */
  // the destinations are distinct opaque types, a predicate and a register
  @SuppressWarnings(Array("baumkuchen:common-factor"))
  enum BarrierResult {
    case Combined(destination: Predicate, operation: PredicateOperation)
    case Count(destination: Register)

    def combination: Option[PredicateOperation] = this match {
      case Combined(operation = operation) => Some(operation)
      case Count(_)                        => None
    }
  }

  /** What a video instruction does to each lane or scalar: arithmetic, a shift of the first operand by the second, or a
    * comparison that yields 1 or 0.
    */
  enum VideoOperation derives CanEqual {
    case Add, Subtract, Average, AbsoluteDifference, Minimum, Maximum
    case ShiftLeft, ShiftRight
    case Equal, NotEqual, Less, LessEqual, Greater, GreaterEqual
  }

  /** The part of a word a scalar video instruction reads or writes: all of it, a byte, or a half-word. */
  enum VideoSelect(val offset: Int in (0, 8, 16, 24), val bits: Int in (8, 16, 32)) derives CanEqual {
    case Word extends VideoSelect(offset = 0, bits = 32)
    case B0   extends VideoSelect(offset = 0, bits = 8)
    case B1   extends VideoSelect(offset = 8, bits = 8)
    case B2   extends VideoSelect(offset = 16, bits = 8)
    case B3   extends VideoSelect(offset = 24, bits = 8)
    case H0   extends VideoSelect(offset = 0, bits = 16)
    case H1   extends VideoSelect(offset = 16, bits = 16)
  }

  /** The secondary operation of a scalar video instruction between its result and the third operand. */
  enum VideoSecondary derives CanEqual {
    case Plain, Add, Minimum, Maximum
  }

  /** The 8-bit floating-point formats: four exponent and three mantissa bits without infinities, or five and two. */
  enum Float8Format derives CanEqual {
    case E4M3, E5M2
  }

  /** The encodings of the paired narrow conversions: OCP floating-point formats, the unsigned scale exponent and the
    * signed fixed-point S2F6, two integer and six fraction bits.
    */
  enum NarrowFormat derives CanEqual {
    case E4M3, E5M2, E2M3, E3M2, E2M1, UE8M0, S2F6
  }

  /** The narrow formats `cvt.rs` rounds binary32 values to with random bits. */
  enum StochasticFormat derives CanEqual {
    case Half, BFloat16, E4M3, E5M2, E2M3, E3M2, E2M1
  }

  /** What `testp` asks of a floating-point value. */
  enum FloatProperty derives CanEqual {
    case Finite, Infinite, Number, NotANumber, Normal, Subnormal
  }

  /** The state space whose window a generic address may fall in. */
  enum AddressKind derives CanEqual {
    case Global, Shared, Local
  }

  enum VoteOperation derives CanEqual {
    case All
    case Any
    case Uniform
  }

  enum ShuffleMode derives CanEqual {
    case Up, Down, Index, Butterfly
  }

  enum BitMaskMode derives CanEqual {
    case Clamp, Wrap
  }

  enum BytePermuteMode derives CanEqual {
    case Default, F4E, B4E, RC8, ECL, ECR, RC16
  }

  enum NumericFormat(val width: ScalarBitWidth) derives CanEqual {
    case Bits(bits: Int in (8, 16, 32, 64))     extends NumericFormat(bits.widen)
    case Signed(bits: Int in (8, 16, 32, 64))   extends NumericFormat(bits.widen)
    case Unsigned(bits: Int in (8, 16, 32, 64)) extends NumericFormat(bits.widen)
    case IEEE(bits: Int in (16, 32, 64))        extends NumericFormat(bits.widen)
    case BF16                                   extends NumericFormat(16)
  }

  enum ConversionRounding derives CanEqual {
    case RN, RNI, RZ, RZI, RM, RMI, RP, RPI
  }

  /** Range limit applied to a conversion result: `Saturate` bounds floats to [0, 1] and integers to the destination
    * range, `Relu` clamps negative floats to zero.
    */
  enum ConversionClamp derives CanEqual {
    case Saturate, Relu
  }

  enum Comparison derives CanEqual {
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

  enum PackedFloatFormat derives CanEqual {
    case F16x2, BF16x2, F32x2
  }

  enum FloatOperation derives CanEqual {
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

  enum FloatUnaryOperation derives CanEqual {
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

    /** The high product half plus the addend and the carry flag, as `madc.hi` computes. */
    case High(operandWidth: ScalarBitWidth, resultWidth: ScalarBitWidth, carryOut: Boolean)

    /** `mad.hi`: the high half of the signed or unsigned product plus the addend, clamped to the signed range where
      * `saturate`.
      */
    case HighSum(width: ScalarBitWidth, signed: Boolean, saturate: Boolean)

    /** `mad.wide`: the full product of the extended operands plus the addend of twice their width. */
    case Wide(operandWidth: Int in (16, 32), signed: Boolean)

    /** `mul24` and `mad24`: the product of the low 24 bits of the operands, its low 32 bits or bits 16 to 47, plus the
      * addend, clamped to the signed range where `saturate`.
      */
    case Narrow(high: Boolean, signed: Boolean, saturate: Boolean)
  }

  /** Memory space of an atomic or matrix access: explicit global/shared, or generic resolved from the address. */
  enum MemorySpace derives CanEqual {
    case Generic, Global, Shared
  }

  /** Memory domain and visibility carried by ordinary load/store instructions. */
  enum MemoryAccess derives CanEqual {
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
  enum Builtin derives CanEqual {
    case GetDevice
    case DeviceAttribute
    case Occupancy
    case NoError
    case ParameterBuffer
    case Malloc
    case Free
    case AssertFail
    case LaunchDevice
  }

  /** Warp-level matrix operand types, their fragments across a warp's lanes, and the operations over them. */
  object Matrix {
    import net.kurobako.gemstone.constraint.auto.*

    /** A matrix element as registers hold it: `bits` wide and `packed` to a 32-bit register, or one to a 64-bit
      * register for F64. The narrow floats of `kind::f8f6f4` each take a byte: E3M2 and E2M3 its low six bits,
      * [[E2M1Byte]] its middle four.
      */
    enum Element(val bits: Int in (1, 4, 8, 16, 32, 64), val packed: Int > 0) derives CanEqual {
      case F16      extends Element(16, 2)
      case BF16     extends Element(16, 2)
      case TF32     extends Element(32, 1)
      case F32      extends Element(32, 1)
      case F64      extends Element(64, 1)
      case S8       extends Element(8, 4)
      case U8       extends Element(8, 4)
      case S4       extends Element(4, 8)
      case U4       extends Element(4, 8)
      case B1       extends Element(1, 32)
      case S32      extends Element(32, 1)
      case E4M3     extends Element(8, 4)
      case E5M2     extends Element(8, 4)
      case E3M2     extends Element(8, 4)
      case E2M3     extends Element(8, 4)
      case E2M1Byte extends Element(8, 4)
      case E2M1     extends Element(4, 8)

      /** The encoding a container of `bits` bits holds: TF32 is the high 19 bits of a binary32 register. */
      def code(container: Long): Long = this match {
        case TF32        => (container >>> 13) & 0x7ffffL
        case E3M2 | E2M3 => container & 0x3fL
        case E2M1Byte    => (container >>> 2) & 0xfL
        case _           => Bits.truncate(container, bits.widen)
      }

      def integer: Boolean = this match {
        case S8 | U8 | S4 | U4 | B1 | S32 => true
        case _                            => false
      }

      /** The narrow floats `kind::f8f6f4` and the block-scaled kinds take. */
      def narrow: Boolean = this match {
        case E4M3 | E5M2 | E3M2 | E2M3 | E2M1Byte | E2M1 => true
        case _                                           => false
      }
    }

    /** The element format of the scale factors of block-scaled products: a power of two, or an unsigned E4M3. */
    enum ScaleFormat derives CanEqual {
      case UE8M0, UE4M3
    }

    enum BitOperation derives CanEqual {
      case And, Xor
    }

    case class Shape(m: Int > 0, n: Int > 0, k: Int > 0) derives CanEqual
    enum Role derives CanEqual { case A, B, C }

    /** How the products of a row of A and a column of B add to an element of C. */
    enum Accumulation derives CanEqual {

      /** Exactly, as integers, then wrapped or saturated to the 32-bit result; saturation also clamps the running sum
        * after each `chunk` products.
        */
      case Exact(chunk: Int > 0)

      /** By a chain of fused multiply-adds from C on, each rounded as `rounding` says. */
      case Fused(rounding: ConversionRounding)

      /** In consecutive blocks of `size` products, each fused with the running sum, which starts at C or, when `late`,
        * at zero with C added once at the end, rounding to nearest. The terms of a block align to the largest exponent
        * among them, no lower than `floor`, keeping `extra` bits below a 24-bit significand (fewer when negative) and
        * dropping the rest of each magnitude; the exact sum of the aligned terms then rounds to the accumulator format
        * with `fraction` fraction bits, toward zero or to nearest even, and an infinite sum stays infinite. A product's
        * exponent is the sum of its factors' exponents, each no lower than its format's least normal exponent.
        */
      case Blocked(size: Int > 0, extra: Int, fraction: Int > 0, nearest: Boolean, floor: Int, late: Boolean)

      /** `mma.m8n8k4` with half-precision inputs where no tensor core runs it: ptxas emits binary32 fused multiply-adds
        * rounding to nearest, summing the products in order from zero and then adding C or, for a half-precision
        * result, the first two and last two products in pairs added to C in turn.
        */
      case Emulated
    }

    private val GuardBits        = Vec(0, 1, 2)
    private val Tf32Sizes        = Vec(4, 4, 8)
    private val DefaultSizes     = Vec(4, 8, 16)
    private val ByteElements     = Set(Element.S8, Element.U8)
    private val NibbleElements   = Set(Element.S4, Element.U4)
    private val Fp8Elements      = Set(Element.E4M3, Element.E5M2)
    private val F8F6F4Elements   = Set(Element.E4M3, Element.E5M2, Element.E3M2, Element.E2M3, Element.E2M1Byte)
    private val FloatingElements = Set(Element.F16, Element.F32)
    private val WmmaShapes       = Set(Shape(16, 16, 16), Shape(8, 32, 16), Shape(32, 8, 16))

    /** The format a floating-point product accumulates in: half precision only when both C and D are. */
    def accumulator(c: Element, d: Element): Element =
      if (c == Element.F16 && d == Element.F16) Element.F16 else Element.F32

    /** The accumulation `mma` performs on an architecture, from measurements of each generation's tensor cores
      * (arXiv:2512.07004, and an RTX 4060 Ti for Ada) and the code ptxas emits: Volta and Turing fuse 4 half-precision
      * products, Ampere and Ada 8 with one extra alignment bit (4 for TF32), Hopper and Blackwell 16 with two (8 for
      * TF32). Ada fuses 16 FP8 products with 13 fraction bits; Hopper and the Blackwell data-centre parts convert FP8
      * to half precision for two half-precision blocks from zero and add C after; consumer Blackwell fuses 32.
      */
    def accumulation(
        architecture: Int,
        shape: Shape,
        input: Element,
        result: Element,
        rounding: ConversionRounding
    ): Accumulation =
      if (input.integer) Accumulation.Exact(if (input.bits == 4) 32 else 16)
      else if (input == Element.F64) Accumulation.Fused(rounding)
      else if (shape == Shape(8, 8, 4) && architecture >= 80) Accumulation.Emulated
      else {
        val generation    = if (architecture < 80) 0 else if (architecture < 90) 1 else 2
        val guard         = GuardBits(generation)
        val (size, extra) =
          if (input == Element.TF32) (Tf32Sizes(generation), guard)
          else if (input.narrow && generation == 1) (16, -10)
          else if (input.narrow && architecture >= 120) (32, guard)
          else (DefaultSizes(generation), guard)
        val half              = result == Element.F16
        val fraction: Int > 0 = if (half) 10 else (23 + math.min(extra, 0)).unsafe[Int > 0]
        Accumulation.Blocked(
          size.unsafe[Int > 0],
          extra,
          fraction,
          nearest = half,
          floor = (if (half) -14 else -126) - 5 - guard,
          late = input.narrow && generation == 2 && architecture < 120
        )
      }

    /** The metadata of a sparse A and the selector of the lanes that provide it. */
    case class Sparsity(metadata: Value, selector: Value)

    /** Block scale factors: `vector` per row of A and column of B, each a byte of a lane's `data`, as the byte and
      * thread selectors pick them.
      */
    case class Scaling(
        format: ScaleFormat,
        vector: Int in (1, 2, 4),
        aData: Value,
        aByte: Value,
        aThread: Value,
        bData: Value,
        bByte: Value,
        bThread: Value
    )

    /** The elements a sparse A packs from each of its rows' chunks: one of two TF32 elements, two of four 16- or 8-bit
      * ones, or two pairs of four pairs of 4-bit ones.
      */
    def packedPerChunk(element: Element): Int > 0 = element.bits.raw match {
      case 32 => 1
      case 4  => 4
      case _  => 2
    }

    /** The elements of a row of a sparse A that one metadata nibble describes. */
    def chunkWidth(element: Element): Int > 0 = (2 * packedPerChunk(element)).unsafe[Int > 0]

    /** The lane and bit offset of the nibble of metadata that describes `chunk` of `row` of a sparse A with `depth`
      * columns. 16- and 32-bit elements put a row's nibbles in the low and high halves of a quad's lanes, narrower ones
      * a whole register per row.
      */
    // call sites name these arguments, which the argument checks enforce
    @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
    def metadataNibble(
        element: Element,
        depth: Int,
        row: Int,
        chunk: Int,
        selector: Int
    ): (lane: Int, offset: Int) =
      if (element.bits >= 16) {
        val threads = depth / chunkWidth(element) / 4
        (4 * (row % 8) + threads * selector + chunk / 4, 16 * (row / 8) + 4 * (chunk % 4))
      } else (4 * (row % 8) + 2 * selector + row / 8 + 2 * (chunk / 8), 4 * (chunk % 8))

    /** The metadata index a sparse A's packed element `index` reads; the 4-bit elements of a pair share one. */
    def selectionField(element: Element, index: Int): Int =
      if (element.bits == 4) index / 4 * 2 + index % 4 / 2 else index

    /** Element types, shape and layouts `mma` (or `wmma` with `wmma` set) accepts, dense or with a `sparse` A: D = A *
      * B + C.
      */
    // call sites name these arguments, which the argument checks enforce
    @SuppressWarnings(Array("scalafix:PublicSignatures.primitive-run"))
    def supportsMultiply(
        shape: Shape,
        a: Element,
        b: Element,
        c: Element,
        d: Element,
        aColumnMajor: Boolean,
        bColumnMajor: Boolean,
        wmma: Boolean,
        sparse: Boolean = false
    ): Boolean = {
      import Element.*
      val k                                   = shape.k.raw
      val m16n8                               = shape.m == 16 && shape.n == 8
      val m8n8                                = shape.m == 8 && shape.n == 8
      def integer(set: Set[Element]): Boolean = set(a) && set(b) && c == S32 && d == S32 && (!wmma || a == b)
      val accepted                            =
        if (wmma)
          if (a == F64) shape == Shape(8, 8, 4) && b == F64 && c == F64 && d == F64
          else if (a == TF32) shape == Shape(16, 16, 8) && b == TF32 && c == F32 && d == F32
          else if (a == B1) shape == Shape(8, 8, 128) && b == B1 && c == S32 && d == S32
          else if (NibbleElements(a)) shape == Shape(8, 8, 32) && integer(NibbleElements)
          else
            WmmaShapes(shape) && (
              integer(ByteElements) ||
                (a == F16 && b == F16 && FloatingElements(c) && FloatingElements(d)) ||
                (a == BF16 && b == BF16 && c == F32 && d == F32)
            )
        else if (sparse)
          !aColumnMajor && bColumnMajor && m16n8 && c == d && (
            ((a == F16 && b == F16 && FloatingElements(c)) || (a == BF16 && b == BF16 && c == F32)) &&
              PartialFunction.cond(k) { case 16 | 32 => true } ||
              a == TF32 && b == TF32 && c == F32 && PartialFunction.cond(k) { case 8 | 16 => true } ||
              integer(ByteElements) && PartialFunction.cond(k) { case 32 | 64 => true } ||
              integer(NibbleElements) && Set(64, 128)(k) ||
              F8F6F4Elements(a) && F8F6F4Elements(b) && FloatingElements(c) && k == 64 ||
              a == E2M1 && b == E2M1 && c == F32 && k == 128
          )
        else if (shape == Shape(8, 8, 4) && a == F16)
          b == F16 && FloatingElements(c) && FloatingElements(d) && (c == F16 || d == F32)
        else
          !aColumnMajor && bColumnMajor && (
            if (a == F64) b == F64 && c == F64 && d == F64 && (shape == Shape(8, 8, 4) || m16n8 && Set(4, 8, 16)(k))
            else if (a == TF32) b == TF32 && c == F32 && d == F32 && m16n8 && Set(4, 8)(k)
            else if (a == BF16)
              b == BF16 && c == F32 && d == F32 && m16n8 && PartialFunction.cond(k) { case 8 | 16 => true }
            else if (a == F16)
              b == F16 && FloatingElements(c) && c == d && m16n8 && PartialFunction.cond(k) { case 8 | 16 => true }
            else if (ByteElements(a)) integer(ByteElements) &&
            (m8n8 && k == 16 || m16n8 && PartialFunction.cond(k) { case 16 | 32 => true })
            else if (NibbleElements(a)) integer(NibbleElements) &&
            (m8n8 && k == 32 || m16n8 && PartialFunction.cond(k) { case 32 | 64 => true })
            else if (a == B1) b == B1 && c == S32 && d == S32 && (m8n8 && k == 128 || m16n8 && Set(128, 256)(k))
            else if (a == E2M1) b == E2M1 && c == F32 && d == F32 && m16n8 && k == 64
            else F8F6F4Elements(a) && F8F6F4Elements(b) && FloatingElements(c) && c == d && m16n8 &&
            (k == 32 || Fp8Elements(a) && Fp8Elements(b) && k == 16)
          )
      accepted
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
      def coordinate(lane: WarpLane, index: Int >= 0): (row: Int >= 0, column: Int >= 0) = {
        val laneIndex: Int >= 0 = lane
        if (mma884F16) {
          val thread         = laneIndex % 4
          val high: Int >= 0 = if (lane < 16) 0 else 4
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
              val chunk: Int > 0 = if (element.bits < 32) element.packed else 1
              (
                group + ((index / chunk) % 2) * 8,
                thread * chunk + index   % chunk + (index / (chunk * 2)) * (chunk * 4)
              )
            case Role.B =>
              val chunk: Int > 0 = if (element.bits < 32) element.packed else 1
              (thread * chunk + index % chunk + (index / chunk) * (chunk * 4), group)
          }
        }
      }
    }

    /** The matrices `ldmatrix` and `stmatrix` move: 8x8 16-bit ones, 16x16 and 8x16 byte ones loaded from 6- or 4-bit
      * elements packed sixteen to a padded row, and 16x8 byte ones stored.
      */
    enum TileShape(val rows: Int > 0, val columns: Int > 0, val bytes: Int in (1, 2)) derives CanEqual {
      case M8N8   extends TileShape(8, 8, 2)
      case M16N16 extends TileShape(16, 16, 1)
      case M8N16  extends TileShape(8, 16, 1)
      case M16N8  extends TileShape(16, 8, 1)
    }

    /** How memory holds a byte tile's rows: a byte per element, or sixteen 6- or 4-bit elements packed and padded to 16
      * bytes, which unpack to the low six or middle four bits of a byte.
      */
    enum Packing derives CanEqual {
      case Bytes, Six, Four

      /** Sixteen elements packed this way from the low 16 bytes of `row`, each widened to its byte. */
      def unpack(row: Array[Byte]): Array[Byte] = this match {
        case Bytes => row
        case Six   =>
          Array.tabulate(16) { element =>
            val bit  = element * 6
            val word = (row(bit / 8) & 0xff) | ((row(math.min(bit / 8 + 1, 15)) & 0xff) << 8)
            ((word >>> (bit % 8)) & 0x3f).toByte
          }
        case Four => Array.tabulate(16)(element => (((row(element / 2) >>> (element % 2 * 4)) & 0xf) << 2).toByte)
      }
    }

    case class Tile(shape: TileShape, count: Int in (1, 2, 4), transpose: Boolean, packing: Packing) {

      /** Registers each lane holds per matrix. */
      def registers: Int > 0 = if (shape == TileShape.M16N16) 2 else 1

      /** The lanes that provide each matrix's row addresses: eight, or sixteen for a 16x16 matrix. */
      def addresses: Int > 0 = if (shape == TileShape.M16N16) 16 else 8

      /** The fragment element a lane's register holds at `part` (a 16-bit half or a byte): its matrix and its row and
        * column in the matrix as registers hold it, before any transpose.
        */
      def coordinate(at: WarpLane, register: Int, part: Int): (matrix: Int, row: Int, column: Int) = {
        val lane = at.raw
        shape match {
          case TileShape.M8N8   => (register, lane / 4, lane % 4 * 2 + part)
          case TileShape.M16N16 => (register / 2, lane / 4 + 8 * (register % 2), lane % 4 * 4 + part)
          case TileShape.M8N16  => (register, lane / 4, lane % 4 * 4 + part)
          case TileShape.M16N8  => (register, lane / 4 + 8 * (part / 2), lane % 4 * 2 + part % 2)
        }
      }

      /** The memory row and the element within it that hold a fragment element. */
      def stored(row: Int, column: Int): (row: Int, column: Int) = if (transpose) (column, row) else (row, column)
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

      /** D = A * B + C; a sparse A packs half its columns, which its metadata places, and scaling multiplies each block
        * of A's rows and B's columns by its factor.
        */
      case Multiply(
          a: Fragment,
          b: Fragment,
          c: Fragment,
          d: Fragment,
          destinations: Vec[Register],
          left: Vec[Value],
          right: Vec[Value],
          addend: Vec[Value],
          accumulation: Accumulation,
          saturate: Boolean = false,
          bits: Option[BitOperation] = None,
          sparsity: Option[Sparsity] = None,
          scaling: Option[Scaling] = None
      )
      case LoadMatrix(tile: Tile, destinations: Vec[Register], address: Value, space: MemorySpace)
      case StoreMatrix(tile: Tile, sources: Vec[Value], address: Value, space: MemorySpace)

      /** `movmatrix`: the 8x8 matrix of 16-bit elements whose rows lanes hold, transposed. */
      case Transpose(destination: Register, source: Value)

      /** `MOVM.U4TO8.M832`: an 8x32 matrix of 4-bit elements, lane `4 * row + quarter` holding columns `8 * quarter` to
        * `8 * quarter + 7` low first, regrouped as the byte fragment 8-bit products take: lane `4 * row + quarter` gets
        * columns `4 * quarter` to `4 * quarter + 3` in `low` and the same 16 columns on in `high`, each zero-extended.
        */
      case Widen(low: Register, high: Register, source: Value)

      /** `wgmma.mma_async`: `product` across each warpgroup, D in its `destinations`, A in its `left` registers or,
        * where they are empty, at the 64-bit descriptor `a` holds low word first, and B at the descriptor `b` holds. A
        * false `scale` drops D as the addend; A and B may each be negated and, from shared memory, MN-major.
        */
      case WarpgroupMultiply(
          product: Multiply,
          a: Vec[Value],
          b: Vec[Value],
          scale: Value,
          negateA: Boolean,
          negateB: Boolean,
          transposeA: Boolean,
          transposeB: Boolean
      )
    }
  }

  /** The operands of the fifth-generation tensor core instructions (tcgen05), which move values between registers,
    * shared memory and the tensor memory [[TensorMemory]] models, and multiply matrices there.
    */
  object Tcgen05 {

    /** The shapes tcgen05.ld and tcgen05.st move between a warp's registers and its 32 lanes of tensor memory, each
      * repeated across the columns: `registers` per thread and repeat.
      */
    enum DataShape(val registers: Int in (1, 2, 4)) derives CanEqual {
      case Lanes32Bits32   extends DataShape(1)
      case Lanes16Bits64   extends DataShape(1)
      case Lanes16Bits128  extends DataShape(2)
      case Lanes16Bits256  extends DataShape(4)
      case Lanes16Bits32x2 extends DataShape(1)

      /** The lane, among the warp's 32, and the column of register `register` of thread `thread`. `.16x32bx2` is two
        * accesses of 16 lanes, threads 16 to 31 at the address plus the split offset.
        */
      def lane(thread: Int, register: Int): Int = this match {
        case Lanes32Bits32   => thread
        case Lanes16Bits64   => thread / 4 + 8 * (thread   % 2)
        case Lanes16Bits128  => thread / 4 + 8 * (register % registers)
        case Lanes16Bits256  => thread / 4 + 8 * (register % registers / 2)
        case Lanes16Bits32x2 => thread % 16
      }

      def column(thread: Int, register: Int): Int = {
        val repeat = register / registers
        this match {
          case Lanes32Bits32   => repeat
          case Lanes16Bits64   => 2 * repeat + thread / 2                  % 2
          case Lanes16Bits128  => 4 * repeat + thread                      % 4
          case Lanes16Bits256  => 8 * repeat + 2 * (thread % 4) + register % registers % 2
          case Lanes16Bits32x2 => repeat
        }
      }
    }

    /** The threads of a warp a move involves: SASS splits `.16x32bx2` into one move per half of the warp, the second at
      * the address plus the split offset.
      */
    enum Threads derives CanEqual {
      case All, Low, High

      def includes(thread: Int): Boolean = this match {
        case All  => true
        case Low  => thread < 16
        case High => thread >= 16
      }
    }

    /** A tcgen05.ld or tcgen05.st: its shape, the registers each thread moves, the address offset of the second access
      * of `.16x32bx2`, whether two 16-bit columns pack into each register, and the threads it involves.
      */
    case class Movement(shape: DataShape, registers: Int, split: Int, packed: Boolean, threads: Threads = Threads.All)

    enum ReductionKind derives CanEqual {
      case Float(absolute: Boolean, propagate: Boolean)
      case Signed, Unsigned
    }

    /** `tcgen05.ld.red`: the minimum or maximum of the values each thread loads, to `destination`. */
    case class Reduction(destination: Register, maximum: Boolean, kind: ReductionKind)

    /** The shapes tcgen05.cp copies from shared memory: `rows` rows of `bytes` bytes. */
    enum CopyShape(val rows: Int in (4, 32, 64, 128), val bytes: Int in (16, 32)) derives CanEqual {
      case Rows128Bits256 extends CopyShape(rows = 128, bytes = 32)
      case Rows4Bits256   extends CopyShape(rows = 4, bytes = 32)
      case Rows128Bits128 extends CopyShape(rows = 128, bytes = 16)
      case Rows64Bits128  extends CopyShape(rows = 64, bytes = 16)
      case Rows32Bits128  extends CopyShape(rows = 32, bytes = 16)
    }

    /** How tcgen05.cp replicates its rows across the four 32-lane partitions. */
    enum Replication derives CanEqual {
      case Once, PairsOfAlternate, PairsOfAdjacent, Every

      /** The lanes, from the address's lane, that row `row` of a copy lands in. */
      def lanes(row: Int): Vec[Int] = this match {
        case Once             => Vec(row)
        case PairsOfAlternate => Vec(row, row + 64)
        case PairsOfAdjacent  => Vec(row % 32 + 64 * (row / 32), row % 32 + 64 * (row / 32) + 32)
        case Every            => Vec(row, row + 32, row + 64, row + 96)
      }
    }

    /** A tcgen05.cp: its shape and replication, whether it unpacks sixteen 6- or 4-bit elements to bytes per 16, and
      * whether the peer CTA copies too.
      */
    case class Copy(shape: CopyShape, replication: Replication, packing: Matrix.Packing, pair: Boolean)

    /** The element types a tcgen05.mma kind takes, which its instruction descriptor narrows. */
    enum Kind derives CanEqual {
      case F16, TF32, F8F6F4, I8, MXF8F6F4, MXF4, MXF4NVF4

      /** The dense K; sparse products take twice as many. */
      def depth: Int = this match {
        case F16                    => 16
        case TF32                   => 8
        case MXF4 | MXF4NVF4        => 64
        case F8F6F4 | I8 | MXF8F6F4 => 32
      }

      def scaled: Boolean = this match {
        case MXF8F6F4 | MXF4 | MXF4NVF4 => true
        case _                          => false
      }
    }

    /** A tcgen05.mma: D at the tensor memory address `d` from A at the tensor memory address or shared memory
      * descriptor `a`, B at the descriptor `b`, and the instruction descriptor `descriptor`, adding D unless `enable`
      * is false; lanes set in `disabled` keep their D. Block-scaled kinds take their scale factors and sparse ones
      * their metadata from tensor memory, and `.ws` ones may zero columns of B by `zeroColumns`.
      */
    case class Multiply(
        kind: Kind,
        architecture: Int,
        pair: Boolean,
        ws: Boolean,
        vector: Int,
        shift: Int,
        shiftA: Boolean,
        d: Value,
        a: Value,
        aInTensorMemory: Boolean,
        b: Value,
        descriptor: Value,
        enable: Value,
        disabled: Vec[Value],
        scales: Option[(a: Value, b: Value)],
        metadata: Option[Value],
        zeroColumns: Option[Value],
        sparseByDescriptor: Boolean = false
    ) {

      /** Whether A is sparse: as the instruction says, or, where SASS always names the metadata, as the descriptor
        * says.
        */
      def sparse(bits: Long): Boolean = if (sparseByDescriptor) Bits.isSet(bits, 2) else metadata.nonEmpty
    }
  }

  // predicate move, uniform move and transpose are distinct instructions with the same operands; saturating
  // conversions pass a rounding mode and a clamp together
  @SuppressWarnings(Array("baumkuchen:duplicate-shape", "baumkuchen:exclusive-options"))
  enum Instruction derives CanEqual {
    case Tensor(operation: Matrix.Operation)

    /** `cvt.tf32.f32`: to nearest with ties away where `away`, otherwise to nearest even or `towardZero`. */
    case ConvertTF32(
        destination: Register,
        source: Value,
        away: Boolean,
        towardZero: Boolean,
        relu: Boolean,
        saturate: Boolean
    )

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

    /** `addend` plus the products of the two 16-bit halves of `left` with the low or, where `high`, the high byte pair
      * of `right`.
      */
    case DotProduct2(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        high: Boolean,
        leftSigned: Boolean,
        rightSigned: Boolean
    )
    case SumAbsoluteDifference(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        width: ScalarBitWidth,
        signed: Boolean
    )
    case FindNthSet(destination: Register, mask: Value, base: Value, offset: Value)

    /** `high` and `low` clamped to `bits`-bit integers and packed, `low` lowest, over the low bits of `rest`. */
    case PackSaturated(
        destination: Register,
        high: Value,
        low: Value,
        rest: Value,
        bits: Int in 1 ~ 16,
        signed: Boolean
    )

    /** Two values converted to `format`, the first into the upper byte of the low half, over the upper half of `rest`;
      * from binary16 halves of `high` and `low` where `halves`, else from binary32 values; negative ones zero where
      * `relu`.
      */
    case ConvertToFloat8(
        destination: Register,
        high: Value,
        low: Value,
        rest: Value,
        format: Float8Format,
        halves: Boolean,
        relu: Boolean
    )

    /** Two values narrowed to `format` and packed high over low, in bytes or, for E2M1, nibbles: binary32 `high` and
      * `low`, or the halves of `high` as binary16 or, where `bf16`, bfloat16 when `halves`. S2F6 values are divided by
      * the scale exponent in the matching byte of `scale` first; negative results are zero where `relu`.
      */
    case ConvertNarrow(
        destination: Register,
        high: Value,
        low: Value,
        scale: Value,
        format: NarrowFormat,
        halves: Boolean,
        bf16: Boolean,
        rounding: ConversionRounding,
        relu: Boolean,
        saturate: Boolean
    )

    /** The two `format` values of `source` widened to the halves of a binary16 or, where `bf16`, bfloat16 pair, each
      * multiplied by the scale exponent in the matching byte of `scale`; negative results are zero where `relu` and
      * results beyond the largest finite magnitude that magnitude where `saturate`.
      */
    case ConvertWiden(
        destination: Register,
        source: Value,
        scale: Value,
        format: NarrowFormat,
        bf16: Boolean,
        relu: Boolean,
        saturate: Boolean
    )

    /** The two `format` values in the low half of `source` widened to the halves of a binary16 pair. */
    case ConvertFromFloat8(destination: Register, source: Value, format: Float8Format)

    /** Whether the generic address `address` falls in the window of `space`. */
    case SpaceTest(destination: Predicate, address: Value, space: AddressKind)

    /** `lanes` lanes of `left` and `right` combined by `operation`, packed or, where `accumulate`, summed onto
      * `addend`.
      */
    case VideoSimd(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        operation: VideoOperation,
        lanes: Int in (2, 4),
        leftSigned: Boolean,
        rightSigned: Boolean,
        resultSigned: Boolean,
        saturate: Boolean,
        accumulate: Boolean
    )

    /** A scalar video operation on the selected parts of `left` and `right`, extended as their signedness says, clamped
      * to the range of the result part where `saturate`, then combined with `addend` by `secondary` or merged into the
      * part of it that `merge` names. A shift amount wraps at 32 where `wrap` and is clamped to 32 otherwise.
      */
    case VideoScalar(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        operation: VideoOperation,
        leftSelect: VideoSelect,
        rightSelect: VideoSelect,
        leftSigned: Boolean,
        rightSigned: Boolean,
        resultSigned: Boolean,
        saturate: Boolean,
        secondary: VideoSecondary,
        merge: VideoSelect,
        wrap: Boolean
    )

    /** `vmad`: the product of the selected parts of `left` and `right`, negated where `negateProduct`, plus `addend`,
      * negated where `negateAddend`, plus one where `plusOne`, shifted right by `shift` and clamped to 32 bits where
      * `saturate`.
      */
    case VideoMultiplyAdd(
        destination: Register,
        left: Value,
        right: Value,
        addend: Value,
        leftSelect: VideoSelect,
        rightSelect: VideoSelect,
        leftSigned: Boolean,
        rightSigned: Boolean,
        negateProduct: Boolean,
        negateAddend: Boolean,
        plusOne: Boolean,
        shift: Int in (0, 7, 15),
        saturate: Boolean
    )

    /** `cvt.rs`: `high` and `low` rounded to `format` with random bits from `bits`, packed into the upper or, where
      * `!upper`, the lower half of the result over the other half of `rest`.
      */
    case ConvertStochastic(
        destination: Register,
        high: Value,
        low: Value,
        bits: Value,
        rest: Value,
        format: StochasticFormat,
        upper: Boolean,
        relu: Boolean,
        saturate: Boolean
    )

    /** `slct`: `whenNonNegative` where `condition`, a signed word or a binary32 value, is not negative. */
    case SelectSign(
        destination: Register,
        whenNonNegative: Value,
        whenNegative: Value,
        condition: Value,
        width: ScalarBitWidth,
        floating: Boolean,
        flushSubnormal: Boolean
    )

    /** `testp`: whether `source`, of `width` bits, has `property`. */
    case TestProperty(destination: Predicate, source: Value, property: FloatProperty, width: Int in (32, 64))

    /** `set` with a Boolean operator: the comparison combined by `operation` with `predicate`, negated where `negated`,
      * yielding `truth` or 0.
      */
    case CompareSetCombined(
        destination: Register,
        comparison: Comparison,
        format: NumericFormat,
        left: Value,
        right: Value,
        truth: Long,
        flushSubnormal: Boolean,
        operation: PredicateOperation,
        predicate: Predicate,
        negated: Boolean
    )

    /** `setp` with a Boolean operator: the comparison combined by `operation` with `predicate`, negated where
      * `negated`.
      */
    case SetPredicateCombined(
        destination: Predicate,
        comparison: Comparison,
        format: NumericFormat,
        left: Value,
        right: Value,
        flushSubnormal: Boolean,
        operation: PredicateOperation,
        predicate: Predicate,
        negated: Boolean
    )

    /** `alloca`: `size` bytes below the lane's stack pointer, aligned to `alignment`, which becomes the new stack
      * pointer and the frame-local address written to `destination`.
      */
    case StackAllocate(destination: Register, size: Value, alignment: Long > 0)

    /** `stacksave`: the lane's stack pointer as a frame-local address. */
    case StackSave(destination: Register)

    /** `stackrestore`: sets the lane's stack pointer to the frame-local address `source`. */
    case StackRestore(source: Value)
    case IntegerBinary(
        operation: IntegerOperation,
        destination: Register,
        left: Value,
        right: Value,
        width: ScalarBitWidth
    )
    case FunnelShift(destination: Register, low: Value, high: Value, amount: Value, left: Boolean, clamp: Boolean)
    case Logic3(
        destination: Register,
        first: Value,
        second: Value,
        third: Value,
        table: Int in 0 ~ 255,
        width: ScalarBitWidth
    )
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

    /** `redux.sync` of binary32 operands to their minimum or maximum, of their magnitudes where `absolute`, with a NaN
      * operand making the result NaN only where `propagateNaN`.
      */
    case WarpReduceFloat(
        destination: Register,
        source: Value,
        memberMask: Value,
        maximum: Boolean,
        absolute: Boolean,
        propagateNaN: Boolean
    )

    /** `match.sync`: the participating lanes whose `source` equals this lane's, or under `all` every participating lane
      * where all hold the same value and none otherwise, with `predicate` set where all match.
      */
    case WarpMatch(
        destination: Option[Register],
        predicate: Option[Predicate],
        source: Value,
        memberMask: Value,
        width: ScalarBitWidth,
        all: Boolean
    )
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

    /** `high` and `low` converted and packed into the upper and lower halves of the destination. */
    case ConvertPacked(
        destination: Register,
        high: Value,
        low: Value,
        sourceFormat: NumericFormat,
        destinationFormat: NumericFormat,
        rounding: ConversionRounding,
        clamp: Option[ConversionClamp]
    )

    /** `truth` where `left` and `right` compare as `comparison` says, otherwise 0; each 16-bit half on its own where
      * `packed`.
      */
    case CompareSet(
        destination: Register,
        comparison: Comparison,
        format: NumericFormat,
        left: Value,
        right: Value,
        truth: Long,
        packed: Boolean,
        flushSubnormal: Boolean
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

    /** A bulk copy into shared::cluster memory from global or, where `sharedSource`, shared::cta memory, completing its
      * bytes on `barrier`; with `multicast`, into the same offsets of every cluster CTA its 16-bit mask names.
      */
    case AsyncBulkCopy(
        destination: Value,
        source: Value,
        size: Value,
        barrier: Value,
        sharedSource: Boolean = false,
        multicast: Option[Value] = None,
        ignored: Option[(left: Value, right: Value)] = None
    )

    /** A bulk reduction of shared::cta elements into global memory or, with `barrier`, shared::cluster memory. */
    case AsyncBulkReduce(
        destination: Value,
        source: Value,
        size: Value,
        barrier: Option[Value],
        operator: TensorMap.Reduction,
        element: TensorMap.Element
    )

    /** `size` bytes of shared::cta memory set to zero. */
    case AsyncBulkFill(address: Value, size: Value)

    /** A tensor-map copy of the box at `coordinates`, its shared::cta side at `shared` and completing on `barrier`.
      * With `gather` the coordinates name a column and four rows of a 2D tensor, as `.tile::gather4` and
      * `.tile::scatter4` do; with `pair` each destination completes the mbarrier at `barrier`'s offset in the CTA of
      * its pair whose rank has the parity of `barrier`'s, as `.cta_group::2` does.
      */
    case AsyncTensor(
        operation: TensorMap.Operation,
        tensorMap: Value,
        coordinates: Vec[Value],
        shared: Value,
        barrier: Option[Value],
        multicast: Option[Value],
        offsets: Vec[Value],
        im2col: Boolean,
        gather: Boolean = false,
        pair: Boolean = false
    )

    /** `value` written into one field of the 128-byte tensor map at `address`. */
    case TensorMapReplace(address: Value, field: TensorMap.Field, index: Int in 0 ~ 4, value: Value)

    /** A 128-byte tensor map copied from shared::cta to global memory. */
    case TensorMapCopy(destination: Value, source: Value)

    /** An asynchronous store or reduction into shared::cluster memory completing its bytes on `barrier`. */
    case AsyncSharedStore(address: Value, sources: Vec[Value], width: ScalarBitWidth, barrier: Value)
    case AsyncSharedReduce(
        address: Value,
        source: Value,
        width: ScalarBitWidth,
        operation: AtomicOperation,
        barrier: Value
    )

    /** The shared::cluster address of `address` in the cluster CTA of rank `rank`, and the rank holding an address. */
    case ClusterMap(destination: Register, address: Value, rank: Value)
    case ClusterRankOf(destination: Register, address: Value)
    case ClusterBarrier(arrive: Boolean)

    /** `clusterlaunchcontrol.try_cancel`: its response at `response` and the transaction completing on the mbarrier at
      * `barrier`, in every CTA of the cluster at the same offsets where `multicast`.
      */
    case ClusterTryCancel(response: Value, barrier: Value, multicast: Boolean = false)
    case ClusterCancelStatus(destination: Predicate, response: Value)
    case ClusterCancelCta(destination: Register, responseLow: Value, responseHigh: Value, axis: Axis)

    /** A warp's allocation of `columns` tensor memory columns, its address stored at the shared `destination`. */
    case TensorMemoryAllocate(destination: Value, columns: Value)
    case TensorMemoryDeallocate(address: Value, columns: Value)
    case TensorMemoryRelinquish

    /** The first free aligned run of `granules` granules claimed, its first granule to `destination` and whether one
      * was free to `claimed`, or the allocation bits ANDed with `mask`, as SASS manages the allocation state.
      */
    case TensorMemoryClaim(destination: Register, claimed: Predicate, granules: Value)
    case TensorMemoryRelease(mask: Value)

    /** A warp's collective move between its registers and its lanes of tensor memory at `address`. */
    case TensorMemoryLoad(
        movement: Tcgen05.Movement,
        destinations: Vec[Register],
        address: Value,
        reduction: Option[Tcgen05.Reduction]
    )
    case TensorMemoryStore(movement: Tcgen05.Movement, sources: Vec[Value], address: Value)
    case TensorMemoryCopy(copy: Tcgen05.Copy, address: Value, descriptor: Value)
    case TensorMemoryShift(address: Value, pair: Boolean)
    case TensorMemoryMultiply(multiply: Tcgen05.Multiply)

    /** An arrival on the mbarrier at `barrier` in this CTA, or at the same offset in each CTA `mask` names. */
    case TensorMemoryCommit(barrier: Value, mask: Option[Value])
    case MBarrierLoad(address: Value)
    case MBarrierInit(address: Value, count: Value)
    case MBarrierArrive(
        destination: Option[Register],
        address: Value,
        count: Value,
        expectedBytes: Value,
        drop: Boolean = false,
        complete: Boolean = true
    )

    /** The arrivals an mbarrier token's phase still awaited. */
    case MBarrierPending(destination: Register, token: Value)

    /** Transaction bytes the current phase expects more of or, where `complete`, has received. */
    case MBarrierTransaction(address: Value, bytes: Value, complete: Boolean)
    case MBarrierTest(destination: Predicate, address: Value, phase: Value, parity: Boolean)
    case MBarrierInvalidate(address: Value)

    /** Locks a shared word for this thread and loads it into `result` when no other thread holds its lock, and says in
      * `destination` whether it did, as Kepler's `LDSLK` does for shared atomics.
      */
    case SharedLockLoad(destination: Predicate, result: Register, address: Value, width: ScalarBitWidth)

    /** Stores to a shared word whose lock this thread holds and releases the lock, and says in `destination` whether it
      * did, as Kepler's `STSCUL` does.
      */
    case SharedStoreUnlock(destination: Predicate, address: Value, source: Value, width: ScalarBitWidth)

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

    /** Each active lane branches to the target its index selects; one whose index is past the table falls through. */
    case BranchTable(index: Value, targets: Vec[Pc])
    case BarrierSync(id: Value, count: Option[Value] = None)
    case BarrierArrive(id: Value, count: Value)
    case BarrierReduce(
        result: BarrierResult,
        id: Value,
        predicate: Predicate,
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
    case ReturnStackSave(source: Register)
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
      def toInt: Int = pc.raw
    }
  }

  object ExecutableProgram {
    val NoSource: Long = -1L
  }

  /** Lowered instructions and, when the lowering recorded them, the source ISA address each one came from. */
  case class ExecutableProgram(instructions: Vec[PredicatedInstruction], sources: Vec[Long] = Vec.empty) {
    def source(pc: Pc): Option[Long >= 0] = sources.lift(pc.toInt).flatMap(_.refine[Long >= 0].toOption)
  }

  /** Executable code, the register and predicate slots its lanes are sized for up front, its frame-local storage and,
    * when known, the source names of its predicate and register slots.
    */
  case class Routine(
      name: NonEmpty[String],
      program: ExecutableProgram,
      registerCount: Int >= 0,
      predicateCount: Int >= 0,
      localBytes: Int >= 0 = 0,
      predicateNames: Vec[NonEmpty[String]] = Vec.empty,
      registerNames: Vec[NonEmpty[String]] = Vec.empty
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
      functionTable: FunctionTable = FunctionTable(Set.empty),
      cluster: Option[Dimensions] = None,
      pairAllocationBarrier: Option[Long] = None
  ) {
    def name: NonEmpty[String] = entry.name
  }

}
