// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv.v1_6

import net.kurobako.gemstone.Constraint.NonEmptyString

import net.kurobako.gemstone.{Vec}

@javax.annotation.processing.Generated(Array("sbt codegen"))
object InstructionMetadata1 {
  lazy val m0: Metadata = Metadata(
    NonEmptyString("Derivative"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DerivativeControl")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("p"), OperandQuantifier.One)
    )
  )
  lazy val m1: Metadata =
    Metadata(NonEmptyString("Primitive"), Some(NonEmptyString("1.0")), Vec(NonEmptyString("Geometry")), Vec())
  lazy val m2: Metadata =
    Metadata(NonEmptyString("Primitive"), Some(NonEmptyString("1.0")), Vec(NonEmptyString("Geometry")), Vec())
  lazy val m3: Metadata = Metadata(
    NonEmptyString("Primitive"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("GeometryStreams")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("stream"), OperandQuantifier.One))
  )
  lazy val m4: Metadata = Metadata(
    NonEmptyString("Primitive"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("GeometryStreams")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("stream"), OperandQuantifier.One))
  )
  lazy val m5: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m6: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m7: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m8: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m9: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m10: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("equal"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("unequal"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("comparator"), OperandQuantifier.One)
    )
  )
  lazy val m11: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("equal"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("unequal"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("comparator"), OperandQuantifier.One)
    )
  )
  lazy val m12: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m13: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m14: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m15: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m16: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m17: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m18: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m19: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m20: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m21: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m22: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m23: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("PairIdRefIdRef"), NonEmptyString("variableParent"), OperandQuantifier.Repeated)
    )
  )
  lazy val m24: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mergeBlock"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("continueTarget"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LoopControl"), NonEmptyString("loopControl"), OperandQuantifier.One)
    )
  )
  lazy val m25: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mergeBlock"), OperandQuantifier.One),
      OperandShape(NonEmptyString("SelectionControl"), NonEmptyString("selectionControl"), OperandQuantifier.One)
    )
  )
  lazy val m26: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m27: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("targetLabel"), OperandQuantifier.One))
  )
  lazy val m28: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("trueLabel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("falseLabel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("branchWeights"), OperandQuantifier.Repeated)
    )
  )
  lazy val m29: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("selector"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("default"), OperandQuantifier.One),
      OperandShape(NonEmptyString("PairLiteralIntegerIdRef"), NonEmptyString("target"), OperandQuantifier.Repeated)
    )
  )
  lazy val m30: Metadata =
    Metadata(NonEmptyString("Control-Flow"), Some(NonEmptyString("1.0")), Vec(NonEmptyString("Shader")), Vec())
  lazy val m31: Metadata = Metadata(NonEmptyString("Control-Flow"), Some(NonEmptyString("1.0")), Vec(), Vec())
  lazy val m32: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One))
  )
  lazy val m33: Metadata = Metadata(NonEmptyString("Control-Flow"), Some(NonEmptyString("1.0")), Vec(), Vec())
  lazy val m34: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("size"), OperandQuantifier.One)
    )
  )
  lazy val m35: Metadata = Metadata(
    NonEmptyString("Control-Flow"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("size"), OperandQuantifier.One)
    )
  )
  lazy val m36: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("destination"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numElements"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One)
    )
  )
  lazy val m37: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numEvents"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("eventsList"), OperandQuantifier.One)
    )
  )
  lazy val m38: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m39: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m40: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("localId"), OperandQuantifier.One)
    )
  )
  lazy val m41: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m42: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m43: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m44: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m45: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m46: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m47: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m48: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Groups")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("x"), OperandQuantifier.One)
    )
  )
  lazy val m49: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m50: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m51: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m52: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m53: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numPackets"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m54: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numPackets"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m55: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m56: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m57: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One)
    )
  )
  lazy val m58: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m59: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m60: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numPackets"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m61: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numPackets"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m62: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m63: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Pipes")),
    Vec(
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipe"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("reserveId"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("packetAlignment"), OperandQuantifier.One)
    )
  )
  lazy val m64: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("queue"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numEvents"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("waitEvents"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("retEvent"), OperandQuantifier.One)
    )
  )
  lazy val m65: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("queue"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("flags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("nDRange"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numEvents"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("waitEvents"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("retEvent"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("localSize"), OperandQuantifier.Repeated)
    )
  )
  lazy val m66: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("nDRange"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m67: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("nDRange"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m68: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m69: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m70: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One))
  )
  lazy val m71: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One))
  )
  lazy val m72: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m73: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One)
    )
  )
  lazy val m74: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("status"), OperandQuantifier.One)
    )
  )
  lazy val m75: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("profilingInfo"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m76: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m77: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("DeviceEnqueue")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("globalWorkSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("localWorkSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("globalWorkOffset"), OperandQuantifier.One)
    )
  )
  lazy val m78: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SparseResidency")),
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
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SparseResidency")),
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
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SparseResidency")),
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
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
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
    Vec(NonEmptyString("SparseResidency")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("residentCode"), OperandQuantifier.One)
    )
  )
  lazy val m90: Metadata = Metadata(NonEmptyString("Debug"), Some(NonEmptyString("1.0")), Vec(), Vec())
  lazy val m91: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m92: Metadata = Metadata(
    NonEmptyString("Atomic"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("Kernel")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m93: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("1.0")),
    Vec(NonEmptyString("SparseResidency")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("image"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinate"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ImageOperands"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m94: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("Addresses")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One)
    )
  )
  lazy val m95: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("PipeStorage")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m96: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("PipeStorage")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("packetSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("packetAlignment"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("capacity"), OperandQuantifier.One)
    )
  )
  lazy val m97: Metadata = Metadata(
    NonEmptyString("Pipe"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("PipeStorage")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pipeStorage"), OperandQuantifier.One)
    )
  )
  lazy val m98: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("SubgroupDispatch")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("subgroupCount"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m99: Metadata = Metadata(
    NonEmptyString("Device-Side_Enqueue"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("SubgroupDispatch")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invoke"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("param"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramSize"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("paramAlign"), OperandQuantifier.One)
    )
  )
  lazy val m100: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("NamedBarrier")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m101: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("NamedBarrier")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("subgroupCount"), OperandQuantifier.One)
    )
  )
  lazy val m102: Metadata = Metadata(
    NonEmptyString("Barrier"),
    Some(NonEmptyString("1.1")),
    Vec(NonEmptyString("NamedBarrier")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("namedBarrier"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("memory"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdMemorySemantics"), NonEmptyString("semantics"), OperandQuantifier.One)
    )
  )
  lazy val m103: Metadata = Metadata(
    NonEmptyString("Debug"),
    Some(NonEmptyString("1.1")),
    Vec(),
    Vec(OperandShape(NonEmptyString("LiteralString"), NonEmptyString("process"), OperandQuantifier.One))
  )
  lazy val m104: Metadata = Metadata(
    NonEmptyString("Mode-Setting"),
    Some(NonEmptyString("1.2")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("entryPoint"), OperandQuantifier.One),
      OperandShape(NonEmptyString("ExecutionMode"), NonEmptyString("mode"), OperandQuantifier.One)
    )
  )
  lazy val m105: Metadata = Metadata(
    NonEmptyString("Annotation"),
    Some(NonEmptyString("1.2")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("target"), OperandQuantifier.One),
      OperandShape(NonEmptyString("Decoration"), NonEmptyString("decoration"), OperandQuantifier.One)
    )
  )
  lazy val m106: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniform")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One)
    )
  )
  lazy val m107: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformVote")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m108: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformVote")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m109: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformVote")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m110: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invocationId"), OperandQuantifier.One)
    )
  )
  lazy val m111: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m112: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m113: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m114: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m115: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m116: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m117: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformBallot")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m118: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformShuffle")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("invocationId"), OperandQuantifier.One)
    )
  )
  lazy val m119: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformShuffle")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("mask"), OperandQuantifier.One)
    )
  )
  lazy val m120: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformShuffleRelative")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("delta"), OperandQuantifier.One)
    )
  )
  lazy val m121: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformShuffleRelative")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("delta"), OperandQuantifier.One)
    )
  )
  lazy val m122: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m123: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m124: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m125: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m126: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m127: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m128: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m129: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m130: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m131: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m132: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m133: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m134: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m135: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m136: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m137: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(
      NonEmptyString("GroupNonUniformArithmetic"),
      NonEmptyString("GroupNonUniformClustered"),
      NonEmptyString("GroupNonUniformPartitionedEXT")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("GroupOperation"), NonEmptyString("operation"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m138: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformQuad")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m139: Metadata = Metadata(
    NonEmptyString("Non-Uniform"),
    Some(NonEmptyString("1.3")),
    Vec(NonEmptyString("GroupNonUniformQuad")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("direction"), OperandQuantifier.One)
    )
  )
  lazy val m140: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("1.4")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One)
    )
  )
  lazy val m141: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.4")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m142: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.4")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m143: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("1.4")),
    Vec(
      NonEmptyString("Addresses"),
      NonEmptyString("VariablePointers"),
      NonEmptyString("VariablePointersStorageBuffer")
    ),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One)
    )
  )
  lazy val m144: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TileImageColorReadAccessEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("attachment"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sample"), OperandQuantifier.Optional)
    )
  )
  lazy val m145: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TileImageDepthReadAccessEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sample"), OperandQuantifier.Optional)
    )
  )
  lazy val m146: Metadata = Metadata(
    NonEmptyString("Image"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TileImageStencilReadAccessEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sample"), OperandQuantifier.Optional)
    )
  )
  lazy val m147: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorsARM")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rank"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("shape"), OperandQuantifier.Optional)
    )
  )
  lazy val m148: Metadata = Metadata(
    NonEmptyString("Tensor"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorsARM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensor"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("TensorOperands"), NonEmptyString("tensorOperands"), OperandQuantifier.Optional)
    )
  )
  lazy val m149: Metadata = Metadata(
    NonEmptyString("Tensor"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorsARM")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensor"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("coordinates"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("TensorOperands"), NonEmptyString("tensorOperands"), OperandQuantifier.Optional)
    )
  )
  lazy val m150: Metadata = Metadata(
    NonEmptyString("Tensor"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("TensorsARM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("tensor"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dimension"), OperandQuantifier.One)
    )
  )
  lazy val m151: Metadata = Metadata(
    NonEmptyString("Graph"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("graphConstantID"), OperandQuantifier.One)
    )
  )
  lazy val m152: Metadata = Metadata(
    NonEmptyString("Graph"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("graph"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralString"), NonEmptyString("name"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("interface"), OperandQuantifier.Repeated)
    )
  )
  lazy val m153: Metadata = Metadata(
    NonEmptyString("Graph"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One)
    )
  )
  lazy val m154: Metadata = Metadata(
    NonEmptyString("Graph"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("inputIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementIndex"), OperandQuantifier.Repeated)
    )
  )
  lazy val m155: Metadata = Metadata(
    NonEmptyString("Graph"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("outputIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementIndex"), OperandQuantifier.Repeated)
    )
  )
  lazy val m156: Metadata =
    Metadata(NonEmptyString("Graph"), Some(NonEmptyString("None")), Vec(NonEmptyString("GraphARM")), Vec())
  lazy val m157: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GraphARM")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("numInputs"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("inOutTypes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m158: Metadata = Metadata(
    NonEmptyString("Conversion"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("BitcastExtractEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("offset"), OperandQuantifier.One)
    )
  )
  lazy val m159: Metadata =
    Metadata(NonEmptyString("Control-Flow"), Some(NonEmptyString("1.6")), Vec(NonEmptyString("Shader")), Vec())
  lazy val m160: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One)
    )
  )
  lazy val m161: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("StorageClass"), NonEmptyString("storageClass"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("initializer"), OperandQuantifier.Optional)
    )
  )
  lazy val m162: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("baseType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m163: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("baseType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m164: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBallotKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m165: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBallotKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m166: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("baseType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("element"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m167: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("baseType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("base"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("element"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("indexes"), OperandQuantifier.Repeated)
    )
  )
  lazy val m168: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("structure"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralInteger"), NonEmptyString("arrayMember"), OperandQuantifier.One)
    )
  )
  lazy val m169: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointerType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numBytes"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rW"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("locality"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cacheType"), OperandQuantifier.Optional)
    )
  )
  lazy val m170: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("FMAKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcA"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcB"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcC"), OperandQuantifier.One)
    )
  )
  lazy val m171: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupVoteKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m172: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupVoteKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m173: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupVoteKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pred"), OperandQuantifier.One)
    )
  )
  lazy val m174: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("GroupNonUniformRotateKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("delta"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("clusterSize"), OperandQuantifier.Optional)
    )
  )
  lazy val m175: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("SubgroupBallotKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("index"), OperandQuantifier.One)
    )
  )
  lazy val m176: Metadata = Metadata(
    NonEmptyString("Extension"),
    Some(NonEmptyString("None")),
    Vec(),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("set"), OperandQuantifier.One),
      OperandShape(NonEmptyString("LiteralExtInstInteger"), NonEmptyString("inst"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("srcs"), OperandQuantifier.Repeated)
    )
  )
  lazy val m177: Metadata = Metadata(
    NonEmptyString("Group"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("UntypedPointersKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("execution"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("destination"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("elementNumBytes"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("numElements"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("event"), OperandQuantifier.One),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsA"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("modsB"), OperandQuantifier.Optional)
    )
  )
  lazy val m178: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingKHR")),
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
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("payload"), OperandQuantifier.One)
    )
  )
  lazy val m179: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("sBTIndex"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("callableData"), OperandQuantifier.One)
    )
  )
  lazy val m180: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayTracingKHR"), NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One)
    )
  )
  lazy val m181: Metadata =
    Metadata(NonEmptyString("Reserved"), Some(NonEmptyString("None")), Vec(NonEmptyString("RayTracingKHR")), Vec())
  lazy val m182: Metadata =
    Metadata(NonEmptyString("Reserved"), Some(NonEmptyString("None")), Vec(NonEmptyString("RayTracingKHR")), Vec())
  lazy val m183: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m184: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m185: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m186: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m187: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m188: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("1.6")),
    Vec(NonEmptyString("DotProduct")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector1"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("vector2"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accumulator"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("PackedVectorFormat"),
        NonEmptyString("packedVectorFormat"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m189: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("componentType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdScope"), NonEmptyString("scope"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rows"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("columns"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("use"), OperandQuantifier.One)
    )
  )
  lazy val m190: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m191: Metadata = Metadata(
    NonEmptyString("Memory"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("pointer"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("src"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("memoryLayout"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("stride"), OperandQuantifier.Optional),
      OperandShape(NonEmptyString("MemoryAccess"), NonEmptyString("mods"), OperandQuantifier.Optional)
    )
  )
  lazy val m192: Metadata = Metadata(
    NonEmptyString("Arithmetic"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("a"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("b"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("c"), OperandQuantifier.One),
      OperandShape(
        NonEmptyString("CooperativeMatrixOperands"),
        NonEmptyString("cooperativeMatrixOperands"),
        OperandQuantifier.Optional
      )
    )
  )
  lazy val m193: Metadata = Metadata(
    NonEmptyString("Miscellaneous"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("CooperativeMatrixKHR")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("dataType"), OperandQuantifier.One)
    )
  )
  lazy val m194: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ReplicatedCompositesEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m195: Metadata = Metadata(
    NonEmptyString("Constant-Creation"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ReplicatedCompositesEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m196: Metadata = Metadata(
    NonEmptyString("Composite"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("ReplicatedCompositesEXT")),
    Vec(
      OperandShape(NonEmptyString("IdResultType"), NonEmptyString("resultType"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("value"), OperandQuantifier.One)
    )
  )
  lazy val m197: Metadata = Metadata(
    NonEmptyString("Type-Declaration"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(OperandShape(NonEmptyString("IdResult"), NonEmptyString("dest"), OperandQuantifier.One))
  )
  lazy val m198: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("accel"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayFlags"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("cullMask"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayOrigin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTMin"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayDirection"), OperandQuantifier.One),
      OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayTMax"), OperandQuantifier.One)
    )
  )
  lazy val m199: Metadata = Metadata(
    NonEmptyString("Reserved"),
    Some(NonEmptyString("None")),
    Vec(NonEmptyString("RayQueryKHR")),
    Vec(OperandShape(NonEmptyString("IdRef"), NonEmptyString("rayQuery"), OperandQuantifier.One))
  )
}
