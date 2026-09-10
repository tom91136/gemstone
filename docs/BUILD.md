# Build

sbt is the build entry point. CMake/Ninja and SDK containers are build tools, not
alternative project workflows. Versions are pinned in `build.sbt` and `project/`.

| Command | Purpose |
| --- | --- |
| `sbt compile` | Prepare references, generate missing/changed sources, compile Scala |
| `sbt test` | Incremental MUnit tests |
| `sbt testFull` | Full MUnit suites, including Native rows |
| `sbt nativeIntegration/testFull` | Native facade, authentication and cross-workgroup integration |
| `sbt conformanceIntegration/testFull` | Unified, miniapp and CTS workloads through CTest |
| `sbt integration/testFull` | Both integration projects |
| `sbt serverNative/nativeLink` | Optimized standalone Native endpoint |
| `sbt 'cuda/nativeBuild'` | Explicit native facade build; analogous tasks exist for other providers |
| `sbt 'hsa/ctest'` | Explicit provider CTest run |
| `sbt check` | Format/lint, generated freshness, full unit and integration checks |
| `sbt clean` | Remove repository target outputs and retained integration results |

Use `--server --batch` when commands depend on the current environment. Native facade
builds default to CMake Release; standard `CMAKE_BUILD_TYPE` or explicit
`nativeConfigureArguments` can override it. Tests retain assertions. The Native
endpoint uses the normal Scala Native `nativeLink` task and ReleaseFast configuration;
`nativeConfig` remains available for debugging.

sbt may run independent MUnit projects concurrently. Integration suites serialize their
top-level orchestration, while CTest schedules the individual cases. Scala Native links
remain limited to one at a time because each link can consume several gigabytes.

Native builds are explicit integration prerequisites, not ordinary Scala compile or
server-unit-test dependencies. The native CUDA cross-workgroup client remains covered
by `nativeIntegration`. Generated runtime properties are recreated if removed.

## Generation

The dependency order is `core → apiModel → codegen → api → rpcCodegen → server`.
ISA generators also consume codegen. JVM generators serve both JVM and Native rows.

Checked-in generated sources belong to `src/generated`; CUDA C glue and manifests
belong to `runtime/cuda/generated`. Change generators, not generated files.

```console
sbt --server --batch rpcCodegen/checkAPISurfaces rpcCodegen/checkGeneratedAPISources
```

Generator caches track declared source/reference contents, tools, arguments and outputs.
See [pinned references](../codegen/ISA_REFERENCES.md). Tool overrides use the build's
existing generator settings; cache tests cover mutation and deleted-output recovery.

## Resources and outputs

`CMAKE_BUILD_PARALLEL_LEVEL` controls compiler workers within one serialized fixture build.
`GEMSTONE_EXECUTION_PARALLELISM` controls the shared execution pool and native CTest jobs.
Integration can place
build containers and processes under `GEMSTONE_BUILD_SLICE` for one systemd budget.

Build outputs, fixtures and reports live below `target/`. External caches and caller-specified
output directories remain caller-owned. For source-only copying, exclude target directories
and local IDE data. Preserve needed reports before cleaning.

## CI

The GitHub workflow uses the same documented sbt unit, native and host-smoke entry points.
It uses the generic no-GPU profile. Full SDK/physical-oracle qualification remains an explicit
integration run; the smoke workflow does not certify every backend or hosted-runner performance.
