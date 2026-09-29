// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata0 {
  lazy val m0: Metadata = Metadata("Miscellaneous", Some("1.0"), Vec(), Vec())
  lazy val m1: Metadata = Metadata(
    "Miscellaneous",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m2: Metadata =
    Metadata("Debug", Some("1.0"), Vec(), Vec(OperandShape("LiteralString", "continuedSrc", OperandQuantifier.One)))
  lazy val m3: Metadata = Metadata(
    "Debug",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("SourceLanguage", "sourceLanguage", OperandQuantifier.One),
      OperandShape("LiteralInteger", "version", OperandQuantifier.One),
      OperandShape("IdRef", "file", OperandQuantifier.Optional),
      OperandShape("LiteralString", "src", OperandQuantifier.Optional)
    )
  )
  lazy val m4: Metadata =
    Metadata("Debug", Some("1.0"), Vec(), Vec(OperandShape("LiteralString", "extensionValue", OperandQuantifier.One)))
  lazy val m5: Metadata = Metadata(
    "Debug",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "Debug",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "dataType", OperandQuantifier.One),
      OperandShape("LiteralInteger", "member", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Debug",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralString", "string", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Debug",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "file", OperandQuantifier.One),
      OperandShape("LiteralInteger", "line", OperandQuantifier.One),
      OperandShape("LiteralInteger", "column", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata =
    Metadata("Extension", Some("1.0"), Vec(), Vec(OperandShape("LiteralString", "name", OperandQuantifier.One)))
  lazy val m10: Metadata = Metadata(
    "Extension",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    "Extension",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "set", OperandQuantifier.One),
      OperandShape("LiteralExtInstInteger", "inst", OperandQuantifier.One),
      OperandShape("IdRef", "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m12: Metadata = Metadata(
    "Mode-Setting",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("AddressingModel", "addressingModel", OperandQuantifier.One),
      OperandShape("MemoryModel", "memoryModel", OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    "Mode-Setting",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("ExecutionModel", "executionModel", OperandQuantifier.One),
      OperandShape("IdRef", "entryPoint", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One),
      OperandShape("IdRef", "interface", OperandQuantifier.Repeated)
    )
  )
  lazy val m14: Metadata = Metadata(
    "Mode-Setting",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "entryPoint", OperandQuantifier.One),
      OperandShape("ExecutionMode", "mode", OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata =
    Metadata("Mode-Setting", Some("1.0"), Vec(), Vec(OperandShape("Capability", "capability", OperandQuantifier.One)))
  lazy val m16: Metadata =
    Metadata("Type-Declaration", Some("1.0"), Vec(), Vec(OperandShape("IdResult", "dest", OperandQuantifier.One)))
  lazy val m17: Metadata =
    Metadata("Type-Declaration", Some("1.0"), Vec(), Vec(OperandShape("IdResult", "dest", OperandQuantifier.One)))
  lazy val m18: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "width", OperandQuantifier.One),
      OperandShape("LiteralInteger", "signedness", OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "width", OperandQuantifier.One),
      OperandShape("FPEncoding", "floatingPointEncoding", OperandQuantifier.Optional)
    )
  )
  lazy val m20: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "componentType", OperandQuantifier.One),
      OperandShape("LiteralInteger", "componentCount", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "columnType", OperandQuantifier.One),
      OperandShape("LiteralInteger", "columnCount", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledType", OperandQuantifier.One),
      OperandShape("Dim", "dim", OperandQuantifier.One),
      OperandShape("LiteralInteger", "depth", OperandQuantifier.One),
      OperandShape("LiteralInteger", "arrayed", OperandQuantifier.One),
      OperandShape("LiteralInteger", "mS", OperandQuantifier.One),
      OperandShape("LiteralInteger", "sampled", OperandQuantifier.One),
      OperandShape("ImageFormat", "imageFormat", OperandQuantifier.One),
      OperandShape("AccessQualifier", "accessQualifier", OperandQuantifier.Optional)
    )
  )
  lazy val m23: Metadata =
    Metadata("Type-Declaration", Some("1.0"), Vec(), Vec(OperandShape("IdResult", "dest", OperandQuantifier.One)))
  lazy val m24: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "imageType", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "elementType", OperandQuantifier.One),
      OperandShape("IdRef", "length", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "elementType", OperandQuantifier.One)
    )
  )
  lazy val m27: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "member0TypeMember1Type", OperandQuantifier.Repeated)
    )
  )
  lazy val m28: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralString", "theNameOfTheOpaqueType", OperandQuantifier.One)
    )
  )
  lazy val m29: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One),
      OperandShape("IdRef", "dataType", OperandQuantifier.One)
    )
  )
  lazy val m30: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "returnType", OperandQuantifier.One),
      OperandShape("IdRef", "parameter0TypeParameter1Type", OperandQuantifier.Repeated)
    )
  )
  lazy val m31: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Kernel"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m32: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m33: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Pipes"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m34: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m35: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("AccessQualifier", "qualifier", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "Type-Declaration",
    Some("1.0"),
    Vec("Addresses", "PhysicalStorageBufferAddresses"),
    Vec(
      OperandShape("IdRef", "pointerType", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralContextDependentNumber", "value", OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "constituents", OperandQuantifier.Repeated)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec("LiteralSampler"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("SamplerAddressingMode", "samplerAddressingMode", OperandQuantifier.One),
      OperandShape("LiteralInteger", "param", OperandQuantifier.One),
      OperandShape("SamplerFilterMode", "samplerFilterMode", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralContextDependentNumber", "value", OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "constituents", OperandQuantifier.Repeated)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Constant-Creation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralSpecConstantOpInteger", "instructionOpcode", OperandQuantifier.One),
      OperandShape("LiteralInteger", "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Function",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("FunctionControl", "functionControl", OperandQuantifier.One),
      OperandShape("IdRef", "functionType", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Function",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata("Function", Some("1.0"), Vec(), Vec())
  lazy val m51: Metadata = Metadata(
    "Function",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "function", OperandQuantifier.One),
      OperandShape("IdRef", "argument0Argument1", OperandQuantifier.Repeated)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One),
      OperandShape("IdRef", "initializer", OperandQuantifier.Optional)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "sample", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("MemoryAccess", "modsA", OperandQuantifier.Optional),
      OperandShape("MemoryAccess", "modsB", OperandQuantifier.Optional)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec("Addresses", "UntypedPointersKHR"),
    Vec(
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "size", OperandQuantifier.One),
      OperandShape("MemoryAccess", "modsA", OperandQuantifier.Optional),
      OperandShape("MemoryAccess", "modsB", OperandQuantifier.Optional)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m59: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec("Addresses", "VariablePointers", "VariablePointersStorageBuffer", "PhysicalStorageBufferAddresses"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "element", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "structure", OperandQuantifier.One),
      OperandShape("LiteralInteger", "arrayMember", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Memory",
    Some("1.0"),
    Vec("Addresses"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "element", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Annotation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("Decoration", "decoration", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Annotation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "structureType", OperandQuantifier.One),
      OperandShape("LiteralInteger", "member", OperandQuantifier.One),
      OperandShape("Decoration", "decoration", OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata =
    Metadata("Annotation", Some("1.0"), Vec(), Vec(OperandShape("IdResult", "dest", OperandQuantifier.One)))
  lazy val m67: Metadata = Metadata(
    "Annotation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "decorationGroup", OperandQuantifier.One),
      OperandShape("IdRef", "targets", OperandQuantifier.Repeated)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Annotation",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "decorationGroup", OperandQuantifier.One),
      OperandShape("PairIdRefLiteralInteger", "targets", OperandQuantifier.Repeated)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One),
      OperandShape("IdRef", "component", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("LiteralInteger", "components", OperandQuantifier.Repeated)
    )
  )
  lazy val m72: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "constituents", OperandQuantifier.Repeated)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "composite", OperandQuantifier.One),
      OperandShape("LiteralInteger", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "composite", OperandQuantifier.One),
      OperandShape("LiteralInteger", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m75: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    "Composite",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "sampler", OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.One)
    )
  )
  lazy val m80: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dref", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m81: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dref", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.One)
    )
  )
  lazy val m82: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m83: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.One)
    )
  )
  lazy val m84: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dref", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dref", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m87: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "component", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m88: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dref", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m90: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "texel", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m91: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One)
    )
  )
  lazy val m94: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel", "ImageQuery"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "levelOfDetail", OperandQuantifier.One)
    )
  )
  lazy val m95: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel", "ImageQuery"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One)
    )
  )
  lazy val m96: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("ImageQuery"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m97: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel", "ImageQuery"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("Kernel", "ImageQuery"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m101: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "signedValue", OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "unsignedValue", OperandQuantifier.One)
    )
  )
  lazy val m103: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "unsignedValue", OperandQuantifier.One)
    )
  )
  lazy val m104: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "signedValue", OperandQuantifier.One)
    )
  )
  lazy val m105: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m106: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Addresses", "PhysicalStorageBufferAddresses"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m108: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "signedValue", OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "unsignedValue", OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Addresses", "PhysicalStorageBufferAddresses"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "integerValue", OperandQuantifier.One)
    )
  )
  lazy val m111: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("StorageClass", "storage", OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    "Conversion",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m123: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m124: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m125: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m126: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m127: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m128: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m129: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m130: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m131: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One),
      OperandShape("IdRef", "scalar", OperandQuantifier.One)
    )
  )
  lazy val m132: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("IdRef", "scalar", OperandQuantifier.One)
    )
  )
  lazy val m133: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One)
    )
  )
  lazy val m134: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One)
    )
  )
  lazy val m135: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "leftMatrix", OperandQuantifier.One),
      OperandShape("IdRef", "rightMatrix", OperandQuantifier.One)
    )
  )
  lazy val m136: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec("Matrix"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One)
    )
  )
  lazy val m137: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One)
    )
  )
  lazy val m138: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    "Arithmetic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector", OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m145: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m146: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m147: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m148: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m149: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One),
      OperandShape("IdRef", "y", OperandQuantifier.One)
    )
  )
  lazy val m150: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One),
      OperandShape("IdRef", "y", OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One),
      OperandShape("IdRef", "y", OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m153: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m154: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m155: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m156: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m157: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("IdRef", "object1", OperandQuantifier.One),
      OperandShape("IdRef", "object2", OperandQuantifier.One)
    )
  )
  lazy val m158: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m159: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m160: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m162: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m163: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m164: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m165: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m167: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m168: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m170: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m175: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m176: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m177: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m178: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    "Relational_and_Logical",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "shift", OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "shift", OperandQuantifier.One)
    )
  )
  lazy val m182: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "shift", OperandQuantifier.One)
    )
  )
  lazy val m183: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m184: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m185: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m186: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m187: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec("Shader", "BitInstructions"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "insert", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "count", OperandQuantifier.One)
    )
  )
  lazy val m188: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec("Shader", "BitInstructions"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "count", OperandQuantifier.One)
    )
  )
  lazy val m189: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec("Shader", "BitInstructions"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "count", OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec("Shader", "BitInstructions"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One)
    )
  )
  lazy val m191: Metadata = Metadata(
    "Bit",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One)
    )
  )
  lazy val m192: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m193: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("Shader"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m198: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
}
