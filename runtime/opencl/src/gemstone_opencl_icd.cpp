#include <CL/cl_icd.h>

#include "gemstone_execution_rpc.h"

#include <algorithm>
#include <array>
#include <atomic>
#include <cctype>
#include <chrono>
#include <condition_variable>
#include <cstdint>
#include <cstdio>
#include <cstdlib>
#include <cstring>
#include <fcntl.h>
#include <fstream>
#include <functional>
#include <iterator>
#include <limits>
#include <map>
#include <memory>
#include <mutex>
#include <new>
#include <sstream>
#include <set>
#include <string>
#include <sys/wait.h>
#include <thread>
#include <utility>
#include <vector>

#include <unistd.h>

namespace {

constexpr uint64_t kMagic = UINT64_C(0x47454d53544f4e45);
constexpr uint32_t kPointerRelocationSentinel = std::numeric_limits<uint32_t>::max();

enum class Kind : uint32_t { Platform, Device, Context, Queue, Memory, Program, Kernel, Event };

struct Header {
  explicit Header(Kind value) : kind(value) {}
  uint64_t magic = kMagic;
  Kind kind;
  std::atomic_uint references{1};
};

std::mutex live_objects_mutex;
std::set<const void *> live_objects;

void register_live_object(const void *value) {
  std::lock_guard lock(live_objects_mutex);
  live_objects.insert(value);
}

void unregister_live_object(const void *value) {
  std::lock_guard lock(live_objects_mutex);
  live_objects.erase(value);
}

bool is_live_object(const void *value) {
  std::lock_guard lock(live_objects_mutex);
  return live_objects.find(value) != live_objects.end();
}

template <class T> T *checked(T *value, Kind kind) {
  if (value == nullptr || !is_live_object(value) || value->header.magic != kMagic || value->header.kind != kind) return nullptr;
  return value;
}

template <class T> void retain(T *value) { value->header.references.fetch_add(1, std::memory_order_relaxed); }

template <class T> bool release(T *value) {
  return value->header.references.fetch_sub(1, std::memory_order_acq_rel) == 1;
}

} // namespace

struct _cl_platform_id {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Platform};
};

struct _cl_device_id {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Device};
  _cl_platform_id *platform = nullptr;
};

struct _cl_context {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Context};
  _cl_device_id *device = nullptr;
  std::vector<cl_context_properties> properties;
  std::vector<std::pair<void (CL_CALLBACK *)(cl_context, void *), void *>> destructor_callbacks;
};

struct BufferMapping {
  size_t offset = 0;
  size_t size = 0;
  void *pointer = nullptr;
  bool release_pointer = false;
};

struct Allocation {
  void *base = nullptr;
  size_t size = 0;
  uint64_t id = 0;
  std::atomic_bool externally_mutable{false};
  std::atomic_bool released{false};
  bool runtime_owns_base = false;
  // A host allocator can recycle an address while a stale registry entry is
  // still retained by an object which no longer owns the host storage.
  bool owns_base = true;
  // Host-visible buffers can be copied by the SYCL runtime without updating
  // their host mirror.  Preserve pointer identities discovered by the
  // relocation scan so copied aggregate arguments remain relocatable.
  std::map<size_t, std::pair<std::shared_ptr<Allocation>, size_t>> pointer_relocations;
};

struct _cl_command_queue {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Queue};
  _cl_context *context = nullptr;
  _cl_device_id *device = nullptr;
  cl_command_queue_properties properties = 0;
  std::vector<cl_queue_properties> properties_array;
  std::mutex pending_mutex;
  std::condition_variable pending_condition;
  size_t pending_commands = 0;
  bool pending_failed = false;
};

struct _cl_mem {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Memory};
  _cl_context *context = nullptr;
  std::shared_ptr<Allocation> allocation;
  cl_mem_flags flags = 0;
  void *host_ptr = nullptr;
  std::vector<cl_mem_properties> properties;
  _cl_mem *parent = nullptr;
  size_t origin = 0;
  size_t size = 0;
  std::map<uintptr_t, BufferMapping> mappings;
  std::vector<std::pair<void (CL_CALLBACK *)(cl_mem, void *), void *>> destructor_callbacks;
};

struct _cl_program {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Program};
  _cl_context *context = nullptr;
  std::string source;
  std::vector<uint8_t> il;
  std::vector<uint8_t> bitcode;
  uint64_t remote_id = 0;
  cl_build_status build_status = CL_BUILD_NONE;
  cl_program_binary_type binary_type = CL_PROGRAM_BINARY_TYPE_NONE;
  std::string build_source;
  std::string kernel_names;
  std::string build_options;
  std::string build_log;
};

struct KernelArgument {
  enum class Kind { Scalar, Allocation, Local, SVM } kind = Kind::Scalar;
  std::vector<uint8_t> bytes;
  std::shared_ptr<Allocation> allocation;
  size_t offset = 0;
};

struct KernelArgumentInfo {
  cl_kernel_arg_address_qualifier address = CL_KERNEL_ARG_ADDRESS_PRIVATE;
  cl_kernel_arg_access_qualifier access = CL_KERNEL_ARG_ACCESS_NONE;
  cl_kernel_arg_type_qualifier type = CL_KERNEL_ARG_TYPE_NONE;
  std::string type_name;
  std::string name;
};

struct KernelParameterMetadata {
  bool pointer_aggregate = false;
  bool local = false;
  bool contains_pointer = false;
};

struct _cl_kernel {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Kernel};
  _cl_program *program = nullptr;
  std::string name;
  size_t expected_arguments = 0;
  std::array<size_t, 3> required_work_group_size{0, 0, 0};
  size_t static_local_memory = 0;
  std::string attributes;
  std::vector<size_t> expected_argument_sizes;
  std::vector<KernelParameterMetadata> parameter_metadata;
  std::vector<KernelArgumentInfo> argument_info;
  std::vector<KernelArgument> arguments;
  std::vector<bool> arguments_set;
};

struct _cl_event {
  const cl_icd_dispatch *dispatch = nullptr;
  Header header{Kind::Event};
  _cl_command_queue *queue = nullptr;
  _cl_context *context = nullptr;
  cl_command_type command = CL_COMMAND_MARKER;
  cl_int status = CL_COMPLETE;
  uint64_t queued = 0;
  uint64_t submitted = 0;
  uint64_t started = 0;
  uint64_t ended = 0;
  struct Callback {
    cl_int trigger;
    void (CL_CALLBACK *function)(cl_event, cl_int, void *);
    void *user;
  };
  std::vector<Callback> callbacks;
};

namespace {

struct Remote {
  std::once_flag initialized;
  gemstone_execution_client *client = nullptr;
  bool ready = false;
  std::string error;
};

Remote remote;
std::mutex remote_mutex;
std::mutex allocations_mutex;
std::mutex events_mutex;
std::condition_variable events_condition;
std::map<uintptr_t, std::shared_ptr<Allocation>> allocations;
std::map<uintptr_t, std::weak_ptr<Allocation>> allocation_history;
std::map<uintptr_t, BufferMapping> svm_mappings;
std::vector<_cl_event *> live_events;
std::atomic_uint64_t next_allocation{1};
std::once_flag dispatch_initialized;
cl_icd_dispatch dispatch{};
_cl_platform_id platform;
_cl_device_id device;

uint64_t now_nanos() {
  return static_cast<uint64_t>(std::chrono::duration_cast<std::chrono::nanoseconds>(
      std::chrono::steady_clock::now().time_since_epoch()).count());
}

void notify_event_callbacks(_cl_event *event) {
  std::vector<_cl_event::Callback> ready;
  cl_int status = CL_COMPLETE;
  {
    std::lock_guard lock(events_mutex);
    status = event->status;
    for (auto callback = event->callbacks.begin(); callback != event->callbacks.end();) {
      if (status < 0 || status <= callback->trigger) {
        ready.push_back(*callback);
        callback = event->callbacks.erase(callback);
      } else ++callback;
    }
  }
  for (const auto &callback : ready)
    callback.function(reinterpret_cast<cl_event>(event), status, callback.user);
}

Remote &remote_state() {
  std::call_once(remote.initialized, [] {
    remote.client = gemstone_execution_client_create_from_environment();
    if (remote.client == nullptr) {
      remote.error = "GEMSTONE_RPC_TOKEN is absent or invalid";
      return;
    }
    if (gemstone_execution_attest(remote.client) != 0) {
      remote.error = gemstone_execution_client_error(remote.client);
      gemstone_execution_client_destroy(remote.client);
      remote.client = nullptr;
      return;
    }
    remote.ready = true;
  });
  return remote;
}

const char *remote_error() {
  const auto &state = remote_state();
  if (state.client != nullptr) {
    const char *message = gemstone_execution_client_error(state.client);
    if (message != nullptr && message[0] != '\0') return message;
  }
  return state.error.empty() ? "Gemstone execution RPC failed" : state.error.c_str();
}

bool scan_buffer_pointer_relocations() {
  const char *value = std::getenv("GEMSTONE_OPENCL_SCAN_BUFFER_POINTERS");
  return value != nullptr && std::strcmp(value, "1") == 0;
}

cl_ulong max_mem_alloc_size() {
  // Keep the advertised limit below the remote service's practical per-allocation
  // envelope.  The environment override remains available for larger deployments.
  constexpr cl_ulong default_size = UINT64_C(0x10000000);
  const char *value = std::getenv("GEMSTONE_OPENCL_MAX_MEM_ALLOC_SIZE");
  if (value == nullptr || value[0] == '\0') return default_size;
  char *end = nullptr;
  const auto parsed = std::strtoull(value, &end, 0);
  return end != value && *end == '\0' && parsed != 0 ? static_cast<cl_ulong>(parsed) : default_size;
}

cl_ulong local_mem_size() {
  constexpr cl_ulong default_size = UINT64_C(0x10000);
  const char *value = std::getenv("GEMSTONE_OPENCL_LOCAL_MEM_SIZE");
  if (value == nullptr || value[0] == '\0') return default_size;
  char *end = nullptr;
  const auto parsed = std::strtoull(value, &end, 0);
  return end != value && *end == '\0' && parsed != 0 ? static_cast<cl_ulong>(parsed) : default_size;
}

uint64_t next_id() {
  const auto process = static_cast<uint64_t>(static_cast<uint32_t>(getpid())) & UINT64_C(0x7fffffff);
  return (process << 32) | (next_allocation.fetch_add(1) & UINT64_C(0xffffffff));
}

std::shared_ptr<Allocation> find_pointer(const void *pointer, size_t *offset = nullptr) {
  const auto address = reinterpret_cast<uintptr_t>(pointer);
  std::lock_guard lock(allocations_mutex);
  const auto entry = allocations.upper_bound(address);
  if (entry == allocations.begin()) return {};
  const auto candidate = std::prev(entry);
  const auto start = candidate->first;
  if (address >= start && address - start <= candidate->second->size) {
    if (offset != nullptr) *offset = address - start;
    return candidate->second;
  }
  return {};
}

bool valid_range(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  return allocation != nullptr && offset <= allocation->size && size <= allocation->size - offset;
}

std::shared_ptr<Allocation> find_allocation(uint64_t id) {
  std::lock_guard lock(allocations_mutex);
  for (const auto &[_, allocation] : allocations)
    if (allocation->id == id) return allocation;
  return {};
}

bool valid_mem_range(const _cl_mem *mem, size_t offset, size_t size) {
  return mem != nullptr && offset <= mem->size && size <= mem->size - offset;
}

bool host_read_allowed(const _cl_mem *mem) {
  return mem != nullptr && (mem->flags & (CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0;
}

bool host_write_allowed(const _cl_mem *mem) {
  return mem != nullptr && (mem->flags & (CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_NO_ACCESS)) == 0;
}

void invalidate_pointer_relocations(const std::shared_ptr<Allocation> &allocation, size_t offset, size_t size) {
  if (allocation == nullptr || size == 0) return;
  const size_t end = offset > std::numeric_limits<size_t>::max() - size
    ? std::numeric_limits<size_t>::max() : offset + size;
  for (auto it = allocation->pointer_relocations.begin(); it != allocation->pointer_relocations.end();) {
    const size_t pointer_end = it->first > std::numeric_limits<size_t>::max() - sizeof(uint64_t)
      ? std::numeric_limits<size_t>::max() : it->first + sizeof(uint64_t);
    if (it->first < end && pointer_end > offset) it = allocation->pointer_relocations.erase(it);
    else ++it;
  }
}

void copy_pointer_relocations(const std::shared_ptr<Allocation> &source, size_t source_offset,
                              const std::shared_ptr<Allocation> &target, size_t target_offset, size_t size) {
  if (source == nullptr || target == nullptr || size < sizeof(uint64_t)) return;
  const size_t source_end = source_offset > std::numeric_limits<size_t>::max() - size
    ? std::numeric_limits<size_t>::max() : source_offset + size;
  for (const auto &[offset, relocation] : source->pointer_relocations) {
    if (offset >= source_offset && offset <= source_end - sizeof(uint64_t)) {
      const size_t target_offset_for_pointer = target_offset + offset - source_offset;
      if (target_offset_for_pointer >= target_offset)
        target->pointer_relocations[target_offset_for_pointer] = relocation;
    }
  }
}

size_t allocation_offset(const _cl_mem *mem, size_t offset) {
  return mem->origin + offset;
}

std::shared_ptr<Allocation> allocate(size_t size, cl_int *error, size_t requested_alignment = 128) {
  if (size == 0) {
    if (error != nullptr) *error = CL_INVALID_BUFFER_SIZE;
    return {};
  }
  const size_t requested = requested_alignment == 0 ? 128 : requested_alignment;
  if ((requested & (requested - 1)) != 0) {
    if (error != nullptr) *error = CL_INVALID_VALUE;
    return {};
  }
  const size_t alignment = std::max(sizeof(void *), requested);
  void *base = nullptr;
  if (posix_memalign(&base, alignment, size) != 0 || base == nullptr) {
    if (error != nullptr) *error = CL_OUT_OF_HOST_MEMORY;
    return {};
  }
  auto result = std::make_shared<Allocation>();
  result->base = base;
  result->size = size;
  result->id = next_id();
  auto &state = remote_state();
  if (state.ready) {
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_register_allocation(state.client, result->id, size, GEMSTONE_ALLOCATION_SHARED) != 0) {
      std::free(base);
      if (error != nullptr) *error = CL_OUT_OF_RESOURCES;
      return {};
    }
  }
  {
    std::lock_guard lock(allocations_mutex);
    const auto key = reinterpret_cast<uintptr_t>(base);
    const auto existing = allocations.find(key);
    if (existing == allocations.end()) {
      allocations.emplace(key, result);
    } else {
      // There cannot be two live host allocations with the same address.  If
      // the allocator has nevertheless recycled an address before the old
      // registry entry disappeared, the new allocation is the only one which
      // can safely service pointer lookups at that address.
      existing->second->owns_base = false;
      existing->second = result;
    }
    const auto history = allocation_history.find(key);
    if (history != allocation_history.end()) {
      if (const auto previous = history->second.lock()) previous->owns_base = false;
      else allocation_history.erase(history);
    }
    allocation_history[key] = result;
  }
  if (std::getenv("GEMSTONE_OPENCL_TRACE_ALLOC") != nullptr)
    std::fprintf(stderr, "gemstone alloc base=%p id=%llu size=%zu\n", base,
                 static_cast<unsigned long long>(result->id), size);
  return result;
}

cl_int release_allocation(const std::shared_ptr<Allocation> &allocation, bool release_runtime_base = false) {
  if (allocation == nullptr) return CL_SUCCESS;
  if (allocation->released.exchange(true, std::memory_order_acq_rel)) return CL_SUCCESS;
  bool owns_base = false;
  {
    std::lock_guard lock(allocations_mutex);
    owns_base = allocation->owns_base;
    const auto entry = allocations.find(reinterpret_cast<uintptr_t>(allocation->base));
    if (entry != allocations.end() && entry->second.get() == allocation.get()) allocations.erase(entry);
    else if (entry != allocations.end()) owns_base = false;
  }
  auto &state = remote_state();
  cl_int result = CL_SUCCESS;
  if (state.ready) {
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_release_allocation(state.client, allocation->id) != 0) result = CL_OUT_OF_RESOURCES;
  }
  if (owns_base && (!allocation->runtime_owns_base || release_runtime_base)) std::free(allocation->base);
  return result;
}

cl_int write_allocation(const std::shared_ptr<Allocation> &allocation, size_t offset, const void *bytes, size_t size) {
  if (!valid_range(allocation, offset, size) || (size != 0 && bytes == nullptr)) return CL_INVALID_VALUE;
  invalidate_pointer_relocations(allocation, offset, size);
  if (size != 0) std::memcpy(static_cast<uint8_t *>(allocation->base) + offset, bytes, size);
  auto &state = remote_state();
  if (state.ready && size != 0) {
    if (size > std::numeric_limits<uint32_t>::max()) return CL_INVALID_VALUE;
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_write_allocation(state.client, allocation->id, offset, bytes, static_cast<uint32_t>(size)) != 0)
      return CL_OUT_OF_RESOURCES;
  }
  return CL_SUCCESS;
}

cl_int read_allocation(const std::shared_ptr<Allocation> &allocation, size_t offset, void *bytes, size_t size) {
  if (!valid_range(allocation, offset, size) || (size != 0 && bytes == nullptr)) return CL_INVALID_VALUE;
  auto &state = remote_state();
  if (state.ready && size != 0) {
    if (size > std::numeric_limits<uint32_t>::max()) return CL_INVALID_VALUE;
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_read_allocation(state.client, allocation->id, offset, bytes, static_cast<uint32_t>(size)) != 0)
      return CL_OUT_OF_RESOURCES;
    std::memcpy(static_cast<uint8_t *>(allocation->base) + offset, bytes, size);
  } else if (size != 0) {
    std::memcpy(bytes, static_cast<uint8_t *>(allocation->base) + offset, size);
  }
  return CL_SUCCESS;
}

template <class T> cl_int output_scalar(size_t capacity, void *output, size_t *written, const T &value) {
  if (written != nullptr) *written = sizeof(T);
  if (output == nullptr) return CL_SUCCESS;
  if (capacity < sizeof(T)) return CL_INVALID_VALUE;
  std::memcpy(output, &value, sizeof(T));
  return CL_SUCCESS;
}

cl_int output_bytes(size_t capacity, void *output, size_t *written, const void *bytes, size_t size) {
  if (written != nullptr) *written = size;
  if (output == nullptr) return CL_SUCCESS;
  if (capacity < size) return CL_INVALID_VALUE;
  if (size != 0) std::memcpy(output, bytes, size);
  return CL_SUCCESS;
}

cl_int output_string(size_t capacity, void *output, size_t *written, const std::string &value) {
  return output_bytes(capacity, output, written, value.c_str(), value.size() + 1);
}

bool valid_platform(cl_platform_id value) { return checked(reinterpret_cast<_cl_platform_id *>(value), Kind::Platform) == &platform; }
bool valid_device(cl_device_id value) { return checked(reinterpret_cast<_cl_device_id *>(value), Kind::Device) == &device; }

void initialize_dispatch();

struct ProcessResult {
  int status = -1;
  std::string output;
};

ProcessResult run_process(const std::vector<std::string> &arguments) {
  ProcessResult result;
  if (arguments.empty()) return result;
  int pipe_fds[2];
  if (pipe(pipe_fds) != 0) return result;
  std::vector<char *> argv;
  argv.reserve(arguments.size() + 1);
  for (const auto &argument : arguments) argv.push_back(const_cast<char *>(argument.c_str()));
  argv.push_back(nullptr);
  const pid_t child = fork();
  if (child == -1) {
    close(pipe_fds[0]);
    close(pipe_fds[1]);
    return result;
  }
  if (child == 0) {
    close(pipe_fds[0]);
    if (dup2(pipe_fds[1], STDOUT_FILENO) == -1 || dup2(pipe_fds[1], STDERR_FILENO) == -1) _exit(126);
    close(pipe_fds[1]);
    execvp(argv[0], argv.data());
    _exit(127);
  }
  close(pipe_fds[1]);
  char buffer[4096];
  for (;;) {
    const ssize_t count = read(pipe_fds[0], buffer, sizeof(buffer));
    if (count > 0) result.output.append(buffer, static_cast<size_t>(count));
    else break;
  }
  close(pipe_fds[0]);
  int status = 0;
  if (waitpid(child, &status, 0) == child) {
    if (WIFEXITED(status)) result.status = WEXITSTATUS(status);
    else if (WIFSIGNALED(status)) result.status = 128 + WTERMSIG(status);
  }
  return result;
}

bool split_options(const std::string &options, std::vector<std::string> *result, std::string *error) {
  std::string current;
  char quote = '\0';
  bool escaped = false;
  for (const char value : options) {
    if (escaped) {
      current.push_back(value);
      escaped = false;
    } else if (value == '\\' && quote != '\'') {
      escaped = true;
    } else if (quote != '\0') {
      if (value == quote) quote = '\0';
      else current.push_back(value);
    } else if (value == '\'' || value == '"') {
      quote = value;
    } else if (std::isspace(static_cast<unsigned char>(value))) {
      if (!current.empty()) {
        result->push_back(std::move(current));
        current.clear();
      }
    } else {
      current.push_back(value);
    }
  }
  if (escaped || quote != '\0') {
    if (error != nullptr) *error = "unterminated escape or quote in OpenCL build options";
    return false;
  }
  if (!current.empty()) result->push_back(std::move(current));
  return true;
}

std::string compiler_path() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_COMPILER");
  if (configured != nullptr && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/icpx";
}

std::string spirv_converter_path() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_LLVM_SPIRV");
  if (configured != nullptr && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/compiler/llvm-spirv";
}

std::string bitcode_linker_path() {
  const char *configured = std::getenv("GEMSTONE_OPENCL_LLVM_LINK");
  if (configured != nullptr && configured[0] != '\0') return configured;
  return "/opt/intel/oneapi/compiler/2026.1/bin/compiler/llvm-link";
}

std::string make_global_memory_volatile(const std::string &source) {
  static constexpr const char marker[] = "__global";
  std::string result;
  result.reserve(source.size() + source.size() / 32);
  size_t position = 0;
  while (position < source.size()) {
    const size_t found = source.find(marker, position);
    if (found == std::string::npos) {
      result.append(source, position, std::string::npos);
      break;
    }
    const bool starts_word =
      found == 0 || (!std::isalnum(static_cast<unsigned char>(source[found - 1])) && source[found - 1] != '_');
    const size_t after_marker = found + sizeof(marker) - 1;
    const bool ends_word =
      after_marker == source.size() ||
      (!std::isalnum(static_cast<unsigned char>(source[after_marker])) && source[after_marker] != '_');
    result.append(source, position, found - position);
    result.append(marker);
    if (starts_word && ends_word) {
      size_t next = after_marker;
      while (next < source.size() && std::isspace(static_cast<unsigned char>(source[next]))) ++next;
      const bool already_volatile =
        source.compare(next, sizeof("volatile") - 1, "volatile") == 0 &&
        (next + sizeof("volatile") - 1 == source.size() ||
         (!std::isalnum(static_cast<unsigned char>(source[next + sizeof("volatile") - 1])) &&
          source[next + sizeof("volatile") - 1] != '_'));
      if (!already_volatile) result.append(" volatile");
    }
    position = after_marker;
  }
  return result;
}

bool write_bytes(const std::string &path, const uint8_t *bytes, size_t size) {
  const int descriptor = open(path.c_str(), O_WRONLY | O_CREAT | O_EXCL, 0600);
  if (descriptor == -1) return false;
  size_t offset = 0;
  while (offset < size) {
    const ssize_t count = write(descriptor, bytes + offset, size - offset);
    if (count <= 0) {
      close(descriptor);
      unlink(path.c_str());
      return false;
    }
    offset += static_cast<size_t>(count);
  }
  return close(descriptor) == 0;
}

bool write_file(const std::string &path, const std::string &contents) {
  return write_bytes(path, reinterpret_cast<const uint8_t *>(contents.data()), contents.size());
}

bool read_bytes(const std::string &path, std::vector<uint8_t> *bytes) {
  std::ifstream stream(path, std::ios::binary);
  if (!stream) return false;
  bytes->assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
  return stream.good() || stream.eof();
}

bool compile_source(const std::string &source, const std::string &options, std::vector<uint8_t> *il, std::string *log,
                    std::vector<uint8_t> *bitcode_output = nullptr) {
  std::vector<std::string> option_arguments;
  std::string option_error;
  if (!split_options(options, &option_arguments, &option_error)) {
    if (log != nullptr) *log = option_error;
    return false;
  }
  char directory[] = "/tmp/gemstone-opencl-XXXXXX";
  if (mkdtemp(directory) == nullptr) {
    if (log != nullptr) *log = "cannot create temporary directory for OpenCL compilation";
    return false;
  }
  const std::string root(directory);
  const std::string input = root + "/source.cl";
  const std::string bitcode = root + "/source.bc";
  const std::string output = root + "/source.spv";
  // The installed LLVM-to-SPIR-V converter rejects legal OpenCL swizzles when
  // the frontend narrows an otherwise unused vector lane (for example, a
  // char8 .even expression becomes an invalid LLVM <7 x i8>). Volatile global
  // accesses preserve the source semantics while forcing the frontend to keep
  // the declared vector width, which is representable in SPIR-V.
  const bool has_vector_swizzle =
    source.find(".hi") != std::string::npos || source.find(".lo") != std::string::npos ||
    source.find(".even") != std::string::npos || source.find(".odd") != std::string::npos;
  const bool uses_enqueued_local_size = source.find("get_enqueued_local_size") != std::string::npos;
  const std::string compiler_source = has_vector_swizzle ? make_global_memory_volatile(source) : source;
  bool success = write_file(input, compiler_source);
  std::string diagnostic;
  if (success) {
    std::vector<std::string> command = {
      compiler_path(), "-x", "cl", "-cl-std=CL1.2", "-target", "spir64",
      "-emit-llvm", "-c", input, "-o", bitcode
    };
    static constexpr const char *unsupported_features[] = {
      "__opencl_c_program_scope_global_variables", "__opencl_c_3d_image_writes",
      "__opencl_c_atomic_order_acq_rel", "__opencl_c_atomic_order_seq_cst",
      "__opencl_c_atomic_scope_device", "__opencl_c_atomic_scope_all_devices",
      "__opencl_c_device_enqueue", "__opencl_c_generic_address_space", "__opencl_c_pipes",
      "__opencl_c_read_write_images", "__opencl_c_work_group_collective_functions", "__opencl_c_images",
      "__opencl_c_integer_dot_product_input_4x8bit",
      "__opencl_c_integer_dot_product_input_4x8bit_packed", "__opencl_c_ext_image_unorm_int_2_101010",
      "__IMAGE_SUPPORT__"
    };
    for (const auto *feature : unsupported_features) {
      command.emplace_back("-U");
      command.emplace_back(feature);
    }
    for (const auto &argument : option_arguments)
      if (argument != "-cl-no-subgroup-ifp") command.push_back(argument);
    // Some OpenCL 3.0 CTS cases use 2.0 built-ins while the device advertises
    // the optional 1.2 C language profile. Compile those source strings as
    // CL3.0 so their conditional built-in code is present.
    if (uses_enqueued_local_size) {
      command.emplace_back("-cl-std=CL3.0");
      command.emplace_back("-D__OPENCL_VERSION__=300");
    }
    const ProcessResult compilation = run_process(command);
    diagnostic = compilation.output;
    success = compilation.status == 0;
  } else {
    diagnostic = "cannot write temporary OpenCL source";
  }
  if (success) {
    if (bitcode_output != nullptr) {
      success = read_bytes(bitcode, bitcode_output) && !bitcode_output->empty();
      if (!success) diagnostic += "cannot read generated LLVM bitcode";
    }
  }
  if (success) {
    const ProcessResult conversion = run_process({spirv_converter_path(), bitcode, "-o", output});
    if (!conversion.output.empty()) diagnostic += conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    std::ifstream stream(output, std::ios::binary);
    if (!stream) {
      success = false;
      diagnostic += "cannot read generated SPIR-V";
    } else {
      il->assign(std::istreambuf_iterator<char>(stream), std::istreambuf_iterator<char>());
      success = !il->empty() && il->size() <= std::numeric_limits<uint32_t>::max();
      if (!success) diagnostic += "generated SPIR-V is empty or too large";
    }
  }
  unlink(input.c_str());
  unlink(bitcode.c_str());
  unlink(output.c_str());
  rmdir(root.c_str());
  if (log != nullptr) *log = diagnostic;
  return success;
}

bool reverse_spirv(const std::vector<uint8_t> &il, std::vector<uint8_t> *bitcode, std::string *log) {
  char directory[] = "/tmp/gemstone-opencl-reverse-XXXXXX";
  if (mkdtemp(directory) == nullptr) {
    if (log != nullptr) *log = "cannot create temporary directory for SPIR-V linking";
    return false;
  }
  const std::string root(directory);
  const std::string input = root + "/input.spv";
  const std::string output = root + "/output.bc";
  bool success = write_bytes(input, il.data(), il.size());
  std::string diagnostic;
  if (success) {
    const ProcessResult conversion = run_process({spirv_converter_path(), "-r", input, "-o", output});
    diagnostic = conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    success = read_bytes(output, bitcode) && !bitcode->empty();
    if (!success) diagnostic += "cannot read reverse-translated LLVM bitcode";
  }
  unlink(input.c_str());
  unlink(output.c_str());
  rmdir(root.c_str());
  if (log != nullptr) *log = diagnostic;
  return success;
}

bool link_bitcode(const std::vector<std::vector<uint8_t>> &modules, std::vector<uint8_t> *il,
                  std::vector<uint8_t> *bitcode, std::string *log) {
  if (modules.empty()) return false;
  char directory[] = "/tmp/gemstone-opencl-link-XXXXXX";
  if (mkdtemp(directory) == nullptr) {
    if (log != nullptr) *log = "cannot create temporary directory for OpenCL linking";
    return false;
  }
  const std::string root(directory);
  const std::string linked_bitcode = root + "/linked.bc";
  const std::string output = root + "/linked.spv";
  std::vector<std::string> inputs;
  inputs.reserve(modules.size());
  bool success = true;
  for (size_t index = 0; index < modules.size(); ++index) {
    const std::string path = root + "/input" + std::to_string(index) + ".bc";
    if (!write_bytes(path, modules[index].data(), modules[index].size())) {
      success = false;
      break;
    }
    inputs.push_back(path);
  }
  std::string diagnostic;
  if (success) {
    std::vector<std::string> command = {bitcode_linker_path()};
    command.insert(command.end(), inputs.begin(), inputs.end());
    command.emplace_back("-o");
    command.push_back(linked_bitcode);
    const ProcessResult linking = run_process(command);
    diagnostic = linking.output;
    success = linking.status == 0;
  }
  if (success) {
    success = read_bytes(linked_bitcode, bitcode) && !bitcode->empty();
    if (!success) diagnostic += "cannot read linked LLVM bitcode";
  }
  if (success) {
    const ProcessResult conversion = run_process({spirv_converter_path(), linked_bitcode, "-o", output});
    if (!conversion.output.empty()) diagnostic += conversion.output;
    success = conversion.status == 0;
  }
  if (success) {
    success = read_bytes(output, il) && !il->empty() && il->size() <= std::numeric_limits<uint32_t>::max();
    if (!success) diagnostic += "generated linked SPIR-V is empty or too large";
  }
  for (const auto &path : inputs) unlink(path.c_str());
  unlink(linked_bitcode.c_str());
  unlink(output.c_str());
  rmdir(root.c_str());
  if (log != nullptr) *log = diagnostic;
  return success;
}

std::string extract_kernel_names(const std::vector<uint8_t> &il) {
  if (il.size() < sizeof(uint32_t) || il.size() % sizeof(uint32_t) != 0) return {};
  std::vector<std::string> names;
  size_t offset = 5 * sizeof(uint32_t);
  for (; offset + sizeof(uint32_t) <= il.size();) {
    uint32_t instruction = 0;
    std::memcpy(&instruction, il.data() + offset, sizeof(instruction));
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if (opcode == 15 && words >= 4) {
      uint32_t execution_model = 0;
      std::memcpy(&execution_model, il.data() + offset + sizeof(uint32_t), sizeof(execution_model));
      if (execution_model == 6) {
        const size_t name_offset = offset + 3 * sizeof(uint32_t);
        const size_t name_limit = offset + words * sizeof(uint32_t);
        size_t end = name_offset;
        while (end < name_limit && il[end] != 0) ++end;
        if (end < name_limit) names.emplace_back(reinterpret_cast<const char *>(il.data() + name_offset), end - name_offset);
      }
    }
    offset += words * sizeof(uint32_t);
  }
  std::ostringstream result;
  for (size_t index = 0; index < names.size(); ++index) {
    if (index != 0) result << ';';
    result << names[index];
  }
  return result.str();
}

size_t kernel_count(const std::string &names) {
  if (names.empty()) return 0;
  return static_cast<size_t>(std::count(names.begin(), names.end(), ';')) + 1;
}

bool contains_kernel_name(const std::string &names, const std::string &name) {
  size_t start = 0;
  while (start < names.size()) {
    const size_t end = names.find(';', start);
    if (names.compare(start, end == std::string::npos ? names.size() - start : end - start, name) == 0) return true;
    if (end == std::string::npos) break;
    start = end + 1;
  }
  return false;
}

size_t kernel_parameter_count(const std::vector<uint8_t> &il, const std::string &name) {
  if (il.size() < 5 * sizeof(uint32_t) || il.size() % sizeof(uint32_t) != 0) return 0;
  auto word = [&il](size_t offset) {
    uint32_t value = 0;
    std::memcpy(&value, il.data() + offset, sizeof(value));
    return value;
  };
  uint32_t entry = 0;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if ((instruction & 0xffff) == 15 && words >= 4 && word(offset + sizeof(uint32_t)) == 6) {
      const size_t name_start = offset + 3 * sizeof(uint32_t);
      const size_t name_end = offset + words * sizeof(uint32_t);
      size_t end = name_start;
      while (end < name_end && il[end] != 0) ++end;
      if (end < name_end && std::string(reinterpret_cast<const char *>(il.data() + name_start), end - name_start) == name)
        entry = word(offset + 2 * sizeof(uint32_t));
    }
    offset += words * sizeof(uint32_t);
  }
  if (entry == 0) return 0;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if ((instruction & 0xffff) == 54 && words >= 3 && word(offset + 2 * sizeof(uint32_t)) == entry) {
      size_t parameters = 0;
      for (size_t next = offset + words * sizeof(uint32_t); next + sizeof(uint32_t) <= il.size();) {
        const uint32_t nested = word(next);
        const size_t nested_words = nested >> 16;
        if (nested_words == 0 || next + nested_words * sizeof(uint32_t) > il.size()) break;
        if ((nested & 0xffff) == 55) ++parameters;
        if ((nested & 0xffff) == 56) return parameters;
        next += nested_words * sizeof(uint32_t);
      }
      return parameters;
    }
    offset += words * sizeof(uint32_t);
  }
  return 0;
}

std::vector<size_t> kernel_parameter_sizes(const std::vector<uint8_t> &il, const std::string &name) {
  struct TypeInfo {
    uint32_t kind = 0;
    uint32_t element = 0;
    uint64_t count = 0;
    size_t bytes = 0;
    uint32_t storage = 0;
    std::vector<uint32_t> members;
    size_t stride = 0;
  };
  if (il.size() < 5 * sizeof(uint32_t) || il.size() % sizeof(uint32_t) != 0) return {};
  auto word = [&il](size_t offset) {
    uint32_t value = 0;
    std::memcpy(&value, il.data() + offset, sizeof(value));
    return value;
  };
  std::map<uint32_t, TypeInfo> types;
  std::map<uint32_t, uint64_t> constants;
  std::map<std::pair<uint32_t, uint32_t>, size_t> member_offsets;
  uint32_t entry = 0;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if (opcode == 21 && words >= 4) {
      TypeInfo info; info.kind = 1; info.bytes = word(offset + 2 * sizeof(uint32_t)) / 8;
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 22 && words >= 3) {
      TypeInfo info; info.kind = 2; info.bytes = word(offset + 2 * sizeof(uint32_t)) / 8;
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 23 && words >= 4) {
      TypeInfo info; info.kind = 3; info.element = word(offset + 2 * sizeof(uint32_t)); info.count = word(offset + 3 * sizeof(uint32_t));
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if ((opcode == 28 || opcode == 29) && words >= 3) {
      TypeInfo info; info.kind = opcode == 28 ? 5 : 6; info.element = word(offset + 2 * sizeof(uint32_t));
      if (opcode == 28 && words >= 4) info.count = constants[word(offset + 3 * sizeof(uint32_t))];
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 30 && words >= 2) {
      TypeInfo info; info.kind = 7;
      for (size_t index = 2; index < words; ++index) info.members.push_back(word(offset + index * sizeof(uint32_t)));
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 32 && words >= 4) {
      TypeInfo info; info.kind = 4; info.storage = word(offset + 2 * sizeof(uint32_t)); info.element = word(offset + 3 * sizeof(uint32_t)); info.bytes = sizeof(cl_mem);
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if ((opcode == 43 || opcode == 50) && words >= 4) {
      uint64_t value = word(offset + 3 * sizeof(uint32_t));
      if (words >= 5) value |= static_cast<uint64_t>(word(offset + 4 * sizeof(uint32_t))) << 32;
      constants[word(offset + 2 * sizeof(uint32_t))] = value;
    } else if (opcode == 71 && words >= 4 && word(offset + 2 * sizeof(uint32_t)) == 6) {
      auto found = types.find(word(offset + sizeof(uint32_t)));
      if (found != types.end()) found->second.stride = word(offset + 3 * sizeof(uint32_t));
    } else if (opcode == 72 && words >= 5 && word(offset + 3 * sizeof(uint32_t)) == 35) {
      member_offsets[{word(offset + sizeof(uint32_t)), word(offset + 2 * sizeof(uint32_t))}] = word(offset + 4 * sizeof(uint32_t));
    }
    else if (opcode == 15 && words >= 4) {
      size_t name_offset = offset + 3 * sizeof(uint32_t);
      size_t name_limit = offset + words * sizeof(uint32_t);
      size_t end = name_offset;
      while (end < name_limit && il[end] != 0) ++end;
      if (end < name_limit && std::string(reinterpret_cast<const char *>(il.data() + name_offset), end - name_offset) == name)
        entry = word(offset + 2 * sizeof(uint32_t));
    }
    offset += words * sizeof(uint32_t);
  }
  auto align_up = [](size_t value, size_t alignment) {
    return alignment == 0 ? value : value + (alignment - value % alignment) % alignment;
  };
  std::function<size_t(uint32_t)> size_for;
  std::function<size_t(uint32_t)> alignment_for;
  alignment_for = [&](uint32_t type_id) -> size_t {
    const auto found = types.find(type_id);
    if (found == types.end()) return 1;
    const auto &type = found->second;
    if (type.kind == 4) return type.storage == 7 ? alignment_for(type.element) : sizeof(cl_mem);
    if (type.kind == 3) return std::min<size_t>(16, alignment_for(type.element) * (type.count == 3 ? 4 : type.count));
    if (type.kind == 7) {
      size_t alignment = 1;
      for (uint32_t member : type.members) alignment = std::max(alignment, alignment_for(member));
      return alignment;
    }
    if (type.kind == 5 || type.kind == 6) return alignment_for(type.element);
    return std::max<size_t>(1, type.bytes);
  };
  size_for = [&](uint32_t type_id) -> size_t {
    const auto found = types.find(type_id);
    if (found == types.end()) return 0;
    const auto &type = found->second;
    if (type.kind == 3) return (type.count == 3 ? 4 : type.count) * size_for(type.element);
    if (type.kind == 4) return type.storage == 7 ? size_for(type.element) : sizeof(cl_mem);
    if (type.kind == 5) return (type.stride != 0 ? type.stride : size_for(type.element)) * type.count;
    if (type.kind == 6) return 0;
    if (type.kind == 7) {
      size_t size = 0;
      size_t alignment = 1;
      for (size_t index = 0; index < type.members.size(); ++index) {
        const uint32_t member = type.members[index];
        const size_t member_alignment = alignment_for(member);
        const auto offset = member_offsets.find({type_id, static_cast<uint32_t>(index)});
        const size_t member_offset = offset == member_offsets.end() ? align_up(size, member_alignment) : offset->second;
        size = member_offset + size_for(member);
        alignment = std::max(alignment, member_alignment);
      }
      return align_up(size, alignment);
    }
    return type.bytes;
  };
  if (entry == 0) return {};
  std::vector<size_t> result;
  bool in_function = false;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if (opcode == 54 && words >= 3) in_function = word(offset + 2 * sizeof(uint32_t)) == entry;
    else if (in_function && opcode == 55 && words >= 2) result.push_back(size_for(word(offset + sizeof(uint32_t))));
    else if (in_function && opcode == 56) break;
    offset += words * sizeof(uint32_t);
  }
  return result;
}

std::vector<KernelParameterMetadata> kernel_parameter_metadata(const std::vector<uint8_t> &il,
                                                               const std::string &name) {
  struct TypeInfo {
    enum class Kind { Aggregate, Pointer } kind = Kind::Aggregate;
    uint32_t pointee = 0;
    uint32_t storage = 0;
    std::vector<uint32_t> members;
  };
  if (il.size() < 5 * sizeof(uint32_t) || il.size() % sizeof(uint32_t) != 0) return {};
  auto word = [&il](size_t offset) {
    uint32_t value = 0;
    std::memcpy(&value, il.data() + offset, sizeof(value));
    return value;
  };
  std::map<uint32_t, TypeInfo> types;
  uint32_t entry = 0;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if ((opcode == 28 || opcode == 29) && words >= 3) {
      TypeInfo info;
      info.members.push_back(word(offset + 2 * sizeof(uint32_t)));
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 30 && words >= 2) {
      TypeInfo info;
      for (size_t index = 2; index < words; ++index)
        info.members.push_back(word(offset + index * sizeof(uint32_t)));
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 32 && words >= 4) {
      TypeInfo info;
      info.kind = TypeInfo::Kind::Pointer;
      info.storage = word(offset + 2 * sizeof(uint32_t));
      info.pointee = word(offset + 3 * sizeof(uint32_t));
      types[word(offset + sizeof(uint32_t))] = std::move(info);
    } else if (opcode == 15 && words >= 4) {
      const size_t name_offset = offset + 3 * sizeof(uint32_t);
      const size_t name_limit = offset + words * sizeof(uint32_t);
      size_t end = name_offset;
      while (end < name_limit && il[end] != 0) ++end;
      if (end < name_limit && std::string(reinterpret_cast<const char *>(il.data() + name_offset), end - name_offset) == name)
        entry = word(offset + 2 * sizeof(uint32_t));
    }
    offset += words * sizeof(uint32_t);
  }
  if (entry == 0) return {};
  std::function<bool(uint32_t)> contains_pointer = [&](uint32_t type_id) {
    const auto found = types.find(type_id);
    if (found == types.end()) return false;
    if (found->second.kind == TypeInfo::Kind::Pointer) return true;
    return std::any_of(found->second.members.begin(), found->second.members.end(), contains_pointer);
  };
  std::vector<KernelParameterMetadata> result;
  bool in_function = false;
  for (size_t offset = 5 * sizeof(uint32_t); offset + sizeof(uint32_t) <= il.size();) {
    const uint32_t instruction = word(offset);
    const size_t words = instruction >> 16;
    const uint32_t opcode = instruction & 0xffff;
    if (words == 0 || offset + words * sizeof(uint32_t) > il.size()) break;
    if (opcode == 54 && words >= 3) in_function = word(offset + 2 * sizeof(uint32_t)) == entry;
    else if (in_function && opcode == 55 && words >= 2) {
      const auto parameter_type = word(offset + sizeof(uint32_t));
      const auto found = types.find(parameter_type);
      const auto pointee = found == types.end() || found->second.kind != TypeInfo::Kind::Pointer
                               ? types.end()
                               : types.find(found->second.pointee);
      result.push_back({pointee != types.end() && pointee->second.kind == TypeInfo::Kind::Aggregate,
                        found != types.end() && found->second.kind == TypeInfo::Kind::Pointer &&
                            found->second.storage == 4,
                        contains_pointer(parameter_type)});
    } else if (in_function && opcode == 56) break;
    offset += words * sizeof(uint32_t);
  }
  return result;
}

std::string source_kernel_text(const std::string &source, const std::string &name) {
  auto identifier = [](char value) {
    const auto character = static_cast<unsigned char>(value);
    return std::isalnum(character) || value == '_';
  };
  size_t position = 0;
  while ((position = source.find(name, position)) != std::string::npos) {
    const bool left_boundary = position == 0 || !identifier(source[position - 1]);
    size_t open = position + name.size();
    const bool right_boundary = open == source.size() || !identifier(source[open]);
    while (open < source.size() && std::isspace(static_cast<unsigned char>(source[open]))) ++open;
    if (!left_boundary || !right_boundary || open == source.size() || source[open] != '(') {
      position += name.size();
      continue;
    }
    const size_t prior_semicolon = source.rfind(';', position);
    const size_t prior_brace = source.rfind('}', position);
    const size_t prior = prior_semicolon == std::string::npos ? prior_brace
                         : prior_brace == std::string::npos ? prior_semicolon
                                                           : std::max(prior_semicolon, prior_brace);
    const size_t start = prior == std::string::npos ? 0 : prior + 1;
    const size_t underscored_kernel = source.find("__kernel", start);
    const size_t plain_kernel = source.find("kernel", start);
    const bool has_kernel_qualifier =
        (underscored_kernel != std::string::npos && underscored_kernel < position) ||
        (plain_kernel != std::string::npos && plain_kernel < position);
    if (!has_kernel_qualifier) {
      position += name.size();
      continue;
    }
    size_t close = open;
    size_t depth = 0;
    for (; close < source.size(); ++close) {
      if (source[close] == '(') ++depth;
      else if (source[close] == ')' && --depth == 0) break;
    }
    if (close == source.size()) return {};
    const size_t body = source.find('{', close + 1);
    const size_t declaration_end = source.find(';', close + 1);
    if (body == std::string::npos || (declaration_end != std::string::npos && declaration_end < body))
      return source.substr(start, close + 1 - start);
    size_t body_end = body;
    depth = 0;
    for (; body_end < source.size(); ++body_end) {
      if (source[body_end] == '{') ++depth;
      else if (source[body_end] == '}' && --depth == 0) {
        ++body_end;
        break;
      }
    }
    return source.substr(start, body_end - start);
  }
  return {};
}

std::array<size_t, 3> source_required_work_group_size(const std::string &source) {
  std::array<size_t, 3> result{0, 0, 0};
  const std::string marker = "reqd_work_group_size(";
  const size_t start = source.find(marker);
  if (start == std::string::npos) return result;
  size_t position = start + marker.size();
  for (size_t index = 0; index < result.size(); ++index) {
    while (position < source.size() && std::isspace(static_cast<unsigned char>(source[position]))) ++position;
    if (position == source.size() || !std::isdigit(static_cast<unsigned char>(source[position]))) return {0, 0, 0};
    size_t value = 0;
    while (position < source.size() && std::isdigit(static_cast<unsigned char>(source[position]))) {
      const size_t digit = static_cast<size_t>(source[position] - '0');
      if (value > (std::numeric_limits<size_t>::max() - digit) / 10) return {0, 0, 0};
      value = value * 10 + digit;
      ++position;
    }
    result[index] = value;
    while (position < source.size() && std::isspace(static_cast<unsigned char>(source[position]))) ++position;
    if (index + 1 < result.size()) {
      if (position == source.size() || source[position] != ',') return {0, 0, 0};
      ++position;
    }
  }
  return result;
}

size_t source_static_local_memory(const std::string &source) {
  size_t total = 0;
  size_t position = 0;
  while ((position = source.find("__local", position)) != std::string::npos) {
    const size_t open = source.find('[', position + 7);
    const size_t close = open == std::string::npos ? std::string::npos : source.find(']', open + 1);
    if (open != std::string::npos && close != std::string::npos) {
      size_t count = 0;
      size_t digit = open + 1;
      while (digit < close && std::isspace(static_cast<unsigned char>(source[digit]))) ++digit;
      while (digit < close && std::isdigit(static_cast<unsigned char>(source[digit]))) {
        count = count * 10 + static_cast<size_t>(source[digit] - '0');
        ++digit;
      }
      size_t type_start = position + 7;
      while (type_start < source.size() && std::isspace(static_cast<unsigned char>(source[type_start]))) ++type_start;
      const size_t type_end = source.find_first_of(" \t\n\r", type_start);
      const std::string type = source.substr(type_start,
                                             type_end == std::string::npos ? open - type_start : type_end - type_start);
      const size_t element_size = type.find("char") != std::string::npos ? 1 :
                                  type.find("short") != std::string::npos ? 2 :
                                  type.find("long") != std::string::npos ? 8 : 4;
      if (count != 0 && count <= (std::numeric_limits<size_t>::max() - total) / element_size)
        total += count * element_size;
    }
    position += 7;
  }
  return total;
}

std::string source_kernel_attributes(const std::string &source) {
  std::string result;
  const std::string marker = "__attribute__((";
  size_t position = 0;
  while ((position = source.find(marker, position)) != std::string::npos) {
    const size_t start = position + marker.size();
    const size_t end = source.find(")))", start);
    if (end == std::string::npos) break;
    if (!result.empty()) result.push_back(' ');
    result.append(source, start, end - start + 1);
    position = end + 3;
  }
  return result;
}

std::vector<KernelArgumentInfo> source_kernel_argument_info(const std::string &source, const std::string &name) {
  std::vector<KernelArgumentInfo> result;
  const size_t name_position = source.find(name);
  if (name_position == std::string::npos) return result;
  const size_t open = source.find('(', name_position + name.size());
  if (open == std::string::npos) return result;
  size_t close = open;
  size_t depth = 0;
  for (; close < source.size(); ++close) {
    if (source[close] == '(') ++depth;
    else if (source[close] == ')' && --depth == 0) break;
  }
  if (close >= source.size()) return result;
  const std::string parameters = source.substr(open + 1, close - open - 1);
  size_t start = 0;
  while (start < parameters.size()) {
    size_t end = start;
    size_t nested = 0;
    for (; end < parameters.size(); ++end) {
      if (parameters[end] == '(') ++nested;
      else if (parameters[end] == ')' && nested != 0) --nested;
      else if (parameters[end] == ',' && nested == 0) break;
    }
    std::string parameter = parameters.substr(start, end - start);
    const auto first = parameter.find_first_not_of(" \t\n\r");
    const auto last = parameter.find_last_not_of(" \t\n\r");
    if (first != std::string::npos) parameter = parameter.substr(first, last - first + 1);
    if (!parameter.empty()) {
      const size_t name_end = parameter.find_last_not_of(" \t\n\r");
      size_t name_start = name_end;
      while (name_start != std::string::npos &&
             (std::isalnum(static_cast<unsigned char>(parameter[name_start])) || parameter[name_start] == '_')) {
        if (name_start == 0) break;
        --name_start;
      }
      if (name_start != 0 || !std::isalnum(static_cast<unsigned char>(parameter[name_start]))) ++name_start;
      KernelArgumentInfo info;
      if (name_start <= name_end) {
        info.name = parameter.substr(name_start, name_end - name_start + 1);
        parameter.erase(name_start);
      }
      std::istringstream tokens(parameter);
      std::vector<std::string> words;
      for (std::string word; tokens >> word;) words.push_back(word);
      std::vector<std::string> type_words;
      bool pointer = false;
      bool unsigned_type = false;
      bool signed_type = false;
      for (auto word : words) {
        if (word == "global") info.address = CL_KERNEL_ARG_ADDRESS_GLOBAL;
        else if (word == "local") info.address = CL_KERNEL_ARG_ADDRESS_LOCAL;
        else if (word == "constant") info.address = CL_KERNEL_ARG_ADDRESS_CONSTANT;
        else if (word == "private") info.address = CL_KERNEL_ARG_ADDRESS_PRIVATE;
        else if (word == "read_only") info.access = CL_KERNEL_ARG_ACCESS_READ_ONLY;
        else if (word == "write_only") info.access = CL_KERNEL_ARG_ACCESS_WRITE_ONLY;
        else if (word == "read_write") info.access = CL_KERNEL_ARG_ACCESS_READ_WRITE;
        else if (word == "const") info.type |= CL_KERNEL_ARG_TYPE_CONST;
        else if (word == "volatile") info.type |= CL_KERNEL_ARG_TYPE_VOLATILE;
        else if (word == "restrict") info.type |= CL_KERNEL_ARG_TYPE_RESTRICT;
        else if (word == "pipe") info.type |= CL_KERNEL_ARG_TYPE_PIPE;
        else {
          if (word.find('*') != std::string::npos) pointer = true;
          word.erase(std::remove(word.begin(), word.end(), '*'), word.end());
          if (word == "unsigned") unsigned_type = true;
          else if (word == "signed") signed_type = true;
          else if (!word.empty()) type_words.push_back(word);
        }
      }
      std::string base = type_words.empty() ? "int" : type_words.back();
      if (base == "signed") base = "int";
      if (base == "unsigned") base = "int";
      if (unsigned_type && base.rfind("u", 0) != 0) base = "u" + base;
      if (signed_type && base == "") base = "int";
      info.type_name = base + (pointer ? "*" : "");
      if (info.address == CL_KERNEL_ARG_ADDRESS_CONSTANT) info.type |= CL_KERNEL_ARG_TYPE_CONST;
      if (!pointer) info.type = CL_KERNEL_ARG_TYPE_NONE;
      result.push_back(std::move(info));
    }
    if (end == parameters.size()) break;
    start = end + 1;
  }
  return result;
}

std::string without_library_option(const std::string &options) {
  std::vector<std::string> arguments;
  if (!split_options(options, &arguments, nullptr)) return options;
  std::ostringstream result;
  bool first = true;
  for (const auto &argument : arguments) {
    if (argument == "-create-library") continue;
    if (!first) result << ' ';
    first = false;
    if (argument.find_first_of(" \t\n\r") == std::string::npos) result << argument;
    else result << '"' << argument << '"';
  }
  return result.str();
}

constexpr uint64_t kProgramBinaryMagic = UINT64_C(0x47454d4f434c4231);
constexpr size_t kProgramBinaryHeaderSize = 32;

std::vector<uint8_t> program_binary(const _cl_program *program) {
  if (program->il.empty()) return {};
  if (program->binary_type == CL_PROGRAM_BINARY_TYPE_NONE) return program->il;

  const std::string &source = program->build_source.empty() ? program->source : program->build_source;
  if (source.size() > std::numeric_limits<uint64_t>::max() - kProgramBinaryHeaderSize ||
      program->il.size() > std::numeric_limits<uint64_t>::max() - kProgramBinaryHeaderSize - source.size()) return {};
  const size_t size = kProgramBinaryHeaderSize + source.size() + program->il.size();
  std::vector<uint8_t> result(size);
  uint32_t type = static_cast<uint32_t>(program->binary_type);
  uint64_t source_size = source.size();
  uint64_t il_size = program->il.size();
  std::memcpy(result.data(), &kProgramBinaryMagic, sizeof(kProgramBinaryMagic));
  std::memcpy(result.data() + 8, &type, sizeof(type));
  std::memcpy(result.data() + 16, &source_size, sizeof(source_size));
  std::memcpy(result.data() + 24, &il_size, sizeof(il_size));
  if (!source.empty()) std::memcpy(result.data() + kProgramBinaryHeaderSize, source.data(), source.size());
  std::memcpy(result.data() + kProgramBinaryHeaderSize + source.size(), program->il.data(), program->il.size());
  return result;
}

bool decode_program_binary(const unsigned char *bytes, size_t size, cl_program_binary_type *type,
                           std::string *source, std::vector<uint8_t> *il) {
  if (bytes == nullptr || size < kProgramBinaryHeaderSize) return false;
  uint64_t magic = 0;
  std::memcpy(&magic, bytes, sizeof(magic));
  if (magic != kProgramBinaryMagic) return false;
  uint32_t encoded_type = 0;
  uint64_t source_size = 0;
  uint64_t il_size = 0;
  std::memcpy(&encoded_type, bytes + 8, sizeof(encoded_type));
  std::memcpy(&source_size, bytes + 16, sizeof(source_size));
  std::memcpy(&il_size, bytes + 24, sizeof(il_size));
  if (encoded_type != CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT && encoded_type != CL_PROGRAM_BINARY_TYPE_LIBRARY &&
      encoded_type != CL_PROGRAM_BINARY_TYPE_EXECUTABLE) return false;
  if (source_size > size - kProgramBinaryHeaderSize || il_size > size - kProgramBinaryHeaderSize - source_size ||
      kProgramBinaryHeaderSize + source_size + il_size != size) return false;
  *type = static_cast<cl_program_binary_type>(encoded_type);
  source->assign(reinterpret_cast<const char *>(bytes + kProgramBinaryHeaderSize), static_cast<size_t>(source_size));
  il->assign(bytes + kProgramBinaryHeaderSize + source_size,
             bytes + kProgramBinaryHeaderSize + source_size + il_size);
  return !il->empty();
}

bool expand_headers(const std::string &source, cl_uint count, const cl_program *headers,
                    const char **names, std::string *expanded, std::string *error) {
  *expanded = source;
  for (cl_uint index = 0; index < count; ++index) {
    auto *header = checked(reinterpret_cast<_cl_program *>(headers[index]), Kind::Program);
    if (header == nullptr || names[index] == nullptr) {
      if (error != nullptr) *error = "invalid OpenCL embedded header";
      return false;
    }
    const std::string quoted = std::string("#include \"") + names[index] + "\"";
    const std::string angled = std::string("#include <") + names[index] + ">";
    size_t position = 0;
    while ((position = expanded->find(quoted, position)) != std::string::npos) {
      expanded->replace(position, quoted.size(), header->source);
      position += header->source.size();
    }
    position = 0;
    while ((position = expanded->find(angled, position)) != std::string::npos) {
      expanded->replace(position, angled.size(), header->source);
      position += header->source.size();
    }
  }
  return true;
}

std::vector<std::string> include_directories(const std::string &options) {
  std::vector<std::string> arguments;
  if (!split_options(options, &arguments, nullptr)) return {};
  std::vector<std::string> result;
  for (size_t index = 0; index < arguments.size(); ++index) {
    if (arguments[index] == "-I" && index + 1 < arguments.size()) result.push_back(arguments[++index]);
    else if (arguments[index].rfind("-I", 0) == 0 && arguments[index].size() > 2) result.push_back(arguments[index].substr(2));
  }
  return result;
}

bool expand_filesystem_includes(const std::string &source, const std::string &options, std::string *expanded,
                                std::string *error, unsigned depth = 0) {
  if (depth > 16) {
    if (error != nullptr) *error = "OpenCL include nesting is too deep";
    return false;
  }
  const auto directories = include_directories(options);
  *expanded = source;
  if (directories.empty()) return true;

  std::ostringstream result;
  size_t line_start = 0;
  while (line_start < source.size()) {
    const size_t line_end = source.find('\n', line_start);
    const size_t length = line_end == std::string::npos ? source.size() - line_start : line_end - line_start;
    const std::string line = source.substr(line_start, length);
    const size_t directive = line.find("#include");
    size_t name_start = directive == std::string::npos ? std::string::npos : directive + 8;
    while (name_start < line.size() && std::isspace(static_cast<unsigned char>(line[name_start]))) ++name_start;
    const bool quoted = name_start < line.size() && (line[name_start] == '"' || line[name_start] == '<');
    const char terminator = quoted ? (line[name_start] == '"' ? '"' : '>') : '\0';
    const size_t name_end = quoted ? line.find(terminator, name_start + 1) : std::string::npos;
    bool replaced = false;
    if (quoted && name_end != std::string::npos && name_end > name_start + 1) {
      const std::string name = line.substr(name_start + 1, name_end - name_start - 1);
      for (const auto &directory : directories) {
        const std::string path = (directory.empty() || directory.back() == '/') ? directory + name : directory + "/" + name;
        std::ifstream header(path);
        if (!header) continue;
        const std::string contents{std::istreambuf_iterator<char>(header), std::istreambuf_iterator<char>()};
        std::string nested;
        if (!expand_filesystem_includes(contents, options, &nested, error, depth + 1)) return false;
        result << nested;
        replaced = true;
        break;
      }
    }
    if (!replaced) result << line;
    if (line_end != std::string::npos) result << '\n';
    if (line_end == std::string::npos) break;
    line_start = line_end + 1;
  }
  *expanded = result.str();
  return true;
}

cl_int wait_queue(_cl_command_queue *queue) {
  if (queue == nullptr) return CL_SUCCESS;
  std::unique_lock lock(queue->pending_mutex);
  queue->pending_condition.wait(lock, [queue] { return queue->pending_commands == 0; });
  if (!queue->pending_failed) return CL_SUCCESS;
  queue->pending_failed = false;
  return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
}

cl_int wait_list(_cl_command_queue *queue, cl_uint count, const cl_event *events) {
  if (count != 0 && events == nullptr) return CL_INVALID_EVENT_WAIT_LIST;
  if (count == 0 && events != nullptr) return queue == nullptr ? CL_INVALID_VALUE : CL_INVALID_EVENT_WAIT_LIST;
  const auto queue_result = wait_queue(queue);
  if (queue_result != CL_SUCCESS) return queue_result;
  std::vector<_cl_event *> checked_events;
  checked_events.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(reinterpret_cast<_cl_event *>(events[index]), Kind::Event);
    if (event == nullptr) return queue == nullptr ? CL_INVALID_EVENT : CL_INVALID_EVENT_WAIT_LIST;
    if (queue != nullptr && event->context != queue->context) return CL_INVALID_CONTEXT;
    checked_events.push_back(event);
  }
  std::unique_lock lock(events_mutex);
  for (auto *event : checked_events) {
    events_condition.wait(lock, [event] { return event->status == CL_COMPLETE || event->status < 0; });
    if (event->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  return CL_SUCCESS;
}

cl_int validate_wait_list(_cl_command_queue *queue, cl_uint count, const cl_event *events) {
  if (count != 0 && events == nullptr) return CL_INVALID_EVENT_WAIT_LIST;
  if (count == 0 && events != nullptr) return queue == nullptr ? CL_INVALID_VALUE : CL_INVALID_EVENT_WAIT_LIST;
  const auto queue_result = wait_queue(queue);
  if (queue_result != CL_SUCCESS) return queue_result;
  std::lock_guard lock(events_mutex);
  for (cl_uint index = 0; index < count; ++index) {
    auto *event = checked(reinterpret_cast<_cl_event *>(events[index]), Kind::Event);
    if (event == nullptr) return queue == nullptr ? CL_INVALID_EVENT : CL_INVALID_EVENT_WAIT_LIST;
    if (queue != nullptr && event->context != queue->context) return CL_INVALID_CONTEXT;
    if (event->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
  }
  return CL_SUCCESS;
}

_cl_event *make_event(_cl_command_queue *queue, cl_command_type command, cl_event *output) {
  if (output == nullptr) return nullptr;
  auto *event = new (std::nothrow) _cl_event;
  if (event == nullptr) return nullptr;
  register_live_object(event);
  event->dispatch = &dispatch;
  event->queue = queue;
  event->context = queue->context;
  event->command = command;
  event->queued = now_nanos();
  event->submitted = event->queued + 1;
  event->started = event->submitted + 1;
  event->ended = event->started + 1;
  retain(queue);
  retain(queue->context);
  {
    std::lock_guard lock(events_mutex);
    live_events.push_back(event);
  }
  *output = reinterpret_cast<cl_event>(event);
  return event;
}

cl_int complete_event(_cl_command_queue *queue, cl_command_type command, cl_uint count, const cl_event *waits,
                      cl_event *output) {
  const auto checked_queue = checked(queue, Kind::Queue);
  if (checked_queue == nullptr) return CL_INVALID_COMMAND_QUEUE;
  const auto waited = wait_list(checked_queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  if (output != nullptr && make_event(checked_queue, command, output) == nullptr) return CL_OUT_OF_HOST_MEMORY;
  return CL_SUCCESS;
}

struct MirrorResult {
  cl_int status = CL_SUCCESS;
  uint64_t bytes = 0;
};

MirrorResult mirror_allocations() {
  auto &state = remote_state();
  if (!state.ready) return {};
  MirrorResult result;
  std::lock_guard lock(remote_mutex);
  std::lock_guard memory_lock(allocations_mutex);
  for (const auto &[_, allocation] : allocations) {
    if (!allocation->externally_mutable.load(std::memory_order_relaxed)) continue;
    if (allocation->size > std::numeric_limits<uint32_t>::max() ||
        gemstone_execution_write_allocation(state.client, allocation->id, 0, allocation->base,
                                            static_cast<uint32_t>(allocation->size)) != 0)
      return {CL_OUT_OF_RESOURCES, result.bytes};
    result.bytes += allocation->size;
  }
  return result;
}

std::vector<gemstone_pointer_relocation> collect_relocations(uint64_t &scanned_bytes) {
  std::vector<gemstone_pointer_relocation> result;
  std::vector<std::shared_ptr<Allocation>> snapshot;
  {
    std::lock_guard lock(allocations_mutex);
    for (const auto &[_, allocation] : allocations)
      if (allocation->externally_mutable.load(std::memory_order_relaxed) || scan_buffer_pointer_relocations())
        snapshot.push_back(allocation);
  }
  for (const auto &source : snapshot) {
    scanned_bytes += source->size;
    const auto *bytes = static_cast<const uint8_t *>(source->base);
    for (auto it = source->pointer_relocations.begin(); it != source->pointer_relocations.end();) {
      const auto &target = it->second;
      const auto target_address = reinterpret_cast<uintptr_t>(target.first->base) + target.second;
      uint64_t value = 0;
      const bool in_range = it->first <= source->size && sizeof(value) <= source->size - it->first;
      if (!in_range) {
        it = source->pointer_relocations.erase(it);
        continue;
      }
      std::memcpy(&value, bytes + it->first, sizeof(value));
      if (value != static_cast<uint64_t>(target_address)) it = source->pointer_relocations.erase(it);
      else ++it;
    }
    for (size_t offset = 0; offset + sizeof(uint64_t) <= source->size; offset += sizeof(uint32_t)) {
      uint64_t value = 0;
      std::memcpy(&value, bytes + offset, sizeof(value));
      size_t target_offset = 0;
      const auto target = find_pointer(reinterpret_cast<const void *>(static_cast<uintptr_t>(value)), &target_offset);
      if (target != nullptr && target_offset < target->size) {
        source->pointer_relocations[offset] = {target, target_offset};
        result.push_back({source->id, kPointerRelocationSentinel, offset, target->id, target_offset});
      }
    }
  }
  for (const auto &source : snapshot) {
    for (const auto &[offset, target] : source->pointer_relocations) {
      const auto found = std::find_if(result.begin(), result.end(), [&](const auto &relocation) {
        return relocation.source_allocation_id == source->id && relocation.source_offset == offset;
      });
      if (found == result.end() && target.first != nullptr && target.second < target.first->size)
        result.push_back({source->id, kPointerRelocationSentinel, offset, target.first->id, target.second});
    }
  }
  return result;
}

cl_int finish_remote(_cl_command_queue *queue, _cl_kernel *kernel, size_t work_dim, const size_t *offset,
                     const size_t *global, const size_t *local, cl_event *output) {
  const uint64_t started = now_nanos();
  uint64_t scanned_bytes = 0;
  uint64_t downloaded_bytes = 0;
  auto &state = remote_state();
  if (!state.ready || kernel->program->remote_id == 0) return CL_OUT_OF_RESOURCES;
  if (work_dim == 0 || work_dim > 3 || global == nullptr) return CL_INVALID_WORK_DIMENSION;
  size_t group[3] = {1, 1, 1};
  size_t count[3] = {1, 1, 1};
  size_t exact[3] = {1, 1, 1};
  size_t origin[3] = {0, 0, 0};
  for (size_t index = 0; index < work_dim; ++index) {
    if (global[index] == 0 || global[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_WORK_SIZE;
    if (offset != nullptr && offset[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_OFFSET;
    exact[index] = global[index];
    if (offset != nullptr) origin[index] = offset[index];
    group[index] = local == nullptr
      ? (kernel->required_work_group_size != std::array<size_t, 3>{0, 0, 0}
          ? kernel->required_work_group_size[index] : (index == 0 ? 32 : 1))
      : local[index];
    if (group[index] == 0 || group[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
    count[index] = (global[index] + group[index] - 1) / group[index];
    if (count[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_GLOBAL_WORK_SIZE;
  }
  if (local == nullptr && kernel->required_work_group_size != std::array<size_t, 3>{0, 0, 0}) {
    for (size_t index = work_dim; index < 3; ++index) {
      group[index] = kernel->required_work_group_size[index];
      if (group[index] == 0 || group[index] > std::numeric_limits<uint32_t>::max()) return CL_INVALID_WORK_GROUP_SIZE;
    }
  }
  std::vector<gemstone_kernel_argument> arguments(kernel->arguments.size());
  std::vector<std::vector<uint8_t>> scalar_storage(kernel->arguments.size());
  std::vector<gemstone_pointer_relocation> relocations;
  for (size_t index = 0; index < kernel->arguments.size(); ++index) {
    const auto &argument = kernel->arguments[index];
    auto &output = arguments[index];
    if (argument.kind == KernelArgument::Kind::Local) {
      output.kind = GEMSTONE_ARGUMENT_LOCAL;
      output.local_size = argument.offset;
    } else if (argument.kind == KernelArgument::Kind::Allocation) {
      output.kind = GEMSTONE_ARGUMENT_ALLOCATION;
      output.allocation_id = argument.allocation->id;
      output.allocation_offset = argument.offset;
    } else if (argument.kind == KernelArgument::Kind::SVM) {
      output.kind = GEMSTONE_ARGUMENT_ALLOCATION;
      output.allocation_id = argument.allocation->id;
      output.allocation_offset = argument.offset;
    } else {
      output.kind = GEMSTONE_ARGUMENT_SCALAR;
      scalar_storage[index] = argument.bytes;
      output.scalar_bytes = scalar_storage[index].data();
      output.scalar_size = static_cast<uint32_t>(scalar_storage[index].size());
      if (index < kernel->parameter_metadata.size() &&
          !kernel->parameter_metadata[index].contains_pointer) continue;
      scanned_bytes += scalar_storage[index].size();
      for (size_t offset = 0; offset + sizeof(uint64_t) <= scalar_storage[index].size(); offset += sizeof(uint32_t)) {
        uint64_t raw = 0;
        std::memcpy(&raw, scalar_storage[index].data() + offset, sizeof(raw));
        size_t target_offset = 0;
        const auto target = find_pointer(reinterpret_cast<const void *>(static_cast<uintptr_t>(raw)), &target_offset);
        if (target != nullptr)
          relocations.push_back({0, static_cast<uint32_t>(index), offset, target->id, target_offset});
      }
    }
  }
  auto memory_relocations = collect_relocations(scanned_bytes);
  relocations.insert(relocations.end(), memory_relocations.begin(), memory_relocations.end());
  const auto mirrored = mirror_allocations();
  if (mirrored.status != CL_SUCCESS) return mirrored.status;
  const uint64_t uploaded_bytes = mirrored.bytes;
  const gemstone_dimensions group_size{static_cast<uint32_t>(group[0]), static_cast<uint32_t>(group[1]), static_cast<uint32_t>(group[2])};
  const gemstone_dimensions group_count{static_cast<uint32_t>(count[0]), static_cast<uint32_t>(count[1]), static_cast<uint32_t>(count[2])};
  const gemstone_dimensions global_size{static_cast<uint32_t>(exact[0]), static_cast<uint32_t>(exact[1]), static_cast<uint32_t>(exact[2])};
  const gemstone_dimensions global_offset{static_cast<uint32_t>(origin[0]), static_cast<uint32_t>(origin[1]),
                                          static_cast<uint32_t>(origin[2])};
  uint64_t completion = 0;
  int launched;
  std::string failure_message;
  {
    std::lock_guard lock(remote_mutex);
    launched = relocations.empty()
      ? gemstone_execution_launch_with_geometry(state.client, kernel->program->remote_id, kernel->name.c_str(),
                                                group_size, group_count, global_size, static_cast<uint32_t>(work_dim),
                                                global_offset, 0, 0, arguments.data(),
                                                static_cast<uint32_t>(arguments.size()), &completion)
      : gemstone_execution_launch_with_geometry_and_relocations(
          state.client, kernel->program->remote_id, kernel->name.c_str(), group_size, group_count, global_size,
          static_cast<uint32_t>(work_dim), global_offset, 0, 0, arguments.data(),
          static_cast<uint32_t>(arguments.size()), relocations.data(), static_cast<uint32_t>(relocations.size()),
          &completion);
    if (launched == 0) {
      int status = 1;
      while (status == 1) status = gemstone_execution_query_completion(state.client, completion);
      if (status != 0) failure_message = remote_error();
      if (status == 0) {
        uint32_t count_ranges = 0;
        if (gemstone_execution_completion_dirty_ranges(state.client, completion, nullptr, 0, &count_ranges) != 0) {
          status = -1;
        } else if (count_ranges != 0) {
          std::vector<gemstone_dirty_range> ranges(count_ranges);
          if (gemstone_execution_completion_dirty_ranges(state.client, completion, ranges.data(), count_ranges, &count_ranges) == 0) {
            for (const auto &range : ranges) {
              std::shared_ptr<Allocation> allocation;
              {
                std::lock_guard memory_lock(allocations_mutex);
                for (const auto &[_, candidate] : allocations)
                  if (candidate->id == range.allocation_id) allocation = candidate;
              }
              if (allocation == nullptr || !valid_range(allocation, range.offset, range.size)) {
                status = -1;
                break;
              }
              invalidate_pointer_relocations(allocation, range.offset, range.size);
              if (gemstone_execution_read_allocation(state.client, allocation->id, range.offset,
                                                     static_cast<uint8_t *>(allocation->base) + range.offset,
                                                     range.size) != 0) {
                status = -1;
                break;
              }
              downloaded_bytes += range.size;
            }
          } else status = -1;
        }
        if (status == 0) {
          uint32_t count_relocations = 0;
          if (gemstone_execution_completion_pointer_relocations(
                  state.client, completion, nullptr, 0, &count_relocations) != 0) {
            status = -1;
          } else if (count_relocations != 0) {
            std::vector<gemstone_pointer_relocation> output_relocations(count_relocations);
            if (gemstone_execution_completion_pointer_relocations(
                    state.client, completion, output_relocations.data(), count_relocations, &count_relocations) != 0) {
              status = -1;
            } else {
              for (const auto &relocation : output_relocations) {
                const auto source = find_allocation(relocation.source_allocation_id);
                const auto target = find_allocation(relocation.target_allocation_id);
                if (!valid_range(source, relocation.source_offset, sizeof(uintptr_t)) ||
                    target == nullptr || relocation.target_offset > target->size) {
                  status = -1;
                  break;
                }
                const auto address = reinterpret_cast<uintptr_t>(target->base) + relocation.target_offset;
                std::memcpy(static_cast<uint8_t *>(source->base) + relocation.source_offset,
                            &address, sizeof(address));
                source->pointer_relocations[relocation.source_offset] = {target, relocation.target_offset};
              }
            }
          }
        }
      }
      if (gemstone_execution_release_completion(state.client, completion) != 0) {
        if (failure_message.empty()) failure_message = remote_error();
        status = -1;
      }
      if (status != 0) launched = -1;
    }
  }
  if (launched != 0) {
    std::fprintf(stderr, "Gemstone OpenCL launch failed: %s\n",
                 failure_message.empty() ? remote_error() : failure_message.c_str());
    return CL_OUT_OF_RESOURCES;
  }
  if (std::getenv("GEMSTONE_PERF_TRACE") != nullptr)
    std::fprintf(stderr,
                 "Gemstone OpenCL perf: kernel=%s upload=%llu download=%llu scan=%llu relocations=%llu elapsed_us=%llu\n",
                 kernel->name.c_str(), static_cast<unsigned long long>(uploaded_bytes),
                 static_cast<unsigned long long>(downloaded_bytes), static_cast<unsigned long long>(scanned_bytes),
                 static_cast<unsigned long long>(relocations.size()),
                 static_cast<unsigned long long>((now_nanos() - started) / 1000));
  return complete_event(queue, CL_COMMAND_NDRANGE_KERNEL, 0, nullptr, output);
}

} // namespace

extern "C" {

CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddress(const char *name);
CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddressForPlatform(cl_platform_id value, const char *name);

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformIDs(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initialize_dispatch();
  (void)remote_state();
  if (entries == 0 && platforms != nullptr) return CL_INVALID_VALUE;
  if (platforms != nullptr) platforms[0] = reinterpret_cast<cl_platform_id>(&platform);
  if (count != nullptr) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clIcdGetPlatformIDsKHR(cl_uint entries, cl_platform_id *platforms, cl_uint *count) {
  initialize_dispatch();
  return clGetPlatformIDs(entries, platforms, count);
}

CL_API_ENTRY void *CL_API_CALL clIcdGetFunctionAddressForPlatformKHR(cl_platform_id value, const char *name) {
  return clGetExtensionFunctionAddressForPlatform(value, name);
}

CL_API_ENTRY cl_int CL_API_CALL clIcdSetPlatformDispatchDataKHR(cl_platform_id value, void *) {
  return valid_platform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}

CL_API_ENTRY cl_int CL_API_CALL clGetPlatformInfo(cl_platform_id value, cl_platform_info name, size_t size, void *output,
                                                  size_t *written) {
  if (!valid_platform(value)) return CL_INVALID_PLATFORM;
  switch (name) {
    case CL_PLATFORM_PROFILE: return output_string(size, output, written, "FULL_PROFILE");
    case CL_PLATFORM_VERSION: return output_string(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_PLATFORM_NAME: return output_string(size, output, written, "Intel(R) OpenCL Gemstone");
    case CL_PLATFORM_VENDOR: return output_string(size, output, written, "Intel(R) Corporation");
    case CL_PLATFORM_ICD_SUFFIX_KHR: return output_string(size, output, written, "GEMSTONE");
    case CL_PLATFORM_EXTENSIONS:
      return output_string(size, output, written,
        "cl_khr_icd cl_khr_il_program cl_khr_byte_addressable_store "
        "cl_khr_fp64 "
        "cl_khr_global_int32_base_atomics cl_khr_global_int32_extended_atomics "
        "cl_khr_local_int32_base_atomics cl_khr_local_int32_extended_atomics "
        "cl_khr_int64_base_atomics cl_khr_int64_extended_atomics "
        "cl_intel_subgroups cl_intel_spirv_subgroups cl_intel_device_attribute_query "
        "cl_intel_required_subgroup_size "
        "cl_intel_unified_shared_memory");
    case CL_PLATFORM_EXTENSIONS_WITH_VERSION: {
      static const cl_name_version extensions[] = {
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_icd"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_il_program"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_byte_addressable_store"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp64"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_subgroups"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_spirv_subgroups"},
        {CL_MAKE_VERSION(0, 0, 0), "cl_intel_device_attribute_query"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_required_subgroup_size"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_unified_shared_memory"}
      };
      return output_bytes(size, output, written, extensions, sizeof(extensions));
    }
    case CL_PLATFORM_HOST_TIMER_RESOLUTION: { cl_ulong value = 0; return output_scalar(size, output, written, value); }
    case CL_PLATFORM_NUMERIC_VERSION: { cl_version value = CL_MAKE_VERSION(3, 0, 0); return output_scalar(size, output, written, value); }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceIDs(cl_platform_id value, cl_device_type type, cl_uint entries,
                                               cl_device_id *devices, cl_uint *count) {
  if (!valid_platform(value)) return CL_INVALID_PLATFORM;
  constexpr cl_device_type valid_types = CL_DEVICE_TYPE_DEFAULT | CL_DEVICE_TYPE_CPU | CL_DEVICE_TYPE_GPU |
                                          CL_DEVICE_TYPE_ACCELERATOR | CL_DEVICE_TYPE_CUSTOM;
  if (type == 0 || (type != CL_DEVICE_TYPE_ALL && (type & ~valid_types) != 0)) return CL_INVALID_DEVICE_TYPE;
  if (type != CL_DEVICE_TYPE_GPU && type != CL_DEVICE_TYPE_DEFAULT && type != CL_DEVICE_TYPE_ALL &&
      (type & CL_DEVICE_TYPE_GPU) == 0)
    return CL_DEVICE_NOT_FOUND;
  if (entries == 0 && devices != nullptr) return CL_INVALID_VALUE;
  if (devices != nullptr) devices[0] = reinterpret_cast<cl_device_id>(&device);
  if (devices == nullptr && count == nullptr) return CL_INVALID_VALUE;
  if (count != nullptr) *count = 1;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainDevice(cl_device_id value) {
  // Gemstone exposes one root device. It is owned by the ICD for the
  // lifetime of the process, so retaining it is a successful no-op rather
  // than a reference-count transition that could invalidate the static
  // device object.
  return valid_device(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clReleaseDevice(cl_device_id value) {
  // The root device remains available until the ICD is unloaded.
  return valid_device(value) ? CL_SUCCESS : CL_INVALID_DEVICE;
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceInfo(cl_device_id value, cl_device_info name, size_t size, void *output,
                                                size_t *written) {
  if (!valid_device(value)) return CL_INVALID_DEVICE;
  static const size_t work_sizes[] = {1024, 1024, 1024};
  // The provider completes each enqueue synchronously through the execution
  // RPC.  It therefore remains serialized even when an out-of-order queue is
  // requested, but it can still honor the OpenCL queue property: out-of-order
  // execution permits reordering; it does not require concurrent execution.
  static const cl_command_queue_properties queue_properties =
    CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  static const cl_device_svm_capabilities svm = CL_DEVICE_SVM_COARSE_GRAIN_BUFFER | CL_DEVICE_SVM_FINE_GRAIN_BUFFER;
  static const cl_device_fp_config fp = CL_FP_INF_NAN | CL_FP_ROUND_TO_NEAREST;
  static const cl_device_fp_config double_fp =
    CL_FP_FMA | CL_FP_ROUND_TO_NEAREST | CL_FP_ROUND_TO_ZERO |
    CL_FP_ROUND_TO_INF | CL_FP_INF_NAN | CL_FP_DENORM;
  switch (name) {
    case CL_DEVICE_TYPE: { cl_device_type v = CL_DEVICE_TYPE_GPU; return output_scalar(size, output, written, v); }
    case CL_DEVICE_VENDOR_ID: { cl_uint v = 0x8086; return output_scalar(size, output, written, v); }
    case CL_DEVICE_IP_VERSION_INTEL: { cl_uint v = 0x00010000; return output_scalar(size, output, written, v); }
    case CL_DEVICE_ID_INTEL: { cl_uint v = 22177; return output_scalar(size, output, written, v); }
    case CL_DEVICE_NUM_SLICES_INTEL: { cl_uint v = 1; return output_scalar(size, output, written, v); }
    case CL_DEVICE_NUM_SUB_SLICES_PER_SLICE_INTEL: { cl_uint v = 2; return output_scalar(size, output, written, v); }
    case CL_DEVICE_NUM_EUS_PER_SUB_SLICE_INTEL: { cl_uint v = 8; return output_scalar(size, output, written, v); }
    case CL_DEVICE_NUM_THREADS_PER_EU_INTEL: { cl_uint v = 7; return output_scalar(size, output, written, v); }
    case CL_DEVICE_FEATURE_CAPABILITIES_INTEL: {
      cl_device_feature_capabilities_intel v = 0;
      return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_COMPUTE_UNITS: { cl_uint v = 16; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_WORK_ITEM_DIMENSIONS: { cl_uint v = 3; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_WORK_GROUP_SIZE: { size_t v = 1024; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_WORK_ITEM_SIZES: return output_bytes(size, output, written, work_sizes, sizeof(work_sizes));
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_CHAR: case CL_DEVICE_PREFERRED_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_INT: case CL_DEVICE_PREFERRED_VECTOR_WIDTH_LONG:
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_FLOAT: {
      cl_uint v = 1; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_DOUBLE: {
      cl_uint v = 1; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_VECTOR_WIDTH_HALF: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_CHAR: case CL_DEVICE_NATIVE_VECTOR_WIDTH_SHORT:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_INT: case CL_DEVICE_NATIVE_VECTOR_WIDTH_LONG:
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_FLOAT: {
      cl_uint v = 1; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_DOUBLE: {
      cl_uint v = 1; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_NATIVE_VECTOR_WIDTH_HALF: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_CLOCK_FREQUENCY: { cl_uint v = 1000; return output_scalar(size, output, written, v); }
    case CL_DEVICE_ADDRESS_BITS: { cl_uint v = 64; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_MEM_ALLOC_SIZE: { const cl_ulong v = max_mem_alloc_size(); return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_READ_IMAGE_ARGS: case CL_DEVICE_MAX_WRITE_IMAGE_ARGS:
    case CL_DEVICE_MAX_READ_WRITE_IMAGE_ARGS: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_IMAGE2D_MAX_WIDTH: case CL_DEVICE_IMAGE2D_MAX_HEIGHT:
    case CL_DEVICE_IMAGE3D_MAX_WIDTH: case CL_DEVICE_IMAGE3D_MAX_HEIGHT:
    case CL_DEVICE_IMAGE3D_MAX_DEPTH: case CL_DEVICE_IMAGE_MAX_BUFFER_SIZE:
    case CL_DEVICE_IMAGE_MAX_ARRAY_SIZE: {
      size_t v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_IMAGE_PITCH_ALIGNMENT: case CL_DEVICE_IMAGE_BASE_ADDRESS_ALIGNMENT: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PARTITION_MAX_SUB_DEVICES: { cl_uint v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_PARTITION_AFFINITY_DOMAIN: { cl_device_affinity_domain v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_PARTITION_PROPERTIES: {
      static const cl_device_partition_property properties[] = {0};
      return output_bytes(size, output, written, properties, sizeof(properties));
    }
    case CL_DEVICE_PARTITION_TYPE: {
      static const cl_device_partition_property type[] = {0};
      return output_bytes(size, output, written, type, sizeof(type));
    }
    case CL_DEVICE_PARENT_DEVICE: { cl_device_id v = nullptr; return output_scalar(size, output, written, v); }
    case CL_DEVICE_REFERENCE_COUNT: { cl_uint v = 1; return output_scalar(size, output, written, v); }
    case CL_DEVICE_IMAGE_SUPPORT: { cl_bool v = CL_FALSE; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_PARAMETER_SIZE: { size_t v = 4096; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_SAMPLERS: { cl_uint v = 16; return output_scalar(size, output, written, v); }
    case CL_DEVICE_SINGLE_FP_CONFIG: return output_scalar(size, output, written, fp);
    case CL_DEVICE_GLOBAL_MEM_CACHE_TYPE: { cl_uint v = CL_NONE; return output_scalar(size, output, written, v); }
    case CL_DEVICE_GLOBAL_MEM_CACHELINE_SIZE: { cl_uint v = 64; return output_scalar(size, output, written, v); }
    case CL_DEVICE_GLOBAL_MEM_CACHE_SIZE: { cl_ulong v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_GLOBAL_MEM_SIZE: { cl_ulong v = UINT64_C(0x80000000); return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_CONSTANT_BUFFER_SIZE: { cl_ulong v = UINT64_C(0x10000); return output_scalar(size, output, written, v); }
    case CL_DEVICE_MAX_CONSTANT_ARGS: { cl_uint v = 8; return output_scalar(size, output, written, v); }
    case CL_DEVICE_LOCAL_MEM_TYPE: { cl_uint v = CL_LOCAL; return output_scalar(size, output, written, v); }
    case CL_DEVICE_LOCAL_MEM_SIZE: { const cl_ulong v = local_mem_size(); return output_scalar(size, output, written, v); }
    // This provider is an emulator, so fp64 is a supported device feature.
    // DPC++/IGC lowers it through Intel's DP emulation path, enabled by the
    // environment supplied by the oneDPL conformance harness.
    case CL_DEVICE_DOUBLE_FP_CONFIG: return output_scalar(size, output, written, double_fp);
    case CL_DEVICE_MEM_BASE_ADDR_ALIGN: { cl_uint v = 1024; return output_scalar(size, output, written, v); }
    case CL_DEVICE_MIN_DATA_TYPE_ALIGN_SIZE: { cl_uint v = 16; return output_scalar(size, output, written, v); }
    case CL_DEVICE_ERROR_CORRECTION_SUPPORT: case CL_DEVICE_PREFERRED_INTEROP_USER_SYNC: {
      cl_bool v = CL_FALSE; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PROFILING_TIMER_RESOLUTION: { size_t v = 1; return output_scalar(size, output, written, v); }
    case CL_DEVICE_ENDIAN_LITTLE: case CL_DEVICE_AVAILABLE: case CL_DEVICE_COMPILER_AVAILABLE:
    case CL_DEVICE_LINKER_AVAILABLE: case CL_DEVICE_HOST_UNIFIED_MEMORY: {
      cl_bool v = CL_TRUE; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_EXECUTION_CAPABILITIES: { cl_device_exec_capabilities v = CL_EXEC_KERNEL; return output_scalar(size, output, written, v); }
    case CL_DEVICE_QUEUE_PROPERTIES: return output_scalar(size, output, written, queue_properties);
    case CL_DEVICE_MAX_GLOBAL_VARIABLE_SIZE: case CL_DEVICE_GLOBAL_VARIABLE_PREFERRED_TOTAL_SIZE: {
      size_t v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_MAX_PIPE_ARGS: case CL_DEVICE_PIPE_MAX_ACTIVE_RESERVATIONS: case CL_DEVICE_PIPE_MAX_PACKET_SIZE: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PREFERRED_PLATFORM_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_GLOBAL_ATOMIC_ALIGNMENT:
    case CL_DEVICE_PREFERRED_LOCAL_ATOMIC_ALIGNMENT: {
      cl_uint v = 0; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_NAME: return output_string(size, output, written, "Gemstone SPIR-V GPU");
    case CL_DEVICE_VENDOR: return output_string(size, output, written, "Intel(R) Corporation");
    case CL_DRIVER_VERSION: return output_string(size, output, written, "26.22.38646.6");
    case CL_DEVICE_PROFILE: return output_string(size, output, written, "FULL_PROFILE");
    case CL_DEVICE_VERSION: return output_string(size, output, written, "OpenCL 3.0 Gemstone");
    case CL_DEVICE_OPENCL_C_VERSION: return output_string(size, output, written, "OpenCL C 1.2 Gemstone");
    case CL_DEVICE_EXTENSIONS: return output_string(size, output, written,
      "cl_khr_icd cl_khr_il_program cl_khr_byte_addressable_store "
      "cl_khr_fp64 "
      "cl_khr_global_int32_base_atomics cl_khr_global_int32_extended_atomics "
      "cl_khr_local_int32_base_atomics cl_khr_local_int32_extended_atomics "
      "cl_khr_int64_base_atomics cl_khr_int64_extended_atomics "
      "cl_intel_subgroups cl_intel_spirv_subgroups cl_intel_device_attribute_query "
      "cl_intel_required_subgroup_size "
      "cl_intel_unified_shared_memory");
    case CL_DEVICE_NUMERIC_VERSION: { cl_version v = CL_MAKE_VERSION(3, 0, 0); return output_scalar(size, output, written, v); }
    case CL_DEVICE_EXTENSIONS_WITH_VERSION: {
      static const cl_name_version extensions[] = {
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_icd"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_il_program"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_byte_addressable_store"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_fp64"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_global_int32_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_local_int32_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_base_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_khr_int64_extended_atomics"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_subgroups"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_spirv_subgroups"},
        {CL_MAKE_VERSION(0, 0, 0), "cl_intel_device_attribute_query"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_required_subgroup_size"},
        {CL_MAKE_VERSION(1, 0, 0), "cl_intel_unified_shared_memory"}
      };
      return output_bytes(size, output, written, extensions, sizeof(extensions));
    }
    case CL_DEVICE_ILS_WITH_VERSION: {
      static const cl_name_version il_versions[] = {{CL_MAKE_VERSION(1, 0, 0), "SPIR-V"}};
      return output_bytes(size, output, written, il_versions, sizeof(il_versions));
    }
    case CL_DEVICE_BUILT_IN_KERNELS: return output_string(size, output, written, "");
    case CL_DEVICE_BUILT_IN_KERNELS_WITH_VERSION: {
      if (written != nullptr) *written = 0;
      return output == nullptr || size == 0 ? CL_SUCCESS : CL_INVALID_VALUE;
    }
    case CL_DEVICE_OPENCL_C_ALL_VERSIONS: {
      static const cl_name_version versions[] = {
        {CL_MAKE_VERSION(1, 0, 0), "OpenCL C"},
        {CL_MAKE_VERSION(1, 1, 0), "OpenCL C"},
        {CL_MAKE_VERSION(1, 2, 0), "OpenCL C"},
        {CL_MAKE_VERSION(3, 0, 0), "OpenCL C"}
      };
      return output_bytes(size, output, written, versions, sizeof(versions));
    }
    case CL_DEVICE_OPENCL_C_FEATURES: {
      static const cl_name_version features[] = {
        {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_int64"},
        {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_fp64"},
        {CL_MAKE_VERSION(3, 0, 0), "__opencl_c_subgroups"}
      };
      return output_bytes(size, output, written, features, sizeof(features));
    }
    case CL_DEVICE_ATOMIC_MEMORY_CAPABILITIES: {
      cl_device_atomic_capabilities v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP;
      return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_ATOMIC_FENCE_CAPABILITIES: {
      cl_device_atomic_capabilities v = CL_DEVICE_ATOMIC_ORDER_RELAXED | CL_DEVICE_ATOMIC_ORDER_ACQ_REL |
                                        CL_DEVICE_ATOMIC_SCOPE_WORK_GROUP;
      return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_WORK_GROUP_COLLECTIVE_FUNCTIONS_SUPPORT: case CL_DEVICE_GENERIC_ADDRESS_SPACE_SUPPORT:
    case CL_DEVICE_SUB_GROUP_INDEPENDENT_FORWARD_PROGRESS: {
      cl_bool v = CL_FALSE; return output_scalar(size, output, written, v);
    }
    case CL_DEVICE_PRINTF_BUFFER_SIZE: { size_t v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_LATEST_CONFORMANCE_VERSION_PASSED:
      return output_string(size, output, written, "v2026-07-09-00");
    case CL_DEVICE_PLATFORM: { cl_platform_id v = reinterpret_cast<cl_platform_id>(&platform); return output_scalar(size, output, written, v); }
    case CL_DEVICE_SVM_CAPABILITIES: return output_scalar(size, output, written, svm);
    case CL_DEVICE_IL_VERSION: return output_string(size, output, written, "SPIR-V_1.0");
    case CL_DEVICE_MAX_NUM_SUB_GROUPS: { cl_uint v = 32; return output_scalar(size, output, written, v); }
    case CL_DEVICE_SUB_GROUP_SIZES_INTEL: {
      static const size_t sizes[] = {8, 16, 32};
      return output_bytes(size, output, written, sizes, sizeof(sizes));
    }
    case CL_DEVICE_NON_UNIFORM_WORK_GROUP_SUPPORT: { cl_bool v = CL_FALSE; return output_scalar(size, output, written, v); }
    case CL_DEVICE_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: { size_t v = 32; return output_scalar(size, output, written, v); }
    case CL_DEVICE_QUEUE_ON_DEVICE_PROPERTIES: { cl_command_queue_properties v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_QUEUE_ON_DEVICE_PREFERRED_SIZE: case CL_DEVICE_QUEUE_ON_DEVICE_MAX_SIZE:
    case CL_DEVICE_MAX_ON_DEVICE_QUEUES: case CL_DEVICE_MAX_ON_DEVICE_EVENTS: { cl_uint v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_DEVICE_ENQUEUE_CAPABILITIES: { cl_device_device_enqueue_capabilities v = 0; return output_scalar(size, output, written, v); }
    case CL_DEVICE_PIPE_SUPPORT: { cl_bool v = CL_FALSE; return output_scalar(size, output, written, v); }
    case CL_DEVICE_HOST_MEM_CAPABILITIES_INTEL: case CL_DEVICE_DEVICE_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_SINGLE_DEVICE_SHARED_MEM_CAPABILITIES_INTEL: case CL_DEVICE_CROSS_DEVICE_SHARED_MEM_CAPABILITIES_INTEL:
    case CL_DEVICE_SHARED_SYSTEM_MEM_CAPABILITIES_INTEL: {
      cl_device_unified_shared_memory_capabilities_intel v = CL_UNIFIED_SHARED_MEMORY_ACCESS_INTEL |
                                                            CL_UNIFIED_SHARED_MEMORY_CONCURRENT_ACCESS_INTEL;
      return output_scalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clGetDeviceAndHostTimer(cl_device_id value, cl_ulong *device_timestamp,
                                                        cl_ulong *host_timestamp) {
  if (!valid_device(value)) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_int CL_API_CALL clGetHostTimer(cl_device_id value, cl_ulong *host_timestamp) {
  if (!valid_device(value)) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContext(const cl_context_properties *properties, cl_uint count,
                                                    const cl_device_id *devices,
                                                    void (CL_CALLBACK *notify)(const char *, const void *, size_t, void *),
                                                    void *user, cl_int *error) {
  if (properties != nullptr) {
    bool platform_seen = false;
    bool interop_seen = false;
    for (size_t index = 0; properties[index] != 0; index += 2) {
      const cl_context_properties property = properties[index];
      const cl_context_properties value = properties[index + 1];
      if (property == CL_CONTEXT_PLATFORM) {
        if (platform_seen) {
          if (error) *error = CL_INVALID_PROPERTY;
          return nullptr;
        }
        if (reinterpret_cast<cl_platform_id>(value) != reinterpret_cast<cl_platform_id>(&platform)) {
          if (error) *error = CL_INVALID_PLATFORM;
          return nullptr;
        }
        platform_seen = true;
      } else if (property == CL_CONTEXT_INTEROP_USER_SYNC) {
        if (interop_seen || (value != CL_TRUE && value != CL_FALSE)) {
          if (error) *error = CL_INVALID_PROPERTY;
          return nullptr;
        }
        interop_seen = true;
      } else {
        if (error) *error = CL_INVALID_PROPERTY;
        return nullptr;
      }
    }
  }
  if (count != 1 || devices == nullptr || !valid_device(devices[0])) { if (error) *error = CL_INVALID_DEVICE; return nullptr; }
  if (notify == nullptr && user != nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  auto *result = new (std::nothrow) _cl_context;
  if (result == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(result);
  result->dispatch = &dispatch;
  result->device = &device;
  if (properties != nullptr) {
    for (size_t index = 0; properties[index] != 0; index += 2) {
      result->properties.push_back(properties[index]);
      result->properties.push_back(properties[index + 1]);
    }
    result->properties.push_back(0);
  }
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_context>(result);
}

CL_API_ENTRY cl_context CL_API_CALL clCreateContextFromType(const cl_context_properties *properties, cl_device_type type,
                                                            void (CL_CALLBACK *notify)(const char *, const void *, size_t, void *), void *user,
                                                            cl_int *error) {
  cl_device_id selected = nullptr;
  cl_int result = clGetDeviceIDs(reinterpret_cast<cl_platform_id>(&platform), type, 1, &selected, nullptr);
  if (result != CL_SUCCESS) { if (error) *error = result; return nullptr; }
  return clCreateContext(properties, 1, &selected, notify, user, error);
}

CL_API_ENTRY cl_int CL_API_CALL clRetainContext(cl_context value) { auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context); if (!context) return CL_INVALID_CONTEXT; retain(context); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseContext(cl_context value) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (release(context)) {
    for (auto callback = context->destructor_callbacks.rbegin(); callback != context->destructor_callbacks.rend(); ++callback)
      if (callback->first != nullptr) callback->first(value, callback->second);
    unregister_live_object(context);
    delete context;
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clSetContextDestructorCallback(
    cl_context value, void (CL_CALLBACK *callback)(cl_context, void *), void *user) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (callback == nullptr) return CL_INVALID_VALUE;
  try {
    context->destructor_callbacks.emplace_back(callback, user);
  } catch (const std::bad_alloc &) {
    return CL_OUT_OF_HOST_MEMORY;
  }
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetContextInfo(cl_context value, cl_context_info name, size_t size, void *output, size_t *written) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context); if (!context) return CL_INVALID_CONTEXT;
  switch (name) {
    case CL_CONTEXT_REFERENCE_COUNT: { cl_uint v = context->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_CONTEXT_NUM_DEVICES: { cl_uint v = 1; return output_scalar(size, output, written, v); }
    case CL_CONTEXT_DEVICES: { cl_device_id v = reinterpret_cast<cl_device_id>(&device); return output_scalar(size, output, written, v); }
    case CL_CONTEXT_PROPERTIES:
      return output_bytes(size, output, written, context->properties.data(),
                          context->properties.size() * sizeof(cl_context_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueue(cl_context value, cl_device_id selected, cl_command_queue_properties properties, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  if (!valid_device(selected) || context->device != &device) { if (error) *error = CL_INVALID_DEVICE; return nullptr; }
  constexpr cl_command_queue_properties known_properties =
    CL_QUEUE_OUT_OF_ORDER_EXEC_MODE_ENABLE | CL_QUEUE_PROFILING_ENABLE;
  if ((properties & ~known_properties) != 0) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  auto *queue = new (std::nothrow) _cl_command_queue;
  if (!queue) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(queue);
  queue->dispatch = &dispatch;
  queue->context = context; queue->device = &device; queue->properties = properties;
  retain(context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_command_queue>(queue);
}

CL_API_ENTRY cl_command_queue CL_API_CALL clCreateCommandQueueWithProperties(cl_context context, cl_device_id device_value,
                                                                               const cl_queue_properties *properties, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(context), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (!valid_device(device_value)) {
    if (error) *error = CL_INVALID_DEVICE;
    return nullptr;
  }
  cl_command_queue_properties queue_properties = 0;
  if (properties != nullptr) for (size_t index = 0; properties[index] != 0; index += 2) {
    if (properties[index] != CL_QUEUE_PROPERTIES) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
    queue_properties = static_cast<cl_command_queue_properties>(properties[index + 1]);
  }
  auto result = clCreateCommandQueue(context, device_value, queue_properties, error);
  if (result != nullptr && properties != nullptr) {
    auto *queue = reinterpret_cast<_cl_command_queue *>(result);
    for (size_t index = 0; properties[index] != 0; index += 2) {
      queue->properties_array.push_back(properties[index]);
      queue->properties_array.push_back(properties[index + 1]);
    }
    queue->properties_array.push_back(0);
  }
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clSetDefaultDeviceCommandQueue(cl_context value, cl_device_id selected,
                                                               cl_command_queue) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context) return CL_INVALID_CONTEXT;
  if (!valid_device(selected) || context->device != &device) return CL_INVALID_DEVICE;
  return CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainCommandQueue(cl_command_queue value) { auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue); if (!queue) return CL_INVALID_COMMAND_QUEUE; retain(queue); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseCommandQueue(cl_command_queue value) { auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue); if (!queue) return CL_INVALID_COMMAND_QUEUE; if (release(queue)) { auto *context = queue->context; unregister_live_object(queue); delete queue; clReleaseContext(reinterpret_cast<cl_context>(context)); } return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clSetCommandQueueProperty(cl_command_queue value, cl_command_queue_properties,
                                                           cl_bool, cl_command_queue_properties *) {
  return checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue) == nullptr
    ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_OPERATION;
}
CL_API_ENTRY cl_int CL_API_CALL clGetCommandQueueInfo(cl_command_queue value, cl_command_queue_info name, size_t size, void *output, size_t *written) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue); if (!queue) return CL_INVALID_COMMAND_QUEUE;
  switch (name) {
    case CL_QUEUE_CONTEXT: { cl_context v = reinterpret_cast<cl_context>(queue->context); return output_scalar(size, output, written, v); }
    case CL_QUEUE_DEVICE: { cl_device_id v = reinterpret_cast<cl_device_id>(&device); return output_scalar(size, output, written, v); }
    case CL_QUEUE_REFERENCE_COUNT: { cl_uint v = queue->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_QUEUE_PROPERTIES: return output_scalar(size, output, written, queue->properties);
    case CL_QUEUE_PROPERTIES_ARRAY:
      return output_bytes(size, output, written, queue->properties_array.data(),
                          queue->properties_array.size() * sizeof(cl_queue_properties));
    case CL_QUEUE_SIZE: return CL_INVALID_COMMAND_QUEUE;
    case CL_QUEUE_DEVICE_DEFAULT: { cl_command_queue v = nullptr; return output_scalar(size, output, written, v); }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBuffer(cl_context value, cl_mem_flags flags, size_t size, void *host, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context || size == 0) { if (error) *error = context ? CL_INVALID_BUFFER_SIZE : CL_INVALID_CONTEXT; return nullptr; }
  constexpr cl_mem_flags access_flags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  if ((flags & access_flags) == 0) flags |= CL_MEM_READ_WRITE;
  if ((flags & CL_MEM_USE_HOST_PTR) && host == nullptr) { if (error) *error = CL_INVALID_HOST_PTR; return nullptr; }
  if ((flags & CL_MEM_COPY_HOST_PTR) && host == nullptr) { if (error) *error = CL_INVALID_HOST_PTR; return nullptr; }
  size_t host_offset = 0;
  auto allocation = (flags & CL_MEM_USE_HOST_PTR) != 0 ? find_pointer(host, &host_offset) : nullptr;
  const bool aliases_runtime_allocation = allocation != nullptr && allocation->runtime_owns_base;
  if (aliases_runtime_allocation && !valid_range(allocation, host_offset, size)) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return nullptr;
  }
  if (!aliases_runtime_allocation) {
    host_offset = 0;
    allocation = allocate(size, error);
  }
  if (!allocation) return nullptr;
  if (!aliases_runtime_allocation && host != nullptr && (flags & (CL_MEM_USE_HOST_PTR | CL_MEM_COPY_HOST_PTR))) {
    std::memcpy(allocation->base, host, size);
  }
  cl_int write_result = CL_SUCCESS;
  if (remote_state().ready) {
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_write_allocation(remote.client, allocation->id, host_offset,
                                            static_cast<uint8_t *>(allocation->base) + host_offset,
                                            static_cast<uint32_t>(size)) != 0)
      write_result = CL_OUT_OF_RESOURCES;
  }
  if (write_result != CL_SUCCESS) {
    if (!aliases_runtime_allocation) release_allocation(allocation);
    if (error) *error = write_result;
    return nullptr;
  }
  auto *result = new (std::nothrow) _cl_mem;
  if (!result) {
    if (!aliases_runtime_allocation) release_allocation(allocation);
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  register_live_object(result);
  result->dispatch = &dispatch;
  result->context = context; result->allocation = std::move(allocation); result->flags = flags; result->size = size; retain(context);
  result->origin = host_offset;
  result->host_ptr = (flags & CL_MEM_USE_HOST_PTR) == 0 ? nullptr : host;
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_mem>(result);
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateBufferWithProperties(cl_context value, const cl_mem_properties *properties,
                                                             cl_mem_flags flags, size_t size, void *host, cl_int *error) {
  if (properties != nullptr) {
    for (size_t index = 0; properties[index] != 0; index += 2)
      return (error != nullptr ? (*error = CL_INVALID_PROPERTY) : 0), nullptr;
  }
  auto result = clCreateBuffer(value, flags, size, host, error);
  if (result != nullptr && properties != nullptr) {
    auto *buffer = reinterpret_cast<_cl_mem *>(result);
    buffer->properties.push_back(0);
  }
  return result;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreatePipe(cl_context value, cl_mem_flags, cl_uint, cl_uint,
                                             const cl_pipe_properties *, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImage(cl_context value, cl_mem_flags,
                                              const cl_image_format *, const cl_image_desc *, void *, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) {
    if (error) *error = CL_INVALID_CONTEXT;
    return nullptr;
  }
  if (error) *error = CL_INVALID_OPERATION;
  return nullptr;
}

CL_API_ENTRY cl_mem CL_API_CALL clCreateImageWithProperties(cl_context value, const cl_mem_properties *, cl_mem_flags,
                                                            const cl_image_format *, const cl_image_desc *, void *, cl_int *error) {
  return clCreateImage(value, 0, nullptr, nullptr, nullptr, error);
}

CL_API_ENTRY cl_int CL_API_CALL clGetImageInfo(cl_mem value, cl_image_info, size_t, void *, size_t *) {
  return checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory) == nullptr
    ? CL_INVALID_MEM_OBJECT : CL_INVALID_MEM_OBJECT;
}

CL_API_ENTRY cl_int CL_API_CALL clGetPipeInfo(cl_mem value, cl_pipe_info, size_t, void *, size_t *) {
  if (!checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory)) return CL_INVALID_MEM_OBJECT;
  return CL_INVALID_MEM_OBJECT;
}

CL_API_ENTRY cl_int CL_API_CALL clRetainMemObject(cl_mem value) { auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory); if (!mem) return CL_INVALID_MEM_OBJECT; retain(mem); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseMemObject(cl_mem value) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (!mem) return CL_INVALID_MEM_OBJECT;
  if (release(mem)) {
    for (auto callback = mem->destructor_callbacks.rbegin(); callback != mem->destructor_callbacks.rend(); ++callback)
      callback->first(reinterpret_cast<cl_mem>(mem), callback->second);
    auto allocation = mem->allocation;
    auto *context = mem->context;
    auto *parent = mem->parent;
    unregister_live_object(mem);
    delete mem;
    if (!allocation->runtime_owns_base && allocation.use_count() <= 2) release_allocation(allocation);
    clReleaseContext(reinterpret_cast<cl_context>(context));
    if (parent != nullptr) clReleaseMemObject(reinterpret_cast<cl_mem>(parent));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetMemObjectDestructorCallback(cl_mem value,
                                                                 void (CL_CALLBACK *callback)(cl_mem, void *),
                                                                 void *user) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (mem == nullptr) return CL_INVALID_MEM_OBJECT;
  if (callback == nullptr) return CL_INVALID_VALUE;
  mem->destructor_callbacks.emplace_back(callback, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_mem CL_API_CALL clCreateSubBuffer(cl_mem value, cl_mem_flags flags, cl_buffer_create_type type,
                                                  const void *description, cl_int *error) {
  auto *parent = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory);
  if (parent == nullptr) { if (error) *error = CL_INVALID_MEM_OBJECT; return nullptr; }
  if (type != CL_BUFFER_CREATE_TYPE_REGION || description == nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  const auto *region = static_cast<const cl_buffer_region *>(description);
  if (!valid_mem_range(parent, region->origin, region->size) || region->size == 0) {
    if (error) *error = CL_INVALID_BUFFER_SIZE;
    return nullptr;
  }
  auto *result = new (std::nothrow) _cl_mem;
  if (result == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(result);
  result->dispatch = &dispatch;
  result->context = parent->context;
  result->allocation = parent->allocation;
  constexpr cl_mem_flags access_flags = CL_MEM_READ_WRITE | CL_MEM_READ_ONLY | CL_MEM_WRITE_ONLY;
  constexpr cl_mem_flags host_access_flags = CL_MEM_HOST_READ_ONLY | CL_MEM_HOST_WRITE_ONLY | CL_MEM_HOST_NO_ACCESS;
  cl_mem_flags inherited_flags = flags;
  if ((flags & access_flags) == 0) inherited_flags |= parent->flags & access_flags;
  inherited_flags |= parent->flags & (CL_MEM_ALLOC_HOST_PTR | CL_MEM_COPY_HOST_PTR | CL_MEM_USE_HOST_PTR);
  if ((flags & host_access_flags) == 0) inherited_flags |= parent->flags & host_access_flags;
  result->flags = inherited_flags;
  result->host_ptr = parent->host_ptr == nullptr ? nullptr : static_cast<uint8_t *>(parent->host_ptr) + region->origin;
  result->parent = parent;
  result->origin = allocation_offset(parent, region->origin);
  result->size = region->size;
  retain(parent);
  retain(parent->context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_mem>(result);
}
CL_API_ENTRY cl_int CL_API_CALL clGetMemObjectInfo(cl_mem value, cl_mem_info name, size_t size, void *output, size_t *written) {
  auto *mem = checked(reinterpret_cast<_cl_mem *>(value), Kind::Memory); if (!mem) return CL_INVALID_MEM_OBJECT;
  switch (name) {
    case CL_MEM_TYPE: { cl_mem_object_type v = CL_MEM_OBJECT_BUFFER; return output_scalar(size, output, written, v); }
    case CL_MEM_FLAGS: return output_scalar(size, output, written, mem->flags);
    case CL_MEM_SIZE: return output_scalar(size, output, written, mem->size);
    case CL_MEM_HOST_PTR: return output_scalar(size, output, written, mem->host_ptr);
    case CL_MEM_REFERENCE_COUNT: { cl_uint v = mem->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_MEM_CONTEXT: { cl_context v = reinterpret_cast<cl_context>(mem->context); return output_scalar(size, output, written, v); }
    case CL_MEM_ASSOCIATED_MEMOBJECT: { cl_mem v = reinterpret_cast<cl_mem>(mem->parent); return output_scalar(size, output, written, v); }
    case CL_MEM_OFFSET: return output_scalar(size, output, written, mem->origin);
    case CL_MEM_MAP_COUNT: { cl_uint v = static_cast<cl_uint>(mem->mappings.size()); return output_scalar(size, output, written, v); }
    case CL_MEM_PROPERTIES:
      return output_bytes(size, output, written, mem->properties.data(),
                          mem->properties.size() * sizeof(cl_mem_properties));
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithIL(cl_context value, const void *il, size_t length, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (!context || il == nullptr || length == 0 || length > std::numeric_limits<uint32_t>::max()) { if (error) *error = context ? CL_INVALID_VALUE : CL_INVALID_CONTEXT; return nullptr; }
  auto *program = new (std::nothrow) _cl_program; if (!program) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(program);
  program->dispatch = &dispatch;
  program->context = context;
  program->il.assign(static_cast<const uint8_t *>(il), static_cast<const uint8_t *>(il) + length);
  program->kernel_names = extract_kernel_names(program->il);
  retain(context);
  auto &state = remote_state();
  if (state.ready) {
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_load_program(state.client, GEMSTONE_PROGRAM_SPIRV, program->il.data(), static_cast<uint32_t>(program->il.size()), &program->remote_id) != 0) {
      unregister_live_object(program); delete program; clReleaseContext(reinterpret_cast<cl_context>(context)); if (error) *error = CL_INVALID_BINARY; return nullptr;
    }
  }
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithBinary(cl_context context, cl_uint count, const cl_device_id *devices,
                                                              const size_t *lengths, const unsigned char **binaries, cl_int *binary_status, cl_int *error) {
  auto *context_value = checked(reinterpret_cast<_cl_context *>(context), Kind::Context);
  if (context_value == nullptr) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  if (count != 1 || devices == nullptr || !valid_device(devices[0]) || lengths == nullptr || binaries == nullptr ||
      binaries[0] == nullptr || lengths[0] == 0) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  if (binary_status) binary_status[0] = CL_INVALID_BINARY;
  cl_program_binary_type binary_type = CL_PROGRAM_BINARY_TYPE_NONE;
  std::string source;
  std::vector<uint8_t> il;
  if (!decode_program_binary(binaries[0], lengths[0], &binary_type, &source, &il)) {
    auto result = clCreateProgramWithIL(context, binaries[0], lengths[0], error);
    if (result != nullptr) {
      reinterpret_cast<_cl_program *>(result)->binary_type = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
      if (binary_status) binary_status[0] = CL_SUCCESS;
    }
    return result;
  }

  auto *program = new (std::nothrow) _cl_program;
  if (program == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(program);
  program->dispatch = &dispatch;
  program->context = context_value;
  program->source = std::move(source);
  program->build_source = program->source;
  program->il = std::move(il);
  program->kernel_names = extract_kernel_names(program->il);
  program->binary_type = binary_type;
  program->build_status = CL_BUILD_SUCCESS;
  retain(context_value);
  auto &state = remote_state();
  if (!state.ready) {
    unregister_live_object(program);
    delete program;
    clReleaseContext(context);
    if (error) *error = CL_INVALID_BINARY;
    return nullptr;
  }
  {
    std::lock_guard lock(remote_mutex);
    if (gemstone_execution_load_program(state.client, GEMSTONE_PROGRAM_SPIRV, program->il.data(),
                                         static_cast<uint32_t>(program->il.size()), &program->remote_id) != 0) {
      unregister_live_object(program);
      delete program;
      clReleaseContext(context);
      if (error) *error = CL_INVALID_BINARY;
      return nullptr;
    }
  }
  if (binary_status) binary_status[0] = CL_SUCCESS;
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}

CL_API_ENTRY cl_program CL_API_CALL clCreateProgramWithSource(cl_context value, cl_uint count, const char **strings,
                                                              const size_t *lengths, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (context == nullptr) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  if (count == 0 || strings == nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  std::string source;
  for (cl_uint index = 0; index < count; ++index) {
    if (strings[index] == nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
    const size_t length = lengths == nullptr || lengths[index] == 0 ? std::strlen(strings[index]) : lengths[index];
    if (length > std::numeric_limits<size_t>::max() - source.size()) {
      if (error) *error = CL_OUT_OF_HOST_MEMORY;
      return nullptr;
    }
    source.append(strings[index], length);
  }
  auto *program = new (std::nothrow) _cl_program;
  if (program == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(program);
  program->dispatch = &dispatch;
  program->context = context;
  program->source = std::move(source);
  retain(context);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_program>(program);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainProgram(cl_program value) { auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program); if (!program) return CL_INVALID_PROGRAM; retain(program); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseProgram(cl_program value) { auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program); if (!program) return CL_INVALID_PROGRAM; if (release(program)) { auto &state = remote_state(); if (state.ready && program->remote_id != 0) { std::lock_guard lock(remote_mutex); gemstone_execution_unload_program(state.client, program->remote_id); } auto *context = program->context; unregister_live_object(program); delete program; clReleaseContext(reinterpret_cast<cl_context>(context)); } return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clBuildProgram(cl_program value, cl_uint count, const cl_device_id *devices, const char *options,
                                              void (CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program); if (!program) return CL_INVALID_PROGRAM;
  if (count != 0 && devices == nullptr) return CL_INVALID_VALUE;
  if (count > 1 || (count == 1 && !valid_device(devices[0]))) return CL_INVALID_DEVICE;
  program->build_options = options == nullptr ? "" : options;
  const std::string source = program->build_source.empty() ? program->source : program->build_source;
  if (!source.empty() && program->remote_id != 0) {
    auto &state = remote_state();
    if (state.ready) {
      std::lock_guard lock(remote_mutex);
      gemstone_execution_unload_program(state.client, program->remote_id);
    }
    program->remote_id = 0;
    program->il.clear();
    program->bitcode.clear();
  }
  if (!source.empty() && program->remote_id == 0) {
    std::vector<uint8_t> compiled;
    std::vector<uint8_t> compiled_bitcode;
    if (!compile_source(source, program->build_options, &compiled, &program->build_log, &compiled_bitcode)) {
      program->build_status = CL_BUILD_ERROR;
      if (notify) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
    program->kernel_names = extract_kernel_names(compiled);
    program->il = std::move(compiled);
    program->bitcode = std::move(compiled_bitcode);
    program->build_source = source;
    if (!program->kernel_names.empty()) {
      auto &state = remote_state();
      if (!state.ready) {
        program->build_status = CL_BUILD_ERROR;
        program->build_log = remote_error();
        if (notify) notify(value, user);
        return CL_BUILD_PROGRAM_FAILURE;
      }
      std::lock_guard lock(remote_mutex);
      if (gemstone_execution_load_program(state.client, GEMSTONE_PROGRAM_SPIRV, program->il.data(),
                                           static_cast<uint32_t>(program->il.size()), &program->remote_id) != 0) {
      program->build_status = CL_BUILD_ERROR;
        program->build_log = remote_error();
        if (notify) notify(value, user);
        return CL_BUILD_PROGRAM_FAILURE;
      }
    }
  }
  program->build_status = program->remote_id == 0 && program->kernel_names.empty() ? CL_BUILD_SUCCESS
                                                                                    : (program->remote_id == 0 ? CL_BUILD_ERROR : CL_BUILD_SUCCESS);
  if (program->build_status != CL_BUILD_SUCCESS) {
    if (program->build_log.empty()) program->build_log = remote_error();
    if (notify) notify(value, user);
    return CL_BUILD_PROGRAM_FAILURE;
  }
  if (program->kernel_names.empty()) program->kernel_names = extract_kernel_names(program->il);
  program->binary_type = CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  if (notify) notify(value, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clUnloadCompiler(void) { return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clUnloadPlatformCompiler(cl_platform_id value) {
  return valid_platform(value) ? CL_SUCCESS : CL_INVALID_PLATFORM;
}
CL_API_ENTRY cl_int CL_API_CALL clCompileProgram(cl_program value, cl_uint count, const cl_device_id *devices,
                                                 const char *options, cl_uint input_header_count,
                                                 const cl_program *input_headers, const char **header_names,
                                                 void (CL_CALLBACK *notify)(cl_program, void *), void *user) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (program == nullptr) return CL_INVALID_PROGRAM;
  if (input_header_count != 0 && (input_headers == nullptr || header_names == nullptr)) return CL_INVALID_VALUE;
  if (input_header_count != 0) {
    std::string expanded;
    if (!expand_headers(program->source, input_header_count, input_headers, header_names, &expanded,
                        &program->build_log)) {
      program->build_status = CL_BUILD_ERROR;
      if (notify != nullptr) notify(value, user);
      return CL_INVALID_PROGRAM;
    }
    program->build_source = std::move(expanded);
  }
  {
    const std::string source = program->build_source.empty() ? program->source : program->build_source;
    std::string expanded;
    if (!expand_filesystem_includes(source, options == nullptr ? "" : options, &expanded, &program->build_log)) {
      program->build_status = CL_BUILD_ERROR;
      if (notify != nullptr) notify(value, user);
      return CL_BUILD_PROGRAM_FAILURE;
    }
    program->build_source = std::move(expanded);
  }
  const cl_int result = clBuildProgram(value, count, devices, options, nullptr, nullptr);
  if (result == CL_SUCCESS && program != nullptr) program->binary_type = CL_PROGRAM_BINARY_TYPE_COMPILED_OBJECT;
  if (notify != nullptr && result != CL_INVALID_PROGRAM) notify(value, user);
  return result;
}
CL_API_ENTRY cl_program CL_API_CALL clLinkProgram(cl_context context_value, cl_uint count, const cl_device_id *devices,
                                                 const char *options, cl_uint input_count, const cl_program *inputs,
                                                 void (CL_CALLBACK *notify)(cl_program, void *), void *user, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(context_value), Kind::Context);
  if (context == nullptr) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  if (count != 0 && devices == nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  if (count > 1 || (count == 1 && !valid_device(devices[0]))) { if (error) *error = CL_INVALID_DEVICE; return nullptr; }
  if (input_count == 0 || inputs == nullptr) { if (error) *error = CL_INVALID_VALUE; return nullptr; }
  std::string source;
  std::string build_options;
  std::vector<std::vector<uint8_t>> bitcode_modules;
  bool all_source_bitcode = true;
  _cl_program *binary_input = nullptr;
  std::vector<_cl_program *> binary_inputs;
  for (cl_uint index = 0; index < input_count; ++index) {
    auto *program = checked(reinterpret_cast<_cl_program *>(inputs[index]), Kind::Program);
    if (program == nullptr) {
      if (error) *error = CL_INVALID_PROGRAM;
      return nullptr;
    }
    if (program->context != context || program->build_status != CL_BUILD_SUCCESS) {
      if (error) *error = CL_INVALID_PROGRAM;
      return nullptr;
    }
    if (program->source.empty()) {
      if (program->il.empty() || !source.empty() || !bitcode_modules.empty()) {
        if (error) *error = CL_INVALID_PROGRAM;
        return nullptr;
      }
      binary_inputs.push_back(program);
      continue;
    }
    if (!source.empty()) source.push_back('\n');
    source += program->build_source.empty() ? program->source : program->build_source;
    if (program->bitcode.empty()) all_source_bitcode = false;
    else bitcode_modules.push_back(program->bitcode);
    if (!program->build_options.empty()) {
      if (!build_options.empty()) build_options.push_back(' ');
      build_options += program->build_options;
    }
  }
  if (binary_inputs.size() == 1) binary_input = binary_inputs.front();
  if (binary_inputs.size() > 1) {
    std::vector<std::vector<uint8_t>> binary_bitcode;
    binary_bitcode.reserve(binary_inputs.size());
    std::string link_log;
    for (const auto *program : binary_inputs) {
      std::vector<uint8_t> bitcode;
      if (!reverse_spirv(program->il, &bitcode, &link_log)) {
        if (error) *error = CL_BUILD_PROGRAM_FAILURE;
        return nullptr;
      }
      binary_bitcode.push_back(std::move(bitcode));
    }
    std::vector<uint8_t> linked_il;
    std::vector<uint8_t> linked_bitcode;
    if (!link_bitcode(binary_bitcode, &linked_il, &linked_bitcode, &link_log)) {
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    cl_int link_error = CL_SUCCESS;
    auto *linked = reinterpret_cast<_cl_program *>(clCreateProgramWithIL(
      context_value, linked_il.data(), linked_il.size(), &link_error));
    if (linked == nullptr) {
      if (error) *error = link_error == CL_SUCCESS ? CL_BUILD_PROGRAM_FAILURE : link_error;
      return nullptr;
    }
    linked->bitcode = std::move(linked_bitcode);
    linked->build_status = CL_BUILD_SUCCESS;
    linked->binary_type = options != nullptr && std::strstr(options, "-create-library") != nullptr
      ? CL_PROGRAM_BINARY_TYPE_LIBRARY
      : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    linked->build_options = std::move(build_options);
    linked->build_log = std::move(link_log);
    if (notify != nullptr) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (binary_input == nullptr && all_source_bitcode) {
    std::vector<uint8_t> linked_il;
    std::vector<uint8_t> linked_bitcode;
    std::string link_log;
    if (!link_bitcode(bitcode_modules, &linked_il, &linked_bitcode, &link_log)) {
      if (error) *error = CL_BUILD_PROGRAM_FAILURE;
      return nullptr;
    }
    cl_int link_error = CL_SUCCESS;
    auto *linked = reinterpret_cast<_cl_program *>(clCreateProgramWithIL(
      context_value, linked_il.data(), linked_il.size(), &link_error));
    if (linked == nullptr) {
      if (error) *error = link_error == CL_SUCCESS ? CL_BUILD_PROGRAM_FAILURE : link_error;
      return nullptr;
    }
    linked->source = std::move(source);
    linked->build_source = linked->source;
    linked->bitcode = std::move(linked_bitcode);
    linked->kernel_names = extract_kernel_names(linked->il);
    linked->build_status = CL_BUILD_SUCCESS;
    linked->binary_type = options != nullptr && std::strstr(options, "-create-library") != nullptr
      ? CL_PROGRAM_BINARY_TYPE_LIBRARY
      : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    linked->build_options = std::move(build_options);
    linked->build_log = std::move(link_log);
    if (notify != nullptr) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (binary_input != nullptr) {
    auto *linked = new (std::nothrow) _cl_program;
    if (linked == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
    register_live_object(linked);
    linked->dispatch = &dispatch;
    linked->context = context;
    linked->source = binary_input->source;
    linked->build_source = binary_input->build_source;
    linked->il = binary_input->il;
    linked->kernel_names = extract_kernel_names(linked->il);
    linked->build_status = CL_BUILD_SUCCESS;
    linked->binary_type = options != nullptr && std::strstr(options, "-create-library") != nullptr
      ? CL_PROGRAM_BINARY_TYPE_LIBRARY
      : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
    retain(context);
    auto &state = remote_state();
    if (!state.ready) {
      unregister_live_object(linked);
      delete linked;
      clReleaseContext(context);
      if (error) *error = CL_INVALID_BINARY;
      return nullptr;
    }
    {
      std::lock_guard lock(remote_mutex);
      if (gemstone_execution_load_program(state.client, GEMSTONE_PROGRAM_SPIRV, linked->il.data(),
                                           static_cast<uint32_t>(linked->il.size()), &linked->remote_id) != 0) {
        unregister_live_object(linked);
        delete linked;
        clReleaseContext(context);
        if (error) *error = CL_BUILD_PROGRAM_FAILURE;
        return nullptr;
      }
    }
    if (notify != nullptr) notify(reinterpret_cast<cl_program>(linked), user);
    if (error) *error = CL_SUCCESS;
    return reinterpret_cast<cl_program>(linked);
  }
  if (options != nullptr && options[0] != '\0') {
    if (!build_options.empty()) build_options.push_back(' ');
    build_options += without_library_option(options);
  }
  const char *source_pointer = source.c_str();
  cl_program result = clCreateProgramWithSource(context_value, 1, &source_pointer, nullptr, error);
  if (result == nullptr) return nullptr;
  const cl_int build = clBuildProgram(result, count, devices, build_options.c_str(), nullptr, nullptr);
  if (build != CL_SUCCESS) {
    clReleaseProgram(result);
    if (error) *error = build;
    return nullptr;
  }
  auto *linked = reinterpret_cast<_cl_program *>(result);
  linked->binary_type = (options != nullptr && std::strstr(options, "-create-library") != nullptr)
    ? CL_PROGRAM_BINARY_TYPE_LIBRARY
    : CL_PROGRAM_BINARY_TYPE_EXECUTABLE;
  if (notify != nullptr) notify(result, user);
  if (error) *error = CL_SUCCESS;
  return result;
}

CL_API_ENTRY cl_int CL_API_CALL clGetSupportedImageFormats(cl_context value, cl_mem_flags, cl_mem_object_type,
                                                           cl_uint num_entries, cl_image_format *image_formats,
                                                           cl_uint *num_image_formats) {
  if (!checked(reinterpret_cast<_cl_context *>(value), Kind::Context)) return CL_INVALID_CONTEXT;
  if (image_formats != nullptr && num_entries == 0) return CL_INVALID_VALUE;
  if (num_image_formats != nullptr) *num_image_formats = 0;
  return CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clGetProgramInfo(cl_program value, cl_program_info name, size_t size, void *output, size_t *written) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program); if (!program) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_REFERENCE_COUNT: { cl_uint v = program->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_PROGRAM_CONTEXT: { cl_context v = reinterpret_cast<cl_context>(program->context); return output_scalar(size, output, written, v); }
    case CL_PROGRAM_NUM_DEVICES: { cl_uint v = 1; return output_scalar(size, output, written, v); }
    case CL_PROGRAM_DEVICES: { cl_device_id v = reinterpret_cast<cl_device_id>(&device); return output_scalar(size, output, written, v); }
    case CL_PROGRAM_SOURCE: return output_bytes(size, output, written, program->source.c_str(), program->source.size() + 1);
    case CL_PROGRAM_NUM_KERNELS: {
      if (program->build_status != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      const size_t v = kernel_count(program->kernel_names);
      return output_scalar(size, output, written, v);
    }
    case CL_PROGRAM_KERNEL_NAMES:
      if (program->build_status != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
      return output_string(size, output, written, program->kernel_names);
    case CL_PROGRAM_IL: return output_bytes(size, output, written, program->il.data(), program->il.size());
    case CL_PROGRAM_BINARY_SIZES: {
      const auto binary = program_binary(program);
      return output_scalar(size, output, written, binary.size());
    }
    case CL_PROGRAM_BINARIES:
      if (written != nullptr) *written = sizeof(unsigned char *);
      if (output == nullptr) return CL_SUCCESS;
      if (size < sizeof(unsigned char *)) return CL_INVALID_VALUE;
      {
        const auto binary = program_binary(program);
        auto *destination = *static_cast<unsigned char **>(output);
        if (destination == nullptr && !binary.empty()) return CL_INVALID_VALUE;
        if (!binary.empty()) std::memcpy(destination, binary.data(), binary.size());
      }
      return CL_SUCCESS;
    case CL_PROGRAM_SCOPE_GLOBAL_CTORS_PRESENT: case CL_PROGRAM_SCOPE_GLOBAL_DTORS_PRESENT: {
      cl_bool v = CL_FALSE; return output_scalar(size, output, written, v);
    }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetProgramBuildInfo(cl_program value, cl_device_id selected, cl_program_build_info name, size_t size, void *output, size_t *written) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program); if (!program || !valid_device(selected)) return CL_INVALID_PROGRAM;
  switch (name) {
    case CL_PROGRAM_BUILD_STATUS: return output_scalar(size, output, written, program->build_status);
    case CL_PROGRAM_BUILD_OPTIONS: return output_string(size, output, written, program->build_options);
    case CL_PROGRAM_BUILD_LOG: return output_string(size, output, written, program->build_log);
    case CL_PROGRAM_BINARY_TYPE: return output_scalar(size, output, written, program->binary_type);
    case CL_PROGRAM_BUILD_GLOBAL_VARIABLE_TOTAL_SIZE: { size_t v = 0; return output_scalar(size, output, written, v); }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetProgramReleaseCallback(cl_program value,
                                                            void (CL_CALLBACK *)(cl_program, void *), void *) {
  return checked(reinterpret_cast<_cl_program *>(value), Kind::Program) == nullptr
    ? CL_INVALID_PROGRAM : CL_INVALID_OPERATION;
}

CL_API_ENTRY cl_kernel CL_API_CALL clCreateKernel(cl_program value, const char *name, cl_int *error) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (!program || name == nullptr || name[0] == '\0') { if (error) *error = program ? CL_INVALID_KERNEL_NAME : CL_INVALID_PROGRAM; return nullptr; }
  if (program->build_status != CL_BUILD_SUCCESS) { if (error) *error = CL_INVALID_PROGRAM_EXECUTABLE; return nullptr; }
  if (!contains_kernel_name(program->kernel_names, name)) { if (error) *error = CL_INVALID_KERNEL_NAME; return nullptr; }
  auto *kernel = new (std::nothrow) _cl_kernel; if (!kernel) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(kernel);
  kernel->dispatch = &dispatch;
  kernel->program = program;
  kernel->name = name;
  const auto kernel_source = source_kernel_text(program->source, kernel->name);
  kernel->expected_arguments = kernel_parameter_count(program->il, kernel->name);
  kernel->required_work_group_size = source_required_work_group_size(kernel_source);
  kernel->static_local_memory = source_static_local_memory(kernel_source);
  kernel->attributes = source_kernel_attributes(kernel_source);
  kernel->expected_argument_sizes = kernel_parameter_sizes(program->il, kernel->name);
  kernel->parameter_metadata = kernel_parameter_metadata(program->il, kernel->name);
  kernel->argument_info = source_kernel_argument_info(kernel_source, kernel->name);
  kernel->arguments.resize(kernel->expected_arguments);
  kernel->arguments_set.resize(kernel->expected_arguments, false);
  retain(program);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_kernel>(kernel);
}
CL_API_ENTRY cl_int CL_API_CALL clCreateKernelsInProgram(cl_program value, cl_uint num_kernels,
                                                         cl_kernel *kernels, cl_uint *num_kernels_ret) {
  auto *program = checked(reinterpret_cast<_cl_program *>(value), Kind::Program);
  if (program == nullptr) return CL_INVALID_PROGRAM;
  if (program->build_status != CL_BUILD_SUCCESS) return CL_INVALID_PROGRAM_EXECUTABLE;
  const size_t total = kernel_count(program->kernel_names);
  if (num_kernels_ret != nullptr) *num_kernels_ret = static_cast<cl_uint>(total);
  if (num_kernels == 0 && kernels == nullptr) return CL_SUCCESS;
  if (total != 0 && (kernels == nullptr || num_kernels < total)) return CL_INVALID_VALUE;
  if (kernels == nullptr && num_kernels != 0) return CL_INVALID_VALUE;
  size_t start = 0;
  for (size_t index = 0; index < total; ++index) {
    const size_t end = program->kernel_names.find(';', start);
    const std::string name = program->kernel_names.substr(start, end == std::string::npos ? end : end - start);
    cl_int error = CL_SUCCESS;
    kernels[index] = clCreateKernel(value, name.c_str(), &error);
    if (kernels[index] == nullptr || error != CL_SUCCESS) {
      for (size_t created = 0; created < index; ++created) clReleaseKernel(kernels[created]);
      return error == CL_SUCCESS ? CL_OUT_OF_HOST_MEMORY : error;
    }
    if (end == std::string::npos) break;
    start = end + 1;
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_kernel CL_API_CALL clCloneKernel(cl_kernel value, cl_int *error) {
  auto *source = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (source == nullptr) {
    if (error) *error = CL_INVALID_KERNEL;
    return nullptr;
  }
  auto *clone = new (std::nothrow) _cl_kernel;
  if (clone == nullptr) {
    if (error) *error = CL_OUT_OF_HOST_MEMORY;
    return nullptr;
  }
  register_live_object(clone);
  clone->dispatch = &dispatch;
  clone->program = source->program;
  clone->name = source->name;
  clone->expected_arguments = source->expected_arguments;
  clone->required_work_group_size = source->required_work_group_size;
  clone->static_local_memory = source->static_local_memory;
  clone->attributes = source->attributes;
  clone->expected_argument_sizes = source->expected_argument_sizes;
  clone->parameter_metadata = source->parameter_metadata;
  clone->argument_info = source->argument_info;
  clone->arguments = source->arguments;
  clone->arguments_set = source->arguments_set;
  retain(clone->program);
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_kernel>(clone);
}
CL_API_ENTRY cl_int CL_API_CALL clRetainKernel(cl_kernel value) { auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel); if (!kernel) return CL_INVALID_KERNEL; retain(kernel); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseKernel(cl_kernel value) { auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel); if (!kernel) return CL_INVALID_KERNEL; if (release(kernel)) { auto *program = kernel->program; unregister_live_object(kernel); delete kernel; clReleaseProgram(reinterpret_cast<cl_program>(program)); } return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArg(cl_kernel value, cl_uint index, size_t size, const void *argument) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel); if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->expected_arguments) return CL_INVALID_ARG_INDEX;
  if (argument == nullptr && size == 0) return CL_INVALID_ARG_SIZE;
  if (argument != nullptr && size == 0) return CL_INVALID_ARG_SIZE;
  auto &output = kernel->arguments[index];
  const bool pointer_aggregate = index < kernel->parameter_metadata.size() &&
                                 kernel->parameter_metadata[index].pointer_aggregate;
  const bool local_argument = index < kernel->parameter_metadata.size() && kernel->parameter_metadata[index].local;
  if (argument == nullptr && size == sizeof(cl_mem) && !local_argument) {
      output.kind = KernelArgument::Kind::Scalar;
      output.allocation.reset();
      output.offset = 0;
      output.bytes.assign(sizeof(cl_mem), 0);
      kernel->arguments_set[index] = true;
    return CL_SUCCESS;
  }
  if (argument == nullptr) {
    if (!local_argument) return CL_INVALID_ARG_VALUE;
    output.kind = KernelArgument::Kind::Local;
    output.bytes.clear();
    output.allocation.reset();
    output.offset = size;
    kernel->arguments_set[index] = true;
    return CL_SUCCESS;
  }
  if (size >= sizeof(cl_mem)) {
    cl_mem handle = nullptr; std::memcpy(&handle, argument, sizeof(handle));
    auto *mem = checked(reinterpret_cast<_cl_mem *>(handle), Kind::Memory);
    if (mem != nullptr) {
      if (size != sizeof(cl_mem)) return CL_INVALID_ARG_SIZE;
      output.kind = KernelArgument::Kind::Allocation; output.allocation = mem->allocation; output.offset = mem->origin; output.bytes.clear();
      kernel->arguments_set[index] = true;
      return CL_SUCCESS;
    }
  }
  if (size == sizeof(uintptr_t) && !pointer_aggregate) {
    uintptr_t pointer = 0;
    std::memcpy(&pointer, argument, sizeof(pointer));
    size_t pointer_offset = 0;
    auto allocation = find_pointer(reinterpret_cast<const void *>(pointer), &pointer_offset);
    if (allocation != nullptr) {
      output.kind = KernelArgument::Kind::Allocation;
      output.allocation = std::move(allocation);
      output.offset = pointer_offset;
      output.bytes.clear();
      kernel->arguments_set[index] = true;
      return CL_SUCCESS;
    }
  }
  if (index < kernel->expected_argument_sizes.size() && kernel->expected_argument_sizes[index] != 0 &&
      size != kernel->expected_argument_sizes[index] && !pointer_aggregate) {
    // DPC++ passes the host ABI size for pointer-bearing aggregates.  The
    // physical SPIR-V type may include storage-class padding that is absent
    // from that ABI blob; aggregate arguments are materialized by the
    // execution service with the missing trailing bytes.
    return CL_INVALID_ARG_SIZE;
  }
  output.kind = KernelArgument::Kind::Scalar; output.allocation.reset(); output.offset = 0;
  output.bytes.assign(static_cast<const uint8_t *>(argument), static_cast<const uint8_t *>(argument) + size);
  kernel->arguments_set[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgSVMPointer(cl_kernel value, cl_uint index, const void *argument) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel); if (!kernel) return CL_INVALID_KERNEL;
  size_t offset = 0; auto allocation = find_pointer(argument, &offset); if (!allocation) return CL_INVALID_ARG_VALUE;
  if (index >= kernel->expected_arguments) return CL_INVALID_ARG_INDEX;
  auto &output = kernel->arguments[index]; output.kind = KernelArgument::Kind::SVM; output.allocation = std::move(allocation); output.offset = offset;
  output.bytes.resize(sizeof(uint64_t)); const uint64_t pointer = reinterpret_cast<uintptr_t>(argument); std::memcpy(output.bytes.data(), &pointer, sizeof(pointer));
  kernel->arguments_set[index] = true;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelExecInfo(cl_kernel value, cl_kernel_exec_info, size_t size, const void *value_data) {
  if (!checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel)) return CL_INVALID_KERNEL;
  if (size != 0 && value_data == nullptr) return CL_INVALID_VALUE;
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelInfo(cl_kernel value, cl_kernel_info name, size_t size, void *output, size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  switch (name) {
    case CL_KERNEL_FUNCTION_NAME: return output_string(size, output, written, kernel->name);
    case CL_KERNEL_NUM_ARGS: { cl_uint v = static_cast<cl_uint>(kernel->arguments.size()); return output_scalar(size, output, written, v); }
    case CL_KERNEL_REFERENCE_COUNT: { cl_uint v = kernel->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_KERNEL_CONTEXT: { cl_context v = reinterpret_cast<cl_context>(kernel->program->context); return output_scalar(size, output, written, v); }
    case CL_KERNEL_PROGRAM: { cl_program v = reinterpret_cast<cl_program>(kernel->program); return output_scalar(size, output, written, v); }
    case CL_KERNEL_ATTRIBUTES: return output_string(size, output, written, kernel->attributes);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelArgInfo(cl_kernel value, cl_uint index, cl_kernel_arg_info name,
                                                   size_t size, void *output, size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (index >= kernel->arguments.size()) return CL_INVALID_ARG_INDEX;
  if (index >= kernel->argument_info.size()) return CL_KERNEL_ARG_INFO_NOT_AVAILABLE;
  const auto &info = kernel->argument_info[index];
  switch (name) {
    case CL_KERNEL_ARG_ADDRESS_QUALIFIER: return output_scalar(size, output, written, info.address);
    case CL_KERNEL_ARG_ACCESS_QUALIFIER: return output_scalar(size, output, written, info.access);
    case CL_KERNEL_ARG_TYPE_QUALIFIER: return output_scalar(size, output, written, info.type);
    case CL_KERNEL_ARG_TYPE_NAME: return output_string(size, output, written, info.type_name);
    case CL_KERNEL_ARG_NAME: return output_string(size, output, written, info.name);
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelWorkGroupInfo(cl_kernel value, cl_device_id selected, cl_kernel_work_group_info name,
                                                          size_t size, void *output, size_t *written) {
  auto *kernel = checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel);
  if (!kernel) return CL_INVALID_KERNEL;
  if (!valid_device(selected)) return CL_INVALID_DEVICE;
  switch (name) {
    case CL_KERNEL_WORK_GROUP_SIZE: { size_t v = 1024; return output_scalar(size, output, written, v); }
    case CL_KERNEL_COMPILE_WORK_GROUP_SIZE: return output_bytes(size, output, written,
                                                                kernel->required_work_group_size.data(),
                                                                sizeof(kernel->required_work_group_size));
    case CL_KERNEL_LOCAL_MEM_SIZE: {
      cl_ulong v = static_cast<cl_ulong>(kernel->static_local_memory);
      for (const auto &argument : kernel->arguments)
        if (argument.kind == KernelArgument::Kind::Local) v += static_cast<cl_ulong>(argument.offset);
      return output_scalar(size, output, written, v);
    }
    case CL_KERNEL_PREFERRED_WORK_GROUP_SIZE_MULTIPLE: { size_t v = 32; return output_scalar(size, output, written, v); }
    case CL_KERNEL_PRIVATE_MEM_SIZE: { cl_ulong v = 0; return output_scalar(size, output, written, v); }
    case CL_KERNEL_GLOBAL_WORK_SIZE: { size_t v[3] = {0, 0, 0}; return output_bytes(size, output, written, v, sizeof(v)); }
    default: return CL_INVALID_VALUE;
  }
}

CL_API_ENTRY cl_int CL_API_CALL clWaitForEvents(cl_uint count, const cl_event *events) {
  return wait_list(nullptr, count, events);
}
CL_API_ENTRY cl_event CL_API_CALL clCreateUserEvent(cl_context value, cl_int *error) {
  auto *context = checked(reinterpret_cast<_cl_context *>(value), Kind::Context);
  if (context == nullptr) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  auto *event = new (std::nothrow) _cl_event;
  if (event == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  register_live_object(event);
  event->dispatch = &dispatch;
  event->context = context;
  event->command = CL_COMMAND_USER;
  event->status = CL_SUBMITTED;
  event->started = now_nanos();
  retain(context);
  {
    std::lock_guard lock(events_mutex);
    live_events.push_back(event);
  }
  if (error) *error = CL_SUCCESS;
  return reinterpret_cast<cl_event>(event);
}
CL_API_ENTRY cl_int CL_API_CALL clSetUserEventStatus(cl_event value, cl_int status) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (event == nullptr) return CL_INVALID_EVENT;
  if (event->command != CL_COMMAND_USER) return CL_INVALID_OPERATION;
  if (status != CL_COMPLETE && status >= 0) return CL_INVALID_VALUE;
  {
    std::lock_guard lock(events_mutex);
    if (event->status != CL_SUBMITTED) return CL_INVALID_OPERATION;
    event->status = status;
    event->ended = now_nanos();
  }
  events_condition.notify_all();
  notify_event_callbacks(event);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clSetEventCallback(
    cl_event value, cl_int trigger, void (CL_CALLBACK *callback)(cl_event, cl_int, void *), void *user) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (event == nullptr) return CL_INVALID_EVENT;
  if (callback == nullptr || (trigger != CL_SUBMITTED && trigger != CL_RUNNING && trigger != CL_COMPLETE))
    return CL_INVALID_VALUE;
  bool ready = false;
  cl_int status = CL_COMPLETE;
  {
    std::lock_guard lock(events_mutex);
    status = event->status;
    ready = status < 0 || status <= trigger;
    if (!ready) event->callbacks.push_back({trigger, callback, user});
  }
  if (ready) callback(value, status, user);
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventInfo(cl_event value, cl_event_info name, size_t size, void *output, size_t *written) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event); if (!event) return CL_INVALID_EVENT;
  switch (name) {
    case CL_EVENT_COMMAND_QUEUE: { cl_command_queue v = reinterpret_cast<cl_command_queue>(event->queue); return output_scalar(size, output, written, v); }
    case CL_EVENT_COMMAND_TYPE: return output_scalar(size, output, written, event->command);
    case CL_EVENT_REFERENCE_COUNT: { cl_uint v = event->header.references.load(); return output_scalar(size, output, written, v); }
    case CL_EVENT_COMMAND_EXECUTION_STATUS: { std::lock_guard lock(events_mutex); return output_scalar(size, output, written, event->status); }
    case CL_EVENT_CONTEXT: { cl_context v = reinterpret_cast<cl_context>(event->context); return output_scalar(size, output, written, v); }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clRetainEvent(cl_event value) { auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event); if (!event) return CL_INVALID_EVENT; retain(event); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clReleaseEvent(cl_event value) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event);
  if (!event) return CL_INVALID_EVENT;
  if (release(event)) {
    auto *queue = event->queue;
    auto *context = event->context;
    {
      std::lock_guard lock(events_mutex);
      live_events.erase(std::remove(live_events.begin(), live_events.end(), event), live_events.end());
    }
    unregister_live_object(event);
    delete event;
    if (queue != nullptr) clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
    if (context != nullptr) clReleaseContext(reinterpret_cast<cl_context>(context));
  }
  return CL_SUCCESS;
}
CL_API_ENTRY cl_int CL_API_CALL clGetEventProfilingInfo(cl_event value, cl_profiling_info name, size_t size, void *output, size_t *written) {
  auto *event = checked(reinterpret_cast<_cl_event *>(value), Kind::Event); if (!event) return CL_INVALID_EVENT;
  if (event->queue == nullptr || (event->queue->properties & CL_QUEUE_PROFILING_ENABLE) == 0)
    return CL_PROFILING_INFO_NOT_AVAILABLE;
  if (name == CL_PROFILING_COMMAND_QUEUED) return output_scalar(size, output, written, event->queued);
  if (name == CL_PROFILING_COMMAND_SUBMIT) return output_scalar(size, output, written, event->submitted);
  if (name == CL_PROFILING_COMMAND_START) return output_scalar(size, output, written, event->started);
  if (name == CL_PROFILING_COMMAND_END) return output_scalar(size, output, written, event->ended);
  return CL_INVALID_VALUE;
}
CL_API_ENTRY cl_int CL_API_CALL clFlush(cl_command_queue value) { return checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue) ? CL_SUCCESS : CL_INVALID_COMMAND_QUEUE; }
CL_API_ENTRY cl_int CL_API_CALL clFinish(cl_command_queue value) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(value), Kind::Queue);
  if (queue == nullptr) return CL_INVALID_COMMAND_QUEUE;
  const auto pending = wait_queue(queue);
  if (pending != CL_SUCCESS) return pending;
  std::unique_lock lock(events_mutex);
  events_condition.wait(lock, [queue] {
    return std::none_of(live_events.begin(), live_events.end(), [queue](const _cl_event *event) {
      return event->queue == queue && event->status > CL_COMPLETE;
    });
  });
  const bool failed = std::any_of(live_events.begin(), live_events.end(), [queue](const _cl_event *event) {
    return event->queue == queue && event->status < CL_COMPLETE;
  });
  return failed ? CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST : CL_SUCCESS;
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBuffer(cl_command_queue queue_value, cl_mem mem_value, cl_bool, size_t offset, size_t size,
                                                    void *output, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); auto *mem = checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory);
  auto result = validate_wait_list(queue, count, waits); if (result != CL_SUCCESS) return result;
  if (mem == nullptr || mem->context != queue->context || !valid_mem_range(mem, offset, size)) return CL_INVALID_VALUE;
  if (!host_read_allowed(mem)) return CL_INVALID_OPERATION;
  result = read_allocation(mem->allocation, allocation_offset(mem, offset), output, size); if (result != CL_SUCCESS) return result;
  return complete_event(queue, CL_COMMAND_READ_BUFFER, 0, nullptr, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBuffer(cl_command_queue queue_value, cl_mem mem_value, cl_bool, size_t offset, size_t size,
                                                     const void *input, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); auto *mem = checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory);
  auto result = validate_wait_list(queue, count, waits); if (result != CL_SUCCESS) return result;
  if (mem == nullptr || mem->context != queue->context || !valid_mem_range(mem, offset, size)) return CL_INVALID_VALUE;
  if (!host_write_allowed(mem)) return CL_INVALID_OPERATION;
  result = write_allocation(mem->allocation, allocation_offset(mem, offset), input, size); if (result != CL_SUCCESS) return result;
  return complete_event(queue, CL_COMMAND_WRITE_BUFFER, 0, nullptr, event);
}

bool rect_pitch(size_t requested_row, size_t requested_slice, const size_t region[3], size_t *row, size_t *slice) {
  if (region == nullptr || row == nullptr || slice == nullptr || region[0] == 0 || region[1] == 0 || region[2] == 0)
    return false;
  *row = requested_row == 0 ? region[0] : requested_row;
  if (*row < region[0] || region[1] > std::numeric_limits<size_t>::max() / *row) return false;
  const size_t minimum_slice = *row * region[1];
  *slice = requested_slice == 0 ? minimum_slice : requested_slice;
  return *slice >= minimum_slice;
}

bool rect_end(const size_t origin[3], const size_t region[3], size_t row, size_t slice, size_t *end) {
  if (origin == nullptr || end == nullptr || region == nullptr) return false;
  size_t value = origin[0];
  if (region[1] - 1 > std::numeric_limits<size_t>::max() / row) return false;
  if (value > std::numeric_limits<size_t>::max() - (region[1] - 1) * row) return false;
  value += (region[1] - 1) * row;
  if (region[2] - 1 > std::numeric_limits<size_t>::max() / slice) return false;
  if (value > std::numeric_limits<size_t>::max() - (region[2] - 1) * slice) return false;
  value += (region[2] - 1) * slice;
  if (value > std::numeric_limits<size_t>::max() - region[0]) return false;
  *end = value + region[0];
  return true;
}

cl_int enqueue_read_buffer_rect(_cl_command_queue *queue, _cl_mem *mem, const size_t buffer_origin[3],
                                const size_t host_origin[3], const size_t region[3], size_t buffer_row_pitch,
                                size_t buffer_slice_pitch, size_t host_row_pitch, size_t host_slice_pitch,
                                void *output, cl_uint count, const cl_event *waits, cl_event *event) {
  auto result = complete_event(queue, CL_COMMAND_READ_BUFFER, count, waits, nullptr);
  if (result != CL_SUCCESS) return result;
  if (mem == nullptr || mem->context != queue->context || output == nullptr || buffer_origin == nullptr ||
      host_origin == nullptr || region == nullptr)
    return CL_INVALID_VALUE;
  if (!host_read_allowed(mem)) return CL_INVALID_OPERATION;
  size_t source_row = 0, source_slice = 0, target_row = 0, target_slice = 0, source_end = 0, target_end = 0;
  if (!rect_pitch(buffer_row_pitch, buffer_slice_pitch, region, &source_row, &source_slice) ||
      !rect_pitch(host_row_pitch, host_slice_pitch, region, &target_row, &target_slice) ||
      !rect_end(buffer_origin, region, source_row, source_slice, &source_end) ||
      !rect_end(host_origin, region, target_row, target_slice, &target_end) ||
      source_end > mem->size)
    return CL_INVALID_VALUE;
  std::vector<uint8_t> row(region[0]);
  for (size_t z = 0; z < region[2]; ++z) {
    for (size_t y = 0; y < region[1]; ++y) {
      const size_t source_offset = mem->origin + (buffer_origin[2] + z) * source_slice +
                                   (buffer_origin[1] + y) * source_row + buffer_origin[0];
      const size_t target_offset = (host_origin[2] + z) * target_slice + (host_origin[1] + y) * target_row + host_origin[0];
      result = read_allocation(mem->allocation, source_offset, row.data(), row.size());
      if (result != CL_SUCCESS) return result;
      std::memcpy(static_cast<uint8_t *>(output) + target_offset, row.data(), row.size());
    }
  }
  return complete_event(queue, CL_COMMAND_READ_BUFFER, 0, nullptr, event);
}

cl_int enqueue_write_buffer_rect(_cl_command_queue *queue, _cl_mem *mem, const size_t buffer_origin[3],
                                 const size_t host_origin[3], const size_t region[3], size_t buffer_row_pitch,
                                 size_t buffer_slice_pitch, size_t host_row_pitch, size_t host_slice_pitch,
                                 const void *input, cl_uint count, const cl_event *waits, cl_event *event) {
  auto result = complete_event(queue, CL_COMMAND_WRITE_BUFFER, count, waits, nullptr);
  if (result != CL_SUCCESS) return result;
  if (mem == nullptr || mem->context != queue->context || input == nullptr || buffer_origin == nullptr ||
      host_origin == nullptr || region == nullptr)
    return CL_INVALID_VALUE;
  if (!host_write_allowed(mem)) return CL_INVALID_OPERATION;
#ifdef CL_MEM_IMMUTABLE_EXT
  if ((mem->flags & CL_MEM_IMMUTABLE_EXT) != 0) return CL_INVALID_OPERATION;
#endif
  size_t source_row = 0, source_slice = 0, target_row = 0, target_slice = 0, source_end = 0, target_end = 0;
  if (!rect_pitch(buffer_row_pitch, buffer_slice_pitch, region, &target_row, &target_slice) ||
      !rect_pitch(host_row_pitch, host_slice_pitch, region, &source_row, &source_slice) ||
      !rect_end(buffer_origin, region, target_row, target_slice, &target_end) ||
      !rect_end(host_origin, region, source_row, source_slice, &source_end) ||
      target_end > mem->size)
    return CL_INVALID_VALUE;
  std::vector<uint8_t> row(region[0]);
  for (size_t z = 0; z < region[2]; ++z) {
    for (size_t y = 0; y < region[1]; ++y) {
      const size_t source_offset = (host_origin[2] + z) * source_slice + (host_origin[1] + y) * source_row + host_origin[0];
      const size_t target_offset = mem->origin + (buffer_origin[2] + z) * target_slice +
                                   (buffer_origin[1] + y) * target_row + buffer_origin[0];
      std::memcpy(row.data(), static_cast<const uint8_t *>(input) + source_offset, row.size());
      result = write_allocation(mem->allocation, target_offset, row.data(), row.size());
      if (result != CL_SUCCESS) return result;
    }
  }
  return complete_event(queue, CL_COMMAND_WRITE_BUFFER, 0, nullptr, event);
}

cl_int enqueue_copy_buffer_rect(_cl_command_queue *queue, _cl_mem *source, _cl_mem *target,
                                const size_t source_origin[3], const size_t target_origin[3], const size_t region[3],
                                size_t source_row_pitch, size_t source_slice_pitch, size_t target_row_pitch,
                                size_t target_slice_pitch, cl_uint count, const cl_event *waits, cl_event *event) {
  auto result = complete_event(queue, CL_COMMAND_COPY_BUFFER, count, waits, nullptr);
  if (result != CL_SUCCESS) return result;
  if (source == nullptr || target == nullptr || source->context != queue->context || target->context != queue->context ||
      source_origin == nullptr || target_origin == nullptr || region == nullptr)
    return CL_INVALID_VALUE;
  size_t source_row = 0, source_slice = 0, target_row = 0, target_slice = 0, source_end = 0, target_end = 0;
  if (!rect_pitch(source_row_pitch, source_slice_pitch, region, &source_row, &source_slice) ||
      !rect_pitch(target_row_pitch, target_slice_pitch, region, &target_row, &target_slice) ||
      !rect_end(source_origin, region, source_row, source_slice, &source_end) ||
      !rect_end(target_origin, region, target_row, target_slice, &target_end) ||
      source_end > source->size || target_end > target->size)
    return CL_INVALID_VALUE;
  const size_t row_count = region[1] * region[2];
  if (region[0] != 0 && row_count > std::numeric_limits<size_t>::max() / region[0]) return CL_INVALID_VALUE;
  std::vector<uint8_t> packed(region[0] * row_count);
  size_t packed_offset = 0;
  for (size_t z = 0; z < region[2]; ++z) {
    for (size_t y = 0; y < region[1]; ++y) {
      const size_t source_offset = source->origin + (source_origin[2] + z) * source_slice +
                                   (source_origin[1] + y) * source_row + source_origin[0];
      result = read_allocation(source->allocation, source_offset, packed.data() + packed_offset, region[0]);
      if (result != CL_SUCCESS) return result;
      packed_offset += region[0];
    }
  }
  packed_offset = 0;
  for (size_t z = 0; z < region[2]; ++z) {
    for (size_t y = 0; y < region[1]; ++y) {
      const size_t source_offset = source->origin + (source_origin[2] + z) * source_slice +
                                   (source_origin[1] + y) * source_row + source_origin[0];
      const size_t target_offset = target->origin + (target_origin[2] + z) * target_slice +
                                   (target_origin[1] + y) * target_row + target_origin[0];
      result = write_allocation(target->allocation, target_offset, packed.data() + packed_offset, region[0]);
      if (result != CL_SUCCESS) return result;
      copy_pointer_relocations(source->allocation, source_offset, target->allocation, target_offset, region[0]);
      packed_offset += region[0];
    }
  }
  return complete_event(queue, CL_COMMAND_COPY_BUFFER, 0, nullptr, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueReadBufferRect(cl_command_queue queue_value, cl_mem mem_value, cl_bool,
                                                        const size_t *buffer_origin, const size_t *host_origin,
                                                        const size_t *region, size_t buffer_row_pitch,
                                                        size_t buffer_slice_pitch, size_t host_row_pitch,
                                                        size_t host_slice_pitch, void *output, cl_uint count,
                                                        const cl_event *waits, cl_event *event) {
  return enqueue_read_buffer_rect(checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue),
                                  checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory), buffer_origin,
                                  host_origin, region, buffer_row_pitch, buffer_slice_pitch, host_row_pitch,
                                  host_slice_pitch, output, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueWriteBufferRect(cl_command_queue queue_value, cl_mem mem_value, cl_bool,
                                                         const size_t *buffer_origin, const size_t *host_origin,
                                                         const size_t *region, size_t buffer_row_pitch,
                                                         size_t buffer_slice_pitch, size_t host_row_pitch,
                                                         size_t host_slice_pitch, const void *input, cl_uint count,
                                                         const cl_event *waits, cl_event *event) {
  return enqueue_write_buffer_rect(checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue),
                                   checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory), buffer_origin,
                                   host_origin, region, buffer_row_pitch, buffer_slice_pitch, host_row_pitch,
                                   host_slice_pitch, input, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBufferRect(cl_command_queue queue_value, cl_mem source_value,
                                                        cl_mem target_value, const size_t *source_origin,
                                                        const size_t *target_origin, const size_t *region,
                                                        size_t source_row_pitch, size_t source_slice_pitch,
                                                        size_t target_row_pitch, size_t target_slice_pitch,
                                                        cl_uint count, const cl_event *waits, cl_event *event) {
  return enqueue_copy_buffer_rect(checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue),
                                  checked(reinterpret_cast<_cl_mem *>(source_value), Kind::Memory),
                                  checked(reinterpret_cast<_cl_mem *>(target_value), Kind::Memory), source_origin,
                                  target_origin, region, source_row_pitch, source_slice_pitch, target_row_pitch,
                                  target_slice_pitch, count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueCopyBuffer(cl_command_queue queue_value, cl_mem src_value, cl_mem dst_value, size_t src_offset,
                                                    size_t dst_offset, size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); auto *src = checked(reinterpret_cast<_cl_mem *>(src_value), Kind::Memory); auto *dst = checked(reinterpret_cast<_cl_mem *>(dst_value), Kind::Memory);
  auto result = complete_event(queue, CL_COMMAND_COPY_BUFFER, count, waits, nullptr); if (result != CL_SUCCESS) return result;
  if (!src || !dst || src->context != queue->context || dst->context != queue->context ||
      !valid_mem_range(src, src_offset, size) || !valid_mem_range(dst, dst_offset, size)) return CL_INVALID_VALUE;
  const size_t source_offset = allocation_offset(src, src_offset);
  const size_t target_offset = allocation_offset(dst, dst_offset);
  std::memmove(static_cast<uint8_t *>(dst->allocation->base) + target_offset,
               static_cast<uint8_t *>(src->allocation->base) + source_offset, size);
  result = write_allocation(dst->allocation, target_offset, static_cast<uint8_t *>(dst->allocation->base) + target_offset, size); if (result != CL_SUCCESS) return result;
  copy_pointer_relocations(src->allocation, source_offset, dst->allocation, target_offset, size);
  return complete_event(queue, CL_COMMAND_COPY_BUFFER, 0, nullptr, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueFillBuffer(cl_command_queue queue_value, cl_mem mem_value, const void *pattern, size_t pattern_size,
                                                    size_t offset, size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); auto *mem = checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory);
  auto result = validate_wait_list(queue, count, waits); if (result != CL_SUCCESS) return result;
  if (!mem || mem->context != queue->context || pattern == nullptr || pattern_size == 0 || size % pattern_size != 0 || !valid_mem_range(mem, offset, size)) return CL_INVALID_VALUE;
  const size_t target_offset = allocation_offset(mem, offset);
  auto *bytes = static_cast<uint8_t *>(mem->allocation->base); for (size_t index = 0; index < size; ++index) bytes[target_offset + index] = static_cast<const uint8_t *>(pattern)[index % pattern_size];
  result = write_allocation(mem->allocation, target_offset, bytes + target_offset, size); if (result != CL_SUCCESS) return result;
  return complete_event(queue, CL_COMMAND_FILL_BUFFER, 0, nullptr, event);
}

CL_API_ENTRY void *CL_API_CALL clEnqueueMapBuffer(cl_command_queue queue_value, cl_mem mem_value, cl_bool,
                                                  cl_map_flags flags, size_t offset, size_t size, cl_uint count,
                                                  const cl_event *waits, cl_event *event, cl_int *error) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  auto *mem = checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory);
  if (queue == nullptr) { if (error) *error = CL_INVALID_COMMAND_QUEUE; return nullptr; }
  if (mem == nullptr || mem->context != queue->context) { if (error) *error = CL_INVALID_MEM_OBJECT; return nullptr; }
  if ((flags & ~(CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0 ||
      (flags & (CL_MAP_READ | CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) == 0 ||
      !valid_mem_range(mem, offset, size)) {
    if (error) *error = CL_INVALID_VALUE;
    return nullptr;
  }
  if (((flags & CL_MAP_READ) != 0 && !host_read_allowed(mem)) ||
      ((flags & (CL_MAP_WRITE | CL_MAP_WRITE_INVALIDATE_REGION)) != 0 && !host_write_allowed(mem))) {
    if (error) *error = CL_INVALID_OPERATION;
    return nullptr;
  }
  cl_int result = validate_wait_list(queue, count, waits);
  if (result != CL_SUCCESS) { if (error) *error = result; return nullptr; }
  const size_t global_offset = allocation_offset(mem, offset);
  const bool direct_mapping = mem->host_ptr != nullptr && (mem->flags & CL_MEM_USE_HOST_PTR) != 0;
  void *pointer = direct_mapping
    ? static_cast<uint8_t *>(mem->host_ptr) + offset
    : std::malloc(size == 0 ? 1 : size);
  if (pointer == nullptr) { if (error) *error = CL_OUT_OF_HOST_MEMORY; return nullptr; }
  if ((flags & CL_MAP_WRITE_INVALIDATE_REGION) == 0) {
    result = read_allocation(mem->allocation, global_offset, pointer, size);
    if (result != CL_SUCCESS) { if (!direct_mapping) std::free(pointer); if (error) *error = result; return nullptr; }
  }
  mem->mappings.emplace(reinterpret_cast<uintptr_t>(pointer),
                        BufferMapping{global_offset, size, pointer, !direct_mapping});
  result = complete_event(queue, CL_COMMAND_MAP_BUFFER, 0, nullptr, event);
  if (result != CL_SUCCESS) {
    mem->mappings.erase(reinterpret_cast<uintptr_t>(pointer));
    if (!direct_mapping) std::free(pointer);
  }
  if (error) *error = result;
  return result == CL_SUCCESS ? pointer : nullptr;
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueUnmapMemObject(cl_command_queue queue_value, cl_mem mem_value,
                                                        void *pointer, cl_uint count, const cl_event *waits,
                                                        cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  auto *mem = checked(reinterpret_cast<_cl_mem *>(mem_value), Kind::Memory);
  if (queue == nullptr) return CL_INVALID_COMMAND_QUEUE;
  if (mem == nullptr || mem->context != queue->context || pointer == nullptr) return CL_INVALID_MEM_OBJECT;
  cl_int result = validate_wait_list(queue, count, waits);
  if (result != CL_SUCCESS) return result;
  const auto mapping = mem->mappings.find(reinterpret_cast<uintptr_t>(pointer));
  if (mapping == mem->mappings.end()) return CL_INVALID_VALUE;
  const auto mapped = mapping->second;
  result = write_allocation(mem->allocation, mapped.offset, mapped.pointer, mapped.size);
  if (result != CL_SUCCESS) return result;
  mem->mappings.erase(mapping);
  if (mapped.release_pointer) std::free(mapped.pointer);
  return complete_event(queue, CL_COMMAND_UNMAP_MEM_OBJECT, 0, nullptr, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueMigrateMemObjects(cl_command_queue queue_value, cl_uint count,
                                                           const cl_mem *mem_objects, cl_mem_migration_flags,
                                                           cl_uint wait_count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  if (queue == nullptr) return CL_INVALID_COMMAND_QUEUE;
  if (count == 0 || mem_objects == nullptr) return CL_INVALID_VALUE;
  for (cl_uint index = 0; index < count; ++index)
    if (checked(reinterpret_cast<_cl_mem *>(mem_objects[index]), Kind::Memory) == nullptr) return CL_INVALID_MEM_OBJECT;
  return complete_event(queue, CL_COMMAND_MIGRATE_MEM_OBJECTS, wait_count, waits, event);
}

CL_API_ENTRY cl_int CL_API_CALL clEnqueueNDRangeKernel(cl_command_queue queue_value, cl_kernel kernel_value, cl_uint work_dim,
                                                       const size_t *offset, const size_t *global, const size_t *local, cl_uint count,
                                                       const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); auto *kernel = checked(reinterpret_cast<_cl_kernel *>(kernel_value), Kind::Kernel);
  if (!queue || !kernel) return !queue ? CL_INVALID_COMMAND_QUEUE : CL_INVALID_KERNEL;
  if (work_dim == 0 || work_dim > 3) return CL_INVALID_WORK_DIMENSION;
  if (kernel->arguments.size() != kernel->expected_arguments ||
      std::find(kernel->arguments_set.begin(), kernel->arguments_set.end(), false) != kernel->arguments_set.end())
    return CL_INVALID_KERNEL_ARGS;
  if (global == nullptr || std::any_of(global, global + work_dim, [](size_t value) { return value == 0; })) {
    if (local != nullptr) {
      for (cl_uint index = 0; index < work_dim; ++index)
        if (local[index] == 0) return CL_INVALID_WORK_GROUP_SIZE;
    }
    return complete_event(queue, CL_COMMAND_NDRANGE_KERNEL, count, waits, event);
  }
  if (local != nullptr && global != nullptr) {
    for (cl_uint index = 0; index < work_dim; ++index)
      if (local[index] == 0 || global[index] % local[index] != 0) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (local != nullptr && kernel->required_work_group_size != std::array<size_t, 3>{0, 0, 0}) {
    if (work_dim != 3) return CL_INVALID_WORK_GROUP_SIZE;
    for (size_t index = 0; index < 3; ++index)
      if (local[index] != kernel->required_work_group_size[index]) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if (kernel->program->build_options.find("-cl-uniform-work-group-size") != std::string::npos && local != nullptr) {
    for (cl_uint index = 0; index < work_dim; ++index)
      if (local[index] == 0 || global == nullptr || global[index] % local[index] != 0) return CL_INVALID_WORK_GROUP_SIZE;
  }
  if ((count != 0 && waits == nullptr) || (count == 0 && waits != nullptr)) return CL_INVALID_EVENT_WAIT_LIST;
  std::vector<_cl_event *> dependencies;
  dependencies.reserve(count);
  for (cl_uint index = 0; index < count; ++index) {
    auto *dependency = checked(reinterpret_cast<_cl_event *>(waits[index]), Kind::Event);
    if (dependency == nullptr) return CL_INVALID_EVENT_WAIT_LIST;
    if (dependency->context != queue->context) return CL_INVALID_CONTEXT;
    dependencies.push_back(dependency);
  }
  const auto prior = wait_queue(queue);
  if (prior != CL_SUCCESS) return prior;
  bool pending = false;
  {
    std::lock_guard lock(events_mutex);
    for (auto *dependency : dependencies) {
      if (dependency->status < 0) return CL_EXEC_STATUS_ERROR_FOR_EVENTS_IN_WAIT_LIST;
      pending = pending || dependency->status != CL_COMPLETE;
    }
  }
  if (pending) {
    _cl_event *completion = nullptr;
    if (event != nullptr) {
      completion = make_event(queue, CL_COMMAND_NDRANGE_KERNEL, event);
      if (completion == nullptr) return CL_OUT_OF_HOST_MEMORY;
      {
        std::lock_guard lock(events_mutex);
        completion->status = CL_SUBMITTED;
      }
      retain(completion);
    }
    const bool has_offset = offset != nullptr;
    const bool has_local = local != nullptr;
    std::array<size_t, 3> deferred_offset{0, 0, 0};
    std::array<size_t, 3> deferred_global{1, 1, 1};
    std::array<size_t, 3> deferred_local{1, 1, 1};
    for (cl_uint index = 0; index < work_dim; ++index) {
      if (has_offset) deferred_offset[index] = offset[index];
      deferred_global[index] = global[index];
      if (has_local) deferred_local[index] = local[index];
    }
    retain(queue);
    retain(kernel);
    for (auto *dependency : dependencies) retain(dependency);
    {
      std::lock_guard lock(queue->pending_mutex);
      ++queue->pending_commands;
    }
    std::thread([queue, kernel, work_dim, dependencies = std::move(dependencies), completion, has_offset,
                 has_local, deferred_offset, deferred_global, deferred_local]() {
      std::vector<cl_event> waits;
      waits.reserve(dependencies.size());
      for (auto *dependency : dependencies) waits.push_back(reinterpret_cast<cl_event>(dependency));
      cl_int result = wait_list(nullptr, static_cast<cl_uint>(waits.size()), waits.data());
      if (result == CL_SUCCESS)
        result = finish_remote(queue, kernel, work_dim, has_offset ? deferred_offset.data() : nullptr,
                               deferred_global.data(), has_local ? deferred_local.data() : nullptr, nullptr);
      if (completion != nullptr) {
        {
          std::lock_guard lock(events_mutex);
          completion->status = result == CL_SUCCESS ? CL_COMPLETE : result;
          completion->ended = now_nanos();
        }
        events_condition.notify_all();
        notify_event_callbacks(completion);
        clReleaseEvent(reinterpret_cast<cl_event>(completion));
      }
      for (auto *dependency : dependencies) clReleaseEvent(reinterpret_cast<cl_event>(dependency));
      {
        std::lock_guard lock(queue->pending_mutex);
        queue->pending_failed = queue->pending_failed || result != CL_SUCCESS;
        --queue->pending_commands;
      }
      queue->pending_condition.notify_all();
      clReleaseKernel(reinterpret_cast<cl_kernel>(kernel));
      clReleaseCommandQueue(reinterpret_cast<cl_command_queue>(queue));
    }).detach();
    return CL_SUCCESS;
  }
  auto result = wait_list(queue, count, waits); if (result != CL_SUCCESS) return result;
  result = finish_remote(queue, kernel, work_dim, offset, global, local, event);
  if (result == CL_SUCCESS && event != nullptr) reinterpret_cast<_cl_event *>(*event)->queue = queue;
  return result;
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueTask(cl_command_queue queue, cl_kernel kernel, cl_uint count, const cl_event *waits, cl_event *event) {
  const size_t global = 1;
  return clEnqueueNDRangeKernel(queue, kernel, 1, nullptr, &global, nullptr, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMarkerWithWaitList(cl_command_queue queue_value, cl_uint count, const cl_event *waits, cl_event *event) {
  return complete_event(checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue), CL_COMMAND_MARKER, count, waits, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueBarrierWithWaitList(cl_command_queue queue_value, cl_uint count, const cl_event *waits, cl_event *event) {
  return complete_event(checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue), CL_COMMAND_BARRIER, count, waits, event);
}

CL_API_ENTRY void *CL_API_CALL clSVMAlloc(cl_context context_value, cl_svm_mem_flags, size_t size, cl_uint alignment) {
  if (!checked(reinterpret_cast<_cl_context *>(context_value), Kind::Context)) return nullptr;
  cl_int error = CL_SUCCESS;
  auto allocation = allocate(size, &error, alignment);
  if (allocation != nullptr) {
    allocation->externally_mutable.store(true, std::memory_order_relaxed);
    allocation->runtime_owns_base = true;
  }
  return allocation == nullptr ? nullptr : allocation->base;
}
CL_API_ENTRY void CL_API_CALL clSVMFree(cl_context, void *pointer) {
  std::shared_ptr<Allocation> allocation; {
    std::lock_guard lock(allocations_mutex);
    auto found = allocations.find(reinterpret_cast<uintptr_t>(pointer));
    if (found == allocations.end()) return;
    allocation = found->second;
    const auto start = reinterpret_cast<uintptr_t>(allocation->base);
    for (auto mapping = svm_mappings.begin(); mapping != svm_mappings.end();) {
      if (mapping->first >= start && mapping->first <= start + allocation->size) mapping = svm_mappings.erase(mapping);
      else ++mapping;
    }
    allocations.erase(found);
  }
  release_allocation(allocation, true);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemcpy(cl_command_queue queue_value, cl_bool, void *dst, const void *src, size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue); if (!queue) return CL_INVALID_COMMAND_QUEUE; auto result = wait_list(queue, count, waits); if (result != CL_SUCCESS) return result;
  size_t dst_offset = 0, src_offset = 0;
  auto dst_allocation = find_pointer(dst, &dst_offset);
  auto src_allocation = find_pointer(src, &src_offset);
  if (dst_allocation != nullptr && !valid_range(dst_allocation, dst_offset, size)) return CL_INVALID_VALUE;
  if (src_allocation != nullptr && !valid_range(src_allocation, src_offset, size)) return CL_INVALID_VALUE;
  if (dst_allocation == nullptr && src_allocation == nullptr) return CL_INVALID_VALUE;
  // For an SVM-to-host copy, the remote mirror is the source of truth after a
  // kernel has written the allocation.  Keep the host-visible bytes as the
  // source for SVM-to-SVM copies; those are commonly used to upload host-side
  // changes between launches.
  if (src_allocation != nullptr && dst_allocation == nullptr) {
    result = read_allocation(src_allocation, src_offset, static_cast<uint8_t *>(src_allocation->base) + src_offset, size);
    if (result != CL_SUCCESS) return result;
  }
  if (dst_allocation != nullptr) {
    const void *source = src_allocation == nullptr
      ? src
      : static_cast<const uint8_t *>(src_allocation->base) + src_offset;
    std::memmove(static_cast<uint8_t *>(dst_allocation->base) + dst_offset, source, size);
    result = write_allocation(dst_allocation, dst_offset, static_cast<uint8_t *>(dst_allocation->base) + dst_offset, size);
    if (result != CL_SUCCESS) return result;
  } else {
    std::memmove(dst, static_cast<const uint8_t *>(src_allocation->base) + src_offset, size);
  }
  return complete_event(queue, CL_COMMAND_SVM_MEMCPY, 0, nullptr, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMemFill(cl_command_queue queue_value, void *pointer, const void *pattern, size_t pattern_size, size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto waited = wait_list(queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  size_t offset = 0; auto allocation = find_pointer(pointer, &offset); if (!allocation || pattern == nullptr || pattern_size == 0 || size % pattern_size != 0 || !valid_range(allocation, offset, size)) return CL_INVALID_VALUE;
  auto *bytes = static_cast<uint8_t *>(allocation->base); for (size_t index = 0; index < size; ++index) bytes[offset + index] = static_cast<const uint8_t *>(pattern)[index % pattern_size];
  auto result = write_allocation(allocation, offset, bytes + offset, size); if (result != CL_SUCCESS) return result;
  return complete_event(queue, CL_COMMAND_SVM_MEMFILL, 0, nullptr, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMMap(cl_command_queue queue_value, cl_bool, cl_map_flags, void *pointer, size_t size, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto waited = wait_list(queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  size_t offset = 0; auto allocation = find_pointer(pointer, &offset);
  if (!valid_range(allocation, offset, size)) return CL_INVALID_VALUE;
  {
    std::lock_guard lock(allocations_mutex);
    if (svm_mappings.find(reinterpret_cast<uintptr_t>(pointer)) != svm_mappings.end()) return CL_INVALID_OPERATION;
  }
  auto result = read_allocation(allocation, offset, static_cast<uint8_t *>(allocation->base) + offset, size);
  if (result != CL_SUCCESS) return result;
  {
    std::lock_guard lock(allocations_mutex);
    svm_mappings[reinterpret_cast<uintptr_t>(pointer)] = BufferMapping{offset, size, pointer};
  }
  return complete_event(queue, CL_COMMAND_SVM_MAP, 0, nullptr, event);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueSVMUnmap(cl_command_queue queue_value, void *pointer, cl_uint count, const cl_event *waits, cl_event *event) {
  auto *queue = checked(reinterpret_cast<_cl_command_queue *>(queue_value), Kind::Queue);
  if (!queue) return CL_INVALID_COMMAND_QUEUE;
  auto waited = wait_list(queue, count, waits);
  if (waited != CL_SUCCESS) return waited;
  BufferMapping mapping;
  {
    std::lock_guard lock(allocations_mutex);
    auto found = svm_mappings.find(reinterpret_cast<uintptr_t>(pointer));
    if (found == svm_mappings.end()) return CL_INVALID_VALUE;
    mapping = found->second;
  }
  size_t offset = 0; auto allocation = find_pointer(pointer, &offset);
  if (!valid_range(allocation, mapping.offset, mapping.size) || offset != mapping.offset) return CL_INVALID_VALUE;
  auto result = write_allocation(allocation, mapping.offset, static_cast<uint8_t *>(allocation->base) + mapping.offset, mapping.size);
  if (result != CL_SUCCESS) return result;
  {
    std::lock_guard lock(allocations_mutex);
    svm_mappings.erase(reinterpret_cast<uintptr_t>(pointer));
  }
  return complete_event(queue, CL_COMMAND_SVM_UNMAP, 0, nullptr, event);
}

CL_API_ENTRY void *CL_API_CALL clHostMemAllocINTEL(cl_context context, const cl_mem_properties_intel *, size_t size, cl_uint alignment, cl_int *error) {
  if (!checked(reinterpret_cast<_cl_context *>(context), Kind::Context)) { if (error) *error = CL_INVALID_CONTEXT; return nullptr; }
  auto allocation = allocate(size, error, alignment);
  if (allocation != nullptr) {
    allocation->externally_mutable.store(true, std::memory_order_relaxed);
    allocation->runtime_owns_base = true;
  }
  return allocation == nullptr ? nullptr : allocation->base;
}
CL_API_ENTRY void *CL_API_CALL clDeviceMemAllocINTEL(cl_context context, cl_device_id, const cl_mem_properties_intel *p, size_t size, cl_uint a, cl_int *error) { return clHostMemAllocINTEL(context, p, size, a, error); }
CL_API_ENTRY void *CL_API_CALL clSharedMemAllocINTEL(cl_context context, cl_device_id, const cl_mem_properties_intel *p, size_t size, cl_uint a, cl_int *error) { return clHostMemAllocINTEL(context, p, size, a, error); }
CL_API_ENTRY cl_int CL_API_CALL clMemFreeINTEL(cl_context, void *pointer) { clSVMFree(nullptr, pointer); return CL_SUCCESS; }
CL_API_ENTRY cl_int CL_API_CALL clMemBlockingFreeINTEL(cl_context context, void *pointer) { return clMemFreeINTEL(context, pointer); }
CL_API_ENTRY cl_int CL_API_CALL clGetMemAllocInfoINTEL(cl_context, const void *pointer, cl_mem_info_intel name, size_t size, void *output, size_t *written) {
  size_t offset = 0; auto allocation = find_pointer(pointer, &offset);
  if (!allocation) {
    if (name == CL_MEM_ALLOC_TYPE_INTEL) {
      cl_unified_shared_memory_type_intel type = CL_MEM_TYPE_UNKNOWN_INTEL;
      return output_scalar(size, output, written, type);
    }
    if (name == CL_MEM_ALLOC_DEVICE_INTEL) {
      cl_device_id device_value = nullptr;
      return output_scalar(size, output, written, device_value);
    }
    return CL_INVALID_VALUE;
  }
  switch (name) {
    case CL_MEM_ALLOC_TYPE_INTEL: { cl_unified_shared_memory_type_intel v = CL_MEM_TYPE_SHARED_INTEL; return output_scalar(size, output, written, v); }
    case CL_MEM_ALLOC_BASE_PTR_INTEL: return output_scalar(size, output, written, allocation->base);
    case CL_MEM_ALLOC_SIZE_INTEL: return output_scalar(size, output, written, allocation->size);
    case CL_MEM_ALLOC_DEVICE_INTEL: { cl_device_id v = reinterpret_cast<cl_device_id>(&device); return output_scalar(size, output, written, v); }
    default: return CL_INVALID_VALUE;
  }
}
CL_API_ENTRY cl_int CL_API_CALL clSetKernelArgMemPointerINTEL(cl_kernel value, cl_uint index, const void *pointer) {
  return clSetKernelArgSVMPointer(value, index, pointer);
}
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemcpyINTEL(cl_command_queue queue_value, cl_bool blocking, void *dst, const void *src, size_t size, cl_uint count, const cl_event *waits, cl_event *event) { return clEnqueueSVMMemcpy(queue_value, blocking, dst, src, size, count, waits, event); }
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemFillINTEL(cl_command_queue queue_value, void *dst, const void *pattern, size_t pattern_size, size_t size, cl_uint count, const cl_event *waits, cl_event *event) { return clEnqueueSVMMemFill(queue_value, dst, pattern, pattern_size, size, count, waits, event); }
CL_API_ENTRY cl_int CL_API_CALL clEnqueueMemAdviseINTEL(cl_command_queue queue, const void *, size_t, cl_mem_advice_intel, cl_uint count, const cl_event *waits, cl_event *event) { return complete_event(checked(reinterpret_cast<_cl_command_queue *>(queue), Kind::Queue), CL_COMMAND_MEMADVISE_INTEL, count, waits, event); }

CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddress(const char *name) {
  if (name == nullptr) return nullptr;
#define GEMSTONE_EXTENSION(function) if (std::strcmp(name, #function) == 0) return reinterpret_cast<void *>(&function)
  GEMSTONE_EXTENSION(clHostMemAllocINTEL); GEMSTONE_EXTENSION(clDeviceMemAllocINTEL); GEMSTONE_EXTENSION(clSharedMemAllocINTEL);
  GEMSTONE_EXTENSION(clMemFreeINTEL); GEMSTONE_EXTENSION(clMemBlockingFreeINTEL); GEMSTONE_EXTENSION(clGetMemAllocInfoINTEL);
  GEMSTONE_EXTENSION(clSetKernelArgMemPointerINTEL); GEMSTONE_EXTENSION(clEnqueueMemFillINTEL); GEMSTONE_EXTENSION(clEnqueueMemcpyINTEL);
  GEMSTONE_EXTENSION(clEnqueueMemAdviseINTEL); GEMSTONE_EXTENSION(clCreateProgramWithIL); GEMSTONE_EXTENSION(clSetKernelArgSVMPointer);
  GEMSTONE_EXTENSION(clSetKernelExecInfo);
#undef GEMSTONE_EXTENSION
  return nullptr;
}
CL_API_ENTRY void *CL_API_CALL clGetExtensionFunctionAddressForPlatform(cl_platform_id value, const char *name) {
  return valid_platform(value) ? clGetExtensionFunctionAddress(name) : nullptr;
}
CL_API_ENTRY cl_int CL_API_CALL clGetKernelSubGroupInfo(cl_kernel value, cl_device_id selected,
                                                        cl_kernel_sub_group_info name, size_t input_size,
                                                        const void *input, size_t size, void *output,
                                                        size_t *written) {
  if (!checked(reinterpret_cast<_cl_kernel *>(value), Kind::Kernel) || !valid_device(selected)) return CL_INVALID_KERNEL;
  constexpr size_t subgroup_size = 32;
  constexpr size_t max_work_group_size = 1024;
  if (name == CL_KERNEL_MAX_SUB_GROUP_SIZE_FOR_NDRANGE) {
    size_t v = subgroup_size;
    return output_scalar(size, output, written, v);
  }
  if (name == CL_KERNEL_MAX_NUM_SUB_GROUPS) {
    size_t v = max_work_group_size / subgroup_size;
    return output_scalar(size, output, written, v);
  }
  if (name == CL_KERNEL_SUB_GROUP_COUNT_FOR_NDRANGE) {
    if (input == nullptr || input_size == 0 || input_size % sizeof(size_t) != 0 ||
        input_size > 3 * sizeof(size_t)) return CL_INVALID_VALUE;
    const size_t dimensions = input_size / sizeof(size_t);
    const auto *local = static_cast<const size_t *>(input);
    size_t work_items = 1;
    for (size_t index = 0; index < dimensions; ++index) {
      if (local[index] == 0 || work_items > std::numeric_limits<size_t>::max() / local[index]) return CL_INVALID_VALUE;
      work_items *= local[index];
    }
    const size_t v = (work_items + subgroup_size - 1) / subgroup_size;
    return output_scalar(size, output, written, v);
  }
  if (name == CL_KERNEL_LOCAL_SIZE_FOR_SUB_GROUP_COUNT) {
    if (input == nullptr || input_size != sizeof(size_t) || output == nullptr ||
        size < sizeof(size_t)) return CL_INVALID_VALUE;
    size_t count = 0;
    std::memcpy(&count, input, sizeof(count));
    size_t local[3] = {0, 0, 0};
    if (count != 0 && count <= max_work_group_size / subgroup_size) {
      local[0] = count * subgroup_size;
      local[1] = 1;
      local[2] = 1;
    }
    return output_bytes(size, output, written, local, size < 3 * sizeof(size_t) ? size : 3 * sizeof(size_t));
  }
  return CL_INVALID_VALUE;
}

} // extern "C"

namespace {

void initialize_dispatch() {
  std::call_once(dispatch_initialized, [] {
    register_live_object(&platform);
    register_live_object(&device);
    platform.dispatch = &dispatch;
    device.dispatch = &dispatch;
    device.platform = &platform;
    dispatch.clGetPlatformIDs = &clGetPlatformIDs;
    dispatch.clGetPlatformInfo = &clGetPlatformInfo;
    dispatch.clGetDeviceIDs = &clGetDeviceIDs;
    dispatch.clGetDeviceInfo = &clGetDeviceInfo;
    dispatch.clRetainDevice = &clRetainDevice;
    dispatch.clReleaseDevice = &clReleaseDevice;
    dispatch.clGetDeviceAndHostTimer = &clGetDeviceAndHostTimer;
    dispatch.clGetHostTimer = &clGetHostTimer;
    dispatch.clCreateContext = &clCreateContext;
    dispatch.clCreateContextFromType = &clCreateContextFromType;
    dispatch.clRetainContext = &clRetainContext;
    dispatch.clReleaseContext = &clReleaseContext;
    dispatch.clSetContextDestructorCallback = &clSetContextDestructorCallback;
    dispatch.clGetContextInfo = &clGetContextInfo;
    dispatch.clCreateCommandQueue = &clCreateCommandQueue;
    dispatch.clRetainCommandQueue = &clRetainCommandQueue;
    dispatch.clReleaseCommandQueue = &clReleaseCommandQueue;
    dispatch.clSetCommandQueueProperty = &clSetCommandQueueProperty;
    dispatch.clGetCommandQueueInfo = &clGetCommandQueueInfo;
    dispatch.clCreateBuffer = &clCreateBuffer;
    dispatch.clCreateBufferWithProperties = &clCreateBufferWithProperties;
    dispatch.clCreatePipe = &clCreatePipe;
    dispatch.clCreateImage = &clCreateImage;
    dispatch.clCreateImageWithProperties = &clCreateImageWithProperties;
    dispatch.clCreateSubBuffer = &clCreateSubBuffer;
    dispatch.clGetSupportedImageFormats = &clGetSupportedImageFormats;
    dispatch.clRetainMemObject = &clRetainMemObject;
    dispatch.clReleaseMemObject = &clReleaseMemObject;
    dispatch.clSetMemObjectDestructorCallback = &clSetMemObjectDestructorCallback;
    dispatch.clGetMemObjectInfo = &clGetMemObjectInfo;
    dispatch.clGetImageInfo = &clGetImageInfo;
    dispatch.clGetPipeInfo = &clGetPipeInfo;
    dispatch.clCreateProgramWithSource = &clCreateProgramWithSource;
    dispatch.clCreateProgramWithBinary = &clCreateProgramWithBinary;
    dispatch.clRetainProgram = &clRetainProgram;
    dispatch.clReleaseProgram = &clReleaseProgram;
    dispatch.clBuildProgram = &clBuildProgram;
    dispatch.clUnloadCompiler = &clUnloadCompiler;
    dispatch.clCompileProgram = &clCompileProgram;
    dispatch.clLinkProgram = &clLinkProgram;
    dispatch.clUnloadPlatformCompiler = &clUnloadPlatformCompiler;
    dispatch.clGetProgramInfo = &clGetProgramInfo;
    dispatch.clGetProgramBuildInfo = &clGetProgramBuildInfo;
    dispatch.clSetProgramReleaseCallback = &clSetProgramReleaseCallback;
    dispatch.clCreateKernel = &clCreateKernel;
    dispatch.clCreateKernelsInProgram = &clCreateKernelsInProgram;
    dispatch.clCloneKernel = &clCloneKernel;
    dispatch.clRetainKernel = &clRetainKernel;
    dispatch.clReleaseKernel = &clReleaseKernel;
    dispatch.clSetKernelArg = &clSetKernelArg;
    dispatch.clGetKernelInfo = &clGetKernelInfo;
    dispatch.clGetKernelWorkGroupInfo = &clGetKernelWorkGroupInfo;
    dispatch.clGetKernelArgInfo = &clGetKernelArgInfo;
    dispatch.clSetKernelExecInfo = &clSetKernelExecInfo;
    dispatch.clWaitForEvents = &clWaitForEvents;
    dispatch.clCreateUserEvent = &clCreateUserEvent;
    dispatch.clSetUserEventStatus = &clSetUserEventStatus;
    dispatch.clSetEventCallback = &clSetEventCallback;
    dispatch.clGetEventInfo = &clGetEventInfo;
    dispatch.clRetainEvent = &clRetainEvent;
    dispatch.clReleaseEvent = &clReleaseEvent;
    dispatch.clGetEventProfilingInfo = &clGetEventProfilingInfo;
    dispatch.clFlush = &clFlush;
    dispatch.clFinish = &clFinish;
    dispatch.clEnqueueReadBuffer = &clEnqueueReadBuffer;
    dispatch.clEnqueueWriteBuffer = &clEnqueueWriteBuffer;
    dispatch.clEnqueueReadBufferRect = &clEnqueueReadBufferRect;
    dispatch.clEnqueueWriteBufferRect = &clEnqueueWriteBufferRect;
    dispatch.clEnqueueCopyBufferRect = &clEnqueueCopyBufferRect;
    dispatch.clEnqueueCopyBuffer = &clEnqueueCopyBuffer;
    dispatch.clEnqueueMapBuffer = &clEnqueueMapBuffer;
    dispatch.clEnqueueUnmapMemObject = &clEnqueueUnmapMemObject;
    dispatch.clEnqueueMigrateMemObjects = &clEnqueueMigrateMemObjects;
    dispatch.clEnqueueNDRangeKernel = &clEnqueueNDRangeKernel;
    dispatch.clEnqueueTask = &clEnqueueTask;
    dispatch.clGetExtensionFunctionAddress = &clGetExtensionFunctionAddress;
    dispatch.clCreateCommandQueueWithProperties = &clCreateCommandQueueWithProperties;
    dispatch.clSetDefaultDeviceCommandQueue = &clSetDefaultDeviceCommandQueue;
    dispatch.clSVMAlloc = &clSVMAlloc;
    dispatch.clSVMFree = &clSVMFree;
    dispatch.clSetKernelArgSVMPointer = &clSetKernelArgSVMPointer;
    dispatch.clEnqueueSVMMemcpy = &clEnqueueSVMMemcpy;
    dispatch.clEnqueueSVMMemFill = &clEnqueueSVMMemFill;
    dispatch.clEnqueueSVMMap = &clEnqueueSVMMap;
    dispatch.clEnqueueSVMUnmap = &clEnqueueSVMUnmap;
    dispatch.clCreateProgramWithIL = &clCreateProgramWithIL;
    dispatch.clEnqueueFillBuffer = &clEnqueueFillBuffer;
    dispatch.clEnqueueMarkerWithWaitList = &clEnqueueMarkerWithWaitList;
    dispatch.clEnqueueBarrierWithWaitList = &clEnqueueBarrierWithWaitList;
    dispatch.clGetExtensionFunctionAddressForPlatform = &clGetExtensionFunctionAddressForPlatform;
    dispatch.clGetKernelSubGroupInfo = &clGetKernelSubGroupInfo;
  });
}

} // namespace
