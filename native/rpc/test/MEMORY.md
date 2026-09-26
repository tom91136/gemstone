# Memory implementation regressions

Each native API owns its memory regression client in `native/<api>/test/memory.cpp`.
The shared checkpoint, contention and transport harness lives beside this guide
in `memory_patterns.hpp`, with allocation and fixture helpers in `memory_test.hpp`.
These clients use native API calls directly and include no conformance headers.

Public memory cases run through the independent unified suite; see the
[conformance guide](../../../conformance/README.md#shared-memory-cases).
Native tests retain implementation assertions around the transport and scheduler;
they do not register another portable conformance matrix.

The Scala suites retain checks that need Gemstone's RPC journal, interpreter
checkpoints, advertised capabilities, ordinary-host extensions or scheduling
behaviour. They start real execution servers and exercise all five APIs:
CUDA/PTX, HIP/AMDGPU, HSA/AMDGPU, Level Zero/SPIR-V and OpenCL/SPIR-V.

```sh
sbt 'server/testOnly *UnifiedMemoryProgressSuite *UnifiedMemoryReviewSuite'
```

## Retained coverage

| Progress scenarios | Processes | Kernel launches |
| --- | ---: | ---: |
| Representative host-memory traffic checks, one per API | 5 | 10 |
| Ordinary-host extension access patterns | 4 | 32 |
| Disjoint CPU/device bytes with interpreter checkpoints | 14 | 84 |
| Acquire/release atomics with forced CAS collisions | 28 | 28 |
| Relaxed atomics with explicit fences and forced collisions | 28 | 28 |
| 128-work-item / four-workgroup forced contention | 28 | 28 |
| Submit then publish | 28 | 28 |
| Overlapping consumer/producer queues | 28 | 56 |
| Representative failed-CAS RPC traffic checks | 5 | 5 |
| **Progress suite** | **168** | **299** |
| Three overlapping device/mixed-memory review scenarios per API | 5 | 30 |
| **Combined implementation suites** | **173** | **329** |

Concurrency allocation coverage remains managed/pinned/registered for CUDA,
managed/host/registered for HIP, region/pool/locked for HSA, shared/host for Level
Zero, and SVM/Intel host USM/Intel shared USM for OpenCL. Atomic cases
cover both 32- and 64-bit words.

The forced-contention tests pause each device RMW before native CAS. CPU writes
force the first two attempts to fail; the server asserts exactly three attempts.
The disjoint-access tests pause before execution and completion to verify that
untouched host bytes survive. RPC journal assertions reject speculative uploads
of host-backed allocations and ordinary reads of atomic control words.

The submit/overlap cases retain their existing two-second rescue mechanism and
are Gemstone scheduling regressions. They are not general vendor conformance
requirements or performance benchmarks. The review suite retains disjoint device
writes, mixed device/shared publication and device pointer replacement between
overlapping kernels. Synchronized CPU pointer replacement now uses one kernel
and a host worker in unified conformance.

Run only the 145 atomic/fence/contention/submission/overlap/compare cases:

```sh
GEMSTONE_TEST_HOST_ATOMICS=1 sbt 'server/testOnly *UnifiedMemoryProgressSuite'
```

## Generated fixtures

The native CMake build invokes the independent artifact project with the public
`fixture` and `command` interfaces documented in the
[conformance guide](../../../conformance/README.md#external-fixture-and-toolchain-builds).
It does not import that project's CMake modules or read suite source files.
AMDGPU and SPIR-V images and exported PTX artifacts are written under
`target/native/build/rpc/memory-fixtures/`. Native interpreter fixtures target
gfx900. No generated binary is checked in.

```sh
sbt 'native/build'
```

SYCL coverage lives in the conformance unified suite and CTS, not in the native
tree. The native build passes a recipe and declared inputs/outputs to the
artifact builder, which owns compiler images and runtime packaging.
