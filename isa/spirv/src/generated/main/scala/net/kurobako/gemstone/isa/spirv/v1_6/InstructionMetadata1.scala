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
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "p", OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata = Metadata("Primitive", Some("1.0"), Vec("Geometry"), Vec())
  lazy val m2: Metadata = Metadata("Primitive", Some("1.0"), Vec("Geometry"), Vec())
  lazy val m3: Metadata = Metadata(
    "Primitive",
    Some("1.0"),
    Vec("GeometryStreams"),
    Vec(OperandShape("IdRef", "stream", OperandQuantifier.One))
  )
  lazy val m4: Metadata = Metadata(
    "Primitive",
    Some("1.0"),
    Vec("GeometryStreams"),
    Vec(OperandShape("IdRef", "stream", OperandQuantifier.One))
  )
  lazy val m5: Metadata = Metadata(
    "Barrier",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    "Barrier",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "equal", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "unequal", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "comparator", OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "equal", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "unequal", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "comparator", OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m17: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("PairIdRefIdRef", "variableParent", OperandQuantifier.Repeated)
    )
  )
  lazy val m24: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "mergeBlock", OperandQuantifier.One),
      OperandShape("IdRef", "continueTarget", OperandQuantifier.One),
      OperandShape("LoopControl", "loopControl", OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "mergeBlock", OperandQuantifier.One),
      OperandShape("SelectionControl", "selectionControl", OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata =
    Metadata("Control-Flow", Some("1.0"), Vec(), Vec(OperandShape("IdResult", "dest", OperandQuantifier.One)))
  lazy val m27: Metadata =
    Metadata("Control-Flow", Some("1.0"), Vec(), Vec(OperandShape("IdRef", "targetLabel", OperandQuantifier.One)))
  lazy val m28: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "pred", OperandQuantifier.One),
      OperandShape("IdRef", "trueLabel", OperandQuantifier.One),
      OperandShape("IdRef", "falseLabel", OperandQuantifier.One),
      OperandShape("LiteralInteger", "branchWeights", OperandQuantifier.Repeated)
    )
  )
  lazy val m29: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec(),
    Vec(
      OperandShape("IdRef", "selector", OperandQuantifier.One),
      OperandShape("IdRef", "default", OperandQuantifier.One),
      OperandShape("PairLiteralIntegerIdRef", "target", OperandQuantifier.Repeated)
    )
  )
  lazy val m30: Metadata = Metadata("Control-Flow", Some("1.0"), Vec("Shader"), Vec())
  lazy val m31: Metadata = Metadata("Control-Flow", Some("1.0"), Vec(), Vec())
  lazy val m32: Metadata =
    Metadata("Control-Flow", Some("1.0"), Vec(), Vec(OperandShape("IdRef", "value", OperandQuantifier.One)))
  lazy val m33: Metadata = Metadata("Control-Flow", Some("1.0"), Vec(), Vec())
  lazy val m34: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("LiteralInteger", "size", OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    "Control-Flow",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("LiteralInteger", "size", OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "destination", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "numElements", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.One),
      OperandShape("IdRef", "event", OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "numEvents", OperandQuantifier.One),
      OperandShape("IdRef", "eventsList", OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "localId", OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    "Group",
    Some("1.0"),
    Vec("Groups"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "x", OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "numPackets", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "numPackets", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "numPackets", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "numPackets", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    "Pipe",
    Some("1.0"),
    Vec("Pipes"),
    Vec(
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pipe", OperandQuantifier.One),
      OperandShape("IdRef", "reserveId", OperandQuantifier.One),
      OperandShape("IdRef", "packetSize", OperandQuantifier.One),
      OperandShape("IdRef", "packetAlignment", OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "queue", OperandQuantifier.One),
      OperandShape("IdRef", "numEvents", OperandQuantifier.One),
      OperandShape("IdRef", "waitEvents", OperandQuantifier.One),
      OperandShape("IdRef", "retEvent", OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "queue", OperandQuantifier.One),
      OperandShape("IdRef", "flags", OperandQuantifier.One),
      OperandShape("IdRef", "nDRange", OperandQuantifier.One),
      OperandShape("IdRef", "numEvents", OperandQuantifier.One),
      OperandShape("IdRef", "waitEvents", OperandQuantifier.One),
      OperandShape("IdRef", "retEvent", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One),
      OperandShape("IdRef", "localSize", OperandQuantifier.Repeated)
    )
  )
  lazy val m66: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "nDRange", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "nDRange", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape("IdRef", "event", OperandQuantifier.One))
  )
  lazy val m71: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape("IdRef", "event", OperandQuantifier.One))
  )
  lazy val m72: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "event", OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(OperandShape("IdRef", "event", OperandQuantifier.One), OperandShape("IdRef", "status", OperandQuantifier.One))
  )
  lazy val m75: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdRef", "event", OperandQuantifier.One),
      OperandShape("IdRef", "profilingInfo", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.0"),
    Vec("DeviceEnqueue"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "globalWorkSize", OperandQuantifier.One),
      OperandShape("IdRef", "localWorkSize", OperandQuantifier.One),
      OperandShape("IdRef", "globalWorkOffset", OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Some("None"),
    Vec("SparseResidency"),
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
    Some("None"),
    Vec("SparseResidency"),
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
    Some("None"),
    Vec("SparseResidency"),
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
    Some("None"),
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
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
    Vec("SparseResidency"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "residentCode", OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata("Debug", Some("1.0"), Vec(), Vec())
  lazy val m91: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    "Atomic",
    Some("1.0"),
    Vec("Kernel"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    "Image",
    Some("1.0"),
    Vec("SparseResidency"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "image", OperandQuantifier.One),
      OperandShape("IdRef", "coordinate", OperandQuantifier.One),
      OperandShape("ImageOperands", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m94: Metadata = Metadata(
    "Miscellaneous",
    Some("1.1"),
    Vec("Addresses"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One)
    )
  )
  lazy val m95: Metadata = Metadata(
    "Type-Declaration",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m96: Metadata = Metadata(
    "Pipe",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "packetSize", OperandQuantifier.One),
      OperandShape("LiteralInteger", "packetAlignment", OperandQuantifier.One),
      OperandShape("LiteralInteger", "capacity", OperandQuantifier.One)
    )
  )
  lazy val m97: Metadata = Metadata(
    "Pipe",
    Some("1.1"),
    Vec("PipeStorage"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pipeStorage", OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.1"),
    Vec("SubgroupDispatch"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "subgroupCount", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    "Device-Side_Enqueue",
    Some("1.1"),
    Vec("SubgroupDispatch"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "invoke", OperandQuantifier.One),
      OperandShape("IdRef", "param", OperandQuantifier.One),
      OperandShape("IdRef", "paramSize", OperandQuantifier.One),
      OperandShape("IdRef", "paramAlign", OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    "Type-Declaration",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m101: Metadata = Metadata(
    "Barrier",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "subgroupCount", OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    "Barrier",
    Some("1.1"),
    Vec("NamedBarrier"),
    Vec(
      OperandShape("IdRef", "namedBarrier", OperandQuantifier.One),
      OperandShape("IdScope", "memory", OperandQuantifier.One),
      OperandShape("IdMemorySemantics", "semantics", OperandQuantifier.One)
    )
  )
  lazy val m103: Metadata =
    Metadata("Debug", Some("1.1"), Vec(), Vec(OperandShape("LiteralString", "process", OperandQuantifier.One)))
  lazy val m104: Metadata = Metadata(
    "Mode-Setting",
    Some("1.2"),
    Vec(),
    Vec(
      OperandShape("IdRef", "entryPoint", OperandQuantifier.One),
      OperandShape("ExecutionMode", "mode", OperandQuantifier.One)
    )
  )
  lazy val m105: Metadata = Metadata(
    "Annotation",
    Some("1.2"),
    Vec(),
    Vec(
      OperandShape("IdRef", "target", OperandQuantifier.One),
      OperandShape("Decoration", "decoration", OperandQuantifier.One)
    )
  )
  lazy val m106: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniform"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m108: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformVote"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m111: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformBallot"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffle"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "invocationId", OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffle"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "mask", OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffleRelative"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "delta", OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformShuffleRelative"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "delta", OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m123: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m124: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m125: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m126: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m127: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m128: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m129: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m130: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m131: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m132: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m133: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m134: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m135: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m136: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m137: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformArithmetic", "GroupNonUniformClustered", "GroupNonUniformPartitionedEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("GroupOperation", "operation", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m138: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformQuad"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    "Non-Uniform",
    Some("1.3"),
    Vec("GroupNonUniformQuad"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "direction", OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    "Composite",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    "Memory",
    Some("1.4"),
    Vec("Addresses", "VariablePointers", "VariablePointersStorageBuffer"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageColorReadAccessEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "attachment", OperandQuantifier.One),
      OperandShape("IdRef", "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageDepthReadAccessEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    "Image",
    Some("None"),
    Vec("TileImageStencilReadAccessEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "sample", OperandQuantifier.Optional)
    )
  )
  lazy val m147: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "elementType", OperandQuantifier.One),
      OperandShape("IdRef", "rank", OperandQuantifier.Optional),
      OperandShape("IdRef", "shape", OperandQuantifier.Optional)
    )
  )
  lazy val m148: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensor", OperandQuantifier.One),
      OperandShape("IdRef", "coordinates", OperandQuantifier.One),
      OperandShape("TensorOperands", "tensorOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m149: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape("IdRef", "tensor", OperandQuantifier.One),
      OperandShape("IdRef", "coordinates", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("TensorOperands", "tensorOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m150: Metadata = Metadata(
    "Tensor",
    Some("None"),
    Vec("TensorsARM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "tensor", OperandQuantifier.One),
      OperandShape("IdRef", "dimension", OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "graphConstantID", OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdRef", "graph", OperandQuantifier.One),
      OperandShape("LiteralString", "name", OperandQuantifier.One),
      OperandShape("IdRef", "interface", OperandQuantifier.Repeated)
    )
  )
  lazy val m153: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One)
    )
  )
  lazy val m154: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "inputIndex", OperandQuantifier.One),
      OperandShape("IdRef", "elementIndex", OperandQuantifier.Repeated)
    )
  )
  lazy val m155: Metadata = Metadata(
    "Graph",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "outputIndex", OperandQuantifier.One),
      OperandShape("IdRef", "elementIndex", OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata = Metadata("Graph", Some("None"), Vec("GraphARM"), Vec())
  lazy val m157: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("GraphARM"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("LiteralInteger", "numInputs", OperandQuantifier.One),
      OperandShape("IdRef", "inOutTypes", OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    "Conversion",
    Some("None"),
    Vec("BitcastExtractEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "offset", OperandQuantifier.One)
    )
  )
  lazy val m159: Metadata = Metadata("Control-Flow", Some("1.6"), Vec("Shader"), Vec())
  lazy val m160: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("StorageClass", "storageClass", OperandQuantifier.One),
      OperandShape("IdRef", "dataType", OperandQuantifier.Optional),
      OperandShape("IdRef", "initializer", OperandQuantifier.Optional)
    )
  )
  lazy val m162: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "baseType", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "baseType", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m165: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "baseType", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "element", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m167: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "baseType", OperandQuantifier.One),
      OperandShape("IdRef", "base", OperandQuantifier.One),
      OperandShape("IdRef", "element", OperandQuantifier.One),
      OperandShape("IdRef", "indexes", OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "structure", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("LiteralInteger", "arrayMember", OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdRef", "pointerType", OperandQuantifier.One),
      OperandShape("IdRef", "numBytes", OperandQuantifier.One),
      OperandShape("IdRef", "rW", OperandQuantifier.Optional),
      OperandShape("IdRef", "locality", OperandQuantifier.Optional),
      OperandShape("IdRef", "cacheType", OperandQuantifier.Optional)
    )
  )
  lazy val m170: Metadata = Metadata(
    "Arithmetic",
    Some("None"),
    Vec("FMAKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "srcA", OperandQuantifier.One),
      OperandShape("IdRef", "srcB", OperandQuantifier.One),
      OperandShape("IdRef", "srcC", OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupVoteKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pred", OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("GroupNonUniformRotateKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdScope", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "delta", OperandQuantifier.One),
      OperandShape("IdRef", "clusterSize", OperandQuantifier.Optional)
    )
  )
  lazy val m175: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("SubgroupBallotKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One),
      OperandShape("IdRef", "index", OperandQuantifier.One)
    )
  )
  lazy val m176: Metadata = Metadata(
    "Extension",
    Some("None"),
    Vec(),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "set", OperandQuantifier.One),
      OperandShape("LiteralExtInstInteger", "inst", OperandQuantifier.One),
      OperandShape("IdRef", "srcs", OperandQuantifier.Repeated)
    )
  )
  lazy val m177: Metadata = Metadata(
    "Group",
    Some("None"),
    Vec("UntypedPointersKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "execution", OperandQuantifier.One),
      OperandShape("IdRef", "destination", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "elementNumBytes", OperandQuantifier.One),
      OperandShape("IdRef", "numElements", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.One),
      OperandShape("IdRef", "event", OperandQuantifier.One),
      OperandShape("MemoryAccess", "modsA", OperandQuantifier.Optional),
      OperandShape("MemoryAccess", "modsB", OperandQuantifier.Optional)
    )
  )
  lazy val m178: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR"),
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
      OperandShape("IdRef", "payload", OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR"),
    Vec(
      OperandShape("IdRef", "sBTIndex", OperandQuantifier.One),
      OperandShape("IdRef", "callableData", OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayTracingKHR", "RayQueryKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "accel", OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingKHR"), Vec())
  lazy val m182: Metadata = Metadata("Reserved", Some("None"), Vec("RayTracingKHR"), Vec())
  lazy val m183: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m184: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m185: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m186: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m187: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m188: Metadata = Metadata(
    "Arithmetic",
    Some("1.6"),
    Vec("DotProduct"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "vector1", OperandQuantifier.One),
      OperandShape("IdRef", "vector2", OperandQuantifier.One),
      OperandShape("IdRef", "accumulator", OperandQuantifier.One),
      OperandShape("PackedVectorFormat", "packedVectorFormat", OperandQuantifier.Optional)
    )
  )
  lazy val m189: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "componentType", OperandQuantifier.One),
      OperandShape("IdScope", "scope", OperandQuantifier.One),
      OperandShape("IdRef", "rows", OperandQuantifier.One),
      OperandShape("IdRef", "columns", OperandQuantifier.One),
      OperandShape("IdRef", "use", OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "memoryLayout", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.Optional),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m191: Metadata = Metadata(
    "Memory",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape("IdRef", "pointer", OperandQuantifier.One),
      OperandShape("IdRef", "src", OperandQuantifier.One),
      OperandShape("IdRef", "memoryLayout", OperandQuantifier.One),
      OperandShape("IdRef", "stride", OperandQuantifier.Optional),
      OperandShape("MemoryAccess", "mods", OperandQuantifier.Optional)
    )
  )
  lazy val m192: Metadata = Metadata(
    "Arithmetic",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "a", OperandQuantifier.One),
      OperandShape("IdRef", "b", OperandQuantifier.One),
      OperandShape("IdRef", "c", OperandQuantifier.One),
      OperandShape("CooperativeMatrixOperands", "cooperativeMatrixOperands", OperandQuantifier.Optional)
    )
  )
  lazy val m193: Metadata = Metadata(
    "Miscellaneous",
    Some("None"),
    Vec("CooperativeMatrixKHR"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "dataType", OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    "Constant-Creation",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    "Composite",
    Some("None"),
    Vec("ReplicatedCompositesEXT"),
    Vec(
      OperandShape("IdResultType", "resultType", OperandQuantifier.One),
      OperandShape("IdResult", "dest", OperandQuantifier.One),
      OperandShape("IdRef", "value", OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    "Type-Declaration",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape("IdResult", "dest", OperandQuantifier.One))
  )
  lazy val m198: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(
      OperandShape("IdRef", "rayQuery", OperandQuantifier.One),
      OperandShape("IdRef", "accel", OperandQuantifier.One),
      OperandShape("IdRef", "rayFlags", OperandQuantifier.One),
      OperandShape("IdRef", "cullMask", OperandQuantifier.One),
      OperandShape("IdRef", "rayOrigin", OperandQuantifier.One),
      OperandShape("IdRef", "rayTMin", OperandQuantifier.One),
      OperandShape("IdRef", "rayDirection", OperandQuantifier.One),
      OperandShape("IdRef", "rayTMax", OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    "Reserved",
    Some("None"),
    Vec("RayQueryKHR"),
    Vec(OperandShape("IdRef", "rayQuery", OperandQuantifier.One))
  )
}
