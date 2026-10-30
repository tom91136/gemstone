# Work Plan

Small and medium items, in priority order. Larger efforts have their own plans: `performance-profile.md` and `spirv-validation.md`.

| # | Item | Why | Where | Size | Depends on | Status |
|---|---|---|---|---|---|---|
| 1 | Render-cycle refactor benchmark | The nvidia and spirv-vm acyclic refactor moved declarations and the sampling dispatch; confirm it is neutral | `tools/benchmarks` | S | | open |
| 2 | Copy geometry errors | Invalid copy geometry must return the API's invalid-value error, as the real runtimes do | `native/cuda/src/runtime.cpp` `copyVolume`, `native/hsa/src/runtime.cpp` `copyPitched`, `native/hip/src/runtime.cpp:269` `mapHsa` | S | | open |
| 3 | Launch connection reuse | Every NDRange launch opens a new RPC connection (thread start, ~92 KB Hello); 150k to 570k per conformance shard | `native/rpc/completion.hpp:35` `launchAndCollect` | S | | open |
| 4 | Image programs back in `compact` | Copy, fill and read/write image programs were excluded from the compact workload for per-row RPC cost, now gone | `conformance/workloads/compact.toml` | XS | a full CI conformance run | open |
| 5 | PTX register-declaration typing | Tables 26 to 28 of the PTX ISA are in `operand-domains.json` but not applied to register operands | `isa/ptx-asm/.../ptx/Validation.scala` | M | | open |
| 6 | Parked SPIR-V and NV renders | Explorer renders for the SPIR-V image level and descriptor logic, device enqueue and event forms, and the NV tensor layouts and views | `isa/spirv-vm`, `isa/nvidia` render instances | M | | open |
| 7 | nvidia barrier cohort queue | The barrier release and warp arrival scans in `VMInterpreter` are the two true baumkuchen complexity findings | `isa/nvidia/.../VMInterpreter.scala` | M | | open |
| 8 | AMDGPU gfx1250 and gfx1251 | About 1025 forms are NotImplemented and need hardware evidence | `explorer/src/test/resources/coverage/policy.tsv` | L | hardware access | blocked |

## 1. Render-cycle refactor benchmark

Run the benchmark protocol in `performance-profile.md` ("What already exists"), HEAD before the refactor against HEAD, as JFR comparisons on a busy host (total allocated bytes and CPU execution samples; `jfr view allocation-by-site` to attribute a difference):
- nvidia: `ptx-tensor`, `ptx-integer-alu`, `nvidia-control-flow`, `ptx-bulk-copy`. Only the move of `NvidiaStaged.execute` into `VMSemantics.stage` touches the VM path.
- spirv-vm: `spirv-image-sampling` (the only case whose path changed: `ImageLowering.ImageOp.run` matches the sampling `Option` instead of a `getOrElse` closure), `spirv-execution`, `spirv-cooperative-matrix`.
- control: `amdgpu-alu`, for host drift.

Accept no growth in allocation or samples beyond run-to-run spread; on a quiet host, a median ratio of 1.01 or better.

## 2. Copy geometry errors

- CUDA: a pitch below the row width returns `CUDA_ERROR_UNKNOWN` from the rectangle helper. Check the geometry up front in `copyVolume` and return `CUDA_ERROR_INVALID_VALUE`.
- HSA and HIP: a device span that leaves its allocation returns `HSA_STATUS_ERROR_INVALID_ALLOCATION`, which HIP reports as `hipErrorInvalidDevicePointer`. Return `INVALID_ALLOCATION` only when the base pointer is in no allocation, and `HSA_STATUS_ERROR_INVALID_ARGUMENT` (`hipErrorInvalidValue`) when the span leaves it. Confirm the code ROCm's `hipMemcpy2D` returns before settling the mapping.
- A test per case and facade.

## 3. Launch connection reuse

`launchAndCollect` opens a dedicated connection per launch so the shared client keeps serving host-memory requests during the dispatch. Keep one dedicated client per launching thread and drop it on any failure, so a broken connection is never reused. The cost today is about 70 to 80 us per launch, serialised under `kernelSubmissionMutex`: 10 to 46 s per conformance shard, nearly all in `image_streams/1D` and `1Darray`.

## 4. Image programs back in `compact`

Once a full CI run reports the times, move `opencl_cts_cl_copy_images`, `opencl_cts_cl_fill_images` and `opencl_cts_cl_read_write_images` out of `exclude_programs` if they fit the compact budget. Locally, the CI-timed copy cases run in about 22 s each (they timed out at 2400 s before). `opencl_cts_image_streams` stays excluded until item 3 lands.

## 5. PTX register-declaration typing

PTX modifier domains, operand types and value limits are already checked (`isa/ptx-asm/src/main/scala/net/kurobako/gemstone/isa/ptx/Validation.scala`, from `isa/ptx-asm/src/generated/.../operand-domains.json`). The type-checking tables (which instruction types accept which register types, and the relaxed source and destination rules) are not applied to register operands. Apply them using the register declarations in the module, with ptxas as the differential reference. Checks: `ptxAsm` and `ptxVm` test suites, `scalafmtCheckAll`, `scalafix --check`.
