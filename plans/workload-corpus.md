# Workload corpus

Status: in progress. Step 1 and T42 are done. The bounded workload now meets the 300 s headroom budget (183.6 s total in the default-concurrency run). Step 2's harness is fixed: the manifest now comes from the Makefile run targets and the runtime data tree, the hardware rerun classifies 478 of 535 programs as pass or completion, and the emulation rerun of those 478 is partial (shards 0 to 3). Step 3 (HIP, SYCL, OpenMP) not started. Triage uses `concurrency-checking.md` to separate races from emulator defects.

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

Triage of the first failing benchmarks on the cuda sm_89 vendor artifact, compared against the RTX 4060 Ti on this host:

- `bitpacking`: emulator defect in the CUDA fatbin loader. The `-rdc` device-link wrapper registers its linked cubin before the per-object metadata stubs; a stub at the same target replaced the linked image, so the module had no kernels and every launch failed with `CUDA_ERROR_NOT_FOUND`. The benchmark ignored the failed launches and printed FAIL for every size. The loader now keeps the first executable cubin at a target over same-target metadata stubs (`cfbdf318`, failing test in `native/cuda/test/code-mode.cpp`). With the compress loop at eight iterations, all six shipped sizes pass on `cuda-sass/sm_89` in 479.8 s and on PTX; the shipped 1000 iterations over 100M elements exceed the 600 s case timeout on `cuda-sass/sm_89`.
- `chemv` and `winograd`: the same loader defect. Both pass on hardware and, after the fix, on `cuda-sass/sm_89` (86 s and 29 s) and on PTX.
- `mdh`: the same loader defect. The benchmark's shipped inputs make its check vacuous: `gendata` sets `size[iatom]` to `natom` (5877) and `xkappa` is 0.0736, so `-xkappa * (dist - size[iatom])` is about +432 for every atom pair. `expf` overflows to `+inf` on both CPU and GPU, `fabsf(inf - inf) > 1e-3f` is false, and the program prints PASS without comparing a finite value. With `-itmax 1` and the default 1e11 instruction budget the first kernel is cut off after 61 s and the later launches fail, so the case prints FAIL in 64 s. A probe with a 1e12 budget reported 333-406 s per kernel on SASS, which exceeds the 600 s case limit for three kernels, and 20-38 s on PTX. The default `itmax 100` is dominated by the benchmark's serial CPU reference (6.4 s per iteration), which exceeds the case timeout on hardware too. No argument changes the hardcoded inputs, so the check cannot be made meaningful this way; mdh is not counted as verified and is not in the bounded workload.
- `laplace`: the same loader defect, reported as unsupported cubin text relocation `0x4a` because the per-object relocatable image was selected instead of the linked cubin. A reduced `NUM 256` build passes on both routes with the same 3185 iterations and norms as hardware (SASS 299.9 s, PTX 168 s). The shipped `NUM 1024` default needs 27,250 GPU iterations at 16 times the grid size, and its serial CPU reference alone exceeds 15 minutes on this host, so it is a time limit.

Harness correction and reclassification:

The first classification called 369 programs benchmark failures because the artifact manifest only carried the 144 `benchmarks.yaml` `test.args`. The per-benchmark Makefile `run:` targets were ignored, so 550 programs ran with no arguments (for example `adamw` and `atan2` printed their usage lines), and every case ran in its report directory, so relative data paths such as `../data/bfs/graph1MW_6.txt` did not resolve. That was a harness defect, not a benchmark failure, and it invalidated the emulator comparison for those programs.

- The artifact now generates its manifest from the pinned source: `make -n run` expands each benchmark's `run:` target, including make variables and line continuations. The `benchmarks.yaml` arguments override the run target when present, preserving the smaller upstream test configuration. Programs with no run target or with shell syntax in it are listed in `hecbench-unparsed-runs.txt`.
- The files the run target and the source literals open relative to the benchmark directory are copied into a source-shaped tree under `data/hecbench/tree/`, and the driver runs each case with that tree as its working directory. Paths that do not exist are listed in `hecbench-missing-data.txt`; most are outputs, and the remainder are sibling-model data (for example `urng-sycl`, `sobel-sycl`) or data HeCBench does not ship (`daphne`, `resnet-kernels`, `tpacf`).
- The generated tree is 1.1 GiB. The support artifact grows by that amount.
- Test-first: `conformance_hecbench` now builds synthetic Makefiles with a fixed argument list, a make variable, a missing data file, a shell target, and a yaml override, and checks the manifest, the copied data, and the two reports. The build also fails if a program whose run target returns arguments ends up with an empty manifest entry.
- The driver test runs a case in a configured directory and checks its reported working directory as well as its arguments.

Hardware rerun with the corrected manifest on the RTX 4060 Ti (535 programs; `cmp` and `si` are triaged separately):

| Result | Cases |
| --- | --- |
| Pass | 289 |
| Completion only | 189 |
| Fail | 48 |
| Time limit | 9 |

That replaces the earlier 369 benchmark failures. 478 programs pass or complete on hardware. The 48 failures are mostly missing data (`aes`, `bicgstab`, `daphne`, `deredundancy`, `egs`, `leukocyte`, `lr`, `mace`, `minibude`, `minimap2`, `mpc`, `sobel`, `ss`, `tgvnn`, `tonemapping`, `urng`, `word2vec`) and programs whose own check fails on this GPU (`assert`, `attention-paged`, `blockScan`, `channelShuffle`, `flip`, `nbody`, `storeKVCache`, `topk`, and others). `si` exceeds 32 GiB of host memory and is not run; `cmp` hangs for 600 s.

Emulation rerun with the corrected manifest:

The 478 hardware pass/completion programs were split into 48 interleaved shards. Shards 0 to 3 are complete (38 cases); the rest are not rerun. Of the 38, 5 pass, 6 complete, 6 fail, and 21 hit the 600 s limit. The time-limit fraction is too high to finish the full rerun in the time available: the emulator is 20 to 200 times slower than the RTX 4060 Ti, and the Makefile run targets are sized for hardware. The old emulator classifications for the other 440 programs are invalid because they ran without arguments.

New or confirmed emulator defects in the 38 rerun cases, compared with hardware:

- `abacus` still fails with `operation not supported`; the reduced `abacus 4 1 4 0.04 1` case reproduces it in 2.3 s.
- `blas-dot`, `fsm`, `sddmm-batch`, and `sdma` fail under emulation but pass on hardware. `streamcluster` fails under emulation and completes on hardware.
- The first classification's defect list was built on the broken harness and is superseded by this list until the full emulation rerun is done.

Argument changes and measured times:

- `bitpacking`: 1000 to 3 compress iterations, 179.5 s in the default-concurrency bounded run and 199.2 s with two jobs.
- `laplace`: `NUM 1024` to 256 and tolerance 1e-6 to 1e-4, 733 iterations, 183.6 s in the default-concurrency bounded run and 206.2 s with two jobs.
- `minimod`: no arguments, overriding the Makefile run target, 4.4 s completion.
- `softmax`: `repeat` 100 to 32, keeping the shipped slice count and size, 439.9 s. The shipped arguments exceed 600 s under emulation and pass on hardware.
- `mdh`: not bounded; see above.
- The time limits in the 38 rerun cases keep their shipped arguments in this pass. Shrinking them needs a per-program measured search; the current set is dominated by the large neural, BLAS, and stencil kernels.
- The `cuda-ptx` artifact does not carry HeCBench because `BUILD_HECBENCH` is fatbin-only, so the PTX comparison compiled the same sources with `-gencode arch=compute_89,code=compute_89` and ran them through `GEMSTONE_CUDA_CODE_MODE=ptx`.

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

Ticket T42 (bounded arguments):

- Rule: every `hecbench-bounded` case must finish within half the 600 s case limit (300 s) in the measured bounded run, so a loaded machine and CI retain headroom. The default 16-job run on 2026-11-17 took 183.6 s in total; the 2-job run took 225.1 s.
- Default test arguments from `benchmarks.yaml` now override the per-benchmark Makefile `run:` target (144 programs have yaml arguments). The artifact generates `data/hecbench/hecbench-arguments.json` and `data/hecbench/hecbench-directories.json` after the build, and installs the source-shaped data tree the run targets need. The conformance driver merges workload `program_arguments` over the generated arguments and runs each program in its data directory.
- `bitpacking`: `main()` takes no command-line arguments. Shipped `kernels.cu` runs 1000 compress iterations. The staging patch runs 3 iterations, measured at 179.5 s in the default-concurrency bounded run and 199.2 s with two jobs, passing all six sizes.
- `laplace`: `main(void)` takes no command-line arguments. Shipped `NUM 1024` needs 27,250 GPU iterations and its serial CPU reference exceeds 15 minutes. The staging patch uses `NUM 256` and lowers the convergence tolerance from 1e-6 to 1e-4, giving 733 iterations and 183.6 s in the default-concurrency bounded run (206.2 s with two jobs), passing with the same GPU and CPU norms. `NUM 128` is not valid: `BLOCK_SIZE` is 128, so the grid dimension becomes zero and the benchmark prints PASS after one empty iteration.
- `minimod`: the Makefile run target (`--grid 256 --nsteps 1000`) exceeds 300 s under emulation. The bounded workload overrides it with no arguments and the program's default case, which completes in 4.4 s; the case has no pass or fail marker.
- `mdh`: `-itmax 1` is the smallest argument and brings the GPU work within reach, but the process still runs the serial CPU reference and three kernels, so it exceeds 600 s once each kernel is allowed to complete. At the default 1e11 instruction budget the first kernel is cut off after 61 s and the later launches fail, and the case prints FAIL in 64 s. Its inputs are hardcoded (`size[iatom]` is `natom` 5877 and `xkappa` is 0.0736), so `-xkappa * (dist - size[iatom])` is about +432 for every pair; `expf` overflows to `+inf` on both sides and the comparator's NaN test prints PASS without checking a value. No command-line input changes that, and the benchmark source is not patched to hide it. `mdh` is therefore not in the bounded workload and is not counted as a verified pass.

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
