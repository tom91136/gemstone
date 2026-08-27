# AMDGPU ISA

This module provides aligned JVM/Scala Native AMDGPU APIs under architectural-family packages. Generated data and
tests are checked in so normal compilation is offline and upstream changes produce reviewable diffs.

- `net.kurobako.gemstone.isa.amdgpu.gfx6` through `gfx12`; and
- `net.kurobako.gemstone.isa.amdgpu.cdna1` through `cdna5`.

Each package exports the same `Isa`, `Parser`, `Printer`, `Program`, `Statement`, generated `Instruction`, `Operand`,
and `Modifier` vocabulary as the PTX side. Its top-level `Version` identifies the generation and top-level
`Instructions` contains only metadata for that family. SOPP encoding/decoding remains bound to the package family.
Reusable machinery and private aggregate metadata live in `amdgpu.common`; clients should import a versioned package.

## Generated inputs

`generateAmdGpuSources` normalizes two independently pinned sources into checked-in instruction enum, metadata, and
per-family package sources:

- Mesa ACO contributes 3,821 GFX6–GFX9 opcode/format records. `codegen/amdgpu/export_aco.py` loads the pinned
  `aco_opcodes.py` model and exports only primitive data; the Scala generator owns normalization.
- AMD's machine-readable XML contributes 40,059 GFX10+, RDNA, and CDNA instruction-encoding records, including
  typed operand direction, width, implicitness, format, opcode, and functional group.

The public AST has one generated Scala 3 `Instruction` enum with one parameterized case per normalized operand form.
Each case exposes meaningful positional fields such as `dest`, `srcA`, `srcB`, `srcAddr`, `destAddr`, and `mods`.
A closed `InstructionDef` descriptor set has one singleton per canonical opcode and owns that opcode's decoder and
fully structured `Metadata(Vector(Encoding(...)))` value directly; there is no encoded string table or index lookup.
`Instructions` is its sole ordered enumeration. Each decoded instruction exposes its descriptor, opcode,
decoder, metadata, and operand vector directly. Text and binary decoding construct these cases or return a typed
error. There is no raw unknown-instruction node, detached metadata catalog, or second “known instruction” layer.

## Version granularity

The package boundary is the major encoding family (`gfx9`, `gfx10`, and so on), not an individual processor such as
`gfx900` or `gfx906`. That is the finest distinction consistently present across the current ACO/XML normalization.
Exact processors should become targets within their family when feature predicates and stepping-specific opcode
availability are imported; making each processor a package now would duplicate the API while pretending the current
metadata is more precise than it is. RDNA revisions currently normalize to their corresponding GFX family, while
CDNA remains a separate family sequence because the official XML identifies it independently. Generated records
retain their exact XML subfamily (`rdna1`, `rdna2`, `rdna3_5`, and so on), and the codec rejects a family-level
operation if those records disagree instead of choosing one silently.

`generateAmdGpuGoldenTests` scans the pinned LLVM `llvm/test/MC/AMDGPU` tree as an independent oracle. It currently
retains 308,161 architecture-tagged assembly entries. The JVM suite checks every entry; Scala Native runs the focused
parser, descriptor, codec, and semantics suites. A checked, bounded inventory records the 170 oracle entries whose 15
opcodes are genuinely absent from the pinned Mesa/AMD production metadata, so unsupported input is never silently
skipped or admitted into the AST.

## Current binary boundary

`Binary` encodes and decodes the common 32-bit SOPP layout for GFX6–GFX12, including the official GFX11/GFX12
opcode renumbering. Unsupported formats return a typed `CodecError`; no fallback guesses an encoding.

The normalized catalog does not yet contain conditional bit-field rules, so VOP, SMEM, VMEM, DS, image, and dual
VOPD binary encoding/decoding remain intentionally unsupported. The parser also excludes LLVM's `::` dual-VOPD
spelling from the generated round-trip corpus until the AST gains an explicit paired-instruction statement. LLVM
diagnostic checks are retained as reference material but are not yet converted into semantic-invalid test cases.
