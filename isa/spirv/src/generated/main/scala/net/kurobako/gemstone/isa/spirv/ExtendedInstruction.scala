// Generated from the pinned Khronos SPIR-V extended instruction grammars. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

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
