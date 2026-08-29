# HSA boundary

Gemstone preserves the stock public HSA/ROCr contract. Applications allocate
queues and ring doorbells with ordinary C atomics; therefore a wrapper API is
not sufficient. The native HSA runtime provides a CPU-visible AQL ring and a
doorbell monitor that observes stores, validates packets, and submits typed
launches to the shared execution service.

The deployment boundary is:

```text
HIP / rocThrust
  -> Gemstone public HIP Runtime
  -> Gemstone public HSA Runtime
  -> CPU-visible AQL queue + doorbell monitor
  -> typed execution RPC
  -> AMDGPU decoder and interpreter
```

Direct HSA applications follow the same path beginning at the public HSA Runtime. The virtual topology exposes a CPU
node and a `gfx900` GPU node. Allocation
identities are process-namespaced, code objects retain their exact bytes, and
completion signals are updated only after dirty memory has been copied back.
No conformance run may open KFD, DRM, or XDNA devices.

Gemstone does not patch or rebuild ROCr for deployment and does not use DTIF, `HSA_TOOLS_LIB`, or a substituted
hsakmt. Those are private implementation boundaries and would require an end user to alter their ROCm installation.
The existing hsakmt-shaped native code is retained only as implementation material for queue, doorbell, allocation,
and code-object mechanics while the public `libhsa-runtime64` facade is completed; it is not a supported launch path.

## Invariants

- Doorbell observation works for an ordinary release atomic store.
- Packet parsing is bounds checked and rejects unsupported packet kinds.
- Queue teardown stops and joins its monitor before releasing queue memory.
- Native pointers are classified only against registered allocation ranges;
  raw host addresses never become trusted guest addresses.
- Synchronization packets preserve ordering even when the mirrored-memory
  model needs no physical cache operation.
- Failures are typed and reach the producer rather than being converted into
  successful completion.

Focused native tests currently cover the monitor, AQL execution, code-object metadata, and the legacy lower boundary.
Public-HSA samples in Conformance are the acceptance gate for the replacement facade. The exhaustive rocThrust inventory is
kept in the separate conformance lane described in
`conformance/rocthrust/README.md`.
