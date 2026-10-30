package net.kurobako.gemstone.isa.spirv

import ExtendedInstruction.GlslStd450 as Glsl
import ExtendedInstruction.OpenClStd as OpenCL
import StagedTesting.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.binary.Bits.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.*
import net.kurobako.gemstone.testing.Edges.*
import net.kurobako.gemstone.testing.Suite

/** Staged GLSL.std.450 and OpenCL.std semantics against the hand-written kernels they replaced. The math helpers the
  * kernels called, such as `sinPi` and the musl adaptations, moved unchanged and serve both sides.
  */
class StagedExtendedSuite extends Suite {
  private case class PointerOutcome(result: Double, stored: Long)

  private val ExponentEdges  = Seq(-1L, -2L, -40L, 1000L)
  private val PowerOpcodes   = Seq(OpenCL.Pown, OpenCL.Rootn)
  private val PointerOpcodes = Seq(OpenCL.Sincos, OpenCL.LgammaR, OpenCL.Frexp, OpenCL.Modf, OpenCL.Fract)
  private val ScalarModes    = Seq(true, false)

  private def signedOpcode(opcode: Int): Boolean = {
    import OpenCL.*
    opcode match {
      case `SAbsDiff` | `SAddSat` | `SRhadd` | `SClamp` | `SMadHi` | `SMadSat` | `SMulHi` | `SSubSat` | `SUpsample` |
          `SMad24` | `SMul24` => true
      case _ => false
    }
  }

  private def glslUnary(opcode: Int, width: Int in (16, 32, 64)): Long => Long =
    GLSLSemantics.trigonometric(Execute, Closures, opcode, width)(OperandIds.none)(
      GLSLSemantics.elementary(
        Execute,
        Closures,
        opcode,
        width
      )(OperandIds.none)(fail(s"GLSL opcode $opcode has no unary semantics"))
    ).asUnary

  private def glslBinary(opcode: Int, width: Int in (16, 32, 64)): (Long, Long) => Long =
    GLSLSemantics.floatingBinary(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"GLSL opcode $opcode has no binary semantics")).asBinary

  private def glslTernary(opcode: Int, width: Int in (16, 32, 64)): Kernel3 =
    GLSLSemantics.floatingTernary(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"GLSL opcode $opcode has no semantics")).asTernary

  private def openCLUnary(opcode: Int, width: Int in (16, 32, 64)): Long => Long =
    OpenCLSemantics.inverseTrigonometric(Execute, Closures, opcode, width)(OperandIds.none)(
      OpenCLSemantics.trigonometric(Execute, Closures, opcode, width)(OperandIds.none)(
        openCLUnaryRest(opcode, width)
      )
    ).asUnary

  private def openCLUnaryRest(opcode: Int, width: Int in (16, 32, 64)): Closures.Closure =
    OpenCLSemantics.exponential(Execute, Closures, opcode, width)(OperandIds.none)(
      OpenCLSemantics.elementary(Execute, Closures, opcode, width)(OperandIds.none)(
        OpenCLSemantics.native(
          Execute,
          Closures,
          opcode,
          width
        )(OperandIds.none)(fail(s"OpenCL opcode $opcode has no unary semantics"))
      )
    )

  private def openCLBinary(opcode: Int, width: Int in (16, 32, 64)): (Long, Long) => Long =
    OpenCLSemantics.floatingBinary(Execute, Closures, opcode, width)(OperandIds.none)(
      OpenCLSemantics.floatingOrder(
        Execute,
        Closures,
        opcode,
        width
      )(OperandIds.none)(fail(s"OpenCL opcode $opcode has no semantics"))
    ).asBinary

  private def openCLTernary(opcode: Int, width: Int in (16, 32, 64)): Kernel3 =
    OpenCLSemantics.floatingTernary(
      Execute,
      Closures,
      opcode,
      width
    )(OperandIds.none)(fail(s"OpenCL opcode $opcode has no semantics")).asTernary

  /** A binary64 function rounded to the width, as the replaced elementwise kernels evaluated it. */
  private def rounded1(width: Int in (16, 32, 64), function: Double => Double)(raw: Long): Long =
    fromDouble(width, function(toDouble(width, raw)))

  private def rounded2(width: Int in (16, 32, 64), function: (Double, Double) => Double)(a: Long, b: Long): Long =
    fromDouble(width, function(toDouble(width, a), toDouble(width, b)))

  private inline def rounded3(width: Int in (16, 32, 64), inline function: (Double, Double, Double) => Double)(
      a: Long,
      b: Long,
      c: Long
  ): Long = fromDouble(width, function(toDouble(width, a), toDouble(width, b), toDouble(width, c)))

  private def floatingMinimum(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.minNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  private def floatingMaximum(left: Double, right: Double): Double = IEEE754.binary64ToDouble(
    IEEE754.maxNum(IEEE754.doubleToBinary64(left), IEEE754.doubleToBinary64(right), 64)
  )

  test("GLSL integer instructions match the replaced kernel") {
    val opcodes = Seq(
      Glsl.SAbs,
      Glsl.SSign,
      Glsl.FindILsb,
      Glsl.FindSMsb,
      Glsl.FindUMsb,
      Glsl.UMin,
      Glsl.SMin,
      Glsl.UMax,
      Glsl.SMax,
      Glsl.UClamp,
      Glsl.SClamp
    )
    for {
      opcode <- opcodes
      width  <- integerWidths if opcode < Glsl.FindILsb || width == 32
    } {
      val signed                          = opcode == Glsl.SMin || opcode == Glsl.SMax || opcode == Glsl.SClamp
      val bits                            = mask(width)
      def less(a: Long, b: Long): Boolean =
        if (signed) signExtend(a, width) < signExtend(b, width) else java.lang.Long.compareUnsigned(a, b) < 0
      def minimum(a: Long, b: Long): Long            = if (less(b, a)) b else a
      def maximum(a: Long, b: Long): Long            = if (less(a, b)) b else a
      def reference(x: Long, y: Long, z: Long): Long = {
        val a     = x & bits
        val b     = y & bits
        val value = opcode match {
          case Glsl.SAbs     => if (signExtend(a, width) < 0) -a else a
          case Glsl.SSign    => java.lang.Long.signum(signExtend(a, width)).toLong
          case Glsl.FindILsb => if (a == 0) -1L else java.lang.Long.numberOfTrailingZeros(a).toLong
          case Glsl.FindSMsb =>
            (63 - java.lang.Long.numberOfLeadingZeros(if (signExtend(a, width) < 0) ~a & bits else a)).toLong
          case Glsl.FindUMsb         => (63 - java.lang.Long.numberOfLeadingZeros(a)).toLong
          case Glsl.UMin | Glsl.SMin => minimum(a, b)
          case Glsl.UMax | Glsl.SMax => maximum(a, b)
          case _                     => minimum(maximum(a, b), z & bits)
        }
        value & bits
      }
      val values          = integers(width)
      val staged: Kernel3 =
        if (opcode == Glsl.UClamp || opcode == Glsl.SClamp)
          GLSLSemantics.integerTernary(
            Execute,
            Closures,
            opcode,
            width
          )(OperandIds.none)(fail("no semantics")).asTernary
        else if (opcode != Glsl.SAbs && opcode != Glsl.SSign && opcode < Glsl.FindILsb)
          (a, b, _) =>
            GLSLSemantics.integerBinary(
              Execute,
              Closures,
              opcode,
              width
            )(OperandIds.none)(fail("no semantics")).asBinary(a, b)
        else (a, _, _) =>
          GLSLSemantics.integerUnary(Execute, Closures, opcode, width)(OperandIds.none)(fail("no semantics")).asUnary(a)
      assertEquals(mismatches(triples(values), staged.tupled, reference.tupled), Vector.empty, s"GLSL opcode $opcode")
    }
  }

  test("GLSL floating-point instructions match the replaced kernels") {
    for (width <- floatWidths) {
      def round(result: Double): Double       = toDouble(width, fromDouble(width, result))
      val unary: Seq[(Int, Double => Double)] = Seq(
        Glsl.Round       -> (Math.rint(_)),
        Glsl.RoundEven   -> (Math.rint(_)),
        Glsl.Trunc       -> (value => if (value < 0) Math.ceil(value) else Math.floor(value)),
        Glsl.FSign       -> (value => if (value < 0) -1.0 else if (value > 0) 1.0 else 0.0),
        Glsl.Floor       -> (Math.floor(_)),
        Glsl.Ceil        -> (Math.ceil(_)),
        Glsl.Fract       -> (value => value - Math.floor(value)),
        Glsl.Radians     -> (_ * (Math.PI / 180.0)),
        Glsl.Degrees     -> (_ * (180.0 / Math.PI)),
        Glsl.Sin         -> (Math.sin(_)),
        Glsl.Cos         -> (Math.cos(_)),
        Glsl.Tan         -> (Math.tan(_)),
        Glsl.Asin        -> (Math.asin(_)),
        Glsl.Acos        -> (Math.acos(_)),
        Glsl.Atan        -> (Math.atan(_)),
        Glsl.Sinh        -> (value => round(round(Math.exp(value)) - round(Math.exp(-value))) / 2.0),
        Glsl.Cosh        -> (value => round(round(Math.exp(value)) + round(Math.exp(-value))) / 2.0),
        Glsl.Tanh        -> (Math.tanh(_)),
        Glsl.Asinh       -> (OpenCLMath.asinh(_)),
        Glsl.Acosh       -> (OpenCLMath.acosh(_)),
        Glsl.Atanh       -> (OpenCLMath.atanh(_)),
        Glsl.Exp         -> (Math.exp(_)),
        Glsl.Log         -> (Math.log(_)),
        Glsl.Exp2        -> (Math.pow(2.0, _)),
        Glsl.Log2        -> (value => Math.log(value) / Math.log(2.0)),
        Glsl.Sqrt        -> (Math.sqrt(_)),
        Glsl.InverseSqrt -> (value => 1.0 / Math.sqrt(value))
      )
      val values = floats(width)
      for ((opcode, function) <- unary)
        assertEquals(
          mismatches(values, glslUnary(opcode, width), rounded1(width, function), same(width)),
          Vector.empty,
          s"GLSL opcode $opcode at $width bits"
        )
      val magnitude = (1L << (width - 1)) - 1L
      assertEquals(mismatches(values, glslUnary(Glsl.FAbs, width), _ & magnitude), Vector.empty)
      def minimum(a: Double, b: Double): Double          = if (a.isNaN) b else if (b.isNaN) a else Math.min(a, b)
      def maximum(a: Double, b: Double): Double          = if (a.isNaN) b else if (b.isNaN) a else Math.max(a, b)
      val binary: Seq[(Int, (Double, Double) => Double)] = Seq(
        Glsl.FMin  -> (minimum(_, _)),
        Glsl.NMin  -> (minimum(_, _)),
        Glsl.FMax  -> (maximum(_, _)),
        Glsl.NMax  -> (maximum(_, _)),
        Glsl.Atan2 -> (Math.atan2(_, _)),
        Glsl.Pow   -> (Math.pow(_, _)),
        Glsl.Step  -> ((edge, x) => if (x < edge) 0.0 else 1.0)
      )
      for ((opcode, function) <- binary)
        assertEquals(
          mismatches(pairs(values), glslBinary(opcode, width).tupled, rounded2(width, function), same(width)),
          Vector.empty,
          s"GLSL opcode $opcode at $width bits"
        )
      val ternary: Seq[(Int, (Double, Double, Double) => Double)] = Seq(
        Glsl.FClamp     -> ((a, b, c) => minimum(maximum(a, b), c)),
        Glsl.NClamp     -> ((a, b, c) => minimum(maximum(a, b), c)),
        Glsl.FMix       -> ((x, y, a) => x * (1.0 - a) + y * a),
        Glsl.SmoothStep -> { (low, high, x) =>
          val t = Math.max(0.0, Math.min(1.0, (x - low) / (high - low)))
          t * t * (3.0 - 2.0 * t)
        }
      )
      for ((opcode, function) <- ternary)
        assertEquals(
          mismatches(triples(values), glslTernary(opcode, width).tupled, rounded3(width, function), same(width)),
          Vector.empty,
          s"GLSL opcode $opcode at $width bits"
        )
      assertEquals(
        mismatches(
          triples(values),
          glslTernary(Glsl.Fma, width).tupled,
          IEEE754.fusedMultiplyAdd(_, _, _, width),
          same(width)
        ),
        Vector.empty
      )
    }
  }

  test("GLSL decomposition, lengths, distances and packing match the replaced kernels") {
    for {
      width   <- floatWidths
      integer <- integerWidths
    } {
      val values = floats(width)
      val modf   = GLSLSemantics.decompose(
        Execute,
        Closures,
        Glsl.ModfStruct,
        width,
        integer
      )(OperandIds.none)(fail("no modf")).asPair
      val frexp = GLSLSemantics.decompose(
        Execute,
        Closures,
        Glsl.FrexpStruct,
        width,
        integer
      )(OperandIds.none)(fail("no frexp")).asPair
      assertEquals(
        mismatches(
          values,
          raw => modf(raw),
          { raw =>
            val value   = toDouble(width, raw)
            val integer = if (value < 0.0) Math.ceil(value) else Math.floor(value)
            (
              fromDouble(
                width,
                if (value.isInfinity) Math.copySign(0.0, value) else Math.copySign(value - integer, value)
              ),
              fromDouble(width, integer)
            )
          },
          (left, right) => same(width)(left._1, right._1) && same(width)(left._2, right._2)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          values,
          raw => frexp(raw),
          { raw =>
            val value    = toDouble(width, raw)
            val exponent = if (value == 0.0 || !value.isFinite) 0
            else {
              val initial = Math.getExponent(value)
              (if (initial == -1023) Math.getExponent(Math.scalb(value, 54)) - 54 else initial) + 1
            }
            (fromDouble(width, Math.scalb(value, -exponent)), exponent.toLong & mask(integer))
          },
          (left, right) => same(width)(left._1, right._1) && left._2 == right._2
        ),
        Vector.empty
      )
    }
    for (width <- floatWidths) {
      val vectors  = floats(width).sliding(3, 2).filter(_.size == 3).toSeq
      val length   = OpenCLSemantics.length(Execute, Closures, width)(OperandIds.none).asFold
      val distance = GLSLSemantics.distance(Execute, Closures, width)(OperandIds.none).asBinaryFold
      assertEquals(
        mismatches(
          vectors,
          lanes => length(lanes),
          lanes => fromDouble(width, sumOver(lanes.size)((sum, lane) => Math.hypot(sum, toDouble(width, lanes(lane))))),
          same(width)
        ),
        Vector.empty
      )
      assertEquals(
        mismatches(
          vectors.zip(vectors.reverse),
          distance.tupled,
          (a, b) =>
            fromDouble(
              width,
              sumOver(a.size) { (sum, lane) =>
                Math.hypot(
                  sum,
                  toDouble(width, fromDouble(width, toDouble(width, a(lane)) - toDouble(width, b(lane))))
                )
              }
            ),
          same(width)
        ),
        Vector.empty
      )
    }
    val packs = Seq(Glsl.PackSnorm4x8, Glsl.PackUnorm4x8, Glsl.PackSnorm2x16, Glsl.PackUnorm2x16, Glsl.PackHalf2x16)
    for (opcode <- packs) {
      val count  = if (opcode == Glsl.PackSnorm4x8 || opcode == Glsl.PackUnorm4x8) 4 else 2
      val bits   = if (count == 4) 8 else 16
      val signed = opcode == Glsl.PackSnorm4x8 || opcode == Glsl.PackSnorm2x16
      val scale  = if (signed) (1 << (bits - 1)) - 1 else (1 << bits) - 1
      val lanes  = floats(32).sliding(count, 1).filter(_.size == count).toSeq
      val pack   = GLSLSemantics.pack(Execute, Closures, opcode)(OperandIds.none)(fail("no packing")).asFold
      assertEquals(
        mismatches(
          lanes,
          packed => pack(packed),
          _.zipWithIndex.foldLeft(0L) { case (packed, (raw, lane)) =>
            val value = IEEE754.binary32ToFloat(Binary32.low(raw))
            val word  =
              if (opcode == Glsl.PackHalf2x16) IEEE754.floatToBinary16(value).toLong
              else Math.round(Math.max(if (signed) -1.0 else 0.0, Math.min(1.0, value.toDouble)) * scale)
            packed | ((word & ((1L << bits) - 1)) << (bits * lane))
          }
        ),
        Vector.empty,
        s"GLSL opcode $opcode"
      )
    }
    val doubles = pairs(integers(32)).map((low, high) => Seq(low, high))
    val pack    = GLSLSemantics.pack(Execute, Closures, Glsl.PackDouble2x32)(OperandIds.none)(fail("no packing")).asFold
    assertEquals(
      mismatches(
        doubles,
        lanes => pack(lanes),
        lanes => (lanes(0) & 0xffffffffL) | (lanes(1) << 32)
      ),
      Vector.empty
    )
    val unpacks =
      Seq(Glsl.UnpackSnorm2x16, Glsl.UnpackUnorm2x16, Glsl.UnpackHalf2x16, Glsl.UnpackSnorm4x8, Glsl.UnpackUnorm4x8)
    for (opcode <- unpacks) {
      val count    = if (opcode == Glsl.UnpackSnorm4x8 || opcode == Glsl.UnpackUnorm4x8) 4 else 2
      val bits     = if (count == 4) 8 else 16
      val signed   = opcode == Glsl.UnpackSnorm2x16 || opcode == Glsl.UnpackSnorm4x8
      val unpacked = GLSLSemantics.unpack(Execute, Closures, opcode)(OperandIds.none)(fail("no unpacking")).asBinary
      val inputs   = for {
        word <- integers(32)
        lane <- 0 until count
      } yield (word, lane.toLong)
      assertEquals(
        mismatches(
          inputs,
          unpacked.tupled,
          { (packed, lane) =>
            val word  = (packed >>> (bits * lane.toInt)) & ((1L << bits) - 1)
            val value =
              if (opcode == Glsl.UnpackHalf2x16) IEEE754.binary16ToFloat(Binary16.low(word))
              else if (signed)
                Math.max(-1.0, ((word ^ (1L << (bits - 1))) - (1L << (bits - 1))).toDouble / ((1 << (bits - 1)) - 1))
                  .toFloat
              else (word.toDouble / ((1 << bits) - 1)).toFloat
            IEEE754.floatToBinary32(value).toLong
          },
          same(32)
        ),
        Vector.empty,
        s"GLSL opcode $opcode"
      )
    }
    val unpackDouble =
      GLSLSemantics.unpack(Execute, Closures, Glsl.UnpackDouble2x32)(OperandIds.none)(fail("no unpacking")).asBinary
    assertEquals(
      mismatches(
        integers(64).flatMap(word => Seq((word, 0L), (word, 1L))),
        unpackDouble.tupled,
        (word, lane) => if (lane == 0L) word & 0xffffffffL else word >>> 32
      ),
      Vector.empty
    )
  }

  private val openCLUnaries: Seq[(Int, Double => Double)] = Seq(
    OpenCL.Acos   -> (Math.acos(_)),
    OpenCL.Acosh  -> (OpenCLMath.acosh(_)),
    OpenCL.Acospi -> (value => Math.acos(value) / Math.PI),
    OpenCL.Asin   -> (Math.asin(_)),
    OpenCL.Asinh  -> (OpenCLMath.asinh(_)),
    OpenCL.Asinpi -> (value => Math.asin(value) / Math.PI),
    OpenCL.Atan   -> (Math.atan(_)),
    OpenCL.Atanh  -> (OpenCLMath.atanh(_)),
    OpenCL.Atanpi -> (value => Math.atan(value) / Math.PI),
    OpenCL.Cbrt   -> (Math.cbrt(_)),
    OpenCL.Ceil   -> (Math.ceil(_)),
    OpenCL.Cos    -> (Math.cos(_)),
    OpenCL.Cosh   -> (Math.cosh(_)),
    OpenCL.Cospi  -> (DoubleMath.cosPi(_)),
    OpenCL.Erf    -> (OpenCLMath.erf(_)),
    OpenCL.Erfc   -> (OpenCLMath.erfc(_)),
    OpenCL.Tgamma -> (OpenCLMath.tgamma(_)),
    OpenCL.Lgamma -> (OpenCLMath.lgamma(_)),
    OpenCL.Exp    -> (Math.exp(_)),
    OpenCL.Exp2   -> (value => Math.pow(2.0, value)),
    OpenCL.Exp10  -> (value => Math.pow(10.0, value)),
    OpenCL.Expm1  -> (Math.expm1(_)),
    OpenCL.Fabs   -> (Math.abs(_)),
    OpenCL.Floor  -> (Math.floor(_)),
    OpenCL.Log    -> (Math.log(_)),
    OpenCL.Log2   -> (value => Math.log(value) / Math.log(2.0)),
    OpenCL.Log10  -> (Math.log10(_)),
    OpenCL.Log1p  -> (Math.log1p(_)),
    OpenCL.Logb   -> (DoubleMath.logb(_)),
    OpenCL.Rint   -> (Math.rint(_)),
    OpenCL.Round  -> { value =>
      val whole = Math.floor(Math.abs(value))
      Math.copySign(if (Math.abs(value) - whole >= 0.5) whole + 1.0 else whole, value)
    },
    OpenCL.Rsqrt       -> (value => 1.0 / Math.sqrt(value)),
    OpenCL.Sin         -> (Math.sin(_)),
    OpenCL.Sinh        -> (Math.sinh(_)),
    OpenCL.Sinpi       -> (DoubleMath.sinPi(_)),
    OpenCL.Sqrt        -> (Math.sqrt(_)),
    OpenCL.Tan         -> (Math.tan(_)),
    OpenCL.Tanh        -> (Math.tanh(_)),
    OpenCL.Tanpi       -> (DoubleMath.tanPi(_)),
    OpenCL.Trunc       -> (value => if (value < 0.0) Math.ceil(value) else Math.floor(value)),
    OpenCL.NativeCos   -> (Math.cos(_)),
    OpenCL.NativeExp   -> (Math.exp(_)),
    OpenCL.NativeExp2  -> (value => Math.pow(2.0, value)),
    OpenCL.NativeExp10 -> (value => Math.pow(10.0, value)),
    OpenCL.NativeLog   -> (Math.log(_)),
    OpenCL.NativeLog2  -> (value => Math.log(value) / Math.log(2.0)),
    OpenCL.NativeLog10 -> (Math.log10(_)),
    OpenCL.NativeRecip -> (value => 1.0 / value),
    OpenCL.NativeRsqrt -> (value => 1.0 / Math.sqrt(value)),
    OpenCL.NativeSin   -> (Math.sin(_)),
    OpenCL.NativeSqrt  -> (Math.sqrt(_)),
    OpenCL.NativeTan   -> (Math.tan(_)),
    OpenCL.Degrees     -> (_ * (180.0 / Math.PI)),
    OpenCL.Radians     -> (_ * (Math.PI / 180.0)),
    OpenCL.Sign        -> (value => if (value > 0.0) 1.0 else if (value < 0.0) -1.0 else 0.0)
  )

  test("OpenCL floating-point functions match the replaced kernels") {
    for (width <- floatWidths) {
      val values = floats(width)
      for ((opcode, function) <- openCLUnaries) {
        val expected: Long => Long =
          if (opcode == OpenCL.Fabs) _ & ((1L << (width - 1)) - 1L) else rounded1(width, function)
        assertEquals(
          mismatches(values, openCLUnary(opcode, width), expected, same(width)),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
      }
      val sign                                     = 1L << (width - 1)
      val binary: Seq[(Int, (Long, Long) => Long)] = Seq(
        OpenCL.Atan2pi      -> rounded2(width, (a, b) => Math.atan2(a, b) / Math.PI),
        OpenCL.Fdim         -> rounded2(width, (a, b) => Math.max(a - b, 0.0)),
        OpenCL.Fmod         -> rounded2(width, _ % _),
        OpenCL.Remainder    -> rounded2(width, DoubleMath.remainder(_, _)),
        OpenCL.NativeDivide -> rounded2(width, _ / _),
        OpenCL.NativePowr   -> rounded2(width, DoubleMath.powr(_, _)),
        OpenCL.Powr         -> rounded2(width, DoubleMath.powr(_, _)),
        OpenCL.Atan2        -> rounded2(width, Math.atan2(_, _)),
        OpenCL.Hypot        -> rounded2(width, Math.hypot(_, _)),
        OpenCL.Pow          -> rounded2(width, Math.pow(_, _)),
        OpenCL.Step         -> rounded2(width, (edge, x) => if (x < edge) 0.0 else 1.0),
        OpenCL.FmaxCommon   -> rounded2(width, floatingMaximum(_, _)),
        OpenCL.FminCommon   -> rounded2(width, floatingMinimum(_, _)),
        OpenCL.Copysign     -> ((a, b) => (a & ~sign) | (b & sign)),
        OpenCL.Maxmag       -> rounded2(width, maximumMagnitude(_, _)),
        OpenCL.Minmag       -> rounded2(width, minimumMagnitude(_, _)),
        OpenCL.Nextafter    -> nextAfter(width)
      )
      for ((opcode, expected) <- binary)
        assertEquals(
          mismatches(pairs(values), openCLBinary(opcode, width).tupled, expected.tupled, same(width)),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
      def round(value: Double): Double                    = toDouble(width, fromDouble(width, value))
      val ternary: Seq[(Int, (Long, Long, Long) => Long)] = Seq(
        OpenCL.Fma -> (IEEE754.fusedMultiplyAdd(_, _, _, width)),
        OpenCL.Mad ->
          ((a, b, c) =>
            IEEE754.add(IEEE754.multiply(a, b, width, IEEE754.NaNOperand.Left), c, width, IEEE754.NaNOperand.Left)
          ),
        OpenCL.Fclamp     -> rounded3(width, (a, b, c) => floatingMinimum(floatingMaximum(a, b), c)),
        OpenCL.Mix        -> rounded3(width, (a, b, c) => a + (b - a) * c),
        OpenCL.Smoothstep -> rounded3(
          width,
          { (low, high, x) =>
            val ratio = round(round(x - low) / round(high - low))
            val t     = floatingMinimum(floatingMaximum(ratio, 0.0), 1.0)
            round(t * t) * round(3.0 - round(2.0 * t))
          }
        )
      )
      for ((opcode, expected) <- ternary)
        assertEquals(
          mismatches(triples(values), openCLTernary(opcode, width).tupled, expected.tupled, same(width)),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
    }
  }

  private def maximumMagnitude(left: Double, right: Double): Double =
    if (Math.abs(left) > Math.abs(right)) left
    else if (Math.abs(right) > Math.abs(left)) right
    else floatingMaximum(left, right)

  private def minimumMagnitude(left: Double, right: Double): Double =
    if (Math.abs(left) > Math.abs(right)) right
    else if (Math.abs(right) > Math.abs(left)) left
    else floatingMinimum(left, right)

  private def nextAfter(width: Int in (16, 32, 64))(left: Long, right: Long): Long = width.raw match {
    case 16 =>
      val from = toDouble(width, left)
      val to   = toDouble(width, right)
      if (from.isNaN || to.isNaN) fromDouble(width, Double.NaN)
      else if (from == to) right
      else if (from == 0.0) { if (to < 0.0) 0x8001L else 1L }
      else left + (if ((to > from) == (from > 0.0)) 1L else -1L)
    case 32 =>
      IEEE754.floatToBinary32(Math.nextAfter(
        IEEE754.binary32ToFloat(Binary32.low(left)),
        IEEE754.binary32ToFloat(Binary32.low(right)).toDouble
      )).toLong
    case _ =>
      IEEE754.doubleToBinary64(Math.nextAfter(IEEE754.binary64ToDouble(left), IEEE754.binary64ToDouble(right)))
  }

  test("OpenCL functions with integer operands or results match the replaced kernels") {
    for {
      width         <- floatWidths
      exponentWidth <- integerWidths
    } {
      val values    = floats(width)
      val exponents = integers(exponentWidth) ++ ExponentEdges.map(_ & mask(exponentWidth))
      val inputs    = for {
        value    <- values
        exponent <- exponents
      } yield (value, exponent)
      assertEquals(
        mismatches(
          inputs,
          OpenCLSemantics.ldexp(Execute, Closures, width, exponentWidth)(OperandIds.none).asBinary.tupled,
          { (raw, power) =>
            val scale =
              Math.max(Int.MinValue.toLong, Math.min(Int.MaxValue.toLong, signExtend(power, exponentWidth))).toInt
            fromDouble(width, Math.scalb(toDouble(width, raw), scale))
          },
          same(width)
        ),
        Vector.empty
      )
      for (opcode <- PowerOpcodes)
        assertEquals(
          mismatches(
            inputs,
            OpenCLSemantics.integerPower(
              Execute,
              Closures,
              opcode,
              width,
              exponentWidth
            )(OperandIds.none)(fail("no power")).asBinary.tupled,
            { (raw, exponentRaw) =>
              val value     = toDouble(width, raw)
              val n         = signExtend(exponentRaw, exponentWidth).toInt
              val negative  = (raw & (1L << (width - 1))) != 0L
              val evaluated =
                if (opcode == OpenCL.Pown) Math.pow(value, n.toDouble)
                else if (n == 0) Double.NaN
                else if (value == 0.0 && negative && (n & 1) != 0) { if (n < 0) Double.NegativeInfinity else -0.0 }
                else if (value < 0.0 && (n & 1) != 0) -Math.pow(-value, 1.0 / n.toDouble)
                else if (value < 0.0) Double.NaN
                else Math.pow(value, 1.0 / n.toDouble)
              fromDouble(width, evaluated)
            },
            same(width)
          ),
          Vector.empty
        )
    }
    for (width <- floatWidths) {
      val values = floats(width)
      assertEquals(
        mismatches(
          values,
          OpenCLSemantics.ilogb(Execute, Closures, width)(OperandIds.none).asUnary,
          { raw =>
            val x = toDouble(width, raw)
            (if (x == 0.0) Int.MinValue else if (!x.isFinite) Int.MaxValue else DoubleMath.exponent(x)).toLong &
              0xffffffffL
          }
        ),
        Vector.empty
      )
      val quiet = width.raw match {
        case 16 => 0x7e00L
        case 32 => 0x7fc00000L
        case _  => 0x7ff8000000000000L
      }
      val payload = width.raw match {
        case 16 => 0x1ffL
        case 32 => 0x3fffffL
        case _  => 0x7ffffffffffffL
      }
      assertEquals(
        mismatches(
          integers(64),
          OpenCLSemantics.nan(Execute, Closures, width)(OperandIds.none).asUnary,
          payloadOf => quiet | (payloadOf & payload)
        ),
        Vector.empty
      )
    }
  }

  test("OpenCL pointer math, remquo, lengths and distances match the replaced kernels") {
    for (width <- floatWidths) {
      val values  = floats(width)
      val maximum = width.raw match {
        case 16 => 1.0 - Math.scalb(1.0, -11)
        case 32 => Math.nextDown(1.0f).toDouble
        case _  => Math.nextDown(1.0)
      }
      for (opcode <- PointerOpcodes) {
        val pointer =
          OpenCLSemantics.pointer(Execute, Closures, opcode, width)(OperandIds.none)(fail("no pointer math")).asPair
        assertEquals(
          mismatches(
            values,
            raw => pointer(raw),
            { raw =>
              val x                              = toDouble(width, raw)
              val PointerOutcome(result, stored) = opcode match {
                case OpenCL.Sincos  => PointerOutcome(Math.sin(x), fromDouble(width, Math.cos(x)))
                case OpenCL.LgammaR =>
                  PointerOutcome(OpenCLMath.lgamma(x), OpenCLMath.gammaSign(x).toLong & 0xffffffffL)
                case OpenCL.Frexp =>
                  val power = if (x == 0.0 || !x.isFinite) 0 else DoubleMath.exponent(x) + 1
                  PointerOutcome(Math.scalb(x, -power), power.toLong & 0xffffffffL)
                case OpenCL.Modf =>
                  val integral = if (x < 0.0) Math.ceil(x) else Math.floor(x)
                  PointerOutcome(
                    if (x.isInfinite) Math.copySign(0.0, x) else Math.copySign(x - integral, x),
                    fromDouble(width, integral)
                  )
                case _ =>
                  val integral = Math.floor(x)
                  val fraction =
                    if (x.isNaN) x
                    else if (x == 0.0 || x.isInfinite) Math.copySign(0.0, x)
                    else Math.min(x - integral, maximum)
                  PointerOutcome(fraction, fromDouble(width, integral))
              }
              (fromDouble(width, result), stored)
            },
            (left, right) =>
              same(width)(left._1, right._1) &&
                (if (opcode == OpenCL.Frexp || opcode == OpenCL.LgammaR) left._2 == right._2
                 else same(width)(left._2, right._2))
          ),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
      }
      assertEquals(
        mismatches(
          pairs(values),
          OpenCLSemantics.remquo(Execute, Closures, width)(OperandIds.none).asBinaryPair.tupled,
          (a, b) =>
            (
              fromDouble(width, DoubleMath.remainder(toDouble(width, a), toDouble(width, b))),
              IEEE754.remainderQuotient(toDouble(width, a), toDouble(width, b)).toLong
            ),
          (left, right) => same(width)(left._1, right._1) && left._2 == right._2
        ),
        Vector.empty
      )
      val vectors = values.sliding(4, 3).filter(_.size == 4).toSeq
      assertEquals(
        mismatches(
          vectors.zip(vectors.reverse),
          OpenCLSemantics.distance(Execute, Closures, width)(OperandIds.none).asBinaryFold.tupled,
          (a, b) =>
            fromDouble(
              width,
              sumOver(a.size)((distance, lane) =>
                Math.hypot(distance, toDouble(width, a(lane)) - toDouble(width, b(lane)))
              )
            ),
          same(width)
        ),
        Vector.empty
      )
    }
  }

  test("OpenCL integer instructions match the replaced kernel") {
    import OpenCL.*
    val binary = Set(
      SAbsDiff,
      UAbsDiff,
      SAddSat,
      UAddSat,
      SSubSat,
      USubSat,
      UHadd,
      SRhadd,
      URhadd,
      SMulHi,
      UMulHi,
      Rotate,
      SUpsample,
      UUpsample,
      SMul24,
      UMul24
    )
    val ternary = Set(SClamp, UClamp, SMadHi, UMadHi, SMadSat, UMadSat, SMad24, UMad24)
    for {
      opcode <- binary ++ ternary
      width  <- integerWidths
    } {
      val widening     = opcode == SUpsample || opcode == UUpsample
      val smallProduct = opcode == SMul24 || opcode == UMul24 || opcode == SMad24 || opcode == UMad24
      if ((!widening || width != 8) && (!smallProduct || width == 32)) {
        val inputWidth: Int in 1 ~ 64                     = if (widening) (width / 2).unsafe else width
        val signed                                        = signedOpcode(opcode)
        val productWidth: Int in 1 ~ 64                   = if (smallProduct) 24 else inputWidth
        def integer(raw: Long, bits: Int in 1 ~ 64): Long = if (signed) signExtend(raw, bits) else raw & mask(bits)
        def reference(a: Long, b: Long, c: Long): Long    = {
          val left       = integer(a, productWidth)
          val right      = integer(b, productWidth)
          val third      = if (ternary.contains(opcode)) integer(c, inputWidth) else 0L
          val calculated = opcode match {
            case `SAbsDiff` => if (left >= right) left - right else right - left
            case `UAbsDiff` => if (java.lang.Long.compareUnsigned(left, right) >= 0) left - right else right - left
            case `SAddSat` | `UAddSat` => IntegerArithmetic.addSaturated(left, right, inputWidth, signed)
            case `SSubSat` | `USubSat` => IntegerArithmetic.subtractSaturated(left, right, inputWidth, signed)
            case `SClamp`              => left.max(right).min(third)
            case `UClamp`              =>
              val lower = if (java.lang.Long.compareUnsigned(left, right) < 0) right else left
              if (java.lang.Long.compareUnsigned(lower, third) > 0) third else lower
            case `UHadd`               => (left & right) + ((left ^ right) >>> 1)
            case `SRhadd`              => (left & right) + ((left ^ right) >> 1) + ((left ^ right) & 1L)
            case `URhadd`              => (left & right) + ((left ^ right) >>> 1) + ((left ^ right) & 1L)
            case `SMulHi` | `UMulHi`   => IntegerArithmetic.multiplyHigh(left, right, inputWidth, signed)
            case `SMadHi` | `UMadHi`   => IntegerArithmetic.multiplyHigh(left, right, inputWidth, signed) + third
            case `SMadSat` | `UMadSat` =>
              IntegerArithmetic.multiplyAddSaturated(
                left = left,
                right = right,
                addend = third,
                width = inputWidth,
                signed = signed
              )
            case `Rotate` =>
              val shift = right.toInt & (width - 1)
              if (shift == 0) left else (left << shift) | (left >>> (width - shift))
            case `SUpsample` | `UUpsample` => (left << inputWidth) | (right & mask(inputWidth))
            case `SMul24` | `UMul24`       => left * right
            case _                         => left * right + third
          }
          calculated & mask(width)
        }
        val staged: Kernel3 =
          if (ternary.contains(opcode))
            OpenCLSemantics.integerTernary(
              Execute,
              Closures,
              opcode,
              width
            )(OperandIds.none)(fail("no semantics")).asTernary
          else (a, b, _) =>
            OpenCLSemantics.integerBinary(
              Execute,
              Closures,
              opcode,
              width,
              inputWidth
            )(OperandIds.none)(fail("no semantics")).asBinary(a, b)
        val values = integers(inputWidth)
        assertEquals(
          mismatches(triples(values), staged.tupled, reference.tupled),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
      }
    }
    for (width <- integerWidths) {
      val values                                   = integers(width)
      val bits                                     = mask(width)
      val minMax: Seq[(Int, (Long, Long) => Long)] = Seq(
        SMax  -> ((l, r) => if (signExtend(l, width) >= signExtend(r, width)) l else r),
        UMax  -> ((l, r) => if (java.lang.Long.compareUnsigned(l, r) >= 0) l else r),
        SMin  -> ((l, r) => if (signExtend(l, width) <= signExtend(r, width)) l else r),
        UMin  -> ((l, r) => if (java.lang.Long.compareUnsigned(l, r) <= 0) l else r),
        SHadd -> { (a, b) =>
          val left  = signExtend(a, width)
          val right = signExtend(b, width)
          (left & right) + ((left ^ right) >> 1)
        }
      )
      for ((opcode, kernel) <- minMax)
        assertEquals(
          mismatches(
            pairs(values),
            OpenCLSemantics.integerBinary(
              Execute,
              Closures,
              opcode,
              width,
              width
            )(OperandIds.none)(fail("no semantics")).asBinary.tupled,
            (a, b) => kernel(a, b) & bits
          ),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
      val unary: Seq[(Int, Long => Long)] = Seq(
        SAbs -> { raw =>
          val value = signExtend(raw, width)
          if (value < 0L) -value else value
        },
        UAbs -> identity,
        Clz  -> { raw =>
          val value = raw & bits
          if (value == 0L) width.toLong else (java.lang.Long.numberOfLeadingZeros(value) - (64 - width)).toLong
        },
        Ctz -> { raw =>
          val value = raw & bits
          if (value == 0L) width.toLong else java.lang.Long.numberOfTrailingZeros(value).toLong
        },
        Popcount -> (Bits.bitCount(_, width).toLong)
      )
      for ((opcode, kernel) <- unary)
        assertEquals(
          mismatches(
            values,
            OpenCLSemantics.integerUnary(
              Execute,
              Closures,
              opcode,
              width
            )(OperandIds.none)(fail("no semantics")).asUnary,
            kernel(_) & bits
          ),
          Vector.empty,
          s"OpenCL opcode $opcode at $width bits"
        )
    }
  }

  test("OpenCL selection matches the replaced kernels") {
    for (width <- integerWidths) {
      val values = integers(width)
      val sign   = 1L << (width - 1)
      assertEquals(
        mismatches(
          triples(values),
          OpenCLSemantics.bitselect(Execute, Closures)(OperandIds.none).asTernary.tupled,
          (a, b, c) => (a & ~c) | (b & c)
        ),
        Vector.empty
      )
      for (scalar <- ScalarModes)
        assertEquals(
          mismatches(
            triples(values),
            OpenCLSemantics.select(Execute, Closures, width, scalar)(OperandIds.none).asTernary.tupled,
            (a, b, condition) => if (if (scalar) condition != 0L else (condition & sign) != 0L) b else a
          ),
          Vector.empty
        )
    }
  }
}
