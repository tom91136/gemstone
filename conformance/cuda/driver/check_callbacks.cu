#pragma region case: driver-callbacks
#pragma region targets: cuda,gemstone
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 {cuda_driver_defines} -x cu -o {output} {input} {cuda_driver_link}
#pragma region do: {output}
#pragma region requires: pass

#include <atomic>
#include <cstdint>
#include <cstdio>
#include <thread>

#include <cuda.h>

#if defined(GEMSTONE_CUDA_ABI) && defined(__GNUC__)
extern "C" CUresult CUDAAPI gemstone_cuCtxCreate_v2(CUcontext *, unsigned int, CUdevice) __asm__("cuCtxCreate_v2");
#endif

#define CUDA_REQUIRE(expression)                                                                                                           \
  do {                                                                                                                                     \
    const CUresult status = (expression);                                                                                                  \
    if (status != CUDA_SUCCESS) {                                                                                                          \
      std::fprintf(stderr, "%s failed with CUDA error %d at line %d\n", #expression, static_cast<int>(status), __LINE__);                  \
      return 1;                                                                                                                            \
    }                                                                                                                                      \
  } while (false)

namespace {
struct State {
  std::atomic<int> phase{0};
  std::atomic<int> calls{0};
  std::atomic<int> failures{0};
  std::atomic<std::uint64_t> checksum{0};
};

enum class Kind { host, stream };

struct CallbackData {
  State *state;
  int expected_phase;
  int next_phase;
  std::uint64_t token;
  Kind kind;
};

thread_local int callback_tls = 0;

void apply(CallbackData &data, Kind actual_kind) {
  auto &state = *data.state;
  if (data.kind != actual_kind)
    state.failures.fetch_add(1, std::memory_order_relaxed);

#if defined(GEMSTONE_CUDA_ABI)
  // Reverse calls are deliberately serviced on the thread blocked in the
  // original RPC. This catches accidental dispatch on a reader/worker thread.
  if (callback_tls != 700 + data.expected_phase)
    state.failures.fetch_add(1, std::memory_order_relaxed);
  callback_tls = 700 + data.next_phase;
#else
  // NVIDIA is free to use an internal worker. Still verify that callbacks do
  // not overlap on the same worker's TLS while sharing the stream.
  if (callback_tls != 0)
    state.failures.fetch_add(1, std::memory_order_relaxed);
  callback_tls = data.next_phase;
#endif

  int expected = data.expected_phase;
  if (!state.phase.compare_exchange_strong(expected, data.next_phase, std::memory_order_acq_rel))
    state.failures.fetch_add(1, std::memory_order_relaxed);
  state.checksum.fetch_add(data.token * static_cast<std::uint64_t>(data.next_phase), std::memory_order_relaxed);
  state.calls.fetch_add(1, std::memory_order_release);
  std::this_thread::yield();

#if defined(GEMSTONE_CUDA_ABI)
  if (callback_tls != 700 + data.next_phase)
    state.failures.fetch_add(1, std::memory_order_relaxed);
#else
  if (callback_tls != data.next_phase)
    state.failures.fetch_add(1, std::memory_order_relaxed);
  callback_tls = 0;
#endif
}

void CUDA_CB host_callback(void *opaque) {
  apply(*static_cast<CallbackData *>(opaque), Kind::host);
}

void CUDA_CB stream_callback(CUstream stream, CUresult status, void *opaque) {
  auto &data = *static_cast<CallbackData *>(opaque);
  if (stream != nullptr || status != CUDA_SUCCESS)
    data.state->failures.fetch_add(1, std::memory_order_relaxed);
  apply(data, Kind::stream);
}
} // namespace

int main() {
  CUDA_REQUIRE(cuInit(0));

  CUdevice device{};
  CUcontext context{};
  CUDA_REQUIRE(cuDeviceGet(&device, 0));
#if defined(GEMSTONE_CUDA_ABI) && defined(__GNUC__)
  CUDA_REQUIRE(gemstone_cuCtxCreate_v2(&context, 0, device));
#elif CUDA_VERSION >= 13000
  CUDA_REQUIRE(cuCtxCreate(&context, nullptr, 0, device));
#else
  CUDA_REQUIRE(cuCtxCreate(&context, 0, device));
#endif

  State state;
  CallbackData first{&state, 0, 1, 11, Kind::host};
  CallbackData second{&state, 1, 2, 17, Kind::stream};
  CallbackData third{&state, 2, 3, 23, Kind::host};
  CallbackData fourth{&state, 3, 4, 31, Kind::stream};

#if defined(GEMSTONE_CUDA_ABI)
  callback_tls = 700;
#endif
  CUDA_REQUIRE(cuLaunchHostFunc(nullptr, host_callback, &first));
  CUDA_REQUIRE(cuStreamAddCallback(nullptr, stream_callback, &second, 0));
  CUDA_REQUIRE(cuLaunchHostFunc(nullptr, host_callback, &third));
  CUDA_REQUIRE(cuStreamAddCallback(nullptr, stream_callback, &fourth, 0));
  CUDA_REQUIRE(cuCtxSynchronize());

  constexpr std::uint64_t expected_checksum = 11 * 1 + 17 * 2 + 23 * 3 + 31 * 4;
  if (state.phase.load(std::memory_order_acquire) != 4 || state.calls.load(std::memory_order_acquire) != 4 ||
      state.failures.load(std::memory_order_acquire) != 0 || state.checksum.load(std::memory_order_acquire) != expected_checksum) {
    std::fprintf(stderr, "callback state mismatch: phase=%d calls=%d failures=%d checksum=%llu\n",
                 state.phase.load(), state.calls.load(), state.failures.load(),
                 static_cast<unsigned long long>(state.checksum.load()));
    return 1;
  }
#if defined(GEMSTONE_CUDA_ABI)
  if (callback_tls != 704) {
    std::fprintf(stderr, "callback TLS did not return through the synchronizing thread: %d\n", callback_tls);
    return 1;
  }
#endif

  CUDA_REQUIRE(cuCtxDestroy_v2(context));
  std::puts("pass");
  return 0;
}
