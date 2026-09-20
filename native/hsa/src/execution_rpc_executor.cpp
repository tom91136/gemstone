#include <cstdio>
#include <cstring>
#include <memory>
#include <new>
#include <string>
#include <vector>

#include <stdint.h>

#include "gemstone/hsa/aql_execution.h"
#include "gemstone/hsa/trace.hpp"
#include "gemstone_execution_rpc.hpp"

namespace {

using namespace gemstone::execution;

struct EnvironmentExecutor {
  std::unique_ptr<Client> client;
  std::string error;
};

template <class T> int accept(EnvironmentExecutor &executor, Result<T> result, T *output = nullptr) {
  if (!result) {
    executor.error = result.error().detail;
    return -1;
  }
  executor.error.clear();
  if (output) *output = std::move(result.value());
  return 0;
}

int loadProgram(void *context, const void *image, uint32_t image_size, uint64_t *program_id) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!program_id || (!image && image_size != 0)) return -1;
  if (gemstone::hsa::traceEnabled("packets")) std::fprintf(stderr, "Gemstone loading HSA code object bytes=%u\n", image_size);
  auto result = executor.client->call(
      LoadProgram{ProgramFormat::HSACodeObjectBinary,
                  std::vector<uint8_t>(static_cast<const uint8_t *>(image), static_cast<const uint8_t *>(image) + image_size)});
  ProgramId program{};
  if (accept(executor, std::move(result), &program) != 0) return -1;
  *program_id = static_cast<uint64_t>(program.value);
  return 0;
}

int unloadProgram(void *context, uint64_t program_id) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  return accept(executor, executor.client->call(UnloadProgram{ProgramId{static_cast<int64_t>(program_id)}}));
}

int bindGlobal(void *context, uint64_t program_id, const char *name, uint64_t allocation_id, uint64_t offset) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!name) return -1;
  return accept(executor,
                executor.client->call(BindProgramGlobal{ProgramId{static_cast<int64_t>(program_id)}, name,
                                                        AllocationId{static_cast<int64_t>(allocation_id)}, static_cast<int64_t>(offset)}));
}

int writeGlobal(void *context, uint64_t program_id, const char *name, uint64_t offset, const void *bytes, uint32_t size) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!name || (!bytes && size != 0)) return -1;
  const auto *begin = static_cast<const uint8_t *>(bytes);
  return accept(executor,
                executor.client->call(WriteProgramGlobal{ProgramId{static_cast<int64_t>(program_id)}, name, static_cast<int64_t>(offset),
                                                         std::vector<uint8_t>(begin, begin + size)}));
}

int describeKernel(void *context, uint64_t program_id, const char *name, uint32_t *kernarg_size, gemstone_hsa_kernarg_field *fields,
                   uint32_t capacity, uint32_t *field_count, uint32_t *explicit_argument_count) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!name || !kernarg_size || !field_count || !explicit_argument_count || (capacity != 0 && !fields)) return -1;
  KernelMetadata metadata{};
  if (accept(executor, executor.client->call(DescribeKernel{ProgramId{static_cast<int64_t>(program_id)}, name}), &metadata) != 0) return -1;
  if (metadata.parameters.size() > UINT32_MAX) return -1;
  *kernarg_size = static_cast<uint32_t>(metadata.argumentBlockSize.value);
  *field_count = static_cast<uint32_t>(metadata.parameters.size());
  *explicit_argument_count = static_cast<uint32_t>(metadata.explicitParameterCount.value);
  if (capacity == 0) return 0;
  if (capacity < metadata.parameters.size()) return -1;
  for (size_t index = 0; index < metadata.parameters.size(); ++index) {
    const auto &parameter = metadata.parameters[index];
    const auto kind = parameter.kind == KernelParameterKind::Allocation ? GEMSTONE_HSA_ARGUMENT_ALLOCATION
                      : parameter.kind == KernelParameterKind::Scalar   ? GEMSTONE_HSA_ARGUMENT_SCALAR
                                                                        : gemstone_hsa_argument_kind{};
    if (kind == gemstone_hsa_argument_kind{}) return -1;
    fields[index] = {kind, static_cast<uint32_t>(parameter.offset.value), static_cast<uint32_t>(parameter.width.value)};
  }
  return 0;
}

int launch(void *context, uint64_t program_id, const char *kernel_name, gemstone_hsa_dimensions group_size,
           gemstone_hsa_dimensions group_count, gemstone_hsa_dimensions global_size, uint32_t private_segment_bytes,
           uint32_t group_segment_bytes, const gemstone_hsa_argument *arguments, uint32_t argument_count, uint64_t *completion_id) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!kernel_name || !completion_id || (!arguments && argument_count != 0)) return -1;
  std::vector<KernelArgument> converted;
  converted.reserve(argument_count);
  for (uint32_t index = 0; index < argument_count; ++index) {
    const auto &input = arguments[index];
    if (input.kind == GEMSTONE_HSA_ARGUMENT_SCALAR) {
      if (!input.scalar_bytes && input.scalar_size != 0) return -1;
      converted.emplace_back(KernelArgumentScalar{std::vector<uint8_t>(input.scalar_bytes, input.scalar_bytes + input.scalar_size)});
    } else if (input.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION) {
      converted.emplace_back(
          KernelArgumentAllocation{AllocationId{static_cast<int64_t>(input.allocation_id)}, static_cast<int64_t>(input.allocation_offset)});
    } else return -1;
  }
  CompletionId completion{};
  const auto result = accept(
      executor,
      executor.client->call(Launch{
          ProgramId{static_cast<int64_t>(program_id)},
          kernel_name,
          Dimensions{static_cast<int32_t>(group_size.x), static_cast<int32_t>(group_size.y), static_cast<int32_t>(group_size.z)},
          Dimensions{static_cast<int32_t>(group_count.x), static_cast<int32_t>(group_count.y), static_cast<int32_t>(group_count.z)},
          GlobalDimensions{static_cast<int64_t>(global_size.x), static_cast<int64_t>(global_size.y), static_cast<int64_t>(global_size.z)},
          3,
          Coordinates{},
          static_cast<int32_t>(private_segment_bytes),
          static_cast<int32_t>(group_segment_bytes),
          std::move(converted),
          {}}),
      &completion);
  if (result == 0) *completion_id = static_cast<uint64_t>(completion.value);
  return result;
}

int awaitCompletion(void *context, uint64_t completion_id) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  return accept(executor, executor.client->call(AwaitCompletion{CompletionId{static_cast<int64_t>(completion_id)}}));
}

int completionDirtyRanges(void *context, uint64_t completion_id, gemstone_hsa_dirty_range *ranges, uint32_t capacity, uint32_t *count) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  if (!count || (capacity != 0 && !ranges)) return -1;
  std::vector<DirtyRange> converted;
  if (accept(executor, executor.client->call(CompletionDirtyRanges{CompletionId{static_cast<int64_t>(completion_id)}}), &converted) != 0)
    return -1;
  if (converted.size() > UINT32_MAX) return -1;
  *count = static_cast<uint32_t>(converted.size());
  if (capacity == 0) return 0;
  if (capacity < converted.size()) return -1;
  for (size_t index = 0; index < converted.size(); ++index) {
    const auto &range = converted[index];
    ranges[index] = {static_cast<uint64_t>(range.allocation.value), static_cast<uint64_t>(range.offset.value),
                     static_cast<uint32_t>(range.size.value)};
  }
  return 0;
}

int releaseCompletion(void *context, uint64_t completion_id) {
  auto &executor = *static_cast<EnvironmentExecutor *>(context);
  return accept(executor, executor.client->call(ReleaseCompletion{CompletionId{static_cast<int64_t>(completion_id)}}));
}

const char *error(void *context) { return static_cast<EnvironmentExecutor *>(context)->error.c_str(); }

void destroy(void *context) { delete static_cast<EnvironmentExecutor *>(context); }

} // namespace

extern "C" gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create_from_environment(const gemstone_hsa_signal_operations *signals) {
  if (!signals) return nullptr;
  auto *executor = new (std::nothrow) EnvironmentExecutor();
  if (!executor) return nullptr;
  executor->client = Client::fromEnvironment();
  if (!executor->client) {
    delete executor;
    return nullptr;
  }
  const gemstone_hsa_executor operations{executor,          loadProgram, unloadProgram, launch,      awaitCompletion, completionDirtyRanges,
                                         releaseCompletion, error,       destroy,       writeGlobal, bindGlobal,      describeKernel};
  auto *runtime = gemstone_hsa_aql_runtime_create(&operations, signals);
  if (!runtime) destroy(executor);
  return runtime;
}
