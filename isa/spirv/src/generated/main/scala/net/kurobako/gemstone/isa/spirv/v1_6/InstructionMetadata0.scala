// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.{Vec}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata0 {
  lazy val m0: Metadata = Metadata(NonEmptyString("Miscellaneous"), Some(NonEmptyString("1.0")), Vec(), Vec())
  lazy val m1: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m2: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("LiteralString"), NonEmptyString("continuedSrc"), OperandQuantifier.One))
  )
  lazy val m3: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("SourceLanguage"), NonEmptyString("sourceLanguage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("version"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("file"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("src"), OperandQuantifier.Optional)
    )
  )
  lazy val m4: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("LiteralString"), NonEmptyString("extensionValue"), OperandQuantifier.One))
  )
  lazy val m5: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("member"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("string"), OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("file"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("line"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("column"), OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    NonEmptyString("Extension"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One))
  )
  lazy val m10: Metadata = Metadata(
    NonEmptyString("Extension"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    NonEmptyString("Extension"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("set"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralExtInstInteger"), NonEmptyString("inst"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcs"), OperandQuantifier.Repeated)
    )
  )
  lazy val m12: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("AddressingModel"), NonEmptyString("addressingModel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryModel"), NonEmptyString("memoryModel"), OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("ExecutionModel"), NonEmptyString("executionModel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("entryPoint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("interface"), OperandQuantifier.Repeated)
    )
  )
  lazy val m14: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("entryPoint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ExecutionMode"), NonEmptyString("mode"), OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("Capability"), NonEmptyString("capability"), OperandQuantifier.One))
  )
  lazy val m16: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m17: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m18: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("width"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("signedness"), OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("width"), OperandQuantifier.One),
      OperandShape(NonEmptyString("FPEncoding"), NonEmptyString("floatingPointEncoding"), OperandQuantifier.Optional)
    )
  )
  lazy val m20: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("componentType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("componentCount"), OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("columnType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("columnCount"), OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Dim"), NonEmptyString("dim"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("depth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("arrayed"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("mS"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("sampled"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageFormat"), NonEmptyString("imageFormat"), OperandQuantifier.One),
      OperandShape(NonEmptyString("AccessQualifier"), NonEmptyString("accessQualifier"), OperandQuantifier.Optional)
    )
  )
  lazy val m23: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m24: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("imageType"), OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("length"), OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementType"), OperandQuantifier.One)
    )
  )
  lazy val m27: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("member0TypeMember1Type"), OperandQuantifier.Repeated)
    )
  )
  lazy val m28: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("theNameOfTheOpaqueType"), OperandQuantifier.One)
    )
  )
  lazy val m29: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.One)
    )
  )
  lazy val m30: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("returnType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("parameter0TypeParameter1Type"), OperandQuantifier.Repeated)
    )
  )
  lazy val m31: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m32: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m33: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m34: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m35: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("AccessQualifier"), NonEmptyString("qualifier"), OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Addresses"), NonEmptyString("PhysicalStorageBufferAddresses")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointerType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralContextDependentNumber"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated)
    )
  )
  lazy val m41: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("LiteralSampler")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("SamplerAddressingMode"),
        NonEmptyString("samplerAddressingMode"),
        OperandQuantifier.One
      ),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("SamplerFilterMode"), NonEmptyString("samplerFilterMode"), OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralContextDependentNumber"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated)
    )
  )
  lazy val m47: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("LiteralSpecConstantOpInteger"),
        NonEmptyString("instructionOpcode"),
        OperandQuantifier.One
      ),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("srcs"), OperandQuantifier.Repeated)
    )
  )
  lazy val m48: Metadata = Metadata(
    NonEmptyString("Function"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("FunctionControl"), NonEmptyString("functionControl"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("functionType"), OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    NonEmptyString("Function"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(NonEmptyString("Function"), Some(NonEmptyString("1.0")), Vec(), Vec())
  lazy val m51: Metadata = Metadata(
    NonEmptyString("Function"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("function"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("argument0Argument1"), OperandQuantifier.Repeated)
    )
  )
  lazy val m52: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("initializer"), OperandQuantifier.Optional)
    )
  )
  lazy val m53: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sample"), OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m55: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m56: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsA"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsB"), OperandQuantifier.Optional)
    )
  )
  lazy val m57: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Addresses"), NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("size"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsA"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsB"), OperandQuantifier.Optional)
    )
  )
  lazy val m58: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m59: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m60: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(
      NonEmptyString("Addresses"),
      NonEmptyString("VariablePointers"),
      NonEmptyString("VariablePointersStorageBuffer"),
      NonEmptyString("PhysicalStorageBufferAddresses")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("element"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m61: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("structure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("arrayMember"), OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Addresses")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("element"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m64: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Decoration"), NonEmptyString("decoration"), OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("structureType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("member"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Decoration"), NonEmptyString("decoration"), OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m67: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("decorationGroup"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targets"), OperandQuantifier.Repeated)
    )
  )
  lazy val m68: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("decorationGroup"), OperandQuantifier.One),
      OperandShape(NonEmptyString("PairIdRefLiteralInteger"), NonEmptyString("targets"), OperandQuantifier.Repeated)
    )
  )
  lazy val m69: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("component"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("components"), OperandQuantifier.Repeated)
    )
  )
  lazy val m72: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated)
    )
  )
  lazy val m73: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("composite"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m74: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("composite"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m75: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampler"), OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.One)
    )
  )
  lazy val m80: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dref"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m81: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dref"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.One)
    )
  )
  lazy val m82: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m83: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.One)
    )
  )
  lazy val m84: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dref"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dref"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m87: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("component"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m88: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dref"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m90: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("texel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m91: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One)
    )
  )
  lazy val m94: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel"), NonEmptyString("ImageQuery")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("levelOfDetail"), OperandQuantifier.One)
    )
  )
  lazy val m95: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel"), NonEmptyString("ImageQuery")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One)
    )
  )
  lazy val m96: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("ImageQuery")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One)
    )
  )
  lazy val m97: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel"), NonEmptyString("ImageQuery")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel"), NonEmptyString("ImageQuery")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("floatValue"), OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("floatValue"), OperandQuantifier.One)
    )
  )
  lazy val m101: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("signedValue"), OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("unsignedValue"), OperandQuantifier.One)
    )
  )
  lazy val m103: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("unsignedValue"), OperandQuantifier.One)
    )
  )
  lazy val m104: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("signedValue"), OperandQuantifier.One)
    )
  )
  lazy val m105: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("floatValue"), OperandQuantifier.One)
    )
  )
  lazy val m106: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Addresses"), NonEmptyString("PhysicalStorageBufferAddresses")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m108: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("signedValue"), OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("unsignedValue"), OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Addresses"), NonEmptyString("PhysicalStorageBufferAddresses")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("integerValue"), OperandQuantifier.One)
    )
  )
  lazy val m111: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storage"), OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m123: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m124: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m125: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m126: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m127: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m128: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m129: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m130: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m131: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("scalar"), OperandQuantifier.One)
    )
  )
  lazy val m132: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("scalar"), OperandQuantifier.One)
    )
  )
  lazy val m133: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One)
    )
  )
  lazy val m134: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One)
    )
  )
  lazy val m135: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("leftMatrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rightMatrix"), OperandQuantifier.One)
    )
  )
  lazy val m136: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Matrix")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One)
    )
  )
  lazy val m137: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One)
    )
  )
  lazy val m138: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector"), OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m145: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m146: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m147: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m148: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m149: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("y"), OperandQuantifier.One)
    )
  )
  lazy val m150: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("y"), OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("y"), OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m153: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m154: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m155: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m156: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m157: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("object1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("object2"), OperandQuantifier.One)
    )
  )
  lazy val m158: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m159: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m160: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m162: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m163: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m164: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m165: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m167: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m168: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m170: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m175: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m176: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m177: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m178: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    NonEmptyString("Relational_and_Logical"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("shift"), OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("shift"), OperandQuantifier.One)
    )
  )
  lazy val m182: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("shift"), OperandQuantifier.One)
    )
  )
  lazy val m183: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m184: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m185: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m186: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m187: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader"), NonEmptyString("BitInstructions")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("insert"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("count"), OperandQuantifier.One)
    )
  )
  lazy val m188: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader"), NonEmptyString("BitInstructions")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("count"), OperandQuantifier.One)
    )
  )
  lazy val m189: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader"), NonEmptyString("BitInstructions")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("count"), OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader"), NonEmptyString("BitInstructions")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One)
    )
  )
  lazy val m191: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One)
    )
  )
  lazy val m192: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m193: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Shader")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m198: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
}
