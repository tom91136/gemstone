# Shared execution protocol

The CUDA, HSA, and Level Zero frontends submit work through one vendor-neutral
execution boundary. Native API objects and process addresses never become JVM
capabilities.

## Identities and ownership

- A native allocation has an `AllocationId` and a bounded mirror owned by the
  execution service.
- A loaded PTX, AMDGPU code object, or SPIR-V module has a `ProgramId`.
- A submitted dispatch has a `CompletionId`. Completion identity is separate
  from submission so the wire contract remains asynchronous even while an
  early executor completes work inline.
- Every identity is positive. Zero is reserved as the invalid wire sentinel.
- Identities belong to one authenticated process session and are released
  explicitly. A process may open multiple connections carrying the same random
  session identity (for example, HSA memory and AQL workers); another process
  may reuse numeric IDs without gaining access to that session.

Kernel arguments are copied scalar bytes, an `(AllocationId, byte offset)`
pair, or an explicitly sized work-group-local allocation. A raw native pointer
is translated only by the native allocation registry; it is never sent to,
trusted by, or dereferenced by the executor process.

## Memory synchronization

The execution service owns a bounded mirror for every registered allocation.
Completion records identify dirty ranges, so providers read back only bytes
written by a dispatch. Level Zero additionally restricts non-indirect launches
to argument-reachable allocations and stores 4 KiB page fingerprints: its first
launch uploads reachable pages, unchanged launches send nothing, and raw host
writes cause only changed pages to be uploaded. Kernels declaring indirect
access remain conservative and consider every live allocation.

OpenCL also uses completion dirty ranges for copy-back. Host-mutable SVM remains
conservatively uploaded before launch because applications can modify it without
passing through an ICD operation.

The protocol does not prescribe that transport. A later POSIX shared-memory or
Windows section mapping can replace the byte copies without changing program,
argument, or completion semantics.

Set `GEMSTONE_PERF_TRACE=1` to emit one provider line per dispatch containing
uploaded bytes, downloaded bytes, pointer-relocation scan bytes, relocation
count, and elapsed microseconds. The SPIR-V interpreter also exposes opt-in
`Interpreter.PerformanceMetrics`; it counts dispatches, invocations,
instructions, retained value-state instances, dense ID slots, estimated dense
state bytes, and elapsed nanoseconds without affecting the default API.

## Native wire protocol

`native/execution/gemstone_execution_rpc.*` implements the authenticated binary
client. It uses the existing loopback port and 64-hex authentication token with
its own magic and version. Supported operations are:

1. register, write, read, and release allocation;
2. load and unload program;
3. submit dispatch;
4. query and release completion.

Dispatch messages carry exact global dimensions separately from padded group
dimensions, plus private- and group-segment byte counts. The server rejects unknown formats, argument kinds, invalid dimensions,
unregistered allocation capabilities, out-of-range offsets, trailing bytes,
and oversized payloads. ISA backends must return `Unsupported`; they must not
report a successful launch without executing it.

Loss of an established session is terminal for a native client. It does not
silently reconnect with stale allocation or program IDs; the frontend must
create a new client and rebuild its session state.

## Runtime binding

Level Zero translates USM pointer arguments through its allocation registry.
HSA translates AQL kernel-dispatch packets and kernarg contents after the
virtual KFD queue consumer receives the ordinary direct-store doorbell. Neither
runtime depends on the other, and both depend only on the native execution
client.
