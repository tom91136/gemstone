# AMDGPU ISA

This module provides aligned JVM/Scala Native AMDGPU APIs under architectural-family packages. Generated data and
tests are checked in so normal compilation is offline and upstream changes produce reviewable diffs.

- `net.kurobako.gemstone.isa.amdgpu.gfx6` through `gfx12`; and
- `net.kurobako.gemstone.isa.amdgpu.cdna1` through `cdna5`.

Each package exports the same `Isa`, `Parser`, `Printer`, `Program`, `Statement`, `Instruction`, `Opcode`, `Operand`,
`Modifier`, and catalog vocabulary as the PTX side. `Isa.catalog` is filtered to the package's family, and SOPP
encoding/decoding is bound to that family. Reusable machinery and the complete generated metadata database live in
the implementation package `amdgpu.common`; clients should import a versioned package.

## Generated inputs

`generateAmdGpuSources` normalizes two independently pinned sources into one internal `InstructionDatabase`, from
which the version packages expose family-scoped `InstructionCatalog` views:

- Mesa ACO contributes 3,821 GFX6–GFX9 opcode/format records. `codegen/amdgpu/export_aco.py` loads the pinned
  `aco_opcodes.py` model and exports only primitive data; the Scala generator owns normalization.
- AMD's machine-readable XML contributes 40,059 GFX10+, RDNA, and CDNA instruction-encoding records, including
  typed operand direction, width, implicitness, format, opcode, and functional group.

The public AST deliberately has one `Instruction` node with an `Opcode`, typed `Operand` values, and orthogonal
`Modifier` values. It does not generate a class for every opcode/operand combination.

## Version granularity

The package boundary is the major encoding family (`gfx9`, `gfx10`, and so on), not an individual processor such as
`gfx900` or `gfx906`. That is the finest distinction consistently present across the current ACO/XML normalization.
Exact processors should become targets within their family when feature predicates and stepping-specific opcode
availability are imported; making each processor a package now would duplicate the API while pretending the current
metadata is more precise than it is. RDNA revisions currently normalize to their corresponding GFX family, while
CDNA remains a separate family sequence because the official XML identifies it independently. Generated records
retain their exact XML subfamily (`rdna1`, `rdna2`, `rdna3_5`, and so on), and the codec rejects a family-level
operation if those records disagree instead of choosing one silently.

`generateAmdGpuGoldenTests` scans the pinned LLVM `llvm/test/MC/AMDGPU` tree. It deduplicates checks, samples evenly
across GFX6–GFX12, preserves extra SOPP coverage, and currently generates 1,605 assembly/exact-byte oracle cases.
The same generated suite compiles and runs on JVM and Scala Native.

## Current binary boundary

`Binary` encodes and decodes the common 32-bit SOPP layout for GFX6–GFX12, including the official GFX11/GFX12
opcode renumbering. Unsupported formats return a typed `CodecError`; no fallback guesses an encoding.

The normalized catalog does not yet contain conditional bit-field rules, so VOP, SMEM, VMEM, DS, image, and dual
VOPD binary encoding/decoding remain intentionally unsupported. The parser also excludes LLVM's `::` dual-VOPD
spelling from the generated round-trip corpus until the AST gains an explicit paired-instruction statement. LLVM
diagnostic checks are retained as reference material but are not yet converted into semantic-invalid test cases.
