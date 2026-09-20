# Architecture Migration Work Log

## Purpose

This is the mutable execution record for [PLAN.md](PLAN.md). It tracks progress, implementation decisions, verification results, known failures, and the next safe action.

`PLAN.md` is frozen. Do not edit it during implementation unless the user explicitly reopens it. Clarifications that do not change the architecture belong here. If implementation reveals that the plan itself must change, stop, record the issue under **Decision required**, and ask before editing the plan.

## Current status

- Overall state: ready to begin Phase 0.
- Active phase: Phase 0 — inventory and baseline.
- Active work item: none.
- Architecture implementation changes made: none.
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
| 0 | Ready | Inventory, baseline, unrelated lint stabilization, terminology cleanup |
| 1 | Not started | Clean ISA and VM interfaces |
| 2 | Not started | Server runtime adapters beside the legacy path |
| 3 | Not started | PTX vertical migration |
| 4 | Not started | SPIR-V migration and source-shape convergence |
| 5 | Not started | AMDGPU/HSA migration and source-shape convergence |
| 6 | Not started | Server switch and legacy abstraction removal |
| 7 | Not started | Open program encoding and RPC/native migration |
| 8 | Not started | Final simplification, residue removal, lint cleanup, full verification |

States used here: `Not started`, `Ready`, `In progress`, `Blocked`, `Complete`.

## Phase 0 checklist

- [ ] Re-read repository status and identify overlapping user work.
- [ ] Record JVM and Native compile/test baseline.
- [x] Run the initial full Scalafix baseline.
- [ ] Reduce repeated JVM/Native diagnostics to a unique source inventory.
- [ ] Map each generated failure to its generator.
- [ ] Inventory legacy execution types and every consumer.
- [ ] Inventory deprecated aliases, compatibility helpers, fallback branches, pass-through wrappers, duplicate validation, and other migration residue; assign each item a deletion phase.
- [ ] Inventory RPC codec derivation in domain sources.
- [ ] Remove the prohibited terminology identified in `PLAN.md` from repository-authored sources.
- [ ] Add the repository terminology check with narrow, reviewed exclusions.
- [ ] Fix unrelated lint failures that would obscure the architecture migration.
- [ ] Re-run the full lint baseline and record remaining ISA migration failures.

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

## Decision required

None currently. Before broad Phase 1 edits, validate these planned encodings with a minimal JVM/Native compile slice:

- marker `isa.Program`, with SPIR-V retaining `Module` as its concrete name;
- VM-domain `MemoryRegion` plus a separate optional address-space capability;
- fallible semantic event delivery and passive diagnostic tracing;
- independent optional facets for global binding and global writing;
- typed backend program/VM pairing erased only inside `RuntimeProgram`.

If any of these fail to compile cleanly or produce excessive type complexity, record alternatives here before proceeding.

## Active work item

No active implementation item.

When work starts, replace this section with:

```text
Title:
Phase:
State:
Scope:
Files expected to change:
User changes to preserve:
Acceptance checks:
Notes:
```

Keep only one active work item. Move completed items to the work history.

## Verification ledger

| Date | Scope | Command | Result | Notes |
|---|---|---|---|---|
| 2026-09-20 | Repository Scalafix baseline | `sbt 'scalafixAll --check'` | Failed | Expected baseline; cohesion and forbidden-API failures recorded above |
| 2026-09-20 | Plan Markdown whitespace | `git diff --check --no-index /dev/null PLAN.md` | Passed | Documentation-only verification |

## Work history

No implementation work items completed yet.

## Blockers and risks

- PTX is being independently refactored; stale assumptions are a direct overwrite risk.
- Generated API surface layout needs a deliberate generator-owned design before lint fixes are applied.
- AMDGPU mutable code-object global storage must be isolated per load.
- SPIR-V environment selection must become explicit without changing current API behaviour.
- JVM and Scala Native must both validate any path-dependent or erased backend design before it spreads.

## Next action

Begin Phase 0 with a fresh repository/status inventory, then build a unique lint-failure list and migration-residue inventory that map every generated file to its generator and every temporary or legacy path to its deletion phase.
