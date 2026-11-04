FetchContent_MakeAvailable(googletest rocprim hipcub rocthrust)

set(BUILD_GMOCK OFF CACHE BOOL "" FORCE)
set(INSTALL_GTEST OFF CACHE BOOL "" FORCE)
add_subdirectory("${googletest_SOURCE_DIR}" googletest EXCLUDE_FROM_ALL)

find_package(hip REQUIRED CONFIG)
find_package(rocprim REQUIRED CONFIG)
find_package(hipcub REQUIRED CONFIG)
find_package(rocthrust REQUIRED CONFIG)

function(conformance_vendor_gtest_program target)
  cmake_parse_arguments(PARSE_ARGV 1 ARG "" "NAME;FILTER" "SOURCES;INCLUDES;LIBRARIES")
  add_library("${target}_main" STATIC "${CMAKE_CURRENT_LIST_DIR}/gtest_main.cpp")
  conformance_project_target("${target}_main")
  target_link_libraries("${target}_main" PUBLIC GTest::gtest)
  if(ARG_FILTER)
    target_compile_definitions("${target}_main" PRIVATE "CONFORMANCE_GTEST_FILTER=\"${ARG_FILTER}\"")
  endif()
  set_source_files_properties(${ARG_SOURCES} PROPERTIES LANGUAGE HIP)
  add_executable("${target}" ${ARG_SOURCES})
  set_target_properties("${target}" PROPERTIES OUTPUT_NAME "${ARG_NAME}")
  target_include_directories("${target}" SYSTEM PRIVATE ${ARG_INCLUDES})
  target_link_libraries("${target}" PRIVATE "${target}_main" hip::device ${ARG_LIBRARIES})
  conformance_register_test_program("${target}" ${CONFORMANCE_TIMEOUT_CTS} gtest)
  list(APPEND conformance_vendor_targets "${target}")
  set(conformance_vendor_targets "${conformance_vendor_targets}" PARENT_SCOPE)
endfunction()

set(conformance_vendor_rocprim_tests
  test_intrinsics test_block_reduce test_device_select
  test_device_histogram test_device_merge_sort test_device_transform)
list(TRANSFORM conformance_vendor_rocprim_tests PREPEND "${rocprim_SOURCE_DIR}/test/rocprim/")
list(TRANSFORM conformance_vendor_rocprim_tests APPEND ".cpp")
conformance_vendor_gtest_program(vendor_hip_rocprim_tests NAME vendor_hip_rocprim_tests
  SOURCES ${conformance_vendor_rocprim_tests}
  FILTER "RocprimIntrinsicsTests/0.ShuffleXor:RocprimIntrinsicsTests/0.WarpPermute:RocprimBlockReduceSingleValueTestsIntegral/0.Reduce:RocprimBlockReduceSingleValueTestsFloating/0.Reduce:RocprimDeviceSelectTests/0.Flagged:RocprimDeviceSelectTests/0.Unique:RocprimDeviceHistogramEven/0.*:RocprimDeviceHistogramMultiRange/0.*:RocprimDeviceSortTests/0.*:RocprimDeviceTransformTests/0.Transform:RocprimDeviceTransformTests/0.TernaryTransform"
  INCLUDES "${rocprim_SOURCE_DIR}/test" "${rocprim_SOURCE_DIR}/test/rocprim"
  LIBRARIES roc::rocprim)

set(conformance_vendor_hipcub_tests
  test_hipcub_block_scan test_hipcub_block_radix_sort test_hipcub_warp_reduce test_hipcub_device_scan
  test_hipcub_device_select test_hipcub_device_histogram)
list(TRANSFORM conformance_vendor_hipcub_tests PREPEND "${hipcub_SOURCE_DIR}/test/hipcub/")
list(TRANSFORM conformance_vendor_hipcub_tests APPEND ".cpp")
conformance_vendor_gtest_program(vendor_hip_hipcub_tests NAME vendor_hip_hipcub_tests
  SOURCES ${conformance_vendor_hipcub_tests}
  FILTER "HipcubBlockScanSingleValueTests/0.InclusiveSum:HipcubBlockScanSingleValueTests/0.ExclusiveScan:HipcubBlockRadixSort/0.*:HipcubWarpReduceTests/0.Reduce:HipcubWarpReduceTests/0.HeadSegmentedReduceSum:HipcubDeviceScanTests/0.InclusiveScan:HipcubDeviceScanTests/0.ExclusiveScanByKey:HipcubDeviceSelectTests/0.Flagged:HipcubDeviceSelectTests/0.Unique:HipcubDeviceHistogramEven/0.*:HipcubDeviceHistogramMultiRange/0.*"
  INCLUDES "${hipcub_SOURCE_DIR}/test" "${hipcub_SOURCE_DIR}/test/hipcub"
  LIBRARIES hip::hipcub)

set(RNG_SEED_COUNT 0)
set(PRNG_SEEDS_INITIALIZER 1)
configure_file("${rocthrust_SOURCE_DIR}/test/test_seed.in.hpp" "${CMAKE_CURRENT_BINARY_DIR}/rocthrust/test_seed.hpp")
set(conformance_vendor_rocthrust_filter_count "CountTests/10.*:CountPrimitiveTests/1.*")
set(conformance_vendor_rocthrust_filter_reduce "ReduceTests/10.*:ReducePrimitiveTests/1.*")
set(conformance_vendor_rocthrust_filter_transform "TransformTests/9.*:TransformVectorTests/9.*")
foreach(test IN ITEMS count reduce transform)
  conformance_vendor_gtest_program(vendor_hip_rocthrust_${test} NAME vendor_hip_rocthrust_${test}
    SOURCES "${rocthrust_SOURCE_DIR}/test/test_${test}.cpp"
    FILTER "${conformance_vendor_rocthrust_filter_${test}}"
    INCLUDES "${rocthrust_SOURCE_DIR}/test" "${CMAKE_CURRENT_BINARY_DIR}/rocthrust"
    LIBRARIES roc::rocthrust sqlite3)
endforeach()
