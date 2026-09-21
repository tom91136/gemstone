/* Generated from ExecutionRPC.Operation. Regenerate with: sbt codegen */
#define _POSIX_C_SOURCE 200112L
#include "gemstone_execution_rpc.hpp"
#include "process_identity.hpp"

#include <arpa/inet.h>
#include <cerrno>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <mutex>
#include <netdb.h>
#include <netinet/tcp.h>
#include <sys/socket.h>
#include <sys/time.h>
#include <type_traits>
#include <unistd.h>

namespace gemstone::execution {
namespace {

constexpr int SocketTimeoutSeconds = 300;

class Writer {
 public:
  void u8(uint8_t value) { if (bytes_.size() == MaximumPayload) { valid_ = false; return; } bytes_.push_back(value); }
  void u32(uint32_t value) { u8(value >> 24); u8(value >> 16); u8(value >> 8); u8(value); }
  void u64(uint64_t value) { u32(value >> 32); u32(value); }
  void bytes(const uint8_t *value, size_t size) { if (size > MaximumPayload - bytes_.size()) { valid_ = false; return; } bytes_.insert(bytes_.end(), value, value + size); }
  void invalidate() { valid_ = false; }
  bool valid() const { return valid_; }
  const std::vector<uint8_t> &result() const { return bytes_; }
 private:
  bool valid_ = true;
  std::vector<uint8_t> bytes_;
};

class Reader {
 public:
  explicit Reader(const std::vector<uint8_t> &bytes) : bytes_(bytes) {}
  bool u8(uint8_t &value) { if (remaining() < 1) return false; value = bytes_[offset_++]; return true; }
  bool u32(uint32_t &value) { if (remaining() < 4) return false; value = (uint32_t(bytes_[offset_]) << 24) | (uint32_t(bytes_[offset_ + 1]) << 16) | (uint32_t(bytes_[offset_ + 2]) << 8) | bytes_[offset_ + 3]; offset_ += 4; return true; }
  bool u64(uint64_t &value) { uint32_t high = 0, low = 0; if (!u32(high) || !u32(low)) return false; value = (uint64_t(high) << 32) | low; return true; }
  bool bytes(uint8_t *value, size_t size) { if (size > remaining()) return false; std::memcpy(value, bytes_.data() + offset_, size); offset_ += size; return true; }
  size_t remaining() const { return bytes_.size() - offset_; }
 private:
  const std::vector<uint8_t> &bytes_;
  size_t offset_ = 0;
};

template <class T> struct Codec;
template <> struct Codec<Unit> { static void write(Writer &, Unit) {} static bool read(Reader &, Unit &) { return true; } };
template <> struct Codec<bool> { static void write(Writer &w, bool v) { w.u8(v ? 1 : 0); } static bool read(Reader &r, bool &v) { uint8_t n = 0; if (!r.u8(n) || n > 1) return false; v = n != 0; return true; } };
template <> struct Codec<uint8_t> { static void write(Writer &w, uint8_t v) { w.u8(v); } static bool read(Reader &r, uint8_t &v) { return r.u8(v); } };
template <> struct Codec<int32_t> { static void write(Writer &w, int32_t v) { w.u32(static_cast<uint32_t>(v)); } static bool read(Reader &r, int32_t &v) { uint32_t n = 0; if (!r.u32(n)) return false; v = static_cast<int32_t>(n); return true; } };
template <> struct Codec<int64_t> { static void write(Writer &w, int64_t v) { w.u64(static_cast<uint64_t>(v)); } static bool read(Reader &r, int64_t &v) { uint64_t n = 0; if (!r.u64(n)) return false; v = static_cast<int64_t>(n); return true; } };
template <> struct Codec<std::string> { static void write(Writer &w, const std::string &v) { if (v.size() > MaximumTextBytes) { w.invalidate(); return; } w.u32(static_cast<uint32_t>(v.size())); w.bytes(reinterpret_cast<const uint8_t *>(v.data()), v.size()); } static bool read(Reader &r, std::string &v) { uint32_t n = 0; if (!r.u32(n) || n > MaximumTextBytes || n > r.remaining()) return false; v.resize(n); return r.bytes(reinterpret_cast<uint8_t *>(v.data()), n); } };
template <class T> struct Codec<std::vector<T>> { static void write(Writer &w, const std::vector<T> &v) { const size_t limit = std::is_same_v<T, uint8_t> ? MaximumPayload : MaximumElements; if (v.size() > limit) { w.invalidate(); return; } w.u32(static_cast<uint32_t>(v.size())); if constexpr (std::is_same_v<T, uint8_t>) w.bytes(v.data(), v.size()); else for (const auto &item : v) Codec<T>::write(w, item); } static bool read(Reader &r, std::vector<T> &v) { uint32_t n = 0; const uint32_t limit = std::is_same_v<T, uint8_t> ? MaximumPayload : MaximumElements; if (!r.u32(n) || n > limit) return false; v.resize(n); if constexpr (std::is_same_v<T, uint8_t>) return r.bytes(v.data(), v.size()); else { for (auto &item : v) if (!Codec<T>::read(r, item)) return false; return true; } } };
template <class T> struct Codec<std::optional<T>> { static void write(Writer &w, const std::optional<T> &v) { w.u8(v ? 1 : 0); if (v) Codec<T>::write(w, *v); } static bool read(Reader &r, std::optional<T> &v) { uint8_t tag = 0; if (!r.u8(tag) || tag > 1) return false; if (tag == 0) { v.reset(); return true; } T item{}; if (!Codec<T>::read(r, item)) return false; v = std::move(item); return true; } };

template <> struct Codec<AllocationKind> {
  static void write(Writer &writer, AllocationKind value) { writer.u64(static_cast<uint64_t>(value)); }
  static bool read(Reader &reader, AllocationKind &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x550c809331279417ULL: value = AllocationKind::Device; return true; case 0xe3e753f7a49fdebaULL: value = AllocationKind::Managed; return true; case 0x3cca1c4bb4e27b28ULL: value = AllocationKind::Shared; return true; case 0x1502c13a23427453ULL: value = AllocationKind::Host; return true; default: return false; } }
};

template <> struct Codec<LongPositive> {
  static void write(Writer &writer, const LongPositive &value) { if (!((value.value > 0))) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, LongPositive &value) { return Codec<int64_t>::read(reader, value.value) && ((value.value > 0)); }
};

template <> struct Codec<AllocationId> {
  static void write(Writer &writer, const AllocationId &value) { if (!((value.value > 0))) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, AllocationId &value) { return Codec<int64_t>::read(reader, value.value) && ((value.value > 0)); }
};

template <> struct Codec<GuestAddress> {
  static void write(Writer &writer, const GuestAddress &value) { if (!(true)) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, GuestAddress &value) { return Codec<int64_t>::read(reader, value.value) && (true); }
};

template <> struct Codec<LongPositive0> {
  static void write(Writer &writer, const LongPositive0 &value) { if (!((value.value >= 0))) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, LongPositive0 &value) { return Codec<int64_t>::read(reader, value.value) && ((value.value >= 0)); }
};

template <> struct Codec<IntPositive0> {
  static void write(Writer &writer, const IntPositive0 &value) { if (!((value.value >= 0))) { writer.invalidate(); return; } Codec<int32_t>::write(writer, value.value); }
  static bool read(Reader &reader, IntPositive0 &value) { return Codec<int32_t>::read(reader, value.value) && ((value.value >= 0)); }
};

template <> struct Codec<ProgramId> {
  static void write(Writer &writer, const ProgramId &value) { if (!((value.value > 0))) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, ProgramId &value) { return Codec<int64_t>::read(reader, value.value) && ((value.value > 0)); }
};

template <> struct Codec<KernelName> {
  static void write(Writer &writer, const KernelName &value) { if (!((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos))) { writer.invalidate(); return; } Codec<std::string>::write(writer, value.value); }
  static bool read(Reader &reader, KernelName &value) { return Codec<std::string>::read(reader, value.value) && ((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos)); }
};

template <> struct Codec<LinkageName> {
  static void write(Writer &writer, const LinkageName &value) { if (!((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos))) { writer.invalidate(); return; } Codec<std::string>::write(writer, value.value); }
  static bool read(Reader &reader, LinkageName &value) { return Codec<std::string>::read(reader, value.value) && ((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos)); }
};

template <> struct Codec<ProgramKernel> {
  static void write(Writer &writer, const ProgramKernel &value) { Codec<KernelName>::write(writer, value.name); Codec<LinkageName>::write(writer, value.linkageName); }
  static bool read(Reader &reader, ProgramKernel &value) { return Codec<KernelName>::read(reader, value.name) && Codec<LinkageName>::read(reader, value.linkageName); }
};

template <> struct Codec<GlobalName> {
  static void write(Writer &writer, const GlobalName &value) { if (!((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos))) { writer.invalidate(); return; } Codec<std::string>::write(writer, value.value); }
  static bool read(Reader &reader, GlobalName &value) { return Codec<std::string>::read(reader, value.value) && ((value.value.size() >= 1) && (value.value.find(std::string("\x00", 1U)) == std::string::npos)); }
};

template <> struct Codec<ProgramGlobalMetadata> {
  static void write(Writer &writer, const ProgramGlobalMetadata &value) { Codec<GlobalName>::write(writer, value.name); Codec<std::optional<GuestAddress>>::write(writer, value.virtualAddress); Codec<LongPositive0>::write(writer, value.size); Codec<std::vector<IntPositive0>>::write(writer, value.pointerOffsets); }
  static bool read(Reader &reader, ProgramGlobalMetadata &value) { return Codec<GlobalName>::read(reader, value.name) && Codec<std::optional<GuestAddress>>::read(reader, value.virtualAddress) && Codec<LongPositive0>::read(reader, value.size) && Codec<std::vector<IntPositive0>>::read(reader, value.pointerOffsets); }
};

template <> struct Codec<ProgramCatalog> {
  static void write(Writer &writer, const ProgramCatalog &value) { Codec<std::vector<ProgramKernel>>::write(writer, value.kernels); Codec<std::vector<ProgramGlobalMetadata>>::write(writer, value.globals); }
  static bool read(Reader &reader, ProgramCatalog &value) { return Codec<std::vector<ProgramKernel>>::read(reader, value.kernels) && Codec<std::vector<ProgramGlobalMetadata>>::read(reader, value.globals); }
};

template <> struct Codec<IntPositive> {
  static void write(Writer &writer, const IntPositive &value) { if (!((value.value > 0))) { writer.invalidate(); return; } Codec<int32_t>::write(writer, value.value); }
  static bool read(Reader &reader, IntPositive &value) { return Codec<int32_t>::read(reader, value.value) && ((value.value > 0)); }
};

template <> struct Codec<KernelParameterKind> {
  static void write(Writer &writer, KernelParameterKind value) { writer.u64(static_cast<uint64_t>(value)); }
  static bool read(Reader &reader, KernelParameterKind &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x8e8237265871dac9ULL: value = KernelParameterKind::Scalar; return true; case 0x003f04bd0f952e6bULL: value = KernelParameterKind::Allocation; return true; case 0xf746e4b741bcb38cULL: value = KernelParameterKind::Local; return true; default: return false; } }
};

template <> struct Codec<KernelParameterMetadata> {
  static void write(Writer &writer, const KernelParameterMetadata &value) { Codec<IntPositive>::write(writer, value.width); Codec<IntPositive0>::write(writer, value.offset); Codec<KernelParameterKind>::write(writer, value.kind); Codec<bool>::write(writer, value.containsPointers); }
  static bool read(Reader &reader, KernelParameterMetadata &value) { return Codec<IntPositive>::read(reader, value.width) && Codec<IntPositive0>::read(reader, value.offset) && Codec<KernelParameterKind>::read(reader, value.kind) && Codec<bool>::read(reader, value.containsPointers); }
};

template <> struct Codec<KernelMetadata> {
  static void write(Writer &writer, const KernelMetadata &value) { Codec<IntPositive0>::write(writer, value.argumentBlockSize); Codec<IntPositive0>::write(writer, value.explicitParameterCount); Codec<std::vector<KernelParameterMetadata>>::write(writer, value.parameters); }
  static bool read(Reader &reader, KernelMetadata &value) { return Codec<IntPositive0>::read(reader, value.argumentBlockSize) && Codec<IntPositive0>::read(reader, value.explicitParameterCount) && Codec<std::vector<KernelParameterMetadata>>::read(reader, value.parameters); }
};

template <> struct Codec<ProgramGlobal> {
  static void write(Writer &writer, const ProgramGlobal &value) { Codec<LongPositive0>::write(writer, value.size); Codec<std::vector<uint8_t>>::write(writer, value.initial); }
  static bool read(Reader &reader, ProgramGlobal &value) { return Codec<LongPositive0>::read(reader, value.size) && Codec<std::vector<uint8_t>>::read(reader, value.initial); }
};

template <> struct Codec<Dimensions> {
  static void write(Writer &writer, const Dimensions &value) { Codec<IntPositive>::write(writer, value.x); Codec<IntPositive>::write(writer, value.y); Codec<IntPositive>::write(writer, value.z); }
  static bool read(Reader &reader, Dimensions &value) { return Codec<IntPositive>::read(reader, value.x) && Codec<IntPositive>::read(reader, value.y) && Codec<IntPositive>::read(reader, value.z); }
};

template <> struct Codec<GlobalDimensions> {
  static void write(Writer &writer, const GlobalDimensions &value) { Codec<LongPositive>::write(writer, value.x); Codec<LongPositive>::write(writer, value.y); Codec<LongPositive>::write(writer, value.z); }
  static bool read(Reader &reader, GlobalDimensions &value) { return Codec<LongPositive>::read(reader, value.x) && Codec<LongPositive>::read(reader, value.y) && Codec<LongPositive>::read(reader, value.z); }
};

template <> struct Codec<IntInterval1To3> {
  static void write(Writer &writer, const IntInterval1To3 &value) { if (!((value.value >= 1 && value.value <= 3))) { writer.invalidate(); return; } Codec<int32_t>::write(writer, value.value); }
  static bool read(Reader &reader, IntInterval1To3 &value) { return Codec<int32_t>::read(reader, value.value) && ((value.value >= 1 && value.value <= 3)); }
};

template <> struct Codec<Coordinates> {
  static void write(Writer &writer, const Coordinates &value) { Codec<IntPositive0>::write(writer, value.x); Codec<IntPositive0>::write(writer, value.y); Codec<IntPositive0>::write(writer, value.z); }
  static bool read(Reader &reader, Coordinates &value) { return Codec<IntPositive0>::read(reader, value.x) && Codec<IntPositive0>::read(reader, value.y) && Codec<IntPositive0>::read(reader, value.z); }
};

template <> struct Codec<KernelArgument> {
  static void write(Writer &writer, const KernelArgument &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; if constexpr (std::is_same_v<T, KernelArgumentScalar>) { writer.u64(0x4dc1565dba3b9867ULL); Codec<std::vector<uint8_t>>::write(writer, item.bytes); } else if constexpr (std::is_same_v<T, KernelArgumentAllocation>) { writer.u64(0xc9602c96b7ee44fdULL); Codec<AllocationId>::write(writer, item.id); Codec<LongPositive0>::write(writer, item.offset); } else if constexpr (std::is_same_v<T, KernelArgumentLocal>) { writer.u64(0x9fa3570b0ce037faULL); Codec<LongPositive>::write(writer, item.size); } }, value); }
  static bool read(Reader &reader, KernelArgument &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x4dc1565dba3b9867ULL: { KernelArgumentScalar item{}; if (!(Codec<std::vector<uint8_t>>::read(reader, item.bytes))) return false; value = std::move(item); return true; } case 0xc9602c96b7ee44fdULL: { KernelArgumentAllocation item{}; if (!(Codec<AllocationId>::read(reader, item.id) && Codec<LongPositive0>::read(reader, item.offset))) return false; value = std::move(item); return true; } case 0x9fa3570b0ce037faULL: { KernelArgumentLocal item{}; if (!(Codec<LongPositive>::read(reader, item.size))) return false; value = std::move(item); return true; } default: return false; } }
};

template <> struct Codec<RelocationSource> {
  static void write(Writer &writer, const RelocationSource &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; if constexpr (std::is_same_v<T, RelocationSourceAllocation>) { writer.u64(0x7f6ec03808ee2e12ULL); Codec<AllocationId>::write(writer, item.id); } else if constexpr (std::is_same_v<T, RelocationSourceArgument>) { writer.u64(0xd0123da2fbcfda83ULL); Codec<IntPositive0>::write(writer, item.index); } }, value); }
  static bool read(Reader &reader, RelocationSource &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x7f6ec03808ee2e12ULL: { RelocationSourceAllocation item{}; if (!(Codec<AllocationId>::read(reader, item.id))) return false; value = std::move(item); return true; } case 0xd0123da2fbcfda83ULL: { RelocationSourceArgument item{}; if (!(Codec<IntPositive0>::read(reader, item.index))) return false; value = std::move(item); return true; } default: return false; } }
};

template <> struct Codec<PointerRelocation> {
  static void write(Writer &writer, const PointerRelocation &value) { Codec<RelocationSource>::write(writer, value.source); Codec<LongPositive0>::write(writer, value.sourceOffset); Codec<AllocationId>::write(writer, value.target); Codec<LongPositive0>::write(writer, value.targetOffset); }
  static bool read(Reader &reader, PointerRelocation &value) { return Codec<RelocationSource>::read(reader, value.source) && Codec<LongPositive0>::read(reader, value.sourceOffset) && Codec<AllocationId>::read(reader, value.target) && Codec<LongPositive0>::read(reader, value.targetOffset); }
};

template <> struct Codec<CompletionId> {
  static void write(Writer &writer, const CompletionId &value) { if (!((value.value > 0))) { writer.invalidate(); return; } Codec<int64_t>::write(writer, value.value); }
  static bool read(Reader &reader, CompletionId &value) { return Codec<int64_t>::read(reader, value.value) && ((value.value > 0)); }
};

template <> struct Codec<ExecutionFailure> {
  static void write(Writer &writer, const ExecutionFailure &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; if constexpr (std::is_same_v<T, ExecutionFailureInvalidRequest>) { writer.u64(0xa706a7cf8cba6a29ULL); Codec<std::string>::write(writer, item.detail); } else if constexpr (std::is_same_v<T, ExecutionFailureNotFound>) { writer.u64(0x39b74efbac06d31eULL); Codec<std::string>::write(writer, item.detail); } else if constexpr (std::is_same_v<T, ExecutionFailureUnsupported>) { writer.u64(0x67943c05acc71738ULL); Codec<std::string>::write(writer, item.detail); } else if constexpr (std::is_same_v<T, ExecutionFailureFailed>) { writer.u64(0xa2f22dde2ffdb70cULL); Codec<std::string>::write(writer, item.detail); } }, value); }
  static bool read(Reader &reader, ExecutionFailure &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0xa706a7cf8cba6a29ULL: { ExecutionFailureInvalidRequest item{}; if (!(Codec<std::string>::read(reader, item.detail))) return false; value = std::move(item); return true; } case 0x39b74efbac06d31eULL: { ExecutionFailureNotFound item{}; if (!(Codec<std::string>::read(reader, item.detail))) return false; value = std::move(item); return true; } case 0x67943c05acc71738ULL: { ExecutionFailureUnsupported item{}; if (!(Codec<std::string>::read(reader, item.detail))) return false; value = std::move(item); return true; } case 0xa2f22dde2ffdb70cULL: { ExecutionFailureFailed item{}; if (!(Codec<std::string>::read(reader, item.detail))) return false; value = std::move(item); return true; } default: return false; } }
};

template <> struct Codec<CompletionStatus> {
  static void write(Writer &writer, const CompletionStatus &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; if constexpr (std::is_same_v<T, CompletionStatusPending>) { writer.u64(0x4be231fa7a2a5d64ULL);  } else if constexpr (std::is_same_v<T, CompletionStatusSucceeded>) { writer.u64(0xd5313ba452a59f6aULL);  } else if constexpr (std::is_same_v<T, CompletionStatusFailed>) { writer.u64(0x4167002bdf4255fcULL); Codec<ExecutionFailure>::write(writer, item.error); } }, value); }
  static bool read(Reader &reader, CompletionStatus &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x4be231fa7a2a5d64ULL: { CompletionStatusPending item{}; if (!(true)) return false; value = std::move(item); return true; } case 0xd5313ba452a59f6aULL: { CompletionStatusSucceeded item{}; if (!(true)) return false; value = std::move(item); return true; } case 0x4167002bdf4255fcULL: { CompletionStatusFailed item{}; if (!(Codec<ExecutionFailure>::read(reader, item.error))) return false; value = std::move(item); return true; } default: return false; } }
};

template <> struct Codec<DirtyRange> {
  static void write(Writer &writer, const DirtyRange &value) { Codec<AllocationId>::write(writer, value.allocation); Codec<LongPositive0>::write(writer, value.offset); Codec<IntPositive>::write(writer, value.size); }
  static bool read(Reader &reader, DirtyRange &value) { return Codec<AllocationId>::read(reader, value.allocation) && Codec<LongPositive0>::read(reader, value.offset) && Codec<IntPositive>::read(reader, value.size); }
};

template <> struct Codec<PrintArgument> {
  static void write(Writer &writer, const PrintArgument &value) { std::visit([&](const auto &item) { using T = std::decay_t<decltype(item)>; if constexpr (std::is_same_v<T, PrintArgumentInteger>) { writer.u64(0x2fffc162da8adfbbULL); Codec<std::vector<int64_t>>::write(writer, item.bits); Codec<int32_t>::write(writer, item.width); Codec<bool>::write(writer, item.isSigned); } else if constexpr (std::is_same_v<T, PrintArgumentFloating>) { writer.u64(0x7c64cbc3d8134ee9ULL); Codec<std::vector<int64_t>>::write(writer, item.bits); Codec<int32_t>::write(writer, item.width); } else if constexpr (std::is_same_v<T, PrintArgumentPointer>) { writer.u64(0xb57d2dc948534440ULL); Codec<int64_t>::write(writer, item.address); } else if constexpr (std::is_same_v<T, PrintArgumentText>) { writer.u64(0x722582b3534d844cULL); Codec<std::string>::write(writer, item.value); } }, value); }
  static bool read(Reader &reader, PrintArgument &value) { uint64_t id = 0; if (!reader.u64(id)) return false; switch (id) { case 0x2fffc162da8adfbbULL: { PrintArgumentInteger item{}; if (!(Codec<std::vector<int64_t>>::read(reader, item.bits) && Codec<int32_t>::read(reader, item.width) && Codec<bool>::read(reader, item.isSigned))) return false; value = std::move(item); return true; } case 0x7c64cbc3d8134ee9ULL: { PrintArgumentFloating item{}; if (!(Codec<std::vector<int64_t>>::read(reader, item.bits) && Codec<int32_t>::read(reader, item.width))) return false; value = std::move(item); return true; } case 0xb57d2dc948534440ULL: { PrintArgumentPointer item{}; if (!(Codec<int64_t>::read(reader, item.address))) return false; value = std::move(item); return true; } case 0x722582b3534d844cULL: { PrintArgumentText item{}; if (!(Codec<std::string>::read(reader, item.value))) return false; value = std::move(item); return true; } default: return false; } }
};

template <> struct Codec<PrintEvent> {
  static void write(Writer &writer, const PrintEvent &value) { Codec<std::string>::write(writer, value.format); Codec<std::vector<PrintArgument>>::write(writer, value.arguments); }
  static bool read(Reader &reader, PrintEvent &value) { return Codec<std::string>::read(reader, value.format) && Codec<std::vector<PrintArgument>>::read(reader, value.arguments); }
};

std::string failureDetail(const ExecutionFailure &failure) {
  return std::visit([](const auto &value) { return value.detail; }, failure);
}

bool parseClient(const char *text, uint64_t &value) {
  if (text == nullptr || text[0] == '\0' || text[0] == '-') return false;
  errno = 0; char *end = nullptr; const auto parsed = std::strtoull(text, &end, 10);
  if (errno == ERANGE || end == text || *end != '\0' || parsed == 0) return false;
  value = static_cast<uint64_t>(parsed); return true;
}

bool sendAll(int fd, const uint8_t *data, size_t size) {
  while (size != 0) { const auto sent = ::send(fd, data, size, MSG_NOSIGNAL); if (sent < 0 && errno == EINTR) continue; if (sent <= 0) return false; data += sent; size -= static_cast<size_t>(sent); } return true;
}
bool receiveAll(int fd, uint8_t *data, size_t size) {
  while (size != 0) { const auto received = ::recv(fd, data, size, 0); if (received < 0 && errno == EINTR) continue; if (received <= 0) return false; data += received; size -= static_cast<size_t>(received); } return true;
}

} // namespace

class Client::Impl {
 public:
  Impl(uint64_t client, uint64_t high, uint64_t low) : client_(client), sessionHigh_(high), sessionLow_(low) {}
  ~Impl() { if (fd_ >= 0) ::close(fd_); }
  std::string error() const { std::lock_guard<std::mutex> guard(mutex_); return error_; }

  template <class T, class Encode> Result<T> call(uint64_t operation, Encode encode) {
    std::lock_guard<std::mutex> guard(mutex_); error_.clear(); Writer writer; encode(writer);
    if (!writer.valid()) return failed<T>("execution request exceeds the encoding limits");
    std::vector<uint8_t> response; uint32_t status = 0;
    if (!exchange(operation, writer.result(), status, response)) return Result<T>::failure(Error{error_, std::nullopt});
    Reader reader(response);
    if (status == 1) { ExecutionFailure failure{}; if (!Codec<ExecutionFailure>::read(reader, failure) || reader.remaining() != 0) { poison(); return failed<T>("execution failure response is invalid"); } error_ = failureDetail(failure); return Result<T>::failure(Error{error_, std::move(failure)}); }
    T value{}; if (!Codec<T>::read(reader, value) || reader.remaining() != 0) { poison(); return failed<T>("execution response payload is invalid"); }
    return Result<T>::success(std::move(value));
  }

 private:
  template <class T> Result<T> failed(std::string detail) { error_ = std::move(detail); return Result<T>::failure(Error{error_, std::nullopt}); }
  void poison() { if (fd_ >= 0) ::close(fd_); fd_ = -1; if (established_) poisoned_ = true; }
  bool connectServer() {
    if (fd_ >= 0) return true;
    if (poisoned_) { error_ = "execution session was lost; create a fresh client and rebuild its state"; return false; }
    const char *port = std::getenv("GEMSTONE_RPC_PORT"); if (port == nullptr || port[0] == '\0') { error_ = "GEMSTONE_RPC_PORT is not set"; return false; }
    addrinfo hints{}; hints.ai_family = AF_INET; hints.ai_socktype = SOCK_STREAM; addrinfo *addresses = nullptr;
    if (::getaddrinfo("127.0.0.1", port, &hints, &addresses) != 0) { error_ = "cannot resolve the Gemstone RPC endpoint"; return false; }
    int connected = -1; for (auto *address = addresses; address != nullptr; address = address->ai_next) { connected = ::socket(address->ai_family, address->ai_socktype, address->ai_protocol); if (connected < 0) continue; if (::connect(connected, address->ai_addr, address->ai_addrlen) == 0) break; ::close(connected); connected = -1; }
    ::freeaddrinfo(addresses); if (connected < 0) { error_ = "cannot connect to the Gemstone RPC endpoint"; return false; }
    const timeval timeout{SocketTimeoutSeconds, 0}; const int noDelay = 1;
    if (::setsockopt(connected, IPPROTO_TCP, TCP_NODELAY, &noDelay, sizeof(noDelay)) != 0 ||
        ::setsockopt(connected, SOL_SOCKET, SO_RCVTIMEO, &timeout, sizeof(timeout)) != 0 ||
        ::setsockopt(connected, SOL_SOCKET, SO_SNDTIMEO, &timeout, sizeof(timeout)) != 0) {
      ::close(connected); error_ = "cannot configure the Gemstone RPC connection"; return false;
    }
    fd_ = connected; established_ = true; return true;
  }
  bool exchange(uint64_t operation, const std::vector<uint8_t> &payload, uint32_t &status, std::vector<uint8_t> &response) {
    if (!connectServer()) return false;
    Writer header; header.u32(RequestMagic); header.u64(SchemaFingerprint); header.u64(client_); header.u64(sessionHigh_); header.u64(sessionLow_); header.u64(operation); header.u32(static_cast<uint32_t>(payload.size()));
    if (!sendAll(fd_, header.result().data(), header.result().size()) || (!payload.empty() && !sendAll(fd_, payload.data(), payload.size()))) { poison(); error_ = "execution request transport failed"; return false; }
    uint8_t headerBytes[ResponseHeaderBytes]; if (!receiveAll(fd_, headerBytes, sizeof(headerBytes))) { poison(); error_ = "execution response transport failed"; return false; }
    const std::vector<uint8_t> headerVector(headerBytes, headerBytes + sizeof(headerBytes)); Reader headerReader(headerVector); uint32_t magic = 0, size = 0; uint64_t fingerprint = 0;
    if (!headerReader.u32(magic) || !headerReader.u64(fingerprint) || !headerReader.u32(status) || !headerReader.u32(size) || magic != ResponseMagic || fingerprint != SchemaFingerprint || status > 1 || size > MaximumPayload) { poison(); error_ = "execution response header is invalid"; return false; }
    response.resize(size); if (!response.empty() && !receiveAll(fd_, response.data(), response.size())) { poison(); error_ = "execution response body is truncated"; return false; } return true;
  }
  mutable std::mutex mutex_; int fd_ = -1; bool established_ = false; bool poisoned_ = false;
  uint64_t client_; uint64_t sessionHigh_; uint64_t sessionLow_; std::string error_;
};

std::unique_ptr<Client> Client::fromEnvironment() {
  uint64_t client = 0; if (!parseClient(std::getenv("GEMSTONE_RPC_CLIENT"), client)) return nullptr;
  const auto incarnation = processIncarnation(); if (incarnation == 0) return nullptr;
  return std::unique_ptr<Client>(new Client(std::make_unique<Impl>(client, static_cast<uint32_t>(::getpid()), incarnation)));
}
Client::Client(std::unique_ptr<Impl> implementation) : implementation_(std::move(implementation)) {}
Client::~Client() = default;
std::string Client::error() const { return implementation_->error(); }

Result<Unit> Client::call(const Ping &) {
  return implementation_->call<Unit>(operation::Ping, [](Writer &) {  });
}

Result<AllocationId> Client::call(const RegisterAllocation &request) {
  return implementation_->call<AllocationId>(operation::RegisterAllocation, [&](Writer &writer) { Codec<AllocationKind>::write(writer, request.kind); Codec<LongPositive>::write(writer, request.size); });
}

Result<Unit> Client::call(const RegisterAllocationAddress &request) {
  return implementation_->call<Unit>(operation::RegisterAllocationAddress, [&](Writer &writer) { Codec<AllocationId>::write(writer, request.allocation); Codec<GuestAddress>::write(writer, request.nativeBase); });
}

Result<Unit> Client::call(const ReleaseAllocation &request) {
  return implementation_->call<Unit>(operation::ReleaseAllocation, [&](Writer &writer) { Codec<AllocationId>::write(writer, request.allocation); });
}

Result<Unit> Client::call(const WriteAllocation &request) {
  return implementation_->call<Unit>(operation::WriteAllocation, [&](Writer &writer) { Codec<AllocationId>::write(writer, request.allocation); Codec<LongPositive0>::write(writer, request.offset); Codec<std::vector<uint8_t>>::write(writer, request.bytes); });
}

Result<std::vector<uint8_t>> Client::call(const ReadAllocation &request) {
  return implementation_->call<std::vector<uint8_t>>(operation::ReadAllocation, [&](Writer &writer) { Codec<AllocationId>::write(writer, request.allocation); Codec<LongPositive0>::write(writer, request.offset); Codec<IntPositive0>::write(writer, request.size); });
}

Result<ProgramId> Client::call(const LoadProgram &request) {
  return implementation_->call<ProgramId>(operation::LoadProgram, [&](Writer &writer) { Codec<std::string>::write(writer, request.encoding); Codec<std::vector<uint8_t>>::write(writer, request.image); });
}

Result<Unit> Client::call(const UnloadProgram &request) {
  return implementation_->call<Unit>(operation::UnloadProgram, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); });
}

Result<ProgramCatalog> Client::call(const DescribeProgram &request) {
  return implementation_->call<ProgramCatalog>(operation::DescribeProgram, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); });
}

Result<KernelMetadata> Client::call(const DescribeKernel &request) {
  return implementation_->call<KernelMetadata>(operation::DescribeKernel, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); Codec<KernelName>::write(writer, request.name); });
}

Result<ProgramGlobal> Client::call(const DescribeGlobal &request) {
  return implementation_->call<ProgramGlobal>(operation::DescribeGlobal, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); Codec<GlobalName>::write(writer, request.name); });
}

Result<Unit> Client::call(const BindProgramGlobal &request) {
  return implementation_->call<Unit>(operation::BindProgramGlobal, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); Codec<GlobalName>::write(writer, request.name); Codec<AllocationId>::write(writer, request.allocation); Codec<LongPositive0>::write(writer, request.offset); });
}

Result<Unit> Client::call(const WriteProgramGlobal &request) {
  return implementation_->call<Unit>(operation::WriteProgramGlobal, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); Codec<GlobalName>::write(writer, request.name); Codec<LongPositive0>::write(writer, request.offset); Codec<std::vector<uint8_t>>::write(writer, request.bytes); });
}

Result<CompletionId> Client::call(const Launch &request) {
  return implementation_->call<CompletionId>(operation::Launch, [&](Writer &writer) { Codec<ProgramId>::write(writer, request.program); Codec<KernelName>::write(writer, request.kernelName); Codec<Dimensions>::write(writer, request.groupSize); Codec<Dimensions>::write(writer, request.groupCount); Codec<GlobalDimensions>::write(writer, request.globalSize); Codec<IntInterval1To3>::write(writer, request.workDim); Codec<Coordinates>::write(writer, request.globalOffset); Codec<IntPositive0>::write(writer, request.privateSegmentBytes); Codec<IntPositive0>::write(writer, request.groupSegmentBytes); Codec<std::vector<KernelArgument>>::write(writer, request.arguments); Codec<std::vector<PointerRelocation>>::write(writer, request.pointerRelocations); });
}

Result<CompletionStatus> Client::call(const QueryCompletion &request) {
  return implementation_->call<CompletionStatus>(operation::QueryCompletion, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

Result<Unit> Client::call(const AwaitCompletion &request) {
  return implementation_->call<Unit>(operation::AwaitCompletion, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

Result<std::vector<DirtyRange>> Client::call(const CompletionDirtyRanges &request) {
  return implementation_->call<std::vector<DirtyRange>>(operation::CompletionDirtyRanges, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

Result<std::vector<PointerRelocation>> Client::call(const CompletionPointerRelocations &request) {
  return implementation_->call<std::vector<PointerRelocation>>(operation::CompletionPointerRelocations, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

Result<std::vector<PrintEvent>> Client::call(const CompletionPrints &request) {
  return implementation_->call<std::vector<PrintEvent>>(operation::CompletionPrints, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

Result<Unit> Client::call(const ReleaseCompletion &request) {
  return implementation_->call<Unit>(operation::ReleaseCompletion, [&](Writer &writer) { Codec<CompletionId>::write(writer, request.completion); });
}

} // namespace gemstone::execution
