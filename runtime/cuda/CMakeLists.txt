cmake_minimum_required(VERSION 3.25)
project(gemstone-runtime-cuda LANGUAGES C CXX)

find_package(Threads REQUIRED)
find_path(CUDA_INCLUDE_DIR cuda.h REQUIRED)
# Generated driver stubs are pinned to this API profile, including its ABI types.
file(STRINGS "${CUDA_INCLUDE_DIR}/cuda.h" CUDA_VERSION_DEFINITION
  REGEX "^#[ 	]*define[ 	]+CUDA_VERSION[ 	]+[0-9]+")
if(NOT CUDA_VERSION_DEFINITION MATCHES "CUDA_VERSION[ 	]+13030([ 	]|$)")
  message(FATAL_ERROR "Gemstone generated CUDA bindings require CUDA 13.3 headers (CUDA_VERSION=13030); selected ${CUDA_INCLUDE_DIR}/cuda.h: ${CUDA_VERSION_DEFINITION}")
endif()


add_library(gemstone_cuda SHARED
  generated/c/gemstone_cuda_rpc_client.c
  generated/c/gemstone_cuda_driver_stubs.c
  src/main/c/gemstone_cuda_compat.c
  src/main/c/gemstone_cublas_compat.c
  src/main/c/gemstone_cuda_rpc_transport.c)
set_target_properties(gemstone_cuda PROPERTIES OUTPUT_NAME cuda SOVERSION 1 C_STANDARD 17)
target_include_directories(gemstone_cuda PRIVATE generated/c src/main/c "${CUDA_INCLUDE_DIR}" "${CMAKE_CURRENT_SOURCE_DIR}/../../native/execution")
target_compile_options(gemstone_cuda PRIVATE -Wall -Wextra -Werror)
target_link_libraries(gemstone_cuda PRIVATE Threads::Threads m)
target_link_options(gemstone_cuda PRIVATE
  "LINKER:--version-script=${CMAKE_CURRENT_SOURCE_DIR}/generated/c/gemstone_cuda.map"
  "LINKER:--version-script=${CMAKE_CURRENT_SOURCE_DIR}/src/main/c/gemstone_cublas.map")
set_property(TARGET gemstone_cuda APPEND PROPERTY LINK_DEPENDS
  "${CMAKE_CURRENT_SOURCE_DIR}/generated/c/gemstone_cuda.map"
  "${CMAKE_CURRENT_SOURCE_DIR}/src/main/c/gemstone_cublas.map")
install(TARGETS gemstone_cuda LIBRARY DESTINATION lib)

include(CTest)
if(BUILD_TESTING)
  add_executable(cuda_rpc_boundaries test/rpc-boundaries.cpp)
  target_compile_features(cuda_rpc_boundaries PRIVATE cxx_std_17)
  target_compile_options(cuda_rpc_boundaries PRIVATE -UNDEBUG -Wall -Wextra -Werror)
  target_link_libraries(cuda_rpc_boundaries PRIVATE Threads::Threads ${CMAKE_DL_LIBS})
  foreach(boundary context-routing session-loss global-outputs cublas-axpy cublas-gemm cublas-scal cublas-gemv cublas-results cublas-half)
    add_test(NAME cuda/${boundary} COMMAND ${CMAKE_COMMAND} -E env
      "LD_LIBRARY_PATH=$<TARGET_FILE_DIR:gemstone_cuda>" $<TARGET_FILE:cuda_rpc_boundaries> ${boundary})
    set_tests_properties(cuda/${boundary} PROPERTIES TIMEOUT 15)
  endforeach()
  add_test(NAME cuda/header-compatibility COMMAND ${CMAKE_COMMAND}
    "-DCUDA_SOURCE=${CMAKE_CURRENT_SOURCE_DIR}"
    "-DTEST_DIRECTORY=${CMAKE_CURRENT_BINARY_DIR}/header-compatibility"
    -P "${CMAKE_CURRENT_SOURCE_DIR}/test/header-compatibility.cmake")
  add_executable(cuda_cross_workgroup_test test/cross-workgroup.c)
  target_compile_features(cuda_cross_workgroup_test PRIVATE c_std_17)
  target_compile_options(cuda_cross_workgroup_test PRIVATE -Wall -Wextra -Werror)
  target_link_libraries(cuda_cross_workgroup_test PRIVATE ${CMAKE_DL_LIBS})
  add_executable(cuda_rpc_auth_smoke test/rpc-auth-smoke.c)
  target_link_libraries(cuda_rpc_auth_smoke PRIVATE gemstone_cuda)
  add_executable(cuda_surface_smoke test/surface-smoke.cpp)
  target_compile_features(cuda_surface_smoke PRIVATE cxx_std_17)
  target_include_directories(cuda_surface_smoke PRIVATE generated/c)
  target_link_libraries(cuda_surface_smoke PRIVATE ${CMAKE_DL_LIBS})
  add_test(NAME cuda/surface COMMAND ${CMAKE_COMMAND} -E env
    "LD_LIBRARY_PATH=$<TARGET_FILE_DIR:gemstone_cuda>" $<TARGET_FILE:cuda_surface_smoke>)
endif()
