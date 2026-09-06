# SPDX-License-Identifier: Apache-2.0
cmake_minimum_required(VERSION 3.25)
project(gemstone-runtime-hsa LANGUAGES CXX)

include(../../cmake/Aspartame.cmake)

set(HSAKMT_INCLUDE_DIR "" CACHE PATH "Directory containing hsakmt/hsakmt.h")
if(NOT EXISTS "${HSAKMT_INCLUDE_DIR}/hsakmt/hsakmt.h")
  include(FetchContent)
  FetchContent_Declare(rocr_headers
    URL https://github.com/ROCm/ROCR-Runtime/archive/refs/tags/rocm-7.2.4.tar.gz
    URL_HASH SHA256=60532cd86edce5a603aa18df406ec1f5a3d18f0663d79b3b7822ff721c5a04ec
    SOURCE_SUBDIR gemstone-no-cmake-project
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE
  )
  FetchContent_MakeAvailable(rocr_headers)
  set(HSAKMT_INCLUDE_DIR "${rocr_headers_SOURCE_DIR}/libhsakmt/include")
endif()

set(HSA_INCLUDE_DIR "" CACHE PATH "Directory containing the public HSA headers")
if(NOT EXISTS "${HSA_INCLUDE_DIR}/hsa/hsa.h")
  if(EXISTS "${rocr_headers_SOURCE_DIR}/runtime/hsa-runtime/inc/hsa.h")
    set(HSA_INCLUDE_DIR "${rocr_headers_SOURCE_DIR}/runtime/hsa-runtime/inc")
  elseif(EXISTS "/usr/include/hsa/hsa.h")
    set(HSA_INCLUDE_DIR "/usr/include")
  else()
    message(FATAL_ERROR "Unable to locate the public HSA headers; set HSA_INCLUDE_DIR")
  endif()
endif()

add_library(gemstone_execution_rpc STATIC
  ../../native/execution/gemstone_execution_rpc.cpp
)
set_target_properties(gemstone_execution_rpc PROPERTIES POSITION_INDEPENDENT_CODE ON)
target_include_directories(gemstone_execution_rpc PUBLIC ../../native/execution)
target_compile_features(gemstone_execution_rpc PUBLIC cxx_std_17)

add_library(gemstone_hsa_doorbell STATIC
  src/aql.cpp
  src/aql_execution.cpp
  src/code_object_metadata.cpp
  src/doorbell_monitor.cpp
  src/execution_rpc_executor.cpp
  src/virtual_kfd.cpp
)
set_target_properties(gemstone_hsa_doorbell PROPERTIES POSITION_INDEPENDENT_CODE ON)
target_include_directories(gemstone_hsa_doorbell PUBLIC include)
target_include_directories(gemstone_hsa_doorbell PRIVATE ../../native/execution)
target_compile_features(gemstone_hsa_doorbell PUBLIC cxx_std_17)
target_compile_options(gemstone_hsa_doorbell PRIVATE -Wall -Wextra -Wpedantic -Werror)
target_link_libraries(gemstone_hsa_doorbell PUBLIC gemstone_execution_rpc PRIVATE Aspartame::Aspartame)

add_library(gemstone_hsakmt SHARED
  src/hsakmt.cpp
  src/hsakmt_unsupported.cpp
)
target_include_directories(gemstone_hsakmt PUBLIC include ${HSAKMT_INCLUDE_DIR})
target_include_directories(gemstone_hsakmt PRIVATE ../../native/execution)
target_compile_features(gemstone_hsakmt PUBLIC cxx_std_17)
# The official hsakmt ABI header deliberately uses GNU anonymous structs.
target_compile_options(gemstone_hsakmt PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hsakmt PRIVATE gemstone_hsa_doorbell gemstone_execution_rpc)

add_library(gemstone_hsa_runtime SHARED
  src/hsa_runtime.cpp
)
set_target_properties(gemstone_hsa_runtime PROPERTIES OUTPUT_NAME "hsa-runtime64" SOVERSION 1)
target_include_directories(gemstone_hsa_runtime PUBLIC include ${HSA_INCLUDE_DIR})
target_include_directories(gemstone_hsa_runtime PRIVATE ../../native/execution)
target_compile_features(gemstone_hsa_runtime PUBLIC cxx_std_17)
target_compile_options(gemstone_hsa_runtime PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hsa_runtime PRIVATE gemstone_hsa_doorbell gemstone_execution_rpc)
target_link_options(gemstone_hsa_runtime PRIVATE
  "-Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/abi/hsa-runtime64.map")

include(CTest)
if(BUILD_TESTING)
  add_executable(gemstone_hsa_doorbell_test tests/doorbell_monitor_test.cpp)
  target_link_libraries(gemstone_hsa_doorbell_test PRIVATE gemstone_hsa_doorbell)
  add_executable(gemstone_hsa_aql_execution_test tests/aql_execution_test.cpp)
  target_link_libraries(gemstone_hsa_aql_execution_test PRIVATE gemstone_hsa_doorbell)
  add_executable(gemstone_hsa_code_object_metadata_test
    tests/code_object_metadata_test.cpp)
  target_link_libraries(gemstone_hsa_code_object_metadata_test PRIVATE gemstone_hsa_doorbell)
  add_executable(gemstone_hsa_runtime_test tests/hsa_runtime_test.cpp)
  target_link_libraries(gemstone_hsa_runtime_test PRIVATE gemstone_hsa_runtime)
  add_executable(gemstone_hsa_target_test tests/target_test.cpp)
  target_include_directories(gemstone_hsa_target_test PRIVATE include)
  add_executable(gemstone_hsakmt_test tests/hsakmt_test.cpp)
  target_include_directories(gemstone_hsakmt_test PRIVATE ${HSAKMT_INCLUDE_DIR})
  target_link_libraries(gemstone_hsakmt_test PRIVATE gemstone_hsakmt)
  if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    target_compile_options(gemstone_hsa_doorbell_test PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
    target_compile_options(gemstone_hsa_aql_execution_test PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
    target_compile_options(gemstone_hsa_code_object_metadata_test PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
    target_compile_options(gemstone_hsa_runtime_test PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
    target_compile_options(gemstone_hsa_target_test PRIVATE -UNDEBUG -Wall -Wextra -Wpedantic -Werror)
    target_compile_options(gemstone_hsakmt_test PRIVATE -UNDEBUG -Wall -Wextra -Werror -Wno-pedantic)
  endif()
  add_test(NAME gemstone-hsa-doorbell COMMAND gemstone_hsa_doorbell_test)
  add_test(NAME gemstone-hsa-aql-execution COMMAND gemstone_hsa_aql_execution_test)
  add_test(NAME gemstone-hsa-code-object-metadata COMMAND gemstone_hsa_code_object_metadata_test)
  add_test(NAME gemstone-hsa-runtime COMMAND gemstone_hsa_runtime_test)
  add_test(NAME gemstone-hsa-target COMMAND gemstone_hsa_target_test)
  add_test(NAME gemstone-hsakmt COMMAND gemstone_hsakmt_test)
  add_test(
    NAME gemstone-hsakmt-symbols
    COMMAND ${CMAKE_COMMAND}
      -DNM=${CMAKE_NM}
      -DLIBRARY=$<TARGET_FILE:gemstone_hsakmt>
      -DMANIFEST=${CMAKE_CURRENT_SOURCE_DIR}/abi/hsakmt-symbols.txt
      -P ${CMAKE_CURRENT_SOURCE_DIR}/tests/check_hsakmt_symbols.cmake
  )
endif()
