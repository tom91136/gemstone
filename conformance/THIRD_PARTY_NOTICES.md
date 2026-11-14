# Third-party notices

`conformance/` is licensed under the Apache License 2.0, the same licence as the
repository root (`LICENCE.txt`, copied here as `LICENSE`). The files listed
below contain code, patch text, or data derived from upstream projects and keep
their upstream licence. The four GPL-3.0-or-later files are separate from the
Apache-2.0 grant.

`conformance/unified/licences/gpuocelot.txt` is a verbatim copy of the GPU
Ocelot licence text and is installed into CUDA and HIP artifacts.

## Packaging decisions

- `conformance/LICENSE` carries the Apache-2.0 text for the project code.
- GPL-3.0-or-later derivatives stay in place and are marked with an SPDX line
  where the format accepts one. The CloverLeaf patch text moved out of
  `miniapps/cloverleaf.cmake` into `patches/cloverleaf-cuda-architectures.patch`
  so the GPL part is a whole file.
- CloverLeaf and TeaLeaf artifacts install the upstream licence text and a
  source pointer generated from the pins in `dependencies.cmake`.
- Artifacts install runtime component licences and notices by copying the files
  that the toolchain image already carries. The check derives components from
  the libraries actually bundled; it does not test a fixed list of libraries.
- Aspartame is pending: the pinned commit has no licence, so no notice can be
  installed for it. The pin must change once Aspartame carries a licence.

## Derived files

| File | Upstream and pin | Licence | Copyright |
|------|------------------|---------|-----------|
| `unified/src/backend_thrust.hpp` | GPU Ocelot direct and indirect device-function checks; exact upstream revision not recorded | BSD-3-Clause | Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech |
| `unified/src/hsa/runtime.cpp` | AMD direct-HSA dispatch sample; exact sample and revision not recorded | Apache-2.0 | No copyright line in the file |
| `unified/src/cases/rocthrust_iterators.inl` | rocThrust `rocm-7.1.1`, `testing/zip_iterator_reduce`, `zip_iterator_reduce_by_key`, zip and counting iterators | Apache-2.0 | No copyright line in the file |
| `unified/src/cases/rocthrust_complex_async.inl` | rocThrust `rocm-7.1.1`, dereference, complex, permutation and reverse iterator, tabulate output, async for_each and async reduce tests | Apache-2.0 | No copyright line in the file |
| `unified/src/cases/rocthrust_device.inl` | rocThrust `rocm-7.1.1`, static assert, vector, device pointer and reference, memory and universal memory tests | Apache-2.0 | No copyright line in the file |
| `miniapps/babelstream.cmake` | BabelStream `5cb9445`; OpenCL, Kokkos, and CUDA model patches | BabelStream custom permissive licence | No copyright line in the file |
| `miniapps/cloverleaf.cmake` | CloverLeaf `e9afc2b`; build integration after the GPL patch text moved to `patches/cloverleaf-cuda-architectures.patch` | Apache-2.0 | No copyright line in the file |
| `miniapps/minibude.cmake` | miniBUDE `2b23c27`; CUDA and OpenCL patches | Apache-2.0 | No copyright line in the file |
| `vendor/cuda/patches/binaryPartitionCG.cmake` | cuda-samples `v11.8` and `v13.3`; binaryPartitionCG changes | BSD-3-Clause | No copyright line in the file |
| `vendor/cuda/patches/conjugateGradient.cmake` | cuda-samples `v11.8` and `v13.3`; conjugateGradient changes | BSD-3-Clause | No copyright line in the file |
| `vendor/cuda/patches/radixSortThrust.cmake` | cuda-samples `v11.8` and `v13.3`; radixSortThrust changes | BSD-3-Clause | No copyright line in the file |
| `vendor/cuda/patches/simpleCudaGraphs.cmake` | cuda-samples `v11.8` and `v13.3`; simpleCudaGraphs changes | BSD-3-Clause | No copyright line in the file |
| `vendor/cuda/patches/UnifiedMemoryStreams.cmake` | cuda-samples `v11.8` and `v13.3`; UnifiedMemoryStreams changes | BSD-3-Clause | No copyright line in the file |
| `vendor/cuda/cccl.cmake` | CCCL `v2.8.2` and `v3.4.3`; Thrust examples and libcudacxx tests | Apache-2.0 with LLVM exception | No copyright line in the file |
| `vendor/cuda/libraries.cmake` | CUDALibrarySamples `72f16ae`; cuSPARSE examples | Apache-2.0 | No copyright line in the file |
| `vendor/hip.cmake` | rocm-examples `rocm-7.1.1`; HIP-Basic, Applications, and Libraries examples | MIT | No copyright line in the file |
| `vendor/sycl.cmake` | oneAPI-samples `2025.3.0`; DirectProgramming and oneMKL samples | MIT | No copyright line in the file |
| `cts/opencl/CMakeLists.txt` | OpenCL-CTS `726a6d9`; CMake common file | Apache-2.0 | No copyright line in the file |
| `cts/sycl/CMakeLists.txt` | SYCL-CTS `5ea6259`; CMake and test sources | Apache-2.0 | No copyright line in the file |
| `cts/levelzero/CMakeLists.txt` | level-zero-tests `de9791d`; CMake and test sources | MIT | No copyright line in the file |
| `patches/libgomp-default-device.patch` | GCC 14.2.0, `libgomp/target.c`; two added lines | GPL-3.0-or-later with GCC Runtime Library Exception | Copyright (C) Free Software Foundation, Inc. |
| `patches/hip-device-lib-decls.patch` | ROCm 7.1.1 HIP headers, `include/hip/amd_detail`; added after the audit | MIT | No copyright line in the file |
| `miniapps/decks/cloverleaf.in` | CloverLeaf `e9afc2b`, `InputDecks`; adapted grid, step, and tolerance values | GPL-3.0-or-later | No copyright line in the file |
| `miniapps/decks/tealeaf.in` | TeaLeaf `883b811`, `Benchmarks/tea_bm_1.in`; adapted benchmark data | GPL-3.0-or-later | No copyright line in the file |
| `unified/licences/gpuocelot.txt` | GPU Ocelot licence text, copied verbatim | BSD-3-Clause | Copyright (c) 2015, Computer Architecture and Systems Laboratory at Georgia Tech |

The new GPL file `patches/cloverleaf-cuda-architectures.patch` is derived from
CloverLeaf `e9afc2b`, `src/cuda/model.cmake`, and is GPL-3.0-or-later.

## GPL components in artifacts

The CloverLeaf and TeaLeaf miniapps build GPL-3.0-or-later programs. Their
artifacts install `licences/CloverLeaf.txt`, `licences/TeaLeaf.txt`,
`licences/cloverleaf-SOURCE.txt`, and `licences/tealeaf-SOURCE.txt`. The source
pointers are generated from `dependencies.cmake` and name the pinned upstream
URL and commit.

## Runtime libraries in artifacts

Artifacts that bundle runtime libraries install the matching component notices
under `licences/runtime/<component>/`, together with
`licences/runtime-libraries.tsv`, which records which bundled library came from
which component. CUDA notices come from the CUDA image, oneAPI notices from
`/opt/intel/oneapi`, ROCm notices from `/opt/rocm/share/doc`, GCC libgomp
notices from the rebuilt runtime, and distribution loader notices from
`/usr/share/doc`. The artifact build fails when a bundled library has no
component notice.
