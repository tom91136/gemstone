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
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "input", OperandQuantifier.One),
      OperandShape("LiteralInteger", "s", OperandQuantifier.One),
      OperandShape("LiteralInteger", "i", OperandQuantifier.One),
      OperandShape("LiteralInteger", "rI", OperandQuantifier.One),
      OperandShape("LiteralInteger", "q", OperandQuantifier.One),
      OperandShape("LiteralInteger", "o", OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("ArbitraryPrecisionFixedPointALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "input", OperandQuantifier.One),
      OperandShape("LiteralInteger", "s", OperandQuantifier.One),
      OperandShape("LiteralInteger", "i", OperandQuantifier.One),
      OperandShape("LiteralInteger", "rI", OperandQuantifier.One),
      OperandShape("LiteralInteger", "q", OperandQuantifier.One),
      OperandShape("LiteralInteger", "o", OperandQuantifier.One)
    )
  )
  lazy val m2: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("USMStorageClassesALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("USMStorageClassesALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("BlockingPipesALTERA"),
    Vec(
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("BlockingPipesALTERA"),
    Vec(
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("FPGARegALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "input", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
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
  lazy val m10: Metadata = Metadata(
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
  lazy val m11: Metadata = Metadata(
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
  lazy val m12: Metadata = Metadata(
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
  lazy val m13: Metadata = Metadata(
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
  lazy val m14: Metadata = Metadata(
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
  lazy val m15: Metadata = Metadata(
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
  lazy val m16: Metadata = Metadata(
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
  lazy val m17: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
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
  lazy val m19: Metadata = Metadata(
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
  lazy val m20: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
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
  lazy val m23: Metadata = Metadata(
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
  lazy val m24: Metadata = Metadata(
    "Atomic",
    Some("None"),
    Vec("AtomicFloat16AddEXT", "AtomicFloat32AddEXT", "AtomicFloat64AddEXT", "AtomicFloat16VectorNV"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("VectorComputeINTEL"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("AccessQualifier", "accessQualifier", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape("IdRef", "member0TypeMember1Type", OperandQuantifier.Repeated))
  )
  lazy val m27: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape("IdRef", "constituents", OperandQuantifier.Repeated))
  )
  lazy val m28: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape("IdRef", "constituents", OperandQuantifier.Repeated))
  )
  lazy val m29: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("LongCompositesINTEL"),
    Vec(OperandShape("IdRef", "constituents", OperandQuantifier.Repeated))
  )
  lazy val m30: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BFloat16ConversionINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BFloat16ConversionINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "bFloat16Value", OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    "Barrier",
    Some("None"),
    Vec("SplitBarrierEXT"),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    "Barrier",
    Some("None"),
    Vec("SplitBarrierEXT"),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("ArithmeticFenceEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "target", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "function", OperandQuantifier.One),
      OperandShape("LiteralInteger", "pipelined", OperandQuantifier.One),
      OperandShape("LiteralInteger", "useStallEnableClusters", OperandQuantifier.One),
      OperandShape("LiteralInteger", "getCapacity", OperandQuantifier.One),
      OperandShape("LiteralInteger", "asyncCapacity", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape("IdRef", "sequence", OperandQuantifier.One),
      OperandShape("IdRef", "arguments", OperandQuantifier.Repeated)
    )
  )
  lazy val m37: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sequence", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(OperandShape("IdRef", "sequence", OperandQuantifier.One))
  )
  lazy val m39: Metadata = Metadata(
    "@exclude",
    Some("None"),
    Vec("TaskSequenceALTERA"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m40: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBufferPrefetchINTEL"),
    Vec(
      OperandShape("IdRef", "ptr", OperandQuantifier.One),
      OperandShape("IdRef", "numBytes", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape("IdRef", "elementSize", OperandQuantifier.One),
      OperandShape("IdRef", "blockWidth", OperandQuantifier.One),
      OperandShape("IdRef", "blockHeight", OperandQuantifier.One),
      OperandShape("IdRef", "blockCount", OperandQuantifier.One),
      OperandShape("IdRef", "srcBasePointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryWidth", OperandQuantifier.One),
      OperandShape("IdRef", "memoryHeight", OperandQuantifier.One),
      OperandShape("IdRef", "memoryPitch", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockTransformINTEL"),
    Vec(
      OperandShape("IdRef", "elementSize", OperandQuantifier.One),
      OperandShape("IdRef", "blockWidth", OperandQuantifier.One),
      OperandShape("IdRef", "blockHeight", OperandQuantifier.One),
      OperandShape("IdRef", "blockCount", OperandQuantifier.One),
      OperandShape("IdRef", "srcBasePointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryWidth", OperandQuantifier.One),
      OperandShape("IdRef", "memoryHeight", OperandQuantifier.One),
      OperandShape("IdRef", "memoryPitch", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockTransposeINTEL"),
    Vec(
      OperandShape("IdRef", "elementSize", OperandQuantifier.One),
      OperandShape("IdRef", "blockWidth", OperandQuantifier.One),
      OperandShape("IdRef", "blockHeight", OperandQuantifier.One),
      OperandShape("IdRef", "blockCount", OperandQuantifier.One),
      OperandShape("IdRef", "srcBasePointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryWidth", OperandQuantifier.One),
      OperandShape("IdRef", "memoryHeight", OperandQuantifier.One),
      OperandShape("IdRef", "memoryPitch", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("IdRef", "dstPointer", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape("IdRef", "elementSize", OperandQuantifier.One),
      OperandShape("IdRef", "blockWidth", OperandQuantifier.One),
      OperandShape("IdRef", "blockHeight", OperandQuantifier.One),
      OperandShape("IdRef", "blockCount", OperandQuantifier.One),
      OperandShape("IdRef", "srcBasePointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryWidth", OperandQuantifier.One),
      OperandShape("IdRef", "memoryHeight", OperandQuantifier.One),
      OperandShape("IdRef", "memoryPitch", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("Subgroup2DBlockIOINTEL"),
    Vec(
      OperandShape("IdRef", "elementSize", OperandQuantifier.One),
      OperandShape("IdRef", "blockWidth", OperandQuantifier.One),
      OperandShape("IdRef", "blockHeight", OperandQuantifier.One),
      OperandShape("IdRef", "blockCount", OperandQuantifier.One),
      OperandShape("IdRef", "srcPointer", OperandQuantifier.One),
      OperandShape("IdRef", "dstBasePointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryWidth", OperandQuantifier.One),
      OperandShape("IdRef", "memoryHeight", OperandQuantifier.One),
      OperandShape("IdRef", "memoryPitch", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupMatrixMultiplyAccumulateINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "kDim", OperandQuantifier.One),
      OperandShape("IdRef", "matrixA", OperandQuantifier.One),
      OperandShape("IdRef", "matrixB", OperandQuantifier.One),
      OperandShape("IdRef", "matrixC", OperandQuantifier.One),
      OperandShape("MatrixMultiplyAccumulateOperands", "matrixMultiplyAccumulateOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Bit",
    Some("None"),
    Vec("TernaryBitwiseFunctionINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "a", OperandQuantifier.One),
      OperandShape("IdRef", "b", OperandQuantifier.One),
      OperandShape("IdRef", "c", OperandQuantifier.One),
      OperandShape("IdRef", "lUTIndex", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedVariableLengthArrayINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "elementType", OperandQuantifier.One),
      OperandShape("IdRef", "length", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Extension",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Mode-Setting",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("ExecutionModel", "executionModel", OperandQuantifier.One),
      OperandShape("IdRef", "entryPoint", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One),
      OperandShape("IdRef", "interface", OperandQuantifier.Repeated)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Mode-Setting",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("Capability", "capability", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "target", OperandQuantifier.One),
      OperandShape("LiteralInteger", "features", OperandQuantifier.Repeated)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "category", OperandQuantifier.One),
      OperandShape("LiteralInteger", "family", OperandQuantifier.One),
      OperandShape("LiteralInteger", "instructionOpcode", OperandQuantifier.One),
      OperandShape("LiteralInteger", "architecture", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("FunctionVariantsINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("Capability", "capabilities", OperandQuantifier.Repeated)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("SpecConditionalINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "condition0Operand0Condition1Operand1", OperandQuantifier.Repeated)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("PredicatedIOINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("IdRef", "defaultValue", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("PredicatedIOINTEL"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupUniformArithmeticKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("TensorFloat32RoundingINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "floatValue", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("MaskedGatherScatterINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "ptrVector", OperandQuantifier.One),
      OperandShape("LiteralInteger", "alignment", OperandQuantifier.One),
      OperandShape("IdRef", "mask", OperandQuantifier.One),
      OperandShape("IdRef", "fillEmpty", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("MaskedGatherScatterINTEL"),
    Vec(
      OperandShape("IdRef", "inputVector", OperandQuantifier.One),
      OperandShape("IdRef", "ptrVector", OperandQuantifier.One),
      OperandShape("LiteralInteger", "alignment", OperandQuantifier.One),
      OperandShape("IdRef", "mask", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("BindlessImagesINTEL"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat16AccFloat32VALVE", "DotProductBFloat16AccVALVE"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat16AccFloat16VALVE", "DotProductBFloat16AccVALVE"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("DotProductFloat8AccFloat32VALVE"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One)
    )
  )
}
