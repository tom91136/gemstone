package net.kurobako.gemstone.codegen

/** Operand constraints the SPIR-V grammar leaves to prose. Rows name the operand by its grammar name, or by its kind
  * where the operand is unnamed, and cite the SPIRV-Tools validator function that enforces them.
  */
object SPIRVOperandConstraints {
  enum ValueClass derives CanEqual {
    case BoolScalar, IntScalar, Pointer, Vector
  }

  enum Constraint derives CanEqual {
    case Type
    case Declares(opcodes: Vector[String])
    case Value(kind: ValueClass)
    case ScopeId
    case MemorySemanticsId
    case ScalarComponent
    case ByteWidth
    case OneOf(values: Vector[Long])
  }

  case class Rule(opcode: String, operand: String, constraint: Constraint, source: String)

  /** Constraints on every operand of a kind, whichever instruction it appears in. */
  val kinds: Vector[(String, Constraint, String)] = Vector(
    ("IdResultType", Constraint.Type, "SPIRV-Tools validate_id.cpp IdPass"),
    ("IdScope", Constraint.ScopeId, "SPIRV-Tools validate_scopes.cpp ValidateScope"),
    ("IdMemorySemantics", Constraint.MemorySemanticsId, "SPIRV-Tools validate_memory_semantics.cpp")
  )

  private val typeSource      = "SPIRV-Tools validate_type.cpp"
  private val imageSource     = "SPIRV-Tools validate_image.cpp ValidateTypeImage"
  private val cfgSource       = "SPIRV-Tools validate_cfg.cpp"
  private val functionSource  = "SPIRV-Tools validate_function.cpp"
  private val memorySource    = "SPIRV-Tools validate_memory.cpp"
  private val atomicSource    = "SPIRV-Tools validate_atomics.cpp AtomicsPass"
  private val compositeSource = "SPIRV-Tools validate_composites.cpp"
  private val groupSource     = "SPIRV-Tools validate_group.cpp ValidateGroupAnyAll"
  private val selectionSource = "SPIR-V unified spec, OpSelectionMerge"

  private val label    = Constraint.Declares(Vector("OpLabel"))
  private val pointer  = Constraint.Value(ValueClass.Pointer)
  private val intValue = Constraint.Value(ValueClass.IntScalar)

  /** Families in table order; each becomes one method of the generated table. */
  val families: Vector[(String, Vector[Rule])] = Vector(
    "typeDeclarations" -> Vector(
      Rule("OpTypeVector", "Component Type", Constraint.ScalarComponent, s"$typeSource ValidateTypeVector"),
      Rule(
        "OpTypeVector",
        "Component Count",
        Constraint.OneOf(Vector(2L, 3L, 4L, 8L, 16L)),
        s"$typeSource ValidateTypeVector"
      ),
      Rule(
        "OpTypeMatrix",
        "Column Type",
        Constraint.Declares(Vector("OpTypeVector")),
        s"$typeSource ValidateTypeMatrix"
      ),
      Rule("OpTypeMatrix", "Column Count", Constraint.OneOf(Vector(2L, 3L, 4L)), s"$typeSource ValidateTypeMatrix"),
      Rule("OpTypeInt", "Width", Constraint.ByteWidth, s"$typeSource ValidateTypeInt"),
      Rule("OpTypeInt", "Signedness", Constraint.OneOf(Vector(0L, 1L)), s"$typeSource ValidateTypeInt"),
      Rule("OpTypeFloat", "Width", Constraint.OneOf(Vector(6L, 8L, 16L, 32L, 64L)), s"$typeSource ValidateTypeFloat"),
      Rule("OpTypePointer", "Type", Constraint.Type, s"$typeSource ValidateTypePointer"),
      Rule("OpTypeArray", "Element Type", Constraint.Type, s"$typeSource ValidateTypeArray"),
      Rule("OpTypeFunction", "Return Type", Constraint.Type, s"$typeSource ValidateTypeFunction"),
      Rule(
        "OpTypeImage",
        "Sampled Type",
        Constraint.Declares(Vector("OpTypeVoid", "OpTypeInt", "OpTypeFloat")),
        imageSource
      ),
      Rule("OpTypeImage", "Depth", Constraint.OneOf(Vector(0L, 1L, 2L)), imageSource),
      Rule("OpTypeImage", "Arrayed", Constraint.OneOf(Vector(0L, 1L)), imageSource),
      Rule("OpTypeImage", "MS", Constraint.OneOf(Vector(0L, 1L)), imageSource),
      Rule("OpTypeImage", "Sampled", Constraint.OneOf(Vector(0L, 1L, 2L)), imageSource)
    ),
    "controlFlow" -> Vector(
      Rule(
        "OpFunction",
        "Function Type",
        Constraint.Declares(Vector("OpTypeFunction")),
        s"$functionSource ValidateFunction"
      ),
      Rule(
        "OpFunctionCall",
        "Function",
        Constraint.Declares(Vector("OpFunction")),
        s"$functionSource ValidateFunctionCall"
      ),
      Rule("OpBranch", "Target Label", label, s"$cfgSource ValidateBranch"),
      Rule(
        "OpBranchConditional",
        "Condition",
        Constraint.Value(ValueClass.BoolScalar),
        s"$cfgSource ValidateBranchConditional"
      ),
      Rule("OpBranchConditional", "True Label", label, s"$cfgSource ValidateBranchConditional"),
      Rule("OpBranchConditional", "False Label", label, s"$cfgSource ValidateBranchConditional"),
      Rule("OpSwitch", "Selector", intValue, s"$cfgSource ValidateSwitch"),
      Rule("OpSwitch", "Default", label, s"$cfgSource ValidateSwitch"),
      Rule("OpSelectionMerge", "Merge Block", label, selectionSource),
      Rule("OpLoopMerge", "Merge Block", label, s"$cfgSource ValidateLoopMerge"),
      Rule("OpLoopMerge", "Continue Target", label, s"$cfgSource ValidateLoopMerge")
    ),
    "memory" -> Vector(
      Rule("OpLoad", "Pointer", pointer, s"$memorySource ValidateLoad"),
      Rule("OpStore", "Pointer", pointer, s"$memorySource ValidateStore"),
      Rule("OpCopyMemory", "Target", pointer, s"$memorySource ValidateCopyMemory"),
      Rule("OpCopyMemory", "Source", pointer, s"$memorySource ValidateCopyMemory"),
      Rule(
        "OpVariable",
        "IdResultType",
        Constraint.Declares(Vector("OpTypePointer", "OpTypeUntypedPointerKHR")),
        s"$memorySource ValidateVariable"
      ),
      Rule("OpAccessChain", "Base", pointer, s"$memorySource ValidateAccessChain"),
      Rule("OpAccessChain", "Indexes", intValue, s"$memorySource ValidateAccessChain"),
      Rule("OpInBoundsAccessChain", "Base", pointer, s"$memorySource ValidateAccessChain"),
      Rule("OpInBoundsAccessChain", "Indexes", intValue, s"$memorySource ValidateAccessChain"),
      Rule("OpPtrAccessChain", "Base", pointer, s"$memorySource ValidatePtrAccessChain"),
      Rule("OpPtrAccessChain", "Element", intValue, s"$memorySource ValidatePtrAccessChain"),
      Rule("OpPtrAccessChain", "Indexes", intValue, s"$memorySource ValidatePtrAccessChain")
    ),
    "atomics" -> Vector(
      Rule("OpAtomicLoad", "Pointer", pointer, atomicSource),
      Rule("OpAtomicStore", "Pointer", pointer, atomicSource),
      Rule("OpAtomicExchange", "Pointer", pointer, atomicSource),
      Rule("OpAtomicIAdd", "Pointer", pointer, atomicSource),
      Rule("OpAtomicCompareExchange", "Pointer", pointer, atomicSource)
    ),
    "composites" -> Vector(
      Rule(
        "OpVectorExtractDynamic",
        "Vector",
        Constraint.Value(ValueClass.Vector),
        s"$compositeSource ValidateVectorExtractDynamic"
      ),
      Rule("OpVectorExtractDynamic", "Index", intValue, s"$compositeSource ValidateVectorExtractDynamic"),
      Rule("OpGroupAll", "Predicate", Constraint.Value(ValueClass.BoolScalar), groupSource)
    )
  )
}
