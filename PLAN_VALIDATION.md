# SPIR-V Validation

Status: future work, not scheduled. Everything this work needs to know about the repository is in this file.

## Goal

Gemstone rejects every SPIR-V module the specification makes invalid, for the environments it runs: OpenCL compute (`Kernel`) and Vulkan (`Shader`, with `GLCompute`, `Vertex` and `Fragment` entry points). Each rejection names the rule, cites its source and points at the instruction.

spirv-val (from SPIRV-Tools) is the reference, not the oracle. It is incomplete and accepts some modules that are invalid by construction. Where Gemstone and spirv-val disagree, the specification decides, and the disagreement is recorded.

## Non-goals

- Optimisation or legalisation passes (spirv-opt).
- Pipeline state outside the module, such as descriptor set layouts against pipeline layouts, or vertex input formats.
- Extensions Gemstone does not execute. An unmodelled extension is rejected as unsupported, not validated.
- Beating spirv-val on speed. Validation runs once at load and must stay linear.

## Sources

| Source | Where | Use |
|---|---|---|
| SPIR-V grammar JSON (core, `GLSL.std.450`, `OpenCL.std`) | KhronosGroup/SPIRV-Headers; per-version copies in `.cache/conformance/opencl_spirv_headers/include/spirv/<version>/` | operand shapes, capability, extension and version requirements |
| SPIR-V specification (unified) | registry.khronos.org/SPIR-V/specs/unified1/SPIRV.html | prose rules: types, layout, control flow, memory model |
| Vulkan "SPIR-V environment" appendix and Valid Usage IDs (VUIDs) | the Vulkan specification (KhronosGroup/Vulkan-Docs); the VUID list is published as `validusage.json` with specification builds and in the Vulkan SDK | Vulkan rules |
| OpenCL SPIR-V environment specification | registry.khronos.org/OpenCL (the `OpenCL_Env` document) | OpenCL rules |
| SPIRV-Tools validator | KhronosGroup/SPIRV-Tools, `source/val/validate_*.cpp` and its tests | cross-check for rule coverage and test cases; spirv-val and spirv-reduce are installed in `/usr/bin` |

## Principles

- **Generate what the grammar knows.** Operand counts, kinds and quantifiers, and the capabilities, extensions and versions every instruction, operand kind and enumerant requires, come from the grammar JSON at codegen time. They are not hand-written.
- **Prose rules are tables with provenance.** Each rule the grammar cannot express is one row in a codegen table, with the specification section it comes from and a short verbatim quote. Codegen fails if the quote is not found in the cited section. The PTX operand rules already work this way (`tools/codegen/src/main/scala/net/kurobako/gemstone/codegen/PTXOperandRules.scala`, checked by `GeneratePTXDomains`). A Vulkan rule also carries its VUID.
- **One engine, rules as data.** A rule is a predicate over a small set of module facts: types, decorations, definitions and uses, the dominator tree and constructs. Rules do not walk the module themselves.
- **Environment as data.** OpenCL and Vulkan differ by rule sets and parameters selected by `Validation.Environment`, never by branches through the checker.
- **No cost on execution.** Validation runs in `LoadedProgram.load` (`isa/spirv-vm/src/main/scala/net/kurobako/gemstone/isa/spirv/LoadedProgram.scala`), before the program executes. `loadUnchecked` in the same file skips it and stays as it is. Facts are built once per module. The check allocates nothing per operand unless a rule breaks: precompute rule tables in object-level `val`s, iterate with index loops, and build messages only on a violation. A first version of the operand constraint check that used nested `flatMap`/`collect` over every operand allocated about 120 MiB per OpenCL CTS benchmark run; the loop form allocates nothing.
- **Every rule is tested both ways.** Each rule has a valid module and a module with exactly that one rule broken.

## What already exists

- `isa/spirv-asm/src/main/scala/net/kurobako/gemstone/isa/spirv/Validation.scala`: header, version and environment checks. `Validation.Issue` lists the rejections (invalid header, invalid operand, missing `Kernel` or `Shader` capability, missing entry point, missing memory model, unsupported version). `Validation.Environment` selects OpenCL compute, OpenCL library, Vulkan graphics or Vulkan compute.
- `OperandConstraintCheck.scala` (same package) and the generated `isa/spirv-asm/src/generated/.../OperandConstraintTable.scala`: 46 instruction rows and 3 operand-kind rules (result types name a type, scope operands are scope values, memory semantics use only defined bits), cited to the SPIRV-Tools 1.4.357.0 validator functions they mirror. Some rows were relaxed to what the VM loads: integer widths of any whole number of bytes, float widths of 6, 8, 16, 32 and 64 bits, pointer vector components under `MaskedGatherScatterINTEL`. `OperandRule`, `KindRule` and `OperandViolation` are in their own files next to it.
- `tools/codegen/src/main/scala/net/kurobako/gemstone/codegen/GenerateSPIRVISA.scala` reads the core grammar and the `GLSL.std.450` and `OpenCL.std` extended instruction grammars and writes the instruction model; `SPIRVOperandConstraints.scala` next to it holds the constraint rows.
- Coverage: `explorer/src/test/scalajvmnative/net/kurobako/gemstone/explorer/CoverageSuite.scala` walks every instruction form of every target and classifies it; exceptions are listed in `explorer/src/test/resources/coverage/policy.tsv` with a reason each. Every SPIR-V form has an executable semantic (no Mechanics or NotImplemented forms remain), so every instruction validation accepts can run.
- Corpora: the OpenCL CTS and Vulkan conformance modules under `conformance/`, the explorer examples, and the SPIR-V test fixtures in `isa/spirv-asm/src/test` and `isa/spirv-vm/src/test`.

## Architecture

The checker runs in layers. Each layer consumes the facts of the layers below it.

| Layer | Checks | Source |
|---|---|---|
| L0 structure | operand count, kind and quantifier; logical layout order (capabilities, extensions, imports, memory model, entry points, execution modes, debug, annotations, types, functions); ids defined once, forward references only where allowed | grammar JSON, specification 2.4 |
| L1 requirements | capabilities, extensions and version required by every instruction, operand kind and enumerant, with implicit declarations (capability `depends`) followed transitively | grammar JSON |
| L2 types | result and operand type rules: scalar, vector, matrix, pointer and storage class, width, signedness, component count, and relations such as "same type as Result Type" or "same component count as Operand 1" | specification prose, transcribed per instruction |
| L3 functions and control flow | block and terminator rules, dominance (definitions dominate uses, `OpPhi` parents), structured control flow (selection, loop and continue constructs, merge and back-edge rules) | specification 2.11 to 2.16 |
| L4 decorations and layout | `Block` and `BufferBlock`, `Offset`, `ArrayStride` and `MatrixStride` against std140, std430 and scalar layout, `BuiltIn` types and storage classes, interface `Location` and `Component` matching | specification, Vulkan environment appendix |
| L5 environment | Vulkan rules (allowed capabilities, storage classes, execution models and modes, VUIDs) and OpenCL rules (addressing models, `Kernel` restrictions, `OpenCL.std` use) | Vulkan environment appendix and VUIDs; OpenCL SPIR-V environment specification |
| L6 memory model | scope and memory semantics operands per instruction and environment, atomics, `Volatile`, the Vulkan memory model | specification 3.25, 3.26 and the memory model appendix |

The facts are built once per module: a type table, decoration sets per id, definition and use indexes, per-function CFG, dominator trees (O(n log n)) and the construct tree. A rule row names the instruction or operand kind it applies to, the predicate over facts, the environments it holds in, its citation and its message. `Issue` gains a rule id and a citation, and the explorer shows both.

## Phases

Task classes: **H** is transcription from a written source into tables and tests, suited to a cheaper model with every diff reviewed; **S** is engine design, which needs a stronger model.

| Task | Class | Work |
|---|---|---|
| V0 | S | Differential harness: run Gemstone's checker and spirv-val over the corpora, and over mutated modules that are one rule away from valid (a generator over Gemstone's own module model, with `spirv-reduce` to minimise). Triage file: one row per disagreement with verdict (Gemstone wrong, spirv-val misses, specification ambiguous) and citation. |
| V1 | H | L0 and L1 generated from the grammar: layout order, operand shape, transitive requirements for instructions, operand kinds and enumerants, per SPIR-V version. |
| V2 | S | Type algebra and the rule engine; move the existing operand constraint rows onto it. |
| V3a, V3b, V3c | H | L2 type rules transcribed with quotes, one task per instruction class (arithmetic and conversion; composite, memory and pointer; group, image and extended instruction sets). |
| V4 | S | L3 control flow: CFG, dominance, structured control flow and `OpPhi`. |
| V5 | H | L4 decorations and layout rules. |
| V6a | S | L5 Vulkan: rule set from the environment appendix and the SPIR-V VUIDs, per Vulkan version. |
| V6b | H | L5 OpenCL: rule set from the OpenCL SPIR-V environment specification. |
| V7 | S | L6 memory model rules. |
| V8 | H | Coverage check in the default test run, in the style of `CoverageSuite`: every instruction and operand kind has its rules, or a reason why none apply. The explorer shows the rules for each form. |

About 13 tasks. V0 runs first, and its triage file guides every later task.

## Checks for every task

- Build with sbt 2 through the running server (`sbt --client`). The modules are `spirvAsm` (`isa/spirv-asm`), `spirvVm` (`isa/spirv-vm`), `explorer` and `codegen` (`tools/codegen`). Regenerate checked-in sources with `sbt --client codegen`.
- Run tests with `testOnly *` (for example `sbt --client "spirvAsm/testOnly *"`). Plain `test` runs only tests whose inputs changed.
- `Test/compile` for the whole build, `scalafmtCheckAll`, and `<module>/scalafix --check` on the modules touched (generated code under `src/generated` has known findings; only hand-written files must be clean).
- `MethodSizeSuite` (in `isa/spirv-vm` tests) keeps hot methods under the JIT inlining size limit; split large methods per form if it fails.
- Never change an existing test's expectation without a triage row that shows the old expectation was wrong.

## Validation of the validator

- Per rule: a valid module and a one-violation module, both committed as tests.
- Differential: every disagreement with spirv-val is triaged before a task is accepted. Disagreements where spirv-val misses a rule become committed tests with the citation.
- Corpora: every module the conformance runner executes must still load unless a triage row shows it is invalid.
- Performance: the SPIR-V benchmark cases (`tools/benchmarks`; `BenchmarkRunner run spirv-execution`, `opencl-cts-async-copy`, `spirv-cooperative-matrix`, `spirv-image-sampling`) record JFR by default. Compare total allocated bytes (`jdk.ThreadAllocationStatistics`) and CPU execution samples against the build before the change, in alternating base and new runs; neither may grow. The execute path does not change.

## Risks

- **Strictness against practice.** Drivers and spirv-val accept some invalid modules, and some CTS or application modules may depend on that. A strict default could reject modules that run elsewhere. Mitigation: each rule row records whether spirv-val enforces it, and the triage file shows what would be rejected before a rule ships.
- **Specification ambiguity.** Some prose is unclear or changes between versions. Rows cite the version, and ambiguous rules are listed rather than guessed.
- **Transcription errors.** The quote check at codegen catches misquotes, not misreadings. The one-violation tests and the differential harness catch most misreadings.
- **Size of the Vulkan rule set.** The VUID list is large. V6a starts from the VUIDs that apply to compute and the vertex and fragment stages Gemstone runs.

## Open questions

- When the specification and spirv-val disagree and the module runs on drivers: reject (strict), or report as a warning (compatible)?
- Which SPIR-V versions (1.0 to 1.6) and Vulkan versions (1.0 to 1.4) are in scope, and which extensions beyond those Gemstone executes?
- Should validation also run as a standalone tool (a CLI next to the explorer) for modules Gemstone cannot execute?

## Sibling task: PTX register-declaration typing

PTX modifier domains, operand types and value limits are already checked (`isa/ptx-asm/src/main/scala/net/kurobako/gemstone/isa/ptx/Validation.scala`, from `isa/ptx-asm/src/generated/.../operand-domains.json`). The PTX ISA document's type-checking tables (Tables 26 to 28: which instruction types accept which register types, and the relaxed source and destination rules) are in `operand-domains.json` but not applied to register operands. Apply them using the register declarations in the module, with ptxas as the differential reference. One or two tasks, with the same checks as above for `ptxAsm` and `ptxVm`.
