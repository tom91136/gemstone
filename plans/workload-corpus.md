# Workload corpus

Status: proposed, not started. Its build and first runs do not wait for other plans. Triage uses `concurrency-checking.md` to separate races from emulator defects.

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

## Steps

### 1. Build

- Fetch and pin HeCBench, build every model on every route, and record which benchmarks fail to build and why. A build failure in the conformance compilers is reported, not patched around.
- Check the licence of each benchmark taken from another project, and record any that differ from the repository's.

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

## Risks to confirm

- HIP, SYCL, and OpenMP build times, measured rather than extrapolated from the CUDA count.
- Run times under emulation, which decide the bounded workload's arguments.

## Non-goals

- Performance comparison with hardware.
- Fixing benchmarks upstream, beyond reporting what is found.

## Open decisions

- Whether completion-only benchmarks are in the CI workload. Recommendation: yes, since a crash or a hang is still a failure, but reported apart from verified results.
- Whether to compare results across models of the same benchmark where the benchmark does not verify itself. Recommendation: later, only for benchmarks whose output is deterministic.
- Which licences the corpus admits. Recommendation: leave out non-commercial and proprietary benchmarks (cm, vanGenuchten, complex), detected from each directory's licence text rather than kept as a list, and admit copyleft ones, since the corpus builds and runs them and does not redistribute them.
- How configure-time downloads are fed. Recommendation: pin the DVC archives and the cxxopts and fmt sources in the dependency cache, as for the other conformance sources, so the build needs neither git nor network.
