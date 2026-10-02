// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata4 {
  lazy val m0: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("ArbitraryPrecisionFixedPointALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "input", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "s", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "i", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "rI", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "q", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "o", OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("ArbitraryPrecisionFixedPointALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "input", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "s", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "i", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "rI", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "q", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "o", OperandQuantifier.One)
    )
  )
  lazy val m2: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("USMStorageClassesALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("USMStorageClassesALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("BlockingPipesALTERA"),
    Vec(
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("BlockingPipesALTERA"),
    Vec(
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("FPGARegALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "input", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
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
  lazy val m10: Metadata = Metadata(
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
  lazy val m11: Metadata = Metadata(
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
  lazy val m12: Metadata = Metadata(
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
  lazy val m13: Metadata = Metadata(
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
  lazy val m14: Metadata = Metadata(
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
  lazy val m15: Metadata = Metadata(
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
  lazy val m16: Metadata = Metadata(
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
  lazy val m17: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
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
  lazy val m19: Metadata = Metadata(
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
  lazy val m20: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
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
  lazy val m23: Metadata = Metadata(
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
  lazy val m24: Metadata = Metadata(
    "Atomic",
    Some("None"),
    Vec("AtomicFloat16AddEXT", "AtomicFloat32AddEXT", "AtomicFloat64AddEXT", "AtomicFloat16VectorNV"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("VectorComputeINTEL"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.AccessQualifier, "accessQualifier", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape(OperandKind.IdRef, "member0TypeMember1Type", OperandQuantifier.Repeated))
  )
  lazy val m27: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape(OperandKind.IdRef, "constituents", OperandQuantifier.Repeated))
  )
  lazy val m28: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape(OperandKind.IdRef, "constituents", OperandQuantifier.Repeated))
  )
  lazy val m29: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape(OperandKind.IdRef, "constituents", OperandQuantifier.Repeated))
  )
  lazy val m30: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BFloat16ConversionINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BFloat16ConversionINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "bFloat16Value", OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    "Barrier",
    Some("None"),
    Vec("SplitBarrierEXT"),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    "Barrier",
    Some("None"),
    Vec("SplitBarrierEXT"),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("ArithmeticFenceEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "target", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "function", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "pipelined", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "useStallEnableClusters", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "getCapacity", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "asyncCapacity", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape(OperandKind.IdRef, "sequence", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "arguments", OperandQuantifier.Repeated)
    )
  )
  lazy val m37: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sequence", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(OperandShape(OperandKind.IdRef, "sequence", OperandQuantifier.One))
  )
  lazy val m39: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m40: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferPrefetchINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "ptr", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numBytes", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "elementSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcBasePointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryPitch", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockTransformINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "elementSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcBasePointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryPitch", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockTransposeINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "elementSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcBasePointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryPitch", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "elementSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcBasePointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryPitch", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "elementSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "blockCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcPointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dstBasePointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryWidth", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryHeight", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryPitch", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupMatrixMultiplyAccumulateINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "kDim", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixA", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixB", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "matrixC", OperandQuantifier.One),
      OperandShape(
        OperandKind.MatrixMultiplyAccumulateOperands,
        "matrixMultiplyAccumulateOperands",
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m47: Metadata = Metadata(
    "Bit",
    Some("None"),
    Vec("TernaryBitwiseFunctionINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "a", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "b", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "c", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "lUTIndex", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedVariableLengthArrayINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "length", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Extension",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralString, "name", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Mode-Setting",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.ExecutionModel, "executionModel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "entryPoint", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralString, "name", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "interface", OperandQuantifier.Repeated)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Mode-Setting",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.Capability, "capability", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "target", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "features", OperandQuantifier.Repeated)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "category", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "family", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "instructionOpcode", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "architecture", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.Capability, "capabilities", OperandQuantifier.Repeated)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "condition0Operand0Condition1Operand1", OperandQuantifier.Repeated)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("PredicatedIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "defaultValue", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("PredicatedIOINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("TensorFloat32RoundingINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("MaskedGatherScatterINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "ptrVector", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "alignment", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "mask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "fillEmpty", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("MaskedGatherScatterINTEL"),
    Vec(
      OperandShape(OperandKind.IdRef, "inputVector", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "ptrVector", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "alignment", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "mask", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat16AccFloat32VALVE", "DotProductBFloat16AccVALVE"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat16AccFloat16VALVE", "DotProductBFloat16AccVALVE"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat8AccFloat32VALVE"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One)
    )
  )
}
