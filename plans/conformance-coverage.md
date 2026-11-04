# Conformance Coverage Follow-up

Status: open. Items deferred from the vendor-suite and coverage expansion of the conformance project (tiers `short`, `long`, `xlong`, `all`; vendor test suites; PTX-only CUDA artifacts; device printf; AMD image sampling; cross-program calls; RPC heartbeats). Everything this work needs to know is in this file.

## Standing rules

- No known reds. A failure is root-caused and fixed in the emulator or a shim. Skips are only for genuine capability gaps the test itself detects, or that follow from a toolchain or device capability, never from a hand list of failing cases.
- A bug found through conformance also gets a unit or regression test in the owning module (Scala for VM bugs, native ctest for shim bugs), red before the fix and green after.
- Heavy local work runs under `systemd-run --user --scope -p MemoryMax=<cap> -p MemorySwapMax=0`. Emulation runs use `CONFORMANCE_JOB_COUNT` explicitly; the CLI default (`processors / 2`) does not account for per-case memory.

## Verification still owed

CI runs the `xlong` tier on the complete profile. Locally the merged tree passed conformance host ctest, native ctest, the Scala suites of every module, `conformance/codestyleLint`, `bonsaiAnalyse`, `lintSources` apart from 10 findings of bonsai rules that wait on bonsai tuning (CI does not load bonsai), and container builds of `hip/gfx1150`, `cuda/sm_120` and `openacc/sm_120`. Still owed:

- The remaining complete-profile container builds and the package size check (last measured 4.69 GiB with `zstd -12 --long=30`; the PTX-only artifacts add an estimated 0.17 to 0.68 GiB; limit 10G).
- The release tier `all` on emulated targets: the Level Zero CTS from about case 8896 (the earlier families are fixed, see item 1), the remaining 1 GB SVM variants (item 2), all `[release]` CUDA samples on `cuda-sass/sm_70`, `cuda-sass/sm_50` and metal `cuda-sass`.
- `cuda-ptx/sm_70` emulated.
- A wide emulated pass over SASS programs after the warp partition changes (item 13) and the legacy address rewrite (item 8).
- One report mentioned a `hip/gfx908` complete-profile failure at `conformance/artifacts/Assemble.cmake:263` (`artifacts/hip/gfx908/artifact.toml`) without a log. The earlier full build passed gfx908; check the CI build.

## Items

| # | Item | Where | Size | Status |
|---|---|---|---|---|
| 1 | Level Zero `all` tier from about case 8896 | `conformance/cts/levelzero`, `native/levelzero` | L | known families fixed; run owed |
| 2 | Level Zero 1 GB SVM case cost | `core/.../rpc/HostCoherence.scala`, `HostMemoryMirror.scala` | M | done |
| 3 | HSA queue drain bound | `native/hsa/src/runtime.cpp` | S | done |
| 4 | SPIR-V linking inside the emulator | `isa/spirv-asm` `Linkage`, Level Zero shim | M | done |
| 5 | Print text bound | `core/.../rpc/ExecutionRPC.scala` | S | done |
| 6 | gfx90a image capability in the device model | `native/hsa/include/gemstone/hsa/device_model.hpp` | S | done |
| 7 | Multiple emulated devices | emulator, all shims | L | done, with limits |
| 8 | Legacy `LEA.HI.X` auxiliary predicate | `isa/sass-vm` | M | done |
| 9 | Legacy SASS semantics without hardware evidence | `isa/sass-vm` | M | blocked on hardware |
| 10 | Explorer entry for `warpPartition` | `explorer` | XS | done |
| 11 | Device-lost and fault stickiness outside HIP | `native/levelzero`, `native/opencl` | S | done |
| 12 | Smaller items | various | XS | done; verification below |
| 13 | Labeled partition ballot on emulated SASS | `isa/nvidia`, `isa/sass-vm` | M | done |
| 14 | Known shim and test-harness limits | various | S | open |
| 15 | Workgroup skew and the cuSPARSE `find_colors_ker` race | `core/.../vm/Pacing.scala`, NVIDIA and AMDGPU interpreters | M | done |

## 1. Level Zero `all` tier

The `all` tier runs all 21323 Level Zero CTS cases (`short`, `long` and `xlong` use an allow-list). Earlier runs were OOM-killed around cases 8896 to 9417 with four 1 GB SVM cases at once under a 16G scope that also held sbt. The failure families from cases 0 to 8896 are fixed:

- `ImmediateAppend*` allocate with alignment 12. The shim now rounds the alignment to the allocation granularity, as Intel's compute-runtime does (`granularityAlignment`, `native/levelzero/src/driver.cpp`).
- `SyncTimeoutParams` overshot the 2% timeout bound by the 50 us default Linux timer slack. Timed shim waits narrow the thread's slack (`TightTimerSlack`, `waitWithin` in `native/include/gemstone/command_queue.hpp`), and the driver runs these cases with no other case on the machine (`LevelzeroExclusivePrograms`, `LevelzeroExclusiveCases` in `conformance/src/suite_policy.hpp`). 282 of 282 passed at the default job count.
- fp16 atomics: 16-bit load, store and exchange work (`AtomicOps` `memoryWidth`, `AtomicSuite`).
- `zeCommandListAppendMemoryCopyTests.GivenZeroRegionParams*` (4 cases): an empty copy region is a no-op that succeeds, as in Intel's compute-runtime; the shim rejected it.

Resume the run from about case 8896 with `CONFORMANCE_JOB_COUNT=2` and a 12G scope; root-cause each new failure family.

## 2. Level Zero 1 GB SVM case cost

Done. The host mirror fetches page windows with sequential readahead up to 1 MiB (clamped to registered allocations), is sharded by page into 64 locks, batches `prepareReads`, and keeps workgroup-or-narrower atomics on host memory in the mirror (`HostMemoryMirror.scala`, `HostCoherence.scala`, `AddressSpace.atomicModifyLocalValue`). The client replies with the readable runs of what the server requested (`readableExtent`, `native/rpc/completion.cpp`); unreadable bytes fault. `svm_to_usm_non_atomic`, `svm_to_usm_atomic` and `usm_to_svm_non_atomic` (1 GB) pass in 149 to 161 s at load 11 to 20. The other `_1GB` variants (immediate, 2KB tails, advised or prefetched, `svm_to_svm`) are part of the owed `all` run.

Speculative window bytes may be older than a later first use without an acquire or system-scope load; this is not tracked per byte.

## 7. Multiple emulated devices

`GEMSTONE_DEVICE_COUNT` (1 to 16, `native/include/gemstone/devices.hpp`) sets the device count for CUDA, HIP, HSA, Level Zero, OpenCL and Vulkan, with per-device ownership, peer access queries and identity. Level Zero honours `ZE_AFFINITY_MASK`. The conformance driver passes two devices to the cases in `LevelzeroMultiDeviceCases` and to the HIP vendor cases in `conformance_vendor_environment` (`rocFFT/multi_gpu`, `HIP-Basic/multi_gpu_data_transfer`). The ten Level Zero cases pass, except the sub-device affinity case, which skips because no sub-devices are modelled. rocFFT `multi_gpu` passes on two emulated devices; the upstream example's brick strides (`{1, 64, 8, 512}` for a 4x8x8 brick) read past each device buffer, so `conformance/vendor/hip.cmake` patches them to the compact layout, and the example's result check (also patched in) now holds.

Limits, see item 14.

## 8. Legacy `LEA.HI.X` auxiliary predicate

Done. Cache-hinted generic memory (`CV`, `WT`), MUFU, BFI, PRMT, SHF and ISET scalar forms, control-flow-target windows, call-frame proofs, the sm_50 `FSET`/`DSET` zero-flag combine, the copy-form `LEA.HI.X` predicate read by a branch guard (cuSPARSE coomm and spgemm choosing a global or shared atomic; the predicate is the same global-space test as the window form), `IMNMX.XHI`, and `FSET`/`DSET` with a condition-code register destination (`LegacyAddressRewrite.scala`, `LegacyFlowGraph.scala`, `VMIntegerCompiler.scala`, `VMFloatingCompiler.scala`; `GenericMemorySuite`, `ConditionCodeSuite`, `MinMaxSuite`, `LegacyVideoSuite`). All 3997 kernels in the 107 sm_50 cuSPARSE cubins build. The four sm_60 libcudacxx programs pass emulated.

Inferred without hardware evidence (item 9): the copy-form predicate meaning, `IMNMX.XHI` without `.CC` leaving the flags alone, and `FSET`/`DSET` register `.CC` following `ISET.CC`; the last form occurs in no shipped cubin, so its tests are synthetic. BRX indirect calls push empty frames and `RET` does not record the BRX return site; the proof stays conservative there.

A wider probe across architectures found library kernels using `TEX.LZ` with a handle outside the kernel arguments (about 1142 kernels), `CCTL.E.IV` (322), `SGXT.U32` (108) and `SR_SWINLO` (44). None of them is known to be reached by a program the conformance suite runs.

## 9. Legacy SASS semantics

`ISETP`/`CSETP.NEU` after `ISET.CC` and `IADD3.CC` follow inferred semantics without hardware evidence; the sm_35 trap-mode listing in the texture fixtures has an unexplained instruction. Hardware also serialises exclusive WARPSYNC groups where the VM interleaves them. Confirm on sm_35 to sm_70 hardware when available.

## 12. Smaller items

Done: `conformance/vendor/hip/cases.cmake` holds the case loop; `matchesDft` is O(N log N) (radix-2 and Bluestein) with an error bound that scales with `log2 N`; ptx-vm tests use legal `sured` forms; `conformance/vendor/hip/offload_targets.cmake` replaces the Python target scan; helper-level negative checks in `conformance/tests/reference_test.cpp`. HIP launch-time kernel build failures keep returning an error from the launch; only execution faults abort the process.

Verified: rocFFT `callback` passes emulated on gfx1030 and gfx90a. With a perturbed callback it fails on emulated and metal gfx1150; a perturbed rocFFT output fails `matchesDft` on metal; unperturbed, both pass on metal. libcudacxx `group_memcpy_async_16b` timed out at 400 s on `cuda-sass/sm_75` and `sm_120` because SASS kernel compilation scanned the instruction list per lookup (`VMCompiler`); with indexed lookups `sm_120` passes in 23 s and `sm_75` in 28 s. `sameMean` took its standard error from the device variance, so a single outlier widened its own threshold; it now uses the host variance, as the null hypothesis allows. `matchesDft` prints the observed error and its bound, and a NaN output fails. On metal gfx1150 the worst correct output, `rocFFT/callback`, sits at 0.094 of the bound without the factor 64; `complex_complex` at 0.019; `complex_real` and `real_complex` are exact (one run each). Cold-cache PTX JIT of the clang unified module takes about 12 minutes on the metal RTX 5070; the driver's JIT warm-up absorbs it (718 s against its 7200 s bound), after which the 1005 timed `cuda-ptx-clang` unified cases reuse the 341 MB cache and finish in 437 s, the slowest in 10.6 s.

## 14. Known shim and test-harness limits

- An 8- or 16-bit host atomic whose aligned 32-bit word extends past its allocation faults.
- Peer access is enforced at the memory access only for HSA and hsakmt allocations: each launch names its device and the session faults an access outside the allocation's device set, which the HSA shim aborts like a ROCm page fault. CUDA, OpenCL, Level Zero, and Vulkan allocations stay open to every device, and `hipDeviceDisablePeerAccess` does not revoke a grant.

## 15. Workgroup skew and the cuSPARSE `find_colors_ker` race

Emulated cuSPARSE `cg` and `bicgstab` hang under host load on every SASS generation. `cusparse::find_colors_ker` (SpSV analysis) computes levels one at a time: each level ends with a grid barrier (`RED` plus an `LDG.E.CV` spin), then thread 0 of every block reads the shared queue count and stores it to `levels[l]`, then blocks append rows for the next level. If one block reads the count after another block has started appending, it stores an inflated count, rows drop out of every later level, the count never reaches the row count, and the barrier counters run off their array.

This is a data race under the CUDA memory model: nothing orders the late block's read against the other block's append. It relies on an implicit property of NVIDIA hardware, where all blocks of a launch run at near-equal rates and leave a grid barrier within about a microsecond, so the window is never hit. The emulator runs the workgroups of a non-cooperative launch on pool threads that the host can deschedule for milliseconds, which exposes it.

The emulator matches the hardware property instead of working around the library: once a launch polls global memory (a warp re-reads the same global address with an unchanged value across yields, `SpinWatch`), running workgroups stay within a small step bound of the slowest running one (`Pacing`). Unstarted, finished and host-blocked workgroups do not hold others back. `GridPacingSuite` reproduces the level pattern with one workgroup held after the barrier; it fails without pacing. Engaged launches cost about 20% more; others are unaffected. SPIR-V is not wired: its interpreter runs invocations one at a time in long slices and has no polling detector.

Two other defects showed up on the same samples. Legacy SASS read-only polling loops never yielded (`LegacyFlowGraph.pollingBranches`, `LegacySpinSuite`). `EXIT` ignored its predicate operand, so `@P0 EXIT P2` in CUDA 13.3 cuSPARSE `csrmv_v3_kernel` retired lane 0 before it added its row and every SpMV on sm_75 and newer was wrong (`VMControlCompiler`, `ControlFlowSuite`). With grid 64, `cg` and `bicgstab` now match the RTX 5070 (13 and 32 iterations) on emulated sm_50, sm_60, sm_75 and sm_120, in 32 to 96 s per case.
