set(miniapp_tealeaf_models cuda hip omp-target kokkos sycl-usm sycl-acc)
set(miniapp_tealeaf_main driver/main.cpp)
set(miniapp_tealeaf_executable [=[add_executable(${EXE_NAME} ${IMPL_SOURCES})]=])
set(miniapp_tealeaf_catch_executable
  [=[add_executable(${EXE_NAME} ${IMPL_SOURCES} conformance_test.cpp)]=])
set(miniapp_tealeaf_omp_target_upstream_model omp)
set(miniapp_tealeaf_openmp_definitions OMP_TARGET DIFFUSE_OVERLOAD)

function(miniapp_tealeaf_patch model source)
  patch_exact("${source}/CMakeLists.txt" "elseif ()" "else ()")
endfunction()

function(miniapp_tealeaf_test_arguments model output)
  set(arguments --file [=[${CONFORMANCE_SUPPORT_ROOT}/data/decks/tealeaf.in]=]
    --problems [=[${CONFORMANCE_SUPPORT_ROOT}/data/miniapps/tealeaf/tea.problems]=]
    --out [=[${CONFORMANCE_REPORT_DIRECTORY}/tea.out]=])
  set(${output} "${arguments}" PARENT_SCOPE)
endfunction()

conformance_register_miniapp(tealeaf)
if(miniapp_tealeaf_enabled)
  install(FILES "${miniapp_tealeaf_source}/tea.problems"
    DESTINATION data/miniapps/tealeaf COMPONENT support)
  install(FILES "${CMAKE_CURRENT_LIST_DIR}/decks/tealeaf.in"
    DESTINATION data/decks COMPONENT support)
endif()
