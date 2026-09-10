# LUNA action ledger

Updated 2026-09-09 against the merged implementation. This records the disposition of the
27 reviewed actions; it is not a whole-repository review or a final qualification result.
**The baseline full qualification and read-only review are complete; the subsequent 24 fixes are implemented and qualified.**
Top-level unit checks passed 3,384/3,384; Native integration passed 8/8, and root Native
capture/replay checks are green. The concurrent TeaLeaf preflight passed 21/21 executions and
12/12 paired comparisons in 31.4 seconds. The later full matrix passed 80,552 cases, skipped 25,678 and failed none; see `target/luna/REPORT.md`.

Original reviews, inventories and superseded guides are preserved in `target/luna/docs-before/`
with a hash manifest. Keep that evidence before cleaning. Current run evidence belongs under
`target/luna/`; historical full/corrective results remain under `target/cts-bringup/`.

| # | Action | Disposition and evidence |
| --- | --- | --- |
| 1 | Preserve conformance failures | Implemented: primary workload outcomes survive statistics/cleanup failures; successful workloads still fail on infrastructure errors. [Driver regressions](conformance/tools/test-case-run.py). |
| 2 | Resource-generator cache contract | Hardening: runtime properties use an uncached resource generator and belong to integration. Both old and new forms pass resource restoration checks; no baseline cache defect was reproduced. [Build](build.sbt), [lifecycle guide](docs/BUILD.md). |
| 3 | PTX reduction identity | Implemented: participating-value seed, explicit invalid masks and correct unsigned modifier selection. [Runtime regression](isa/ptx/src/test/scala/net/kurobako/gemstone/isa/ptx/v9_3/WarpReductionSuite.scala). |
| 4 | AMDGPU worker globals | Implemented: explicit global regions replace caller-thread fallback. [Unbound-global worker regression](isa/amdgpu/src/test/scala/net/kurobako/gemstone/isa/amdgpu/GlobalWorkerSuite.scala). |
| 5 | CUDA pointer classification | Implemented: tracked allocations/mappings replace address-threshold classification. [Native boundary tests](runtime/cuda/test/surface-smoke.cpp). |
| 6 | Generator cache inputs | Implemented: declared reference contents, tools, versions and relevant environment enter the cache key. [Generator](project/Generator.scala); this does not claim every ambient SDK input is universally captured. |
| 7 | Missing CUDA CTest coverage | **Rejected as stated.** Real clients already had test owners; cross-workgroup coverage now belongs to [native integration](integration/native/src/test/scala/net/kurobako/gemstone/server/CUDACrossWorkgroupSuite.scala), alongside authentication checks. |
| 8 | Header compatibility and paths | Implemented: direct-CMake CUDA header checks, path-segment handling and portable compiler input. [Header regression](runtime/cuda/test/header-compatibility.cmake), [reference handling](project/ReferenceArtifacts.scala). No Windows runtime qualification implied. |
| 9 | Trailing AMDGPU arguments | Implemented: reject excess typed arguments while retaining inferred linked descriptors. [Packing tests](isa/amdgpu/src/test/scala/net/kurobako/gemstone/isa/amdgpu/GlobalWorkerSuite.scala); seven unchanged Clover HIP/HSA/Kokkos/StdPar/OpenMP replays passed. |
| 10 | Virtual geometry overflow | **Rejected as stated.** Positive Int operands widened to Long cannot overflow the cited per-axis multiplication; Int multiplication already returns a validation error. [Protocol](core/src/main/scala/net/kurobako/gemstone/execution/Protocol.scala). |
| 11 | Formatting/check gate | Top-level compile/testFull is green; baseline and follow-up qualification pass. Historical formatting failures are not current results. Use [the existing checks](docs/BUILD.md). |
| 12 | CTS status/defaults | Documentation updated: representative OpenCL/SYCL CTS is intentional default coverage, not exhaustive certification. [Conformance](conformance/README.md). |
| 13 | Stale commands/guides | Documentation consolidated into [build](docs/BUILD.md), [runtime](docs/RUNTIME.md) and [replay](docs/REPLAY.md) guides; obsolete aliases/harness descriptions removed. |
| 14 | Native outbound RPC bounds | Implemented: preflight aggregate sizes before reading caller buffers or growing payloads. [Bounds regressions](native/execution/test/request_bounds.cpp). Existing inbound limits retained. |
| 15 | Generated provenance | Implemented through the [generator](codegen/rpc/src/main/scala/net/kurobako/gemstone/codegen/GenerateCUDABindings.scala), including stable schema/pin attribution; no handwritten generated-output edits. |
| 16 | Process incarnation fallback | Implemented fail-closed behavior when process identity is unavailable. [Identity regression](native/execution/test/process_identity.cpp). PID reuse, not simultaneous sibling PIDs, was the concern. |
| 17 | Trailing/oracle coverage | Completed with item 9's runtime regressions. Existing [LLVM oracle coverage](isa/amdgpu/src/test/scala/net/kurobako/gemstone/isa/amdgpu/LLVMOracleSuite.scala) remains; no duplicate broad inventory added. |
| 18 | Documentation/review drift | Documentation consolidated and historical claims separated. Baseline qualification and whole-repository review completed; subsequent fixes are tracked separately. |
| 19 | Generic scalar allocation | Implemented using existing value methods. [Boundary test](core/src/test/scala/net/kurobako/gemstone/memory/ExecutionAddressSpaceSuite.scala). One warmed sample fell from about 2,938 to 70 allocated bytes per 8-byte read/write pair; not a CUDA-provider or latency claim. |
| 20 | AMDGPU vector copies | Implemented by consuming owned immutable snapshots directly; no mutable backing exposure. [Snapshot regression](isa/amdgpu/src/test/scala/net/kurobako/gemstone/isa/amdgpu/GlobalWorkerSuite.scala). One warmed 64-lane fixture fell from about 33,077 to 30,829 allocated bytes per dispatch. |
| 21 | Dynamic lookup locking | Implemented: remove only the inner reentrant lookup lock; caller-held shared-state locking remains. [Memory tests](core/src/test/scala/net/kurobako/gemstone/memory/CheckedMemorySuite.scala). No independent lock-speedup claim. |
| 22 | CUDA response allocation | Implemented: size-check and encode into one bounded response buffer using the generated codec. [Endpoint](server/src/main/scala/net/kurobako/gemstone/server/GemstoneRPCServer.scala). |
| 23 | Native prerequisite for server tests | Implemented: native-dependent CUDA client tests/resources moved to native integration. Ordinary server unit tests retain no CMake prerequisite. [Build lifecycle](docs/BUILD.md). |
| 24 | Native builds on Scala compile | Implemented: explicit native/integration tasks replace the broad compile dependency. [Native build plugin](project/NativeBuild.scala). Standard root `target`/`cleanFiles` ownership is also corrected; external caches and explicit caller outputs remain caller-owned. |
| 25 | Nested HIP/HSA outputs | Implemented: unrelated standalone HSA outputs excluded from HIP's default build. [HIP CMake graph](runtime/hip/CMakeLists.txt). Intentional HSA embedding and standalone HSA builds remain. |
| 26 | Remove Native server row | **Rejected.** Native support is intentional; a shared Native endpoint and explicit optimized build now exist. [Build](docs/BUILD.md), [replay](docs/REPLAY.md). Linux Native executable checks and the JVM conformance matrix pass; see `target/review-fixes/REPORT.md`. |
| 27 | Authoritative CUDA value codec | Implemented through [schema-derived generation](codegen/rpc/src/main/scala/net/kurobako/gemstone/codegen/GenerateCUDABindings.scala); handwritten framing, authentication and callback ownership remain. |

Allocation figures above are single warmed before/after fixture samples, not application speedups.
Focused JVM/Native and application checks are complemented by the passing final merged matrix. The earlier
106,230-case run and later 1,320-case ByVal corrective run are separate baselines, not a final
qualification of these changes.

Optional façade, diagnostic-rendering and printer-layout harmonization remains deferred until
there is a concrete consumer or source convention. ISA-specific execution models remain separate;
see [ISA execution](docs/ISA_EXECUTION.md). Remote execution, physical-GPU forwarding and
fault-driven paging remain in the [plan](GEMSTONE_PLAN.md).
