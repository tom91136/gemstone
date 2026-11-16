# Ecosystem conformance

Status: in progress. Steps 1, 2, and 3 are done after implementing the missing HIP and Level Zero entry points the first Julia runs reached; step 4 is next. It depends on `concurrency-checking.md` steps 1 and 2 to classify failures, and on `machine-identity.md` for packages that query NVML or AMD SMI.

## Goal

Run GPU packages from Python and Julia unchanged under gemstone, with their vendor libraries as shipped, and keep them as conformance cases. Gemstone stays at its layer: above the kernel driver, below the vendor implementation. It provides `libcuda.so.1`, the HIP and HSA runtimes, the Level Zero driver, and the OpenCL ICD, and nothing in a package or vendor library is replaced or patched.

## Current state

- Conformance builds C, C++, CUDA, HIP, SYCL, and OpenMP programs in containers, runs them under gemstone, and groups them into workloads such as `short`, `miniapps`, and the bounded vendor conformance test suites.
- There are no Python or Julia cases.
- Gemstone already serves what these packages do at load time: PTX through `cuModuleLoadData` reaches the PTX VM, cubins reach the SASS VM, code objects reach the AMDGPU VM, SPIR-V reaches the SPIR-V VM, and `cuGetExportTable` is implemented for libraries that use it.

## Packages

- **Numba** (`numba-cuda`): generates PTX through NVVM and calls the driver API directly. Small, and the first case.
- **Julia:** CUDA.jl, AMDGPU.jl, oneAPI.jl, and KernelAbstractions.jl. BabelStream's `JuliaStream.jl` has CUDA, AMDGPU, oneAPI, KernelAbstractions, and threaded CPU versions of one program, so its results compare directly with the C++ BabelStream models already in `conformance/miniapps`.
- **CuPy:** NVRTC and the CUDA maths libraries (cuBLAS, cuFFT, cuRAND, cuSPARSE, cuSOLVER) as shipped.
- **Triton:** its own compiler, producing cubins for NVIDIA and code objects for AMD.
- **PyTorch:** precompiled fat binaries, cuBLAS and cuDNN, NVRTC for fused element-wise kernels, and Triton through `torch.compile`. The largest, and the last.

## Design

- A new conformance source kind for interpreter-hosted programs. Its environment is pinned by a lock file: pip requirements with hashes, or a Julia `Manifest.toml`. The environment builds in a conformance container and caches like the artifact archives, keyed by the lock file.
- A case is a test identifier from the package's own suite, or a small program kept in gemstone. Each package's suite is far larger than an emulated run can afford, so bounded workloads choose by module or name pattern, not by a hand-kept list.
- Each failure is classified: emulator defect, missing entry point, missing code for the target (a wheel with no SASS or PTX for it), race in the program or library (from `concurrency-checking.md`), or time limit.

## Steps

### 1. Harness and inventory

- The source kind, the environment cache, and one Numba case end to end.
- For each package, record which driver-level libraries it loads and which entry points it calls, so missing entry points appear as a list before any test runs.
- Completed:
  - Interpreter-hosted source kind with lock-file keyed environment cache in `conformance/interpreters/`.
  - Numba environment pinned via `requirements-numba.txt` and standalone CPython 3.12.
  - Driver inventory for Numba (`python_numba --case inventory`): driver API 13030; 467 entry points referenced by `cuda-bindings`; 19 missing entry points under gemstone, all EGL, VDPAU, and OpenGL interop (`cuEGLStream*`, `cuEventCreateFromEGLSync`, `cuGLGetDevices`, `cuGraphicsEGL*`, `cuGraphicsGL*`, `cuGraphicsResourceGetMappedEglFrame`, `cuGraphicsVDPAU*`, `cuVDPAU*`).
  - `smoke/vector_add` passes under gemstone emulation on `cuda-sass/sm_89`.

### 2. Numba

- Bounded subsets of the driver and kernel tests in `numba-cuda`.
- Completed:
  - 238 bounded cases in `numba-cuda` tested on `cuda-sass/sm_89` under gemstone emulation: 215 passed, 19 failed, 4 skipped.
  - Failures classified:
    - `cudadrv/test_cuda_driver.py::test_cuda_driver_occupancy` and `cudapy/test_vectorize.py` (15 cases): `cuOccupancyMaxPotentialBlockSize` returned `CUDA_ERROR_NOT_SUPPORTED` (801). Resolved: implemented full `cuOccupancy*` family in `native/cuda` derived from `Architecture.Limits` / `Occupancy.of` and multiprocessor counts from active device presets. Added reduced tests in `native/cuda/test/code-mode.cpp` and `device-limits.cpp`.
    - `cudapy/test_atomics.py::test_atomic_add_double_global` (3 cases): numerical results correct on both GPU and emulator; test asserted `'atom.add.f64'` in PTX text, but NVVM 7.0.1 on sm_87/sm_89 emits `'atom.global.add.f64'`. Verified identical failure on bare-metal RTX 4060 Ti host GPU. Classification: package/toolkit mismatch.
    - `cudapy/test_reduction.py::TestReduction::test_max_reduce`: fails during Numba untyped pass / type inference with `TypeError: Signature mismatch: 2 argument types given, but function takes 1 arguments`. Verified identical failure on bare-metal RTX 4060 Ti host GPU. Classification: package defect.

### 3. Julia

- `JuliaStream.jl` on CUDA, AMDGPU, oneAPI, and KernelAbstractions, compared with the C++ models.
- Bounded subsets of each package's tests.
- Check how each package finds the driver library and what version it requires. Julia packages download their own toolkit artifacts but take the driver from the system.
- Completed:
  - Pinned Julia upgraded from 1.9.3 to 1.13.4 in `conformance/dependencies.cmake`. Pinned manifests resolved for CUDA.jl (v6.5.1), AMDGPU.jl (v2.11.0), oneAPI.jl (v2.10.0), and KernelAbstractions.jl (v0.10.7).
  - Driver discovery and version checks:
    - `CUDA.jl`: Resolves `libcuda` via `CUDA_Driver_jll.libcuda` (fallback `Libdl.find_library(["libcuda.so.1", "libcuda.so"])`). Requires driver version >= 11.0; gemstone reports 13.3 (13030). Downloads CUDA runtime toolkit artifact (`CUDA_Runtime_jll` 0.25.0 / CUDA 13.4).
    - `AMDGPU.jl`: Finds `libamdhip64` and `libhsa-runtime64.so.1` in the directory given by `ROCM_PATH`, else through `hipconfig`, `/opt/rocm`, and `/usr`. `/dev/kfd` only gates `HSA.init()` in `__init__`; see the AMDGPU.jl risk below.
    - `oneAPI.jl`: Resolves Level Zero driver via `oneAPI_Level_Zero_Loader_jll` or system `libze_loader.so.1`, routing to gemstone Level Zero driver.
  - Entry points the Julia packages reached, implemented test-first:
    - 18 HIP entries in `native/hip`: `hipCtxCreate`, `hipCtxGetCurrent`, `hipCtxSetCurrent`, `hipDeviceGetMemPool`, `hipDeviceSetMemPool`, `hipMemPoolCreate`, `hipMemPoolGetAttribute`, `hipMemPoolSetAttribute`, `hipMallocFromPoolAsync`, `hipMemsetD8Async`, `hipMemsetD16Async`, `hipMemsetD32Async`, `hipModuleGetGlobal`, `hipFuncGetAttribute`, `hipModuleOccupancyMaxPotentialBlockSize`, `hipStreamGetPriority`, `hipIpcGetMemHandle`, `hipThreadExchangeStreamCaptureMode`.
    - `AMDGPU.jl` `ones` and broadcast now run on the `hip` route. `hipPointerGetAttributes` now reports an unknown pointer as host memory with `hipInvalidDeviceId`, matching the gfx1036 host, instead of failing; this was the next failure after the entry points.
    - `zeModuleGetKernelNames` added to the Level Zero module table. Matching each of the 74 `ze*` functions `oneAPI.jl` calls against its own `ze_*_dditable_t` rather than by name leaves one gap: `zeDeviceSystemBarrier`, absent from the pinned Level Zero headers, whose package test is commented out as unsupported. No other table entry is missing.
  - JuliaStream.jl correctness compared with C++ models under gemstone emulation (array size 4096, 2 iterations):
    - `CUDAStream.jl` on `cuda` route: ran under gemstone emulation; verification passed. Compares with C++ `miniapp_babelstream_cuda` on `cuda` route (passed).
    - `AMDGPUStream.jl` on `hip` route: cannot run with the pinned BabelStream source (f9e95b0), because its `devices()` calls `AMDGPU.get_agents`, which AMDGPU.jl 2.11.0 does not define; it sees no devices and exits at `Stream.jl:184`. The identical failure occurs without gemstone on gfx1036. Classification: benchmark/package mismatch, not an emulator defect. C++ `miniapp_babelstream_hip` in artifact `hip/gfx1030` passes under gemstone with `GEMSTONE_TARGET_ARCH=gfx1030`.
    - `oneAPIStream.jl` on `levelzero` route: cannot load with oneAPI.jl 2.10.0 because it uses `@LocalMemory`, removed in oneAPI 2 in favour of `oneLocalArray`; the identical failure occurs without gemstone. Classification: benchmark/package mismatch. C++ `miniapp_babelstream_sycl2020_usm` passes under gemstone on the `levelzero` route; the available artifact rejects `--float`, so the comparison used its default double precision.
    - `KernelAbstractionsStream.jl` on `cuda` and CPU: ran under gemstone emulation; verification passed. Compares with C++ `miniapp_babelstream_cuda` and `miniapp_babelstream_omp` (both passed).
  - Bounded package test suites under gemstone emulation:
    - `CUDA.jl` (`core/initialization`, `core/cudadrv`, `base/array`, `base/broadcast`): ran under gemstone emulation; initialisation and driver cases pass after implementing `CUDA_VISIBLE_DEVICES` (filtering ordinals, empty string, `-1`, UUIDs), `cuOccupancy*` queries, and stream priority.
    - `AMDGPU.jl` (`device/array`, `device/execution_control`): 22/22 and 5/5 passed on gfx1036. The suite runner needs `ParallelTestRunner` and `JLD2`, now pinned in the tests manifest.
    - `oneAPI.jl` (`level-zero`, `array`): `level-zero` passed 50/50 on the `levelzero` route after keeping deferred-free allocations queryable until reaped. `array` reached 53 passes and 36 errors from one root cause: `OpCompositeInsert` of an aggregate containing pointers is unsupported in the SPIR-V VM (`composite insertion requires scalar data`, `gpu_mapreduce_1d_kernel` instruction 0x11a). The same case passes 244/244 on the host Arc A750. Classification: emulator defect; a reduced case needs a VM representation for pointer-bearing aggregates, which does not exist yet. The suite runner now pins `ParallelTestRunner`, `JLD2`, and `BFloat16s`.
  - Environment footprints:
    - Standalone CPython 3.12: 234 MB.
    - Numba environment: 551 MB (wheels cache: 163 MB), build time ~20 s.
    - Standalone Julia 1.13.4: 1.1 GB (archive: 288 MB).
    - Julia package environments: depot with artifacts for CUDA, AMDGPU, oneAPI, KernelAbstractions: 8.0 GB (artifacts: 7.1 GB, compiled cache: 791 MB). Build time ~3.5 min.

### 4. CuPy

- Array, kernel, and maths-library tests, on CUDA first.

### 5. Triton

- The tutorial kernels (vector add, softmax, matrix multiply) and a bounded subset of the unit tests, on NVIDIA and AMD targets.

### 6. PyTorch

- Small model forward and backward passes, `torch.compile` through Triton, and a bounded subset of the CUDA tests. CUDA wheels first, ROCm wheels after AMDGPU.jl and Triton on AMD pass.

### 7. Management queries

- Tests that call NVML or AMD SMI, such as device utilisation queries, once `machine-identity.md` steps 3 and 4 exist.

## Checks

- Test first, seen failing, for behaviour changes in gemstone.
- Every failure carries a classification, and emulator defects get a reduced case in gemstone's own suites.
- Failures stay red.

## Risks to confirm

- Size: the PyTorch CUDA wheels and their NVIDIA dependencies are several GB, and the ROCm wheels are larger. The cache must hold them without bloating every conformance image.
- Time: PyTorch loads cuBLAS and cuDNN and runs many small kernels at start-up, which may not fit an emulated time budget.
- Driver version checks: packages compare the driver version gemstone reports with the toolkit they ship, and refuse to run below it.
- Targets: a wheel carries SASS for a fixed set of architectures plus PTX for the newest, so the target must be one it covers.
- Licences: whether vendor wheels may sit in the conformance cache and package, or must be fetched at build time only.
- AMDGPU.jl (v2.11.0) discovery, checked by running it in the HIP route image with no `/dev/kfd`:
  - Load-time checks: `ROCmDiscovery.__init__` reads `/sys/class/kfd/kfd/topology/nodes` only if the directory exists, then picks the ROCm library directory from `ROCM_PATH`, `hipconfig --rocmpath`, `/opt/rocm`, then `/usr`. It takes the first `libhsa-runtime64*so.1` and `libamdhip64*` in that directory, `ld.lld` from `<rocm>/llvm/bin` or the in-process `AMDGPU_LLVM_Backend_jll`, and device bitcode from that artifact. `AMDGPU.__init__` then returns early if `/dev/kfd` is absent, otherwise calls `HSA.init()` (`dlopen` of `libhsa-runtime64`), checks LLD and the device libraries, and calls `HIP.devices()` (`dlopen` of `libamdhip64`). Library paths are plain strings, so the `dlopen` happens at the first `ccall`.
  - Supported settings: `ROCM_PATH`, `HIP_DEVICE_LIB_PATH` and `DEVICE_LIB_PATH` (device bitcode only), `JULIA_AMDGPU_FORCE_EMULATED_SIGNALS`, `JULIA_AMDGPU_CORE_MUST_LOAD`, `JULIA_AMDGPU_HIP_MUST_LOAD`, `HIP_VISIBLE_DEVICES`, `HSA_OVERRIDE_GFX_VERSION`, `AMD_OPT_FLUSH`. The only Preferences are memory limits, `eager_gc`, `memory_pool_handle_type`, and `system_scope_fences`, none of which select a runtime. There is no JLL for HIP or HSA in this version.
  - `ROCM_PATH=$GEMSTONE_NATIVE_DIR` is a supported setting and selects gemstone's `libhsa-runtime64.so.1` and `libamdhip64.so` (the directory holds `libamdhip64.so`, which `check_rocm_path` tests for). The route's `LD_PRELOAD` already loads the same files, so the path-based `dlopen` resolves to the loaded copy. Without it the package uses the image's `/opt/rocm`, so the HIP image runs the vendor libraries and `hipGetDeviceCount` returns `hipErrorNoDevice`.
  - With `ROCM_PATH` set and no `/dev/kfd`, `AMDGPU.functional()` is true and `HIP.devices()` returns `Gemstone gfx1036` (or the selected `GEMSTONE_TARGET_ARCH`), because the `/dev/kfd` test only skips `HSA.init()` and the later checks. With the 18 entry points implemented, `AMDGPU.ones` and broadcast run. `AMDGPU.get_agents(:gpu)` is not defined in v2.11.0, so BabelStream's `AMDGPUStream.jl` device enumeration fails on any backend; see step 3.
  - Entry points the package called that gemstone previously exported as the shared unsupported stub (18 of 341 stubs in `libamdhip64`): `hipCtxCreate`, `hipCtxGetCurrent`, `hipCtxSetCurrent`, `hipDeviceGetMemPool`, `hipDeviceSetMemPool`, `hipMemPoolCreate`, `hipMemPoolGetAttribute`, `hipMemPoolSetAttribute`, `hipMallocFromPoolAsync`, `hipMemsetD8Async`, `hipMemsetD16Async`, `hipMemsetD32Async`, `hipModuleGetGlobal`, `hipFuncGetAttribute`, `hipModuleOccupancyMaxPotentialBlockSize`, `hipStreamGetPriority`, `hipIpcGetMemHandle`, `hipThreadExchangeStreamCaptureMode`. All 18 are now implemented. The package's 11 HSA calls (`hsa_init`, `hsa_shut_down`, `hsa_status_string`, `hsa_signal_*`, `hsa_memory_copy`) are all implemented.
  - An empty `/dev/kfd` is a workaround, not emulation: it satisfies a presence test, serves no ioctl, and only matters for the `HSA.init()` call. It is not needed to reach HIP, and the HIP entry points and Julia array/execution subsets run without it. Recommendation: do not add it. The 18 entry points above are implemented test-first in `native/hip`, context entries first. Revisit `/dev/kfd` only if a run shows the package needing an initialised HSA runtime, and then prefer `JULIA_AMDGPU_FORCE_EMULATED_SIGNALS=1` over a fake node if the hostcall path is not under test. Not run: whether kernels that use hostcalls need a live HSA runtime.
- oneAPI.jl (v2.10.0) loader and driver selection, checked in the Level Zero route image:
  - `oneL0` loads `oneAPI_Level_Zero_Loader_jll` and `NEO_jll` unconditionally on Linux. `oneL0.__init__` only tests `is_available()` on both JLLs, then sets `ZE_ENABLE_ALT_DRIVERS` to `NEO_jll.libze_intel_gpu` if the variable is unset. `oneAPI.__init__` also `dlopen`s `libze_intel_gpu.so.1`, sets `OCL_ICD_FILENAMES`, and extends `LD_LIBRARY_PATH`, all from `NEO_jll`. There is no package Preference for a system loader.
  - Supported settings: `ZE_ENABLE_ALT_DRIVERS` (honoured when already set), and the JLLWrappers preference `libze_loader_path` for `oneAPI_Level_Zero_Loader_jll`, which replaces the artifact loader with any path. The preference only takes effect when the JLL is listed under `[extras]` of the active `Project.toml` next to its `LocalPreferences.toml`.
  - The Level Zero route already exports `ZE_ENABLE_ALT_DRIVERS=<native>/libze_gemstone.so.1`. With that and no other setting, the artifact loader enumerates gemstone's driver and device (`ZeDevice(GPU, vendor 0x8086, device 0x0001)`) and `oneAPI.functional` is true. The NEO libraries are loaded but never registered as a driver. With `libze_loader_path=/usr/lib/x86_64-linux-gnu/libze_loader.so.1` the image's system loader (1.28.2) is used instead, with the same result.
  - `zeModuleGetKernelNames` was the first failure: `oneArray(...) .+ 2` failed with `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE` because `native/levelzero` had no `pfnGetKernelNames` in its module table. Matching each of the 74 `ze*` functions the package calls outside its generated bindings to the `ze_*_dditable_t` that declares its `ze_pfn*_t` leaves one unmatched function, `zeDeviceSystemBarrier`, which is absent from the pinned headers and whose package test is commented out as unsupported. The earlier by-name match could not tell tables apart for names such as `GetProperties`.
  - Outcome: no setting is needed; the route environment already works with the default JLL loader, so keep it and skip the system-loader preference. `zeModuleGetKernelNames` is implemented test-first in `native/levelzero`. `oneAPI.jl` `level-zero` passes 50/50 and the `array` subset reaches 53 passes with 36 errors from one unsupported SPIR-V composite insertion, after keeping deferred-free allocations queryable. The earlier statement that `oneAPI.jl` requires `NEO_jll` was wrong: the artifact is only checked for availability.

## Non-goals

- Replacing or patching any package or vendor library.
- Performance numbers from these runs.
- JAX, TensorFlow, and other frameworks until these pass.

## Open decisions

- Where environments build. Recommendation: in the existing conformance container images, so the vendor toolchains match the C++ routes.
- Whether the first Julia target is CUDA.jl or AMDGPU.jl. Recommendation: CUDA.jl, since it goes through the same PTX path as Numba, then AMDGPU.jl and oneAPI.jl.
