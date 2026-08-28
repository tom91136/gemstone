# Generated API transport

The generated `CUDASurface` owns the 820 declarations from the pinned CUDA 13.3 Driver and Runtime headers. The
handwritten `CUDA` overlay refines the implemented RPC and native-delegate subset and adds the compatibility/compiler
entry points; concrete defaults fail as unsupported. Exact C function names identify methods, and a Scala 3 quoted
macro derives the export inventory plus typed schemas, IDs, proxies, dispatch, and fingerprints. See
[CUDA_RPC.md](CUDA_RPC.md).

`codegen` currently emits:

- `gemstone_status.h`: transport-independent CUDA status constants used by generated C;
- `gemstone_cuda.map`: the exact exported ELF symbol surface and version node;
- `cuda-rpc-schema.json` and `gemstone_cuda_rpc.inc`: trait-derived names, types, IDs, and schema fingerprint;
- `gemstone_cuda_rpc_client.c`: generated CUDA Driver ABI projection and typed marshalling;
- `GeneratedCUDARPC.scala`: typed client and receiving dispatcher for JVM and Scala Native;
- `cuda-driver-surface.json`: the 484-method Driver inventory derived from the Scala `CUDA` trait and each method's implementation status.

Generated C outputs and manifests are checked into `generated/`; generated Scala is owned by
`api/src/generated/main/scala` so native C consumers and Scala builds use the same reviewed protocol.
Run the generator with `sbt 'codegen/run generated api/src/generated/main/scala'` and verify it with
`sbt 'codegen/run --check generated api/src/generated/main/scala'`.

## IPC transport

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
  - invokes GeneratedCUDARPC.Dispatcher
      |
      v
CUDAEmulatorServer
```

The current JVM endpoint binds only to the loopback interface on an ephemeral port. The conformance runner supplies a random
256-bit token to both processes. Names are capped at 128 bytes and request/response payloads at 64 MiB. Multi-byte
values use network byte order; every variable-length byte array has a checked length prefix. The current C socket
backend is POSIX; the framing and TCP design are portable, while Windows still needs its Winsock implementation. The
server semantics, generated dispatcher, and PTX execution layer also compile as `serverNative`; a Native transport
endpoint can be added without changing the API or wire schema.

## Recording and blob evolution

Setting `GEMSTONE_RECORD` to a path records ordered JSON-lines events. The JVM RPC record contains exact function name,
message ID, payload sizes, CUDA status, and response size.

Host blobs currently travel inline as owned copies. Content-addressed or shared-memory blob transfer can replace that
representation above a size threshold without changing the generated dispatcher; the JVM will continue to receive
owned bytes rather than a foreign address.

Kernel launches follow the same rule. The facade retains only opaque module/function handles and the parameter-width
signature returned by function lookup. It copies the caller's parameter values into a bounded launch message before
RPC; the server never sees or dereferences the native `kernelParams` pointers.

The initial CUDA Runtime compatibility calls are C-side compositions over this Driver transport. This lets host-only
allocation/copy programs use the same server without introducing a second RPC schema; Runtime registration and launch
machinery remain a separate, fail-closed boundary.

Callbacks use reverse messages. C-owned callback dispatchers—not Scala code—invoke host function pointers.
