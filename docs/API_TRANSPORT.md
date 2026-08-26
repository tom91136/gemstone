# Generated API transport

The Scala `CUDA` trait owns the complete 144-method GPUOcelot Driver surface. Its abstract, typed methods are the
implemented RPC subset; concrete defaults return `CUDA_ERROR_NOT_SUPPORTED`. Exact C function names identify methods,
and a Scala 3 quoted macro derives the export inventory plus typed schemas, IDs, proxies, dispatch, and fingerprints.
See [CUDA_RPC.md](CUDA_RPC.md). The older registry remains only for the phase-0 two-call transport.

`codegen` currently emits:

- `gemsi_protocol.h`: pointer-free request and response structures, protocol version, status values, and opcodes;
- `gemsi_cuda.h` and `gemsi_cuda.c`: the public ABI and pure-C validation/marshalling facade;
- `gemsi_cuda.map`: the exact exported ELF symbol surface and version node;
- `GeneratedApi.scala`: handler interface, allocation-free result representation, and opcode dispatch;
- `api-manifest.json`: machine-readable ABI and protocol coverage metadata.
- `cuda-rpc-schema.json` and `gemsi_cuda_rpc.inc`: trait-derived names, types, IDs, and schema fingerprint;
- `gemsi_cuda_rpc_client.c`: generated CUDA Driver ABI projection and typed marshalling;
- `GeneratedCudaRpc.scala`: typed client and receiving dispatcher for JVM and Scala Native;
- `cuda-driver-surface.json`: the 144-method Driver inventory derived from the Scala `CUDA` trait and each method's implementation status.

Generated outputs are checked into `generated/` so native C consumers and Scala builds use the same reviewed protocol.
Run the generator with `sbt 'codegen/run generated'` and verify it with
`sbt 'codegen/run --check generated'`.

## In-process transport

The original two-call phase-0 smoke path remains in process while it continues to stress foreign-thread isolation:

```text
application thread
      |
      v
generated vendor facade
  - validates pointers
  - copies scalar/input values
  - preserves C TLS state
      |
      v
libgemsi_dispatch.so
  - allocates request ID
  - records request
  - queues stack-owned call envelope
  - waits on per-call condition variable
      |
      v
Scala Native-owned thread in libgemsi-core.so
  - decodes generated opcode
  - invokes logical vendor handler
  - catches every exception
  - completes response
```

Only the wire request and response cross the language boundary. They contain fixed-width values and no application,
Scala, or guest pointers. The C-only call envelope contains queue links and synchronization objects but is not part of
the serializable protocol.

## JVM IPC transport

The trait-derived Driver path is now isolated:

```text
CUDA application
      |
      v
generated libcuda.so.1
  - copies C inputs into bounded, big-endian payloads
  - sends schema fingerprint + 128-bit ID + exact name
      |
      v
authenticated loopback TCP connection
      |
      v
server
  - validates framing, token, schema, ID, name, and typed payload
  - invokes GeneratedCudaRpc.Dispatcher
      |
      v
CUDAEmulatorServer
```

The server binds only to the loopback interface on an ephemeral port. The conformance runner supplies a random
256-bit token to both processes. Names are capped at 128 bytes and request/response payloads at 64 MiB. Multi-byte
values use network byte order; every variable-length byte array has a checked length prefix. The current C socket
backend is POSIX; the framing and TCP design are portable, while Windows still needs its Winsock implementation.

## Recording and blob evolution

Setting `GEMSI_RECORD` to a path records ordered JSON-lines events. The JVM RPC record contains exact function name,
message ID, payload sizes, CUDA status, and response size. Validation failures that do not reach the legacy queue are
recorded as boundary events there.

Host blobs currently travel inline as owned copies. Content-addressed or shared-memory blob transfer can replace that
representation above a size threshold without changing the generated dispatcher; the JVM will continue to receive
owned bytes rather than a foreign address.

Kernel launches follow the same rule. The facade retains only opaque module/function handles and the parameter-width
signature returned by function lookup. It copies the caller's parameter values into a bounded launch message before
RPC; the server never sees or dereferences the native `kernelParams` pointers.

The initial CUDA Runtime compatibility calls are C-side compositions over this Driver transport. This lets host-only
allocation/copy programs use the same server without introducing a second RPC schema; Runtime registration and launch
machinery remain a separate, fail-closed boundary.

Callbacks will use reverse messages. C-owned callback dispatchers—not Scala code—will invoke host function pointers.
