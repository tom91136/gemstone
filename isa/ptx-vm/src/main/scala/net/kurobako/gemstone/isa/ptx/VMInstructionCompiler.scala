package net.kurobako.gemstone.isa.ptx

import Lowering.*
import cats.syntax.all.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Widths.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.nvidia.*
import net.kurobako.gemstone.isa.nvidia.VMIR.{Instruction as _, *}
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

/** Lowers one PTX instruction to the NVIDIA VM within a routine [[VMCompiler.Scope]]. */
// the operand lists mirror the PTX instruction forms they lower
@SuppressWarnings(Array("baumkuchen:data-clump"))
private object VMInstructionCompiler {
  private enum CarryForm(val subtract: Boolean, val carryIn: Boolean) {
    case AddCc extends CarryForm(subtract = false, carryIn = false)
    case Addc  extends CarryForm(subtract = false, carryIn = true)
    case SubCc extends CarryForm(subtract = true, carryIn = false)
    case Subc  extends CarryForm(subtract = true, carryIn = true)
  }

  private val PackedFloat32Modifiers                                    = Set(Modifier.F32x2, Modifier.Rn, Modifier.Ftz)
  private val VideoRelations: PartialFunction[Modifier, VideoOperation] = {
    case Modifier.Eq => VideoOperation.Equal
    case Modifier.Ne => VideoOperation.NotEqual
    case Modifier.Lt => VideoOperation.Less
    case Modifier.Le => VideoOperation.LessEqual
    case Modifier.Gt => VideoOperation.Greater
    case Modifier.Ge => VideoOperation.GreaterEqual
  }
  private val VideoModifiers = Set(
    Modifier.U32,
    Modifier.S32,
    Modifier.Sat,
    Modifier.Clamp,
    Modifier.Wrap,
    Modifier.Add,
    Modifier.Min,
    Modifier.Max,
    Modifier.Eq,
    Modifier.Ne,
    Modifier.Lt,
    Modifier.Le,
    Modifier.Gt,
    Modifier.Ge
  )
  private val ClusterBarrierModifiers = Set(
    Vec(Modifier.Arrive),
    Vec(Modifier.Arrive, Modifier.Release),
    Vec(Modifier.Arrive, Modifier.Relaxed),
    Vec(Modifier.Wait),
    Vec(Modifier.Wait, Modifier.Acquire)
  )
  private val ClusterCancelModifiers = Set(
    Vec(Modifier.Async, Modifier.SharedCta, Modifier.MbarrierCompleteTxBytes, Modifier.B128),
    Vec(Modifier.Async, Modifier.MbarrierCompleteTxBytes, Modifier.B128)
  )
  private val FenceScopeModifiers = Set(Modifier.Cta, Modifier.Gl, Modifier.Sys)
  private val AtomicWords         = Set(Modifier.U32, Modifier.U64, Modifier.S32, Modifier.S64)

  private type Lowered = Either[Error, VMIR.Instruction]

  private val SignedTypes: Set[Modifier]   = Set(Modifier.S8, Modifier.S16, Modifier.S32, Modifier.S64)
  private val UnsignedTypes: Set[Modifier] = Set(Modifier.U8, Modifier.U16, Modifier.U32, Modifier.U64)
  private val BitTypes: Set[Modifier]      = Set(Modifier.B8, Modifier.B16, Modifier.B32, Modifier.B64)
  private val FloatTypes: Set[Modifier]    = Set(Modifier.F16, Modifier.F32, Modifier.F64, Modifier.Bf16)
  private val PackedTypes: Set[Modifier]   = Set(Modifier.F16x2, Modifier.Bf16x2, Modifier.F32x2)
  private val IntegerTypes: Set[Modifier]  = SignedTypes ++ UnsignedTypes ++ BitTypes
  private val Word32Types: Set[Modifier]   = Set(Modifier.S32, Modifier.U32, Modifier.B32)
  private val Scopes: Set[Modifier]        = Set(Modifier.Cta, Modifier.Gpu, Modifier.Sys)
  private val Orders: Set[Modifier]        = Set(Modifier.Relaxed, Modifier.Acquire, Modifier.Release, Modifier.AcqRel)
  private val FullWarp: Value              = Value.Immediate(0xffffffffL)

  private val builtins: Map[String, Builtin] = Map(
    "cudaGetDevice"                                               -> Builtin.GetDevice,
    "cudaDeviceGetAttribute"                                      -> Builtin.DeviceAttribute,
    "cudaOccupancyMaxActiveBlocksPerMultiprocessor"               -> Builtin.Occupancy,
    "cudaOccupancyMaxActiveBlocksPerMultiprocessorWithFlags"      -> Builtin.Occupancy,
    "__cuda_syscall_cnpMaxActiveBlocksPerMultiprocessorWithFlags" -> Builtin.Occupancy,
    "cudaGetLastError"                                            -> Builtin.NoError,
    "cudaPeekAtLastError"                                         -> Builtin.NoError,
    "__cudaDeviceSynchronizeDeprecationAvoidance"                 -> Builtin.NoError,
    "cudaGetErrorName"                                            -> Builtin.NoError,
    "cudaGetErrorString"                                          -> Builtin.NoError,
    "vprintf"                                                     -> Builtin.NoError,
    "cudaGetParameterBuffer"                                      -> Builtin.ParameterBuffer,
    "malloc"                                                      -> Builtin.Malloc,
    "free"                                                        -> Builtin.Free,
    "__assertfail"                                                -> Builtin.AssertFail,
    "cudaLaunchDevice"                                            -> Builtin.LaunchDevice
  )

  private val integerUnary: Map[Opcode, IntegerUnaryOperation] = Map(
    Opcode.Cnot -> IntegerUnaryOperation.LogicalNot,
    Opcode.Popc -> IntegerUnaryOperation.PopulationCount,
    Opcode.Clz  -> IntegerUnaryOperation.LeadingZeroCount,
    Opcode.Brev -> IntegerUnaryOperation.BitReverse
  )

  private val floatUnary: Map[Opcode, FloatUnaryOperation] = Map(
    Opcode.Rcp   -> FloatUnaryOperation.Reciprocal,
    Opcode.Rsqrt -> FloatUnaryOperation.ReciprocalSquareRoot,
    Opcode.Sqrt  -> FloatUnaryOperation.SquareRoot,
    Opcode.Ex2   -> FloatUnaryOperation.Exponent2,
    Opcode.Lg2   -> FloatUnaryOperation.Log2,
    Opcode.Sin   -> FloatUnaryOperation.Sine,
    Opcode.Cos   -> FloatUnaryOperation.Cosine,
    Opcode.Tanh  -> FloatUnaryOperation.Tanh
  )

  /** The special-function unit's function each approximation lowers to. */
  private val mufuUnary: Map[Opcode, VMIR.MufuFunction] = Map(
    Opcode.Rcp   -> VMIR.MufuFunction.Reciprocal,
    Opcode.Rsqrt -> VMIR.MufuFunction.ReciprocalSquareRoot,
    Opcode.Sqrt  -> VMIR.MufuFunction.SquareRoot,
    Opcode.Ex2   -> VMIR.MufuFunction.Exponent2,
    Opcode.Lg2   -> VMIR.MufuFunction.Log2,
    Opcode.Sin   -> VMIR.MufuFunction.Sine,
    Opcode.Cos   -> VMIR.MufuFunction.Cosine
  )

  private def unary(destination: Operand, source: Operand)(make: (Register, Value) => VMIR.Instruction)(
      using scope: Scope
  ): Lowered = (scope.register(destination), scope.value(source)).mapN(make)

  private def binary(destination: Operand, left: Operand, right: Operand)(
      make: (Register, Value, Value) => VMIR.Instruction
  )(using scope: Scope): Lowered = (scope.register(destination), scope.value(left), scope.value(right)).mapN(make)

  private def ternary(destination: Operand, left: Operand, right: Operand, addend: Operand)(
      make: (Register, Value, Value, Value) => VMIR.Instruction
  )(using scope: Scope): Lowered =
    (scope.register(destination), scope.value(left), scope.value(right), scope.value(addend)).mapN(make)

  private def floatWidth(modifiers: Vec[Modifier]): Either[Error, Int in (16, 32, 64)] =
    modifiers.reverse.view.collectFirst {
      case Modifier.F16 => 16: Int in (16, 32, 64)
      case Modifier.F32 => 32: Int in (16, 32, 64)
      case Modifier.F64 => 64: Int in (16, 32, 64)
    }.toRight(Error.Invalid(s"expected an f16, f32, or f64 type in ${modifiers.map(_.name.raw).mkString(".")}"))

  /** Words wider than 32 bits select 64-bit carry and multiply-add arithmetic. */
  private def wordWidth(modifiers: Vec[Modifier]): ScalarBitWidth =
    if (modifiers.exists(Word32Types)) 32 else 64

  def format(modifier: Modifier): Option[NumericFormat] = modifier match {
    case Modifier.B8   => Some(NumericFormat.Bits(8))
    case Modifier.S8   => Some(NumericFormat.Signed(8))
    case Modifier.U8   => Some(NumericFormat.Unsigned(8))
    case Modifier.B16  => Some(NumericFormat.Bits(16))
    case Modifier.S16  => Some(NumericFormat.Signed(16))
    case Modifier.U16  => Some(NumericFormat.Unsigned(16))
    case Modifier.F16  => Some(NumericFormat.IEEE(16))
    case Modifier.Bf16 => Some(NumericFormat.BF16)
    case Modifier.B32  => Some(NumericFormat.Bits(32))
    case Modifier.S32  => Some(NumericFormat.Signed(32))
    case Modifier.U32  => Some(NumericFormat.Unsigned(32))
    case Modifier.F32  => Some(NumericFormat.IEEE(32))
    case Modifier.B64  => Some(NumericFormat.Bits(64))
    case Modifier.S64  => Some(NumericFormat.Signed(64))
    case Modifier.U64  => Some(NumericFormat.Unsigned(64))
    case Modifier.F64  => Some(NumericFormat.IEEE(64))
    case _             => None
  }

  private def rounding(modifier: Modifier): Option[ConversionRounding] = modifier match {
    case Modifier.Rn  => Some(ConversionRounding.RN)
    case Modifier.Rni => Some(ConversionRounding.RNI)
    case Modifier.Rz  => Some(ConversionRounding.RZ)
    case Modifier.Rzi => Some(ConversionRounding.RZI)
    case Modifier.Rm  => Some(ConversionRounding.RM)
    case Modifier.Rmi => Some(ConversionRounding.RMI)
    case Modifier.Rp  => Some(ConversionRounding.RP)
    case Modifier.Rpi => Some(ConversionRounding.RPI)
    case _            => None
  }

  def comparison(modifier: Modifier): Option[Comparison] = modifier match {
    case Modifier.Num => Some(Comparison.Ordered)
    case Modifier.Nan => Some(Comparison.Unordered)
    case Modifier.Eq  => Some(Comparison.Equal)
    case Modifier.Equ => Some(Comparison.EqualUnordered)
    case Modifier.Ne  => Some(Comparison.NotEqual)
    case Modifier.Neu => Some(Comparison.NotEqualUnordered)
    case Modifier.Lt  => Some(Comparison.LessThan)
    case Modifier.Ltu => Some(Comparison.LessThanUnordered)
    case Modifier.Le  => Some(Comparison.LessThanOrEqual)
    case Modifier.Leu => Some(Comparison.LessThanOrEqualUnordered)
    case Modifier.Gt  => Some(Comparison.GreaterThan)
    case Modifier.Gtu => Some(Comparison.GreaterThanUnordered)
    case Modifier.Ge  => Some(Comparison.GreaterThanOrEqual)
    case Modifier.Geu => Some(Comparison.GreaterThanOrEqualUnordered)
    case Modifier.Lo  => Some(Comparison.LessThan)
    case Modifier.Ls  => Some(Comparison.LessThanOrEqual)
    case Modifier.Hi  => Some(Comparison.GreaterThan)
    case Modifier.Hs  => Some(Comparison.GreaterThanOrEqual)
    case _            => None
  }

  private def bytePermuteMode(modifier: Modifier): Option[BytePermuteMode] = modifier match {
    case Modifier.F4e  => Some(BytePermuteMode.F4E)
    case Modifier.B4e  => Some(BytePermuteMode.B4E)
    case Modifier.Rc8  => Some(BytePermuteMode.RC8)
    case Modifier.Ecl  => Some(BytePermuteMode.ECL)
    case Modifier.Ecr  => Some(BytePermuteMode.ECR)
    case Modifier.Rc16 => Some(BytePermuteMode.RC16)
    case _             => None
  }

  private def videoSelect(part: OperandDomains.RegisterPart): Option[VideoSelect] = part.spelling.raw match {
    case "b0" => Some(VideoSelect.B0)
    case "b1" => Some(VideoSelect.B1)
    case "b2" => Some(VideoSelect.B2)
    case "b3" => Some(VideoSelect.B3)
    case "h0" => Some(VideoSelect.H0)
    case "h1" => Some(VideoSelect.H1)
    case _    => None
  }

  /** The elements of the register pair a packed video source names, in the order its lanes read them. */
  private def videoElements(operand: Operand): Option[VideoElements] = operand match {
    case Operand.Register(_, Some(part: OperandDomains.RegisterPart.Elements)) =>
      Some(VideoElements(part.indices.reverse, part.bits))
    case _ => None
  }

  /** The operand without the part that names the elements of a register pair it reads. */
  private def plain(operand: Operand): Operand = operand match {
    case Operand.Register(name, Some(_)) => Operand.Register(name, None)
    case other                           => other
  }

  private def floatControl(modifiers: Vec[Modifier]): FloatControl = FloatControl(
    flushSubnormal = modifiers.contains(Modifier.Ftz),
    saturate = modifiers.contains(Modifier.Sat),
    propagateNaN = modifiers.contains(Modifier.NaN),
    absolute = modifiers.contains(Modifier.Abs),
    xorSign = modifiers.contains(Modifier.Xorsign),
    rounding = modifiers
      .find(DirectedRoundings)
      .flatMap(rounding(_))
      .map(VMArithmetic.roundingMode)
      .getOrElse(IEEE754.Rounding.NearestEven),
    approximate = modifiers.contains(Modifier.Approx),
    full = modifiers.contains(Modifier.Full)
  )

  private def packedFormat(modifiers: Vec[Modifier]): PackedFloatFormat =
    if (modifiers.contains(Modifier.F32x2)) PackedFloatFormat.F32x2
    else if (modifiers.contains(Modifier.Bf16x2)) PackedFloatFormat.BF16x2
    else PackedFloatFormat.F16x2

  private val FloatingOpcodes = Set(
    Opcode.Add,
    Opcode.Sub,
    Opcode.Mul,
    Opcode.Div,
    Opcode.Fma,
    Opcode.Abs,
    Opcode.Neg,
    Opcode.Min,
    Opcode.Max,
    Opcode.CopySign,
    Opcode.Rcp,
    Opcode.Rsqrt,
    Opcode.Sqrt,
    Opcode.Ex2,
    Opcode.Lg2,
    Opcode.Sin,
    Opcode.Cos,
    Opcode.Tanh
  )
  private val ScalarFloatTypes               = Set(Modifier.F16, Modifier.F32, Modifier.F64)
  private val DirectedRoundings              = Set(Modifier.Rz, Modifier.Rm, Modifier.Rp)
  private val DirectedPackedFloat32Modifiers = DirectedRoundings ++
    Set(Modifier.F32x2, Modifier.Rn, Modifier.Ftz)
  private val DirectedSaturatingModifiers = DirectedRoundings ++ Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)

  def validateFloating(instruction: Instruction): Either[Error, Unit] = {
    val opcode    = instruction.signature.opcode
    val modifiers = instruction.signature.modifiers
    val types     = ScalarFloatTypes ++ Modifier.PackedHalfTypes + Modifier.Bf16 + Modifier.F32x2
    if (!FloatingOpcodes(opcode) || !modifiers.exists(types)) Right(())
    else {
      val packed  = modifiers.exists(Modifier.PackedHalfTypes)
      val bf16    = modifiers.contains(Modifier.Bf16)
      val allowed =
        if (
          modifiers.contains(Modifier.F32x2) &&
          PartialFunction.cond(opcode) { case Opcode.Add | Opcode.Sub | Opcode.Mul => true }
        )
          PackedFloat32Modifiers
        else if (modifiers.contains(Modifier.F32x2) && opcode == Opcode.Fma) DirectedPackedFloat32Modifiers
        else if (packed)
          Modifier.PackedHalfTypes ++
            (opcode match {
              case Opcode.Fma              => Set(Modifier.Rn, Modifier.Sat, Modifier.Relu)
              case Opcode.Min | Opcode.Max => Set(Modifier.NaN)
              case Opcode.Add | Opcode.Sub | Opcode.Mul if modifiers.contains(Modifier.F16x2) =>
                Set(Modifier.Rn, Modifier.Sat)
              // the half unit's exponential is the unit's own approximation
              case Opcode.Ex2 => Set(Modifier.Rn, Modifier.Approx)
              case _          => Set(Modifier.Rn)
            })
        else if (bf16 && opcode == Opcode.Fma) Set(Modifier.Bf16, Modifier.Rn, Modifier.Relu)
        else if (bf16) Set(Modifier.Bf16, Modifier.Rn)
        else ScalarFloatTypes ++
          (opcode match {
            case Opcode.Abs | Opcode.Neg              => Set(Modifier.Ftz)
            case Opcode.Min | Opcode.Max              => Set(Modifier.Ftz, Modifier.NaN, Modifier.Abs, Modifier.Xorsign)
            case Opcode.CopySign                      => Set.empty[Modifier]
            case Opcode.Add | Opcode.Sub | Opcode.Mul =>
              if (modifiers.contains(Modifier.F16)) Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)
              else if (modifiers.contains(Modifier.F64)) DirectedRoundings + Modifier.Rn
              else DirectedSaturatingModifiers
            case Opcode.Fma if modifiers.contains(Modifier.F16) =>
              Set(Modifier.Rn, Modifier.Rz, Modifier.Rm, Modifier.Rp, Modifier.Ftz, Modifier.Sat, Modifier.Relu)
            case Opcode.Fma => Set(Modifier.Rn, Modifier.Rz, Modifier.Rm, Modifier.Rp, Modifier.Ftz, Modifier.Sat)
            case Opcode.Rcp | Opcode.Sqrt =>
              // the directed roundings of the reciprocal and the square root are binary32 forms
              if (modifiers.contains(Modifier.F32))
                DirectedRoundings + Modifier.Rn + Modifier.Ftz + Modifier.Approx
              else Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx)
            case Opcode.Div =>
              if (modifiers.contains(Modifier.F32))
                DirectedRoundings + Modifier.Rn + Modifier.Ftz + Modifier.Approx + Modifier.Full
              else Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx, Modifier.Full)
            case _ => Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx)
          })
      val threeInput = instruction.form match {
        case Form.Min(_, _, _, Some(_)) | Form.Max(_, _, _, Some(_)) => true
        case _                                                       => false
      }
      val unsupported = modifiers.filterNot(allowed)
      if (threeInput && (modifiers.contains(Modifier.Xorsign) || !modifiers.contains(Modifier.F32)))
        invalid("three-input min and max take binary32 operands without xorsign")
      else if (threeInput) Right(())
      else if (bf16 && opcode != Opcode.Fma)
        Left(Error.Unsupported(
          instruction.qualifiedOpcode,
          "scalar bf16 arithmetic is not implemented for this operation"
        ))
      else if (unsupported.nonEmpty)
        Left(Error.Unsupported(
          instruction.qualifiedOpcode,
          s"unsupported floating modifiers: ${unsupported.mkString(".")}"
        ))
      else if (modifiers.distinct.size != modifiers.size || modifiers.count(types) != 1)
        invalid("expected one floating type and no duplicate modifiers")
      else if (modifiers.contains(Modifier.Xorsign) && !modifiers.contains(Modifier.Abs))
        invalid("xorsign requires abs")
      else Right(())
    }
  }

  def lowerOperation(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Lowered = {
    def has(modifier: Modifier): Boolean       = modifiers.contains(modifier)
    def hasAny(values: Set[Modifier]): Boolean = modifiers.exists(values)
    def endsWith(values: Modifier*): Boolean   = modifiers.lastOption.exists(values.contains)
    def startsWith(values: Modifier*): Boolean = modifiers.take(values.size) == Vec.from(values)
    def are(values: Modifier*): Boolean        = modifiers == Vec.from(values)
    def signed: Boolean                        = hasAny(SignedTypes)
    def integer(operation: IntegerOperation, destination: Operand, left: Operand, right: Operand): Lowered =
      scalarWidth(modifiers).flatMap(width =>
        binary(destination, left, right)(VMIR.Instruction.IntegerBinary(operation, _, _, _, width))
      )
    def float(operation: FloatOperation, destination: Operand, left: Operand, right: Operand): Lowered =
      floatWidth(modifiers).flatMap { width =>
        val control = floatControl(modifiers)
        modifiers.find(DirectedRoundings).flatMap(rounding(_)) match {
          case None =>
            binary(destination, left, right)(VMIR.Instruction.FloatBinary(operation, _, _, _, width, control))
          case Some(_) if operation == FloatOperation.Divide =>
            // the division rounds its own quotient, which no fused form computes
            binary(destination, left, right)(VMIR.Instruction.FloatBinary(operation, _, _, _, width, control))
          case Some(directed) =>
            // The fused form rounds the exact sum, difference or product once. A zero addend of this sign cannot flip
            // the sign of an exact zero product under the rounding.
            def constant(value: Double) = Value.Immediate(IEEE754.fromDouble(value, width))
            val zero                    = constant(if (directed == ConversionRounding.RM) 0.0 else -0.0)
            binary(destination, left, right) { (target, a, b) =>
              def fused(left: Value, right: Value, addend: Value) =
                VMIR.Instruction.FusedMultiplyAdd(target, left, right, addend, width, control, directed)
              operation match {
                case FloatOperation.Add      => fused(a, constant(1.0), b)
                case FloatOperation.Subtract => fused(b, constant(-1.0), a)
                case _                       => fused(a, b, zero)
              }
            }
        }
      }
    def packed(operation: FloatOperation, destination: Operand, left: Operand, right: Operand): Lowered =
      // a saturated sum, difference or product is the one rounding of the fused form with a unit or -0 operand
      if (has(Modifier.Sat)) binary(destination, left, right) { (target, a, b) =>
        def fused(left: Value, right: Value, addend: Value) =
          VMIR.Instruction.FusedMultiplyAddPacked(target, left, right, addend, bf16 = false, saturate = true)
        operation match {
          case FloatOperation.Add      => fused(a, Value.Immediate(0x3c003c00L), b)
          case FloatOperation.Subtract => fused(b, Value.Immediate(0xbc00bc00L), a)
          case _                       => fused(a, b, Value.Immediate(0x80008000L))
        }
      }
      else binary(destination, left, right)(
        VMIR.Instruction.FloatBinaryPacked(operation, _, _, _, packedFormat(modifiers), floatControl(modifiers))
      )
    def arithmetic(
        integerOperation: IntegerOperation,
        floatOperation: FloatOperation,
        destination: Operand,
        left: Operand,
        right: Operand
    ): Lowered =
      if (endsWith(PackedTypes.toSeq*)) packed(floatOperation, destination, left, right)
      else if (endsWith(Modifier.F16, Modifier.F32, Modifier.F64)) float(floatOperation, destination, left, right)
      else integer(integerOperation, destination, left, right)
    def unaryFloat(
        operation: FloatUnaryOperation,
        destination: Operand,
        source: Operand,
        width: Option[Int in (16, 32, 64)] = None
    )
        : Lowered =
      width.fold(floatWidth(modifiers))(Right(_)).flatMap(width =>
        unary(destination, source)(VMIR.Instruction.FloatUnary(operation, _, _, width, floatControl(modifiers)))
      )

    /** The unit's approximation of the function, whose polynomials differ between the architectures. */
    def mufuFloat(function: VMIR.MufuFunction, destination: Operand, source: Operand): Lowered = {
      val legacy = function match {
        case VMIR.MufuFunction.SquareRoot                      => scope.architecture < 61
        case VMIR.MufuFunction.Sine | VMIR.MufuFunction.Cosine => scope.architecture < 70
        case _                                                 => false
      }
      val flavour = if (legacy) Mufu.Flavour.Legacy else Mufu.Flavour.Modern
      unary(destination, source)(VMIR.Instruction.MufuUnary(_, _, function, flavour, floatControl(modifiers)))
    }
    def unaryInteger(operation: IntegerUnaryOperation, destination: Operand, source: Operand): Lowered =
      scalarWidth(modifiers).flatMap(width =>
        unary(destination, source)(VMIR.Instruction.IntegerUnary(operation, _, _, width))
      )
    def multiplyAdd(destination: Operand, left: Operand, right: Operand, addend: Operand)(
        operation: IntegerMultiplyAddOperation
    ): Lowered = ternary(destination, left, right, addend)(VMIR.Instruction.MultiplyAdd(_, _, _, _, operation))
    def carry(
        destination: Operand,
        left: Operand,
        right: Operand,
        form: CarryForm,
        carryOut: Boolean
    )
        : Lowered = {
      val width = wordWidth(modifiers)
      binary(destination, left, right)((register, a, b) =>
        if (form.subtract) VMIR.Instruction.IntegerSubtractWithBorrow(register, a, b, width, form.carryIn, carryOut)
        else VMIR.Instruction.IntegerAddWithCarry(register, a, b, width, form.carryIn, carryOut)
      )
    }
    def extremum(kind: Extremum, destination: Operand, left: Operand, right: Operand): Lowered = {
      val minimum  = kind == Extremum.Minimum
      val floating =
        if (minimum) if (has(Modifier.NaN)) FloatOperation.Minimum else FloatOperation.MinimumNumber
        else if (has(Modifier.NaN)) FloatOperation.Maximum
        else FloatOperation.MaximumNumber
      if (endsWith(Modifier.F16x2, Modifier.Bf16x2)) packed(floating, destination, left, right)
      else if (hasAny(FloatTypes)) float(floating, destination, left, right)
      else integer(
        if (minimum) IntegerOperation.Minimum(signed) else IntegerOperation.Maximum(signed),
        destination,
        left,
        right
      )
    }
    // bar is barrier with .aligned, and .cta names the only scope; the VM's barriers wait per lane either way.
    def barrier(first: Operand, second: Option[Operand], third: Option[Operand], fourth: Option[Operand]): Lowered = {
      def reduce(id: Operand, count: Option[Operand], source: Operand)(
          result: VMIR.BarrierResult
      ): Lowered = (scope.value(id), scope.predicateSource(source), count.traverse(scope.value(_))).mapN {
        case (barrier, (predicate, negated), threads) =>
          VMIR.Instruction.BarrierReduce(result, barrier, predicate, threads, negated)
      }
      (
        modifiers.filterNot(PartialFunction.cond(_) { case Modifier.Cta | Modifier.Aligned => true }),
        second,
        third,
        fourth
      ) match {
        case (Vec(Modifier.Sync | Modifier.Arrive), _, None, None) if instruction.pred.nonEmpty =>
          invalid(s"${instruction.qualifiedOpcode} must not be predicated in the supported execution profile")
        case (Vec(Modifier.Sync), count, None, None) =>
          (scope.value(first), count.traverse(scope.value(_))).mapN(VMIR.Instruction.BarrierSync(_, _))
        case (Vec(Modifier.Arrive), Some(count), None, None) =>
          (scope.value(first), scope.value(count)).mapN(VMIR.Instruction.BarrierArrive(_, _))
        case (Vec(Modifier.Red, operation @ (Modifier.And | Modifier.Or), Modifier.Pred), Some(id), Some(next), last) =>
          scope.predicate(first).flatMap(destination =>
            reduce(id, last.map(_ => next), last.getOrElse(next))(VMIR.BarrierResult.Combined(
              destination,
              if (operation == Modifier.And) PredicateOperation.And else PredicateOperation.Or
            ))
          )
        case (Vec(Modifier.Red, Modifier.Popc, Modifier.U32), Some(id), Some(next), last) =>
          scope.register(first).flatMap(destination =>
            reduce(id, last.map(_ => next), last.getOrElse(next))(VMIR.BarrierResult.Count(destination))
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
      }
    }
    val intWords = endsWith(Modifier.S32, Modifier.U32, Modifier.S64, Modifier.U64)
    def selected(operand: Operand): Either[Error, (value: Value, select: VideoSelect, negated: Boolean)] =
      operand match {
        case Operand.Negate(inner)              => selected(inner).map(found => (found.value, found.select, true))
        case Operand.Register(name, Some(part)) =>
          videoSelect(part).toRight(Error.Invalid(s"invalid video selector ${part.spelling}")).flatMap(select =>
            scope.value(Operand.Register(name, None)).map(value => (value, select, false))
          )
        case other => scope.value(other).map(value => (value, VideoSelect.Word, false))
      }
    def videoDestination(operand: Operand): Either[Error, (Register, VideoSelect)] = operand match {
      case Operand.Register(name, Some(part)) =>
        videoSelect(part).toRight(Error.Invalid(s"invalid video selector ${part.spelling}")).flatMap(select =>
          scope.register(Operand.Register(name, None)).map(_ -> select)
        )
      case other => scope.register(other).map(_ -> VideoSelect.Word)
    }
    def scalarVideo(operation: Option[VideoOperation], d: Operand, a: Operand, b: Operand, c: Option[Operand])
        : Lowered = {
      // the qualifiers after the types may come in any order
      val types = modifiers.filter(_.isInteger32)
      val shift = operation.exists(PartialFunction.cond(_) {
        case VideoOperation.ShiftLeft | VideoOperation.ShiftRight => true
      })
      val relations   = modifiers.collect(VideoRelations)
      val relation    = if (operation.nonEmpty || relations.size != 1) None else relations.headOption
      val saturate    = has(Modifier.Sat)
      val modes       = modifiers.filter(PartialFunction.cond(_) { case Modifier.Clamp | Modifier.Wrap => true })
      val mode        = if (shift && modes.size == 1) modes.headOption else None
      val secondaries = modifiers.filter(PartialFunction.cond(_) { case Modifier.Add | Modifier.Min | Modifier.Max =>
        true
      })
      val secondary = secondaries match {
        case _ if !modifiers.forall(VideoModifiers) || (!shift && modes.nonEmpty) => None
        case Vec()                                                                => Some(VideoSecondary.Plain)
        case Vec(Modifier.Add)                                                    => Some(VideoSecondary.Add)
        case Vec(Modifier.Min)                                                    => Some(VideoSecondary.Minimum)
        case Vec(Modifier.Max)                                                    => Some(VideoSecondary.Maximum)
        case _                                                                    => None
      }
      val signs = (operation.isEmpty, types) match {
        case (false, Vec(result, left, right)) => Some((result, left, right))
        case (true, Vec(left, right))          => Some((Modifier.U32, left, right))
        case _                                 => None
      }
      (operation.orElse(relation), signs, secondary) match {
        case (Some(chosen), Some((result, left, right)), Some(combine))
            if (!shift || (mode.nonEmpty && right == Modifier.U32)) && (operation.nonEmpty || !saturate) =>
          for {
            target <- videoDestination(d)
            first  <- selected(a)
            second <- selected(b)
            third  <- c.traverse(scope.value(_))
            _      <- Either.cond(
              !first.negated && !second.negated &&
                third.nonEmpty == (target._2 != VideoSelect.Word || combine != VideoSecondary.Plain) &&
                (target._2 == VideoSelect.Word || combine == VideoSecondary.Plain),
              (),
              Error.Invalid(
                s"${instruction.qualifiedOpcode} needs a third operand exactly for a merge or second operation"
              )
            )
          } yield VMIR.Instruction.VideoScalar(
            target._1,
            first.value,
            second.value,
            third.getOrElse(Value.Immediate(0L)),
            chosen,
            first.select,
            second.select,
            left == Modifier.S32,
            right == Modifier.S32,
            result == Modifier.S32,
            saturate,
            combine,
            target._2,
            mode.contains(Modifier.Wrap)
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported scalar video form"))
      }
    }
    def videoMultiplyAdd(d: Operand, a: Operand, b: Operand, c: Operand): Lowered = {
      val (types, rest)                    = modifiers.span(_.isInteger32)
      val plusOne                          = rest.headOption.contains(Modifier.Po)
      val afterPlus                        = if (plusOne) rest.drop(1) else rest
      val saturate                         = afterPlus.headOption.contains(Modifier.Sat)
      val shift: Option[Int in (0, 7, 15)] = (if (saturate) afterPlus.drop(1) else afterPlus) match {
        case Vec()               => Some(0)
        case Vec(Modifier.Shr7)  => Some(7)
        case Vec(Modifier.Shr15) => Some(15)
        case _                   => None
      }
      (types, shift) match {
        case (Vec(_, left, right), Some(scale)) =>
          for {
            target <- scope.register(d)
            first  <- selected(a)
            second <- selected(b)
            third  <- selected(c)
            _      <- Either.cond(
              third.select == VideoSelect.Word && (!plusOne || !(first.negated || second.negated || third.negated)) &&
                !(first.negated != second.negated && third.negated),
              (),
              Error.Invalid(s"${instruction.qualifiedOpcode} negates both the product and the addend")
            )
          } yield VMIR.Instruction.VideoMultiplyAdd(
            target,
            first.value,
            second.value,
            third.value,
            first.select,
            second.select,
            left == Modifier.S32,
            right == Modifier.S32,
            first.negated != second.negated,
            third.negated,
            // from sm_70 the hardware adds the addend sign-extended
            scope.architecture >= 70,
            plusOne,
            scale,
            saturate
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported vmad form"))
      }
    }
    def narrowMultiply(destination: Operand, left: Operand, right: Operand, addend: Option[Operand]): Lowered =
      modifiers match {
        case Vec(mode @ (Modifier.Lo | Modifier.Hi), kind @ (Modifier.U32 | Modifier.S32)) =>
          ternary(destination, left, right, addend.getOrElse(Operand.Immediate("0")))(
            VMIR.Instruction.MultiplyAdd(
              _,
              _,
              _,
              _,
              IntegerMultiplyAddOperation.Narrow(mode == Modifier.Hi, kind == Modifier.S32, false)
            )
          )
        case Vec(Modifier.Hi, Modifier.Sat, Modifier.S32) if addend.nonEmpty =>
          ternary(destination, left, right, addend.getOrElse(Operand.Immediate("0")))(
            VMIR.Instruction.MultiplyAdd(
              _,
              _,
              _,
              _,
              IntegerMultiplyAddOperation.Narrow(high = true, signed = true, saturate = true)
            )
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported 24-bit multiply form"))
      }
    def combinedComparison(other: Operand)(
        lowered: (Comparison, PredicateOperation, Vec[Modifier], Predicate, Boolean) => Lowered
    ): Lowered = {
      val operation = modifiers.lift(1).collect {
        case Modifier.And => PredicateOperation.And
        case Modifier.Or  => PredicateOperation.Or
        case Modifier.Xor => PredicateOperation.Xor
      }
      (modifiers.headOption.flatMap(comparison(_)), operation) match {
        case (Some(relation), Some(combine)) =>
          scope.predicateSource(other).flatMap((predicate, negated) =>
            lowered(relation, combine, modifiers.drop(2).filterNot(_ == Modifier.Ftz), predicate, negated)
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported comparison with a Boolean operator"))
      }
    }
    def stochastic(destination: Operand, source: Operand, second: Operand, third: Option[Operand]): Lowered = {
      val relu     = has(Modifier.Relu)
      val saturate = has(Modifier.Satfinite)
      (
        modifiers.filterNot(PartialFunction.cond(_) { case Modifier.Rs | Modifier.Relu | Modifier.Satfinite => true }),
        third
      ) match {
        case (Vec(kind @ (Modifier.F16x2 | Modifier.Bf16x2), Modifier.F32), Some(bits)) =>
          (scope.register(destination), scope.value(source), scope.value(second), scope.value(bits)).mapN(
            VMIR.Instruction.ConvertStochastic(
              _,
              _,
              _,
              _,
              Value.Immediate(0L),
              if (kind == Modifier.F16x2) StochasticFormat.Half else StochasticFormat.BFloat16,
              false,
              relu,
              saturate
            )
          )
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported stochastic conversion"))
      }
    }
    // Each family falls through to the next in source order, keeping every method within the JIT's size limit.
    def extensions(): Lowered = instruction.form match {
      case Form.Alloca(pointer, size, alignment) if are(Modifier.U32) || are(Modifier.U64) =>
        for {
          bytes <- alignment.traverse(value =>
            NumericLiteral.immediate(value).filter(bytes =>
              bytes > 0 && bytes <= (1L << 23) && (bytes & (bytes - 1)) == 0
            )
              .toRight(Error.Invalid(s"alloca alignment must be a power of two up to 2^23, found $value"))
          )
          lowered <- unary(pointer, size)(
            VMIR.Instruction.StackAllocate(_, _, math.max(bytes.getOrElse(8L), 8L).unsafe[Long > 0])
          )
        } yield lowered
      case Form.StackSave(destination) if are(Modifier.U32) || are(Modifier.U64) =>
        scope.register(destination).map(VMIR.Instruction.StackSave(_))
      case Form.StackRestore(source) if are(Modifier.U32) || are(Modifier.U64) =>
        scope.value(source).map(VMIR.Instruction.StackRestore(_))
      case Form.Cvt(destination, source, None, None)
          if modifiers.lastOption.contains(Modifier.F32) &&
            modifiers.dropRight(1).lastOption.contains(Modifier.Tf32) =>
        val (rounding, rest) = modifiers.dropRight(2).partition(PartialFunction.cond(_) {
          case Modifier.Rna | Modifier.Rn | Modifier.Rz => true
        })
        (
          rounding,
          rest.filterNot(PartialFunction.cond(_) { case Modifier.Satfinite | Modifier.Relu => true }),
          rest.contains(Modifier.Relu)
        ) match {
          case (Vec(mode), Vec(), relu) if mode != Modifier.Rna || !relu =>
            unary(destination, source)(
              VMIR.Instruction.ConvertTF32(
                _,
                _,
                mode == Modifier.Rna,
                mode == Modifier.Rz,
                relu,
                rest.contains(Modifier.Satfinite)
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported TF32 conversion"))
        }
      case Form.Cvt(destination, source, Some(second), third) if has(Modifier.Rs) =>
        stochastic(destination, source, second, third)
      case Form.Vadd(d, a, b, c)     => scalarVideo(Some(VideoOperation.Add), d, a, b, c)
      case Form.Vsub(d, a, b, c)     => scalarVideo(Some(VideoOperation.Subtract), d, a, b, c)
      case Form.Vabsdiff(d, a, b, c) => scalarVideo(Some(VideoOperation.AbsoluteDifference), d, a, b, c)
      case Form.Vmin(d, a, b, c)     => scalarVideo(Some(VideoOperation.Minimum), d, a, b, c)
      case Form.Vmax(d, a, b, c)     => scalarVideo(Some(VideoOperation.Maximum), d, a, b, c)
      case Form.Vshl(d, a, b, c)     => scalarVideo(Some(VideoOperation.ShiftLeft), d, a, b, c)
      case Form.Vshr(d, a, b, c)     => scalarVideo(Some(VideoOperation.ShiftRight), d, a, b, c)
      case Form.Vset(d, a, b, c)     => scalarVideo(None, d, a, b, c)
      case Form.Vmad(d, a, b, c)     => videoMultiplyAdd(d, a, b, c)
      case _                         => narrowAndCombined()
    }
    def narrowAndCombined(): Lowered = instruction.form match {
      case Form.Mul24(destination, left, right) =>
        narrowMultiply(destination, left, right, None)
      case Form.Mad24(destination, left, right, addend) =>
        narrowMultiply(destination, left, right, Some(addend))
      case Form.Mad(destination, left, right, addend) if has(Modifier.Hi) =>
        (
          modifiers.filterNot(PartialFunction.cond(_) { case Modifier.Hi | Modifier.Sat | Modifier.Cc => true }),
          has(Modifier.Sat),
          has(Modifier.Cc)
        ) match {
          case (Vec(Modifier.S32), true, _) =>
            multiplyAdd(destination, left, right, addend)(IntegerMultiplyAddOperation.HighSum(32, true, true))
          case (Vec(kind), false, true)
              if (SignedTypes ++ UnsignedTypes)(kind) && kind != Modifier.S8 && kind != Modifier.U8 =>
            scalarWidth(modifiers).flatMap(width =>
              multiplyAdd(destination, left, right, addend)(
                IntegerMultiplyAddOperation.HighSumCarry(width, SignedTypes(kind))
              )
            )
          case (Vec(kind), false, false)
              if (SignedTypes ++ UnsignedTypes)(kind) && kind != Modifier.S8 && kind != Modifier.U8 =>
            scalarWidth(modifiers).flatMap(width =>
              multiplyAdd(destination, left, right, addend)(
                IntegerMultiplyAddOperation.HighSum(width, SignedTypes(kind), saturate = false)
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported mad.hi form"))
        }
      case Form.Mad(destination, left, right, addend)
          if has(Modifier.Wide) && !endsWith(Modifier.U32) =>
        modifiers.filterNot(_ == Modifier.Wide) match {
          case Vec(kind @ (Modifier.S32 | Modifier.S16 | Modifier.U16)) =>
            multiplyAdd(destination, left, right, addend)(
              IntegerMultiplyAddOperation.Wide(if (kind == Modifier.S32) 32 else 16, kind != Modifier.U16)
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported mad.wide form"))
        }
      case Form.Slct(destination, whenNonNegative, whenNegative, condition) =>
        modifiers.filterNot(_ == Modifier.Ftz) match {
          case Vec(kind, test @ (Modifier.S32 | Modifier.F32))
              if (IntegerTypes ++ Set(Modifier.F32, Modifier.F64))(kind) &&
                (!has(Modifier.Ftz) || test == Modifier.F32) =>
            scalarWidth(Vec(kind)).flatMap(width =>
              ternary(destination, whenNonNegative, whenNegative, condition)(
                VMIR.Instruction.SelectSign(_, _, _, _, width, test == Modifier.F32, has(Modifier.Ftz))
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported slct form"))
        }
      case Form.TestPredicate(destination, source) =>
        val property = modifiers.headOption.collect {
          case Modifier.Finite     => FloatProperty.Finite
          case Modifier.Infinite   => FloatProperty.Infinite
          case Modifier.Number     => FloatProperty.Number
          case Modifier.Notanumber => FloatProperty.NotANumber
          case Modifier.Normal     => FloatProperty.Normal
          case Modifier.Subnormal  => FloatProperty.Subnormal
        }
        (property, modifiers.drop(1)) match {
          case (Some(found), Vec(kind @ (Modifier.F32 | Modifier.F64))) =>
            (scope.predicate(destination), scope.value(source))
              .mapN(VMIR.Instruction.TestProperty(_, _, found, if (kind == Modifier.F32) 32 else 64))
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported testp form"))
        }
      case Form.Set(destination, left, right, Some(other)) =>
        combinedComparison(other) { (relation, operation, rest, predicate, negated) =>
          (rest, rest.lift(1).flatMap(format(_))) match {
            case (Vec(result, _), Some(sourceFormat)) =>
              val truth = result match {
                case Modifier.F32                => Some(0x3f800000L)
                case Modifier.F16                => Some(0x3c00L)
                case Modifier.Bf16               => Some(0x3f80L)
                case Modifier.S32 | Modifier.U32 => Some(0xffffffffL)
                case _                           => None
              }
              truth.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported set result type")).flatMap(
                value =>
                  binary(destination, left, right)(
                    VMIR.Instruction.CompareSetCombined(
                      _,
                      relation,
                      sourceFormat,
                      _,
                      _,
                      value,
                      has(Modifier.Ftz),
                      operation,
                      predicate,
                      negated
                    )
                  )
              )
            case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported set types"))
          }
        }
      case Form.Setp(destination, left, right, Some(other)) =>
        combinedComparison(other) { (relation, operation, rest, predicate, negated) =>
          rest match {
            case Vec(kind) if format(kind).nonEmpty =>
              (scope.predicate(destination), scope.value(left), scope.value(right)).mapN(
                VMIR.Instruction.SetPredicateCombined(
                  _,
                  relation,
                  format(kind).getOrElse(NumericFormat.Bits(32)),
                  _,
                  _,
                  has(Modifier.Ftz),
                  operation,
                  predicate,
                  negated
                )
              )
            case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported setp types"))
          }
        }
      case _ => memoryAndMoves()
    }
    // the VM keeps no caches, so a cache policy only has to be a value
    def cachePolicy(policy: Option[Operand]): Either[Error, Unit] = policy.traverse(scope.value(_)).map(_ => ())
    def memoryAndMoves(): Lowered                                 = instruction.form match {
      case _: Form.WmmaLoad | _: Form.WmmaStore | _: Form.WmmaMma | _: Form.Mma |
          _: Form.MmaSp | _: Form.MmaSpOrderedMetadata | Form.Ldmatrix(_, _) | Form.Stmatrix(_, _) |
          Form.Movmatrix(_, _) =>
        VMTensorCompiler.lower(instruction)
      case _: Form.WgmmaMmaAsync | _: Form.WgmmaMmaAsyncSp | Form.WgmmaFence() |
          Form.WgmmaCommitGroup() | Form.WgmmaWaitGroup(_) =>
        VMWarpgroupCompiler.lower(instruction)
      case Form.Ld(destination, address, policy) if policy.isEmpty == !has(Modifier.L2CacheHint) =>
        cachePolicy(policy).flatMap(_ => VMMemoryCompiler.load(destination, address, modifiers))
      case Form.LdGlobalNc(destination, address, policy)
          if policy.isEmpty == !has(Modifier.L2CacheHint) =>
        cachePolicy(policy).flatMap(_ => VMMemoryCompiler.loadNonCoherent(destination, address, modifiers))
      // ldu reads memory no lane writes during the kernel, as a non-coherent load does
      case Form.Ldu(destination, address) =>
        VMMemoryCompiler.loadNonCoherent(destination, address, modifiers)
      case Form.St(address, source, policy) if policy.isEmpty == !has(Modifier.L2CacheHint) =>
        cachePolicy(policy).flatMap(_ => VMMemoryCompiler.store(address, source, modifiers))
      case Form.Mov(destination, source) => move(destination, source, modifiers)
      case _                             => unaryAndVideo()
    }
    def unaryAndVideo(): Lowered = instruction.form match {
      case Form.Activemask(destination) =>
        scope.register(destination).map(VMIR.Instruction.Move(_, Value.ActiveMask, 32))
      // a half pair's sign bits, which leave NaN payloads as unspecified as the PTX ISA allows
      case Form.Neg(destination, source) if hasAny(Modifier.PackedHalfTypes) && !has(Modifier.Ftz) =>
        unary(destination, source)(
          VMIR.Instruction.FloatUnaryPacked(
            FloatUnaryOperation.Negate,
            _,
            _,
            packedFormat(modifiers)
          )
        )
      case Form.Abs(destination, source) if hasAny(Modifier.PackedHalfTypes) && !has(Modifier.Ftz) =>
        unary(destination, source)(
          VMIR.Instruction.FloatUnaryPacked(
            FloatUnaryOperation.Absolute,
            _,
            _,
            packedFormat(modifiers)
          )
        )
      case Form.Neg(destination, source) =>
        if (hasAny(FloatTypes)) unaryFloat(FloatUnaryOperation.Negate, destination, source)
        else unaryInteger(IntegerUnaryOperation.Negate, destination, source)
      case Form.Abs(destination, source) =>
        if (hasAny(FloatTypes)) unaryFloat(FloatUnaryOperation.Absolute, destination, source)
        else unaryInteger(IntegerUnaryOperation.Absolute, destination, source)
      case Form.RcpApproxFtzF64(destination, source) =>
        unaryFloat(FloatUnaryOperation.Reciprocal, destination, source, Some(64))
      case Form.RsqrtApproxFtzF64(destination, source) =>
        unaryFloat(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, Some(64))
      // The half unit's exponential is less accurate than the binary32 one, which its own path takes.
      case Form.Ex2(destination, source) if endsWith(Modifier.F16x2) =>
        unary(destination, source)(
          VMIR.Instruction.FloatUnaryPacked(
            FloatUnaryOperation.Exponent2,
            _,
            _,
            packedFormat(modifiers),
            floatControl(modifiers)
          )
        )
      case Form.Ex2(destination, source) if endsWith(Modifier.Bf16x2) =>
        Left(Error.Unsupported(instruction.qualifiedOpcode, "the bfloat16 pair exponential is not implemented"))
      case Form.Ex2(destination, source) if endsWith(Modifier.F16) =>
        unaryFloat(FloatUnaryOperation.Exponent2, destination, source)
      case _ if floatUnary.contains(instruction.signature.opcode) =>
        // the unit's approximations are binary32 forms, which the binary64 ones do not use
        if (has(Modifier.Approx) && mufuUnary.contains(instruction.signature.opcode) && !endsWith(Modifier.F64))
          mufuFloat(mufuUnary(instruction.signature.opcode), instruction.operands(0), instruction.operands(1))
        else unaryFloat(floatUnary(instruction.signature.opcode), instruction.operands(0), instruction.operands(1))
      case Form.Not(destination, source) if are(Modifier.Pred) =>
        (scope.predicate(destination), scope.predicate(source)).mapN(VMIR.Instruction.PredicateUnary(_, _))
      case Form.Not(destination, source) =>
        unaryInteger(IntegerUnaryOperation.BitwiseNot, destination, source)
      case _ if integerUnary.contains(instruction.signature.opcode) =>
        unaryInteger(integerUnary(instruction.signature.opcode), instruction.operands(0), instruction.operands(1))
      case Form.Bfind(destination, source) =>
        scalarWidth(modifiers).flatMap(width =>
          unary(destination, source)(VMIR.Instruction.BitFind(_, _, width, signed, has(Modifier.Shiftamt)))
        )
      case Form.Dp4a(destination, left, right, addend) =>
        modifiers.filter(_.isInteger32) match {
          case Vec(leftType, rightType) =>
            ternary(destination, left, right, addend)(
              VMIR.Instruction.DotProduct4(_, _, _, _, leftType == Modifier.S32, rightType == Modifier.S32)
            )
          case _ => invalid(s"expected two dp4a operand types: ${instruction.qualifiedOpcode}")
        }
      case _ => lanes()
    }
    // Operations over the lanes of a word: SIMD video and packed integer arithmetic, and their neighbours.
    def video(operation: Option[VideoOperation], lanes: Int in (2, 4), operands: Vec[Operand]): Lowered = {
      val (types, rest) = modifiers.partition(_.isInteger32)
      val chosen        = operation.orElse(rest.headOption.collect(VideoRelations))
      val tail          = if (operation.isEmpty) rest.drop(1) else rest
      def simd(signs: Vec[Boolean], saturate: Boolean): Lowered = chosen.toRight(
        Error.Unsupported(instruction.qualifiedOpcode, "unsupported SIMD video comparison")
      ).flatMap(found =>
        ternary(
          plain(operands(0)),
          plain(operands(1)),
          plain(operands(2)),
          plain(operands(3))
        )(
          VMIR.Instruction.VideoSimd(
            _,
            _,
            _,
            _,
            operation = found,
            lanes = lanes,
            leftSigned = signs(1),
            rightSigned = signs(2),
            resultSigned = signs(0),
            saturate = saturate,
            accumulate = has(Modifier.Add),
            leftElements = videoElements(operands(1)),
            rightElements = videoElements(operands(2))
          )
        )
      )
      (types.map(_ == Modifier.S32), tail) match {
        case (Vec(result, left, right), Vec() | Vec(Modifier.Sat) | Vec(Modifier.Add)) if operation.nonEmpty =>
          simd(Vec(result, left, right), has(Modifier.Sat))
        case (Vec(left, right), Vec() | Vec(Modifier.Add)) if operation.isEmpty => simd(Vec(false, left, right), false)
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported SIMD video form"))
      }
    }
    def packedInteger(operation: VideoOperation, destination: Operand, left: Operand, right: Operand): Lowered = {
      val (lanes, signed) = modifiers.lastOption match {
        case Some(Modifier.S16x2) => (2: Int in (2, 4), true)
        case Some(Modifier.U16x2) => (2: Int in (2, 4), false)
        case Some(Modifier.S8x4)  => (4: Int in (2, 4), true)
        case _                    => (4: Int in (2, 4), false)
      }
      binary(destination, left, right)(
        VMIR.Instruction.VideoSimd(
          _,
          _,
          _,
          Value.Immediate(0L),
          operation,
          lanes,
          signed,
          signed,
          signed,
          has(Modifier.Sat),
          false
        )
      )
    }
    def lanes(): Lowered = instruction.form match {
      case Form.Add(destination, left, right) if endsWith(Modifier.U16x2, Modifier.S16x2) =>
        packedInteger(VideoOperation.Add, destination, left, right)
      case Form.Sub(destination, left, right) if endsWith(Modifier.U16x2, Modifier.S16x2) =>
        packedInteger(VideoOperation.Subtract, destination, left, right)
      case Form.Min(dest = destination, srcA = left, srcB = right, srcC = None)
          if endsWith(Modifier.U16x2, Modifier.S16x2, Modifier.U8x4, Modifier.S8x4) && !has(Modifier.Relu) =>
        packedInteger(VideoOperation.Minimum, destination, left, right)
      case Form.Max(dest = destination, srcA = left, srcB = right, srcC = None)
          if endsWith(Modifier.U16x2, Modifier.S16x2, Modifier.U8x4, Modifier.S8x4) && !has(Modifier.Relu) =>
        packedInteger(VideoOperation.Maximum, destination, left, right)
      case Form.Vadd2(d, a, b, c)     => video(Some(VideoOperation.Add), 2, Vec(d, a, b, c))
      case Form.Vsub2(d, a, b, c)     => video(Some(VideoOperation.Subtract), 2, Vec(d, a, b, c))
      case Form.Vavrg2(d, a, b, c)    => video(Some(VideoOperation.Average), 2, Vec(d, a, b, c))
      case Form.Vabsdiff2(d, a, b, c) => video(Some(VideoOperation.AbsoluteDifference), 2, Vec(d, a, b, c))
      case Form.Vmin2(d, a, b, c)     => video(Some(VideoOperation.Minimum), 2, Vec(d, a, b, c))
      case Form.Vmax2(d, a, b, c)     => video(Some(VideoOperation.Maximum), 2, Vec(d, a, b, c))
      case Form.Vset2(d, a, b, c)     => video(None, 2, Vec(d, a, b, c))
      case Form.Vadd4(d, a, b, c)     => video(Some(VideoOperation.Add), 4, Vec(d, a, b, c))
      case Form.Vsub4(d, a, b, c)     => video(Some(VideoOperation.Subtract), 4, Vec(d, a, b, c))
      case Form.Vavrg4(d, a, b, c)    => video(Some(VideoOperation.Average), 4, Vec(d, a, b, c))
      case Form.Vabsdiff4(d, a, b, c) => video(Some(VideoOperation.AbsoluteDifference), 4, Vec(d, a, b, c))
      case Form.Vmin4(d, a, b, c)     => video(Some(VideoOperation.Minimum), 4, Vec(d, a, b, c))
      case Form.Vmax4(d, a, b, c)     => video(Some(VideoOperation.Maximum), 4, Vec(d, a, b, c))
      case Form.Vset4(d, a, b, c)     => video(None, 4, Vec(d, a, b, c))
      case Form.Dp2a(destination, left, right, addend) =>
        modifiers match {
          case Vec(
                mode @ (Modifier.Lo | Modifier.Hi),
                leftType @ (Modifier.S32 | Modifier.U32),
                rightType @ (Modifier.S32 | Modifier.U32)
              ) =>
            ternary(destination, left, right, addend)(
              VMIR.Instruction.DotProduct2(
                _,
                _,
                _,
                _,
                mode == Modifier.Hi,
                leftType == Modifier.S32,
                rightType == Modifier.S32
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported dp2a form"))
        }
      case Form.Sad(destination, left, right, addend) =>
        scalarWidth(modifiers).flatMap(width =>
          ternary(destination, left, right, addend)(VMIR.Instruction.SumAbsoluteDifference(_, _, _, _, width, signed))
        )
      case Form.CvtPack(destination, high, low, rest) =>
        val target = modifiers.filterNot(_ == Modifier.Pack) match {
          case Vec(Modifier.Sat, kind @ (Modifier.S16 | Modifier.U16), Modifier.S32) if rest.isEmpty => Some(kind)
          case Vec(Modifier.Sat, kind, Modifier.S32, Modifier.B32) if rest.nonEmpty                  => Some(kind)
          case _                                                                                     => None
        }
        target.flatMap {
          case Modifier.S16 => Some((16: Int in 1 ~ 16, true))
          case Modifier.U16 => Some((16: Int in 1 ~ 16, false))
          case Modifier.S8  => Some((8: Int in 1 ~ 16, true))
          case Modifier.U8  => Some((8: Int in 1 ~ 16, false))
          case Modifier.S4  => Some((4: Int in 1 ~ 16, true))
          case Modifier.U4  => Some((4: Int in 1 ~ 16, false))
          case Modifier.S2  => Some((2: Int in 1 ~ 16, true))
          case Modifier.U2  => Some((2: Int in 1 ~ 16, false))
          case _            => None
        }.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported cvt.pack form")).flatMap((bits, signed) =>
          (scope.register(destination), scope.value(high), scope.value(low), rest.traverse(scope.value(_)))
            .mapN((register, a, b, c) =>
              VMIR.Instruction.PackSaturated(register, a, b, c.getOrElse(Value.Immediate(0L)), bits, signed)
            )
        )
      case Form.IsSpacePredicate(destination, address) =>
        modifiers.match {
          case Vec(Modifier.Global)                           => Some(AddressKind.Global)
          case Vec(Modifier.Shared) | Vec(Modifier.SharedCta) => Some(AddressKind.Shared)
          case Vec(Modifier.SharedCluster)                    => Some(AddressKind.Shared)
          case Vec(Modifier.Local)                            => Some(AddressKind.Local)
          case Vec(Modifier.Const)                            => Some(AddressKind.Constant)
          case _                                              => None
        }.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported isspacep state space")).flatMap(space =>
          (scope.predicate(destination), scope.value(address)).mapN(VMIR.Instruction.SpaceTest(_, _, space))
        )
      case Form.Fns(destination, mask, base, offset) if are(Modifier.B32) =>
        ternary(destination, mask, base, offset)(VMIR.Instruction.FindNthSet(_, _, _, _))
      case Form.Lop3(destination, first, second, third, table, None) if are(Modifier.B32) =>
        logicTable(table)
          .flatMap(lut => ternary(destination, first, second, third)(VMIR.Instruction.Logic3(_, _, _, _, lut, 32)))
      case Form.Mad(destination, left, right, addend)
          if has(Modifier.Lo) && endsWith(Modifier.S16, Modifier.U16) =>
        multiplyAdd(destination, left, right, addend)(IntegerMultiplyAddOperation.Low(16, 16, false, false))
      case _ => multiplicationAndShifts()
    }
    def multiplicationAndShifts(): Lowered = instruction.form match {
      case Form.Mul(destination, left, right) if has(Modifier.Wide) =>
        val (operation, width) = modifiers.filterNot(_ == Modifier.Wide) match {
          case Vec(Modifier.U32) => (Some(IntegerOperation.MultiplyWideUnsigned32), 64: ScalarBitWidth)
          case Vec(Modifier.S32) => (Some(IntegerOperation.MultiplyWideSigned32), 64: ScalarBitWidth)
          case Vec(Modifier.U16) => (Some(IntegerOperation.MultiplyWideUnsigned16), 32: ScalarBitWidth)
          case Vec(Modifier.S16) => (Some(IntegerOperation.MultiplyWideSigned16), 32: ScalarBitWidth)
          case _                 => (None, 64: ScalarBitWidth)
        }
        operation.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported widening multiply"))
          .flatMap(operation =>
            binary(destination, left, right)(VMIR.Instruction.IntegerBinary(operation, _, _, _, width))
          )
      case Form.Mul(destination, left, right) if startsWith(Modifier.Lo) =>
        integer(IntegerOperation.MultiplyLow, destination, left, right)
      case Form.Mul(destination, left, right) if startsWith(Modifier.Hi) =>
        integer(IntegerOperation.MultiplyHigh(signed), destination, left, right)
      case Form.Mul(destination, left, right) if endsWith((PackedTypes ++ ScalarFloatTypes).toSeq*) =>
        arithmetic(IntegerOperation.MultiplyLow, FloatOperation.Multiply, destination, left, right)
      case Form.Div(destination, left, right) =>
        arithmetic(IntegerOperation.Divide(signed), FloatOperation.Divide, destination, left, right)
      case Form.Rem(destination, left, right) =>
        integer(IntegerOperation.Remainder(signed), destination, left, right)
      case Form.CopySign(destination, left, right) =>
        float(FloatOperation.CopySign, destination, left, right)
      case Form.Fma(destination, left, right, addend) if endsWith(Modifier.F16x2, Modifier.Bf16x2) =>
        ternary(destination, left, right, addend)(
          VMIR.Instruction.FusedMultiplyAddPacked(_, _, _, _, endsWith(Modifier.Bf16x2), has(Modifier.Sat))
        )
      case Form.Fma(destination, left, right, addend) if endsWith(Modifier.Bf16) =>
        ternary(destination, left, right, addend)(VMIR.Instruction.FusedMultiplyAddBf16(_, _, _, _))
      case Form.Fma(destination, left, right, addend) =>
        floatWidth(modifiers).flatMap(width =>
          ternary(destination, left, right, addend)(
            VMIR.Instruction.FusedMultiplyAdd(
              _,
              _,
              _,
              _,
              width,
              floatControl(modifiers),
              modifiers.view.flatMap(rounding(_)).headOption.getOrElse(ConversionRounding.RN)
            )
          )
        )
      case Form.Shl(destination, left, right) =>
        integer(IntegerOperation.ShiftLeft, destination, left, right)
      case Form.Shr(destination, left, right) if modifiers.headOption.exists(UnsignedTypes ++ BitTypes) =>
        integer(IntegerOperation.ShiftRightLogical, destination, left, right)
      case Form.Shr(destination, left, right) if modifiers.headOption.exists(SignedTypes) =>
        integer(IntegerOperation.ShiftRightArithmetic, destination, left, right)
      case Form.Shf(destination, low, high, amount) if endsWith(Modifier.B32) =>
        ternary(destination, low, high, amount)(
          VMIR.Instruction.FunnelShift(_, _, _, _, startsWith(Modifier.L), has(Modifier.Clamp))
        )
      case _ => warpCollectives()
    }
    // Without .sync, vote and shfl act on the whole warp, which their .sync forms do with a full member mask.
    def members(memberMask: Option[Operand]): Either[Error, Value] = memberMask.fold(Right(FullWarp))(scope.value(_))
    def vote(destination: Operand, predicate: Operand, memberMask: Option[Operand]): Lowered =
      modifiers.filterNot(_ == Modifier.Sync) match {
        case Vec(Modifier.Ballot, Modifier.B32) =>
          (scope.register(destination), scope.predicateSource(predicate), members(memberMask)).mapN {
            case (register, (source, negated), mask) => VMIR.Instruction.VoteBallot(register, source, mask, negated)
          }
        case Vec(mode @ (Modifier.All | Modifier.Any | Modifier.Uni), Modifier.Pred) =>
          val operation = mode match {
            case Modifier.All => VoteOperation.All
            case Modifier.Any => VoteOperation.Any
            case _            => VoteOperation.Uniform
          }
          (scope.predicate(destination), scope.predicateSource(predicate), members(memberMask)).mapN {
            case (target, (source, negated), mask) =>
              VMIR.Instruction.VotePredicate(target, source, mask, operation, negated)
          }
        case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
      }
    def shuffle(destination: Operand, source: Operand, selector: Operand, clamp: Operand, memberMask: Option[Operand])
        : Lowered = {
      val mode = modifiers.filterNot(_ == Modifier.Sync) match {
        case Vec(Modifier.Down, Modifier.B32 | Modifier.B64) => Some(ShuffleMode.Down)
        case Vec(Modifier.Up, Modifier.B32 | Modifier.B64)   => Some(ShuffleMode.Up)
        case Vec(Modifier.Idx, Modifier.B32 | Modifier.B64)  => Some(ShuffleMode.Index)
        case Vec(Modifier.Bfly, Modifier.B32 | Modifier.B64) => Some(ShuffleMode.Butterfly)
        case _                                               => None
      }
      val (target, validity) = Operand.splitPredicate(destination)
      for {
        shuffle <- mode.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported shuffle mode"))
        width   <- scalarWidth(modifiers)
        lowered <- (
          scope.register(target),
          validity.traverse(scope.predicate(_)),
          scope.value(source),
          scope.value(selector),
          scope.value(clamp),
          members(memberMask)
        ).mapN(VMIR.Instruction.Shuffle(shuffle, _, _, _, _, _, _, width))
      } yield lowered
    }
    def warpCollectives(): Lowered = instruction.form match {
      case Form.Shfl(destination, source, selector, clamp) if endsWith(Modifier.B32) =>
        shuffle(destination, source, selector, clamp, None)
      case Form.ShflSync(destination, source, selector, clamp, memberMask)
          if modifiers.size == 3 && startsWith(Modifier.Sync) && endsWith(Modifier.B32, Modifier.B64) =>
        shuffle(destination, source, selector, clamp, Some(memberMask))
      case Form.ElectSync(Operand.WithPredicate(destination, predicate), memberMask)
          if are(Modifier.Sync) =>
        (
          scope.sink(destination),
          scope.predicate(predicate),
          scope.value(memberMask)
        ).mapN(VMIR.Instruction.Elect(_, _, _))
      case Form.Vote(destination, predicate) => vote(destination, predicate, None)
      case Form.VoteSync(destination, predicate, memberMask) if startsWith(Modifier.Sync) =>
        vote(destination, predicate, Some(memberMask))
      case _ => bitsAndArithmetic()
    }
    def bitsAndArithmetic(): Lowered = instruction.form match {
      case Form.Bfe(destination, source, start, length) =>
        scalarWidth(modifiers).flatMap(width =>
          ternary(destination, source, start, length)(VMIR.Instruction.BitFieldExtract(_, _, _, _, width, signed))
        )
      case Form.Bfi(destination, insert, base, position, length) =>
        scalarWidth(modifiers).flatMap(width =>
          (
            scope.register(destination),
            scope.value(insert),
            scope.value(base),
            scope.value(position),
            scope.value(length)
          )
            .mapN(VMIR.Instruction.BitFieldInsert(_, _, _, _, _, width))
        )
      case Form.Bmsk(destination, start, width) =>
        for {
          mode <- modifiers.collectFirst {
            case Modifier.Clamp => BitMaskMode.Clamp
            case Modifier.Wrap  => BitMaskMode.Wrap
          }.toRight(Error.Invalid("bmsk requires .clamp or .wrap"))
          _       <- Either.cond(has(Modifier.B32), (), Error.Invalid("bmsk requires a .b32 width"))
          lowered <- binary(destination, start, width)(VMIR.Instruction.BitMask(_, _, _, mode))
        } yield lowered
      case Form.Prmt(destination, left, right, control) if modifiers.headOption.contains(Modifier.B32) =>
        modifiers.drop(1).headOption.fold(Option(BytePermuteMode.Default))(bytePermuteMode(_))
          .toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported prmt mode"))
          .flatMap(mode => ternary(destination, left, right, control)(VMIR.Instruction.BytePermute(_, _, _, _, mode)))
      case Form.Mad(destination, left, right, addend) if has(Modifier.Lo) && intWords =>
        val width = wordWidth(modifiers)
        multiplyAdd(
          destination,
          left,
          right,
          addend
        )(IntegerMultiplyAddOperation.Low(width, width, carryIn = false, has(Modifier.Cc)))
      case Form.MadCc(destination, left, right, addend) if has(Modifier.Lo) && intWords =>
        val width = wordWidth(modifiers)
        multiplyAdd(
          destination,
          left,
          right,
          addend
        )(IntegerMultiplyAddOperation.Low(width, width, carryIn = false, carryOut = true))
      case Form.Mad(destination, left, right, addend) if has(Modifier.Wide) && endsWith(Modifier.U32) =>
        multiplyAdd(destination, left, right, addend)(IntegerMultiplyAddOperation.Low(32, 64, false, false))
      case Form.Madc(destination, left, right, addend)
          if (has(Modifier.Lo) || has(Modifier.Hi)) && intWords =>
        val width  = wordWidth(modifiers)
        val signed = modifiers.contains(Modifier.S32)
        multiplyAdd(destination, left, right, addend)(
          if (has(Modifier.Hi)) IntegerMultiplyAddOperation.High(width, width, has(Modifier.Cc), signed)
          else IntegerMultiplyAddOperation.Low(width, width, carryIn = true, has(Modifier.Cc))
        )
      case Form.AddCc(destination, left, right) if intWords =>
        carry(destination, left, right, CarryForm.AddCc, carryOut = true)
      case Form.Addc(destination, left, right) if intWords =>
        carry(destination, left, right, CarryForm.Addc, carryOut = has(Modifier.Cc))
      case Form.SubCc(destination, left, right) if intWords =>
        carry(destination, left, right, CarryForm.SubCc, carryOut = true)
      case Form.Subc(destination, left, right) if intWords =>
        carry(destination, left, right, CarryForm.Subc, carryOut = has(Modifier.Cc))
      case Form.And(destination, left, right) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Form.Or(destination, left, right) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Form.Xor(destination, left, right) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Form.And(destination, left, right) =>
        integer(IntegerOperation.BitwiseAnd, destination, left, right)
      case Form.Or(destination, left, right) =>
        integer(IntegerOperation.BitwiseOr, destination, left, right)
      case Form.Xor(destination, left, right) =>
        integer(IntegerOperation.BitwiseXor, destination, left, right)
      case Form.Add(destination, left, right) =>
        arithmetic(
          if (has(Modifier.Sat)) IntegerOperation.AddSaturated else IntegerOperation.Add,
          FloatOperation.Add,
          destination,
          left,
          right
        )
      case Form.Sub(destination, left, right) =>
        arithmetic(
          if (has(Modifier.Sat)) IntegerOperation.SubtractSaturated else IntegerOperation.Subtract,
          FloatOperation.Subtract,
          destination,
          left,
          right
        )
      case Form.Min(dest = destination, srcA = left, srcB = right) =>
        extremum(Extremum.Minimum, destination, left, right)
      case Form.Max(dest = destination, srcA = left, srcB = right) =>
        extremum(Extremum.Maximum, destination, left, right)
      case _ => conversions()
    }
    def conversions(): Lowered = instruction.form match {
      // Local-space addresses are relative to the frame's window; the other windows are already generic.
      case Form.Cvta(destination, source)
          if (if (startsWith(Modifier.To)) modifiers.drop(1) else modifiers).match {
            case Vec(space, Modifier.U64) =>
              PartialFunction.cond(space) {
                case Modifier.Const | Modifier.Global | Modifier.Local | Modifier.Param | Modifier.Shared => true
              }
            case _ => false
          } =>
        (scope.register(destination), scope.offsetValue(source)).mapN((register, value) =>
          if (!has(Modifier.Local)) VMIR.Instruction.Move(register, value, 64)
          else if (has(Modifier.To))
            VMIR.Instruction.IntegerBinary(IntegerOperation.Subtract, register, value, Value.LocalBase, 64)
          else VMIR.Instruction.IntegerBinary(IntegerOperation.Add, register, value, Value.LocalBase, 64)
        )
      case Form.Cvt(destination, source, second, None)
          if modifiers.exists(PartialFunction.cond(_) { case Modifier.E4m3x2 | Modifier.E5m2x2 => true }) =>
        val format = if (has(Modifier.E4m3x2)) Float8Format.E4M3 else Float8Format.E5M2
        val relu   = has(Modifier.Relu)
        (modifiers.filterNot(_ == Modifier.Relu), second) match {
          case (Vec(Modifier.Rn, Modifier.Satfinite, Modifier.E4m3x2 | Modifier.E5m2x2, Modifier.F32), Some(low)) =>
            ternary(destination, source, low, source)((register, a, b, _) =>
              VMIR.Instruction.ConvertToFloat8(register, a, b, Value.Immediate(0L), format, halves = false, relu)
            )
          case (Vec(Modifier.Rn, Modifier.Satfinite, Modifier.E4m3x2 | Modifier.E5m2x2, Modifier.F16x2), None) =>
            unary(destination, source)((register, value) =>
              VMIR.Instruction.ConvertToFloat8(register, value, value, Value.Immediate(0L), format, halves = true, relu)
            )
          case (Vec(Modifier.Rn, Modifier.F16x2, Modifier.E4m3x2 | Modifier.E5m2x2), None) if !relu =>
            unary(destination, source)(VMIR.Instruction.ConvertFromFloat8(_, _, format))
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported 8-bit floating conversion"))
        }
      case Form.Cvt(destination, high, Some(low), None) =>
        (
          modifiers.flatMap(rounding(_)),
          modifiers.filterNot(modifier => rounding(modifier).nonEmpty || modifier == Modifier.Relu)
        ) match {
          case (Vec(mode), Vec(target @ (Modifier.F16x2 | Modifier.Bf16x2), Modifier.F32)) =>
            binary(destination, high, low)(
              VMIR.Instruction.ConvertPacked(
                _,
                _,
                _,
                NumericFormat.IEEE(32),
                if (target == Modifier.F16x2) NumericFormat.IEEE(16) else NumericFormat.BF16,
                mode,
                Option.when(has(Modifier.Relu))(ConversionClamp.Relu)
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported packed conversion"))
        }
      case Form.Set(destination, left, right, None) =>
        (modifiers.headOption.flatMap(comparison(_)), modifiers.drop(1).filterNot(_ == Modifier.Ftz)) match {
          case (Some(relation), Vec(result, source @ (Modifier.F16x2 | Modifier.Bf16x2))) =>
            val truth = result match {
              case Modifier.F16x2              => Some(0x3c00L)
              case Modifier.Bf16x2             => Some(0x3f80L)
              case Modifier.U32 | Modifier.S32 => Some(0xffffL)
              case _                           => None
            }
            truth.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported set result type")).flatMap(
              value =>
                binary(destination, left, right)(
                  VMIR.Instruction.CompareSet(
                    _,
                    relation,
                    if (source == Modifier.F16x2) NumericFormat.IEEE(16) else NumericFormat.BF16,
                    _,
                    _,
                    value,
                    packed = true,
                    flushSubnormal = has(Modifier.Ftz)
                  )
                )
            )
          case (Some(relation), Vec(result, source)) =>
            val truth = result match {
              case Modifier.U32 | Modifier.S32 => Some(0xffffffffL)
              case Modifier.U16 | Modifier.S16 => Some(0xffffL)
              case Modifier.F32                => Some(0x3f800000L)
              case Modifier.F16                => Some(0x3c00L)
              case Modifier.Bf16               => Some(0x3f80L)
              case _                           => None
            }
            (truth, format(source)) match {
              case (Some(value), Some(sourceFormat)) =>
                binary(destination, left, right)(
                  VMIR.Instruction.CompareSet(_, relation, sourceFormat, _, _, value, packed = false, has(Modifier.Ftz))
                )
              case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported set types"))
            }
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported set form"))
        }
      case Form.Cvt(destination, source, None, None) =>
        modifiers.flatMap(format(_)) match {
          case types if types.size >= 2 =>
            unary(destination, source)(
              VMIR.Instruction.Convert(
                _,
                _,
                types.last,
                types(types.size - 2),
                modifiers.view.flatMap(rounding(_)).headOption,
                if (has(Modifier.Sat)) Some(ConversionClamp.Saturate)
                else Option.when(has(Modifier.Relu))(ConversionClamp.Relu)
              )
            )
          case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "unsupported conversion types"))
        }
      case Form.Selp(destination, whenTrue, whenFalse, predicate) =>
        for {
          width   <- scalarWidth(modifiers)
          lowered <-
            (scope.register(destination), scope.value(whenTrue), scope.value(whenFalse), scope.predicate(predicate))
              .mapN(VMIR.Instruction.Select(_, _, _, _, width))
        } yield lowered
      case _ => asynchronous()
    }
    def asynchronous(): Lowered = VMAsyncCompiler.lower(instruction, modifiers)
      .orElse(VMTensorMemoryCompiler.lower(instruction, modifiers)).getOrElse(instruction.form match {
        case Form.MbarrierInit(address, count) if localMBarrier(modifiers) =>
          (scope.address(address), scope.value(count)).mapN(VMIR.Instruction.MBarrierInit(_, _))
        case Form.MbarrierInval(address) if localMBarrier(modifiers) =>
          scope.address(address).map(VMIR.Instruction.MBarrierInvalidate(_))
        case Form.CpAsync(
              destAddr = destination,
              srcAddr = source,
              copySize = copySize,
              srcSizeOrIgnore = sourceSize
            ) =>
          for {
            size <- NumericLiteral.immediate(copySize).filter(Set(4L, 8L, 16L)).map(_.toInt.unsafe[Int > 0])
              .toRight(Error.Invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $copySize"))
            bytes   <- sourceSize.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(size.toLong)))
            lowered <-
              (scope.address(destination), scope.address(source)).mapN(VMIR.Instruction.AsyncCopy(_, _, size, bytes))
          } yield lowered
        case Form.CpAsyncCommitGroup() | Form.CpAsyncWaitAll() | Form.CpAsyncWaitGroup(_) =>
          Right(VMIR.Instruction.NoOp)
        case _ => synchronisation()
      })
    def synchronisation(): Lowered = instruction.form match {
      case Form.ReduxSync(destination, source, memberMask)
          if modifiers.match {
            case Vec(Modifier.Sync, operation, width) =>
              (PartialFunction.cond(operation) { case Modifier.Add | Modifier.Min | Modifier.Max => true } &&
                PartialFunction.cond(width) { case Modifier.U32 | Modifier.S32 | Modifier.U64 | Modifier.S64 =>
                  true
                }) ||
              (PartialFunction.cond(operation) { case Modifier.And | Modifier.Or | Modifier.Xor => true } &&
                width == Modifier.B32)
            case _ => false
          } =>
        val operation = modifiers(1) match {
          case Modifier.Add => IntegerOperation.Add
          case Modifier.Min => IntegerOperation.Minimum(signed)
          case Modifier.Max => IntegerOperation.Maximum(signed)
          case Modifier.And => IntegerOperation.BitwiseAnd
          case Modifier.Or  => IntegerOperation.BitwiseOr
          case _            => IntegerOperation.BitwiseXor
        }
        for {
          width   <- scalarWidth(modifiers)
          lowered <- binary(destination, source, memberMask)(VMIR.Instruction.WarpReduce(_, _, _, operation, width))
        } yield lowered
      case atom: Form.Atom                            => atomic(instruction, atom, modifiers)
      case red: Form.Red                              => reduction(instruction, red, modifiers)
      case Form.Setp(destination, left, right, None)  => setPredicate(destination, left, right, modifiers)
      case Form.Barrier(first, second, third, fourth) => barrier(first, second, third, fourth)
      case Form.Bar(first, second, third, fourth)     => barrier(first, second, third, fourth)
      case Form.BarrierCluster()
          if ClusterBarrierModifiers(instruction.mods.filterNot(_ == Modifier.Aligned)) =>
        Right(VMIR.Instruction.ClusterBarrier(has(Modifier.Arrive)))
      case Form.ClusterLaunchControlTryCancel(response, barrier)
          if ClusterCancelModifiers(instruction.mods.filterNot(_ == Modifier.MulticastClusterAll)) =>
        (scope.address(response), scope.address(barrier)).mapN(
          VMIR.Instruction.ClusterTryCancel(_, _, has(Modifier.MulticastClusterAll))
        )
      case Form.ClusterLaunchControlQueryCancel(destination, response)
          if instruction.mods == Vec(Modifier.IsCanceled, Modifier.Pred, Modifier.B128) =>
        for {
          predicate <- scope.predicate(destination)
          parts     <- scope.values(response).flatMap(pair(_, "cluster cancellation response"))
        } yield VMIR.Instruction.ClusterCancelStatus(predicate, parts(1))
      case Form.ClusterLaunchControlQueryCancel(destination, response)
          if instruction.mods.drop(1) == Vec(Modifier.B32, Modifier.B128) =>
        for {
          register <- scope.register(destination)
          parts    <- scope.values(response).flatMap(pair(_, "cluster cancellation response"))
          axis     <- instruction.mods.headOption match {
            case Some(Modifier.GetFirstCtaidX) => Right(Axis.X)
            case Some(Modifier.GetFirstCtaidY) => Right(Axis.Y)
            case Some(Modifier.GetFirstCtaidZ) => Right(Axis.Z)
            case _                             => invalid("expected a cluster cancellation coordinate dimension")
          }
        } yield VMIR.Instruction.ClusterCancelCta(register, parts(0), parts(1), axis)
      // The launcher retains completion dependencies instead of scheduling dependent grids early. A full fence gives
      // the required visibility; launch_dependents may conservatively delay release until grid exit.
      case Form.GridDependencyControl() if are(Modifier.Wait) || are(Modifier.LaunchDependents) =>
        Right(VMIR.Instruction.Fence)
      case Form.Membar() if modifiers.size == 1 && hasAny(FenceScopeModifiers) =>
        Right(VMIR.Instruction.Fence)
      // Supported thread fences execute with stronger full-fence semantics. clang can name the scope first, as in
      // fence.gpu.sc, which ptxas accepts.
      case Form.Fence(None, None)
          if modifiers.match {
            case Vec(scope)         => (Scopes + Modifier.Cluster)(scope)
            case Vec(first, second) =>
              def ordering(value: Modifier) = PartialFunction.cond(value) {
                case Modifier.Sc | Modifier.AcqRel | Modifier.Acquire | Modifier.Release => true
              }
              val scopes = Scopes + Modifier.Cluster
              (ordering(first) && scopes(second)) || (scopes(first) && ordering(second))
            case _ => false
          } =>
        Right(VMIR.Instruction.Fence)
      case Form.Prefetch(_) | Form.NanoSleep(_) => Right(VMIR.Instruction.NoOp)
      case Form.BarWarpSync(memberMask)         => scope.value(memberMask).map(VMIR.Instruction.WarpBarrier(_))
      case Form.Exit()                          => Right(VMIR.Instruction.Exit)
      case Form.Ret()                           => Right(VMIR.Instruction.Return)
      case Form.Trap()                          => Right(VMIR.Instruction.Trap)
      case _                                    => calls()
    }
    def calls(): Lowered = instruction.form match {
      case Form.Call(returnParams = returnParameters, function = function, params = parameters) =>
        for {
          returns   <- returnParameters.traverse(scope.callParameters(_)).map(_.getOrElse(Vec.empty[VMIR.Parameter]))
          arguments <- parameters.traverse(scope.callParameters(_)).map(_.getOrElse(Vec.empty[VMIR.Parameter]))
          lowered   <- function match {
            case Operand.Symbol(name) if scope.definedFunction(name) =>
              scope.directCalls += name
              Right(VMIR.Instruction.Call(
                Value.Immediate(scope.functionAddress(name).getOrElse(0L)),
                returns,
                arguments
              ))
            case Operand.Symbol(name) if builtins.contains(name.raw) =>
              Right(VMIR.Instruction.CallBuiltin(builtins(name.raw), returns, arguments))
            case Operand.Symbol(name) =>
              scope.functionAddress(name).map(address =>
                VMIR.Instruction.Call(Value.Immediate(address), returns, arguments)
              )
                .toRight(Error.Invalid(s"unknown function $name"))
            case other => scope.value(other).map(VMIR.Instruction.Call(_, returns, arguments))
          }
        } yield lowered
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
    }

    extensions()
  }

  private def move(destination: Operand, source: Operand, modifiers: Vec[Modifier])(using scope: Scope): Lowered =
    modifiers match {
      case Vec(Modifier.B128) =>
        for {
          destinations <- scope.registers(destination).flatMap(pair(_, "mov.b128 destination"))
          sources      <- scope.values(source).flatMap(pair(_, "mov.b128 source"))
        } yield VMIR.Instruction.MoveVector(destinations, sources, 64)
      case _ if modifiers.contains(Modifier.V2) || modifiers.contains(Modifier.V4) =>
        val count = if (modifiers.contains(Modifier.V2)) 2 else 4
        for {
          destinations <- scope.registers(destination)
          sources      <- scope.values(source)
          _            <- Either.cond(
            destinations.size == count && sources.size == count,
            (),
            Error.Invalid(s"vector move requires $count source and destination components")
          )
          width <- scalarWidth(modifiers)
          _     <- Either.cond(count * width <= 128, (), Error.Invalid("vector move exceeds 128 bits"))
        } yield VMIR.Instruction.MoveVector(destinations, sources, width)
      case Vec(Modifier.Pred) =>
        (scope.predicate(destination), scope.predicateValue(source)).mapN(VMIR.Instruction.MovePredicate(_, _))
      case Vec(Modifier.U64 | Modifier.B64 | Modifier.B32) if Operand.isVectorLiteral(destination) =>
        val part: Int in (16, 32) = if (modifiers.head == Modifier.B32) 16 else 32
        for {
          destinations <- scope.sinks(destination).flatMap(pair(_, s"mov.b${part * 2} destination"))
          value        <- scope.value(source)
        } yield VMIR.Instruction.MoveSplit(destinations, value, part)
      case Vec(Modifier.U64 | Modifier.B64 | Modifier.B32) if Operand.isVectorLiteral(source) =>
        val part: Int in (16, 32) = if (modifiers.head == Modifier.B32) 16 else 32
        for {
          register <- scope.register(destination)
          sources  <- scope.values(source).flatMap(pair(_, s"mov.b${part * 2} source"))
        } yield VMIR.Instruction.MoveCombine(register, sources, part)
      case Vec(scalar) if (IntegerTypes ++ Set(Modifier.F32, Modifier.F64))(scalar) =>
        scalarWidth(modifiers).flatMap(width => unary(destination, source)(VMIR.Instruction.Move(_, _, width)))
      case _ =>
        Left(Error.Unsupported(s"mov.${modifiers.map(_.name.raw).mkString(".")}", "no handwritten semantic family"))
    }

  private def predicateBinary(operation: PredicateOperation, destination: Operand, left: Operand, right: Operand)(
      using scope: Scope
  ): Lowered = (scope.predicate(destination), scope.predicate(left), scope.predicate(right))
    .mapN(VMIR.Instruction.PredicateBinary(operation, _, _, _))

  private val LocalMBarrierModifiers: Set[Modifier] = Set(
    Modifier.Init,
    Modifier.Inval,
    Modifier.Arrive,
    Modifier.ExpectTx,
    Modifier.TestWait,
    Modifier.TryWait,
    Modifier.Parity,
    Modifier.B64,
    // every VM mbarrier has the v0 layout; v1 tracks fabric reports, which the VM does not model
    Modifier.LayoutV0,
    Modifier.Shared,
    Modifier.SharedCta,
    Modifier.Cta,
    Modifier.Acquire,
    Modifier.Release,
    Modifier.Relaxed
  )

  private def localMBarrier(modifiers: Vec[Modifier]): Boolean = modifiers.forall(LocalMBarrierModifiers)

  private def setPredicate(destination: Operand, left: Operand, right: Operand, modifiers: Vec[Modifier])(
      using scope: Scope
  ): Lowered =
    for {
      relation <- modifiers.view.flatMap(comparison(_)).headOption
        .toRight(Error.Unsupported(s"setp.${modifiers.map(_.name.raw).mkString(".")}", "unsupported setp comparison"))
      operandType <- modifiers.find(modifier => modifier == Modifier.Pred || format(modifier).nonEmpty)
        .toRight(Error.Unsupported(s"setp.${modifiers.map(_.name.raw).mkString(".")}", "unsupported setp type"))
      _ <- Either.cond(
        !modifiers.contains(Modifier.Ftz) || operandType == Modifier.F32,
        (),
        Error.Unsupported("setp", "ftz requires f32 comparison operands")
      )
      target  <- scope.predicate(destination)
      lowered <- format(operandType) match {
        case None => (
            scope.predicate(left),
            scope.predicate(right)
          ).mapN(VMIR.Instruction.PredicateCompare(target, relation, _, _))
        case Some(numeric) =>
          (scope.value(left), scope.value(right)).mapN(
            VMIR.Instruction.SetPredicate(target, relation, numeric, _, _, modifiers.contains(Modifier.Ftz))
          )
      }
    } yield lowered

  /** The state space an atomic or reduction names, and the modifiers after it, without ordering, scope or cache hint;
    * the host CAS path is sequentially consistent, which satisfies every weaker PTX atomic order and scope.
    */
  def atomicSpace(modifiers: Vec[Modifier]): (MemorySpace, Vec[Modifier]) =
    modifiers.filterNot(Orders ++ Scopes + Modifier.L2CacheHint + Modifier.Cluster) match {
      case Vec(Modifier.Global, rest*) => (MemorySpace.Global, rest.toVec)
      case Vec(Modifier.Shared, rest*) => (MemorySpace.Shared, rest.toVec)
      // shared::cluster addresses route by their window like generic ones.
      case Vec(Modifier.SharedCluster, rest*) => (MemorySpace.Generic, rest.toVec)
      case rest                               => (MemorySpace.Generic, rest)
    }

  def atomicWidth(modifier: Modifier): ScalarBitWidth =
    if (modifier.is64Bit) 64
    else if (modifier == Modifier.F16 || modifier == Modifier.Bf16 || modifier == Modifier.B16) 16
    else 32

  /** The operation and type of an atomic or reduction whose modifiers are `rest`. */
  def atomicOperation(rest: Vec[Modifier]): Option[(Modifier, AtomicOperation)] =
    rest match {
      case Vec(Modifier.Max, kind) if AtomicWords(kind) =>
        Some(kind -> (if (SignedTypes(kind)) AtomicOperation.MaximumSigned else AtomicOperation.MaximumUnsigned))
      case Vec(Modifier.Min, kind) if AtomicWords(kind) =>
        Some(kind -> (if (SignedTypes(kind)) AtomicOperation.MinimumSigned else AtomicOperation.MinimumUnsigned))
      case Vec(Modifier.And, kind @ (Modifier.B32 | Modifier.B64))  => Some(kind -> AtomicOperation.And)
      case Vec(Modifier.Or, kind @ (Modifier.B32 | Modifier.B64))   => Some(kind -> AtomicOperation.Or)
      case Vec(Modifier.Xor, kind @ (Modifier.B32 | Modifier.B64))  => Some(kind -> AtomicOperation.Xor)
      case Vec(Modifier.Exch, kind @ (Modifier.B32 | Modifier.B64)) => Some(kind -> AtomicOperation.Exchange)
      case Vec(Modifier.Add, kind) if AtomicWords(kind)             => Some(kind -> AtomicOperation.Add)
      case Vec(Modifier.Add, kind @ (Modifier.F32 | Modifier.F64))  => Some(kind -> AtomicOperation.FloatAdd)
      case Vec(Modifier.Add, Modifier.Noftz, Modifier.F16)          => Some(Modifier.F16 -> AtomicOperation.FloatAdd)
      case Vec(Modifier.Add, Modifier.Noftz, Modifier.Bf16)   => Some(Modifier.Bf16 -> AtomicOperation.BFloat16Add)
      case Vec(Modifier.Add, Modifier.Noftz, Modifier.F16x2)  => Some(Modifier.F16x2 -> AtomicOperation.PackedHalfAdd)
      case Vec(Modifier.Add, Modifier.Noftz, Modifier.Bf16x2) =>
        Some(Modifier.Bf16x2 -> AtomicOperation.PackedBFloat16Add)
      case Vec(operation @ (Modifier.Min | Modifier.Max), Modifier.Noftz, kind) =>
        val maximum = operation == Modifier.Max
        (kind match {
          case Modifier.F16   => Some(if (maximum) AtomicOperation.FloatMaximum else AtomicOperation.FloatMinimum)
          case Modifier.Bf16  => Some(if (maximum) AtomicOperation.BFloat16Maximum else AtomicOperation.BFloat16Minimum)
          case Modifier.F16x2 =>
            Some(if (maximum) AtomicOperation.PackedHalfMaximum else AtomicOperation.PackedHalfMinimum)
          case Modifier.Bf16x2 =>
            Some(if (maximum) AtomicOperation.PackedBFloat16Maximum else AtomicOperation.PackedBFloat16Minimum)
          case _ => None
        }).map(kind -> _)
      case Vec(Modifier.Inc, Modifier.U32) => Some(Modifier.U32 -> AtomicOperation.IncrementWrap)
      case Vec(Modifier.Dec, Modifier.U32) => Some(Modifier.U32 -> AtomicOperation.DecrementWrap)
      case _                               => None
    }

  private def reduction(instruction: Instruction, red: Form.Red, modifiers: Vec[Modifier])(using
      scope: Scope
  ): Lowered = {
    val Form.Red(address, value, cachePolicy) = red
    val (space, rest)                         = atomicSpace(modifiers)
    (cachePolicy, atomicOperation(rest)) match {
      case (policy, Some((kind, operation))) if policy.isEmpty == !modifiers.contains(Modifier.L2CacheHint) =>
        (scope.address(address), scope.value(value), policy.traverse(scope.value(_))).mapN((target, source, _) =>
          VMIR.Instruction.Atomic(None, target, source, atomicWidth(kind), space, operation)
        )
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
    }
  }

  private def atomic(instruction: Instruction, atom: Form.Atom, modifiers: Vec[Modifier])(using
      scope: Scope
  ): Lowered = {
    val Form.Atom(destination, address, value, compareOrCachePolicy) = atom
    val (space, rest)                                                = atomicSpace(modifiers)
    def width(modifier: Modifier): ScalarBitWidth                    = atomicWidth(modifier)
    (rest, compareOrCachePolicy, atomicOperation(rest)) match {
      case (Vec(Modifier.Cas, kind @ (Modifier.B16 | Modifier.B32 | Modifier.B64)), Some(source), _) =>
        (scope.register(destination), scope.address(address), scope.value(value), scope.value(source))
          .mapN(VMIR.Instruction.AtomicCompareExchange(_, _, _, _, width(kind), space))
      case (_, policy, Some((kind, atomicOperation)))
          if policy.isEmpty == !modifiers.contains(Modifier.L2CacheHint) =>
        (scope.register(destination), scope.address(address), scope.value(value), policy.traverse(scope.value(_)))
          .mapN((register, target, source, _) =>
            VMIR.Instruction.Atomic(Some(register), target, source, width(kind), space, atomicOperation)
          )
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
    }
  }
}
