// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata2 {
  lazy val m0: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape("IdRef", "rayQuery", OperandQuantifier.One), OperandShape("IdRef", "hitT", OperandQuantifier.One))
  )
  lazy val m1: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape("IdRef", "rayQuery", OperandQuantifier.One))
  )
  lazy val m2: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureSampleWeightedQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "texture", OperandQuantifier.One),
      OperandShape("IdRef", "coordinates", OperandQuantifier.One),
      OperandShape("IdRef", "weights", OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBoxFilterQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "texture", OperandQuantifier.One),
      OperandShape("IdRef", "coordinates", OperandQuantifier.One),
      OperandShape("IdRef", "boxSize", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatchQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "reference", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatchQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "reference", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcArray", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "targetSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "referenceSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "targetSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "referenceSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "targetSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "referenceSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "targetSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "targetCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "referenceSampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "referenceCoordinates", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcArray", OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcCooperativeMatrix", OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcArray", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("ImageGatherLinearQCOM", "ImageGatherExtendedModesQCOM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "component", OperandQuantifier.One),
      OperandShape("IdRef", "mode", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m17: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m24: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentMaskAMD"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentMaskAMD"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "fragmentIndex", OperandQuantifier.One)
    )
  )
  lazy val m27: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderClockKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "scope", OperandQuantifier.One)
    )
  )
  lazy val m28: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "visibility", OperandQuantifier.One),
      OperandShape("IdRef", "payloadCount", OperandQuantifier.One),
      OperandShape("IdRef", "nodeIndex", OperandQuantifier.One)
    )
  )
  lazy val m29: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(OperandShape("IdRef", "payloadArray", OperandQuantifier.One))
  )
  lazy val m30: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "payloadType", OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "payloadArray", OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "payloadType", OperandQuantifier.One),
      OperandShape("IdRef", "nodeIndex", OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralString", "literalString", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralString", "literalString", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("QuadControlKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("QuadControlKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "buffer", OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    "Control-Flow",
    Some("None"),
    Vec("AbortKHR"),
    Vec(
      OperandShape("IdRef", "messageType", OperandQuantifier.One),
      OperandShape("IdRef", "message", OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "imageType", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "sample", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Annotation",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape("IdRef", "structureType", OperandQuantifier.One),
      OperandShape("LiteralInteger", "member", OperandQuantifier.One),
      OperandShape("Decoration", "decoration", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "dataType", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ConstantDataKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "data", OperandQuantifier.Repeated)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ConstantDataKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "data", OperandQuantifier.Repeated)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("PoisonFreezeKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("PoisonFreezeKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordStride", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "sBTIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullmask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "time", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(OperandShape("IdRef", "hitObject", OperandQuantifier.One))
  )
  lazy val m59: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullmask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordStride", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "sBTIndex", OperandQuantifier.One),
      OperandShape("IdRef", "origin", OperandQuantifier.One),
      OperandShape("IdRef", "tMin", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One),
      OperandShape("IdRef", "tMax", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttribute", OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m75: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "hint", OperandQuantifier.Optional),
      OperandShape("IdRef", "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(OperandShape("IdRef", "hint", OperandQuantifier.One), OperandShape("IdRef", "bits", OperandQuantifier.One))
  )
  lazy val m80: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m81: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("ImageFootprintNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sampledImage", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "granularity", OperandQuantifier.One),
      OperandShape("IdRef", "coarse", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m82: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeVectorNV", "LongVectorEXT"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "componentType", OperandQuantifier.One),
      OperandShape("IdRef", "componentCount", OperandQuantifier.One)
    )
  )
  lazy val m83: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "input", OperandQuantifier.One),
      OperandShape("IdRef", "inputInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("IdRef", "matrixOffset", OperandQuantifier.One),
      OperandShape("IdRef", "matrixInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "m", OperandQuantifier.One),
      OperandShape("IdRef", "k", OperandQuantifier.One),
      OperandShape("IdRef", "memoryLayout", OperandQuantifier.One),
      OperandShape("IdRef", "transpose", OperandQuantifier.One),
      OperandShape("IdRef", "matrixStride", OperandQuantifier.Optional),
      OperandShape("CooperativeMatrixOperands", "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m84: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorTrainingNV"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "a", OperandQuantifier.One),
      OperandShape("IdRef", "b", OperandQuantifier.One),
      OperandShape("IdRef", "memoryLayout", OperandQuantifier.One),
      OperandShape("IdRef", "matrixInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "matrixStride", OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorTrainingNV"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "v", OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "input", OperandQuantifier.One),
      OperandShape("IdRef", "inputInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("IdRef", "matrixOffset", OperandQuantifier.One),
      OperandShape("IdRef", "matrixInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "bias", OperandQuantifier.One),
      OperandShape("IdRef", "biasOffset", OperandQuantifier.One),
      OperandShape("IdRef", "biasInterpretation", OperandQuantifier.One),
      OperandShape("IdRef", "m", OperandQuantifier.One),
      OperandShape("IdRef", "k", OperandQuantifier.One),
      OperandShape("IdRef", "memoryLayout", OperandQuantifier.One),
      OperandShape("IdRef", "transpose", OperandQuantifier.One),
      OperandShape("IdRef", "matrixStride", OperandQuantifier.Optional),
      OperandShape("CooperativeMatrixOperands", "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m87: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionsNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One)
    )
  )
  lazy val m88: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingEXT"),
    Vec(
      OperandShape("IdRef", "groupCountX", OperandQuantifier.One),
      OperandShape("IdRef", "groupCountY", OperandQuantifier.One),
      OperandShape("IdRef", "groupCountZ", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingEXT"),
    Vec(
      OperandShape("IdRef", "vertexCount", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveCount", OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m91: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingNV"),
    Vec(
      OperandShape("IdRef", "indexOffset", OperandQuantifier.One),
      OperandShape("IdRef", "packedIndices", OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DisplacementMicromapNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveIndex", OperandQuantifier.One),
      OperandShape("IdRef", "barycentric", OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DisplacementMicromapNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "instanceId", OperandQuantifier.One),
      OperandShape("IdRef", "geometryIndex", OperandQuantifier.One),
      OperandShape("IdRef", "primitiveIndex", OperandQuantifier.One),
      OperandShape("IdRef", "barycentric", OperandQuantifier.One)
    )
  )
  lazy val m94: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m95: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m96: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordIndex", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttributes", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.Optional)
    )
  )
  lazy val m97: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m101: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "sBTRecordIndex", OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One),
      OperandShape("IdRef", "hint", OperandQuantifier.Optional),
      OperandShape("IdRef", "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m103: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One),
      OperandShape("IdRef", "hint", OperandQuantifier.Optional),
      OperandShape("IdRef", "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m104: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One),
      OperandShape("IdRef", "hint", OperandQuantifier.Optional),
      OperandShape("IdRef", "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m105: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m106: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(OperandShape("IdRef", "hint", OperandQuantifier.One), OperandShape("IdRef", "bits", OperandQuantifier.One))
  )
  lazy val m107: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "hint", OperandQuantifier.Optional),
      OperandShape("IdRef", "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m108: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "accelerationStructure", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "currentTime", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(OperandShape("IdRef", "hitObject", OperandQuantifier.One))
  )
  lazy val m111: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdRef", "hitObject", OperandQuantifier.One),
      OperandShape("IdRef", "hitObjectAttribute", OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m123: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m124: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m125: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m126: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV", "RayTracingKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hit", OperandQuantifier.One),
      OperandShape("IdRef", "hitKind", OperandQuantifier.One)
    )
  )
  lazy val m127: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingNV"), Vec())
  lazy val m128: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingNV"), Vec())
  lazy val m129: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV"),
    Vec(
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "payloadId", OperandQuantifier.One)
    )
  )
  lazy val m130: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "time", OperandQuantifier.One),
      OperandShape("IdRef", "payloadId", OperandQuantifier.One)
    )
  )
  lazy val m131: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingMotionBlurNV"),
    Vec(
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "sBTOffset", OperandQuantifier.One),
      OperandShape("IdRef", "sBTStride", OperandQuantifier.One),
      OperandShape("IdRef", "missIndex", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTmax", OperandQuantifier.One),
      OperandShape("IdRef", "time", OperandQuantifier.One),
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m132: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryPositionFetchKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m133: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("RayTracingNV", "RayTracingKHR", "RayQueryKHR", "DisplacementMicromapNV"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m134: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV"),
    Vec(
      OperandShape("IdRef", "sBTIndex", OperandQuantifier.One),
      OperandShape("IdRef", "callableDataId", OperandQuantifier.One)
    )
  )
  lazy val m135: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingClusterAccelerationStructureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m136: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingClusterAccelerationStructureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m137: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m138: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "componentType", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "rows", OperandQuantifier.One),
      OperandShape("IdRef", "columns", OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.One),
      OperandShape("IdRef", "columnMajor", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.One),
      OperandShape("IdRef", "columnMajor", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "a", OperandQuantifier.One),
      OperandShape("IdRef", "b", OperandQuantifier.One),
      OperandShape("IdRef", "c", OperandQuantifier.One)
    )
  )
  lazy val m147: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "dataType", OperandQuantifier.One)
    )
  )
  lazy val m148: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentShaderSampleInterlockEXT", "FragmentShaderPixelInterlockEXT", "FragmentShaderShadingRateInterlockEXT"),
    Vec()
  )
  lazy val m149: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentShaderSampleInterlockEXT", "FragmentShaderPixelInterlockEXT", "FragmentShaderShadingRateInterlockEXT"),
    Vec()
  )
  lazy val m150: Metadata = Metadata(
    "Arithmetic",
    Some("None"),
    Vec("CooperativeMatrixReductionsNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("CooperativeMatrixReduce", "reduce", OperandQuantifier.One),
      OperandShape("IdRef", "combineFunc", OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixTensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.One),
      OperandShape("TensorAddressingOperands", "tensorAddressingOperands", OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixTensorAddressingNV"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.One),
      OperandShape("TensorAddressingOperands", "tensorAddressingOperands", OperandQuantifier.One)
    )
  )
  lazy val m153: Metadata = Metadata(
    "Function",
    Some("None"),
    Vec("CooperativeMatrixPerElementOperationsNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One),
      OperandShape("IdRef", "func", OperandQuantifier.One),
      OperandShape("IdRef", "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m154: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "dim", OperandQuantifier.One),
      OperandShape("IdRef", "clampMode", OperandQuantifier.One)
    )
  )
  lazy val m155: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "dim", OperandQuantifier.One),
      OperandShape("IdRef", "hasDimensions", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m157: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("IdRef", "dim", OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.Repeated)
    )
  )
  lazy val m159: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("IdRef", "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m160: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m162: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorView", OperandQuantifier.One),
      OperandShape("IdRef", "dim", OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorView", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata("Control-Flow", Some("1.6"), Vec("DemoteToHelperInvocation"), Vec())
  lazy val m165: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DemoteToHelperInvocation"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorView", OperandQuantifier.One),
      OperandShape("IdRef", "clipRowOffset", OperandQuantifier.One),
      OperandShape("IdRef", "clipRowSpan", OperandQuantifier.One),
      OperandShape("IdRef", "clipColOffset", OperandQuantifier.One),
      OperandShape("IdRef", "clipColSpan", OperandQuantifier.One)
    )
  )
  lazy val m167: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensorLayout", OperandQuantifier.One),
      OperandShape("IdRef", "blockSize", OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionsNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "matrix", OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m170: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m175: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(OperandShape("LiteralInteger", "bitWidth", OperandQuantifier.One))
  )
  lazy val m176: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("RawAccessChainsNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "byteStride", OperandQuantifier.One),
      OperandShape("IdRef", "elementIndex", OperandQuantifier.One),
      OperandShape("IdRef", "byteOffset", OperandQuantifier.One),
      OperandShape("RawAccessChainOperands", "rawAccessChainOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m177: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m178: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m182: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m183: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m184: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m185: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m186: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m187: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m188: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m189: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "intersection", OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "data", OperandQuantifier.One),
      OperandShape("IdRef", "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m191: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "current", OperandQuantifier.One),
      OperandShape("IdRef", "next", OperandQuantifier.One),
      OperandShape("IdRef", "delta", OperandQuantifier.One)
    )
  )
  lazy val m192: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "previous", OperandQuantifier.One),
      OperandShape("IdRef", "current", OperandQuantifier.One),
      OperandShape("IdRef", "delta", OperandQuantifier.One)
    )
  )
  lazy val m193: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "data", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferBlockIOINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "ptr", OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferBlockIOINTEL"),
    Vec(OperandShape("IdRef", "ptr", OperandQuantifier.One), OperandShape("IdRef", "data", OperandQuantifier.One))
  )
  lazy val m196: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageBlockIOINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageBlockIOINTEL"),
    Vec(
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "data", OperandQuantifier.One)
    )
  )
  lazy val m198: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageMediaBlockIOINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "width", OperandQuantifier.One),
      OperandShape("IdRef", "height", OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageMediaBlockIOINTEL"),
    Vec(
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "width", OperandQuantifier.One),
      OperandShape("IdRef", "height", OperandQuantifier.One),
      OperandShape("IdRef", "data", OperandQuantifier.One)
    )
  )
}
