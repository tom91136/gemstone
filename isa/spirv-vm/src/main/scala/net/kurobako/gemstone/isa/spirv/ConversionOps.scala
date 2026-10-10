package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.constraint.auto.*
import net.kurobako.gemstone.vm.*

/** Width, signedness and encoding conversions, bit casts and value copies. */
private object ConversionOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.UConvert(t, d, a) =>
      typed(site, a) match {
        case Typed.Undefined(value) => Fail(undefined(value))
        case Typed.Opaque(_, _)     => Fail(Fault.InvalidRequest("OpUConvert cannot convert a pointer"))
        case Typed.Data(_, _)       =>
          val bits = mask(site.program.scalarWidth(id(t)))
          unary(site, id(t), id(d), id(a))(_ & bits)
      }
    case Instruction.SConvert(t, d, a) =>
      integerToInteger(site, t, a).fold(
        Fail(_),
        (source, result) => unary(site, id(t), id(d), id(a))(value => signExtend(value, source) & mask(result))
      )
    case Instruction.ConvertSToF(t, d, a) =>
      val mode = rounding(site, d, IEEE754.Rounding.NearestEven)
      integerToFloating(
        site,
        "OpConvertSToF",
        t,
        d,
        a,
        (raw, width, result) => IEEE754.integerToBinary(signExtend(raw, width), unsigned = false, result, mode)
      )
    case Instruction.ConvertUToF(t, d, a) =>
      val mode = rounding(site, d, IEEE754.Rounding.NearestEven)
      integerToFloating(
        site,
        "OpConvertUToF",
        t,
        d,
        a,
        (raw, width, result) => IEEE754.integerToBinary(raw & mask(width), true, result, mode)
      )
    case Instruction.ConvertFToS(t, d, a)   => floatingToSigned(site, t, d, a)
    case Instruction.ConvertFToU(t, d, a)   => floatingToUnsigned(site, t, d, a)
    case Instruction.FConvert(t, d, a)      => floatingToFloating(site, t, d, a)
    case Instruction.QuantizeToF16(t, d, a) =>
      typed(site, a) match {
        case Typed.Undefined(value) => Fail(undefined(value))
        case Typed.Data(_, sourceType)
            if sourceType == id(t) && site.program.floatingShape(sourceType).exists(_._1.raw == 32) =>
          unary(site, id(t), id(d), id(a))(quantizeToF16(_))
        case _ => Fail(Fault.InvalidRequest("OpQuantizeToF16 requires matching float32 scalar or vector types"))
      }
    case Instruction.CopyObject(t, d, a)   => copy(id(t), id(d), id(a))
    case Instruction.ExpectKHR(t, d, a, _) => copy(id(t), id(d), id(a))
    case Instruction.CopyLogical(t, d, a)  => copy(id(t), id(d), id(a))
    case Instruction.Bitcast(t, d, a)      => bitcast(site, id(t), id(d), id(a))
  }

  // Quantisation retains a float32 encoding but flushes values below the smallest normal half to signed zero.
  def quantizeToF16(bits: Long): Long = {
    val value = IEEE754.binary32ToFloat(Binary32.low(bits))
    val half  =
      if (Math.abs(value) < Math.scalb(1.0f, -14)) (bits >>> 16) & 0x8000L else IEEE754.floatToBinary16(value).toLong
    IEEE754.floatToBinary32(IEEE754.binary16ToFloat(Binary16.low(half))).toLong
  }

  private def copy(resultType: Int, dest: Int, source: Int): Op =
    step((execution, _) => transfer(execution.values, source, dest, resultType))

  private def source(site: Site, opcode: String, operand: Operand): Either[Fault, Int] = typed(site, operand) match {
    case Typed.Undefined(value) => Left(undefined(value))
    case Typed.Opaque(_, _)     => Left(Fault.InvalidRequest(s"$opcode source %${id(operand)} is a pointer"))
    case Typed.Data(_, typeId)  => Right(typeId)
  }

  private def lanes(opcode: String, sourceLanes: Int, resultLanes: Int): Fault =
    Fault.InvalidRequest(s"$opcode cannot convert $sourceLanes source lanes to $resultLanes result lanes")

  private def integerToInteger(site: Site, resultType: Operand, operand: Operand)
      : Either[Fault, (Int in 1 ~ 64, Int in 1 ~ 64)] =
    source(site, "OpSConvert", operand).flatMap(sourceType =>
      (site.program.integerShape(sourceType), site.program.integerShape(id(resultType))) match {
        case (Some((width, count)), Some((resultWidth, resultCount))) if count == resultCount =>
          Right((width, resultWidth))
        case (None, _) =>
          Left(Fault.InvalidRequest(s"OpSConvert source %${id(operand)} is not an integer scalar or vector"))
        case (_, None) =>
          Left(Fault.InvalidRequest(s"OpSConvert result type %${id(resultType)} is not an integer scalar or vector"))
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes("OpSConvert", count, resultCount))
      }
    )

  // Integers convert in one rounding, so 64-bit sources beyond binary64 precision do not round twice.
  private def integerToFloating(
      site: Site,
      opcode: String,
      resultType: Operand,
      dest: Operand,
      operand: Operand,
      convert: (Long, Int in 1 ~ 64, Int in (16, 32, 64)) => Long
  ): Op = source(site, opcode, operand).flatMap(sourceType =>
    (site.program.integerShape(sourceType), site.program.floatingShape(id(resultType))) match {
      case (Some((width, count)), Some((resultWidth, resultCount))) if count == resultCount =>
        Right(unary(site, id(resultType), id(dest), id(operand))(raw => convert(raw, width, resultWidth)))
      case (None, _) =>
        Left(Fault.InvalidRequest(s"$opcode source %${id(operand)} is not an integer scalar or vector"))
      case (_, None) =>
        Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a floating-point scalar or vector"))
      case (Some((_, count)), Some((_, resultCount))) => Left(lanes(opcode, count, resultCount))
    }
  ).fold(Fail(_), identity)

  private def floatingToIntegerShapes(site: Site, opcode: String, resultType: Operand, operand: Operand)
      : Either[Fault, (Int in (16, 32, 64), Int in 1 ~ 64)] =
    source(site, opcode, operand).flatMap(sourceType =>
      (site.program.floatingShape(sourceType), site.program.integerShape(id(resultType))) match {
        case (Some((width, count)), Some((resultWidth, resultCount))) if count == resultCount =>
          Right((width, resultWidth))
        case (None, _) =>
          Left(Fault.InvalidRequest(s"$opcode source %${id(operand)} is not a floating-point scalar or vector"))
        case (_, None) =>
          Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not an integer scalar or vector"))
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes(opcode, count, resultCount))
      }
    )

  /** The decorated rounding of a conversion; float-to-integer conversions truncate by default. */
  private def rounding(site: Site, dest: Operand, default: IEEE754.Rounding): IEEE754.Rounding =
    site.program.roundingModes.get(id(dest)).fold(default)(Op.rounding(_, default))

  private def integral(value: Double, mode: IEEE754.Rounding): Double = mode match {
    case IEEE754.Rounding.NearestEven => math.rint(value)
    case IEEE754.Rounding.Up          => math.ceil(value)
    case IEEE754.Rounding.Down        => math.floor(value)
    case IEEE754.Rounding.TowardZero  => if (value < 0.0) math.ceil(value) else math.floor(value)
  }

  private def floatingToSigned(site: Site, resultType: Operand, dest: Operand, operand: Operand): Op =
    floatingToIntegerShapes(site, "OpConvertFToS", resultType, operand).fold(
      Fail(_),
      { (width, resultWidth) =>
        val mode     = rounding(site, dest, IEEE754.Rounding.TowardZero)
        val saturate = site.program.saturatedConversions.contains(id(dest)) || site.program.vulkan
        unary(site, id(resultType), id(dest), id(operand)) { raw =>
          val value = IEEE754.toDouble(raw, width)
          // Without saturation the conversion is the JVM's, which clamps to 64 bits before wrapping to the width.
          if (saturate) IEEE754.toIntegerSaturated(value, resultWidth, signed = true, mode)
          else integral(value, mode).toLong & mask(resultWidth)
        }
      }
    )

  private def floatingToUnsigned(site: Site, resultType: Operand, dest: Operand, operand: Operand): Op =
    floatingToIntegerShapes(site, "OpConvertFToU", resultType, operand).fold(
      Fail(_),
      { (width, resultWidth) =>
        val conversion = UnsignedConversion(
          width,
          resultWidth,
          rounding(site, dest, IEEE754.Rounding.TowardZero),
          site.program.saturatedConversions.contains(id(dest)),
          site.program.vulkan
        )
        if (site.program.scalarSlot(id(dest)) && site.program.scalarSlot(id(operand)))
          UnsignedScalar(id(dest), id(operand), conversion)
        else UnsignedLanes(id(dest), id(resultType), id(operand), conversion)
      }
    )

  /** Saturated and Vulkan conversions clamp an unrepresentable value; others fault on it. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private class UnsignedConversion(
      width: Int in (16, 32, 64),
      resultWidth: Int in 1 ~ 64,
      mode: IEEE754.Rounding,
      saturate: Boolean,
      vulkan: Boolean
  ) {
    def integralValue(raw: Long): Double = integral(IEEE754.toDouble(raw, width), mode)

    def faults(value: Double): Boolean = !saturate && !vulkan && truncationProblem(value, resultWidth) != null

    def failure(lane: Int, value: Double): Fault = Fault.InvalidRequest(
      s"OpConvertFToU source lane $lane cannot be represented as an unsigned $resultWidth-bit integer: " +
        truncationProblem(value, resultWidth)
    )

    def bits(value: Double): Long =
      if (saturate && (value.isNaN || value <= 0.0)) 0L
      else if (saturate && value >= Math.scalb(1.0, resultWidth.raw)) mask(resultWidth)
      else if (truncationProblem(value, resultWidth) == null) truncated(value, resultWidth)
      else if (value.isNaN || value <= 0.0) 0L
      else mask(resultWidth)
  }

  private class UnsignedScalar(dest: Int, a: Int, conversion: UnsignedConversion) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val registers = execution.values.scalars
      val value     = conversion.integralValue(registers(a))
      if (conversion.faults(value)) Left(conversion.failure(0, value))
      else {
        registers(dest) = conversion.bits(value)
        execution.pc += 1
        Done
      }
    }
  }

  private class UnsignedLanes(dest: Int, resultType: Int, a: Int, conversion: UnsignedConversion) extends Op {
    @SuppressWarnings(Array("scalafix:DisableSyntax.null", "scalafix:DisableSyntax.var"))
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = {
      val values = execution.values
      val source = values.lanes(a)
      if (source == null) Left(undefined(a))
      else {
        val result                = Array.ofDim[Long](source.length)
        var failure: Fault | Null = null
        var lane                  = 0
        while (lane < result.length && failure == null) {
          val value = conversion.integralValue(source(lane))
          if (conversion.faults(value)) failure = conversion.failure(lane, value)
          else result(lane) = conversion.bits(value)
          lane += 1
        }
        if (failure != null) Left(failure)
        else {
          values.write(dest, resultType, result)
          execution.pc += 1
          Done
        }
      }
    }
  }

  /** Why an integral binary64 value has no unsigned `width`-bit representation, or null when it has one. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.null"))
  private def truncationProblem(value: Double, width: Int in 1 ~ 64): String | Null = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    if (exponent == 0x7ff) "NaN and infinity are not integers"
    else if (exponent < 1023) null
    else if (bits < 0L) "negative values are outside the unsigned range"
    else if (exponent - 1023 >= width.raw) "value exceeds the result width"
    else null
  }

  /** Truncates a representable integral binary64 value into an unsigned width without narrowing through a Long. */
  private def truncated(value: Double, width: Int in 1 ~ 64): Long = {
    val bits     = IEEE754.doubleToBinary64(value)
    val exponent = ((bits >>> 52) & 0x7ffL).toInt
    if (exponent < 1023) 0L
    else {
      val shift       = exponent - 1023
      val significand = (1L << 52) | (bits & 0xfffffffffffffL)
      (if (shift >= 52) significand << (shift - 52) else significand >>> (52 - shift)) & mask(width)
    }
  }

  private def floatingToFloating(site: Site, resultType: Operand, dest: Operand, operand: Operand): Op =
    source(site, "OpFConvert", operand).flatMap(sourceType =>
      (site.program.floatingShape(sourceType), site.program.floatingShape(id(resultType))) match {
        case (Some((width, count)), Some((resultWidth, resultCount))) if count == resultCount =>
          val mode = if (resultWidth.raw >= width.raw) IEEE754.Rounding.NearestEven
          else rounding(site, dest, IEEE754.Rounding.NearestEven)
          Right(unary(site, id(resultType), id(dest), id(operand))(raw =>
            IEEE754.fromDouble(IEEE754.toDouble(raw, width), resultWidth, mode)
          ))
        case (None, _) =>
          Left(Fault.InvalidRequest(s"OpFConvert source %${id(operand)} is not a floating-point scalar or vector"))
        case (_, None) =>
          Left(
            Fault.InvalidRequest(s"OpFConvert result type %${id(resultType)} is not a floating-point scalar or vector")
          )
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes("OpFConvert", count, resultCount))
      }
    ).fold(Fail(_), identity)

  private def bitShape(program: VMProgram, typeId: Int): Option[(Int in 1 ~ 64, Int)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Pointer(_, _)) => Some((64, 1))
      case _                               => program.numericShape(typeId)
    }

  /** Reinterprets the bits of `lanes` from the source lane shape into the result lane shape. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def repack(
      program: VMProgram,
      sourceType: Int,
      resultType: Int,
      lanes: Array[Long]
  ): Either[Fault, Array[Long]] =
    (bitShape(program, sourceType), bitShape(program, resultType)) match {
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount)))
          if lanes.length == sourceCount && sourceWidth.raw * sourceCount == resultWidth.raw * resultCount =>
        val result = Array.ofDim[Long](resultCount)
        var lane   = 0
        while (lane < resultCount) {
          var bits = 0L
          var bit  = 0
          while (bit < resultWidth.raw) {
            val sourceBit = lane * resultWidth.raw + bit
            if (((lanes(sourceBit / sourceWidth.raw) >>> (sourceBit % sourceWidth.raw)) & 1L) != 0L) bits |= 1L << bit
            bit += 1
          }
          result(lane) = bits & mask(resultWidth)
          lane += 1
        }
        Right(result)
      case (Some((sourceWidth, sourceCount)), Some((resultWidth, resultCount))) =>
        Left(Fault.InvalidRequest(
          s"OpBitcast source %$sourceType has $sourceCount x $sourceWidth bits and result %$resultType " +
            s"has $resultCount x $resultWidth bits (value has ${lanes.length} lanes)"
        ))
      case (None, _) =>
        Left(Fault.InvalidRequest(s"OpBitcast source type %$sourceType is not scalar/vector numeric data"))
      case (_, None) =>
        Left(Fault.InvalidRequest(s"OpBitcast result type %$resultType is not scalar/vector numeric data"))
    }

  private def bitcast(site: Site, resultType: Int, dest: Int, source: Int): Op = {
    val program = site.program
    val numeric =
      (bitShape(program, program.valueType(source)), program.runtimeType(resultType), program.numericShape(resultType))
    numeric match {
      // Same-width scalar reinterpretation only retypes the register.
      case (Some((width, 1)), _, Some((resultWidth, 1)))
          if width == resultWidth && program.scalarSlot(source) && program.scalarSlot(dest) =>
        val bits = mask(resultWidth)
        Scalar1(dest, source, _ & bits)
      case _ =>
        step { (execution, _) =>
          val values = execution.values
          val result = values.get(source) match {
            case Some(VMValue.Address(sourceType, pointer)) =>
              program.runtimeType(resultType) match {
                case Some(RuntimeType.Pointer(storageClass, pointee)) =>
                  program.runtimeType(sourceType) match {
                    case Some(RuntimeType.Pointer(sourceStorage, _)) if sourceStorage == storageClass =>
                      Right(VMValue.Address(resultType, pointer.copy(pointee = pointee)))
                    case _ => Left(Fault.InvalidRequest("OpBitcast pointer types must have the same storage class"))
                  }
                case _ if program.integerShape(resultType).nonEmpty =>
                  repack(program, sourceType, resultType, Array(pointer.address)).map(VMValue.Bits(resultType, _))
                case _ => Left(Fault.InvalidRequest("OpBitcast pointer requires an integer or pointer result"))
              }
            case Some(VMValue.Bits(sourceType, lanes)) =>
              program.runtimeType(resultType) match {
                case Some(RuntimeType.Pointer(storageClass, pointee)) if program.integerShape(sourceType).nonEmpty =>
                  repack(program, sourceType, resultType, lanes).flatMap { bits =>
                    program.runtimeType(pointee) match {
                      case Some(RuntimeType.Function(_, _)) => Right(VMValue.Bits(resultType, bits))
                      case _ => Right(VMValue.Address(resultType, Pointer(bits(0), pointee, storageClass)))
                    }
                  }
                case Some(RuntimeType.Pointer(_, _)) =>
                  Left(Fault.InvalidRequest("OpBitcast pointer requires an integer or pointer operand"))
                case _ => repack(program, sourceType, resultType, lanes).map(VMValue.Bits(resultType, _))
              }
            case None => Left(undefined(source))
          }
          result.map(values.update(dest, _))
        }
    }
  }
}
