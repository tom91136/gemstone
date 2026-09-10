# Mini-app conformance

This lane brings the four Polyregion mini-app workloads into Gemstone's
compile-once, run-twice conformance model:

| application | pinned upstream revision | vendor ports exercised |
| --- | --- | --- |
| BabelStream | `UoB-HPC/BabelStream@17ab377b0e919e14fd3df2b67268761fdac8abb3` | CUDA, HIP, OpenCL, SYCL, OpenMP target, Kokkos, OpenACC, StdPar |
| miniBUDE | `UoB-HPC/miniBUDE@570f66c1fd0c29b7f3ab6fa96fba87f4561aa44c` | CUDA, HIP, OpenCL, SYCL, OpenMP target, Kokkos, OpenACC, StdPar |
| CloverLeaf | `UoB-HPC/CloverLeaf@3e10ff9268d3704a744e5a2a4ea3c62831078082` | CUDA, HIP, SYCL accessor/USM, OpenMP target, Kokkos, StdPar |
| TeaLeaf | `UoB-HPC/TeaLeaf@e70261c0be40537da75b258108ed2898f84f3c58` | CUDA, HIP, SYCL accessor/USM, OpenMP target, Kokkos, StdPar |

CloverLeaf also has an optional OpenACC port. OpenACC models require an explicit NVHPC image; Kokkos requires a target supported by its pinned architecture map.

The upstream trees are fetched into `target/conformance/miniapps`; they are
not copied into this repository and are never edited. Each selected vendor
port is configured and compiled once inside the matching pinned vendor
container. The installed artifact contains the executable, its non-driver
user-space dependency closure, and the small runtime deck/data inputs. Its
RPATH is relative to the artifact, and the build/source/container paths are
checked out of the installed ELF metadata. Standard OpenCL and Level-Zero
loaders, vendor drivers, and host C++ ABI libraries remain host-provided so
they can match the installed device driver. The exact installed executable is
then run unchanged against the vendor runtime and against the corresponding
authenticated Gemstone provider.

The CUDA BabelStream builder stages a private copy only to remove its
reporting-only NVML memory-clock query. NVML is not available through a
Gemstone driver provider; the CUDA kernels and numerical validation are left
unchanged, and the pinned checkout remains read-only.

The HIP CloverLeaf builder likewise stages a private compatibility copy for
ROCm 7, replacing its reporting-only use of the removed `gcnArch` property
with `gcnArchName`; the pinned checkout is still read-only.

TeaLeaf's HIP StdPar build stages a compiler workaround for the row-norm
transform: its lambda is marked `noinline` so rocPRIM does not duplicate the
inner row loop throughout the reduction kernels. On the pinned ROCm compiler,
that expansion caused pathological `StructurizeCFG` compilation time. The
arithmetic and reduction remain unchanged, and normal `-O2` optimization is
retained. The pinned source checkout is not modified.

Each artifact retains Ninja command timings and, where available, compile
commands under `share/miniapps/build/<app>/<model>/`.

CloverLeaf and TeaLeaf require double precision. Oracle selection is declared by the host profile; the harness does
not silently substitute a CPU device for a configured GPU oracle. Unmatched hosts skip GPU oracle variants.

Run this lane through the integration project, which prepares its vendor SDK artifacts and native providers:

```console
GEMSTONE_MINIAPP_PORTS=hip \
GEMSTONE_TEST_PROFILE=default \
GEMSTONE_CONFORMANCE_BACKENDS= \
sbt --server --batch \
  conformanceIntegration/testFull
```

Without `GEMSTONE_MINIAPP_PORTS`, every available vendor port is selected. The
CUDA, HIP, OpenCL, and SYCL/Level Zero models are kept separate, including
both Gemstone backends for the SYCL models when both providers are available.
`GEMSTONE_CONFORMANCE_FILTER` selects individual logical identities. CTest schedules independent cases concurrently; model builds remain serial. Compiler workers use `CMAKE_BUILD_PARALLEL_LEVEL`, with the common
bounded default of at most eight. Both retained suites use the shared pinned container infrastructure and
`GEMSTONE_CONTAINER_ENGINE` (`podman` or `docker`). Image overrides are `GEMSTONE_<BACKEND>_IMAGE`; OpenCL
uses the host base image and OpenACC requires an explicit `GEMSTONE_OPENACC_IMAGE`.
Relocatable artifacts live below `target/conformance/miniapps/artifacts`.
See [the shared guide](../README.md) for provider paths and configuration.

The default bounded inputs are deliberately small while retaining the
mini-app's self-validation:

- BabelStream: 4,096 elements and two repetitions;
- miniBUDE: one pose, one PPWI, and one iteration (the smallest useful case for emulation);
- CloverLeaf: a generated 32×32, two-step deck;
- TeaLeaf: a generated 64×64, one-step conjugate-gradient deck matching the upstream reference case, with a bounded default convergence tolerance that takes 36 iterations on the host oracle.

CloverLeaf validation consumes its report file and checks the finite final
state and kinetic energy; it intentionally does not compare pressure because
the pinned CUDA port currently reports that field from the wrong reduction
argument. TeaLeaf validation requires the matching reference entry and rejects
the upstream “expected value will be invalid” fallback.

Override these with `GEMSTONE_MINIAPP_STREAM_SIZE`,
`GEMSTONE_MINIAPP_STREAM_ITERATIONS`, `GEMSTONE_MINIAPP_BUDE_POSES`, and
`GEMSTONE_MINIAPP_BUDE_ITERATIONS`. `GEMSTONE_CUDA_ARCH` and
`GEMSTONE_AMDGPU_TARGETS` select device-code targets for the container build.
TeaLeaf's bounded defaults can be made stricter or shorter with
`GEMSTONE_MINIAPP_TEA_EPS`, `GEMSTONE_MINIAPP_TEA_MAX_ITERS`, and
`GEMSTONE_MINIAPP_TEA_TIMEOUT_SECONDS` (the defaults are `1e-8`, `1000`, and
`300`, respectively). TeaLeaf validation checks both its fixed upstream
reference and the actual value produced by the paired oracle run.
The existing `GEMSTONE_*_RUNTIME` provider variables are honored, with
already-built providers under `target/conformance` and `target/out` used as
fallbacks.

The upstream projects retain their own licences. Gemstone only adds the test
driver and generated temporary decks here; consult each fetched source tree
for its licence and citation information.
