package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.testing.*

class IntelBlockSuite extends munit.FunSuite {
  private val header =
    s"""OpCapability SubgroupBufferBlockIOINTEL
       |OpExtension "SPV_INTEL_subgroups"
       |$openCL""".stripMargin

  test("buffer block reads and writes give each lane its element, striding vector components by the subgroup size") {
    val module = spv"""
      $header
      OpEntryPoint Kernel %main "block" %lid
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ushort = OpTypeInt 16 0
      %v4uint = OpTypeVector %uint 4
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %ushort_ptr = OpTypePointer CrossWorkgroup %ushort
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %uint_ptr %uint_ptr %uint_ptr %ushort_ptr %ushort_ptr
      %main = OpFunction %void None %fn
      %in = OpFunctionParameter %uint_ptr
      %scalars = OpFunctionParameter %uint_ptr
      %copy = OpFunctionParameter %uint_ptr
      %narrow_in = OpFunctionParameter %ushort_ptr
      %narrow_out = OpFunctionParameter %ushort_ptr
      %entry = OpLabel
      %lane = OpLoad %uint %lid
      %value = OpSubgroupBlockReadINTEL %uint %in
      %slot = OpPtrAccessChain %uint_ptr %scalars %lane
      OpStore %slot %value
      %vector = OpSubgroupBlockReadINTEL %v4uint %in
      OpSubgroupBlockWriteINTEL %copy %vector
      %narrow = OpSubgroupBlockReadINTEL %ushort %narrow_in
      OpSubgroupBlockWriteINTEL %narrow_out %narrow
      OpReturn
      OpFunctionEnd
    """
    val memory    = RegionMemory()
    val in        = memory.allocate(128L)
    val scalars   = memory.allocate(32L)
    val copy      = memory.allocate(128L)
    val narrowIn  = memory.allocate(16L)
    val narrowOut = memory.allocate(16L)
    val values    = (0 until 32).map(index => index * 3L + 1L)
    writeWords(memory, in, 0, values*)
    (0 until 8).foreach(index => memory.array(narrowIn)(index * 2) = (index + 40).toByte)
    assertEquals(
      run(
        module,
        "block",
        memory,
        Vec(buffer(in), buffer(scalars), buffer(copy), buffer(narrowIn), buffer(narrowOut)),
        local = testDimensions(8)
      ),
      Right(())
    )
    assertEquals(words(memory, scalars, offset = 0, count = 8), Vec.from(values.take(8)))
    assertEquals(words(memory, copy, offset = 0, count = 32), Vec.from(values))
    assertEquals(memory.bytes(narrowOut).toVector, memory.bytes(narrowIn).toVector)
  }
}
