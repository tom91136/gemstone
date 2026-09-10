# Plan

Gemstone is a local, checked CPU GPU emulator for unmodified application artifacts.
JVM and Native share the execution design. sbt owns builds and tests. Unsupported behavior fails
explicitly; emulation never silently falls back to a physical GPU.

## Completed

The 24 reviewed defects are fixed. Focused failures preceded correctness changes; merged unit, native-integration,
style and generated-source checks pass. The full oracle/emulator matrix and scheduler audit pass.
CUDA, HIP, Level Zero, OpenCL and HSA ownership/lifetime contracts now have explicit cross-context or reset/shutdown
coverage; CUDA current-context identity crosses the RPC boundary without changing the shared worker pool.

## Deferred

- Remote CPU execution and physical-GPU forwarding: explicit endpoint/backend selection, client/process
  and handle ownership, disconnect cleanup and reverse callbacks. Other frontends do not yet transport
  every raw vendor API. Keep physical execution explicit.
- Fault-driven UVM and paravirtualised paging: agree allocation scope and coherence before implementation.
  Candidate mechanisms are mprotect/signal handling, Linux userfaultfd, Windows vectored exceptions and
  macOS signals/Mach exceptions. Use a minimal native fault stub and an independent pager; managed code,
  allocation and ordinary RPC calls cannot run in signal context.
- RMA/shared-memory/RDMA transfer: define ownership, versions, registration and in-flight retirement first.
  CPU protection does not intercept GPU faults. Opaque pointer graphs require valid address mappings;
  never guess pointer-valued words. Kernel I/O and concurrent CPU/GPU atomics need explicit contracts.

## Qualification

The latest run in `target/review-fixes/full/` completed 106,230 CTest executions in 1h39m49s:
80,552 passed, 25,678 skipped, none failed. Emulator cases spanned 1h04m55s while oracles ran concurrently.
The run serialized 34 fixture builds with 32 workers; two CTS artifacts were built and smoke-tested immediately
beforehand. Shared slice memory peaked at 54.39 GiB under a 64 GiB limit. All 12 paired comparisons and the
scheduler audit passed. Separate merged gates passed 3,649 JVM/Native unit tests and native integration.

The previous full run and LUNA evidence remain under `target/luna/`; earlier corrective runs remain under
`target/cts-bringup/`. Preserve needed reports before `sbt clean`.

Windows/macOS execution and broader upstream CTS coverage require separate qualification. Remote forwarding and
fault-driven UVM remain deferred.
