# HIP runtime routes

HIP is the application API in both routes:

| Route | HIP frontend | Execution boundary | Platform eligibility |
| --- | --- | --- | --- |
| `hip` | Gemstone HIP | Gemstone execution service | Independent of an external HSA provider |
| `hip-hsa` | The artifact's pinned vendor HIP | Gemstone HSA, then the execution service | Compatible Linux ROCm artifacts only |

Select a route with `gemstone run --api hip --hip-route hip-hsa --hip-runtime <vendor-library> --hsa-runtime <gemstone-library> -- <application> ...`.
The default is `hip`. The direct route requires only `--hip-runtime`; the HSA route requires both libraries. The same compiled artifact and workload arguments can be used for both routes. Qualification reports must verify actual HIP and HSA dynamic-loader bindings; execution request counts alone do not distinguish the routes.

AMD documents ROCclr's [ROCr backend on Linux and PAL backend on Windows](https://rocm.docs.amd.com/projects/HIP/en/docs-7.1.0/how-to/hip_runtime_api.html). The platform model therefore excludes `hip-hsa` on Windows unconditionally. Linux tests of route selection do not qualify a Windows runtime. The current Linux Gemstone HIP implementation internally embeds its HSA implementation; that implementation choice does not make HSA a requirement of the HIP API or of Windows HIP.

The miniapp catalog keeps HIP, Kokkos-HIP and HIP StdPar application ports separate from their runtime route. Both routes share the `hip` native-oracle identity for a given artifact and input. OpenMP target and direct HSA remain HSA applications. Route eligibility is not a claim that every application/runtime combination has passed: vendor HIP global-symbol and explicitly advised host-memory interoperability need their own qualification, and failures remain failures.

The pinned ROCm StdPar fixture build selects the toolchain's explicit managed allocator (`hipMallocManaged`/`hipFree`).
Its newer malloc-plus-advice allocator does not expose allocation lifetime at the HSA boundary, so it cannot establish
safe ownership there. Hidden application/inline visibility prevents compiler-interposed STL definitions from leaking
into ROCclr initialization and recursively entering the allocation pool. `-fno-sized-deallocation` selects the shipped
header-aware delete path because this pinned allocator’s sized-delete path bypasses its allocation header. These are fixture compiler settings; the
upstream workloads and their expected results are unchanged. This does not claim general implicit HMM/SVM support.

OpenMP-target fixtures require nonempty device images and target entries, target-launch references, and the upstream
source activation defines. Runs set `OMP_TARGET_OFFLOAD=MANDATORY`. Initialization requests alone are insufficient
proof of offload: the earlier vendor/architecture CMake branch omitted those defines and produced host-only work.

Ordinary nested pointers use explicit, per-allocation native address mappings. Dispatch snapshots retain their backing
allocations, and CPU-pool/registered-host buffers refresh through chunk snapshots that also track GPU copy-back.
Unrelated device-only allocations are not implicitly uploaded. Snapshots cost one extra copy of CPU-visible storage;
unchanged chunks avoid redundant transfers. No opaque buffer words are guessed to be pointers.

A native pointer to an ELF-bound global inside opaque ordinary memory can access the same storage but differs
numerically from the canonical linked ELF pointer. That mixed-address boundary is tested; it is not universal
pointer-graph equality or transparent shared-memory support.

## HSA queue contract

Public HSA producers use ordinary release stores to AQL doorbells. The monitor must bounds-check
packets, retain synchronization ordering and report failures. Dirty copy-back precedes completion
signals. Queue teardown stops and joins its monitor before freeing queue memory; failures cannot
be converted into successful completion. Emulation must not access physical GPU device nodes.
See [runtime ownership](../docs/RUNTIME.md) and [conformance](../conformance/README.md).
