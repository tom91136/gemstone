package net.kurobako.gemstone.isa.amdgpu

import net.kurobako.gemstone.collection.*

private object VMFunction {
  private lazy val patterns = Vec.from(values)

  def matching(name: String): Vec[VMFunction] =
    patterns.filter(value => name.contains(value.pattern))
}

private enum VMFunction(val pattern: String) {
  case ComplexLogF32   extends VMFunction("logIf")
  case ComplexLogF64   extends VMFunction("logId")
  case ComplexAsinhF32 extends VMFunction("casinhf")
  case ComplexAsinhF64 extends VMFunction("casinhE")
  case ComplexAcosF32  extends VMFunction("cacosf")
  case ComplexAcosF64  extends VMFunction("cacosE")
  case ComplexAtanF32  extends VMFunction("catanf")
  case ComplexAtanF64  extends VMFunction("catanE")
  case ComplexAcoshF32 extends VMFunction("cacoshf")
  case ComplexAcoshF64 extends VMFunction("cacoshE")
  case ComplexAtanhF32 extends VMFunction("catanhf")
  case ComplexAtanhF64 extends VMFunction("catanhE")
  case ComplexSinhF32  extends VMFunction("csinhf")
  case ComplexCoshF32  extends VMFunction("ccoshf")
  case ComplexTanhF32  extends VMFunction("ctanhf")
  case ComplexSinhF64  extends VMFunction("csinhE")
  case ComplexCoshF64  extends VMFunction("ccoshE")
  case ComplexTanhF64  extends VMFunction("ctanhE")
  case ComplexExpF32   extends VMFunction("expIf")
  case ComplexExpF64   extends VMFunction("expId")
}
