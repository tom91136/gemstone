#pragma once

#include <cstdlib>
#include <string_view>

namespace gemstone::environment {

// The constants below originate from string literals, so their data() is null-terminated and safe for getenv.
inline const char *value(std::string_view name) { return std::getenv(name.data()); }
inline bool present(std::string_view name) { return value(name) != nullptr; }

/** Parses a non-zero unsigned integer override, falling back when absent or invalid. */
inline unsigned long long unsignedValue(std::string_view name, unsigned long long fallback) {
  const char *value = std::getenv(name.data());
  if (!value || value[0] == '\0') return fallback;
  char *end = nullptr;
  const auto parsed = std::strtoull(value, &end, 0);
  return end != value && *end == '\0' && parsed != 0 ? parsed : fallback;
}

inline constexpr std::string_view Trace = "GEMSTONE_TRACE";
inline constexpr std::string_view SystemPaging = "GEMSTONE_SYSTEM_PAGING";
inline constexpr std::string_view TargetArch = "GEMSTONE_TARGET_ARCH";
inline constexpr std::string_view Profile = "GEMSTONE_PROFILE";
inline constexpr std::string_view VulkanDump = "GEMSTONE_VULKAN_DUMP";
inline constexpr std::string_view Record = "GEMSTONE_RECORD";
inline constexpr std::string_view RpcClient = "GEMSTONE_RPC_CLIENT";
inline constexpr std::string_view RpcPort = "GEMSTONE_RPC_PORT";
inline constexpr std::string_view RpcTimeoutSeconds = "GEMSTONE_RPC_TIMEOUT_SECONDS";
inline constexpr std::string_view ExecutionParallelism = "GEMSTONE_EXECUTION_PARALLELISM";
inline constexpr std::string_view ExecutionMaxInstructions = "GEMSTONE_EXECUTION_MAX_INSTRUCTIONS";
inline constexpr std::string_view ExecutionQuantum = "GEMSTONE_EXECUTION_QUANTUM";
inline constexpr std::string_view ExecutionHoldAllocation = "GEMSTONE_EXECUTION_HOLD_ALLOCATION";
inline constexpr std::string_view NativeDirectory = "GEMSTONE_NATIVE_DIRECTORY";
inline constexpr std::string_view NativeTestDirectory = "GEMSTONE_NATIVE_TEST_DIRECTORY";
inline constexpr std::string_view TestLibrary = "GEMSTONE_TEST_LIBRARY";
inline constexpr std::string_view BenchmarkJfr = "GEMSTONE_BENCHMARK_JFR";
inline constexpr std::string_view OpenCLCompiler = "GEMSTONE_OPENCL_COMPILER";
inline constexpr std::string_view OpenCLLlvmSpirv = "GEMSTONE_OPENCL_LLVM_SPIRV";
inline constexpr std::string_view OpenCLLlvmLink = "GEMSTONE_OPENCL_LLVM_LINK";
inline constexpr std::string_view OpenCLScanBufferPointers = "GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS";
inline constexpr std::string_view OpenCLMaxMemAllocSize = "GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE";
inline constexpr std::string_view OpenCLLocalMemSize = "GEMSTONE_OPENCL_LOCAL_MEM_SIZE";
inline constexpr std::string_view OpenCLMaxWorkGroupSize = "GEMSTONE_OPENCL_MAX_WORK_GROUP_SIZE";
inline constexpr std::string_view HsaPagingTimeoutSeconds = "GEMSTONE_HSA_PAGING_TIMEOUT_SECONDS";
inline constexpr std::string_view HsaRejectedCodeObject = "GEMSTONE_HSA_REJECTED_CODE_OBJECT";
inline constexpr std::string_view MemoryConcurrencyDirectory = "GEMSTONE_MEMORY_CONCURRENCY_DIRECTORY";
inline constexpr std::string_view MemoryAllocationKind = "GEMSTONE_MEMORY_ALLOCATION_KIND";
inline constexpr std::string_view MemoryAtomicBits = "GEMSTONE_MEMORY_ATOMIC_BITS";
inline constexpr std::string_view MemoryProbe = "GEMSTONE_MEMORY_PROBE";

} // namespace gemstone::environment
