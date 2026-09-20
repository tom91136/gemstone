find_package(Threads REQUIRED)
find_path(CUDA_INCLUDE_DIR cuda.h REQUIRED)
# Generated ABI declarations are pinned to this API profile, including its ABI types.
include(ValidateCUDAHeader.cmake)

add_library(gemstone_cuda SHARED
  src/runtime.cpp
  src/unsupported.cpp
  src/cublas.cpp)
set_target_properties(gemstone_cuda PROPERTIES OUTPUT_NAME cuda SOVERSION 1 CXX_STANDARD 17)
target_include_directories(gemstone_cuda PRIVATE "${CUDA_INCLUDE_DIR}")
target_compile_options(gemstone_cuda PRIVATE -Wall -Wextra -Werror)
set_source_files_properties(src/unsupported.cpp PROPERTIES
  COMPILE_OPTIONS "-Wno-deprecated-declarations")
target_link_libraries(gemstone_cuda PRIVATE
  gemstone_zstd
  gemstone_lz4
  gemstone_execution_client
  Aspartame::Aspartame
  Threads::Threads
  m)
gemstone_require_abi_implementations(
  TARGET gemstone_cuda
  PLATFORM cuda)
# The facade spells several ABI-compatible opaque types without vendor typedefs.
set_target_properties(gemstone_cuda PROPERTIES C_VISIBILITY_PRESET default CXX_VISIBILITY_PRESET default)
if(BUILD_TESTING)
  add_test(NAME cuda/header-compatibility COMMAND ${CMAKE_COMMAND}
    "-DCUDA_SOURCE=${CMAKE_CURRENT_SOURCE_DIR}"
    "-DTEST_DIRECTORY=${CMAKE_CURRENT_BINARY_DIR}/header-compatibility"
    -P "${CMAKE_CURRENT_SOURCE_DIR}/test/header-compatibility.cmake")
  add_executable(cuda_cross_workgroup_test test/cross-workgroup.cpp)
  target_compile_features(cuda_cross_workgroup_test PRIVATE cxx_std_17)
  target_include_directories(cuda_cross_workgroup_test PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_cross_workgroup_test PRIVATE ${CMAKE_DL_LIBS})
  gemstone_test_target(cuda_cross_workgroup_test)
  add_executable(cuda_execution_unavailable_smoke test/execution-unavailable-smoke.cpp)
  target_compile_features(cuda_execution_unavailable_smoke PRIVATE cxx_std_17)
  target_compile_options(cuda_execution_unavailable_smoke PRIVATE -Wno-deprecated-declarations)
  target_include_directories(cuda_execution_unavailable_smoke PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_execution_unavailable_smoke PRIVATE gemstone_cuda)
  gemstone_test_target(cuda_execution_unavailable_smoke)
  add_test(NAME cuda/execution-boundary COMMAND ${CMAKE_COMMAND} -E env
    "LD_LIBRARY_PATH=$<TARGET_FILE_DIR:gemstone_cuda>" $<TARGET_FILE:cuda_execution_unavailable_smoke>)
endif()
