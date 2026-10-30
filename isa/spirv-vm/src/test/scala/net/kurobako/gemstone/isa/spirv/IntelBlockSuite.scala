package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.binary.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.constraint.*
import net.kurobako.gemstone.memory.*
import net.kurobako.gemstone.testing.*
import net.kurobako.gemstone.testing.bits.*

class IntelBlockSuite extends Suite {
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

  // the operands llvm-spirv --spirv-ext=+all gives __spirv_Subgroup2DBlock*INTEL calls: constant element size and block
  // shape, a CrossWorkgroup base, the region's width, height and pitch in bytes, an int2 coordinate and Function storage
  test("2D block loads, transforms, transposes and stores assign block columns to the lanes of an 8-wide subgroup") {
    val module = spv"""
      OpCapability Subgroup2DBlockIOINTEL
      OpCapability Subgroup2DBlockTransformINTEL
      OpCapability Subgroup2DBlockTransposeINTEL
      OpExtension "SPV_INTEL_2d_block_io"
      $openCL
      OpEntryPoint Kernel %main "block2d" %lid
      OpExecutionMode %main SubgroupSize 8
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %ushort = OpTypeInt 16 0
      %ulong = OpTypeInt 64 0
      %v2uint = OpTypeVector %uint 2
      %uint_1 = OpConstant %uint 1
      %uint_2 = OpConstant %uint 2
      %uint_4 = OpConstant %uint 4
      %uint_8 = OpConstant %uint 8
      %uint_32 = OpConstant %uint 32
      %uint_64 = OpConstant %uint 64
      %uint_0 = OpConstant %uint 0
      %at = OpConstantComposite %v2uint %uint_4 %uint_2
      %origin = OpConstantComposite %v2uint %uint_0 %uint_0
      %eight_ushorts = OpTypeArray %ushort %uint_8
      %four_uints = OpTypeArray %uint %uint_4
      %eight_uints = OpTypeArray %uint %uint_8
      %ushorts_ptr = OpTypePointer Function %eight_ushorts
      %packed_ptr = OpTypePointer Function %four_uints
      %transposed_ptr = OpTypePointer Function %eight_uints
      %ushort_ptr = OpTypePointer CrossWorkgroup %ushort
      %uint_ptr = OpTypePointer CrossWorkgroup %uint
      %ushorts_out = OpTypePointer CrossWorkgroup %eight_ushorts
      %packed_out = OpTypePointer CrossWorkgroup %four_uints
      %transposed_out = OpTypePointer CrossWorkgroup %eight_uints
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %ushort_ptr %uint_ptr %ushort_ptr %ushorts_out %packed_out %transposed_out
      %main = OpFunction %void None %fn
      %src = OpFunctionParameter %ushort_ptr
      %words = OpFunctionParameter %uint_ptr
      %dst = OpFunctionParameter %ushort_ptr
      %loaded_out = OpFunctionParameter %ushorts_out
      %transformed_out = OpFunctionParameter %packed_out
      %transposed_words = OpFunctionParameter %transposed_out
      %entry = OpLabel
      %loaded = OpVariable %ushorts_ptr Function
      %transformed = OpVariable %packed_ptr Function
      %transposed = OpVariable %transposed_ptr Function
      %lane = OpLoad %uint %lid
      OpSubgroup2DBlockLoadINTEL %uint_2 %uint_8 %uint_8 %uint_1 %src %uint_32 %uint_8 %uint_32 %at %loaded
      OpSubgroup2DBlockLoadTransformINTEL %uint_2 %uint_8 %uint_8 %uint_1 %src %uint_32 %uint_8 %uint_32 %at %transformed
      OpSubgroup2DBlockLoadTransposeINTEL %uint_4 %uint_8 %uint_8 %uint_1 %words %uint_64 %uint_8 %uint_64 %origin %transposed
      OpSubgroup2DBlockPrefetchINTEL %uint_2 %uint_8 %uint_8 %uint_1 %src %uint_32 %uint_8 %uint_32 %at
      OpSubgroup2DBlockStoreINTEL %uint_2 %uint_8 %uint_8 %uint_1 %loaded %dst %uint_32 %uint_8 %uint_32 %origin
      %a = OpLoad %eight_ushorts %loaded
      %a_slot = OpPtrAccessChain %ushorts_out %loaded_out %lane
      OpStore %a_slot %a
      %b = OpLoad %four_uints %transformed
      %b_slot = OpPtrAccessChain %packed_out %transformed_out %lane
      OpStore %b_slot %b
      %c = OpLoad %eight_uints %transposed
      %c_slot = OpPtrAccessChain %transposed_out %transposed_words %lane
      OpStore %c_slot %c
      OpReturn
      OpFunctionEnd
    """
    val memory      = RegionMemory()
    val src         = memory.allocate(256L)
    val table       = memory.allocate(512L)
    val dst         = memory.allocate(256L)
    val loaded      = memory.allocate(128L)
    val transformed = memory.allocate(128L)
    val transposed  = memory.allocate(256L)
    // 16 ushorts and 16 uints a row, 8 rows each; the region is 32 or 64 bytes wide, so columns 4 to 11 reach past it
    def element(row: Int, column: Int): Long = row * 64L + column
    (0 until 8).foreach(row =>
      (0 until 16).foreach(column =>
        LittleEndian.write(element(row, column), memory.array(src), (row * 16 + column) * 2, 2)
      )
    )
    writeWords(memory, table, 0, (0 until 128).map(index => 0x10000L + index)*)
    assertEquals(
      run(
        module,
        "block2d",
        memory,
        Vec(buffer(src), buffer(table), buffer(dst), buffer(loaded), buffer(transformed), buffer(transposed)),
        local = testDimensions(8)
      ),
      Right(())
    )
    def inside(row: Int, column: Int): Long                 = if (row < 8 && column < 16) element(row, column) else 0L
    def halves(region: AllocationId, count: Int): Vec[Long] =
      Vec.tabulate(count)(index => LittleEndian.decode(memory.bytes(region), index * 2, 2))
    // lane l takes column 4 + l of rows 2 to 9; rows 8 and 9 lie past the region
    val expected = Vec.tabulate(8)(lane => Vec.tabulate(8)(k => inside(2 + k, 4 + lane)))
    assertEquals(halves(loaded, 64), expected.flatten)
    assertEquals(
      words(memory, transformed, offset = 0, count = 32),
      Vec.tabulate(8)(lane =>
        Vec.tabulate(4)(k => inside(2 + 2 * k, 4 + lane) | (inside(3 + 2 * k, 4 + lane) << 16))
      ).flatten
    )
    // transposed, lane l takes row l of the 8x8 block of 32-bit words at the origin
    assertEquals(
      words(memory, transposed, offset = 0, count = 64),
      Vec.tabulate(8)(lane => Vec.tabulate(8)(k => 0x10000L + lane * 16 + k)).flatten
    )
    assertEquals(
      halves(dst, 128),
      Vec.tabulate(8)(row => Vec.tabulate(16)(column => if (column < 8) expected(column)(row) else 0L)).flatten
    )
  }

  /** A subgroup of eight lanes each loading its A part, B column and C column as eight 32-bit words, multiplying with
    * `operands` over K = `inner` and storing its result column.
    */
  private def multiply(accumulator: String, inner: Int, operands: String, a: Seq[Long], b: Seq[Long], c: Seq[Long])
      : Vec[Long] = {
    val module = spv"""
      OpCapability SubgroupMatrixMultiplyAccumulateINTEL
      OpExtension "SPV_INTEL_subgroup_matrix_multiply_accumulate"
      $openCL
      OpEntryPoint Kernel %main "mad" %lid
      OpExecutionMode %main SubgroupSize 8
      OpDecorate %lid BuiltIn LocalInvocationIndex
      %void = OpTypeVoid
      %uint = OpTypeInt 32 0
      %float = OpTypeFloat 32
      %v8uint = OpTypeVector %uint 8
      %v8float = OpTypeVector %float 8
      %k = OpConstant %uint $inner
      %words_ptr = OpTypePointer CrossWorkgroup %v8uint
      %column_ptr = OpTypePointer CrossWorkgroup $accumulator
      %lid_ptr = OpTypePointer Input %uint
      %lid = OpVariable %lid_ptr Input
      %fn = OpTypeFunction %void %words_ptr %words_ptr %column_ptr %column_ptr
      %main = OpFunction %void None %fn
      %a_in = OpFunctionParameter %words_ptr
      %b_in = OpFunctionParameter %words_ptr
      %c_in = OpFunctionParameter %column_ptr
      %out = OpFunctionParameter %column_ptr
      %entry = OpLabel
      %lane = OpLoad %uint %lid
      %a_slot = OpPtrAccessChain %words_ptr %a_in %lane
      %a = OpLoad %v8uint %a_slot
      %b_slot = OpPtrAccessChain %words_ptr %b_in %lane
      %b = OpLoad %v8uint %b_slot
      %c_slot = OpPtrAccessChain %column_ptr %c_in %lane
      %c = OpLoad $accumulator %c_slot
      %d = OpSubgroupMatrixMultiplyAccumulateINTEL $accumulator %k %a %b %c $operands
      %out_slot = OpPtrAccessChain %column_ptr %out %lane
      OpStore %out_slot %d
      OpReturn
      OpFunctionEnd
    """
    val memory  = RegionMemory()
    val buffers = Vec(a, b, c, Seq.fill(64)(0L)).map { values =>
      val region = memory.allocate(256L)
      writeWords(memory, region, 0, values*)
      region
    }
    assertEquals(run(module, "mad", memory, buffers.map(buffer(_)), local = testDimensions(8)), Right(()))
    words(memory, buffers(3), offset = 0, count = 64)
  }

  /** `elements` per 32-bit word of `bits` each, lowest first. */
  private def pack(values: Seq[Long], bits: Int): Long =
    values.zipWithIndex.foldLeft(0L)((word, entry) => word | ((entry._1 & ((1L << bits) - 1)) << (entry._2 * bits)))

  test("subgroup matrix multiply-accumulate gathers A from every lane and packs int8 A rows and B columns") {
    val a = Vec.tabulate(8)(row => Vec.tabulate(32)(k => ((row * 7 + k * 3) % 23 - 11).toLong))
    val b = Vec.tabulate(32)(k => Vec.tabulate(8)(column => ((k * 5 + column) % 19 - 9).toLong))
    val c = Vec.tabulate(8)(row => Vec.tabulate(8)(column => (row * 100 + column).toLong))
    // lane l passes columns 4l to 4l + 3 of each row of A, and rows 4j to 4j + 3 of its column of B in word j
    val passedA = (0 until 8).flatMap(lane =>
      (0 until 8).map(row => pack((0 until 4).map(t => a(row)(4 * lane + t)), 8))
    )
    val passedB = (0 until 8).flatMap(lane =>
      (0 until 8).map(word => pack((0 until 4).map(t => b(4 * word + t)(lane)), 8))
    )
    val passedC = (0 until 8).flatMap(lane => (0 until 8).map(row => c(row)(lane) & 0xffffffffL))
    val result  = multiply(
      "%v8uint",
      32,
      "MatrixASignedComponentsINTEL|MatrixBSignedComponentsINTEL|MatrixAPackedInt8INTEL|MatrixBPackedInt8INTEL",
      passedA,
      passedB,
      passedC
    )
    assertEquals(
      result,
      Vec.from((0 until 8).flatMap(lane =>
        (0 until 8).map(row => (c(row)(lane) + (0 until 32).map(k => a(row)(k) * b(k)(lane)).sum) & 0xffffffffL)
      ))
    )
  }

  test("subgroup matrix multiply-accumulate reads packed binary16 A and B into binary32 accumulators") {
    def half(value: Double): Long = IEEE754.fromDouble(value, 16)
    val a                         = Vec.tabulate(8)(row => Vec.tabulate(16)(k => ((row + 2 * k) % 7 - 3) * 0.5))
    val b                         = Vec.tabulate(16)(k => Vec.tabulate(8)(column => ((k + column) % 5 - 2) * 0.25))
    val c                         = Vec.tabulate(8)(row => Vec.tabulate(8)(column => row - column * 0.5))
    val passedA                   = (0 until 8).flatMap(lane =>
      (0 until 8).map(row => pack((0 until 2).map(t => half(a(row)(2 * lane + t))), 16))
    )
    val passedB = (0 until 8).flatMap(lane =>
      (0 until 8).map(word => pack((0 until 2).map(t => half(b(2 * word + t)(lane))), 16))
    )
    val passedC = (0 until 8).flatMap(lane => (0 until 8).map(row => single(c(row)(lane).toFloat)))
    val result  = multiply(
      "%v8float",
      16,
      "MatrixAPackedFloat16INTEL|MatrixBPackedFloat16INTEL",
      passedA,
      passedB,
      passedC
    )
    assertEquals(
      result,
      Vec.from((0 until 8).flatMap(lane =>
        (0 until 8).map(row => single((c(row)(lane) + (0 until 16).map(k => a(row)(k) * b(k)(lane)).sum).toFloat))
      ))
    )
  }
}
