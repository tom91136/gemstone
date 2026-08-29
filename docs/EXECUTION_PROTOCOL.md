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

## Initial memory strategy

The first implementation mirrors allocation contents over RPC before a launch
and copies them back after completion. This is intentionally conservative and
functional. It also works with normal `malloc`/USM pointers and does not need
page-fault handling.

The protocol does not prescribe that transport. A later POSIX shared-memory or
Windows section mapping can replace the byte copies without changing program,
argument, or completion semantics.

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
