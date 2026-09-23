#include "gemstone/hsa/aql_execution.h"

#include <algorithm>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <mutex>
#include <string>
#include <unordered_map>
#include <unordered_set>
#include <utility>
#include <vector>

#include <stdint.h>

#include <aspartame/unordered_map.hpp>
#include <aspartame/vector.hpp>

#include "gemstone/hsa/trace.hpp"
#include <gemstone/bytes.hpp>

namespace {

using namespace aspartame;
using gemstone::readNative;

constexpr uint64_t AllocationBase = UINT64_C(0x400000000000);
constexpr uint64_t AllocationSpan = UINT64_C(0x000100000000);

struct KernelBinding {
  uint64_t program_id = 0;
  std::string name;
  uint32_t kernarg_size = 0;
  uint32_t explicit_argument_count = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

struct AllocationBinding {
  uint64_t size = 0;
  uint64_t id = 0;
};

bool validExecutor(const gemstone_hsa_executor &executor) {
  return executor.load_program && executor.unload_program && executor.launch && executor.await_completion
         && executor.completion_dirty_ranges && executor.release_completion;
}

bool validSignals(const gemstone_hsa_signal_operations &signals) {
  return signals.load_acquire && signals.complete_release && signals.fail_release;
}

uint32_t divideRoundingUp(uint32_t value, uint32_t divisor) { return value / divisor + static_cast<uint32_t>(value % divisor != 0); }

} // namespace

struct gemstone_hsa_aql_runtime {
  gemstone_hsa_executor executor{};
  gemstone_hsa_signal_operations signals{};
  mutable std::mutex lock;
  std::unordered_map<uint64_t, KernelBinding> kernels;
  std::unordered_map<uint64_t, uint32_t> program_references;
  std::unordered_map<uint64_t, std::vector<uint64_t>> program_globals;
  std::unordered_map<uint64_t, AllocationBinding> allocations;
  std::vector<gemstone_hsa_dirty_range> dirty_ranges;
  std::string error;
};

namespace {

void setError(gemstone_hsa_aql_runtime &runtime, std::string message) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  runtime.error = std::move(message);
}

std::string executorError(const gemstone_hsa_aql_runtime &runtime) {
  if (!runtime.executor.error) return "execution service request failed";
  const char *message = runtime.executor.error(runtime.executor.context);
  return !message || message[0] == '\0' ? "execution service request failed" : message;
}

std::string kernelError(const KernelBinding &binding, std::string message) { return binding.name + ": " + std::move(message); }

bool copyFields(uint32_t kernarg_size, const gemstone_hsa_kernarg_field *fields, uint32_t field_count,
                std::vector<gemstone_hsa_kernarg_field> &copied) {
  if (field_count != 0 && !fields) return false;
  copied.clear();
  copied.reserve(field_count);
  for (uint32_t index = 0; index < field_count; ++index) {
    const auto &field = fields[index];
    const uint64_t end = static_cast<uint64_t>(field.offset) + field.size;
    if ((field.kind != GEMSTONE_HSA_ARGUMENT_SCALAR && field.kind != GEMSTONE_HSA_ARGUMENT_ALLOCATION) || field.contains_pointers > 1
        || field.size == 0 || end > kernarg_size || (field.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION && field.size != sizeof(uint64_t)))
      return false;
    copied.push_back(field);
  }
  copied ^= sort_by([](const auto &field) { return field.offset; });
  for (size_t index = 1; index < copied.size(); ++index) {
    const auto &previous = copied[index - 1];
    if (static_cast<uint64_t>(previous.offset) + previous.size > copied[index].offset) return false;
  }
  return true;
}

gemstone_hsa_packet_result failPacket(gemstone_hsa_aql_runtime &runtime, uint64_t completion_signal, int32_t error_code,
                                      std::string message) {
  setError(runtime, std::move(message));
  if (gemstone::hsa::traceEnabled("packets"))
    std::fprintf(stderr, "Gemstone AQL dispatch failure: %s\n", gemstone_hsa_aql_runtime_error(&runtime));
  if (completion_signal == 0) return GEMSTONE_HSA_PACKET_FATAL;
  return runtime.signals.fail_release(runtime.signals.context, completion_signal, error_code) == 0 ? GEMSTONE_HSA_PACKET_ACCEPTED
                                                                                                   : GEMSTONE_HSA_PACKET_FATAL;
}

enum class BarrierState { Ready, Retry, Error };

BarrierState barrierState(gemstone_hsa_aql_runtime &runtime, const gemstone_hsa_aql_barrier &barrier) {
  const bool wait_for_any = barrier.header.type == GEMSTONE_HSA_AQL_BARRIER_OR;
  bool any_satisfied = false;
  bool all_satisfied = true;
  bool has_dependency = false;
  for (uint64_t signal : barrier.dependency_signals) {
    if (signal == 0) {
      if (wait_for_any) all_satisfied = false;
      continue;
    }
    has_dependency = true;
    int64_t value = 0;
    if (runtime.signals.load_acquire(runtime.signals.context, signal, &value) != 0) return BarrierState::Error;
    const bool satisfied = value < 1;
    any_satisfied = any_satisfied || satisfied;
    all_satisfied = all_satisfied && satisfied;
  }
  const bool ready = wait_for_any ? has_dependency && any_satisfied : all_satisfied;
  return ready ? BarrierState::Ready : BarrierState::Retry;
}

bool resolveAllocation(gemstone_hsa_aql_runtime &runtime, uint64_t pointer, gemstone_hsa_argument &argument) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  // Prefer an allocation that contains the address, in particular an exact
  // base, before considering a one-past endpoint of another allocation.
  for (const auto &[base, allocation] : runtime.allocations) {
    if (pointer >= base && pointer - base < allocation.size) {
      argument = {};
      argument.kind = GEMSTONE_HSA_ARGUMENT_ALLOCATION;
      argument.allocation_id = allocation.id;
      argument.allocation_offset = pointer - base;
      return true;
    }
  }
  // C/C++ kernels routinely carry one-past pointers as loop bounds. They are
  // valid values for relocation even though dereferencing them remains out of
  // range. The shared execution service likewise admits offset == size.
  for (const auto &[base, allocation] : runtime.allocations) {
    if (pointer >= base && pointer - base == allocation.size) {
      argument = {};
      argument.kind = GEMSTONE_HSA_ARGUMENT_ALLOCATION;
      argument.allocation_id = allocation.id;
      argument.allocation_offset = allocation.size;
      return true;
    }
  }
  return false;
}

bool resolveArguments(gemstone_hsa_aql_runtime &runtime, const KernelBinding &binding, uint64_t kernarg_address,
                      std::vector<gemstone_hsa_argument> &arguments) {
  if (binding.kernarg_size != 0 && kernarg_address == 0) {
    return false;
  }
  if (binding.kernarg_size != 0) {
    bool readable = false;
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &[base, allocation] : runtime.allocations) {
      if (kernarg_address >= base && kernarg_address - base <= allocation.size
          && binding.kernarg_size <= allocation.size - (kernarg_address - base)) {
        readable = true;
        break;
      }
    }
    if (!readable) {
      return false;
    }
  }
  const auto *kernarg = reinterpret_cast<const uint8_t *>(static_cast<uintptr_t>(kernarg_address));
  arguments.reserve(binding.fields.size() + 2);
  const auto append_scalar = [&](uint32_t offset, uint32_t size) {
    if (size == 0) return;
    gemstone_hsa_argument scalar{};
    scalar.kind = GEMSTONE_HSA_ARGUMENT_SCALAR;
    scalar.scalar_bytes = kernarg + offset;
    scalar.scalar_size = size;
    arguments.push_back(scalar);
  };
  uint32_t covered = 0;
  for (const auto &field : binding.fields) {
    if (field.offset < covered) return false;
    append_scalar(covered, field.offset - covered);
    if (field.kind == GEMSTONE_HSA_ARGUMENT_SCALAR && field.contains_pointers != 0) {
      // AMDGPU metadata exposes C++ functors as opaque by-value fields. Split
      // naturally aligned pointer words that resolve to registered allocations
      // into typed arguments, retaining all surrounding bytes as scalar data.
      // Concatenating those arguments preserves the exact kernarg layout while
      // giving the execution service enough information to relocate pointers.
      uint32_t scalar_start = 0;
      uint32_t cursor = 0;
      while (cursor + sizeof(uint64_t) <= field.size) {
        if ((field.offset + cursor) % alignof(uint64_t) != 0) {
          ++cursor;
          continue;
        }
        const auto pointer = readNative<uint64_t>(kernarg, field.offset + cursor);
        gemstone_hsa_argument allocation{};
        if (!resolveAllocation(runtime, pointer, allocation)) {
          cursor += sizeof(uint64_t);
          continue;
        }
        append_scalar(field.offset + scalar_start, cursor - scalar_start);
        arguments.push_back(allocation);
        cursor += sizeof(uint64_t);
        scalar_start = cursor;
      }
      append_scalar(field.offset + scalar_start, field.size - scalar_start);
    } else if (field.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION) {
      const auto pointer = readNative<uint64_t>(kernarg, field.offset);
      // LLVM's OpenMP AMDGPU plugin prepends a kernel-launch-environment
      // pointer to every target kernel.  For kernels without reductions it
      // deliberately uses ~0 as the sentinel instead of allocating an
      // environment object.  This field is part of the device ABI. Preserve
      // its eight bytes as a scalar so the execution service packs the
      // following arguments at their ABI offsets, but never resolve it as a
      // user allocation.
      if (field.offset == 0 && pointer == UINT64_MAX) {
        append_scalar(field.offset, field.size);
        covered = field.offset + field.size;
        continue;
      }
      // A global-buffer argument may legally be null (for example, an
      // optional output or an empty range). Preserve its zero bytes instead
      // of trying to manufacture an allocation identity for the sentinel.
      if (pointer == 0) {
        append_scalar(field.offset, field.size);
        covered = field.offset + field.size;
        continue;
      }
      gemstone_hsa_argument allocation{};
      if (!resolveAllocation(runtime, pointer, allocation)) return false;
      arguments.push_back(allocation);
    } else {
      append_scalar(field.offset, field.size);
    }
    covered = field.offset + field.size;
  }
  append_scalar(covered, binding.kernarg_size - covered);
  return true;
}

bool copyKernelBindings(const gemstone_hsa_kernel_binding *bindings, uint32_t binding_count,
                        std::vector<std::pair<uint64_t, KernelBinding>> &copied) {
  if (!bindings || binding_count == 0) return false;
  copied.clear();
  copied.reserve(binding_count);
  std::unordered_set<uint64_t> objects;
  objects.reserve(binding_count);
  for (uint32_t index = 0; index < binding_count; ++index) {
    const auto &input = bindings[index];
    if (input.kernel_object == 0 || !input.kernel_name || input.kernel_name[0] == '\0' || input.explicit_argument_count > input.field_count
        || !objects.insert(input.kernel_object).second)
      return false;
    std::vector<gemstone_hsa_kernarg_field> fields;
    if (!copyFields(input.kernarg_size, input.fields, input.field_count, fields)) return false;
    copied.emplace_back(input.kernel_object,
                        KernelBinding{0, input.kernel_name, input.kernarg_size, input.explicit_argument_count, std::move(fields)});
  }
  return true;
}

int installLoadedProgram(gemstone_hsa_aql_runtime &runtime, uint64_t program_id, std::vector<std::pair<uint64_t, KernelBinding>> copied) {
  bool collision = false;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &[object, _] : copied)
      collision = collision || runtime.kernels.count(object) != 0;
  }
  if (collision) {
    runtime.executor.unload_program(runtime.executor.context, program_id);
    return -1;
  }
  if (runtime.executor.describe_kernel) {
    for (auto &[_, binding] : copied) {
      uint32_t field_count = 0;
      uint32_t kernarg_size = 0;
      uint32_t explicit_argument_count = 0;
      if (runtime.executor.describe_kernel(runtime.executor.context, program_id, binding.name.c_str(), &kernarg_size, nullptr, 0,
                                           &field_count, &explicit_argument_count)
          != 0) {
        const auto error = executorError(runtime);
        runtime.executor.unload_program(runtime.executor.context, program_id);
        setError(runtime, error);
        return -1;
      }
      std::vector<gemstone_hsa_kernarg_field> fields(field_count);
      if (runtime.executor.describe_kernel(runtime.executor.context, program_id, binding.name.c_str(), &kernarg_size, fields.data(),
                                           static_cast<uint32_t>(fields.size()), &field_count, &explicit_argument_count)
          != 0) {
        const auto error = executorError(runtime);
        runtime.executor.unload_program(runtime.executor.context, program_id);
        setError(runtime, error);
        return -1;
      }
      if (!copyFields(kernarg_size, fields.data(), field_count, binding.fields)) {
        runtime.executor.unload_program(runtime.executor.context, program_id);
        setError(runtime, "execution service returned invalid AMDGPU kernel metadata");
        return -1;
      }
      binding.kernarg_size = kernarg_size;
      binding.explicit_argument_count = explicit_argument_count;
    }
  }
  bool installed = true;
  {
    std::lock_guard<std::mutex> guard(runtime.lock);
    for (const auto &[object, _] : copied)
      installed = installed && runtime.kernels.count(object) == 0;
    if (installed) {
      for (auto &[object, binding] : copied) {
        binding.program_id = program_id;
        runtime.kernels.emplace(object, std::move(binding));
      }
      runtime.program_references.emplace(program_id, static_cast<uint32_t>(copied.size()));
    }
  }
  if (!installed) runtime.executor.unload_program(runtime.executor.context, program_id);
  return installed ? 0 : -1;
}

} // namespace

extern "C" gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create(const gemstone_hsa_executor *executor,
                                                                     const gemstone_hsa_signal_operations *signals) {
  if (!executor || !signals || !validExecutor(*executor) || !validSignals(*signals)) return nullptr;
  auto *runtime = new (std::nothrow) gemstone_hsa_aql_runtime();
  if (!runtime) return nullptr;
  runtime->executor = *executor;
  runtime->signals = *signals;
  return runtime;
}

extern "C" void gemstone_hsa_aql_runtime_destroy(gemstone_hsa_aql_runtime *runtime) {
  if (!runtime) return;
  std::unordered_set<uint64_t> programs;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    programs = runtime->program_references ^ keys();
  }
  for (uint64_t program : programs)
    runtime->executor.unload_program(runtime->executor.context, program);
  if (runtime->executor.destroy) runtime->executor.destroy(runtime->executor.context);
  delete runtime;
}

extern "C" const char *gemstone_hsa_aql_runtime_error(const gemstone_hsa_aql_runtime *runtime) {
  if (!runtime) return "AQL runtime is unavailable";
  thread_local std::string snapshot;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    snapshot = runtime->error;
  }
  return snapshot.c_str();
}

extern "C" int gemstone_hsa_aql_runtime_install_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const void *code_object,
                                                       uint32_t code_object_size, const char *kernel_name, uint32_t kernarg_size,
                                                       const gemstone_hsa_kernarg_field *fields, uint32_t field_count) {
  const gemstone_hsa_kernel_binding binding{kernel_object, kernel_name, kernarg_size, fields, field_count, field_count};
  return gemstone_hsa_aql_runtime_install_code_object(runtime, code_object, code_object_size, &binding, 1);
}

extern "C" int gemstone_hsa_aql_runtime_install_code_object(gemstone_hsa_aql_runtime *runtime, const void *code_object,
                                                            uint32_t code_object_size, const gemstone_hsa_kernel_binding *bindings,
                                                            uint32_t binding_count) {
  if (!runtime || !code_object || code_object_size == 0 || !bindings || binding_count == 0) return -1;
  std::vector<std::pair<uint64_t, KernelBinding>> copied;
  if (!copyKernelBindings(bindings, binding_count, copied)) return -1;
  uint64_t program_id = 0;
  if (runtime->executor.load_program(runtime->executor.context, code_object, code_object_size, &program_id) != 0 || program_id == 0) {
    setError(*runtime, executorError(*runtime));
    return -1;
  }
  return installLoadedProgram(*runtime, program_id, std::move(copied));
}

extern "C" int gemstone_hsa_aql_runtime_install_loaded_program(gemstone_hsa_aql_runtime *runtime, uint64_t program_id,
                                                               const gemstone_hsa_kernel_binding *bindings, uint32_t binding_count) {
  if (!runtime || program_id == 0) return -1;
  std::vector<std::pair<uint64_t, KernelBinding>> copied;
  if (!copyKernelBindings(bindings, binding_count, copied)) {
    runtime->executor.unload_program(runtime->executor.context, program_id);
    return -1;
  }
  return installLoadedProgram(*runtime, program_id, std::move(copied));
}

extern "C" int gemstone_hsa_aql_runtime_describe_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, uint32_t *kernarg_size,
                                                        gemstone_hsa_kernarg_field *fields, uint32_t capacity, uint32_t *field_count,
                                                        uint32_t *explicit_argument_count) {
  if (!runtime || kernel_object == 0 || !kernarg_size || !field_count || !explicit_argument_count || (capacity != 0 && !fields)) return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  const auto found = runtime->kernels.find(kernel_object);
  if (found == runtime->kernels.end() || found->second.fields.size() > std::numeric_limits<uint32_t>::max()) return -1;
  const auto &binding = found->second;
  const auto required = static_cast<uint32_t>(binding.fields.size());
  *kernarg_size = binding.kernarg_size;
  *field_count = required;
  *explicit_argument_count = binding.explicit_argument_count;
  if (capacity == 0) return 0;
  if (capacity < required) return -1;
  std::copy(binding.fields.begin(), binding.fields.end(), fields);
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_uses_server_kernel_metadata(const gemstone_hsa_aql_runtime *runtime) {
  return runtime && runtime->executor.describe_kernel ? 1 : 0;
}

extern "C" int gemstone_hsa_aql_runtime_remove_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object) {
  if (!runtime || kernel_object == 0) return -1;
  uint64_t program_id = 0;
  bool unload = false;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto found = runtime->kernels.find(kernel_object);
    if (found == runtime->kernels.end()) return -1;
    program_id = found->second.program_id;
    runtime->kernels.erase(found);
    const auto references = runtime->program_references.find(program_id);
    if (references == runtime->program_references.end() || references->second == 0) return -1;
    --references->second;
    if (references->second == 0) {
      runtime->program_references.erase(references);
      runtime->program_globals.erase(program_id);
      unload = true;
    }
  }
  return !unload || runtime->executor.unload_program(runtime->executor.context, program_id) == 0 ? 0 : -1;
}

extern "C" int gemstone_hsa_aql_runtime_bind_global(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const char *name,
                                                    uint64_t allocation_id, uint64_t offset) {
  if (!runtime || !name || name[0] == '\0' || allocation_id == 0) return -1;
  uint64_t program_id = 0;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto found = runtime->kernels.find(kernel_object);
    if (found != runtime->kernels.end()) program_id = found->second.program_id;
  }
  if (program_id == 0) {
    setError(*runtime, "global belongs to an unknown kernel object");
    return -1;
  }
  if (!runtime->executor.bind_global) {
    setError(*runtime, "execution backend does not support global binding");
    return -1;
  }
  if (runtime->executor.bind_global(runtime->executor.context, program_id, name, allocation_id, offset) != 0) {
    setError(*runtime, executorError(*runtime));
    return -1;
  }
  std::lock_guard<std::mutex> guard(runtime->lock);
  auto &ids = runtime->program_globals[program_id];
  if (!(ids ^ contains(allocation_id))) ids.push_back(allocation_id);
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_write_global(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object, const char *name,
                                                     uint64_t offset, const void *bytes, uint32_t size) {
  if (!runtime || kernel_object == 0 || !name || name[0] == '\0' || (size != 0 && !bytes)) return -1;
  uint64_t program_id = 0;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto found = runtime->kernels.find(kernel_object);
    if (found != runtime->kernels.end()) program_id = found->second.program_id;
  }
  if (program_id == 0) {
    setError(*runtime, "program global belongs to an unknown kernel object");
    return -1;
  }
  if (!runtime->executor.write_global) {
    setError(*runtime, "execution backend does not support program globals");
    return -1;
  }
  std::vector<uint8_t> relocated(size);
  if (size != 0) std::memcpy(relocated.data(), bytes, size);
  // HIP functors copied into constant memory contain ordinary host-process
  // device pointers. Convert any registered pointer-sized word to the same
  // stable virtual address encoding used by AMDGPU kernarg packing before the
  // bytes cross the process boundary. Only exact registered ranges are
  // rewritten, so scalar data remains byte-for-byte unchanged.
  for (uint32_t position = 0; position + sizeof(uint64_t) <= size; ++position) {
    const auto pointer = readNative<uint64_t>(relocated.data(), position);
    gemstone_hsa_argument allocation{};
    if (!resolveAllocation(*runtime, pointer, allocation)) continue;
    if (allocation.allocation_id == 0 || allocation.allocation_id - 1 > (UINT64_MAX - AllocationBase) / AllocationSpan) continue;
    const uint64_t virtual_pointer = AllocationBase + (allocation.allocation_id - 1) * AllocationSpan + allocation.allocation_offset;
    std::memcpy(relocated.data() + position, &virtual_pointer, sizeof(virtual_pointer));
    position += sizeof(uint64_t) - 1;
  }
  const void *write_bytes = size == 0 ? bytes : relocated.data();
  const int result = runtime->executor.write_global(runtime->executor.context, program_id, name, offset, write_bytes, size);
  if (result != 0) {
    setError(*runtime, executorError(*runtime));
    return -1;
  }
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_register_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base, uint64_t size,
                                                            uint64_t allocation_id) {
  if (!runtime || native_base == 0 || size == 0 || allocation_id == 0 || native_base > std::numeric_limits<uint64_t>::max() - size)
    return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  if (runtime->allocations ^ exists([&](uint64_t base, const AllocationBinding &allocation) {
        return native_base < base + allocation.size && base < native_base + size;
      }))
    return -1;
  runtime->allocations.emplace(native_base, AllocationBinding{size, allocation_id});
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_unregister_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base) {
  if (!runtime || native_base == 0) return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  const auto removed = runtime->allocations.erase(native_base) == 1;
  return removed ? 0 : -1;
}

extern "C" int gemstone_hsa_aql_runtime_dispatch_allocations(gemstone_hsa_aql_runtime *runtime, const void *packet, size_t packet_size,
                                                             uint64_t *allocation_ids, uint32_t capacity, uint32_t *count) {
  if (!runtime) return -1;
  const auto reject = [&](std::string message) {
    setError(*runtime, std::move(message));
    return -1;
  };
  if (!packet || !count || (capacity != 0 && !allocation_ids)) return reject("invalid dispatch allocation query");
  gemstone_hsa_aql_packet decoded{};
  const auto decode = gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded);
  if (decode != GEMSTONE_HSA_AQL_DECODE_SUCCESS) {
    return reject(std::string("cannot decode dispatch packet: ") + gemstone_hsa_aql_decode_error_string(decode));
  }
  if (decoded.type != GEMSTONE_HSA_AQL_KERNEL_DISPATCH) {
    *count = 0;
    return 0;
  }

  const auto &dispatch = decoded.value.kernel_dispatch;
  KernelBinding binding;
  bool found_binding = false;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto found = runtime->kernels.find(dispatch.kernel_object);
    if (found != runtime->kernels.end()) {
      binding = found->second;
      found_binding = true;
    }
  }
  if (!found_binding) return reject("kernel object is not installed: 0x" + std::to_string(dispatch.kernel_object));
  std::vector<gemstone_hsa_argument> arguments;
  if (!resolveArguments(*runtime, binding, dispatch.kernarg_address, arguments)) {
    return reject(kernelError(binding, "kernarg buffer or allocation pointer is invalid"));
  }

  std::vector<uint64_t> ids;
  ids.reserve(arguments.size() + 1);
  const auto append = [&](uint64_t id) {
    if (!(ids ^ contains(id))) ids.push_back(id);
  };
  if (binding.kernarg_size != 0) {
    gemstone_hsa_argument kernarg{};
    if (!resolveAllocation(*runtime, dispatch.kernarg_address, kernarg)) {
      return reject(kernelError(binding, "kernarg allocation pointer is not registered"));
    }
    append(kernarg.allocation_id);
  }
  for (const auto &argument : arguments)
    if (argument.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION) append(argument.allocation_id);

  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto globals = runtime->program_globals.find(binding.program_id);
    if (globals != runtime->program_globals.end())
      for (auto id : globals->second)
        append(id);
  }
  if (ids.size() > std::numeric_limits<uint32_t>::max()) return reject(kernelError(binding, "too many allocation arguments"));
  const auto required = static_cast<uint32_t>(ids.size());
  *count = required;
  if (capacity == 0) return 0;
  if (capacity < required) {
    return reject(kernelError(binding, "allocation query capacity is too small"));
  }
  std::copy(ids.begin(), ids.end(), allocation_ids);
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_take_dirty_ranges(gemstone_hsa_aql_runtime *runtime, gemstone_hsa_dirty_range *ranges,
                                                          uint32_t capacity, uint32_t *count) {
  if (!runtime || !count || (capacity != 0 && !ranges)) return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  if (runtime->dirty_ranges.size() > std::numeric_limits<uint32_t>::max()) return -1;
  const auto required = static_cast<uint32_t>(runtime->dirty_ranges.size());
  *count = required;
  if (capacity == 0) return 0;
  if (capacity < required) return -1;
  std::copy(runtime->dirty_ranges.begin(), runtime->dirty_ranges.end(), ranges);
  runtime->dirty_ranges.clear();
  return 0;
}

extern "C" gemstone_hsa_packet_result gemstone_hsa_aql_runtime_consume(uint64_t, uint64_t, const void *packet, size_t packet_size,
                                                                       void *opaque_runtime) {
  auto *runtime = static_cast<gemstone_hsa_aql_runtime *>(opaque_runtime);
  if (!runtime) return GEMSTONE_HSA_PACKET_FATAL;
  gemstone_hsa_aql_packet decoded{};
  const auto decode = gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded);
  if (decode != GEMSTONE_HSA_AQL_DECODE_SUCCESS) {
    const std::string message = gemstone_hsa_aql_decode_error_string(decode);
    if (gemstone::hsa::traceEnabled("packets")) std::fprintf(stderr, "Gemstone rejected AQL packet: %s\n", message.c_str());
    uint64_t completion_signal = 0;
    if (decoded.type == GEMSTONE_HSA_AQL_KERNEL_DISPATCH) completion_signal = decoded.value.kernel_dispatch.completion_signal;
    else if (decoded.type == GEMSTONE_HSA_AQL_BARRIER_AND || decoded.type == GEMSTONE_HSA_AQL_BARRIER_OR)
      completion_signal = decoded.value.barrier.completion_signal;
    else if (decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) completion_signal = decoded.value.pm4_ib.completion_signal;
    if (completion_signal != 0) return failPacket(*runtime, completion_signal, -6, message);
    setError(*runtime, message);
    return GEMSTONE_HSA_PACKET_FATAL;
  }
  const bool trace_packets = gemstone::hsa::traceEnabled("packets");

  if (decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) {
    const auto &pm4 = decoded.value.pm4_ib;
    if (trace_packets) std::fprintf(stderr, "Gemstone observed AQL PM4-IB synchronization packet completion=%lu\n", pm4.completion_signal);
    if (pm4.completion_signal != 0 && runtime->signals.complete_release(runtime->signals.context, pm4.completion_signal) != 0)
      return GEMSTONE_HSA_PACKET_FATAL;
    return GEMSTONE_HSA_PACKET_ACCEPTED;
  }

  if (decoded.type == GEMSTONE_HSA_AQL_BARRIER_AND || decoded.type == GEMSTONE_HSA_AQL_BARRIER_OR) {
    const auto &barrier = decoded.value.barrier;
    const auto state = barrierState(*runtime, barrier);
    if (trace_packets) {
      const char *state_name = state == BarrierState::Ready ? "ready" : state == BarrierState::Retry ? "retry" : "error";
      std::fprintf(stderr, "Gemstone observed AQL barrier=%s state=%s completion=%lu dependencies=",
                   decoded.type == GEMSTONE_HSA_AQL_BARRIER_AND ? "and" : "or", state_name, barrier.completion_signal);
      for (uint64_t signal : barrier.dependency_signals) {
        if (signal == 0) continue;
        int64_t value = 0;
        const int loaded = runtime->signals.load_acquire(runtime->signals.context, signal, &value);
        std::fprintf(stderr, "%lu:%s%ld ", signal, loaded == 0 ? "" : "error/", value);
      }
      std::fprintf(stderr, "\n");
    }
    if (state == BarrierState::Retry) return GEMSTONE_HSA_PACKET_RETRY;
    if (state == BarrierState::Error) return failPacket(*runtime, barrier.completion_signal, -4, "cannot read barrier dependency signal");
    if (barrier.completion_signal != 0 && runtime->signals.complete_release(runtime->signals.context, barrier.completion_signal) != 0)
      return GEMSTONE_HSA_PACKET_FATAL;
    return GEMSTONE_HSA_PACKET_ACCEPTED;
  }

  const auto &dispatch = decoded.value.kernel_dispatch;
  KernelBinding binding;
  bool found_binding = false;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    const auto found = runtime->kernels.find(dispatch.kernel_object);
    if (found != runtime->kernels.end()) {
      binding = found->second;
      found_binding = true;
    }
  }
  if (!found_binding) return failPacket(*runtime, dispatch.completion_signal, -2, "kernel object is not installed");

  std::vector<gemstone_hsa_argument> arguments;
  if (!resolveArguments(*runtime, binding, dispatch.kernarg_address, arguments))
    return failPacket(*runtime, dispatch.completion_signal, -5, "kernarg buffer or allocation pointer is invalid");

  const gemstone_hsa_dimensions group_size{dispatch.workgroup_size_x, dispatch.workgroup_size_y, dispatch.workgroup_size_z};
  const gemstone_hsa_dimensions group_count{divideRoundingUp(dispatch.grid_size_x, dispatch.workgroup_size_x),
                                            divideRoundingUp(dispatch.grid_size_y, dispatch.workgroup_size_y),
                                            divideRoundingUp(dispatch.grid_size_z, dispatch.workgroup_size_z)};
  if (trace_packets)
    std::fprintf(stderr, "Gemstone launching AQL kernel=%s grid=%ux%ux%u group=%ux%ux%u arguments=%zu\n", binding.name.c_str(),
                 dispatch.grid_size_x, dispatch.grid_size_y, dispatch.grid_size_z, group_size.x, group_size.y, group_size.z,
                 arguments.size());
  uint64_t completion_id = 0;
  if (runtime->executor.launch(runtime->executor.context, binding.program_id, binding.name.c_str(), group_size, group_count,
                               gemstone_hsa_dimensions{dispatch.grid_size_x, dispatch.grid_size_y, dispatch.grid_size_z},
                               dispatch.private_segment_size, dispatch.group_segment_size, arguments.data(),
                               static_cast<uint32_t>(arguments.size()), &completion_id)
          != 0
      || completion_id == 0)
    return failPacket(*runtime, dispatch.completion_signal, -3, kernelError(binding, executorError(*runtime)));
  const int awaited = runtime->executor.await_completion(runtime->executor.context, completion_id);
  // Releasing the remote completion is a separate RPC exchange.  It may clear
  // or replace the client's error buffer, so preserve the execution failure
  // before performing the mandatory release.
  const std::string await_error = awaited == 0 ? std::string{} : executorError(*runtime);
  std::vector<gemstone_hsa_dirty_range> dirty_ranges;
  int queried = 0;
  std::string query_error;
  if (awaited == 0) {
    uint32_t dirty_count = 0;
    queried = runtime->executor.completion_dirty_ranges(runtime->executor.context, completion_id, nullptr, 0, &dirty_count);
    if (queried == 0 && dirty_count != 0) {
      dirty_ranges.resize(dirty_count);
      uint32_t returned_count = dirty_count;
      queried = runtime->executor.completion_dirty_ranges(runtime->executor.context, completion_id, dirty_ranges.data(), dirty_count,
                                                          &returned_count);
      if (queried == 0 && returned_count != dirty_count) queried = -1;
    }
    if (queried == 0) {
      for (const auto &range : dirty_ranges) {
        const bool invalid =
            range.allocation_id == 0 || range.size == 0 || range.offset > std::numeric_limits<uint64_t>::max() - range.size;
        if (invalid) {
          queried = -1;
          break;
        }
      }
    }
    if (queried != 0) query_error = executorError(*runtime);
  }
  const int released = runtime->executor.release_completion(runtime->executor.context, completion_id);
  if (trace_packets)
    std::fprintf(stderr, "Gemstone completed AQL kernel=%s result=%d release=%d\n", binding.name.c_str(), awaited, released);
  if (awaited != 0) return failPacket(*runtime, dispatch.completion_signal, -3, kernelError(binding, await_error));
  if (queried != 0) return failPacket(*runtime, dispatch.completion_signal, -3, kernelError(binding, query_error));
  if (released != 0) return failPacket(*runtime, dispatch.completion_signal, -3, kernelError(binding, executorError(*runtime)));
  if (!dirty_ranges.empty()) {
    std::lock_guard<std::mutex> guard(runtime->lock);
    runtime->dirty_ranges.insert(runtime->dirty_ranges.end(), dirty_ranges.begin(), dirty_ranges.end());
  }
  if (dispatch.completion_signal != 0 && runtime->signals.complete_release(runtime->signals.context, dispatch.completion_signal) != 0)
    return GEMSTONE_HSA_PACKET_FATAL;
  return GEMSTONE_HSA_PACKET_ACCEPTED;
}

namespace {

int rocrLoad(void *, uint64_t signal, int64_t *value) {
  if (signal == 0 || !value) return -1;
  const auto *address = reinterpret_cast<const int64_t *>(static_cast<uintptr_t>(signal) + 8);
  *value = __atomic_load_n(address, __ATOMIC_ACQUIRE);
  return 0;
}

int rocrComplete(void *, uint64_t signal) {
  if (signal == 0) return -1;
  auto *address = reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8);
  __atomic_fetch_sub(address, INT64_C(1), __ATOMIC_RELEASE);
  return 0;
}

int rocrFail(void *, uint64_t signal, int32_t error_code) {
  if (signal == 0) return -1;
  auto *address = reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8);
  const int64_t value = error_code < 0 ? error_code : -static_cast<int64_t>(error_code) - 1;
  __atomic_store_n(address, value, __ATOMIC_RELEASE);
  return 0;
}

} // namespace

extern "C" gemstone_hsa_signal_operations gemstone_hsa_rocr_signal_operations() {
  return gemstone_hsa_signal_operations{nullptr, rocrLoad, rocrComplete, rocrFail};
}
