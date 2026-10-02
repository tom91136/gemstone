// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata1 {
  lazy val m0: Metadata = Metadata(
    "Derivative",
    Some("1.0"),
    Vec("DerivativeControl"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "p", OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata("Primitive", Some("1.0"), Vec("Geometry"), Vec())
  lazy val m2: Metadata = Metadata("Primitive", Some("1.0"), Vec("Geometry"), Vec())
  lazy val m3: Metadata = Metadata(
    "Primitive",
    Some("1.0"),
    Vec("GeometryStreams"),
    Vec(OperandShape(OperandKind.IdRef, "stream", OperandQuantifier.One))
  )
  lazy val m4: Metadata = Metadata(
    "Primitive",
    Some("1.0"),
    Vec("GeometryStreams"),
    Vec(OperandShape(OperandKind.IdRef, "stream", OperandQuantifier.One))
  )
  lazy val m5: Metadata = Metadata(
    "Barrier",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "Barrier",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "equal", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "unequal", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "comparator", OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "equal", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "unequal", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "comparator", OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m17: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.PairIdRefIdRef, "variableParent", OperandQuantifier.Repeated)
    )
  )
  lazy val m24: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "mergeBlock", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "continueTarget", OperandQuantifier.One),
      OperandShape(OperandKind.LoopControl, "loopControl", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "mergeBlock", OperandQuantifier.One),
      OperandShape(OperandKind.SelectionControl, "selectionControl", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata =
    Metadata("Control-Flow", Some("1.0"), Vec(), Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)))
  lazy val m27: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(OperandShape(OperandKind.IdRef, "targetLabel", OperandQuantifier.One))
  )
  lazy val m28: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "trueLabel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "falseLabel", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "branchWeights", OperandQuantifier.Repeated)
    )
  )
  lazy val m29: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "selector", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "default", OperandQuantifier.One),
      OperandShape(OperandKind.PairLiteralIntegerIdRef, "target", OperandQuantifier.Repeated)
    )
  )
  lazy val m30: Metadata = Metadata("Control-Flow", Some("1.0"), Vec("Shader"), Vec())
  lazy val m31: Metadata = Metadata("Control-Flow", Some("1.0"), Vec(), Vec())
  lazy val m32: Metadata =
    Metadata("Control-Flow", Some("1.0"), Vec(), Vec(OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)))
  lazy val m33: Metadata = Metadata("Control-Flow", Some("1.0"), Vec(), Vec())
  lazy val m34: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "size", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "size", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "destination", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numElements", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numEvents", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "eventsList", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "localId", OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "x", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numPackets", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numPackets", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numPackets", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numPackets", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipe", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "reserveId", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "queue", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numEvents", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "waitEvents", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "retEvent", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "queue", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "flags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "nDRange", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numEvents", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "waitEvents", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "retEvent", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "localSize", OperandQuantifier.Repeated)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "nDRange", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "nDRange", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One))
  )
  lazy val m71: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One))
  )
  lazy val m72: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "status", OperandQuantifier.One)
    )
  )
  lazy val m75: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "profilingInfo", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "globalWorkSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "localWorkSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "globalWorkOffset", OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m79: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.One)
    )
  )
  lazy val m80: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dref", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m81: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dref", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.One)
    )
  )
  lazy val m82: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m83: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.One)
    )
  )
  lazy val m84: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dref", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m85: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dref", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.One)
    )
  )
  lazy val m86: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m87: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "component", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m88: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sampledImage", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dref", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m89: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "residentCode", OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata("Debug", Some("1.0"), Vec(), Vec())
  lazy val m91: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "image", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinate", OperandQuantifier.One),
      OperandShape(OperandKind.ImageOperands, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m94: Metadata = Metadata(
    "Miscellaneous",
    Some("1.1"),
    Vec("Addresses"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One)
    )
  )
  lazy val m95: Metadata = Metadata(
    "Type-Declaration",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m96: Metadata = Metadata(
    "Pipe",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "packetSize", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "packetAlignment", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "capacity", OperandQuantifier.One)
    )
  )
  lazy val m97: Metadata = Metadata(
    "Pipe",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pipeStorage", OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.1"),
    Vec("SubgroupDispatch"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "subgroupCount", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.1"),
    Vec("SubgroupDispatch"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invoke", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "param", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramSize", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    "Type-Declaration",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m101: Metadata = Metadata(
    "Barrier",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "subgroupCount", OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    "Barrier",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(
      OperandShape(OperandKind.IdRef, "namedBarrier", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "memory", OperandQuantifier.One),
      OperandShape(OperandKind.IdMemorySemantics, "semantics", OperandQuantifier.One)
    )
  )
  lazy val m103: Metadata = Metadata(
    "Debug",
    Some("1.1"),
    Vec(),
    Vec(OperandShape(OperandKind.LiteralString, "process", OperandQuantifier.One))
  )
  lazy val m104: Metadata = Metadata(
    "Mode-Setting",
    Some("1.2"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "entryPoint", OperandQuantifier.One),
      OperandShape(OperandKind.ExecutionMode, "mode", OperandQuantifier.One)
    )
  )
  lazy val m105: Metadata = Metadata(
    "Annotation",
    Some("1.2"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdRef, "target", OperandQuantifier.One),
      OperandShape(OperandKind.Decoration, "decoration", OperandQuantifier.One)
    )
  )
  lazy val m106: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniform"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m108: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m111: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffle"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffle"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "mask", OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffleRelative"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "delta", OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffleRelative"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "delta", OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m123: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m124: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m125: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m126: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m127: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m128: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m129: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m130: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m131: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m132: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m133: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m134: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m135: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m136: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m137: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.GroupOperation, "operation", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m138: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformQuad"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformQuad"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "direction", OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    "Composite",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcA", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcB", OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcA", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcB", OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec("Addresses", "VariablePointers", "VariablePointersStorageBuffer"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcA", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcB", OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageColorReadAccessEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "attachment", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageDepthReadAccessEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageStencilReadAccessEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m147: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rank", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "shape", OperandQuantifier.Optional)
    )
  )
  lazy val m148: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensor", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinates", OperandQuantifier.One),
      OperandShape(OperandKind.TensorOperands, "tensorOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m149: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape(OperandKind.IdRef, "tensor", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "coordinates", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.TensorOperands, "tensorOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m150: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "tensor", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dimension", OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "graphConstantID", OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdRef, "graph", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralString, "name", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "interface", OperandQuantifier.Repeated)
    )
  )
  lazy val m153: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One)
    )
  )
  lazy val m154: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "inputIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementIndex", OperandQuantifier.Repeated)
    )
  )
  lazy val m155: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "outputIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementIndex", OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata = Metadata("Graph", Some("None"), Vec("GraphARM"), Vec())
  lazy val m157: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "numInputs", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "inOutTypes", OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BitcastExtractEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "offset", OperandQuantifier.One)
    )
  )
  lazy val m159: Metadata = Metadata("Control-Flow", Some("1.6"), Vec("Shader"), Vec())
  lazy val m160: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.StorageClass, "storageClass", OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.StorageClass, "storageClass", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dataType", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "initializer", OperandQuantifier.Optional)
    )
  )
  lazy val m162: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "baseType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "baseType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m165: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "baseType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "element", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m167: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "baseType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "base", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "element", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "structure", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralInteger, "arrayMember", OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointerType", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numBytes", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rW", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "locality", OperandQuantifier.Optional),
      OperandShape(OperandKind.IdRef, "cacheType", OperandQuantifier.Optional)
    )
  )
  lazy val m170: Metadata = Metadata(
    "Arithmetic",
    Some("None"),
    Vec("FMAKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcA", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcB", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcC", OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pred", OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupNonUniformRotateKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "delta", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m175: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "index", OperandQuantifier.One)
    )
  )
  lazy val m176: Metadata = Metadata(
    "Extension",
    Some("None"),
    Vec(),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "set", OperandQuantifier.One),
      OperandShape(OperandKind.LiteralExtInstInteger, "inst", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m177: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "execution", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "destination", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "elementNumBytes", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "numElements", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "event", OperandQuantifier.One),
      OperandShape(OperandKind.MemoryAccess, "modsA", OperandQuantifier.Optional),
      OperandShape(OperandKind.MemoryAccess, "modsB", OperandQuantifier.Optional)
    )
  )
  lazy val m178: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR"),
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
      OperandShape(OperandKind.IdRef, "payload", OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR"),
    Vec(
      OperandShape(OperandKind.IdRef, "sBTIndex", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "callableData", OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR", "RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingKHR"), Vec())
  lazy val m182: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingKHR"), Vec())
  lazy val m183: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m184: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m185: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m186: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m187: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m188: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector1", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "vector2", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accumulator", OperandQuantifier.One),
      OperandShape(OperandKind.PackedVectorFormat, "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m189: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "componentType", OperandQuantifier.One),
      OperandShape(OperandKind.IdScope, "scope", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rows", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "columns", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "use", OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.Optional),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m191: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape(OperandKind.IdRef, "pointer", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "src", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "memoryLayout", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "stride", OperandQuantifier.Optional),
      OperandShape(OperandKind.MemoryAccess, "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m192: Metadata = Metadata(
    "Arithmetic",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "a", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "b", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "c", OperandQuantifier.One),
      OperandShape(OperandKind.CooperativeMatrixOperands, "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m193: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "dataType", OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape(OperandKind.IdResultType, "resultType", OperandQuantifier.One),
      OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "value", OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape(OperandKind.IdResult, "dest", OperandQuantifier.One))
  )
  lazy val m198: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "accel", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayFlags", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "cullMask", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayOrigin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTMin", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayDirection", OperandQuantifier.One),
      OperandShape(OperandKind.IdRef, "rayTMax", OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape(OperandKind.IdRef, "rayQuery", OperandQuantifier.One))
  )
}
