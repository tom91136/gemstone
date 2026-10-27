// Generated from the pinned Khronos SPIR-V core grammar. Regenerate with: sbt codegen
package net.kurobako.gemstone.isa.spirv

import net.kurobako.gemstone.collection.*

@javax.annotation.processing.Generated(Array("sbt codegen"))
object OperandConstraintTable {
  val kinds: Vec[KindRule] = Vec(
    KindRule(OperandKind.IdResultType, OperandConstraint.Type, "SPIRV-Tools validate_id.cpp IdPass"),
    KindRule(OperandKind.IdScope, OperandConstraint.ScopeId, "SPIRV-Tools validate_scopes.cpp ValidateScope"),
    KindRule(
      OperandKind.IdMemorySemantics,
      OperandConstraint.MemorySemanticsId,
      "SPIRV-Tools validate_memory_semantics.cpp"
    )
  )

  val rules: Vec[OperandRule] = typeDeclarations ++ controlFlow ++ memory ++ atomics ++ composites

  private def typeDeclarations: Vec[OperandRule] = Vec(
    OperandRule(
      "OpTypeVector",
      1,
      "Component Type",
      OperandQuantifier.One,
      OperandConstraint.ScalarComponent,
      "SPIRV-Tools validate_type.cpp ValidateTypeVector"
    ),
    OperandRule(
      "OpTypeVector",
      2,
      "Component Count",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(2L, 3L, 4L, 8L, 16L)),
      "SPIRV-Tools validate_type.cpp ValidateTypeVector"
    ),
    OperandRule(
      "OpTypeMatrix",
      1,
      "Column Type",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpTypeVector")),
      "SPIRV-Tools validate_type.cpp ValidateTypeMatrix"
    ),
    OperandRule(
      "OpTypeMatrix",
      2,
      "Column Count",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(2L, 3L, 4L)),
      "SPIRV-Tools validate_type.cpp ValidateTypeMatrix"
    ),
    OperandRule(
      "OpTypeInt",
      1,
      "Width",
      OperandQuantifier.One,
      OperandConstraint.ByteWidth,
      "SPIRV-Tools validate_type.cpp ValidateTypeInt"
    ),
    OperandRule(
      "OpTypeInt",
      2,
      "Signedness",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(0L, 1L)),
      "SPIRV-Tools validate_type.cpp ValidateTypeInt"
    ),
    OperandRule(
      "OpTypeFloat",
      1,
      "Width",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(6L, 8L, 16L, 32L, 64L)),
      "SPIRV-Tools validate_type.cpp ValidateTypeFloat"
    ),
    OperandRule(
      "OpTypePointer",
      2,
      "Type",
      OperandQuantifier.One,
      OperandConstraint.Type,
      "SPIRV-Tools validate_type.cpp ValidateTypePointer"
    ),
    OperandRule(
      "OpTypeArray",
      1,
      "Element Type",
      OperandQuantifier.One,
      OperandConstraint.Type,
      "SPIRV-Tools validate_type.cpp ValidateTypeArray"
    ),
    OperandRule(
      "OpTypeFunction",
      1,
      "Return Type",
      OperandQuantifier.One,
      OperandConstraint.Type,
      "SPIRV-Tools validate_type.cpp ValidateTypeFunction"
    ),
    OperandRule(
      "OpTypeImage",
      1,
      "Sampled Type",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpTypeVoid", "OpTypeInt", "OpTypeFloat")),
      "SPIRV-Tools validate_image.cpp ValidateTypeImage"
    ),
    OperandRule(
      "OpTypeImage",
      3,
      "Depth",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(0L, 1L, 2L)),
      "SPIRV-Tools validate_image.cpp ValidateTypeImage"
    ),
    OperandRule(
      "OpTypeImage",
      4,
      "Arrayed",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(0L, 1L)),
      "SPIRV-Tools validate_image.cpp ValidateTypeImage"
    ),
    OperandRule(
      "OpTypeImage",
      5,
      "MS",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(0L, 1L)),
      "SPIRV-Tools validate_image.cpp ValidateTypeImage"
    ),
    OperandRule(
      "OpTypeImage",
      6,
      "Sampled",
      OperandQuantifier.One,
      OperandConstraint.OneOf(Vec(0L, 1L, 2L)),
      "SPIRV-Tools validate_image.cpp ValidateTypeImage"
    )
  )

  private def controlFlow: Vec[OperandRule] = Vec(
    OperandRule(
      "OpFunction",
      3,
      "Function Type",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpTypeFunction")),
      "SPIRV-Tools validate_function.cpp ValidateFunction"
    ),
    OperandRule(
      "OpFunctionCall",
      2,
      "Function",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpFunction")),
      "SPIRV-Tools validate_function.cpp ValidateFunctionCall"
    ),
    OperandRule(
      "OpBranch",
      0,
      "Target Label",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateBranch"
    ),
    OperandRule(
      "OpBranchConditional",
      0,
      "Condition",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.BoolScalar),
      "SPIRV-Tools validate_cfg.cpp ValidateBranchConditional"
    ),
    OperandRule(
      "OpBranchConditional",
      1,
      "True Label",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateBranchConditional"
    ),
    OperandRule(
      "OpBranchConditional",
      2,
      "False Label",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateBranchConditional"
    ),
    OperandRule(
      "OpSwitch",
      0,
      "Selector",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_cfg.cpp ValidateSwitch"
    ),
    OperandRule(
      "OpSwitch",
      1,
      "Default",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateSwitch"
    ),
    OperandRule(
      "OpSelectionMerge",
      0,
      "Merge Block",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIR-V unified spec, OpSelectionMerge"
    ),
    OperandRule(
      "OpLoopMerge",
      0,
      "Merge Block",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateLoopMerge"
    ),
    OperandRule(
      "OpLoopMerge",
      1,
      "Continue Target",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpLabel")),
      "SPIRV-Tools validate_cfg.cpp ValidateLoopMerge"
    )
  )

  private def memory: Vec[OperandRule] = Vec(
    OperandRule(
      "OpLoad",
      2,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateLoad"
    ),
    OperandRule(
      "OpStore",
      0,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateStore"
    ),
    OperandRule(
      "OpCopyMemory",
      0,
      "Target",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateCopyMemory"
    ),
    OperandRule(
      "OpCopyMemory",
      1,
      "Source",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateCopyMemory"
    ),
    OperandRule(
      "OpVariable",
      0,
      "IdResultType",
      OperandQuantifier.One,
      OperandConstraint.Declares(Vec("OpTypePointer", "OpTypeUntypedPointerKHR")),
      "SPIRV-Tools validate_memory.cpp ValidateVariable"
    ),
    OperandRule(
      "OpAccessChain",
      2,
      "Base",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateAccessChain"
    ),
    OperandRule(
      "OpAccessChain",
      3,
      "Indexes",
      OperandQuantifier.Repeated,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_memory.cpp ValidateAccessChain"
    ),
    OperandRule(
      "OpInBoundsAccessChain",
      2,
      "Base",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidateAccessChain"
    ),
    OperandRule(
      "OpInBoundsAccessChain",
      3,
      "Indexes",
      OperandQuantifier.Repeated,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_memory.cpp ValidateAccessChain"
    ),
    OperandRule(
      "OpPtrAccessChain",
      2,
      "Base",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_memory.cpp ValidatePtrAccessChain"
    ),
    OperandRule(
      "OpPtrAccessChain",
      3,
      "Element",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_memory.cpp ValidatePtrAccessChain"
    ),
    OperandRule(
      "OpPtrAccessChain",
      4,
      "Indexes",
      OperandQuantifier.Repeated,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_memory.cpp ValidatePtrAccessChain"
    )
  )

  private def atomics: Vec[OperandRule] = Vec(
    OperandRule(
      "OpAtomicLoad",
      2,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_atomics.cpp AtomicsPass"
    ),
    OperandRule(
      "OpAtomicStore",
      0,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_atomics.cpp AtomicsPass"
    ),
    OperandRule(
      "OpAtomicExchange",
      2,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_atomics.cpp AtomicsPass"
    ),
    OperandRule(
      "OpAtomicIAdd",
      2,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_atomics.cpp AtomicsPass"
    ),
    OperandRule(
      "OpAtomicCompareExchange",
      2,
      "Pointer",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Pointer),
      "SPIRV-Tools validate_atomics.cpp AtomicsPass"
    )
  )

  private def composites: Vec[OperandRule] = Vec(
    OperandRule(
      "OpVectorExtractDynamic",
      2,
      "Vector",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.Vector),
      "SPIRV-Tools validate_composites.cpp ValidateVectorExtractDynamic"
    ),
    OperandRule(
      "OpVectorExtractDynamic",
      3,
      "Index",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.IntScalar),
      "SPIRV-Tools validate_composites.cpp ValidateVectorExtractDynamic"
    ),
    OperandRule(
      "OpGroupAll",
      3,
      "Predicate",
      OperandQuantifier.One,
      OperandConstraint.Value(OperandConstraint.ValueClass.BoolScalar),
      "SPIRV-Tools validate_group.cpp ValidateGroupAnyAll"
    )
  )
}
