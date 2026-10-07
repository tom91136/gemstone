// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata0 {
  lazy val m0: Vec[OperandShape] = Vec()
  lazy val m1: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m2: Vec[OperandShape] = Vec(OperandShape(OperandKind.LiteralString, OperandQuantifier.One))
  lazy val m3: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.SourceLanguage, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Optional),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.Optional)
  )
  lazy val m4: Vec[OperandShape] = Vec(OperandShape(OperandKind.LiteralString, OperandQuantifier.One))
  lazy val m5: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One)
  )
  lazy val m6: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One)
  )
  lazy val m7: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One)
  )
  lazy val m8: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One)
  )
  lazy val m9: Vec[OperandShape] = Vec(OperandShape(OperandKind.LiteralString, OperandQuantifier.One))
  lazy val m10: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One)
  )
  lazy val m11: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralExtInstInteger, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m12: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.AddressingModel, OperandQuantifier.One),
    OperandShape(OperandKind.MemoryModel, OperandQuantifier.One)
  )
  lazy val m13: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.ExecutionModel, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m14: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ExecutionMode, OperandQuantifier.One)
  )
  lazy val m15: Vec[OperandShape] = Vec(OperandShape(OperandKind.Capability, OperandQuantifier.One))
  lazy val m16: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m17: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m18: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One)
  )
  lazy val m19: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.FPEncoding, OperandQuantifier.Optional)
  )
  lazy val m20: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One)
  )
  lazy val m21: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One)
  )
  lazy val m22: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.Dim, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.ImageFormat, OperandQuantifier.One),
    OperandShape(OperandKind.AccessQualifier, OperandQuantifier.Optional)
  )
  lazy val m23: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m24: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m25: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m26: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m27: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m28: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralString, OperandQuantifier.One)
  )
  lazy val m29: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.StorageClass, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m30: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m31: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m32: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m33: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m34: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m35: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.AccessQualifier, OperandQuantifier.One)
  )
  lazy val m36: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.StorageClass, OperandQuantifier.One)
  )
  lazy val m37: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m38: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m39: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralContextDependentNumber, OperandQuantifier.One)
  )
  lazy val m40: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m41: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.SamplerAddressingMode, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.SamplerFilterMode, OperandQuantifier.One)
  )
  lazy val m42: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m43: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m44: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m45: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralContextDependentNumber, OperandQuantifier.One)
  )
  lazy val m46: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m47: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralSpecConstantOpInteger, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.Repeated)
  )
  lazy val m48: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.FunctionControl, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m49: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One)
  )
  lazy val m50: Vec[OperandShape] = Vec()
  lazy val m51: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m52: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.StorageClass, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Optional)
  )
  lazy val m53: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m54: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional)
  )
  lazy val m55: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional)
  )
  lazy val m56: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional)
  )
  lazy val m57: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional),
    OperandShape(OperandKind.MemoryAccess, OperandQuantifier.Optional)
  )
  lazy val m58: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m59: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m60: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m61: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One)
  )
  lazy val m62: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m63: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m64: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.Decoration, OperandQuantifier.One)
  )
  lazy val m65: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.One),
    OperandShape(OperandKind.Decoration, OperandQuantifier.One)
  )
  lazy val m66: Vec[OperandShape] = Vec(OperandShape(OperandKind.IdResult, OperandQuantifier.One))
  lazy val m67: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m68: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.PairIdRefLiteralInteger, OperandQuantifier.Repeated)
  )
  lazy val m69: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m70: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m71: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.Repeated)
  )
  lazy val m72: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.Repeated)
  )
  lazy val m73: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.Repeated)
  )
  lazy val m74: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.LiteralInteger, OperandQuantifier.Repeated)
  )
  lazy val m75: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m76: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m77: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m78: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m79: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.One)
  )
  lazy val m80: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m81: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.One)
  )
  lazy val m82: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m83: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.One)
  )
  lazy val m84: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m85: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.One)
  )
  lazy val m86: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m87: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m88: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m89: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m90: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.ImageOperands, OperandQuantifier.Optional)
  )
  lazy val m91: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m92: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m93: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m94: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m95: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m96: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m97: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m98: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m99: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m100: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m101: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m102: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m103: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m104: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m105: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m106: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m107: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m108: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m109: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m110: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m111: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m112: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m113: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.StorageClass, OperandQuantifier.One)
  )
  lazy val m114: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m115: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m116: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m117: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m118: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m119: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m120: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m121: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m122: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m123: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m124: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m125: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m126: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m127: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m128: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m129: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m130: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m131: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m132: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m133: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m134: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m135: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m136: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m137: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m138: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m139: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m140: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m141: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m142: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m143: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m144: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m145: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m146: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m147: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m148: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m149: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m150: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m151: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m152: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m153: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m154: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m155: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m156: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m157: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m158: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m159: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m160: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m161: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m162: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m163: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m164: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m165: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m166: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m167: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m168: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m169: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m170: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m171: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m172: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m173: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m174: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m175: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m176: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m177: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m178: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m179: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m180: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m181: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m182: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m183: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m184: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m185: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m186: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m187: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m188: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m189: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m190: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m191: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m192: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m193: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m194: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m195: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m196: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m197: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m198: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
  lazy val m199: Vec[OperandShape] = Vec(
    OperandShape(OperandKind.IdResultType, OperandQuantifier.One),
    OperandShape(OperandKind.IdResult, OperandQuantifier.One),
    OperandShape(OperandKind.IdRef, OperandQuantifier.One)
  )
}
