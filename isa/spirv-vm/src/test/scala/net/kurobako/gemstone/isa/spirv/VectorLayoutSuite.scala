package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*
import net.kurobako.gemstone.testing.either.*
import net.kurobako.gemstone.vm.*

class VectorLayoutSuite extends Suite {
  for {
    (width, count) <- Seq(32 -> 4, 32 -> 8, 32 -> 16, 64 -> 3, 64 -> 4, 64 -> 8)
    byValue        <- booleans
  } test(
    s"OpenCL struct of float$width and $count-lane vector keeps natural alignment and array stride" +
      s"${if (byValue) ", passed by value" else ""}"
  ) {
    val bytes                            = width / 8
    val align                            = bytes * (if (count == 3) 4 else count)
    val size                             = align * 2
    def encode(value: Double): Vec[Byte] = LittleEndian.toVec(
      if (width == 32) single(value.toFloat) else double(value),
      bytes.refine[Int in 0 ~ 8].value
    )
    val element = encode(2.25) ++ Vec.fill(align - bytes)(0.toByte) ++
      Vec.fill(count)(encode(1.5)).flatten ++ Vec.fill(size - align - count * bytes)(0.toByte)
    val value  = if (byValue) "%loaded" else "%constant"
    val module = spv"""
      $openCL
      OpEntryPoint Kernel %main "layout"
      ${if (byValue) "OpDecorate %by_value FuncParamAttr ByVal" else ""}
      %void = OpTypeVoid
      %float = OpTypeFloat $width
      %vector = OpTypeVector %float $count
      %pair = OpTypeStruct %float %vector
      %pair_ptr = OpTypePointer CrossWorkgroup %pair
      %private_pair_ptr = OpTypePointer Function %pair
      %uint = OpTypeInt 32 0
      %one = OpConstant %uint 1
      %lane = OpConstant %float 1.5
      %head = OpConstant %float 2.25
      %lanes = OpConstantComposite %vector ${Seq.fill(count)("%lane").mkString(" ")}
      %constant = OpConstantComposite %pair %head %lanes
      %fn = OpTypeFunction %void ${if (byValue) "%private_pair_ptr" else ""} %pair_ptr
      %main = OpFunction %void None %fn
      ${if (byValue) "%by_value = OpFunctionParameter %private_pair_ptr" else ""}
      %out = OpFunctionParameter %pair_ptr
      %entry = OpLabel
      ${if (byValue) "%loaded = OpLoad %pair %by_value" else ""}
      OpStore %out $value
      %second = OpPtrAccessChain %pair_ptr %out %one
      OpStore %second $value
      OpReturn
      OpFunctionEnd
    """
    val memory = RegionMemory()
    val out    = memory.allocate((size * 2).toLong.refine[Long > 0].value)
    val passed = Option.when(byValue)(KernelTask.Argument.Scalar(element.refine[NonEmpty[Vec[Byte]]].value))
    assertEquals(run(module, "layout", memory, Vec.from(passed) :+ buffer(out)), Right(()))
    assertEquals(memory.bytes(out), element ++ element)
  }
}
