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
  set(HSAKMT_INCLUDE_DIR "${rocr_headers_SOURCE_DIR}/libhsakmt/include" CACHE PATH "Directory containing hsakmt/hsakmt.h" FORCE)
endif()

set(HSA_INCLUDE_DIR "" CACHE PATH "Directory containing the public HSA headers")
if(NOT EXISTS "${HSA_INCLUDE_DIR}/hsa.h")
  if(EXISTS "${rocr_headers_SOURCE_DIR}/runtime/hsa-runtime/inc/hsa.h")
    set(HSA_INCLUDE_DIR "${rocr_headers_SOURCE_DIR}/runtime/hsa-runtime/inc" CACHE PATH "Directory containing the public HSA headers" FORCE)
  elseif(EXISTS "/usr/include/hsa/hsa.h")
    set(HSA_INCLUDE_DIR "/usr/include/hsa" CACHE PATH "Directory containing the public HSA headers" FORCE)
  else()
    message(FATAL_ERROR "Unable to locate the public HSA headers; set HSA_INCLUDE_DIR")
  endif()
endif()

add_library(gemstone_hsa_core STATIC
  src/aql.cpp
  src/aql_execution.cpp
  src/doorbell_monitor.cpp
  src/execution_rpc_executor.cpp
  src/virtual_kfd.cpp
)
set_target_properties(gemstone_hsa_core PROPERTIES
  POSITION_INDEPENDENT_CODE ON
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES)
target_include_directories(gemstone_hsa_core PUBLIC include PRIVATE ${GEMSTONE_NATIVE_INCLUDE_DIRECTORY})
target_compile_features(gemstone_hsa_core PUBLIC cxx_std_17)
target_compile_options(gemstone_hsa_core PRIVATE -Wall -Wextra -Wpedantic -Werror)
target_link_libraries(gemstone_hsa_core PRIVATE gemstone_execution_client Aspartame::Aspartame)

# HIP must carry a private HSA provider to avoid binding its internal calls to
# a vendor libhsa-runtime64 with the same SONAME.  HSA owns that embedded build;
# the public HSA DSO below compiles a separately exported variant.
add_library(gemstone_hsa_runtime_embedded OBJECT src/runtime.cpp)
set_target_properties(gemstone_hsa_runtime_embedded PROPERTIES
  POSITION_INDEPENDENT_CODE ON
  CXX_VISIBILITY_PRESET hidden
  VISIBILITY_INLINES_HIDDEN YES)
target_include_directories(gemstone_hsa_runtime_embedded PRIVATE
  include
  ${HSA_INCLUDE_DIR}
  ${GEMSTONE_NATIVE_INCLUDE_DIRECTORY})
target_compile_features(gemstone_hsa_runtime_embedded PRIVATE cxx_std_17)
target_compile_options(gemstone_hsa_runtime_embedded PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hsa_runtime_embedded PRIVATE gemstone_hsa_core gemstone_execution_client Aspartame::Aspartame)

add_library(gemstone_hsakmt SHARED
  src/hsakmt.cpp
  src/hsakmt_unsupported.cpp
)
target_include_directories(gemstone_hsakmt PUBLIC include ${HSAKMT_INCLUDE_DIR})
target_compile_features(gemstone_hsakmt PUBLIC cxx_std_17)
# The official hsakmt ABI header deliberately uses GNU anonymous structs.
target_compile_options(gemstone_hsakmt PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hsakmt PRIVATE gemstone_hsa_core gemstone_execution_client Aspartame::Aspartame)
gemstone_require_abi_implementations(
  TARGET gemstone_hsakmt
  PLATFORM hsakmt
  FORCE_INCLUDE)

add_library(gemstone_hsa_runtime SHARED
  src/runtime.cpp
  src/unsupported.cpp
)
set_target_properties(gemstone_hsa_runtime PROPERTIES OUTPUT_NAME "hsa-runtime64" SOVERSION 1)
target_include_directories(gemstone_hsa_runtime PUBLIC include ${HSA_INCLUDE_DIR})
target_compile_features(gemstone_hsa_runtime PUBLIC cxx_std_17)
target_compile_definitions(gemstone_hsa_runtime PRIVATE GEMSTONE_HSA_RUNTIME_LIBRARY=1)
target_compile_options(gemstone_hsa_runtime PRIVATE -Wall -Wextra -Werror -Wno-pedantic)
target_link_libraries(gemstone_hsa_runtime PRIVATE gemstone_hsa_core gemstone_execution_client Aspartame::Aspartame)
gemstone_require_abi_implementations(
  TARGET gemstone_hsa_runtime
  PLATFORM hsa
  FORCE_INCLUDE)

if(BUILD_TESTING)
  gemstone_memory_client(hsa gemstone_hsa_runtime)
  add_executable(gemstone_hsa_doorbell_test test/doorbell_monitor_test.cpp)
  target_link_libraries(gemstone_hsa_doorbell_test PRIVATE gemstone_hsa_core)
  gemstone_test_target(gemstone_hsa_doorbell_test)
  add_executable(gemstone_hsa_aql_execution_test test/aql_execution_test.cpp)
  target_link_libraries(gemstone_hsa_aql_execution_test PRIVATE gemstone_hsa_core)
  gemstone_test_target(gemstone_hsa_aql_execution_test)
  add_executable(gemstone_hsa_runtime_test test/hsa_runtime_test.cpp)
  target_link_libraries(gemstone_hsa_runtime_test PRIVATE gemstone_hsa_runtime)
  gemstone_test_target(gemstone_hsa_runtime_test)
  add_executable(gemstone_hsa_target_test test/target_test.cpp)
  target_include_directories(gemstone_hsa_target_test PRIVATE include ${GEMSTONE_NATIVE_INCLUDE_DIRECTORY})
  target_link_libraries(gemstone_hsa_target_test PRIVATE Aspartame::Aspartame)
  gemstone_test_target(gemstone_hsa_target_test)
  add_executable(gemstone_hsakmt_test
    test/hsakmt_test.cpp
    src/hsakmt.cpp
    src/hsakmt_unsupported.cpp)
  target_include_directories(gemstone_hsakmt_test PRIVATE
    include
    ${HSAKMT_INCLUDE_DIR}
    ${GEMSTONE_NATIVE_INCLUDE_DIRECTORY}
    src)
  target_compile_definitions(gemstone_hsakmt_test PRIVATE GEMSTONE_HSAKMT_TEST_SUPPORT=1)
  target_link_libraries(gemstone_hsakmt_test PRIVATE gemstone_hsa_core gemstone_execution_client Aspartame::Aspartame)
  gemstone_test_target(gemstone_hsakmt_test)
  if(CMAKE_CXX_COMPILER_ID MATCHES "Clang|GNU")
    # The official ROCr ABI headers deliberately use GNU anonymous structs and flexible arrays.
    target_compile_options(gemstone_hsa_runtime_test PRIVATE -Wno-pedantic)
    target_compile_options(gemstone_hsakmt_test PRIVATE -Wno-pedantic)
  endif()
  gemstone_discover_tests(gemstone_hsa_doorbell_test TEST_PREFIX "hsa/")
  gemstone_discover_tests(gemstone_hsa_aql_execution_test TEST_PREFIX "hsa/")
  gemstone_discover_tests(gemstone_hsa_runtime_test TEST_PREFIX "hsa/")
  gemstone_discover_tests(gemstone_hsa_target_test TEST_PREFIX "hsa/")
  gemstone_discover_tests(gemstone_hsakmt_test TEST_PREFIX "hsa/")
endif()
