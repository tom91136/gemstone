# Mini-app conformance

This lane brings the four Polyregion mini-app workloads into Gemstone's
compile-once, run-twice conformance model:

| application | pinned upstream revision | vendor ports exercised |
| --- | --- | --- |
| BabelStream | `UoB-HPC/BabelStream@17ab377b0e919e14fd3df2b67268761fdac8abb3` | CUDA, HIP, OpenCL, SYCL |
| miniBUDE | `UoB-HPC/miniBUDE@570f66c1fd0c29b7f3ab6fa96fba87f4561aa44c` | CUDA, HIP, OpenCL, SYCL |
| CloverLeaf | `UoB-HPC/CloverLeaf@3e10ff9268d3704a744e5a2a4ea3c62831078082` | CUDA, HIP, SYCL accessor, SYCL USM |
| TeaLeaf | `UoB-HPC/TeaLeaf@e70261c0be40537da75b258108ed2898f84f3c58` | CUDA, HIP, SYCL accessor, SYCL USM |

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

CloverLeaf and TeaLeaf require double precision, which is not exposed by the
Intel Arc GPU used by the SYCL oracle on this host. Their SYCL oracle command
therefore uses the bundled OpenCL adapter with the host OpenCL CPU device;
Gemstone still runs the requested SYCL/OpenCL or SYCL/Level-Zero provider.

The lane is opt-in because its compiler matrix is necessarily host-dependent:

```console
GEMSTONE_MINIAPPS=1 \
GEMSTONE_MINIAPP_PORTS=hip \
GEMSTONE_NO_ORACLE=1 \
sbt --server -batch \
  'conformance / Test / testOnly net.kurobako.gemstone.conformance.MiniappsConformanceSuite'
```

Without `GEMSTONE_MINIAPP_PORTS`, every available vendor port is selected. The
CUDA, HIP, OpenCL, and SYCL/Level Zero models are kept separate, including
both Gemstone backends for the SYCL models when both providers are available.
`GEMSTONE_MINIAPP_FILTER` and `GEMSTONE_MINIAPP_SHARD=index/count` filter the
fully qualified identities. Runtime cases remain isolated and must use
`GEMSTONE_MINIAPP_JOBS=1`; each model build uses bounded Ninja parallelism via
`GEMSTONE_MINIAPP_BUILD_JOBS` (default: up to `8` host compile jobs). Model
invocations themselves remain serialized so a large translation unit cannot be
multiplied across applications. Set it to `1` for a fully serial build or
lower it if the container host has limited memory.

The build container is selected by `GEMSTONE_MINIAPP_CONTAINER_ENGINE` (or the
existing `GEMSTONE_UNIFIED_CONTAINER_ENGINE`) and the pinned CUDA, HIP, OpenCL,
and oneAPI images can be replaced with approved local images through
`GEMSTONE_MINIAPP_<BACKEND>_IMAGE`. The host supplies only the ELF loader,
libc, standard compute loaders, host C++ ABI, and the kernel-facing vendor
driver at execution time. The generated
relocatable artifacts live below `target/conformance/miniapps/artifacts`.

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
The existing `GEMSTONE_NVCC`, `GEMSTONE_HIPCC`, and
`GEMSTONE_SYCL_COMPILER` variables do not cause a host-side compile; use the
container image overrides when a local image/toolchain is required.
The existing `GEMSTONE_*_RUNTIME` provider variables are honored, with
already-built providers under `target/conformance` and `target/out` used as
fallbacks.

The upstream projects retain their own licences. Gemstone only adds the test
driver and generated temporary decks here; consult each fetched source tree
for its licence and citation information.
