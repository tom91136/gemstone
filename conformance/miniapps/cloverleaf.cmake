set(miniapp_cloverleaf_models cuda hip omp-target kokkos sycl-usm sycl-acc acc)
set(miniapp_cloverleaf_license LICENCE.txt)
set(miniapp_cloverleaf_license_name CloverLeaf.txt)
set(miniapp_cloverleaf_main driver/clover_leaf.cpp)
set(miniapp_cloverleaf_executable [=[add_executable(${EXE_NAME} ${IMPL_SOURCES})]=])
set(miniapp_cloverleaf_catch_executable
  [=[add_executable(${EXE_NAME} ${IMPL_SOURCES} conformance_test.cpp)]=])
set(miniapp_cloverleaf_openmp_definitions OMP_TARGET DIFFUSE_OVERLOAD)

function(miniapp_cloverleaf_patch model source)
  if(CONFORMANCE_BACKEND STREQUAL "hip")
    patch_exact("${source}/src/hip/initialise.cpp"
      [=[<< "gfx" << props.gcnArch << ")"]=] [=[<< props.gcnArchName << ")"]=])
  endif()
endfunction()

function(miniapp_cloverleaf_configure model)
  if(model MATCHES "^(cuda|hip)$")
    # CloverLeaf's CUDA-style models rely on launch completion between kernels.
    # Keep this scoped to its subdirectory instead of changing the process-wide
    # launch mode or leaking an upstream cache option into other miniapps.
    set(SYNC_ALL_KERNELS ON PARENT_SCOPE)
  endif()
endfunction()

function(miniapp_cloverleaf_test_arguments model output)
  set(arguments --file [=[${CONFORMANCE_SUPPORT_ROOT}/data/decks/cloverleaf.in]=]
    --out [=[${CONFORMANCE_REPORT_DIRECTORY}/clover.out]=])
  set(${output} "${arguments}" PARENT_SCOPE)
endfunction()

conformance_register_miniapp(cloverleaf)
if(miniapp_cloverleaf_enabled)
  install(FILES "${CMAKE_CURRENT_LIST_DIR}/decks/cloverleaf.in"
    DESTINATION data/decks COMPONENT support)
endif()
