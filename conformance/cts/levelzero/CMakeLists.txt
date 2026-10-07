# The upstream suite is Google Test based. Build only the core executables
# whose short cases cover driver objects, memory, command submission and SPIR-V.
set(levelzero_cts_programs
  test_driver test_device test_context test_cmdqueue test_event test_fence
  test_memory test_module test_copy)

FetchContent_MakeAvailable(levelzero_cts levelzero_gtest)

set(staged_source "${CMAKE_CURRENT_BINARY_DIR}/source")
set(staging "${staged_source}.staging")
stage_source("${levelzero_cts_SOURCE_DIR}" "${staging}")
file(MAKE_DIRECTORY "${staging}/third_party/googletest")
file(COPY "${levelzero_gtest_SOURCE_DIR}/" DESTINATION "${staging}/third_party/googletest")

# This upstream revision requires static Boost 1.92, while supported toolchains
# ship a shared Boost 1.90. Neither choice affects the Level Zero API coverage.
patch_exact("${staging}/CMakeLists.txt" "set(Boost_USE_STATIC_LIBS ON)" "set(Boost_USE_STATIC_LIBS OFF)")
patch_exact("${staging}/CMakeLists.txt" "find_package(Boost 1.92" "find_package(Boost 1.90")

# Upstream configures every core subtree even when only a few executables are
# built. Some unrelated subtrees fetch Vulkan headers at configure time.
set(core_cmake "${staging}/conformance_tests/core/CMakeLists.txt")
file(READ "${core_cmake}" core_content)
foreach(program IN LISTS levelzero_cts_programs)
  string(FIND "${core_content}" "add_subdirectory(${program})" at)
  if(at EQUAL -1)
    message(FATAL_ERROR "Pinned Level Zero CTS core integration point changed: ${program}")
  endif()
endforeach()
string(REGEX MATCHALL "add_subdirectory\\(test_[A-Za-z0-9_]+\\)" core_subdirectories "${core_content}")
foreach(line IN LISTS core_subdirectories)
  string(REGEX REPLACE "^add_subdirectory\\(([^)]+)\\)$" "\\1" program "${line}")
  if(NOT program IN_LIST levelzero_cts_programs)
    string(REPLACE "${line}" "" core_content "${core_content}")
  endif()
endforeach()
file(WRITE "${core_cmake}" "${core_content}")

# The pinned CTS gives a 16-element buffer to module_add_two_arrays but
# launches 16 groups of 16 work items. Correct the group count so its multiple
# indirect-launch assertion does not depend on out-of-bounds device writes.
patch_exact("${staging}/conformance_tests/core/test_module/src/test_module.cpp"
  "mult_th_group_dim.groupCountX = 16;" "mult_th_group_dim.groupCountX = 1;")

# The sampler guard accepts UNSUPPORTED_FEATURE and then uses an uninitialised image module.
# Query capability instead; this also avoids leaking the guard's temporary sampler.
patch_exact("${staging}/conformance_tests/core/test_module/src/test_kernel_args.cpp" [=[bool sampler_support() {
  ze_sampler_desc_t descriptor = {};
  descriptor.stype = ZE_STRUCTURE_TYPE_SAMPLER_DESC;
  ze_sampler_handle_t sampler = nullptr;
  if (zeSamplerCreate(lzt::get_default_context(),
                      lzt::zeDevice::get_instance()->get_device(), &descriptor,
                      &sampler) == ZE_RESULT_ERROR_UNINITIALIZED) {
    return false;
  } else {
    return true;
  }
}]=] [=[bool sampler_support() {
  ze_device_image_properties_t properties{};
  properties.stype = ZE_STRUCTURE_TYPE_IMAGE_PROPERTIES;
  return zeDeviceGetImageProperties(lzt::zeDevice::get_instance()->get_device(), &properties) == ZE_RESULT_SUCCESS
      && properties.maxSamplers > 0;
}]=])

# Upstream initialises a device before Google Test can list cases. Artifacts
# must expose their inventory during relocation checks without a GPU present.
set(init_line "::testing::InitGoogleMock(&argc, argv);")
foreach(program IN LISTS levelzero_cts_programs)
  patch_exact("${staging}/conformance_tests/core/${program}/src/main.cpp"
    "${init_line}" "${init_line}\n  if (GTEST_FLAG_GET(list_tests)) return RUN_ALL_TESTS();")
endforeach()
commit_staged_source("${staging}" "${staged_source}")

set(GROUP "/conformance_tests/core")
set(GTEST_ROOT "${staged_source}/third_party/googletest")
set(LevelZero_INCLUDE_DIR "${conformance_levelzero_include}")
set(LevelZero_LIBRARY "${CONFORMANCE_LEVELZERO_LOADER}")
add_subdirectory("${staged_source}" build EXCLUDE_FROM_ALL)

# Device enumeration and affinity tests spawn these upstream helpers.
set(levelzero_device_helpers
  test_affinity_helper test_pci_device_order_helper test_device_hierarchy_helper)
install(TARGETS ${levelzero_device_helpers} RUNTIME DESTINATION device COMPONENT architecture)

foreach(program IN LISTS levelzero_cts_programs)
  set_target_properties("${program}" PROPERTIES OUTPUT_NAME "levelzero_cts_${program}")
  conformance_register_test_program("${program}" ${CONFORMANCE_TIMEOUT_CTS} gtest)
  # The upstream tests open SPIR-V files relative to their working directory.
  file(GLOB kernels "${staged_source}/conformance_tests/core/${program}/kernels/*.spv")
  if(kernels)
    install(FILES ${kernels} DESTINATION . COMPONENT architecture)
  endif()
endforeach()
install(FILES "${staged_source}/mediadata/internal/png/test_input.png"
  DESTINATION . COMPONENT architecture OPTIONAL)
install(FILES "${staged_source}/LICENSE"
  DESTINATION licences RENAME Level-Zero-Tests.txt COMPONENT support)
install(FILES "${staged_source}/third_party/googletest/LICENSE"
  DESTINATION licences RENAME GoogleTest.txt COMPONENT support)
add_custom_target(conformance_cts_artifact DEPENDS ${levelzero_cts_programs} ${levelzero_device_helpers})
