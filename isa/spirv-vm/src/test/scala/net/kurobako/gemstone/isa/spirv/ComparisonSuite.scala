package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*

class ComparisonSuite extends Suite {
  private val FloatPairs =
    Seq("0x1.800002p+128" -> "1.0", "1.0" -> "0x1.800002p+128", "0.0" -> "-0.0", "-1.0" -> "2.0", "2.0" -> "-1.0")

  for {
    (opcode, expected) <- Seq(
      "FOrdEqual"              -> Seq(0, 0, 1, 0, 0),
      "FUnordEqual"            -> Seq(1, 1, 1, 0, 0),
      "FOrdNotEqual"           -> Seq(0, 0, 0, 1, 1),
      "LessOrGreater"          -> Seq(0, 0, 0, 1, 1),
      "FUnordNotEqual"         -> Seq(1, 1, 0, 1, 1),
      "FOrdLessThan"           -> Seq(0, 0, 0, 1, 0),
      "FUnordLessThan"         -> Seq(1, 1, 0, 1, 0),
      "FOrdLessThanEqual"      -> Seq(0, 0, 1, 1, 0),
      "FUnordLessThanEqual"    -> Seq(1, 1, 1, 1, 0),
      "FOrdGreaterThan"        -> Seq(0, 0, 0, 0, 1),
      "FUnordGreaterThan"      -> Seq(1, 1, 0, 0, 1),
      "FOrdGreaterThanEqual"   -> Seq(0, 0, 1, 0, 1),
      "FUnordGreaterThanEqual" -> Seq(1, 1, 1, 0, 1)
    )
  } test(s"Op$opcode handles NaN in either operand, signed zero, and finite ordering") {
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "compare"
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %bool = OpTypeBool
      %float = OpTypeFloat 32
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %fn = OpTypeFunction %void %uint_ptr
      %zero = OpConstant %uint 0
      %one = OpConstant %uint 1
      ${FloatPairs.zipWithIndex.map { case ((left, right), i) =>
        s"%left$i = OpConstant %float $left\n%right$i = OpConstant %float $right\n%index$i = OpConstant %uint $i"
      }.mkString("\n")}
      %main = OpFunction %void None %fn
      %out = OpFunctionParameter %uint_ptr
      %entry = OpLabel
      ${FloatPairs.indices.map { i =>
        s"""%compare$i = Op$opcode %bool %left$i %right$i
           |%value$i = OpSelect %uint %compare$i %one %zero
           |%slot$i = OpPtrAccessChain %uint_ptr %out %index$i
           |OpStore %slot$i %value$i""".stripMargin
      }.mkString("\n")}
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate(20L)
    assertEquals(run(module, "compare", memory, Vec(buffer(out))), Right(()))
    assertEquals(words(memory, out, offset = 0, count = 5), Vec.from(expected.map(_.toLong)))
  }

  for {
    (opcode, expected) <- Seq("IEqual" -> 3L, "SLessThan" -> 0xfffffff9L, "SGreaterThan" -> 3L)
  } test(s"Op$opcode selects between signed operands through a boolean result") {
    val memory = RegionMemory()
    val out    = memory.allocate(4L)
    assertEquals(
      run(
        kernel(s"%compare = Op$opcode %bool %a %b\n%result = OpSelect %uint %compare %a %b"),
        "compare",
        memory,
        Vec(buffer(out), scalar(0xfffffff9L, 4), scalar(3L, 4), scalar(0L, 4), scalar(0L, 4))
      ),
      Right(())
    )
    assertEquals(word(memory, out, 0), expected)
  }

  for {
    (instruction, message) <- Seq[(String, NonEmpty[String])](
      "OpIEqual %uint %a %b"             -> "OpIEqual result type %4 is not a boolean scalar or vector",
      "OpSRem %bool %a %b"               -> "OpSRem result type %5 is not an integer scalar or vector",
      "OpSDiv %bool %a %b"               -> "OpSDiv result type %5 is not an integer scalar or vector",
      "OpFOrdEqual %float %x %y"         -> "OpFOrdEqual result type %6 is not a boolean scalar or vector",
      "OpFOrdLessThanEqual %float %x %y" -> "OpFOrdLessThanEqual result type %6 is not a boolean scalar or vector"
    )
  } test(s"${instruction.takeWhile(_ != ' ')} rejects a mistyped result") {
    val program = loadUnchecked(kernel(s"%result = $instruction"))
    val memory  = RegionMemory()
    val out     = memory.allocate(4L)
    val outcome = program.flatMap(execute(_, task("compare", Vec(buffer(out)) ++ Vec.fill(4)(scalar(0L, 4))), memory))
    assertEquals(outcome, Left(Fault.InvalidRequest(message)))
  }

  private def kernel(body: String)(using munit.Location): Module = spv"""
    $openCL
    OpEntryPoint Kernel %main "compare"
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %bool = OpTypeBool
    %float = OpTypeFloat 32
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %fn = OpTypeFunction %void %uint_ptr %uint %uint %float %float
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %a = OpFunctionParameter %uint
    %b = OpFunctionParameter %uint
    %x = OpFunctionParameter %float
    %y = OpFunctionParameter %float
    %entry = OpLabel
    $body
    ${if (body.contains("OpSelect")) "OpStore %out %result" else ""}
    OpReturn
    OpFunctionEnd
  """
}
