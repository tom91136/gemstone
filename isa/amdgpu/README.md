# AMDGPU ISA

This module provides aligned JVM/Scala Native AMDGPU APIs under architectural-family packages. Generated data and
tests are checked in so normal compilation is offline and upstream changes produce reviewable diffs.

- `net.kurobako.gemstone.isa.amdgpu.gfx6` through `gfx12`; and
- `net.kurobako.gemstone.isa.amdgpu.cdna1` through `cdna5`.

The root `amdgpu` package owns `Parser`, `Printer`, `Program`, `Statement`, generated `Instruction`, `Operand`, and
`Modifier`. Each architecture package contains only its `family`, family-filtered `Instructions`, and target-checking
`ISA` profile. SOPP encoding/decoding remains bound to that profile; there is no aggregate compatibility package or
re-export layer.

## Generated inputs

`generateAMDGPUSources` normalizes two independently pinned sources into checked-in instruction enum, metadata, and
per-family package sources:

- Mesa ACO contributes 3,821 GFX6–GFX9 opcode/format records. `codegen/amdgpu/export_aco.py` loads the pinned
  `aco_opcodes.py` model and exports only primitive data; the Scala generator owns normalization.
- AMD's machine-readable XML contributes 40,059 GFX10+, RDNA, and CDNA instruction-encoding records, including
  typed operand direction, width, implicitness, format, opcode, and functional group.

The public AST has one generated Scala 3 `Instruction` enum with one parameterized case per normalized operand form.
Each case exposes meaningful positional fields such as `dest`, `srcA`, `srcB`, `srcAddr`, `destAddr`, and `mods`.
A completely generated `InstructionDef` descriptor set has one singleton per canonical opcode and owns that opcode's
decoder and fully structured `Metadata(Col(Encoding(...)))` value directly; there is no encoded string table or
index lookup. `Instructions` is its sole ordered enumeration. Each decoded instruction exposes its descriptor, opcode,
decoder, metadata, and operand collection directly. Text and binary decoding construct these cases or return a typed
error. There is no raw unknown-instruction node, detached metadata catalog, or second “known instruction” layer.

Generated definition sources are organized by their fundamental encoding domain rather than anonymous numeric chunks
or an arbitrary owning GPU generation. Scalar definitions are split into SOP1, SOP2, SOPC, SOPK, SOPP, and scalar
memory; vector definitions into VOP1, VOP2, VOP3, packed, compare, dual, interpolation, and matrix domains; and memory
definitions into buffer, data-share, flat/global/scratch, and image domains. An instruction supporting multiple GFX or
CDNA generations remains one definition with all of its encoding metadata attached. Large domains are divided by a
bounded generated-source size and named for their inclusive instruction range, such as `VOP2AddCoCiU32ToMaxI16`.
Each definition filename exactly matches its generated top-level object, for example
`VOP2AddCoCiU32ToMaxI16InstructionDefs.scala` and `VOP2AddCoCiU32ToMaxI16InstructionDefs`.

## Architecture granularity

The package boundary is the major encoding family (`gfx9`, `gfx10`, and so on), not an individual processor such as
`gfx900` or `gfx906`. That is the finest distinction consistently present across the current ACO/XML normalization.
Exact processors should become targets within their family when feature predicates and stepping-specific opcode
availability are imported; making each processor a package now would duplicate the API while pretending the current
metadata is more precise than it is. RDNA revisions currently normalize to their corresponding GFX family, while
CDNA remains a separate family sequence because the official XML identifies it independently. Generated records
retain their exact XML subfamily (`rdna1`, `rdna2`, `rdna3_5`, and so on), and the codec rejects a family-level
operation if those records disagree instead of choosing one silently.

`generateAMDGPUOracle` scans the pinned LLVM `llvm/test/MC/AMDGPU` tree as an independent oracle and writes ordinary,
reviewable `.s` fixtures grouped by exact processor target and upstream source file. Selection is deterministic and
coverage-driven: it retains every canonical opcode and every contributing upstream file, then adds evenly distributed
per-family/per-target opcode samples and witnesses for register, literal, modifier, suffix, instruction-width, and
cache-policy syntax. Diagnostics receive corresponding category, source-file, family, and target coverage.

The current checked-in oracle is 565 KB and contains 4,150 positive cases plus 1,074 diagnostics selected from
308,161 positive and 98,730 diagnostic LLVM cases. The manifest records both source and selected counts, and the JVM
suite verifies the selection while the focused handwritten parser, descriptor, codec, validation, and semantics suites
remain separate. Scala Native compiles only those focused handwritten suites. A checked inventory names LLVM opcodes
that are genuinely absent from the pinned Mesa/AMD production metadata, so unsupported input is never silently
admitted into the AST.

## Current binary boundary

`Binary` encodes and decodes the common 32-bit SOPP layout for GFX6–GFX12, including the official GFX11/GFX12
opcode renumbering. Unsupported formats return a typed `CodecError`; no fallback guesses an encoding.

The normalized catalog does not yet contain conditional bit-field rules, so VOP, SMEM, VMEM, DS, image, and dual
VOPD binary encoding/decoding remain intentionally unsupported. Unsupported LLVM spellings have an explicit bounded
inventory, and selected LLVM diagnostics are fed through conservative target validation and must never validate
successfully.
