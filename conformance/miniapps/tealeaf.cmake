conformance_miniapp(tealeaf
  MODELS cuda hip omp-target=omp kokkos sycl-usm sycl-acc
  MAIN driver/main.cpp
  OPENMP_DEFINITIONS OMP_TARGET DIFFUSE_OVERLOAD
  DATA tea.problems
  DECKS decks/tealeaf.in
  ARGUMENTS --file [=[${CONFORMANCE_SUPPORT_DIR}/data/decks/tealeaf.in]=]
    --problems [=[${CONFORMANCE_SUPPORT_DIR}/data/miniapps/tealeaf/tea.problems]=]
    --out [=[${CONFORMANCE_REPORT_DIR}/tea.out]=])
