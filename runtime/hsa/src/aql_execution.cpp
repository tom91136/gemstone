// SPDX-License-Identifier: Apache-2.0
#include "gemstone/hsa/aql_execution.h"

#include <aspartame/unordered_map.hpp>

#include <algorithm>
#include <cstdint>
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

namespace {

using namespace aspartame;

struct KernelBinding {
  uint64_t program_id = 0;
  std::string name;
  uint32_t kernarg_size = 0;
  std::vector<gemstone_hsa_kernarg_field> fields;
};

struct AllocationBinding {
  uint64_t size = 0;
  uint64_t id = 0;
};

bool valid_executor(const gemstone_hsa_executor &executor) {
  return executor.load_program != nullptr && executor.unload_program != nullptr && executor.launch != nullptr
         && executor.await_completion != nullptr && executor.completion_dirty_ranges != nullptr && executor.release_completion != nullptr;
}

bool valid_signals(const gemstone_hsa_signal_operations &signals) {
  return signals.load_acquire != nullptr && signals.complete_release != nullptr && signals.fail_release != nullptr;
}

uint32_t divide_rounding_up(uint32_t value, uint32_t divisor) { return value / divisor + static_cast<uint32_t>(value % divisor != 0); }

} // namespace

struct gemstone_hsa_aql_runtime {
  gemstone_hsa_executor executor{};
  gemstone_hsa_signal_operations signals{};
  mutable std::mutex lock;
  std::unordered_map<uint64_t, KernelBinding> kernels;
  std::unordered_map<uint64_t, uint32_t> program_references;
  std::unordered_map<uint64_t, AllocationBinding> allocations;
  std::vector<gemstone_hsa_dirty_range> dirty_ranges;
  std::string error;
};

namespace {

void set_error(gemstone_hsa_aql_runtime &runtime, std::string message) {
  std::lock_guard<std::mutex> guard(runtime.lock);
  runtime.error = std::move(message);
}

std::string executor_error(const gemstone_hsa_aql_runtime &runtime) {
  if (runtime.executor.error == nullptr) return "execution service request failed";
  const char *message = runtime.executor.error(runtime.executor.context);
  return message == nullptr || message[0] == '\0' ? "execution service request failed" : message;
}

std::string kernel_error(const KernelBinding &binding, std::string message) { return binding.name + ": " + std::move(message); }

bool copy_fields(uint32_t kernarg_size, const gemstone_hsa_kernarg_field *fields, uint32_t field_count,
                 std::vector<gemstone_hsa_kernarg_field> &copied) {
  if (field_count != 0 && fields == nullptr) return false;
  copied.clear();
  copied.reserve(field_count);
  for (uint32_t index = 0; index < field_count; ++index) {
    const auto &field = fields[index];
    const uint64_t end = static_cast<uint64_t>(field.offset) + field.size;
    if ((field.kind != GEMSTONE_HSA_ARGUMENT_SCALAR && field.kind != GEMSTONE_HSA_ARGUMENT_ALLOCATION) || field.size == 0
        || end > kernarg_size || (field.kind == GEMSTONE_HSA_ARGUMENT_ALLOCATION && field.size != sizeof(uint64_t)))
      return false;
    copied.push_back(field);
  }
  std::sort(copied.begin(), copied.end(), [](const auto &left, const auto &right) { return left.offset < right.offset; });
  for (size_t index = 1; index < copied.size(); ++index) {
    const auto &previous = copied[index - 1];
    if (static_cast<uint64_t>(previous.offset) + previous.size > copied[index].offset) return false;
  }
  return true;
}

gemstone_hsa_packet_result fail_packet(gemstone_hsa_aql_runtime &runtime, uint64_t completion_signal, int32_t error_code,
                                       std::string message) {
  set_error(runtime, std::move(message));
  if (completion_signal == 0) return GEMSTONE_HSA_PACKET_FATAL;
  return runtime.signals.fail_release(runtime.signals.context, completion_signal, error_code) == 0 ? GEMSTONE_HSA_PACKET_ACCEPTED
                                                                                                   : GEMSTONE_HSA_PACKET_FATAL;
}

enum class BarrierState { ready, retry, error };

BarrierState barrier_state(gemstone_hsa_aql_runtime &runtime, const gemstone_hsa_aql_barrier &barrier) {
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
    if (runtime.signals.load_acquire(runtime.signals.context, signal, &value) != 0) return BarrierState::error;
    const bool satisfied = value < 1;
    any_satisfied = any_satisfied || satisfied;
    all_satisfied = all_satisfied && satisfied;
  }
  const bool ready = wait_for_any ? has_dependency && any_satisfied : all_satisfied;
  return ready ? BarrierState::ready : BarrierState::retry;
}

bool resolve_allocation(gemstone_hsa_aql_runtime &runtime, uint64_t pointer, gemstone_hsa_argument &argument) {
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

bool resolve_arguments(gemstone_hsa_aql_runtime &runtime, const KernelBinding &binding, uint64_t kernarg_address,
                       std::vector<gemstone_hsa_argument> &arguments) {
  if (binding.kernarg_size != 0 && kernarg_address == 0) return false;
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
    if (!readable) return false;
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
    if (field.kind == GEMSTONE_HSA_ARGUMENT_SCALAR) {
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
        uint64_t pointer = 0;
        std::memcpy(&pointer, kernarg + field.offset + cursor, sizeof(pointer));
        gemstone_hsa_argument allocation{};
        if (!resolve_allocation(runtime, pointer, allocation)) {
          cursor += sizeof(uint64_t);
          continue;
        }
        append_scalar(field.offset + scalar_start, cursor - scalar_start);
        arguments.push_back(allocation);
        cursor += sizeof(uint64_t);
        scalar_start = cursor;
      }
      append_scalar(field.offset + scalar_start, field.size - scalar_start);
    } else {
      uint64_t pointer = 0;
      std::memcpy(&pointer, kernarg + field.offset, sizeof(pointer));
      gemstone_hsa_argument allocation{};
      if (!resolve_allocation(runtime, pointer, allocation)) return false;
      arguments.push_back(allocation);
    }
    covered = field.offset + field.size;
  }
  append_scalar(covered, binding.kernarg_size - covered);
  return true;
}

} // namespace

extern "C" gemstone_hsa_aql_runtime *gemstone_hsa_aql_runtime_create(const gemstone_hsa_executor *executor,
                                                                     const gemstone_hsa_signal_operations *signals) {
  if (executor == nullptr || signals == nullptr || !valid_executor(*executor) || !valid_signals(*signals)) return nullptr;
  auto *runtime = new (std::nothrow) gemstone_hsa_aql_runtime();
  if (runtime == nullptr) return nullptr;
  runtime->executor = *executor;
  runtime->signals = *signals;
  return runtime;
}

extern "C" void gemstone_hsa_aql_runtime_destroy(gemstone_hsa_aql_runtime *runtime) {
  if (runtime == nullptr) return;
  std::unordered_set<uint64_t> programs;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    programs = runtime->program_references ^ keys();
  }
  for (uint64_t program : programs)
    runtime->executor.unload_program(runtime->executor.context, program);
  if (runtime->executor.destroy != nullptr) runtime->executor.destroy(runtime->executor.context);
  delete runtime;
}

extern "C" const char *gemstone_hsa_aql_runtime_error(const gemstone_hsa_aql_runtime *runtime) {
  if (runtime == nullptr) return "AQL runtime is unavailable";
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
  const gemstone_hsa_kernel_binding binding{kernel_object, kernel_name, kernarg_size, fields, field_count};
  return gemstone_hsa_aql_runtime_install_code_object(runtime, code_object, code_object_size, &binding, 1);
}

extern "C" int gemstone_hsa_aql_runtime_install_code_object(gemstone_hsa_aql_runtime *runtime, const void *code_object,
                                                            uint32_t code_object_size, const gemstone_hsa_kernel_binding *bindings,
                                                            uint32_t binding_count) {
  if (runtime == nullptr || code_object == nullptr || code_object_size == 0 || bindings == nullptr || binding_count == 0) return -1;
  std::vector<std::pair<uint64_t, KernelBinding>> copied;
  copied.reserve(binding_count);
  std::unordered_set<uint64_t> objects;
  objects.reserve(binding_count);
  for (uint32_t index = 0; index < binding_count; ++index) {
    const auto &input = bindings[index];
    if (input.kernel_object == 0 || input.kernel_name == nullptr || input.kernel_name[0] == '\0'
        || !objects.insert(input.kernel_object).second)
      return -1;
    std::vector<gemstone_hsa_kernarg_field> fields;
    if (!copy_fields(input.kernarg_size, input.fields, input.field_count, fields)) return -1;
    copied.emplace_back(input.kernel_object, KernelBinding{0, input.kernel_name, input.kernarg_size, std::move(fields)});
  }
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    for (const auto &[object, _] : copied)
      if (runtime->kernels.count(object) != 0) return -1;
  }
  uint64_t program_id = 0;
  if (runtime->executor.load_program(runtime->executor.context, code_object, code_object_size, &program_id) != 0 || program_id == 0) {
    set_error(*runtime, executor_error(*runtime));
    return -1;
  }
  bool installed = true;
  {
    std::lock_guard<std::mutex> guard(runtime->lock);
    for (const auto &[object, _] : copied)
      installed = installed && runtime->kernels.count(object) == 0;
    if (installed) {
      for (auto &[object, binding] : copied) {
        binding.program_id = program_id;
        runtime->kernels.emplace(object, std::move(binding));
      }
      runtime->program_references.emplace(program_id, binding_count);
    }
  }
  if (!installed) {
    runtime->executor.unload_program(runtime->executor.context, program_id);
    return -1;
  }
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_remove_kernel(gemstone_hsa_aql_runtime *runtime, uint64_t kernel_object) {
  if (runtime == nullptr || kernel_object == 0) return -1;
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
      unload = true;
    }
  }
  return !unload || runtime->executor.unload_program(runtime->executor.context, program_id) == 0 ? 0 : -1;
}

extern "C" int gemstone_hsa_aql_runtime_register_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base, uint64_t size,
                                                            uint64_t allocation_id) {
  if (runtime == nullptr || native_base == 0 || size == 0 || allocation_id == 0
      || native_base > std::numeric_limits<uint64_t>::max() - size)
    return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  for (const auto &[base, allocation] : runtime->allocations) {
    if (native_base < base + allocation.size && base < native_base + size) return -1;
  }
  runtime->allocations.emplace(native_base, AllocationBinding{size, allocation_id});
  return 0;
}

extern "C" int gemstone_hsa_aql_runtime_unregister_allocation(gemstone_hsa_aql_runtime *runtime, uint64_t native_base) {
  if (runtime == nullptr || native_base == 0) return -1;
  std::lock_guard<std::mutex> guard(runtime->lock);
  return runtime->allocations.erase(native_base) == 1 ? 0 : -1;
}

extern "C" int gemstone_hsa_aql_runtime_take_dirty_ranges(gemstone_hsa_aql_runtime *runtime, gemstone_hsa_dirty_range *ranges,
                                                          uint32_t capacity, uint32_t *count) {
  if (runtime == nullptr || count == nullptr || (capacity != 0 && ranges == nullptr)) return -1;
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
  if (runtime == nullptr) return GEMSTONE_HSA_PACKET_FATAL;
  gemstone_hsa_aql_packet decoded{};
  const auto decode = gemstone_hsa_decode_aql_packet(packet, packet_size, &decoded);
  if (decode != GEMSTONE_HSA_AQL_DECODE_SUCCESS) {
    set_error(*runtime, gemstone_hsa_aql_decode_error_string(decode));
    return GEMSTONE_HSA_PACKET_FATAL;
  }
  const bool trace_packets = std::getenv("GEMSTONE_HSA_TRACE_PACKETS") != nullptr;

  if (decoded.type == GEMSTONE_HSA_AQL_VENDOR_SPECIFIC) {
    const auto &pm4 = decoded.value.pm4_ib;
    if (trace_packets) std::fprintf(stderr, "Gemstone observed AQL PM4-IB synchronization packet completion=%lu\n", pm4.completion_signal);
    if (pm4.completion_signal != 0 && runtime->signals.complete_release(runtime->signals.context, pm4.completion_signal) != 0)
      return GEMSTONE_HSA_PACKET_FATAL;
    return GEMSTONE_HSA_PACKET_ACCEPTED;
  }

  if (decoded.type == GEMSTONE_HSA_AQL_BARRIER_AND || decoded.type == GEMSTONE_HSA_AQL_BARRIER_OR) {
    const auto &barrier = decoded.value.barrier;
    const auto state = barrier_state(*runtime, barrier);
    if (trace_packets) {
      const char *state_name = state == BarrierState::ready ? "ready" : state == BarrierState::retry ? "retry" : "error";
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
    if (state == BarrierState::retry) return GEMSTONE_HSA_PACKET_RETRY;
    if (state == BarrierState::error) return fail_packet(*runtime, barrier.completion_signal, -4, "cannot read barrier dependency signal");
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
  if (!found_binding) return fail_packet(*runtime, dispatch.completion_signal, -2, "kernel object is not installed");

  std::vector<gemstone_hsa_argument> arguments;
  if (!resolve_arguments(*runtime, binding, dispatch.kernarg_address, arguments))
    return fail_packet(*runtime, dispatch.completion_signal, -5, "kernarg buffer or allocation pointer is invalid");

  const gemstone_hsa_dimensions group_size{dispatch.workgroup_size_x, dispatch.workgroup_size_y, dispatch.workgroup_size_z};
  const gemstone_hsa_dimensions group_count{divide_rounding_up(dispatch.grid_size_x, dispatch.workgroup_size_x),
                                            divide_rounding_up(dispatch.grid_size_y, dispatch.workgroup_size_y),
                                            divide_rounding_up(dispatch.grid_size_z, dispatch.workgroup_size_z)};
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
    return fail_packet(*runtime, dispatch.completion_signal, -3, kernel_error(binding, executor_error(*runtime)));
  const int awaited = runtime->executor.await_completion(runtime->executor.context, completion_id);
  // Releasing the remote completion is a separate RPC exchange.  It may clear
  // or replace the client's error buffer, so preserve the execution failure
  // before performing the mandatory release.
  const std::string await_error = awaited == 0 ? std::string{} : executor_error(*runtime);
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
    if (queried != 0) query_error = executor_error(*runtime);
  }
  const int released = runtime->executor.release_completion(runtime->executor.context, completion_id);
  if (trace_packets)
    std::fprintf(stderr, "Gemstone completed AQL kernel=%s result=%d release=%d\n", binding.name.c_str(), awaited, released);
  if (awaited != 0) return fail_packet(*runtime, dispatch.completion_signal, -3, kernel_error(binding, await_error));
  if (queried != 0) return fail_packet(*runtime, dispatch.completion_signal, -3, kernel_error(binding, query_error));
  if (released != 0) return fail_packet(*runtime, dispatch.completion_signal, -3, kernel_error(binding, executor_error(*runtime)));
  if (!dirty_ranges.empty()) {
    std::lock_guard<std::mutex> guard(runtime->lock);
    runtime->dirty_ranges.insert(runtime->dirty_ranges.end(), dirty_ranges.begin(), dirty_ranges.end());
  }
  if (dispatch.completion_signal != 0 && runtime->signals.complete_release(runtime->signals.context, dispatch.completion_signal) != 0)
    return GEMSTONE_HSA_PACKET_FATAL;
  return GEMSTONE_HSA_PACKET_ACCEPTED;
}

namespace {

int rocr_load(void *, uint64_t signal, int64_t *value) {
  if (signal == 0 || value == nullptr) return -1;
  const auto *address = reinterpret_cast<const int64_t *>(static_cast<uintptr_t>(signal) + 8);
  *value = __atomic_load_n(address, __ATOMIC_ACQUIRE);
  return 0;
}

int rocr_complete(void *, uint64_t signal) {
  if (signal == 0) return -1;
  auto *address = reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8);
  __atomic_fetch_sub(address, INT64_C(1), __ATOMIC_RELEASE);
  return 0;
}

int rocr_fail(void *, uint64_t signal, int32_t error_code) {
  if (signal == 0) return -1;
  auto *address = reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8);
  const int64_t value = error_code < 0 ? error_code : -static_cast<int64_t>(error_code) - 1;
  __atomic_store_n(address, value, __ATOMIC_RELEASE);
  return 0;
}

} // namespace

extern "C" gemstone_hsa_signal_operations gemstone_hsa_rocr_signal_operations(void) {
  return gemstone_hsa_signal_operations{nullptr, rocr_load, rocr_complete, rocr_fail};
}
