# ISA and Virtual Machine Cleanup Work Log

## Purpose

This is the mutable execution record for [CLEAN_UP_PLAN.md](CLEAN_UP_PLAN.md). The plan is frozen. Record progress, implementation findings, verification, and the next safe action here.

## Working discipline

- Re-read affected files immediately before editing, especially the actively refactored PTX package.
- Preserve unrelated working-tree changes.
- Prefer small hand-written patches; use generators only for generated sources and review their output.
- Keep touched source lint-clean and test JVM and Native variants at viable boundaries.
- Delete empty source directories as files move.
- Remove temporary bridges and aliases with their final consumer. The finished tree retains no migration residue.

## Current status

- Overall state: complete.
- Active phase: none.
- Active work item: none; the cleanup and final verification are complete.
- Important tree condition: this work continues on the existing architecture-migration and user PTX changes; do not rewrite or discard unrelated modifications.

## Decisions

- Each ISA owns a private `VMIR`; no cross-ISA IR is introduced.
- Each program-to-VMIR stage is named `VMCompiler`.
- PTX `ISA.lower` and `ISA.compile` are removed with their in-tree callers.
- AMDGPU uses one canonical enum-like VM instruction representation; the partial packed/fallback path is deleted.
- SPIR-V performance data is emitted through observation, not interpreter parameters.
- Core observation has an extensible `Event` root, while `VirtualMachine` directly owns `Observer[Event]` without an associated event type.
- Public ISA concepts and substantial VM collaborators remain top-level; small VM-only vocabulary nests under its owner. AMDGPU wave state, lane bits, vector registers, and scratch memory therefore live in the `VirtualMachine` companion.
- AMDGPU processor targets use one exact classification table. Validation, ISA family, and metadata generation derive from that table rather than duplicated constraints or numeric-name parsing.
- Collection pipelines use reusable lazy views rather than single-use iterator selections. Repository Scalafix rejects `iterator`, `keysIterator`, and `valuesIterator` selections, including Java collection calls.
- Runtime type inspection and unchecked casts are excluded from production Scala. Repository Scalafix rejects both casting selections; typed pattern matching owns the narrowing instead.
- Generated ISA definitions remain the instruction identity through decoding and VM compilation. Textual opcode names are confined to source parsing, printing, diagnostics, and generator input classification.
- Integral types follow the represented domain: bounded fields and array-backed sizes are `Int`, address arithmetic and complete register words remain `Long`, and narrowing occurs only after an explicit range proof, mask, or intentional 32-bit interpretation.

## Phase progress

| Phase | State | Summary |
|---|---|---|
| 1 | Complete | Core observation is open and every ISA owns a private `VMIR` and `VMCompiler` |
| 2 | Complete | Backend-owned encodings and one cohesive runtime globals capability |
| 3 | Complete | PTX has one VM surface, direct `VMLaneMask`, VM-owned loaded state, and no static trace/execution path |
| 4 | Complete | AMDGPU has one canonical VM instruction form and separate compiler, loader, loaded state, and VM components |
| 5 | Complete | SPIR-V has separate IR, compiler, loaded state, memory, invocation, and collectives; performance uses observation |
| 6 | Complete | Migration residue, obsolete branches, stale adapters, and empty directories removed; full verification complete |
| 7 | Complete | Decoder clarity, typed AMDGPU operations, cross-ISA semantic cohesion, ownership alignment, and residual local duplication |

## Active checklist

- [x] Make core `Event` extensible and remove `VirtualMachine.Event`.
- [x] Introduce PTX `VMIR` and `VMCompiler` ownership.
- [x] Introduce AMDGPU `VMIR` and `VMCompiler` ownership.
- [x] Introduce SPIR-V `VMIR` and `VMCompiler` ownership.
- [x] Compile and test every boundary on JVM and Native.
- [x] Remove temporary aliases, stale files, alternate paths, and empty directories.
- [x] Run lint and generated-source freshness checks.
- [x] Run compact conformance on hardware and emulation.
- [x] Centralize meaningful bit-field operations and clarify ISA decoding with named values and arguments.
- [x] Replace AMDGPU opcode strings in VMIR with typed semantic operations.
- [x] Remove PTX and SPIR-V semantic and construction residue found by the post-plan review.
- [x] Align small VM-only vocabulary under its owning namespace without flattening substantial ISA components.
- [x] Replace iterator-based collection pipelines with views and enforce the policy in repository Scalafix.
- [x] Re-run full lint, tests, and compact conformance where affected.

## Verification ledger

| Date | Scope | Command | Result | Notes |
|---|---|---|---|---|
| 2026-09-20 | Extensible core observation | `sbt 'core/compile' 'coreNative/compile' 'server/compile' 'serverNative/compile'` | Passed | Removed the VM-associated event type; all concrete VMs use the open core event root |
| 2026-09-20 | Server boundary source | `sbt 'server/compile' 'server/Test/compile'` | Passed | Encodings are backend-owned; runtime globals are one capability |
| 2026-09-20 | Cross-ISA integration | `sbt 'amdgpu/Test/compile' 'spirv/Test/compile' 'ptx/Test/compile' 'server/Test/compile' 'benchmark/Test/compile'` | Passed | All three final ISA shapes and downstream server/benchmark callers compile together |
| 2026-09-20 | Formatting and lint | `sbt scalafmtAll lint` | Passed | JVM/Native Scalafix, generated-source freshness, native style, conformance style, and repository terminology checks are green |
| 2026-09-20 | JVM and Native suites | `sbt test` plus clean-cache PTX/AMDGPU Native reruns | Passed | JVM: core 12, API 1, PTX 123, AMDGPU 318, SPIR-V 66, server 44, benchmark 3, codegen 4; Native: PTX 128, AMDGPU 336, SPIR-V 50, server 42. The shared sbt content cache initially replayed deleted Native IR test artifacts; isolated clean-cache reruns proved the source tree clean and green |
| 2026-09-20 | Native integration | `sbt native/test` | Passed | 68/68 CTest cases passed |
| 2026-09-20 | Conformance harness | `sbt conformance/test` | Passed | 541/541 CTest cases passed |
| 2026-09-21 | Compact hardware conformance | `sbt 'conformance/run --machine soraws-uk --workload compact'` | Passed | 2,664 cases across nine NVIDIA, AMD, Intel, OpenCL, Rusticl, HSA, HIP, CUDA, and SYCL routes; only expected profile/capability skips |
| 2026-09-21 | Compact emulated conformance | `sbt 'conformance/emulate --machine soraws-uk --workload compact'` | Passed | 1,132 cases across five unique ISA routes; zero failures and only expected profile/capability skips |
| 2026-09-21 | Post-plan JVM suites | `sbt test` | Passed | Core 9, PTX 89, AMDGPU 325, SPIR-V 66, server 36, and benchmark 3 tests passed; all remaining JVM aggregates were green |
| 2026-09-21 | Post-plan Native ISA suites | clean-cache `ptxNative/test`, `amdgpuNative/test`, and `spirvNative/test` | Passed | PTX 128, AMDGPU 335, and SPIR-V 66 tests passed from clean Native outputs; the final AMDGPU ownership move was rechecked on JVM and Native |
| 2026-09-21 | Native integration | `sbt native/test` | Passed | 68/68 CTest cases passed |
| 2026-09-21 | Conformance harness | `sbt conformance/test` | Passed | 541/541 CTest cases passed |
| 2026-09-21 | Final formatting and lint | `sbt scalafmtAll` and `sbt lint` | Passed | Formatting, Scalafix, generated-source freshness, native style, conformance style, and terminology checks are green |
| 2026-09-21 | AMDGPU target classification | `sbt scalafmtAll 'amdgpu/test' 'amdgpuNative/test'` and `sbt lint` | Passed | JVM 331/331 and Native 331/331; exact known targets classify through one table and speculative numeric names are rejected |
| 2026-09-21 | View-based collection traversal | `sbt test`, forced Native `testOnly`, `codestyleTests/testOnly fix.RuleSuite`, and `sbt lint` | Passed | Scalafix 8/8; JVM PTX 128, AMDGPU 336, SPIR-V 66, server 55; Native PTX 128, AMDGPU 336, SPIR-V 66, server 55; generated-source freshness and all lint checks green |
| 2026-09-21 | Typed generated identities and integral domains | `sbt lint` and forced JVM/Native `testOnly` suites | Passed | JVM core 56, PTX 128, AMDGPU 336, SPIR-V 66, server 55, codegen 4; Native core 56, PTX 128, AMDGPU 336, SPIR-V 66, server 55. Generated AMDGPU and SPIR-V freshness checks passed |

## Findings during implementation

- PTX direct interpreter tests and one benchmark were the only users of public `ISA.compile`; they now use the VM or narrow package-private compiler support.
- Moving SPIR-V performance to observation gives a concrete use for ISA-specific events. Core `Event` is therefore an open root, while an associated VM event member remains unnecessary.
- SPIR-V collective-scope classification belongs with collective semantics, so it moved from `VMIR` to `VMCollectives`.
- AMDGPU environment-driven semantic toggles and workload-name-specific tracing were implementation residue, not supported diagnostics; target/ISA semantics now determine behaviour.
- The shared sbt cache retained Native IR archives for deleted test suites. Clean isolated caches linked and passed all affected PTX and AMDGPU Native tests without compatibility shims or restored legacy types.
- Core `Bits` now owns the recurring signed and unsigned field extraction, bit tests, and low-byte operation used by ISA decoders.
- AMDGPU VMIR operations are typed enums rather than opcode strings. Decoder fields are named once, long constructors use named arguments, duplicate carry-decoding branches are collapsed, and class comparison and memory-offset inconsistencies found during the pass are corrected.
- PTX shares integer and floating semantic helpers across its staged and fast paths; unused datum handling, redundant masks, and ambiguous long constructors are gone.
- SPIR-V operand access belongs to `Operand`; compiler and interpreter no longer carry duplicate accessors, and its long program construction is named.
- AMDGPU's compact execution-state vocabulary is nested in `VirtualMachine`; PTX and SPIR-V retain only substantial state components and named VM subsystems at package level.
- The server's synthetic process-neutral allocation address is active canonical behaviour for allocations without a client address; its test no longer mislabels that address as an obsolete alias.
- sbt's shared content cache replayed a serialized Scala Native configuration containing a deleted Polyregion LLVM path. A fresh cache evaluated the installed LLVM 22 toolchain and the clean AMDGPU Native suite passed 335/335; no repository setting refers to Polyregion.
- AMDGPU target support and classification were two representations of the same closed set. They are now one exact table mapping processor names to ISA family and metadata family; the separate refined-type predicate, digit helpers, and permissive numeric ranges are gone.
- Iterator selections had spread across runtime, ISA, server, tests, generators, generated source, and Java boundaries. They now use views or a view-backed snapshot where indexed fail-fast traversal is required; a syntactic Scalafix rule prevents all three iterator-selection spellings from returning.
- AMDGPU decoding had discarded generated definition identity and reconstructed semantics from mnemonic strings. The opcode index, binary decoder, VM compiler, comparison metadata, and format-specific classification now pass generated definitions directly. SPIR-V operand decoding and PTX instruction rebuilding likewise retain their generated definition instead of performing a name round trip.
- Eager AMDGPU compiler sets of generated case objects could deadlock concurrent JVM suite initialization against the generated instruction catalog. Those immutable tables are lazy, removing class-initialization order as a runtime dependency.
- The integral review replaced repeated `Long` extraction followed by `.toInt` with bounded `Int` bit-field and little-endian helpers, made device and array-backed allocation sizes `Int`, kept wire/address sizes `Long`, and made SPIR-V single-word fields expose their native `Int` representation. Remaining narrowing sites are range-checked indices and sizes, masked shift/selector fields, numeric parsing, floating conversion, or deliberate signed 32-bit register interpretation.
- Final searches found no removed names, alternate public execution surfaces, ISA-to-server dependency, semantic debug branch, migration bridge, or empty source directory. The only excluded terminology occurrence is unchanged third-party licence text.
