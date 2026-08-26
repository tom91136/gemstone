# Vendored CUDA conformance sources

This directory contains the implementation source needed by CUDA conformance tests. The installed CUDA toolkit is
only a compiler and reference-ABI boundary: tests may use `nvcc`, CUDA ABI/runtime headers, `libcudart`, and the CUDA
driver, but must not take Thrust, CUB, sample helpers, or sample implementations from the toolkit installation.

| Directory | Upstream | Pin | Licence | Contents |
| --- | --- | --- | --- | --- |
| `thrust` | <https://github.com/NVIDIA/thrust> | tag `1.15.0`, commit `67d6622e48ea44cfaa462d44772cc7663026c952` | Apache-2.0 with exceptions detailed in `thrust/LICENSE` | Complete `thrust/` public header tree |
| `cub` | <https://github.com/NVIDIA/cub> | tag `1.15.0`, commit `0905d7effcb3395d4157895e1d77bbcb252e55c8` | BSD-3-Clause (`cub/LICENSE.TXT`) | Complete `cub/` public header tree |
| `cuda-samples` | <https://github.com/NVIDIA/cuda-samples> | commit `b7c5481c556c3fe98db060207ecaa41a4b9a9abc` | BSD-3-Clause plus the notice in `cuda-samples/LICENSE` | Licence for selected, locally adapted sample sources |

Thrust and CUB 1.15.0 deliberately match the versions distributed with CUDA 11.6, a useful compatibility baseline.
A Thrust test declares `#pragma region vendored: thrust,cub`, compiles with `{cuda_vendor_includes}`, and emits `{dependencies}`.
The Scala suite verifies that both vendored trees occur in that dependency file and rejects toolkit paths such as
`.../include/thrust/...` or `.../include/cub/...`. This makes accidental fallback to a system library a test failure.

The Thrust command also pre-includes `{cuda_host_compat}`. This small local header disables only newer libstdc++
language/builtin fast paths that NVIDIA's CUDA device front end may not parse when the host compiler is newer than
the toolkit. It does not provide any CUDA or algorithm implementation.

CUDA Samples are not imported wholesale. A selected kernel is adapted into a standalone `check_*.cu` translation
unit under `../samples`, retains NVIDIA's licence header, identifies its exact upstream revision, performs its own
deterministic assertions, and prints only `pass`. This avoids the Samples helper framework and any sample-specific
third-party dependencies.
