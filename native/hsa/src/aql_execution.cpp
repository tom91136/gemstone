#include "gemstone/hsa/aql_execution.hpp"

#include <algorithm>
#include <cstdio>
#include <unordered_set>
#include <utility>

#include <aspartame/unordered_map.hpp>
#include <aspartame/vector.hpp>

#include <gemstone/bytes.hpp>
#include <gemstone/trace.hpp>

#include "completion.hpp"
#include <environment_keys.hpp>

namespace gemstone::hsa {

namespace {

using namespace aspartame;
using namespace gemstone::execution;

uint32_t divideRoundingUp(uint32_t value, uint32_t divisor) { return value / divisor + static_cast<uint32_t>(value % divisor != 0); }

bool tracePackets() { return traceEnabled("hsa", "packets"); }

std::optional<KernelLayout> layoutFrom(const KernelMetadata &metadata) {
  KernelLayout layout{
      static_cast<uint32_t>(metadata.argumentBlockSize.value), static_cast<uint32_t>(metadata.explicitParameterCount.value), {}};
  for (const auto &parameter : metadata.parameters) {
    const KernargField field{parameter.kind, static_cast<uint32_t>(parameter.offset.value), static_cast<uint32_t>(parameter.width.value),
                             parameter.containsPointers};
    const uint64_t end = static_cast<uint64_t>(field.offset) + field.size;
    if ((field.kind != ParameterKind::Scalar && field.kind != ParameterKind::Allocation) || end > layout.kernargSize
        || (field.kind == ParameterKind::Allocation && field.size != sizeof(uint64_t)))
      return std::nullopt;
    layout.fields.push_back(field);
  }
  layout.fields ^= sort_by([](const auto &field) { return field.offset; });
  for (size_t index = 1; index < layout.fields.size(); ++index) {
    const auto &previous = layout.fields[index - 1];
    if (static_cast<uint64_t>(previous.offset) + previous.size > layout.fields[index].offset) return std::nullopt;
  }
  return layout;
}

} // namespace

bool RocrSignals::load(uint64_t signal, int64_t &value) {
  if (signal == 0) return false;
  value = __atomic_load_n(reinterpret_cast<const int64_t *>(static_cast<uintptr_t>(signal) + 8), __ATOMIC_ACQUIRE);
  return true;
}

bool RocrSignals::complete(uint64_t signal) {
  if (signal == 0) return false;
  __atomic_fetch_sub(reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8), static_cast<int64_t>(1), __ATOMIC_RELEASE);
  return true;
}

bool RocrSignals::fail(uint64_t signal, int32_t code) {
  if (signal == 0) return false;
  const int64_t value = code < 0 ? code : -static_cast<int64_t>(code) - 1;
  __atomic_store_n(reinterpret_cast<int64_t *>(static_cast<uintptr_t>(signal) + 8), value, __ATOMIC_RELEASE);
  return true;
}

AqlRuntime::AqlRuntime(std::unique_ptr<Signals> signals) : signals(std::move(signals)) {}

std::unique_ptr<AqlRuntime> AqlRuntime::fromEnvironment(std::unique_ptr<Signals> signals) {
  std::unique_ptr<AqlRuntime> runtime(new AqlRuntime(std::move(signals)));
  return runtime->client() ? std::move(runtime) : nullptr;
}

AqlRuntime::~AqlRuntime() {
  auto *connection = client();
  if (!connection) return;
  for (const auto &[program, _] : programReferences)
    (void)connection->call(UnloadProgram{ProgramId{program}});
}

Client *AqlRuntime::client() {
  std::lock_guard guard(lock);
  auto &value = clients[std::this_thread::get_id()];
  if (!value) value = Client::fromEnvironment();
  return value.get();
}

std::string AqlRuntime::lastError() const {
  std::lock_guard guard(lock);
  return failure;
}

void AqlRuntime::fail(std::string message) {
  std::lock_guard guard(lock);
  failure = std::move(message);
}

bool AqlRuntime::installCodeObject(std::span<const uint8_t> image, std::span<const KernelSymbol> kernels) {
  if (image.empty() || kernels.empty()) return false;
  auto *connection = client();
  if (!connection) return false;
  if (tracePackets()) std::fprintf(stderr, "Gemstone loading HSA code object bytes=%zu\n", image.size());
  const auto program = connection->call(LoadProgram{ProgramEncoding::HSACodeObject, std::vector<uint8_t>(image.begin(), image.end())});
  if (!program) {
    fail(program.error().detail);
    return false;
  }
  return installLoadedProgram(program.value(), kernels);
}

bool AqlRuntime::installLoadedProgram(ProgramId program, std::span<const KernelSymbol> kernels) {
  auto *connection = client();
  if (!connection) return false;
  const auto reject = [&](std::string message) {
    (void)connection->call(UnloadProgram{program});
    fail(std::move(message));
    return false;
  };
  std::unordered_set<uint64_t> objects;
  for (const auto &symbol : kernels)
    if (symbol.object == 0 || symbol.name.empty() || !objects.insert(symbol.object).second) return reject("invalid AMDGPU kernel binding");
  if (kernels.empty()) return reject("invalid AMDGPU kernel binding");
  const auto installed = [&] {
    std::lock_guard guard(lock);
    return std::any_of(kernels.begin(), kernels.end(), [&](const auto &symbol) { return installedKernels.contains(symbol.object); });
  };
  if (installed()) return reject("kernel object is already installed");
  std::vector<std::pair<uint64_t, Kernel>> bound;
  for (const auto &symbol : kernels) {
    const auto metadata = connection->call(DescribeKernel{program, symbol.name});
    if (!metadata) return reject(metadata.error().detail);
    auto layout = layoutFrom(metadata.value());
    if (!layout) return reject("execution service returned invalid AMDGPU kernel metadata");
    bound.emplace_back(symbol.object, Kernel{program, symbol.name, std::move(*layout)});
  }
  {
    std::lock_guard guard(lock);
    const bool collided = bound ^ exists([&](const auto &entry) { return installedKernels.contains(entry.first); });
    if (!collided) {
      for (auto &[object, kernel] : bound)
        installedKernels.emplace(object, std::move(kernel));
      programReferences.emplace(program.value, static_cast<uint32_t>(bound.size()));
      return true;
    }
  }
  return reject("kernel object is already installed");
}

std::optional<AqlRuntime::Kernel> AqlRuntime::kernel(uint64_t kernelObject) const {
  std::lock_guard guard(lock);
  const auto found = installedKernels.find(kernelObject);
  return found == installedKernels.end() ? std::nullopt : std::optional(found->second);
}

std::optional<KernelLayout> AqlRuntime::layout(uint64_t kernelObject) const {
  const auto found = kernel(kernelObject);
  return found ? std::optional(found->layout) : std::nullopt;
}

bool AqlRuntime::removeKernel(uint64_t kernelObject) {
  ProgramId program{};
  {
    std::lock_guard guard(lock);
    const auto found = installedKernels.find(kernelObject);
    if (found == installedKernels.end()) return false;
    program = found->second.program;
    installedKernels.erase(found);
    const auto references = programReferences.find(program.value);
    if (references == programReferences.end() || references->second == 0) return false;
    if (--references->second != 0) return true;
    programReferences.erase(references);
  }
  auto *connection = client();
  return connection && connection->call(UnloadProgram{program});
}

bool AqlRuntime::bindGlobal(uint64_t kernelObject, const std::string &name, uint64_t allocation, uint64_t offset) {
  if (name.empty() || allocation == 0) return false;
  const auto found = kernel(kernelObject);
  if (!found) {
    fail("global belongs to an unknown kernel object");
    return false;
  }
  auto *connection = client();
  if (!connection) return false;
  const auto bound = connection->call(
      BindProgramGlobal{found->program, name, AllocationId{static_cast<int64_t>(allocation)}, static_cast<int64_t>(offset)});
  if (!bound) fail(bound.error().detail);
  return static_cast<bool>(bound);
}

bool AqlRuntime::registerAllocation(uint64_t base, uint64_t size, uint64_t allocation) {
  if (base == 0 || size == 0 || allocation == 0 || base > UINT64_MAX - size) return false;
  std::lock_guard guard(lock);
  if (allocations ^ exists([&](uint64_t other, const Allocation &entry) { return base < other + entry.size && other < base + size; }))
    return false;
  allocations.emplace(base, Allocation{size, allocation});
  return true;
}

bool AqlRuntime::unregisterAllocation(uint64_t base) {
  std::lock_guard guard(lock);
  return allocations.erase(base) == 1;
}

std::vector<DirtyRange> AqlRuntime::takeDirtyRanges() {
  std::lock_guard guard(lock);
  return std::exchange(dirtyRanges, {});
}

std::optional<AqlRuntime::Resolved> AqlRuntime::resolve(uint64_t pointer) {
  std::lock_guard guard(lock);
  // Prefer an allocation containing the address, in particular an exact base,
  // over a one-past endpoint of another allocation. C/C++ kernels routinely
  // carry one-past pointers as loop bounds; the service admits offset == size.
  std::optional<Resolved> endpoint;
  for (const auto &[base, allocation] : allocations) {
    if (pointer < base || pointer - base > allocation.size) continue;
    if (pointer - base < allocation.size) return Resolved{allocation.id, pointer - base};
    endpoint = Resolved{allocation.id, allocation.size};
  }
  return endpoint;
}

bool AqlRuntime::relocations(const Kernel &kernel, uint64_t kernarg, std::optional<ArgumentBuffer> &buffer,
                             std::vector<PointerRelocation> &relocations) {
  if (kernarg == 0) return kernel.layout.kernargSize == 0;
  {
    std::lock_guard guard(lock);
    for (const auto &[base, allocation] : allocations)
      if (kernarg >= base && kernarg - base < allocation.size && kernel.layout.kernargSize <= allocation.size - (kernarg - base)) {
        buffer = ArgumentBuffer{AllocationId{static_cast<int64_t>(allocation.id)}, static_cast<int64_t>(kernarg - base)};
        break;
      }
  }
  if (!buffer) return false;
  const auto *bytes = reinterpret_cast<const uint8_t *>(static_cast<uintptr_t>(kernarg));
  const auto relocate = [&](uint32_t offset, const Resolved &target) {
    relocations.push_back({RelocationSourceMemory{buffer->region}, static_cast<int64_t>(buffer->offset.value + offset),
                           AllocationId{static_cast<int64_t>(target.id)}, static_cast<int64_t>(target.offset)});
  };
  for (const auto &field : kernel.layout.fields) {
    if (field.kind == ParameterKind::Scalar && field.containsPointers) {
      // AMDGPU metadata exposes C++ functors as opaque by-value fields.
      // Naturally aligned words resolving to registered allocations are relocated.
      for (uint32_t cursor = 0; cursor + sizeof(uint64_t) <= field.size;) {
        const uint32_t offset = field.offset + cursor;
        if (offset % alignof(uint64_t) != 0) {
          ++cursor;
          continue;
        }
        if (const auto target = resolve(readNative<uint64_t>(bytes, offset))) relocate(offset, *target);
        cursor += sizeof(uint64_t);
      }
    } else if (field.kind == ParameterKind::Allocation) {
      const auto pointer = readNative<uint64_t>(bytes, field.offset);
      // LLVM's OpenMP AMDGPU plugin prepends a kernel-launch-environment
      // pointer to every target kernel and uses ~0 as the sentinel when there
      // is no environment object. Null buffer arguments are legal too.
      if ((field.offset == 0 && pointer == UINT64_MAX) || pointer == 0) continue;
      if (const auto target = resolve(pointer)) relocate(field.offset, *target);
      else if (!environment::enabled(environment::SystemPaging)) return false;
    }
  }
  return true;
}

PacketResult AqlRuntime::failPacket(uint64_t completionSignal, PacketFailure failure, std::string message) {
  if (tracePackets()) std::fprintf(stderr, "Gemstone AQL dispatch failure: %s\n", message.c_str());
  fail(std::move(message));
  const bool published = signals->fail(completionSignal, static_cast<int32_t>(failure));
  return completionSignal != 0 && published ? PacketResult::Accepted : PacketResult::Fatal;
}

PacketResult AqlRuntime::barrier(const AqlPacket &packet) {
  const bool any = packet.type == PacketType::BarrierOr;
  bool anySatisfied = false, allSatisfied = true, hasDependency = false, readable = true;
  for (uint64_t signal : packet.dependencySignals) {
    if (signal == 0) {
      if (any) allSatisfied = false;
      continue;
    }
    hasDependency = true;
    int64_t value = 0;
    if (!signals->load(signal, value)) {
      readable = false;
      break;
    }
    anySatisfied = anySatisfied || value < 1;
    allSatisfied = allSatisfied && value < 1;
  }
  const bool ready = any ? hasDependency && anySatisfied : allSatisfied;
  if (tracePackets()) {
    std::fprintf(stderr, "Gemstone observed AQL barrier=%s state=%s completion=%lu dependencies=", any ? "or" : "and",
                 !readable ? "error"
                 : ready   ? "ready"
                           : "retry",
                 packet.completionSignal);
    for (uint64_t signal : packet.dependencySignals) {
      int64_t value = 0;
      if (signal != 0) std::fprintf(stderr, "%lu:%s%ld ", signal, signals->load(signal, value) ? "" : "error/", value);
    }
    std::fprintf(stderr, "\n");
  }
  if (!readable) return failPacket(packet.completionSignal, PacketFailure::UnreadableDependency, "cannot read barrier dependency signal");
  if (!ready) return PacketResult::Retry;
  if (packet.completionSignal != 0 && !signals->complete(packet.completionSignal)) return PacketResult::Fatal;
  return PacketResult::Accepted;
}

PacketResult AqlRuntime::dispatch(const AqlPacket &packet) {
  const auto &work = packet.dispatch;
  const auto found = kernel(work.kernelObject);
  if (!found) return failPacket(packet.completionSignal, PacketFailure::KernelNotInstalled, "kernel object is not installed");
  std::optional<ArgumentBuffer> buffer;
  std::vector<PointerRelocation> pointerRelocations;
  if (!relocations(*found, work.kernargAddress, buffer, pointerRelocations))
    return failPacket(packet.completionSignal, PacketFailure::InvalidMemoryReference, "kernarg buffer or allocation pointer is invalid");
  const auto failed = [&](const std::string &detail) {
    return failPacket(packet.completionSignal, PacketFailure::ExecutionFailed, found->name + ": " + detail);
  };

  const auto &group = work.workgroupSize;
  const auto &grid = work.gridSize;
  if (tracePackets())
    std::fprintf(stderr, "Gemstone launching AQL kernel=%s grid=%ux%ux%u group=%ux%ux%u relocations=%zu\n", found->name.c_str(), grid[0],
                 grid[1], grid[2], group[0], group[1], group[2], pointerRelocations.size());
  auto *connection = client();
  if (!connection) return failed("Gemstone execution RPC client is not configured");
  if (environment::enabled(environment::SystemPaging)) {
    if (const auto enabled = connection->call(EnableHostMemory{}); !enabled) return failed(enabled.error().detail);
  }
  const auto launched = connection->call(Launch{
      found->program,
      found->name,
      Dimensions{static_cast<int32_t>(group[0]), static_cast<int32_t>(group[1]), static_cast<int32_t>(group[2])},
      Dimensions{static_cast<int32_t>(divideRoundingUp(grid[0], group[0])), static_cast<int32_t>(divideRoundingUp(grid[1], group[1])),
                 static_cast<int32_t>(divideRoundingUp(grid[2], group[2]))},
      GlobalDimensions{static_cast<int64_t>(grid[0]), static_cast<int64_t>(grid[1]), static_cast<int64_t>(grid[2])},
      3,
      Coordinates{},
      static_cast<int32_t>(work.privateSegmentSize),
      static_cast<int32_t>(work.groupSegmentSize),
      {},
      std::move(pointerRelocations),
      false,
      {},
      std::move(buffer)});
  if (!launched) return failed(launched.error().detail);
  const auto completion = launched.value();
  std::string awaitError, queryError;
  std::vector<DirtyRange> dirty;
  const bool awaited = awaitCompletion(*connection, completion, awaitError);
  const bool queried = awaited && completionDirtyRanges(*connection, completion, dirty, queryError);
  const auto released = connection->call(ReleaseCompletion{completion});
  if (tracePackets())
    std::fprintf(stderr, "Gemstone completed AQL kernel=%s result=%d release=%d\n", found->name.c_str(), awaited ? 0 : -1,
                 released ? 0 : -1);
  if (!awaited) return failed(awaitError);
  if (!queried) return failed(queryError);
  if (!released) return failed(released.error().detail);
  if (!dirty.empty()) {
    std::lock_guard guard(lock);
    dirtyRanges.insert(dirtyRanges.end(), dirty.begin(), dirty.end());
  }
  if (packet.completionSignal != 0 && !signals->complete(packet.completionSignal)) return PacketResult::Fatal;
  return PacketResult::Accepted;
}

PacketResult AqlRuntime::consume(const void *packet) {
  AqlPacket decoded;
  const auto status = decodeAqlPacket(packet, AqlPacketSize, decoded);
  if (status != DecodeStatus::Success) {
    if (tracePackets()) std::fprintf(stderr, "Gemstone rejected AQL packet: %s\n", describe(status));
    const auto failure = status == DecodeStatus::InvalidDimensions ? PacketFailure::InvalidDimensions
                         : status == DecodeStatus::InvalidGrid     ? PacketFailure::InvalidGrid
                                                                   : PacketFailure::MalformedPacket;
    return failPacket(decoded.completionSignal, failure, describe(status));
  }
  if (decoded.type == PacketType::VendorSpecific) {
    if (tracePackets())
      std::fprintf(stderr, "Gemstone observed AQL PM4-IB synchronisation packet completion=%lu\n", decoded.completionSignal);
    if (decoded.completionSignal != 0 && !signals->complete(decoded.completionSignal)) return PacketResult::Fatal;
    return PacketResult::Accepted;
  }
  if (decoded.type == PacketType::BarrierAnd || decoded.type == PacketType::BarrierOr) return barrier(decoded);
  return dispatch(decoded);
}

} // namespace gemstone::hsa
