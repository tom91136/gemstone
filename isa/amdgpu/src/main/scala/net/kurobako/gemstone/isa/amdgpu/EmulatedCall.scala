package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

private object EmulatedCall {
  private lazy val calls = Vec.from(values)

  def matching(name: String): Vec[EmulatedCall] = calls.filter(call => name.contains(call.pattern))

  enum Function {
    case Log, Asinh, Acos, Atan, Acosh, Atanh, Sinh, Cosh, Tanh, Exp

    def apply(real: Double, imaginary: Double): (Double, Double) = this match {
      case Log   => log(real, imaginary)
      case Asinh =>
        val root = sqrt(real * real - imaginary * imaginary + 1.0, 2.0 * real * imaginary)
        log(real + root._1, imaginary + root._2)
      case Acos =>
        val square = multiply(real, imaginary, real, imaginary)
        val root   = sqrt(1.0 - square._1, -square._2)
        val logged = log(-imaginary + root._1, real + root._2)
        (math.Pi * 0.5 - logged._2, logged._1)
      case Atan =>
        val left  = log(1.0 - imaginary, real)
        val right = log(1.0 + imaginary, -real)
        ((left._2 - right._2) * 0.5, (right._1 - left._1) * 0.5)
      case Acosh =>
        val left    = sqrt(real + 1.0, imaginary)
        val right   = sqrt(real - 1.0, imaginary)
        val product = multiply(left._1, left._2, right._1, right._2)
        log(real + product._1, imaginary + product._2)
      case Atanh =>
        val left  = log(1.0 + real, imaginary)
        val right = log(1.0 - real, -imaginary)
        ((left._1 - right._1) * 0.5, (left._2 - right._2) * 0.5)
      case Sinh => (math.sinh(real) * math.cos(imaginary), math.cosh(real) * math.sin(imaginary))
      case Cosh => (math.cosh(real) * math.cos(imaginary), math.sinh(real) * math.sin(imaginary))
      case Tanh =>
        val denominator = math.cosh(2.0 * real) + math.cos(2.0 * imaginary)
        (math.sinh(2.0 * real) / denominator, math.sin(2.0 * imaginary) / denominator)
      case Exp =>
        val scale = math.exp(real)
        (scale * math.cos(imaginary), scale * math.sin(imaginary))
    }
  }

  private def log(real: Double, imaginary: Double): (Double, Double) =
    (math.log(math.hypot(real, imaginary)), math.atan2(imaginary, real))

  private def sqrt(real: Double, imaginary: Double): (Double, Double) = {
    val magnitude = math.hypot(real, imaginary)
    (math.sqrt((magnitude + real) * 0.5), math.copySign(math.sqrt((magnitude - real) * 0.5), imaginary))
  }

  private def multiply(leftReal: Double, leftImaginary: Double, rightReal: Double, rightImaginary: Double) =
    (leftReal * rightReal - leftImaginary * rightImaginary, leftReal * rightImaginary + leftImaginary * rightReal)
}

/** Device-library complex functions evaluated on the host, recognised by a mangled-name fragment. Operands and results
  * occupy v0-v1 (binary32) or v[0:1] and v[2:3] (binary64).
  */
private enum EmulatedCall(val pattern: String, val function: EmulatedCall.Function, val double: Boolean) {
  case LogF32   extends EmulatedCall("logIf", EmulatedCall.Function.Log, false)
  case LogF64   extends EmulatedCall("logId", EmulatedCall.Function.Log, true)
  case AsinhF32 extends EmulatedCall("casinhf", EmulatedCall.Function.Asinh, false)
  case AsinhF64 extends EmulatedCall("casinhE", EmulatedCall.Function.Asinh, true)
  case AcosF32  extends EmulatedCall("cacosf", EmulatedCall.Function.Acos, false)
  case AcosF64  extends EmulatedCall("cacosE", EmulatedCall.Function.Acos, true)
  case AtanF32  extends EmulatedCall("catanf", EmulatedCall.Function.Atan, false)
  case AtanF64  extends EmulatedCall("catanE", EmulatedCall.Function.Atan, true)
  case AcoshF32 extends EmulatedCall("cacoshf", EmulatedCall.Function.Acosh, false)
  case AcoshF64 extends EmulatedCall("cacoshE", EmulatedCall.Function.Acosh, true)
  case AtanhF32 extends EmulatedCall("catanhf", EmulatedCall.Function.Atanh, false)
  case AtanhF64 extends EmulatedCall("catanhE", EmulatedCall.Function.Atanh, true)
  case SinhF32  extends EmulatedCall("csinhf", EmulatedCall.Function.Sinh, false)
  case CoshF32  extends EmulatedCall("ccoshf", EmulatedCall.Function.Cosh, false)
  case TanhF32  extends EmulatedCall("ctanhf", EmulatedCall.Function.Tanh, false)
  case SinhF64  extends EmulatedCall("csinhE", EmulatedCall.Function.Sinh, true)
  case CoshF64  extends EmulatedCall("ccoshE", EmulatedCall.Function.Cosh, true)
  case TanhF64  extends EmulatedCall("ctanhE", EmulatedCall.Function.Tanh, true)
  case ExpF32   extends EmulatedCall("expIf", EmulatedCall.Function.Exp, false)
  case ExpF64   extends EmulatedCall("expId", EmulatedCall.Function.Exp, true)
}
