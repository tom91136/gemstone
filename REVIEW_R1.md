# Human review R1

Review actions are recorded here for deferred implementation. No implementation changes are made during the review.

## Implementation order

Apply the actions as seven review batches, keeping the build green after each batch:

1. Flatten `quality` and normalize the module tree (actions 9 and 10).
2. Establish ordinary shared integration support and explicit test tiers (action 6).
3. Replace conformance Python, centralize fixtures/containers and consolidate dependency/cache identity together
   (actions 3, 4 and 5).
4. Isolate CUDA RPC and fold the ACO bridge into codegen (actions 1 and 2).
5. Replace incidental regex parsing after the new ownership boundaries are stable (action 7).
6. Add the cross-platform benchmark subsystem last, reusing the final module and runner APIs (action 8).
7. Delete and consolidate documentation only after the implemented structure and command surface are final (action 11).

Do not combine all actions into one unreviewable change. Each batch must update paths/documentation and pass the test
tier it changes before the next batch begins.

## 1. Isolate the CUDA call-level RPC model

**Agreed, with scope narrowed.** CUDA legitimately has a distinct call-level RPC path: the other native facades lower
work to the shared execution protocol, while selected CUDA Driver/Runtime calls require CUDA-specific marshalling,
context and callback behavior. That architectural difference should remain.

The current ownership and naming are misleading, however. `apiModel` exposes a nominally generic
`net.kurobako.gemstone.rpc` schema whose wire types, values, responses and derivation constraints are concretely CUDA
specific (`CUDAStatus`, `CUDAError`, CUDA handles and callbacks, CUDA symbol prefixes, and a mandatory CUDA-status
result). This makes the common API-model layer appear extensible while coupling it to the one exceptional façade.

Deferred action:

- Add a `cudaRpc` project at `api/cuda-rpc`, after `api` and before CUDA RPC codegen/server, using the
  `net.kurobako.gemstone.cuda.rpc` package. Move CUDA-specific schema values, transport/dispatcher contracts and
  derivation into that stage. Rename `rpcCodegen` to `cudaRpcCodegen` and place it at `codegen/cuda-rpc`. Leave only
  genuinely transport-neutral schema primitives in a shared module.
- Keep genuinely shared ABI/schema concepts vendor-neutral; do not generalize CUDA-specific variants merely by leaving
  them under generic names.
- Preserve the separate generic execution protocol used by HIP, HSA, OpenCL and Level Zero; do not introduce per-API
  RPC symmetry for its own sake.
- While moving the model, audit schema/value variants that are generated into the codec but absent from the derived
  CUDA method schema, and remove or justify them.

Acceptance: the project/module and package boundaries make it obvious that CUDA call RPC is an intentional adapter,
not the common transport abstraction for all generated API surfaces, with generated-source and protocol-fingerprint
checks still passing.

## 2. Fold the ACO Python exporter into codegen

**Agreed by review direction.** The standalone `codegen/amdgpu/export_aco.py` is only a small bridge that loads Mesa's
pinned executable `aco_opcodes.py` model and emits the interchange rows consumed by `GenerateAMDGPUISA`. Keep Python
as the interpreter for Mesa's source model, but make the bridge an implementation detail of the AMDGPU generator
rather than a separate repository-level generator script.

Deferred action:

- Put the bridge under `GenerateAMDGPUISA`/the codegen module and remove the separate exporter-path argument and
  `build.sbt` input plumbing.
- Keep `build.sbt` responsible only for resolving the pinned references, Python executable, outputs and generator task;
  do not embed multiline Python directly in the sbt definition.
- Store the bridge as a codegen resource owned and launched by `GenerateAMDGPUISA`, and declare that resource as an sbt
  generator input so ordinary task invalidation tracks it.
- Preserve `python -B`, deterministic instruction ordering, the deliberately narrow interchange format, and clear
  failure propagation from Python to sbt.

Acceptance: AMDGPU generation has no standalone `export_aco.py` or exporter-path CLI argument, changing the folded
bridge invalidates generation, and fresh plus cached generation reproduce the checked-in outputs.

## 3. Eliminate project-owned standalone Python from conformance

**Agreed by review direction.** None of the Python files checked into `conformance/` is vendored: all sixteen are new,
project-owned harness or test code. The pinned Mesa `aco_opcodes.py` executed by codegen is upstream material prepared
outside the repository and is the only relevant external Python source; action 2 retains its Python execution while
folding Gemstone's adapter into codegen.

Deferred file disposition:

| Current files | Owner after cleanup |
| --- | --- |
| `tools/case-run.py`, `tools/runtime_provenance.py` | A Scala conformance CLI invoked by generated CTest cases; keep process supervision, validation, callback counters, timeout/signal behavior and Linux loader evidence intact. |
| `tools/audit-run.py`, `tools/report-run.py` | Scala conformance post-run components invoked by `CTestConformanceSuite`; the scheduler/isolation audit becomes an authoritative qualification step and report generation becomes an automatic artifact. |
| `tools/fixture-run.py` | Scala/sbt conformance fixture support, including cross-process locking, process-group termination, measurement recording and atomic/recoverable artifact publication. |
| `cts/opencl/prepare.py` | Host-side acquisition/hash verification in Scala fixture preparation; container-local archive extraction becomes a fail-closed heredoc in the OpenCL Containerfile. |
| `cts/opencl/inventory.py`, `cts/sycl/inventory.py` | Python heredocs in their respective Containerfiles, because they interrogate freshly built upstream executables and emit container artifacts. |
| `miniapps/tools/verify-openmp-target.py` | A Python heredoc in the miniapp Containerfile, preserving compile-definition, target-architecture, offload-section and launch-symbol checks. |
| `unified/tools/embed-binary.py` | A CMake script/custom command owned by the unified fixture build. |
| All six `tools/test-*.py` files | MUnit/Scala contract tests for the replacement components or their container/CMake invocation boundaries. |

Do not put multiline harness logic in `build.sbt`; sbt should declare and connect the Scala tasks. Do not retain
standalone project Python merely because Python remains installed in SDK containers or is required to execute pinned
upstream Mesa data.

Acceptance: the repository contains no Gemstone-owned standalone `.py` files, prepared upstream Python remains
explicitly pinned, CTest/container/CMake boundaries retain their current fail-closed behavior, report and audit checks
run in the normal qualification lifecycle, and equivalent regression coverage is in the ordinary test gate.

## 4. Centralize container fixtures and qualify one prepared toolchain image

**Agreed, with an explicit qualification gate.** Container use is confined to conformance fixture construction, but
its lifecycle is divided among unified, miniapp and two CTS pipelines. The currently pinned CUDA, ROCm and oneAPI
images are all Linux/amd64 Ubuntu 24.04 images, so their toolchains can in principle coexist in one prepared builder.
Their current compressed layers total roughly 14.6 GB before Gemstone's common additions, however, and each vendor
image exports global compiler/library paths that must not leak into another backend's build.

Deferred action:

- Add one dedicated container/fixture subproject owning image pins, engine invocation, source acquisition, cache keys,
  locking, measurements, staging/publication, manifests and cleanup for unified, miniapp and CTS artifacts.
- Build and publish one immutable, digest-pinned Gemstone toolchain image containing the host, CUDA, ROCm/HIP/HSA,
  oneAPI/SYCL, OpenCL and SPIR-V build prerequisites.
- Give each backend a sanitized activation profile with explicit compiler, include, library and CMake search paths;
  never rely on the combined image's inherited `PATH`, `LD_LIBRARY_PATH`, `CPATH`, `CMAKE_PREFIX_PATH` or vendor
  entrypoint.
- Keep workload sources, pinned CTS checkouts and generated artifacts outside the image in the declarative fixture cache
  defined by action 5. The image is a toolchain input, not a cache of repository state.
- Keep OpenACC as an explicit external image unless a pinned NVIDIA HPC SDK installation can be incorporated with
  acceptable distribution/licensing terms and the same isolation guarantees.
- Retain workload-specific build recipes rather than creating one conditional mega-script; the central subproject and
  image unify infrastructure and tools, while unified, miniapp, OpenCL CTS and SYCL CTS retain typed recipe identities.
- Measure pull/storage size, cold and warm build time, rebuild invalidation and every backend's generated artifact
  provenance before retiring the current image-family path.

Acceptance: one pinned prepared image builds the full non-OpenACC fixture matrix reproducibly, backend builds cannot
discover another vendor's tools or libraries accidentally, changing one recipe invalidates only its artifacts, and
the previous per-vendor builders are removed only after artifact and conformance equivalence passes.

## 5. Consolidate build identities and remove decorative hashing

**Agreed on fragmentation and excess; disagreed on trusting a legitimate URL as content identity.** HTTPS authenticates
the connection to a server, not that a release URL, Git tag, documentation page or container tag will always return the
same bytes. The thirteen archives/pages/packages used by codegen, the OpenCL CTS archives and container bases therefore
still need an immutable revision or expected digest. This is also the model used by CMake's declarative dependency
facilities: exact Git revisions are preferred and `URL_HASH` is strongly recommended for URL downloads.

The repository currently gives unrelated mechanisms similar names and implements build identity in several places:
`ReferenceArtifacts` owns downloading, SHA verification, selective safe extraction and a prepared-tree fingerprint;
`Generator` hashes classpath files, executable version output and selected environment despite its tasks being wrapped
in `Def.uncached`; unified, miniapp and CTS catalogs each construct a different truncated SHA cache key; container image
IDs are separately captured; OpenCL inventory hashes recipes and patches again; generated CUDA files repeat a hash of
the official-header manifest; and HIP runtime provenance hashes loaded libraries without comparing those hashes to an
expected value. This makes the build hard to reason about without adding equivalent assurance.

Deferred action:

- Give every external input one declarative owner in the nearest build system: sbt/Coursier for JVM dependencies, a
  codegen reference catalog for non-library specifications/headers, CMake `FetchContent`/`ExternalProject` for native
  sources, and the fixture project for source revisions and digest-qualified toolchain images. Do not add a universal
  meta-catalog that merely mirrors those declarations, and remove the field-per-pin parameter list in `build.sbt`.
- Apply one integrity rule at those owners: URL/archive inputs require an expected digest, VCS inputs require an exact
  commit rather than a movable branch or tag, and container inputs require an image digest. Do not add a second hash
  when the owning facility already verifies that identity.
- Define each product version once and feed it to every consumer. In particular, reconcile the codegen and native
  OpenCL-header versions and add the missing `URL_HASH` to the native OpenCL `FetchContent` declaration. Keep selective
  safe extraction only in the codegen reference preparer, where CMake would invert ownership.
- Make the central fixture owner from action 4 calculate artifact identity once from declared source revisions,
  container identity, recipe inputs and relevant build options. Remove the three bespoke unified/miniapp/CTS key
  implementations and do not separately hash the same recipe and patch files into manifests.
- Replace the custom generator invalidation wrapper with ordinary sbt tracked inputs/outputs and task caching. Declare
  exact tool paths and relevant environment values as task inputs; remove hashing of arbitrary `--version` stdout and
  the hand-maintained environment list.
- Emit one generated-artifact provenance manifest containing human-readable dependency versions/revisions and the
  canonical input identity. Do not repeat the official-header-manifest digest across generated files, and remove the
  current HIP loaded-library SHA fields because nothing validates them; retain path and dynamic binding evidence,
  which is what the qualification actually checks. Add a digest comparison later only with a declared expected value.
- Use precise names for distinct concepts: dependency integrity, cache key, generated-source provenance, protocol
  fingerprint, journal checksum and execution evidence. Do not label ordinary emulator-use counters as cryptographic
  attestation.

Explicitly retain the CUDA RPC schema fingerprint and deterministic message IDs (wire compatibility), RPC journal
blob/archive digests (deduplication and corruption detection), Level Zero mirror-page hashes (host/device coherence),
AMDGPU code-object cache hashing with byte equality (performance), and emulator request evidence (test validity). These
are runtime semantics with actual consumers, not duplicated dependency verification. The portable journal SHA-256
implementation remains while the server targets both JVM and Scala Native; document that portability requirement and
keep its standard-vector tests authoritative.

Acceptance: every remaining digest has one documented consumer and failure mode; fetched mutable bytes are verified;
one declaration owns each external input and fixture identity; changing a declared input invalidates exactly the
affected outputs; and manifests contain useful identities without recomputing or repeating unvalidated hashes.

## 6. Reorganize tests around ownership and execution tier

**Agreed.** The ordinary module tests are mostly correctly co-located, including shared JVM/Native ISA tests and their
generated reference suites. The cross-project harness and integration boundaries are not: the `conformance` project
has no main sources, yet its reusable process capture, profiles, CTest matrix and three fixture catalogs all live in
`src/test`; integration projects consume them through `conformance % "test->test"`. Cross-language tests then bypass
those facilities: `server`'s JVM tests compile native C++ directly, and the native CUDA integration suites independently
implement runtime discovery, token creation, server lifetime, process timeout and output handling.

There are also duplicate authorities. Native CTest is exposed both as `NativeBuild.ctest` and through
`NativeRuntimeSuite`; `APIConformanceSuite` expands the same complete official-header comparison already performed by
the `checkAPISurfaces` build gate; and shared `native/execution` sources and `rpc_fixture.hpp` are built or included by
several runtime projects without owning a CMake project of their own. Six benchmark entry points in five files live
under `src/test-jvm` even though they are not tests. Finally, the 2,558-line SPIR-V `ExecutionSuite` and 2,455-line
AMDGPU `CodeObjectSuite` combine many independent behavioral areas and hide otherwise reusable fixtures.

Deferred action:

- Establish explicit tiers with one entry point each: fast module unit/contract tests, native component CTest,
  cross-language integration, external conformance, and opt-in benchmarks. Make the root `check` composition state
  these tiers directly instead of relying on overlapping `testFull` traversal and wrapper side effects.
- Add a non-published `integration/support` main-code project for repository paths, temporary directories, generated
  runtime locations, authenticated server lifecycle, subprocess environment, timeout/termination, captured diagnostics
  and measurement. Move `ProcessCapture` and the generic parts of `ConformanceSupport` there.
- Turn `conformance` into normal non-published main code. Move `TestProfile`, `CTestMatrix`, `UnifiedInventory`,
  `MiniappCatalog`, `CTSCatalog` and conformance-specific support out of `src/test`; remove all `"test->test"` project
  dependencies. Both integration leaf projects consume `integration/support` normally, and only the conformance leaf
  also consumes `conformance`.
- Migrate the native CUDA suites and every JVM/native process test to the shared integration fixture rather than using
  raw `ProcessBuilder`.
- Give `native/execution` its own CMake target and test ownership. Build its smoke/session-loss/bounds/identity clients
  once through CMake, export the common RPC fixture as test support, and consume targets from CUDA, HIP/HSA, OpenCL and
  Level Zero instead of compiling the same source directly or assigning its tests to HSA.
- Keep native runtime ABI/component tests beside their runtime, but normalize `test` versus `tests` layout and common
  CMake test helpers. Bind each CMake-backed runtime project's conventional sbt `test` task to its own CTest component
  suite. Make the measured native integration coordinator the authoritative full native runner; its conventional
  project-level `test` task composes the runtime leaves through the same helper. A lower-level `ctest` task may remain
  as an implementation detail or diagnostic entry point, but documentation and CI must not require it.
- Keep official API validation as one build gate. Replace the duplicate manifest-wide MUnit expansion with focused
  unit tests of validation rules and failure diagnostics. Preserve generated-source freshness as a separate build
  check.
- Remove the six `*Benchmark` entry points from test sources as part of action 8; that action is their sole destination
  and runner design. They must not remain duplicated in their current modules or run in normal verification.
- Split oversized ISA suites by responsibility while retaining them in their owning modules—for example SPIR-V
  control flow, memory, atomics, conversions and extended instructions, and AMDGPU ELF parsing, metadata, relocations,
  kernel extraction and cache behavior. Extract only genuinely repeated test fixtures, not domain assertions, into
  module-local test support.
- Add the custom-rule testkit coverage specified by action 9; `quality` is its sole owner.

Acceptance: a test's directory identifies both the component it verifies and its execution tier; shared harness code
is consumed through ordinary main/testkit dependencies; no Scala test invokes an ad hoc native compilation; native and
API validation each have one authoritative full-matrix execution path; benchmarks are opt-in; and each tier can be run
or diagnosed independently from sbt/CI.

## 7. Replace incidental regex parsing with structured extractors

**Agreed, with regex retained only where it expresses an actual pattern language more clearly.** Regex use is heavily
concentrated in codegen and conformance, but important production cases also parse fixed internal formats repeatedly.
`AMDGPUKernelVariants` regexes mangled architecture markers and numeric families; `TestProfile` regexes identifiers,
assignments and oracle specifications; PTX lowering reparses `qualifiedOpcode` with regex, prefix and substring tests
despite already owning an instruction ADT and modifiers; and the CUDA PTX backend uses one large regex to recover global
declarations independently of the PTX parser. Fixed revision, digest, target, shared-object and CTest quoting shapes add
more isolated regex validation across the harness.

Deferred action:

- Prefer existing structured values first. Extend PTX parsing/lowering data so address space, scalar type, width,
  operation and modifiers are matched from the instruction model, eliminating regex and `contains(".shared.")`-style
  reparsing of `qualifiedOpcode`.
- Parse CUDA module global/constant declarations and initializers through the PTX parser/AST. Remove the independent
  multiline `GlobalDeclaration` regex from server execution rather than translating it into an equally opaque custom
  extractor.
- At fixed textual boundaries, use Scala 3 interpolation patterns for the outer shape and compose typed extractors for
  captured components—for example mangled `target_archE${...}E` names, environment `${key}=${value}` assignments,
  `${provider}@${target}!${resource}` oracle specifications and segmented opcodes. Each component extractor must return
  a validated domain value, not an unvalidated `String` followed by another regex guard.
- Centralize reusable lexical extractors or predicates by domain: C identifiers, hexadecimal revisions/digests, GFX
  targets, integer/scalar widths, safe profile/resource names and shared-library suffixes. Reuse the existing
  character-based `Identifiers` approach instead of introducing a global miscellaneous-regex utility.
- Replace regex-backed delimiter splitting and whitespace cleanup with character/string operations where the grammar
  is only a fixed delimiter set. Replace shell filename/number checks with `case`/glob or arithmetic validation when
  that is clearer, and make CTest serialization expose a matching reader rather than regexing its emitted syntax in
  tests.
- Audit code generators individually. Keep named, tested regexes for genuine upstream textual conventions such as
  LLVM `RUN`/FileCheck lines, GoogleTest macro discovery and documentation prose when they are clearer than a parser;
  migrate nested or stateful PTX pseudo-grammar and Mesa C++ scraping to parser combinators or upstream structured
  data instead of accumulating larger regexes.
- Retain explicitly user-supplied regex filtering in conformance and the unified runner: regex is the advertised query
  language there. Retain idiomatic, simple CMake `MATCHES` checks where CMake has no clearer structural facility.
- Add table-driven positive, negative and near-miss tests for every new extractor/parser, including full-consumption,
  empty-segment, Unicode/ASCII, case-normalization and overflow behavior. Do not silently broaden formats while
  replacing regexes.

Do not impose a blanket Scalafix prohibition on regex. The review criterion is that regex must represent a genuinely
variable textual language and be locally named and tested; fixed product formats should be decoded into typed values,
and already parsed domain models must never be converted back to strings for classification.

Acceptance: production control flow no longer classifies internal identifiers or opcodes with regex; PTX declarations
have one parser; fixed external shapes use composable, full-match extractors; remaining regex sites document the input
language they implement; and conformance's deliberate user-regex filtering retains its current behavior.

## 8. Add one cross-platform benchmark subsystem

**Agreed.** The current six entry points—PTX execution, generic memory boundary, AMDGPU execution, AMDGPU ALU,
AMDGPU vector memory and journal inspection—each implement their own argument parsing, warm-up, timing, allocation
measurement, correctness check and textual output. They are JVM-only solely because of their location and use of JVM
management/JFR APIs, even though most measured kernels exercise shared code that also builds for Scala Native.

MUnit is suitable for correctness tests of benchmark cases and runner behavior, but it is not a benchmarking harness:
it does not provide measurement forks, warm-up policy, statistical sampling, result formats or protection against JVM
microbenchmark pitfalls. `sbt-jmh` remains maintained, its current plugin supports sbt 2, and it is appropriate for
high-quality JVM microbenchmarks; JMH cannot be the canonical harness because it does not execute Scala Native.

Deferred action:

- Add a non-published `benchmarks` project matrix with JVM and Scala Native variants. Depend on the benchmarkable
  library projects (`core`, the ISA projects and `server` where required), not indiscriminately on codegen, quality,
  conformance, integration aggregators or native façade builds.
- Define a small shared benchmark case model: stable case name, typed parameters, setup, warm-up operation, measured
  operation count, teardown and mandatory correctness validation. Move all six existing cases into this model
  without importing fixtures from another project's `Test` configuration.
- Provide one CLI/runner that lists and filters cases, validates parameters, runs configurable warm-up and measured
  samples, and emits both concise console output and versioned JSON/JSONL. Record platform, Scala/runtime mode, build
  configuration, parameters, operation count, elapsed time and optional platform metrics; do not pretend JVM and
  Native allocation counters are equivalent when a platform cannot measure the same quantity.
- Run each serious sample in a fresh process through sbt launcher tasks so JVM forks and Native invocations have the
  same isolation model. Keep a short in-process developer mode for profiling, and expose explicit JVM and Native tasks
  plus an opt-in comparison task; benchmarks remain outside `check` and ordinary `test`.
- Put JFR, `ThreadMXBean` allocation data and any async-profiler integration behind a JVM runner adapter. Do not add
  `sbt-jmh` in R1: the shared runner is the single catalog and canonical measurement path. Reconsider a thin JMH adapter
  later only if a concrete JVM microbenchmark cannot be measured reliably by the shared process runner.
- Use MUnit in the benchmark project's test configuration only to verify case discovery, parameter validation, result
  encoding, deterministic correctness checks and a tiny one-sample smoke run on both supported platforms. Never make
  a noisy timing threshold an ordinary unit-test assertion.
- Allow regression gating only in a separate, explicitly configured performance job with controlled hardware,
  toolchain/runtime metadata, repetitions and a stored baseline. The default benchmark command reports observations
  and returns failure only for invalid setup or incorrect results.

Acceptance: all benchmark cases are discoverable from one catalog and runnable on every platform they support; PTX,
AMDGPU and shared-memory cases run through the same API on JVM and Scala Native; JVM-only profilers are optional
adapters; results are machine-readable and comparable without claiming false metric equivalence; and no benchmark
entry point remains under a module's test sources.

## 9. Flatten the quality module and root its configurations

**Agreed.** A compiled custom-rule project remains appropriate, but the current filesystem boundary is awkward:
`quality/rules` is the actual sbt module while its parent `quality` directory contains only the test and codegen
Scalafix configuration variants. A reader reasonably expects `quality` itself to be the module.

Deferred action:

- Move `quality/rules/src` to `quality/src` and define the sbt project at `file("quality")`, named `quality` in the
  build with artifact name `gemstone-quality`.
- Rename the build reference from `qualityRules` to `quality` and update every `% ScalafixConfig` dependency. Continue
  disabling the Scalafix plugin on this project itself so the rule provider cannot depend on its own compiled rules.
- Keep the rule implementations and `META-INF/services/scalafix.v1.Rule` registration together under the flattened
  module, and add the Scalafix testkit/input-output coverage required by action 6 there.
- Move the two variant configurations to repository-root dotfiles, alongside `.scalafix.conf` and `.scalafmt.conf`,
  named `.scalafix-test.conf` and `.scalafix-codegen.conf`. Update scoped `scalafixConfig`
  settings to resolve them from the root project directory.
- Do not merge the three configurations merely to reduce file count: production, test and generated/codegen sources
  intentionally enforce different rule sets. Preserve those policies while making their location and names obvious.

Acceptance: `quality/` is one self-contained, non-published custom-rule module; no `quality/rules` nesting remains;
all formatting/lint configuration entry points are visible at repository root; every consumer resolves the intended
configuration; and the full Scalafix check loads and tests the local rules without a dependency cycle.

## 10. Normalize module and integration topology

**Agreed.** The physical tree currently assigns several meanings to parent directories. `integration` is an sbt
aggregator containing `integration/native`, but its other child project is physically `conformance/integration`;
`conformance` simultaneously denotes fixture assets, test-only reusable Scala infrastructure and an integration area.
The `isa`, `runtime` and `integration` parent directories are also sbt projects despite containing no sources. They
inherit normal Scala/test/quality settings, and the empty `isa` and `runtime` aggregators are not explicitly prevented
from publishing empty artifacts. The CMake-backed runtime wrapper projects likewise receive Scala and MUnit settings
although they contain no Scala sources.

Additional ownership inconsistencies are smaller but concrete: `ABISuite` tests types implemented by `apiModel` but
lives under the downstream `api` project; native runtimes mix `src/main/c`, `src`, `include`, `test` and `tests`
conventions; and the shared `native/execution` component has source and tests but no module-level build definition.

Deferred action:

- Move `conformance/integration` to `integration/conformance`. Keep `integration/native` and
  `integration/conformance` as separate projects because they have materially different cost and prerequisites, but
  name their sbt identifiers `integrationNative` and `integrationConformance` and aggregate
  both from `integration`.
- Make `conformance/` exclusively own the non-published conformance implementation and assets described in actions 3,
  4 and 6: coordinator/testkit code, profiles, suite catalogs, workload recipes and fixture construction. Integration
  suites may invoke this implementation but must not own or duplicate it.
- Place the dedicated container/fixture project from action 4 at `conformance/fixtures`, with recipes at
  `conformance/fixtures/recipes/{unified,miniapp,opencl-cts,sycl-cts}` rather than mixing top-level `unified`,
  `miniapps`, `cts` and `containers` ownership conventions.
- Keep `isa`, `runtime` and `integration` as pure, non-published aggregate projects with no inherited MUnit dependency,
  Scalafix rule dependency or source compilation; they exist only for discoverable aggregate commands.
- Treat each CMake-backed runtime sbt project as a non-published build bridge with only native-build settings. Do not
  apply the full Scala `commonSettings` bundle merely to obtain organization/name values.
- Move `ABISuite` to `api/model`; leave `SurfaceDefaultsSuite` under `api`. In general, relocate tests to the leaf module
  that owns the implementation unless the test deliberately crosses module/runtime boundaries, in which case it
  belongs under `integration`.
- Standardize native leaf layout on `src/`, public `include/`, `test/`, optional `abi/` and generated output where
  required. Fold CUDA's `src/main/c` exception and the HIP/HSA plural `tests/` directories into that convention, and
  give `native/execution` its own CMake ownership as required by action 6.
- Retain `api/model` as the nested foundational API module and use the exact CUDA-specific staged paths from action 1.
  These are dependency stages with distinct ownership, not organizational directories masquerading as modules.
- Keep the checked-in `src/generated` convention: it is deliberate, documented and centrally wired. Do not mix this
  structural cleanup with a generated-source policy change.

Acceptance: every physical child project sits beneath the subsystem or execution tier that aggregates it; aggregator
projects cannot compile or publish artifacts; native build bridges do not masquerade as Scala libraries; tests reside
with their implementation or in the explicit integration tier; and one documented directory convention covers every
native runtime leaf.

## 11. Delete and consolidate documentation after restructuring

**Agreed.** Excluding this review ledger, the repository currently has seventeen Markdown files and roughly 1,233
lines. Build commands, runtime boundaries, conformance behavior, generator inputs, implementation plans and prior-review
status are spread across the root, `docs/`, `codegen/`, `conformance/`, ISA leaves and `server/`. Several documents
describe transitional plans or paths and command names that actions 1–10 will replace. Updating each file in place
would preserve the fragmentation and create multiple authorities for the same behavior.

Deferred action, performed only after actions 1–10:

- Rewrite the top-level `README.md` as the sole required entry point. It owns the project purpose and scope, supported
  platforms/backends, concise architecture map, prerequisites, conventional build/test commands and links to external
  upstream specifications. It must describe the implemented repository, not retain a history of how it arrived there.
- Delete planning, handoff and completed-review documents after transferring any still-valid requirement into code,
  tests or the top-level README. In particular, `GEMSTONE_PLAN.md` and `LUNA_REVIEW_1.md` must not remain as parallel
  statements of current architecture. Remove `REVIEW_R1.md` once its actions are implemented and verified.
- Delete `docs/` and leaf README files whose content is duplicated, historical, discoverable from conventional project
  structure or better expressed by task help and tests. Do not preserve a document merely because another document
  links to it; repair links as part of the same cleanup.
- Permit one README in a subproject only when that subproject has an independently operated workflow or a non-obvious
  external contract that cannot be stated concisely in the root README or exposed through conventional task help.
  Each retained subproject README must have one named audience and no duplicated build instructions, version catalog,
  roadmap, review history or repository-wide architecture narrative.
- Keep dependency/reference revisions in their declarative build owners from action 5 rather than in prose tables.
  Generated command help and machine-readable manifests remain implementation outputs, not additional narrative
  documentation authorities.
- Make conventional sbt commands the public build interface. From the repository root, `sbt compile` must prepare the
  declared inputs and compile every normal Scala/JVM/Scala Native module and CMake-backed native bridge, and `sbt test`
  must run the ordinary unit and component-test tier without project selection or a custom alias. Within every
  test-bearing leaf or integration project, `sbt projectId/test` must run that project's applicable tests; expensive
  external conformance and benchmarks may remain explicitly opt-in but must use conventional `test` or `run` semantics
  inside their owning projects.
- Retain `sbt check` as the explicit full repository qualification composition from action 6, including formatting,
  lint, generated-source freshness and the integration tiers available in its declared environment. Custom operational
  tasks may fetch references, regenerate checked-in sources or run benchmarks, but neither compilation nor ordinary
  testing may require users to know `testFull`, internal generator task names or undocumented command sequences.

Acceptance: a fresh checkout can be compiled with `sbt compile` and ordinarily tested with `sbt test`; each subproject
obeys conventional `compile`, `test` and `run` meanings where applicable; `sbt check` is the one documented full
qualification command; no documentation refers to removed paths, projects, tasks or planned-but-unimplemented behavior;
the root README is sufficient for a new contributor; and every surviving subproject README passes the necessity rule
above. If conventional `compile` and `test` commands cannot successfully exercise the reorganized project, the
reorganization has failed regardless of whether a custom alias can make CI pass.
