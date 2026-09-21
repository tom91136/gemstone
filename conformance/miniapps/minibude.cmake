set(miniapp_minibude_models cuda hip omp-target kokkos ocl sycl acc)
set(miniapp_minibude_license LICENSE.txt)
set(miniapp_minibude_license_name miniBUDE.txt)
set(miniapp_minibude_main src/main.cpp)
set(miniapp_minibude_executable [=[add_executable(${EXE_NAME} ${IMPL_SOURCES})]=])
set(miniapp_minibude_catch_executable
  [=[add_executable(${EXE_NAME} ${IMPL_SOURCES} conformance_test.cpp)]=])
set(miniapp_minibude_omp_target_upstream_model omp)
set(miniapp_minibude_openmp_definitions OMP_TARGET)

function(miniapp_minibude_patch model source)
  set(cmake_file "${source}/CMakeLists.txt")
  file(READ "${cmake_file}" content)
  string(REPLACE [=[${CMAKE_SOURCE_DIR}]=] [=[${CMAKE_CURRENT_SOURCE_DIR}]=] content "${content}")
  # The upstream generated header is keyed to the top-level build directory,
  # which collides when several models share one build tree. Scope it to the
  # model's own binary directory.
  string(REPLACE [=[${CMAKE_BINARY_DIR}/generated]=] [=[${CMAKE_CURRENT_BINARY_DIR}/generated]=] content "${content}")
  file(WRITE "${cmake_file}" "${content}")
  foreach(model_file IN ITEMS src/ocl/model.cmake src/sycl/model.cmake)
    set(path "${source}/${model_file}")
    file(READ "${path}" content)
    string(REPLACE [=[${CMAKE_SOURCE_DIR}]=] [=[${CMAKE_CURRENT_SOURCE_DIR}]=] content "${content}")
    string(REPLACE [=[${CMAKE_BINARY_DIR}/generated]=] [=[${CMAKE_CURRENT_BINARY_DIR}/generated]=] content "${content}")
    file(WRITE "${path}" "${content}")
  endforeach()
  if(CONFORMANCE_BACKEND STREQUAL "opencl" AND model STREQUAL "ocl")
    foreach(axis RANGE 0 2)
      foreach(function IN ITEMS sin cos)
        patch_exact("${source}/src/ocl/fasten.hpp"
          "${function}(transforms_${axis}[index])"
          "native_${function}(transforms_${axis}[index])")
      endforeach()
    endforeach()
  endif()
  if(CONFORMANCE_BACKEND STREQUAL "hip" AND model STREQUAL "omp-target")
    patch_exact("${source}/src/omp/fasten.hpp" "devices.template emplace_back" "devices.emplace_back")
  endif()
endfunction()

function(miniapp_minibude_configure model)
  set(PPWI 1 CACHE STRING "" FORCE)
endfunction()

function(miniapp_minibude_test_arguments model output)
  set(arguments --deck [=[${CONFORMANCE_SUPPORT_ROOT}/data/miniapps/minibude/data/bm1]=]
    -n 1 -i 1 -p 1 -w 1)
  set(${output} "${arguments}" PARENT_SCOPE)
endfunction()

conformance_register_miniapp(minibude)
if(miniapp_minibude_enabled)
  install(DIRECTORY "${miniapp_minibude_source}/data"
    DESTINATION data/miniapps/minibude COMPONENT support)
endif()
