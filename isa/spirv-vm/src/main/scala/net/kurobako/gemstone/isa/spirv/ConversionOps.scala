package net.kurobako.gemstone.isa.spirv

import LaneOps.*
import Op.*
import OpBuilder.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*

/** Width, signedness and encoding conversions, bit casts and value copies. */
private object ConversionOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case instruction @ Instruction.UConvert(_, _, a) =>
      typed(site, a) match {
        case Typed.Undefined(value) => Fail(undefined(value))
        case Typed.Opaque(_, _)     => Fail(Fault.InvalidRequest("OpUConvert cannot convert a pointer"))
        case Typed.Data(_, _)       => staged(site, instruction, CoreFamily.Conversion)
      }
    case instruction @ Instruction.SConvert(t, _, a) =>
      lowered(integerToInteger(site, "OpSConvert", t, a).map(_ => staged(site, instruction, CoreFamily.Conversion)))
    case instruction @ Instruction.SatConvertSToU(t, _, a) =>
      lowered(integerToInteger(site, "OpSatConvertSToU", t, a).map(_ =>
        staged(site, instruction, CoreFamily.Conversion)
      ))
    case instruction @ Instruction.SatConvertUToS(t, _, a) =>
      lowered(integerToInteger(site, "OpSatConvertUToS", t, a).map(_ =>
        staged(site, instruction, CoreFamily.Conversion)
      ))
    case instruction @ Instruction.ConvertSToF(t, _, a) =>
      lowered(integerToFloating(site, "OpConvertSToF", t, a).map(_ => staged(site, instruction, CoreFamily.Conversion)))
    case instruction @ Instruction.ConvertUToF(t, _, a) =>
      lowered(integerToFloating(site, "OpConvertUToF", t, a).map(_ => staged(site, instruction, CoreFamily.Conversion)))
    case instruction @ Instruction.ConvertFToS(t, _, a) =>
      lowered(floatingToIntegerShapes(site, "OpConvertFToS", t, a).map(_ =>
        staged(site, instruction, CoreFamily.Conversion)
      ))
    case instruction @ Instruction.ConvertFToU(t, _, a) =>
      lowered(floatingToIntegerShapes(site, "OpConvertFToU", t, a).map(_ =>
        staged(site, instruction, CoreFamily.Conversion)
      ))
    case instruction @ Instruction.FConvert(t, _, a) =>
      lowered(floatingToFloating(site, t, a).map(_ => staged(site, instruction, CoreFamily.Conversion)))
    case instruction @ Instruction.QuantizeToF16(t, _, a) =>
      typed(site, a) match {
        case Typed.Undefined(value) => Fail(undefined(value))
        case Typed.Data(_, sourceType)
            if sourceType == id(t) && site.program.floatingShape(sourceType).exists(_._1 == 32) =>
          staged(site, instruction, CoreFamily.Conversion)
        case _ => Fail(Fault.InvalidRequest("OpQuantizeToF16 requires matching float32 scalar or vector types"))
      }
    case instruction @ Instruction.ArithmeticFenceEXT(t, _, a) =>
      typed(site, a) match {
        case Typed.Data(_, sourceType) if sameShape(site, sourceType, id(t)) =>
          staged(site, instruction, CoreFamily.Fence)
        case Typed.Undefined(value) => Fail(undefined(value))
        case _ => Fail(Fault.InvalidRequest("OpArithmeticFenceEXT requires an operand of its result type"))
      }
    case instruction @ Instruction.FreezeKHR(t, _, a) =>
      typed(site, a) match {
        case Typed.Data(_, sourceType) if sourceType == id(t) =>
          staged(site, instruction, CoreFamily.Freeze)
        case Typed.Undefined(value) => Fail(undefined(value))
        case _                      => Fail(Fault.InvalidRequest("OpFreezeKHR requires a value of its result type"))
      }
    case instruction @ Instruction.BitcastExtractEXT(t, _, base, offset) =>
      val program = site.program
      typed(site, base) match {
        case Typed.Data(_, baseType)
            if program.numericShape(baseType).exists(shape =>
              program.numericShape(id(t)).exists(result => result._2 == shape._2 && result._1 < shape._1)
            ) && program.integerShape(program.valueType(id(offset))).exists(_._2 == 1) =>
          staged(site, instruction, CoreFamily.BitcastExtract)
        case Typed.Undefined(value) => Fail(undefined(value))
        case _                      => Fail(Fault.InvalidRequest(
            "OpBitcastExtractEXT requires a narrower numeric result of the base's lanes and a scalar integer offset"
          ))
      }
    // the operand whose specialised condition holds, which the module's constants already decide
    case Instruction.ConditionalCopyObjectINTEL(t, d, pairs) =>
      pairs.grouped(2).collectFirst {
        case Vec(condition, operand)
            if site.program.constants.get(id(condition)).exists(VMValue.isTrue) =>
          copy(id(t), id(d), id(operand))
      }.getOrElse(Fail(Fault.InvalidRequest("OpConditionalCopyObjectINTEL has no operand whose condition holds")))
    case Instruction.CopyObject(t, d, a)            => copy(id(t), id(d), id(a))
    case Instruction.ExpectKHR(t, d, a, _)          => copy(id(t), id(d), id(a))
    case Instruction.CopyLogical(t, d, a)           => copy(id(t), id(d), id(a))
    case instruction @ Instruction.Bitcast(t, d, a) => bitcast(site, instruction, id(t), id(d), id(a))
  }

  private def sameShape(site: Site, left: Int, right: Int): Boolean = {
    val program = site.program
    program.floatingShape(left).exists(program.floatingShape(right).contains) ||
    program.integerShape(left).exists(program.integerShape(right).contains)
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

  private def integerToInteger(site: Site, opcode: String, resultType: Operand, operand: Operand)
      : Either[Fault, (Int in 1 ~ 64, Int in 1 ~ 64)] =
    source(site, opcode, operand).flatMap(sourceType =>
      (site.program.integerShape(sourceType), site.program.integerShape(id(resultType))) match {
        case (Some((width, count)), Some((resultWidth, resultCount))) if count == resultCount =>
          Right((width, resultWidth))
        case (None, _) =>
          Left(Fault.InvalidRequest(s"$opcode source %${id(operand)} is not an integer scalar or vector"))
        case (_, None) =>
          Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not an integer scalar or vector"))
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes(opcode, count, resultCount))
      }
    )

  // Integers convert in one rounding, so 64-bit sources beyond binary64 precision do not round twice.
  private def integerToFloating(
      site: Site,
      opcode: String,
      resultType: Operand,
      operand: Operand
  ): Either[Fault, Unit] =
    source(site, opcode, operand).flatMap(sourceType =>
      (site.program.integerShape(sourceType), site.program.floatingShape(id(resultType))) match {
        case (Some((_, count)), Some((_, resultCount))) if count == resultCount => Right(())
        case (None, _)                                                          =>
          Left(Fault.InvalidRequest(s"$opcode source %${id(operand)} is not an integer scalar or vector"))
        case (_, None) =>
          Left(Fault.InvalidRequest(s"$opcode result type %${id(resultType)} is not a floating-point scalar or vector"))
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes(opcode, count, resultCount))
      }
    )

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

  private def floatingToFloating(site: Site, resultType: Operand, operand: Operand): Either[Fault, Unit] =
    source(site, "OpFConvert", operand).flatMap(sourceType =>
      (site.program.floatingShape(sourceType), site.program.floatingShape(id(resultType))) match {
        case (Some((_, count)), Some((_, resultCount))) if count == resultCount => Right(())
        case (None, _)                                                          =>
          Left(Fault.InvalidRequest(s"OpFConvert source %${id(operand)} is not a floating-point scalar or vector"))
        case (_, None) =>
          Left(
            Fault.InvalidRequest(s"OpFConvert result type %${id(resultType)} is not a floating-point scalar or vector")
          )
        case (Some((_, count)), Some((_, resultCount))) => Left(lanes("OpFConvert", count, resultCount))
      }
    )

  private def bitShape(program: VMProgram, typeId: Int): Option[(Int in 1 ~ 64, Int)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Pointer(_, _)) => Some((64, 1))
      case _ => program.numericShape(typeId).orElse(program.float8Shape(typeId).map((_, lanes) => (8, lanes)))
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
          if lanes.length == sourceCount && sourceWidth * sourceCount == resultWidth * resultCount =>
        val result = Array.ofDim[Long](resultCount)
        var lane   = 0
        while (lane < resultCount) {
          var bits = 0L
          var bit  = 0
          while (bit < resultWidth) {
            val sourceBit = lane * resultWidth + bit
            if (((lanes(sourceBit / sourceWidth) >>> (sourceBit % sourceWidth)) & 1L) != 0L) bits |= 1L << bit
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

  private def bitcast(site: Site, instruction: Instruction, resultType: Int, dest: Int, source: Int): Op = {
    val program = site.program
    val numeric =
      (bitShape(program, program.valueType(source)), program.runtimeType(resultType), program.numericShape(resultType))
    numeric match {
      // Same-width scalar reinterpretation only retypes the register.
      case (Some((width, 1)), _, Some((resultWidth, 1)))
          if width == resultWidth && program.scalarSlot(source) && program.scalarSlot(dest) =>
        staged(site, instruction, CoreFamily.Conversion)
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
                      case Some(RuntimeType.Function) => Right(VMValue.Bits(resultType, bits))
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
