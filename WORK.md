# Architecture Migration Work Log

## Purpose

This is the mutable execution record for [PLAN.md](PLAN.md). It tracks progress, implementation decisions, verification results, known failures, and the next safe action.

`PLAN.md` is frozen. Do not edit it during implementation unless the user explicitly reopens it. Clarifications that do not change the architecture belong here. If implementation reveals that the plan itself must change, stop, record the issue under **Decision required**, and ask before editing the plan.

## Current status

- Overall state: implementation complete.
- Active phase: Phase 8 complete.
- Active work item: None; ready for review.
- Architecture implementation changes made: the typed core and runtime boundaries are in place; PTX, SPIR-V, and AMDGPU expose typed VMs and runtime backends; RPC DTOs are isolated in `ExecutionProtocol`; the legacy execution model and its memory bridge are deleted.
- Documentation created: `PLAN.md`, `WORK.md`.
- Important working-tree condition: PTX contains active unrelated user refactoring. Re-read its files and status immediately before every PTX task.

## Working discipline

- Re-read affected files immediately before editing.
- Prefer small hand-written patches; do not use bulk rewrite scripts unless the transformation and review boundary are explicit.
- Preserve unrelated working-tree changes.
- Never edit generated output directly. Find and change its generator, regenerate through the existing task, and inspect the complete diff.
- Record each meaningful implementation choice in the decision log before depending on it elsewhere.
- Run focused verification after each work item and record the exact command and result.
- A phase is complete only when its exit criteria in `PLAN.md` are met.
- New or changed source must be lint-clean when introduced.

## Phase progress

| Phase | State | Summary |
|---|---|---|
| 0 | Complete | Inventory, baseline, lint stabilization, terminology cleanup |
| 1 | Complete | Clean ISA and VM interfaces compile and are tested on JVM and Native |
| 2 | Complete | Runtime boundary and fake-backend verification are green |
| 3 | Complete | PTX implements the typed VM/backend directly; temporary execution bridges are gone |
| 4 | Complete | SPIR-V implements the typed VM/backend directly with cohesive source ownership |
| 5 | Complete | AMDGPU implements the typed VM/backend directly with isolated loaded globals |
| 6 | Complete | Server owns backend sessions/runtime programs and submits VM `KernelTask` values directly |
| 7 | Complete | RPC owns wire DTOs and uses an open string encoding; native callers use the current schema |
| 8 | Complete | Legacy execution and memory adapters are deleted; lint and the full JVM/Native matrix are green |

States used here: `Not started`, `Ready`, `In progress`, `Blocked`, `Complete`.

## Phase 0 checklist

- [x] Re-read repository status and identify overlapping user work.
- [x] Record JVM and Native compile/test baseline.
- [x] Run the initial full Scalafix baseline.
- [x] Reduce repeated JVM/Native diagnostics to a unique source inventory.
- [x] Map each generated failure to its generator.
- [x] Inventory legacy execution types and every consumer.
- [x] Inventory deprecated aliases, compatibility helpers, fallback branches, pass-through wrappers, duplicate validation, and other migration residue; assign each item a deletion phase.
- [x] Inventory RPC codec derivation in domain sources.
- [x] Remove the prohibited terminology identified in `PLAN.md` from repository-authored sources.
- [x] Add the repository terminology check with narrow, reviewed exclusions.
- [x] Fix unrelated lint failures that would obscure the architecture migration.
- [x] Re-run the full lint baseline and record remaining ISA migration failures.

## Known baseline

### Scalafix

Command run on 2026-09-20:

```text
sbt 'scalafixAll --check'
```

Result: failed, as expected. The first observed categories were:

- filename/top-level-declaration cohesion failures in handwritten core, server, codegen, benchmarks, AMDGPU, and SPIR-V sources;
- the same cohesion failure in generated native API and generated AMDGPU/SPIR-V sources;
- forbidden direct `java.lang.Runtime` use in `GemstoneLauncher`.

The aggregate reported failures in JVM and Native variants, so many diagnostics are duplicates. The output is not considered exhaustive because the filename rule emits one actionable diagnostic per file and later failures may be masked until earlier ones are fixed.

The committed baseline compiles and tests cleanly across `core`, `ptx`, `amdgpu`, `spirv`, and `server` on both JVM and Scala Native. This is the behavioural baseline for the migration.

After the first cleanup batch, the full check reports 29 unique failing source files: five generated API surfaces; seven legacy core ABI/execution files; seven AMDGPU handwritten/generated files; two SPIR-V handwritten/generated files; four server files; three benchmark files; and one codegen utility. The forbidden-runtime diagnostic is resolved.

After generator-owned API cleanup plus server, codegen, and benchmark cohesion fixes, the remaining 16 files are all migration-owned: seven legacy core ABI/execution files, seven AMDGPU files, and two SPIR-V files.

### Notable generated-source owners to locate

- Native API surfaces: CUDA, HIP, HSA, OpenCL, Level Zero.
- AMDGPU instruction definitions, metadata, architecture facades, and encoding constants.
- SPIR-V instruction definitions and metadata.
- PTX generated instruction model is currently the reference layout and should remain green.

## Decision log

| ID | Date | State | Decision |
|---|---|---|---|
| D-001 | 2026-09-20 | Accepted | `PLAN.md` is frozen; implementation tracking and non-architectural clarification go in `WORK.md`. |
| D-002 | 2026-09-20 | Accepted | Rename the current exported-symbol summary from `ProgramCatalog` to `ProgramSymbols`; reserve `BackendCatalog` for backend registration and `ProgramStore` for loaded runtime ownership. |
| D-003 | 2026-09-20 | Accepted | Treat PTX's cohesive root/companion/component organization as the reference shape for AMDGPU and SPIR-V without forcing PTX terminology or stages onto them. |
| D-004 | 2026-09-20 | Accepted | The repository must become lint-green, including generated sources; fix generators rather than generated output and avoid blanket suppressions. |
| D-005 | 2026-09-20 | Accepted | Finish with one current implementation path: remove all pre-existing and migration-created legacy adapters, aliases, fallbacks, duplicate representations, forwarding-only layers, and obsolete fixtures. Add no speculative ABI/version negotiation or duplicate semantic checking outside the ISA-owned decoder, validator, loader, and VM; retain only necessary transport-safety, ownership, allocation-bound, and lifecycle/concurrency checks. |
| D-006 | 2026-09-20 | Accepted | The JVM/Native compile slice validates the planned typed core: `isa.Program`/`Decoder`/`Printer`; VM-owned `KernelTask`, `MemoryRegion`, `Memory`, `AddressSpace`, `TaskControl`, `Scheduler`, `Observer`, `Fault`, `Result`, and `VirtualMachine`; and format-neutral symbols/metadata without wire codecs. Geometry and task-owned vocabulary live in their owning companions. |
| D-007 | 2026-09-20 | Accepted | `Observer.observe` receives a non-wire `ObservationContext` carried by `TaskControl`. The server maps its internal `TaskToken` to that context, allowing concurrent execution events to be correlated without placing `CompletionId` or another API identity in `KernelTask`. |
| D-008 | 2026-09-20 | Accepted | `ExecutionProtocol` owns only process-neutral RPC DTOs and codecs. `ExecutionRPCProtocol` maps launch fields once into a VM `KernelTask`; `ExecutionService` accepts the API `ProgramId` separately, resolves the runtime program, and never reintroduces a transport dispatch model. |

## Migration-residue inventory

- Removed the legacy execution model (`ProgramFormat`, `ProgramDecoder`, `ProgramInstance`, `ProgramExecutor`, `KernelDispatch`, `ExecutionMemory`, `ExecutionControl`, and `ExecutionOutput`) with its final consumers in Phase 6/7.
- Remove the `GEMSTONE_CUDA_TARGET_ARCH` compatibility spelling in the final cleanup after all in-tree callers use `GEMSTONE_CUDA_TARGET`.
- Treat CUDA/HSA symbol aliases required by vendor ABIs and AMDGPU ELF/kernel aliases as real external or ISA semantics, not migration aliases.
- Deleted each fake backend or old/new conversion introduced during Phases 2–6 after its replacement received real backend coverage.
- Deleted the PTX execution scheduler/cancellation bridges and old execution-value overload after callers moved to the VM-domain entry point.
- Deleted the SPIR-V cancellation, scheduler, output, and memory bridges after the interpreter moved to VM-domain values.
- Deleted AMDGPU's legacy decoder, task, control, scheduler, and memory bridges after code-object loading and dispatch interpretation moved to VM-domain values.
- Moved wire codec derivation to RPC DTOs; ISA and VM domain sources do not derive wire codecs.

## Decision required

None currently. The core interface compile slice passed on JVM and Native. Optional runtime facets and typed backend erasure remain Phase 2 work and must receive the same early cross-platform check.

## Active work item

Title: Final verification and residue audit
Phase: 8
State: Complete
Scope: Verify the completed architecture on JVM and Native, check native/conformance style, and remove only concrete residue exposed by those checks.
Files expected to change: `WORK.md` plus any source identified by a failing required check.
User changes to preserve: checkpoint commit `78886d4`, especially the current PTX refactor.
Acceptance checks: all ISA/server JVM and Native tests, full Scalafix, terminology, native lint, and conformance lint.
Notes: Make only hand-reviewed source patches; generated files must be changed through their generators.

## Verification ledger

| Date | Scope | Command | Result | Notes |
|---|---|---|---|---|
| 2026-09-20 | Repository Scalafix baseline | `sbt 'scalafixAll --check'` | Failed | Expected baseline; cohesion and forbidden-API failures recorded above |
| 2026-09-20 | Plan Markdown whitespace | `git diff --check --no-index /dev/null PLAN.md` | Passed | Documentation-only verification |
| 2026-09-20 | Pre-migration checkpoint | `git commit -m "checkpoint: snapshot before execution architecture migration"` | Passed | Commit `78886d4`; exact prior tree captured before implementation |
| 2026-09-20 | Architecture project compile baseline | `sbt 'core/compile' 'coreNative/compile' 'ptx/compile' 'ptxNative/compile' 'amdgpu/compile' 'amdgpuNative/compile' 'spirv/compile' 'spirvNative/compile' 'server/compile' 'serverNative/compile'` | Passed | JVM and Native compile cleanly before migration |
| 2026-09-20 | Architecture project test baseline | `sbt 'core/test' 'coreNative/test' 'ptx/test' 'ptxNative/test' 'amdgpu/test' 'amdgpuNative/test' 'spirv/test' 'spirvNative/test' 'server/test' 'serverNative/test'` | Passed | All selected suites passed; Native server link/test completed in 157 seconds |
| 2026-09-20 | Codestyle fixture baseline | `sbt 'codestyleTests/test'` | Passed | Sbt reported no tests to run; fixture wiring must be checked while adding terminology enforcement |
| 2026-09-20 | Refreshed repository Scalafix baseline | `sbt 'scalafixAll --check'` | Failed | Expected filename/cohesion failures in handwritten and generated sources plus direct `java.lang.Runtime` use |
| 2026-09-20 | Repository terminology | `sbt terminologyCheck` | Passed | Root lint task scans authored and generated text sources with narrow plan/licence/build-output exclusions |
| 2026-09-20 | Native and conformance codestyle | `sbt 'native/lint' 'conformance/lint'` | Passed | Terminology edits remain clang-format and native-codestyle clean |
| 2026-09-20 | New core ISA/VM compile slice | `sbt 'core/compile' 'coreNative/compile'` | Passed | Typed interfaces compile on JVM and Native without RPC/server imports |
| 2026-09-20 | Core regression and VM model tests | `sbt 'core/test' 'coreNative/test'` | Passed | JVM core and existing Native core suites passed |
| 2026-09-20 | Focused Native VM model | `sbt 'coreNative/testOnly net.kurobako.gemstone.vm.VMModelSuite'` | Passed | Four task/control/scheduler/observer tests passed |
| 2026-09-20 | Unique Scalafix source inventory | filtered `sbt 'scalafixAll --check'` diagnostics | Failed | 29 unique files remain; all current diagnostics are filename/cohesion failures |
| 2026-09-20 | Generated API facade layout | `sbt 'api/compile' 'apiNative/compile' 'api/test' 'apiNative/test' 'api/scalafixAll --check' 'apiNative/scalafixAll --check'` | Passed | API aliases are generated as companion members; generated sources are cohesion-clean |
| 2026-09-20 | Server runtime boundary | `sbt 'server/compile' 'serverNative/compile' 'server/scalafixAll --check' 'serverNative/scalafixAll --check'` | Passed | Open encoding and typed backend/session/program/global abstractions compile and lint on both platforms |
| 2026-09-20 | Runtime fake backend | `sbt 'server/testOnly net.kurobako.gemstone.server.execution.RuntimeBoundarySuite' 'serverNative/testOnly net.kurobako.gemstone.server.execution.RuntimeBoundarySuite'` | Passed | Covers routing, load/decode, execute, cancellation, observation, and close on JVM/Native |
| 2026-09-20 | AMDGPU cohesive source/generator layout | `sbt 'amdgpu/scalafixAll --check' 'amdgpuNative/scalafixAll --check'` | Passed | Program, Printer, Instruction, metadata, architecture facades, and execution-state roots are filename/cohesion clean |
| 2026-09-20 | AMDGPU loaded-state isolation | `sbt 'amdgpu/testOnly net.kurobako.gemstone.isa.amdgpu.VirtualMachineSuite' 'amdgpuNative/testOnly net.kurobako.gemstone.isa.amdgpu.VirtualMachineSuite'` | Passed | Two loads of one immutable code-object program have independent writable global images |
| 2026-09-20 | Remaining full Scalafix inventory | `sbt 'scalafixAll --check'` | Failed | Only the seven legacy core ABI/execution grouping files remained; ABI was subsequently given one cohesive facade, leaving the execution model as the deletion target |
| 2026-09-20 | Server runtime ownership switch | `sbt 'server/test' 'benchmarks/compile' 'benchmarksNative/compile' 'server/scalafixAll --check' 'serverNative/scalafixAll --check'` | Passed | Server uses only backend/session/runtime-program dependencies; real PTX, SPIR-V, AMDGPU global, lifecycle, and native CUDA boundary coverage passed |
| 2026-09-20 | Open RPC encoding native lint | `sbt 'native/lint' 'conformance/lint' terminologyCheck` | Passed | Generated C++ and all native callers use explicit encoding strings; no closed format tag remains on the wire |
| 2026-09-20 | Full Scalafix after server switch | `sbt 'scalafixAll --check'` | Failed | Remaining diagnostics are confined to the six legacy core execution grouping files; benchmark compile drift found by the aggregate was fixed afterward |
| 2026-09-20 | Legacy execution deletion | `sbt 'core/test' 'server/test' 'benchmarks/test'` | Passed | RPC DTOs moved to `ExecutionProtocol`; transport maps directly to `KernelTask`; obsolete execution declarations, memory address-space adapter, and benchmark were deleted; 61 tests passed |
| 2026-09-20 | Final repository Scalafix | `sbt 'scalafixAll --check'` | Passed | JVM and Native source sets satisfy the active lint rules |
| 2026-09-20 | RPC identifier Native portability | `sbt 'coreNative/test'` | Passed | Wire refined identifiers use cohesive top-level companions; Scala Native compiler crash from nested refined companions is eliminated; 11 focused tests passed |
| 2026-09-20 | Repository terminology and native style | `sbt terminologyCheck 'native/lint' 'conformance/lint' 'codestyleTests/test'` | Passed | Authored/generated terminology, native formatting, conformance formatting, and lint fixtures are green |
| 2026-09-20 | Final JVM/Native architecture matrix | `sbt 'core/test' 'coreNative/test' 'ptx/test' 'ptxNative/test' 'amdgpu/test' 'amdgpuNative/test' 'spirv/test' 'spirvNative/test' 'server/test' 'serverNative/test' 'benchmarks/compile' 'benchmarksNative/compile'` | Passed | All JVM suites passed; all Native projects compiled/linked and server/core Native suites passed; benchmark sources compile on both platforms |
| 2026-09-20 | Final server lifecycle preservation | `sbt 'server/test' 'serverNative/test' 'server/scalafixAll --check' 'serverNative/scalafixAll --check'` | Passed | Address-aliased allocations remain retained for the lifetime of pending completions without restoring the deleted address snapshot DTO |
| 2026-09-20 | Final residue and whitespace audit | legacy-name searches, empty-directory scan, `git diff --check` | Passed | No legacy execution types/imports or migration bridges remain outside the frozen plan/work history; no empty source directories or whitespace errors remain |

## Work history

- Phase 1 typed core model completed and verified on JVM and Native.
- Generator-owned API cohesion, server support, codegen, and benchmark baseline lint cleanup completed.
- Phase 2 runtime boundary and fake-backend behavioural slice completed.
- SPIR-V source/generator convergence and typed VM/backend adapters completed on JVM and Native.
- AMDGPU source/generator convergence, typed VM/backend adapter, and cross-load writable-global isolation completed on JVM and Native.
- `ExecutionService` and server composition now depend exclusively on `BackendCatalog`, `BackendSession`, and `RuntimeProgram`; the old server registry and its obsolete executor fixtures were removed.
- The RPC schema and generated C++ surface now carry an open encoding string; CUDA, HSA/HIP, OpenCL, Level Zero, and native RPC callers send their explicit encoding names.
- Server memory now implements VM `Memory`/`AddressSpace`, including stable synthetic addresses for allocations without native aliases; VM observer events feed completion print and relocation records.
- PTX, SPIR-V, and AMDGPU now consume only VM-domain task, memory, control, scheduler, observer, result, and fault types.
- RPC values and codecs now live under `ExecutionProtocol`; the server receives `ProgramId` separately from `KernelTask`, so API identity does not leak into the VM domain.
- The old execution package, old address-space adapter, migration bridges, and their obsolete tests/benchmark have been removed.

## Blockers and risks

- PTX is being independently refactored; stale assumptions are a direct overwrite risk.
- Generated API surface layout needs a deliberate generator-owned design before lint fixes are applied.
- AMDGPU mutable code-object global storage must be isolated per load.
- SPIR-V environment selection must become explicit without changing current API behaviour.
- JVM and Scala Native must both validate any path-dependent or erased backend design before it spreads.

## Next action

Review the completed implementation and create the final implementation commit when desired.
