package net.kurobako.gemstone.isa.spirv

import assembly.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.isa.*

class OperandConstraintSuite extends munit.FunSuite {
  private val declarations =
    """%void = OpTypeVoid
      |%bool = OpTypeBool
      |%int = OpTypeInt 32 0
      |%long = OpTypeInt 64 0
      |%float = OpTypeFloat 32
      |%vec4 = OpTypeVector %float 4
      |%ptr = OpTypePointer Function %int
      |%fn = OpTypeFunction %void
      |%one = OpConstant %int 1
      |%zero = OpConstant %int 0
      |%fval = OpConstant %float 1.0
      |%yes = OpConstantTrue %bool
      |%scope = OpConstant %int 2
      |%scope64 = OpConstant %long 2
      |%notscope = OpConstant %int 9
      |%sem = OpConstant %int 264
      |%unknown_sem = OpConstant %int 65536
      |""".stripMargin

  private def inFunction(body: String)(using munit.Location): Module =
    assemble(
      s"""$declarations
         |%main = OpFunction %void None %fn
         |%entry = OpLabel
         |$body
         |OpReturn
         |OpFunctionEnd
         |""".stripMargin
    )

  private def declared(extra: String)(using munit.Location): Module = assemble(s"$declarations\n$extra")

  /** The (opcode, operand) pairs of the operands that break the table, in module order. */
  private def broken(module: Module): Vec[(String, String)] =
    OperandConstraintCheck.violations(module).map(violation => violation.opcode -> violation.operand)

  test("well-formed uses of declared values break no constraint") {
    val module = inFunction(
      """%var = OpVariable %ptr Function
        |%loaded = OpLoad %int %var
        |%sum = OpIAdd %int %loaded %one
        |OpStore %var %sum
        |%pick = OpAccessChain %ptr %var %one
        |OpControlBarrier %scope %scope %sem
        |OpMemoryBarrier %scope %sem
        |%all = OpGroupAll %bool %scope %yes
        |%unknown = OpLoad %int %undeclared""".stripMargin
    )
    assertEquals(broken(module), Vec.empty[(String, String)])
  }

  test("a result type must name a type") {
    assertEquals(broken(inFunction("%copy = OpCopyObject %entry %one")), Vec("OpCopyObject" -> "IdResultType"))
  }

  test("a vector's component type must be a scalar and its count 2, 3, 4, 8 or 16") {
    assertEquals(broken(declared("%v = OpTypeVector %void 4")), Vec("OpTypeVector" -> "Component Type"))
    assertEquals(broken(declared("%v = OpTypeVector %float 5")), Vec("OpTypeVector" -> "Component Count"))
    assertEquals(broken(declared("%v = OpTypeVector %float 8")), Vec.empty[(String, String)])
    assertEquals(broken(declared("%v = OpTypeVector %float 16")), Vec.empty[(String, String)])
  }

  test("a matrix's columns must be vectors and its count 2, 3 or 4") {
    assertEquals(broken(declared("%m = OpTypeMatrix %float 4")), Vec("OpTypeMatrix" -> "Column Type"))
    assertEquals(broken(declared("%m = OpTypeMatrix %vec4 5")), Vec("OpTypeMatrix" -> "Column Count"))
    assertEquals(broken(declared("%m = OpTypeMatrix %vec4 3")), Vec.empty[(String, String)])
  }

  test("a vector's pointer components need the MaskedGatherScatterINTEL capability") {
    assertEquals(broken(declared("%pv = OpTypeVector %ptr 4")), Vec("OpTypeVector" -> "Component Type"))
    assertEquals(
      broken(assemble(s"OpCapability MaskedGatherScatterINTEL\n$declarations\n%pv = OpTypeVector %ptr 4")),
      Vec.empty[(String, String)]
    )
  }

  test("integer widths are whole bytes and signedness is 0 or 1") {
    assertEquals(broken(declared("%i = OpTypeInt 12 0")), Vec("OpTypeInt" -> "Width"))
    assertEquals(broken(declared("%i = OpTypeInt 32 2")), Vec("OpTypeInt" -> "Signedness"))
    assertEquals(broken(declared("%i = OpTypeInt 24 0")), Vec.empty[(String, String)])
  }

  test("float widths are 6, 8, 16, 32 or 64") {
    assertEquals(broken(declared("%f = OpTypeFloat 24")), Vec("OpTypeFloat" -> "Width"))
    assertEquals(broken(declared("%f = OpTypeFloat 8")), Vec.empty[(String, String)])
  }

  test("pointee, element and return types must be types") {
    assertEquals(broken(declared("%p = OpTypePointer Function %one")), Vec("OpTypePointer" -> "Type"))
    assertEquals(broken(declared("%a = OpTypeArray %one %one")), Vec("OpTypeArray" -> "Element Type"))
    assertEquals(broken(declared("%f2 = OpTypeFunction %one")), Vec("OpTypeFunction" -> "Return Type"))
  }

  test("image sampled types and parameters take their listed values") {
    assertEquals(broken(declared("%img = OpTypeImage %bool 1D 0 0 0 1 Unknown")), Vec("OpTypeImage" -> "Sampled Type"))
    assertEquals(broken(declared("%img = OpTypeImage %float 2D 3 0 0 1 Unknown")), Vec("OpTypeImage" -> "Depth"))
    assertEquals(broken(declared("%img = OpTypeImage %float 2D 2 1 0 2 Unknown")), Vec.empty[(String, String)])
  }

  test("branch and merge targets must be labels") {
    assertEquals(broken(inFunction("OpBranch %one")), Vec("OpBranch" -> "Target Label"))
    assertEquals(
      broken(inFunction("OpBranchConditional %yes %one %one")),
      Vec("OpBranchConditional" -> "True Label", "OpBranchConditional" -> "False Label")
    )
    assertEquals(broken(inFunction("OpLoopMerge %one %entry None")), Vec("OpLoopMerge" -> "Merge Block"))
  }

  test("a branch condition is boolean and a switch selector is a scalar integer") {
    assertEquals(
      broken(inFunction("OpBranchConditional %one %entry %entry")),
      Vec("OpBranchConditional" -> "Condition")
    )
    assertEquals(broken(inFunction("OpSwitch %fval %entry")), Vec("OpSwitch" -> "Selector"))
    assertEquals(broken(inFunction("OpSwitch %one %entry")), Vec.empty[(String, String)])
  }

  test("a function call names a function") {
    assertEquals(broken(inFunction("%call = OpFunctionCall %void %one")), Vec("OpFunctionCall" -> "Function"))
  }

  test("loads, stores and atomics take pointers") {
    assertEquals(broken(inFunction("%v = OpLoad %int %one")), Vec("OpLoad" -> "Pointer"))
    assertEquals(broken(inFunction("OpStore %one %one")), Vec("OpStore" -> "Pointer"))
    assertEquals(
      broken(inFunction("OpCopyMemory %one %one")),
      Vec("OpCopyMemory" -> "Target", "OpCopyMemory" -> "Source")
    )
    assertEquals(
      broken(inFunction("%v = OpAtomicLoad %int %one %scope %sem")),
      Vec("OpAtomicLoad" -> "Pointer")
    )
  }

  test("a variable's result type is a pointer") {
    assertEquals(broken(inFunction("%v = OpVariable %int Function")), Vec("OpVariable" -> "IdResultType"))
  }

  test("access chain indexes are scalar integers, checked one at a time") {
    assertEquals(
      broken(inFunction("%var = OpVariable %ptr Function\n%ac = OpAccessChain %ptr %var %one %fval %one")),
      Vec("OpAccessChain" -> "Indexes")
    )
  }

  test("a scope names a 32-bit integer whose constant value is a Scope") {
    assertEquals(broken(inFunction("OpControlBarrier %notscope %scope %sem")), Vec("OpControlBarrier" -> "IdScope"))
    assertEquals(broken(inFunction("OpMemoryBarrier %scope64 %sem")), Vec("OpMemoryBarrier" -> "IdScope"))
  }

  test("memory semantics name a 32-bit integer with only MemorySemantics bits") {
    assertEquals(
      broken(inFunction("OpMemoryBarrier %scope %unknown_sem")),
      Vec("OpMemoryBarrier" -> "IdMemorySemantics")
    )
  }

  test("vector extraction takes a vector and an integer index") {
    assertEquals(
      broken(inFunction("%vu = OpUndef %vec4\n%ex = OpVectorExtractDynamic %float %vu %fval")),
      Vec("OpVectorExtractDynamic" -> "Index")
    )
    assertEquals(
      broken(inFunction("%ex = OpVectorExtractDynamic %float %one %one")),
      Vec("OpVectorExtractDynamic" -> "Vector")
    )
  }

  test("a group predicate is boolean") {
    assertEquals(broken(inFunction("%g = OpGroupAll %bool %scope %one")), Vec("OpGroupAll" -> "Predicate"))
  }

  test("the validator rejects a module that breaks a constraint") {
    val module = inFunction("%v = OpLoad %int %one")
    Validation.validate(module, Validation.Environment.OpenCLLibrary) match {
      case ValidationOutcome.Rejected(issues) =>
        assert(issues.exists {
          case Validation.Issue.InvalidOperand(violation) => violation.operand == "Pointer"
          case _                                          => false
        })
      case other => fail(s"expected rejection, found $other")
    }
  }

  test("the table names the constraint on an operand by its position or kind") {
    assertEquals(
      OperandConstraintCheck.of("OpTypeVector", 2, OperandKind.LiteralInteger),
      Some(OperandConstraint.OneOf(Vec(2L, 3L, 4L, 8L, 16L)))
    )
    assertEquals(OperandConstraintCheck.of("OpIAdd", 0, OperandKind.IdResultType), Some(OperandConstraint.Type))
    assertEquals(OperandConstraintCheck.of("OpIAdd", 2, OperandKind.IdRef), None)
  }
}
