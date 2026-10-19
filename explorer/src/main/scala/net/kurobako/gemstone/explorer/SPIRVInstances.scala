package net.kurobako.gemstone.explorer

import SPIRVKernel.*
import java.util.concurrent.ConcurrentHashMap
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.isa.spirv.*
import net.kurobako.gemstone.semantics.render.*
import scala.annotation.tailrec
import scala.collection.concurrent
import scala.jdk.CollectionConverters.*

/** The staged SPIR-V instructions at the types each takes: every integer width or float width it supports, lane counts
  * from 2 to 4 where lanes combine, square and one non-square shape for matrices, and one width pair for each decorated
  * rounding. A lane-wise instruction takes scalars at each width and one two-lane vector, since every lane repeats the
  * scalar semantics.
  */
object SPIRVInstances {

  private def integer(width: Int, signed: Boolean): Shape =
    Shape.Scalar(Type.Integer(width.unsafe[Int in 1 ~ 64], signed))

  private def u(width: Int): Shape           = integer(width, signed = false)
  private def s(width: Int): Shape           = integer(width, signed = true)
  private def f(width: Int): Shape           = Shape.Scalar(Type.Float(width.unsafe[Int in 1 ~ 64]))
  private def f8(format: Minifloat): Shape   = Shape.Scalar(Type.Encoded(format))
  private val bool: Shape                    = Shape.Scalar(Type.Bool)
  private def p(pointee: Shape): Shape       = Shape.Pointer(pointee)
  private def struct(members: Shape*): Shape = Shape.Structure(Vec.from(members))

  extension (lane: Shape) {
    private def vector(count: Int): Shape                             = Shape.Vector(lane.scalar, count)
    private def matrix(rows: Int, columns: Int): Shape                = Shape.Matrix(lane.scalar, rows, columns)
    private def cooperative(rows: Int, columns: Int, use: Int): Shape =
      Shape.Cooperative(lane.scalar, rows, columns, use)
  }

  private case class CooperativeUse(left: Int, right: Int, accumulator: Int)

  private val KHR = CooperativeUse(left = 0, right = 1, accumulator = 2)
  private val NV  = CooperativeUse(left = -1, right = -1, accumulator = -1)

  private val TileRows    = 2
  private val TileDepth   = 3
  private val TileColumns = 2

  private def mulAdd(use: CooperativeUse)(accumulator: Shape, left: Shape, right: Shape): Signature =
    sig(
      accumulator.cooperative(TileRows, TileColumns, use.accumulator),
      left.cooperative(TileRows, TileDepth, use.left),
      right.cooperative(TileDepth, TileColumns, use.right),
      accumulator.cooperative(TileRows, TileColumns, use.accumulator)
    )

  private def sig(result: Shape, operands: Shape*): Signature = Signature(Some(result), Vec.from(operands))
  private def void(operands: Shape*): Signature               = Signature(None, Vec.from(operands))

  private val integers       = Vec(8, 16, 32, 64)
  private val floats         = Vec(16, 32, 64)
  private val float8s        = Vec(Minifloat.E4M3, Minifloat.E5M2)
  private val lanes          = Vec(2, 3, 4)
  private val crossLanes     = lanes.drop(1)
  private val squareSizes    = lanes.take(2)
  private val storedWidths   = floats.drop(1)
  private val matrixVectors  = Vec((2, 2), (3, 3), (2, 3))
  private val matrixProducts = Vec((2, 2, 2), (3, 3, 3), (2, 3, 2))
  private val outerProducts  = Vec((2, 2), (3, 2))
  private val operandLabels  = Vec("A", "B", "C")
  private val modes          = Vec(
    IEEE754.Rounding.NearestEven,
    IEEE754.Rounding.TowardZero,
    IEEE754.Rounding.Up,
    IEEE754.Rounding.Down
  )

  private def pairs(widths: Vec[Int], results: Vec[Int]): Vec[(Int, Int)] = for {
    width  <- widths
    result <- results if result != width
  } yield (width, result)

  private def show(shape: Shape): String = shape match {
    case Shape.Scalar(Type.Integer(width, true))          => s"int$width"
    case Shape.Scalar(Type.Integer(width, false))         => s"uint$width"
    case Shape.Scalar(Type.Float(width))                  => s"float$width"
    case Shape.Scalar(Type.Encoded(format))               => Type.name(format)
    case Shape.Scalar(_)                                  => "bool"
    case Shape.Vector(component, count)                   => s"${show(Shape.Scalar(component))}[$count]"
    case Shape.Matrix(component, rows, columns)           => s"${show(Shape.Scalar(component))}[${rows}x$columns]"
    case Shape.Structure(members)                         => members.map(show(_)).mkString("{", ", ", "}")
    case Shape.Pointer(pointee)                           => s"*${show(pointee)}"
    case Shape.Cooperative(component, rows, columns, use) =>
      s"${show(Shape.Scalar(component))}[${rows}x$columns${if (use < 0) "" else s" ${operandLabels(use)}"}]"
  }

  /** The grammar's name of each operand of `instruction`, in grammar order, which only the generated instruction cases
    * carry.
    */
  @SuppressWarnings(Array("scalafix:BannedSyntax.product-traversal"))
  def operandNames(instruction: Instruction): Vec[String] = instruction.productElementNames.toVec

  /** The grammar's name of each id `instruction` binds, by the operand that binds it. */
  private def idNames(instruction: Instruction): Map[Int, String] =
    operandNames(instruction).zip(instruction.operands).collect {
      case (name, Operand(kind, words)) if kind.category == OperandKind.Category.Id => words.head.raw -> name
    }.toMap

  private def textOf(mnemonic: String, signature: Signature): String =
    s"$mnemonic(${signature.operands.map(show(_)).mkString(", ")}): ${signature.result.fold("void")(show(_))}" +
      (if (signature.tag.isEmpty) "" else s" ${signature.tag}") +
      signature.rounding.fold("")(mode => s" ${rounding(mode)}") + (if (signature.saturated) " saturated" else "")

  private def unsignedBinary(widths: Vec[Int]): Vec[Signature] = widths.map(width => sig(u(width), u(width), u(width)))
  private def signedBinary(widths: Vec[Int]): Vec[Signature]   = widths.map(width => sig(s(width), s(width), s(width)))
  private def floatUnary: Vec[Signature]                       = floats.map(width => sig(f(width), f(width)))
  private def floatBinary: Vec[Signature]                      = floats.map(width => sig(f(width), f(width), f(width)))
  private def floatTernary: Vec[Signature] = floats.map(width => sig(f(width), f(width), f(width), f(width)))

  private def float8Dots: Vec[Signature] = for {
    left  <- float8s
    right <- float8s
  } yield sig(f(32), f8(left).vector(4), f8(right).vector(4), f(32))

  private def selects: Vec[Signature] = (integers.map(u(_)) ++ floats.map(f(_)) :+ bool).map(value =>
    sig(value, bool, value, value)
  )

  private def atomic(values: Vec[Shape]): Vec[Signature] =
    values.map(value => sig(value, p(value), u(32), u(32), value))

  private def unsignedAtomic: Vec[Signature] = atomic(Vec(u(32), u(64)))
  private def signedAtomic: Vec[Signature]   = atomic(Vec(s(32), s(64)))

  private def shader(signature: Signature): Signature = signature.copy(shader = true)

  private def packed(signature: Signature): Signature =
    signature.copy(literals = Vec(0), tag = "PackedVectorFormat4x8Bit")

  private val core: Vec[(String, Vec[Signature])] = Vec(
    "OpIAdd"                 -> unsignedBinary(integers),
    "OpISub"                 -> unsignedBinary(integers),
    "OpIMul"                 -> unsignedBinary(integers),
    "OpUDiv"                 -> unsignedBinary(integers),
    "OpUMod"                 -> unsignedBinary(integers),
    "OpSDiv"                 -> signedBinary(integers),
    "OpSRem"                 -> signedBinary(integers),
    "OpSMod"                 -> signedBinary(integers),
    "OpBitwiseOr"            -> unsignedBinary(integers),
    "OpBitwiseXor"           -> unsignedBinary(integers),
    "OpBitwiseAnd"           -> unsignedBinary(integers),
    "OpShiftLeftLogical"     -> unsignedBinary(integers),
    "OpShiftRightLogical"    -> unsignedBinary(integers),
    "OpShiftRightArithmetic" -> integers.map(width => sig(s(width), s(width), u(width))),
    "OpSNegate"              -> integers.map(width => sig(s(width), s(width))),
    "OpNot"                  -> integers.map(width => sig(u(width), u(width))),
    "OpBitCount"             -> integers.map(width => sig(u(width), u(width))),
    "OpBitReverse"           -> integers.map(width => sig(u(width), u(width))),
    "OpBitFieldInsert"       -> integers.map(width => shader(sig(u(width), u(width), u(width), u(32), u(32)))),
    "OpBitFieldSExtract"     -> integers.map(width => shader(sig(s(width), s(width), u(32), u(32)))),
    "OpBitFieldUExtract"     -> integers.map(width => shader(sig(u(width), u(width), u(32), u(32)))),
    "OpIAddCarry"            -> integers.map(width => sig(struct(u(width), u(width)), u(width), u(width))),
    "OpISubBorrow"           -> integers.map(width => sig(struct(u(width), u(width)), u(width), u(width))),
    "OpUMulExtended"         -> integers.map(width => sig(struct(u(width), u(width)), u(width), u(width))),
    "OpSMulExtended"         -> integers.map(width => sig(struct(s(width), s(width)), s(width), s(width))),
    "OpLogicalEqual"         -> Vec(sig(bool, bool, bool)),
    "OpLogicalNotEqual"      -> Vec(sig(bool, bool, bool)),
    "OpLogicalOr"            -> Vec(sig(bool, bool, bool)),
    "OpLogicalAnd"           -> Vec(sig(bool, bool, bool)),
    "OpLogicalNot"           -> Vec(sig(bool, bool)),
    "OpIEqual"               -> integers.map(width => sig(bool, u(width), u(width))),
    "OpINotEqual"            -> integers.map(width => sig(bool, u(width), u(width))),
    "OpULessThan"            -> integers.map(width => sig(bool, u(width), u(width))),
    "OpUGreaterThan"         -> integers.map(width => sig(bool, u(width), u(width))),
    "OpULessThanEqual"       -> integers.map(width => sig(bool, u(width), u(width))),
    "OpUGreaterThanEqual"    -> integers.map(width => sig(bool, u(width), u(width))),
    "OpSLessThan"            -> integers.map(width => sig(bool, s(width), s(width))),
    "OpSGreaterThan"         -> integers.map(width => sig(bool, s(width), s(width))),
    "OpSLessThanEqual"       -> integers.map(width => sig(bool, s(width), s(width))),
    "OpSGreaterThanEqual"    -> integers.map(width => sig(bool, s(width), s(width))),
    "OpSelect"               -> selects,
    "OpAny"                  -> lanes.map(count => sig(bool, bool.vector(count))),
    "OpAll"                  -> lanes.map(count => sig(bool, bool.vector(count))),
    "OpFAdd"                 -> floatBinary,
    "OpFSub"                 -> floatBinary,
    "OpFMul"                 -> floatBinary,
    "OpFDiv"                 -> floatBinary,
    "OpFRem"                 -> floatBinary,
    "OpFMod"                 -> floatBinary,
    "OpFNegate"              -> floatUnary,
    "OpFmaKHR"               -> floatTernary,
    "OpDot"                  ->
      floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count), f(width).vector(count)))),
    "OpVectorTimesScalar" ->
      floats.flatMap(width => lanes.map(count => sig(f(width).vector(count), f(width).vector(count), f(width)))),
    "OpMatrixTimesScalar" -> floats.flatMap(width =>
      squareSizes.map(size => shader(sig(f(width).matrix(size, size), f(width).matrix(size, size), f(width))))
    ),
    "OpMatrixTimesVector" -> floats.flatMap(width =>
      matrixVectors.map((rows, columns) =>
        shader(sig(f(width).vector(rows), f(width).matrix(rows, columns), f(width).vector(columns)))
      )
    ),
    "OpVectorTimesMatrix" -> floats.flatMap(width =>
      matrixVectors.map((rows, columns) =>
        shader(sig(f(width).vector(columns), f(width).vector(rows), f(width).matrix(rows, columns)))
      )
    ),
    "OpMatrixTimesMatrix" -> floats.flatMap(width =>
      matrixProducts.map((rows, inner, columns) =>
        shader(sig(f(width).matrix(rows, columns), f(width).matrix(rows, inner), f(width).matrix(inner, columns)))
      )
    ),
    "OpOuterProduct" -> floats.flatMap(width =>
      outerProducts.map((rows, columns) =>
        shader(sig(f(width).matrix(rows, columns), f(width).vector(rows), f(width).vector(columns)))
      )
    ),
    "OpFOrdEqual"              -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFOrdNotEqual"           -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFOrdLessThan"           -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFOrdGreaterThan"        -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFOrdLessThanEqual"      -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFOrdGreaterThanEqual"   -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordEqual"            -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordNotEqual"         -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordLessThan"         -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordGreaterThan"      -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordLessThanEqual"    -> floats.map(width => sig(bool, f(width), f(width))),
    "OpFUnordGreaterThanEqual" -> floats.map(width => sig(bool, f(width), f(width))),
    "OpOrdered"                -> floats.map(width => sig(bool, f(width), f(width))),
    "OpUnordered"              -> floats.map(width => sig(bool, f(width), f(width))),
    "OpIsNan"                  -> floats.map(width => sig(bool, f(width))),
    "OpIsInf"                  -> floats.map(width => sig(bool, f(width))),
    "OpIsFinite"               -> floats.map(width => sig(bool, f(width))),
    "OpIsNormal"               -> floats.map(width => sig(bool, f(width))),
    "OpSignBitSet"             -> floats.map(width => sig(bool, f(width))),
    "OpUConvert"               -> pairs(integers, integers).map((width, result) => sig(u(result), u(width))),
    "OpSConvert"               -> pairs(integers, integers).map((width, result) => sig(s(result), s(width))),
    "OpBitcast"                -> floats.flatMap(width => Vec(sig(u(width), f(width)), sig(f(width), u(width)))),
    "OpConvertSToF"            ->
      (integers.flatMap(width => floats.map(result => sig(f(result), s(width)))) ++
        modes.map(sig(f(32), s(64)).rounded(_))),
    "OpConvertUToF" ->
      (integers.flatMap(width => floats.map(result => sig(f(result), u(width)))) ++
        modes.map(sig(f(32), u(64)).rounded(_))),
    "OpConvertFToS" ->
      (floats.flatMap(width => integers.map(result => sig(s(result), f(width)))) ++
        floats.flatMap(width => integers.map(result => sig(s(result), f(width)).copy(saturated = true))) ++
        modes.map(sig(s(32), f(32)).rounded(_))),
    "OpConvertFToU" ->
      (floats.flatMap(width => integers.map(result => shader(sig(u(result), f(width))))) ++
        modes.map(shader(sig(u(32), f(32))).rounded(_))),
    "OpFConvert" ->
      (pairs(floats, floats).map((width, result) => sig(f(result), f(width))) ++
        Vec((32, 16), (64, 16), (64, 32)).flatMap((width, result) => modes.map(sig(f(result), f(width)).rounded(_)))),
    "OpQuantizeToF16"         -> Vec(sig(f(32), f(32))),
    "OpArithmeticFenceEXT"    -> (floatUnary ++ integers.map(width => sig(u(width), u(width)))),
    "OpSatConvertSToU"        -> integers.flatMap(width => integers.map(result => sig(u(result), s(width)))),
    "OpSatConvertUToS"        -> integers.flatMap(width => integers.map(result => sig(s(result), u(width)))),
    "OpLessOrGreater"         -> floats.map(width => sig(bool, f(width), f(width))),
    "OpAtomicExchange"        -> unsignedAtomic,
    "OpAtomicIAdd"            -> unsignedAtomic,
    "OpAtomicISub"            -> unsignedAtomic,
    "OpAtomicSMin"            -> signedAtomic,
    "OpAtomicUMin"            -> unsignedAtomic,
    "OpAtomicSMax"            -> signedAtomic,
    "OpAtomicUMax"            -> unsignedAtomic,
    "OpAtomicAnd"             -> unsignedAtomic,
    "OpAtomicOr"              -> unsignedAtomic,
    "OpAtomicXor"             -> unsignedAtomic,
    "OpAtomicIIncrement"      -> Vec(32, 64).map(width => sig(u(width), p(u(width)), u(32), u(32))),
    "OpAtomicIDecrement"      -> Vec(32, 64).map(width => sig(u(width), p(u(width)), u(32), u(32))),
    "OpAtomicCompareExchange" ->
      Vec(32, 64).map(width => sig(u(width), p(u(width)), u(32), u(32), u(32), u(width), u(width))),
    "OpAtomicCompareExchangeWeak" ->
      Vec(32, 64).map(width => sig(u(width), p(u(width)), u(32), u(32), u(32), u(width), u(width))),
    "OpAtomicFAddEXT" -> floats.map(width => sig(f(width), p(f(width)), u(32), u(32), f(width))),
    "OpAtomicFMinEXT" -> floats.map(width => sig(f(width), p(f(width)), u(32), u(32), f(width))),
    "OpAtomicFMaxEXT" -> floats.map(width => sig(f(width), p(f(width)), u(32), u(32), f(width))),
    "OpPtrEqual"      -> Vec(sig(bool, p(u(32)), p(u(32)))),
    "OpPtrNotEqual"   -> Vec(sig(bool, p(u(32)), p(u(32)))),
    "OpPtrDiff"       -> Vec(sig(s(64), p(u(32)), p(u(32))), sig(s(64), p(f(64)), p(f(64)))),
    "OpSDot"          -> Vec(
      sig(s(32), s(8).vector(4), s(8).vector(4)),
      sig(s(64), s(16).vector(4), s(16).vector(4)),
      packed(sig(s(32), u(32), u(32)))
    ),
    "OpUDot" -> Vec(
      sig(u(32), u(8).vector(4), u(8).vector(4)),
      sig(u(64), u(16).vector(4), u(16).vector(4)),
      packed(sig(u(32), u(32), u(32)))
    ),
    "OpSUDot" -> Vec(
      sig(s(32), s(8).vector(4), u(8).vector(4)),
      sig(s(64), s(16).vector(4), u(16).vector(4)),
      packed(sig(s(32), u(32), u(32)))
    ),
    "OpSDotAccSat" -> Vec(
      sig(s(32), s(8).vector(4), s(8).vector(4), s(32)),
      sig(s(64), s(16).vector(4), s(16).vector(4), s(64)),
      packed(sig(s(32), u(32), u(32), s(32)))
    ),
    "OpUDotAccSat" -> Vec(
      sig(u(32), u(8).vector(4), u(8).vector(4), u(32)),
      sig(u(64), u(16).vector(4), u(16).vector(4), u(64)),
      packed(sig(u(32), u(32), u(32), u(32)))
    ),
    "OpSUDotAccSat" -> Vec(
      sig(s(32), s(8).vector(4), u(8).vector(4), s(32)),
      sig(s(64), s(16).vector(4), u(16).vector(4), s(64)),
      packed(sig(s(32), u(32), u(32), s(32)))
    ),
    "OpUCountLeadingZerosINTEL"    -> Vec(sig(u(32), u(32))),
    "OpUCountTrailingZerosINTEL"   -> Vec(sig(u(32), u(32))),
    "OpAbsISubINTEL"               -> integers.map(width => sig(u(width), s(width), s(width))),
    "OpAbsUSubINTEL"               -> unsignedBinary(integers),
    "OpIAddSatINTEL"               -> signedBinary(integers),
    "OpUAddSatINTEL"               -> unsignedBinary(integers),
    "OpISubSatINTEL"               -> signedBinary(integers),
    "OpUSubSatINTEL"               -> unsignedBinary(integers),
    "OpIAverageINTEL"              -> signedBinary(integers),
    "OpUAverageINTEL"              -> unsignedBinary(integers),
    "OpIAverageRoundedINTEL"       -> signedBinary(integers),
    "OpUAverageRoundedINTEL"       -> unsignedBinary(integers),
    "OpIMul32x16INTEL"             -> Vec(sig(s(32), s(32), s(32))),
    "OpUMul32x16INTEL"             -> Vec(sig(u(32), u(32), u(32))),
    "OpBitwiseFunctionINTEL"       -> integers.map(width => sig(u(width), u(width), u(width), u(width), u(32))),
    "OpConvertFToBF16INTEL"        -> Vec(sig(u(16), f(32))),
    "OpConvertBF16ToFINTEL"        -> Vec(sig(f(32), u(16))),
    "OpRoundFToTF32INTEL"          -> Vec(sig(f(32), f(32))),
    "OpFDot2MixAcc32VALVE"         -> Vec(sig(f(32), f(16).vector(2), f(16).vector(2), f(32))),
    "OpFDot2MixAcc16VALVE"         -> Vec(sig(f(16), f(16).vector(2), f(16).vector(2), f(16))),
    "OpFDot4MixAcc32VALVE"         -> float8Dots,
    "OpFreezeKHR"                  -> (integers.map(width => sig(u(width), u(width))) ++ floatUnary :+ sig(bool, bool)),
    "OpCooperativeMatrixMulAddKHR" -> Vec(
      mulAdd(KHR)(f(32), f(16), f(16)),
      mulAdd(KHR)(f(16), f(16), f(16)),
      mulAdd(KHR)(u(32), u(8), u(8)),
      mulAdd(KHR)(s(32), s(8), s(8)).copy(literals = Vec(0x1f), tag = "signed saturating"),
      mulAdd(KHR)(f(32), u(16), u(16)).copy(literals = Vec(0x40), tag = "bfloat16"),
      mulAdd(KHR)(u(16), u(16), u(16)).copy(literals = Vec(0x1c0), tag = "bfloat16 accumulator"),
      mulAdd(KHR)(f(32), f(32), f(32)).copy(literals = Vec(0x20), tag = "TF32")
    ),
    "OpCooperativeMatrixMulAddNV" -> Vec(
      mulAdd(NV)(f(32), f(16), f(16)),
      mulAdd(NV)(u(32), u(8), u(8))
    ),
    "OpBitcastExtractEXT" ->
      Vec(sig(u(8), u(32), u(32)), sig(u(16), u(64), u(32)), sig(f(16), u(32), u(32)), sig(s(32), f(64), u(32)))
  )

  /** An extended instruction: its name in its set, its operand names in the set's grammar and its signatures. */
  private case class Extended(name: String, operands: Vec[String], signatures: Vec[Signature])

  private def extended(name: String, operands: String*)(signatures: Vec[Signature]): Extended =
    Extended(name, Vec.from(operands), signatures)

  private def each(names: String*)(operands: String*)(signatures: Vec[Signature]): Vec[Extended] =
    Vec.from(names).map(name => Extended(name, Vec.from(operands), signatures))

  private val geometry: Vec[Signature] =
    floats.flatMap(width =>
      lanes.map(count => sig(f(width).vector(count), f(width).vector(count), f(width).vector(count)))
    )

  private val glsl: Vec[Extended] = Vec(
    each("Round", "RoundEven", "Trunc", "FAbs", "FSign", "Floor", "Ceil", "Fract")("x")(floatUnary),
    Vec(extended("Radians", "degrees")(floatUnary), extended("Degrees", "radians")(floatUnary)),
    each("Sin", "Cos", "Tan", "Asin", "Acos", "Sinh", "Cosh", "Tanh", "Asinh", "Acosh", "Atanh")("x")(floatUnary),
    Vec(extended("Atan", "y_over_x")(floatUnary), extended("Atan2", "y", "x")(floatBinary)),
    each("Exp", "Log", "Exp2", "Log2", "Sqrt", "InverseSqrt")("x")(floatUnary),
    Vec(
      extended("Pow", "x", "y")(floatBinary),
      extended("SAbs", "x")(integers.map(width => sig(s(width), s(width)))),
      extended("SSign", "x")(integers.map(width => sig(s(width), s(width)))),
      extended("FindILsb", "Value")(Vec(sig(u(32), u(32)))),
      extended("FindUMsb", "Value")(Vec(sig(u(32), u(32)))),
      extended("FindSMsb", "Value")(Vec(sig(s(32), s(32))))
    ),
    each("FMin", "FMax", "NMin", "NMax")("x", "y")(floatBinary),
    each("UMin", "UMax")("x", "y")(unsignedBinary(integers)),
    each("SMin", "SMax")("x", "y")(signedBinary(integers)),
    each("FClamp", "NClamp")("x", "minVal", "maxVal")(floatTernary),
    Vec(
      extended("UClamp", "x", "minVal", "maxVal")(integers.map(width => sig(u(width), u(width), u(width), u(width)))),
      extended("SClamp", "x", "minVal", "maxVal")(integers.map(width => sig(s(width), s(width), s(width), s(width)))),
      extended("FMix", "x", "y", "a")(floatTernary),
      extended("Step", "edge", "x")(floatBinary),
      extended("SmoothStep", "edge0", "edge1", "x")(floatTernary),
      extended("Fma", "a", "b", "c")(floatTernary),
      extended("Modf", "x", "i")(floats.map(width => sig(f(width), f(width), p(f(width))))),
      extended("ModfStruct", "x")(floats.map(width => sig(struct(f(width), f(width)), f(width)))),
      extended("Frexp", "x", "exp")(floats.map(width => sig(f(width), f(width), p(s(32))))),
      extended("FrexpStruct", "x")(floats.map(width => sig(struct(f(width), s(32)), f(width)))),
      extended("Ldexp", "x", "exp")(floats.map(width => sig(f(width), f(width), s(32)))),
      extended("PackSnorm4x8", "v")(Vec(sig(u(32), f(32).vector(4)))),
      extended("PackUnorm4x8", "v")(Vec(sig(u(32), f(32).vector(4)))),
      extended("PackSnorm2x16", "v")(Vec(sig(u(32), f(32).vector(2)))),
      extended("PackUnorm2x16", "v")(Vec(sig(u(32), f(32).vector(2)))),
      extended("PackHalf2x16", "v")(Vec(sig(u(32), f(32).vector(2)))),
      extended("PackDouble2x32", "v")(Vec(sig(f(64), u(32).vector(2)))),
      extended("UnpackSnorm2x16", "p")(Vec(sig(f(32).vector(2), u(32)))),
      extended("UnpackUnorm2x16", "p")(Vec(sig(f(32).vector(2), u(32)))),
      extended("UnpackHalf2x16", "v")(Vec(sig(f(32).vector(2), u(32)))),
      extended("UnpackSnorm4x8", "p")(Vec(sig(f(32).vector(4), u(32)))),
      extended("UnpackUnorm4x8", "p")(Vec(sig(f(32).vector(4), u(32)))),
      extended("UnpackDouble2x32", "v")(Vec(sig(u(32).vector(2), f(64)))),
      extended("Length", "x")(floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count))))),
      extended(
        "Normalize",
        "x"
      )(floats.flatMap(width => lanes.map(count => sig(f(width).vector(count), f(width).vector(count))))),
      extended("Distance", "p0", "p1")(
        floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count), f(width).vector(count))))
      ),
      extended("Cross", "x", "y")(floats.map(width => sig(f(width).vector(3), f(width).vector(3), f(width).vector(3)))),
      extended("FaceForward", "N", "I", "Nref")(floats.flatMap(width =>
        lanes.map(count =>
          sig(f(width).vector(count), f(width).vector(count), f(width).vector(count), f(width).vector(count))
        )
      )),
      extended("Reflect", "I", "N")(geometry),
      extended("Refract", "I", "N", "eta")(floats.flatMap(width =>
        lanes.map(count => sig(f(width).vector(count), f(width).vector(count), f(width).vector(count), f(width)))
      ))
    )
  ).flatten

  private def native(names: String*)(operands: String*): Vec[Extended] = Vec.from(names).map(name =>
    Extended(name, Vec.from(operands), Vec(Signature(Some(f(32)), Vec.fill(operands.size)(f(32)))))
  )

  private enum HalfAccess derives CanEqual {
    case Load, Store, StoreRounded
  }

  private def halves(name: String, operands: Vec[String], access: HalfAccess): Extended = {
    val counts                              = if (name.contains("halfn")) lanes else Vec(1)
    def data(width: Int, count: Int): Shape = if (count == 1) f(width) else f(width).vector(count)
    Extended(
      name,
      operands,
      counts.flatMap(count =>
        if (access == HalfAccess.Load) Vec(sig(data(32, count), u(32), p(f(16))))
        else
          storedWidths.flatMap(width =>
            if (access == HalfAccess.StoreRounded)
              modes.indices.map(mode =>
                void(data(width, count), u(32), p(f(16))).copy(
                  literals = Vec(mode),
                  tag = SPIRVKernel.rounding(modes(mode))
                )
              )
            else Vec(void(data(width, count), u(32), p(f(16))))
          )
      )
    )
  }

  private val openCL: Vec[Extended] = Vec(
    each(
      "acos",
      "acosh",
      "acospi",
      "asin",
      "asinh",
      "asinpi",
      "atan",
      "atanh",
      "atanpi",
      "cbrt",
      "ceil"
    )("x")(floatUnary),
    each("cos", "cosh", "cospi", "erfc", "erf", "exp", "exp2", "exp10", "expm1", "fabs", "floor")("x")(floatUnary),
    each("lgamma", "log", "log2", "log10", "log1p", "logb", "rint", "round", "rsqrt", "sin", "sinh")("x")(floatUnary),
    each("sinpi", "sqrt", "tan", "tanh", "tanpi", "tgamma", "trunc", "sign")("x")(floatUnary),
    Vec(extended("degrees", "radians")(floatUnary), extended("radians", "degrees")(floatUnary)),
    Vec(extended("atan2", "y", "x")(floatBinary), extended("atan2pi", "y", "x")(floatBinary)),
    each("copysign", "fdim", "fmax", "fmin", "fmod", "hypot", "maxmag", "minmag", "nextafter", "pow", "powr")("x", "y")(
      floatBinary
    ),
    each("remainder", "fmax_common", "fmin_common")("x", "y")(floatBinary),
    Vec(
      extended("fma", "a", "b", "c")(floatTernary),
      extended("mad", "a", "b", "c")(floatTernary),
      extended("fclamp", "x", "minval", "maxval")(floatTernary),
      extended("mix", "x", "y", "a")(floatTernary),
      extended("step", "edge", "x")(floatBinary),
      extended("smoothstep", "edge0", "edge1", "x")(floatTernary),
      extended("fract", "x", "ptr")(floats.map(width => sig(f(width), f(width), p(f(width))))),
      extended("frexp", "x", "exp")(floats.map(width => sig(f(width), f(width), p(s(32))))),
      extended("modf", "x", "iptr")(floats.map(width => sig(f(width), f(width), p(f(width))))),
      extended("sincos", "x", "cosval")(floats.map(width => sig(f(width), f(width), p(f(width))))),
      extended("lgamma_r", "x", "signp")(floats.map(width => sig(f(width), f(width), p(s(32))))),
      extended("remquo", "x", "y", "quo")(floats.map(width => sig(f(width), f(width), f(width), p(s(32))))),
      extended("ilogb", "x")(floats.map(width => sig(s(32), f(width)))),
      extended("ldexp", "x", "k")(floats.map(width => sig(f(width), f(width), s(32)))),
      extended("pown", "x", "y")(floats.map(width => sig(f(width), f(width), s(32)))),
      extended("rootn", "x", "y")(floats.map(width => sig(f(width), f(width), s(32)))),
      extended("nan", "nancode")(floats.map(width => sig(f(width), u(width)))),
      extended("cross", "p0", "p1")(
        floats.flatMap(width =>
          crossLanes.map(count => sig(f(width).vector(count), f(width).vector(count), f(width).vector(count)))
        )
      ),
      extended("length", "p")(floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count))))),
      extended("fast_length", "p")(floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count))))),
      extended(
        "normalize",
        "p"
      )(floats.flatMap(width => lanes.map(count => sig(f(width).vector(count), f(width).vector(count))))),
      extended("fast_normalize", "p")(
        floats.flatMap(width => lanes.map(count => sig(f(width).vector(count), f(width).vector(count))))
      ),
      extended("distance", "p0", "p1")(
        floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count), f(width).vector(count))))
      ),
      extended("fast_distance", "p0", "p1")(
        floats.flatMap(width => lanes.map(count => sig(f(width), f(width).vector(count), f(width).vector(count))))
      ),
      extended("bitselect", "a", "b", "c")(
        integers.map(width => sig(u(width), u(width), u(width), u(width))) ++ floatTernary
      ),
      extended("select", "a", "b", "c")(
        integers.map(width => sig(u(width), u(width), u(width), u(width))) ++
          floats.map(width => sig(f(width), f(width), f(width), s(width))) :+
          sig(f(32).vector(4), f(32).vector(4), f(32).vector(4), s(32).vector(4))
      )
    ),
    native("half_cos", "half_exp", "half_exp2", "half_exp10", "half_log", "half_log2", "half_log10")("x"),
    native("half_recip", "half_rsqrt", "half_sin", "half_sqrt", "half_tan")("x"),
    native("half_divide", "half_powr")("x", "y"),
    native("native_cos", "native_exp", "native_exp2", "native_exp10", "native_log", "native_log2", "native_log10")("x"),
    native("native_recip", "native_rsqrt", "native_sin", "native_sqrt", "native_tan")("x"),
    native("native_divide", "native_powr")("x", "y"),
    Vec(
      extended("s_abs", "x")(integers.map(width => sig(u(width), s(width)))),
      extended("u_abs", "x")(integers.map(width => sig(u(width), u(width)))),
      extended("s_abs_diff", "x", "y")(integers.map(width => sig(u(width), s(width), s(width)))),
      extended("u_abs_diff", "x", "y")(unsignedBinary(integers))
    ),
    each("s_add_sat", "s_hadd", "s_rhadd", "s_max", "s_min", "s_mul_hi", "s_sub_sat")("x", "y")(signedBinary(integers)),
    each("u_add_sat", "u_hadd", "u_rhadd", "u_max", "u_min", "u_mul_hi", "u_sub_sat")("x", "y")(
      unsignedBinary(integers)
    ),
    each("clz", "ctz", "popcount")("x")(integers.map(width => sig(u(width), u(width)))),
    Vec(
      extended("rotate", "v", "i")(unsignedBinary(integers)),
      extended("s_clamp", "x", "minval", "maxval")(integers.map(width => sig(s(width), s(width), s(width), s(width)))),
      extended("u_clamp", "x", "minval", "maxval")(integers.map(width => sig(u(width), u(width), u(width), u(width)))),
      extended("s_mad_hi", "a", "b", "c")(integers.map(width => sig(s(width), s(width), s(width), s(width)))),
      extended("u_mad_hi", "a", "b", "c")(integers.map(width => sig(u(width), u(width), u(width), u(width)))),
      extended("s_mad_sat", "x", "y", "z")(integers.map(width => sig(s(width), s(width), s(width), s(width)))),
      extended("u_mad_sat", "x", "y", "z")(integers.map(width => sig(u(width), u(width), u(width), u(width)))),
      extended("s_upsample", "hi", "lo")(Vec(8, 16, 32).map(width => sig(s(width * 2), s(width), u(width)))),
      extended("u_upsample", "hi", "lo")(Vec(8, 16, 32).map(width => sig(u(width * 2), u(width), u(width)))),
      extended("s_mul24", "x", "y")(Vec(sig(s(32), s(32), s(32)))),
      extended("u_mul24", "x", "y")(Vec(sig(u(32), u(32), u(32)))),
      extended("s_mad24", "x", "y", "z")(Vec(sig(s(32), s(32), s(32), s(32)))),
      extended("u_mad24", "x", "y", "z")(Vec(sig(u(32), u(32), u(32), u(32)))),
      halves("vload_half", Vec("offset", "p"), HalfAccess.Load),
      halves("vload_halfn", Vec("offset", "p"), HalfAccess.Load),
      halves("vloada_halfn", Vec("offset", "p"), HalfAccess.Load),
      halves("vstore_half", Vec("data", "offset", "p"), HalfAccess.Store),
      halves("vstore_half_r", Vec("data", "offset", "p"), HalfAccess.StoreRounded),
      halves("vstore_halfn", Vec("data", "offset", "p"), HalfAccess.Store),
      halves("vstore_halfn_r", Vec("data", "offset", "p"), HalfAccess.StoreRounded),
      halves("vstorea_halfn", Vec("data", "offset", "p"), HalfAccess.Store),
      halves("vstorea_halfn_r", Vec("data", "offset", "p"), HalfAccess.StoreRounded)
    )
  ).flatten

  private val ResultTypeId = 1
  private val ResultId     = 2
  private val SetId        = 3

  /** The instance of `definition` at `signature`: ids from 1 in grammar order, with the result type and result first,
    * then the value operands, each shaped by the signature, and literal operands from its words.
    */
  private inline def instance(
      definition: Instruction.Definition,
      signature: Signature,
      inline names: Int => Option[String]
  ): Option[Instance] = {
    val operands = signature.operands
    val taken    = Vec.newBuilder[Int]
    @tailrec
    def take(index: Int, id: Int, literals: Vec[Int]): Unit =
      if (index < definition.shapes.length) {
        val shape    = definition.shapes(index)
        val special  = shape.kind == OperandKind.IdResultType || shape.kind == OperandKind.IdResult
        val isId     = shape.kind.category == OperandKind.Category.Id
        val repeated = !special && isId && shape.quantifier == OperandQuantifier.Repeated
        val single   = !special && isId && shape.quantifier == OperandQuantifier.One
        val next     =
          if (shape.kind == OperandKind.IdResultType) Vec(ResultTypeId)
          else if (shape.kind == OperandKind.IdResult) Vec(ResultId)
          else if (repeated) Vec.tabulate(operands.size)(id + _) ++ literals
          else if (single) Vec(id)
          else if (shape.quantifier == OperandQuantifier.One) Vec(literals.headOption.getOrElse(0))
          else literals.take(1)
        val ids  = if (repeated) operands.size else if (single) 1 else 0
        val rest =
          if (special || single) literals
          else if (repeated) Vec.empty[Int]
          else literals.drop(1)
        val _ = taken ++= next
        take(index + 1, id + ids, rest)
      }
    // each shape takes the next ids or literal words: the next id and the literals not yet taken
    take(0, ResultId + 1, signature.literals)
    val words = taken.result()
    definition.decoder(RawInstruction(definition.opcodeNumber, words.map(Word(_)))).toOption.map { instruction =>
      val values =
        SPIRVStaged.operandIds(instruction, if (definition == Instruction.Definition.ExtInst) 4 else 2)
          .operands.take(operands.size)
      val shapes = Map(ResultTypeId -> signature.result, ResultId -> signature.result).collect {
        case (id, Some(shape)) =>
          id -> shape
      } ++ values.zip(operands)
      val grammar = idNames(instruction)
      Instance(
        "",
        instruction,
        None,
        IdRenderer.Instance(
          (shapes.keys ++ grammar.keys).map(id => id -> names(id).orElse(grammar.get(id)).getOrElse(s"%$id")).toMap,
          shapes,
          signature.rounding.map(ResultId -> _).toMap,
          if (signature.saturated) Set(ResultId) else Set.empty
        ),
        signature
      )
    }
  }

  /** Instructions whose operands are not lanes of one vector: memory operations and packed dot products. */
  private val whole: Set[String] = Set(
    "OpPtrEqual",
    "OpPtrNotEqual",
    "OpPtrDiff",
    "OpSDot",
    "OpUDot",
    "OpSUDot",
    "OpSDotAccSat",
    "OpUDotAccSat",
    "OpSUDotAccSat",
    "OpFDot2MixAcc32VALVE",
    "OpFDot2MixAcc16VALVE",
    "OpFDot4MixAcc32VALVE"
  )

  /** Operands a lane-wise instruction reads as scalars whatever the lane count. */
  private val scalarOperands: Map[String, Set[Int]] =
    Map(
      "OpBitFieldInsert"       -> Set(2, 3),
      "OpBitFieldSExtract"     -> Set(1, 2),
      "OpBitFieldUExtract"     -> Set(1, 2),
      "OpBitwiseFunctionINTEL" -> Set(3),
      "OpBitcastExtractEXT"    -> Set(1)
    )

  /** A two-lane instance of a lane-wise instruction, from its signature at 32 bits where it has one, whose lanes run
    * the execution's lane loop where scalars run its scalar path.
    */
  private def lanewise(mnemonic: String, signatures: Vec[Signature]): Vec[Signature] = {
    def lanes(shape: Shape): Option[Shape] = shape match {
      case Shape.Scalar(tpe)                => Some(Shape.Vector(tpe, 2))
      case Shape.Pointer(Shape.Scalar(tpe)) => Some(Shape.Pointer(Shape.Vector(tpe, 2)))
      case Shape.Structure(members)         =>
        Option.when(members.forall(lanes(_).nonEmpty))(Shape.Structure(members.flatMap(lanes(_))))
      case _ => None
    }
    def wide(signature: Signature): Boolean = (signature.result.toVec ++ signature.operands).exists(shape =>
      shape.components("").exists(_.tpe match {
        case Type.Integer(width, _) => width == 32
        case Type.Float(width)      => width == 32
        case _                      => false
      })
    )
    val scalar = scalarOperands.getOrElse(mnemonic, Set.empty[Int])
    val chosen = signatures.find(wide(_)).orElse(signatures.headOption).filter(_ =>
      !whole(mnemonic) && !mnemonic.startsWith("OpAtomic") && !mnemonic.startsWith("vload") &&
        !mnemonic.startsWith("vstore")
    )
    chosen.flatMap { signature =>
      val operands = signature.operands.zipWithIndex.map((shape, index) =>
        if (scalar(index)) Some(shape) else lanes(shape)
      )
      val result = signature.result.map(lanes(_))
      Option.when(operands.forall(_.nonEmpty) && result.forall(_.nonEmpty))(
        signature.copy(result = result.flatten, operands = operands.flatten)
      )
    }.toVec
  }

  lazy val definitions: Map[String, Instruction.Definition] =
    Instruction.definitions.map(definition => definition.opcode.raw -> definition).toMap

  /** The mnemonic an extended instruction's entry goes by. */
  def mnemonic(set: String, name: String): String = s"$set $name"

  /** Builds of each staged instruction's instances, by the mnemonic of its entry. */
  private lazy val builders: Map[String, () => Vec[Instance]] = {
    val coreInstances = core.map { (mnemonic, signatures) =>
      (
        mnemonic,
        () =>
          (signatures ++ lanewise(mnemonic, signatures)).flatMap(signature =>
            definitions.get(mnemonic).flatMap(instance(_, signature, _ => None))
              .map(_.copy(text = textOf(mnemonic, signature)))
          )
      )
    }
    val extendedInstances = Vec("GLSL.std.450" -> glsl, "OpenCL.std" -> openCL).flatMap { (set, functions) =>
      functions.map { function =>
        (
          mnemonic(set, function.name),
          () => {
            val number = ExtendedInstruction.names(set)(function.name)
            (function.signatures ++ lanewise(function.name, function.signatures)).flatMap { signature =>
              val operands = function.operands
              instance(
                Instruction.Definition.ExtInst,
                signature.copy(literals = Vec(number) ++ signature.literals),
                id => if (id > SetId) operands.lift(id - SetId - 1) else Option.when(id == ResultId)("dest")
              ).map { found =>
                found.copy(
                  text = textOf(function.name, signature),
                  extendedSet = Some(set),
                  signature = signature
                )
              }
            }
          }
        )
      }
    }
    (coreInstances ++ extendedInstances).toMap
  }

  /** Signatures for an instruction the table does not list: its value operands and result at each of these types, or a
    * Boolean result, so the pipeline finds whatever stages it.
    */
  private def probes(count: Int): Vec[Signature] = Vec(u(32), s(32), f(32), bool).flatMap(value =>
    Vec(sig(value, Vec.fill(count)(value)*), sig(bool, Vec.fill(count)(value)*))
  ).distinct

  private def probed(mnemonic: String): Vec[Instance] = mnemonic.split(' ') match {
    case Array(set, name) =>
      (for {
        number   <- ExtendedInstruction.names.get(set).flatMap(_.get(name))
        operands <- ExtendedInstruction.grammar.get(set).flatMap(_.get(name)).map(_.operands)
      } yield probes(operands.size).flatMap { signature =>
        instance(
          Instruction.Definition.ExtInst,
          signature.copy(literals = Vec(number)),
          id => if (id > SetId) operands.lift(id - SetId - 1) else Option.when(id == ResultId)("dest")
        ).map(_.copy(text = textOf(name, signature), extendedSet = Some(set)))
      }).getOrElse(Vec.empty)
    case _ =>
      definitions.get(mnemonic).filter(_.shapes.take(2).map(_.kind) ==
        Vec(OperandKind.IdResultType, OperandKind.IdResult))
        .fold(Vec.empty[Instance]) { definition =>
          val values = definition.shapes.drop(2).count(shape =>
            shape.kind.category == OperandKind.Category.Id && shape.quantifier != OperandQuantifier.Optional
          )
          probes(values).flatMap(signature =>
            instance(definition, signature, _ => None).map(_.copy(text = textOf(mnemonic, signature)))
          )
        }
  }

  private val built: concurrent.Map[String, Vec[Instance]] = ConcurrentHashMap[String, Vec[Instance]]().asScala

  /** The instances of the entry `mnemonic`: the table's, or probes at scalar types where the table has none. */
  def instances(mnemonic: String): Vec[Instance] = built.getOrElse(
    mnemonic, {
      val computed = builders.get(mnemonic).fold(probed(mnemonic))(_())
      built.putIfAbsent(mnemonic, computed).getOrElse(computed)
    }
  )
}
