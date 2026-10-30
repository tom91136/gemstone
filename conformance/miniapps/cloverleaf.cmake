function(cloverleaf_adapt model source)
  if(model STREQUAL "cuda")
    # The 1.0 release disables CMake's architecture selection in favour of its
    # own -arch option. Conformance needs both real and virtual code images.
    patch_exact("${source}/src/cuda/model.cmake"
      "        set(CMAKE_CUDA_ARCHITECTURES OFF)" "")
  endif()
  if(model MATCHES "^(cuda|hip)$")
    # CloverLeaf's CUDA-style models rely on launch completion between kernels.
    # Keep this scoped to its subdirectory instead of changing the process-wide
    # launch mode or leaking an upstream cache option into other miniapps.
    set(SYNC_ALL_KERNELS ON PARENT_SCOPE)
  endif()
endfunction()

conformance_miniapp(cloverleaf
  MODELS cuda hip omp-target kokkos sycl-usm sycl-acc acc
  MAIN driver/clover_leaf.cpp
  LICENCE_FILE LICENCE.txt CloverLeaf.txt
  OPENMP_DEFINITIONS OMP_TARGET DIFFUSE_OVERLOAD
  DECKS decks/cloverleaf.in
  ARGUMENTS --file [=[${CONFORMANCE_SUPPORT_DIR}/data/decks/cloverleaf.in]=]
    --out [=[${CONFORMANCE_REPORT_DIR}/clover.out]=]
  ADAPT cloverleaf_adapt)
