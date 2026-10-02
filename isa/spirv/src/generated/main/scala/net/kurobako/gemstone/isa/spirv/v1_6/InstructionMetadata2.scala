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
    Vec(
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitT", OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One))
  )
  lazy val m2: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureSampleWeightedQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "texture", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "weights", OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBoxFilterQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "texture", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "boxSize", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatchQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "target", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reference", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatchQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "target", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reference", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcArray", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TextureBlockMatch2QCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "targetCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceSampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "referenceCoordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcArray", OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcCooperativeMatrix", OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("CooperativeMatrixConversionQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcArray", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("ImageGatherLinearQCOM", "ImageGatherExtendedModesQCOM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "component", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "mode", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m17: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m24: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentMaskAMD"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("FragmentMaskAMD"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "fragmentIndex", OperandQuantifier.One)
    )
  )
  lazy val m27: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderClockKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "scope", OperandQuantifier.One)
    )
  )
  lazy val m28: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "visibility", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "nodeIndex", OperandQuantifier.One)
    )
  )
  lazy val m29: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(OperandShape(OperandKind.IdRef, "payloadArray", OperandQuantifier.One))
  )
  lazy val m30: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadType", OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadArray", OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "nodeIndex", OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralString, "literalString", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderEnqueueAMDX"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralString, "literalString", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("QuadControlKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("QuadControlKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.StorageClass, "storageClass", OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "buffer", OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    "Control-Flow",
    Some("None"),
    Vec("AbortKHR"),
    Vec(
      OperandShape(OperandKind.IdRef, "messageType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "message", OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "imageType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sample", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Annotation",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "structureType", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "member", OperandQuantifier.One),
      OperandShape(OperandKind.Decoration, "decoration", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("DescriptorHeapEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dataType", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ConstantDataKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "data", OperandQuantifier.Repeated)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ConstantDataKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "data", OperandQuantifier.Repeated)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("PoisonFreezeKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("PoisonFreezeKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullmask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "time", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One))
  )
  lazy val m59: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullmask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttributes", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "origin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tMax", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttribute", OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m75: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.One)
    )
  )
  lazy val m80: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("ShaderInvocationReorderNV"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m81: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("ImageFootprintNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "granularity", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coarse", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m82: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeVectorNV", "LongVectorEXT"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "componentType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "componentCount", OperandQuantifier.One)
    )
  )
  lazy val m83: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "input", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "inputInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "m", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "k", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "transpose", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixStride", OperandQuantifier.Optional),
      OperandShape(OperandKind.CooperativeMatrixOperands, "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m84: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorTrainingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "offset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "a", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "b", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixStride", OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorTrainingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "offset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "v", OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "input", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "inputInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "bias", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "biasOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "biasInterpretation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "m", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "k", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "transpose", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixStride", OperandQuantifier.Optional),
      OperandShape(OperandKind.CooperativeMatrixOperands, "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m87: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionsNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One)
    )
  )
  lazy val m88: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "groupCountX", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "groupCountY", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "groupCountZ", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "vertexCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveCount", OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata(
    "Non-Uniform",
    Some("None"),
    Vec("GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m91: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("MeshShadingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "indexOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packedIndices", OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DisplacementMicromapNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "barycentric", OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DisplacementMicromapNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "instanceId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "geometryIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "primitiveIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "barycentric", OperandQuantifier.One)
    )
  )
  lazy val m94: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "offset", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m95: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeVectorNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "offset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m96: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttributes", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.Optional)
    )
  )
  lazy val m97: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m101: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTRecordIndex", OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m103: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m104: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m105: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m106: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hint", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "bits", OperandQuantifier.Optional)
    )
  )
  lazy val m108: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accelerationStructure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "currentTime", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One))
  )
  lazy val m111: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT", "RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObjectAttribute", OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m123: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m124: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m125: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m126: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV", "RayTracingKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hit", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitKind", OperandQuantifier.One)
    )
  )
  lazy val m127: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingNV"), Vec())
  lazy val m128: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingNV"), Vec())
  lazy val m129: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadId", OperandQuantifier.One)
    )
  )
  lazy val m130: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "time", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payloadId", OperandQuantifier.One)
    )
  )
  lazy val m131: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingMotionBlurNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sBTStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "missIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTmax", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "time", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m132: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryPositionFetchKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m133: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("RayTracingNV", "RayTracingKHR", "RayQueryKHR", "DisplacementMicromapNV"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m134: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "sBTIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "callableDataId", OperandQuantifier.One)
    )
  )
  lazy val m135: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingClusterAccelerationStructureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m136: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingClusterAccelerationStructureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m137: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m138: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("ShaderInvocationReorderEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "componentType", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rows", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "columns", OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "columnMajor", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "columnMajor", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "a", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "b", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "c", OperandQuantifier.One)
    )
  )
  lazy val m147: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("CooperativeMatrixNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dataType", OperandQuantifier.One)
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
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One),
      OperandShape(OperandKind.CooperativeMatrixReduce, "reduce", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "combineFunc", OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixTensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.One),
      OperandShape(OperandKind.TensorAddressingOperands, "tensorAddressingOperands", OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixTensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.One),
      OperandShape(OperandKind.TensorAddressingOperands, "tensorAddressingOperands", OperandQuantifier.One)
    )
  )
  lazy val m153: Metadata = Metadata(
    "Function",
    Some("None"),
    Vec("CooperativeMatrixPerElementOperationsNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "func", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m154: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dim", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clampMode", OperandQuantifier.One)
    )
  )
  lazy val m155: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dim", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hasDimensions", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "p", OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m157: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dim", OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.Repeated)
    )
  )
  lazy val m159: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m160: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m162: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorView", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dim", OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorView", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata("Control-Flow", Some("1.6"), Vec("DemoteToHelperInvocation"), Vec())
  lazy val m165: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DemoteToHelperInvocation"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorView", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clipRowOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clipRowSpan", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clipColOffset", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clipColSpan", OperandQuantifier.One)
    )
  )
  lazy val m167: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("TensorAddressingNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensorLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockSize", OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("CooperativeMatrixConversionsNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrix", OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m170: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m175: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("BindlessTextureNV"),
    Vec(OperandShape(OperandKind.LiteralInteger, "bitWidth", OperandQuantifier.One))
  )
  lazy val m176: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("RawAccessChainsNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "byteStride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "byteOffset", OperandQuantifier.One),
      OperandShape(OperandKind.RawAccessChainOperands, "rawAccessChainOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m177: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m178: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m182: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m183: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m184: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m185: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m186: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m187: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "hitObject", OperandQuantifier.One)
    )
  )
  lazy val m188: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m189: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingLinearSweptSpheresGeometryNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "intersection", OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "data", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m191: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "current", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "next", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "delta", OperandQuantifier.One)
    )
  )
  lazy val m192: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "previous", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "current", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "delta", OperandQuantifier.One)
    )
  )
  lazy val m193: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupShuffleINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "data", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "ptr", OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "ptr", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "data", OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "data", OperandQuantifier.One)
    )
  )
  lazy val m198: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageMediaBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "width", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "height", OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupImageMediaBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "width", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "height", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "data", OperandQuantifier.One)
    )
  )
}
