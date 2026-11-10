# Ecosystem conformance

Status: proposed, not started. It depends on `concurrency-checking.md` steps 1 and 2 to classify failures, and on `machine-identity.md` for packages that query NVML or AMD SMI.

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

### 2. Numba

- Bounded subsets of the driver and kernel tests in `numba-cuda`.

### 3. Julia

- `JuliaStream.jl` on CUDA, AMDGPU, oneAPI, and KernelAbstractions, compared with the C++ models.
- Bounded subsets of each package's tests.
- Check how each package finds the driver library and what version it requires. Julia packages download their own toolkit artifacts but take the driver from the system.

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
