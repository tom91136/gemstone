find_package(Threads REQUIRED)
if(NOT CUDA_INCLUDE_DIR OR NOT EXISTS "${CUDA_INCLUDE_DIR}/cuda.h")
  message(FATAL_ERROR "CUDA_INCLUDE_DIR must point at the pinned CUDA headers containing cuda.h")
endif()
include(ValidateCUDAHeader.cmake)

add_library(gemstone_cuda SHARED
  src/runtime.cpp
  ../generated/cuda/gemstone_cuda_unsupported.cpp)
set_target_properties(gemstone_cuda PROPERTIES OUTPUT_NAME cuda SOVERSION 1)
target_include_directories(gemstone_cuda SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
gemstone_project_target(gemstone_cuda)
target_link_libraries(gemstone_cuda PRIVATE
  gemstone_zstd
  gemstone_lz4
  gemstone_execution_client
  Threads::Threads
  m)
gemstone_require_abi_implementations(
  TARGET gemstone_cuda
  PLATFORM cuda)
set_target_properties(gemstone_cuda PROPERTIES C_VISIBILITY_PRESET default CXX_VISIBILITY_PRESET default)
if(BUILD_TESTING)
  add_executable(cuda_process_exit_test test/process-exit.cpp)
  gemstone_project_target(cuda_process_exit_test)
  target_include_directories(cuda_process_exit_test SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_process_exit_test PRIVATE gemstone_cuda)
  add_test(NAME cuda/process-exit COMMAND cuda_process_exit_test)
  gemstone_set_tests_properties(cuda/process-exit PROPERTIES TIMEOUT 10)
  gemstone_memory_client(cuda gemstone_cuda)
  add_test(NAME cuda/header-compatibility COMMAND ${CMAKE_COMMAND}
    "-DCUDA_SOURCE=${CMAKE_CURRENT_SOURCE_DIR}"
    "-DTEST_DIRECTORY=${CMAKE_CURRENT_BINARY_DIR}/header-compatibility"
    -P "${CMAKE_CURRENT_SOURCE_DIR}/test/header-compatibility.cmake")
  add_executable(cuda_cross_workgroup_test test/cross-workgroup.cpp)
  target_include_directories(cuda_cross_workgroup_test SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_cross_workgroup_test PRIVATE ${CMAKE_DL_LIBS})
  gemstone_test_target(cuda_cross_workgroup_test)
  add_executable(cuda_execution_unavailable_smoke test/execution-unavailable-smoke.cpp)
  target_include_directories(cuda_execution_unavailable_smoke SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_execution_unavailable_smoke PRIVATE gemstone_cuda)
  gemstone_test_target(cuda_execution_unavailable_smoke)
  target_compile_options(cuda_execution_unavailable_smoke PRIVATE -Wno-deprecated-declarations)
  add_test(NAME cuda/execution-boundary COMMAND ${CMAKE_COMMAND} -E env
    "LD_LIBRARY_PATH=$<TARGET_FILE_DIR:gemstone_cuda>" $<TARGET_FILE:cuda_execution_unavailable_smoke>)
  add_executable(cuda_code_mode_test test/code-mode.cpp)
  target_include_directories(cuda_code_mode_test SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_code_mode_test PRIVATE gemstone_cuda)
  gemstone_test_target(cuda_code_mode_test)
  # The facade connects to the execution service once per process, so each case and its fixture run in their own.
  gemstone_discover_tests(cuda_code_mode_test TEST_PREFIX "cuda/code-mode/")
  add_executable(cuda_tensor_map_test test/tensor-map.cpp)
  target_include_directories(cuda_tensor_map_test SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_tensor_map_test PRIVATE gemstone_cuda)
  gemstone_test_target(cuda_tensor_map_test)
  add_test(NAME cuda/tensor-map COMMAND ${CMAKE_COMMAND} -E env
    "LD_LIBRARY_PATH=$<TARGET_FILE_DIR:gemstone_cuda>" $<TARGET_FILE:cuda_tensor_map_test>)
  add_executable(cuda_memcpy_rectangle_test test/memcpy-rectangle.cpp)
  target_include_directories(cuda_memcpy_rectangle_test SYSTEM PRIVATE "${CUDA_INCLUDE_DIR}")
  target_link_libraries(cuda_memcpy_rectangle_test PRIVATE gemstone_cuda)
  gemstone_test_target(cuda_memcpy_rectangle_test)
  add_test(NAME cuda/memcpy-rectangle COMMAND cuda_memcpy_rectangle_test)
endif()
