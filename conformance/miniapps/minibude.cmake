function(minibude_adapt model source)
  set(cmake_file "${source}/CMakeLists.txt")
  file(READ "${cmake_file}" content)
  string(REPLACE [=[${CMAKE_SOURCE_DIR}]=] [=[${CMAKE_CURRENT_SOURCE_DIR}]=] content "${content}")
  # The upstream generated header is keyed to the top-level build directory,
  # which collides when several models share one build tree.
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
  set(PPWI 1 CACHE STRING "" FORCE)
endfunction()

conformance_miniapp(minibude
  MODELS cuda hip omp-target=omp kokkos ocl sycl acc
  MAIN src/main.cpp
  LICENCE_FILE LICENSE.txt miniBUDE.txt
  EXCLUDE data makedeck
  OPENMP_DEFINITIONS OMP_TARGET
  DATA data
  ARGUMENTS --deck [=[${CONFORMANCE_SUPPORT_ROOT}/data/miniapps/minibude/data/bm1]=] -n 1 -i 1 -p 1 -w 1
  ADAPT minibude_adapt)
