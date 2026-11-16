# Ecosystem conformance

Status: in progress. Step 1 is done. Step 2 (Numba) has run on the emulated `cuda-ptx` routes: 232 of 238 cases pass on each route, and the six failures are classified below. Step 3 (Julia) has partial results: the `hip/gfx1030` route is blocked at discovery, and the `levelzero` route reports all four of its cases failed. Steps 4 to 7 have not started. It depends on `concurrency-checking.md` steps 1 and 2 to classify failures, which are done (its steps 3 to 7 are not marked done). Step 7 depends on `machine-identity.md`, whose steps 1 to 5 are marked done.

Each result below names the route or real device it ran on and cites its log. The logs under `/var/tmp/gs/t47/` and `/var/tmp/gs/t24-ecosystem/` are outside the repository. Run A and Run B are directories under the worktree's `target/conformance/emulated/direct/`. Log paths in this plan that begin with a route name, such as `cuda-ptx-sm_89/`, are relative to the run directory:

- Run A: `run-1794816923696879407-0`, the `interpreters-bounded` workload on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`. Per route, `junit.xml` lists 241 testcases: 232 pass and 9 fail (Numba 238 cases: 232 pass, 6 fail; Julia 3 cases: all fail).
- Run B: `run-1794818705200584085-0`, the same workload on the `levelzero` route. `junit.xml` lists 4 testcases, all failed.

## Goal

Run GPU packages from Python and Julia unchanged under gemstone, with their vendor libraries as shipped, and keep them as conformance cases. Gemstone stays at its layer: above the kernel driver, below the vendor implementation. It provides `libcuda.so.1`, the HIP and HSA runtimes, the Level Zero driver, and the OpenCL ICD, and nothing in a package or vendor library is replaced or patched.

## Current state

- Conformance builds C, C++, CUDA, HIP, SYCL, and OpenMP programs in containers, runs them under gemstone, and groups them into workloads such as `short`, `miniapps`, and the bounded vendor conformance test suites.
- The `interpreters-bounded` workload runs Numba and Julia cases on the emulated `cuda-ptx` and `levelzero` routes (Run A and Run B). No `cuda-sass` case has run. No interpreter case has run on the `hip` route, which stops at discovery (Step 3).

## Packages

- **Numba** (`numba-cuda`): generates PTX through NVVM and calls the driver API directly. Small, and the first case.
- **Julia:** CUDA.jl, AMDGPU.jl, oneAPI.jl, and KernelAbstractions.jl. BabelStream's Julia variants `CUDAStream.jl`, `AMDGPUStream.jl`, and `oneAPIStream.jl` have been attempted (Step 3). `KernelAbstractionsStream.jl` has not.
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
  - Driver inventory for Numba (Run A, `cuda-ptx-sm_89/python_numba/0/output.log`, emulated `cuda-ptx/sm_89`): driver API 13030; 467 entry points referenced by `cuda-bindings`; 19 missing under gemstone. The missing names, as listed in that log: `cuEGLStreamConsumerAcquireFrame`, `cuEGLStreamConsumerConnect`, `cuEGLStreamConsumerConnectWithFlags`, `cuEGLStreamConsumerDisconnect`, `cuEGLStreamConsumerReleaseFrame`, `cuEGLStreamProducerConnect`, `cuEGLStreamProducerDisconnect`, `cuEGLStreamProducerPresentFrame`, `cuEGLStreamProducerReturnFrame`, `cuEventCreateFromEGLSync`, `cuGLGetDevices`, `cuGraphicsEGLRegisterImage`, `cuGraphicsGLRegisterBuffer`, `cuGraphicsGLRegisterImage`, `cuGraphicsResourceGetMappedEglFrame`, `cuGraphicsVDPAURegisterOutputSurface`, `cuGraphicsVDPAURegisterVideoSurface`, `cuVDPAUCtxCreate`, `cuVDPAUGetDevice`.
  - `smoke/vector_add` passes on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang` (Run A `junit.xml`). No `cuda-sass/sm_89` case has run.

### 2. Numba

- Bounded subsets of the driver and kernel tests in `numba-cuda`.
- Completed (Run A, emulated `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`):
  - 238 bounded cases per route: 232 pass and 6 fail. The six are the same on both routes. The occupancy case `cudadrv/test_cuda_driver.py::TestCudaDriver::test_cuda_driver_occupancy` passes on both routes (Run A `junit.xml`).
  - Failing cases:
    - `cudapy/test_atomics.py`: `test_atomic_add_double`, `_2`, and `_3`. Each fails at its `assertIn` for `red.shared.add.f64` before any launch. The test expects the `red` form for runtime versions above (12, 1). The emulated NVVM is release 13.3 and emits `atom.shared.add.f64` (`cuda-ptx-sm_89/python_numba/75/output.log`). The real RTX 4060 Ti (NVVM 11.6, runtime (11, 6)) takes the `atom` branch and passes these three (`/var/tmp/gs/t47/numba-real-atomics.log`). Classification: unresolved. The emulated runtime version is not traced.
    - `cudapy/test_atomics.py`: `test_atomic_add_double_global`, `_2`, and `_3`. The test expects `red.add.f64`. The emulated PTX has `atom.global.add.f64` (`cuda-ptx-sm_89/python_numba/78/output.log`, `79`, and `80`). On the real RTX 4060 Ti the same three fail, asserting `atom.add.f64` (`numba-real-atomics.log`). Classification: the test's expected PTX does not match the PTX the toolkit emits. Left red.
  - Not selected by the bounded workload: `cudapy/test_reduction.py::TestReduction::test_max_reduce`. On the real RTX 4060 Ti it fails with `TypeError: Signature mismatch: 2 argument types given, but function takes 1 arguments` at `numba/cuda/kernels/reduction.py:241` (`/var/tmp/gs/t47/numba-real-max-reduce.log`). The emulated result is unknown.

### 3. Julia

- `JuliaStream.jl` on CUDA, AMDGPU, oneAPI, and KernelAbstractions, compared with the C++ models.
- Bounded subsets of each package's tests.
- Completed:
  - The pinned Julia manifests under `conformance/interpreters/julia/` pin Julia 1.13.4 (`julia_version = 1.13.4`) and KernelAbstractions.jl 0.10.7. CUDA.jl 6.5.1 and CUPTI.jl 6.5.1 are pinned in `cuda/tests/Manifest.toml` and `cuda/babelstream/Manifest.toml`, AMDGPU.jl 2.11.0 in `amdgpu/tests/Manifest.toml`, and oneAPI.jl 2.10.0 in `oneapi/tests/Manifest.toml`.

#### CUDA.jl

Run A, emulated, `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`:

- `core/initialization` and `core/cudadrv` fail on both routes at start with `CUPTI.CUPTIError(code=CUPTI.CUptiResult(0x00000001))` (`cuda-ptx-sm_89/julia_tests_cuda/1/output.log` and `/0/output.log`). Real RTX 4060 Ti baselines: `core/initialization` 63 of 63 pass in 29.2 s (`/var/tmp/gs/t47/cupti-baseline-init.log`); `core/cudadrv` 2216 pass and 1 broken of 2217 in 45.4 s (`cupti-baseline-cudadrv.log`).
- `julia_babelstream_cuda/babelstream` fails on both routes. The log shows `Failed to load compiled kernel image`, then `CUDA error: operation not supported (code 801)` from `cuThreadExchangeStreamCaptureMode`, reached through `relaxed_capture_mode` (`cudadrv/graph.jl:53`) (`cuda-ptx-sm_89/julia_babelstream_cuda/0/output.log`). The stub at `native/generated/cuda/gemstone_cuda_unsupported.cpp:279` returns 801. Classification: emulator gap, an unimplemented entry point.
- Run A's `junit.xml` has no testcase for `base/array` or `base/broadcast`, although the include list names them. The reason is not established.

Earlier t24 runs, not repeated on this branch. The build revision is not recorded:

- `base/array` on device `Gemstone PTX emulator`: 128 pass, 79 error, 207 total (`/var/tmp/gs/t24-ecosystem/julia-test-cuda-array.log`). The route is not recorded in the log.
- `base/broadcast` on device `Gemstone PTX emulator`: 0 pass, 25 error, 25 total (`julia-test-cuda-broadcast.log`). The route is not recorded in the log.
- `CUDAStream.jl` on the real RTX 4060 Ti: the log names the device (`/var/tmp/gs/t24-ecosystem/julia-cuda-stream.log`, line 8). It has no launch or verification line, so no BabelStream result is claimed.

CUPTI, the cause of the `core` failures. The link is inferred, not confirmed by disassembly:

- Real driver (`cupti-trace/tables.log`): `CUPTI.version()` requests export table `7cb63f9b9ca48e42a484beb66e6aed7f` (lines 27-29: status 0, first word 560, which `tables.py` reads as a size) and table `d7bd8af8e7acca4abdbcfb1f1b3fcf33` (lines 99-101: status 0, first word 112). After the version call returns 13.4.1 (line 117), table `d7bd8af8...` is requested again and returns status 4, `CUDA_ERROR_DEINITIALIZED` (lines 118-119). Why the second request is made is not established.
- Emulator (`cupti-trace/emu-export.log`, lines 46-47): the first request, for `7cb63f9b9ca48e42a484beb66e6aed7f`, returns `CUDA_ERROR_INVALID_VALUE` with a null table. The second table is never requested. `cuptiGetVersion` returns `CUPTI_ERROR_INVALID_PARAMETER` (`cupti-trace/probe.log`).
- The link between the failed first request and the version error is inferred from call order. libcupti's handling of that failure is not disassembled. The table contents are not identified, so no fix is implemented.

#### AMDGPU.jl

- `device/array`: 22 of 22 pass, and `device/execution_control`: 5 of 5 pass, both on emulated `Gemstone gfx1036`. These are earlier t24 runs; the logs do not record the route (`/var/tmp/gs/t24-ecosystem/amdgpu-committed-device-array.log`, `amdgpu-committed-device-execution_control.log`).
- `hip/gfx1030` route: blocked at discovery in `julia_tests_amdgpu`. The log records `hipErrorNoDevice`, and no case runs (`/var/tmp/gs/t47/emulate-hip-gfx1030-3.log`, line 37). The cause under emulation is not established. Direct vendor probes without the emulator reproduce the error when both `ROCR_VISIBLE_DEVICES` and `HIP_VISIBLE_DEVICES=0` are set (`/var/tmp/gs/t47/uuid-probe-index0.log`, `uuid-probe-uuid.log`), and report one device without them (`uuid-probe-none.log`, `uuid-probe-hip0.log`). The probe logs do not print their selector values, so this mapping rests on the file names. The HIP probe sets these selectors (`conformance/src/device_runtime.cpp:302`), discovery runs with them (`conformance/src/driver.cpp:745-747`), and the `--list` branch of `conformance/interpreters/julia/package_tests.jl` does not clear them. That is an untested hypothesis under emulation.
- `AMDGPUStream.jl` on the hip route fails with `Device 1 out of range (1..0)` (`/var/tmp/gs/t24-ecosystem/amdgpu-stream-2.log`, gemstone, `--apis hip`). The same error appears without gemstone (`amdgpu-stream-native.log`, line 1), so the emulator does not cause it. Why BabelStream sees one device is not established.
- The C++ HIP BabelStream on `Gemstone gfx1030` passes (`/var/tmp/gs/t24-ecosystem/hip-babelstream-cpp2.log`, earlier t24 run).

#### oneAPI.jl

Run B, emulated `levelzero` route:

- `level-zero` testset (`levelzero/julia_tests_oneapi/2/output.log`): 50 pass, 1 error, of 51. The error is `ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE` at `level-zero.jl:377`. Real Intel Arc A750: 50 of 50 pass (`/var/tmp/gs/t24-ecosystem/oneapi-case-level-zero-real.log`).
- `array` testset (`levelzero/julia_tests_oneapi/0/output.log`): 53 pass, 36 error, of 89. All 36 errors are `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`. The source of that status is not established. Real Arc A750: 244 of 244 pass (`/var/tmp/gs/t24-ecosystem/oneapi-case-array-real.log`). An earlier t24 run logged `composite insertion requires scalar data` (`oneapi-case-array-trace.log`); the current run does not.
- `execution` testset (`levelzero/julia_tests_oneapi/1/output.log`): 35 pass, 1 fail, 32 error, 1 broken, of 69. All 32 errors are `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`. The failure at `execution.jl:237` (`"" == "2"`) is not analysed.
- All four Run B cases are reported failed (`/var/tmp/gs/t47/emulate-levelzero.log`, lines 361-365).
- `oneAPIStream.jl` fails at load with `UndefVarError: @LocalMemory not defined in Main` (`levelzero/julia_babelstream_oneapi/0/output.log`). The same error appears without gemstone (`/var/tmp/gs/t24-ecosystem/oneapi-stream-native.log`, line 3), so the emulator does not cause it. The cause is not established.
- The C++ SYCL BabelStream passes on the emulated `Gemstone Virtual SPIR-V GPU`, at its default double precision (`/var/tmp/gs/t24-ecosystem/levelzero-babelstream-cpp.log`, earlier t24 run; route not recorded in the log). Its `--float` option is rejected (`levelzero-babelstream-cpp-float.log`).

#### KernelAbstractionsStream.jl

No run log exists for any route. No result is claimed.

#### Environment footprints

From `/var/tmp/gs/t47/footprint-all.log`, measured with `du`:

- Standalone CPython 3.12.16: 234 MB.
- Numba environment: 551 MB. The wheels cache is a separate directory, 163 MB.
- Standalone Julia 1.13.4: 1.1 GB.
- Julia depots: CUDA.jl 5.0 GB, AMDGPU.jl 1.2 GB, oneAPI.jl 3.3 GB.

### 4. CuPy

- Array, kernel, and maths-library tests, on CUDA first.

### 5. Triton

- The tutorial kernels (vector add, softmax, matrix multiply) and a bounded subset of the unit tests, on NVIDIA and AMD targets.

### 6. PyTorch

- Small model forward and backward passes, `torch.compile` through Triton, and a bounded subset of the CUDA tests. CUDA wheels first, ROCm wheels after AMDGPU.jl and Triton on AMD pass.

### 7. Management queries

- Tests that call NVML or AMD SMI, such as device utilisation queries.

## Checks

- Test first, seen failing, for behaviour changes in gemstone.
- Every failure carries a classification, and emulator defects get a reduced case in gemstone's own suites.
- Failures stay red.

## Risks to confirm

- Size: the PyTorch CUDA wheels and their NVIDIA dependencies are several GB, and the ROCm wheels are larger. The cache must hold them without bloating every conformance image. The CUDA.jl depot alone is 5.0 GB (Step 3).
- Time: PyTorch loads cuBLAS and cuDNN and runs many small kernels at start-up, which may not fit an emulated time budget.
- Driver version checks: packages may compare the driver version gemstone reports with the toolkit they ship, and refuse to run below it. The Numba inventory reports driver API 13030 (Run A `cuda-ptx-sm_89/python_numba/0/output.log`, line 1).
- Targets: a wheel carries SASS for a fixed set of architectures plus PTX for the newest, so the target must be one it covers.
- Licences: whether vendor wheels may sit in the conformance cache and package, or must be fetched at build time only.
- Device discovery: the `hip/gfx1030` route stops at `hipErrorNoDevice` before any case runs, and the cause under emulation is not established (Step 3). The `levelzero` route reports all four cases failed, with `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE` as the dominant error, and its source is not established (Step 3).

## Non-goals

- Replacing or patching any package or vendor library.
- Performance numbers from these runs.
- JAX, TensorFlow, and other frameworks until these pass.

## Open decisions

- Where environments build. Recommendation: in the existing conformance container images, so the vendor toolchains match the C++ routes.
- Whether the first Julia target is CUDA.jl or AMDGPU.jl. Recommendation: CUDA.jl, since it goes through the same PTX path as Numba, then AMDGPU.jl and oneAPI.jl.
