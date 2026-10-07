package net.kurobako.gemstone.isa.spirv

import ArithmeticOps.*
import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import LaneOps.*
import Op.*
import VMInvocation.*
import VMLowering.*
import VMMemory.*
import VMProgram.*
import java.nio.charset.StandardCharsets
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.vm.*
import scala.collection.mutable

/** GLSL.std.450 and OpenCL.std extended instructions; the instruction set and opcode resolve when the site lowers. */
private object ExtendedOps {
  import Operand.*

  def lower(site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.ExtInst(resultType, dest, set, instruction, sources) =>
      val encoded = id(instruction)
      site.program.extendedInstructionSets.get(id(set)) match {
        case None                 => Fail(Fault.InvalidRequest(s"OpExtInst references undefined import %${id(set)}"))
        case Some("GLSL.std.450") => glsl(site, id(resultType), id(dest), encoded, sources.map(id(_)))
        // The half_* family permits reduced precision; the native implementation supplies at least that accuracy.
        case Some("OpenCL.std") =>
          val opcode =
            if (encoded >= OpenCL.HalfCos && encoded <= OpenCL.HalfTan) encoded + OpenCL.NativeCos - OpenCL.HalfCos
            else encoded
          openCL(site, id(resultType), id(dest), opcode, sources.map(id(_)))
        case Some(name) => Fail(Fault.Unsupported(s"OpExtInst $name opcode $encoded execution is not implemented"))
      }
  }

  private def count(name: String, sources: Vec[Int], expected: Int, plural: String = "operands")(op: => Op): Op =
    if (sources.size != expected)
      Fail(Fault.InvalidRequest(s"$name expects $expected $plural, received ${sources.size}"))
    else op

  /** Static types of data operands; `opaque` names the fault for a pointer operand. */
  private def dataTypes(site: Site, sources: Vec[Int], opaque: Int => Fault): Either[Fault, Vec[Int]] =
    sources.foldLeft[Either[Fault, Vec[Int]]](Right(Vec.empty)) { (result, source) =>
      result.flatMap(found =>
        typed(site, source) match {
          case Typed.Data(_, typeId) => Right(found :+ typeId)
          case Typed.Opaque(_, _)    => Left(opaque(source))
          case Typed.Undefined(_)    => Left(undefined(source))
        }
      )
    }

  private def lanes(values: InvocationValues, source: Int): Either[Fault, Array[Long]] =
    Option(values.lanes(source)).toRight(undefined(source))

  private def write(values: InvocationValues, dest: Int, resultType: Int, result: Array[Long]): Unit =
    values.update(dest, VMValue.Bits(resultType, result))

  /** Lanes of every source, passed to `compute` once they are all defined. */
  private def gather(sources: Vec[Int])(compute: (
      InvocationState,
      MemoryState,
      Array[Array[Long]]
  ) => AddressSpace.ReadResult[Unit]): Op =
    step { (execution, memory) =>
      val values = execution.values
      sources.foldLeft[Either[Fault, Vector[Array[Long]]]](Right(Vector.empty))((result, source) =>
        result.flatMap(found => lanes(values, source).map(found :+ _))
      ).flatMap(found => compute(execution, memory, found.toArray))
    }

  private def toDouble(width: Int in (16, 32, 64), bits: Long): Double    = IEEE754.toDouble(bits, width)
  private def fromDouble(width: Int in (16, 32, 64), value: Double): Long = IEEE754.fromDouble(value, width)

  /** Elementwise floating-point builtins whose scalar operands broadcast across the result's lanes. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def elementwise(
      site: Site,
      resultType: Int,
      dest: Int,
      sources: Vec[Int],
      name: String,
      set: String = "OpenCL.std"
  )(
      operation: Array[Double] => Double
  ): Op = site.program.floatingShape(resultType) match {
    case None                 => Fail(Fault.InvalidRequest(s"$set $name result is not floating-point"))
    case Some((width, count)) =>
      val shapes = sources.zipWithIndex.map { (source, index) =>
        typed(site, source) match {
          case Typed.Undefined(_)    => Left(undefined(source))
          case Typed.Opaque(_, _)    => Left(Fault.InvalidRequest(s"$set $name operand ${index + 1} is a pointer"))
          case Typed.Data(_, typeId) => site.program.floatingShape(typeId) match {
              case Some((sourceWidth, sourceCount))
                  if sourceWidth == width && (sourceCount == count || sourceCount == 1) =>
                Right(sourceCount)
              case _ =>
                Left(
                  Fault.InvalidRequest(s"$set $name operand ${index + 1} does not match result shape ($width,$count)")
                )
            }
        }
      }
      shapes.collectFirst { case Left(error) => error } match {
        case Some(error) => Fail(error)
        case None        =>
          gather(sources) { (execution, _, operands) =>
            val arguments = Array.ofDim[Double](operands.length)
            val result    = Array.tabulate(count) { lane =>
              var index = 0
              while (index < operands.length) {
                arguments(index) = toDouble(width, operands(index)(lane % operands(index).length))
                index += 1
              }
              fromDouble(width, operation(arguments))
            }
            Right(execution.values.write(dest, resultType, result))
          }
      }
  }

  /** Floating builtins whose operands and result share one exact shape. */
  @SuppressWarnings(Array("scalafix:DisableSyntax.var"))
  private def sameFloating(
      site: Site,
      resultType: Int,
      dest: Int,
      sources: Vec[Int],
      mismatch: NonEmpty[String],
      opaque: NonEmpty[String]
  )(
      kernel: (Int in (16, 32, 64)) => Array[Long] => Long
  ): Op = lowered(dataTypes(site, sources, _ => Fault.InvalidRequest(opaque)).flatMap { types =>
    val shape = site.program.floatingShape(resultType)
    Either.cond(
      shape.isDefined && types.forall(typeId => site.program.floatingShape(typeId) == shape), {
        val (width, count) = shape.getOrElse(throw IllegalStateException("validated floating shape"))
        val evaluate       = kernel(width)
        gather(sources) { (execution, _, operands) =>
          val arguments = Array.ofDim[Long](operands.length)
          val result    = Array.tabulate(count) { lane =>
            var index = 0
            while (index < operands.length) {
              arguments(index) = operands(index)(lane)
              index += 1
            }
            evaluate(arguments)
          }
          Right(execution.values.write(dest, resultType, result))
        }
      },
      Fault.InvalidRequest(mismatch)
    )
  })

  private def unaryFloating(
      site: Site,
      set: String,
      name: String,
      resultType: Int,
      dest: Int,
      source: Int,
      absolute: Boolean
  )(
      evaluate: (Int in (16, 32, 64), Double) => Double
  ): Op = typed(site, source) match {
    case Typed.Undefined(_) => Fail(undefined(source))
    case Typed.Opaque(_, _) =>
      Fail(Fault.InvalidRequest(if (set == "GLSL.std.450") "GLSL.std.450 unary operand is not floating-point"
      else s"OpenCL.std $name operand is not floating-point"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(sourceType), site.program.floatingShape(resultType)) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape)) if sourceShape == resultShape =>
          val magnitude = ArithmeticOps.signMask(width) - 1L
          unary(site, resultType, dest, source)(raw =>
            if (absolute) raw & magnitude else fromDouble(width, evaluate(width, toDouble(width, raw)))
          )
        case _ =>
          Fail(Fault.InvalidRequest(
            if (set == "GLSL.std.450") "GLSL.std.450 unary floating types do not match"
            else s"OpenCL.std $name requires matching floating-point scalar/vector operand and result types"
          ))
      }
  }

  private def glsl(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = opcode match {
    case Glsl.SAbs | Glsl.SSign | Glsl.UMin | Glsl.SMin | Glsl.UMax | Glsl.SMax | Glsl.UClamp | Glsl.SClamp | Glsl.FindILsb |
        Glsl.FindSMsb | Glsl.FindUMsb => glslInteger(site, resultType, dest, opcode, sources)
    case Glsl.Atan2 =>
      if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL Atan2 expects two operands"))
      else atan2OrHypot(site, resultType, dest, sources(0), sources(1), hypot = false)
    case Glsl.Pow =>
      if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL.std.450 Pow expects two operands"))
      else pow(site, resultType, dest, sources(0), sources(1))
    case unaryOpcode if unaryOpcode >= Glsl.Round && unaryOpcode <= Glsl.InverseSqrt =>
      if (sources.size != 1) Fail(Fault.InvalidRequest(s"GLSL.std.450 opcode $opcode expects one operand"))
      else
        unaryFloating(site, "GLSL.std.450", opcode.toString, resultType, dest, sources.head, opcode == Glsl.FAbs) {
          (width, value) =>
            opcode match {
              case Glsl.Round | Glsl.RoundEven => Math.rint(value)
              case Glsl.Trunc                  => if (value < 0) Math.ceil(value) else Math.floor(value)
              case Glsl.FSign                  => if (value < 0) -1.0 else if (value > 0) 1.0 else 0.0
              case Glsl.Floor                  => Math.floor(value)
              case Glsl.Ceil                   => Math.ceil(value)
              case Glsl.Fract                  => value - Math.floor(value)
              case Glsl.Radians                => value * (Math.PI / 180.0)
              case Glsl.Degrees                => value * (180.0 / Math.PI)
              case Glsl.Sin                    => Math.sin(value)
              case Glsl.Cos                    => Math.cos(value)
              case Glsl.Tan                    => Math.tan(value)
              case Glsl.Asin                   => Math.asin(value)
              case Glsl.Acos                   => Math.acos(value)
              case Glsl.Atan                   => Math.atan(value)
              case Glsl.Sinh | Glsl.Cosh       =>
                // GLSL defines these through exp at the operand precision, preserving intermediate overflow.
                def rounded(result: Double): Double = toDouble(width, fromDouble(width, result))
                val positive                        = rounded(Math.exp(value))
                val negative                        = rounded(Math.exp(-value))
                rounded(if (opcode == Glsl.Sinh) positive - negative else positive + negative) / 2.0
              case Glsl.Tanh        => Math.tanh(value)
              case Glsl.Asinh       => OpenCLMath.asinh(value)
              case Glsl.Acosh       => OpenCLMath.acosh(value)
              case Glsl.Atanh       => OpenCLMath.atanh(value)
              case Glsl.Exp         => Math.exp(value)
              case Glsl.Log         => Math.log(value)
              case Glsl.Exp2        => Math.pow(2.0, value)
              case Glsl.Log2        => Math.log(value) / Math.log(2.0)
              case Glsl.Sqrt        => Math.sqrt(value)
              case Glsl.InverseSqrt => 1.0 / Math.sqrt(value)
              case _                => Double.NaN
            }
        }
    case Glsl.FMin | Glsl.FMax | Glsl.FClamp | Glsl.NMin | Glsl.NMax | Glsl.NClamp =>
      glslNumericMinMax(site, resultType, dest, opcode, sources)
    case Glsl.Determinant | Glsl.MatrixInverse => glslMatrix(site, resultType, dest, opcode, sources)
    case Glsl.Length | Glsl.Distance           => glslLengthDistance(site, resultType, dest, opcode, sources)
    case packing if packing >= Glsl.PackSnorm4x8 && packing <= Glsl.UnpackDouble2x32 =>
      glslPack(site, resultType, dest, opcode, sources)
    case Glsl.Modf | Glsl.ModfStruct | Glsl.Frexp | Glsl.FrexpStruct =>
      glslDecompose(site, resultType, dest, opcode, sources)
    case Glsl.FMix | Glsl.Step | Glsl.SmoothStep =>
      val arity = if (opcode == Glsl.Step) 2 else 3
      if (sources.size != arity) Fail(Fault.InvalidRequest(s"GLSL opcode $opcode expects $arity operands"))
      else
        elementwise(site, resultType, dest, sources, opcode.toString, "GLSL.std.450") { a =>
          opcode match {
            case Glsl.FMix => a(0) * (1.0 - a(2)) + a(1) * a(2)
            case Glsl.Step => if (a(1) < a(0)) 0.0 else 1.0
            case _         =>
              val t = Math.max(0.0, Math.min(1.0, (a(2) - a(0)) / (a(1) - a(0))))
              t * t * (3.0 - 2.0 * t)
          }
        }
    case Glsl.Fma =>
      if (sources.size != 3) Fail(Fault.InvalidRequest("GLSL Fma expects three operands"))
      else mad(site, resultType, dest, sources, fused = true)
    case Glsl.Ldexp =>
      if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL Ldexp expects two operands"))
      else ldexp(site, resultType, dest, sources(0), sources(1), glsl = true)
    case Glsl.Cross                                     => geometric(site, resultType, dest, OpenCL.Cross, sources)
    case Glsl.Normalize                                 => geometric(site, resultType, dest, OpenCL.Normalize, sources)
    case Glsl.FaceForward | Glsl.Reflect | Glsl.Refract => glslGeometric(site, resultType, dest, opcode, sources)
    case _ => Fail(Fault.Unsupported(s"OpExtInst GLSL.std.450 opcode $opcode execution is not implemented"))
  }

  private def glslInteger(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val unary    = opcode == Glsl.SAbs || opcode == Glsl.SSign || opcode >= Glsl.FindILsb
    val required = if (unary) 1 else if (opcode == Glsl.UClamp || opcode == Glsl.SClamp) 3 else 2
    if (sources.size != required) Fail(Fault.InvalidRequest(s"GLSL integer opcode $opcode has the wrong operand count"))
    else site.program.integerShape(resultType) match {
      case None => Fail(Fault.InvalidRequest(s"GLSL integer opcode $opcode requires an integer type"))
      case Some((width, _)) if opcode >= Glsl.FindILsb && width.raw != 32 =>
        Fail(Fault.InvalidRequest("GLSL integer bit searches require 32-bit components"))
      case Some(shape @ (width, count)) =>
        lowered(dataTypes(
          site,
          sources,
          _ => Fault.InvalidRequest(s"GLSL integer opcode $opcode operand types do not match")
        ).flatMap {
          types =>
            Either.cond(
              types.forall(typeId => site.program.integerShape(typeId).contains(shape)), {
                // The instruction selects signedness independently of the operand type's signedness.
                val signed = opcode == Glsl.SMin || opcode == Glsl.SMax || opcode == Glsl.SClamp
                val bits   = mask(width)
                def less(a: Long, b: Long): Boolean =
                  if (signed) signExtend(a, width) < signExtend(b, width) else java.lang.Long.compareUnsigned(a, b) < 0
                def minimum(a: Long, b: Long): Long = if (less(b, a)) b else a
                def maximum(a: Long, b: Long): Long = if (less(a, b)) b else a
                gather(sources) { (execution, _, operands) =>
                  val result = Array.tabulate(count) { lane =>
                    val a     = operands(0)(lane) & bits
                    val b     = if (unary) 0L else operands(1)(lane) & bits
                    val value = opcode match {
                      case Glsl.SAbs     => if (signExtend(a, width) < 0) -a else a
                      case Glsl.SSign    => java.lang.Long.signum(signExtend(a, width)).toLong
                      case Glsl.FindILsb => if (a == 0) -1L else java.lang.Long.numberOfTrailingZeros(a).toLong
                      case Glsl.FindSMsb =>
                        (63 - java.lang.Long.numberOfLeadingZeros(if (signExtend(a, width) < 0) ~a & bits
                        else a)).toLong
                      case Glsl.FindUMsb         => (63 - java.lang.Long.numberOfLeadingZeros(a)).toLong
                      case Glsl.UMin | Glsl.SMin => minimum(a, b)
                      case Glsl.UMax | Glsl.SMax => maximum(a, b)
                      case _                     => minimum(maximum(a, b), operands(2)(lane) & bits)
                    }
                    value & bits
                  }
                  Right(write(execution.values, dest, resultType, result))
                }
              },
              Fault.InvalidRequest(s"GLSL integer opcode $opcode operand types do not match")
            )
        })
    }
  }

  // FMin and FMax permit the same numeric operand preference for NaNs as NMin and NMax.
  private def glslNumericMinMax(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val required = if (opcode == Glsl.FClamp || opcode == Glsl.NClamp) 3 else 2
    if (sources.size != required) Fail(Fault.InvalidRequest("GLSL numeric min/max has the wrong operand count"))
    else if (site.program.floatingShape(resultType).isEmpty)
      Fail(Fault.InvalidRequest("GLSL numeric min/max requires a floating type"))
    else
      sameFloating(
        site,
        resultType,
        dest,
        sources,
        "GLSL numeric min/max operand types do not match",
        "GLSL numeric min/max operand types do not match"
      ) { width => operands =>
        def minimum(a: Double, b: Double): Double = if (a.isNaN) b else if (b.isNaN) a else Math.min(a, b)
        def maximum(a: Double, b: Double): Double = if (a.isNaN) b else if (b.isNaN) a else Math.max(a, b)
        val a                                     = toDouble(width, operands(0))
        val b                                     = toDouble(width, operands(1))
        fromDouble(
          width,
          opcode match {
            case Glsl.FMin | Glsl.NMin => minimum(a, b)
            case Glsl.FMax | Glsl.NMax => maximum(a, b)
            case _                     => minimum(maximum(a, b), toDouble(width, operands(2)))
          }
        )
      }
  }

  private def glslMatrix(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op =
    if (sources.size != 1) Fail(Fault.InvalidRequest("GLSL matrix operation requires one operand"))
    else typed(site, sources.head) match {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Opaque(_, _)     => Fail(Fault.InvalidRequest("GLSL determinant/inverse operand is not matrix data"))
      case Typed.Data(_, typeId)  =>
        ArithmeticOps.matrixShape(site.program, typeId) match {
          case Some(shape @ (width, rows, columns))
              if rows == columns && (if (opcode == Glsl.Determinant)
                                       site.program.floatingShape(resultType).contains(width -> 1)
                                     else ArithmeticOps.matrixShape(site.program, resultType).contains(shape)) =>
            gather(sources) { (execution, _, operands) =>
              val lanes = operands.head
              if (lanes.length != rows * columns)
                Left(Fault.InvalidRequest("GLSL determinant/inverse requires matching square matrix types"))
              else {
                val matrix = Vector.tabulate(rows, columns)((r, c) => toDouble(width, lanes(c * rows + r)))
                def minor(m: Vector[Vector[Double]], row: Int, column: Int): Vector[Vector[Double]] =
                  m.zipWithIndex.collect {
                    case (values, r) if r != row =>
                      values.zipWithIndex.collect { case (value, c) if c != column => value }
                  }
                def determinant(m: Vector[Vector[Double]]): Double =
                  if (m.size == 1) m.head.head
                  else (1 until m.size).foldLeft(m.head.head * determinant(minor(m, 0, 0))) { (sum, column) =>
                    val term = m.head(column) * determinant(minor(m, 0, column))
                    if (column % 2 == 0) sum + term else sum - term
                  }
                val det    = determinant(matrix)
                val result =
                  if (opcode == Glsl.Determinant) Array(fromDouble(width, det))
                  else Array.tabulate(rows * columns) { lane =>
                    val r        = lane % rows
                    val c        = lane / rows
                    val cofactor = determinant(minor(matrix, c, r))
                    fromDouble(width, (if ((r + c) % 2 == 0) cofactor else -cofactor) / det)
                  }
                Right(write(execution.values, dest, resultType, result))
              }
            }
          case _ => Fail(Fault.InvalidRequest("GLSL determinant/inverse requires matching square matrix types"))
        }
    }

  private def glslLengthDistance(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op =
    if (sources.size != (if (opcode == Glsl.Length) 1 else 2))
      Fail(Fault.InvalidRequest("GLSL Length/Distance operand count is invalid"))
    else site.program.floatingShape(resultType) match {
      case None => Fail(Fault.InvalidRequest("GLSL Length/Distance result must be floating point"))
      case Some(resultShape @ (width, _)) =>
        val shapes = sources.map(source =>
          typed(site, source) match {
            case Typed.Data(_, typeId) =>
              site.program.floatingShape(
                typeId
              ).toRight(Fault.InvalidRequest("GLSL Length/Distance operand must be floating point"))
            case _ => Left(Fault.InvalidRequest("GLSL Length/Distance operand is missing"))
          }
        )
        shapes.collectFirst { case Left(error) => error } match {
          case Some(error) => Fail(error)
          case None        =>
            val found = shapes.collect { case Right(shape) => shape }
            if (resultShape._2 != 1 || !found.forall(shape => shape == found.head && shape._1 == width))
              Fail(Fault.InvalidRequest("GLSL Length/Distance types do not match"))
            else
              gather(sources) { (execution, _, operands) =>
                val length = operands.head.indices.foldLeft(0.0) { (length, lane) =>
                  val a     = toDouble(width, operands(0)(lane))
                  val value =
                    if (opcode == Glsl.Length) a
                    else toDouble(width, fromDouble(width, a - toDouble(width, operands(1)(lane))))
                  Math.hypot(length, value)
                }
                Right(execution.values.updateScalar(dest, resultType, fromDouble(width, length)))
              }
        }
    }

  private def glslPack(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op =
    if (sources.size != 1) Fail(Fault.InvalidRequest("GLSL packing expects one operand"))
    else typed(site, sources.head) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("GLSL packing operand is a pointer"))
      case Typed.Data(_, sourceType) =>
        val program     = site.program
        val sourceFloat = program.floatingShape(sourceType).map((w, n) => w.raw -> n)
        val resultFloat = program.floatingShape(resultType).map((w, n) => w.raw -> n)
        val sourceInt   = signedIntegerShape(program, sourceType)
        val resultInt   = signedIntegerShape(program, resultType)
        val count       =
          if (
            opcode == Glsl.PackSnorm4x8 || opcode == Glsl.PackUnorm4x8 || opcode == Glsl.UnpackSnorm4x8 || opcode == Glsl.UnpackUnorm4x8
          ) 4
          else 2
        val bits                                        = if (count == 4) 8 else 16
        val packing: Option[Array[Long] => Array[Long]] =
          if (opcode <= Glsl.PackHalf2x16 && sourceFloat.contains(32 -> count) && resultInt.contains((32, 1, false))) {
            val signed = opcode == Glsl.PackSnorm4x8 || opcode == Glsl.PackSnorm2x16
            val scale  = if (signed) (1 << (bits - 1)) - 1 else (1 << bits) - 1
            Some(lanes =>
              Array(lanes.zipWithIndex.foldLeft(0L) { case (acc, (raw, lane)) =>
                val value = IEEE754.binary32ToFloat(Binary32.low(raw))
                val word  =
                  if (opcode == Glsl.PackHalf2x16) IEEE754.floatToBinary16(value).raw.toLong
                  else Math.round(Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value.toDouble)) * scale)
                acc | ((word & ((1L << bits) - 1)) << (bits * lane))
              })
            )
          } else if (
            opcode == Glsl.PackDouble2x32 && sourceInt.contains((32, 2, false)) && resultFloat.contains(64 -> 1)
          )
            Some(lanes => Array((lanes(0) & 0xffffffffL) | (lanes(1) << 32)))
          else if (
            opcode == Glsl.UnpackDouble2x32 && sourceFloat.contains(64 -> 1) && resultInt.contains((32, 2, false))
          )
            Some(lanes => Array(lanes(0) & 0xffffffffL, lanes(0) >>> 32))
          else if (
            opcode >= Glsl.UnpackSnorm2x16 && opcode <= Glsl.UnpackUnorm4x8 && sourceInt.contains((32, 1, false)) &&
            resultFloat.contains(32 -> count)
          ) {
            val signed = opcode == Glsl.UnpackSnorm2x16 || opcode == Glsl.UnpackSnorm4x8
            Some(lanes =>
              Array.tabulate(count) { lane =>
                val word  = (lanes(0) >>> (bits * lane)) & ((1L << bits) - 1)
                val value =
                  if (opcode == Glsl.UnpackHalf2x16) IEEE754.binary16ToFloat(Binary16.low(word))
                  else if (signed)
                    Math.max(
                      -1.0,
                      ((word ^ (1L << (bits - 1))) - (1L << (bits - 1))).toDouble / ((1 << (bits - 1)) - 1)
                    ).toFloat
                  else (word.toDouble / ((1 << bits) - 1)).toFloat
                IEEE754.floatToBinary32(value).toLong
              }
            )
          } else None
        packing match {
          case None => Fail(Fault.InvalidRequest(s"GLSL packing opcode $opcode has invalid operand or result types"))
          case Some(convert) => gather(sources)((execution, _, operands) =>
              Right(write(execution.values, dest, resultType, convert(operands.head)))
            )
        }
    }

  private def glslDecompose(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val program     = site.program
    val pointerForm = opcode == Glsl.Modf || opcode == Glsl.Frexp
    val modf        = opcode == Glsl.Modf || opcode == Glsl.ModfStruct
    if (sources.size != (if (pointerForm) 2 else 1))
      Fail(Fault.InvalidRequest("GLSL decomposition has an incorrect operand count"))
    else typed(site, sources.head) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("GLSL decomposition requires floating-point input"))
      case Typed.Data(_, sourceType) =>
        val incompatible = Fault.InvalidRequest("GLSL decomposition has incompatible operand or result types")
        def valid(outputType: Int): Boolean = program.floatingShape(sourceType).exists { case (_, count) =>
          if (modf) outputType == sourceType else program.integerShape(outputType).exists(_._2 == count)
        }
        program.floatingShape(sourceType) match {
          case None                 => Fail(incompatible)
          case Some((width, count)) =>
            def decompose(lanes: Array[Long], outputType: Int): (Array[Long], Array[Long]) = {
              val fractional = Array.ofDim[Long](count)
              val integral   = Array.ofDim[Long](count)
              (0 until count).foreach { lane =>
                val value = toDouble(width, lanes(lane))
                if (modf) {
                  val integer = if (value < 0.0) Math.ceil(value) else Math.floor(value)
                  fractional(lane) =
                    fromDouble(
                      width,
                      if (value.isInfinity) Math.copySign(0.0, value) else Math.copySign(value - integer, value)
                    )
                  integral(lane) = fromDouble(width, integer)
                } else {
                  val exponent = if (value == 0.0 || !value.isFinite) 0
                  else {
                    val initial = Math.getExponent(value)
                    (if (initial == -1023) Math.getExponent(Math.scalb(value, 54)) - 54 else initial) + 1
                  }
                  fractional(lane) = fromDouble(width, Math.scalb(value, -exponent))
                  integral(lane) = exponent.toLong & program.integerShape(outputType).fold(-1L)(shape => mask(shape._1))
                }
              }
              (fractional, integral)
            }
            if (pointerForm)
              step { (execution, memory) =>
                val values = execution.values
                (lanes(values, sources(0)), values.get(sources(1))) match {
                  case (Left(error), _) => Left(error)
                  case (Right(source), Some(VMValue.Address(_, pointer)))
                      if resultType == sourceType && valid(pointer.pointee) &&
                        source.length == count =>
                    val (fractional, integral) = decompose(source, pointer.pointee)
                    program.encode(VMValue.Bits(pointer.pointee, integral)).flatMap(memory.write(pointer, _)).map(_ =>
                      write(values, dest, resultType, fractional)
                    )
                  case _ => Left(incompatible)
                }
              }
            else
              program.runtimeType(resultType) match {
                case Some(RuntimeType.Structure(members))
                    if members.size == 2 && members(0) == sourceType && valid(members(1)) =>
                  gather(sources) { (execution, _, operands) =>
                    val (fractional, integral) = decompose(operands.head, members(1))
                    Right(write(execution.values, dest, resultType, fractional ++ integral))
                  }
                case _ => Fail(incompatible)
              }
        }
    }
  }

  private def glslGeometric(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val arity = if (opcode == Glsl.Reflect) 2 else 3
    site.program.floatingShape(resultType) match {
      case Some((width, count)) if sources.size == arity =>
        val checks = sources.zipWithIndex.map { (source, index) =>
          val expected = if (opcode == Glsl.Refract && index == 2) 1 else count
          typed(site, source) match {
            case Typed.Undefined(value) => Some(undefined(value))
            case Typed.Data(_, typeId) if site.program.floatingShape(typeId).contains(width -> expected) => None
            case _ => Some(Fault.InvalidRequest("GLSL geometric operand types do not match"))
          }
        }
        checks.flatten.headOption match {
          case Some(error) => Fail(error)
          case None        =>
            gather(sources) { (execution, _, raw) =>
              val operands = raw.map(_.map(toDouble(width, _)))
              val dot      =
                if (opcode == Glsl.FaceForward) operands(2).zip(operands(1)).map(_ * _).sum
                else operands(0).zip(operands(1)).map(_ * _).sum
              val result = Array.tabulate(count) { lane =>
                val value = opcode match {
                  case Glsl.FaceForward => if (dot < 0.0) operands(0)(lane) else -operands(0)(lane)
                  case Glsl.Reflect     => operands(0)(lane) - 2.0 * dot * operands(1)(lane)
                  case _                =>
                    val eta = operands(2)(0)
                    val k   = 1.0 - eta * eta * (1.0 - dot * dot)
                    if (k < 0.0) 0.0 else eta * operands(0)(lane) - (eta * dot + Math.sqrt(k)) * operands(1)(lane)
                }
                fromDouble(width, value)
              }
              Right(write(execution.values, dest, resultType, result))
            }
        }
      case _ => Fail(Fault.InvalidRequest("GLSL geometric result or operand count is invalid"))
    }
  }

  private def signedIntegerShape(program: VMProgram, typeId: Int): Option[(Int, Int, Boolean)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, signed))       => Some((width.raw, 1, signed))
      case Some(RuntimeType.VectorType(component, count)) => program.runtimeType(component).collect {
          case RuntimeType.Integer(width, signed) => (width.raw, count.raw, signed)
        }
      case _ => None
    }

  private val unaryNames: Map[Int, (String, Double => Double)] = Map(
    OpenCL.Acos -> ("acos", Math.acos(_)),
    OpenCL.Acosh  -> ("acosh", OpenCLMath.acosh),
    OpenCL.Acospi -> ("acospi", value => Math.acos(value) / Math.PI),
    OpenCL.Asin -> ("asin", Math.asin(_)),
    OpenCL.Asinh  -> ("asinh", OpenCLMath.asinh),
    OpenCL.Asinpi -> ("asinpi", value => Math.asin(value) / Math.PI),
    OpenCL.Atan -> ("atan", Math.atan(_)),
    OpenCL.Atanh  -> ("atanh", OpenCLMath.atanh),
    OpenCL.Atanpi -> ("atanpi", value => Math.atan(value) / Math.PI),
    OpenCL.Cbrt  -> ("cbrt", Math.cbrt(_)),
    OpenCL.Ceil  -> ("ceil", Math.ceil(_)),
    OpenCL.Cos   -> ("cos", Math.cos(_)),
    OpenCL.Cosh  -> ("cosh", Math.cosh(_)),
    OpenCL.Cospi -> ("cospi", cosPi(_)),
    OpenCL.Erf    -> ("erf", OpenCLMath.erf),
    OpenCL.Erfc   -> ("erfc", OpenCLMath.erfc),
    OpenCL.Tgamma -> ("tgamma", OpenCLMath.tgamma),
    OpenCL.Lgamma -> ("lgamma", OpenCLMath.lgamma),
    OpenCL.Exp -> ("exp", Math.exp(_)),
    OpenCL.Exp2  -> ("exp2", value => Math.pow(2.0, value)),
    OpenCL.Exp10 -> ("exp10", value => Math.pow(10.0, value)),
    OpenCL.Expm1 -> ("expm1", Math.expm1(_)),
    OpenCL.Fabs  -> ("fabs", Math.abs(_)),
    OpenCL.Floor -> ("floor", Math.floor(_)),
    OpenCL.Log   -> ("log", Math.log(_)),
    OpenCL.Log2 -> ("log2", value => Math.log(value) / Math.log(2.0)),
    OpenCL.Log10 -> ("log10", Math.log10(_)),
    OpenCL.Log1p -> ("log1p", Math.log1p(_)),
    OpenCL.Logb -> (
      "logb",
      value =>
        if (value.isNaN) Double.NaN
        else if (value == 0.0) Double.NegativeInfinity
        else if (value.isInfinite) Double.PositiveInfinity
        else exponent(value).toDouble
    ),
    OpenCL.Rint -> ("rint", Math.rint(_)),
    OpenCL.Round -> ("round", value => if (value < 0.0) Math.ceil(value - 0.5) else Math.floor(value + 0.5)),
    OpenCL.Rsqrt -> ("rsqrt", value => 1.0 / Math.sqrt(value)),
    OpenCL.Sin   -> ("sin", Math.sin(_)),
    OpenCL.Sinh  -> ("sinh", Math.sinh(_)),
    OpenCL.Sinpi -> ("sinpi", sinPi(_)),
    OpenCL.Sqrt  -> ("sqrt", Math.sqrt(_)),
    OpenCL.Tan   -> ("tan", Math.tan(_)),
    OpenCL.Tanh  -> ("tanh", Math.tanh(_)),
    OpenCL.Tanpi -> ("tanpi", tanPi(_)),
    OpenCL.Trunc -> ("trunc", value => if (value < 0.0) Math.ceil(value) else Math.floor(value)),
    OpenCL.NativeCos -> ("native_cos", Math.cos(_)),
    OpenCL.NativeExp -> ("native_exp", Math.exp(_)),
    OpenCL.NativeExp2  -> ("native_exp2", value => Math.pow(2.0, value)),
    OpenCL.NativeExp10 -> ("native_exp10", value => Math.pow(10.0, value)),
    OpenCL.NativeLog -> ("native_log", Math.log(_)),
    OpenCL.NativeLog2 -> ("native_log2", value => Math.log(value) / Math.log(2.0)),
    OpenCL.NativeLog10 -> ("native_log10", Math.log10(_)),
    OpenCL.NativeRecip -> ("native_recip", value => 1.0 / value),
    OpenCL.NativeRsqrt -> ("native_rsqrt", value => 1.0 / Math.sqrt(value)),
    OpenCL.NativeSin  -> ("native_sin", Math.sin(_)),
    OpenCL.NativeSqrt -> ("native_sqrt", Math.sqrt(_)),
    OpenCL.NativeTan  -> ("native_tan", Math.tan(_))
  )

  private val binaryNames: Map[Int, (String, (Double, Double) => Double)] = Map(
    OpenCL.Atan2pi -> ("atan2pi", (a, b) => Math.atan2(a, b) / Math.PI),
    OpenCL.Fdim    -> ("fdim", (a, b) => Math.max(a - b, 0.0)),
    OpenCL.Fmod         -> ("fmod", _ % _),
    OpenCL.Remainder    -> ("remainder", Math.IEEEremainder(_, _)),
    OpenCL.NativeDivide -> ("native_divide", _ / _),
    OpenCL.NativePowr   -> ("native_powr", powr(_, _)),
    OpenCL.Powr         -> ("powr", powr(_, _))
  )

  private def sinPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else if (value == Math.rint(value)) Math.copySign(0.0, value)
    else {
      val reduced   = Math.IEEEremainder(value, 2.0)
      val x         = Math.abs(reduced)
      val magnitude =
        if (x <= 0.25) Math.sin(Math.PI * x)
        else if (x <= 0.75) Math.cos(Math.PI * (0.5 - x)) else Math.sin(Math.PI * (1.0 - x))
      Math.copySign(magnitude, reduced)
    }

  private def cosPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val x = Math.abs(Math.IEEEremainder(value, 2.0))
      if (x == 0.5) 0.0
      else if (x <= 0.25) Math.cos(Math.PI * x)
      else if (x <= 0.75) Math.sin(Math.PI * (0.5 - x))
      else -Math.cos(Math.PI * (1.0 - x))
    }

  private def tanPi(value: Double): Double =
    if (!value.isFinite) Double.NaN
    else {
      val period  = Math.IEEEremainder(value, 2.0)
      val reduced = Math.IEEEremainder(value, 1.0)
      val x       = Math.abs(reduced)
      if (x == 0.0) Math.copySign(0.0, if (Math.abs(period) == 1.0) -value else value)
      else if (x == 0.5) Math.copySign(Double.PositiveInfinity, period)
      else Math.copySign(if (x <= 0.25) Math.tan(Math.PI * x) else 1.0 / Math.tan(Math.PI * (0.5 - x)), reduced)
    }

  private def powr(base: Double, exponent: Double): Double =
    if (
      base.isNaN || exponent.isNaN || base < 0.0 || (base == 1.0 && exponent.isInfinity) ||
      ((base == 0.0 || base == Double.PositiveInfinity) && exponent == 0.0)
    ) Double.NaN
    else if (base == 0.0) { if (exponent < 0.0) Double.PositiveInfinity else 0.0 }
    else Math.pow(base, exponent)

  private def exponent(value: Double): Int = {
    val exponent = Math.getExponent(value)
    if (exponent == java.lang.Double.MIN_EXPONENT - 1) Math.getExponent(Math.scalb(value, 54)) - 54 else exponent
  }

  private def floatingMinimum(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.minNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  private def floatingMaximum(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.maxNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  private def openCL(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = opcode match {
    case OpenCL.Copysign =>
      count("OpenCL.std copysign", sources, 2)(
        sameFloating(
          site,
          resultType,
          dest,
          sources,
          "OpenCL.std copysign requires matching floating-point scalar/vector operand and result types",
          "OpenCL.std copysign operands are not floating-point"
        )(width =>
          operands => (operands(0) & ~ArithmeticOps.signMask(width)) | (operands(1) & ArithmeticOps.signMask(width))
        )
      )
    case OpenCL.Fma | OpenCL.Mad =>
      count(
        s"OpenCL.std ${if (opcode == OpenCL.Fma) "fma" else "mad"}",
        sources,
        3
      )(mad(site, resultType, dest, sources, opcode == OpenCL.Fma))
    case OpenCL.Maxmag | OpenCL.Minmag =>
      count("OpenCL.std maxmag", sources, 2)(magnitude(site, resultType, dest, sources, opcode == OpenCL.Maxmag))
    case OpenCL.Pow => count("OpenCL.std pow", sources, 2)(pow(site, resultType, dest, sources(0), sources(1)))
    case OpenCL.SMax | OpenCL.UMax | OpenCL.SMin | OpenCL.UMin =>
      val name = opcode match {
        case OpenCL.SMax => "s_max"
        case OpenCL.UMax => "u_max"
        case OpenCL.SMin => "s_min"
        case _           => "u_min"
      }
      count(s"OpenCL.std $name", sources, 2)(
        ArithmeticOps.bitwise(
          site,
          s"OpenCL.std $name",
          operand(resultType),
          operand(dest),
          operand(sources(0)),
          operand(sources(1))
        ) {
          width => (left, right) =>
            opcode match {
              case OpenCL.SMax => if (signExtend(left, width) >= signExtend(right, width)) left else right
              case OpenCL.UMax => if (java.lang.Long.compareUnsigned(left, right) >= 0) left else right
              case OpenCL.SMin => if (signExtend(left, width) <= signExtend(right, width)) left else right
              case _           => if (java.lang.Long.compareUnsigned(left, right) <= 0) left else right
            }
        }
      )
    case OpenCL.Sincos | OpenCL.Fract | OpenCL.Modf | OpenCL.Frexp | OpenCL.LgammaR =>
      count(
        s"OpenCL.std pointer math opcode $opcode",
        sources,
        2
      )(pointerMath(site, resultType, dest, sources(0), sources(1), opcode))
    case OpenCL.Ilogb =>
      if (sources.size != 1) Fail(Fault.InvalidRequest("OpenCL.std ilogb requires one operand"))
      else ilogb(site, resultType, dest, sources.head)
    case OpenCL.Remquo =>
      if (sources.size != 3) Fail(Fault.InvalidRequest("OpenCL.std remquo requires three operands"))
      else remquo(site, resultType, dest, sources)
    case OpenCL.Printf             => printf(site, resultType, dest, sources)
    case OpenCL.Fmax | OpenCL.Fmin =>
      count(s"OpenCL.std ${if (opcode == OpenCL.Fmax) "fmax" else "fmin"}", sources, 2)(
        ArithmeticOps.floating(
          site,
          if (opcode == OpenCL.Fmax) FloatingOperation.Maximum else FloatingOperation.Minimum,
          operand(resultType),
          operand(dest),
          operand(sources(0)),
          operand(sources(1))
        )
      )
    case OpenCL.Ldexp => count("OpenCL.std ldexp", sources, 2)(ldexp(site, resultType, dest, sources(0), sources(1)))
    case unaryOpcode if unaryNames.contains(unaryOpcode) =>
      val (name, evaluate) = unaryNames(unaryOpcode)
      count(s"OpenCL.std $name", sources, 1, "operand")(
        unaryFloating(site, "OpenCL.std", name, resultType, dest, sources.head, opcode == OpenCL.Fabs)((_, value) =>
          evaluate(value)
        )
      )
    case OpenCL.Atan2 | OpenCL.Hypot =>
      count(s"OpenCL.std ${if (opcode == OpenCL.Hypot) "hypot" else "atan2"}", sources, 2)(
        atan2OrHypot(site, resultType, dest, sources(0), sources(1), opcode == OpenCL.Hypot)
      )
    case binaryOpcode if binaryNames.contains(binaryOpcode) =>
      val (name, evaluate) = binaryNames(binaryOpcode)
      count(s"OpenCL.std $name", sources, 2)(elementwise(site, resultType, dest, sources, name)(a =>
        evaluate(a(0), a(1))
      ))
    case OpenCL.Nan => count("OpenCL.std nan", sources, 1, "operand")(nan(site, resultType, dest, sources.head))
    case OpenCL.Pown | OpenCL.Rootn =>
      count(s"OpenCL.std ${if (opcode == OpenCL.Pown) "pown" else "rootn"}", sources, 2)(
        integerPower(site, resultType, dest, sources(0), sources(1), opcode == OpenCL.Rootn)
      )
    case OpenCL.Nextafter => count("OpenCL.std nextafter", sources, 2)(nextAfter(site, resultType, dest, sources))
    case OpenCL.SHadd     =>
      count("OpenCL.std hadd", sources, 2)(
        integerSame(
          site,
          resultType,
          dest,
          sources,
          "OpenCL.std hadd requires matching integer scalar/vector operand and result types"
        ) {
          width => (a, b) =>
            val left  = signExtend(a, width)
            val right = signExtend(b, width)
            ((left & right) + ((left ^ right) >> 1)) & mask(width)
        }
      )
    case integerOpcode if integerArithmetic(integerOpcode) => openCLInteger(site, resultType, dest, opcode, sources)
    case OpenCL.SAbs | OpenCL.UAbs                         =>
      count("OpenCL.std abs", sources, 1, "operand")(
        integerUnary(site, resultType, dest, sources.head, "abs") { (width, raw) =>
          val value = if (opcode == OpenCL.SAbs) signExtend(raw, width) else raw
          if (opcode == OpenCL.SAbs && value < 0L) -value else value
        }
      )
    case OpenCL.Clz =>
      count("OpenCL.std clz", sources, 1, "operand")(
        integerUnary(site, resultType, dest, sources.head, "clz") { (width, raw) =>
          val value = raw & mask(width)
          if (value == 0L) width.raw.toLong else (java.lang.Long.numberOfLeadingZeros(value) - (64 - width.raw)).toLong
        }
      )
    case OpenCL.Ctz =>
      count("OpenCL.std ctz", sources, 1, "operand")(
        integerUnary(site, resultType, dest, sources.head, "ctz") { (width, raw) =>
          val value = raw & mask(width)
          if (value == 0L) width.raw.toLong else java.lang.Long.numberOfTrailingZeros(value).toLong
        }
      )
    case OpenCL.Popcount =>
      count("OpenCL.std popcount", sources, 1, "operand")(
        integerUnary(site, resultType, dest, sources.head, "popcount")((width, raw) =>
          Bits.bitCount(raw, width).raw.toLong
        )
      )
    case common if common >= OpenCL.Fclamp && common <= OpenCL.Sign =>
      openCLCommon(site, resultType, dest, opcode, sources)
    case geometricOpcode if geometricOpcode >= OpenCL.Cross && geometricOpcode <= OpenCL.FastNormalize =>
      geometric(site, resultType, dest, opcode, sources)
    case OpenCL.VloadHalf | OpenCL.VloadHalfn | OpenCL.VloadaHalfn =>
      halfLoad(site, resultType, dest, sources, aligned = opcode == OpenCL.VloadaHalfn)
    case store if store >= OpenCL.VstoreHalf && store <= OpenCL.VstoreaHalfnR =>
      halfStore(
        site,
        sources,
        aligned = opcode >= OpenCL.VstoreaHalfn,
        rounded = opcode == OpenCL.VstoreHalfR || opcode == OpenCL.VstoreHalfnR || opcode == OpenCL.VstoreaHalfnR
      )
    case OpenCL.Vloadn                    => vectorLoad(site, resultType, dest, sources)
    case OpenCL.Vstoren                   => count("OpenCL.std vstoren", sources, 3)(vectorStore(site, sources))
    case OpenCL.Shuffle | OpenCL.Shuffle2 => shuffle(site, resultType, dest, sources, opcode == OpenCL.Shuffle2)
    case OpenCL.Bitselect => count("OpenCL.std bitselect", sources, 3)(bitselect(site, resultType, dest, sources))
    case OpenCL.Select    => count("OpenCL.std select", sources, 3)(select(site, resultType, dest, sources))
    case OpenCL.Prefetch  =>
      if (sources.isEmpty) Fail(Fault.InvalidRequest("OpenCL.std prefetch is missing its pointer operand"))
      else step((execution, _) => address(execution.values, sources.head).map(_ => ()))
    case _ => Fail(Fault.Unsupported(s"OpExtInst OpenCL.std opcode $opcode execution is not implemented"))
  }

  private def operand(value: Int): Operand = Operand(OperandKind.IdRef, Vec.nonEmpty(Word(value)))

  private def integerArithmetic(opcode: Int): Boolean =
    (opcode >= OpenCL.SAbsDiff && opcode <= OpenCL.UClamp && opcode != OpenCL.SHadd) ||
      (opcode >= OpenCL.SMadHi && opcode <= OpenCL.SMadSat) || (opcode >= OpenCL.SMulHi && opcode <= OpenCL.SUpsample) ||
      (opcode >= OpenCL.SMad24 && opcode <= OpenCL.UMul24) || (opcode >= OpenCL.UAbsDiff && opcode <= OpenCL.UMadHi)

  /** Integer operands and result with one shape; the kernel's result is masked to the result width. */
  private def integerSame(site: Site, resultType: Int, dest: Int, sources: Vec[Int], mismatch: NonEmpty[String])(
      kernel: (Int in 1 ~ 64) => (Long, Long) => Long
  ): Op = lowered(dataTypes(site, sources, _ => Fault.InvalidRequest(mismatch)).flatMap { types =>
    site.program.integerShape(resultType) match {
      case Some(shape @ (width, _)) if types.forall(typeId => site.program.integerShape(typeId).contains(shape)) =>
        val evaluate = kernel(width)
        Right(binary(site, resultType, dest, sources(0), sources(1))((a, b) => evaluate(a, b)))
      case _ => Left(Fault.InvalidRequest(mismatch))
    }
  })

  private def integerUnary(site: Site, resultType: Int, dest: Int, source: Int, name: String)(
      kernel: (Int in 1 ~ 64, Long) => Long
  ): Op = typed(site, source) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"OpenCL.std $name operand is a pointer"))
    case Typed.Data(_, sourceType) =>
      (site.program.integerShape(sourceType), site.program.integerShape(resultType)) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape @ (resultWidth, _))) if sourceShape == resultShape =>
          val bits = mask(resultWidth)
          unary(site, resultType, dest, source)(raw => kernel(width, raw) & bits)
        case _ => Fail(Fault.InvalidRequest(s"OpenCL.std $name requires matching integer scalar/vector types"))
      }
  }

  private def openCLInteger(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    import OpenCL.*
    val arity = opcode match {
      case `SClamp` | `UClamp` | `SMadHi` | `UMadHi` | `SMadSat` | `UMadSat` | `SMad24` | `UMad24` => 3
      case _                                                                                       => 2
    }
    val widening     = opcode == SUpsample || opcode == UUpsample
    val smallProduct = opcode == SMul24 || opcode == UMul24 || opcode == SMad24 || opcode == UMad24
    if (sources.size != arity)
      Fail(Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode expects $arity operands, received ${sources.size}"))
    else site.program.integerShape(resultType) match {
      case Some((width, count))
          if (width.raw == 8 || width.raw == 16 || width.raw == 32 || width.raw == 64) && (!widening || width.raw != 8) &&
            (!smallProduct || width.raw == 32) =>
        val inputWidth: Int in 1 ~ 64 = if (widening) (width.raw / 2).unsafe else width
        lowered(dataTypes(
          site,
          sources,
          _ => Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode requires matching integer scalar/vector types")
        ).flatMap {
          types =>
            Either.cond(
              types.forall(typeId => site.program.integerShape(typeId).contains(inputWidth -> count)), {
                val signed = opcode match {
                  case `SAbsDiff` | `SAddSat` | `SRhadd` | `SClamp` | `SMadHi` | `SMadSat` | `SMulHi` | `SSubSat` | `SUpsample` |
                      `SMad24` | `SMul24` => true
                  case _ => false
                }
                val productWidth: Int in 1 ~ 64                   = if (smallProduct) 24 else inputWidth
                def integer(raw: Long, bits: Int in 1 ~ 64): Long =
                  if (signed) signExtend(raw, bits) else raw & mask(bits)
                val result          = mask(width)
                val kernel: Kernel3 = { (a, b, c) =>
                  val left       = integer(a, productWidth)
                  val right      = integer(b, productWidth)
                  val third      = if (arity == 3) integer(c, inputWidth) else 0L
                  val calculated = opcode match {
                    case `SAbsDiff` => if (left >= right) left - right else right - left
                    case `UAbsDiff` =>
                      if (java.lang.Long.compareUnsigned(left, right) >= 0) left - right else right - left
                    case `SAddSat` | `UAddSat` => IntegerArithmetic.addSaturated(left, right, inputWidth, signed)
                    case `SSubSat` | `USubSat` => IntegerArithmetic.subtractSaturated(left, right, inputWidth, signed)
                    case `SClamp`              => left.max(right).min(third)
                    case `UClamp`              =>
                      val lower = if (java.lang.Long.compareUnsigned(left, right) < 0) right else left
                      if (java.lang.Long.compareUnsigned(lower, third) > 0) third else lower
                    case `UHadd`             => (left & right) + ((left ^ right) >>> 1)
                    case `SRhadd`            => (left & right) + ((left ^ right) >> 1) + ((left ^ right) & 1L)
                    case `URhadd`            => (left & right) + ((left ^ right) >>> 1) + ((left ^ right) & 1L)
                    case `SMulHi` | `UMulHi` => IntegerArithmetic.multiplyHigh(left, right, inputWidth, signed)
                    case `SMadHi` | `UMadHi` => IntegerArithmetic.multiplyHigh(left, right, inputWidth, signed) + third
                    case `SMadSat` | `UMadSat` =>
                      IntegerArithmetic.multiplyAddSaturated(left, right, third, inputWidth, signed)
                    case `Rotate` =>
                      val shift = right.toInt & (width.raw - 1)
                      if (shift == 0) left else (left << shift) | (left >>> (width.raw - shift))
                    case `SUpsample` | `UUpsample` => (left << inputWidth.raw) | (right & mask(inputWidth))
                    case `SMul24` | `UMul24`       => left * right
                    case _                         => left * right + third
                  }
                  calculated & result
                }
                if (arity == 3) ternary(site, resultType, dest, sources(0), sources(1), sources(2))(kernel)
                else binary(site, resultType, dest, sources(0), sources(1))((a, b) => kernel(a, b, 0L))
              },
              Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode requires matching integer scalar/vector types")
            )
        })
      case _ =>
        Fail(Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode requires an 8, 16, 32 or 64-bit integer result"))
    }
  }

  private def openCLCommon(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val (name, arity) = opcode match {
      case OpenCL.Fclamp     => "clamp"      -> 3
      case OpenCL.Degrees    => "degrees"    -> 1
      case OpenCL.FmaxCommon => "fmax"       -> 2
      case OpenCL.FminCommon => "fmin"       -> 2
      case OpenCL.Mix        => "mix"        -> 3
      case OpenCL.Radians    => "radians"    -> 1
      case OpenCL.Step       => "step"       -> 2
      case OpenCL.Smoothstep => "smoothstep" -> 3
      case _                 => "sign"       -> 1
    }
    val width = site.program.floatingShape(resultType).map(_._1).getOrElse(64: Int in (16, 32, 64))
    count(s"OpenCL.std $name", sources, arity)(elementwise(site, resultType, dest, sources, name) { operands =>
      opcode match {
        case OpenCL.Fclamp     => floatingMinimum(floatingMaximum(operands(0), operands(1)), operands(2))
        case OpenCL.Degrees    => operands(0) * (180.0 / Math.PI)
        case OpenCL.FmaxCommon => floatingMaximum(operands(0), operands(1))
        case OpenCL.FminCommon => floatingMinimum(operands(0), operands(1))
        case OpenCL.Mix        => operands(0) + (operands(1) - operands(0)) * operands(2)
        case OpenCL.Radians    => operands(0) * (Math.PI / 180.0)
        case OpenCL.Step       => if (operands(1) < operands(0)) 0.0 else 1.0
        case OpenCL.Smoothstep =>
          def round(value: Double): Double = toDouble(width, fromDouble(width, value))
          val ratio                        = round(round(operands(2) - operands(0)) / round(operands(1) - operands(0)))
          val t                            = floatingMinimum(floatingMaximum(ratio, 0.0), 1.0)
          round(t * t) * round(3.0 - round(2.0 * t))
        case _ => if (operands(0) > 0.0) 1.0 else if (operands(0) < 0.0) -1.0 else 0.0
      }
    })
  }

  private def magnitude(site: Site, resultType: Int, dest: Int, sources: Vec[Int], maximum: Boolean): Op =
    sameFloating(
      site,
      resultType,
      dest,
      sources,
      "OpenCL.std magnitude selection requires matching floating-point types",
      "OpenCL.std magnitude selection operands are not floating-point"
    ) { width => operands =>
      val left     = toDouble(width, operands(0))
      val right    = toDouble(width, operands(1))
      val selected =
        if (Math.abs(left) > Math.abs(right)) { if (maximum) left else right }
        else if (Math.abs(right) > Math.abs(left)) { if (maximum) right else left }
        else if (maximum) floatingMaximum(left, right)
        else floatingMinimum(left, right)
      fromDouble(width, selected)
    }

  private def pow(site: Site, resultType: Int, dest: Int, left: Int, right: Int): Op =
    sameFloating(
      site,
      resultType,
      dest,
      Vec(left, right),
      "OpenCL.std pow requires matching floating-point types",
      "OpenCL.std pow operands are not floating-point"
    )(width => operands => fromDouble(width, Math.pow(toDouble(width, operands(0)), toDouble(width, operands(1)))))

  private def atan2OrHypot(site: Site, resultType: Int, dest: Int, left: Int, right: Int, hypot: Boolean): Op =
    sameFloating(
      site,
      resultType,
      dest,
      Vec(left, right),
      s"OpenCL.std ${if (hypot) "hypot" else "atan2"} requires matching floating-point scalar/vector types",
      "OpenCL.std operands are not floating-point"
    ) { width => operands =>
      val a = toDouble(width, operands(0))
      val b = toDouble(width, operands(1))
      fromDouble(width, if (hypot) Math.hypot(a, b) else Math.atan2(a, b))
    }

  private def mad(site: Site, resultType: Int, dest: Int, sources: Vec[Int], fused: Boolean): Op = {
    val name = if (fused) "fma" else "mad"
    sameFloating(
      site,
      resultType,
      dest,
      sources,
      s"OpenCL.std $name requires matching floating-point types",
      s"OpenCL.std $name operands are not floating-point"
    ) { width => operands =>
      if (fused) IEEE754.fusedMultiplyAdd(operands(0), operands(1), operands(2), width)
      else IEEE754.add(IEEE754.multiply(operands(0), operands(1), width), operands(2), width)
    }
  }

  private def ldexp(
      site: Site,
      resultType: Int,
      dest: Int,
      source: Int,
      exponentValue: Int,
      glsl: Boolean = false
  ): Op = {
    val mismatch: NonEmpty[String] =
      if (glsl) "GLSL Ldexp requires matching floating values and integer exponents"
      else "OpenCL.std ldexp requires matching floating values and i32 exponents"
    lowered(dataTypes(
      site,
      Vec(source, exponentValue),
      _ => Fault.InvalidRequest("OpenCL.std ldexp operands are not value types")
    ).flatMap {
      types =>
        (
          site.program.floatingShape(types(0)),
          site.program.floatingShape(resultType),
          site.program.integerShape(types(1))
        ) match {
          case (Some((width, count)), Some((resultWidth, resultCount)), Some((exponentWidth, exponentCount)))
              if width == resultWidth && count == resultCount && (glsl || exponentWidth.raw == 32) && exponentCount == count =>
            Right(binary(site, resultType, dest, source, exponentValue) { (raw, power) =>
              val scale =
                Math.max(Int.MinValue.toLong, Math.min(Int.MaxValue.toLong, signExtend(power, exponentWidth))).toInt
              fromDouble(width, Math.scalb(toDouble(width, raw), scale))
            })
          case _ => Left(Fault.InvalidRequest(mismatch))
        }
    })
  }

  private def nan(site: Site, resultType: Int, dest: Int, source: Int): Op = typed(site, source) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std nan payload is a pointer"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(resultType), site.program.integerShape(sourceType)) match {
        case (Some((width, count)), Some((_, sourceCount))) if sourceCount == count || sourceCount == 1 =>
          val (quiet, payload) = width.raw match {
            case 16 => (0x7e00L, 0x1ffL)
            case 32 => (0x7fc00000L, 0x3fffffL)
            case _  => (0x7ff8000000000000L, 0x7ffffffffffffL)
          }
          gather(Vec(source)) { (execution, _, operands) =>
            Right(write(
              execution.values,
              dest,
              resultType,
              Array.tabulate(count)(lane => quiet | (operands(0)(lane % sourceCount) & payload))
            ))
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std nan requires a floating result and integer payload"))
      }
  }

  private def integerPower(site: Site, resultType: Int, dest: Int, source: Int, power: Int, root: Boolean): Op =
    lowered(dataTypes(
      site,
      Vec(source, power),
      _ => Fault.InvalidRequest("OpenCL.std integer-power operands are not value types")
    ).flatMap {
      types =>
        (
          site.program.floatingShape(resultType),
          site.program.floatingShape(types(0)),
          site.program.integerShape(types(1))
        ) match {
          case (Some((width, count)), Some((sourceWidth, sourceCount)), Some((exponentWidth, exponentCount)))
              if width == sourceWidth && count == sourceCount && (exponentCount == count || exponentCount == 1) =>
            Right(binary(site, resultType, dest, source, power) { (raw, exponentRaw) =>
              val value     = toDouble(width, raw)
              val n         = signExtend(exponentRaw, exponentWidth).toInt
              val negative  = (raw & ArithmeticOps.signMask(width)) != 0L
              val evaluated =
                if (!root) Math.pow(value, n.toDouble)
                else if (n == 0) Double.NaN
                else if (value == 0.0 && negative && (n & 1) != 0) { if (n < 0) Double.NegativeInfinity else -0.0 }
                else if (value < 0.0 && (n & 1) != 0) -Math.pow(-value, 1.0 / n.toDouble)
                else if (value < 0.0) Double.NaN
                else Math.pow(value, 1.0 / n.toDouble)
              fromDouble(width, evaluated)
            })
          case _ =>
            Left(Fault.InvalidRequest(
              s"OpenCL.std ${if (root) "rootn" else "pown"} requires floating values and integer exponents"
            ))
        }
    })

  private def nextAfter(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op =
    sameFloating(
      site,
      resultType,
      dest,
      sources,
      "OpenCL.std nextafter requires matching floating-point scalar/vector operand and result types",
      "OpenCL.std nextafter operands are not floating-point"
    ) { width => operands =>
      width.raw match {
        case 16 =>
          val from = toDouble(width, operands(0))
          val to   = toDouble(width, operands(1))
          if (from.isNaN || to.isNaN) fromDouble(width, Double.NaN)
          else if (from == to) operands(1)
          else if (from == 0.0) { if (to < 0.0) 0x8001L else 1L }
          else operands(0) + (if ((to > from) == (from > 0.0)) 1L else -1L)
        case 32 =>
          IEEE754.floatToBinary32(Math.nextAfter(
            IEEE754.binary32ToFloat(Binary32.low(operands(0))),
            IEEE754.binary32ToFloat(Binary32.low(operands(1))).toDouble
          )).toLong
        case _ => IEEE754.doubleToBinary64(Math.nextAfter(
            IEEE754.binary64ToDouble(operands(0)),
            IEEE754.binary64ToDouble(operands(1))
          ))
      }
    }

  private def ilogb(site: Site, resultType: Int, dest: Int, source: Int): Op = typed(site, source) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std ilogb input is not floating-point"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(sourceType), site.program.integerShape(resultType)) match {
        case (Some((width, count)), Some((resultWidth, resultCount)))
            if resultWidth.raw == 32 && count == resultCount =>
          unary(site, resultType, dest, source) { raw =>
            val x = toDouble(width, raw)
            (if (x == 0.0) Int.MinValue else if (!x.isFinite) Int.MaxValue else exponent(x)).toLong & 0xffffffffL
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std ilogb requires floating input and matching i32 result lanes"))
      }
  }

  private def pointerMath(site: Site, resultType: Int, dest: Int, source: Int, output: Int, opcode: Int): Op = {
    val program = site.program
    val shapes  = typed(site, source) match {
      case Typed.Undefined(value) => Left(undefined(value))
      case Typed.Opaque(_, _)     => Left(Fault.InvalidRequest("OpenCL.std pointer math source is not floating-point"))
      case Typed.Data(_, sourceType) =>
        (program.floatingShape(sourceType), program.floatingShape(resultType)) match {
          case (Some(shape), Some(result)) if shape == result => Right(shape)
          case _                                              =>
            Left(Fault.InvalidRequest("OpenCL.std pointer math requires matching floating-point scalar/vector types"))
        }
    }
    shapes match {
      case Left(error)           => Fail(error)
      case Right((width, count)) =>
        val integerOutput = opcode == OpenCL.Frexp || opcode == OpenCL.LgammaR
        step { (execution, memory) =>
          val values = execution.values
          (lanes(values, source), address(values, output)) match {
            case (Left(error), _)                     => Left(error)
            case (_, Left(error))                     => Left(error)
            case (Right(sourceLanes), Right(pointer)) =>
              val validPointer =
                if (integerOutput) program.integerShape(pointer.pointee).contains((32, count))
                else program.floatingShape(pointer.pointee).contains((width, count))
              if (!validPointer) Left(Fault.InvalidRequest("OpenCL.std pointer math output type does not match input"))
              else {
                val paired = sourceLanes.map { raw =>
                  val x                = toDouble(width, raw)
                  val (result, stored) = opcode match {
                    case OpenCL.Sincos  => (Math.sin(x), fromDouble(width, Math.cos(x)))
                    case OpenCL.LgammaR => (OpenCLMath.lgamma(x), OpenCLMath.gammaSign(x).toLong & 0xffffffffL)
                    case OpenCL.Frexp   =>
                      val power = if (x == 0.0 || !x.isFinite) 0 else exponent(x) + 1
                      (Math.scalb(x, -power), power.toLong & 0xffffffffL)
                    case OpenCL.Modf =>
                      val integral = if (x < 0.0) Math.ceil(x) else Math.floor(x)
                      (
                        if (x.isInfinite) Math.copySign(0.0, x) else Math.copySign(x - integral, x),
                        fromDouble(width, integral)
                      )
                    case _ =>
                      val integral = Math.floor(x)
                      val maximum  = width.raw match {
                        case 16 => 1.0 - Math.scalb(1.0, -11)
                        case 32 => Math.nextDown(1.0f).toDouble
                        case _  => Math.nextDown(1.0)
                      }
                      val fraction =
                        if (x.isNaN) x
                        else if (x == 0.0 || x.isInfinite) Math.copySign(0.0, x) else Math.min(x - integral, maximum)
                      (fraction, fromDouble(width, integral))
                  }
                  (fromDouble(width, result), stored)
                }
                val bytes = Array.ofDim[Byte](program.typeSize(pointer.pointee).raw)
                program.writeLeaves(pointer.pointee, paired.map(_._2), bytes, 0L)
                memory.write(pointer, bytes).map(_ => write(values, dest, resultType, paired.map(_._1)))
              }
          }
        }
    }
  }

  private def remquo(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op = {
    val program = site.program
    program.floatingShape(resultType) match {
      case Some(shape @ (width, count))
          if sources.take(2).forall(source => program.floatingShape(program.valueType(source)).contains(shape)) =>
        step { (execution, memory) =>
          val values = execution.values
          (lanes(values, sources(0)), lanes(values, sources(1)), address(values, sources(2))) match {
            case (Left(error), _, _) => Left(error)
            case (_, Left(error), _) => Left(error)
            case (_, _, Left(error)) => Left(error)
            case (Right(left), Right(right), Right(pointer))
                if program.integerShape(pointer.pointee).contains((32, count)) =>
              val remainders = Array.tabulate(count)(lane =>
                fromDouble(width, Math.IEEEremainder(toDouble(width, left(lane)), toDouble(width, right(lane))))
              )
              val quotients = Array.tabulate(count)(lane =>
                IEEE754.remainderQuotient(toDouble(width, left(lane)), toDouble(width, right(lane))).toLong
              )
              val bytes = Array.ofDim[Byte](program.typeSize(pointer.pointee).raw)
              program.writeLeaves(pointer.pointee, quotients, bytes, 0L)
              memory.write(pointer, bytes).map(_ => write(values, dest, resultType, remainders))
            case _ =>
              Left(Fault.InvalidRequest("OpenCL.std remquo requires matching floating operands and i32 pointer lanes"))
          }
        }
      case _ =>
        sources.take(2).find(source => program.valueType(source) < 0) match {
          case Some(source) => Fail(undefined(source))
          case None         =>
            Fail(Fault.InvalidRequest("OpenCL.std remquo requires matching floating operands and i32 pointer lanes"))
        }
    }
  }

  private def geometric(site: Site, resultType: Int, dest: Int, opcode: Int, sources: Vec[Int]): Op = {
    val name = opcode match {
      case OpenCL.Cross        => "cross"
      case OpenCL.Distance     => "distance"
      case OpenCL.Length       => "length"
      case OpenCL.Normalize    => "normalize"
      case OpenCL.FastDistance => "fast_distance"
      case OpenCL.FastLength   => "fast_length"
      case _                   => "fast_normalize"
    }
    val arity = if (opcode == OpenCL.Cross || opcode == OpenCL.Distance || opcode == OpenCL.FastDistance) 2 else 1
    count(s"OpenCL.std $name", sources, arity)(
      lowered(dataTypes(site, sources, _ => Fault.InvalidRequest(s"OpenCL.std $name operand is a pointer")).flatMap {
        types =>
          val shapes = types.map(site.program.floatingShape)
          Either.cond(
            shapes.forall(_.isDefined) && shapes.forall(_.map(_._1) == shapes.head.map(_._1)) &&
              shapes.forall(_.map(_._2) == shapes.head.map(_._2)),
            shapes.head,
            Fault.InvalidRequest(s"OpenCL.std $name operands have incompatible types")
          ).flatMap(_.toRight(Fault.InvalidRequest(s"OpenCL.std $name has no floating-point operands"))).flatMap {
            (width, count) =>
              val resultShape                               = site.program.floatingShape(resultType)
              def magnitude(operand: Array[Double]): Double = operand.foldLeft(0.0)(Math.hypot(_, _))
              opcode match {
                case OpenCL.Cross =>
                  if (count != 3 && count != 4)
                    Left(Fault.InvalidRequest("OpenCL.std cross requires 3- or 4-component vectors"))
                  else if (!resultShape.contains((width, count)))
                    Left(Fault.InvalidRequest("OpenCL.std cross result type does not match its operands"))
                  else
                    Right(gather(sources) { (execution, _, operands) =>
                      val a      = operands(0).map(toDouble(width, _))
                      val b      = operands(1).map(toDouble(width, _))
                      val result = Array(
                        fromDouble(width, a(1) * b(2) - a(2) * b(1)),
                        fromDouble(width, a(2) * b(0) - a(0) * b(2)),
                        fromDouble(width, a(0) * b(1) - a(1) * b(0))
                      ) ++ Option.when(count == 4)(fromDouble(width, 0.0))
                      Right(write(execution.values, dest, resultType, result))
                    })
                case OpenCL.Distance | OpenCL.FastDistance | OpenCL.Length | OpenCL.FastLength =>
                  if (!resultShape.contains((width, 1)))
                    Left(Fault.InvalidRequest(s"OpenCL.std $name result is not a matching floating scalar"))
                  else
                    Right(gather(sources) { (execution, _, operands) =>
                      val a     = operands(0).map(toDouble(width, _))
                      val value =
                        if (arity == 2) {
                          val b = operands(1).map(toDouble(width, _))
                          a.indices.foldLeft(0.0)((distance, lane) => Math.hypot(distance, a(lane) - b(lane)))
                        } else magnitude(a)
                      Right(execution.values.updateScalar(dest, resultType, fromDouble(width, value)))
                    })
                case _ =>
                  if (!resultShape.contains((width, count)))
                    Left(Fault.InvalidRequest(s"OpenCL.std $name result type does not match its operand"))
                  else
                    Right(gather(sources) { (execution, _, operands) =>
                      val a        = operands(0).map(toDouble(width, _))
                      val scale    = a.foldLeft(0.0)((maximum, value) => math.max(maximum, math.abs(value)))
                      val unscaled = scale == 0.0 || !scale.isFinite
                      val length   = if (unscaled) magnitude(a) else magnitude(a.map(_ / scale))
                      val result = a.map(value => fromDouble(width, (if (unscaled) value else value / scale) / length))
                      Right(write(execution.values, dest, resultType, result))
                    })
              }
          }
      })
    )
  }

  private def halfLoad(site: Site, resultType: Int, dest: Int, sources: Vec[Int], aligned: Boolean): Op = {
    val vector = sources.size == 3
    if ((!vector && sources.size != 2) || (vector && sources.size != 3))
      Fail(Fault.InvalidRequest("OpenCL.std half load has an invalid operand count"))
    else site.program.floatingShape(resultType) match {
      case Some((width, count)) if width.raw == 32 && (!vector || unsignedLiteral(sources(2)) == count.toLong) =>
        val stride = if (aligned && count == 3) 4 else count
        step { (execution, memory) =>
          val values = execution.values
          for {
            offset  <- scalar(values, sources(0))
            pointer <- address(values, sources(1))
            base = pointer.address + Math.multiplyExact(offset, stride.toLong * 2L)
            result <- (0 until count).foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(Array.ofDim[Long](count))) {
              (result, lane) =>
                result.flatMap(data =>
                  memory.readScalar(pointer.copy(address = base + lane.toLong * 2L), 2).map { half =>
                    data(lane) = IEEE754.floatToBinary32(IEEE754.binary16ToFloat(Binary16.low(half))).toLong
                    data
                  }
                )
            }
          } yield write(values, dest, resultType, result)
        }
      case _ => Fail(Fault.InvalidRequest("OpenCL.std half load requires a float result and half pointer"))
    }
  }

  /** A literal OpenCL.std operand; vector widths are encoded as literal words, not ids. */
  private def unsignedLiteral(word: Int): Long = word.toLong & 0xffffffffL

  private def halfStore(site: Site, sources: Vec[Int], aligned: Boolean, rounded: Boolean): Op = {
    val expected = if (rounded) 4 else 3
    if (sources.size != expected) Fail(Fault.InvalidRequest(s"OpenCL.std half store expects $expected operands"))
    else typed(site, sources(0)) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std half store operands are not value types"))
      case Typed.Data(_, sourceType) =>
        site.program.floatingShape(sourceType) match {
          case Some((width, count)) if width.raw == 32 || width.raw == 64 =>
            val stride   = if (aligned && count == 3) 4 else count
            val rounding =
              if (rounded) Op.rounding(sources(3), IEEE754.Rounding.NearestEven) else IEEE754.Rounding.NearestEven
            step { (execution, memory) =>
              val values = execution.values
              for {
                data    <- lanes(values, sources(0))
                offset  <- scalar(values, sources(1))
                pointer <- address(values, sources(2))
                base = pointer.address + Math.multiplyExact(offset, stride.toLong * 2L)
                _ <- (0 until count).foldLeft[Either[Fault, Unit]](Right(())) { (result, lane) =>
                  result.flatMap(_ =>
                    memory.writeScalar(
                      pointer.copy(address = base + lane.toLong * 2L),
                      IEEE754.fromDouble(toDouble(width, data(lane)), 16, rounding),
                      2
                    )
                  )
                }
              } yield ()
            }
          case _ => Fail(Fault.InvalidRequest("OpenCL.std half store source is not float or double"))
        }
    }
  }

  private def vectorLoad(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op =
    if (sources.size != 3) Fail(Fault.InvalidRequest(s"OpenCL.std vloadn expects 3 operands, received ${sources.size}"))
    else site.program.runtimeType(resultType) match {
      case Some(RuntimeType.VectorType(component, count)) if unsignedLiteral(sources(2)) == count.raw.toLong =>
        site.program.scalarMemoryWidth(component) match {
          case Left(error)        => Fail(error)
          case Right(elementSize) =>
            step { (execution, memory) =>
              val values = execution.values
              for {
                offset  <- scalar(values, sources(0))
                pointer <- address(values, sources(1))
                base = pointer.address +
                  Math.multiplyExact(Math.multiplyExact(offset, count.raw.toLong), elementSize.raw.toLong)
                result <- (0 until count.raw).foldLeft[AddressSpace.ReadResult[Array[Long]]](Right(
                  Array.ofDim[Long](count.raw)
                )) {
                  (result, index) =>
                    result.flatMap(data =>
                      memory.readScalar(
                        pointer.copy(address = base + index.toLong * elementSize.raw, pointee = component),
                        elementSize
                      ).map { value =>
                        data(index) = value
                        data
                      }
                    )
                }
              } yield write(values, dest, resultType, result)
            }
        }
      case Some(RuntimeType.VectorType(_, count)) =>
        Fail(Fault.InvalidRequest(
          s"OpenCL.std vloadn literal vector width ${unsignedLiteral(sources(2))} does not match $count"
        ))
      case Some(_) => Fail(Fault.InvalidRequest("OpenCL.std vloadn result type is not a vector"))
      case None    => Fail(Fault.InvalidRequest(s"OpenCL.std vloadn result type %$resultType is undefined"))
    }

  private def vectorStore(site: Site, sources: Vec[Int]): Op = typed(site, sources(0)) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std vstoren source is not numeric"))
    case Typed.Data(_, sourceType) =>
      site.program.runtimeType(sourceType) match {
        case Some(RuntimeType.VectorType(component, count)) =>
          site.program.scalarMemoryWidth(component) match {
            case Left(error)        => Fail(error)
            case Right(elementSize) =>
              step { (execution, memory) =>
                val values = execution.values
                for {
                  data    <- lanes(values, sources(0))
                  offset  <- scalar(values, sources(1))
                  pointer <- address(values, sources(2))
                  _       <- Either.cond(
                    data.length == count.raw,
                    (),
                    Fault.InvalidRequest(s"OpenCL.std vstoren has ${data.length} lanes, expected $count")
                  )
                  base = pointer.address +
                    Math.multiplyExact(Math.multiplyExact(offset, count.raw.toLong), elementSize.raw.toLong)
                  _ <- (0 until count.raw).foldLeft[Either[Fault, Unit]](Right(())) { (result, index) =>
                    result.flatMap(_ =>
                      memory.writeScalar(
                        pointer.copy(address = base + index.toLong * elementSize.raw, pointee = component),
                        data(index),
                        elementSize
                      )
                    )
                  }
                } yield ()
              }
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std vstoren source is not a vector"))
      }
  }

  private def shuffle(site: Site, resultType: Int, dest: Int, sources: Vec[Int], twoInputs: Boolean): Op = {
    val expected = if (twoInputs) 3 else 2
    if (sources.size != expected) Fail(Fault.InvalidRequest(s"OpenCL.std shuffle expects $expected operands"))
    else {
      val program   = site.program
      val maskIndex = if (twoInputs) 2 else 1
      (typed(site, sources(0)), typed(site, sources(maskIndex))) match {
        case (Typed.Undefined(value), _)                         => Fail(undefined(value))
        case (_, Typed.Undefined(value))                         => Fail(undefined(value))
        case (Typed.Data(_, firstType), Typed.Data(_, maskType)) =>
          val secondShape = if (twoInputs) program.numericShape(program.valueType(sources(1))) else None
          (program.numericShape(firstType), program.integerShape(maskType), program.numericShape(resultType)) match {
            case (Some((width, sourceCount)), Some((_, maskCount)), Some((resultWidth, resultCount)))
                if width == resultWidth && maskCount == resultCount && (!twoInputs || secondShape.contains((
                  width,
                  sourceCount
                ))) =>
              gather(if (twoInputs) sources else Vec(sources(0), sources(1))) { (execution, _, operands) =>
                val combined = if (twoInputs) operands(0) ++ operands(1) else operands(0)
                val masks    = operands(maskIndex)
                Right(write(
                  execution.values,
                  dest,
                  resultType,
                  Array.tabulate(resultCount)(lane =>
                    combined(java.lang.Long.remainderUnsigned(masks(lane), combined.length.toLong).toInt)
                  )
                ))
              }
            case _ => Fail(Fault.InvalidRequest("OpenCL.std shuffle operand types are incompatible"))
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std shuffle operands are not value types"))
      }
    }
  }

  private def bitselect(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op = {
    val invalid = Fault.InvalidRequest("OpenCL.std bitselect operands are undefined or pointer values")
    lowered(dataTypes(site, sources, _ => invalid).left.map(_ => invalid).flatMap { types =>
      val shape = site.program.numericShape(resultType)
      Either.cond(
        types.forall(typeId => site.program.numericShape(typeId) == shape),
        ternary(site, resultType, dest, sources(0), sources(1), sources(2))((a, b, c) => (a & ~c) | (b & c)),
        Fault.InvalidRequest("OpenCL.std bitselect operands must have matching numeric shapes")
      )
    })
  }

  private def select(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op = {
    val invalid = Fault.InvalidRequest("OpenCL.std select operands are undefined or pointer values")
    lowered(dataTypes(site, sources, _ => invalid).left.map(_ => invalid).flatMap { types =>
      (
        site.program.numericShape(resultType),
        site.program.numericShape(types(0)),
        site.program.numericShape(types(1)),
        site.program.integerShape(types(2))
      ) match {
        case (Some(resultShape @ (_, count)), Some(aShape), Some(bShape), Some((conditionWidth, conditionCount)))
            if resultShape == aShape && resultShape == bShape && (conditionCount == count || conditionCount == 1) =>
          val sign            = 1L << (conditionWidth.raw - 1)
          val scalarCondition = count == 1 && conditionCount == 1
          Right(ternary(site, resultType, dest, sources(0), sources(1), sources(2)) { (a, b, condition) =>
            if (if (scalarCondition) condition != 0L else (condition & sign) != 0L) b else a
          })
        case _ => Left(Fault.InvalidRequest("OpenCL.std select operand types are incompatible"))
      }
    })
  }

  private def printf(site: Site, resultType: Int, dest: Int, sources: Vec[Int]): Op =
    if (sources.isEmpty) Fail(Fault.InvalidRequest("OpenCL.std printf requires a format string"))
    else
      Dynamic { (execution, memory) =>
        val values = execution.values
        val result = for {
          formatPointer <- values.get(sources.head) match {
            case Some(VMValue.Address(_, pointer)) => Right(pointer)
            case Some(_) => Left(Fault.InvalidRequest("OpenCL.std printf format is not a pointer"))
            case None    => Left(undefined(sources.head))
          }
          format      <- readCString(memory, formatPointer)
          conversions <- printfConversions(format)
          _           <- Either.cond(
            conversions.size <= sources.size - 1,
            (),
            Fault.InvalidRequest(
              s"OpenCL.std printf format expects at least ${conversions.size} arguments, received ${sources.size - 1}"
            )
          )
          arguments <- conversions.zipWithIndex.foldLeft[AddressSpace.ReadResult[Vector[Event.PrintArgument]]](Right(
            Vector.empty
          )) {
            case (result, (conversion, index)) =>
              result.flatMap { found =>
                val source = sources(index + 1)
                values.get(source) match {
                  case Some(VMValue.Bits(typeId, lanes)) =>
                    site.program.integerShape(typeId) match {
                      case Some((width, _)) => Right(found :+ Event.PrintArgument.Integer(Vec.from(lanes), width))
                      case None             => site.program.floatingShape(typeId) match {
                          case Some((width, _)) => Right(found :+ Event.PrintArgument.Floating(Vec.from(lanes), width))
                          case None             =>
                            Left(Fault.InvalidRequest(
                              s"OpenCL.std printf argument ${index + 1} is not an integer or floating-point value"
                            ))
                        }
                    }
                  case Some(VMValue.Address(_, pointer)) if conversion == 's' =>
                    readCString(memory, pointer).map(text => found :+ Event.PrintArgument.Text(text))
                  case Some(VMValue.Address(_, pointer)) =>
                    Right(found :+ Event.PrintArgument.Pointer(pointer.address))
                  case None => Left(undefined(source))
                }
              }
          }
          _ <- memory.handler(Event.Print(format, Vec.from(arguments)))
          _ <- site.program.integerShape(resultType) match {
            case Some((_, 1)) => Right(values.updateScalar(dest, resultType, 0L))
            case _            => Left(Fault.InvalidRequest("OpenCL.std printf result is not an integer scalar"))
          }
        } yield execution.pc += 1
        result
      }

  // C strings read byte by byte so a terminator ends the read before any later fault.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def readCString(memory: MemoryState, pointer: Pointer): AddressSpace.ReadResult[String] = {
    val bytes = mutable.ArrayBuffer.empty[Byte]
    var index = 0L
    while (index < 1024L * 1024L) {
      memory.read(pointer.copy(address = pointer.address + index), 1) match {
        case Left(error)                   => return Left(error)
        case Right(value) if value(0) == 0 => return Right(String(bytes.toArray, StandardCharsets.UTF_8))
        case Right(value)                  => bytes += value(0)
      }
      index += 1L
    }
    Left(Fault.InvalidRequest("OpenCL.std printf string exceeds 1 MiB"))
  }

  // Conversion scanning advances a cursor through flags, width, precision and length modifiers.
  @SuppressWarnings(Array("scalafix:DisableSyntax.return", "scalafix:DisableSyntax.var"))
  private def printfConversions(format: String): Either[Fault, Vec[Char]] = {
    val result = mutable.ArrayBuffer.empty[Char]
    var index  = 0
    while (index < format.length)
      if (format(index) != '%') index += 1
      else if (index + 1 < format.length && format(index + 1) == '%') index += 2
      else {
        index += 1
        while (index < format.length && "-+ #0".contains(format(index))) index += 1
        if (index < format.length && format(index) == '*')
          return Left(Fault.Unsupported("OpenCL.std printf dynamic field widths are not implemented"))
        while (index < format.length && format(index).isDigit) index += 1
        if (index < format.length && format(index) == '.') {
          index += 1
          if (index < format.length && format(index) == '*')
            return Left(Fault.Unsupported("OpenCL.std printf dynamic precision is not implemented"))
          while (index < format.length && format(index).isDigit) index += 1
        }
        if (index < format.length && format(index) == 'v') {
          index += 1
          while (index < format.length && format(index).isDigit) index += 1
        }
        while (index < format.length && (format(index) == 'h' || format(index) == 'l')) index += 1
        if (index >= format.length)
          return Left(Fault.InvalidRequest("OpenCL.std printf format ends within a conversion"))
        result += format(index).toLower
        index += 1
      }
    Right(Vec.from(result))
  }
}
