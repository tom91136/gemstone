package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.vm.*
import net.kurobako.gemstone.vm.LaunchGeometry.*

class FaultLocationSuite extends Suite {
  private val module = spv"""
    $openCL
    OpEntryPoint Kernel %main "overrun" %lid
    OpDecorate %lid BuiltIn LocalInvocationIndex
    %void = OpTypeVoid
    %uint = OpTypeInt 32 0
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %lid_ptr = OpTypePointer Input %uint
    %lid = OpVariable %lid_ptr Input
    %fn = OpTypeFunction %void %uint_ptr
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %lane = OpLoad %uint %lid
    %slot = OpPtrAccessChain %uint_ptr %out %lane
    OpStore %slot %lane
    OpReturn
    OpFunctionEnd
  """

  test("a faulting lane reports its kernel, instruction, workgroup and lane") {
    val memory = RegionMemory()
    val out    = memory.allocate(8L)
    val store  = module.instructions.indexWhere { case Instruction.Store(_, _, _) => true; case _ => false }
    val result = load(module).flatMap(execute(_, task("overrun", Vec(buffer(out)), Dimensions(4, 1, 1)), memory))
    assertEquals(
      result.left.map(_.location),
      Left(Some(Fault.Location("overrun", None, Some(store.toLong.unsafe), Some(Coordinates.origin), Some(2))))
    )
  }
}
