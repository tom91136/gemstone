package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.semantics.prelude.*

/** The binary64 functions extended instructions evaluate before rounding once to their width, and the exact widening
  * and correctly rounded narrowing between binary64 and the narrower widths. Where GLSL.std.450 and OpenCL.std define
  * one name differently, the GLSL form carries a `glsl` prefix.
  */
object Elementary {
  def widen16(x: Float16): Float64  = Float64(x.toDouble)
  def widen32(x: Float32): Float64  = Float64(x.toDouble)
  def narrow16(x: Float64): Float16 = Float16(x.toDouble)
  def narrow32(x: Float64): Float32 = Float32(x.toDouble)

  def widenE4M3(x: Float8E4M3): Float64 = Float64(x.toDouble)
  def widenE5M2(x: Float8E5M2): Float64 = Float64(x.toDouble)

  def narrow16(x: Float64, mode: IEEE754.Rounding): Float16 =
    FloatingPoint[16](IEEE754.fromDouble(x.toDouble, 16, mode))
  def narrow32(x: Float64, mode: IEEE754.Rounding): Float32 =
    FloatingPoint[32](IEEE754.fromDouble(x.toDouble, 32, mode))

  def sin(x: Float64): Float64     = Float64(Math.sin(x.toDouble))
  def cos(x: Float64): Float64     = Float64(Math.cos(x.toDouble))
  def tan(x: Float64): Float64     = Float64(Math.tan(x.toDouble))
  def asin(x: Float64): Float64    = Float64(Math.asin(x.toDouble))
  def acos(x: Float64): Float64    = Float64(Math.acos(x.toDouble))
  def atan(x: Float64): Float64    = Float64(Math.atan(x.toDouble))
  def sinh(x: Float64): Float64    = Float64(Math.sinh(x.toDouble))
  def cosh(x: Float64): Float64    = Float64(Math.cosh(x.toDouble))
  def tanh(x: Float64): Float64    = Float64(Math.tanh(x.toDouble))
  def asinh(x: Float64): Float64   = Float64(OpenCLMath.asinh(x.toDouble))
  def acosh(x: Float64): Float64   = Float64(OpenCLMath.acosh(x.toDouble))
  def atanh(x: Float64): Float64   = Float64(OpenCLMath.atanh(x.toDouble))
  def sinpi(x: Float64): Float64   = Float64(DoubleMath.sinPi(x.toDouble))
  def cospi(x: Float64): Float64   = Float64(DoubleMath.cosPi(x.toDouble))
  def tanpi(x: Float64): Float64   = Float64(DoubleMath.tanPi(x.toDouble))
  def asinpi(x: Float64): Float64  = Float64(Math.asin(x.toDouble) / Math.PI)
  def acospi(x: Float64): Float64  = Float64(Math.acos(x.toDouble) / Math.PI)
  def atanpi(x: Float64): Float64  = Float64(Math.atan(x.toDouble) / Math.PI)
  def radians(x: Float64): Float64 = Float64(x.toDouble * (Math.PI / 180.0))
  def degrees(x: Float64): Float64 = Float64(x.toDouble * (180.0 / Math.PI))

  /** GLSL's hyperbolic sine and cosine, through exponentials rounded to `width` so intermediates overflow there. */
  def glslSinh(x: Float64, width: Int in (16, 32, 64)): Float64 =
    Float64(DoubleMath.hyperbolic(x.toDouble, width, sine = true))
  def glslCosh(x: Float64, width: Int in (16, 32, 64)): Float64 =
    Float64(DoubleMath.hyperbolic(x.toDouble, width, sine = false))

  def exp(x: Float64): Float64   = Float64(Math.exp(x.toDouble))
  def exp2(x: Float64): Float64  = Float64(Math.pow(2.0, x.toDouble))
  def exp10(x: Float64): Float64 = Float64(Math.pow(10.0, x.toDouble))
  def expm1(x: Float64): Float64 = Float64(Math.expm1(x.toDouble))
  def log(x: Float64): Float64   = Float64(Math.log(x.toDouble))
  def log2(x: Float64): Float64  = Float64(Math.log(x.toDouble) / Math.log(2.0))
  def log10(x: Float64): Float64 = Float64(Math.log10(x.toDouble))
  def log1p(x: Float64): Float64 = Float64(Math.log1p(x.toDouble))
  def logb(x: Float64): Float64  = Float64(DoubleMath.logb(x.toDouble))
  def cbrt(x: Float64): Float64  = Float64(Math.cbrt(x.toDouble))
  def sqrt(x: Float64): Float64  = Float64(Math.sqrt(x.toDouble))
  def rsqrt(x: Float64): Float64 = Float64(1.0 / Math.sqrt(x.toDouble))
  def recip(x: Float64): Float64 = Float64(1.0 / x.toDouble)

  def ceil(x: Float64): Float64      = Float64(Math.ceil(x.toDouble))
  def floor(x: Float64): Float64     = Float64(Math.floor(x.toDouble))
  def rint(x: Float64): Float64      = Float64(Math.rint(x.toDouble))
  def round(x: Float64): Float64     = Float64(DoubleMath.round(x.toDouble))
  def trunc(x: Float64): Float64     = Float64(DoubleMath.truncate(x.toDouble))
  def sign(x: Float64): Float64      = Float64(DoubleMath.signum(x.toDouble))
  def glslFract(x: Float64): Float64 = Float64(x.toDouble - Math.floor(x.toDouble))
  def erf(x: Float64): Float64       = Float64(OpenCLMath.erf(x.toDouble))
  def erfc(x: Float64): Float64      = Float64(OpenCLMath.erfc(x.toDouble))
  def tgamma(x: Float64): Float64    = Float64(OpenCLMath.tgamma(x.toDouble))
  def lgamma(x: Float64): Float64    = Float64(OpenCLMath.lgamma(x.toDouble))

  /** The sign of the gamma function, as a 32-bit integer. */
  def lgammaSign(x: Float64): UInt[32] = UInt[32](OpenCLMath.gammaSign(x.toDouble).toLong)

  /** OpenCL's fract: `x - floor(x)`, below one by at least the last place of `width`. */
  def fract(x: Float64, width: Int in (16, 32, 64)): Float64 = Float64(DoubleMath.fraction(
    x.toDouble,
    width.raw match {
      case 16 => 1.0 - Math.scalb(1.0, -11)
      case 32 => Math.nextDown(1.0f).toDouble
      case _  => Math.nextDown(1.0)
    }
  ))

  /** modf's fractional part, with the operand's sign. */
  def modf(x: Float64): Float64 = Float64(DoubleMath.fractional(x.toDouble))

  /** frexp's significand in [0.5, 1), and its exponent. */
  def frexp(x: Float64): Float64              = Float64(DoubleMath.significand(x.toDouble))
  def frexpExponent(x: Float64): SInt[64]     = SInt[64](DoubleMath.frexpExponent(x.toDouble).toLong)
  def ilogb(x: Float64): UInt[32]             = UInt[32](DoubleMath.integerLogarithm(x.toDouble))
  def ldexp(x: Float64, n: SInt[64]): Float64 =
    Float64(Math.scalb(x.toDouble, Math.max(Int.MinValue.toLong, Math.min(Int.MaxValue.toLong, n.raw)).toInt))
  def pown(x: Float64, n: SInt[64]): Float64  = Float64(Math.pow(x.toDouble, n.raw.toInt.toDouble))
  def rootn(x: Float64, n: SInt[64]): Float64 = Float64(DoubleMath.rootn(x.toDouble, n.raw.toInt))

  def atan2(y: Float64, x: Float64): Float64      = Float64(Math.atan2(y.toDouble, x.toDouble))
  def atan2pi(y: Float64, x: Float64): Float64    = Float64(Math.atan2(y.toDouble, x.toDouble) / Math.PI)
  def hypot(x: Float64, y: Float64): Float64      = Float64(Math.hypot(x.toDouble, y.toDouble))
  def pow(x: Float64, y: Float64): Float64        = Float64(Math.pow(x.toDouble, y.toDouble))
  def powr(x: Float64, y: Float64): Float64       = Float64(DoubleMath.powr(x.toDouble, y.toDouble))
  def fdim(x: Float64, y: Float64): Float64       = Float64(Math.max(x.toDouble - y.toDouble, 0.0))
  def fmod(x: Float64, y: Float64): Float64       = Float64(x.toDouble % y.toDouble)
  def remainder(x: Float64, y: Float64): Float64  = Float64(DoubleMath.remainder(x.toDouble, y.toDouble))
  def divide(x: Float64, y: Float64): Float64     = Float64(x.toDouble / y.toDouble)
  def step(edge: Float64, x: Float64): Float64    = Float64(if (x.toDouble < edge.toDouble) 0.0 else 1.0)
  def remquo(x: Float64, y: Float64): SInt[64]    = SInt[64](IEEE754.remainderQuotient(x.toDouble, y.toDouble).toLong)
  def difference(x: Float64, y: Float64): Float64 = Float64(x.toDouble - y.toDouble)

  /** OpenCL's mix, `x + (y - x) * a`. */
  def mix(x: Float64, y: Float64, a: Float64): Float64 = Float64(x.toDouble + (y.toDouble - x.toDouble) * a.toDouble)

  /** GLSL's FMix, `x * (1 - a) + y * a`. */
  def glslMix(x: Float64, y: Float64, a: Float64): Float64 =
    Float64(x.toDouble * (1.0 - a.toDouble) + y.toDouble * a.toDouble)

  /** GLSL's SmoothStep, evaluated in binary64. */
  def glslSmoothStep(edge0: Float64, edge1: Float64, x: Float64): Float64 = {
    val t = Math.max(0.0, Math.min(1.0, (x.toDouble - edge0.toDouble) / (edge1.toDouble - edge0.toDouble)))
    Float64(t * t * (3.0 - 2.0 * t))
  }

  /** OpenCL's smoothstep, rounding each intermediate to `width`. */
  def smoothstep(edge0: Float64, edge1: Float64, x: Float64, width: Int in (16, 32, 64)): Float64 =
    Float64(DoubleMath.smoothstep(low = edge0.toDouble, high = edge1.toDouble, value = x.toDouble, width = width))

  /** `w * x - y * z`, a lane of a cross product. */
  def cross(w: Float64, x: Float64, y: Float64, z: Float64): Float64 =
    Float64(w.toDouble * x.toDouble - y.toDouble * z.toDouble)

  /** `sum + x * y`, unfused, a step of a dot product. */
  def dot(sum: Float64, x: Float64, y: Float64): Float64 = Float64(sum.toDouble + x.toDouble * y.toDouble)

  /** `sum` and `x` as the legs of a right triangle, a step of a Euclidean norm. */
  def norm(sum: Float64, x: Float64): Float64 = Float64(Math.hypot(sum.toDouble, x.toDouble))

  /** The larger of `largest` and the magnitude of `x`, NaN where either is: a step of normalize's scale. */
  def magnitude(largest: Float64, x: Float64): Float64 = Float64(Math.max(largest.toDouble, Math.abs(x.toDouble)))

  /** A step of the norm of the lanes normalize scales by `scale`. */
  def scaledNorm(sum: Float64, x: Float64, scale: Float64): Float64 =
    Float64(Math.hypot(sum.toDouble, DoubleMath.scaled(x.toDouble, scale.toDouble)))

  /** A lane of normalize, from its scale and the norm of the scaled lanes: zero vectors are returned as they are. */
  def normalised(x: Float64, scale: Float64, norm: Float64): Float64 =
    Float64(if (scale.toDouble == 0.0) x.toDouble
    else DoubleMath.scaled(x.toDouble, scale.toDouble) / norm.toDouble)

  /** FaceForward's lane of `n` for the dot product of the other operands. */
  def faceForward(dot: Float64, n: Float64): Float64 = Float64(if (dot.toDouble < 0.0) n.toDouble else -n.toDouble)

  /** Reflect's lane of incident `i` about normal `n`, for their dot product. */
  def reflect(dot: Float64, i: Float64, n: Float64): Float64 = Float64(i.toDouble - 2.0 * dot.toDouble * n.toDouble)

  /** Refract's lane of incident `i` through normal `n` at ratio `eta`, for their dot product. */
  def refract(dot: Float64, i: Float64, n: Float64, eta: Float64): Float64 = {
    val d = dot.toDouble
    val e = eta.toDouble
    val k = 1.0 - e * e * (1.0 - d * d)
    Float64(if (k < 0.0) 0.0 else e * i.toDouble - (e * d + Math.sqrt(k)) * n.toDouble)
  }
}
