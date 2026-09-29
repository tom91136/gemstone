// Generated from build.sbt: environmentVariables. Regenerate with: sbt codegen
#pragma once

#include <cstdint>

namespace gemstone::environment {
inline constexpr char BenchmarkJfr[] = "GEMSTONE_BENCHMARK_JFR";
inline constexpr char ExecutionMaxInstructions[] = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS";
inline constexpr char ExecutionParallelism[] = "GEMSTONE_EXECUTION_PARALLELISM";
inline constexpr char HsaPagingTimeoutSeconds[] = "GEMSTONE_HSA_PAGING_TIMEOUT_SECONDS";
inline constexpr char HsaRejectedCodeObject[] = "GEMSTONE_HSA_REJECTED_CODE_OBJECT";
inline constexpr char MemoryAllocationKind[] = "GEMSTONE_MEMORY_ALLOCATION_KIND";
inline constexpr char MemoryAtomicBits[] = "GEMSTONE_MEMORY_ATOMIC_BITS";
inline constexpr char MemoryConcurrencyDirectory[] = "GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY";
inline constexpr char MemoryProbe[] = "GEMSTONE_MEMORY_PROBE";
inline constexpr char NativeDirectory[] = "GEMSTONE_NATIVE_DIRECTORY";
inline constexpr char Offline[] = "GEMSTONE_OFFLINE";
inline constexpr char OpenclCompiler[] = "GEMSTONE_OPENCL_COMPILER";
inline constexpr char OpenclLlvmLink[] = "GEMSTONE_OPENCL_LLVM_LINK";
inline constexpr char OpenclLlvmSpirv[] = "GEMSTONE_OPENCL_LLVM_SPIRV";
inline constexpr char OpenclLocalMemSize[] = "GEMSTONE_OPENCL_LOCAL_MEM_SIZE";
inline constexpr uint64_t OpenclLocalMemSizeDefault = 65536;
inline constexpr char OpenclMaxMemAllocSize[] = "GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE";
inline constexpr uint64_t OpenclMaxMemAllocSizeDefault = 268435456;
inline constexpr char OpenclMaxWorkGroupSize[] = "GEMSTONE_OPENCL_MAX_WORK_GROUP_SIZE";
inline constexpr uint64_t OpenclMaxWorkGroupSizeDefault = 64;
inline constexpr char OpenclScanBufferPointers[] = "GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS";
inline constexpr char Profile[] = "GEMSTONE_PROFILE";
inline constexpr char Record[] = "GEMSTONE_RECORD";
inline constexpr char RpcClient[] = "GEMSTONE_RPC_CLIENT";
inline constexpr char RpcEndpoint[] = "GEMSTONE_RPC_ENDPOINT";
inline constexpr char RpcTimeoutSeconds[] = "GEMSTONE_RPC_TIMEOUT_SECONDS";
inline constexpr uint64_t RpcTimeoutSecondsDefault = 300;
inline constexpr char SystemPaging[] = "GEMSTONE_SYSTEM_PAGING";
inline constexpr char TargetArch[] = "GEMSTONE_TARGET_ARCH";
inline constexpr char TestLibrary[] = "GEMSTONE_TEST_LIBRARY";
inline constexpr char Trace[] = "GEMSTONE_TRACE";
inline constexpr char VulkanDump[] = "GEMSTONE_VULKAN_DUMP";
} // namespace gemstone::environment
