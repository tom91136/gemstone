package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.semantics.*

/** Instructions wired to their semantics: one dispatcher per instruction set over the families [[CoreFamily]],
  * [[GLSLFamily]] and [[OpenCLFamily]] list. Each family reads the result type, result and operand ids from the
  * instruction's grammar operands and the widths it specialises on from the sinks, so execution and description share
  * one definition, and defers to `otherwise` for instructions outside it.
  */
object SPIRVStaged {

  /** The id operands of `instruction` from grammar position `from`, after its result type and result. */
  def operandIds(instruction: Instruction, from: Int = 2): OperandIds = {
    val operands = instruction.operands
    OperandIds(
      Operand.id(operands(0)),
      Operand.id(operands(1)),
      operands.drop(from).filter(_.kind.category == OperandKind.Category.Id).map(Operand.id(_))
    )
  }

  /** The extended opcode of an OpExtInst, with OpenCL's half-precision functions as their native counterparts, which
    * supply at least the precision they permit.
    */
  def extendedOpcode(instruction: Instruction, openCL: Boolean): Int = {
    val encoded = Operand.id(instruction.operands(3))
    if (openCL && encoded >= OpenCL.HalfCos && encoded <= OpenCL.HalfTan) encoded + OpenCL.NativeCos - OpenCL.HalfCos
    else encoded
  }

  private def shift(opcode: Int): Boolean =
    opcode == SPIRV.Opcode.ShiftLeftLogical || opcode == SPIRV.Opcode.ShiftRightLogical ||
      opcode == SPIRV.Opcode.ShiftRightArithmetic

  /** The semantics of `instruction` in core family `family`, or `otherwise`. Each family is a method of its own, so
    * expanding them all keeps every method within the JVM's size limits; the op classes an expansion generates are
    * generated once per call site.
    */
  inline def core[V, R](
      family: CoreFamily,
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      instruction: Instruction
  )(otherwise: => R): R = {

    /** Integer arithmetic, bitwise operations, shifts, bit counts and reversal. */
    def integer: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      val width  = sinks.width(if (opcode == SPIRV.Opcode.BitCount) sinks.typeOf(ids(0)) else ids.resultType)
      IntegerSemantics.binary(
        algebra,
        sinks,
        opcode,
        width,
        if (shift(opcode)) Bits.mask(sinks.width(sinks.typeOf(ids(1)))) else -1L
      )(ids)(IntegerSemantics.unary(algebra, sinks, opcode, width)(ids)(otherwise))
    }

    def bitField: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      val width  = sinks.width(ids.resultType)
      if (opcode == SPIRV.Opcode.BitFieldInsert) IntegerSemantics.bitFieldInsert(algebra, sinks, width)(ids)
      else IntegerSemantics.bitFieldExtract(algebra, sinks, opcode, width)(ids)(otherwise)
    }

    /** Add with carry, subtract with borrow and extended multiplication. */
    def extended: R = {
      val ids = operandIds(instruction)
      IntegerSemantics.extended(algebra, sinks, instruction.opcodeNumber.toInt, sinks.width(sinks.typeOf(ids(0))))(ids)(
        otherwise
      )
    }

    /** Integer and Boolean comparisons, selection and Boolean reductions. */
    def comparison: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      ComparisonSemantics.binary(algebra, sinks, opcode, sinks.width(sinks.typeOf(ids(0))))(ids)(
        ComparisonSemantics.unary(algebra, sinks, opcode)(ids)(
          ComparisonSemantics.ternary(algebra, sinks, opcode)(ids)(
            ComparisonSemantics.reduction(algebra, sinks, opcode)(ids)(otherwise)
          )
        )
      )
    }

    /** Floating-point arithmetic, negation, scaling, dot products and matrix products. */
    def floating: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      val width  = sinks.floatingWidth(ids.resultType)
      val scaled = opcode == SPIRV.Opcode.VectorTimesScalar || opcode == SPIRV.Opcode.MatrixTimesScalar
      FloatingSemantics.binary(algebra, sinks, if (scaled) SPIRV.Opcode.FMul else opcode, width)(ids)(
        FloatingSemantics.unary(algebra, sinks, opcode, width)(ids)(
          opcode match {
            case SPIRV.Opcode.Dot =>
              FloatingSemantics.dot(algebra, sinks, sinks.floatingWidth(sinks.typeOf(ids(0))))(ids)
            case SPIRV.Opcode.MatrixTimesMatrix | SPIRV.Opcode.MatrixTimesVector | SPIRV.Opcode.VectorTimesMatrix =>
              FloatingSemantics.product(algebra, sinks, width)(ids)
            case SPIRV.Opcode.OuterProduct => FloatingSemantics.outerProduct(algebra, sinks, width)(ids)
            case _                         => otherwise
          }
        )
      )
    }

    /** Ordered floating-point comparisons. */
    def orderedComparison: R = {
      val ids = operandIds(instruction)
      FloatingSemantics.orderedComparison(
        algebra,
        sinks,
        instruction.opcodeNumber.toInt,
        sinks.floatingWidth(sinks.typeOf(ids(0)))
      )(ids)(otherwise)
    }

    /** Unordered floating-point comparisons. */
    def unorderedComparison: R = {
      val ids = operandIds(instruction)
      FloatingSemantics.unorderedComparison(
        algebra,
        sinks,
        instruction.opcodeNumber.toInt,
        sinks.floatingWidth(sinks.typeOf(ids(0)))
      )(ids)(otherwise)
    }

    /** Floating-point classification. */
    def classification: R = {
      val ids = operandIds(instruction)
      FloatingSemantics.classify(
        algebra,
        sinks,
        instruction.opcodeNumber.toInt,
        sinks.floatingWidth(sinks.typeOf(ids(0)))
      )(ids)(otherwise)
    }

    /** Width, signedness and encoding conversions, rounding in the decorated mode. */
    def conversion: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      val source = sinks.typeOf(ids(0))
      opcode match {
        case SPIRV.Opcode.UConvert | SPIRV.Opcode.SConvert | SPIRV.Opcode.Bitcast =>
          ConversionSemantics.integer(algebra, sinks, opcode, sinks.width(source), sinks.width(ids.resultType))(ids)(
            otherwise
          )
        case SPIRV.Opcode.SatConvertSToU | SPIRV.Opcode.SatConvertUToS =>
          ConversionSemantics.saturating(algebra, sinks, opcode, sinks.width(source), sinks.width(ids.resultType))(ids)(
            otherwise
          )
        case SPIRV.Opcode.ConvertSToF | SPIRV.Opcode.ConvertUToF =>
          ConversionSemantics.toFloating(
            algebra,
            sinks,
            opcode,
            sinks.width(source),
            sinks.floatingWidth(ids.resultType),
            sinks.rounding(ids.result, IEEE754.Rounding.NearestEven)
          )(ids)(otherwise)
        case SPIRV.Opcode.ConvertFToS =>
          ConversionSemantics.toSigned(
            algebra,
            sinks,
            sinks.floatingWidth(source),
            sinks.width(ids.resultType),
            sinks.rounding(ids.result, IEEE754.Rounding.TowardZero),
            sinks.saturates(ids.result)
          )(ids)
        case SPIRV.Opcode.ConvertFToU =>
          ConversionSemantics.toUnsigned(
            algebra,
            sinks,
            sinks.floatingWidth(source),
            sinks.width(ids.resultType),
            sinks.rounding(ids.result, IEEE754.Rounding.TowardZero)
          )(ids)
        case SPIRV.Opcode.FConvert =>
          val width  = sinks.floatingWidth(source)
          val result = sinks.floatingWidth(ids.resultType)
          ConversionSemantics.toFloatingWidth(
            algebra,
            sinks,
            width,
            result,
            if (result >= width) IEEE754.Rounding.NearestEven
            else sinks.rounding(ids.result, IEEE754.Rounding.NearestEven)
          )(ids)
        case SPIRV.Opcode.QuantizeToF16 => ConversionSemantics.quantize(algebra, sinks)(ids)
        case _                          => otherwise
      }
    }

    /** The values atomic read-modify-writes store. */
    def atomic: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      opcode match {
        case SPIRV.Opcode.AtomicFAddEXT | SPIRV.Opcode.AtomicFMinEXT | SPIRV.Opcode.AtomicFMaxEXT =>
          AtomicSemantics.floating(algebra, sinks, opcode, sinks.floatingWidth(ids.resultType))(ids)(otherwise)
        case SPIRV.Opcode.AtomicCompareExchange | SPIRV.Opcode.AtomicCompareExchangeWeak =>
          AtomicSemantics.compareExchange(algebra, sinks, sinks.width(ids.resultType))(ids)
        case _ =>
          val width = sinks.width(ids.resultType)
          AtomicSemantics.binary(algebra, sinks, opcode, width)(ids)(
            AtomicSemantics.unary(algebra, sinks, opcode, width)(ids)(otherwise)
          )
      }
    }

    /** Pointer comparisons and differences. */
    def pointer: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      if (opcode == SPIRV.Opcode.PtrDiff) PointerSemantics.difference(algebra, sinks, sinks.width(ids.resultType))(ids)
      else PointerSemantics.binary(algebra, sinks, opcode)(ids)(otherwise)
    }

    /** Integer dot products; the packed forms carry a packed vector format literal. */
    def integerDot: R = {
      val opcode       = instruction.opcodeNumber.toInt
      val ids          = operandIds(instruction)
      val width        = sinks.width(ids.resultType)
      val accumulating = ids.operands.size == 3
      val packed       = instruction.operands.size > ids.operands.size + 2
      if (packed) {
        if (accumulating) IntegerDotSemantics.packedAccumulate(algebra, sinks, opcode, width)(ids)(otherwise)
        else IntegerDotSemantics.packed(algebra, sinks, opcode, width)(ids)(otherwise)
      } else {
        val component = sinks.width(sinks.typeOf(ids(0)))
        if (accumulating) IntegerDotSemantics.vectorAccumulate(algebra, sinks, opcode, component, width)(ids)(otherwise)
        else IntegerDotSemantics.vector(algebra, sinks, opcode, component, width)(ids)(otherwise)
      }
    }

    /** SPV_KHR_fma's fused multiply-add, rounded once. */
    def fusedMultiplyAdd: R = {
      val ids = operandIds(instruction)
      if (instruction.opcodeNumber.toInt != SPIRV.Opcode.FmaKHR) otherwise
      else
        OpenCLSemantics.floatingTernary(algebra, sinks, OpenCL.Fma, sinks.floatingWidth(ids.resultType))(ids)(otherwise)
    }

    /** SPV_EXT_arithmetic_fence's fence, which passes its operand through: it only forbids reassociating across it. */
    def fence: R =
      if (instruction.opcodeNumber.toInt != SPIRV.Opcode.ArithmeticFenceEXT) otherwise
      else sinks.unary(operandIds(instruction))(a => a)

    /** SPV_INTEL_shader_integer_functions2 and SPV_INTEL_ternary_bitwise_function. */
    def intelInteger: R = {
      val opcode = instruction.opcodeNumber.toInt
      val ids    = operandIds(instruction)
      val width  = sinks.width(ids.resultType)
      if (opcode == SPIRV.Opcode.BitwiseFunctionINTEL) IntelSemantics.bitwiseFunction(algebra, sinks, width)(ids)
      else
        IntelSemantics.integerUnary(algebra, sinks, opcode, width)(ids)(
          IntelSemantics.integerBinary(algebra, sinks, opcode, width)(ids)(otherwise)
        )
    }

    /** SPV_INTEL_bfloat16_conversion and SPV_INTEL_tensor_float32_conversion. */
    def intelConversion: R =
      IntelSemantics.conversion(algebra, sinks, instruction.opcodeNumber.toInt)(operandIds(instruction))(otherwise)

    /** SPV_VALVE_mixed_float_dot_product over binary16 lanes. */
    def mixedDot: R =
      MixedDotSemantics.dot(algebra, sinks, instruction.opcodeNumber.toInt)(operandIds(instruction))(otherwise)

    /** SPV_KHR_poison_freeze's freeze, which passes its operand through: the VM never produces poison. */
    def freeze: R =
      if (instruction.opcodeNumber.toInt != SPIRV.Opcode.FreezeKHR) otherwise
      else sinks.unary(operandIds(instruction))(a => a)

    /** OpBitcastExtractEXT's bit field of a wider base. */
    def bitcastExtract: R =
      if (instruction.opcodeNumber.toInt != SPIRV.Opcode.BitcastExtractEXT) otherwise
      else {
        val ids = operandIds(instruction)
        ConversionSemantics.bitcastExtract(
          algebra,
          sinks,
          sinks.width(sinks.typeOf(ids(0))),
          sinks.width(ids.resultType)
        )(
          ids
        )
      }

    /** Cooperative matrix multiply-accumulate: SPV_KHR_cooperative_matrix's, whose operands mask carries signedness,
      * saturation and Intel's bfloat16 and TF32 component flags, and SPV_NV_cooperative_matrix's, whose component types
      * carry signedness.
      */
    def cooperativeMatrix: R = {
      val opcode = instruction.opcodeNumber.toInt
      if (opcode != SPIRV.Opcode.CooperativeMatrixMulAddKHR && opcode != SPIRV.Opcode.CooperativeMatrixMulAddNV)
        otherwise
      else {
        val ids   = operandIds(instruction)
        val types = Vec(sinks.typeOf(ids(0)), sinks.typeOf(ids(1)), sinks.typeOf(ids(2)), ids.resultType)
        def signedBit(index: Int): Int = if (sinks.signed(types(index))) 1 << index else 0
        val mask                       =
          if (opcode == SPIRV.Opcode.CooperativeMatrixMulAddNV) signedBit(0) | signedBit(1) | signedBit(2) |
            signedBit(3)
          else instruction.operands.lift(5).fold(0)(Operand.id(_))
        def element(index: Int, bfloat16: Int, tf32: Int): CooperativeMatrixSemantics.Element =
          CooperativeMatrixSemantics.element(
            sinks,
            types(index),
            mask,
            CooperativeMatrixSemantics.Flags(signed = 1 << index, bfloat16 = bfloat16, tf32 = tf32)
          )
        CooperativeMatrixSemantics.mulAdd(
          algebra,
          sinks,
          element(0, bfloat16 = 0x40, tf32 = 0x20),
          element(1, bfloat16 = 0x40, tf32 = 0x20),
          element(2, bfloat16 = 0x80, tf32 = 0),
          element(3, bfloat16 = 0x100, tf32 = 0),
          (mask & 0x10) != 0
        )(ids)
      }
    }

    family match {
      case CoreFamily.Integer             => integer
      case CoreFamily.BitField            => bitField
      case CoreFamily.Extended            => extended
      case CoreFamily.Comparison          => comparison
      case CoreFamily.Floating            => floating
      case CoreFamily.OrderedComparison   => orderedComparison
      case CoreFamily.UnorderedComparison => unorderedComparison
      case CoreFamily.Classification      => classification
      case CoreFamily.Conversion          => conversion
      case CoreFamily.Atomic              => atomic
      case CoreFamily.Pointer             => pointer
      case CoreFamily.IntegerDot          => integerDot
      case CoreFamily.FusedMultiplyAdd    => fusedMultiplyAdd
      case CoreFamily.Fence               => fence
      case CoreFamily.IntelInteger        => intelInteger
      case CoreFamily.IntelConversion     => intelConversion
      case CoreFamily.MixedDot            => mixedDot
      case CoreFamily.Freeze              => freeze
      case CoreFamily.BitcastExtract      => bitcastExtract
      case CoreFamily.CooperativeMatrix   => cooperativeMatrix
    }
  }

  /** As [[core]] over the GLSL.std.450 families. */
  inline def glsl[V, R](
      family: GLSLFamily,
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      instruction: Instruction
  )(otherwise: => R): R = {

    /** GLSL.std.450 integer instructions. */
    def integer: R = {
      val opcode = extendedOpcode(instruction, openCL = false)
      val ids    = operandIds(instruction, 4)
      val width  = sinks.width(ids.resultType)
      GLSLSemantics.integerUnary(algebra, sinks, opcode, width)(ids)(
        GLSLSemantics.integerBinary(algebra, sinks, opcode, width)(ids)(
          GLSLSemantics.integerTernary(algebra, sinks, opcode, width)(ids)(otherwise)
        )
      )
    }

    /** GLSL.std.450 trigonometric and hyperbolic functions. */
    def trigonometric: R = {
      val ids = operandIds(instruction, 4)
      GLSLSemantics.trigonometric(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = false),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** GLSL.std.450 magnitude, rounding, exponential and root functions. */
    def elementary: R = {
      val ids = operandIds(instruction, 4)
      GLSLSemantics.elementary(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = false),
        sinks.floatingWidth(ids.resultType)
      )(
        ids
      )(otherwise)
    }

    /** GLSL.std.450 floating-point functions of two and three operands, and Ldexp. */
    def floating: R = {
      val opcode = extendedOpcode(instruction, openCL = false)
      val ids    = operandIds(instruction, 4)
      val width  = sinks.floatingWidth(ids.resultType)
      if (opcode == Glsl.Ldexp) OpenCLSemantics.ldexp(algebra, sinks, width, sinks.width(sinks.typeOf(ids(1))))(ids)
      else
        GLSLSemantics.floatingBinary(algebra, sinks, opcode, width)(ids)(
          GLSLSemantics.floatingTernary(algebra, sinks, opcode, width)(ids)(otherwise)
        )
    }

    /** GLSL.std.450 Modf and Frexp, their struct forms, Length, Distance, Normalize, packing and unpacking. */
    def vector: R = {
      val opcode = extendedOpcode(instruction, openCL = false)
      val ids    = operandIds(instruction, 4)
      opcode match {
        case Glsl.Modf | Glsl.Frexp =>
          GLSLSemantics.decompose(
            algebra,
            sinks,
            opcode,
            sinks.floatingWidth(ids.resultType),
            sinks.width(sinks.memberType(sinks.typeOf(ids(1)), 0))
          )(ids)(otherwise)
        case Glsl.ModfStruct | Glsl.FrexpStruct =>
          GLSLSemantics.decompose(
            algebra,
            sinks,
            opcode,
            sinks.floatingWidth(sinks.typeOf(ids(0))),
            sinks.width(sinks.memberType(ids.resultType, 1))
          )(ids)(otherwise)
        case Glsl.Length    => OpenCLSemantics.length(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case Glsl.Normalize => OpenCLSemantics.normalize(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case Glsl.Distance  => GLSLSemantics.distance(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case _              =>
          GLSLSemantics.pack(algebra, sinks, opcode)(ids)(GLSLSemantics.unpack(algebra, sinks, opcode)(ids)(otherwise))
      }
    }

    /** GLSL.std.450 Cross, FaceForward, Reflect and Refract. */
    def geometric: R = {
      val opcode = extendedOpcode(instruction, openCL = false)
      val ids    = operandIds(instruction, 4)
      val width  = sinks.floatingWidth(ids.resultType)
      if (opcode == Glsl.Cross) GLSLSemantics.cross(algebra, sinks, width)(ids)
      else GLSLSemantics.projected(algebra, sinks, opcode, width)(ids)(otherwise)
    }

    family match {
      case GLSLFamily.Integer       => integer
      case GLSLFamily.Trigonometric => trigonometric
      case GLSLFamily.Elementary    => elementary
      case GLSLFamily.Floating      => floating
      case GLSLFamily.Vector        => vector
      case GLSLFamily.Geometric     => geometric
    }
  }

  /** As [[core]] over the OpenCL.std families. */
  inline def openCL[V, R](
      family: OpenCLFamily,
      inline algebra: Algebra[V],
      inline sinks: SPIRVSinks[V, R],
      instruction: Instruction
  )(otherwise: => R): R = {

    /** OpenCL.std integer instructions; upsampling reads halves of the result width. */
    def integer: R = {
      val opcode    = extendedOpcode(instruction, openCL = true)
      val ids       = operandIds(instruction, 4)
      val width     = sinks.width(ids.resultType)
      val upsampled = opcode == OpenCL.SUpsample || opcode == OpenCL.UUpsample
      OpenCLSemantics.integerUnary(algebra, sinks, opcode, width)(ids)(
        OpenCLSemantics.integerBinary(
          algebra,
          sinks,
          opcode,
          width,
          if (upsampled) sinks.width(sinks.typeOf(ids(0))) else width
        )(ids)(OpenCLSemantics.integerTernary(algebra, sinks, opcode, width)(ids)(otherwise))
      )
    }

    /** OpenCL.std inverse trigonometric and hyperbolic functions. */
    def inverseTrigonometric: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.inverseTrigonometric(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std trigonometric and hyperbolic functions. */
    def trigonometric: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.trigonometric(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std exponential, logarithmic and root functions. */
    def exponential: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.exponential(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std magnitude, rounding, error, gamma and angle functions. */
    def elementary: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.elementary(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std native_ functions. */
    def native: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.native(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(
        ids
      )(otherwise)
    }

    /** OpenCL.std floating-point functions of two operands evaluated in binary64. */
    def binary: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.floatingBinary(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std sign transfer, extrema, magnitude selection and adjacency. */
    def order: R = {
      val opcode = extendedOpcode(instruction, openCL = true)
      val ids    = operandIds(instruction, 4)
      val width  = sinks.floatingWidth(ids.resultType)
      opcode match {
        case OpenCL.Fmax => FloatingSemantics.extremum(algebra, sinks, maximum = true, width)(ids)
        case OpenCL.Fmin => FloatingSemantics.extremum(algebra, sinks, maximum = false, width)(ids)
        case _           => OpenCLSemantics.floatingOrder(algebra, sinks, opcode, width)(ids)(otherwise)
      }
    }

    /** OpenCL.std floating-point functions of three operands. */
    def ternary: R = {
      val ids = operandIds(instruction, 4)
      OpenCLSemantics.floatingTernary(
        algebra,
        sinks,
        extendedOpcode(instruction, openCL = true),
        sinks.floatingWidth(ids.resultType)
      )(ids)(otherwise)
    }

    /** OpenCL.std functions with integer operands or results, selections, pointer outputs, reductions and cross
      * products.
      */
    def mixed: R = {
      val opcode = extendedOpcode(instruction, openCL = true)
      val ids    = operandIds(instruction, 4)
      opcode match {
        case OpenCL.Ldexp =>
          OpenCLSemantics.ldexp(algebra, sinks, sinks.floatingWidth(ids.resultType), sinks.width(sinks.typeOf(ids(1))))(
            ids
          )
        case OpenCL.Pown | OpenCL.Rootn =>
          OpenCLSemantics.integerPower(
            algebra,
            sinks,
            opcode,
            sinks.floatingWidth(ids.resultType),
            sinks.width(sinks.typeOf(ids(1)))
          )(ids)(otherwise)
        case OpenCL.Ilogb     => OpenCLSemantics.ilogb(algebra, sinks, sinks.floatingWidth(sinks.typeOf(ids(0))))(ids)
        case OpenCL.Nan       => OpenCLSemantics.nan(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case OpenCL.Bitselect => OpenCLSemantics.bitselect(algebra, sinks)(ids)
        case OpenCL.Select    =>
          val condition = sinks.typeOf(ids(2))
          OpenCLSemantics.select(
            algebra,
            sinks,
            sinks.width(condition),
            sinks.lanes(ids.resultType) == 1 && sinks.lanes(condition) == 1
          )(ids)
        case OpenCL.Remquo => OpenCLSemantics.remquo(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case OpenCL.Cross  => GLSLSemantics.cross(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case OpenCL.Length | OpenCL.FastLength =>
          OpenCLSemantics.length(algebra, sinks, sinks.floatingWidth(sinks.typeOf(ids(0))))(ids)
        case OpenCL.Normalize | OpenCL.FastNormalize =>
          OpenCLSemantics.normalize(algebra, sinks, sinks.floatingWidth(ids.resultType))(ids)
        case OpenCL.Distance | OpenCL.FastDistance =>
          OpenCLSemantics.distance(algebra, sinks, sinks.floatingWidth(sinks.typeOf(ids(0))))(ids)
        case _ => OpenCLSemantics.pointer(algebra, sinks, opcode, sinks.floatingWidth(ids.resultType))(ids)(otherwise)
      }
    }

    /** OpenCL.std half-precision loads and stores. */
    def half: R = {
      val opcode = extendedOpcode(instruction, openCL = true)
      val ids    = operandIds(instruction, 4)
      opcode match {
        case OpenCL.VloadHalf | OpenCL.VloadHalfn | OpenCL.VloadaHalfn =>
          ConversionSemantics.halfLoad(algebra, sinks, aligned = opcode == OpenCL.VloadaHalfn)(ids)
        case store if store >= OpenCL.VstoreHalf && store <= OpenCL.VstoreaHalfnR =>
          val rounded = opcode == OpenCL.VstoreHalfR || opcode == OpenCL.VstoreHalfnR || opcode == OpenCL.VstoreaHalfnR
          ConversionSemantics.halfStore(
            algebra,
            sinks,
            sinks.floatingWidth(sinks.typeOf(ids(0))),
            if (rounded)
              ConversionSemantics.rounding(Operand.id(instruction.operands.last), IEEE754.Rounding.NearestEven)
            else IEEE754.Rounding.NearestEven,
            aligned = opcode >= OpenCL.VstoreaHalfn
          )(OperandIds(ids.resultType, ids.result, ids.operands.take(3)))
        case _ => otherwise
      }
    }

    family match {
      case OpenCLFamily.Integer              => integer
      case OpenCLFamily.InverseTrigonometric => inverseTrigonometric
      case OpenCLFamily.Trigonometric        => trigonometric
      case OpenCLFamily.Exponential          => exponential
      case OpenCLFamily.Elementary           => elementary
      case OpenCLFamily.Native               => native
      case OpenCLFamily.Binary               => binary
      case OpenCLFamily.Order                => order
      case OpenCLFamily.Ternary              => ternary
      case OpenCLFamily.Mixed                => mixed
      case OpenCLFamily.Half                 => half
    }
  }
}
