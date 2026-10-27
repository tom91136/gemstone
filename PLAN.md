# Machine Semantics Plan

## Goal

Every instruction form has a semantic the explorer can show. The only exceptions are:

- literal no-ops;
- no-ops in emulation, because the effect is not observable (cache hints, `nanosleep`, `yield`), each with its reason;
- forms outside the VM (graphics, fabric, debugger state), each with its reason.

`Unavailable.Mechanics` disappears.

Semantics are correct by construction. The lane algebra already works this way: one definition per instruction, with `Execute` run by the VM and `Render` shown by the explorer. This plan extends the same approach from lane values to machine state: memory, barriers, async completion, cross-lane operations and control flow.

`PLAN_DO_NOT_USE.md` is the previous plan. It built a documentation-only machine layer next to the VM. Do not follow it.

## Status (2026-10-26)

The work is finished. The per-task record, briefs and benchmark data were kept outside the repository during the work and have been removed; this file holds the outcome.

- Phases 0 to 5: done.
- Phase 6: done.
  - T6.1: `CoverageSuite` classifies every target of every ISA in the default test run (about 5 minutes). `policy.tsv` lists only exceptions (NoOp, NotModelled, NotImplemented), each with the model's reason. Mechanics is zero on every target. NotImplemented is zero on PTX and SPIR-V. On SASS, 263 (target, form) pairs remain; on AMDGPU, 1025, mostly gfx1250 and gfx1251, which need hardware evidence.
  - T6.2: measured with JFR on a busy host, comparing allocation and CPU samples rather than wall time. CPU work is flat on every case. Two load-time allocation increases from Phase 8 were removed (SPIR-V operand constraint check, AMDGPU layout conditions and decoder). The remaining ptx-integer-alu allocation (+16%) comes from the pre-plan `Cohort` id field, not from the plan.
  - T6.3: site build, browser smoke test, README module table.
- Phase 7: done, apart from three parked SPIR-V renders.
- Phase 8: done for AMDGPU, SASS, PTX and SPIR-V. PTX register-declaration typing (Tables 26-28 on register operands) is not done. SPIR-V does not check capabilities, non-constant scopes or storage class against pointer type.
- Baumkuchen: the whole-build run has no errors. The 74 remaining warnings are reviewed one by one in `/home/tom/baumkuchen-review/gemstone-review.tsv`: 37 intended, 28 false positives, 6 inherent, 3 true (1 fixed, 2 deferred: the nvidia barrier release and warp arrival scans in `VMInterpreter`). The false positives feed the baumkuchen rule work.
- Left: a final whole-build `baumkuchenAnalyse` once baumkuchen is republished, and the commit.

## Principles

- The VM is the execute instance. VM machine code is rewritten as algebra bodies over the VM's existing helpers. No machine operation keeps a hand-written body beside its algebra body.
- The machine algebra is parametric over the value type `V`: `Long` to execute, `Term` to render. Rendering prints symbolic expressions (`pc + 1`, `PC + 4 + 4 * simm16`), never representative values or unknown targets.
- An instruction requests control; it does not schedule. A branch names the lanes and the target and yields the `Step` the VM already returns. Cohort splitting, reconvergence, barrier release and wave scheduling stay in the VM. Each ISA's model page describes them once.
- Rendering goes through the existing `Term`/`StepRenderer`/`ScalaPrinter` pipeline. Machine semantics render as executable Scala definitions, like lane semantics.
- Three tiers:
  - Lane: the existing `Algebra[V]`.
  - Unit: cross-lane operations within a warp, wave or subgroup, such as shuffle, vote, `ldmatrix`, `wmma`, DPP and SPIR-V group operations.
  - Machine: memory, synchronisation, async and control.
- `Primitive(name, function)` is the escape hatch for large layout functions: TMA box generation, tcgen05 layouts and wmma fragments. Execute and render call the same function. The rendered definition shows its signature and source.
- The sandbox is the real VM running a synthesised program (`SandboxRunner`). There is no second VM.
- No new work on unobserved runs. The staged lane path is unchanged unless a task says otherwise.

## Spike findings

The spike covered NVIDIA `Branch` and `cp.async.bulk`. Files:

- `isa/nvidia/.../MachineOps.scala`
- `MachineSemantics.scala`
- `VMMachineOps.scala`
- `MachineOpsRenderer.scala`
- `MachineOpsSuite.scala`

Changes in existing files were limited to `VMSemantics` and `VMAsync`.

- The VM runs both operations through `MachineOps[V, L, B, R]`. `VMMachineOps` is an `AnyVal` over `VMContext` with result type `Either[Fault, Step]`. The old bodies are gone.
- Tests pass with the new classes overlaid on the classpath: nvidia 141, ptxVm 6547, sassVm 6508, `MachineOpsSuite` 4. `sbt compile`, scalafix and baumkuchen never ran, because the sbt server was stuck.
- Branch cost is within noise. New/base medians: nvidia-control-flow 1.025, ptx-calls 0.998, ptx-integer-alu 0.988, ptx-vector-add 1.018. The ptx-cache result is ambiguous: +7% in one set, -6% in another.
- Bulk copy was 17 to 27% faster in a temporary microbenchmark, consistently in every round. The likely cause is the removed `Either`/closure for-comprehension.
- Method sizes:
  - `VMSemantics.executeControl` grew from 2961 to 3137 bytes. The `Branch` case went from about 40 to 216 bytes, because the target round-trips through `Long` and `Pc(...unsafe...)`.
  - `VMAsync.execute` shrank from 3250 to 2873 bytes.
- Lessons for the foundations:
  - Separate statements from the instruction's `Step` result.
  - Take a static `Pc` literal for jump targets.
  - Thread the lane scope explicitly, because an algebra passed into a lambda does not expand inline.
  - Inline continuations used on two branches need a local `def`.
  - Render into `Term`, not `Vec[String]`.

## Process

- The whole plan lands as uncommitted changes on top of HEAD (`b6c72b4a`). Nobody commits, branches or stashes. The working tree already holds the earlier uncommitted patchset and the spike; Phase 1 removes the parts this plan replaces.
- Per-task diffs come from tree snapshots, so the real index is never touched:
  ```bash
  GIT_INDEX_FILE=.git/plan-index git add -A && GIT_INDEX_FILE=.git/plan-index git write-tree
  ```
  - The coordinator records the tree hash before and after each task in the task record.
  - The task's diff is `git diff <before> <after>`. Revert a task with `git diff <before> <after> | git apply -R`.
- Run one implementor at a time, directly in `/home/tom/gemstone`.
- Build only through the running server with `sbt --client`. Never start a second sbt JVM and never run root `clean`.
- Run heavy steps one at a time. Tell the peer session before a long sbt run.
- Lifecycle of each task:
  1. The coordinator snapshots the tree and writes the brief.
  2. The implementor works to the brief and runs the task's checks.
  3. An Opus 5.5 high reviewer reviews the task diff against the brief, this plan and the checks. It reads the diff and the files the brief names, not the whole tree.
  4. The coordinator accepts the task, or returns it with the review, then snapshots and records it.
- Escalation: if an H task fails review twice, or stops on the five-attempt rule, it moves to S. If an S task fails review twice, the coordinator takes it.
- Agent classes:
  - **H**: Haiku 5.5, max effort. This is the default for every task not marked S.
  - **S**: Sonnet 5.5, high effort. Only new abstractions that later tasks build on: T2.1, T2.3, T4.1, T4.3 and T5.1.
  - **R**: Opus 5.5, high effort. Reviews every task diff and does the design reviews.

The eval in `~/gemstone-eval` supports this split. On a constrained core task with a hidden differential grader, Haiku 5.5 max, DeepSeek v4.1 flash, MiMo v2.6 flash and Beam all passed. Haiku's result needed the least review. It is a single small task, so the plan treats H as an experiment: it starts as wide as possible and narrows only where reviews show it fails.

## Cost control

- Measured on the eval and the spike, at API-equivalent prices: a Haiku task cost $0.25, a Sonnet hot-path task $6.68, and the Opus coordinator about $1.3 per hour of active coordination. The 5-hour window costs roughly $0.65 per 1%.
- The task record has one row per task:
  - task, class, attempts;
  - review verdict;
  - API-equivalent cost of the implementor and of the reviewer, from `~/.claude/agent-cost.py`, with the Haiku tier above 100K prompt tokens applied;
  - tree hashes before and after.
- Adjust after every phase, based on the ledger:
  - If H passes review first time on a kind of task, keep later tasks of that kind in H, and consider moving the next S task down.
  - If H needs escalation twice on a kind of task, move the rest of that kind to S.
  - If reviews keep finding nothing, move reviews of mechanical H tasks to Sonnet 5.5 high, or review two tasks per pass.
- Keep Haiku turns under 100K prompt tokens, where it is 5 times cheaper:
  - briefs are short and name files instead of pasting them;
  - every sbt command pipes through `| tail -40`;
  - a brief splits work rather than growing one session past the tier.
- The coordinator keeps its turns short and never reads full agent transcripts.

## Checks

Every task passes the checks for each module it touches:

```bash
sbt --client '<p>/compile; <p>/test'
sbt --client '<p>/scalafmtCheck; <p>/Test/scalafmtCheck; <p>/scalafix --check; <p>/Test/scalafix --check'
```

Hot-path tasks (marked **perf**) also need:

- **Method sizes:** `MethodSizeSuite` passes, and the task reports the size before and after for every method it changes.
- **Bytecode:** `javap -c -p` of the changed hot methods, before and after. Report any added calls, allocations, boxing or range checks.
- **Benchmarks:** `tools/benchmarks` `BenchmarkRunner run <case> --warmup 30 --samples 40 --no-jfr`.
  - Run base and new interleaved for 5 rounds, alternating the order, pinned with `taskset -c 0-7`.
  - Report min, median, ratio and spread.
  - Differences inside the spread are noise.
  - Do not build or format anything while benchmarks run.
  - The budget is a median ratio of at most 1.01 for unobserved cases, unless the result is inside the spread and reruns agree.

From Phase 2 on, the coverage check (T2.4) must not regress.

Whole-build `baumkuchenAnalyse` is expensive, so it runs only once, at the end of each phase. The coordinator runs it and routes each finding to a fix-up task or to the false-positive list. Per-task checks rely on the baumkuchen compiler-plugin warnings that `compile` already prints.

## Phase 0: stabilise

| Task | Class | Work | Acceptance |
|---|---|---|---|
| T0.1 | coordinator | Start a fresh sbt server, take the baseline snapshot, and create the task record. | `sbt --client projects` answers |
| T0.2 | H | Verify the spike through sbt: compile and scalafix for nvidia, ptxVm and sassVm, then fix lint only. | checks pass; diff limited to lint fixes |
| T0.3 | H | Fix `VMLanes.apply` so it calls `ownedBy` only for an observed launch; unobserved register access takes no extra branch or call. **perf** | checks, perf checks on PTX cases |

## Phase 1: remove the documentation-only machine layer

This work was never committed, so it is removed rather than reverted.

| Task | Class | Work |
|---|---|---|
| T1.1 | H | Delete the documentation-only layer and its tests, after checking with `grep` that no production code outside the layer uses each file. |
| T1.2 | H | Restore `PTXModel`, `SASSModel`, `AMDGPUModel` and `SPIRVModel` to lane-only `semanticsOf`. Keep the `Semantics` enum and `SemanticsPlan`, with `Machine` temporarily unused. |

Candidates for T1.1:

- core `semantics/machine/*` and its tests;
- nvidia `NvidiaDocAlgebra`, `NvidiaMachineAlgebra`, `NvidiaMachineDefinition`, `NvidiaMachineSandbox*`, `NvidiaMachineState` and `NvidiaSandboxAlgebra`;
- amdgpu `AmdGpuMachine*` and `AmdGpuSandbox*`;
- spirv `SPIRVDocState`, `SPIRVMachine`, `SPIRVMachineAlgebra` and `SPIRVMachineState`;
- the hand-written `controlDefinition` and `warpIssuedDefinition` in `SASSModel`.

Keep `SPIRVSandbox`, `SPIRVRegisterValue`, `VMRegisterUse`, `RegisterUse` and `SandboxRunner`.

Acceptance: every module compiles, every test suite passes, and the explorer builds for the JVM and JS. Forms the deleted layer described show their pre-patch result again.

## Phase 2: foundations

| Task | Class | Work | Acceptance |
|---|---|---|---|
| T2.1 | S | Core machine algebra from the spike lessons. Statements are separate from `Step`; `Pc` literals; explicit lane scope; a render instance into `Term`/`StepRenderer`, so a machine definition is a `Described` like a lane one. Write it in core for every ISA to extend. | design review by R before T2.2 |
| T2.2 | H | Port the spike `Branch` and `cp.async.bulk` onto T2.1. Remove the `Pc` round-trip and the spike's `Vec[String]` renderer. **perf** | checks, perf checks; branch case no larger than before the spike |
| T2.3 | S | Unit tier and `Primitive(name, function)`, with one example each, such as `shfl` and one tcgen05 layout. | R design review |
| T2.4 | H | Coverage check: a test that classifies every form of every ISA and target as Lane, Machine, Both, NoOp with reason, or NotModelled with reason. A regex policy file assigns expected classes; an unmatched form fails the test with its name. A count file in the repository ratchets Mechanics down to zero. | test passes at the current baseline and fails when the count rises |
| T2.5 | H | Explorer wiring: `semanticsOf` returns `Machine` and `Both` from rendered definitions; the machine panel renders `Described`; the sandbox stays `SandboxRunner`. | explorer and explorerJS tests pass |
| T2.6 | H | Add a permanent `cp.async.bulk` benchmark case to `tools/benchmarks`. | runs on base and new |

## Phase 3: NVIDIA (PTX and SASS through VMIR)

The template for each family is T2.2. Each task removes the old body, adds a test that asserts the rendered text, and moves the coverage ratchet down.

Template rule: do every read, load and check before the first `write`; pass the predicate as `guard` only when the semantics never calls `write`.

| Task | Class | Family |
|---|---|---|
| T3.1 | H | Control: `Call`, `Return`, `Exit`, `Trap`, `Convergence`, `BranchTable`. **perf** |
| T3.2 | H | Async: bulk store, fill and reduce; shared store and reduce. `.inc`/`.dec` keep the VM's `IncrementWrap`/`DecrementWrap`. |
| T3.3 | H | mbarrier: init, invalidate, load, arrive, transaction, test, pending. |
| T3.4 | H | Barriers that park units: `bar.sync`, `bar.arrive`, `bar.red`, the warp barrier and the cluster barrier. **perf** |
| T3.5 | H | Cluster: map, rank-of, try-cancel. |
| T3.6 | H | Tensor through primitives: TMA, tcgen05 memory and MMA, `wmma.*`, `ldmatrix`, `stmatrix` and `movmatrix`. |
| T3.7 | H | PTX leftovers: `cp.async`, `alloca`, `stacksave`, `stackrestore`, `tensormap.replace`, `tensormap.cp_fenceproxy`. |
| T3.8 | H | SASS leftovers: indirect `BRX`/`BRXU`/`JMX`/`JMXU`; `BMOV`, `B2R` and `RPCMOV` state moves; `PREEXIT`, `LDGDEPBAR` and `ENDCOLLECTIVE` as NoOp with reason. |

Run T3.2 and T3.3 first. They are the check that the H class holds at migration scale.

## Phase 4: SPIR-V

| Task | Class | Family |
|---|---|---|
| T4.1 | S | Design pass over the `Op`/`VMProgram` op table: where the execute instance sits and how labels, `OpPhi` predecessors and call frames are exposed. R reviews it. |
| T4.2 | H | Control: `OpBranch`, `OpBranchConditional`, `OpSwitch`, `OpPhi`, `OpFunctionCall`, `OpReturn*`, `OpKill`, `OpTerminateInvocation`, `OpUnreachable`, `OpAbortKHR`, `OpFunctionPointerCallINTEL`. **perf** |
| T4.3 | S | Lane types for aggregates and pointers, for `OpAccessChain*`, `OpComposite*`, `OpVectorShuffle`, `OpCopyObject` and `OpCopyLogical`. |
| T4.4 | H | Memory and atomics: `OpLoad`, `OpStore`, `OpCopyMemory*`, `OpAtomic*`, and the untyped and vendor pointer operations. |
| T4.5 | H | Collectives and barriers: `OpControlBarrier`, `OpMemoryBarrier`, named barriers including `OpNamedBarrierInitialize`, group and subgroup operations, derivatives in compute. |
| T4.6 | H | Extended instructions: GLSL.std.450 and OpenCL.std families still Mechanics, including `printf`, `vloadn` and `vstoren`. |
| T4.7 | H | Load-time and no-op classification from the grammar class, not name lists, including `OpDecorateId`, `OpDecorateString` and `OpMemberDecorateString`. |
| T4.8 | H | Pipes, device-side enqueue, images and cooperative matrix and vector: semantic, or NotModelled with reason. R reviews each boundary call. |

## Phase 5: AMDGPU

| Task | Class | Family |
|---|---|---|
| T5.1 | S | Design pass over `WaveRun` and its `Step`: EXEC, the branch stack, SCC and VCC. R reviews it. |
| T5.2 | H | Scalar branches and calls: `s_branch`, `s_cbranch_*`, `s_call`, `s_setpc`, `s_swappc`, fork and join. Targets render as `PC + 4 + 4 * simm16`. **perf** |
| T5.3 | H | Barriers and GWS: `s_barrier`, the signal, wait, init, join and leave forms, and `ds_gws_*`. |
| T5.4 | H | Wave state: `s_getreg`, `s_setreg`, mode, clocks, wave id, trap, halt, sleep, end, `s_setkill`. |
| T5.5 | H | `s_subvector_loop_begin`/`_end` and `s_movrel*`. |

## Phase 6: close

| Task | Class | Work |
|---|---|---|
| T6.1 | H | Coverage check at zero Mechanics on every target; the policy holds only NoOp and NotModelled rows with reasons. |
| T6.2 | H | Performance report: every benchmark case, base before Phase 0 against final, with the check protocol. |
| T6.3 | H | Explorer JS build and browser smoke test; README module table. |

## Phase 7: follow-ups after phase 6

| Task | Status | Work |
|---|---|---|
| Opaque renders | done | Expand the tcgen05 element math (immediate and register descriptors), the TMA mapping, validation checks and field decode, the SPIR-V collectives (one body for the resolver and the render), images, fragments, determinant and inverse, reinterpret and shuffle. |
| Parked renders | open | Expand the SPIR-V image level and descriptor logic, the device enqueue and event forms, and the NV tensor layouts and views. |
| SPIR-V forms | done | Implement the last Mechanics forms (Intel block, QCOM composites, conditional copy, prefetch) and the six NotImplemented forms (ARM tensors, NV per-element op and reduce, the latter through a looping call frame). |
| AMDGPU gfx1250 | partial | Implement 272 forms. The rest need hardware evidence. |
| SASS min/max | done | Implement the predicate-output forms from sm_120 hardware data, with 49 golden tests. |
| PTX performance | done | Remove the cost of register-use collection and observer checks from unobserved runs. The PTX cases now run at 0.86 to 1.006 of HEAD. |
| Baumkuchen | done | Fix the phase-end findings, then review every remaining warning (`/home/tom/baumkuchen-review/gemstone-review.tsv`); false positives go to baumkuchen. |
| Benchmarks | done | Add `spirv-cooperative-matrix`, `spirv-image-sampling` and `ptx-bulk-copy`. `journal-inspection` cannot take more than one sample, at HEAD too. On a busy host, compare allocated bytes and CPU samples from the JFR recordings instead of wall time. |

## Phase 8: typed operand domains

Operand fields are typed by the ISA's real domain (register ranges, named values, inline constants, immediates with width, signedness and range, ignored fields) rather than as "kind, N bits".

The rules:
- AMD's XML wins; LLVM and the LLVM MC oracle break ties; an unresolved disagreement fails codegen unless an override row gives a reason.
- Domains are kept per ISA family.
- The migration is one change, with no compatibility layer.
- Rules that exist only in prose become small tables, each row citing its source.

| Task | Status | Work |
|---|---|---|
| Quick fixes | done | Restrict `src_vccz`/`src_execz`/`src_pops_exiting_wave_id` per ISA family. Print ttmp, tba and tma as llvm-mc does. Mark fields an opcode does not read in the explorer. |
| 8.1 | done | Domain IR in `tools/codegen`, the AMDGPU merge with overrides, `domains-<isa>.json`, and `domains-report.md`. |
| 8.2 | done | Generated AMDGPU domain types, with the assembler and disassembler moved onto them. |
| 8.3 | done | The AMDGPU VM decoder and the explorer use the domains, and the explorer shows each field's domain. Out-of-domain codes are rejected. |
| 8.4 SASS | done | Per-form barrier register widths, zero registers, immediate widths and per-slot modifier vocabularies. |
| 8.4 PTX | done | Modifier domains from the ISA document, and operand types and ranges from prose tables. Open: register-declaration typing. |
| 8.4 SPIR-V | done | Constraints on `IdRef` and `LiteralInteger`, from prose tables. Open: capabilities, non-constant scopes, storage class against pointer type. |

## Implementor brief template

Each brief states the files it may change, the family, the template to copy, the checks and the acceptance criteria, then these constraints:

- Change only the listed files. Add no dependencies or build settings, and do not change behaviour outside the family.
- Follow the repo's formatting and lint, refined types and existing idioms. Comments only for a non-obvious why: terse, lowercase, no full stop, never about the task.
- Plain ASCII only. UK English.
- Use `sbt --client` only. Never start another JVM. Never run `clean`.
- Do not commit, stash or change git state. The coordinator snapshots the tree around the task.
- If the same failure persists after five attempts, stop and report it.
- Finish with a report: the changes, the commands run and their results, the perf numbers for **perf** tasks, and open doubts.

## Open decisions

- Parallelism: independent tasks ran in separate copies of the tree, each with its own sbt server, and merged into the main tree as patches against a recorded base. sbt 2's action cache is shared between the trees and can replay stale compile failures; move the recent action-cache entries aside, restart the server, and clean the affected variants.
- `ptx-cache` noise: rerun it on a quiet machine before treating any wall-time result as a regression.
- Deferred: the two true baumkuchen complexity findings in nvidia `VMInterpreter` barrier handling, which need a cohort queue redesign.
