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
private object VMInstructionCompiler {
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

  private def format(modifier: Modifier): Option[NumericFormat] = modifier match {
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

  private def comparison(modifier: Modifier): Option[Comparison] = modifier match {
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

  private def floatControl(modifiers: Vec[Modifier]): FloatControl = FloatControl(
    flushSubnormal = modifiers.contains(Modifier.Ftz),
    saturate = modifiers.contains(Modifier.Sat),
    propagateNaN = modifiers.contains(Modifier.NaN),
    absolute = modifiers.contains(Modifier.Abs),
    xorSign = modifiers.contains(Modifier.Xorsign)
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
  private val ScalarFloatTypes  = Set(Modifier.F16, Modifier.F32, Modifier.F64)
  private val DirectedRoundings = Set(Modifier.Rz, Modifier.Rm, Modifier.Rp)
  private val PackedHalfTypes   = Set(Modifier.F16x2, Modifier.Bf16x2)

  private def validateFloating(instruction: Instruction): Either[Error, Unit] = {
    val opcode    = instruction.signature.opcode
    val modifiers = instruction.signature.modifiers
    val types     = ScalarFloatTypes ++ PackedHalfTypes + Modifier.Bf16 + Modifier.F32x2
    if (!FloatingOpcodes(opcode) || !modifiers.exists(types)) Right(())
    else {
      val packed  = modifiers.exists(PackedHalfTypes)
      val bf16    = modifiers.contains(Modifier.Bf16)
      val allowed =
        if (modifiers.contains(Modifier.F32x2) && Set(Opcode.Add, Opcode.Sub, Opcode.Mul)(opcode))
          Set(Modifier.F32x2, Modifier.Rn, Modifier.Ftz)
        else if (packed)
          PackedHalfTypes ++
            (opcode match {
              case Opcode.Fma              => Set(Modifier.Rn, Modifier.Sat)
              case Opcode.Min | Opcode.Max => Set(Modifier.NaN)
              case _                       => Set(Modifier.Rn)
            })
        else if (bf16) Set(Modifier.Bf16, Modifier.Rn)
        else ScalarFloatTypes ++
          (opcode match {
            case Opcode.Abs | Opcode.Neg              => Set(Modifier.Ftz)
            case Opcode.Min | Opcode.Max              => Set(Modifier.Ftz, Modifier.NaN, Modifier.Abs, Modifier.Xorsign)
            case Opcode.CopySign                      => Set.empty[Modifier]
            case Opcode.Add | Opcode.Sub | Opcode.Mul =>
              if (modifiers.contains(Modifier.F16)) Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)
              else if (modifiers.contains(Modifier.F64)) DirectedRoundings + Modifier.Rn
              else DirectedRoundings ++ Set(Modifier.Rn, Modifier.Ftz, Modifier.Sat)
            case Opcode.Fma => Set(Modifier.Rn, Modifier.Rz, Modifier.Rm, Modifier.Rp, Modifier.Ftz, Modifier.Sat)
            case Opcode.Div => Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx, Modifier.Full)
            case _          => Set(Modifier.Rn, Modifier.Ftz, Modifier.Approx)
          })
      val threeInput = instruction match {
        case Instruction.Min(_, _, _, Some(_), _, _) | Instruction.Max(_, _, _, Some(_), _, _) => true
        case _                                                                                 => false
      }
      val unsupported = modifiers.filterNot(allowed)
      if (threeInput) Left(Error.Unsupported(instruction.qualifiedOpcode, "three-input min/max is not implemented"))
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

  def lower(instruction: Instruction)(using scope: Scope): Either[Error, VMAssembler.Statement] = {
    val modifiers = instruction.signature.modifiers
    for {
      _ <- modifiers.collectFirst { case Modifier.Unrecognised(spelling) => spelling }
        .toLeft(()).leftMap(spelling =>
          Error.Unsupported(instruction.qualifiedOpcode, s"unknown modifier: ${spelling.raw}")
        )
      _         <- validateFloating(instruction)
      predicate <- instruction.pred.traverse(value =>
        scope.predicate(value.operand).map(ExecutionPredicate(_, value.negated))
      )
      lowered <- instruction match {
        case Instruction.Bra(target, _, _) if modifiers.isEmpty || modifiers == Vec(Modifier.Uni) =>
          target match {
            case Operand.Symbol(label) => Right(VMAssembler.Statement.Branch(label, predicate))
            case other                 => invalid(s"expected branch label, found $other")
          }
        case _ => lowerOperation(instruction, modifiers).map(VMAssembler.Statement.Execute(_, predicate))
      }
    } yield lowered
  }

  private def lowerOperation(instruction: Instruction, modifiers: Vec[Modifier])(using scope: Scope): Lowered = {
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
      binary(destination, left, right)(
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
        subtract: Boolean,
        carryIn: Boolean,
        carryOut: Boolean
    )
        : Lowered = {
      val width = wordWidth(modifiers)
      binary(destination, left, right)((register, a, b) =>
        if (subtract) VMIR.Instruction.IntegerSubtractWithBorrow(register, a, b, width, carryIn, carryOut)
        else VMIR.Instruction.IntegerAddWithCarry(register, a, b, width, carryIn, carryOut)
      )
    }
    def extremum(minimum: Boolean, destination: Operand, left: Operand, right: Operand): Lowered = {
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
    val intWords = endsWith(Modifier.S32, Modifier.U32, Modifier.S64, Modifier.U64)
    // Each family falls through to the next in source order, keeping every method within the JIT's size limit.
    def memoryAndMoves(): Lowered = instruction match {
      case Instruction.Cvt(destination, source, None, None, _, _)
          if are(Modifier.Rna, Modifier.Tf32, Modifier.F32) =>
        unary(destination, source)(VMIR.Instruction.ConvertTF32(_, _))
      case _: Instruction.WmmaLoad | _: Instruction.WmmaStore | _: Instruction.WmmaMma | _: Instruction.Mma |
          _: Instruction.Ldmatrix | _: Instruction.Stmatrix =>
        VMTensorCompiler.lower(instruction)
      case Instruction.Ld(destination, address, None, _, _) =>
        VMMemoryCompiler.load(destination, address, modifiers, false)
      case Instruction.LdGlobalNc(destination, address, None, _, _) =>
        VMMemoryCompiler.load(destination, address, modifiers, true)
      case Instruction.St(address, source, None, _, _) => VMMemoryCompiler.store(address, source, modifiers)
      case Instruction.Mov(destination, source, _, _)  => move(destination, source, modifiers)
      case _                                           => unaryAndVideo()
    }
    def unaryAndVideo(): Lowered = instruction match {
      case Instruction.Activemask(destination, _, _) =>
        scope.register(destination).map(VMIR.Instruction.Move(_, Value.ActiveMask, 32))
      case Instruction.Neg(destination, source, _, _) =>
        if (hasAny(FloatTypes)) unaryFloat(FloatUnaryOperation.Negate, destination, source)
        else unaryInteger(IntegerUnaryOperation.Negate, destination, source)
      case Instruction.Abs(destination, source, _, _) =>
        if (hasAny(FloatTypes)) unaryFloat(FloatUnaryOperation.Absolute, destination, source)
        else unaryInteger(IntegerUnaryOperation.Absolute, destination, source)
      case Instruction.RcpApproxFtzF64(destination, source, _, _) =>
        unaryFloat(FloatUnaryOperation.Reciprocal, destination, source, Some(64))
      case Instruction.RsqrtApproxFtzF64(destination, source, _, _) =>
        unaryFloat(FloatUnaryOperation.ReciprocalSquareRoot, destination, source, Some(64))
      case _ if floatUnary.contains(instruction.signature.opcode) =>
        unaryFloat(floatUnary(instruction.signature.opcode), instruction.operands(0), instruction.operands(1))
      case Instruction.Not(destination, source, _, _) if are(Modifier.Pred) =>
        (scope.predicate(destination), scope.predicate(source)).mapN(VMIR.Instruction.PredicateUnary(_, _))
      case Instruction.Not(destination, source, _, _) =>
        unaryInteger(IntegerUnaryOperation.BitwiseNot, destination, source)
      case _ if integerUnary.contains(instruction.signature.opcode) =>
        unaryInteger(integerUnary(instruction.signature.opcode), instruction.operands(0), instruction.operands(1))
      case Instruction.Vadd(destination, left, right, Some(addend), _, _)
          if are(Modifier.U32, Modifier.U32, Modifier.U32, Modifier.Add) =>
        ternary(destination, left, right, addend)(
          VMIR.Instruction.IntegerAddThree(_, _, _, _, 32, AddThreeCarryMode.None, false, false, false)
        )
      case Instruction.Vsub(destination, left, right, Some(addend), _, _)
          if are(Modifier.U32, Modifier.U32, Modifier.U32, Modifier.Add) =>
        ternary(destination, left, right, addend)(VMIR.Instruction.IntegerSubtractAdd(_, _, _, _, 32))
      case Instruction.Bfind(destination, source, _, _) =>
        scalarWidth(modifiers).flatMap(width =>
          unary(destination, source)(VMIR.Instruction.BitFind(_, _, width, signed, has(Modifier.Shiftamt)))
        )
      case Instruction.Dp4a(destination, left, right, addend, _, _) =>
        modifiers.filter(Set(Modifier.S32, Modifier.U32)) match {
          case Vec(leftType, rightType) =>
            ternary(destination, left, right, addend)(
              VMIR.Instruction.DotProduct4(_, _, _, _, leftType == Modifier.S32, rightType == Modifier.S32)
            )
          case _ => invalid(s"expected two dp4a operand types: ${instruction.qualifiedOpcode}")
        }
      case _ => multiplicationAndShifts()
    }
    def multiplicationAndShifts(): Lowered = instruction match {
      case Instruction.Mul(destination, left, right, _, _) if has(Modifier.Wide) =>
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
      case Instruction.Mul(destination, left, right, _, _) if startsWith(Modifier.Lo) =>
        integer(IntegerOperation.MultiplyLow, destination, left, right)
      case Instruction.Mul(destination, left, right, _, _) if startsWith(Modifier.Hi) =>
        integer(IntegerOperation.MultiplyHigh(signed), destination, left, right)
      case Instruction.Mul(destination, left, right, _, _) if endsWith((PackedTypes ++ ScalarFloatTypes).toSeq*) =>
        arithmetic(IntegerOperation.MultiplyLow, FloatOperation.Multiply, destination, left, right)
      case Instruction.Div(destination, left, right, _, _) =>
        arithmetic(IntegerOperation.Divide(signed), FloatOperation.Divide, destination, left, right)
      case Instruction.Rem(destination, left, right, _, _) =>
        integer(IntegerOperation.Remainder(signed), destination, left, right)
      case Instruction.CopySign(destination, left, right, _, _) =>
        float(FloatOperation.CopySign, destination, left, right)
      case Instruction.Fma(destination, left, right, addend, _, _) if endsWith(Modifier.F16x2, Modifier.Bf16x2) =>
        ternary(destination, left, right, addend)(
          VMIR.Instruction.FusedMultiplyAddPacked(_, _, _, _, endsWith(Modifier.Bf16x2), has(Modifier.Sat))
        )
      case Instruction.Fma(destination, left, right, addend, _, _) if endsWith(Modifier.Bf16) =>
        ternary(destination, left, right, addend)(VMIR.Instruction.FusedMultiplyAddBf16(_, _, _, _))
      case Instruction.Fma(destination, left, right, addend, _, _) =>
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
      case Instruction.Shl(destination, left, right, _, _) =>
        integer(IntegerOperation.ShiftLeft, destination, left, right)
      case Instruction.Shr(destination, left, right, _, _) if modifiers.headOption.exists(UnsignedTypes ++ BitTypes) =>
        integer(IntegerOperation.ShiftRightLogical, destination, left, right)
      case Instruction.Shr(destination, left, right, _, _) if modifiers.headOption.exists(SignedTypes) =>
        integer(IntegerOperation.ShiftRightArithmetic, destination, left, right)
      case Instruction.Shf(destination, low, high, amount, _, _) if endsWith(Modifier.B32) =>
        ternary(destination, low, high, amount)(
          VMIR.Instruction.FunnelShift(_, _, _, _, startsWith(Modifier.L), has(Modifier.Clamp))
        )
      case _ => warpCollectives()
    }
    def warpCollectives(): Lowered = instruction match {
      case Instruction.ShflSync(destination, source, selector, clamp, memberMask, _, _)
          if modifiers.size == 3 && startsWith(Modifier.Sync) && endsWith(Modifier.B32, Modifier.B64) =>
        val mode = modifiers(1) match {
          case Modifier.Down => Some(ShuffleMode.Down)
          case Modifier.Up   => Some(ShuffleMode.Up)
          case Modifier.Idx  => Some(ShuffleMode.Index)
          case Modifier.Bfly => Some(ShuffleMode.Butterfly)
          case _             => None
        }
        val (target, validity) = destination match {
          case Operand.WithPredicate(base, predicate) => (base, Some(predicate))
          case other                                  => (other, None)
        }
        for {
          shuffle <- mode.toRight(Error.Unsupported(instruction.qualifiedOpcode, "unsupported shuffle mode"))
          width   <- scalarWidth(modifiers)
          lowered <- (
            scope.register(target),
            validity.traverse(scope.predicate(_)),
            scope.value(source),
            scope.value(selector),
            scope.value(clamp),
            scope.value(memberMask)
          ).mapN(VMIR.Instruction.Shuffle(shuffle, _, _, _, _, _, _, width))
        } yield lowered
      case Instruction.ElectSync(Operand.WithPredicate(destination, predicate), memberMask, _, _)
          if are(Modifier.Sync) =>
        (
          scope.sink(destination),
          scope.predicate(predicate),
          scope.value(memberMask)
        ).mapN(VMIR.Instruction.Elect(_, _, _))
      case Instruction.Vote(destination, predicate, _, _) if are(Modifier.Ballot, Modifier.B32) =>
        (scope.register(destination), scope.predicateSource(predicate)).mapN { case (register, (source, negated)) =>
          VMIR.Instruction.VoteBallot(register, source, Value.Immediate(0xffffffffL), negated)
        }
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if are(Modifier.Sync, Modifier.Ballot, Modifier.B32) =>
        (scope.register(destination), scope.predicateSource(predicate), scope.value(memberMask)).mapN {
          case (register, (source, negated), mask) => VMIR.Instruction.VoteBallot(register, source, mask, negated)
        }
      case Instruction.VoteSync(destination, predicate, memberMask, _, _)
          if modifiers.size == 3 && startsWith(Modifier.Sync) && endsWith(Modifier.Pred) &&
            Set(Modifier.All, Modifier.Any, Modifier.Uni)(modifiers(1)) =>
        val operation = modifiers(1) match {
          case Modifier.All => VoteOperation.All
          case Modifier.Any => VoteOperation.Any
          case _            => VoteOperation.Uniform
        }
        (scope.predicate(destination), scope.predicateSource(predicate), scope.value(memberMask)).mapN {
          case (target, (source, negated), mask) =>
            VMIR.Instruction.VotePredicate(target, source, mask, operation, negated)
        }
      case _ => bitsAndArithmetic()
    }
    def bitsAndArithmetic(): Lowered = instruction match {
      case Instruction.Bfe(destination, source, start, length, _, _) =>
        scalarWidth(modifiers).flatMap(width =>
          ternary(destination, source, start, length)(VMIR.Instruction.BitFieldExtract(_, _, _, _, width, signed))
        )
      case Instruction.Bfi(destination, insert, base, position, length, _, _) =>
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
      case Instruction.Bmsk(destination, start, width, _, _) =>
        for {
          mode <- modifiers.collectFirst {
            case Modifier.Clamp => BitMaskMode.Clamp
            case Modifier.Wrap  => BitMaskMode.Wrap
          }.toRight(Error.Invalid("bmsk requires .clamp or .wrap"))
          _       <- Either.cond(has(Modifier.B32), (), Error.Invalid("bmsk requires a .b32 width"))
          lowered <- binary(destination, start, width)(VMIR.Instruction.BitMask(_, _, _, mode))
        } yield lowered
      case Instruction.Prmt(destination, left, right, control, _, _) if modifiers.headOption.contains(Modifier.B32) =>
        modifiers.drop(1).headOption.fold(Option(BytePermuteMode.Default))(bytePermuteMode(_))
          .toRight(Error.Invalid(s"unsupported prmt mode: ${modifiers.map(_.name.raw).mkString(".")}"))
          .flatMap(mode => ternary(destination, left, right, control)(VMIR.Instruction.BytePermute(_, _, _, _, mode)))
      case Instruction.Mad(destination, left, right, addend, _, _) if has(Modifier.Lo) && intWords =>
        val width = wordWidth(modifiers)
        multiplyAdd(
          destination,
          left,
          right,
          addend
        )(IntegerMultiplyAddOperation.Low(width, width, false, has(Modifier.Cc)))
      case Instruction.MadCc(destination, left, right, addend, _, _) if has(Modifier.Lo) && intWords =>
        val width = wordWidth(modifiers)
        multiplyAdd(destination, left, right, addend)(IntegerMultiplyAddOperation.Low(width, width, false, true))
      case Instruction.Mad(destination, left, right, addend, _, _) if has(Modifier.Wide) && endsWith(Modifier.U32) =>
        multiplyAdd(destination, left, right, addend)(IntegerMultiplyAddOperation.Low(32, 64, false, false))
      case Instruction.Madc(destination, left, right, addend, _, _)
          if (has(Modifier.Lo) || has(Modifier.Hi)) && intWords =>
        val width = wordWidth(modifiers)
        multiplyAdd(destination, left, right, addend)(
          if (has(Modifier.Hi)) IntegerMultiplyAddOperation.High(width, width, true, has(Modifier.Cc))
          else IntegerMultiplyAddOperation.Low(width, width, true, has(Modifier.Cc))
        )
      case Instruction.AddCc(destination, left, right, _, _) if intWords =>
        carry(destination, left, right, subtract = false, carryIn = false, carryOut = true)
      case Instruction.Addc(destination, left, right, _, _) if intWords =>
        carry(destination, left, right, subtract = false, carryIn = true, carryOut = has(Modifier.Cc))
      case Instruction.SubCc(destination, left, right, _, _) if intWords =>
        carry(destination, left, right, subtract = true, carryIn = false, carryOut = true)
      case Instruction.Subc(destination, left, right, _, _) if intWords =>
        carry(destination, left, right, subtract = true, carryIn = true, carryOut = has(Modifier.Cc))
      case Instruction.And(destination, left, right, _, _) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.And, destination, left, right)
      case Instruction.Or(destination, left, right, _, _) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.Or, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _) if are(Modifier.Pred) =>
        predicateBinary(PredicateOperation.Xor, destination, left, right)
      case Instruction.And(destination, left, right, _, _) =>
        integer(IntegerOperation.BitwiseAnd, destination, left, right)
      case Instruction.Or(destination, left, right, _, _) =>
        integer(IntegerOperation.BitwiseOr, destination, left, right)
      case Instruction.Xor(destination, left, right, _, _) =>
        integer(IntegerOperation.BitwiseXor, destination, left, right)
      case Instruction.Add(destination, left, right, _, _) =>
        arithmetic(
          if (has(Modifier.Sat)) IntegerOperation.AddSaturated else IntegerOperation.Add,
          FloatOperation.Add,
          destination,
          left,
          right
        )
      case Instruction.Sub(destination, left, right, _, _) =>
        arithmetic(
          if (has(Modifier.Sat)) IntegerOperation.SubtractSaturated else IntegerOperation.Subtract,
          FloatOperation.Subtract,
          destination,
          left,
          right
        )
      case Instruction.Min(destination, left, right, _, _, _) => extremum(minimum = true, destination, left, right)
      case Instruction.Max(destination, left, right, _, _, _) => extremum(minimum = false, destination, left, right)
      case _                                                  => conversions()
    }
    def conversions(): Lowered = instruction match {
      // Local-space addresses are relative to the frame's window; the other windows are already generic.
      case Instruction.Cvta(destination, source, _, _)
          if (if (startsWith(Modifier.To)) modifiers.drop(1) else modifiers).match {
            case Vec(space, Modifier.U64) =>
              Set(Modifier.Const, Modifier.Global, Modifier.Local, Modifier.Param, Modifier.Shared)(space)
            case _ => false
          } =>
        (scope.register(destination), scope.offsetValue(source)).mapN((register, value) =>
          if (!has(Modifier.Local)) VMIR.Instruction.Move(register, value, 64)
          else if (has(Modifier.To))
            VMIR.Instruction.IntegerBinary(IntegerOperation.Subtract, register, value, Value.LocalBase, 64)
          else VMIR.Instruction.IntegerBinary(IntegerOperation.Add, register, value, Value.LocalBase, 64)
        )
      case Instruction.Cvt(destination, source, None, None, _, _) =>
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
          case _ => invalid(s"expected conversion source and destination types: ${instruction.qualifiedOpcode}")
        }
      case Instruction.Selp(destination, whenTrue, whenFalse, predicate, _, _) =>
        for {
          width   <- scalarWidth(modifiers)
          lowered <-
            (scope.register(destination), scope.value(whenTrue), scope.value(whenFalse), scope.predicate(predicate))
              .mapN(VMIR.Instruction.Select(_, _, _, _, width))
        } yield lowered
      case _ => asynchronous()
    }
    def asynchronous(): Lowered = instruction match {
      case Instruction.MbarrierInit(address, count, _, _) if localMBarrier(modifiers) =>
        (scope.address(address), scope.value(count)).mapN(VMIR.Instruction.MBarrierInit(_, _))
      case Instruction.MbarrierInval(address, _, _) if localMBarrier(modifiers) =>
        scope.address(address).map(VMIR.Instruction.MBarrierInvalidate(_))
      case Instruction.MbarrierArrive(destination, address, count, _, _) if localMBarrier(modifiers) =>
        val expected = has(Modifier.ExpectTx)
        for {
          sink    <- scope.sink(destination)
          barrier <- scope.address(address)
          amount  <- count.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(1L)))
        } yield VMIR.Instruction.MBarrierArrive(
          sink,
          barrier,
          if (expected) Value.Immediate(1L) else amount,
          if (expected) amount else Value.Immediate(0L)
        )
      case Instruction.MbarrierTestWait(destination, None, address, phase, _, _) if localMBarrier(modifiers) =>
        (scope.predicate(destination), scope.address(address), scope.value(phase))
          .mapN(VMIR.Instruction.MBarrierTest(_, _, _, has(Modifier.Parity)))
      case Instruction.MbarrierTryWait(destination, None, address, phase, _, _, _) if localMBarrier(modifiers) =>
        (scope.predicate(destination), scope.address(address), scope.value(phase))
          .mapN(VMIR.Instruction.MBarrierTest(_, _, _, has(Modifier.Parity)))
      case Instruction.CpAsyncBulkMBarrier(destination, source, size, barrier, None, None, None, None, _, _)
          if are(
            Modifier.Async,
            Modifier.Bulk,
            Modifier.SharedCta,
            Modifier.Global,
            Modifier.MbarrierCompleteTxBytes
          ) =>
        (scope.address(destination), scope.address(source), scope.value(size), scope.address(barrier))
          .mapN(VMIR.Instruction.AsyncBulkCopy(_, _, _, _))
      case Instruction.CpAsyncBulkGroup(destination, source, size, None, byteMask, _, _)
          if instruction.mods == Vec(Modifier.Global, Modifier.SharedCta, Modifier.BulkGroup) ||
            instruction.mods == Vec(Modifier.Global, Modifier.SharedCta, Modifier.BulkGroup, Modifier.CpMask) =>
        for {
          mask <-
            if (has(Modifier.CpMask))
              byteMask.toRight(Error.Invalid("masked bulk copy requires a byte mask")).flatMap(scope.value(_))
            else if (byteMask.isEmpty) Right(Value.Immediate(0xffffL))
            else invalid("unexpected bulk copy operand")
          lowered <- (scope.address(destination), scope.address(source), scope.value(size))
            .mapN(VMIR.Instruction.AsyncBulkStore(_, _, _, mask))
        } yield lowered
      case Instruction.CpAsyncBulkCommitGroup(_, _) if instruction.mods.isEmpty => Right(VMIR.Instruction.NoOp)
      case Instruction.CpAsyncBulkWaitGroup(count, _, _) if instruction.mods == Vec(Modifier.Read) =>
        NumericLiteral.immediate(count).filter(value => value >= 0 && value <= 7).map(_ => VMIR.Instruction.NoOp)
          .toRight(Error.Invalid("bulk copy pending group count must be between zero and seven"))
      case Instruction.CpAsync(destination, source, copySize, sourceSize, _, _, _) =>
        for {
          size <- NumericLiteral.immediate(copySize).filter(Set(4L, 8L, 16L)).map(_.toInt.unsafe[Int > 0])
            .toRight(Error.Invalid(s"cp.async copy size must be 4, 8, or 16 bytes, found $copySize"))
          bytes   <- sourceSize.traverse(scope.value(_)).map(_.getOrElse(Value.Immediate(size.raw.toLong)))
          lowered <-
            (scope.address(destination), scope.address(source)).mapN(VMIR.Instruction.AsyncCopy(_, _, size, bytes))
        } yield lowered
      case _: Instruction.CpAsyncCommitGroup | _: Instruction.CpAsyncWaitAll | _: Instruction.CpAsyncWaitGroup =>
        Right(VMIR.Instruction.NoOp)
      case _ => synchronisation()
    }
    def synchronisation(): Lowered = instruction match {
      case Instruction.ReduxSync(destination, source, memberMask, _, _)
          if modifiers.match {
            case Vec(Modifier.Sync, operation, width) =>
              (Set(Modifier.Add, Modifier.Min, Modifier.Max)(operation) &&
                Set(Modifier.U32, Modifier.S32, Modifier.U64, Modifier.S64)(width)) ||
              (Set(Modifier.And, Modifier.Or, Modifier.Xor)(operation) && width == Modifier.B32)
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
      case atom: Instruction.Atom                                 => atomic(atom, modifiers)
      case Instruction.Setp(destination, left, right, None, _, _) => setPredicate(destination, left, right, modifiers)
      case Instruction.Barrier(barrier, count, None, None, _, None) if are(Modifier.Sync) =>
        (scope.value(barrier), count.traverse(scope.value(_))).mapN(VMIR.Instruction.BarrierSync(_, _))
      case Instruction.BarrierCluster(_, _)
          if Set(
            Vec(Modifier.Arrive),
            Vec(Modifier.Arrive, Modifier.Release),
            Vec(Modifier.Arrive, Modifier.Relaxed),
            Vec(Modifier.Wait),
            Vec(Modifier.Wait, Modifier.Acquire)
          )(instruction.mods.filterNot(_ == Modifier.Aligned)) =>
        Right(VMIR.Instruction.ClusterBarrier(has(Modifier.Arrive)))
      case Instruction.ClusterLaunchControlTryCancel(response, barrier, _, _)
          if instruction.mods ==
            Vec(Modifier.Async, Modifier.SharedCta, Modifier.MbarrierCompleteTxBytes, Modifier.B128) ||
            instruction.mods == Vec(Modifier.Async, Modifier.MbarrierCompleteTxBytes, Modifier.B128) =>
        (scope.address(response), scope.address(barrier)).mapN(VMIR.Instruction.ClusterTryCancel(_, _))
      case Instruction.ClusterLaunchControlQueryCancel(destination, response, _, _)
          if instruction.mods == Vec(Modifier.IsCanceled, Modifier.Pred, Modifier.B128) =>
        for {
          predicate <- scope.predicate(destination)
          parts     <- scope.values(response).flatMap(pair(_, "cluster cancellation response"))
        } yield VMIR.Instruction.ClusterCancelStatus(predicate, parts(1))
      case Instruction.ClusterLaunchControlQueryCancel(destination, response, _, _)
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
      case Instruction.Bar(destination, Some(barrier), Some(third), fourth, _, _)
          if are(Modifier.Red, Modifier.And, Modifier.Pred) || are(Modifier.Red, Modifier.Or, Modifier.Pred) =>
        (
          scope.predicate(destination),
          scope.value(barrier),
          scope.predicateSource(fourth.getOrElse(third)),
          fourth.map(_ => third).traverse(scope.value(_))
        ).mapN { case (target, id, (source, negated), count) =>
          VMIR.Instruction.BarrierReduce(
            target,
            id,
            source,
            if (has(Modifier.And)) PredicateOperation.And else PredicateOperation.Or,
            count,
            negated
          )
        }
      case Instruction.Bar(barrier, Some(count), None, None, _, None) if are(Modifier.Arrive) =>
        (scope.value(barrier), scope.value(count)).mapN(VMIR.Instruction.BarrierArrive(_, _))
      case Instruction.Bar(_, _, _, _, _, Some(_)) if are(Modifier.Sync) =>
        invalid("bar.sync must not be predicated in the supported execution profile")
      case Instruction.Bar(barrier, count, None, None, _, _) if are(Modifier.Sync) =>
        (scope.value(barrier), count.traverse(scope.value(_))).mapN(VMIR.Instruction.BarrierSync(_, _))
      // The launcher retains completion dependencies instead of scheduling dependent grids early. A full fence gives
      // the required visibility; launch_dependents may conservatively delay release until grid exit.
      case Instruction.GridDependencyControl(_, _) if are(Modifier.Wait) || are(Modifier.LaunchDependents) =>
        Right(VMIR.Instruction.Fence)
      case Instruction.Membar(_, _) if modifiers.size == 1 && hasAny(Set(Modifier.Cta, Modifier.Gl, Modifier.Sys)) =>
        Right(VMIR.Instruction.Fence)
      case Instruction.Fence(None, None, _, _) if are(Modifier.Proxy, Modifier.Async, Modifier.SharedCta) =>
        Right(VMIR.Instruction.Fence)
      // Supported thread fences execute with stronger full-fence semantics.
      case Instruction.Fence(None, None, _, _)
          if modifiers.match {
            case Vec(scope)           => (Scopes + Modifier.Cluster)(scope)
            case Vec(ordering, scope) =>
              Set(Modifier.Sc, Modifier.AcqRel, Modifier.Acquire, Modifier.Release)(ordering) &&
              (Scopes + Modifier.Cluster)(scope)
            case _ => false
          } =>
        Right(VMIR.Instruction.Fence)
      case _: Instruction.Prefetch | _: Instruction.NanoSleep => Right(VMIR.Instruction.NoOp)
      case Instruction.BarWarpSync(memberMask, _, _) => scope.value(memberMask).map(VMIR.Instruction.WarpBarrier(_))
      case Instruction.Exit(_, _)                    => Right(VMIR.Instruction.Exit)
      case Instruction.Ret(_, _)                     => Right(VMIR.Instruction.Return)
      case Instruction.Trap(_, _)                    => Right(VMIR.Instruction.Trap)
      case _                                         => calls()
    }
    def calls(): Lowered = instruction match {
      case Instruction.Call(returnParameters, function, parameters, _, _, _) =>
        for {
          returns   <- scope.callParameters(returnParameters)
          arguments <- scope.callParameters(parameters)
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

    memoryAndMoves()
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
          _     <- Either.cond(count * width.raw <= 128, (), Error.Invalid("vector move exceeds 128 bits"))
        } yield VMIR.Instruction.MoveVector(destinations, sources, width)
      case Vec(Modifier.Pred) =>
        (scope.predicate(destination), scope.predicateValue(source)).mapN(VMIR.Instruction.MovePredicate(_, _))
      case Vec(Modifier.U64 | Modifier.B64 | Modifier.B32) if Operand.isVectorLiteral(destination) =>
        val part: Int in (16, 32) = if (modifiers.head == Modifier.B32) 16 else 32
        for {
          destinations <- scope.sinks(destination).flatMap(pair(_, s"mov.b${part.raw * 2} destination"))
          value        <- scope.value(source)
        } yield VMIR.Instruction.MoveSplit(destinations, value, part)
      case Vec(Modifier.U64 | Modifier.B64 | Modifier.B32) if Operand.isVectorLiteral(source) =>
        val part: Int in (16, 32) = if (modifiers.head == Modifier.B32) 16 else 32
        for {
          register <- scope.register(destination)
          sources  <- scope.values(source).flatMap(pair(_, s"mov.b${part.raw * 2} source"))
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

  private def localMBarrier(modifiers: Vec[Modifier]): Boolean = modifiers.forall(Set(
    Modifier.Init,
    Modifier.Inval,
    Modifier.Arrive,
    Modifier.ExpectTx,
    Modifier.TestWait,
    Modifier.TryWait,
    Modifier.Parity,
    Modifier.B64,
    Modifier.Shared,
    Modifier.SharedCta,
    Modifier.Cta,
    Modifier.Acquire,
    Modifier.Release,
    Modifier.Relaxed
  ))

  private def setPredicate(destination: Operand, left: Operand, right: Operand, modifiers: Vec[Modifier])(
      using scope: Scope
  ): Lowered = {
    val rendered = ("setp" +: modifiers.map(_.name.raw)).mkString(".")
    for {
      relation <- modifiers.view.flatMap(comparison(_)).headOption
        .toRight(Error.Invalid(s"unsupported setp comparison: $rendered"))
      operandType <- modifiers.find(modifier => modifier == Modifier.Pred || format(modifier).nonEmpty)
        .toRight(Error.Invalid(s"unsupported setp type: $rendered"))
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
  }

  /** The host CAS path is sequentially consistent, which satisfies every weaker PTX atomic order and scope. */
  private def atomic(instruction: Instruction.Atom, modifiers: Vec[Modifier])(using scope: Scope): Lowered = {
    val Instruction.Atom(destination, address, value, compareOrCachePolicy, _, _) = instruction
    val stripped      = modifiers.filterNot(Orders ++ Scopes)
    val (space, rest) = stripped match {
      case Vec(Modifier.Global, rest*) => (MemorySpace.Global, rest.toVec)
      case Vec(Modifier.Shared, rest*) => (MemorySpace.Shared, rest.toVec)
      case rest                        => (MemorySpace.Generic, rest)
    }
    val words                                     = Set(Modifier.U32, Modifier.U64, Modifier.S32, Modifier.S64)
    def width(modifier: Modifier): ScalarBitWidth =
      if (Set(Modifier.B64, Modifier.S64, Modifier.U64, Modifier.F64)(modifier)) 64 else 32
    val operation: Option[(Modifier, AtomicOperation)] = rest match {
      case Vec(Modifier.Max, kind) if words(kind) =>
        Some(kind -> (if (SignedTypes(kind)) AtomicOperation.MaximumSigned else AtomicOperation.MaximumUnsigned))
      case Vec(Modifier.Min, kind) if words(kind) =>
        Some(kind -> (if (SignedTypes(kind)) AtomicOperation.MinimumSigned else AtomicOperation.MinimumUnsigned))
      case Vec(Modifier.And, kind @ (Modifier.B32 | Modifier.B64))  => Some(kind -> AtomicOperation.And)
      case Vec(Modifier.Or, kind @ (Modifier.B32 | Modifier.B64))   => Some(kind -> AtomicOperation.Or)
      case Vec(Modifier.Xor, kind @ (Modifier.B32 | Modifier.B64))  => Some(kind -> AtomicOperation.Xor)
      case Vec(Modifier.Exch, kind @ (Modifier.B32 | Modifier.B64)) => Some(kind -> AtomicOperation.Exchange)
      case Vec(Modifier.Add, kind @ (Modifier.U32 | Modifier.U64))  => Some(kind -> AtomicOperation.Add)
      case Vec(Modifier.Add, kind @ (Modifier.F32 | Modifier.F64))  => Some(kind -> AtomicOperation.FloatAdd)
      case _                                                        => None
    }
    (rest, compareOrCachePolicy, operation) match {
      case (Vec(Modifier.Cas, kind @ (Modifier.B32 | Modifier.B64)), Some(source), _) =>
        (scope.register(destination), scope.address(address), scope.value(value), scope.value(source))
          .mapN(VMIR.Instruction.AtomicCompareExchange(_, _, _, _, width(kind), space))
      case (_, None, Some((kind, operation))) =>
        (scope.register(destination), scope.address(address), scope.value(value))
          .mapN((register, target, source) =>
            VMIR.Instruction.Atomic(Some(register), target, source, width(kind), space, operation)
          )
      case _ => Left(Error.Unsupported(instruction.qualifiedOpcode, "no handwritten semantic family"))
    }
  }
}
