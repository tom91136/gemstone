# Work Plan

Small and medium items, in priority order. Larger efforts have their own plans, listed as rows 9 and 10. The performance profile (`performance-profile.md`) is done to F2; F3 and F4 are not scheduled.

| # | Item | Why | Where | Size | Depends on | Status |
|---|---|---|---|---|---|---|
| 1 | Render-cycle refactor benchmark | The nvidia and spirv-vm acyclic refactor moved declarations and the sampling dispatch; confirm it is neutral | `tools/benchmarks` | S | | done |
| 2 | Copy geometry errors | Invalid copy geometry must return the API's invalid-value error, as the real runtimes do | `native/cuda/src/runtime.cpp` `copyVolume`, `native/hsa/src/runtime.cpp` `copyPitched`, `native/hip/src/runtime.cpp:324` `mapHsa` | S | | done |
| 3 | Launch connection reuse | Every NDRange launch opens a new RPC connection (thread start, ~92 KB Hello); 150k to 570k per conformance shard | `native/rpc/completion.hpp` `launchAndCollect` | S | | done |
| 4 | Image programs back in `short` | Copy, fill and read/write image programs were excluded from the short workload for per-row RPC cost, now gone | `conformance/workloads/short.toml` | XS | a full CI conformance run | open |
| 5 | PTX register-declaration typing | Tables 26 to 28 of the PTX ISA are in `operand-domains.json` but not applied to register operands | `isa/ptx-asm/.../ptx/Validation.scala` | M | | open |
| 6 | Parked SPIR-V and NV renders | Explorer renders for the SPIR-V image level and descriptor logic, device enqueue and event forms, and the NV tensor layouts and views | `isa/spirv-vm`, `isa/nvidia` render instances | M | | open |
| 7 | nvidia barrier cohort queue | The barrier release and warp arrival scans in `VMInterpreter` are the two true bonsai complexity findings | `isa/nvidia/.../VMInterpreter.scala` | M | | open |
| 8 | AMDGPU gfx1250 and gfx1251 | About 1025 forms are NotImplemented and need hardware evidence | `explorer/src/test/resources/coverage/policy.tsv` | L | hardware access | blocked |
| 9 | Conformance coverage verification | Code items are closed; the `all` tier, emulated release samples and remaining container builds are not yet run | `conformance-coverage.md` | L | | open |
| 10 | SPIR-V validation | Only header, version, environment and 46 operand constraint rows are checked; V0 to V8 not started | `spirv-validation.md` | L | | open |

## 1. Render-cycle refactor benchmark

Done: neutral. The refactor landed in `d1d618fe`; the comparison is `d1d618fe^` against `d1d618fe`, since later commits change the same VM paths. JFR runs on a busy host (load 8 to 10), pinned to four cores, base and new alternating with the order flipped each round: 4 rounds of all eight cases (30 warmups, 40 samples), then 6 rounds of `ptx-integer-alu` and `spirv-image-sampling` (50 warmups, 150 samples).

- Allocation: median new/base 0.992 to 1.008 by JFR thread totals on every case; per-sample medians match except where both builds vary between rounds (`spirv-execution` 4.63 to 5.30 MB per sample in new, 4.85 to 5.06 in base).
- Time: median new/base 0.96 to 1.07 across cases, inside base-to-base spreads of up to 1.63 between rounds. `spirv-image-sampling`, the only changed path, is 0.99 over the 6 long rounds.
- `ptx-integer-alu` allocated 19.63 MB per sample in 2 of 10 new runs against 16.96 to 17.01 MB in every other run. JFR attributes the difference to the closure in the harness's `BenchmarkMemory.writeValue` not being scalar-replaced in those runs; the same site appears at about 0.15% in base runs, and the harness and `AddressSpace` did not change in the refactor.

## 2. Copy geometry errors

Done.

- CUDA: `copySide` rejects a 3D copy whose slice height (`srcHeight`, `dstHeight`) is below `y + Height` with `CUDA_ERROR_INVALID_VALUE`; the server used to reject the rectangle and the facade returned `CUDA_ERROR_UNKNOWN`.
- HSA: `copyPitched` (also behind `hsa_memory_copy`) returns `HSA_STATUS_ERROR_INVALID_ALLOCATION` only when neither base pointer is in an allocation or registration, and `HSA_STATUS_ERROR_INVALID_ARGUMENT` when a span leaves its GPU allocation. Before, a download whose span left the device allocation copied the bytes past it. The trailing granules every allocation keeps for vendor kernels that overrun it still count as inside.
- HIP: pitched copies report both as `hipErrorInvalidValue`, as ROCm 7.1.1 does (`validateMemoryObject` in clr `hipamd/src/hip_memory.cpp`).
- Tests: `memcpy-rectangle.cpp`, `hsa_runtime_test.cpp`, `hip_runtime_test.cpp`.

## 3. Launch connection reuse

Done. `launchClient` (`native/rpc/completion.cpp`) keeps one launch connection per thread, apart from the shared client, keyed on the process, endpoint and client ID, so a forked child never uses its parent's session. `launchAndCollect` and the HSA AQL dispatch (`AqlRuntime`, which kept its own per-thread map) use it, and drop it on any failed launch. Calls made after a thread's storage is torn down, as `~AqlRuntime` during process exit, get a connection that is never closed. `rpc/test/completion.cpp` checks reuse, replacement after a dropped connection, and a fork.

Before, each launch opened a connection (thread start, ~92 KB Hello), about 70 to 80 us serialised under `kernelSubmissionMutex`: 10 to 46 s per conformance shard, nearly all in `image_streams/1D` and `1Darray`. The saving is not yet measured on a conformance shard.

## 4. Image programs back in `short`

Once a full CI run reports the times, move `opencl_cts_cl_copy_images`, `opencl_cts_cl_fill_images` and `opencl_cts_cl_read_write_images` out of `exclude_programs` if they fit the short budget. Locally, the CI-timed copy cases run in about 22 s each (they timed out at 2400 s before). `opencl_cts_image_streams` stays excluded until a CI run times it with item 3 in place.

## 5. PTX register-declaration typing

PTX modifier domains, operand types and value limits are already checked (`isa/ptx-asm/src/main/scala/net/kurobako/gemstone/isa/ptx/Validation.scala`, from `isa/ptx-asm/src/generated/.../operand-domains.json`). The type-checking tables (which instruction types accept which register types, and the relaxed source and destination rules) are not applied to register operands. Apply them using the register declarations in the module, with ptxas as the differential reference. Checks: `ptxAsm` and `ptxVm` test suites, `scalafmtCheckAll`, `scalafix --check`.
