# Trait-derived CUDA RPC

`net.kurobako.gemstone.cuda.CUDASurface` owns the 820 declarations from the pinned CUDA 13.3 Driver and Runtime
headers. `CUDA` is the handwritten overlay that refines the implemented RPC and native-delegate subset and adds the
compatibility/compiler entry points. Every method name is the exact exported C symbol; the interface has no C-name or
message-ID annotations.

`RPCDerivation.schema[CUDA]` is a Scala 3 quoted macro. At compile time it:

- selects the trait's abstract, non-overloaded methods as RPC endpoints and ignores concrete stubs;
- requires names matching CUDA C symbols (`cu*`, `cuda*`, or `__cuda*`);
- requires one parameter list, no method type parameters, `CUDAResult[T]`, and explicitly supported wire types;
- derives a 128-bit message ID from `SHA-256(UTF-8(functionName))`;
- rejects any derived-ID collision;
- sorts methods by exact name and fingerprints the complete typed schema.

Requests carry both the derived ID and exact function name. The generated dispatcher validates both, and a generated
client rejects a server with a different schema fingerprint before making a call. This makes IDs compact without
allowing a hash collision, stale client, or corrupt mapping to silently invoke another endpoint.

The generator emits:

- `runtime/cuda/generated/manifest/cuda-rpc-schema.json`: reviewed names, types, IDs, and fingerprint;
- `api/src/generated/manifest/official-api-headers.json`: normalized declarations from the pinned vendor headers used
  to validate every API surface;
- `runtime/cuda/generated/c/gemstone_cuda_rpc.inc`: C constants for the same methods and IDs;
- `runtime/cuda/generated/c/gemstone_cuda_rpc_client.c`: exact Driver ABI entry points and typed binary marshalling;
- `api/src/generated/main/scala/.../GeneratedCUDARPC.scala`: a typed `CUDA` client and receiving dispatcher.

`CUDAEmulatorServer` is a normal implementation of `CUDA`. The JVM and Scala Native tests run the generated client
through the generated dispatcher and exercise device allocation, pointer-offset validation, byte transfer, and free.
`server` exposes that same generated dispatcher over an authenticated loopback TCP connection. The C transport
validates the schema fingerprint, derived ID, exact function name, framing lengths, response type, and CUDA status.
Host buffers are copied into bounded messages; no application pointer is dereferenced by the JVM process. Shared
memory can later optimize large blobs without changing the trait or dispatcher.

The typed subset now includes textual PTX module load/unload, entry-point lookup, and kernel launch. Function lookup
returns a checked parameter-width signature to the native facade. At launch, that facade uses the signature to copy
each value behind CUDA's otherwise untyped `void **kernelParams` array into the request. The JVM therefore receives
owned argument bytes, dimensions, and opaque handles rather than process addresses.

`CUDAConformanceSuite` is the external-language path. It compiles every pragma-described translation unit exactly once
with the normal `nvcc` command and stock CUDA libraries. The same executable first runs against NVIDIA as the oracle,
then unchanged through the generated Gemstone `libcuda.so.1`. Runtime-API tests retain stock `libcudart`; interception
happens at the downstream Driver boundary. The PTX vector-addition case additionally runs through the direct Scala
semantic oracle.

Every Gemstone execution receives a fresh authenticated endpoint. A successful process with no observed CUDA request
is rejected, so a loader mistake or vendor-only execution cannot appear green. The source has no Gemstone ABI define,
alternate link line, or `--cudart=none` mode.

The pinned CUDA 13.3 Driver inventory is expressed directly by the generated base trait and checked to contain 484
unique symbols, including the retained compatibility entry point. A call moves from a concrete not-supported method
to the abstract RPC subset only after its pointer direction, byte ownership, lifetime, and C ABI projection have been
reviewed.
