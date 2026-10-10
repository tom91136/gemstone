package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import VMLowering.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Integer and floating-point arithmetic, bitwise operations, shifts, bit fields and the linear algebra opcodes. Each
  * family pairs a shape rule, checked once at load, with a lane kernel.
  */
private object ArithmeticOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.IAdd(t, d, a, b)             => wrapping(site, t, d, a, b, SPIRV.Opcode.IAdd)
    case Instruction.ISub(t, d, a, b)             => wrapping(site, t, d, a, b, SPIRV.Opcode.ISub)
    case Instruction.IMul(t, d, a, b)             => wrapping(site, t, d, a, b, SPIRV.Opcode.IMul)
    case Instruction.UDiv(t, d, a, b)             => division(site, "OpUDiv", t, d, a, b, SPIRV.Opcode.UDiv)
    case Instruction.UMod(t, d, a, b)             => division(site, "OpUMod", t, d, a, b, SPIRV.Opcode.UMod)
    case Instruction.SDiv(t, d, a, b)             => division(site, "OpSDiv", t, d, a, b, SPIRV.Opcode.SDiv)
    case Instruction.SRem(t, d, a, b)             => division(site, "OpSRem", t, d, a, b, SPIRV.Opcode.SRem)
    case Instruction.SMod(t, d, a, b)             => division(site, "OpSMod", t, d, a, b, SPIRV.Opcode.SMod)
    case Instruction.SNegate(t, d, a)             => negate(site, t, d, a)
    case Instruction.ShiftLeftLogical(t, d, a, b) =>
      shift(site, "OpShiftLeftLogical", t, d, a, b, SPIRV.Opcode.ShiftLeftLogical)
    case Instruction.ShiftRightLogical(t, d, a, b) =>
      shift(site, "OpShiftRightLogical", t, d, a, b, SPIRV.Opcode.ShiftRightLogical)
    case Instruction.ShiftRightArithmetic(t, d, a, b) =>
      shift(site, "OpShiftRightArithmetic", t, d, a, b, SPIRV.Opcode.ShiftRightArithmetic)
    case Instruction.BitwiseOr(t, d, a, b) =>
      bitwise(site, "OpBitwiseOr", t, d, a, b)(integerKernels(SPIRV.Opcode.BitwiseOr))
    case Instruction.BitwiseXor(t, d, a, b) =>
      bitwise(site, "OpBitwiseXor", t, d, a, b)(integerKernels(SPIRV.Opcode.BitwiseXor))
    case Instruction.BitwiseAnd(t, d, a, b) =>
      bitwise(site, "OpBitwiseAnd", t, d, a, b)(integerKernels(SPIRV.Opcode.BitwiseAnd))
    case Instruction.Not(t, d, a)                                => not(site, t, d, a)
    case Instruction.BitCount(t, d, a)                           => bitCount(site, t, d, a)
    case Instruction.BitFieldUExtract(t, d, base, offset, count) =>
      bitField(
        site,
        resultType = t,
        dest = d,
        base,
        insert = None,
        offset = Some(offset),
        count = Some(count),
        BitField.UnsignedExtract
      )
    case Instruction.BitFieldSExtract(t, d, base, offset, count) =>
      bitField(
        site,
        resultType = t,
        dest = d,
        base,
        insert = None,
        offset = Some(offset),
        count = Some(count),
        BitField.SignedExtract
      )
    case Instruction.BitFieldInsert(t, d, base, insert, offset, count) =>
      bitField(
        site,
        resultType = t,
        dest = d,
        base,
        insert = Some(insert),
        offset = Some(offset),
        count = Some(count),
        BitField.Insert
      )
    case Instruction.BitReverse(t, d, base) =>
      bitField(site, resultType = t, dest = d, base, insert = None, offset = None, count = None, BitField.Reverse)
    case Instruction.IAddCarry(t, d, a, b)         => extended(site, t, d, a, b, Extended.AddCarry)
    case Instruction.ISubBorrow(t, d, a, b)        => extended(site, t, d, a, b, Extended.SubtractBorrow)
    case Instruction.UMulExtended(t, d, a, b)      => extended(site, t, d, a, b, Extended.UnsignedMultiply)
    case Instruction.SMulExtended(t, d, a, b)      => extended(site, t, d, a, b, Extended.SignedMultiply)
    case Instruction.FAdd(t, d, a, b)              => floating(site, FloatingOperation.Add, t, d, a, b)
    case Instruction.FSub(t, d, a, b)              => floating(site, FloatingOperation.Subtract, t, d, a, b)
    case Instruction.FMul(t, d, a, b)              => floating(site, FloatingOperation.Multiply, t, d, a, b)
    case Instruction.FDiv(t, d, a, b)              => floating(site, FloatingOperation.Divide, t, d, a, b)
    case Instruction.FRem(t, d, a, b)              => floating(site, FloatingOperation.Remainder, t, d, a, b)
    case Instruction.FMod(t, d, a, b)              => floating(site, FloatingOperation.Modulo, t, d, a, b)
    case Instruction.FNegate(t, d, a)              => floatingNegate(site, t, d, a)
    case Instruction.VectorTimesScalar(t, d, a, b) => vectorTimesScalar(site, t, d, a, b)
    case Instruction.Dot(t, d, a, b)               => dot(site, t, d, a, b)
    case Instruction.Transpose(t, d, a)            => matrix(site, t, d, MatrixOperation.Transpose, Vec(a))
    case Instruction.MatrixTimesScalar(t, d, a, b) => matrix(site, t, d, MatrixOperation.Scale, Vec(a, b))
    case Instruction.VectorTimesMatrix(t, d, a, b) => matrix(site, t, d, MatrixOperation.VectorMatrix, Vec(a, b))
    case Instruction.MatrixTimesVector(t, d, a, b) => matrix(site, t, d, MatrixOperation.MatrixVector, Vec(a, b))
    case Instruction.MatrixTimesMatrix(t, d, a, b) => matrix(site, t, d, MatrixOperation.Multiply, Vec(a, b))
    case Instruction.OuterProduct(t, d, a, b)      => matrix(site, t, d, MatrixOperation.OuterProduct, Vec(a, b))
  }

  /** Data operands, or the fault the first absent or opaque one raises. */
  private def data(site: Site, operands: Vec[Operand], opaque: Typed.Opaque => Fault): Either[Fault, Vec[Typed.Data]] =
    operands.map(typed(site, _)).foldLeft[Either[Fault, Vec[Typed.Data]]](Right(Vec.empty)) {
      case (Right(found), value: Typed.Data)  => Right(found :+ value)
      case (Right(_), value: Typed.Opaque)    => Left(opaque(value))
      case (Right(_), value: Typed.Undefined) => Left(undefined(value.id))
      case (failure, _)                       => failure
    }

  /** Integer lane kernels by opcode and width, shared with `OpSpecConstantOp` folding. Unary kernels ignore their
    * second operand; shift amounts arrive masked to their own width. Division by zero, which OpenCL leaves unspecified
    * without trapping, yields zero.
    */
  val integerKernels: Map[Int, (Int in 1 ~ 64) => Kernel2] = {
    def masked(operation: (Long, Long) => Long)(width: Int in 1 ~ 64): Kernel2 = {
      val bits = mask(width)
      (a, b) => operation(a, b) & bits
    }
    def unsigned(operation: (Long, Long) => Long)(width: Int in 1 ~ 64): Kernel2 = {
      val bits = mask(width)
      (a, b) => {
        val divisor = b & bits
        if (divisor == 0L) 0L else operation(a & bits, divisor) & bits
      }
    }
    def signed(operation: (Long, Long) => Long)(width: Int in 1 ~ 64): Kernel2 = {
      val bits = mask(width)
      (a, b) => {
        val divisor = signExtend(b, width)
        if (divisor == 0L) 0L else operation(signExtend(a, width), divisor) & bits
      }
    }
    def shift(operation: (Long, Int, Int in 1 ~ 64) => Long)(width: Int in 1 ~ 64): Kernel2 = {
      val bits = mask(width)
      (value, amount) =>
        if (java.lang.Long.compareUnsigned(amount, width.raw.toLong) >= 0) operation(value, -1, width) & bits
        else operation(value, amount.toInt, width) & bits
    }
    def modulo(dividend: Long, divisor: Long): Long = {
      val remainder = dividend % divisor
      if (remainder != 0 && (remainder < 0) != (divisor < 0)) remainder + divisor else remainder
    }
    Map(
      SPIRV.Opcode.IAdd              -> masked(_ + _),
      SPIRV.Opcode.ISub              -> masked(_ - _),
      SPIRV.Opcode.IMul              -> masked(_ * _),
      SPIRV.Opcode.SNegate           -> masked((a, _) => -a),
      SPIRV.Opcode.Not               -> masked((a, _) => ~a),
      SPIRV.Opcode.BitwiseOr         -> masked(_ | _),
      SPIRV.Opcode.BitwiseXor        -> masked(_ ^ _),
      SPIRV.Opcode.BitwiseAnd        -> masked(_ & _),
      SPIRV.Opcode.UDiv              -> unsigned(java.lang.Long.divideUnsigned(_, _)),
      SPIRV.Opcode.UMod              -> unsigned(java.lang.Long.remainderUnsigned(_, _)),
      SPIRV.Opcode.SDiv              -> signed(_ / _),
      SPIRV.Opcode.SRem              -> signed(_ % _),
      SPIRV.Opcode.SMod              -> signed(modulo(_, _)),
      SPIRV.Opcode.ShiftLeftLogical  -> shift((value, amount, _) => if (amount < 0) 0L else value << amount),
      SPIRV.Opcode.ShiftRightLogical ->
        shift((value, amount, width) => if (amount < 0) 0L else (value & mask(width)) >>> amount),
      SPIRV.Opcode.ShiftRightArithmetic -> shift { (value, amount, width) =>
        val signed = signExtend(value, width)
        if (amount < 0) { if (signed < 0L) -1L else 0L }
        else signed >> amount
      }
    )
  }

  // Wrapping addition, subtraction and multiplication take their width from the result and broadcast operands.
  private def wrapping(site: Site, resultType: Operand, dest: Operand, left: Operand, right: Operand, opcode: Int): Op =
    lowered(
      data(site, Vec(left, right), value => Fault.InvalidRequest(s"SPIR-V value %${value.id} is not numeric")).map { _ =>
        binary(
          site,
          id(resultType),
          id(dest),
          id(left),
          id(right)
        )(integerKernels(opcode)(site.program.scalarWidth(id(resultType))))
      }
    )

  private def sameIntegerShapes(site: Site, opcode: String, resultType: Operand, left: Operand, right: Operand)
      : Either[Fault, (Int in 1 ~ 64, Int)] =
    data(
      site,
      Vec(left, right),
      value => Fault.InvalidRequest(s"$opcode ${side(value.id, left)} operand %${value.id} is a pointer")
    )
      .flatMap { operands =>
        (
          site.program.integerShape(id(resultType)),
          site.program.integerShape(operands(0).typeId),
          site.program.integerShape(operands(1).typeId)
        ) match {
          case (Some(result), Some(leftShape), Some(rightShape)) if result == leftShape && result == rightShape =>
            Right(result)
          case (None, _, _) =>
            Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not an integer scalar or vector"))
          case (_, None, _) =>
            Left(Fault.InvalidRequest(s"$opcode left operand %${id(left)} is not an integer scalar or vector"))
          case (_, _, None) =>
            Left(Fault.InvalidRequest(s"$opcode right operand %${id(right)} is not an integer scalar or vector"))
          case (Some(result), Some(leftShape), Some(rightShape)) =>
            Left(
              Fault.InvalidRequest(
                s"$opcode type shapes do not match: result $result, left $leftShape, right $rightShape"
              )
            )
        }
      }

  private def side(value: Int, left: Operand): String = if (value == id(left)) "left" else "right"

  private def division(
      site: Site,
      name: String,
      resultType: Operand,
      dest: Operand,
      left: Operand,
      right: Operand,
      opcode: Int
  ): Op =
    lowered(sameIntegerShapes(site, name, resultType, left, right).map { case (width, _) =>
      binary(site, id(resultType), id(dest), id(left), id(right))(integerKernels(opcode)(width))
    })

  private def negate(site: Site, resultType: Operand, dest: Operand, source: Operand): Op = lowered(
    data(site, Vec(source), _ => Fault.InvalidRequest("OpSNegate source is a pointer")).flatMap { operands =>
      (site.program.integerShape(operands(0).typeId), site.program.integerShape(id(resultType))) match {
        case (Some(shape @ (width, _)), Some(result)) if shape == result =>
          val kernel = integerKernels(SPIRV.Opcode.SNegate)(width)
          Right(unary(site, id(resultType), id(dest), id(source))(kernel(_, 0L)))
        case _ =>
          Left(Fault.InvalidRequest("OpSNegate requires matching integer scalar/vector operand and result types"))
      }
    }
  )

  private def shift(
      site: Site,
      name: String,
      resultType: Operand,
      dest: Operand,
      base: Operand,
      shift: Operand,
      opcode: Int
  )
      : Op = lowered(
    data(
      site,
      Vec(base, shift),
      _ => Fault.InvalidRequest(s"$name requires integer scalar or vector operands")
    ).flatMap {
      operands =>
        (
          site.program.integerShape(id(resultType)),
          site.program.integerShape(operands(0).typeId),
          site.program.integerShape(operands(1).typeId)
        ) match {
          case (Some(result @ (width, count)), Some(baseShape), Some((shiftWidth, shiftCount)))
              if baseShape == result && shiftCount == count =>
            val kernel    = integerKernels(opcode)(width)
            val shiftBits = mask(shiftWidth)
            Right(binary(site, id(resultType), id(dest), id(base), id(shift))((value, amount) =>
              kernel(value, amount & shiftBits)
            ))
          case _ =>
            Left(Fault.InvalidRequest(
              s"$name requires an integer scalar/vector result and base of the same type, " +
                "plus an integer shift operand with the same lane count"
            ))
        }
    }
  )

  /** Integer operands whose type is the result type or has its integer shape. */
  def bitwise(site: Site, opcode: String, resultType: Operand, dest: Operand, left: Operand, right: Operand)(
      operation: (Int in 1 ~ 64) => Kernel2
  ): Op = site.program.integerShape(id(resultType)) match {
    case None =>
      Fail(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not an integer scalar or vector"))
    case Some(shape @ (width, _)) =>
      def matches(typeId: Int): Boolean = typeId == id(resultType) || site.program.integerShape(typeId).contains(shape)
      (typed(site, left), typed(site, right)) match {
        case (Typed.Undefined(value), _) => Fail(undefined(value))
        case (_, Typed.Undefined(value)) => Fail(undefined(value))
        case (Typed.Data(_, leftType), Typed.Data(_, rightType)) if matches(leftType) && matches(rightType) =>
          val bits   = mask(width)
          val kernel = operation(width)
          binary(site, id(resultType), id(dest), id(left), id(right))((a, b) => kernel(a, b) & bits)
        case _ =>
          Fail(Fault.InvalidRequest(s"$opcode requires matching integer scalar or vector operand and result types"))
      }
  }

  private def not(site: Site, resultType: Operand, dest: Operand, source: Operand): Op =
    site.program.integerShape(id(resultType)) match {
      case None =>
        Fail(Fault.InvalidRequest(s"OpNot result type %${id(resultType)} is not an integer scalar or vector"))
      case Some(shape @ (width, _)) =>
        typed(site, source) match {
          case Typed.Undefined(value) => Fail(undefined(value))
          case Typed.Data(_, sourceType)
              if sourceType == id(resultType) || site.program.integerShape(sourceType).contains(shape) =>
            val kernel = integerKernels(SPIRV.Opcode.Not)(width)
            unary(site, id(resultType), id(dest), id(source))(kernel(_, 0L))
          case _ =>
            Fail(Fault.InvalidRequest("OpNot requires matching integer scalar or vector operand and result types"))
        }
    }

  private def bitCount(site: Site, resultType: Operand, dest: Operand, base: Operand): Op = lowered(
    data(site, Vec(base), _ => Fault.InvalidRequest("OpBitCount base is not an integer value")).flatMap { operands =>
      (site.program.integerShape(id(resultType)), site.program.integerShape(operands(0).typeId)) match {
        case (Some((resultWidth, count)), Some((baseWidth, baseCount))) if count == baseCount =>
          val bits = mask(resultWidth)
          Right(unary(site, id(resultType), id(dest), id(base))(value =>
            Bits.bitCount(value, baseWidth).raw.toLong & bits
          ))
        case _ =>
          Left(Fault.InvalidRequest("OpBitCount requires integer operands and results with equal component counts"))
      }
    }
  )

  private enum BitField {
    case UnsignedExtract, SignedExtract, Insert, Reverse
  }

  private def bitField(
      site: Site,
      resultType: Operand,
      dest: Operand,
      base: Operand,
      insert: Option[Operand],
      offset: Option[Operand],
      count: Option[Operand],
      operation: BitField
  ): Op = site.program.integerShape(id(resultType)) match {
    case None                     => Fail(Fault.InvalidRequest("bit-field result requires integer components"))
    case Some(shape @ (width, _)) =>
      def matching(operand: Operand, message: NonEmpty[String]): Either[Fault, Int] = typed(site, operand) match {
        case Typed.Data(key, typeId) if site.program.integerShape(typeId).contains(shape) => Right(key)
        case _ => Left(Fault.InvalidRequest(message))
      }
      lowered(for {
        source <- matching(base, "bit-field base shape does not match result")
        added  <- insert.fold[Either[Fault, Int]](Right(-1))(matching(
          _,
          "bit-field inserted value shape does not match result"
        ))
      } yield {
        val bits   = mask(width)
        val vulkan = site.program.vulkan
        step { (execution, _) =>
          val values = execution.values
          for {
            off <- offset.fold[Either[Fault, Long]](Right(0L))(operand => scalar(values, id(operand)))
            len <- count.fold[Either[Fault, Long]](Right(0L))(operand => scalar(values, id(operand)))
            validRange = off >= 0 && len >= 0 && off <= width.raw && len <= width.raw - off
            _ <- Either.cond(validRange || vulkan, (), Fault.InvalidRequest("bit-field range exceeds component width"))
            words    <- Option(values.lanes(source)).toRight(undefined(source))
            inserted <-
              if (added < 0) Right(Array.emptyLongArray) else Option(values.lanes(added)).toRight(undefined(added))
          } yield {
            val fieldMask = if (!validRange) 0L else if (len == 64) -1L else (1L << len.toInt) - 1L
            // Invalid shader ranges have undefined results; keep them local to the result instead of losing the device.
            val result = Array.tabulate(words.length) { lane =>
              val word  = words(lane) & bits
              val value =
                if (!validRange) { if (operation == BitField.Insert) word else 0L }
                else if (operation == BitField.Reverse) java.lang.Long.reverse(word) >>> (64 - width.raw)
                else if (len == 0) { if (operation == BitField.Insert) word else 0L }
                else if (operation == BitField.Insert)
                  (word & ~(fieldMask << off.toInt)) | ((inserted(lane) & fieldMask) << off.toInt)
                else {
                  val extracted = (word >>> off.toInt) & fieldMask
                  if (operation == BitField.SignedExtract) (extracted << (64 - len.toInt)) >> (64 - len.toInt)
                  else extracted
                }
              value & bits
            }
            values.write(id(dest), id(resultType), result)
          }
        }
      })
  }

  private enum Extended {
    case AddCarry, SubtractBorrow, UnsignedMultiply, SignedMultiply
  }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def extended(
      site: Site,
      resultType: Operand,
      dest: Operand,
      left: Operand,
      right: Operand,
      operation: Extended
  ): Op = {
    val member = site.program.runtimeType(id(resultType)) match {
      case Some(RuntimeType.Structure(members)) if members.size == 2 && members(0) == members(1) => Some(members(0))
      case _                                                                                     => None
    }
    val shape = member.flatMap(typeId =>
      site.program.runtimeType(typeId) match {
        case Some(RuntimeType.Integer(width, signed))       => Some((width.widen[Int in 1 ~ 64], 1, signed))
        case Some(RuntimeType.VectorType(component, count)) => site.program.runtimeType(component).collect {
            case RuntimeType.Integer(width, signed) => (width.widen[Int in 1 ~ 64], count.raw, signed)
          }
        case _ => None
      }
    )
    (typed(site, left), typed(site, right), shape) match {
      case (Typed.Undefined(value), _, _) => Fail(undefined(value))
      case (_, Typed.Undefined(value), _) => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType), Some((width, count, signed)))
          if member.contains(leftType) && leftType == rightType &&
            (operation == Extended.SignedMultiply || !signed) =>
        val wordMask = mask(width)
        step { (execution, _) =>
          val values = execution.values
          (Option(values.lanes(id(left))), Option(values.lanes(id(right)))) match {
            case (Some(a), Some(b)) if a.length == count && b.length == count =>
              val result = Array.ofDim[Long](count * 2)
              var lane   = 0
              while (lane < count) {
                val x   = a(lane) & wordMask
                val y   = b(lane) & wordMask
                val low =
                  (operation match {
                    case Extended.AddCarry       => x + y
                    case Extended.SubtractBorrow => x - y
                    case _                       => x * y
                  }) & wordMask
                val high = operation match {
                  case Extended.AddCarry       => if (java.lang.Long.compareUnsigned(low, x) < 0) 1L else 0L
                  case Extended.SubtractBorrow => if (java.lang.Long.compareUnsigned(x, y) < 0) 1L else 0L
                  case Extended.SignedMultiply if width.raw < 64 =>
                    (signExtend(x, width) * signExtend(y, width)) >> width.raw
                  case Extended.SignedMultiply => Math.multiplyHigh(x, y)
                  case _ if width.raw < 64     => (x * y) >>> width.raw
                  case _                       => Math.unsignedMultiplyHigh(x, y)
                }
                result(lane) = low
                result(count + lane) = high & wordMask
                lane += 1
              }
              Right(values.update(id(dest), VMValue.Bits(id(resultType), result)))
            case _ =>
              Left(Fault.InvalidRequest(
                "extended integer arithmetic requires a two-member integer structure and matching operands"
              ))
          }
        }
      case _ =>
        Fail(
          Fault.InvalidRequest(
            "extended integer arithmetic requires a two-member integer structure and matching operands"
          )
        )
    }
  }

  enum FloatingOperation(val opcode: String) {
    case Add       extends FloatingOperation("OpFAdd")
    case Subtract  extends FloatingOperation("OpFSub")
    case Multiply  extends FloatingOperation("OpFMul")
    case Divide    extends FloatingOperation("OpFDiv")
    case Remainder extends FloatingOperation("OpFRem")
    case Modulo    extends FloatingOperation("OpFMod")
    case Minimum   extends FloatingOperation("OpenCL.std fmin")
    case Maximum   extends FloatingOperation("OpenCL.std fmax")
  }

  // binary16 operands are exact in binary32, and binary32 holds 2p + 2 bits of a binary16 result, so the basic
  // operations round once into binary16; remainders are exact.
  private def floatingKernel(operation: FloatingOperation, width: Int in (16, 32, 64)): Kernel2 = {
    def evaluate(a: Double, b: Double): Double = operation match {
      case FloatingOperation.Add       => a + b
      case FloatingOperation.Subtract  => a - b
      case FloatingOperation.Multiply  => a * b
      case FloatingOperation.Divide    => a / b
      case FloatingOperation.Remainder => a % b
      case FloatingOperation.Modulo    =>
        val remainder = a % b
        if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
      case _ => Double.NaN
    }
    def evaluateFloat(a: Float, b: Float): Float = operation match {
      case FloatingOperation.Add       => a + b
      case FloatingOperation.Subtract  => a - b
      case FloatingOperation.Multiply  => a * b
      case FloatingOperation.Divide    => a / b
      case FloatingOperation.Remainder => a % b
      case FloatingOperation.Modulo    =>
        val remainder = a % b
        if (remainder != 0 && (remainder < 0) != (b < 0)) remainder + b else remainder
      case _ => Float.NaN
    }
    operation match {
      case FloatingOperation.Minimum => (a, b) => IEEE754.minNum(a, b, width)
      case FloatingOperation.Maximum => (a, b) => IEEE754.maxNum(a, b, width)
      case _                         => width.raw match {
          case 16 =>
            (a, b) =>
              IEEE754.floatToBinary16(evaluateFloat(
                IEEE754.binary16ToFloat(Binary16.low(a)),
                IEEE754.binary16ToFloat(Binary16.low(b))
              )).toLong
          case 32 =>
            (a, b) =>
              IEEE754.floatToBinary32(evaluateFloat(
                IEEE754.binary32ToFloat(Binary32.low(a)),
                IEEE754.binary32ToFloat(Binary32.low(b))
              )).toLong
          case _ =>
            (a, b) => IEEE754.doubleToBinary64(evaluate(IEEE754.binary64ToDouble(a), IEEE754.binary64ToDouble(b)))
        }
    }
  }

  def floating(
      site: Site,
      operation: FloatingOperation,
      resultType: Operand,
      dest: Operand,
      left: Operand,
      right: Operand
  ): Op =
    lowered(
      data(
        site,
        Vec(left, right),
        value =>
          Fault.InvalidRequest(s"${operation.opcode} ${side(value.id, left)} operand %${value.id} is a pointer")
      ).flatMap { operands =>
        (
          site.program.floatingShape(id(resultType)),
          site.program.floatingShape(operands(0).typeId),
          site.program.floatingShape(operands(1).typeId)
        ) match {
          case (Some(result @ (width, _)), Some(leftShape), Some(rightShape))
              if result == leftShape && result == rightShape =>
            Right(binary(site, id(resultType), id(dest), id(left), id(right))(floatingKernel(operation, width)))
          case (None, _, _) =>
            Left(Fault.InvalidRequest(
              s"${operation.opcode} result type %${id(resultType)} is not a floating-point scalar or vector"
            ))
          case (_, None, _) =>
            Left(Fault.InvalidRequest(s"${operation.opcode} left operand %${id(left)} is not floating-point"))
          case (_, _, None) =>
            Left(Fault.InvalidRequest(s"${operation.opcode} right operand %${id(right)} is not floating-point"))
          case (Some(result), Some(leftShape), Some(rightShape)) =>
            Left(Fault.InvalidRequest(
              s"${operation.opcode} type shapes do not match: result $result, left $leftShape, right $rightShape"
            ))
        }
      }
    )

  def signMask(width: Int in (16, 32, 64)): Long = 1L << (width.raw - 1)

  private def floatingNegate(site: Site, resultType: Operand, dest: Operand, source: Operand): Op = lowered(
    data(site, Vec(source), _ => Fault.InvalidRequest("OpFNegate source is a pointer")).flatMap { operands =>
      (site.program.floatingShape(operands(0).typeId), site.program.floatingShape(id(resultType))) match {
        case (Some(shape @ (width, _)), Some(result)) if shape == result =>
          val sign = signMask(width)
          Right(unary(site, id(resultType), id(dest), id(source))(_ ^ sign))
        case _ =>
          Left(
            Fault.InvalidRequest("OpFNegate requires matching floating-point scalar/vector operand and result types")
          )
      }
    }
  )

  private def vectorTimesScalar(site: Site, resultType: Operand, dest: Operand, vector: Operand, scalar: Operand): Op =
    (typed(site, vector), typed(site, scalar)) match {
      case (Typed.Undefined(value), _)                            => Fail(undefined(value))
      case (_, Typed.Undefined(value))                            => Fail(undefined(value))
      case (Typed.Data(_, vectorType), Typed.Data(_, scalarType)) =>
        (
          site.program.floatingShape(vectorType),
          site.program.floatingShape(scalarType),
          site.program.floatingShape(id(resultType))
        ) match {
          case (Some((width, count)), Some((scalarWidth, 1)), Some((resultWidth, resultCount)))
              if width == scalarWidth && width == resultWidth && count == resultCount && count > 1 =>
            Lanes2(
              id(dest),
              id(resultType),
              id(vector),
              id(scalar),
              (a, b) =>
                IEEE754.fromDouble(IEEE754.toDouble(a, width) * IEEE754.toDouble(b, width), width)
            )
          case _ => Fail(Fault.InvalidRequest("OpVectorTimesScalar requires a floating vector and matching scalar"))
        }
      case _ => Fail(Fault.InvalidRequest("OpVectorTimesScalar requires a floating vector and scalar"))
    }

  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def dot(site: Site, resultType: Operand, dest: Operand, left: Operand, right: Operand): Op =
    (typed(site, left), typed(site, right)) match {
      case (Typed.Undefined(value), _)                         => Fail(undefined(value))
      case (_, Typed.Undefined(value))                         => Fail(undefined(value))
      case (Typed.Data(_, leftType), Typed.Data(_, rightType)) =>
        (
          site.program.floatingShape(leftType),
          site.program.floatingShape(rightType),
          site.program.floatingShape(id(resultType))
        ) match {
          case (Some((width, count)), Some((rightWidth, rightCount)), Some((resultWidth, 1)))
              if width == rightWidth && width == resultWidth && count == rightCount =>
            step { (execution, _) =>
              val values = execution.values
              (Option(values.lanes(id(left))), Option(values.lanes(id(right)))) match {
                case (Some(a), Some(b)) =>
                  var accumulated = IEEE754.fromDouble(0.0, width)
                  var lane        = 0
                  while (lane < count) {
                    accumulated = IEEE754.add(accumulated, IEEE754.multiply(a(lane), b(lane), width), width)
                    lane += 1
                  }
                  Right(values.updateScalar(id(dest), id(resultType), accumulated))
                case (None, _) => Left(undefined(id(left)))
                case (_, None) => Left(undefined(id(right)))
              }
            }
          case _ => Fail(Fault.InvalidRequest("OpDot requires matching floating vectors and a scalar result"))
        }
      case _ => Fail(Fault.InvalidRequest("OpDot operands are not floating-point vectors"))
    }

  private enum MatrixOperation {
    case Transpose, Scale, VectorMatrix, MatrixVector, Multiply, OuterProduct
  }

  def matrixShape(program: VMProgram, typeId: Int): Option[(Int in (16, 32, 64), Int, Int)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.MatrixType(column, columns)) =>
        program.floatingShape(column).map { case (width, rows) => (width, rows, columns.raw) }
      case _ => None
    }

  // Every matrix is a sequence of columns; storage padding is not part of a value's lanes.
  private def matrix(
      site: Site,
      resultType: Operand,
      dest: Operand,
      operation: MatrixOperation,
      sources: Vec[Operand]
  ): Op =
    lowered(data(site, sources, _ => Fault.InvalidRequest(s"matrix $operation requires data operands")).flatMap {
      operands =>
        val program      = site.program
        val resultId     = id(resultType)
        val leftType     = operands(0).typeId
        val rightType    = if (operands.size == 2) operands(1).typeId else leftType
        val resultMatrix = matrixShape(program, resultId)
        val leftMatrix   = matrixShape(program, leftType)
        val rightMatrix  = matrixShape(program, rightType)
        val resultVector = program.floatingShape(resultId)
        val leftVector   = program.floatingShape(leftType)
        val rightVector  = program.floatingShape(rightType)
        def product(width: Int in (16, 32, 64), rows: Int, columns: Int, inner: Int)(
            a: (Int, Int) => Long,
            b: (Int, Int) => Long
        )
            : Array[Long] =
          Array.tabulate(rows * columns) { lane =>
            val row    = lane % rows
            val column = lane / rows
            (0 until inner).foldLeft(IEEE754.fromDouble(0.0, width)) { (sum, k) =>
              IEEE754.add(sum, IEEE754.multiply(a(row, k), b(k, column), width), width)
            }
          }
        // Each kernel receives the left and right lanes, validated against the static shapes.
        val kernel: Option[(Int, Int, (Array[Long], Array[Long]) => Array[Long])] = operation match {
          case MatrixOperation.Transpose => (leftMatrix, resultMatrix) match {
              case (Some((width, rows, columns)), Some((outWidth, outRows, outColumns)))
                  if width == outWidth && rows == outColumns && columns == outRows =>
                Some((
                  rows * columns,
                  rows * columns,
                  (left, _) => Array.tabulate(left.length)(lane => left((lane % outRows) * rows + lane / outRows))
                ))
              case _ => None
            }
          case MatrixOperation.Scale => (leftMatrix, rightVector) match {
              case (Some(shape @ (width, rows, columns)), Some((scalarWidth, 1)))
                  if resultMatrix.contains(shape) && width == scalarWidth =>
                Some((rows * columns, 1, (left, right) => left.map(IEEE754.multiply(_, right(0), width))))
              case _ => None
            }
          case MatrixOperation.Multiply => (leftMatrix, rightMatrix, resultMatrix) match {
              case (
                    Some((width, rows, inner)),
                    Some((rightWidth, rightRows, columns)),
                    Some((outWidth, outRows, outColumns))
                  )
                  if width == rightWidth && width == outWidth && inner == rightRows && outRows == rows &&
                    outColumns == columns =>
                Some((
                  rows * inner,
                  inner * columns,
                  (left, right) =>
                    product(width, rows, columns, inner)((r, k) => left(k * rows + r), (k, c) => right(c * inner + k))
                ))
              case _ => None
            }
          case MatrixOperation.MatrixVector => (leftMatrix, rightVector, resultVector) match {
              case (Some((width, rows, columns)), Some((rightWidth, count)), Some((outWidth, outCount)))
                  if width == rightWidth && width == outWidth && columns == count && rows == outCount =>
                Some((
                  rows * columns,
                  count,
                  (left, right) =>
                    product(width, rows, 1, columns)((r, k) => left(k * rows + r), (k, _) => right(k))
                ))
              case _ => None
            }
          case MatrixOperation.VectorMatrix => (leftVector, rightMatrix, resultVector) match {
              case (Some((width, count)), Some((rightWidth, rows, columns)), Some((outWidth, outCount)))
                  if width == rightWidth && width == outWidth && rows == count && columns == outCount =>
                Some((
                  count,
                  rows * columns,
                  (left, right) =>
                    product(width, 1, columns, rows)((_, k) => left(k), (k, c) => right(c * rows + k))
                ))
              case _ => None
            }
          case MatrixOperation.OuterProduct => (leftVector, rightVector, resultMatrix) match {
              case (Some((width, rows)), Some((rightWidth, columns)), Some((outWidth, outRows, outColumns)))
                  if width == rightWidth && width == outWidth && rows == outRows && columns == outColumns =>
                Some((
                  rows,
                  columns,
                  (left, right) =>
                    Array.tabulate(rows * columns)(lane =>
                      IEEE754.multiply(left(lane % rows), right(lane / rows), width)
                    )
                ))
              case _ => None
            }
        }
        kernel.toRight(Fault.InvalidRequest(s"matrix $operation operand/result shapes do not match")).map {
          case (leftLanes, rightLanes, evaluate) =>
            val leftId  = operands(0).id
            val rightId = if (operands.size == 2) operands(1).id else leftId
            step { (execution, _) =>
              val values = execution.values
              (Option(values.lanes(leftId)), Option(values.lanes(rightId))) match {
                case (Some(left), Some(right)) if left.length == leftLanes && right.length == rightLanes =>
                  Right(values.update(id(dest), VMValue.Bits(resultId, evaluate(left, right))))
                case (None, _) => Left(undefined(leftId))
                case (_, None) => Left(undefined(rightId))
                case _         => Left(Fault.InvalidRequest(s"matrix $operation operand/result shapes do not match"))
              }
            }
        }
    })
}
