# Trait-derived CUDA RPC

`net.kurobako.gemstone.cuda.CUDA` owns the complete 144-method GPUOcelot Driver surface. Every method name is the
exact exported C symbol. Implemented operations are abstract and typed; the remaining concrete methods return
`CUDA_ERROR_NOT_SUPPORTED` without entering IPC. The interface has no C-name or message-ID annotations.

`RpcDerivation.schema[CUDA]` is a Scala 3 quoted macro. At compile time it:

- selects the trait's abstract, non-overloaded methods as RPC endpoints and ignores concrete stubs;
- requires names matching CUDA C symbols (`cu*`, `cuda*`, or `__cuda*`);
- requires one parameter list, no method type parameters, `CudaResult[T]`, and explicitly supported wire types;
- derives a 128-bit message ID from `SHA-256(UTF-8(functionName))`;
- rejects any derived-ID collision;
- sorts methods by exact name and fingerprints the complete typed schema.

Requests carry both the derived ID and exact function name. The generated dispatcher validates both, and a generated
client rejects a server with a different schema fingerprint before making a call. This makes IDs compact without
allowing a hash collision, stale client, or corrupt mapping to silently invoke another endpoint.

The generator emits:

- `generated/manifest/cuda-rpc-schema.json`: reviewed names, types, IDs, and fingerprint;
- `generated/c/gemsi_cuda_rpc.inc`: C constants for the same methods and IDs;
- `generated/c/gemsi_cuda_rpc_client.c`: exact Driver ABI entry points and typed binary marshalling;
- `generated/scala/.../GeneratedCudaRpc.scala`: a typed `CUDA` client and receiving dispatcher.

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

`CudaConformanceSuite` is the external-language path. It discovers the pragma-described CUDA translation units,
compiles each enabled backend with `nvcc`, and runs them against either NVIDIA CUDA or the generated Gemstone
`libcuda.so.1`. The GPUOcelot-derived memory and module-launch Driver cases now run on both backends. The PTX vector
addition case additionally runs through the same external ABI/RPC path and through the direct Scala semantic oracle.
JSON-lines recordings include exact names, IDs, payload sizes, statuses, and response sizes.

For Runtime tests that contain no device code, the generated facade also provides local `cudaMalloc`, `cudaFree`, and
`cudaMemcpy` compatibility wrappers composed from the typed Driver RPC methods. Minimal fat-binary registration hooks
allow current `nvcc --cudart=none` host objects to link, but do not claim Runtime kernel-registration support.

The GPUOcelot CUDA 3.2 Driver inventory is now expressed directly by the trait and checked to contain 144 unique
symbols. A call moves from a concrete not-supported method to the abstract RPC subset only after its pointer direction,
byte ownership, lifetime, and C ABI projection have been reviewed.
