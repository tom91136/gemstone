# Workload corpus

Status: in progress. Step 1 is done for the CUDA nvcc 13.3 fatbin route. Its build and first runs do not wait for other plans. Triage uses `concurrency-checking.md` to separate races from emulator defects.

## Goal

Add HeCBench as a conformance source: a large set of real kernels in CUDA, HIP, SYCL, and OpenMP. It widens conformance coverage, and gives `static-pipeline.md` and `rewrite-search.md` hundreds of real kernels to analyse instead of a few benchmark cases.

## Current state

- `conformance/miniapps` builds BabelStream, miniBUDE, CloverLeaf, TeaLeaf, ggml, and an ImGui scene through `conformance_miniapp`, with per-model patches and a `miniapps` workload.
- HeCBench (`zjin-lcf/HeCBench`, BSD-3-Clause) has 567 benchmarks: 566 in CUDA, 563 in HIP, 554 in SYCL, and 367 in OpenMP. It includes its own ports of miniBUDE, BabelStream, LULESH, XSBench, RSBench, and miniWeather in all four models.
- Its `benchmarks.yaml` gives each benchmark's categories, models, test arguments, timeout, and an output pattern. For many benchmarks the pattern matches a timing line, so it shows that a run finished, not that its result is correct.
- Some benchmarks take input data through DVC from an S3 bucket that allows anonymous reads.

## Design

- One pinned HeCBench commit, built through its CMake per model with the conformance compilers: CUDA through `nvcc` and Clang, HIP through `hipcc`, SYCL through `icpx`, and OpenMP through the offload routes.
- Correctness comes from each benchmark's own output. A case passes when the output reports a pass and no failure. A benchmark that verifies nothing counts as a completion case and is reported apart. The split is derived from the output on every run, not kept as a list.
- HeCBench maintains its miniBUDE and BabelStream ports apart from the upstream programs in `conformance/miniapps`. Both stay: the upstream programs cover more models, and the HeCBench copies are separate code to test.
- Workloads follow the existing pattern: a bounded `hecbench-bounded` for CI and a full `hecbench`. Sharding uses the existing regex partition policy.
- The corpus admits permissive and copyleft benchmarks. Each benchmark family's licence text is classified as permissive, copyleft, non-commercial, proprietary, or unknown. Non-commercial, proprietary, and unknown families are left out; unknown families are listed in the generated report for manual classification. The classification is generated from the licence files, not kept as a list of names.

## Steps

### 1. Build

Done for the CUDA nvcc 13.3 fatbin route at sm_89.

- HeCBench is pinned at `4426c9fbdbb5e54b3776bf04a72774f8da4973b3` by SHA256 in `conformance/dependencies.cmake`. The 53 DVC archives that the CUDA benchmarks need are derived from the `.dvc` pointers at that commit (MD5, size, and path) and pinned by MD5 and size; 919,870,463 bytes compressed. cxxopts 3.3.1 and the fmt 7.1.2 source that `src/si-cuda` fetches are pinned by SHA256, so configure needs neither git nor network.
- `conformance/hecbench` stages the pinned source, places the build-input archives, classifies every CUDA benchmark family by its licence text, and configures and builds the admitted targets with `ninja -k 0` in a nested build, one target per benchmark. The outer artifact carries only the binaries that built, plus a generated failure list with the first compiler error for each.
- Every model and the other routes remain for later steps.

Build results at this commit:

- 559 CUDA benchmark directories are listed, 555 have a permissive or copyleft licence, and 554 are selected. 537 built.
- 17 selected benchmarks did not build: diamond (`zlib.h` missing), heun (`cudnn.h` missing), minmax (`thrust::distance` is absent under CUDA 13.3), and 14 that configure skips because the image lacks their dependency: allreduce, ccl, halo-finder, miniWeather, moe-combine, pingpong, and sparkler (MPI); ge-spmm, gerbil, hbc, mmcsf, and warpsort (Boost); stsg (GDAL); xlqc (Eigen).
- axhelm is not selected because the CUDA 13.3 image has no Fortran compiler; its BlasLapack archive is not staged. It is listed separately in the failure report.
- Four families are left out by licence: cm (CC-BY-NC-3.0), vanGenuchten (Dhara non-commercial), complex (NVIDIA proprietary), and wedford (unknown, its licence file is only an apex URL). The generated classification over the 559 CUDA directories is 526 permissive, 29 copyleft, 2 non-commercial, 1 proprietary, and 1 unknown.

### 2. Run

- Run the CUDA models first with the arguments from `benchmarks.yaml`, under the bounded time limit, and classify each result as pass, fail, completion only, or time limit.
- Benchmarks sized for real GPUs that exceed the limit get smaller arguments, recorded next to the measured time that justified each change.

### 3. Other models

- HIP, SYCL, and OpenMP, in that order.

### 4. Kernel corpus

- Feed the compiled kernels to `BenchmarkRunner analyse` and the hardware validation loops, and report kernels the static pipeline model cannot handle.

### 5. Seeded runs

- Run the corpus under seeded schedules once `concurrency-checking.md` step 2 exists, and triage results that change with the seed.

## Checks

- Test first, seen failing, for behaviour changes in gemstone.
- Every emulator defect found gets a reduced case in gemstone's own suites.
- Failures stay red.

## Findings

Reconnaissance at HeCBench `4426c9fbdbb5e54b3776bf04a72774f8da4973b3`:

- Input data: 57 benchmarks need DVC archives, 1.72 GB compressed in total (CUDA, HIP, and SYCL 1.72 GB, OpenMP 1.21 GB). The largest are diamond (658 MB), mpc (275 MB), and rsmt (194 MB). Some archives hold headers, so they are build inputs, not only run inputs. Seven benchmarks download data outside DVC, ge-spmm the largest at over 600 MB.
- Build: the CMake configures every benchmark at once, and each `<name>-<model>` target builds alone. The CUDA model builds in 194 s on 8 cores, 538 of 544 targets. diamond and heun miss `zlib.h` and `cudnn.h`, minmax fails on `thrust::distance` under CUDA 13.3, and sad, seam-carving, and testSNAP need DVC headers. `src/si-cuda` fetches cxxopts and fmt with git at configure time.
- Correctness: none of the 166 output patterns in `benchmarks.yaml` checks a result; all capture a timing or throughput value, and 342 entries have no test. 276 benchmarks print PASS or FAIL themselves, so the pass and fail signal comes from the output, as the design says.
- Licences: 134 benchmarks carry a licence other than BSD-3-Clause, 30 of them copyleft. cm and vanGenuchten are non-commercial, and complex is NVIDIA proprietary.
- Run time: the yaml arguments that exist are sized for real GPUs (softmax, attention, jacobi). Several benchmarks without a yaml test need input files or arguments to run at all.

Step 1 at the same commit:

- Staging the three build-input DVC archives fixed sad, seam-carving, and testSNAP; they build. The remaining CUDA build failures are the 17 above.
- The full `hecbench` workload discovers the 537 built programs on the same artifact. The first `hecbench-bounded` run through Gemstone on `cuda-sass/sm_89` selected assert, bitpacking, minimod, and overlap: 1 verified pass, 1 completion only, 2 failed, 0 skipped, and 0 time limit in 24.8 s. overlap passes, minimod completes without a pass or fail marker, assert fails because Gemstone does not implement device-side `assert`, and bitpacking prints FAIL for every size. The run exits non-zero because failures stay red.
- A probe of larger defaults found page-rank passing in 266 s and overlap in 21 s; chemv, winograd, mdh, and laplace fail under emulation, with laplace failing its own verification after one iteration. Step 2 needs the yaml arguments, smaller inputs, or both before those can be bounded.

## Risks to confirm

- HIP, SYCL, and OpenMP build times, measured rather than extrapolated from the CUDA count.
- Run times under emulation, which decide the bounded workload's arguments.

## Non-goals

- Performance comparison with hardware.
- Fixing benchmarks upstream, beyond reporting what is found.

## Open decisions

- Whether completion-only benchmarks are in the CI workload. Recommendation: yes, since a crash or a hang is still a failure, but reported apart from verified results.
- Whether to compare results across models of the same benchmark where the benchmark does not verify itself. Recommendation: later, only for benchmarks whose output is deterministic.
- How configure-time downloads are fed. Recommendation: pin the DVC archives and the cxxopts and fmt sources in the dependency cache, as for the other conformance sources, so the build needs neither git nor network.
