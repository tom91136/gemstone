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

## Risks to confirm

- DVC data: which benchmarks need it and how large it is.
- Build time for about 2000 programs across routes. The artifact cache must key per benchmark, so one change does not rebuild all of them.
- Run time under emulation for benchmarks whose arguments assume a real GPU.

## Non-goals

- Performance comparison with hardware.
- Fixing benchmarks upstream, beyond reporting what is found.

## Open decisions

- Whether completion-only benchmarks are in the CI workload. Recommendation: yes, since a crash or a hang is still a failure, but reported apart from verified results.
- Whether to compare results across models of the same benchmark where the benchmark does not verify itself. Recommendation: later, only for benchmarks whose output is deterministic.
