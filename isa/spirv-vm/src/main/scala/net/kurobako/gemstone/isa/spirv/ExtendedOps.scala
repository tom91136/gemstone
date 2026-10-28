package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import LaneOps.*
import MemoryOps.*
import Op.*
import OpBuilder.*
import VMInvocation.*
import VMMemory.*
import VMProgram.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.vm.*

/** GLSL.std.450 and OpenCL.std extended instructions; the instruction set and opcode resolve when the site lowers. Lane
  * semantics are staged from [[GLSLSemantics]] and [[OpenCLSemantics]]; the instructions whose results mix lanes, such
  * as cross products and normalisation, compute over whole vectors here.
  */
private object ExtendedOps {
  import Operand.*

  def lower(using site: Site): PartialFunction[Instruction, Op] = {
    case Instruction.ExtInstWithForwardRefsKHR(set = set) =>
      site.program.extendedInstructionSets.get(id(set)) match {
        case Some(name) if nonSemantic(name) => Next
        case _                               =>
          Fail(Fault.InvalidRequest("OpExtInstWithForwardRefsKHR must import from a non-semantic instruction set"))
      }
    case instruction @ Instruction.ExtInst(resultType, dest, set, extended, sources) =>
      given Instruction = instruction
      site.program.extendedInstructionSets.get(id(set)) match {
        case None       => Fail(Fault.InvalidRequest(s"OpExtInst references undefined import %${id(set)}"))
        case Some(name) =>
          ExtendedSet.of(name) match {
            case Some(ExtendedSet.GLSL) =>
              glsl(
                ResultIds(resultType, dest),
                SPIRVStaged.extendedOpcode(instruction, openCL = false),
                sources.map(id(_))
              )
            case Some(ExtendedSet.OpenCL) =>
              openCL(
                ResultIds(resultType, dest),
                SPIRVStaged.extendedOpcode(instruction, openCL = true),
                sources.map(id(_))
              )
            case None if nonSemantic(name) => Next
            case None                      =>
              Fail(Fault.Unsupported(s"OpExtInst $name opcode ${id(extended)} execution is not implemented"))
          }
      }
  }

  /** Sets whose instructions have no semantic impact and can be removed: NonSemantic.* (SPIR-V 3.3.4) and the debug
    * information sets.
    */
  def nonSemantic(set: String): Boolean =
    set.startsWith("NonSemantic.") || set == "OpenCL.DebugInfo.100" || set == "DebugInfo"

  private def count(name: String, sources: Vec[Int], expected: Int, plural: String = "operands")(op: => Op): Op =
    if (sources.size != expected)
      Fail(Fault.InvalidRequest(s"$name expects $expected $plural, received ${sources.size}"))
    else op

  /** Static types of data operands; `opaque` names the fault for a pointer operand. */
  private inline def dataTypes(sources: Vec[Int], inline opaque: Int => Fault)(using
      site: Site
  ): Either[Fault, Vec[Int]] =
    Traversal.traverse(sources)(source =>
      typed(site, source) match {
        case Typed.Data(_, typeId) => Right(typeId)
        case Typed.Opaque(_, _)    => Left(opaque(source))
        case Typed.Undefined(_)    => Left(undefined(source))
      }
    )

  private def valueTypes(sources: Vec[Int], function: String)(using Site): Either[Fault, Vec[Int]] =
    dataTypes(sources, _ => Fault.InvalidRequest(s"OpenCL.std $function operands are not value types"))

  private def elementwise(resultType: Int, sources: Vec[Int], name: String, set: String = "OpenCL.std")(using
      site: Site
  ): Either[Fault, Int in (16, 32, 64)] = site.program.floatingShape(resultType) match {
    case None                 => Left(Fault.InvalidRequest(s"$set $name result is not floating-point"))
    case Some((width, count)) =>
      sources.zipWithIndex.foldLeft[Either[Fault, Int in (16, 32, 64)]](Right(width)) {
        case (result, (source, index)) =>
          result.flatMap(_ =>
            typed(site, source) match {
              case Typed.Undefined(_)    => Left(undefined(source))
              case Typed.Opaque(_, _)    => Left(Fault.InvalidRequest(s"$set $name operand ${index + 1} is a pointer"))
              case Typed.Data(_, typeId) => site.program.floatingShape(typeId) match {
                  case Some((sourceWidth, sourceCount))
                      if sourceWidth == width && (sourceCount == count || sourceCount == 1) =>
                    Right(width)
                  case _ =>
                    Left(
                      Fault.InvalidRequest(
                        s"$set $name operand ${index + 1} does not match result shape ($width,$count)"
                      )
                    )
                }
            }
          )
      }
  }

  /** The width of floating builtins whose operands and result share one exact shape. */
  private def sameFloating(
      resultType: Int,
      sources: Vec[Int],
      mismatch: NonEmpty[String],
      opaque: NonEmpty[String]
  )(using site: Site): Either[Fault, Int in (16, 32, 64)] =
    dataTypes(sources, _ => Fault.InvalidRequest(opaque)).flatMap { types =>
      site.program.floatingShape(resultType)
        .filter(shape => types.forall(typeId => site.program.floatingShape(typeId).contains(shape)))
        .map(_._1)
        .toRight(Fault.InvalidRequest(mismatch))
    }

  private def unaryFloating(set: String, name: String, resultType: Int, source: Int)(using
      site: Site
  )
      : Either[Fault, Int in (16, 32, 64)] = typed(site, source) match {
    case Typed.Undefined(_) => Left(undefined(source))
    case Typed.Opaque(_, _) =>
      Left(Fault.InvalidRequest(if (set == "GLSL.std.450") "GLSL.std.450 unary operand is not floating-point"
      else s"OpenCL.std $name operand is not floating-point"))
    case Typed.Data(_, sourceType) =>
      (site.program.floatingShape(sourceType), site.program.floatingShape(resultType)) match {
        case (Some(sourceShape @ (width, _)), Some(resultShape)) if sourceShape == resultShape => Right(width)
        case _                                                                                 =>
          Left(Fault.InvalidRequest(
            if (set == "GLSL.std.450") "GLSL.std.450 unary floating types do not match"
            else s"OpenCL.std $name requires matching floating-point scalar/vector operand and result types"
          ))
      }
  }

  private def unsupported(using instruction: Instruction): Op =
    Fail(
      Fault.Unsupported(s"${instruction.opcode} ${Operand.id(instruction.operands(3))} execution is not implemented")
    )

  /** The op of `instruction` in the first family of `set` that takes it. The one expansion of the extended families, so
    * each family's op classes are generated once.
    */
  private def staged(set: ExtendedSet)(using site: Site, instruction: Instruction): Op = {
    val sinks = OpBuilder(site, instruction.opcode.raw)
    val ops   = set match {
      case ExtendedSet.GLSL =>
        GLSLFamily.values.view.map(SPIRVStaged.glsl(_, Execute, sinks, instruction)(unmatched))
      case ExtendedSet.OpenCL =>
        OpenCLFamily.values.view.map(SPIRVStaged.openCL(_, Execute, sinks, instruction)(unmatched))
    }
    ops.find(_ ne unmatched).getOrElse(unsupported)
  }

  private def glsl(ids: ResultIds, opcode: Int, sources: Vec[Int])(using Site, Instruction): Op =
    opcode match {
      case Glsl.SAbs | Glsl.SSign | Glsl.UMin | Glsl.SMin | Glsl.UMax | Glsl.SMax | Glsl.UClamp | Glsl.SClamp | Glsl.FindILsb |
          Glsl.FindSMsb | Glsl.FindUMsb => glslInteger(ids.resultType, opcode, sources)
      case Glsl.Atan2 =>
        if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL Atan2 expects two operands"))
        else
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std atan2 requires matching floating-point scalar/vector types",
            "OpenCL.std operands are not floating-point"
          ).map(_ => staged(ExtendedSet.GLSL)))
      case Glsl.Pow =>
        if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL.std.450 Pow expects two operands"))
        else
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std pow requires matching floating-point types",
            "OpenCL.std pow operands are not floating-point"
          ).map(_ => staged(ExtendedSet.GLSL)))
      case unaryOpcode if unaryOpcode >= Glsl.Round && unaryOpcode <= Glsl.InverseSqrt =>
        if (sources.size != 1) Fail(Fault.InvalidRequest(s"GLSL.std.450 opcode $opcode expects one operand"))
        else
          lowered(unaryFloating("GLSL.std.450", opcode.toString, ids.resultType, sources.head).map(_ =>
            staged(ExtendedSet.GLSL)
          ))
      case Glsl.FMin | Glsl.FMax | Glsl.FClamp | Glsl.NMin | Glsl.NMax | Glsl.NClamp =>
        glslNumericMinMax(ids.resultType, opcode, sources)
      case Glsl.Determinant | Glsl.MatrixInverse => glslMatrix(ids, opcode, sources)
      case Glsl.Length | Glsl.Distance           => glslLengthDistance(ids.resultType, opcode, sources)
      case packing if packing >= Glsl.PackSnorm4x8 && packing <= Glsl.UnpackDouble2x32 =>
        glslPack(ids.resultType, opcode, sources)
      case Glsl.Modf | Glsl.ModfStruct | Glsl.Frexp | Glsl.FrexpStruct =>
        glslDecompose(ids.resultType, opcode, sources)
      case Glsl.FMix | Glsl.Step | Glsl.SmoothStep =>
        val arity = if (opcode == Glsl.Step) 2 else 3
        if (sources.size != arity) Fail(Fault.InvalidRequest(s"GLSL opcode $opcode expects $arity operands"))
        else
          lowered(elementwise(ids.resultType, sources, opcode.toString, "GLSL.std.450").map(_ =>
            staged(ExtendedSet.GLSL)
          ))
      case Glsl.Fma =>
        if (sources.size != 3) Fail(Fault.InvalidRequest("GLSL Fma expects three operands"))
        else
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std fma requires matching floating-point types",
            "OpenCL.std fma operands are not floating-point"
          ).map(_ => staged(ExtendedSet.GLSL)))
      case Glsl.Ldexp =>
        if (sources.size != 2) Fail(Fault.InvalidRequest("GLSL Ldexp expects two operands"))
        else ldexp(ids.resultType, sources(0), sources(1), ExtendedSet.GLSL)
      case Glsl.Cross     => cross(ExtendedSet.GLSL, ids.resultType, sources)
      case Glsl.Normalize => geometric(ExtendedSet.GLSL, ids.resultType, OpenCL.Normalize, sources)
      case Glsl.FaceForward | Glsl.Reflect | Glsl.Refract => glslGeometric(ids.resultType, opcode, sources)
      case _                                              => unsupported
    }

  private def glslInteger(resultType: Int, opcode: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op = {
    val unary    = opcode == Glsl.SAbs || opcode == Glsl.SSign || opcode >= Glsl.FindILsb
    val required = if (unary) 1 else if (opcode == Glsl.UClamp || opcode == Glsl.SClamp) 3 else 2
    if (sources.size != required) Fail(Fault.InvalidRequest(s"GLSL integer opcode $opcode has the wrong operand count"))
    else site.program.integerShape(resultType) match {
      case None => Fail(Fault.InvalidRequest(s"GLSL integer opcode $opcode requires an integer type"))
      case Some((width, _)) if opcode >= Glsl.FindILsb && width != 32 =>
        Fail(Fault.InvalidRequest("GLSL integer bit searches require 32-bit components"))
      case Some(shape) =>
        val mismatch = Fault.InvalidRequest(s"GLSL integer opcode $opcode operand types do not match")
        lowered(dataTypes(sources, _ => mismatch).flatMap { types =>
          Either.cond(
            types.forall(typeId => site.program.integerShape(typeId).contains(shape)),
            staged(ExtendedSet.GLSL),
            mismatch
          )
        })
    }
  }

  // FMin and FMax permit the same numeric operand preference for NaNs as NMin and NMax.
  private def glslNumericMinMax(resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op = {
    val required = if (opcode == Glsl.FClamp || opcode == Glsl.NClamp) 3 else 2
    if (sources.size != required) Fail(Fault.InvalidRequest("GLSL numeric min/max has the wrong operand count"))
    else if (site.program.floatingShape(resultType).isEmpty)
      Fail(Fault.InvalidRequest("GLSL numeric min/max requires a floating type"))
    else
      lowered(sameFloating(
        resultType,
        sources,
        "GLSL numeric min/max operand types do not match",
        "GLSL numeric min/max operand types do not match"
      ).map(_ => staged(ExtendedSet.GLSL)))
  }

  private def glslMatrix(ids: ResultIds, opcode: Int, sources: Vec[Int])(using site: Site): Op =
    if (sources.size != 1) Fail(Fault.InvalidRequest("GLSL matrix operation requires one operand"))
    else typed(site, sources.head) match {
      case Typed.Undefined(value) => Fail(undefined(value))
      case Typed.Opaque(_, _)     => Fail(Fault.InvalidRequest("GLSL determinant/inverse operand is not matrix data"))
      case Typed.Data(_, typeId)  =>
        site.program.matrixShape(typeId) match {
          case Some((width, rows, columns))
              if rows == columns && rows >= 2 && rows <= 4 &&
                (if (opcode == Glsl.Determinant)
                   site.program.floatingShape(ids.resultType).contains(width -> 1)
                 else site.program.matrixShape(ids.resultType).contains((width, rows, columns))) =>
            if (opcode == Glsl.Determinant) Determinant(sources.head, rows, width, ids.dest, ids.resultType)
            else Inverse(sources.head, rows, width, ids.dest, ids.resultType)
          case _ => Fail(Fault.InvalidRequest("GLSL determinant/inverse requires matching square matrix types"))
        }
    }

  private class Determinant(source: Int, rows: Int, width: Int in (16, 32, 64), dest: Int, resultType: Int) extends Op {
    private def ofTwo(execution: InvocationState) =
      MatrixSemantics.determinant(Execute, InvocationMachine(execution))(source, 2, width, dest, resultType)

    private def ofThree(execution: InvocationState) =
      MatrixSemantics.determinant(Execute, InvocationMachine(execution))(source, 3, width, dest, resultType)

    private def ofFour(execution: InvocationState) =
      MatrixSemantics.determinant(Execute, InvocationMachine(execution))(source, 4, width, dest, resultType)

    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = rows match {
      case 2 => ofTwo(execution)
      case 3 => ofThree(execution)
      case _ => ofFour(execution)
    }
  }

  private class Inverse(source: Int, rows: Int, width: Int in (16, 32, 64), dest: Int, resultType: Int) extends Op {
    private def ofTwo(execution: InvocationState) =
      MatrixSemantics.inverse(Execute, InvocationMachine(execution))(source, 2, width, dest, resultType)

    private def ofThree(execution: InvocationState) =
      MatrixSemantics.inverse(Execute, InvocationMachine(execution))(source, 3, width, dest, resultType)

    private def ofFour(execution: InvocationState) =
      MatrixSemantics.inverse(Execute, InvocationMachine(execution))(source, 4, width, dest, resultType)

    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] = rows match {
      case 2 => ofTwo(execution)
      case 3 => ofThree(execution)
      case _ => ofFour(execution)
    }
  }

  private def glslLengthDistance(resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op =
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
            else staged(ExtendedSet.GLSL)
        }
    }

  private def glslPack(resultType: Int, opcode: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op =
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
            opcode == Glsl.PackSnorm4x8 || opcode == Glsl.PackUnorm4x8 || opcode == Glsl.UnpackSnorm4x8 ||
            opcode == Glsl.UnpackUnorm4x8
          ) 4
          else 2
        val packing =
          (opcode <= Glsl.PackHalf2x16 && sourceFloat.contains(32 -> count) && resultInt.contains((32, 1, false))) ||
            (opcode == Glsl.PackDouble2x32 && sourceInt.contains((32, 2, false)) && resultFloat.contains(64 -> 1))
        val unpacking =
          (opcode == Glsl.UnpackDouble2x32 && sourceFloat.contains(64 -> 1) && resultInt.contains((32, 2, false))) ||
            (opcode >= Glsl.UnpackSnorm2x16 && opcode <= Glsl.UnpackUnorm4x8 && sourceInt.contains((32, 1, false)) &&
              resultFloat.contains(32 -> count))
        if (packing || unpacking) staged(ExtendedSet.GLSL)
        else Fail(Fault.InvalidRequest(s"GLSL packing opcode $opcode has invalid operand or result types"))
    }

  private def glslDecompose(resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op = {
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
          case None    => Fail(incompatible)
          case Some(_) =>
            if (pointerForm) {
              val pointee = program.runtimeType(program.valueType(sources(1))) match {
                case Some(RuntimeType.Pointer(_, pointee)) => pointee
                case _                                     => -1
              }
              if (resultType == sourceType && valid(pointee)) staged(ExtendedSet.GLSL) else Fail(incompatible)
            } else
              program.runtimeType(resultType) match {
                case Some(RuntimeType.Structure(members))
                    if members.size == 2 && members(0) == sourceType && valid(members(1)) =>
                  staged(ExtendedSet.GLSL)
                case _ => Fail(incompatible)
              }
        }
    }
  }

  private def glslGeometric(resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op = {
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
          case None        => staged(ExtendedSet.GLSL)
        }
      case _ => Fail(Fault.InvalidRequest("GLSL geometric result or operand count is invalid"))
    }
  }

  private def signedIntegerShape(program: VMProgram, typeId: Int): Option[(Int, Int, Boolean)] =
    program.runtimeType(typeId) match {
      case Some(RuntimeType.Integer(width, signed))       => Some((width, 1, signed))
      case Some(RuntimeType.VectorType(component, count)) => program.runtimeType(component).collect {
          case RuntimeType.Integer(width, signed) => (width, count, signed)
        }
      case _ => None
    }

  private val unaryNames: Map[Int, String] = Map(
    OpenCL.Acos        -> "acos",
    OpenCL.Acosh       -> "acosh",
    OpenCL.Acospi      -> "acospi",
    OpenCL.Asin        -> "asin",
    OpenCL.Asinh       -> "asinh",
    OpenCL.Asinpi      -> "asinpi",
    OpenCL.Atan        -> "atan",
    OpenCL.Atanh       -> "atanh",
    OpenCL.Atanpi      -> "atanpi",
    OpenCL.Cbrt        -> "cbrt",
    OpenCL.Ceil        -> "ceil",
    OpenCL.Cos         -> "cos",
    OpenCL.Cosh        -> "cosh",
    OpenCL.Cospi       -> "cospi",
    OpenCL.Erf         -> "erf",
    OpenCL.Erfc        -> "erfc",
    OpenCL.Tgamma      -> "tgamma",
    OpenCL.Lgamma      -> "lgamma",
    OpenCL.Exp         -> "exp",
    OpenCL.Exp2        -> "exp2",
    OpenCL.Exp10       -> "exp10",
    OpenCL.Expm1       -> "expm1",
    OpenCL.Fabs        -> "fabs",
    OpenCL.Floor       -> "floor",
    OpenCL.Log         -> "log",
    OpenCL.Log2        -> "log2",
    OpenCL.Log10       -> "log10",
    OpenCL.Log1p       -> "log1p",
    OpenCL.Logb        -> "logb",
    OpenCL.Rint        -> "rint",
    OpenCL.Round       -> "round",
    OpenCL.Rsqrt       -> "rsqrt",
    OpenCL.Sin         -> "sin",
    OpenCL.Sinh        -> "sinh",
    OpenCL.Sinpi       -> "sinpi",
    OpenCL.Sqrt        -> "sqrt",
    OpenCL.Tan         -> "tan",
    OpenCL.Tanh        -> "tanh",
    OpenCL.Tanpi       -> "tanpi",
    OpenCL.Trunc       -> "trunc",
    OpenCL.NativeCos   -> "native_cos",
    OpenCL.NativeExp   -> "native_exp",
    OpenCL.NativeExp2  -> "native_exp2",
    OpenCL.NativeExp10 -> "native_exp10",
    OpenCL.NativeLog   -> "native_log",
    OpenCL.NativeLog2  -> "native_log2",
    OpenCL.NativeLog10 -> "native_log10",
    OpenCL.NativeRecip -> "native_recip",
    OpenCL.NativeRsqrt -> "native_rsqrt",
    OpenCL.NativeSin   -> "native_sin",
    OpenCL.NativeSqrt  -> "native_sqrt",
    OpenCL.NativeTan   -> "native_tan"
  )

  private val binaryNames: Map[Int, String] = Map(
    OpenCL.Atan2pi      -> "atan2pi",
    OpenCL.Fdim         -> "fdim",
    OpenCL.Fmod         -> "fmod",
    OpenCL.Remainder    -> "remainder",
    OpenCL.NativeDivide -> "native_divide",
    OpenCL.NativePowr   -> "native_powr",
    OpenCL.Powr         -> "powr"
  )

  private def openCL(ids: ResultIds, opcode: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op =
    opcode match {
      case OpenCL.Copysign =>
        count("OpenCL.std copysign", sources, 2)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std copysign requires matching floating-point scalar/vector operand and result types",
            "OpenCL.std copysign operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.Fma | OpenCL.Mad =>
        val name = if (opcode == OpenCL.Fma) "fma" else "mad"
        count(s"OpenCL.std $name", sources, 3)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            s"OpenCL.std $name requires matching floating-point types",
            s"OpenCL.std $name operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.Maxmag | OpenCL.Minmag =>
        count("OpenCL.std maxmag", sources, 2)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std magnitude selection requires matching floating-point types",
            "OpenCL.std magnitude selection operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.Pow =>
        count("OpenCL.std pow", sources, 2)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std pow requires matching floating-point types",
            "OpenCL.std pow operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.SMax | OpenCL.UMax | OpenCL.SMin | OpenCL.UMin =>
        val name = opcode match {
          case OpenCL.SMax => "s_max"
          case OpenCL.UMax => "u_max"
          case OpenCL.SMin => "s_min"
          case _           => "u_min"
        }
        count(s"OpenCL.std $name", sources, 2)(
          ArithmeticOps.bitwiseOperands(
            s"OpenCL.std $name",
            BinaryOperands(ids, operand(sources(0)), operand(sources(1)))
          )(_ => staged(ExtendedSet.OpenCL))
        )
      case OpenCL.Sincos | OpenCL.Fract | OpenCL.Modf | OpenCL.Frexp | OpenCL.LgammaR =>
        count(
          s"OpenCL.std pointer math opcode $opcode",
          sources,
          2
        )(pointerMath(ids.resultType, sources(0), sources(1), opcode))
      case OpenCL.Ilogb =>
        if (sources.size != 1) Fail(Fault.InvalidRequest("OpenCL.std ilogb requires one operand"))
        else ilogb(ids.resultType, sources.head)
      case OpenCL.Remquo =>
        if (sources.size != 3) Fail(Fault.InvalidRequest("OpenCL.std remquo requires three operands"))
        else remquo(ids.resultType, sources)
      case OpenCL.Printf             => printf(ids, sources)
      case OpenCL.Fmax | OpenCL.Fmin =>
        count(s"OpenCL.std ${if (opcode == OpenCL.Fmax) "fmax" else "fmin"}", sources, 2)(
          ArithmeticOps.floating(
            s"OpenCL.std ${if (opcode == OpenCL.Fmax) "fmax" else "fmin"}",
            BinaryOperands(ids, operand(sources(0)), operand(sources(1)))
          )(staged(ExtendedSet.OpenCL))
        )
      case OpenCL.Ldexp =>
        count("OpenCL.std ldexp", sources, 2)(ldexp(ids.resultType, sources(0), sources(1), ExtendedSet.OpenCL))
      case unaryOpcode if unaryNames.contains(unaryOpcode) =>
        val name = unaryNames(unaryOpcode)
        count(s"OpenCL.std $name", sources, 1, "operand")(
          lowered(unaryFloating("OpenCL.std", name, ids.resultType, sources.head).map(_ =>
            staged(ExtendedSet.OpenCL)
          ))
        )
      case OpenCL.Atan2 | OpenCL.Hypot =>
        val name = if (opcode == OpenCL.Hypot) "hypot" else "atan2"
        count(s"OpenCL.std $name", sources, 2)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            s"OpenCL.std $name requires matching floating-point scalar/vector types",
            "OpenCL.std operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case binaryOpcode if binaryNames.contains(binaryOpcode) =>
        val name = binaryNames(binaryOpcode)
        count(s"OpenCL.std $name", sources, 2)(
          lowered(elementwise(ids.resultType, sources, name).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.Nan => count("OpenCL.std nan", sources, 1, "operand")(nan(ids.resultType, sources.head))
      case OpenCL.Pown | OpenCL.Rootn =>
        count(s"OpenCL.std ${if (opcode == OpenCL.Pown) "pown" else "rootn"}", sources, 2)(
          integerPower(ids.resultType, sources(0), sources(1), opcode)
        )
      case OpenCL.Nextafter =>
        count("OpenCL.std nextafter", sources, 2)(
          lowered(sameFloating(
            ids.resultType,
            sources,
            "OpenCL.std nextafter requires matching floating-point scalar/vector operand and result types",
            "OpenCL.std nextafter operands are not floating-point"
          ).map(_ => staged(ExtendedSet.OpenCL)))
        )
      case OpenCL.SHadd =>
        val mismatch =
          Fault.InvalidRequest("OpenCL.std hadd requires matching integer scalar/vector operand and result types")
        count("OpenCL.std hadd", sources, 2)(
          lowered(dataTypes(sources, _ => mismatch).flatMap { types =>
            site.program.integerShape(ids.resultType) match {
              case Some(shape)
                  if types.forall(typeId => site.program.integerShape(typeId).contains(shape)) =>
                Right(staged(ExtendedSet.OpenCL))
              case _ => Left(mismatch)
            }
          })
        )
      case integerOpcode if integerArithmetic(integerOpcode) => openCLInteger(ids.resultType, opcode, sources)
      case OpenCL.SAbs | OpenCL.UAbs                         =>
        count("OpenCL.std abs", sources, 1, "operand")(integerUnary(ids.resultType, sources.head, "abs"))
      case OpenCL.Clz =>
        count("OpenCL.std clz", sources, 1, "operand")(integerUnary(ids.resultType, sources.head, "clz"))
      case OpenCL.Ctz =>
        count("OpenCL.std ctz", sources, 1, "operand")(integerUnary(ids.resultType, sources.head, "ctz"))
      case OpenCL.Popcount =>
        count("OpenCL.std popcount", sources, 1, "operand")(
          integerUnary(ids.resultType, sources.head, "popcount")
        )
      case common if common >= OpenCL.Fclamp && common <= OpenCL.Sign =>
        openCLCommon(ids.resultType, opcode, sources)
      case OpenCL.Cross => cross(ExtendedSet.OpenCL, ids.resultType, sources)
      case geometricOpcode if geometricOpcode > OpenCL.Cross && geometricOpcode <= OpenCL.FastNormalize =>
        geometric(ExtendedSet.OpenCL, ids.resultType, opcode, sources)
      case OpenCL.VloadHalf | OpenCL.VloadHalfn | OpenCL.VloadaHalfn =>
        halfLoad(ids.resultType, sources)
      case store if store >= OpenCL.VstoreHalf && store <= OpenCL.VstoreaHalfnR =>
        halfStore(
          sources,
          rounded = opcode == OpenCL.VstoreHalfR || opcode == OpenCL.VstoreHalfnR || opcode == OpenCL.VstoreaHalfnR
        )
      case OpenCL.Vloadn                    => vectorLoad(ids, sources)
      case OpenCL.Vstoren                   => count("OpenCL.std vstoren", sources, 3)(vectorStore(sources))
      case OpenCL.Shuffle | OpenCL.Shuffle2 => shuffle(ids, sources, opcode == OpenCL.Shuffle2)
      case OpenCL.Bitselect => count("OpenCL.std bitselect", sources, 3)(bitselect(ids.resultType, sources))
      case OpenCL.Select    => count("OpenCL.std select", sources, 3)(select(ids.resultType, sources))
      case OpenCL.Prefetch  =>
        if (sources.isEmpty) Fail(Fault.InvalidRequest("OpenCL.std prefetch is missing its pointer operand"))
        else prefetch(sources.head)
      case _ => unsupported
    }

  /** The static pointee of a pointer value, or -1. */
  private def pointerPointee(program: VMProgram, pointer: Int): Int =
    program.runtimeType(program.valueType(pointer)) match {
      case Some(RuntimeType.Pointer(_, pointee)) => pointee
      case _                                     => -1
    }

  private def operand(value: Int): Operand = Operand(OperandKind.IdRef, Vec.nonEmpty(Word(value)))

  private def integerArithmetic(opcode: Int): Boolean =
    (opcode >= OpenCL.SAbsDiff && opcode <= OpenCL.UClamp && opcode != OpenCL.SHadd) ||
      (opcode >= OpenCL.SMadHi && opcode <= OpenCL.SMadSat) ||
      (opcode >= OpenCL.SMulHi && opcode <= OpenCL.SUpsample) ||
      (opcode >= OpenCL.SMad24 && opcode <= OpenCL.UMul24) ||
      (opcode >= OpenCL.UAbsDiff && opcode <= OpenCL.UMadHi)

  private def integerUnary(resultType: Int, source: Int, name: String)(using site: Site)(using Instruction): Op =
    typed(site, source) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest(s"OpenCL.std $name operand is a pointer"))
      case Typed.Data(_, sourceType) =>
        (site.program.integerShape(sourceType), site.program.integerShape(resultType)) match {
          case (Some(sourceShape), Some(resultShape)) if sourceShape == resultShape =>
            staged(ExtendedSet.OpenCL)
          case _ => Fail(Fault.InvalidRequest(s"OpenCL.std $name requires matching integer scalar/vector types"))
        }
    }

  private def openCLInteger(resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op = {
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
          if (width == 8 || width == 16 || width == 32 || width == 64) &&
            (!widening || width != 8) &&
            (!smallProduct || width == 32) =>
        val inputWidth: Int in 1 ~ 64 = if (widening) (width / 2).unsafe else width
        val mismatch                  =
          Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode requires matching integer scalar/vector types")
        lowered(dataTypes(sources, _ => mismatch).flatMap { types =>
          Either.cond(
            types.forall(typeId => site.program.integerShape(typeId).contains(inputWidth -> count)),
            staged(ExtendedSet.OpenCL),
            mismatch
          )
        })
      case _ =>
        Fail(Fault.InvalidRequest(s"OpenCL.std integer opcode $opcode requires an 8, 16, 32 or 64-bit integer result"))
    }
  }

  private def openCLCommon(resultType: Int, opcode: Int, sources: Vec[Int])(using Site, Instruction): Op = {
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
    count(s"OpenCL.std $name", sources, arity)(lowered(elementwise(resultType, sources, name).map(_ =>
      staged(ExtendedSet.OpenCL)
    )))
  }

  private def ldexp(resultType: Int, source: Int, exponentValue: Int, set: ExtendedSet)(using
      site: Site
  )(using Instruction): Op = {
    val glsl                       = set == ExtendedSet.GLSL
    val mismatch: NonEmpty[String] =
      if (glsl) "GLSL Ldexp requires matching floating values and integer exponents"
      else "OpenCL.std ldexp requires matching floating values and i32 exponents"
    lowered(valueTypes(Vec(source, exponentValue), "ldexp").flatMap {
      types =>
        (
          site.program.floatingShape(types(0)),
          site.program.floatingShape(resultType),
          site.program.integerShape(types(1))
        ) match {
          case (Some((width, count)), Some((resultWidth, resultCount)), Some((exponentWidth, exponentCount)))
              if width == resultWidth && count == resultCount && (glsl || exponentWidth == 32) &&
                exponentCount == count =>
            Right(staged(set))
          case _ => Left(Fault.InvalidRequest(mismatch))
        }
    })
  }

  private def nan(resultType: Int, source: Int)(using site: Site)(using Instruction): Op =
    typed(site, source) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std nan payload is a pointer"))
      case Typed.Data(_, sourceType) =>
        (site.program.floatingShape(resultType), site.program.integerShape(sourceType)) match {
          case (Some((_, count)), Some((_, sourceCount))) if sourceCount == count || sourceCount == 1 =>
            staged(ExtendedSet.OpenCL)
          case _ => Fail(Fault.InvalidRequest("OpenCL.std nan requires a floating result and integer payload"))
        }
    }

  private def integerPower(resultType: Int, source: Int, power: Int, opcode: Int)(using
      site: Site
  )(using Instruction): Op =
    lowered(valueTypes(Vec(source, power), "integer-power").flatMap {
      types =>
        (
          site.program.floatingShape(resultType),
          site.program.floatingShape(types(0)),
          site.program.integerShape(types(1))
        ) match {
          case (Some((width, count)), Some((sourceWidth, sourceCount)), Some((_, exponentCount)))
              if width == sourceWidth && count == sourceCount && (exponentCount == count || exponentCount == 1) =>
            Right(staged(ExtendedSet.OpenCL))
          case _ =>
            Left(Fault.InvalidRequest(
              s"OpenCL.std ${
                  if (opcode == OpenCL.Rootn) "rootn" else "pown"
                } requires floating values and integer exponents"
            ))
        }
    })

  private def ilogb(resultType: Int, source: Int)(using site: Site)(using Instruction): Op =
    typed(site, source) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std ilogb input is not floating-point"))
      case Typed.Data(_, sourceType) =>
        (site.program.floatingShape(sourceType), site.program.integerShape(resultType)) match {
          case (Some((_, count)), Some((resultWidth, resultCount)))
              if resultWidth == 32 && count == resultCount =>
            staged(ExtendedSet.OpenCL)
          case _ => Fail(Fault.InvalidRequest("OpenCL.std ilogb requires floating input and matching i32 result lanes"))
        }
    }

  private def pointerMath(resultType: Int, source: Int, output: Int, opcode: Int)(using
      site: Site
  )(using Instruction): Op = {
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
        val pointee       = pointerPointee(program, output)
        val matching      = program.untyped(pointee) ||
          (if (integerOutput) program.integerShape(pointee).exists((found, lanes) => found == 32 && lanes == count)
           else program.floatingShape(pointee).contains((width, count)))
        if (matching) staged(ExtendedSet.OpenCL)
        else Fail(Fault.InvalidRequest("OpenCL.std pointer math output type does not match input"))
    }
  }

  private def remquo(resultType: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op = {
    val program = site.program
    program.floatingShape(resultType) match {
      case Some(shape @ (_, count))
          if sources.take(2).forall(source => program.floatingShape(program.valueType(source)).contains(shape)) =>
        if (
          program.untyped(pointerPointee(program, sources(2))) ||
          program.integerShape(pointerPointee(program, sources(2))).exists((found, lanes) =>
            found == 32 && lanes == count
          )
        )
          staged(ExtendedSet.OpenCL)
        else Fail(Fault.InvalidRequest("OpenCL.std remquo requires matching floating operands and i32 pointer lanes"))
      case _ =>
        sources.take(2).find(source => program.valueType(source) < 0) match {
          case Some(source) => Fail(undefined(source))
          case None         =>
            Fail(Fault.InvalidRequest("OpenCL.std remquo requires matching floating operands and i32 pointer lanes"))
        }
    }
  }

  /** The floating-point width and lane count every operand of the geometric function `name` shares. */
  private def geometricShape(name: String, sources: Vec[Int])(using
      site: Site
  ): Either[Fault, (Int in (16, 32, 64), Int)] =
    dataTypes(sources, _ => Fault.InvalidRequest(s"OpenCL.std $name operand is a pointer")).flatMap { types =>
      val shapes = types.map(site.program.floatingShape)
      Either.cond(
        shapes.forall(_.isDefined) && shapes.forall(_.map(_._1) == shapes.head.map(_._1)) &&
          shapes.forall(_.map(_._2) == shapes.head.map(_._2)),
        shapes.head,
        Fault.InvalidRequest(s"OpenCL.std $name operands have incompatible types")
      ).flatMap(_.toRight(Fault.InvalidRequest(s"OpenCL.std $name has no floating-point operands")))
    }

  /** A cross product of two three- or four-lane vectors. */
  private def cross(set: ExtendedSet, resultType: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op =
    count("OpenCL.std cross", sources, 2)(lowered(geometricShape("cross", sources).flatMap { (width, count) =>
      if (count != 3 && count != 4) Left(Fault.InvalidRequest("OpenCL.std cross requires 3- or 4-component vectors"))
      else if (!site.program.floatingShape(resultType).contains((width, count)))
        Left(Fault.InvalidRequest("OpenCL.std cross result type does not match its operands"))
      else Right(staged(set))
    }))

  /** Lengths, distances and normalisation. */
  private def geometric(set: ExtendedSet, resultType: Int, opcode: Int, sources: Vec[Int])(using
      site: Site
  )(using Instruction): Op = {
    val name = opcode match {
      case OpenCL.Distance     => "distance"
      case OpenCL.Length       => "length"
      case OpenCL.Normalize    => "normalize"
      case OpenCL.FastDistance => "fast_distance"
      case OpenCL.FastLength   => "fast_length"
      case _                   => "fast_normalize"
    }
    val arity = if (opcode == OpenCL.Distance || opcode == OpenCL.FastDistance) 2 else 1
    count(s"OpenCL.std $name", sources, arity)(lowered(geometricShape(name, sources).flatMap { (width, count) =>
      val resultShape = site.program.floatingShape(resultType)
      opcode match {
        case OpenCL.Distance | OpenCL.FastDistance | OpenCL.Length | OpenCL.FastLength =>
          if (!resultShape.contains((width, 1)))
            Left(Fault.InvalidRequest(s"OpenCL.std $name result is not a matching floating scalar"))
          else Right(staged(set))
        case _ =>
          if (!resultShape.contains((width, count)))
            Left(Fault.InvalidRequest(s"OpenCL.std $name result type does not match its operand"))
          else Right(staged(set))
      }
    }))
  }

  private def halfLoad(resultType: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op = {
    val vector = sources.size == 3
    if ((!vector && sources.size != 2) || (vector && sources.size != 3))
      Fail(Fault.InvalidRequest("OpenCL.std half load has an invalid operand count"))
    else site.program.floatingShape(resultType) match {
      case Some((width, count)) if width == 32 && (!vector || unsignedLiteral(sources(2)) == count.toLong) =>
        staged(ExtendedSet.OpenCL)
      case _ => Fail(Fault.InvalidRequest("OpenCL.std half load requires a float result and half pointer"))
    }
  }

  /** A literal OpenCL.std operand; vector widths are encoded as literal words, not ids. */
  private def unsignedLiteral(word: Int): Long = word.toLong & 0xffffffffL

  private def halfStore(sources: Vec[Int], rounded: Boolean)(using site: Site)(using Instruction): Op = {
    val expected = if (rounded) 4 else 3
    if (sources.size != expected) Fail(Fault.InvalidRequest(s"OpenCL.std half store expects $expected operands"))
    else typed(site, sources(0)) match {
      case Typed.Undefined(value)    => Fail(undefined(value))
      case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std half store operands are not value types"))
      case Typed.Data(_, sourceType) =>
        site.program.floatingShape(sourceType) match {
          case Some((width, _)) if width == 32 || width == 64 =>
            staged(ExtendedSet.OpenCL)
          case _ => Fail(Fault.InvalidRequest("OpenCL.std half store source is not float or double"))
        }
    }
  }

  private def vectorLoad(ids: ResultIds, sources: Vec[Int])(using site: Site): Op =
    if (sources.size != 3) Fail(Fault.InvalidRequest(s"OpenCL.std vloadn expects 3 operands, received ${sources.size}"))
    else site.program.runtimeType(ids.resultType) match {
      case Some(RuntimeType.VectorType(component, count)) if unsignedLiteral(sources(2)) == count.toLong =>
        site.program.scalarMemoryWidth(component) match {
          case Left(error)        => Fail(error)
          case Right(elementSize) =>
            VectorLoad(
              offset = sources(0),
              pointer = sources(1),
              count = count.raw,
              size = elementSize,
              dest = ids.dest,
              resultType = ids.resultType
            )
        }
      case Some(RuntimeType.VectorType(_, count)) =>
        Fail(Fault.InvalidRequest(
          s"OpenCL.std vloadn literal vector width ${unsignedLiteral(sources(2))} does not match $count"
        ))
      case Some(_) => Fail(Fault.InvalidRequest("OpenCL.std vloadn result type is not a vector"))
      case None    => Fail(Fault.InvalidRequest(s"OpenCL.std vloadn result type %${ids.resultType} is undefined"))
    }

  private class VectorLoad(offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8, dest: Int, resultType: Int)
      extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ExtendedSemantics.vectorLoad(InvocationMachine(execution))(offset, pointer, count, size, dest, resultType)
  }

  private def vectorStore(sources: Vec[Int])(using site: Site): Op = typed(site, sources(0)) match {
    case Typed.Undefined(value)    => Fail(undefined(value))
    case Typed.Opaque(_, _)        => Fail(Fault.InvalidRequest("OpenCL.std vstoren source is not numeric"))
    case Typed.Data(_, sourceType) =>
      site.program.runtimeType(sourceType) match {
        case Some(RuntimeType.VectorType(component, count)) =>
          site.program.scalarMemoryWidth(component) match {
            case Left(error)        => Fail(error)
            case Right(elementSize) => VectorStore(sources(0), sources(1), sources(2), count.raw, elementSize)
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std vstoren source is not a vector"))
      }
  }

  private class VectorStore(data: Int, offset: Int, pointer: Int, count: Int, size: Int in 1 ~ 8) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ExtendedSemantics.vectorStore(InvocationMachine(execution))(data, offset, pointer, count, size)
  }

  private def shuffle(ids: ResultIds, sources: Vec[Int], twoInputs: Boolean)(using site: Site): Op = {
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
          (
            program.numericShape(firstType),
            program.integerShape(maskType),
            program.numericShape(ids.resultType)
          ) match {
            case (Some((width, sourceCount)), Some((_, maskCount)), Some((resultWidth, resultCount)))
                if width == resultWidth && maskCount == resultCount &&
                  (!twoInputs || secondShape.contains((
                    width,
                    sourceCount
                  ))) =>
              Permute(sources.toArray, sourceCount * maskIndex, resultCount, ids.dest, ids.resultType)
            case _ => Fail(Fault.InvalidRequest("OpenCL.std shuffle operand types are incompatible"))
          }
        case _ => Fail(Fault.InvalidRequest("OpenCL.std shuffle operands are not value types"))
      }
    }
  }

  private class Permute(operands: Array[Int], total: Int, count: Int, dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ExtendedSemantics.shuffle(Execute, InvocationMachine(execution))(operands, total, count, dest, resultType)
  }

  private def bitselect(resultType: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op = {
    val invalid = Fault.InvalidRequest("OpenCL.std bitselect operands are undefined or pointer values")
    lowered(dataTypes(sources, _ => invalid).left.map(_ => invalid).flatMap { types =>
      val shape = site.program.numericShape(resultType)
      Either.cond(
        types.forall(typeId => site.program.numericShape(typeId) == shape),
        staged(ExtendedSet.OpenCL),
        Fault.InvalidRequest("OpenCL.std bitselect operands must have matching numeric shapes")
      )
    })
  }

  private def select(resultType: Int, sources: Vec[Int])(using site: Site)(using Instruction): Op = {
    val invalid = Fault.InvalidRequest("OpenCL.std select operands are undefined or pointer values")
    lowered(dataTypes(sources, _ => invalid).left.map(_ => invalid).flatMap { types =>
      (
        site.program.numericShape(resultType),
        site.program.numericShape(types(0)),
        site.program.numericShape(types(1)),
        site.program.integerShape(types(2))
      ) match {
        case (Some(resultShape @ (_, count)), Some(aShape), Some(bShape), Some((_, conditionCount)))
            if resultShape == aShape && resultShape == bShape && (conditionCount == count || conditionCount == 1) =>
          Right(staged(ExtendedSet.OpenCL))
        case _ => Left(Fault.InvalidRequest("OpenCL.std select operand types are incompatible"))
      }
    })
  }

  private def printf(ids: ResultIds, sources: Vec[Int]): Op =
    if (sources.isEmpty) Fail(Fault.InvalidRequest("OpenCL.std printf requires a format string"))
    else Printf(sources.head, sources.tail.toArray, ids.dest, ids.resultType)

  private class Printf(format: Int, arguments: Array[Int], dest: Int, resultType: Int) extends Op {
    def run(execution: InvocationState, memory: MemoryState): AddressSpace.ReadResult[Unit] =
      ExtendedSemantics.printf(InvocationMachine(execution))(format, arguments, dest, resultType)
  }
}
