// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.{Vec}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata4 {
  lazy val m0: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ArbitraryPrecisionFixedPointALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("input"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("s"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("i"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("rI"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("q"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("o"), OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ArbitraryPrecisionFixedPointALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("input"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("s"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("i"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("rI"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("q"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("o"), OperandQuantifier.One)
    )
  )
  lazy val m2: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("USMStorageClassesALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m3: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("USMStorageClassesALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m4: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BlockingPipesALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m5: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BlockingPipesALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FPGARegALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("input"), OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
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
  lazy val m10: Metadata = Metadata(
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
  lazy val m11: Metadata = Metadata(
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
  lazy val m12: Metadata = Metadata(
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
  lazy val m13: Metadata = Metadata(
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
  lazy val m14: Metadata = Metadata(
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
  lazy val m15: Metadata = Metadata(
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
  lazy val m16: Metadata = Metadata(
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
  lazy val m17: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
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
  lazy val m19: Metadata = Metadata(
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
  lazy val m20: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
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
  lazy val m23: Metadata = Metadata(
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
  lazy val m24: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("None")),
    Vec(
      NonEmptyString("AtomicFloat16AddEXT"),
      NonEmptyString("AtomicFloat32AddEXT"),
      NonEmptyString("AtomicFloat64AddEXT"),
      NonEmptyString("AtomicFloat16VectorNV")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("VectorComputeINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("AccessQualifier"), NonEmptyString("accessQualifier"), OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("LongCompositesINTEL")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("member0TypeMember1Type"), OperandQuantifier.Repeated))
  )
  lazy val m27: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("LongCompositesINTEL")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated))
  )
  lazy val m28: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("LongCompositesINTEL")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated))
  )
  lazy val m29: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("LongCompositesINTEL")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("constituents"), OperandQuantifier.Repeated))
  )
  lazy val m30: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BFloat16ConversionINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("floatValue"), OperandQuantifier.One)
    )
  )
  lazy val m31: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BFloat16ConversionINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("bFloat16Value"), OperandQuantifier.One)
    )
  )
  lazy val m32: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SplitBarrierEXT")),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m33: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SplitBarrierEXT")),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m34: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ArithmeticFenceEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TaskSequenceALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("function"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("pipelined"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("useStallEnableClusters"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("getCapacity"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("asyncCapacity"), OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TaskSequenceALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sequence"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("arguments"), OperandQuantifier.Repeated)
    )
  )
  lazy val m37: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TaskSequenceALTERA")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sequence"), OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TaskSequenceALTERA")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("sequence"), OperandQuantifier.One))
  )
  lazy val m39: Metadata = Metadata(
    NonEmptyString("@exclude"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TaskSequenceALTERA")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m40: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBufferPrefetchINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("ptr"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numBytes"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m41: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Subgroup2DBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcBasePointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryPitch"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dstPointer"), OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Subgroup2DBlockTransformINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcBasePointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryPitch"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dstPointer"), OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Subgroup2DBlockTransposeINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcBasePointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryPitch"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dstPointer"), OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Subgroup2DBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcBasePointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryPitch"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("Subgroup2DBlockIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("blockCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcPointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dstBasePointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryWidth"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryHeight"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryPitch"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupMatrixMultiplyAccumulateINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("kDim"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixB"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("matrixC"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("MatrixMultiplyAccumulateOperands"),
        NonEmptyString("matrixMultiplyAccumulateOperands"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m47: Metadata = Metadata(
    NonEmptyString("Bit"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TernaryBitwiseFunctionINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("a"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("b"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("c"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("lUTIndex"), OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedVariableLengthArrayINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("length"), OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    NonEmptyString("Extension"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SpecConditionalINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SpecConditionalINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ExecutionModel"), NonEmptyString("executionModel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("entryPoint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("interface"), OperandQuantifier.Repeated)
    )
  )
  lazy val m51: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SpecConditionalINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Capability"), NonEmptyString("capability"), OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FunctionVariantsINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("features"), OperandQuantifier.Repeated)
    )
  )
  lazy val m53: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FunctionVariantsINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("category"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("family"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("instructionOpcode"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("architecture"), OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FunctionVariantsINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Capability"), NonEmptyString("capabilities"), OperandQuantifier.Repeated)
    )
  )
  lazy val m55: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SpecConditionalINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("IdRef"),
        NonEmptyString("condition0Operand0Condition1Operand1"),
        OperandQuantifier.Repeated
      )
    )
  )
  lazy val m56: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("PredicatedIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("defaultValue"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m57: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("PredicatedIOINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m58: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupUniformArithmeticKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m66: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorFloat32RoundingINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("floatValue"), OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("MaskedGatherScatterINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("ptrVector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("alignment"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("fillEmpty"), OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("MaskedGatherScatterINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("inputVector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("ptrVector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("alignment"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mask"), OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessImagesINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessImagesINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m71: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BindlessImagesINTEL")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m72: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DotProductFloat16AccFloat32VALVE"), NonEmptyString("DotProductBFloat16AccVALVE")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DotProductFloat16AccFloat16VALVE"), NonEmptyString("DotProductBFloat16AccVALVE")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("DotProductFloat8AccFloat32VALVE")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One)
    )
  )
}
