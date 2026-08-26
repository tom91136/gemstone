# CUDA conformance tests

This is the active CUDA compatibility corpus. Every test is one self-contained `.cu` translation unit with a
`main`, performs its own assertions, prints exactly `pass` on success, and returns zero only when all checks pass.
The commands and expected output are source metadata using the same durable form as Polyregion's native tests:

```cpp
#pragma region case: runtime-malloc-copy
#pragma region targets: cuda,gemstone
#pragma region do: {nvcc} --allow-unsupported-compiler -std=c++17 -O2 -o {output} {input}
#pragma region do: {output}
#pragma region requires: pass
```

`CudaConformanceSuite` discovers every `check_*.cu` recursively and registers one MUnit test for each enabled backend.
Run it directly, or as part of `testFull`, with:

```console
sbt server/test
```

No Scala registry needs updating when a translation unit is added. With no `targets:` pragma a new test runs against
both `cuda` and `gemstone`; an explicit target list narrows it. Cases whose APIs or instructions are not implemented
yet are marked `targets: cuda`. Both Driver cases, the compact PTX semantic case, and the allocation/copy plus
interior-pointer Runtime cases currently run on Gemstone.

The suite builds the facade, starts a loopback-only JVM server with a random authentication token, substitutes
backend-specific link variables into the same pragma commands, executes each case in an isolated directory, and
compares stdout exactly with `requires:`. It also records Gemstone RPC calls and rejects failed statuses. `NVCC` or
`CUDA_HOME` can select a compiler explicitly; otherwise the newest detected CUDA release is used.

Semantic cases additionally declare `ptx-kernel`, `ptx-shape`, `ptx-input`, and `ptx-output` pragmas and embed their
module in an `R"ptx(...)ptx"` literal. The CUDA test loads that literal through NVIDIA's Driver API. The same Scala
suite discovers and extracts the identical PTX, runs it through Gemstone, and compares the declared output, so adding
a semantic case does not require a Scala registry edit. Enabled semantic cases also run end to end through Gemstone's
generated `libcuda.so.1`, RPC server, module registry, and PTX executor.

More substantial reference cases live under `samples/` and `thrust/`. Their implementation sources and licences are
vendored under `vendor/`; the system toolkit supplies only the CUDA compiler, ABI/runtime headers, and reference
runtime/driver libraries. Thrust cases use the additional metadata and command variables:

```cpp
#pragma region vendored: thrust,cub
#pragma region do: {nvcc} {cuda_vendor_includes} -MD -MF {dependencies} -o {output} {input}
```

The suite audits the dependency file and fails if compilation falls back to toolkit-provided Thrust or CUB headers.
See `vendor/README.md` for exact upstream revisions and licence details.

## Provenance and licence boundary

These are compact adaptations of tests in GPUOcelot revision `b16039dc940dc6bc4ea0a98380495769ff35ed99` under
`ocelot/src/cuda/test`. They retain GPUOcelot's BSD-3-Clause licence in this directory. The mapping is:

| Gemstone test | GPUOcelot source |
| --- | --- |
| `driver/check_memory.cu` | `driver/sequence.cpp` (Driver API setup and memory flow) |
| `driver/check_module_launch.cu` | `driver/sequence.cpp` and `driver/sequence.ptx` |
| `runtime/check_malloc_copy.cu` | `TestCudaMalloc.cu` |
| `runtime/check_pointer_offsets.cu` | `TestCudaMalloc.cu` |
| `runtime/check_pitched_copy.cu` | `TestCudaMalloc.cu` |
| `runtime/check_sequence.cu` | `TestCudaSequence.cu` |
| `runtime/check_shared_barrier.cu` | `TestCudaSequence.cu` |
| `runtime/check_globals.cu` | `TestCudaGlobals.cu` |
| `runtime/check_device_function.cu` | `TestFunctionCall.cu` |
| `runtime/check_indirect_function.cu` | `TestIndirectFunctionCall.cu` |
| `runtime/check_array_copy.cu` | `memory/arrays.cu` |

GPUOcelot's `tests/cuda2.2`, `tests/cuda2.3`, and `tests/cuda3.2` trees are intentionally excluded: they largely
contain NVIDIA SDK samples with separate or unclear redistribution terms. Ocelot-internal parser/executive unit
tests are also not copied here because they test GPUOcelot's C++ internals rather than an observable CUDA ABI.

The donor tree itself is not retained. Adapted tests carry their provenance, copyright, and BSD-3-Clause notice here.
