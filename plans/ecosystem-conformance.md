# Ecosystem conformance

Status: in progress. Steps 1, 2, and 3 are done; step 4 is next. It depends on `concurrency-checking.md` steps 1 and 2 to classify failures, and on `machine-identity.md` for packages that query NVML or AMD SMI.

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
    - `cudadrv/test_cuda_driver.py::test_cuda_driver_occupancy` and `cudapy/test_vectorize.py` (15 cases): `cuOccupancyMaxPotentialBlockSize` returns `CUDA_ERROR_NOT_SUPPORTED` (801). Classification: emulator defect.
    - `cudapy/test_atomics.py::test_atomic_add_double_global` (3 cases): numerical results correct; test asserted `'atom.add.f64'` in PTX text, but NVVM 7.0.1 on sm_87/sm_89 emits `'atom.global.add.f64'`. Classification: emulator defect.

### 3. Julia

- `JuliaStream.jl` on CUDA, AMDGPU, oneAPI, and KernelAbstractions, compared with the C++ models.
- Bounded subsets of each package's tests.
- Check how each package finds the driver library and what version it requires. Julia packages download their own toolkit artifacts but take the driver from the system.
- Completed:
  - Driver discovery and version checks:
    - `CUDA.jl`: Resolves `libcuda` via `CUDA_Driver_jll.libcuda` (fallback `Libdl.find_library(["libcuda.so.1", "libcuda.so"])`). Requires driver version >= 11.0; gemstone reports 13.3 (13030). Downloads CUDA runtime toolkit artifact (`CUDA_Runtime_jll` 0.9.2 / CUDA 12.2).
    - `AMDGPU.jl`: Resolves `libamdhip64.so` and `libhsa-runtime64.so` via system search or JLL wrappers; requires `/dev/kfd` for hardware kernel driver access, routing to gemstone HIP runtime.
    - `oneAPI.jl`: Resolves Level Zero driver via `oneAPI_Level_Zero_Loader_jll` or system `libze_loader.so.1`, routing to gemstone Level Zero driver.
  - Cases on `cuda-sass/sm_89` under gemstone emulation:
    - `julia_babelstream_cuda`: enumerates device (`Gemstone PTX emulator`, `CuDevice(0)`), allocates buffers; fails in `init_arrays!` at `launch_heuristic` -> `cuOccupancyMaxPotentialBlockSize` with `CUDA_ERROR_NOT_SUPPORTED` (801). Classification: emulator defect.
    - `julia_tests_cuda`:
      - `core/initialization`: 27 passed, 3 failed (assertions on absence of device and subprocess spawning). Classification: emulator defect.
      - `core/cudadrv`: 76 passed, 12 failed, 6 errored (`cuOccupancyMaxPotentialBlockSize` [801], `cuStreamGetPriority` [801], timer granularity). Classification: emulator defect.
      - `base/array`: 128 passed, 0 failed, 79 errored (all in `launch_heuristic` -> `cuOccupancyMaxPotentialBlockSize` [801]). Classification: emulator defect.
      - `base/broadcast`: 0 passed, 25 errored (all in `launch_heuristic` -> `cuOccupancyMaxPotentialBlockSize` [801]). Classification: emulator defect.
  - Environment footprints:
    - Standalone CPython 3.12: 234 MB.
    - Numba environment: 551 MB (wheels cache: 163 MB), build time ~20 s.
    - Standalone Julia 1.9.3: 501 MB.
    - Julia CUDA environment: 3.0 GB, build time ~3.5 min.

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

## Non-goals

- Replacing or patching any package or vendor library.
- Performance numbers from these runs.
- JAX, TensorFlow, and other frameworks until these pass.

## Open decisions

- Where environments build. Recommendation: in the existing conformance container images, so the vendor toolchains match the C++ routes.
- Whether the first Julia target is CUDA.jl or AMDGPU.jl. Recommendation: CUDA.jl, since it goes through the same PTX path as Numba, then AMDGPU.jl and oneAPI.jl.
