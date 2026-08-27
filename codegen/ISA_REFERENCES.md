# ISA reference preparation

`codegen / prepareISAReferences` prepares the external inputs for the PTX, AMDGPU, and SPIR-V/compute API generators. It can be run
explicitly to prewarm the cache; ISA compilation also invokes it and downloads only artifacts absent from that cache.

Versions and SHA-256 digests are pinned at the top of `build.sbt`. Downloads use HTTPS and are stored in
`$XDG_CACHE_HOME/gemstone/isa-references`, falling back to the platform user's `.cache` directory. Every cache hit is
hashed before use. Set `GEMSTONE_ISA_REFERENCES_OFFLINE=1` to prohibit downloads and require a complete cache.

The task writes a versioned, generated view beneath `codegen / target / isa-references`:

```text
<fingerprint>/
  MANIFEST.tsv
  llvm/
    llvm/LICENSE.TXT
    llvm/test/MC/AMDGPU/...
  mesa/
    docs/license.rst
    licenses/MIT
    src/amd/compiler/aco_*
  amd/xml/
    amdgpu_isa_*.xml
  nvidia/ptx/index.html
  khronos/opencl/{LICENSE,CL/...}
  oneapi/level-zero/{LICENSE,include/...,doc/...}
  khronos/spirv-headers/{LICENSE,LICENSES/...,include/spirv/unified1/...}
  khronos/spirv-tools/{LICENSE,test/binary_*,test/val/val_opencl_test.cpp}
```

## Inputs

| Input | Purpose | Upstream |
| --- | --- | --- |
| LLVM source archive | Independent AMDGCN assembly/disassembly oracle from `llvm/test/MC/AMDGPU` | <https://github.com/llvm/llvm-project/releases> |
| Mesa source archive | GFX6–9 instruction catalogue and encoding rules from ACO's `aco_opcodes.py`, assembler, and related `aco_*` sources | <https://archive.mesa3d.org/> |
| AMD machine-readable ISA bundle | Official XML specifications for RDNA1–4 and CDNA1–5; each document declares AMD Public Use and MIT | <https://gpuopen.com/machine-readable-isa/> |
| NVIDIA PTX documentation | PTX ISA 9.3 reference distributed with CUDA 13.3 | <https://docs.nvidia.com/cuda/archive/13.3.0/parallel-thread-execution/index.html> |
| Khronos OpenCL Headers | Exact core and extension C symbol surface | <https://github.com/KhronosGroup/OpenCL-Headers> |
| oneAPI Level Zero headers | Exact core `ze*` C symbol surface and loader documentation | <https://github.com/oneapi-src/level-zero> |
| Khronos SPIR-V Headers | Universal 1.6 JSON grammar, enumerants, and licences | <https://github.com/KhronosGroup/SPIRV-Headers> |
| Khronos SPIRV-Tools | Independent binary/endian/OpenCL validation test material | <https://github.com/KhronosGroup/SPIRV-Tools> |

LLVM and Mesa licences are extracted beside their selected sources. The NVIDIA document remains a cached/generated
build input and is not vendored into the repository. `MANIFEST.tsv` records the exact URLs and hashes used.

Archive extraction is deliberately selective. It rejects absolute paths, `..`, links, unexpected archive roots,
duplicate files, oversized entries, and expansion beyond the configured total limit. Preparation is committed to a
content-fingerprinted directory only after every required file is present.

The preparation task itself only acquires and verifies reference material. The PTX generator consumes the document to
produce a typed instruction catalogue, version/SM/form constraints, and documentation-derived parser/round-trip tests.
The AMDGPU generators consume ACO, AMD XML, and LLVM MC material independently. LLVM's 770 AMDGPU assembly files are
extracted exhaustively into exact-target positive and diagnostic corpora; the checked-in TSV resources are compressed
and loaded by the JVM conformance suite instead of being emitted as tens of megabytes of Scala literals.

The SPIR-V generator emits the complete core instruction catalogue and a discovered reference suite. Binary parsing
is intentionally lossless for unknown opcodes, while Compute environment validation is fail-closed. OpenCL and Level
Zero generation emits complete exact-C-name traits; methods remain explicit unsupported stubs until their typed wire
signatures are implemented.
