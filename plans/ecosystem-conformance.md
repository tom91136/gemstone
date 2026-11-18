# Ecosystem conformance

Status: in progress. Step 1 is done. Step 2 (Numba) passes on the real RTX 4060 Ti: in Run I, 1999 cases pass, 63 skip, and none fail, of 2062 (direct route). On the emulated `cuda-ptx/sm_89` route, Run J passes 1916, skips 63, and fails 83, and each of those 83 passes on the real device. Step 3 (Julia) has partial results: BabelStream passes on the emulated `cuda-ptx` routes, the four CUDA.jl `core` cases get past CUPTI and fail on other emulator gaps (Run H), the `hip/gfx1030` route runs `device/array` and `device/execution_control` and passes both, and the `levelzero` route passes one of its four cases on retry (Run D). Steps 4 to 7 have not started. It depends on `concurrency-checking.md` steps 1 and 2 to classify failures, which are done (its steps 3 to 7 are not marked done). Step 7 depends on `machine-identity.md`, whose steps 1 to 5 are marked done.

Each result below names the route or real device it ran on and cites its log. The logs under `/var/tmp/gs/t47/` and `/var/tmp/gs/t24-ecosystem/` are outside the repository. Runs A to D are directories under the worktree's `target/conformance/emulated/direct/`. Log paths in this plan that begin with a route name, such as `cuda-ptx-sm_89/`, are relative to the run directory:

- Run A: `run-1794816923696879407-0`, the `interpreters-bounded` workload on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`, before the include list was corrected. Per route, `junit.xml` lists 241 testcases: 232 pass and 9 fail (Numba 238 cases: 232 pass, 6 fail; Julia 3 cases: all fail).
- Run B: `run-1794818705200584085-0`, the same workload on the `levelzero` route. `junit.xml` lists 4 testcases, all failed. Log: `/var/tmp/gs/t47/emulate-levelzero.log`. Superseded by Run D, below.
- Run C: `run-1794828293754483810-0`, the same workload on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang` with the corrected include list (`conformance/workloads/interpreters-bounded.toml`, commit `95d826fa`). `junit.xml` lists 500 testcases, 250 per route: 476 pass and 24 fail, 12 per route. Log: `/var/tmp/gs/t47/emulate-cuda-ptx-sm89-bounded-2.log`.
- Run D: `run-1794830667312584475-0`, the same workload on the `levelzero` route, after the `oneAPI.jls` failure history was moved aside. `junit.xml` lists 4 testcases: 3 failed and 1 passed on retry. Log: `/var/tmp/gs/t47/emulate-levelzero-3.log`.
- Run E: `run-1794838871438054626-0`, the same workload on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`, from an empty interpreter directory, after the T50 fixes. Log: `/var/tmp/gs/t50/emulate-cuda-final2.log` (line 2467: `470 passed (0 completion only), 8 skipped, 22 failed (0 time limit), 0 not run of 500 cases in 112.2 s`). Per route, `junit.xml` lists 250 testcases: 235 pass, 4 skip, 11 fail.
- Run F: `run-1794833570282972333-0`, the same workload on `hip/gfx1030`, after the listing and shim fixes. Log: `/var/tmp/gs/t50/emulate-hip-3.log`.
- Run G: `run-1794840321391561769-0`, four `julia_tests_oneapi` cases on `levelzero` with `GEMSTONE_TRACE_LIST=levelzero`. Log: `/var/tmp/gs/t50/ze-trace.log`.

## Goal

Run GPU packages from Python and Julia unchanged under gemstone, with their vendor libraries as shipped, and keep them as conformance cases. Gemstone stays at its layer: above the kernel driver, below the vendor implementation. It provides `libcuda.so.1`, the HIP and HSA runtimes, the Level Zero driver, and the OpenCL ICD, and nothing in a package or vendor library is replaced or patched.

## Current state

- Conformance builds C, C++, CUDA, HIP, SYCL, and OpenMP programs in containers, runs them under gemstone, and groups them into workloads such as `short`, `miniapps`, and the bounded vendor conformance test suites.
- The `interpreters-bounded` workload runs Numba and Julia cases on the emulated `cuda-ptx` and `levelzero` routes (Runs A to D). No `cuda-sass` case has run. No interpreter case has run on the `hip` route, which stops at discovery (Step 3).

## Packages

- **Numba** (`numba-cuda`): generates PTX through NVVM and calls the driver API directly. Small, and the first case.
- **Julia:** CUDA.jl, AMDGPU.jl, oneAPI.jl, and KernelAbstractions.jl. BabelStream's Julia variants `CUDAStream.jl`, `AMDGPUStream.jl`, and `oneAPIStream.jl` have been attempted (Step 3). `KernelAbstractionsStream.jl` has not.
- **CuPy:** NVRTC and the CUDA maths libraries (cuBLAS, cuFFT, cuRAND, cuSPARSE, cuSOLVER) as shipped.
- **Triton:** its own compiler, producing cubins for NVIDIA and code objects for AMD.
- **PyTorch:** precompiled fat binaries, cuBLAS and cuDNN, NVRTC for fused element-wise kernels, and Triton through `torch.compile`. The largest, and the last.

## Design

- A new conformance source kind for interpreter-hosted programs. Its environment is pinned by a lock file: pip requirements with hashes, or a Julia `Manifest.toml`. The environment builds on the host at run time and caches keyed by the lock file.
- A case is a test identifier from the package's own suite, or a small program kept in gemstone. Each package's suite is far larger than an emulated run can afford, so bounded workloads choose by module or name pattern, not by a hand-kept list.
- Each failure is classified: emulator defect, missing entry point, missing code for the target (a wheel with no SASS or PTX for it), race in the program or library (from `concurrency-checking.md`), or time limit.

## Steps

### 1. Harness and inventory

- The source kind, the environment cache, and one Numba case end to end.
- For each package, record which driver-level libraries it loads and which entry points it calls, so missing entry points appear as a list before any test runs.
- Completed:
  - Interpreter-hosted source kind with lock-file keyed environment cache in `conformance/interpreters/`.
  - Numba environment pinned via `requirements-numba.txt` and standalone CPython 3.12.
  - Driver inventory for Numba (Run E, `cuda-ptx-sm_89/python_numba/0/output.log`, emulated `cuda-ptx/sm_89`): driver API 13030; the case now derives its expected set from the package, `numba.cuda.cudadrv.drvapi.API_PROTOTYPES`, and fails when the driver lacks an entry point Numba calls. The log reports `entry points called by numba: 75` and `missing entry points called by numba: 0`, so the case passes. It also lists the entry points `cuda-bindings` exposes that Numba does not call: 392, of which 19 are missing under gemstone, the EGL, GL, and VDPAU interop entry points (`cuEGLStream*`, `cuEventCreateFromEGLSync`, `cuGLGetDevices`, `cuGraphicsEGLRegisterImage`, `cuGraphicsGLRegister*`, `cuGraphicsResourceGetMappedEglFrame`, `cuGraphicsVDPAURegister*`, `cuVDPAUCtxCreate`, `cuVDPAUGetDevice`). Run A's earlier figures (467 referenced, 19 missing) counted every name `cuda-bindings` exposes, not what Numba calls.
  - `smoke/vector_add` passes on `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang` (Run E `junit.xml`). No `cuda-sass/sm_89` case has run.

### 2. Numba

- Bounded subsets of the driver and kernel tests in `numba-cuda`.
- Completed (Runs A and C, emulated `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`):
  - Run A: 238 bounded cases per route, 232 pass and 6 fail. Run C: 245 per route, 238 pass and 7 fail. The failing cases are the same on both routes. The occupancy case `cudadrv/test_cuda_driver.py::TestCudaDriver::test_cuda_driver_occupancy` passes on both routes (Run A and Run C `junit.xml`).
  - Run E, after the harness fixes: 245 cases per route, 235 pass, 4 skip, 6 atomics plus `test_max_reduce` fail (`/var/tmp/gs/t50/emulate-cuda-final2.log`, lines 2468-2474 and 2479-2485). Skips are now reported as skipped (the 8 skips across both routes), where Runs A and C counted them as passes. Each of the 14 failures carries the label `package mismatch`, derived from the failure text: the `'atom.add.f64' not found`-style assertion or the `Signature mismatch` error, with no Gemstone error code present. The label `emulator defect` is now reserved for output that carries a `CUDA_ERROR_*` code.
  - Failing cases:
    - `cudapy/test_atomics.py`: `test_atomic_add_double`, `_2`, `_3`, `_global`, `_global_2`, and `_global_3` fail in Run E and in T65's real run. The legacy test tree (`numba/cuda/tests`, in the lead's interpreter) expects `red` above runtime version (12, 1) and `atom` otherwise, then asserts `f'{inst}.add.f64'` (`site-packages/numba/cuda/tests/cudapy/test_atomics.py`, about lines 560-573). The runtime is above (12, 1) in both runs, so the test expects `red`, while NVVM 13.3 emits `atom`. T65's real run failed on `'red.shared.add.f64' not found` (three cases) and `'red.add.f64' not found` (three cases). numba-cuda 0.30.4 accepts `(red|atom)` (`numba_cuda/numba/cuda/tests/cudapy/test_atomics.py`, about lines 684-698). On T71, all 26 shared `test_atomic_*_double*` cases pass on the real RTX 4060 Ti (Run I). The cause is the legacy test tree's expectation, not the emulator or the runtime version.
  - `cudapy/test_reduction.py::TestReduction::test_max_reduce`: not selected in Run A, whose include pattern named `cudapy/test_reduce.py`. Run C selects the whole module: its other six cases pass on each route, and `test_max_reduce` fails on both routes with `TypeError: Signature mismatch: 2 argument types given, but function takes 1 arguments` at `numba/cuda/kernels/reduction.py:241`, before any launch (`cuda-ptx-sm_89/python_numba/224/output.log`). The real RTX 4060 Ti fails it the same way on the legacy tree (`/var/tmp/gs/t47/numba-real-max-reduce.log`). On T71 it passes on the real RTX 4060 Ti (7.22 s, `/var/tmp/gs/t71/real-run-2.log`, line 1742), so the failure came from the legacy copy of `kernels/reduction.py`, not from the emulator.

- Run I, direct route on the real RTX 4060 Ti, pinned environment `numba-dccec4165817b571` (numpy 2.4.6, pytest 9.0.1; `/var/tmp/gs/t71/real-run-4.log`, line 2073): `conformance: 1999 passed (0 completion only), 63 skipped, 0 failed (0 time limit), 0 not run of 2062 cases in 6715.4 s`. The earlier run on numpy 2.4.6 and pytest 8.4.2 (`real-run-2.log`, line 2073, 5428.0 s) gave the same counts. The lead's T65 run on the same device, legacy interpreter (`/var/tmp/gs/t65/real-run.log`, line 2355): `conformance: 1074 passed (0 completion only), 37 skipped, 411 failed (0 time limit), 0 not run of 1522 cases in 1786.6 s`. T65 collected the legacy `numba/cuda/tests` tree and T71 collects `numba_cuda/numba/cuda/tests`, which is why the populations differ (1522 and 2062 ids).
  - Like-for-like on the 1439 shared ids: 401 fail to pass, 997 pass to pass, 9 pass to skip, 13 skip to pass, 19 skip to skip, and no pass to fail or fail to fail. Only in T65: 83 ids (10 failed, 68 passed, 5 skipped), in legacy-only files (`cudadrv/test_mvc.py`, `cudadrv/test_ptds.py`) or legacy versions of shared files (40 in `cudapy/test_ufuncs.py`). Only in T71: 623 ids (588 passed, 35 skipped), in `numba_cuda`-only files (`cudapy/test_flow_control.py`, `cudapy/test_fp8_bindings.py`, `cudapy/test_bfloat16.py`, and others) or in `numba_cuda` versions of shared files.
  - Causes and fixes. numba and numba-cuda are not patched:
    - Group A (353 cases, NVVM rejects `-arch=compute_NN`): the system nvrtc 11.6 (`/usr/local/cuda`) loaded beside NVVM 13.3.73 advertised sm_35 to sm_87, so NVVM rejected `compute_35`. Pinned `nvidia-cuda-nvrtc==13.3.33`.
    - Group B (41 cases, `TypeError: Signature mismatch` raised by numba-cuda's `FixupArgs` pass, for example `TestGUFuncScalar.test_gufunc_old_style_scalar_as_array`): pinned `nvidia-nvjitlink==13.3.33`, which numba-cuda's `cu13` extra does not install. The captured output holds no nvJitLink warning, so this attribution rests on the fix, not on a logged warning.
    - Shim: the interpreter ran with PYTHONPATH and no site processing, so numba-cuda's `.pth` redirector never loaded and `numba.cuda` resolved to the legacy copy. The shim now drops PYTHONPATH and sets `CUDA_HOME`.
    - Group C (10 cases, `fixture 'r' not found`): the T71 collection no longer contains these ids. The legacy tree that T65 ran defines `test_multiple_hcmp_1` to `_5` (in `cudapy/test_intrinsics.py` and `cudapy/test_operator.py`) as functions taking `(r, a, b, c)`. Pytest collected them as tests and read `r`, `a`, `b` and `c` as fixtures, so setup failed (lead's log: `/var/tmp/gs/t65/real-run/direct/run-1794938738622090499-0/cuda-ptx-sm_89/python_numba/1178/output.log`). numba-cuda 0.30.4 renames them to module-level helpers `multiple_hcmp_N`, with no `test_` prefix, so pytest does not collect them. The collected methods `test_multiple_float16_comparisons` in `TestCudaIntrinsic` and `TestOperatorModule` still call the helpers, and both pass on the real GPU (`real-run-4.log`, lines 1140 and 1647). The T65 failures were collection artefacts, not kernel results. Discovery does not follow the package's own `pytest.ini`: the numba-cuda wheel ships none, and the command passes `-c conformance/interpreters/python/pytest.ini`. The rename, not the config, removed the ids. The `filecheck==1.0.6` pin stays, because `numba_cuda/numba/cuda/testing.py:25` imports `filecheck` on Python 3.10 and later.
    - Collection errors (14 before the pins): pinned numpy 2.4.6, cffi 2.1.1 and ml-dtypes 0.7.0. numpy 2.5.1 was tried and failed collection: `npdatetime_helpers.py:31` raises a DeprecationWarning for `np.timedelta64("nat")`, and `pytest.ini` `filterwarnings = error` makes it a collection error (`real-run-3.log`). The pin stays at 2.4.6 with that reason in `requirements-numba.txt`. pytest 9.0.1 is kept.
    - Caching: numba 0.68.x stamps cache index files with a sha256 of the source; numba-cuda 0.30.4 stamps `(st_mtime, st_size)`. CPU and CUDA entries share one `.nbi`, so each side discards the other's index. Pinned numba 0.67.0 with llvmlite 0.49.0, the newest release whose stamp matches.
  - Skips (63): benchmarks needing torch or cupy, multi-GPU cases, test binaries not built (`NUMBA_CUDA_TEST_BIN_DIR` unset), nvdisasm, upstream Issue #317 (seven `test_streams` cases), and runtime-version gates. Seven cooperative-groups cases, five doc examples, and `test_cffi.py::TestCFFI::test_from_buffer` that T65 skipped now pass. The likely cause is the static `libcudadevrt.a`, present in the T71 venv and not found under the lead's interpreter. This is not confirmed.
- Run J, emulated `cuda-ptx/sm_89`, `CONFORMANCE_JOB_COUNT=2`, on the final pins (numpy 2.4.6 and pytest 9.0.1, key `numba-dccec4165817b571`; `/var/tmp/gs/t71/emulate-4.log`, line 3958: `conformance: 1934 passed (0 completion only), 63 skipped, 65 failed (0 time limit), 0 not run of 2062 cases in 4789.7 s`). Run 3 on key `numba-7262f6e75c95be89` (numpy 2.4.6 and pytest 8.4.2; `/var/tmp/gs/t71/emulate-3.log`, line 2267: 83 failures) is superseded. The same route on the T65 interpreter gives `/var/tmp/gs/t65/emulate.log`, line 3017: `conformance: 1051 passed (0 completion only), 37 skipped, 434 failed (0 time limit), 0 not run of 1522 cases in 1394.6 s`.
  - Like-for-like on the 1439 shared ids: 417 fail to pass, 948 pass to pass, 26 pass to fail, 7 fail to fail, 9 pass to skip, 8 skip to fail, 5 skip to pass, and 19 skip to skip.
  - The 65 failing ids on T71 are: 24 absent from T65, 7 failing there, 26 passing there, and 8 skipped there. The 34 that passed or skipped on T65 are new emulator-side failures. Classification: 48 `emulator defect` (a `CUDA_ERROR_*` code in the output) and 17 `unclassified`.
  - Against run 3, 19 failures are gone and one is new: `cudadrv/test_managed_alloc.py::TestManagedAlloc::test_managed_array_attach_global` (`AssertionError: False is not true`). Run 3 used pytest 8.4.2, so the two runs differ in pins as well as in the emulator; the cause of the change is not established.
  - All 65 pass on the real RTX 4060 Ti in Run I (`/var/tmp/gs/t71/real-run-4.log`), and so do the 19 ids that dropped out. A pass on real hardware with the same pins points at the emulator, but no root cause is confirmed. Each failure's first error line is listed in `/var/tmp/gs/t71/report.md` for ticketing.

### 3. Julia

- `JuliaStream.jl` on CUDA, AMDGPU, oneAPI, and KernelAbstractions, compared with the C++ models.
- Bounded subsets of each package's tests.
- Completed:
  - The pinned Julia manifests under `conformance/interpreters/julia/` pin Julia 1.13.4 (`julia_version = 1.13.4`) and KernelAbstractions.jl 0.10.7. CUDA.jl 6.5.1 and CUPTI.jl 6.5.1 are pinned in `cuda/tests/Manifest.toml` and `cuda/babelstream/Manifest.toml`, AMDGPU.jl 2.11.0 in `amdgpu/tests/Manifest.toml`, and oneAPI.jl 2.10.0 in `oneapi/tests/Manifest.toml`.

#### CUDA.jl

Runs A and C, emulated, `cuda-ptx/sm_89` and `cuda-ptx/sm_89/clang`:

- Run A: `core/initialization` and `core/cudadrv` fail on both routes at start with `CUPTI.CUPTIError(code=CUPTI.CUptiResult(0x00000001))` (`cuda-ptx-sm_89/julia_tests_cuda/1/output.log` and `/0/output.log`).
- Run C: all four `core` cases fail on both routes with the same error: `core/array` (`julia_tests_cuda/0`), `core/broadcast` (`/1`), `core/cudadrv` (`/2`), and `core/initialization` (`/3`). Each stops at its first CUPTI call and prints no testset result line, so no per-case pass count exists (Run C `output.log` files).
- Run E: the same four `core` cases fail on both routes, each labelled `unclassified`: the failure text carries a `CUPTI.CUPTIError` and no Gemstone error code or known upstream mismatch (`/var/tmp/gs/t50/emulate-cuda-final2.log`, lines 2475-2478 and 2486-2489). The cause was the CUPTI export-table gap described below, since closed.
- Run H, emulated `cuda-ptx/sm_89` only, after the export tables above (`/var/tmp/gs/t56/emulate-core2.log`, lines 3109-3122: `conformance: 0 passed (0 completion only), 0 skipped, 4 failed (0 time limit), 0 not run of 4 cases in 82.3 s`). No case reaches a CUPTI error. The first errors:
  - `core/array` (43.5 s) and `core/broadcast` (69.1 s): `CUDA error: operation not supported (code 801, ERROR_NOT_SUPPORTED)` from `cuDeviceGraphMemTrim`, called by `trim_pools` in `reclaim.jl:140` (`/var/tmp/gs/t56/emu-out/case0.log` and `case1.log`, lines 78-90).
  - `core/cudadrv` (65.6 s): 205 pass, 9 fail, 12 error, 1 broken of 227 (`emu-out/case2.log`, line 69). Failures include `cache_config`, `shmem_config`, and `limit` readbacks (`cudadrv.jl:200`, `:212`, `:226`), the JIT error log (`:303`), event timing (`:332-336`), and 801 errors at `:364`, `:632`, `:857`, `:1090`, `:1101`, and `:1173`, and `invalid argument` at `:564-572`.
  - `core/initialization` (82.3 s): 56 pass, 1 fail, 1 error of 58 (`emu-out/case3.log`, line 73). The subprocess initialisation check fails (`initialization.jl:29`, its output is not captured), and `stream_flags(stream())` raises 801 (`initialization.jl:310`).
  - A worktree whose path contains `cupti` (or another substring of CUDACore's `runtime_libraries` list) makes every loaded library warn `CUDA runtime library ... was loaded from a system path`. With a worktree named `lane-cupti`, 57544 bytes sat unread in the test worker's stderr pipe while the worker blocked in `uv_write`, and `core/cudadrv` and `core/initialization` hit the 1800 s limit (`/var/tmp/gs/t56/emulate-core.log`, lines 62-68). Run H used a worktree path without those substrings.
- Real RTX 4060 Ti baselines: `core/initialization` 63 of 63 pass in 29.2 s (`/var/tmp/gs/t47/cupti-baseline-init.log`); `core/cudadrv` 2216 pass and 1 broken of 2217 in 45.4 s (`/var/tmp/gs/t47/cupti-baseline-cudadrv.log`).
- `julia_babelstream_cuda/babelstream` failed on both routes in Runs A and C: `CUDA error: operation not supported (code 801)` from `cuThreadExchangeStreamCaptureMode`, reached through `relaxed_capture_mode` (`cudadrv/graph.jl:53`) (`cuda-ptx-sm_89/julia_babelstream_cuda/0/output.log` of Run A). The entry point is now implemented with the per-thread mode exchange and the capture-unsafe prohibition (`native/cuda/src/runtime.cpp`, `native/include/gemstone/graph.hpp`), with a native test (`native/cuda/test/graphs.cpp`; failing run `/var/tmp/gs/t50/native-test-red.log`, passing run `native-test-green.log`). The semantics follow probes of the real RTX 4060 Ti driver (`/var/tmp/gs/t50/probe/capture-modes-real.log`, `capture-invalidate-real.log`). In Run E the case passes on both routes (`junit.xml`: `julia_babelstream_cuda/babelstream` passed on `cuda-ptx-sm_89` and `cuda-ptx-sm_89-clang`).
- Run A's include list named `base/array` and `base/broadcast`, but the package's `--list` output names them `core/array` and `core/broadcast` (`/var/tmp/gs/t47/cuda-list-direct.log`, lines 1-2), so neither was selected. Commit `95d826fa` fixes the names, and Run C selects both.

Earlier t24 runs, not repeated on this branch. The build revision is not recorded:

- `base/array` on device `Gemstone PTX emulator`: 128 pass, 79 error, 207 total (`/var/tmp/gs/t24-ecosystem/julia-test-cuda-array.log`). The route is not recorded in the log.
- `base/broadcast` on device `Gemstone PTX emulator`: 0 pass, 25 error, 25 total (`julia-test-cuda-broadcast.log`). The route is not recorded in the log.
- `CUDAStream.jl` on the real RTX 4060 Ti: the log names the device (`/var/tmp/gs/t24-ecosystem/julia-cuda-stream.log`, line 8). It has no launch or verification line, so no BabelStream result is claimed.

CUPTI export tables, the cause of the `core` failures, now served. `cuptiGetVersion` in libcupti 2026.3.1 (API 13.4.1, the Julia depot artefact `julia-cuda-2f323917ba77a1e0`) requests two driver export tables through `cuGetExportTable` and fails with `CUPTI_ERROR_INVALID_PARAMETER` when the first request fails. Observed on the real RTX 4060 Ti driver 620.70.06 with an interposed `libcuda.so.1` that wraps every table entry and logs its calls (`/var/tmp/gs/t56/probe/shim.c`, `shim.S`):

- Table `7cb63f9b9ca48e42a484beb66e6aed7f`: first word 560 (70 words, 67 non-null entries; entries 36 and 37 are null). libcupti calls only entry 67, from `libcupti.so+0x1038e9`, as `(void **out, void **callbacks, unsigned index, unsigned count)` with `index` 8 and `count` 227. Disassembly of the driver entry (`libcuda.so.620.70.06+0x54a600`) and direct calls from `/var/tmp/gs/t56/probe/probe2.c` (`probe2-real.log`) give its contract: null `out` or `callbacks` returns 400 (`CUDA_ERROR_INVALID_HANDLE`) and leaves `*out` alone; the first call fixes the registered table and count for the process; later calls ignore their own table and count; `index < count` stores `callbacks[index]` and returns 0, otherwise stores null and returns 1.
- Table `d7bd8af8e7acca4abdbcfb1f1b3fcf33`: first word 112 (14 words, 13 non-null entries). libcupti calls only entry 3, from `libcupti.so+0xfe80e`, as `(int *state)`. Null returns 1; the state is 2 before `cuInit`, 0 after it, and 1 after driver teardown (`probe2-real.log`).
- libcupti requests `d7bd...` once more at process exit (`/var/tmp/gs/t56/real-init-exp.log`, last `EXPORT` line). The real driver then answers `CUDA_ERROR_DEINITIALIZED` (4) for both tables and leaves the output pointer untouched (`probe2-real.log`, `FINI` lines).
- Real runs of the four `core` cases with the logging driver request only these two tables for libcupti and call only those two entries: `core/initialization` 63 of 63 pass in 29.5 s, `core/cudadrv` 2216 pass and 1 broken of 2217 in 40.9 s, `core/broadcast` 46 of 46 pass in 29.0 s (`/var/tmp/gs/t56/real-init-run.log`, `real-core_cudadrv-run.log`, `real-core_broadcast-run.log`; call logs `real-*-exp.log`). `core/array` fails on the real GPU before any CUPTI use with `ArgumentError: Package ChainRulesCore not found in current path` from `test/core/array.jl:2` (`real-core_array-run.log`, line 78). The pinned test environment lacks that package.
- The real libnvidia-ml also requests table `69a28e9f24498b47b030c5da330ea4e9` (408 bytes) and calls entries 19 to 25 during those runs. Gemstone ships its own NVML, which does not request it.
- Unobserved entries of both tables stay null in Gemstone, as in `DriverMetadataInterfaceTable`. Their signatures are unknown.

Gemstone serves both tables (`native/cuda/src/runtime.cpp`: `ToolsDriverStateTable`, `ToolsCallbackRegistryTable`) with entries 3 and 67 implemented to the contracts above. The state is 0 once `cuInit` has succeeded, and 1 once the command queue is destroyed at exit. Both table requests return `CUDA_ERROR_DEINITIALIZED` after teardown. Native tests: `native/cuda/test/execution-unavailable-smoke.cpp` (entry 3 argument check, entry 67 contract), `cross-workgroup.cpp` (state 0 after `cuInit` under the emulator service), and `tools-export-tables-exit.cpp` (state 2 before `cuInit`, then status 4 and state 1 after teardown). The earlier emulator trace for the failing request is `/var/tmp/gs/t47/cupti-trace/emu-export.log`, lines 46-47.

#### AMDGPU.jl

- `device/array`: 22 of 22 pass, and `device/execution_control`: 5 of 5 pass, both on emulated `Gemstone gfx1036`. These are earlier t24 runs; the logs do not record the route (`/var/tmp/gs/t24-ecosystem/amdgpu-committed-device-array.log`, `amdgpu-committed-device-execution_control.log`).
- `hip/gfx1030` route, emulated, after the listing and shim fixes (`/var/tmp/gs/t50/emulate-hip-3.log`, run directory `run-1794833570282972333-0`): `julia_tests_amdgpu` discovers 99 cases (`/var/tmp/gs/t50/hip-list.log`). The bounded workload selects `device/array` and `device/execution_control`; both pass (27.87 s and 28.27 s), and `julia_babelstream_amdgpu/babelstream` fails in 6.56 s. Two defects hid the cases before. `--list` ran AMDGPU.jl's Enzyme setup and `cases()` did not parse the boxed test list; `package_tests.jl` now stubs `Pkg.add` for listing and parses the box and bullet forms. The shim did not set `ROCM_PATH` on the hip route, because the emulated `libamdhip64` sits on `LD_LIBRARY_PATH`, not `LD_PRELOAD`, so AMDGPU.jl loaded the host vendor HIP and reported `hipErrorNoDevice`; `shim-julia.sh.in` now derives it from `LD_LIBRARY_PATH`. Earlier t47 notes on the `ROCR_VISIBLE_DEVICES` and `HIP_VISIBLE_DEVICES` selectors apply to the listing branch only.
- `AMDGPUStream.jl` on the hip route fails with `Device 1 out of range (1..0)` (emulated: `run-1794833570282972333-0/hip-gfx1030/julia_babelstream_amdgpu/0/output.log`; earlier `/var/tmp/gs/t24-ecosystem/amdgpu-stream-2.log`, and without gemstone `amdgpu-stream-native.log`, line 1). Cause: the pinned BabelStream source enumerates devices with `AMDGPU.get_agents(:gpu)` inside a `try`, and AMDGPU.jl 2.11.0 defines no `get_agents` (no match in `packages/AMDGPU/xsnzc/src` of the depot `julia-amdgpu-2842773f81864b0b`), so the error is swallowed and the device list is empty. Classification: package mismatch, not an emulator defect.
- The C++ HIP BabelStream on `Gemstone gfx1030` passes (`/var/tmp/gs/t24-ecosystem/hip-babelstream-cpp2.log`, earlier t24 run).

#### oneAPI.jl

Runs B and D, emulated `levelzero` route:

- `level-zero` testset (`levelzero/julia_tests_oneapi/2/output.log`): Run B had 50 pass, 1 error, of 51. The error is `ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE` at `level-zero.jl:377`. It did not recur in Run D. There, attempt 1 failed at 8.08 s (line 34) and attempt 2 passed 50 of 50 in 33.7 s (lines 36-40). The first-attempt cause is not captured, so the case is classified flaky and passed on retry. Real Intel Arc A750: 50 of 50 pass (`/var/tmp/gs/t24-ecosystem/oneapi-case-level-zero-real.log`).
- `array` testset (`levelzero/julia_tests_oneapi/0/output.log`): 53 pass, 36 error, of 89, in Run B and again in Run D. All 36 errors are `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`. The status comes from the SPIR-V virtual machine: its trace for the failing kernel `gpu_mapreduce_1d_kernel_` prints `Gemstone Level Zero: composite insertion requires scalar data` (`/var/tmp/gs/t24-ecosystem/oneapi-case-array-trace.log`, line 71), raised as `Fault.Unsupported` at `isa/spirv-vm/src/main/scala/net/kurobako/gemstone/isa/spirv/InvocationMachine.scala:920`, where the inserted value or the composite has no scalar lanes (pointer-valued composites). That trace is from a t24 run, not Run D, whose logs do not print it. This is a missing machine feature, not a package limitation: the same testset passes 244 of 244 on the real Arc A750 (`/var/tmp/gs/t24-ecosystem/oneapi-case-array-real.log`). Construct and extract on pointer-valued composites reject the same way, so a fix needs a new representation in the machine and is not made here.
- `execution` testset (`levelzero/julia_tests_oneapi/1/output.log`): 35 pass, 1 fail, 32 error, 1 broken, of 69. All 32 errors are `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`; the source has not been traced for this testset and is inferred to be the same fault as `array`. The failure at `execution.jl:237` (`"" == "2"`, from `oneAPI.@print`) is inferred to be device printf output not forwarded. No trace confirms it.
- Run B reported all four cases failed (`/var/tmp/gs/t47/emulate-levelzero.log`, lines 361-365). Run D supersedes it: 1 passed and 3 failed (`/var/tmp/gs/t47/emulate-levelzero-3.log`, lines 50-53). The failure-marker drop that made the intermediate run select one case is recorded in the report.
- CI run 43820128008 (main `d1fcdea9`), shard `sycl-0`, emulated `sycl` route (`/var/tmp/gs/land/ci-conformancesycl-0.log`, line 6387: `4260 passed (0 completion only), 30 skipped, 50 failed (0 time limit), 0 not run of 4340 cases in 3511.5 s`). The 50 failures are one `julia_babelstream_oneapi` case and 49 `julia_tests_oneapi` cases, all labelled `emulator defect` by the old default. Read from the shard's report artefact (`/var/tmp/gs/t50/ci-sycl`, the `output.log` of each case):
  - 42 cases print `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE`: `array`, `execution`, `gpuarrays/*` (accumulate, base, broadcasting, `ext/jld2`, findall, indexing, linalg, math, random, reductions, reverse, sorting, sparse, statistics, uniformscaling, unions), `indexing`, `onemkl`, and `sorting`. Four of them were rerun locally with `GEMSTONE_TRACE_LIST=levelzero` (Run G: `sorting`, `gpuarrays/random`, `gpuarrays/reverse`, `gpuarrays/reductions/sum prod`); each logs `Gemstone Level Zero: composite insertion requires scalar data` next to the status (`/var/tmp/gs/t50/ze-trace.log`), the known pointer-valued composite fault. The other 38 share the status but were not traced individually.
  - 5 cases fail before any kernel with `ArgumentError: Package X not found in current path`: `device/intrinsics` (UnsafeAtomics), `fft` (AbstractFFTs), and `kernelabstractions`, `kernelinterface`, and `unions` (KernelInterface). The test workers' project lacks these packages. This is new, not an emulator defect, and is not yet fixed.
  - `level-zero`: `ZE_RESULT_ERROR_HANDLE_OBJECT_IN_USE` at `level-zero.jl:377`, the same error as Run B. It is flaky (Run D passed on retry) and has no cause established.
  - `profile`: PTI logs `Level-Zero Introspection API is not present. Local Collection not possible`, then the testset errors. New; Gemstone's Level Zero driver does not expose the tracing layer. Not traced further.
  - `babelstream`: `@LocalMemory not defined`, the known package mismatch.
- `oneAPIStream.jl` fails at load with `UndefVarError: @LocalMemory not defined in Main` (`levelzero/julia_babelstream_oneapi/0/output.log`). The same error appears without gemstone (`/var/tmp/gs/t24-ecosystem/oneapi-stream-native.log`, line 3), so the emulator does not cause it. A grep of the depot's packages (`target/conformance/interpreters/julia-oneapi-9151e77dcb2fe024/packages/`) finds no `macro LocalMemory` and no `@LocalMemory`, so this is a test-artefact mismatch, not an emulator defect. Its junit label reads `emulator defect`, the default `classify` returns when no keyword matches (`conformance/interpreters/julia/package_tests.jl:5-12`). That label is wrong for this case.
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
- Device discovery: a failed case drops out of the next discovery, because `cases()` ignores the failure-marker bullets. The `hip/gfx1030` route now discovers 99 AMDGPU.jl cases (Step 3). The `levelzero` route, on its rerun (Run D), passes one of four cases on retry and fails three: `array` and `execution` hit `ZE_RESULT_ERROR_UNSUPPORTED_FEATURE` from a machine fault on pointer-valued composites, and `oneAPIStream.jl` hits a test-artefact mismatch (Step 3).
- Discovery failures: a program whose listing fails is now one failed case named `discovery` with the listing output and its classification, and every other program in the target still runs (CI run 43820128008 lost 16 of 23 shards to one program's failed listing, for example `conformance: test discovery failed for cuda-sass-sm_86/julia_tests_cuda (exit 1)`, `/var/tmp/gs/land/ci-conformancecuda-3.log`, line 1310).
- Listing without a host driver: the Julia listing now runs under the route's emulated driver and clears only the device selectors, because CUDA.jl's `setup.jl` asserts `CUDA.functional(true)` and its list depends on the device. CI failed with `CUDA driver not found` since the listing had cleared the emulated driver. `listing.jl` stubs `Pkg.add` and the print-only `CUDA.versioninfo`, which hits the CUPTI gap above. Proof: a Fedora 45 container with no `/dev/nvidia*`, `/dev/kfd`, or `/dev/dri` and no driver libraries in `ldconfig -p` ran `emulate --dry-run` for `cuda-ptx/sm_89`, `hip/gfx1030`, and `levelzero` and listed `julia_tests_cuda` 4, `julia_tests_amdgpu` 2, and `julia_tests_oneapi` 3 cases (`/var/tmp/gs/t50/container-dry.log`, exit 0).

## PTX on the `cuda-sass` routes

- Programs that hand the driver PTX at run time (Numba through `cuLinkAddData` and `cuLinkComplete`, CUDA.jl through `cuModuleLoadDataEx`) failed with `CUDA_ERROR_NOT_SUPPORTED` on every SASS route, because the strict mode refused text PTX. A real driver compiles the PTX for its GPU.
- Gemstone now compiles the PTX that a module load or link supplies to a cubin for the route's architecture with `libnvJitLink` (`nvJitLinkAddData` with PTX inputs and `-arch=sm_NN`, then `nvJitLinkGetLinkedCubin`), and executes the SASS. Several link inputs go to one link, so extern functions resolve across them. Embedded fatbin PTX is unchanged: the fatbin's cubin runs.
- Why `libnvJitLink`: the CUDA toolkit EULA's Attachment A lists `libnvJitLink.so` and `libnvJitLink_static.a` as redistributable ("NVIDIA JIT Linking Library") and names neither `ptxas` nor `nvlink`. The 13.3 CUDA support closure already ships `libnvJitLink`, so nothing new enters the artifact.
- `GEMSTONE_CUDA_NVJITLINK_LIBRARY_FILE` names the library. The `sbt` launcher derives it from `lib/libnvJitLink.so.<major>` in the artifact's support closure.
- No library: `CUDA_ERROR_JIT_COMPILER_NOT_FOUND`. `nvJitLinkCreate` rejecting the architecture (`NVJITLINK_ERROR_UNSUPPORTED_ARCH`, which 13.3 returns for `sm_70` and below): `CUDA_ERROR_NO_BINARY_FOR_GPU`. PTX newer than the library (`Unsupported .version` in the error log): `CUDA_ERROR_UNSUPPORTED_PTX_VERSION`. Any other failure: `CUDA_ERROR_INVALID_PTX`. The library's error log goes to stderr.
- The 11.8 support closure (`sm_35` to `sm_70` routes) has no `libnvJitLink`, which CUDA 12 introduced, so those routes report `CUDA_ERROR_JIT_COMPILER_NOT_FOUND` for run-time PTX. The 13.x library cannot target them either.
- Open: Numba's NVVM 13.3 rejects `-arch=compute_50`, so libdevice cases that target `compute_50` fail before any driver call on both code modes.

## Non-goals

- Replacing or patching any package or vendor library.
- Performance numbers from these runs.
- JAX, TensorFlow, and other frameworks until these pass.

## Open decisions

- Where environments build. Decided: on the host at run time, keyed by the lock file. The Julia runs executed on Fedora 45, not in a container.
- Whether the first Julia target is CUDA.jl or AMDGPU.jl. Recommendation: CUDA.jl, since it goes through the same PTX path as Numba, then AMDGPU.jl and oneAPI.jl.
