# Work Plan

Small and medium items, in priority order. Larger efforts have their own plans, listed as rows 9 and 10. The performance profile (`performance-profile.md`) is done to F2; F3 and F4 are not scheduled.

| # | Item | Why | Where | Size | Depends on | Status |
|---|---|---|---|---|---|---|
| 1 | Render-cycle refactor benchmark | The nvidia and spirv-vm acyclic refactor moved declarations and the sampling dispatch; confirm it is neutral | `tools/benchmarks` | S | | done |
| 2 | Copy geometry errors | Invalid copy geometry must return the API's invalid-value error, as the real runtimes do | `native/cuda/src/runtime.cpp` `copyVolume`, `native/hsa/src/runtime.cpp` `copyPitched`, `native/hip/src/runtime.cpp:324` `mapHsa` | S | | done |
| 3 | Launch connection reuse | Every NDRange launch opens a new RPC connection (thread start, ~92 KB Hello); 150k to 570k per conformance shard | `native/rpc/completion.hpp` `launchAndCollect` | S | | done |
| 4 | Image programs back in `short` | Copy, fill and read/write image programs were excluded from the short workload for per-row RPC cost, now gone | `conformance/workloads/short.toml` | XS | a full CI conformance run | open |
| 5 | PTX register-declaration typing | Tables 26 to 28 of the PTX ISA are in `operand-domains.json` but not applied to register operands | `isa/ptx-asm/.../ptx/Validation.scala` | M | | done |
| 6 | Parked SPIR-V and NV renders | Explorer renders for the SPIR-V image level and descriptor logic, device enqueue and event forms, and the NV tensor layouts and views | `isa/spirv-vm`, `isa/nvidia` render instances | M | | open |
| 7 | nvidia barrier cohort queue | Waiters are already queued per barrier key; the remaining release and arrival scans cost about 2% of samples in a 1024-thread barrier loop (32 warps at most), so nothing is worth landing. The whole-build `bonsaiAnalyse` runs out of heap, so the two findings could not be re-checked | `isa/nvidia/.../VMInterpreter.scala` | M | | measured, no change |
| 8 | AMDGPU gfx1250 and gfx1251 | About 1025 forms are NotImplemented and need hardware evidence | `explorer/src/test/resources/coverage/policy.tsv` | L | hardware access | blocked |
| 9 | Conformance coverage verification | Code items are closed; the `all` tier, emulated release samples and remaining container builds are not yet run | `conformance-coverage.md` | L | | open |
| 10 | SPIR-V validation | Only header, version, environment and 46 operand constraint rows are checked; V0 to V8 not started | `spirv-validation.md` | L | | open |
| 11 | Cooperative-groups syscalls beyond sm_89 and PTX mode | `CGS_sync` and `CGS_get_intrinsic_handle` are linked and run from the SASS path; the wrapper ABI was read from sm_80 and sm_89 only, and PTX mode rejects `cudaCG*` calls | `isa/sass-asm/.../Cubin.scala`, `isa/ptx-vm/.../VMInstructionCompiler.scala` | M | | open |

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

Done. `Validation` collects the `.reg` declarations in scope for each entry, function and block, and the new `RegisterTypes` checks every register operand whose instruction type the matched syntax form or the operand type table fixes. Table 26 decides exact operands; packed and alternate instruction types accept the register declaration the type table gives them (`.f16x2` takes `.b32`, `.bf16` takes `.b16`). Tables 27 and 28 decide the data operands of `ld`, `st` and `cvt`, so a wider source is chopped and a wider destination is extended, while a narrower operand or a floating-point/integer mix the tables mark `inv` is rejected. `mul.wide` and `mad.wide` take a new `wide` row of the operand type table, which resolves the destination, and the `mad` accumulator, to twice the source width. Operands whose type the instruction prose fixes but the syntax does not name, such as the `vote` predicate or the `mbarrier` count, are left unchecked rather than guessed. `RegisterTypesSuite` covers the four rule kinds and block scoping; 15 ptxas 11.6 cases agree with its accepted and rejected examples. The execution path is unchanged because `PTXBackend` admits programs on availability only (`forms = false`).

## 11. Cooperative-groups syscalls beyond sm_89 and PTX mode

The SASS path provides `__cuda_syscall_CGS_get_intrinsic_handle` and `__cuda_syscall_CGS_sync` (a grid-wide `GridBarrier` over the resident workgroups of a cooperative launch). One part is open:

- Recheck the `libcudadevrt` wrapper ABI before any sm_90 or later work: handle in R4:R5, flags in R6, result in R4 were read from the sm_80 (CUDA 11.6) and sm_89 (CUDA 13.3) members. Disassemble the sm_90, sm_100 and sm_120 members first; ptxas may bind the syscalls through bank 4 there, which the relocation change covers but no test exercises.

PTX mode lowers `cudaCGGetIntrinsicHandle` to `Builtin.GridHandle` and `cudaCGSynchronize` to a load of its arguments, the 0 or 999 return, and the same `GridBarrier`; the `ptxVm` suite runs the conformance fixture's program on a cooperative launch and returns 999 for the zero handle. `CGS_get_size`, `CGS_get_rank` and the multi-grid scope stay unsupported, and a grid synchronisation with flag bit 0 (`cudaCGSynchronizeGrid`) faults.
