#include <cstdint>
#include <cstdlib>
#include <filesystem>
#include <string>
#include <vector>

#include <nvJitLink.h>

#include "cuda/support.cuh"
#include <catch2/catch_test_macros.hpp>

#include <conformance/environment_keys.hpp>

namespace {

constexpr unsigned Blocks = 4;
constexpr unsigned Threads = 32;
constexpr unsigned Count = Blocks * Threads;
constexpr uint32_t Unknown = 999;
constexpr uint64_t GridHandle = 0x7ffffffffffffULL;

// the device runtime's handle and grid synchronisation calls, as Numba and CUDA.jl emit them
constexpr const char *Ptx = R"(.version 8.0
.target sm_70
.address_size 64
.extern .func (.param .b64 r) cudaCGGetIntrinsicHandle(.param .b32 s);
.extern .func (.param .b32 r) cudaCGSynchronize(.param .b64 h, .param .b32 f);
.visible .entry grid_sync(.param .u64 data_p, .param .u64 out_p)
{
  .reg .b32 %r<12>;
  .reg .b64 %rd<12>;
  .param .b64 rh;
  .param .b32 sc;
  .param .b32 rs;
  .param .b64 ah;
  .param .b32 af;
  ld.param.u64 %rd0, [data_p];
  ld.param.u64 %rd1, [out_p];
  mov.u32 %r0, %tid.x;
  mov.u32 %r1, %ctaid.x;
  mov.u32 %r2, %ntid.x;
  mad.lo.u32 %r3, %r1, %r2, %r0;
  mul.wide.u32 %rd2, %r3, 4;
  add.u64 %rd3, %rd0, %rd2;
  add.u32 %r4, %r3, 1;
  st.global.u32 [%rd3], %r4;
  mov.u32 %r5, 1;
  st.param.b32 [sc], %r5;
  call.uni (rh), cudaCGGetIntrinsicHandle, (sc);
  ld.param.b64 %rd4, [rh];
  mov.u32 %r6, 0;
  st.param.b64 [ah], %rd4;
  st.param.b32 [af], %r6;
  call.uni (rs), cudaCGSynchronize, (ah, af);
  ld.param.b32 %r7, [rs];
  xor.b32 %r8, %r3, %r2;
  mul.wide.u32 %rd5, %r8, 4;
  add.u64 %rd6, %rd0, %rd5;
  ld.global.cg.u32 %r9, [%rd6];
  mul.wide.u32 %rd7, %r3, 16;
  add.u64 %rd8, %rd1, %rd7;
  st.global.u64 [%rd8], %rd4;
  st.global.u32 [%rd8+8], %r9;
  st.global.u32 [%rd8+12], %r7;
  ret;
}
)";

struct Result {
  uint64_t handle;
  uint32_t neighbour;
  uint32_t status;
};

// the device runtime ships in the artifact's support closure, so a run on another host resolves it there
std::string deviceRuntimeLibrary() {
  if (const char *named = std::getenv(conformance::environment::CudadevrtLibrary); named && *named) return named;
  if (const char *support = std::getenv(conformance::environment::SupportDir); support && *support) {
    const std::filesystem::path shipped = std::filesystem::path(support) / "lib" / "libcudadevrt.a";
    if (std::filesystem::is_regular_file(shipped)) return shipped.string();
  }
  return UNIFIED_CUDADEVRT_LIBRARY;
}

void checkLink(nvJitLinkResult result, std::string_view operation) {
  if (result != NVJITLINK_SUCCESS) throw std::runtime_error(std::string(operation) + ": nvJitLink result " + std::to_string(result));
}

// links the kernel with the device runtime as nvcc -rdc and the Python and Julia toolchains do
struct Module {
  CUmodule module = nullptr;
  CUfunction function = nullptr;
  Module() {
    check(cuInit(0), "initialise");
    CUdevice device = 0;
    check(cuDeviceGet(&device, 0), "device");
    CUcontext context = nullptr;
    check(cuDevicePrimaryCtxRetain(&context, device), "primary context");
    check(cuCtxSetCurrent(context), "current context");
    int major = 0;
    int minor = 0;
    check(cuDeviceGetAttribute(&major, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MAJOR, device), "capability");
    check(cuDeviceGetAttribute(&minor, CU_DEVICE_ATTRIBUTE_COMPUTE_CAPABILITY_MINOR, device), "capability");
    const std::string architecture = "-arch=sm_" + std::to_string((major * 10) + minor);
    const char *options[]{architecture.c_str()};
    nvJitLinkHandle link = nullptr;
    checkLink(nvJitLinkCreate(&link, 1, options), "link create");
    checkLink(nvJitLinkAddData(link, NVJITLINK_INPUT_PTX, Ptx, std::char_traits<char>::length(Ptx) + 1, "grid_sync.ptx"), "link ptx");
    checkLink(nvJitLinkAddFile(link, NVJITLINK_INPUT_LIBRARY, deviceRuntimeLibrary().c_str()), "link cudadevrt");
    checkLink(nvJitLinkComplete(link), "link complete");
    size_t size = 0;
    checkLink(nvJitLinkGetLinkedCubinSize(link, &size), "cubin size");
    std::vector<char> cubin(size);
    checkLink(nvJitLinkGetLinkedCubin(link, cubin.data()), "cubin");
    checkLink(nvJitLinkDestroy(&link), "link destroy");
    check(cuModuleLoadData(&module, cubin.data()), "module load");
    check(cuModuleGetFunction(&function, module, "grid_sync"), "function");
  }
  Module(const Module &) = delete;
  Module &operator=(const Module &) = delete;
  ~Module() { (void)cuModuleUnload(module); }

  std::vector<Result> run(bool cooperative) const {
    DeviceBuffer<uint32_t> data(std::vector<uint32_t>(Count, 0));
    DeviceBuffer<Result> out(std::vector<Result>(Count, Result{0, 0, 0}));
    void *arguments[]{&data.data, &out.data};
    if (cooperative) check(cuLaunchCooperativeKernel(function, Blocks, 1, 1, Threads, 1, 1, 0, nullptr, arguments), "cooperative launch");
    else check(cuLaunchKernel(function, Blocks, 1, 1, Threads, 1, 1, 0, nullptr, arguments, nullptr), "launch");
    check(cuCtxSynchronize(), "synchronise");
    return out.download();
  }
};

} // namespace

TEST_CASE("runtime/cuda/cooperative/grid-sync-publishes-every-block", "[unified][runtime][cooperative]") {
  if (!std::filesystem::exists(deviceRuntimeLibrary())) SKIP("libcudadevrt.a is not available: " << deviceRuntimeLibrary());
  const Module module;
  const auto results = module.run(true);
  for (unsigned gid = 0; gid < Count; ++gid) {
    INFO("thread " << gid);
    REQUIRE(results[gid].handle == GridHandle);
    REQUIRE(results[gid].status == 0);
    REQUIRE(results[gid].neighbour == (gid ^ Threads) + 1);
  }
}

TEST_CASE("runtime/cuda/cooperative/grid-sync-needs-a-cooperative-launch", "[unified][runtime][cooperative]") {
  if (!std::filesystem::exists(deviceRuntimeLibrary())) SKIP("libcudadevrt.a is not available: " << deviceRuntimeLibrary());
  const Module module;
  for (const auto &result : module.run(false)) {
    REQUIRE(result.handle == 0);
    REQUIRE(result.status == Unknown);
  }
}
