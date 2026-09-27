// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.{Vec}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata2 {
  lazy val m0: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitT"), OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One))
  )
  lazy val m2: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureSampleWeightedQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("texture"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("weights"), OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBoxFilterQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("texture"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("boxSize"), OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatchQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reference"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatchQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reference"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcArray"), OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatch2QCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatch2QCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatch2QCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TextureBlockMatch2QCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceSampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("referenceCoordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcArray"), OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcCooperativeMatrix"), OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcArray"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ImageGatherLinearQCOM"), NonEmptyString("ImageGatherExtendedModesQCOM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("component"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mode"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m17: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m24: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FragmentMaskAMD")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FragmentMaskAMD")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("fragmentIndex"), OperandQuantifier.One)
    )
  )
  lazy val m27: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderClockKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("scope"), OperandQuantifier.One)
    )
  )
  lazy val m28: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("visibility"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("nodeIndex"), OperandQuantifier.One)
    )
  )
  lazy val m29: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadArray"), OperandQuantifier.One))
  )
  lazy val m30: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadType"), OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadArray"), OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("nodeIndex"), OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("literalString"), OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderEnqueueAMDX")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("literalString"), OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("QuadControlKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("QuadControlKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DescriptorHeapEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DescriptorHeapEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("buffer"), OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("AbortKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("messageType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("message"), OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DescriptorHeapEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("imageType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sample"), OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DescriptorHeapEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("structureType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("member"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Decoration"), NonEmptyString("decoration"), OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DescriptorHeapEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ConstantDataKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("data"), OperandQuantifier.Repeated)
    )
  )
  lazy val m45: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ConstantDataKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("data"), OperandQuantifier.Repeated)
    )
  )
  lazy val m46: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("PoisonFreezeKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("PoisonFreezeKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttributes"), OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttributes"), OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullmask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("time"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One))
  )
  lazy val m59: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullmask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttributes"), OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttributes"), OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("origin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tMax"), OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttribute"), OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m75: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.One)
    )
  )
  lazy val m80: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderNV")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m81: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ImageFootprintNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sampledImage"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("granularity"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coarse"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m82: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorNV"), NonEmptyString("LongVectorEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("componentType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("componentCount"), OperandQuantifier.One)
    )
  )
  lazy val m83: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("input"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("inputInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("m"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("k"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("transpose"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixStride"), OperandQuantifier.Optional),
      OperandShape(
        NonEmptyString("CooperativeMatrixOperands"),
        NonEmptyString("cooperativeMatrixOperands"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m84: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorTrainingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("a"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("b"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixStride"), OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorTrainingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("v"), OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("input"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("inputInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bias"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("biasOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("biasInterpretation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("m"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("k"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("transpose"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixStride"), OperandQuantifier.Optional),
      OperandShape(
        NonEmptyString("CooperativeMatrixOperands"),
        NonEmptyString("cooperativeMatrixOperands"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m87: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionsNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One)
    )
  )
  lazy val m88: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("MeshShadingEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("groupCountX"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("groupCountY"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("groupCountZ"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("MeshShadingEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vertexCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveCount"), OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupNonUniformPartitionedEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m91: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("MeshShadingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packedIndices"), OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DisplacementMicromapNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("barycentric"), OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DisplacementMicromapNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("instanceId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("geometryIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("primitiveIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("barycentric"), OperandQuantifier.One)
    )
  )
  lazy val m94: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m95: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeVectorNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m96: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttributes"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.Optional)
    )
  )
  lazy val m97: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m101: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTRecordIndex"), OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.Optional)
    )
  )
  lazy val m103: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.Optional)
    )
  )
  lazy val m104: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.Optional)
    )
  )
  lazy val m105: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m106: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hint"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bits"), OperandQuantifier.Optional)
    )
  )
  lazy val m108: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accelerationStructure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("currentTime"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One))
  )
  lazy val m111: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT"), NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObjectAttribute"), OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m123: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m124: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m125: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m126: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingNV"), NonEmptyString("RayTracingKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hit"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitKind"), OperandQuantifier.One)
    )
  )
  lazy val m127: Metadata =
    Metadata(NonEmptyString("Reserved"), Some(NonEmptyString("None")), Vec(NonEmptyString("RayTracingNV")), Vec())
  lazy val m128: Metadata =
    Metadata(NonEmptyString("Reserved"), Some(NonEmptyString("None")), Vec(NonEmptyString("RayTracingNV")), Vec())
  lazy val m129: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadId"), OperandQuantifier.One)
    )
  )
  lazy val m130: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("time"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payloadId"), OperandQuantifier.One)
    )
  )
  lazy val m131: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingMotionBlurNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("missIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTmax"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("time"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m132: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryPositionFetchKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m133: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(
      NonEmptyString("RayTracingNV"),
      NonEmptyString("RayTracingKHR"),
      NonEmptyString("RayQueryKHR"),
      NonEmptyString("DisplacementMicromapNV")
    ),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m134: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("callableDataId"), OperandQuantifier.One)
    )
  )
  lazy val m135: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingClusterAccelerationStructureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m136: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingClusterAccelerationStructureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m137: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m138: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ShaderInvocationReorderEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixNV")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("componentType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rows"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("columns"), OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("columnMajor"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("columnMajor"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("a"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("b"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("c"), OperandQuantifier.One)
    )
  )
  lazy val m147: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.One)
    )
  )
  lazy val m148: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(
      NonEmptyString("FragmentShaderSampleInterlockEXT"),
      NonEmptyString("FragmentShaderPixelInterlockEXT"),
      NonEmptyString("FragmentShaderShadingRateInterlockEXT")
    ),
    Vec()
  )
  lazy val m149: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(
      NonEmptyString("FragmentShaderSampleInterlockEXT"),
      NonEmptyString("FragmentShaderPixelInterlockEXT"),
      NonEmptyString("FragmentShaderShadingRateInterlockEXT")
    ),
    Vec()
  )
  lazy val m150: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixReductionsNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("CooperativeMatrixReduce"), NonEmptyString("reduce"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("combineFunc"), OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixTensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("TensorAddressingOperands"),
        NonEmptyString("tensorAddressingOperands"),
        OperandQuantifier.One
      )
    )
  )
  lazy val m152: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixTensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("TensorAddressingOperands"),
        NonEmptyString("tensorAddressingOperands"),
        OperandQuantifier.One
      )
    )
  )
  lazy val m153: Metadata = Metadata(
    NonEmptyString("Function"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixPerElementOperationsNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("func"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcs"), OperandQuantifier.Repeated)
    )
  )
  lazy val m154: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dim"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clampMode"), OperandQuantifier.One)
    )
  )
  lazy val m155: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dim"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hasDimensions"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m157: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dim"), OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.Repeated)
    )
  )
  lazy val m159: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcs"), OperandQuantifier.Repeated)
    )
  )
  lazy val m160: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m162: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorView"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dim"), OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorView"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DemoteToHelperInvocation")),
    Vec()
  )
  lazy val m165: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DemoteToHelperInvocation")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorView"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clipRowOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clipRowSpan"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clipColOffset"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clipColSpan"), OperandQuantifier.One)
    )
  )
  lazy val m167: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorAddressingNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensorLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockSize"), OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixConversionsNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrix"), OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m170: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m175: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessTextureNV")),
    Vec(OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("bitWidth"), OperandQuantifier.One))
  )
  lazy val m176: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RawAccessChainsNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("byteStride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("byteOffset"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("RawAccessChainOperands"),
        NonEmptyString("rawAccessChainOperands"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m177: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m178: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m182: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m183: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m184: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m185: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m186: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m187: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("hitObject"), OperandQuantifier.One)
    )
  )
  lazy val m188: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m189: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingLinearSweptSpheresGeometryNV")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("intersection"), OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupShuffleINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("data"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invocationId"), OperandQuantifier.One)
    )
  )
  lazy val m191: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupShuffleINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("current"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("next"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("delta"), OperandQuantifier.One)
    )
  )
  lazy val m192: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupShuffleINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("previous"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("current"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("delta"), OperandQuantifier.One)
    )
  )
  lazy val m193: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupShuffleINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("data"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBufferBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("ptr"), OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBufferBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("ptr"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("data"), OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupImageBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupImageBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("data"), OperandQuantifier.One)
    )
  )
  lazy val m198: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupImageMediaBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("width"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("height"), OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupImageMediaBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("width"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("height"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("data"), OperandQuantifier.One)
    )
  )
}
