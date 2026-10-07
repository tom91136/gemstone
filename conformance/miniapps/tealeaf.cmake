function(tealeaf_adapt model source)
  patch_exact("${source}/CMakeLists.txt" "elseif ()" "else ()")
  if(model STREQUAL "cuda")
    set(flags [=[-forward-unknown-to-host-compiler -arch=${CUDA_ARCH} -use_fast_math -restrict]=])
    if(CONFORMANCE_COMPILER STREQUAL "clang")
      set(flags [=[--cuda-gpu-arch=${CUDA_ARCH}]=])
    endif()
    patch_exact("${source}/src/cuda/model.cmake"
      [=[-forward-unknown-to-host-compiler -arch=${CUDA_ARCH} -use_fast_math -restrict -keep]=] "${flags}")
  endif()
endfunction()

conformance_miniapp(tealeaf
  MODELS cuda hip omp-target=omp kokkos sycl-usm sycl-acc
  MAIN driver/main.cpp
  OPENMP_DEFINITIONS OMP_TARGET DIFFUSE_OVERLOAD
  DATA tea.problems
  DECKS decks/tealeaf.in
  ARGUMENTS --file [=[${CONFORMANCE_SUPPORT_ROOT}/data/decks/tealeaf.in]=]
    --problems [=[${CONFORMANCE_SUPPORT_ROOT}/data/miniapps/tealeaf/tea.problems]=]
    --out [=[${CONFORMANCE_REPORT_DIRECTORY}/tea.out]=]
  ADAPT tealeaf_adapt)
