# Runtime

Native facades preserve supported ABI calls and submit checked work to a local endpoint.
The server runs ISA-specific CPU executors. Generated declarations include unsupported
methods; declaration coverage is not execution coverage.

| Application API | Boundary |
| --- | --- |
| CUDA Runtime/Driver | Gemstone CUDA Driver; stock libcudart stays in place |
| HIP | Gemstone HIP, or vendor HIP over Gemstone HSA |
| HSA | Public HSA facade and AQL queues |
| OpenCL | Gemstone ICD |
| Level Zero | Gemstone driver |
| SYCL | Exactly one OpenCL or Level Zero backend |

HIP embeds its HSA implementation deliberately to avoid external SONAME/symbol conflicts.
HIP-only builds omit unrelated standalone HSA outputs. Vendor-HIP/HSA routing is Linux-only;
Windows HIP does not imply HSA.

## Endpoint and ownership

The JVM and Native endpoint share framing, dispatch and execution code. They bind IPv4
loopback. Native facades currently use local TCP. Remote endpoints and GPU forwarding are
deferred in the [plan](../GEMSTONE_PLAN.md).

Each client credential owns process sessions identified by PID and incarnation. Connections
from one process share execution state. CUDA requests also carry the originating CPU thread's
current context. Allocations, modules, functions and callbacks are context-owned; destroying a
context unloads its modules and invalidates its memory without affecting sibling contexts.
Unavailable process identity fails closed. Losing an established CUDA transport session is
terminal; stale handles are not reused through an automatic reconnect.

HIP exposes one emulated device and its primary runtime context. `hipDeviceReset` retires that
device's allocations, modules, streams and events; stale handles fail. Level Zero allocations,
modules, queues, command lists, event pools, events and fences retain their explicit context,
and cross-context use fails. OpenCL follows its reference-counted model: attached objects keep
their context alive, while cross-context commands fail. HSA has no CUDA-style context; nested
runtime references and outstanding queues, allocations, signals, readers and executables govern
shutdown.

OpenCL IL creation retains and inspects the supplied bytes locally. Remote program loading occurs
at `clBuildProgram`; executable binary creation and successful link operations publish their
program immediately.

Revocation is terminal. Shutdown cancels active scopes before waiting for request threads.

One bounded pool serves all ISAs. Independent workgroups share its workers. Cancellation and
client close retire that client's state. Native OS entropy supplies tokens; no weak random
fallback is used.

## Wire ownership

CUDA's schema generates C marshalling, typed dispatch and authoritative Scala value codecs.
Handwritten code owns framing, authentication, sessions and callback lifetime. Generic execution
has its own versioned allocation/program/launch protocol. Both use bounded big-endian frames;
request sizes are checked before outbound payload construction.
CUDA framing is version 4, which added the request context field. Generic execution remains
version 3. Peers reject other versions before decoding the remainder of a frame.
CUDA module loading batches PTX entry-point resolution and launch signatures into one request;
ordinary Driver `cuModuleGetFunction` calls remain available for dynamically named functions.

Callbacks use reverse frames. The native application invokes its own function pointers; the
server never calls a foreign address. [Replay](REPLAY.md) records callback replies and nested
RPCs without repeating external host effects.

Native socket IO avoids empty writes and uses pushback for credential peeking, keeping buffered
input unmarked. These paths have JVM/Native and fragmented-frame regressions.

## Memory

Guest access is checked by allocation identity, bounds and lifetime. CUDA pointer classification
uses tracked mappings rather than an address threshold. Managed/registered mappings and interior
pointers retain distinct attributes.

AMDGPU globals share backing storage with explicitly bound allocations. Native-address aliases
support ordinary nested pointers. CPU-visible buffers use chunk snapshots and dirty copy-back;
unrelated device-only buffers are not implicitly uploaded. This is explicit coherence, not
fault-driven UVM. Opaque native pointers to ELF globals can share storage without numerical
identity with linked ELF pointers.

The pinned StdPar fixture build uses managed allocation, hidden inline visibility and the
header-aware unsized-delete path. These compiler settings preserve workloads and avoid known
allocator/interposition defects. OpenMP fixtures must contain real device images and launch
entries and run with mandatory offload.

See [ISA execution](ISA_EXECUTION.md) for architectural semantics and
[conformance](../conformance/README.md) for supported qualification paths.
