#pragma once

#include <map>
#include <memory>
#include <mutex>
#include <optional>
#include <span>
#include <string>
#include <thread>
#include <unordered_map>
#include <vector>

#include <stdint.h>

#include "gemstone/hsa/aql.hpp"
#include "gemstone/hsa/doorbell_monitor.hpp"
#include "gemstone_execution_rpc.hpp"

namespace gemstone::hsa {

struct KernargField {
  execution::ParameterKind kind = execution::ParameterKind::Scalar;
  uint32_t offset = 0;
  uint32_t size = 0;
  bool containsPointers = false;
};

struct KernelLayout {
  uint32_t kernargSize = 0;
  uint32_t explicitArgumentCount = 0;
  std::vector<KernargField> fields;
};

/** `object` is the exact handle placed in AQL dispatch packets. */
struct KernelSymbol {
  uint64_t object = 0;
  std::string name;
};

/** Reason a packet cannot execute. The values are published to completion signals by `RocrSignals` and read by
 * `Signals::fail`, so they stay stable.
 */
enum class PacketFailure : int32_t {
  KernelNotInstalled = -2,
  ExecutionFailed = -3,
  UnreadableDependency = -4,
  InvalidMemoryReference = -5,
  MalformedPacket = -6,
  InvalidDimensions = -7,
  InvalidGrid = -8,
};

/** Completion-signal access; ROCr and the public HSA runtime store signals differently. */
class Signals {
public:
  virtual ~Signals() = default;
  virtual bool load(uint64_t signal, int64_t &value) = 0;
  virtual bool complete(uint64_t signal) = 0;
  /** Receives the failure code of a packet that cannot complete; `signal` is zero when the packet has none. */
  virtual bool fail(uint64_t signal, int32_t code) = 0;
};

/** ROCr 7.x ordinary signals: the handle points at amd_signal_t, whose signed value word sits at offset 8. */
class RocrSignals final : public Signals {
public:
  bool load(uint64_t signal, int64_t &value) override;
  bool complete(uint64_t signal) override;
  bool fail(uint64_t signal, int32_t code) override;
};

/** Executes AQL packets on the shared execution service, with no fallback to a physical GPU. Kernarg layouts come
 * from the service's kernel manifest and host pointers in kernarg buffers are relocated against registered allocations.
 */
class AqlRuntime {
public:
  /** Null when GEMSTONE_RPC_CLIENT does not name an execution service client. */
  static std::unique_ptr<AqlRuntime> fromEnvironment(std::unique_ptr<Signals> signals);
  ~AqlRuntime();
  AqlRuntime(const AqlRuntime &) = delete;
  AqlRuntime &operator=(const AqlRuntime &) = delete;

  std::string lastError() const;

  /** Loads one code object and binds every listed kernel object to the shared program. */
  bool installCodeObject(std::span<const uint8_t> image, std::span<const KernelSymbol> kernels);
  /** Takes ownership of `program`: it is unloaded on failure and once its last installed kernel is removed. */
  bool installLoadedProgram(execution::ProgramId program, std::span<const KernelSymbol> kernels);
  std::optional<KernelLayout> layout(uint64_t kernelObject) const;
  bool removeKernel(uint64_t kernelObject);
  /** Binds a named global of the program owning `kernelObject`. */
  bool bindGlobal(uint64_t kernelObject, const std::string &name, uint64_t allocation, uint64_t offset);

  bool registerAllocation(uint64_t base, uint64_t size, uint64_t allocation);
  bool unregisterAllocation(uint64_t base);

  /** Drains the exact service-written ranges of completed dispatches in completion order. */
  std::vector<execution::DirtyRange> takeDirtyRanges();

  /** Executes one `AqlPacketSize`-byte packet, publishing its completion signal through `Signals`. */
  PacketResult consume(const void *packet);

private:
  struct Kernel {
    execution::ProgramId program{};
    std::string name;
    KernelLayout layout;
  };
  struct Allocation {
    uint64_t size = 0;
    uint64_t id = 0;
  };
  struct Resolved {
    uint64_t id = 0;
    uint64_t offset = 0;
  };

  explicit AqlRuntime(std::unique_ptr<Signals> signals);
  execution::Client *client();
  void fail(std::string message);
  std::optional<Resolved> resolve(uint64_t pointer);
  std::optional<Kernel> kernel(uint64_t kernelObject) const;
  bool relocations(const Kernel &kernel, uint64_t kernarg, std::optional<execution::ArgumentBuffer> &buffer,
                   std::vector<execution::PointerRelocation> &relocations);
  PacketResult failPacket(uint64_t completionSignal, PacketFailure failure, std::string message);
  PacketResult barrier(const AqlPacket &packet);
  PacketResult dispatch(const AqlPacket &packet);

  std::unique_ptr<Signals> signals;
  mutable std::mutex lock;
  // Monitor workers block in completion waits that service host memory, so every thread gets its own connection.
  std::map<std::thread::id, std::unique_ptr<execution::Client>> clients;
  std::unordered_map<uint64_t, Kernel> installedKernels;
  std::unordered_map<int64_t, uint32_t> programReferences;
  std::unordered_map<uint64_t, Allocation> allocations;
  std::vector<execution::DirtyRange> dirtyRanges;
  std::string failure;
};

} // namespace gemstone::hsa
