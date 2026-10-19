// Generated from the pinned Khronos SPIR-V extended instruction grammars. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.collection.Vec.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object ExtendedInstruction {
  val names: Map[String, Map[String, Int]] = Map(
    "GLSL.std.450" -> Map(
      "Round" -> 1,
      "RoundEven" -> 2,
      "Trunc" -> 3,
      "FAbs" -> 4,
      "SAbs" -> 5,
      "FSign" -> 6,
      "SSign" -> 7,
      "Floor" -> 8,
      "Ceil" -> 9,
      "Fract" -> 10,
      "Radians" -> 11,
      "Degrees" -> 12,
      "Sin" -> 13,
      "Cos" -> 14,
      "Tan" -> 15,
      "Asin" -> 16,
      "Acos" -> 17,
      "Atan" -> 18,
      "Sinh" -> 19,
      "Cosh" -> 20,
      "Tanh" -> 21,
      "Asinh" -> 22,
      "Acosh" -> 23,
      "Atanh" -> 24,
      "Atan2" -> 25,
      "Pow" -> 26,
      "Exp" -> 27,
      "Log" -> 28,
      "Exp2" -> 29,
      "Log2" -> 30,
      "Sqrt" -> 31,
      "InverseSqrt" -> 32,
      "Determinant" -> 33,
      "MatrixInverse" -> 34,
      "Modf" -> 35,
      "ModfStruct" -> 36,
      "FMin" -> 37,
      "UMin" -> 38,
      "SMin" -> 39,
      "FMax" -> 40,
      "UMax" -> 41,
      "SMax" -> 42,
      "FClamp" -> 43,
      "UClamp" -> 44,
      "SClamp" -> 45,
      "FMix" -> 46,
      "IMix" -> 47,
      "Step" -> 48,
      "SmoothStep" -> 49,
      "Fma" -> 50,
      "Frexp" -> 51,
      "FrexpStruct" -> 52,
      "Ldexp" -> 53,
      "PackSnorm4x8" -> 54,
      "PackUnorm4x8" -> 55,
      "PackSnorm2x16" -> 56,
      "PackUnorm2x16" -> 57,
      "PackHalf2x16" -> 58,
      "PackDouble2x32" -> 59,
      "UnpackSnorm2x16" -> 60,
      "UnpackUnorm2x16" -> 61,
      "UnpackHalf2x16" -> 62,
      "UnpackSnorm4x8" -> 63,
      "UnpackUnorm4x8" -> 64,
      "UnpackDouble2x32" -> 65,
      "Length" -> 66,
      "Distance" -> 67,
      "Cross" -> 68,
      "Normalize" -> 69,
      "FaceForward" -> 70,
      "Reflect" -> 71,
      "Refract" -> 72,
      "FindILsb" -> 73,
      "FindSMsb" -> 74,
      "FindUMsb" -> 75,
      "InterpolateAtCentroid" -> 76,
      "InterpolateAtSample" -> 77,
      "InterpolateAtOffset" -> 78,
      "NMin" -> 79,
      "NMax" -> 80,
      "NClamp" -> 81
    ),
    "OpenCL.std" -> Map(
      "acos" -> 0,
      "acosh" -> 1,
      "acospi" -> 2,
      "asin" -> 3,
      "asinh" -> 4,
      "asinpi" -> 5,
      "atan" -> 6,
      "atan2" -> 7,
      "atanh" -> 8,
      "atanpi" -> 9,
      "atan2pi" -> 10,
      "cbrt" -> 11,
      "ceil" -> 12,
      "copysign" -> 13,
      "cos" -> 14,
      "cosh" -> 15,
      "cospi" -> 16,
      "erfc" -> 17,
      "erf" -> 18,
      "exp" -> 19,
      "exp2" -> 20,
      "exp10" -> 21,
      "expm1" -> 22,
      "fabs" -> 23,
      "fdim" -> 24,
      "floor" -> 25,
      "fma" -> 26,
      "fmax" -> 27,
      "fmin" -> 28,
      "fmod" -> 29,
      "fract" -> 30,
      "frexp" -> 31,
      "hypot" -> 32,
      "ilogb" -> 33,
      "ldexp" -> 34,
      "lgamma" -> 35,
      "lgamma_r" -> 36,
      "log" -> 37,
      "log2" -> 38,
      "log10" -> 39,
      "log1p" -> 40,
      "logb" -> 41,
      "mad" -> 42,
      "maxmag" -> 43,
      "minmag" -> 44,
      "modf" -> 45,
      "nan" -> 46,
      "nextafter" -> 47,
      "pow" -> 48,
      "pown" -> 49,
      "powr" -> 50,
      "remainder" -> 51,
      "remquo" -> 52,
      "rint" -> 53,
      "rootn" -> 54,
      "round" -> 55,
      "rsqrt" -> 56,
      "sin" -> 57,
      "sincos" -> 58,
      "sinh" -> 59,
      "sinpi" -> 60,
      "sqrt" -> 61,
      "tan" -> 62,
      "tanh" -> 63,
      "tanpi" -> 64,
      "tgamma" -> 65,
      "trunc" -> 66,
      "half_cos" -> 67,
      "half_divide" -> 68,
      "half_exp" -> 69,
      "half_exp2" -> 70,
      "half_exp10" -> 71,
      "half_log" -> 72,
      "half_log2" -> 73,
      "half_log10" -> 74,
      "half_powr" -> 75,
      "half_recip" -> 76,
      "half_rsqrt" -> 77,
      "half_sin" -> 78,
      "half_sqrt" -> 79,
      "half_tan" -> 80,
      "native_cos" -> 81,
      "native_divide" -> 82,
      "native_exp" -> 83,
      "native_exp2" -> 84,
      "native_exp10" -> 85,
      "native_log" -> 86,
      "native_log2" -> 87,
      "native_log10" -> 88,
      "native_powr" -> 89,
      "native_recip" -> 90,
      "native_rsqrt" -> 91,
      "native_sin" -> 92,
      "native_sqrt" -> 93,
      "native_tan" -> 94,
      "fclamp" -> 95,
      "degrees" -> 96,
      "fmax_common" -> 97,
      "fmin_common" -> 98,
      "mix" -> 99,
      "radians" -> 100,
      "step" -> 101,
      "smoothstep" -> 102,
      "sign" -> 103,
      "cross" -> 104,
      "distance" -> 105,
      "length" -> 106,
      "normalize" -> 107,
      "fast_distance" -> 108,
      "fast_length" -> 109,
      "fast_normalize" -> 110,
      "s_abs" -> 141,
      "s_abs_diff" -> 142,
      "s_add_sat" -> 143,
      "u_add_sat" -> 144,
      "s_hadd" -> 145,
      "u_hadd" -> 146,
      "s_rhadd" -> 147,
      "u_rhadd" -> 148,
      "s_clamp" -> 149,
      "u_clamp" -> 150,
      "clz" -> 151,
      "ctz" -> 152,
      "s_mad_hi" -> 153,
      "u_mad_sat" -> 154,
      "s_mad_sat" -> 155,
      "s_max" -> 156,
      "u_max" -> 157,
      "s_min" -> 158,
      "u_min" -> 159,
      "s_mul_hi" -> 160,
      "rotate" -> 161,
      "s_sub_sat" -> 162,
      "u_sub_sat" -> 163,
      "u_upsample" -> 164,
      "s_upsample" -> 165,
      "popcount" -> 166,
      "s_mad24" -> 167,
      "u_mad24" -> 168,
      "s_mul24" -> 169,
      "u_mul24" -> 170,
      "vloadn" -> 171,
      "vstoren" -> 172,
      "vload_half" -> 173,
      "vload_halfn" -> 174,
      "vstore_half" -> 175,
      "vstore_half_r" -> 176,
      "vstore_halfn" -> 177,
      "vstore_halfn_r" -> 178,
      "vloada_halfn" -> 179,
      "vstorea_halfn" -> 180,
      "vstorea_halfn_r" -> 181,
      "shuffle" -> 182,
      "shuffle2" -> 183,
      "printf" -> 184,
      "prefetch" -> 185,
      "bitselect" -> 186,
      "select" -> 187,
      "u_abs" -> 201,
      "u_abs_diff" -> 202,
      "u_mul_hi" -> 203,
      "u_mad_hi" -> 204
    )
  )

  /** The capabilities any one of which enables an instruction, and the grammar's names of its operands after its set
    * and opcode, by set and instruction.
    */
  lazy val grammar: Map[String, Map[String, (capabilities: Vec[String], operands: Vec[String])]] =
    Vec(
      "GLSL.std.450\tRound\t\tx\nGLSL.std.450\tRoundEven\t\tx\nGLSL.std.450\tTrunc\t\tx\nGLSL.std.450\tFAbs\t\tx\nGLSL.std.450\tSAbs\t\tx\nGLSL.std.450\tFSign\t\tx\nGLSL.std.450\tSSign\t\tx\nGLSL.std.450\tFloor\t\tx\nGLSL.std.450\tCeil\t\tx\nGLSL.std.450\tFract\t\tx\nGLSL.std.450\tRadians\t\tdegrees\nGLSL.std.450\tDegrees\t\tradians\nGLSL.std.450\tSin\t\tx\nGLSL.std.450\tCos\t\tx\nGLSL.std.450\tTan\t\tx\nGLSL.std.450\tAsin\t\tx\nGLSL.std.450\tAcos\t\tx\nGLSL.std.450\tAtan\t\ty_over_x\nGLSL.std.450\tSinh\t\tx\nGLSL.std.450\tCosh\t\tx\nGLSL.std.450\tTanh\t\tx\nGLSL.std.450\tAsinh\t\tx\nGLSL.std.450\tAcosh\t\tx\nGLSL.std.450\tAtanh\t\tx\nGLSL.std.450\tAtan2\t\ty\tx\nGLSL.std.450\tPow\t\tx\ty\nGLSL.std.450\tExp\t\tx\nGLSL.std.450\tLog\t\tx\nGLSL.std.450\tExp2\t\tx\nGLSL.std.450\tLog2\t\tx\nGLSL.std.450\tSqrt\t\tx\nGLSL.std.450\tInverseSqrt\t\tx\nGLSL.std.450\tDeterminant\t\tx\nGLSL.std.450\tMatrixInverse\t\tx\nGLSL.std.450\tModf\t\tx\ti\nGLSL.std.450\tModfStruct\t\tx\nGLSL.std.450\tFMin\t\tx\ty\nGLSL.std.450\tUMin\t\tx\ty\nGLSL.std.450\tSMin\t\tx\ty\nGLSL.std.450\tFMax\t\tx\ty\nGLSL.std.450\tUMax\t\tx\ty\nGLSL.std.450\tSMax\t\tx\ty\nGLSL.std.450\tFClamp\t\tx\tminVal\tmaxVal\nGLSL.std.450\tUClamp\t\tx\tminVal\tmaxVal\nGLSL.std.450\tSClamp\t\tx\tminVal\tmaxVal\nGLSL.std.450\tFMix\t\tx\ty\ta\nGLSL.std.450\tIMix\t\tx\ty\ta\nGLSL.std.450\tStep\t\tedge\tx\nGLSL.std.450\tSmoothStep\t\tedge0\tedge1\tx\nGLSL.std.450\tFma\t\ta\tb\tc\nGLSL.std.450\tFrexp\t\tx\texp\nGLSL.std.450\tFrexpStruct\t\tx\nGLSL.std.450\tLdexp\t\tx\texp\nGLSL.std.450\tPackSnorm4x8\t\tv\nGLSL.std.450\tPackUnorm4x8\t\tv\nGLSL.std.450\tPackSnorm2x16\t\tv\nGLSL.std.450\tPackUnorm2x16\t\tv\nGLSL.std.450\tPackHalf2x16\t\tv\nGLSL.std.450\tPackDouble2x32\tFloat64\tv\nGLSL.std.450\tUnpackSnorm2x16\t\tp\nGLSL.std.450\tUnpackUnorm2x16\t\tp\nGLSL.std.450\tUnpackHalf2x16\t\tv\nGLSL.std.450\tUnpackSnorm4x8\t\tp\nGLSL.std.450\tUnpackUnorm4x8\t\tp\nGLSL.std.450\tUnpackDouble2x32\tFloat64\tv\nGLSL.std.450\tLength\t\tx\nGLSL.std.450\tDistance\t\tp0\tp1\nGLSL.std.450\tCross\t\tx\ty\nGLSL.std.450\tNormalize\t\tx\nGLSL.std.450\tFaceForward\t\tN\tI\tNref\nGLSL.std.450\tReflect\t\tI\tN\nGLSL.std.450\tRefract\t\tI\tN\teta\nGLSL.std.450\tFindILsb\t\tValue\nGLSL.std.450\tFindSMsb\t\tValue\nGLSL.std.450\tFindUMsb\t\tValue\nGLSL.std.450\tInterpolateAtCentroid\tInterpolationFunction\tinterpolant\nGLSL.std.450\tInterpolateAtSample\tInterpolationFunction\tinterpolant\tsample\nGLSL.std.450\tInterpolateAtOffset\tInterpolationFunction\tinterpolant\toffset\nGLSL.std.450\tNMin\t\tx\ty\nGLSL.std.450\tNMax\t\tx\ty\nGLSL.std.450\tNClamp\t\tx\tminVal\tmaxVal\nOpenCL.std\tacos\t\tx\nOpenCL.std\tacosh\t\tx\nOpenCL.std\tacospi\t\tx\nOpenCL.std\tasin\t\tx\nOpenCL.std\tasinh\t\tx\nOpenCL.std\tasinpi\t\tx\nOpenCL.std\tatan\t\tx\nOpenCL.std\tatan2\t\ty\tx\nOpenCL.std\tatanh\t\tx\nOpenCL.std\tatanpi\t\tx\nOpenCL.std\tatan2pi\t\ty\tx\nOpenCL.std\tcbrt\t\tx\nOpenCL.std\tceil\t\tx\nOpenCL.std\tcopysign\t\tx\ty\nOpenCL.std\tcos\t\tx\nOpenCL.std\tcosh\t\tx\nOpenCL.std\tcospi\t\tx\nOpenCL.std\terfc\t\tx\nOpenCL.std\terf\t\tx\nOpenCL.std\texp\t\tx\nOpenCL.std\texp2\t\tx\nOpenCL.std\texp10\t\tx\nOpenCL.std\texpm1\t\tx\nOpenCL.std\tfabs\t\tx\nOpenCL.std\tfdim\t\tx\ty\nOpenCL.std\tfloor\t\tx\nOpenCL.std\tfma\t\ta\tb\tc\nOpenCL.std\tfmax\t\tx\ty\nOpenCL.std\tfmin\t\tx\ty\nOpenCL.std\tfmod\t\tx\ty\nOpenCL.std\tfract\t\tx\tptr\nOpenCL.std\tfrexp\t\tx\texp\nOpenCL.std\thypot\t\tx\ty\nOpenCL.std\tilogb\t\tx\nOpenCL.std\tldexp\t\tx\tk\nOpenCL.std\tlgamma\t\tx\nOpenCL.std\tlgamma_r\t\tx\tsignp\nOpenCL.std\tlog\t\tx\nOpenCL.std\tlog2\t\tx\nOpenCL.std\tlog10\t\tx\nOpenCL.std\tlog1p\t\tx\nOpenCL.std\tlogb\t\tx\nOpenCL.std\tmad\t\ta\tb\tc\nOpenCL.std\tmaxmag\t\tx\ty\nOpenCL.std\tminmag\t\tx\ty\nOpenCL.std\tmodf\t\tx\tiptr\nOpenCL.std\tnan\t\tnancode\nOpenCL.std\tnextafter\t\tx\ty\nOpenCL.std\tpow\t\tx\ty\nOpenCL.std\tpown\t\tx\ty\nOpenCL.std\tpowr\t\tx\ty\nOpenCL.std\tremainder\t\tx\ty\nOpenCL.std\tremquo\t\tx\ty\tquo\nOpenCL.std\trint\t\tx\nOpenCL.std\trootn\t\tx\ty\nOpenCL.std\tround\t\tx\nOpenCL.std\trsqrt\t\tx\nOpenCL.std\tsin\t\tx\nOpenCL.std\tsincos\t\tx\tcosval\nOpenCL.std\tsinh\t\tx\nOpenCL.std\tsinpi\t\tx\nOpenCL.std\tsqrt\t\tx\nOpenCL.std\ttan\t\tx\nOpenCL.std\ttanh\t\tx\nOpenCL.std\ttanpi\t\tx\nOpenCL.std\ttgamma\t\tx\nOpenCL.std\ttrunc\t\tx\nOpenCL.std\thalf_cos\t\tx\nOpenCL.std\thalf_divide\t\tx\ty\nOpenCL.std\thalf_exp\t\tx\nOpenCL.std\thalf_exp2\t\tx\nOpenCL.std\thalf_exp10\t\tx\nOpenCL.std\thalf_log\t\tx\nOpenCL.std\thalf_log2\t\tx\nOpenCL.std\thalf_log10\t\tx\nOpenCL.std\thalf_powr\t\tx\ty\nOpenCL.std\thalf_recip\t\tx\nOpenCL.std\thalf_rsqrt\t\tx\nOpenCL.std\thalf_sin\t\tx\nOpenCL.std\thalf_sqrt\t\tx\nOpenCL.std\thalf_tan\t\tx\nOpenCL.std\tnative_cos\t\tx\nOpenCL.std\tnative_divide\t\tx\ty\nOpenCL.std\tnative_exp\t\tx\nOpenCL.std\tnative_exp2\t\tx\nOpenCL.std\tnative_exp10\t\tx\nOpenCL.std\tnative_log\t\tx\nOpenCL.std\tnative_log2\t\tx\nOpenCL.std\tnative_log10\t\tx\nOpenCL.std\tnative_powr\t\tx\ty\nOpenCL.std\tnative_recip\t\tx\nOpenCL.std\tnative_rsqrt\t\tx\nOpenCL.std\tnative_sin\t\tx\nOpenCL.std\tnative_sqrt\t\tx\nOpenCL.std\tnative_tan\t\tx\nOpenCL.std\tfclamp\t\tx\tminval\tmaxval\nOpenCL.std\tdegrees\t\tradians\nOpenCL.std\tfmax_common\t\tx\ty\nOpenCL.std\tfmin_common\t\tx\ty\nOpenCL.std\tmix\t\tx\ty\ta\nOpenCL.std\tradians\t\tdegrees\nOpenCL.std\tstep\t\tedge\tx\nOpenCL.std\tsmoothstep\t\tedge0\tedge1\tx\nOpenCL.std\tsign\t\tx\nOpenCL.std\tcross\t\tp0\tp1\nOpenCL.std\tdistance\t\tp0\tp1\nOpenCL.std\tlength\t\tp\nOpenCL.std\tnormalize\t\tp\nOpenCL.std\tfast_distance\t\tp0\tp1\nOpenCL.std\tfast_length\t\tp\nOpenCL.std\tfast_normalize\t\tp\nOpenCL.std\ts_abs\t\tx\nOpenCL.std\ts_abs_diff\t\tx\ty\nOpenCL.std\ts_add_sat\t\tx\ty\nOpenCL.std\tu_add_sat\t\tx\ty\nOpenCL.std\ts_hadd\t\tx\ty\nOpenCL.std\tu_hadd\t\tx\ty\nOpenCL.std\ts_rhadd\t\tx\ty\nOpenCL.std\tu_rhadd\t\tx\ty\nOpenCL.std\ts_clamp\t\tx\tminval\tmaxval\nOpenCL.std\tu_clamp\t\tx\tminval\tmaxval\nOpenCL.std\tclz\t\tx\nOpenCL.std\tctz\t\tx\nOpenCL.std\ts_mad_hi\t\ta\tb\tc\nOpenCL.std\tu_mad_sat\t\tx\ty\tz\nOpenCL.std\ts_mad_sat\t\tx\ty\tz\nOpenCL.std\ts_max\t\tx\ty\nOpenCL.std\tu_max\t\tx\ty\nOpenCL.std\ts_min\t\tx\ty\nOpenCL.std\tu_min\t\tx\ty\nOpenCL.std\ts_mul_hi\t\tx\ty\nOpenCL.std\trotate\t\tv\ti\nOpenCL.std\ts_sub_sat\t\tx\ty\nOpenCL.std\tu_sub_sat\t\tx\ty\nOpenCL.std\tu_upsample\t\thi\tlo\nOpenCL.std\ts_upsample\t\thi\tlo\nOpenCL.std\tpopcount\t\tx\nOpenCL.std\ts_mad24\t\tx\ty\tz\nOpenCL.std\tu_mad24\t\tx\ty\tz\nOpenCL.std\ts_mul24\t\tx\ty\nOpenCL.std\tu_mul24\t\tx\ty\nOpenCL.std\tvloadn\t\toffset\tp\tn\nOpenCL.std\tvstoren\t\tdata\toffset\tp\nOpenCL.std\tvload_half\t\toffset\tp\nOpenCL.std\tvload_halfn\t\toffset\tp\tn\nOpenCL.std\tvstore_half\t\tdata\toffset\tp\nOpenCL.std\tvstore_half_r\t\tdata\toffset\tp\tmode\nOpenCL.std\tvstore_halfn\t\tdata\toffset\tp\nOpenCL.std\tvstore_halfn_r\t\tdata\toffset\tp\tmode\nOpenCL.std\tvloada_halfn\t\toffset\tp\tn\nOpenCL.std\tvstorea_halfn\t\tdata\toffset\tp\nOpenCL.std\tvstorea_halfn_r\t\tdata\toffset\tp\tmode\nOpenCL.std\tshuffle\t\tx\tshuffle mask\nOpenCL.std\tshuffle2\t\tx\ty\tshuffle mask\nOpenCL.std\tprintf\t\tformat\tadditional arguments\nOpenCL.std\tprefetch\t\tptr\tnum elements\nOpenCL.std\tbitselect\t\ta\tb\tc\nOpenCL.std\tselect\t\ta\tb\tc\nOpenCL.std\tu_abs\t\tx\nOpenCL.std\tu_abs_diff\t\tx\ty\nOpenCL.std\tu_mul_hi\t\tx\ty\nOpenCL.std\tu_mad_hi\t\ta\tb\tc\n"
    ).flatMap(_.split('\n').toVec).map(_.split('\t').toVec).groupBy(_.head).map((set, rows) =>
      set -> rows.map(row => row(1) -> (Vec.from(row(2).split(',')).filter(_.nonEmpty), row.drop(3))).toMap
    )

  /** GLSL.std.450 */
  object GlslStd450 {
    final val Round = 1
    final val RoundEven = 2
    final val Trunc = 3
    final val FAbs = 4
    final val SAbs = 5
    final val FSign = 6
    final val SSign = 7
    final val Floor = 8
    final val Ceil = 9
    final val Fract = 10
    final val Radians = 11
    final val Degrees = 12
    final val Sin = 13
    final val Cos = 14
    final val Tan = 15
    final val Asin = 16
    final val Acos = 17
    final val Atan = 18
    final val Sinh = 19
    final val Cosh = 20
    final val Tanh = 21
    final val Asinh = 22
    final val Acosh = 23
    final val Atanh = 24
    final val Atan2 = 25
    final val Pow = 26
    final val Exp = 27
    final val Log = 28
    final val Exp2 = 29
    final val Log2 = 30
    final val Sqrt = 31
    final val InverseSqrt = 32
    final val Determinant = 33
    final val MatrixInverse = 34
    final val Modf = 35
    final val ModfStruct = 36
    final val FMin = 37
    final val UMin = 38
    final val SMin = 39
    final val FMax = 40
    final val UMax = 41
    final val SMax = 42
    final val FClamp = 43
    final val UClamp = 44
    final val SClamp = 45
    final val FMix = 46
    final val IMix = 47
    final val Step = 48
    final val SmoothStep = 49
    final val Fma = 50
    final val Frexp = 51
    final val FrexpStruct = 52
    final val Ldexp = 53
    final val PackSnorm4x8 = 54
    final val PackUnorm4x8 = 55
    final val PackSnorm2x16 = 56
    final val PackUnorm2x16 = 57
    final val PackHalf2x16 = 58
    final val PackDouble2x32 = 59
    final val UnpackSnorm2x16 = 60
    final val UnpackUnorm2x16 = 61
    final val UnpackHalf2x16 = 62
    final val UnpackSnorm4x8 = 63
    final val UnpackUnorm4x8 = 64
    final val UnpackDouble2x32 = 65
    final val Length = 66
    final val Distance = 67
    final val Cross = 68
    final val Normalize = 69
    final val FaceForward = 70
    final val Reflect = 71
    final val Refract = 72
    final val FindILsb = 73
    final val FindSMsb = 74
    final val FindUMsb = 75
    final val InterpolateAtCentroid = 76
    final val InterpolateAtSample = 77
    final val InterpolateAtOffset = 78
    final val NMin = 79
    final val NMax = 80
    final val NClamp = 81
  }

  /** OpenCL.std */
  object OpenClStd {
    final val Acos = 0
    final val Acosh = 1
    final val Acospi = 2
    final val Asin = 3
    final val Asinh = 4
    final val Asinpi = 5
    final val Atan = 6
    final val Atan2 = 7
    final val Atanh = 8
    final val Atanpi = 9
    final val Atan2pi = 10
    final val Cbrt = 11
    final val Ceil = 12
    final val Copysign = 13
    final val Cos = 14
    final val Cosh = 15
    final val Cospi = 16
    final val Erfc = 17
    final val Erf = 18
    final val Exp = 19
    final val Exp2 = 20
    final val Exp10 = 21
    final val Expm1 = 22
    final val Fabs = 23
    final val Fdim = 24
    final val Floor = 25
    final val Fma = 26
    final val Fmax = 27
    final val Fmin = 28
    final val Fmod = 29
    final val Fract = 30
    final val Frexp = 31
    final val Hypot = 32
    final val Ilogb = 33
    final val Ldexp = 34
    final val Lgamma = 35
    final val LgammaR = 36
    final val Log = 37
    final val Log2 = 38
    final val Log10 = 39
    final val Log1p = 40
    final val Logb = 41
    final val Mad = 42
    final val Maxmag = 43
    final val Minmag = 44
    final val Modf = 45
    final val Nan = 46
    final val Nextafter = 47
    final val Pow = 48
    final val Pown = 49
    final val Powr = 50
    final val Remainder = 51
    final val Remquo = 52
    final val Rint = 53
    final val Rootn = 54
    final val Round = 55
    final val Rsqrt = 56
    final val Sin = 57
    final val Sincos = 58
    final val Sinh = 59
    final val Sinpi = 60
    final val Sqrt = 61
    final val Tan = 62
    final val Tanh = 63
    final val Tanpi = 64
    final val Tgamma = 65
    final val Trunc = 66
    final val HalfCos = 67
    final val HalfDivide = 68
    final val HalfExp = 69
    final val HalfExp2 = 70
    final val HalfExp10 = 71
    final val HalfLog = 72
    final val HalfLog2 = 73
    final val HalfLog10 = 74
    final val HalfPowr = 75
    final val HalfRecip = 76
    final val HalfRsqrt = 77
    final val HalfSin = 78
    final val HalfSqrt = 79
    final val HalfTan = 80
    final val NativeCos = 81
    final val NativeDivide = 82
    final val NativeExp = 83
    final val NativeExp2 = 84
    final val NativeExp10 = 85
    final val NativeLog = 86
    final val NativeLog2 = 87
    final val NativeLog10 = 88
    final val NativePowr = 89
    final val NativeRecip = 90
    final val NativeRsqrt = 91
    final val NativeSin = 92
    final val NativeSqrt = 93
    final val NativeTan = 94
    final val Fclamp = 95
    final val Degrees = 96
    final val FmaxCommon = 97
    final val FminCommon = 98
    final val Mix = 99
    final val Radians = 100
    final val Step = 101
    final val Smoothstep = 102
    final val Sign = 103
    final val Cross = 104
    final val Distance = 105
    final val Length = 106
    final val Normalize = 107
    final val FastDistance = 108
    final val FastLength = 109
    final val FastNormalize = 110
    final val SAbs = 141
    final val SAbsDiff = 142
    final val SAddSat = 143
    final val UAddSat = 144
    final val SHadd = 145
    final val UHadd = 146
    final val SRhadd = 147
    final val URhadd = 148
    final val SClamp = 149
    final val UClamp = 150
    final val Clz = 151
    final val Ctz = 152
    final val SMadHi = 153
    final val UMadSat = 154
    final val SMadSat = 155
    final val SMax = 156
    final val UMax = 157
    final val SMin = 158
    final val UMin = 159
    final val SMulHi = 160
    final val Rotate = 161
    final val SSubSat = 162
    final val USubSat = 163
    final val UUpsample = 164
    final val SUpsample = 165
    final val Popcount = 166
    final val SMad24 = 167
    final val UMad24 = 168
    final val SMul24 = 169
    final val UMul24 = 170
    final val Vloadn = 171
    final val Vstoren = 172
    final val VloadHalf = 173
    final val VloadHalfn = 174
    final val VstoreHalf = 175
    final val VstoreHalfR = 176
    final val VstoreHalfn = 177
    final val VstoreHalfnR = 178
    final val VloadaHalfn = 179
    final val VstoreaHalfn = 180
    final val VstoreaHalfnR = 181
    final val Shuffle = 182
    final val Shuffle2 = 183
    final val Printf = 184
    final val Prefetch = 185
    final val Bitselect = 186
    final val Select = 187
    final val UAbs = 201
    final val UAbsDiff = 202
    final val UMulHi = 203
    final val UMadHi = 204
  }
}
