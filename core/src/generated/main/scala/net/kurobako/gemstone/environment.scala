// Generated from build.sbt: environmentVariables. Regenerate with: sbt codegen
package net.kurobako.gemstone

object environment {
  val BenchmarkJfr: String = "GEMSTONE_BENCHMARK_JFR"
  val ExecutionMaxInstructions: String = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS"
  val ExecutionParallelism: String = "GEMSTONE_EXECUTION_PARALLELISM"
  val HsaPagingTimeoutSeconds: String = "GEMSTONE_HSA_PAGING_TIMEOUT_SECONDS"
  val HsaRejectedCodeObject: String = "GEMSTONE_HSA_REJECTED_CODE_OBJECT"
  val MemoryAllocationKind: String = "GEMSTONE_MEMORY_ALLOCATION_KIND"
  val MemoryAtomicBits: String = "GEMSTONE_MEMORY_ATOMIC_BITS"
  val MemoryConcurrencyDirectory: String = "GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY"
  val MemoryProbe: String = "GEMSTONE_MEMORY_PROBE"
  val NativeDirectory: String = "GEMSTONE_NATIVE_DIRECTORY"
  val Offline: String = "GEMSTONE_OFFLINE"
  val OpenclCompiler: String = "GEMSTONE_OPENCL_COMPILER"
  val OpenclLlvmLink: String = "GEMSTONE_OPENCL_LLVM_LINK"
  val OpenclLlvmSpirv: String = "GEMSTONE_OPENCL_LLVM_SPIRV"
  val OpenclLocalMemSize: String = "GEMSTONE_OPENCL_LOCAL_MEM_SIZE"
  val OpenclLocalMemSizeDefault: Long = 65536L
  val OpenclMaxMemAllocSize: String = "GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE"
  val OpenclMaxMemAllocSizeDefault: Long = 268435456L
  val OpenclMaxWorkGroupSize: String = "GEMSTONE_OPENCL_MAX_WORK_GROUP_SIZE"
  val OpenclMaxWorkGroupSizeDefault: Long = 64L
  val OpenclScanBufferPointers: String = "GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS"
  val Profile: String = "GEMSTONE_PROFILE"
  val Record: String = "GEMSTONE_RECORD"
  val RpcClient: String = "GEMSTONE_RPC_CLIENT"
  val RpcEndpoint: String = "GEMSTONE_RPC_ENDPOINT"
  val RpcTimeoutSeconds: String = "GEMSTONE_RPC_TIMEOUT_SECONDS"
  val RpcTimeoutSecondsDefault: Long = 300L
  val SystemPaging: String = "GEMSTONE_SYSTEM_PAGING"
  val TargetArch: String = "GEMSTONE_TARGET_ARCH"
  val TestLibrary: String = "GEMSTONE_TEST_LIBRARY"
  val Trace: String = "GEMSTONE_TRACE"
  val VulkanDump: String = "GEMSTONE_VULKAN_DUMP"
}
