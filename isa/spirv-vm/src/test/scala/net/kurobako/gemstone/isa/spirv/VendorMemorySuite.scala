package net.kurobako.gemstone.isa.spirv

import fixtures.*
import net.kurobako.gemstone.collection.*
import net.kurobako.gemstone.testing.*

/** In the forms llvm-spirv --spirv-ext=+all gives llvm.masked.gather and llvm.masked.scatter over vectors of global
  * pointers and __spirv_PredicatedLoadINTEL and __spirv_PredicatedStoreINTEL calls, with SPV_NV_raw_access_chains'
  * byte-addressed chain hand-assembled from its specification.
  */
class VendorMemorySuite extends Suite {
  private val module = spv"""
    OpCapability MaskedGatherScatterINTEL
    OpCapability PredicatedIOINTEL
    OpCapability RawAccessChainsNV
    OpExtension "SPV_INTEL_masked_gather_scatter"
    OpExtension "SPV_INTEL_predicated_io"
    OpExtension "SPV_NV_raw_access_chains"
    $openCL
    OpEntryPoint Kernel %main "memory"
    %void = OpTypeVoid
    %bool = OpTypeBool
    %uint = OpTypeInt 32 0
    %ulong = OpTypeInt 64 0
    %v4bool = OpTypeVector %bool 4
    %v4uint = OpTypeVector %uint 4
    %v4ulong = OpTypeVector %ulong 4
    %uint_ptr = OpTypePointer CrossWorkgroup %uint
    %v4ptr = OpTypeVector %uint_ptr 4
    %fn = OpTypeFunction %void %uint_ptr %uint_ptr
    %true = OpConstantTrue %bool
    %false = OpConstantFalse %bool
    %mask = OpConstantComposite %v4bool %true %false %true %true
    %scatter_mask = OpConstantComposite %v4bool %false %true %true %true
    %ulong_0 = OpConstant %ulong 0
    %ulong_4 = OpConstant %ulong 4
    %ulong_8 = OpConstant %ulong 8
    %ulong_12 = OpConstant %ulong 12
    %ulong_5 = OpConstant %ulong 5
    %gather_offsets = OpConstantComposite %v4ulong %ulong_12 %ulong_0 %ulong_8 %ulong_4
    %scatter_offsets = OpConstantComposite %v4ulong %ulong_0 %ulong_4 %ulong_8 %ulong_12
    %uint_2 = OpConstant %uint 2
    %uint_4 = OpConstant %uint 4
    %uint_77 = OpConstant %uint 77
    %uint_99 = OpConstant %uint 99
    %main = OpFunction %void None %fn
    %out = OpFunctionParameter %uint_ptr
    %data = OpFunctionParameter %uint_ptr
    %entry = OpLabel
    %data_address = OpConvertPtrToU %ulong %data
    %data_base = OpCompositeConstruct %v4ulong %data_address %data_address %data_address %data_address
    %gather_addresses = OpIAdd %v4ulong %data_base %gather_offsets
    %gather_pointers = OpConvertUToPtr %v4ptr %gather_addresses
    %gathered = OpMaskedGatherINTEL %v4uint %gather_pointers 4 %mask %uint_77
    %out_address = OpConvertPtrToU %ulong %out
    %out_base = OpCompositeConstruct %v4ulong %out_address %out_address %out_address %out_address
    %scatter_addresses = OpIAdd %v4ulong %out_base %scatter_offsets
    %scatter_pointers = OpConvertUToPtr %v4ptr %scatter_addresses
    OpMaskedScatterINTEL %gathered %scatter_pointers 4 %scatter_mask
    %round_trip = OpConvertPtrToU %v4ulong %scatter_pointers
    %third_address = OpCompositeExtract %ulong %round_trip 2
    %third_offset = OpISub %ulong %third_address %out_address
    %third_offset_word = OpUConvert %uint %third_offset
    %slot_4 = OpInBoundsPtrAccessChain %uint_ptr %out %ulong_4
    OpStore %slot_4 %third_offset_word
    %first = OpPredicatedLoadINTEL %uint %data %true %uint_99
    %skipped = OpPredicatedLoadINTEL %uint %data %false %uint_99
    %slot_5 = OpInBoundsPtrAccessChain %uint_ptr %out %ulong_5
    OpPredicatedStoreINTEL %slot_5 %first %true
    %slot_6 = OpRawAccessChainNV %uint_ptr %out %ulong_8 %uint_2 %ulong_8
    OpPredicatedStoreINTEL %slot_6 %skipped %true
    %slot_7 = OpRawAccessChainNV %uint_ptr %out %ulong_8 %uint_4 %ulong_0
    OpPredicatedStoreINTEL %slot_7 %first %false
    OpReturn
    OpFunctionEnd
  """

  test("masked gathers and scatters reach the addresses of pointer vectors' active lanes") {
    val memory = RegionMemory()
    val out    = memory.allocate(64L)
    val data   = memory.allocate(16L)
    writeWords(memory, data, 0, 10L, 11L, 12L, 13L)
    writeWords(memory, out, 0, Vec.fill(16)(5L)*)
    assertEquals(run(module, "memory", memory, Vec(buffer(out), buffer(data))), Right(()))
    // gathered (13, 77, 12, 11); the scatter skips lane 0
    assertEquals(words(memory, out, offset = 0, count = 4), Vec(5L, 77L, 12L, 11L))
    // slot 4 holds the third pointer's offset; the predicated load reads 10 and skips to 99, raw chains reach byte
    // 8 + 8 * 2 = 24 and 8 * 4 = 32, where the predicated store is off
    assertEquals(words(memory, out, offset = 16, count = 5), Vec(8L, 10L, 99L, 5L, 5L))
  }
}
